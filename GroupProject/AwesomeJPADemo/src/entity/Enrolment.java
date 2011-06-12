package entity;

import java.util.Date;

import awesome.persistence.annotations.Basic;
import awesome.persistence.annotations.ID;
import awesome.persistence.annotations.ManyToOne;

public class Enrolment {
	@ID
	private int id;
	
	public void setId(int id) {
		this.id = id;
	}
	
	
	public int getId() {
		return id;
	}
	
	
	@ManyToOne(target = Student.class)
	private Student student;
	
	
	public Student getStudent() {
		return student;
	}
	
	
	public void setStudent(Student student) {
		this.student = student;
	}
	
	
	
	@ManyToOne(target = Paper.class)
	Paper paper;
	
	
	public Paper getPaper() {
		return paper;
	}
	
	
	public void setPaper(Paper paper) {
		this.paper = paper;
	}
	
	@Basic
	private Date date;
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	
	public Date getDate() {
		return date;
	}
}
