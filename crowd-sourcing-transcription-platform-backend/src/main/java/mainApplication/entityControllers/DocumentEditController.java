package mainApplication.entityControllers;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mainApplication.Constants;
import mainApplication.entities.Document;
import mainApplication.entities.DocumentEdit;
import mainApplication.entities.Edit;
import mainApplication.entities.Notification;
import mainApplication.repositories.DocumentEditRepository;
import mainApplication.repositories.DocumentRepository;
import mainApplication.services.DocumentEditService;
import mainApplication.services.NotificationService;

//import javax.servlet.http.Cookie;

@RestController
public class DocumentEditController {
	private final DocumentEditRepository docEditRepo;
	private final DocumentRepository docRepo;
	private final DocumentEditService docEditService;
	private final NotificationService notifService;
	//private final String apiKey = "MPFD3IcCfYfWxgII8LaMvEoR1SGCcUQ1XUMs2IELFsY";
	//private final FusionAuthClient client = new FusionAuthClient(apiKey, "http://localhost:9011");

	public DocumentEditController(DocumentEditRepository docEditRepo, DocumentRepository docRepo, DocumentEditService docEditService, NotificationService notifService) {
		this.docEditRepo = docEditRepo;
		this.docRepo = docRepo;
		this.docEditService = docEditService;
		this.notifService = notifService;
	}
	
	/*
	 * Returns a list of all document edits for testing purposes
	 */
	@GetMapping(path="edits/all")
	public ResponseEntity<Iterable<DocumentEdit>> getAllDocEdits() {
		return new ResponseEntity<>(docEditRepo.findAll(), HttpStatus.OK);
	}
	
	/*
	 * Get the most recent DocumentEdit completed (returned or submitted) by a transcriber or editor.
	 * Returns null if none exist
	 */
	@GetMapping(path="edits/document/{docID}")
	public ResponseEntity<DocumentEdit> getMostRecentlyCompletedTranscriptionOrEditAsResponseEntity(@PathVariable int docID) {
		DocumentEdit docEdit = getMostRecentlyCompletedTranscriptionOrEdit(docID);
		return new ResponseEntity<>(docEdit, HttpStatus.OK);
	}
	
	/*
	 * Returns all of the Documents associated with the given user whose edits are not completed
	 * In other words, these are the Documents that the user currently has checked out
	 */
	@GetMapping(path="edits/{userID}")
	public ResponseEntity<List<Document>> getCheckedOutDocuments(@PathVariable String userID) {
		List<DocumentEdit> list = docEditRepo.findByUserIDAndCompletionDate(userID, null);
		List<Document> docs = new ArrayList<>();
		for (DocumentEdit docEdit : list)
			docs.add(docEdit.getBaseDoc());
		return new ResponseEntity<>(docs, HttpStatus.OK);
	}
	
	/*
	 * Returns the document text based on userID and docID
	 * Returns null if no document found
	 */
	@GetMapping(path="edits/text")
	private ResponseEntity<String> getDocumentText(@RequestParam String userID, @RequestParam int docID) {
		List<DocumentEdit> edits = getCheckedOutDocumentEdits(userID);
		for (int i = 0; i < edits.size(); i++) {
			DocumentEdit edit = edits.get(i);
			if (edit.getBaseDoc().getDocumentID() == docID)
				return new ResponseEntity<>(edit.getDocumentText(), HttpStatus.OK);
		}
		return new ResponseEntity<>(null, HttpStatus.OK);
	}
	
