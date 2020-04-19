package mainApplication.entities;

import java.util.List;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "language")
public class Language {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "languageid")
	private Integer langID;
	
	//Unique values
	@Column(name = "languagedesc")
	private String langDesc;
	
	//Reference list to all linked documents to a language
	@OneToMany(mappedBy = "docLanguage")
	@JsonIgnore
	private List<Document> docList;
	
	public Language() {
		this.langDesc = "";
	}
	public Language(String inDesc) {
		this.langDesc = inDesc;
	}

	public Integer getLangID() {
		return langID;
	}
	public void setLangID(Integer inLangID) {
		this.langID = inLangID;
	}

	public String getLangDesc() {
		return langDesc;
	}
	public void setLangDesc(String langDesc) {
		this.langDesc = langDesc;
	}

	public List<Document> getDocList() {
		return docList;
	}
	public void setDocList(List<Document> docList) {
		this.docList = docList;
	}

}
