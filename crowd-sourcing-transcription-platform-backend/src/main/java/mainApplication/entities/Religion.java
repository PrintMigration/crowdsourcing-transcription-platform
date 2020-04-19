package mainApplication.entities;

import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "religion")
public class Religion {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "religionid")
	private Integer religionID;
	
	//Unique value
	@Column(name = "religiondesc")
	private String religionDesc;
	
	@OneToMany(mappedBy = "religion")
	@JsonIgnore
	private List<OrganizationReligion> orgRelSet;
	
	@OneToMany(mappedBy = "religion")
	@JsonIgnore
	private List<PersonReligion> pRelSet;
	
	public Religion() {
		this.religionDesc = "";
	}

	public Integer getReligionID() {
		return religionID;
	}
	public void setReligionID(Integer religionID) {
		this.religionID = religionID;
	}

	public String getReligionDesc() {
		return religionDesc;
	}
	public void setReligionDesc(String religionDesc) {
		this.religionDesc = religionDesc;
	}

	public List<OrganizationReligion> getOrgRelSet() {
		return orgRelSet;
	}
	public void setOrgRelSet(List<OrganizationReligion> orgRelSet) {
		this.orgRelSet = orgRelSet;
	}

	public List<PersonReligion> getpRelSet() {
		return pRelSet;
	}
	public void setpRelSet(List<PersonReligion> pRelSet) {
		this.pRelSet = pRelSet;
	}
	
	@Override
	public boolean equals(Object o) {
		Religion r = (Religion) o;
		
		if(r.getReligionDesc().equals(this.getReligionDesc()))
			return true;
		
		return false;
	}
}