	/*
	 * Transcriber: check out a document to transcribe
	 * Required input: docID and userID
	 */
	@PostMapping("transcriber/checkout")
	public ResponseEntity transcriberCheckout(@RequestBody Checkout body) { //, @CookieValue(value = "access_token") Cookie cookie) {
//		Verify that the user making the request is the given user
//		Verify that the user has a role of transcriber or higher
		/*
		String test = cookie.getValue();
		String jwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6ImZhNjU5ZTIyZmIifQ.eyJhdWQiOiIyYzI5OGI5MS01NDU4LTRmYTQtYTBiOS0wODFhN2RmNDhhNGUiLCJleHAiOjE1ODE1MzE0ODEsImlhdCI6MTU4MTUyNzg4MSwiaXNzIjoiYWNtZS5jb20iLCJzdWIiOiJiNzIyNWRiMy1lN2FkLTQwYTgtOTNkOC02M2Q4MGJiNWU2MzUiLCJhdXRoZW50aWNhdGlvblR5cGUiOiJQQVNTV09SRCIsImVtYWlsIjoiZGptYXJ0ZWw5N0BnbWFpbC5jb20iLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwiYXBwbGljYXRpb25JZCI6IjJjMjk4YjkxLTU0NTgtNGZhNC1hMGI5LTA4MWE3ZGY0OGE0ZSIsInJvbGVzIjpbIkVkaXRvciJdfQ.BX56JcEmhHcy8hAJtzPeK3S6Q9zFSEEuY3Snb-fEnhs";
		ClientResponse<ValidateResponse, Void> clientResponse = client.validateJWT(jwt);

		if (clientResponse.status == 200) {
			//Boolean containsRole = clientResponse.successResponse.jwt.otherClaims.get("roles").toString().contains("Editor");
			DocumentEdit checkValidation = new DocumentEdit();
			checkValidation.setUserID("Successfull validate");
			//checkValidation.setDocumentText(containsRole.toString());
			return checkValidation;
		} else {

		 */
			if (getNumberOfCheckedOutDocumentEdits(body.userID, Constants.TextType.TRANSCRIPTION) >= Constants.TRANSCRIBER_CHECKOUT_LIMIT)
				return new ResponseEntity<>(String.format("You may only checkout up to %d documents at a time", Constants.TRANSCRIBER_CHECKOUT_LIMIT), HttpStatus.BAD_REQUEST);
			
			Optional<Document> optionalDoc = docRepo.findById(body.docID);
			if (!optionalDoc.isPresent())
				return new ResponseEntity<>("Document ID does not exist", HttpStatus.BAD_REQUEST);
			
			Document doc = optionalDoc.get();
			if (!isDocStatus(doc, Constants.DocumentStatus.NEEDS_TRANSCRIBING))
				return new ResponseEntity<>("This document is not available for transcribing", HttpStatus.BAD_REQUEST);
			
			doc.setStatus(Constants.DocumentStatus.TRANSCRIBING);
			doc.setUser(body.userID);

			DocumentEdit docEdit = new DocumentEdit();
			docEdit.setUserID(body.userID);
			docEdit.setBaseDoc(doc);
			
			// Check if this document has been worked on before by a transcriber or editor
			DocumentEdit prevEdit = getMostRecentlyCompletedTranscriptionOrEdit(body.docID);
			if (prevEdit != null && prevEdit.getCompletionDate() != null)
				docEdit.setDocumentText(prevEdit.getDocumentText()); // Transcriber should start with the work the last person has done
			
			docEdit.setTextType(Constants.TextType.TRANSCRIPTION);
			return new ResponseEntity<>(docEditRepo.save(docEdit), HttpStatus.OK);
//		}
	}
	private static class Checkout {
		public int docID;
		public String userID;
	}
	
	/*
	 * Transcriber: save work for later. This text shouldn't be searchable or viewable by anyone but
	 * the user who has it checked out and the admins
	 * Required input: userID, docID, documentText
	 */
//	We probably don't have to pass in the userID as long as we can get that from the cookie (save, submit, return)
	@PostMapping("transcriber/save")
	public ResponseEntity transcriberSave(@RequestBody Edit body) {
		Integer docEditID = getDocEditID(body.getDocID(), body.getUserID());
		if (docEditID == null)
			return new ResponseEntity<>("Document edit does not exist", HttpStatus.BAD_REQUEST);
		Optional<DocumentEdit> optionalDocEdit = docEditRepo.findById(docEditID);
		if (!optionalDocEdit.isPresent())
			return new ResponseEntity<>("Document edit does not exist", HttpStatus.BAD_REQUEST);
		DocumentEdit docEdit = optionalDocEdit.get();
		Document doc = docEdit.getBaseDoc();
		if (doc == null || doc.getUser() == null || !doc.getUser().equals(body.getUserID()))
			return new ResponseEntity<>("You do not currently have this document checked out", HttpStatus.BAD_REQUEST);
		if (!isDocStatus(doc, Constants.DocumentStatus.TRANSCRIBING))
			return new ResponseEntity<>("This document is not in the transcription phase", HttpStatus.BAD_REQUEST);
		
		docEdit.setDocumentText(body.getDocumentText()); // Overwrite the previously saved edit
		docEdit.setTranslationText(body.getTranslationText());
		docEdit.setLastMotified(new Date(System.currentTimeMillis()));
		return new ResponseEntity<>(docEditRepo.save(docEdit), HttpStatus.OK);
	}
	
