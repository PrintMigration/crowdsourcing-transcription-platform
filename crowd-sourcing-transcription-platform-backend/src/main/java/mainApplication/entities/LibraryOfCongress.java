package mainApplication.entities;

import java.util.*;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "libraryofcongress")
public class LibraryOfCongress {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "locID")
	private Integer locID;
	
	//Unique value
	@Column(name = "locsubjectheading")
	private String subjectHeading;
	
	@ManyToMany(mappedBy = "libraryOfCongresses")
	@JsonIgnore
	private List<Document> docList;
	
	public LibraryOfCongress() {
		this.subjectHeading = "";
	}
	
	public LibraryOfCongress(String inSubjectHeading) {
		this.subjectHeading = inSubjectHeading;
	}

	public Integer getLocID() {
		return locID;
	}
	public void setLocID(Integer locID) {
		this.locID = locID;
	}

	public String getSubjectHeading() {
		return subjectHeading;
	}
	public void setSubjectHeading(String subjectHeading) {
		this.subjectHeading = subjectHeading;
	}
	
	public List<Document> getDocList() {
		return docList;
	}
	public void setDocuments(List<Document> documents) {
		this.docList = documents;
	}
	
	@Override
	public boolean equals(Object o) {
		LibraryOfCongress inLOC = (LibraryOfCongress) o;
		
		if(this.getSubjectHeading().equals(inLOC.getSubjectHeading())) {
			return true;
		}
		
		return false;
	}
}
