package mainApplication.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import mainApplication.entities.IndividualNotification;

@Repository
@Transactional
public interface IndividualNotificationRepository extends JpaRepository<IndividualNotification, Integer>, JpaSpecificationExecutor<IndividualNotification> {
	List<IndividualNotification> findByUserNotified(String userID);
	Page<IndividualNotification> findByUserNotified(String userID, Pageable page);
	List<IndividualNotification> findByUserNotifiedAndSeen(String userID, boolean seen);
}