	/*
	 * Transcriber: submit work to the world and allow an editor to work on it
	 * Required input: userID, docID, documentText
	 */
	@PostMapping("transcriber/submit")
	public ResponseEntity transcriberSubmit(@RequestBody Edit body) {
		Integer docEditID = getDocEditID(body.getDocID(), body.getUserID());
		if (docEditID == null)
			return new ResponseEntity<>("Document edit does not exist", HttpStatus.BAD_REQUEST);
		Optional<DocumentEdit> optionalDocEdit = docEditRepo.findById(docEditID);
		if (!optionalDocEdit.isPresent())
			return new ResponseEntity<>("Document edit does not exist", HttpStatus.BAD_REQUEST);
		DocumentEdit docEdit = optionalDocEdit.get();
		Document doc = docEdit.getBaseDoc();
		if (doc == null || doc.getUser() == null || !doc.getUser().equals(body.getUserID()))
			return new ResponseEntity<>("You do not currently have this document checked out", HttpStatus.BAD_REQUEST);
		if (!isDocStatus(doc, Constants.DocumentStatus.TRANSCRIBING))
			return new ResponseEntity<>("This document is not in the transcription phase", HttpStatus.BAD_REQUEST);
		
		doc.setStatus(Constants.DocumentStatus.NEEDS_EDITING);
		doc.setUser(null); // Disassociate the document with the user
		docEdit.setDocumentText(body.getDocumentText()); // Overwrite the previously saved edit
		docEdit.setTranslationText(body.getTranslationText());
		docEdit.setLastMotified(new Date(System.currentTimeMillis()));
		docEdit.setCompletionDate(new Date(System.currentTimeMillis()));
		
		String notifText = String.format("User %s has finished transcribing document %d", body.getUserID(), doc.getDocumentID());
		Notification notif = new Notification(Constants.NotificationType.TRANSCRIPTION_COMPLETE, notifText, body.getUserID());
		notifService.setupAdminNotification(notif);
		return new ResponseEntity<>(docEditRepo.save(docEdit), HttpStatus.OK);
	}
	
	/*
	 * Transcriber: return document to be transcribed by someone else
	 * Their work should be saved for the next person
	 * Required input: userID, docID, documentText
	 */
	@PostMapping("transcriber/return")
	public ResponseEntity transcriberReturn(@RequestBody Edit body) {
		return docEditService.returnDocument(body, Constants.TextType.TRANSCRIPTION, true);
	}
	
	/*
	 * Editor: check out a document to edit
	 * Required input: docID and userID
	 */
	@PostMapping("editor/checkout")
	public ResponseEntity editorCheckout(@RequestBody Checkout body) {
//		Verify that the user making the request is the given user
//		Verify that the user has a role of editor or higher
		
		if (getNumberOfCheckedOutDocumentEdits(body.userID, Constants.TextType.EDIT) >= Constants.EDITOR_CHECKOUT_LIMIT)
			return new ResponseEntity<>(String.format("You may only checkout up to %d documents at a time", Constants.EDITOR_CHECKOUT_LIMIT), HttpStatus.BAD_REQUEST);
		
		Optional<Document> optionalDoc = docRepo.findById(body.docID);
		if (!optionalDoc.isPresent())
			return new ResponseEntity<>("Document ID does not exist", HttpStatus.BAD_REQUEST);
		
		Document doc = optionalDoc.get();
		if (!isDocStatus(doc, Constants.DocumentStatus.NEEDS_EDITING))
			return new ResponseEntity<>("This document is not available for editing", HttpStatus.BAD_REQUEST);
		doc.setStatus(Constants.DocumentStatus.EDITING);
		doc.setUser(body.userID);
		
		DocumentEdit docEdit = new DocumentEdit();
		docEdit.setUserID(body.userID);
		docEdit.setBaseDoc(doc);
		
		// Get the edit that the last transcriber/editor worked on (and finished)
		DocumentEdit prevEdit = getMostRecentlyCompletedTranscriptionOrEdit(body.docID);
		if (prevEdit == null || prevEdit.getCompletionDate() == null)
			return new ResponseEntity<>("Previous transcription not found (or not completed)", HttpStatus.BAD_REQUEST);
		docEdit.setDocumentText(prevEdit.getDocumentText()); // Editor should start with the work the transcriber (or last editor) has done
		
		docEdit.setTextType(Constants.TextType.EDIT);
		return new ResponseEntity<>(docEditRepo.save(docEdit), HttpStatus.OK);
	}
	
