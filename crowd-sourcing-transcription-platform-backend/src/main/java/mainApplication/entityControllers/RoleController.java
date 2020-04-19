package mainApplication.entityControllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mainApplication.Constants;
import mainApplication.entities.Document;
import mainApplication.entities.DocumentEdit;
import mainApplication.entities.Edit;
import mainApplication.entities.Notification;
import mainApplication.entities.Request;
import mainApplication.fusionAuth.FusionAuthController;
import mainApplication.repositories.RequestRepository;
import mainApplication.services.DocumentEditService;
import mainApplication.services.NotificationService;

@RestController
public class RoleController {
	private final RequestRepository requestRepo;
	private final DocumentEditService docEditService;
	private final NotificationService notifService;
	private final DocumentEditController documentEditController;
	private final FusionAuthController fusionAuthController;

	public RoleController(RequestRepository requestRepo, DocumentEditService docEditService, NotificationService notifService, DocumentEditController documentEditController, FusionAuthController fusionauthController) {
		this.requestRepo = requestRepo;
		this.docEditService = docEditService;
		this.notifService = notifService;
		this.documentEditController = documentEditController;
		this.fusionAuthController = fusionauthController;
	}
	
	/*
	 * Get all requests that are currently pending (most recent first)
	 * Optionally, set a pageNum (default 0) and a pageSize (default 1000)
	 */
	@GetMapping(path="requests/all")
	public ResponseEntity<Page<Request>> getAllRequests(@RequestParam(defaultValue = "0") int pageNum, @RequestParam(defaultValue = "1000") int pageSize) {
		Pageable page = PageRequest.of(pageNum, pageSize);
		Page<Request> requests = requestRepo.findByGrantedOrderByDateRequestedDesc(null, page);
		return new ResponseEntity<>(requests, HttpStatus.OK);
	}
	
	/*
	 * (For a user) Make a request to become a specific role
	 * The request can be approved or denied by an admin
	 */
	@PostMapping(path="requests/make")
	public ResponseEntity<String> makeRequest(@RequestBody UserAndRole body) {
		// If the user already has a request for this role pending, don't allow this request
		List<Request> list = requestRepo.findByRequesterAndRoleRequestedAndGranted(body.userID, body.role, null);
		if (!list.isEmpty())
			return new ResponseEntity<>("This request has already been made", HttpStatus.BAD_REQUEST);
		if (!Constants.Role.roleHierarchy.contains(body.role))
			return new ResponseEntity<>("Invalid role", HttpStatus.BAD_REQUEST);
		Request request = new Request(body.userID, body.role);
		requestRepo.save(request);
		return new ResponseEntity<>(null, HttpStatus.OK);
	}
	
	private static class UserAndRole {
		public String userID;
		public String role;
	}
	
	/*
	 * (For an admin) Approve this request
	 */
	@PostMapping(path="requests/approve")
	public ResponseEntity approveRequest(@RequestBody UserAndRole body) {
		List<Request> list = requestRepo.findByRequesterAndRoleRequestedAndGranted(body.userID, body.role, null);
		if (list.isEmpty())
			return new ResponseEntity<>("No request found", HttpStatus.NOT_FOUND);
		
		List<String> roles = getAllRolesUpToAndIncluding(body.role);
		if (roles == null || roles.size() == 0)
			return new ResponseEntity<>("Invalid role", HttpStatus.BAD_REQUEST);
		// Add roles to FusionAuth
		ResponseEntity response = fusionAuthController.addUserRoles(body.userID, roles);
		if (!response.getStatusCode().equals(HttpStatus.OK))
			return response;
		
		Request request = list.get(0);
		request.setGranted(true);
		requestRepo.save(request);
		
		String notifText = String.format("User %s's request to become a(n) %s has been approved", body.userID, body.role);
		Notification notif = new Notification(Constants.NotificationType.REQUEST_APPROVED, notifText, body.userID);
		notifService.setupAdminNotification(notif);
		return response;
	}
	
	/*
	 * (For an admin) Deny this request
	 */
	@PostMapping(path="requests/deny")
	public ResponseEntity<String> denyRequest(@RequestBody UserAndRole body) {
		List<Request> list = requestRepo.findByRequesterAndRoleRequestedAndGranted(body.userID, body.role, null);
		if (list.isEmpty())
			return new ResponseEntity<>("No request found", HttpStatus.NOT_FOUND);
		Request request = list.get(0);
		request.setGranted(false);
		requestRepo.save(request);
		
		String notifText = String.format("User %s's request to become a(n) %s has been denied", body.userID, body.role);
		Notification notif = new Notification(Constants.NotificationType.REQUEST_DENIED, notifText, body.userID);
		notifService.setupAdminNotification(notif);
		return new ResponseEntity<>(null, HttpStatus.OK);
	}
	
