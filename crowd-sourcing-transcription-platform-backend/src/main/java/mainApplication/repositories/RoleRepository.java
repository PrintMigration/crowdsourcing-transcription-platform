package mainApplication.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import mainApplication.entities.Role;

@Repository
@Transactional
public interface RoleRepository extends JpaRepository<Role, Integer>, JpaSpecificationExecutor<Role>{
	boolean existsByRoleDesc(String inRD);
	Role findByRoleDesc(String inRD);
}