	/*
	 * Editor: save work for later. This text shouldn't be searchable or viewable by anyone but
	 * the user who has it checked out and the admins
	 * Required input: userID, docID, documentText
	 */
//	We probably don't have to pass in the userID as long as we can get that from the cookie (save, submit, return)
	@PostMapping("editor/save")
	public ResponseEntity editorSave(@RequestBody Edit body) {
		Integer docEditID = getDocEditID(body.getDocID(), body.getUserID());
		if (docEditID == null)
			return new ResponseEntity<>("Document edit does not exist", HttpStatus.BAD_REQUEST);
		Optional<DocumentEdit> optionalDocEdit = docEditRepo.findById(docEditID);
		if (!optionalDocEdit.isPresent())
			return new ResponseEntity<>("Document edit does not exist", HttpStatus.BAD_REQUEST);
		DocumentEdit docEdit = optionalDocEdit.get();
		Document doc = docEdit.getBaseDoc();
		if (doc == null || doc.getUser() == null || !doc.getUser().equals(body.getUserID()))
			return new ResponseEntity<>("You do not currently have this document checked out", HttpStatus.BAD_REQUEST);
		if (!isDocStatus(doc, Constants.DocumentStatus.EDITING))
			return new ResponseEntity<>("This document is not in the editing phase", HttpStatus.BAD_REQUEST);
		
		docEdit.setDocumentText(body.getDocumentText()); // Overwrite the previously saved edit
		docEdit.setTranslationText(body.getTranslationText());
		docEdit.setLastMotified(new Date(System.currentTimeMillis()));
		return new ResponseEntity<>(docEditRepo.save(docEdit), HttpStatus.OK);
	}
	
	/*
	 * Editor: submit work to the world and allow an encoder to work on it
	 * Required input: userID, docID, documentText
	 */
	@PostMapping("editor/submit")
	public ResponseEntity editorSubmit(@RequestBody Edit body) {
		Integer docEditID = getDocEditID(body.getDocID(), body.getUserID());
		if (docEditID == null)
			return new ResponseEntity<>("Document edit does not exist", HttpStatus.BAD_REQUEST);
		Optional<DocumentEdit> optionalDocEdit = docEditRepo.findById(docEditID);
		if (!optionalDocEdit.isPresent())
			return new ResponseEntity<>("Document edit does not exist", HttpStatus.BAD_REQUEST);
		DocumentEdit docEdit = optionalDocEdit.get();
		Document doc = docEdit.getBaseDoc();
		if (doc == null || doc.getUser() == null || !doc.getUser().equals(body.getUserID()))
			return new ResponseEntity<>("You do not currently have this document checked out", HttpStatus.BAD_REQUEST);
		if (!isDocStatus(doc, Constants.DocumentStatus.EDITING))
			return new ResponseEntity<>("This document is not in the editing phase", HttpStatus.BAD_REQUEST);
		
		doc.setStatus(Constants.DocumentStatus.NEEDS_TEI_ENCODING);
		doc.setUser(null); // Disassociate the document with the user
		docEdit.setDocumentText(body.getDocumentText()); // Overwrite the previously saved edit
		docEdit.setTranslationText(body.getTranslationText());
		docEdit.setLastMotified(new Date(System.currentTimeMillis()));
		docEdit.setCompletionDate(new Date(System.currentTimeMillis()));
		
		String notifText = String.format("User %s has finished editing document %d", body.getUserID(), doc.getDocumentID());
		Notification notif = new Notification(Constants.NotificationType.EDIT_COMPLETE, notifText, body.getUserID());
		notifService.setupAdminNotification(notif);
		return new ResponseEntity<>(docEditRepo.save(docEdit), HttpStatus.OK);
	}
	
	/*
	 * Editor: return document to be edited by someone else
	 * Their work should be saved for the next person
	 * Required input: userID, docID, documentText
	 */
	@PostMapping("editor/return")
	public ResponseEntity editorReturn(@RequestBody Edit body) {
		return docEditService.returnDocument(body, Constants.TextType.EDIT, true);
	}
	
