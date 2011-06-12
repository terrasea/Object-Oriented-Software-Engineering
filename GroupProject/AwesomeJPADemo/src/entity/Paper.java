package entity;

import java.util.ArrayList;
import java.util.List;

import awesome.persistence.annotations.ID;
import awesome.persistence.annotations.OneToMany;

public class Paper {
	@ID
	private String id;
	
	
	public void setId(String id) {
		this.id = id;
	}
	
	
	public String getId() {
		return id;
	}
	
	
	@OneToMany(mappedBy = Enrolment.class)
	private List<Enrolment> enrolments = new ArrayList<Enrolment>();
	
	
	public List<Enrolment> getEnrolments() {
		return enrolments;
	}
	
	
	public void addEnrolment(Enrolment enrolment) {
		enrolments.add(enrolment);
	}
	
}
