package mainApplication.specifications;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.*;

import org.springframework.data.jpa.domain.Specification;

import mainApplication.entities.*;

@SuppressWarnings("serial")
public class DocumentSpecification implements Specification<Document> {
	private Document filter;
	
	public DocumentSpecification(Document filter) {
		super();
		this.filter = filter;
	}
	
	//We might need to add null checks depending on how the front end wants to send data
	@Override
	public Predicate toPredicate(Root<Document> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
		List<Predicate> p = new ArrayList<>();
		
		if(filter.getImportID() != null && !filter.getImportID().equals("")) {
			p.add(cb.like(root.get("importID"), "%" + filter.getImportID() + "%"));
		}
			
		if(filter.getPdfDesc() != null && !filter.getPdfDesc().equals("")) {
			p.add(cb.like(root.get("pdfDesc"), "%" + filter.getPdfDesc() + "%"));
		}
		
		if(filter.getInternalPDFName() != null && !filter.getInternalPDFName().equals("")) {
			p.add(cb.like(root.get("internalPDFName"), "%" + filter.getInternalPDFName() + "%"));
		}

		if(filter.getCollection() != null && !filter.getCollection().equals("")) {
			p.add(cb.like(root.get("collection"), "%" + filter.getCollection() + "%"));
		}
		
		
		if(filter.getSortingDate() != null && !filter.getSortingDate().equals("")) {
			//This splits the a string into parts based on something
			String[] dates = filter.getSortingDate().split("-");
			
			if(filter.getSortingDate().equals("")) {
				dates[0] = "0";
				dates[1] = "9999";
			}
			
			p.add(cb.and(cb.greaterThanOrEqualTo(root.get("sortingDate"), dates[0]),
					cb.lessThanOrEqualTo(root.get("sortingDate"), dates[1]))
				 );
		}
		
		if(filter.getIsJulian() != null) {
			p.add(cb.equal(root.get("isJulian"), filter.getIsJulian()));
		}
		
		if(filter.getCustomCitation() != null && !filter.getCustomCitation().equals("")) {
			p.add(cb.like(root.get("customCitation"), "%" + filter.getCustomCitation() + "%"));
		}
		
		if(filter.getStatus() != null && !filter.getStatus().equals("") && !filter.getStatus().equals("Select Status")) {
			p.add(cb.equal(root.get("status"), "%" + filter.getStatus() + "%"));
		}
		
		if(filter.getDocAbstract() != null && !filter.getDocAbstract().equals("")) {
			p.add(cb.like(root.get("docAbstract"), "%" + filter.getDocAbstract() + "%"));
		}
		
		if(filter.getDocType() != null && !filter.getDocType().getTypeDesc().equals("")) {
			if(!filter.getDocType().getTypeDesc().equals("")) {
				p.add(cb.like(root.get("docType").get("typeDesc"), "%" + filter.getDocType().getTypeDesc() + "%"));
			}
		}
		
		if(filter.getDocLanguage() != null && !filter.getDocLanguage().getLangDesc().equals("")) {
			if(!filter.getDocLanguage().getLangDesc().equals("")) {
				p.add(cb.like(root.get("docLanguage").get("langDesc"), "%" + filter.getDocLanguage().getLangDesc() + "%"));
			}
		}
		
		if(filter.getDocRepository() != null) {
        	if(!filter.getDocRepository().getRepoDesc().equals("") && !filter.getDocRepository().getRepoLOD().equals("")) {
        		p.add(cb.and(cb.like(root.get("docRepository").get("repoDesc"), "%" + filter.getDocRepository().getRepoDesc() + "%"),
        				cb.like(root.get("docRepository").get("repoLOD"), "%" + filter.getDocRepository().getRepoLOD() + "%"))
        				);
        	}
		}
		
		//I'm not sure how this is going to be done on the front end...
		//Must be only one edit
		if(filter.getEdits() != null && !filter.getEdits().isEmpty()) {
        	if(!filter.getEdits().get(0).getPlainText().equals("")) {
        		Join<Document, DocumentEdit> editJoin = root.join("edits");
				p.add(cb.and(cb.like(editJoin.get("plainText"), "%" + filter.getEdits().get(0).getPlainText() + "%"))
					);
        	}
		}
        
        if(p.isEmpty()) {
        	p.add(cb.notEqual(root.get("documentID"), -1));
        }

        return query.where(cb.and(p.toArray(new Predicate[0]))).distinct(true).orderBy(cb.desc(root.get("documentID"))).getRestriction();
	}
}
