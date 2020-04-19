package mainApplication.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import mainApplication.entities.Document;
import mainApplication.entities.Place;
import mainApplication.entities.PlaceRoleDocument;
import mainApplication.entities.Role;

@Repository
@Transactional
public interface PlaceRoleDocumentRepository extends JpaRepository<PlaceRoleDocument, Integer>, JpaSpecificationExecutor<PlaceRoleDocument> {
	List<PlaceRoleDocument> findByDocument(Document inDoc);
	List<PlaceRoleDocument> findByPlace(Place inPlace);
	//Possible useful in future versions of this project
//	List<PlaceRoleDocument> findByRole(Role inRole);
	void deleteByDocument(Document result);
}
