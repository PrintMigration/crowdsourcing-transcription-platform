package mainApplication.entityControllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import javax.servlet.http.Cookie;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.fusionauth.domain.User;
import mainApplication.Constants;
import mainApplication.entities.*;
import mainApplication.fusionAuth.FusionAuthController;
import mainApplication.repositories.*;
import mainApplication.services.NotificationService;
import mainApplication.specifications.*;

@RestController
@RequestMapping("documents") //Denotes that the URL starts with /documents after the application path
public class DocumentController {
	//This is to initialize the repositories that we have
	private final DocumentRepository docRepo;
	private final LanguageRepository langRepo;
	private final DocumentTypeRepository typeRepo;
	private final DocRepositoryRepository repoRepo;
	private final LibraryOfCongressRepository locRepo;
	private final KeywordRepository keyRepo;
	//Needed objects for calling outside methods
	private final PersonController personController;
	private final PersonRoleDocumentController prdController;
	private final OrganizationController organController;
	private final OrganizationRoleDocumentController ordController;
	private final PlaceController placeController;
	private final PlaceRoleDocumentController pprdController;
	private final DocumentEditController docEditController;
	private final NotificationService notifService;
	private final FusionAuthController fusionAuthController;
	
	public DocumentController(DocumentRepository inDocRepo, LanguageRepository inLangRepo, DocumentTypeRepository inTypeRepo, 
		   DocRepositoryRepository inRepo, LibraryOfCongressRepository inlocRepo, KeywordRepository inKey, PersonController inPC, 
		   PersonRoleDocumentController inPRDCont, OrganizationController inOrgController, OrganizationRoleDocumentController inOrdController,
		   PlaceController inPPC, PlaceRoleDocumentController inPPRDC, DocumentEditController inDocEditController, NotificationService inNotifService, 
		   FusionAuthController inFusionAuth) {
		this.docRepo = inDocRepo;
		this.typeRepo = inTypeRepo;
		this.langRepo = inLangRepo;
		this.repoRepo = inRepo;
		this.locRepo = inlocRepo;
		this.keyRepo = inKey;
		this.personController = inPC;
		this.prdController = inPRDCont;
		this.organController = inOrgController;
		this.ordController = inOrdController;
		this.placeController = inPPC;
		this.pprdController = inPPRDC;
		this.docEditController = inDocEditController;
		this.notifService = inNotifService;
		this.fusionAuthController = inFusionAuth;
	}
	
	/*
	 * Returns a list of all documents for testing purposes
	 */
	//Should we have a full dump of all the documents?
	@GetMapping("all")
	public ResponseEntity<Page<Document>> getAllDocuments(@RequestParam(defaultValue = "0") int pageNum, @RequestParam(defaultValue = "20") int pageSize) {
		Pageable page = PageRequest.of(pageNum, pageSize, Sort.by("dateAdded").descending());
		return new ResponseEntity<>(docRepo.findAll(page), HttpStatus.OK);
	}
	
	@GetMapping("all-importID")
	public ResponseEntity<List<String>> getAllImportID() {
		return new ResponseEntity<>(docRepo.findAllImportID(), HttpStatus.OK);
	}
	
	
	public static class SimplifiedDocInfo{
		public String importID, letterDate, docAbstract;
		public List<Keyword> keywords;
		
		public SimplifiedDocInfo(String importID, String letterDate, String docAbstract, List<Keyword> keywords) {
			this.importID = importID;
			this.letterDate = letterDate;
			this.docAbstract = docAbstract;
			this.keywords = keywords;
		}
	}
	
	//Specific front end use
	@GetMapping("simplified-doc-info")
	public ResponseEntity<List<SimplifiedDocInfo>> getSimplifiedDocInfo(){
		List<SimplifiedDocInfo> ret = new ArrayList<>();
		List<Document> docs = docRepo.findAll();
		docs.forEach(d -> ret.add(new SimplifiedDocInfo(d.getImportID(), d.getLetterDate(), d.getDocAbstract(), d.getKeywords())));
		
		return new ResponseEntity<>(ret, HttpStatus.OK);
	}
	
	public static class PersonRole {
		public Person person;
		public List<Role> pRoles;
		public PersonRole(Person p, List<Role> roles) {
			this.person = p;
			this.pRoles = roles;
		}
	}
	public static class OrgRole {
		public Organization org;
		public List<Role> orgRoles;
		public OrgRole(Organization org, List<Role> roles) {
			this.org = org;
			this.orgRoles = roles;
		}
	}
	public static class PlaceRole {
		public Place place;
		public List<Role> placeRoles;
		public PlaceRole(Place place, List<Role> roles) {
			this.place = place;
			this.placeRoles = roles;
		}
	}
	
	public static class SeparateDocInfo {
		public Document doc;
		public List<PersonRole> people;
		public List<OrgRole> orgs;
		public List<PlaceRole> places;
		
		public SeparateDocInfo(Document doc, List<PersonRole> people, List<OrgRole> orgs, List<PlaceRole> places) {
			this.doc = doc;
			this.people = people;
			this.orgs = orgs;
			this.places = places;
		}
		
		public PersonRole findByPerson(Person p) {
			for(PersonRole pr : this.people) {
				if(pr.person.equals(p)) {
					return pr;
				}
			}
			
			return null;
		}
		
		public OrgRole findByOrg(Organization org) {
			for(OrgRole or : this.orgs) {
				if(or.org.equals(org)) {
					return or;
				}
			}
			
			return null;
		}
		
		public PlaceRole findByPlace(Place place) {
			for(PlaceRole pr : this.places) {
				if(pr.place.equals(place)) {
					return pr;
				}
			}
			
			return null;
		}
	}
	
