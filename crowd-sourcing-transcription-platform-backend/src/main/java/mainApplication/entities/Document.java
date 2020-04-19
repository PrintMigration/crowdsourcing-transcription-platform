package mainApplication.entities;

import java.util.*;

import javax.persistence.*;

import org.springframework.data.annotation.CreatedDate;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import mainApplication.customSerializers.*;

@Entity
@Table(name = "document") //Defining which table this class is for
public class Document {
	//Declaring all values associated with a document
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY) //Tells that id is a generated value and how to generate it
	@Column(name = "documentid")
	private Integer documentID;
	
	@ManyToOne @JoinColumn(name = "doctypeid")
	private DocumentType docType;
	
	@ManyToOne @JoinColumn(name = "languageid")
	private Language docLanguage;
	
	@ManyToOne @JoinColumn(name = "repositoryid")
	private DocRepository docRepository;
	
	@ManyToMany @JoinTable(name = "libraryofcongress2doc",
    joinColumns = @JoinColumn(name = "docID", referencedColumnName = "documentID"),
    inverseJoinColumns = @JoinColumn(name = "locID", referencedColumnName = "locID"))
	private List<LibraryOfCongress> libraryOfCongresses;
	
	@ManyToMany @JoinTable(name = "keyword2document",
	joinColumns = @JoinColumn(name = "docID", referencedColumnName = "documentID"),
	inverseJoinColumns = @JoinColumn(name = "keywordID", referencedColumnName = "keywordID"))
	private List<Keyword> keywords;
	
	//Needs special api to add related letters to list
	@ManyToMany @JoinTable(name = "relatedletters",
	joinColumns = @JoinColumn(name = "documentID", referencedColumnName = "documentID"),
	inverseJoinColumns = @JoinColumn(name = "relatedLetterID", referencedColumnName = "documentID"))
	@JsonSerialize(using = DocumentListSerializer.class)
	private List<Document> relatedDocuments;
	
	@OneToMany(mappedBy = "baseDoc")
	@JsonSerialize(using = DocumentEditSerializer.class)
	private List<DocumentEdit> edits;
	
	@OneToMany(mappedBy = "document")
	private List<PersonRoleDocument> prdSet;
	
	@OneToMany(mappedBy = "document") 
	private List<PlaceRoleDocument> placerdSet;

	@OneToMany(mappedBy = "document")
	private List<OrganizationRoleDocument> ordSet;
	
	//Unique value
	@Column(name = "importid")
	private String importID;
	
	@Column(name = "pdfdesc", columnDefinition = "longtext")
	private String pdfDesc;
	@Column(name = "pdfurl")
	private String pdfURL;
	@Column(name = "internalpdfname")
	private String internalPDFName;
	@Column(name = "collection")
	private String collection;
	@Column(name = "sortingdate")
	private String sortingDate;
	@Column(name = "letterdate")
	private String letterDate;
	@Column(name = "customcitation", columnDefinition = "longtext")
	private String customCitation;
	@Column(name = "status")
	private String status;
	@Column(name = "whoCheckedOut")
	private String user;
	
	@Column(name = "abstract", columnDefinition = "longtext")
	private String docAbstract;
	@Column(name = "researchnotes", columnDefinition = "longtext")
	private String researchNotes;
	
	@Column(name = "isJulian", columnDefinition = "tinyint")
	private Boolean isJulian;
	
	@CreatedDate @Column(name = "dateadded")
	private Date dateAdded;
	
	//Constructors
	public Document() {
		this.docLanguage = null;
		this.docRepository = null;
		this.docType = null;
		this.importID = "";
		this.pdfDesc = "";
		this.pdfURL = "";
		this.sortingDate = "";
		this.letterDate = "";
		this.customCitation = "";
		this.status = "";
		this.docAbstract = "";
		this.researchNotes = "";
		this.isJulian = null;
		this.libraryOfCongresses = null;
		this.keywords = null;
		this.relatedDocuments = null;
		this.edits = null;
		this.dateAdded = null;
	}
	
	public Document(Language inLang, DocRepository inRepo, DocumentType inType, String inImport, String inPdfDesc, String inPdfURL, String inSortingDate, 
					String inLetterDate, String inCustom, String inStatus, String inAbstract, String inResearch, Boolean inJ, 
					List<LibraryOfCongress> inLOC, List<Keyword> inWords, List<Document> inRelated, List<DocumentEdit> inEdits) {
		this.docLanguage = inLang;
		this.docRepository = inRepo;
		this.docType = inType;
		this.importID = inImport;
		this.pdfDesc = inPdfDesc;
		this.pdfURL = inPdfURL;
		this.sortingDate = inSortingDate;
		this.letterDate = inLetterDate;
		this.customCitation = inCustom;
		this.status = inStatus;
		this.docAbstract = inAbstract;
		this.researchNotes = inResearch;
		this.isJulian = inJ;
		this.libraryOfCongresses = inLOC;
		this.keywords = inWords;
		this.relatedDocuments = inRelated;
		this.edits = inEdits;
	}


	public Integer getDocumentID() {
		return documentID;
	}
	public void setDocumentID(Integer documentID) {
		this.documentID = documentID;
	}

	public DocumentType getDocType() {
		return docType;
	}
	public void setDocType(DocumentType docType) {
		this.docType = docType;
	}

	public Language getDocLanguage() {
		return docLanguage;
	}
	public void setDocLanguage(Language docLanguage) {
		this.docLanguage = docLanguage;
	}

	public DocRepository getDocRepository() {
		return docRepository;
	}
	public void setDocRepository(DocRepository docRepository) {
		this.docRepository = docRepository;
	}

	public List<LibraryOfCongress> getLibraryOfCongresses() {
		return libraryOfCongresses;
	}
	public void setLibraryOfCongresses(List<LibraryOfCongress> libraryOfCongresses) {
		this.libraryOfCongresses = libraryOfCongresses;
	}

	public List<Keyword> getKeywords() {
		return keywords;
	}
	public void setKeywords(List<Keyword> keywords) {
		this.keywords = keywords;
	}

	public List<Document> getRelatedDocuments() {
		return relatedDocuments;
	}
	public void setRelatedDocuments(List<Document> relatedDocuments) {
		this.relatedDocuments = relatedDocuments;
	}

	public List<DocumentEdit> getEdits() {
		return edits;
	}
	public void setEdits(List<DocumentEdit> edits) {
		this.edits = edits;
	}

	public List<PersonRoleDocument> getPrdSet() {
		return prdSet;
	}
	public void setPrdSet(List<PersonRoleDocument> prdSet) {
		this.prdSet = prdSet;
	}

	public List<PlaceRoleDocument> getPlacerdSet() {
		return placerdSet;
	}
	public void setPlacerdSet(List<PlaceRoleDocument> placerdSet) {
		this.placerdSet = placerdSet;
	}

	public List<OrganizationRoleDocument> getOrdSet() {
		return ordSet;
	}
	public void setOrdSet(List<OrganizationRoleDocument> ordSet) {
		this.ordSet = ordSet;
	}

	public String getImportID() {
		return importID;
	}
	public void setImportID(String importID) {
		this.importID = importID;
	}

	public String getPdfDesc() {
		return pdfDesc;
	}
	public void setPdfDesc(String pdfDesc) {
		this.pdfDesc = pdfDesc;
	}

	public String getPdfURL() {
		return pdfURL;
	}
	public void setPdfURL(String pdfURL) {
		this.pdfURL = pdfURL;
	}

	public String getCollection() {
		return collection;
	}
	public void setCollection(String collection) {
		this.collection = collection;
	}

	public String getSortingDate() {
		return sortingDate;
	}
	public void setSortingDate(String sortingDate) {
		this.sortingDate = sortingDate;
	}

	public String getLetterDate() {
		return letterDate;
	}
	public void setLetterDate(String letterDate) {
		this.letterDate = letterDate;
	}

	public String getCustomCitation() {
		return customCitation;
	}
	public void setCustomCitation(String customCitation) {
		this.customCitation = customCitation;
	}

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}

	public String getDocAbstract() {
		return docAbstract;
	}
	public void setDocAbstract(String docAbstract) {
		this.docAbstract = docAbstract;
	}

	public String getResearchNotes() {
		return researchNotes;
	}
	public void setResearchNotes(String researchNotes) {
		this.researchNotes = researchNotes;
	}

	public Boolean getIsJulian() {
		return isJulian;
	}
	public void setIsJulian(Boolean isJulian) {
		this.isJulian = isJulian;
	}

	public Date getDateAdded() {
		return dateAdded;
	}
	public void setDateAdded(Date date) {
		this.dateAdded = date;
	}

	public String getInternalPDFName() {
		return internalPDFName;
	}
	public void setInternalPDFName(String internalPDFname) {
		this.internalPDFName = internalPDFname;
	}
	
	@Override
	public String toString() {
		return this.importID;
	}

	@Override
	public int hashCode() {
		return pdfDesc.hashCode() + importID.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		Document test = (Document) o;
		
		if(this.getImportID().equals(test.getImportID())) {
			return true;
		}
		
		return false;
	}
}
