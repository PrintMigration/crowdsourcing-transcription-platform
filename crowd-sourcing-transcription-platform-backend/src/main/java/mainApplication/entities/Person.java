package mainApplication.entities;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "person")
public class Person {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY) //Tells that id is a generated value and how to generate it
	@Column(name = "personid")
	private Integer personID;
	
	@OneToMany(mappedBy = "person")
	@JsonIgnore
	private List<PersonRoleDocument> prdSet;
	
	@OneToMany(mappedBy = "person")
	private List<PersonReligion> pRelSet;
	
	@Column(name = "firstname")
	private String firstName;
	@Column(name = "middlename")
	private String middleName;
	@Column(name = "lastname")
	private String lastName;
	@Column(name = "prefix")
	private String prefix;
	@Column(name = "suffix")
	private String suffix;
	@Column(name = "biography", columnDefinition = "longtext")
	private String biography;
	@Column(name = "gender")
	private Character gender;
	@Column(name = "birthdate")
	private String birthDate;
	@Column(name = "deathdate")
	private String deathDate;
	@Column(name = "occupation")
	private String occupation;
	
	//Unique value
	@Column(name = "personlod")
	private String personLOD;
	
	public Person() {
		this.firstName = "";
		this.middleName = "";
		this.lastName = "";
		this.prefix = "";
		this.suffix = "";
		this.biography = "";
		this.gender = 'U';
		this.birthDate = "";
		this.deathDate = "";
		this.occupation = "";
		this.personLOD = "";
		this.pRelSet = new ArrayList<>();
	}
	
	public Person(String lod) {
		this.personLOD = lod;
	}

	public Integer getPersonID() {
		return personID;
	}
	public void setPersonID(Integer personID) {
		this.personID = personID;
	}
	
	public List<PersonRoleDocument> getPrdSet() {
		return prdSet;
	}
	public void setPrdSet(List<PersonRoleDocument> prdSet) {
		this.prdSet = prdSet;
	}

	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getSuffix() {
		return suffix;
	}
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getBiography() {
		return biography;
	}
	public void setBiography(String biography) {
		this.biography = biography;
	}

	public Character getGender() {
		return gender;
	}
	public void setGender(Character gender) {
		this.gender = gender;
	}

	public String getBirthDate() {
		return birthDate;
	}
	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}

	public String getDeathDate() {
		return deathDate;
	}
	public void setDeathDate(String deathDate) {
		this.deathDate = deathDate;
	}

	public String getOccupation() {
		return occupation;
	}
	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}

	public String getPersonLOD() {
		return personLOD;
	}
	public void setPersonLOD(String personLOD) {
		this.personLOD = personLOD;
	}
	
	public List<PersonReligion> getpRelSet() {
		return pRelSet;
	}
	public void setpRelSet(List<PersonReligion> pRelSet) {
		this.pRelSet = pRelSet;
	}
	
	public boolean noData() {
		if(this.getPersonLOD().equals("") && this.getFirstName().equals("") && this.getMiddleName().equals("")
				&& this.getLastName().equals("") && this.getPrefix().equals("") && this.getSuffix().equals("")
				&& this.getBiography().equals("") && this.getOccupation().equals("")) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean equals(Object o) {
		Person inP = (Person) o;
		if(this.getPersonLOD().equals(inP.getPersonLOD()) && this.getFirstName().equals(inP.getFirstName()) && this.getMiddleName().equals(inP.getMiddleName())
				&& this.getLastName().equals(inP.getLastName()) && this.getPrefix().equals(inP.getPrefix()) && this.getSuffix().equals(inP.getSuffix())
				&& this.getBirthDate().equals(inP.getBirthDate()) && this.getDeathDate().equals(inP.getDeathDate()) 
				&& this.getOccupation().equals(inP.getOccupation())) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		return "(First: " + this.firstName + " Middle: "+ this.middleName + " Last:"+ this.lastName + " LOD:" + this.personLOD + " Prefix: " + this.suffix + 
				" Suffix: " + this.suffix + ")";
	}
}
