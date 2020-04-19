package mainApplication.entities;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "organization2document")
public class OrganizationRoleDocument {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "organization2documentid")
	private Integer o2dID;
	
	@ManyToOne @JoinColumn(name = "organizationID")
	private Organization org;
	
	@ManyToOne @JoinColumn(name = "docID")
	@JsonIgnore
	private Document document;

	@ManyToOne @JoinColumn(name = "roleID")
	private Role role;
	
	public OrganizationRoleDocument() {
	}
	
	public OrganizationRoleDocument(Organization inOrg, Role inR, Document inDoc) {
		this.org = inOrg;
		this.role = inR;
		this.document = inDoc;
	}

	public Integer getO2dID() {
		return o2dID;
	}
	public void setO2dID(Integer o2dID) {
		this.o2dID = o2dID;
	}

	public Organization getOrg() {
		return org;
	}
	public void setOrg(Organization org) {
		this.org = org;
	}

	public Document getDocument() {
		return document;
	}
	public void setDocument(Document doc) {
		this.document = doc;
	}

	public Role getRole() {
		return role;
	}
	public void setRole(Role role) {
		this.role = role;
	}
	
	@Override
	public boolean equals(Object o) {
		OrganizationRoleDocument inPPRD = (OrganizationRoleDocument) o;
		
		if(this.getOrg().equals(inPPRD.getOrg()) && this.getRole().equals(inPPRD.getRole()) && this.getDocument().equals(inPPRD.getDocument())) {
			return true;
		}
		
		return false;
	}
}
