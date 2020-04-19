package mainApplication.entities;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "place2document")
public class PlaceRoleDocument {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "place2docid")
	private Integer pp2dID;
	
	@ManyToOne @JoinColumn(name = "placeID")
	private Place place;
	
	@ManyToOne @JoinColumn(name = "docID")
	@JsonIgnore
	private Document document;
	
	@ManyToOne @JoinColumn(name = "roleID")
	private Role role;
	
	public PlaceRoleDocument() {
	}
	
	public PlaceRoleDocument(Place p, Role r, Document d) {
		this.place = p;
		this.role = r;
		this.document = d;
	}

	public Integer getPp2dID() {
		return pp2dID;
	}
	public void setPp2dID(Integer pp2dID) {
		this.pp2dID = pp2dID;
	}

	public Place getPlace() {
		return place;
	}
	public void setPlace(Place place) {
		this.place = place;
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
		PlaceRoleDocument inPPRD = (PlaceRoleDocument) o;
		
		if(this.getPlace().equals(inPPRD.getPlace()) && this.getRole().equals(inPPRD.getRole()) && this.getDocument().equals(inPPRD.getDocument())) {
			return true;
		}
		
		return false;
	}
}
