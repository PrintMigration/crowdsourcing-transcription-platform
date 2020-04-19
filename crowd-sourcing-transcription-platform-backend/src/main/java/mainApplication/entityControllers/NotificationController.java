package mainApplication.entityControllers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mainApplication.entities.IndividualNotification;
import mainApplication.entities.Request;
import mainApplication.repositories.IndividualNotificationRepository;

@RestController
@RequestMapping("notifs")
public class NotificationController {
	private final IndividualNotificationRepository indNotifRepo;
	private final RoleController requestController;

	public NotificationController(IndividualNotificationRepository indNotifRepo, RoleController requestController) {
		this.indNotifRepo = indNotifRepo;
		this.requestController = requestController;
	}
	
	/*
	 * Returns a list of all notifications meant to be seen by a user (even if they have been seen already)
	 * Optionally, set a pageNum (default 0) and a pageSize (default 1000)
	 */
	@GetMapping(path="/all")
	public ResponseEntity<Page<IndividualNotification>> getUserNotifications(@RequestParam String userID, @RequestParam(defaultValue = "0") int pageNum, @RequestParam(defaultValue = "1000") int pageSize) {
		Pageable page = PageRequest.of(pageNum, pageSize, Sort.by("individualNotificationID").descending());
		Page<IndividualNotification> notifs = indNotifRepo.findByUserNotified(userID, page);
		return new ResponseEntity<>(notifs, HttpStatus.OK);
	}
	
	/*
	 * Returns a list of notifications for a user that are unseen
	 */
	@GetMapping(path="/unseen")
	public ResponseEntity<List<IndividualNotification>> getUnseenUserNotifications(@RequestParam String userID) {
		List<IndividualNotification> list = indNotifRepo.findByUserNotifiedAndSeen(userID, false);
		return new ResponseEntity<>(list, HttpStatus.OK);
	}
	
	/*
	 * Returns the number of notifications for a user that are unseen plus the number of pending requests
	 */
	@GetMapping(path="/count")
	public ResponseEntity<Integer> getCountUserNotifications(@RequestParam String userID) {
		List<IndividualNotification> notif = indNotifRepo.findByUserNotifiedAndSeen(userID, false);
		List<Request> requests = requestController.getAllRequests();
		int total = notif.size() + requests.size();
		return new ResponseEntity<>(total, HttpStatus.OK);
	}
	
	/*
	 * Mark all of a user's notifications as seen
	 */
	@PostMapping(path="/mark-as-seen")
	public ResponseEntity<String> markAsSeen(@RequestParam String userID) {
		List<IndividualNotification> list = indNotifRepo.findByUserNotified(userID);
		for (IndividualNotification indNotif : list) {
			indNotif.setSeen(true);
			indNotifRepo.save(indNotif);
		}
		return new ResponseEntity<>(null, HttpStatus.OK);
	}
}
