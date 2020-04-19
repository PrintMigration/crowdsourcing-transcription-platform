package mainApplication.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import mainApplication.entities.Person;
import mainApplication.entities.Role;
import mainApplication.entities.Document;
import mainApplication.entities.PersonRoleDocument;

@Repository
@Transactional
public interface PersonRoleDocumentRepository extends JpaRepository<PersonRoleDocument, Integer>, JpaSpecificationExecutor<PersonRoleDocument> {
	List<PersonRoleDocument> findByDocument(Document inDoc);
	List<PersonRoleDocument> findByPerson(Person inPerson);
	List<PersonRoleDocument> findByRole(Role inRole);
	void deleteByDocument(Document doc);
}