	/*
	 * Editor: return document to be re-transcribed
	 * This should be used in cases where the transcriber messed up or missed a lot of text
	 * Their work should be saved for the next person
	 * Required input: userID, docID, documentText
	 */
	@PostMapping("editor/restart")
	public ResponseEntity editorRestart(@RequestBody Edit body) {
		Integer docEditID = getDocEditID(body.getDocID(), body.getUserID());
		if (docEditID == null)
			return new ResponseEntity<>("Document edit does not exist", HttpStatus.BAD_REQUEST);
		Optional<DocumentEdit> optionalDocEdit = docEditRepo.findById(docEditID);
		if (!optionalDocEdit.isPresent())
			return new ResponseEntity<>("Document edit does not exist", HttpStatus.BAD_REQUEST);
		DocumentEdit docEdit = optionalDocEdit.get();
		Document doc = docEdit.getBaseDoc();
		if (doc == null || doc.getUser() == null || !doc.getUser().equals(body.getUserID()))
			return new ResponseEntity<>("You do not currently have this document checked out", HttpStatus.BAD_REQUEST);
		if (!isDocStatus(doc, Constants.DocumentStatus.EDITING))
			return new ResponseEntity<>("This document is not in the editing phase", HttpStatus.BAD_REQUEST);
		
		doc.setStatus(Constants.DocumentStatus.NEEDS_TRANSCRIBING);
		doc.setUser(null); // Disassociate the document with the user
		docEdit.setDocumentText(body.getDocumentText()); // Overwrite the previously saved edit
		docEdit.setTranslationText(body.getTranslationText());
		docEdit.setLastMotified(new Date(System.currentTimeMillis()));
		docEdit.setCompletionDate(new Date(System.currentTimeMillis()));
		
		String notifText = String.format("User %s has returned document %d to be transcribed again", body.getUserID(), doc.getDocumentID());
		Notification notif = new Notification(Constants.NotificationType.EDIT_RETURNED_TO_TRANSCRIBER, notifText, body.getUserID());
		notifService.setupAdminNotification(notif);
		return new ResponseEntity<>(docEditRepo.save(docEdit), HttpStatus.OK);
	}
	
	/*
	 * Encoder: check out a document to edit
	 * Required input: docID and userID
	 */
	@PostMapping("encoder/checkout")
	public ResponseEntity encoderCheckout(@RequestBody Checkout body) {
//		Verify that the user making the request is the given user
//		Verify that the user has a role of encoder or higher
		
		if (getNumberOfCheckedOutDocumentEdits(body.userID, Constants.TextType.TEI_ENCODING) >= Constants.TEI_ENCODER_CHECKOUT_LIMIT)
			return new ResponseEntity<>(String.format("You may only checkout up to %d documents at a time", Constants.TEI_ENCODER_CHECKOUT_LIMIT), HttpStatus.BAD_REQUEST);
		
		Optional<Document> optionalDoc = docRepo.findById(body.docID);
		if (!optionalDoc.isPresent())
			return new ResponseEntity<>("Document ID does not exist", HttpStatus.BAD_REQUEST);
		
		Document doc = optionalDoc.get();
		if (!isDocStatus(doc, Constants.DocumentStatus.NEEDS_TEI_ENCODING))
			return new ResponseEntity<>("This document is not available for encoding", HttpStatus.BAD_REQUEST);
		doc.setStatus(Constants.DocumentStatus.TEI_ENCODING);
		doc.setUser(body.userID);
		
		DocumentEdit docEdit = new DocumentEdit();
		docEdit.setUserID(body.userID);
		docEdit.setBaseDoc(doc);
		
		// Get the edit that the editor or last encoder worked on (and finished)
		DocumentEdit prevEdit = getMostRecentlyCompletedOfType(body.docID, Constants.TextType.EDIT, Constants.TextType.TEI_ENCODING);
		if (prevEdit == null || prevEdit.getCompletionDate() == null)
			return new ResponseEntity<>("Previous transcription not found (or not completed)", HttpStatus.BAD_REQUEST);
		docEdit.setDocumentText(prevEdit.getDocumentText()); // Encoder should use the work the editor (or last encoder) has done
		
		docEdit.setTextType(Constants.TextType.TEI_ENCODING);
		return new ResponseEntity<>(docEditRepo.save(docEdit), HttpStatus.OK);
	}
	
