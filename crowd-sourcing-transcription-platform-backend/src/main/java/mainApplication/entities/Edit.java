package mainApplication.entities;

public class Edit {
	private int docID;
	private String userID;
	private String documentText;
	private String translationText;
	
	public Edit(int docID, String userID, String documentText, String translationText) {
		this.docID = docID;
		this.userID = userID;
		this.documentText = documentText;
		this.translationText = translationText;
	}

	public int getDocID() {
		return docID;
	}
	public void setDocID(int docID) {
		this.docID = docID;
	}

	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getDocumentText() {
		return documentText;
	}
	public void setDocumentText(String documentText) {
		this.documentText = documentText;
	}

	public String getTranslationText() {
		return translationText;
	}
	public void setTranslationText(String translationText) {
		this.translationText = translationText;
	}
}
