package mainApplication.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "individualnotification")
public class IndividualNotification {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "individualnotificationid")
	private Integer individualNotificationID;
	
	@ManyToOne @JoinColumn(name = "notificationid")
	private Notification notification;
	
	@Column(name = "notifywhoid")
	private String userNotified;
	@Column(name = "seen", columnDefinition = "tinyint")
	private Boolean seen;
	
	public IndividualNotification() { }
	
	public IndividualNotification(Notification notification, String userID) {
		this.notification = notification;
		this.userNotified = userID;
		this.seen = false;
	}

	public Integer getIndividualNotificationID() {
		return individualNotificationID;
	}
	public void setIndividualNotificationID(Integer individualNotificationID) {
		this.individualNotificationID = individualNotificationID;
	}

	public Notification getNotification() {
		return notification;
	}
	public void setNotification(Notification notification) {
		this.notification = notification;
	}

	public String getUserNotified() {
		return userNotified;
	}
	public void setUserNotified(String userNotified) {
		this.userNotified = userNotified;
	}

	public Boolean getSeen() {
		return seen;
	}
	public void setSeen(Boolean seen) {
		this.seen = seen;
	}
}