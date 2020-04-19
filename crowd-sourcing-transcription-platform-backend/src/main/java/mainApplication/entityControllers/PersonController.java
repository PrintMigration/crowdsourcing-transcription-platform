package mainApplication.entityControllers;

import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.Cookie;

import java.util.ArrayList;

import mainApplication.entities.Person;
import mainApplication.entities.PersonReligion;
import mainApplication.entities.Religion;
import mainApplication.fusionAuth.FusionAuthController;
import mainApplication.repositories.PersonReligionRepository;
import mainApplication.repositories.PersonRepository;
import mainApplication.repositories.ReligionRepository;

@RestController
@RequestMapping("person")
public class PersonController {
	private final PersonRepository pRepo;
	private final ReligionRepository relRepo;
	private final PersonReligionRepository prRepo;
	private final FusionAuthController fusionAuthController;
	
	public PersonController(PersonRepository inPRepo, ReligionRepository inRelRepo, PersonReligionRepository inprRepo, FusionAuthController inFusionAuth) {
		this.pRepo = inPRepo;
		this.relRepo = inRelRepo;
		this.prRepo = inprRepo;
		this.fusionAuthController = inFusionAuth;
	}
	
	//Requires the page number that we are on so we may keep track of what set of results we want
	@GetMapping("all")
	public ResponseEntity<Page<Person>> getAllPeople(@RequestParam(defaultValue = "0") int pageNum, @RequestParam(defaultValue = "100") int pageSize){
		//20 is the number of results which can also be an input if we decide to have the user be able to pick how many results appear
		//I just figured that 20 was probably a good number
		Pageable page = PageRequest.of(pageNum, pageSize, Sort.by("firstName"));
				
		return new ResponseEntity<>(pRepo.findAll(page), HttpStatus.OK);
	}
	
	@GetMapping("/{id}")
	public Optional<Person> getPersonByID(@PathVariable Integer id){
		return pRepo.findById(id);
	}
	
	//Could be an API endpoint at some time in the future, but not for our needs
	public List<Person> addPeople(List<Person> personList) {		
		List<Person> ret = new ArrayList<>();
		
		Person temp = null;		
		for(Person p : personList) {
			if(pRepo.existsByPersonLOD(p.getPersonLOD())) {
				temp = pRepo.findByPersonLOD(p.getPersonLOD());
			}
			else{
				//Enforces default gender
				if(p.getGender() == null) {
					p.setGender('U');
				}
				temp = pRepo.save(p);
			}
			//We just want to know if it is in the database
			ret.add(temp);
		}
		
		return ret;
	}
	
	public Person addPerson(Person p) {
		Person temp = null;
		if(pRepo.existsByPersonLOD(p.getPersonLOD())) {
			temp = pRepo.findByPersonLOD(p.getPersonLOD());
		}
		else {
			temp = pRepo.save(p);
		}
		
		return temp;
	}

	public boolean diffPerson(Person person) {		
		Person temp = null;

		if(pRepo.existsByPersonLOD(person.getPersonLOD())) {
			temp = pRepo.findByPersonLOD(person.getPersonLOD());
			
			//This means that they want to link an already existing person to a document because the lod is the only thing entered
			if(person.getFirstName().equals("") && person.getMiddleName().equals("") && person.getLastName().equals("") && person.getPrefix().equals("")
					&& person.getSuffix().equals("") && person.getGender() == null && person.getBirthDate().equals("") && person.getDeathDate().equals("")
					&& person.getOccupation().equals(""))
				return false;
			
			else if(person.equals(temp))
				return false;
			else
				return true;
		}
		
		//Means that the person does not exists in our database
		return false;
	}
	
//	public static class RelAndDate {
//		public Religion rel;
//		public String spanDate;
//		
//		public RelAndDate() {
//		}
//	}
//	//Custom object to help with updating people
//	public static class PersonWrapper {
//		public Person person;
//		public List<RelAndDate> relAndDate;
//		
//		public PersonWrapper() {
//		}
//	}
	
	//Don't let them change the fields that we don't want them to on the front end?
//	@PostMapping("update")
//	public ResponseEntity<String> updatePerson(@RequestBody PersonWrapper wrap){//, @CookieValue(value = "access_token") Cookie cookie) {
////		if(fusionAuthController.isMetadataExpert(cookie) != null) {
////			return fusionAuthController.isMetadataExpert(cookie);
////		}
//		if(!pRepo.existsById(wrap.person.getPersonID())) {
//			return new ResponseEntity<>("This is not a valid person", HttpStatus.CONFLICT);
//		}
//		
//		if(pRepo.existsByPersonLOD(wrap.person.getPersonLOD()) && !pRepo.findByPersonLOD(wrap.person.getPersonLOD()).getPersonID().equals(wrap.person.getPersonID())) {
//			return new ResponseEntity<>("The entered lod is already being used by another person. Please choose another lod and try again.", HttpStatus.CONFLICT);			
//		}
//		pRepo.saveAndFlush(wrap.person);
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
//				PersonReligion pr = new PersonReligion(wrap.person, temp, r.spanDate);
//				prRepo.save(pr);
//			}
//		}
//		
//		return new ResponseEntity<>("Person was updated", HttpStatus.OK);
//	}
	
	@PostMapping("update")
	public ResponseEntity<String> updatePerson(@RequestBody Person person){//, @CookieValue(value = "access_token") Cookie cookie) {
//		if(fusionAuthController.isMetadataExpert(cookie) != null) {
//			return fusionAuthController.isMetadataExpert(cookie);
//		}
		if(!pRepo.existsById(person.getPersonID())) {
			return new ResponseEntity<>("This is not a valid person", HttpStatus.CONFLICT);
		}
		
		if(pRepo.existsByPersonLOD(person.getPersonLOD()) && !pRepo.findByPersonLOD(person.getPersonLOD()).getPersonID().equals(person.getPersonID())) {
			return new ResponseEntity<>("The entered lod is already being used by another person. Please choose another lod and try again.", HttpStatus.CONFLICT);			
		}
		
		if(person.getGender() == null) {
			person.setGender('U');
		}
		
		Person updated = pRepo.saveAndFlush(person);
		
		if(person.getpRelSet() != null && !person.getpRelSet().isEmpty()) {
			prRepo.deleteByPerson(pRepo.findByPersonLOD(updated.getPersonLOD()));
			for(PersonReligion pr : person.getpRelSet()) {
				Religion temp = new Religion();
				if(relRepo.existsByReligionDesc(pr.getReligion().getReligionDesc())) {
					temp = relRepo.findByReligionDesc(pr.getReligion().getReligionDesc());
				}
				else {
					temp = relRepo.save(pr.getReligion());
				}
				
				PersonReligion prTemp = new PersonReligion(updated, temp, pr.getDateSpan());
				prRepo.save(prTemp);
			}
		}
		
		return new ResponseEntity<>("Person was updated", HttpStatus.OK);
	}
	
	@PostMapping("delete")
	public ResponseEntity<String> deletePerson(@RequestBody Integer personID){//, @CookieValue(value = "access_token") Cookie cookie) {
//		if(fusionAuthController.isMetadataExpert(cookie) != null) {
//			return fusionAuthController.isMetadataExpert(cookie);
//		}
		if(!pRepo.existsById(personID)) {
			return new ResponseEntity<>("It looks like we showed you a person that does not exist in our database. Please refresh the page. If the person still "
					+ "shows, notify tech services.", HttpStatus.BAD_REQUEST);
		}
		
		pRepo.deleteById(personID);
		return new ResponseEntity<>("Person was deleted!", HttpStatus.OK);
	}
}
