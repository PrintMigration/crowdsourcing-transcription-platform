package mainApplication.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import io.fusionauth.domain.User;
import mainApplication.entities.IndividualNotification;
import mainApplication.entities.Notification;
import mainApplication.fusionAuth.FusionAuthController;
import mainApplication.repositories.IndividualNotificationRepository;
import mainApplication.repositories.NotificationRepository;

@Service
public class NotificationService {
	private final NotificationRepository notifRepo;
	private final IndividualNotificationRepository indNotifRepo;
	private final FusionAuthController fusionAuthController;
	
	public NotificationService(NotificationRepository notifRepo, IndividualNotificationRepository indNotifRepo, FusionAuthController fusionAuthController) {
		this.notifRepo = notifRepo;
		this.indNotifRepo = indNotifRepo;
		this.fusionAuthController = fusionAuthController;
	}
	
	/*
	 * Save the notification and create an individual notification for the user who is the subject of the notification
	 */
	public void setupUserNotification(Notification notif) {
		notifRepo.save(notif);
		
		String userID = notif.getSubjectUserID();
		IndividualNotification indNotif = new IndividualNotification(notif, userID);
		indNotifRepo.save(indNotif);
	}
	
	/*
	 * Save the notification and create individual notifications for each administrator
	 * and an optional other user (email)
	 */
	public void setupAdminNotification(Notification notif, String otherUser) {
		notifRepo.save(notif);
		List<User> admins = fusionAuthController.getAllAdmins();
		if (admins == null)
			return;
		boolean notifiedOtherUser = otherUser == null; // We don't want to notify otherUser twice if they're an admin
		for (User user : admins) {
			String userID = user.email;
			if (userID == null)
				continue;
			if (otherUser != null && otherUser.equals(userID))
				notifiedOtherUser = true;
			IndividualNotification indNotif = new IndividualNotification(notif, userID);
			indNotifRepo.save(indNotif);
		}
		if (!notifiedOtherUser) {
			IndividualNotification indNotif = new IndividualNotification(notif, otherUser);
			indNotifRepo.save(indNotif);
		}
	}
	
	/*
	 * Save the notification and create individual notifications for each administrator
	 */
	public void setupAdminNotification(Notification notif) {
		setupAdminNotification(notif, null);
	}
	
	private List<User> getFakeAdminList() {
		String[] arr = {"a@gmail.com", "b@gmail.com", "c@gmail.com"};
		ArrayList<User> fakeAdmins = new ArrayList<>();
		for (int i = 0; i < arr.length; i++) {
			User u = new User();
			u.email = arr[i];
			fakeAdmins.add(u);
		}
		return fakeAdmins;
	}
}
