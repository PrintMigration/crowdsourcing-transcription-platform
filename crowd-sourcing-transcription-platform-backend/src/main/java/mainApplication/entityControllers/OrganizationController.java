package mainApplication.entityControllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.Cookie;

import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import mainApplication.entities.Organization;
import mainApplication.entities.OrganizationReligion;
import mainApplication.entities.PersonReligion;
import mainApplication.entities.Religion;
import mainApplication.fusionAuth.FusionAuthController;
import mainApplication.repositories.OrganizationReligionRepository;
import mainApplication.repositories.OrganizationRepository;
import mainApplication.repositories.ReligionRepository;

@RestController
@RequestMapping("org")
public class OrganizationController {
	private final OrganizationRepository orgRepo;
	private final ReligionRepository relRepo;
	private final OrganizationReligionRepository orRepo;
	private final FusionAuthController fusionAuthController;
	
	public OrganizationController(OrganizationRepository inOR, ReligionRepository inRelRepo, OrganizationReligionRepository inOrRepo, FusionAuthController inFusionAuth) {
		this.orgRepo = inOR;
		this.relRepo = inRelRepo;
		this.orRepo = inOrRepo;
		this.fusionAuthController = inFusionAuth;
	}
	
	@GetMapping("all")
	public ResponseEntity<Page<Organization>> getAllOrganizations(@RequestParam(defaultValue = "0") int pageNum){
		Pageable page = PageRequest.of(pageNum, 20, Sort.by("orgName"));
		
		return new ResponseEntity<>(orgRepo.findAll(page), HttpStatus.OK);
	}
	
	@GetMapping("/{id}")
	public Optional<Organization> getOrgByID(@PathVariable Integer id){
		return orgRepo.findById(id);
	}

	public List<Organization> addOrganizations(List<Organization> orgList) {		
		List<Organization> ret = new ArrayList<>();
		
		Organization temp = null;
		for(Organization org : orgList) {
			if(orgRepo.existsByOrgName(org.getOrgName())) {
				temp = orgRepo.findByOrgName(org.getOrgName());
			}
			else {
				temp = orgRepo.save(org);
			}
			ret.add(temp);
		}
		
		return ret;
	}
	
	public Organization addSingleOrg(Organization org) {
		Organization temp = null;
		if(orgRepo.existsByOrgName(org.getOrgName())) {
			temp = orgRepo.findByOrgName(org.getOrgName());
		}
		else {
			temp = orgRepo.save(org);
		}
		
		return temp;
	}
	
	public boolean diffOrg(Organization org) {
		Organization temp = null;
		
		if(orgRepo.existsByOrgName(org.getOrgName())) {
			temp = orgRepo.findByOrgName(org.getOrgName());
			
			if(org.getFormationDate().equals("") && org.getDissolutionDate().equals("") && org.getOrgLOD().equals(""))
				return false;
			else if(org.equals(temp))
				return false;
			else
				return true;
		}
		
		//Means that the organization does not exist in our database
		return false;
	}
	
//	public static class RelAndDate {
//		public Religion rel;
//		public String spanDate;
//		
//		public RelAndDate() {
//		}
//	}
	//Custom object to help with updating people
//	public static class PersonWrapper {
//		public Organization org;
//		public List<RelAndDate> relAndDate;
//		
//		public PersonWrapper() {
//		}
//	}
//	
//	//Custom object to help with updating orgs
//	public static class OrgWrapper {
//		public Organization org;
//		public List<RelAndDate> relAndDate;
//		
//		public OrgWrapper() {
//		}
//	}
		
//	@PostMapping("update")
//	public ResponseEntity<String> updateOrganization(@RequestBody OrgWrapper wrap){//, @CookieValue(value = "access_token") Cookie cookie) {
////		if(fusionAuthController.isMetadataExpert(cookie) != null) {
////			return fusionAuthController.isMetadataExpert(cookie);
////		}
//		if(!orgRepo.existsById(wrap.org.getOrgID())) {
//			return new ResponseEntity<>("This is not a valid organization", HttpStatus.CONFLICT);
//		}
//		if(orgRepo.existsByOrgName(wrap.org.getOrgName()) && !orgRepo.findByOrgName(wrap.org.getOrgName()).getOrgID().equals(wrap.org.getOrgID())) {
//			return new ResponseEntity<>("The entered organization name is already being used. Fix this and try again.", HttpStatus.CONFLICT);
//		}
//		
//		orgRepo.saveAndFlush(wrap.org);
//		
//		if(wrap.relAndDate != null) {
//			Religion temp = new Religion();
//			for(RelAndDate r : wrap.relAndDate) {
//				if(relRepo.existsByReligionDesc(r.rel.getReligionDesc())) {
//					temp = relRepo.findByReligionDesc(r.rel.getReligionDesc());
//				}
//				else {
//					temp = relRepo.save(r.rel);
//				}
//				
//				OrganizationReligion or = new OrganizationReligion(wrap.org, temp, r.spanDate);
//				orRepo.save(or);
//			}
//		}
//		
//		return new ResponseEntity<>("Organization was updated", HttpStatus.OK);
//	}
	
	@PostMapping("update")
	public ResponseEntity<String> updateOrganization(@RequestBody Organization org){//, @CookieValue(value = "access_token") Cookie cookie) {
//		if(fusionAuthController.isMetadataExpert(cookie) != null) {
//			return fusionAuthController.isMetadataExpert(cookie);
//		}
		if(!orgRepo.existsById(org.getOrgID())) {
			return new ResponseEntity<>("This is not a valid organization", HttpStatus.CONFLICT);
		}
		if(orgRepo.existsByOrgName(org.getOrgName()) && !orgRepo.findByOrgName(org.getOrgName()).getOrgID().equals(org.getOrgID())) {
			return new ResponseEntity<>("The entered organization name is already being used. Fix this and try again.", HttpStatus.CONFLICT);
		}
		
		Organization updated = orgRepo.saveAndFlush(org);
		
		if(org.getOrgRelSet() != null && !org.getOrgRelSet().isEmpty()) {
			orRepo.deleteByOrg(orgRepo.findByOrgName(updated.getOrgName()));
			for(OrganizationReligion or : org.getOrgRelSet()) {
				Religion temp = new Religion();
				if(relRepo.existsByReligionDesc(or.getReligion().getReligionDesc())) {
					temp = relRepo.findByReligionDesc(or.getReligion().getReligionDesc());
				}
				else {
					temp = relRepo.save(or.getReligion());
				}
				
				OrganizationReligion orTemp = new OrganizationReligion(updated, temp, or.getDateSpan());
				orRepo.save(orTemp);
			}
		}
		
		return new ResponseEntity<>("Organization was updated", HttpStatus.OK);
	}
	
	@PostMapping("delete")
	public ResponseEntity<String> deleteOrganization(@RequestBody Integer orgID){//, @CookieValue(value = "access_token") Cookie cookie) {
//		if(fusionAuthController.isMetadataExpert(cookie) != null) {
//			return fusionAuthController.isMetadataExpert(cookie);
//		}
		if(!orgRepo.existsById(orgID)) {
			return new ResponseEntity<>("It looks like we showed you an organization that does not exist in our database. Please refresh the page. If the organization still "
					+ "shows, notify tech services.", HttpStatus.BAD_REQUEST);
		}
		
		orgRepo.deleteById(orgID);
		return new ResponseEntity<>("Organization was deleted!", HttpStatus.OK);
	}
}
