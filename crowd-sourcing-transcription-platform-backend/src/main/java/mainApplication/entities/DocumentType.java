package mainApplication.entities;

import java.util.List;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "documenttype")
public class DocumentType {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "doctypeid")
	private Integer docTypeID;
	
	//Unique value
	@Column(name = "typedesc")
	private String typeDesc;
	
	//Reference to list of docs linked to this doctype
	@OneToMany(mappedBy = "docType")
	@JsonIgnore
	private List<Document> docList;
	
	public DocumentType() {
	}
	public DocumentType(String inTypeDesc) {
		this.typeDesc = inTypeDesc;
	}
	
	public Integer getDocTypeID() {
		return docTypeID;
	}
	public void setDocTypeID(Integer inID) {
		this.docTypeID = inID;
	}
	
	public String getTypeDesc() {
		return typeDesc;
	}
	public void setTypeDesc(String inType) {
		this.typeDesc = inType;
	}
	
	public List<Document> getDocList() {
		return docList;
	}
	public void setDocList(List<Document> inDocList) {
		this.docList = inDocList;
	}
}
