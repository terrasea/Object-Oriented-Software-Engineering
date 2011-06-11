package entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import awesome.persistence.annotations.Basic;
import awesome.persistence.annotations.ID;
import awesome.persistence.annotations.OneToMany;

public class Student {
	private static int enrol_id = 0;
	
	@ID
	private String id;
	
	
	public void setId(String id) {
		this.id = id;
	}
	
	
	public String getId() {
		return id;
	}
	
	
	@Basic
	private boolean active;
	
	public boolean getActive() {
		return active;
	}
	
	
	public void setActive(boolean active) {
		this.active = active;
	}
	
	@OneToMany(mappedBy = Enrolment.class)
	List<Enrolment> enrolments = new ArrayList<Enrolment>();
	
	
	public List<Enrolment> getEnrolments() {
		return enrolments;
	}
	
	
	public void enrol(Paper paper) {
		Enrolment enrolinst = new Enrolment();
		enrolinst.setId(enrol_id++);
		enrolinst.setPaper(paper);
		enrolinst.setStudent(this);
		Date d = new Date();
		enrolinst.setDate(d);
		paper.addEnrolment(enrolinst);
		enrolments.add(enrolinst);
	}
	
}