	/*
	 * Encoder: save work for later.
	 * Required input: userID, docID, documentText
	 */
//	We probably don't have to pass in the userID as long as we can get that from the cookie (save, submit, return)
	@PostMapping("encoder/save")
	public ResponseEntity encoderSave(@RequestBody Edit body) {
		Integer docEditID = getDocEditID(body.getDocID(), body.getUserID());
		if (docEditID == null)
			return new ResponseEntity<>("Document edit does not exist", HttpStatus.BAD_REQUEST);
		Optional<DocumentEdit> optionalDocEdit = docEditRepo.findById(docEditID);
		if (!optionalDocEdit.isPresent())
			return new ResponseEntity<>("Document edit does not exist", HttpStatus.BAD_REQUEST);
		DocumentEdit docEdit = optionalDocEdit.get();
		Document doc = docEdit.getBaseDoc();
		if (doc == null || doc.getUser() == null || !doc.getUser().equals(body.getUserID()))
			return new ResponseEntity<>("You do not currently have this document checked out", HttpStatus.BAD_REQUEST);
		if (!isDocStatus(doc, Constants.DocumentStatus.TEI_ENCODING))
			return new ResponseEntity<>("This document is not in the encoding phase", HttpStatus.BAD_REQUEST);
		
		docEdit.setDocumentText(body.getDocumentText()); // Overwrite the previously saved edit
		docEdit.setTranslationText(body.getTranslationText());
		docEdit.setLastMotified(new Date(System.currentTimeMillis()));
		return new ResponseEntity<>(docEditRepo.save(docEdit), HttpStatus.OK);
	}
	
	/*
	 * Encoder: submit work to the world
	 * Required input: userID, docID, documentText
	 */
	@PostMapping("encoder/submit")
	public ResponseEntity encoderSubmit(@RequestBody Edit body) {
		Integer docEditID = getDocEditID(body.getDocID(), body.getUserID());
		if (docEditID == null)
			return new ResponseEntity<>("Document edit does not exist", HttpStatus.BAD_REQUEST);
		Optional<DocumentEdit> optionalDocEdit = docEditRepo.findById(docEditID);
		if (!optionalDocEdit.isPresent())
			return new ResponseEntity<>("Document edit does not exist", HttpStatus.BAD_REQUEST);
		DocumentEdit docEdit = optionalDocEdit.get();
		Document doc = docEdit.getBaseDoc();
		if (doc == null || doc.getUser() == null || !doc.getUser().equals(body.getUserID()))
			return new ResponseEntity<>("You do not currently have this document checked out", HttpStatus.BAD_REQUEST);
		if (!isDocStatus(doc, Constants.DocumentStatus.TEI_ENCODING))
			return new ResponseEntity<>("This document is not in the encoding phase", HttpStatus.BAD_REQUEST);
		
		doc.setStatus(Constants.DocumentStatus.COMPLETED);
		doc.setUser(null); // Disassociate the document with the user
		docEdit.setDocumentText(body.getDocumentText()); // Overwrite the previously saved edit
		docEdit.setTranslationText(body.getTranslationText());
		docEdit.setLastMotified(new Date(System.currentTimeMillis()));
		docEdit.setCompletionDate(new Date(System.currentTimeMillis()));
		
		String notifText = String.format("User %s has finished encoding document %d", body.getUserID(), doc.getDocumentID());
		Notification notif = new Notification(Constants.NotificationType.ENCODING_COMPLETE, notifText, body.getUserID());
		notifService.setupAdminNotification(notif);
		return new ResponseEntity<>(docEditRepo.save(docEdit), HttpStatus.OK);
	}
	
	/*
	 * Encoder: return document to be encoded by someone else
	 * Their work should be saved for the next person
	 * Required input: userID, docID, documentText
	 */
	@PostMapping("encoder/return")
	public ResponseEntity encoderReturn(@RequestBody Edit body) {
		return docEditService.returnDocument(body, Constants.TextType.TEI_ENCODING, true);
	}
	
