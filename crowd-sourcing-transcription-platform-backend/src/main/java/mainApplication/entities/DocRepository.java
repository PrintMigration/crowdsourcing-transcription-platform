package mainApplication.entities;

import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "repository")
public class DocRepository {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "repoid")
	private Integer repoID;
	
	@Column(name = "repodesc")
	private String repoDesc;
	
	@Column(name = "repolod")
	private String repoLOD;
	
	//Unique value
	@Column(name = "repourl")
	private String repoURL;
	
	@OneToMany(mappedBy = "docRepository")
	@JsonIgnore
	private List<Document> docList;
	
	public DocRepository() {
		this.repoDesc = "";
		this.repoLOD = "";
		this.repoURL = "";
	}
	
	public DocRepository(String inDesc, String inLOD, String inURL) {
		this.repoDesc = inDesc;
		this.repoLOD = inLOD;
		this.repoURL = inURL;
	}
	
	///////////////////////Getters and setters///////////////////////////////
	public Integer getRepoID() {
		return repoID;
	}
	public void setRepoID(Integer repoID) {
		this.repoID = repoID;
	}

	public String getRepoDesc() {
		return repoDesc;
	}
	public void setRepoDesc(String repoDesc) {
		this.repoDesc = repoDesc;
	}

	public String getRepoLOD() {
		return repoLOD;
	}
	public void setRepoLOD(String repoLOD) {
		this.repoLOD = repoLOD;
	}

	public String getRepoURL() {
		return repoURL;
	}
	public void setRepoURL(String repoURL) {
		this.repoURL = repoURL;
	}

	public List<Document> getDocList() {
		return docList;
	}
	public void setDocList(List<Document> docList) {
		this.docList = docList;
	}	
}
