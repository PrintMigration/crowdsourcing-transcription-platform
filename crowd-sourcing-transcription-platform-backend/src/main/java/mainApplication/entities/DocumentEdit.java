package mainApplication.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "documentedit")
public class DocumentEdit {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "documenteditid")
	private Integer documentEditID;
	
	@ManyToOne @JoinColumn(name = "docid")
	private Document baseDoc;
	
	@Column(name = "userid")
	private String userID;
	
	@Column(name = "documenttext", columnDefinition = "longtext")
	private String documentText;
	@Column(name = "texttype")
	private String textType;
	@Column(name = "creationdate")
	private Date creationDate;
	@Column(name = "completiondate")
	private Date completionDate;
	@Column(name = "lastmotified")
	private Date lastMotified;
	@Column(name = "contributed", columnDefinition = "tinyint")
	private Boolean contributed;
	@Column(name = "plainText", columnDefinition = "longtext")
	private String plainText;
	@Column(name = "translationtext", columnDefinition = "longtext")
	private String translationText;
	
	public DocumentEdit() {
		setCreationDate(new Date(System.currentTimeMillis()));
		setLastMotified(new Date(System.currentTimeMillis()));
	}
	
	public Integer getDocumentEditID() {
		return documentEditID;
	}
	public void setDocumentEditID(Integer documentEditID) {
		this.documentEditID = documentEditID;
	}
	
	public Document getBaseDoc() {
		return baseDoc;
	}
	public void setBaseDoc(Document baseDoc) {
		this.baseDoc = baseDoc;
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
	
	public String getTextType() {
		return textType;
	}
	public void setTextType(String textType) {
		this.textType = textType;
	}
	
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	
	public Date getCompletionDate() {
		return completionDate;
	}
	public void setCompletionDate(Date completionDate) {
		this.completionDate = completionDate;
	}
	
	public Date getLastMotified() {
		return lastMotified;
	}
	public void setLastMotified(Date lastMotified) {
		this.lastMotified = lastMotified;
	}
	
	public Boolean getContributed() {
		return contributed;
	}
	public void setContributed(Boolean contributed) {
		this.contributed = contributed;
	}

	public String getPlainText() {
		return plainText;
	}
	public void setPlainText(String plainText) {
		this.plainText = plainText;
	}

	public String getTranslationText() {
		return translationText;
	}
	public void setTranslationText(String translationText) {
		this.translationText = translationText;
	}
}