	/*
	 * Change the status of a document to Needs Transcribing, Needs Editing, Needs TEI Encoding, or Completed
	 */
	@PostMapping("change-status")
	public ResponseEntity changeStatus(@RequestParam int docID, @RequestParam String status) {
		if (!status.equals(Constants.DocumentStatus.NEEDS_TRANSCRIBING) && !status.equals(Constants.DocumentStatus.NEEDS_EDITING)
				&& !status.equals(Constants.DocumentStatus.NEEDS_TEI_ENCODING) && !status.equals(Constants.DocumentStatus.COMPLETED))
			return new ResponseEntity<>("Invalid status", HttpStatus.BAD_REQUEST);
		
		DocumentEdit docEdit = getDocumentEdit(docID);
		if (docEdit != null) {
			Edit edit = new Edit(docID, docEdit.getUserID(), docEdit.getDocumentText(), docEdit.getTranslationText());
			String textType = docEdit.getTextType();
			
			// Return document without notifying admins
			docEditService.returnDocument(edit, textType, false);
		}
		Document doc = docRepo.findByDocumentID(docID);
		if (doc == null)
			return new ResponseEntity<>("Document does not exist", HttpStatus.BAD_REQUEST);
		doc.setStatus(status);
		doc.setUser(null); // Disassociate the document with its user
		docRepo.save(doc);
		
		String notifText = String.format("The status of document %d has been changed to %s", docID, status);
		Notification notif = new Notification(Constants.NotificationType.DOCUMENT_STATUS_CHANGED, notifText, null);
		notifService.setupAdminNotification(notif);
		return new ResponseEntity<>(null, HttpStatus.OK);
	}
	
	/*
	 * Get the current document edit for a docID
	 * Returns null if there is none
	 */
	private DocumentEdit getDocumentEdit(int docID) {
		return docEditRepo.findTopByBaseDoc_DocumentIDOrderByCompletionDate(docID);
	}

	/*
	 * Get the most recent DocumentEdit completed (returned or submitted) by a transcriber or editor.
	 * Returns null if none exist
	 */
	public DocumentEdit getMostRecentlyCompletedTranscriptionOrEdit(int docID) {
		return getMostRecentlyCompletedOfType(docID, Constants.TextType.TRANSCRIPTION, Constants.TextType.EDIT);
	}
	
	/*
	 * Returns all of the DocumentEdits associated with the given user that are not completed
	 * In other words, these are the DocumentEdits for documents that the user currently has checked out
	 */
	public List<DocumentEdit> getCheckedOutDocumentEdits(String userID) {
		return docEditRepo.findByUserIDAndCompletionDate(userID, null);
	}
	
	/*
	 * Returns the number of DocumentEdits of a certain text type associated with the given user that are not completed
	 */
	private int getNumberOfCheckedOutDocumentEdits(String userID, String type) {
		List<DocumentEdit> list = getCheckedOutDocumentEdits(userID);
		int count = 0;
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getTextType().equals(type))
				count++;
		}
		return count;
	}
	
	/*
	 * Returns true if and only if the status of the document is equal to status
	 * Assumes status is not null
	 */
	private boolean isDocStatus(Document doc, String status) {
		return doc.getStatus() != null && doc.getStatus().equals(status);
	}
	
	/*
	 * Get the most recent DocumentEdit completed (returned or submitted) that is either of 2 text types.
	 * Returns null if none exist
	 */
	private DocumentEdit getMostRecentlyCompletedOfType(int docID, String t1, String t2) {
		DocumentEdit prevEdit1 = docEditRepo.findTopByBaseDoc_DocumentIDAndTextTypeOrderByCompletionDateDesc(docID, t1);
		DocumentEdit prevEdit2 = docEditRepo.findTopByBaseDoc_DocumentIDAndTextTypeOrderByCompletionDateDesc(docID, t2);
		if (prevEdit1 == null || prevEdit1.getCompletionDate() == null) {
			if (prevEdit2 == null || prevEdit2.getCompletionDate() == null)
				return null;
			return prevEdit2;
		}
		if (prevEdit2 == null || prevEdit2.getCompletionDate() == null)
			return prevEdit1;
		if (prevEdit1.getCompletionDate().compareTo(prevEdit2.getCompletionDate()) > 0)
			return prevEdit1;
		return prevEdit2;
	}
	
	/*
	 * Get the documentEditID of the edit that is not completed based on the documentID and the userID
	 * There should only be one unfinished edit for a user working on a document
	 * Returns null if none exist
	 */
	private Integer getDocEditID(int docID, String userID) {
		List<DocumentEdit> edits = getCheckedOutDocumentEdits(userID);
		for (int i = 0; i < edits.size(); i++) {
			DocumentEdit edit = edits.get(i);
			if (edit.getBaseDoc().getDocumentID() == docID)
				return edit.getDocumentEditID();
		}
		return null;
	}
}
