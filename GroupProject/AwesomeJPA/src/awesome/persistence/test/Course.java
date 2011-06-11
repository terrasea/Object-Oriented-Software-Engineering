package awesome.persistence.test;

import awesome.persistence.annotations.Basic;
import awesome.persistence.annotations.ID;

/**
 * 
 * Test object use in the TestManager class, used for one to many relationships.
 *
 */
public class Course {

	@ID
	private int courseId;
	
	@Basic
	private String courseName;
	
	public Course(){}
	
	public int getCouseId(){
		return this.courseId;
	}
	
	
	public void setCourseId(int courseId){
		this.courseId = courseId;
	}
	public String getCourseName(){
		return this.courseName;
	}
	
	public void setCourseName(String courseName){
		this.courseName = courseName;
	}
}
