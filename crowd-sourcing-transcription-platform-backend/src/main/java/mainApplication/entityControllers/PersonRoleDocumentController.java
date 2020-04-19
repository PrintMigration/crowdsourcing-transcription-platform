package mainApplication.entityControllers;

import java.util.*;

import org.springframework.web.bind.annotation.*;

import mainApplication.entities.*;
import mainApplication.repositories.*;

@RestController
@RequestMapping("prd")
public class PersonRoleDocumentController {
	public final PersonRepository pRepo;
	public final RoleRepository rRepo;
	public final DocumentRepository dRepo;
	public final PersonRoleDocumentRepository prdRepo;
	
	public PersonRoleDocumentController(PersonRepository inPRepo, RoleRepository inRRepo, DocumentRepository inDRepo, PersonRoleDocumentRepository inPRDRepo) {
		this.pRepo = inPRepo;
		this.rRepo = inRRepo;
		this.dRepo = inDRepo;
		this.prdRepo = inPRDRepo;
	}

	@GetMapping("/{id}")
	public Optional<PersonRoleDocument> getPersonRoleDocumentByID(@PathVariable Integer id){
		return prdRepo.findById(id);
	}
	
	public void deleteByDocument(Document doc) {		
		Document del = dRepo.findByDocumentID(doc.getDocumentID());
		prdRepo.deleteByDocument(del);
	}
	
//	@PostMapping("people-from-doc")
	public List<Person> getAllPeopleFromDoc(Integer docID){
		Document find = dRepo.findByDocumentID(docID);
		List<PersonRoleDocument> relations = prdRepo.findByDocument(find);
		List<Person> people = new ArrayList<>();
		
		for(PersonRoleDocument prd : relations) {
			people.add(prd.getPerson());
		}
		
		return people;
	}
	
	//Might not be a necessary endpoint, but it could be helpful for later development? Mapping people?
	@PostMapping("doc-from-people")
	public List<Document> getAllDocFromPerson(@RequestBody Person inPerson){		
		List<PersonRoleDocument> relations = prdRepo.findByPerson(inPerson);
		List<Document> docList = new ArrayList<>();
		
		for(PersonRoleDocument prd : relations) {
			docList.add(prd.getDocument());
		}
		
		return docList;
	}
	
	public List<PersonRoleDocument> getRelationshipsFromDocAndRepo(Integer docID){
		Document find = dRepo.findByDocumentID(docID);
		return prdRepo.findByDocument(find);
	}
	
	//Don't think this needs to be an endpoint because if they want to add a relationship, they will just update a document
	public void addPersonRoleDocument(PersonRoleDocument inPrd){	
		//Assume doc and person are already added from other calls
		Role role = new Role();
		if(rRepo.existsByRoleDesc(inPrd.getRole().getRoleDesc()))
			role = rRepo.findByRoleDesc(inPrd.getRole().getRoleDesc());
		else
			role = rRepo.save(inPrd.getRole());
		
		//Add role
		inPrd.setRole(role);
		
		//Save prd to repo
		prdRepo.save(inPrd);
	}
}
