package mainApplication.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

import mainApplication.Constants;

@Entity
@Table(name = "request")
public class Request {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "requestid")
	private Integer requestID;
	
	@Column(name = "requester")
	private String requester;
	@Column(name = "rolerequested")
	private String roleRequested;
	@Column(name = "granted", columnDefinition = "tinyint")
	private Boolean granted;
	
	@Column(name = "daterequested")
	@JsonFormat(pattern=Constants.DATE_TIME_FORMAT)
	private Date dateRequested;
	
	public Request() { }
	
	public Request(String requester, String roleRequested) {
		this.requester = requester;
		this.roleRequested = roleRequested;
		setDateRequested(new Date(System.currentTimeMillis()));
	}

	public Integer getRequestID() {
		return requestID;
	}
	public void setRequestID(Integer requestID) {
		this.requestID = requestID;
	}

	public String getRequester() {
		return requester;
	}
	public void setRequester(String requester) {
		this.requester = requester;
	}

	public String getRoleRequested() {
		return roleRequested;
	}
	public void setRoleRequested(String roleRequested) {
		this.roleRequested = roleRequested;
	}

	public Boolean getGranted() {
		return granted;
	}
	public void setGranted(Boolean granted) {
		this.granted = granted;
	}

	public Date getDateRequested() {
		return dateRequested;
	}
	public void setDateRequested(Date dateRequested) {
		this.dateRequested = dateRequested;
	}
}