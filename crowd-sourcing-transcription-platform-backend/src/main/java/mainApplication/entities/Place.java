package mainApplication.entities;

import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "place")
public class Place {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY) //Tells that id is a generated value and how to generate it
	@Column(name = "placeid")
	private Integer placeID;
	
	@OneToMany(mappedBy = "place")
	@JsonIgnore
	private List<PlaceRoleDocument> placerdSet;
	
	//Unique value
	@Column(name = "placelat")
	private Double latitude;
	//Unique value
	@Column(name = "placelong")
	private Double longitude;
	@Column(name = "placecounty")
	private String county;
	@Column(name = "placecountry")
	private String country;
	@Column(name = "placestateprov")
	private String stateProv;
	@Column(name = "placetowncity")
	private String townCity;
	@Column(name = "placedesc")
	private String placeDesc;
	@Column(name = "placelod")
	private String placeLOD;
	
	public Place() {
		this.latitude = null;
		this.longitude = null;
		this.county = "";
		this.country = "";
		this.stateProv = "";
		this.townCity = "";
		this.placeDesc = "";
		this.placeLOD = "";
	}
	
	public Place(Double inLat, Double inLong) {
		this.latitude = inLat;
		this.longitude = inLong;
	}
	
	public Integer getPlaceID() {
		return placeID;
	}
	public void setPlaceID(Integer placeID) {
		this.placeID = placeID;
	}
	
	public List<PlaceRoleDocument> getPlacerdSet() {
		return placerdSet;
	}
	public void setPlacerdSet(List<PlaceRoleDocument> placerdSet) {
		this.placerdSet = placerdSet;
	}

	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double placeLat) {
		this.latitude = placeLat;
	}
	
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double placeLong) {
		this.longitude = placeLong;
	}
	
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	
	public String getStateProv() {
		return stateProv;
	}
	public void setStateProv(String stateProv) {
		this.stateProv = stateProv;
	}
	
	public String getTownCity() {
		return townCity;
	}
	public void setTownCity(String townCity) {
		this.townCity = townCity;
	}
	
	public String getPlaceDesc() {
		return placeDesc;
	}
	public void setPlaceDesc(String placeDesc) {
		this.placeDesc = placeDesc;
	}
	
	public String getPlaceLOD() {
		return placeLOD;
	}
	public void setPlaceLOD(String placeLOD) {
		this.placeLOD = placeLOD;
	}
	
	public String getCounty() {
		return county;
	}
	public void setCounty(String county) {
		this.county = county;
	}
	
	public boolean noData() {
		if(this.getCounty().equals("") && this.getCountry().equals("") && this.getTownCity().equals("") && this.getStateProv().equals("") && this.getPlaceDesc().equals("")
				&& this.getPlaceLOD().equals("")) {
			return true;
		}
		
		return false;
	}

	@Override
	public boolean equals(Object o) {
		Place place = (Place) o;
		
		if(this.getLatitude().equals(place.getLatitude()) && this.getLongitude().equals(place.getLongitude()) && this.getCountry().equals(place.getCountry())
				&& this.getPlaceDesc().equals(place.getPlaceDesc()) && this.getPlaceLOD().equals(place.getPlaceLOD()) 
				&& this.getStateProv().equals(place.getStateProv()) && this.getTownCity().equals(place.getTownCity()) && this.getCounty().equals(place.getCounty())) {
			return true;
		}
		else
			return false;
	}
	
	@Override
	public String toString() {
		return "(Place Description: " + this.placeDesc + ", Latitude: " + this.latitude + ", Longitude: " + this.longitude + " )";
	}
}
