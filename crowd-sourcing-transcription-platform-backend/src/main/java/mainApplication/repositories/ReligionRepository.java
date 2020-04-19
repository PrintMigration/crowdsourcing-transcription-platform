package mainApplication.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import mainApplication.entities.Religion;

@Repository
@Transactional
public interface ReligionRepository extends JpaRepository<Religion, Integer>, JpaSpecificationExecutor<Religion> {
	Religion findByReligionDesc(String religionDesc);
	boolean existsByReligionDesc(String religionDesc);
}
