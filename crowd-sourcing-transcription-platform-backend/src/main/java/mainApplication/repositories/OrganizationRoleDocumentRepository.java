package mainApplication.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import mainApplication.entities.OrganizationRoleDocument;
import mainApplication.entities.Organization;
import mainApplication.entities.Role;
import mainApplication.entities.Document;

import java.util.List;

@Repository
@Transactional
public interface OrganizationRoleDocumentRepository extends JpaRepository<OrganizationRoleDocument, Integer>, JpaSpecificationExecutor<OrganizationRoleDocument> {
	List<OrganizationRoleDocument> findByOrg(Organization inOrg);
	List<OrganizationRoleDocument> findByDocument(Document inDoc);
	List<OrganizationRoleDocument> findByRole(Role inRole);
	void deleteByDocument(Document result);
}
