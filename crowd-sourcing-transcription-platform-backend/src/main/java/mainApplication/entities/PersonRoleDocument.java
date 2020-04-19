package mainApplication.entities;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "person2document")
public class PersonRoleDocument {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "person2document")
	private Integer p2dID;
	
	@ManyToOne @JoinColumn(name = "personID")
	private Person person;
	
	@ManyToOne @JoinColumn(name = "docID")
	@JsonIgnore
	private Document document;
	
	@ManyToOne @JoinColumn(name = "roleID")
	private Role role;
	
	public PersonRoleDocument() {
	}
	//Add another constructor
	public PersonRoleDocument(Person inPer, Role inRole, Document result) {
		this.person = inPer;
		this.document = result;
		this.role = inRole;
	}

	public Integer getP2dID() {
		return p2dID;
	}
	public void setP2dID(Integer p2dID) {
		this.p2dID = p2dID;
	}

	public Person getPerson() {
		return person;
	}
	public void setPerson(Person person) {
		this.person = person;
	}

	public Document getDocument() {
		return document;
	}
	public void setDocument(Document document) {
		this.document = document;
	}

	public Role getRole() {
		return role;
	}
	public void setRole(Role role) {
		this.role = role;
	}
	
	@Override
	public boolean equals(Object o) {
		PersonRoleDocument inPRD = (PersonRoleDocument) o;
		if(this.getDocument().equals(inPRD.getDocument()) && this.getPerson().equals(inPRD.getPerson()) && this.getRole().equals(inPRD.getRole())) {
			return true;
		}
		return false;
	}
}