	/*
	 * (For an admin) Promote or demote a user (without a request)
	 * Adds all roles below and up to the given role and removes all above
	 */
	@PostMapping(path="roles/assign")
	public ResponseEntity assignRole(@RequestBody UserAndRole body) {
		if (!Constants.Role.roleHierarchy.contains(body.role))
			return new ResponseEntity<>("Invalid role", HttpStatus.BAD_REQUEST);
		
		// Remove roles from FusionAuth
		List<String> toRemove = getAllRolesAbove(body.role);
		if (toRemove.size() > 0) {
			ResponseEntity response1 = fusionAuthController.deleteUserRoles(body.userID, toRemove);
			if (!response1.getStatusCode().equals(HttpStatus.OK))
				return response1;
		}
		
		// Add roles to FusionAuth
		List<String> toAdd = getAllRolesUpToAndIncluding(body.role);
		ResponseEntity response2 = fusionAuthController.addUserRoles(body.userID, toAdd);
		if (!response2.getStatusCode().equals(HttpStatus.OK))
			return response2;
		
		String notifText = String.format("User %s has been assigned the role of %s", body.userID, body.role);
		Notification notif = new Notification(Constants.NotificationType.ROLE_ASSIGNED, notifText, body.userID);
		notifService.setupAdminNotification(notif);
		return response2;
	}
	
	/*
	 * Deactivate a user so they can't sign in
	 * Returns all of their checked out documents
	 */
	@DeleteMapping(path="deactivate")
	public ResponseEntity deactivateUser(@RequestParam String userID) {
		// Return all documents currently checked out by the user
		List<DocumentEdit> docEdits = documentEditController.getCheckedOutDocumentEdits(userID);
		for (DocumentEdit docEdit : docEdits) {
			Document doc = docEdit.getBaseDoc();
			String textType = docEdit.getTextType();
			Edit edit = new Edit(doc.getDocumentID(), userID, docEdit.getDocumentText(), docEdit.getTranslationText());
			
			// Return document without notifying admins
			docEditService.returnDocument(edit, textType, false);
		}
		
		// Remove user from FusionAuth
		ResponseEntity response = fusionAuthController.deactivateUser(userID);
		if (!response.getStatusCode().equals(HttpStatus.OK))
			return response;
		
		String notifText = String.format("User %s has been deactivated", userID);
		Notification notif = new Notification(Constants.NotificationType.USER_DEACTIVATED, notifText, userID);
		notifService.setupAdminNotification(notif);
		return new ResponseEntity<>(null, HttpStatus.OK);
	}
	
	/*
	 * Add a single role to a user (for testing purposes)
	 */
	@PostMapping(path="roles/add")
	public ResponseEntity addRole(@RequestBody UserAndRole body) {
		if (!Constants.Role.roleHierarchy.contains(body.role))
			return new ResponseEntity<>("Invalid role", HttpStatus.BAD_REQUEST);
		// Add role to FusionAuth
		List<String> roles = new ArrayList<>();
		roles.add(body.role);
		ResponseEntity response = fusionAuthController.addUserRoles(body.userID, roles);
		return response;
	}
	
	/*
	 * Remove a single role from a user (for testing purposes)
	 */
	@PostMapping(path="roles/remove")
	public ResponseEntity removeRole(@RequestBody UserAndRole body) {
		if (!Constants.Role.roleHierarchy.contains(body.role))
			return new ResponseEntity<>("Invalid role", HttpStatus.BAD_REQUEST);
		// Remove role from FusionAuth
		List<String> roles = new ArrayList<>();
		roles.add(body.role);
		ResponseEntity response = fusionAuthController.deleteUserRoles(body.userID, roles);
		return response;
	}
	
	/*
	 * Get all requests that are currently pending (most recent first)
	 */
	public List<Request> getAllRequests() {
		List<Request> list = requestRepo.findByGrantedOrderByDateRequestedDesc(null);
		return list;
	}

	/*
	 * Returns a list of all roles that have the same or fewer permissions than the given role
	 */
	private List<String> getAllRolesUpToAndIncluding(String role) {
		List<String> allRoles = Constants.Role.roleHierarchy;
		int idx = allRoles.indexOf(role);
		if (idx < 0)
			return null;
		List<String> roles = new ArrayList<>();
		for (int i = 0; i <= idx; i++)
			roles.add(allRoles.get(i));
		return roles;
	}
	
	/*
	 * Returns a list of all roles that have strictly more permissions than the given role
	 */
	private List<String> getAllRolesAbove(String role) {
		List<String> allRoles = Constants.Role.roleHierarchy;
		int idx = allRoles.indexOf(role);
		if (idx < 0)
			return null;
		List<String> roles = new ArrayList<>();
		for (int i = idx+1; i < allRoles.size(); i++)
			roles.add(allRoles.get(i));
		return roles;
	}
}
