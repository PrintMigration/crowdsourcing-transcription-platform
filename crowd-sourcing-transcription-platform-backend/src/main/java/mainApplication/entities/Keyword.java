package mainApplication.entities;

import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "keyword")
public class Keyword {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "keywordid")
	private Integer keywordID;
	
	//Unique value
	@Column(name = "keyword")
	private String keyword;
	@Column(name = "keywordlod")
	private String keywordLOD;
	
	@ManyToMany(mappedBy = "keywords")
	@JsonIgnore
	private List<Document> docList;
	
	public Keyword() {
		this.keyword = "";
		this.keywordLOD = "";
	}
	
	public Keyword(String inWord) {
		this.keyword = inWord;
		this.keywordLOD = "";
	}
	
	public Integer getKeywordID() {
		return keywordID;
	}
	public void setKeywordID(Integer keywordID) {
		this.keywordID = keywordID;
	}
	
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	
	public String getKeywordLOD() {
		return keywordLOD;
	}
	public void setKeywordLOD(String keywordLOD) {
		this.keywordLOD = keywordLOD;
	}
	
	public List<Document> getDocList() {
		return docList;
	}
	public void setDocList(List<Document> docList) {
		this.docList = docList;
	}
	
	@Override
	public boolean equals(Object o) {
		Keyword inKey = (Keyword) o;
		
		if(this.getKeyword().equals(inKey.getKeyword()) && this.getKeywordLOD().equals(inKey.getKeywordLOD())) {
			return true;
		}
		
		return false;
	}
}
