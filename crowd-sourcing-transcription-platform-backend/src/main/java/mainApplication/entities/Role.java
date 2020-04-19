package mainApplication.entities;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

@Entity
@Table(name = "role")
public class Role {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "roleid")
	private Integer roleID;
	
	//Unique value
	@Column(name = "roledesc")
	private String roleDesc;
	
	@OneToMany(mappedBy = "role")
	@JsonIgnore
	private List<PersonRoleDocument> prdSet;
	
	@OneToMany(mappedBy = "role")
	@JsonIgnore
	private List<PlaceRoleDocument> placerdSet;
	
	@OneToMany(mappedBy = "role")
	@JsonIgnore
	private List<OrganizationRoleDocument> ordSet;
	
	public Role() {
		this.roleDesc = "";
	}
	
	public Role(String inDesc) {
		this.roleDesc = inDesc;
	}

	public Integer getRoleID() {
		return roleID;
	}
	public void setRoleID(Integer roleID) {
		this.roleID = roleID;
	}

	public String getRoleDesc() {
		return roleDesc;
	}
	public void setRoleDesc(String roleDesc) {
		this.roleDesc = roleDesc;
	}

	public List<PersonRoleDocument> getPrdSet() {
		return prdSet;
	}
	public void setPrdSet(List<PersonRoleDocument> prdSet) {
		this.prdSet = prdSet;
	}
	
	@Override
	public boolean equals(Object o) {
		Role inR = (Role) o;
		System.out.println(this.getRoleDesc() + " : " + inR.getRoleDesc());
		if(this.getRoleDesc().equals(inR.getRoleDesc())) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		return this.roleDesc;
	}
}
