package mainApplication.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import mainApplication.entities.DocumentType;

@Repository
@Transactional
public interface DocumentTypeRepository extends JpaRepository<DocumentType, Integer>, JpaSpecificationExecutor<DocumentType> {
	DocumentType findByTypeDesc(String typeDesc);
}
