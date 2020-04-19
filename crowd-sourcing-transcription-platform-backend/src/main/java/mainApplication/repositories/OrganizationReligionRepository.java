package mainApplication.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import mainApplication.entities.Organization;
import mainApplication.entities.OrganizationReligion;

@Repository
@Transactional
public interface OrganizationReligionRepository extends JpaRepository<OrganizationReligion, Integer>, JpaSpecificationExecutor<OrganizationReligion> {
	void deleteByOrg(Organization org);

}
