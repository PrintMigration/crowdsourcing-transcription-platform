package mainApplication.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import mainApplication.entities.Request;

@Repository
@Transactional
public interface RequestRepository extends JpaRepository<Request, Integer>, JpaSpecificationExecutor<Request> {
	List<Request> findByRequesterOrderByDateRequestedDesc(String requester);
	List<Request> findByGrantedOrderByDateRequestedDesc(Boolean granted);
	Page<Request> findByGrantedOrderByDateRequestedDesc(Boolean granted, Pageable page);
	List<Request> findByRequesterAndRoleRequestedAndGranted(String requester, String roleRequested, Boolean granted);
}