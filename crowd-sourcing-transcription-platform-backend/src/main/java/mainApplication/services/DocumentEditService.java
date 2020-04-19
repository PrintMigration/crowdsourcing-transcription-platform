package mainApplication.services;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import mainApplication.Constants;
import mainApplication.entities.Document;
import mainApplication.entities.DocumentEdit;
import mainApplication.entities.Edit;
import mainApplication.entities.Notification;
import mainApplication.repositories.DocumentEditRepository;

@Service
public class DocumentEditService {
	private final DocumentEditRepository docEditRepo;
	private final NotificationService notifService;
	
	public DocumentEditService(DocumentEditRepository docEditRepo, NotificationService notifService) {
		this.docEditRepo = docEditRepo;
		this.notifService = notifService;
	}
	
	/*
	 * Return document to be transcribed/edited/encoded by someone else
	 * Text type: Constants.TextType.TRANSCRIPTION or Constants.TextType.EDIT or Constants.TextType.TEI_ENCODING
	 */
	public ResponseEntity returnDocument(Edit edit, String textType, boolean notifsOn) {
		Integer docEditID = getDocEditID(edit.getDocID(), edit.getUserID());
		if (docEditID == null)
			return new ResponseEntity<>("Document edit does not exist", HttpStatus.BAD_REQUEST);
		Optional<DocumentEdit> optionalDocEdit = docEditRepo.findById(docEditID);
		if (!optionalDocEdit.isPresent())
			return new ResponseEntity<>("Document edit does not exist", HttpStatus.BAD_REQUEST);
		DocumentEdit docEdit = optionalDocEdit.get();
		Document doc = docEdit.getBaseDoc();
		if (doc == null || doc.getUser() == null || !doc.getUser().equals(edit.getUserID()))
			return new ResponseEntity<>("You do not currently have this document checked out", HttpStatus.BAD_REQUEST);
		if (!isDocStatus(doc, getStatusFromTextType(textType)))
			return new ResponseEntity<>(String.format("This document is not in the %s phase", textType), HttpStatus.BAD_REQUEST);
		
		doc.setStatus(getReturnedStatusFromTextType(textType));
		doc.setUser(null); // Disassociate the document with the user
		docEdit.setDocumentText(edit.getDocumentText()); // Overwrite the previously saved edit
		docEdit.setTranslationText(edit.getTranslationText());
		docEdit.setLastMotified(new Date(System.currentTimeMillis()));
		docEdit.setCompletionDate(new Date(System.currentTimeMillis()));
		
		if (notifsOn)
			createReturnNotification(edit, doc, textType);
		return new ResponseEntity<>(docEditRepo.save(docEdit), HttpStatus.OK);
	}
	
	/*
	 * Create a notification that notifies all admins
	 * Text type: Constants.TextType.TRANSCRIPTION or Constants.TextType.EDIT or Constants.TextType.TEI_ENCODING
	 */
	private void createReturnNotification(Edit edit, Document doc, String textType) {
		String notifType = "";
		String verb = "";
		if (textType.equals(Constants.TextType.TRANSCRIPTION)) {
			notifType = Constants.NotificationType.TRANSCRIPTION_RETURNED;
			verb = "transcribed";
		}
		else if (textType.equals(Constants.TextType.EDIT)) {
			notifType = Constants.NotificationType.EDIT_RETURNED;
			verb = "edited";
		}
		else if (textType.equals(Constants.TextType.TEI_ENCODING)) {
			notifType = Constants.NotificationType.ENCODING_RETURNED;
			verb = "encoded";
		}
		
		String notifText = String.format("User %s has returned document %d to be %s by someone else", edit.getUserID(), doc.getDocumentID(), verb);
		Notification notif = new Notification(notifType, notifText, edit.getUserID());
		notifService.setupAdminNotification(notif);
	}
	
	private String getStatusFromTextType(String textType) {
		if (textType.equals(Constants.TextType.TRANSCRIPTION))
			return Constants.DocumentStatus.TRANSCRIBING;
		if (textType.equals(Constants.TextType.EDIT))
			return Constants.DocumentStatus.EDITING;
		if (textType.equals(Constants.TextType.TEI_ENCODING))
			return Constants.DocumentStatus.TEI_ENCODING;
		return "";
	}
	
	private String getReturnedStatusFromTextType(String textType) {
		if (textType.equals(Constants.TextType.TRANSCRIPTION))
			return Constants.DocumentStatus.NEEDS_TRANSCRIBING;
		if (textType.equals(Constants.TextType.EDIT))
			return Constants.DocumentStatus.NEEDS_EDITING;
		if (textType.equals(Constants.TextType.TEI_ENCODING))
			return Constants.DocumentStatus.NEEDS_TEI_ENCODING;
		return "";
	}
	
	/*
	 * Returns true if and only if the status of the document is equal to status
	 * Assumes status is not null
	 */
	private boolean isDocStatus(Document doc, String status) {
		return doc.getStatus() != null && doc.getStatus().equals(status);
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
	
	/*
	 * Returns all of the DocumentEdits associated with the given user that are not completed
	 * In other words, these are the DocumentEdits for documents that the user currently has checked out
	 */
	public List<DocumentEdit> getCheckedOutDocumentEdits(String userID) {
		return docEditRepo.findByUserIDAndCompletionDate(userID, null);
	}
}
