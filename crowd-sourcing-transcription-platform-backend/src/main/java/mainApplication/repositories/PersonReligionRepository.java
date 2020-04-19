package mainApplication.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import mainApplication.entities.Person;
import mainApplication.entities.PersonReligion;
import mainApplication.entities.Religion;

@Repository
@Transactional
public interface PersonReligionRepository extends JpaRepository<PersonReligion, Integer>, JpaSpecificationExecutor<PersonReligion> {
	List<PersonReligion> findByReligion(Religion r);
	void deleteByPerson(Person person);
}