	@GetMapping("/separated")
	public ResponseEntity<SeparateDocInfo> getSeparatedDocInfo(@RequestParam Integer docID){
		Document find = docRepo.findByDocumentID(docID);
		
		if(find == null) {
			return new ResponseEntity<>(null, HttpStatus.CONFLICT);
		}
		
		//Create the return object
		SeparateDocInfo ret = new SeparateDocInfo(new Document(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
		//Parse out the people and their list of roles
		for(PersonRoleDocument prd : find.getPrdSet()) {
			PersonRole temp = ret.findByPerson(prd.getPerson());
			if(temp != null) {
				//Java's equality is by reference and thus this will suffice for updating a list in the ret object
				temp.pRoles.add(prd.getRole());
			}
			else {
				ret.people.add(new PersonRole(prd.getPerson(), new ArrayList<>(List.of(prd.getRole()))));
			}
		}
		//Parse out the orgs and their list of roles
		for(OrganizationRoleDocument ord : find.getOrdSet()) {
			OrgRole temp = ret.findByOrg(ord.getOrg());
			if(temp != null) {
				temp.orgRoles.add(ord.getRole());
			}
			else {
				ret.orgs.add(new OrgRole(ord.getOrg(), new ArrayList<>(List.of(ord.getRole()))));
			}
		}
		//Parse out the places and their list of roles
		for(PlaceRoleDocument pprd : find.getPlacerdSet()) {
			PlaceRole temp = ret.findByPlace(pprd.getPlace());
			if(temp != null) {
				temp.placeRoles.add(pprd.getRole());
			}
			else {
				ret.places.add(new PlaceRole(pprd.getPlace(), new ArrayList<>(List.of(pprd.getRole()))));
			}
		}
		
		//We empty these lists to clean up the json
		find.setOrdSet(new ArrayList<>());
		find.setPrdSet(new ArrayList<>());
		find.setPlacerdSet(new ArrayList<>());
		//Finally we set the document
		ret.doc = find;
		
		return new ResponseEntity<>(ret, HttpStatus.OK);
	}
	
	/*
	 * Returns the document with the given id
	 */
	@GetMapping("/{id}")
	public Optional<Document> getDocument(@PathVariable Integer id) {
		return docRepo.findById(id);
	}
	
	/*
	 * Returns the document with the given id, and the most recently completed transcription or edit
	 */
	@GetMapping("/with-edit/{id}")
	public DocumentAndEdit getDocumentAndEdit(@PathVariable Integer id) {
		Optional<Document> optionalDoc = docRepo.findById(id);
		if (optionalDoc.isEmpty())
			return null;
		Document doc = optionalDoc.get();
		DocumentEdit edit = docEditController.getMostRecentlyCompletedTranscriptionOrEdit(doc.getDocumentID());
		return new DocumentAndEdit(doc, edit);
	}
	
	@GetMapping("collection")
	public List<String> getAllCollections(){
		return docRepo.getAllCollections();
	}
	
	@PostMapping("docs-from-collection")
	public ResponseEntity<List<Document>> getDocsByCollection(@RequestParam String collection, @RequestParam(defaultValue = "0") int pageNum, 
			@RequestParam(defaultValue = "20") int pageSize){
		Pageable page = PageRequest.of(pageNum, pageSize, Sort.by("documentID"));
		
		return new ResponseEntity<>(docRepo.docsFromCollection(collection, page), HttpStatus.OK);
	}
	
	/*
	 * Returns documents in the range [startId] - [endId]
	 * Intended for testing purposes only. To get all documents paginated, search should be used
	 */
	//Get pagination to work
	@GetMapping("/{startId}/{endId}")
	public Iterable<Document> getDocumentsInRange(@PathVariable Integer startId, @PathVariable Integer endId) {
		ArrayList<Integer> ids = new ArrayList<>();
		for (int i = startId; i <= endId; i++) {
			ids.add(i);
		}
		return docRepo.findAllById(ids);
	}
	
	public static class SearchWrapper{
		public Document doc;
		public Religion religion;
		public String sortBy;
		public String direction;
		
		public SearchWrapper() {
		}
	}
	
	public static class PretendPage{
		public List<Document> content;
		public double totalPages;
		
		public PretendPage(List<Document> docs, double totalPages) {
			this.content = docs;
			this.totalPages = totalPages;
		}
	}
	
	@PostMapping("user-search")
	public ResponseEntity<PretendPage> userSearch(@RequestBody SearchWrapper search, @RequestParam(defaultValue = "0") int pageNum, 
			@RequestParam(defaultValue = "20") int pageSize){
		//Start out with the list filtered from UserSpecification
		DocumentSpecification spec = new DocumentSpecification(search.doc);
		List<Document> firstFilter = docRepo.findAll(spec);

		//Can now incomplete word search for keywords
		if(search.doc.getKeywords() != null && !search.doc.getKeywords().isEmpty()) {
			for(int i = 0; i < search.doc.getKeywords().size(); i++) {
				if(search.doc.getKeywords().get(i).getKeyword() != null && !search.doc.getKeywords().get(i).getKeyword().equals(""))
					firstFilter.retainAll(docRepo.docsFromKeyword(search.doc.getKeywords().get(i).getKeyword()));
			}
		}
		
		
		//Can now incomplete word search for locs
		if(search.doc.getLibraryOfCongresses() != null && !search.doc.getLibraryOfCongresses().isEmpty()) {
			for(int i = 0; i < search.doc.getLibraryOfCongresses().size(); i++) {
				if(search.doc.getLibraryOfCongresses().get(i).getSubjectHeading() != null && !search.doc.getLibraryOfCongresses().get(i).getSubjectHeading().equals(""))
					firstFilter.retainAll(docRepo.docsFromLOC(search.doc.getLibraryOfCongresses().get(i).getSubjectHeading()));
			}
		}

		
		//Can now incomplete word search for people
		if(search.doc.getPrdSet() != null && !search.doc.getPrdSet().isEmpty()) {
			for(int i = 0; i < search.doc.getPrdSet().size(); i++) {				
				if(!search.doc.getPrdSet().get(i).getPerson().noData() || !search.doc.getPrdSet().get(i).getRole().getRoleDesc().equals("")) {
					//For readability
					String first = search.doc.getPrdSet().get(i).getPerson().getFirstName();
					String middle = search.doc.getPrdSet().get(i).getPerson().getMiddleName();
					String last = search.doc.getPrdSet().get(i).getPerson().getLastName();
					String prefix = search.doc.getPrdSet().get(i).getPerson().getPrefix();
					String suffix = search.doc.getPrdSet().get(i).getPerson().getSuffix();
					//Does this matter?
					String biography = search.doc.getPrdSet().get(i).getPerson().getBiography();
					String occupation = search.doc.getPrdSet().get(i).getPerson().getOccupation();
					//Should this stay?
					String personLOD = search.doc.getPrdSet().get(i).getPerson().getPersonLOD();
					String role = search.doc.getPrdSet().get(i).getRole().getRoleDesc();
					
					firstFilter.retainAll(docRepo.docsFromPeople(first, middle, last, prefix, suffix, biography, occupation, personLOD, role));
				}
			}
		}
		
		
		//Can now do incomplete word search for organizations
		if(search.doc.getOrdSet() != null && !search.doc.getOrdSet().isEmpty()) {
			for(int i = 0; i < search.doc.getOrdSet().size(); i++) {
				String orgName = search.doc.getOrdSet().get(i).getOrg().getOrgName();
				//Should this stay?
				String orgLOD = search.doc.getOrdSet().get(i).getOrg().getOrgLOD();
				String role = search.doc.getOrdSet().get(i).getRole().getRoleDesc();
				
				if((!orgName.equals("") && !orgLOD.equals("")) || !role.equals(""))
					firstFilter.retainAll(docRepo.docsFromOrgs(orgName, orgLOD, role));
			}
		}

		
		//Should we even search by place???
		//Can now do incomplete word search for places
		if(search.doc.getPlacerdSet() != null && !search.doc.getPlacerdSet().isEmpty()) {
			for(int i = 0; i < search.doc.getPlacerdSet().size(); i++) {
				if(!search.doc.getPlacerdSet().get(i).getPlace().noData() || !search.doc.getPlacerdSet().get(i).getRole().getRoleDesc().equals("")) {
					String county = search.doc.getPlacerdSet().get(i).getPlace().getCounty();
					String country = search.doc.getPlacerdSet().get(i).getPlace().getCountry();
					String stateProv = search.doc.getPlacerdSet().get(i).getPlace().getStateProv();
					String townCity = search.doc.getPlacerdSet().get(i).getPlace().getTownCity();
					String placeDesc = search.doc.getPlacerdSet().get(i).getPlace().getPlaceDesc();
					String placeLOD = search.doc.getPlacerdSet().get(i).getPlace().getPlaceLOD();
					String role = search.doc.getPlacerdSet().get(i).getRole().getRoleDesc();
					
					firstFilter.retainAll(docRepo.docsFromPlaces(county, country, stateProv, townCity, placeDesc, placeLOD, role));
				}
			}
		}

		//If we want to filter by religions
		if(search.religion != null && !search.religion.getReligionDesc().equals("")) {
			List<Document> relFilter = new ArrayList<>();
			boolean temp = false;
			for(int i = 0; i < firstFilter.size(); i++) {
				Document d = firstFilter.get(i);
				
				for(PersonRoleDocument prd : d.getPrdSet()) {
					for(PersonReligion pr : prd.getPerson().getpRelSet()) {
						//If a person has the religion, add the doc to our return list and break because we don't want to search the rest of the religions
						if(pr.getReligion().equals(search.religion)) {
							relFilter.add(d);
							temp = true;
							break;
						}
					}
					//If someone has the religion, break because we don't have to check the rest of the people
					if(temp) {
						break;
					}
				}
				
				//If no person was found with the religion in a document, then search organizations
				//This is done because if a person has the religion, the document is returned despite the organization religions and vice versa
				if(!temp) {
					for(OrganizationRoleDocument ord : d.getOrdSet()) {
						for(OrganizationReligion or : ord.getOrg().getOrgRelSet()) {
							//Duplicate check shouldn't be necessary
							if(or.getReligion().equals(search.religion)) {
								relFilter.add(d);
								temp = true;
								break;
							}
						}
						if(temp == true) {
							break;
						}
					}
				}

				temp = false;
			}
			
			relFilter = sortDocuments(search.sortBy, search.direction, relFilter);
			List<Document> ret = new ArrayList<>();
			
			int startIndex = pageSize * pageNum;
			int index = startIndex;
			while(index < startIndex + pageSize && index < relFilter.size()) {
				ret.add(relFilter.get(index));
				index++;
			}
			
			return new ResponseEntity<>(new PretendPage(ret, Math.ceil(relFilter.size()/pageSize)), HttpStatus.OK);
		}
		
		
		//If we didn't filter by religion
		firstFilter = sortDocuments(search.sortBy, search.direction, firstFilter);
	
		List<Document> ret = new ArrayList<>();
		//startIndex = 14
		//index < 14+7 = 21 && index < 15
		int startIndex = pageSize * pageNum;
		int index = startIndex;
		while(index < startIndex + pageSize && index < firstFilter.size()) {
			ret.add(firstFilter.get(index));
			index++;
		}
		
		return new ResponseEntity<>(new PretendPage(ret, Math.ceil(firstFilter.size()/pageSize)), HttpStatus.OK);
	}
	
	//Method used to sort the list of documents for the search functionality
	public List<Document> sortDocuments(String sortBy, String direction, List<Document> list){
		switch(sortBy) {
			case "collection":
				Collections.sort(list, new Comparator<Document>() {
					@Override
					public int compare(Document doc1, Document doc2) {
						//Null check not necessary but added for extreme errors in database
						if(doc1.getCollection() == null && doc2.getCollection() == null) {
							return 0;
						}
						else if(doc1.getCollection() == null) {
							return 1;
						}
						else if(doc2.getCollection() == null) {
							return -1;
						}
						
						return doc1.getCollection().compareTo(doc2.getCollection());
					}
				});
				if(!direction.equals("asce")) {
					Collections.reverse(list);
				}
				break;
				
			case "typeDesc":
				Collections.sort(list, new Comparator<Document>() {
					@Override
					public int compare(Document doc1, Document doc2) {
						if(doc1.getDocType() == null && doc2.getDocType() == null) {
							return 0;
						}
						else if(doc1.getDocType() == null) {
							return 1;
						}
						else if(doc2.getDocType() == null) {
							return -1;
						}
						
						return doc1.getDocType().getTypeDesc().compareTo(doc2.getDocType().getTypeDesc());
					}
				});
				if(!direction.equals("asce")) {
					Collections.reverse(list);
				}
				break;
			case "importID":
				Collections.sort(list, new Comparator<Document>() {
					@Override
					public int compare(Document doc1, Document doc2) {
						//Null check not necessary but added for extreme errors in database
						if(doc1.getImportID() == null && doc2.getImportID() == null) {
							return 0;
						}
						else if(doc1.getImportID() == null) {
							return 1;
						}
						else if(doc2.getImportID() == null) {
							return -1;
						}
						return doc1.getImportID().compareTo(doc2.getImportID());
					}
				});
				if(!direction.equals("asce")) {
					Collections.reverse(list);
				}
				break;				
			case "langDesc":
				Collections.sort(list, new Comparator<Document>() {
					@Override
					public int compare(Document doc1, Document doc2) {
						if(doc1.getDocLanguage() == null && doc2.getDocLanguage() == null) {
							return 0;
						}
						else if(doc1.getDocLanguage() == null) {
							return 1;
						}
						else if(doc2.getDocLanguage() == null) {
							return -1;
						}
						
						return doc1.getDocLanguage().getLangDesc().compareTo(doc2.getDocLanguage().getLangDesc());
					}
				});
				if(!direction.equals("asce")) {
					Collections.reverse(list);
				}
				break;

			case "status":
				Collections.sort(list, new Comparator<Document>() {
					@Override
					public int compare(Document doc1, Document doc2) {
						//Null checks not necessary but added for extreme errors in database
						if(doc1.getStatus() == null && doc2.getStatus() == null) {
							return 0;
						}
						else if(doc1.getStatus() == null) {
							return 1;
						}
						else if(doc2.getStatus() == null) {
							return -1;
						}
						
						int status1 = Constants.DocumentStatus.statusOrder.indexOf(doc1.getStatus());
						int status2 = Constants.DocumentStatus.statusOrder.indexOf(doc2.getStatus());
						return Integer.compare(status1, status2);
					}
				});
				if(direction.equals("asce")) {
					Collections.reverse(list);
				}
		}
		
		return list;
	}
	
	public static class DocWrapperHash {
		public Document doc;

		public Set<Person> people;
		public Map<Person, Set<Role>> peoplesRoles;

		public Set<Organization> organizations;
		public Map<Organization, Set<Role>> organizationsRoles;

		public Set<Place> places;
		public Map<Place, Set<Role>> placesRoles;

		public List<String> relatedDocs;

		
		public DocWrapperHash() {
			people = new HashSet<>();
			peoplesRoles = new HashMap<>();
			organizations = new HashSet<>();
			organizationsRoles = new HashMap<>();
			places = new HashSet<>();
			placesRoles = new HashMap<>();
			relatedDocs = new ArrayList<>();
		}

		public MultiDocWrapper toMultiDocWrapper() {
			MultiDocWrapper docWrapper = new MultiDocWrapper();
			docWrapper.doc = doc;

			docWrapper.people = new ArrayList<>();
			docWrapper.pRole = new ArrayList<>();
			for (Person person : people) {
				docWrapper.people.add(person);
				List<Role> roleList = new ArrayList<>();
				for (Role role : peoplesRoles.get(person))
					roleList.add(role);
				docWrapper.pRole.add(roleList);
			}

			docWrapper.orgs = new ArrayList<>();
			docWrapper.oRole = new ArrayList<>();
			for (Organization organization : organizations) {
				docWrapper.orgs.add(organization);
				List<Role> roleList = new ArrayList<>();
				for (Role role : organizationsRoles.get(organization))
					roleList.add(role);
				docWrapper.oRole.add(roleList);
			}

			docWrapper.places = new ArrayList<>();
			docWrapper.ppRole = new ArrayList<>();
			for (Place place : places) {
				docWrapper.places.add(place);
				List<Role> roleList = new ArrayList<>();
				for (Role role : placesRoles.get(place))
					roleList.add(role);
				docWrapper.ppRole.add(roleList);
			}

			docWrapper.relatedDocs = relatedDocs;

			return docWrapper;
		}
	}

	public static class MultiDocWrapper {
		public Document doc;

		public List<Person> people;
		public List<List<Role>> pRole;

		public List<Organization> orgs;
		public List<List<Role>> oRole;

		public List<Place> places;
		public List<List<Role>> ppRole;

		public List<String> relatedDocs;

		public MultiDocWrapper() {
		}
	}
	
	//Check for all errors here
	//Break on the first error that we find
	@PostMapping("add-excel")
	public ResponseEntity<String> parseExcelThenAdd(@RequestParam("file") MultipartFile importData, @CookieValue(value = "access_token") Cookie cookie) throws IOException {
		ResponseEntity<String> response = parseExcelThenAddInternal(importData);
		
		Object obj = fusionAuthController.getUser(cookie).getBody();
		User user = obj instanceof User ? (User)obj : null;
		String userID = user != null ? user.email : null;
		if (response.getStatusCode().equals(HttpStatus.OK)) {
			String notifText = "A batch of documents has been added to the database";
			Notification notif = new Notification(Constants.NotificationType.MULTIPLE_DOCUMENTS_ADDED, notifText, userID);
			notif.setExtraText(response.getBody());
			// Notify the admins and the user that added the excel file
			notifService.setupAdminNotification(notif, userID);
		}
		else {
			String notifText = "There was an error adding the batch of documents. Click to see more information.";
			Notification notif = new Notification(Constants.NotificationType.MULTIPLE_DOCUMENTS_ERROR, notifText, userID);
			notif.setExtraText(response.getBody());
			// Notify just the user that added the excel file
			notifService.setupUserNotification(notif);
		}
		
		return response;
	}
	
	public ResponseEntity<String> parseExcelThenAddInternal(MultipartFile importData) throws IOException {//, @CookieValue(value = "access_token") Cookie cookie) throws IOException {
//		if(fusionAuthController.isMetadataExpert(cookie) != null) {
//			return fusionAuthController.isMetadataExpert(cookie);
//		}
		int numDocColumns = 15, numPersonColumns = 13, numPlaceColumns = 10, numOrgColumns = 6, numRelatedLetterColumns = 2;
		
		List<DocWrapperHash> docWrapperHashList = new ArrayList<>();
		Map<String, DocWrapperHash> docWrapperByImportID = new HashMap<>();
		int numEmptyFields = 0;

		if(importData == null) {
			return new ResponseEntity<>("No import data given", HttpStatus.CONFLICT);
		}
		XSSFWorkbook wb = new XSSFWorkbook(importData.getInputStream());

		Set<String> usedImportIDs = new HashSet<>();
		XSSFSheet docSheet = wb.getSheet("document");
		if(docSheet != null) {
			for(int i = 1; i < docSheet.getPhysicalNumberOfRows(); i++) {
				XSSFRow row = docSheet.getRow(i);
				if(row == null) {
					continue;
				}

				String[] tempMeta = new String[numDocColumns];
				double isJInput = 0;
				for (int j = 0; j < tempMeta.length; j++) {
				    XSSFCell cell = row.getCell(j);
				    if (cell == null || cell.getCellType().equals(CellType.BLANK)) {
				    	tempMeta[j] = "";
				    	numEmptyFields++;
				    }
				    else if(cell.getCellType().equals(CellType.STRING))
				        tempMeta[j] = cell.getStringCellValue();
				    else if(cell.getCellType().equals(CellType.NUMERIC))
				    	isJInput = cell.getNumericCellValue();
				}

				if(numEmptyFields < numDocColumns) {
					if(tempMeta[0] == "" || tempMeta[0] == null)
						return new ResponseEntity<>("The document at row: " + (i+1) + " does not have the necessary information (importID). "
								+ "Fix this entry and try again.", HttpStatus.CONFLICT);
					if(docRepo.existsByImportID(tempMeta[0]))
						return new ResponseEntity<>("The document at row: " + (i+1) + " with the importID: " + tempMeta[0] +" already exists within our system."
								+ "\nIf you would like to update this document, please visit the edit page. If the document we have is not the same"
								+ " as the one you are attempting to add, please choose a different, unique importID or change the importID of the"
								+ " document we currently have at the edit page.", HttpStatus.CONFLICT);
					if(usedImportIDs.contains(tempMeta[0]))
						return new ResponseEntity<>("The document at row: " + (i+1) + " with the importID: " + tempMeta[0] + " has the same importID as another"
								+ " document within the excel file. \nIf the documents are different from each other please choose a unique importID for each of "
								+ " them. If they are duplicates, please remove one before trying to upload again", HttpStatus.CONFLICT);
	
					Document doc = new Document();
					doc.setImportID(tempMeta[0]);
					usedImportIDs.add(tempMeta[0]);
					doc.setPdfDesc(tempMeta[1]);
					doc.setPdfURL(tempMeta[2]);
					doc.setInternalPDFName(tempMeta[3]);
					doc.setCollection(tempMeta[4]);
					doc.setDocAbstract(tempMeta[5]);
					doc.setSortingDate(tempMeta[6]);
					doc.setLetterDate(tempMeta[7]);
					doc.setIsJulian((isJInput == 0.0) ? false : true);
					doc.setCustomCitation(tempMeta[9]);
	
					DocumentType dt = new DocumentType();
					dt.setTypeDesc(tempMeta[10]);
					doc.setDocType(dt);
	
					Language lang = new Language();
					lang.setLangDesc(tempMeta[11]);
					doc.setDocLanguage(lang);
	
					DocRepository rp = new DocRepository();
					rp.setRepoDesc(tempMeta[12]);
					doc.setDocRepository(rp);
					
					List<Keyword> keywordList = new ArrayList<>();
					String[] keywords = tempMeta[13].split(";\\s*");
					for(String s : keywords)
						keywordList.add(new Keyword(s));
					doc.setKeywords(keywordList);
					
					List<LibraryOfCongress> locList = new ArrayList<>();
					String[] locs = tempMeta[14].split(";\\s*");
					for(String s : locs) {
						locList.add(new LibraryOfCongress(s));
					}
					doc.setLibraryOfCongresses(locList);
	
					DocWrapperHash tempDocWrapperHash = new DocWrapperHash();
					tempDocWrapperHash.doc = doc;
					docWrapperByImportID.put(doc.getImportID(), tempDocWrapperHash);
					docWrapperHashList.add(tempDocWrapperHash);
					numEmptyFields = 0;
				}
				else {
					break;
				}
			}
		}
		else {
			return new ResponseEntity<>("There was no document sheet found in the excel file. Please make sure that "
					+ "one is added or that it has the correct name.", HttpStatus.CONFLICT);
		}

		numEmptyFields = 0;
		Set<String> existingImportIDRolePersonLOD = new HashSet<>();
		HashMap<String, Person> usedPersonLOD = new HashMap<>();
		XSSFSheet personSheet = wb.getSheet("person");
		if(personSheet != null) {
			for(int i = 1; i < personSheet.getPhysicalNumberOfRows(); i++) {
				XSSFRow row = personSheet.getRow(i);
				if(row == null) {
					continue;
				}

				String[] tempMeta = new String[numPersonColumns];
				for (int j = 0; j < tempMeta.length; j++) {
					XSSFCell cell = row.getCell(j);
					if (cell == null || cell.getCellType().equals(CellType.BLANK)) {
						tempMeta[j] = "";
						numEmptyFields++;
					}
					else if(cell.getCellType().equals(CellType.STRING))
						tempMeta[j] = cell.getStringCellValue();
				}
				if(numEmptyFields < numPersonColumns) {
					if(tempMeta[0] == "")
						return new ResponseEntity<>("The person at row: " + (i+1) + " does not have the necessary information (importID). "
								+ "Fix this entry and try again.", HttpStatus.CONFLICT);
					if(tempMeta[11] == "")
						return new ResponseEntity<>("The person at row: " + (i+1) + " does not have the necessary information (role). "
								+ "Fix this entry and try again.", HttpStatus.CONFLICT);
					if(!usedImportIDs.contains(tempMeta[0]))
						return new ResponseEntity<>("The person at row: " + (i+1) + " with the importID: " + tempMeta[0] +" does not belong"
								+ " to a document that exists on the provided excel file.", HttpStatus.CONFLICT);
					if(tempMeta[12] == "")
						return new ResponseEntity<>("The person at row: " + (i+1) + " does not have the necessary information (personLOD). "
								+ "Fix this entry and try again.", HttpStatus.CONFLICT);
	
					Person person = new Person();
					person.setFirstName(tempMeta[1]);
					person.setMiddleName(tempMeta[2]);
					person.setLastName(tempMeta[3]);
					person.setPrefix(tempMeta[4]);
					person.setSuffix(tempMeta[5]);
					person.setBiography(tempMeta[6]);
					
					if(tempMeta[7].equals(""))
						person.setGender('U');
					else
						person.setGender(tempMeta[7].charAt(0));
					
					person.setBirthDate(tempMeta[8]);
					person.setDeathDate(tempMeta[9]);
					person.setOccupation(tempMeta[10]);					
	
					Role role = new Role();
					role.setRoleDesc(tempMeta[11]);
	
					person.setPersonLOD(tempMeta[12]);
	
					if (personController.diffPerson(person))
						return new ResponseEntity<>("The person at row: " + (i+1) + " already exists, but with different information. "
								+ "Fix this entry and try again.", HttpStatus.CONFLICT);
					
					String personLOD = person.getPersonLOD();
					if(usedPersonLOD.containsKey(personLOD)) {
						if(!person.equals(usedPersonLOD.get(personLOD))) {
							return new ResponseEntity<>("The person at row: " + (i+1) + " already exists in the excel sheet with different information", HttpStatus.CONFLICT);
						}
					}
					else {
						usedPersonLOD.put(personLOD, person);
					}
	
					// Check if this combination of importID, role, and personLOD already exists
					String importIDRolePersonLOD = tempMeta[0] + "&" + role.getRoleDesc() + "&" + person.getPersonLOD();
					if(existingImportIDRolePersonLOD.contains(importIDRolePersonLOD)) {
						return new ResponseEntity<>("The person at row: " + (i+1) + " is a duplicate on this excel file."
								+ "Fix this entry and try again.", HttpStatus.CONFLICT);
					} 
					else {
						existingImportIDRolePersonLOD.add(importIDRolePersonLOD);
					}
	
					DocWrapperHash docWrapperHash = docWrapperByImportID.get(tempMeta[0]);
					if (!docWrapperHash.people.contains(person)) {
						docWrapperHash.people.add(person);
						docWrapperHash.peoplesRoles.put(person, new HashSet<>());
					}
					
					docWrapperHash.peoplesRoles.get(person).add(role);
					numEmptyFields = 0;
				}
				else {
					break;
				}
			}
		}

		numEmptyFields = 0;
		Set<String> existingOrgName = new HashSet<>();
		HashMap<String, Organization> usedOrgName = new HashMap<>();
		XSSFSheet orgSheet = wb.getSheet("organization");
		if(orgSheet != null) {
			for(int i = 1; i < orgSheet.getPhysicalNumberOfRows(); i++) {
				XSSFRow row = orgSheet.getRow(i);
				if(row == null) {
					continue;
				}

				String[] tempMeta = new String[numOrgColumns];
				for (int j = 0; j < tempMeta.length; j++) {
					XSSFCell cell = row.getCell(j);
					if (cell == null || cell.getCellType().equals(CellType.BLANK)) {
						tempMeta[j] = "";
						numEmptyFields++;
					}
					else if(cell.getCellType().equals(CellType.STRING))
						tempMeta[j] = cell.getStringCellValue();
				}

				if(numEmptyFields < numOrgColumns) {
					if(tempMeta[0] == "")
						return new ResponseEntity<>("The organization at row: " + (i+1) + " does not have the necessary information (importID). "
								+ "Fix this entry and try again.", HttpStatus.CONFLICT);
					if(tempMeta[1] == "")
						return new ResponseEntity<>("The organization at row: " + (i+1) + " does not have the necessary information (organizationName). "
								+ "Fix this entry and try again.", HttpStatus.CONFLICT);
					if(tempMeta[5] == "")
						return new ResponseEntity<>("The organization at row: " + (i+1) + " does not have the necessary information (role). "
								+ "Fix this entry and try again.", HttpStatus.CONFLICT);
					if(!usedImportIDs.contains(tempMeta[0]))
						return new ResponseEntity<>("The organization at row: " + (i+1) + " with the importID: " + tempMeta[0] +" does not belong"
								+ " to a document that exists on the provided excel file.", HttpStatus.CONFLICT);
	
					Organization organization = new Organization();
					organization.setOrgName(tempMeta[1]);
					organization.setFormationDate(tempMeta[2]);
					organization.setDissolutionDate(tempMeta[3]);
					organization.setOrgLOD(tempMeta[4]);
	
					Role role = new Role();
					role.setRoleDesc(tempMeta[5]);
	
					if (organController.diffOrg(organization))
						return new ResponseEntity<>("The organization at row: " + (i+1) + " already exists, but with different information. "
								+ "Fix this entry and try again.", HttpStatus.CONFLICT);
	
					String orgName = organization.getOrgName();
					if(usedOrgName.containsKey(orgName)) {
						if(!organization.equals(usedOrgName.get(orgName))) {
							return new ResponseEntity<>("The organization at row: " + (i+1) + " already exists in the excel sheet with different information", HttpStatus.CONFLICT);
						}
					}
					else {
						usedOrgName.put(orgName, organization);
					}
					
					String orgNameImportIDRole = organization.getOrgName() + "&" + tempMeta[0] + "&" + role.getRoleDesc();
					if (existingOrgName.contains(orgNameImportIDRole)) {
						return new ResponseEntity<>("The organization at row: " + (i+1) + " is a duplicate on this excel file."
								+ "Fix this entry and try again.", HttpStatus.CONFLICT);
					} 
					else {
						existingOrgName.add(orgNameImportIDRole);
					}
	
					DocWrapperHash docWrapperHash = docWrapperByImportID.get(tempMeta[0]);
					if (!docWrapperHash.organizations.contains(organization)) {
						docWrapperHash.organizations.add(organization);
						docWrapperHash.organizationsRoles.put(organization, new HashSet<>());
					}
					docWrapperHash.organizationsRoles.get(organization).add(role);
					numEmptyFields = 0;
				}
				else {
					break;
				}
			}
		}

		numEmptyFields = 0;
		Set<String> existingPlace = new HashSet<>();
		HashMap<String, Place> usedLatAndLong = new HashMap<>();
		XSSFSheet placeSheet = wb.getSheet("place");
		if(placeSheet != null) {
			for(int i = 1; i < placeSheet.getPhysicalNumberOfRows(); i++) {
				XSSFRow row = placeSheet.getRow(i);
				if(row == null) {
					continue;
				}

				String[] tempMeta = new String[numPlaceColumns];
				Place place = new Place();
				for (int j = 0; j < tempMeta.length; j++) {
					XSSFCell cell = row.getCell(j);
					if (cell == null || cell.getCellType().equals(CellType.BLANK)) {
						tempMeta[j] = "";
						numEmptyFields++;
					}
					else if(cell.getCellType().equals(CellType.STRING))
						tempMeta[j] = cell.getStringCellValue();
					else if(cell.getCellType().equals(CellType.NUMERIC))
						if (place.getLatitude() == null || place.getLatitude() == 0.0)
							place.setLatitude(cell.getNumericCellValue());
						else
							place.setLongitude(cell.getNumericCellValue());
				}

				if(numEmptyFields < numPlaceColumns) {
					if(tempMeta[0] == "")
						return new ResponseEntity<>("The place at row: " + (i+1) + " does not have the necessary information (importID). "
								+ "Fix this entry and try again.", HttpStatus.CONFLICT);
					if(tempMeta[9] == "")
						return new ResponseEntity<>("The place at row: " + (i+1) + " does not have the necessary information (role). "
								+ "Fix this entry and try again.", HttpStatus.CONFLICT);
					if(!usedImportIDs.contains(tempMeta[0]))
						return new ResponseEntity<>("The place at row: " + (i+1) + " with the importID: " + tempMeta[0] +" does not belong"
								+ " to a document that exists on the provided excel file.", HttpStatus.CONFLICT);
					if (place.getLatitude() == null || place.getLatitude() == 0.0)
						return new ResponseEntity<>("The place at row: " + (i+1) + " does not have the necessary information (placeLat). "
								+ "Fix this entry and try again.", HttpStatus.CONFLICT);
					if (place.getLongitude() == null || place.getLongitude() == 0.0)
						return new ResponseEntity<>("The place at row: " + (i+1) + " does not have the necessary information (placeLong). "
								+ "Fix this entry and try again.", HttpStatus.CONFLICT);
	
					place.setCountry(tempMeta[3]);
					place.setStateProv(tempMeta[4]);
					place.setCounty(tempMeta[5]);
					place.setTownCity(tempMeta[6]);
					place.setPlaceDesc(tempMeta[7]);
					place.setPlaceLOD(tempMeta[8]);
	
					Role role = new Role();
					role.setRoleDesc(tempMeta[9]);
	
					if (placeController.diffPlace(place))
						return new ResponseEntity<>("The place at row: " + (i+1) + " already exists, but with different information. "
								+ "Fix this entry and try again.", HttpStatus.CONFLICT);
					
					String latLong = place.getLatitude() + "&" + place.getLongitude();
					if(usedLatAndLong.containsKey(latLong)) {
						if(!place.equals(usedLatAndLong.get(latLong))) {
							return new ResponseEntity<>("The place at row: " + (i+1) + " already exists in the excel sheet with different information", HttpStatus.CONFLICT);
						}
					}
					else {
						usedLatAndLong.put(latLong, place);
					}
	
					String latitudeLongitudeRole = place.getLatitude() + "&" + place.getLongitude() + "&" + tempMeta[0] + "&" + role.getRoleDesc();
					if (existingPlace.contains(latitudeLongitudeRole)) {
						return new ResponseEntity<>("The place at row: " + (i+1) + " is a duplicate on this excel file. "
								+ "Fix this entry and try again.", HttpStatus.CONFLICT);
					} 
					else {
						existingPlace.add(latitudeLongitudeRole);
					}
	
					DocWrapperHash docWrapperHash = docWrapperByImportID.get(tempMeta[0]);
					if (!docWrapperHash.places.contains(place)) {
						docWrapperHash.places.add(place);
						docWrapperHash.placesRoles.put(place, new HashSet<>());
					}
					docWrapperHash.placesRoles.get(place).add(role);
					numEmptyFields = 0;
				}
				else {
					break;
				}
			}
		}

		numEmptyFields = 0;
		Set<String> existingRelatedLetters = new HashSet<>();
		XSSFSheet relatedLetters = wb.getSheet("relatedLetters");
		if(relatedLetters != null) {
			for(int i = 1; i < relatedLetters.getPhysicalNumberOfRows(); i++) {
				XSSFRow row = relatedLetters.getRow(i);
				if(row == null) {
					continue;
				}

				String[] tempMeta = new String[numRelatedLetterColumns];
				for (int j = 0; j < tempMeta.length; j++) {
					XSSFCell cell = row.getCell(j);
					if (cell == null || cell.getCellType().equals(CellType.BLANK)) {
						tempMeta[j] = "";
						numEmptyFields++;
					}
					else if(cell.getCellType().equals(CellType.STRING))
						tempMeta[j] = cell.getStringCellValue();
				}

				if(numEmptyFields < numRelatedLetterColumns) {
					if(tempMeta[0] == "")
						return new ResponseEntity<>("The relatedLetters at row: " + (i+1) + " does not have the necessary information (importID). "
								+ "Fix this entry and try again.", HttpStatus.CONFLICT);
					if(tempMeta[1] == "")
						return new ResponseEntity<>("The relatedLetters at row: " + (i+1) + " does not have the necessary information (relatedImportID). "
								+ "Fix this entry and try again.", HttpStatus.CONFLICT);
					if(!usedImportIDs.contains(tempMeta[0]))
						return new ResponseEntity<>("The relatedLetters at row: " + (i+1) + " has an impordID: " + tempMeta[0]
								+ " that does not exist on this excel file.", HttpStatus.CONFLICT);
					if(!docRepo.existsByImportID(tempMeta[1]) && !usedImportIDs.contains(tempMeta[1]))
						return new ResponseEntity<>("The relatedLetters at row: " + (i+1) + " has a relatedImportID: " + tempMeta[1]
								+ " that does not exist within our system or on this excel file.", HttpStatus.CONFLICT);
					if(tempMeta[0].toLowerCase().equals(tempMeta[1].toLowerCase())){
						return new ResponseEntity<>("You cannot relate a document to itself. Error at row: " + (i+1), HttpStatus.CONFLICT);
					}
	
					String newRelatedLetters = tempMeta[0] + tempMeta[1];
					if (existingRelatedLetters.contains(newRelatedLetters)) {
						return new ResponseEntity<>("The relatedLetters at row: " + (i+1) + " is a duplicate on this excel file."
								+ "Fix this entry and try again.", HttpStatus.CONFLICT);
					} else {
						existingRelatedLetters.add(newRelatedLetters);
					}
	
					DocWrapperHash docWrapperHash = docWrapperByImportID.get(tempMeta[0]);
					docWrapperHash.relatedDocs.add(tempMeta[1]);
					numEmptyFields = 0;
				}
				else {
					break;
				}
			}
		}
		
		wb.close();

		List<MultiDocWrapper> docWrapperList = new ArrayList<>();
		for (DocWrapperHash docWrapperHash : docWrapperHashList) {
			docWrapperList.add(docWrapperHash.toMultiDocWrapper());
		}
		
		return addMultiDocs(docWrapperList);
	}
	
	public ResponseEntity<String> addMultiDocs(List<MultiDocWrapper> wrapList) {
		List<Document> docList = new ArrayList<>();
		for(MultiDocWrapper wrap : wrapList) {
			Document result = addDocToDB(wrap.doc);
			//Errors are already handled and the document should not be a duplicate or have any errors with it
			docList.add(result);
			
			if(wrap.people != null) {
				List<Person> peopleList = personController.addPeople(wrap.people);
				
				//Better than the enhanced for loop because now we can just link the role to a person very easily
				for(int i = 0; i < peopleList.size(); i++) {
					//Get the first person
					Person person = peopleList.get(i);
					//Then, get the index of that person from the original list
					//This gives us the index that their list of roles should be at
					List<Role> roles = wrap.pRole.get(i);
					
					for(Role r : roles) {
						PersonRoleDocument newPrd = new PersonRoleDocument(person, r, result);
						prdController.addPersonRoleDocument(newPrd);
					}
				}
			}
			
			if(wrap.orgs != null) {
				List<Organization> orgList = organController.addOrganizations(wrap.orgs);
				
				for(Organization org : orgList) {
					List<Role> roles = wrap.oRole.get(wrap.orgs.indexOf(org));
					
					for(Role r : roles) {
						OrganizationRoleDocument newORD = new OrganizationRoleDocument(org, r, result);
						ordController.addOrganizationRoleDocument(newORD);
					}
				}
			}
			
			if(wrap.places != null) {
				List<Place> placeList = placeController.addPlaces(wrap.places);
				
				for(Place place : placeList) {
					List<Role> roles = wrap.ppRole.get(wrap.places.indexOf(place));
					
					for(Role r : roles) {
						PlaceRoleDocument newPPRD = new PlaceRoleDocument(place, r, result);
						pprdController.addPlaceRoleDocument(newPPRD);
					}
				}
			}
		}
		
		//Must do this after we add all the docs from the excel file because the related docs can be one of the docs in the excel file to be added
		List<Document> relatedDocList = new ArrayList<>();
		for(MultiDocWrapper wrap : wrapList) {
			//Related documents is similar to keywords, a list of strings that we need to find then link to the document
			if(wrap.relatedDocs != null) {			
				for(String importID : wrap.relatedDocs) {
					if(importID.equals(wrap.doc.getImportID())){
						return new ResponseEntity<>("You cannot relate a document to itself.", HttpStatus.CONFLICT);
					}
					Document temp = docRepo.findByImportID(importID);
					if(temp != null)
						relatedDocList.add(temp);
				}
				
				wrap.doc.setRelatedDocuments(relatedDocList);
				updateRelatedDocs(relatedDocList, wrap.doc);
			}
		}
		
		return new ResponseEntity<>("Data was added successfully!", HttpStatus.OK);
	}
	
	/*
	 * Upload an image to the server
	 * Will be accessible at /images/[fileName]
	 */
	@PostMapping("upload")
	public ResponseEntity<String> uploadToLocalFileSystem(@RequestParam("file") MultipartFile[] file, @RequestParam String importID) throws IOException { 
															//@CookieValue(value = "access_token") Cookie cookie)  {
//		if(fusionAuthController.isMetadataExpert(cookie) != null) {
//			return fusionAuthController.isMetadataExpert(cookie);
//		}
		if(docRepo.existsByImportID(importID))
			return new ResponseEntity<>("The document you tried to add already exists within our system.", HttpStatus.CONFLICT);
		
		if(file == null || file.length < 1)
			return new ResponseEntity<>("There was no images submitted.", HttpStatus.CONFLICT);
		
		PDDocument newFile = new PDDocument();
		
		//Create as many pages as there are images
		for(int i = 0; i < file.length; i++) {			
			if(file[i].getContentType().toString().equals("application/pdf")) {
				File image = new File(file[i].getOriginalFilename());
				image.createNewFile();
				
				FileOutputStream fos = new FileOutputStream(image);
				fos.write(file[i].getBytes());
				fos.close();
				
				
				if(image.length() >= 1048576) {
					return new ResponseEntity<>("One of the images you tried to upload is too big! The max image size is 1048576 MB.", HttpStatus.CONFLICT);
				}
				
				PDDocument old = PDDocument.load(image);
				for(PDPage p : old.getPages()) {
					newFile.addPage(p);
				}
				
				old.close();
			}
			else {
				File image = new File(file[i].getOriginalFilename());
				PDPage newPage = new PDPage();
				newFile.addPage(newPage);
				
				image.createNewFile();
				FileOutputStream fos = new FileOutputStream(image);
				fos.write(file[i].getBytes());
				fos.close();
				
				PDImageXObject pdImage = PDImageXObject.createFromFileByContent(image, newFile);
				PDPageContentStream contentStream = new PDPageContentStream(newFile, newPage);
				
				//To resize image if too large for pdf page
				int newHeight = pdImage.getHeight();
				int newWidth = pdImage.getWidth();
				
				if(pdImage.getHeight() > 792) {
					newHeight = 792;
					newWidth = (newHeight * pdImage.getWidth())/pdImage.getHeight();
				}
				if(newWidth > 612) {
					newWidth = 612;
					newHeight = (newWidth * pdImage.getHeight())/pdImage.getWidth();
				}
				
				contentStream.drawImage(pdImage, 0, 0, newWidth, newHeight);
				contentStream.close();
			}
		}
		
		newFile.save(Constants.LOCAL_IMAGE_DIRECTORY + importID + Constants.FINAL_STORAGE_TYPE);
		newFile.close();
		
		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
				.path(Constants.EXTERNAL_IMAGE_DIRECTORY)
				.path(importID + Constants.FINAL_STORAGE_TYPE)
				.toUriString();
		
		return new ResponseEntity<>(fileDownloadUri, HttpStatus.OK);
	}
	
	public void deleteImage(String importID) {
		File file = new File(Constants.LOCAL_IMAGE_DIRECTORY + importID + Constants.FINAL_STORAGE_TYPE);
		if(file != null)
			file.delete();
	}
	
	public static class SingleDocWrapper {		
		public Document doc;
		public List<PersonRole> people;
		public List<OrgRole> orgs;
		public List<PlaceRole> places;
		public List<String> relatedDocs;
		public SingleDocWrapper() {
			people = new ArrayList<>();
			orgs = new ArrayList<>();
			places = new ArrayList<>();
			relatedDocs = new ArrayList<>();
		}
	}

	@PostMapping("add-one-doc")
	public ResponseEntity<String> addOneDoc(@RequestBody SingleDocWrapper wrap){//, @CookieValue(value = "access_token") Cookie cookie) {
//		if(fusionAuthController.isMetadataExpert(cookie) != null) {
//			return fusionAuthController.isMetadataExpert(cookie);
//		}
		if(docRepo.existsByImportID(wrap.doc.getImportID())) {
			deleteImage(wrap.doc.getImportID());
			return new ResponseEntity<>("The document you tried to add already exists within our system.", HttpStatus.CONFLICT);
		}
		
		List<Person> tempPeople = new ArrayList<>();
		List<PersonRoleDocument> prdList = new ArrayList<>();
		if(wrap.people != null && !wrap.people.isEmpty()) {
			for(PersonRole p : wrap.people) {
				if(personController.diffPerson(p.person)) {
					deleteImage(wrap.doc.getImportID());
					return new ResponseEntity<>("It seems that the following person you tried to add has an lod value that is already being used by "
							+ "another person in our database." + p.person.toString() + "\nPlease use a different lod or update the person "
							+ "we have in the database and try again.", HttpStatus.CONFLICT);
				}
				
				tempPeople.add(p.person);
				for(Role r : p.pRoles) {
					PersonRoleDocument newPRD = new PersonRoleDocument(p.person, r, wrap.doc);
					
					if(!prdList.contains(newPRD))
						prdList.add(newPRD);
				}
			}
		}
		
		List<Organization> tempOrgs = new ArrayList<>();
		List<OrganizationRoleDocument> ordList = new ArrayList<>();
		if(wrap.orgs != null && !wrap.orgs.isEmpty()) {
			for(OrgRole o : wrap.orgs) {
				if(organController.diffOrg(o.org)) {
					deleteImage(wrap.doc.getImportID());
					return new ResponseEntity<>("It seems that the following organization you tried to add has the same name as an organization "
							+ "that is already in our database: " + o.org.toString() + "\nPlease use a different name or update the organization "
							+ "we have in the database", HttpStatus.CONFLICT);
				}
				
				tempOrgs.add(o.org);
				for(Role r : o.orgRoles) {
					OrganizationRoleDocument newORD = new OrganizationRoleDocument(o.org, r, wrap.doc);
					
					if(!ordList.contains(newORD))
						ordList.add(newORD);
				}
				
			}
		}
		
		List<Place> tempPlaces = new ArrayList<>();
		List<PlaceRoleDocument> pprdList = new ArrayList<>();
		if(wrap.places != null && !wrap.places.isEmpty()) {
			for(PlaceRole p : wrap.places) {
				if(placeController.diffPlace(p.place)) {
					deleteImage(wrap.doc.getImportID());
					return new ResponseEntity<>("It seems that the following place you tried to add has the same latitude and longitude as "
							+ "another place that is already in our database: " + p.place.toString() + "\nPlease use a different values or update the place "
							+ "we have in the database", HttpStatus.CONFLICT);
				}
				
				tempPlaces.add(p.place);
				for(Role r : p.placeRoles) {
					PlaceRoleDocument newPPRD = new PlaceRoleDocument(p.place, r, wrap.doc);
					
					if(!pprdList.contains(newPPRD))
						pprdList.add(newPPRD);
				}
			}
		}
		
		//Do we need this since we will be "searching" for the docs?
		if(wrap.relatedDocs != null && !wrap.relatedDocs.isEmpty()) {
			for(String importID : wrap.relatedDocs) {
				if(!docRepo.existsByImportID(importID)) {
					deleteImage(wrap.doc.getImportID());
					//This shouldn't be hit since the front end will be searching for the data before sending it to be added
					return new ResponseEntity<>("The following document you tried to link to the original document "
							+ "does not exist in our system: " + importID + "", HttpStatus.CONFLICT);
				}
				if(importID.toLowerCase().equals(wrap.doc.getImportID().toLowerCase())){
					return new ResponseEntity<>("You cannot relate a document to itself.", HttpStatus.CONFLICT);
				}
			}
		}
		
		Document result = addDocToDB(wrap.doc);
		
		List<Document> relatedDocList = new ArrayList<>();
		//Related documents is similar to keywords, a list of strings that we need to find then link to the document
		if(wrap.relatedDocs != null && !wrap.relatedDocs.isEmpty()) {			
			for(String importID : wrap.relatedDocs) {
				if(!importID.equals("")) {
					Document temp = docRepo.findByImportID(importID);
					//Gets rid of duplicates or we can throw an error??
					if(!relatedDocList.contains(temp))
						relatedDocList.add(temp);
				}
			}
			
			result.setRelatedDocuments(relatedDocList);
			updateRelatedDocs(relatedDocList, result);
		}
		
				
		if(wrap.people != null && !wrap.people.isEmpty()) {
			//Need to make the personLOD required on the front
			List<Person> peopleList = personController.addPeople(tempPeople);
			
			for(int i = 0; i < prdList.size(); i++) {
				prdList.get(i).setDocument(result);
				prdList.get(i).setPerson(peopleList.get(i));
				
				prdController.addPersonRoleDocument(prdList.get(i));
			}
		}
			
		if(wrap.orgs != null && !wrap.orgs.isEmpty()) {
			//Need to also make sure that orgname is there on front end
			List<Organization> orgList = organController.addOrganizations(tempOrgs);
			
			for(int i = 0; i < ordList.size(); i++) {
				ordList.get(i).setDocument(result);
				ordList.get(i).setOrg(orgList.get(i));
				
				ordController.addOrganizationRoleDocument(ordList.get(i));
			}
		}
		
		if(wrap.places != null && !wrap.places.isEmpty()) {
			List<Place> placeList = placeController.addPlaces(tempPlaces);
			
			for(int i = 0; i < pprdList.size(); i++) {
				pprdList.get(i).setDocument(result);
				pprdList.get(i).setPlace(placeList.get(i));
				
				pprdController.addPlaceRoleDocument(pprdList.get(i));
			}
		}
		
		String notifText = String.format("Document %d has been added to the database", result.getDocumentID());
		Notification notif = new Notification(Constants.NotificationType.DOCUMENT_ADDED, notifText, null);
		notifService.setupAdminNotification(notif);
		return new ResponseEntity<>("Data was added successfully!", HttpStatus.OK);
	}
	
	public Document addDocToDB(Document newDoc) {
		//Force language(description)
		if(newDoc.getDocLanguage() != null && !newDoc.getDocLanguage().getLangDesc().equals("")) {
			Language newLang = langRepo.findByLangDesc(newDoc.getDocLanguage().getLangDesc());
			
			if(newLang == null) {
				newLang = langRepo.save(newDoc.getDocLanguage());
			}

			newDoc.setDocLanguage(newLang);
		}
		else {
			newDoc.setDocLanguage(null);
		}
		
		//Force docType
		if(newDoc.getDocType() != null && !newDoc.getDocType().getTypeDesc().equals("")) {
			DocumentType newType = typeRepo.findByTypeDesc(newDoc.getDocType().getTypeDesc());
			
			if(newType == null) {
				newType = typeRepo.save(newDoc.getDocType());
			}

			newDoc.setDocType(newType);
		}
		else {
			newDoc.setDocType(null);
		}
		
		//Force repoURL
		if(newDoc.getDocRepository() != null && !newDoc.getDocRepository().getRepoURL().equals("")) {
			DocRepository newRepo = repoRepo.findByRepoURL(newDoc.getDocRepository().getRepoURL());
			
			if(newRepo == null) {
				newRepo = repoRepo.save(newDoc.getDocRepository());
			}

			newDoc.setDocRepository(newRepo);
		}
		else {
			newDoc.setDocRepository(null);
		}
		
		//Force subject heading if added
		if(newDoc.getLibraryOfCongresses() != null) {
			List<LibraryOfCongress> locBuffer = new ArrayList<>();
			
			for(LibraryOfCongress loc : newDoc.getLibraryOfCongresses()) {
				LibraryOfCongress temp = locRepo.findBySubjectHeading(loc.getSubjectHeading());
				
				if(temp == null && !loc.getSubjectHeading().equals("")) {
					temp = locRepo.save(loc);
				}
				
				if(!locBuffer.contains(temp) && !loc.getSubjectHeading().equals("")) {
						locBuffer.add(temp);
				}
			}

			newDoc.setLibraryOfCongresses(locBuffer);
		}
		
		//Force keyword
		if(newDoc.getKeywords() != null) {
			List<Keyword> keywordBuffer = new ArrayList<>();
			
			for(Keyword key : newDoc.getKeywords()) {
				Keyword temp = keyRepo.findByKeyword(key.getKeyword());
				
				if(temp == null && !key.getKeyword().equals("")) {
					temp = keyRepo.save(key);
				}
				
				if(!keywordBuffer.contains(temp) && !key.getKeyword().equals("")) {
					keywordBuffer.add(temp);
				}
			}
			
			newDoc.setKeywords(keywordBuffer);
		}
		
		//Checking to see if the status is something that was not updated
		if(newDoc.getStatus() == null || newDoc.getStatus().equals(""))
			newDoc.setStatus(Constants.DocumentStatus.NEEDS_TRANSCRIBING);
		
		if(newDoc.getDateAdded() == null)
			newDoc.setDateAdded(new Date(System.currentTimeMillis()));
		
		return docRepo.save(newDoc);		
	}
	
	public void updateRelatedDocs(List<Document> docList, Document relatedDoc) {
		if(docList != null) {
			for(Document d : docList) {
				d.getRelatedDocuments().add(relatedDoc);
				docRepo.save(d);
			}
		}
	}
	
	@PostMapping("update-document")
	public ResponseEntity<String> updateDoc(@RequestBody SingleDocWrapper wrap){//, @CookieValue(value = "access_token") Cookie cookie) {
//		if(fusionAuthController.isMetadataExpert(cookie) != null) {
//			return fusionAuthController.isMetadataExpert(cookie);
//		}
		if(wrap.doc.getDocumentID() == null || !docRepo.existsById(wrap.doc.getDocumentID())) {
			return new ResponseEntity<>("This is not a valid document", HttpStatus.CONFLICT);
		}
		if(wrap.doc.getImportID() == null || wrap.doc.getImportID().equals("")) {
			return new ResponseEntity<>("There was no importID entered. Fix this and try again.", HttpStatus.CONFLICT);
		}
		if(docRepo.existsByImportID(wrap.doc.getImportID()) && !docRepo.findByImportID(wrap.doc.getImportID()).getDocumentID().equals(wrap.doc.getDocumentID())) {
			return new ResponseEntity<>("The entered importID is already being used. Fix this and try again.", HttpStatus.CONFLICT);
		}
		
		HashMap<String, Person> personExistsInList = new HashMap<>();
		List<PersonRoleDocument> prdList = new ArrayList<>();
		if(wrap.people != null && !wrap.people.isEmpty()) {
			//LOD's are forced
			for(PersonRole p : wrap.people) {
				if(personController.diffPerson(p.person)) {
					return new ResponseEntity<>("It seems that the following person you tried to add has an lod value that is already being used by "
							+ "another person in our database." + p.person.toString() + "\nPlease use a different lod or update the person "
							+ "we have in the database and try again.", HttpStatus.CONFLICT);
				}
				if(personExistsInList.containsKey(p.person.getPersonLOD())) {
					if(!p.person.equals(personExistsInList.get(p.person.getPersonLOD()))) {
						return new ResponseEntity<>("The person with the personLOD value of: " + p.person.getPersonLOD() + " is being added twice with different information. "
								+ "Fix this error and try again.", HttpStatus.CONFLICT);
					}
				}
				else {
					personExistsInList.put(p.person.getPersonLOD(), p.person);
				}
				
				for(Role r : p.pRoles) {
					PersonRoleDocument newPRD = new PersonRoleDocument(p.person, r, wrap.doc);
					if(!prdList.contains(newPRD)) {
						prdList.add(newPRD);
					}
				}
			}
		}
		
		HashMap<String, Organization> orgExistsInList = new HashMap<>();
		List<OrganizationRoleDocument> ordList = new ArrayList<>();
		if(wrap.orgs != null && !wrap.orgs.isEmpty()) {
			for(OrgRole o : wrap.orgs) {
				if(organController.diffOrg(o.org)) {
					return new ResponseEntity<>("It seems that the following organization you tried to add has the same name as an organization "
							+ "that is already in our database: " + o.org.toString() + "\nPlease use a different name or update the organization "
							+ "we have in the database", HttpStatus.CONFLICT);
				}
				
				if(orgExistsInList.containsKey(o.org.getOrgName())) {
					if(!o.org.equals(orgExistsInList.get(o.org.getOrgName()))) {
						return new ResponseEntity<>("The organization with the orgName value of: " + o.org.getOrgName() + " is being added twice with different information. "
								+ "Fix this error and try again.", HttpStatus.CONFLICT);
					}
				}
				else {
					orgExistsInList.put(o.org.getOrgName(), o.org);
				}
				for(Role r : o.orgRoles) {
					OrganizationRoleDocument newORD = new OrganizationRoleDocument(o.org, r, wrap.doc);
					if(!ordList.contains(newORD))
						ordList.add(newORD);
				}
			}
		}
		
		HashMap<String, Place> placeExistsInList = new HashMap<>();
		List<PlaceRoleDocument> pprdList = new ArrayList<>();
		if(wrap.places != null && !wrap.places.isEmpty()) {
			for(PlaceRole p : wrap.places) {
				if(placeController.diffPlace(p.place)) {
					return new ResponseEntity<>("It seems that the following place you tried to add has the same latitude and longitude as "
							+ "another place that is already in our database: " + p.place.toString() + "\nPlease use a different values or update the place "
							+ "we have in the database", HttpStatus.CONFLICT);
				}
				
				String latAndLong = p.place.getLatitude() + "&" + p.place.getLongitude();
				if(placeExistsInList.containsKey(latAndLong)) {
					if(!p.place.equals(placeExistsInList.get(latAndLong))) {
						return new ResponseEntity<>("The place with the latitude value of: " + p.place.getLatitude() + " and longitude value of: " + p.place.getLongitude() 
								+ " is being added twice with different information. "
								+ "Fix this error and try again.", HttpStatus.CONFLICT);
					}
				}
				else {
					placeExistsInList.put(latAndLong, p.place);
				}
				for(Role r : p.placeRoles) {
					PlaceRoleDocument newPPRD = new PlaceRoleDocument(p.place, r, wrap.doc);
					if(!pprdList.contains(newPPRD))
						pprdList.add(newPPRD);
				}
			}
		}
		
		//Do we need this since we will be "searching" for the docs?
		if(wrap.relatedDocs != null && !wrap.relatedDocs.isEmpty()) {
			for(String importID : wrap.relatedDocs) {
				if(!docRepo.existsByImportID(importID)) {
					//This shouldn't be hit since the front end will be searching for the data before sending it to be added
					return new ResponseEntity<>("The following document you tried to link to the original document "
							+ "does not exist in our system: " + importID + "", HttpStatus.CONFLICT);
				}
				if(importID.toLowerCase().equals(wrap.doc.getImportID().toLowerCase())){
					return new ResponseEntity<>("You cannot relate a document to itself.", HttpStatus.CONFLICT);
				}
			}
		}
		
		//Go through all document data here
		Document result = addDocToDB(wrap.doc);
		
		List<Document> relatedDocList = new ArrayList<>();
		//Related documents is similar to keywords, a list of strings that we need to find then link to the document
		if(wrap.relatedDocs != null && !wrap.relatedDocs.isEmpty()) {			
			for(String importID : wrap.relatedDocs) {
				Document temp = docRepo.findByImportID(importID);
				if(!relatedDocList.contains(temp) && temp != null)
					relatedDocList.add(temp);	
			}
			
			result.setRelatedDocuments(relatedDocList);
			updateRelatedDocs(relatedDocList, result);
		}
		
		//Delete all relations and add the ones that have been sent
		prdController.deleteByDocument(result);
		if(wrap.people != null && !wrap.people.isEmpty()) {
			
			List<Person> peopleList = new ArrayList<>();
			for(PersonRole p : wrap.people) {
				//Need to make the personLOD required on the front
				peopleList.add(personController.addPerson(p.person));
			}
			
			for(int i = 0; i < prdList.size(); i++) {
				prdList.get(i).setDocument(result);
				prdList.get(i).setPerson(peopleList.get(i));
				
				prdController.addPersonRoleDocument(prdList.get(i));
			}
		}
		
		//Delete all relations and add the ones that have been sent
		ordController.deleteByDocument(result);
		if(wrap.orgs != null && !wrap.orgs.isEmpty()) {			
			//Need to also make sure that orgname is there on front end
			List<Organization> orgList = new ArrayList<>();
			for(OrgRole o : wrap.orgs) {
				orgList.add(organController.addSingleOrg(o.org));
			}
			
			for(int i = 0; i < ordList.size(); i++) {
				ordList.get(i).setDocument(result);
				ordList.get(i).setOrg(orgList.get(i));
				
				ordController.addOrganizationRoleDocument(ordList.get(i));
			}
		}
	
		//Delete all relations and add the ones that have been sent
		pprdController.deleteByDocument(result);
		if(wrap.places != null && !wrap.places.isEmpty()) {
			//Force lat and long
			List<Place> placeList = new ArrayList<>();
			for(PlaceRole p : wrap.places) {
				placeList.add(placeController.addSinglePlace(p.place));
			}
			
			for(int i = 0; i < pprdList.size(); i++) {
				pprdList.get(i).setDocument(result);
				pprdList.get(i).setPlace(placeList.get(i));
				
				pprdController.addPlaceRoleDocument(pprdList.get(i));
			}
		}

		String notifText = String.format("Document %d has been updated", result.getDocumentID());
		Notification notif = new Notification(Constants.NotificationType.DOCUMENT_UPDATED, notifText, null);
		notifService.setupAdminNotification(notif);
		return new ResponseEntity<>("Document was updated", HttpStatus.OK);
	}
	
	@PostMapping("delete-document")
	public ResponseEntity<String> deleteDocument(@RequestParam int docID){//, @CookieValue(value = "access_token") Cookie cookie) {
//		if(fusionAuthController.isMetadataExpert(cookie) != null) {
//			return fusionAuthController.isMetadataExpert(cookie);
//		}
		if(!docRepo.existsById(docID)) {
			return new ResponseEntity<>("It looks like we showed you a document that does not exist in our database. Please refresh the page. If the document still "
					+ "shows, notify tech services.", HttpStatus.CONFLICT);
		}
		
		docRepo.deleteById(docID);
		
		String notifText = String.format("Document %d has been deleted", docID);
		Notification notif = new Notification(Constants.NotificationType.DOCUMENT_DELETED, notifText, null);
		notifService.setupAdminNotification(notif);
		return new ResponseEntity<>("Document was deleted!", HttpStatus.OK);
	}
}
