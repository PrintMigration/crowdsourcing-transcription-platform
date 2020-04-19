package mainApplication.entities;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "person2religion")
public class PersonReligion {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "person2religionid")
	private Integer p2relID;
	
	@ManyToOne @JoinColumn(name = "personid")
	@JsonIgnore
	private Person person;
	
	@ManyToOne @JoinColumn(name = "religionid")
	private Religion religion;
	
	@Column(name = "datespan")
	private String dateSpan;
	
	public PersonReligion() {
	}
	
	public PersonReligion(Person inPerson, Religion inreligion, String indateSpan) {
		this.person = inPerson;
		this.religion = inreligion;
		this.dateSpan = indateSpan;
	}

	public Integer getP2relID() {
		return p2relID;
	}
	public void setP2relID(Integer p2relID) {
		this.p2relID = p2relID;
	}

	public Person getPerson() {
		return person;
	}
	public void setPerson(Person personID) {
		this.person = personID;
	}

	public Religion getReligion() {
		return religion;
	}
	public void setReligion(Religion religionID) {
		this.religion = religionID;
	}

	public String getDateSpan() {
		return dateSpan;
	}
	public void setDateSpan(String dateSpan) {
		this.dateSpan = dateSpan;
	}
}
