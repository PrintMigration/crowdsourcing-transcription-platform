package mainApplication.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import mainApplication.entities.DocRepository;

@Repository
@Transactional
public interface DocRepositoryRepository extends JpaRepository<DocRepository, Integer>, JpaSpecificationExecutor<DocRepository> {
	DocRepository findByRepoURL(String repoURL);
}
