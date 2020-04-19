package mainApplication.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import mainApplication.entities.Document;

@Repository
@Transactional
public interface DocumentRepository extends JpaRepository<Document, Integer>, JpaSpecificationExecutor<Document> {
	Page<Document> findAll(Pageable page);
	Document findByImportID(String importID);
	boolean existsByImportID(String importID);
	Document findByDocumentID(int docID);
	
	@Query(value = "select d.importID "
			+ "from document d",
			nativeQuery = true)
	List<String> findAllImportID();
	
	@Query(value = "select distinct d.* "
			+ "from document d "
			+ "join keyword2document kd on kd.docID = d.documentID "
			+ "join keyword k on k.keywordID = kd.keywordID "
			+ "where k.keyword like %:keyword%",
			nativeQuery = true)
	List<Document> docsFromKeyword(@Param("keyword") String keyword);
	
	@Query(value = "select distinct d.* "
			+ "from document d "
			+ "join libraryofcongress2doc ld on ld.docID = d.documentID "
			+ "join libraryofcongress l on l.locID = ld.locID "
			+ "where l.locSubjectHeading like %:loc%",
			nativeQuery = true)
	List<Document> docsFromLOC(@Param("loc") String loc);
	
	@Query(value = "select distinct d.* " 
			+ "from document d " 
			+ "join person2document prd on prd.docID = d.documentID " 
			+ "join person p on prd.personID = p.personID "
			+ "join role r on prd.roleID = r.roleID " 
			+ "where p.firstName like %:first% and p.middleName like %:middle% and p.lastName like %:last% and p.prefix like %:prefix% and p.suffix like %:suffix% " 
			+ "and p.biography like %:biography% and p.occupation like %:occupation% and p.personLOD like %:personLOD% and r.roleDesc like %:role%",
			nativeQuery = true)
	List<Document> docsFromPeople(@Param("first") String first, @Param("middle") String middle, @Param("last") String last, @Param("prefix") String prefix, 
			@Param("suffix") String suffix, @Param("biography") String biography, @Param("occupation") String occupation, @Param("personLOD") String personLOD,
			@Param("role") String role);
	
	@Query(value = "select distinct d.* " 
			+ "from document d " 
			+ "join organization2document ord on ord.docID = d.documentID " 
			+ "join organization o on ord.organizationID = o.organizationID "
			+ "join role r on ord.roleID = r.roleID " 
			+ "where o.organizationName like %:orgName% and o.organizationLOD like %:orgLOD% and r.roleDesc like %:role%",
			nativeQuery = true)
	List<Document> docsFromOrgs(@Param("orgName") String orgName, @Param("orgLOD") String orgLOD, @Param("role") String role);
	
	@Query(value = "select distinct d.* " 
			+ "from document d " 
			+ "join place2document pprd on pprd.docID = d.documentID " 
			+ "join place pp on pprd.placeID = pp.placeID "
			+ "join role r on pprd.roleID = r.roleID " 
			+ "where pp.placeCounty like %:county% and pp.placeCountry like %:country% and pp.placeStateProv like %:stateProv% and pp.placeTownCity like %:townCity% " 
			+ "and pp.placeDesc like %:placeDesc% and pp.placeLOD like %:placeLOD% and r.roleDesc like %:role%",
			nativeQuery = true)
	List<Document> docsFromPlaces(@Param("county") String county, @Param("country") String country, @Param("stateProv") String stateProv, @Param("townCity") String townCity,
			@Param("placeDesc") String placeDesc, @Param("placeLOD") String placeLOD, @Param("role") String role);
	
	@Query(value = "select distinct d.collection "
			+ "from document d",
			nativeQuery = true)
	List<String> getAllCollections();
	
	@Query(value = "select distinct d.* "
			+ "from document d "
			+ "where d.collection like %:collection%",
			nativeQuery = true)
	List<Document> docsFromCollection(@Param("collection") String collection, Pageable page);
	
	//Needed if going to search for gender
//	@Query(value = "select distinct d.* " 
//			+ "from document d " 
//			+ "join person2document prd on prd.docID = d.documentID " 
//			+ "join person p on prd.personID = p.personID "
//			+ "join role r on prd.roleID = r.roleID " 
//			+ "where p.firstName like %:first% and p.middleName like %:middle% and p.lastName like %:last% and p.prefix like %:prefix% and p.suffix like %:suffix% " 
//			+ "and p.biography like %:biography% and p.occupation like %:occupation% and p.personLOD like %:personLOD% and r.roleDesc like %:role% and p.gender like :gender",
//			nativeQuery = true)
//	List<Document> docsFromPeopleWGender(@Param("first") String first, @Param("middle") String middle, @Param("last") String last, @Param("prefix") String prefix, 
//			@Param("suffix") String suffix, @Param("biography") String biography, @Param("occupation") String occupation, @Param("personLOD") String personLOD,
//			@Param("role") String role, @Param("gender") Character gender);
}
