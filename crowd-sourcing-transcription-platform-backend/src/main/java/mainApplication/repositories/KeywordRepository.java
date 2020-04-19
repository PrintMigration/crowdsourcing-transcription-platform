package mainApplication.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import mainApplication.entities.Keyword;

@Repository
@Transactional
public interface KeywordRepository extends JpaRepository<Keyword, Integer>, JpaSpecificationExecutor<Keyword> {
	Keyword findByKeyword(String keyword);
	Keyword findByKeywordLOD(String keywordLOD);
}
