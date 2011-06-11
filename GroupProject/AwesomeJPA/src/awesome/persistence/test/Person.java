package awesome.persistence.test;

import java.util.List;

import awesome.persistence.annotations.Basic;
import awesome.persistence.annotations.ID;

/**
 * 
 * Test object use in the TestManager class, used for one to many relationships.
 *
 */
public class Person {

	@ID
	private int personId;
	
	@Basic
	private String personName;
	
	@Basic
	private List<Course> courses; 
	
	public Person(){}
	
	public int getPersonId(){
		return this.personId;
	}
	
	public void setPersonId(int personId){
		this.personId = personId;
	}
	
	public String getPersonName(){
		return this.personName;
	}
	
	public void setPersonName(String personName){
		this.personName = personName;
		
	}
	
	public void setCourses(List<Course> courses){
		this.courses = courses;
	}
	
	public List<Course> getCourses(){
		return this.courses;
	}
}
