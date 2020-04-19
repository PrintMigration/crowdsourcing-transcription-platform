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
@Table(name = "notification")
public class Notification {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "notificationid")
	private Integer notificationID;
	
	@Column(name = "notificationtype")
	private String notificationType;
	@Column(name = "notificationtext")
	private String notificationText;
	@Column(name = "extratext", columnDefinition = "longtext")
	private String extraText;
	@Column(name = "subjectuserid")
	private String subjectUserID;
	
	@Column(name = "date")
	@JsonFormat(pattern=Constants.DATE_TIME_FORMAT)
	private Date date; // When the event occurred
	
	public Notification() { }
	
	public Notification(String type, String text, String userId) {
		this.notificationType = type;
		this.notificationText = text;
		this.subjectUserID = userId;
		setDate(new Date(System.currentTimeMillis()));
	}
	
	public Integer getNotificationID() {
		return notificationID;
	}
	public void setNotificationID(Integer notificationID) {
		this.notificationID = notificationID;
	}

	public String getNotificationText() {
		return notificationText;
	}
	public void setNotificationText(String notificationText) {
		this.notificationText = notificationText;
	}
	
	public String getExtraText() {
		return extraText;
	}
	public void setExtraText(String extraText) {
		this.extraText = extraText;
	}

	public String getNotificationType() {
		return notificationType;
	}
	public void setNotificationType(String notificationType) {
		this.notificationType = notificationType;
	}

	public String getSubjectUserID() {
		return subjectUserID;
	}
	public void setSubjectUserID(String subjectUserID) {
		this.subjectUserID = subjectUserID;
	}

	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
}