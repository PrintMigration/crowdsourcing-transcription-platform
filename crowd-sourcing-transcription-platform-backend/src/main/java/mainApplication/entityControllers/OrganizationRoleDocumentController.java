package mainApplication.entityControllers;

import java.util.*;

import org.springframework.web.bind.annotation.*;

import mainApplication.entities.*;
import mainApplication.repositories.*;

@RestController
@RequestMapping("ord")
public class OrganizationRoleDocumentController {
	public final OrganizationRepository oRepo;
	public final RoleRepository rRepo;
	public final DocumentRepository docRepo;
	public final OrganizationRoleDocumentRepository ordRepo;
	
	public OrganizationRoleDocumentController(OrganizationRepository inOR, RoleRepository inRR, DocumentRepository inDR, OrganizationRoleDocumentRepository inORDR) {
		this.docRepo = inDR;
		this.oRepo = inOR;
		this.ordRepo = inORDR;
		this.rRepo = inRR;
	}
	
	//Are these guys necessary?
//	@GetMapping
//	public List<OrganizationRoleDocument> getAllOrganizationRoleDocuments() {
//		return ordRepo.findAll();
//	}
	
	//The following two can be used on the document page and the organization page respectively
//	@PostMapping("orgs-from-doc")
	public List<Organization> getAllOrgsFromDoc(Integer docID) {
		Document find = docRepo.findByDocumentID(docID);
		List<OrganizationRoleDocument> relations = ordRepo.findByDocument(find);
		List<Organization> orgs = new ArrayList<>();
		
		for(OrganizationRoleDocument ord : relations) {
			orgs.add(ord.getOrg());
		}
		
		return orgs;
	}
	
	@PostMapping("docs-from-org")
	public List<Document> getAllDocsFromOrg(@RequestBody Organization inOrg) {
		List<OrganizationRoleDocument> relations = ordRepo.findByOrg(inOrg);
		List<Document> docList = new ArrayList<>();
		
		for(OrganizationRoleDocument ord : relations) {
			docList.add(ord.getDocument());
		}
		
		return docList;
	}
	
	//Links a organization to a document with some role
	public void addOrganizationRoleDocument(OrganizationRoleDocument inORD) {
		Role role = new Role();
		
		if(rRepo.existsByRoleDesc(inORD.getRole().getRoleDesc()))
			role = rRepo.findByRoleDesc(inORD.getRole().getRoleDesc());
		else
			role = rRepo.save(inORD.getRole());
		
		inORD.setRole(role);
		
		ordRepo.save(inORD);
	}

	public void deleteByDocument(Document result) {
		ordRepo.deleteByDocument(result);
	}
}
