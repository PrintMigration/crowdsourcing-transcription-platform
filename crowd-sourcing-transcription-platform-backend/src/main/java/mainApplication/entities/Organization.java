package mainApplication.entities;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import mainApplication.customSerializers.OrganizationReligionSerializer;
import mainApplication.customSerializers.OrganizationRoleDocumentSerializer;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "organization")
public class Organization {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "organizationid")
	private Integer orgID;
	
	@OneToMany(mappedBy = "org")
	@JsonIgnore
	private List<OrganizationRoleDocument> ordSet;
	
	@OneToMany(mappedBy = "org")
	private List<OrganizationReligion> orgRelSet;
	
	//Unique value
	@Column(name = "organizationname")
	private String orgName;
	@Column(name = "formationdate")
	private String formationDate;
	@Column(name = "dissolutiondate")
	private String dissolutionDate;
	@Column(name = "organizationlod")
	private String orgLOD;
	
	public Organization() {
		this.orgName = "";
		this.formationDate = "";
		this.dissolutionDate = "";
		this.orgLOD = "";
		this.orgRelSet = new ArrayList<>();
	}

	public Integer getOrgID() {
		return orgID;
	}
	public void setOrgID(Integer orgID) {
		this.orgID = orgID;
	}

	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getFormationDate() {
		return formationDate;
	}
	public void setFormationDate(String formationDate) {
		this.formationDate = formationDate;
	}

	public String getDissolutionDate() {
		return dissolutionDate;
	}
	public void setDissolutionDate(String dissolutionDate) {
		this.dissolutionDate = dissolutionDate;
	}

	public String getOrgLOD() {
		return orgLOD;
	}
	public void setOrgLOD(String orgLOD) {
		this.orgLOD = orgLOD;
	}
	
	public List<OrganizationRoleDocument> getOrdSet() {
		return ordSet;
	}
	public void setOrdSet(List<OrganizationRoleDocument> ordSet) {
		this.ordSet = ordSet;
	}

	public List<OrganizationReligion> getOrgRelSet() {
		return orgRelSet;
	}
	public void setOrgRelSet(List<OrganizationReligion> orgRelSet) {
		this.orgRelSet = orgRelSet;
	}

	@Override
	public boolean equals(Object o) {
		Organization org = (Organization) o;
		if(this.getOrgName().equals(org.getOrgName()) && this.getOrgLOD().equals(org.getOrgLOD()) && this.getFormationDate().equals(org.getFormationDate())
				&& this.getDissolutionDate().equals(org.getDissolutionDate())) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		return "(" + "Organization Name: " + this.getOrgName() + "LOD: " + this.getOrgLOD() + ")";
	}
}
