package mainApplication.entities;

public class DocumentAndEdit {
	private Document doc;
	private DocumentEdit edit;
	
	public DocumentAndEdit(Document doc, DocumentEdit edit) {
		this.doc = doc;
		this.edit = edit;
	}

	public Document getDoc() {
		return doc;
	}
	public void setDoc(Document doc) {
		this.doc = doc;
	}

	public DocumentEdit getEdit() {
		return edit;
	}
	public void setEdit(DocumentEdit edit) {
		this.edit = edit;
	}
}
