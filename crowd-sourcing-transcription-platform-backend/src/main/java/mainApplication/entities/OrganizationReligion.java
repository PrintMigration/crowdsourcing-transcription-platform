package mainApplication.entities;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "organization2religion")
public class OrganizationReligion {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "organization2religionid")
	private Integer o2rID;
	
	@ManyToOne @JoinColumn(name = "organizationid")
	@JsonIgnore
	private Organization org;
	
	@ManyToOne @JoinColumn(name = "religionid")
	private Religion religion;
	
	@Column(name = "datespan")
	private String dateSpan;
	
	public OrganizationReligion() {
	}
	
	public OrganizationReligion(Organization inOrg, Religion inRel, String inDateSpan) {
		this.org = inOrg;
		this.religion = inRel;
		this.dateSpan = inDateSpan;
	}

	public Integer getO2rID() {
		return o2rID;
	}
	public void setO2rID(Integer o2rID) {
		this.o2rID = o2rID;
	}

	public Organization getOrg() {
		return org;
	}
	public void setOrg(Organization org) {
		this.org = org;
	}

	public Religion getReligion() {
		return religion;
	}
	public void setReligion(Religion religion) {
		this.religion = religion;
	}

	public String getDateSpan() {
		return dateSpan;
	}
	public void setDateSpan(String dateSpan) {
		this.dateSpan = dateSpan;
	}
}
