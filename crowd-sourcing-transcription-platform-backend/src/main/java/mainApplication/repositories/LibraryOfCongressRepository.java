package mainApplication.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import mainApplication.entities.LibraryOfCongress;

@Repository
@Transactional
public interface LibraryOfCongressRepository extends JpaRepository<LibraryOfCongress, Integer>, JpaSpecificationExecutor<LibraryOfCongress> {
	LibraryOfCongress findBySubjectHeading(String subjectHeading);
}
