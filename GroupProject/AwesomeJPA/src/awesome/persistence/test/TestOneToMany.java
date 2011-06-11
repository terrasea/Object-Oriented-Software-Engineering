package awesome.persistence.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.BeforeClass;

import awesome.persistence.annotations.ID;
import awesome.persistence.annotations.ManyToOne;
import awesome.persistence.annotations.OneToMany;


public class TestOneToMany {
	private static Properties properties = new Properties();
	
	@BeforeClass
	public static void setup() {
		properties.put("user", "awesome");
		properties.put("password", "awesome");
		properties.put("url","jdbc:sqlite:test.db");
		properties.put("entities", Test.class.getName()+";"+Test2.class.getName());
	}
	
	public class Test {
		@ID
		private int id;
		
		public void setId(int id) {
			this.id = id;
		}
		
		
		public int getId() {
			return id;
		}
		
		@OneToMany(mappedBy = Test2.class)
		List<Test2> test2 = new ArrayList<Test2>();
		
		List<Test2> getTest2() {
			return test2;
		}
	}
	
	
	public class Test2 {
		@ID
		private int id;
		
		public void setId(int id) {
			this.id = id;
		}
		
		
		public int getId() {
			return id;
		}
		
		
		
		@ManyToOne(target = Test.class)
		Test test;
		
		public void setTest(Test test) {
			this.test = test;
		}
		
		
		public Test getTest() {
			return test;
		}
		
	}
}
