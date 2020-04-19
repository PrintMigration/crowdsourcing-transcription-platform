package mainApplication.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import mainApplication.entities.DocumentEdit;

@Repository
@Transactional
public interface DocumentEditRepository extends JpaRepository<DocumentEdit, Integer>, JpaSpecificationExecutor<DocumentEdit> {
	List<DocumentEdit> findByUserID(String userID);
	List<DocumentEdit> findByUserIDAndCompletionDate(String userID, Date completionDate);
	DocumentEdit findTopByBaseDoc_DocumentIDOrderByCompletionDate(int docID);
	
	/*
	 * Find the most recently completed Document Edits with a certain textType for a document.
	 * If none are completed, it might return an edit in progress.
	 */
	DocumentEdit findTopByBaseDoc_DocumentIDAndTextTypeOrderByCompletionDateDesc(int docID, String textType);
}
