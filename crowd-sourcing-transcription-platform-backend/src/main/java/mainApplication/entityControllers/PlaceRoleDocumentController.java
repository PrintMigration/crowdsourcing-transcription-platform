package mainApplication.entityControllers;

import java.util.*;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import mainApplication.entities.*;
import mainApplication.repositories.*;

@RestController
@RequestMapping("pprd")
public class PlaceRoleDocumentController {
	public final PlaceRepository pRepo;
	public final RoleRepository rRepo;
	public final DocumentRepository dRepo;
	public final PlaceRoleDocumentRepository placerdRepo;
	
	public PlaceRoleDocumentController(PlaceRepository inPRepo, RoleRepository inRRepo, DocumentRepository inDRepo, PlaceRoleDocumentRepository inPlaceRDRepo) {
		this.pRepo = inPRepo;
		this.dRepo = inDRepo;
		this.rRepo = inRRepo;
		this.placerdRepo = inPlaceRDRepo;
	}
	
	//Are these necessary?
//	@GetMapping
//	public @ResponseBody ResponseEntity<Iterable<PlaceRoleDocument>> getAllPlaceRoleDocuments() {
//		return new ResponseEntity<>(placerdRepo.findAll(), HttpStatus.OK);
//	}
	
	@PostMapping("docs-from-place")
	public ResponseEntity<List<Document>> getAllDocFromPlace(@RequestBody Place inPlace) {
		List<PlaceRoleDocument> relations = placerdRepo.findByPlace(inPlace);
		List<Document> docList = new ArrayList<>();
		
		for(PlaceRoleDocument pprd : relations) {
			docList.add(pprd.getDocument());
		}
		
		return new ResponseEntity<>(docList, HttpStatus.OK);		
	}
	
//	@PostMapping("places-from-doc")
	public List<Place> getAllPlaceFromDoc(@RequestBody Integer docID) {
		Document find = dRepo.findByDocumentID(docID);
		List<PlaceRoleDocument> relations = placerdRepo.findByDocument(find);
		List<Place> placeList = new ArrayList<>();
		
		for(PlaceRoleDocument pprd : relations) {
			placeList.add(pprd.getPlace());
		}
		
		return placeList;	
	}
	
	public void addPlaceRoleDocument(PlaceRoleDocument inPPRD) {
		Role role = new Role();
		
		if(rRepo.existsByRoleDesc(inPPRD.getRole().getRoleDesc()))
			role = rRepo.findByRoleDesc(inPPRD.getRole().getRoleDesc());
		else
			role = rRepo.save(inPPRD.getRole());
		
		inPPRD.setRole(role);
		
		placerdRepo.save(inPPRD);
	}

	public void deleteByDocument(Document result) {
		placerdRepo.deleteByDocument(result);
	}
}