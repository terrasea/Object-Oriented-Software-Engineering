package main;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import entity.Enrolment;
import entity.Paper;
import entity.Student;

import awesome.persistence.agent.AgentException;
import awesome.persistence.agent.LazyInitAgent;
import awesome.persistence.agent.Transformer;
import awesome.persistence.manager.AQLException;
import awesome.persistence.manager.EntityException;
import awesome.persistence.manager.Manager;
import awesome.persistence.manager.NotAEntity;
import awesome.persistence.manager.PropertiesException;

public class Main {

	
	public static void agentmain(String args, Instrumentation inst) {
		LazyInitAgent agent = new LazyInitAgent();
		agent.addEntity(entity.Enrolment.class.getName());
		agent.addEntity(entity.Paper.class.getName());
		agent.addEntity(entity.Student.class.getName());
		inst.addTransformer(agent);
		//Transformer.agentmain(args, inst);
	}
	
	private static String propertiesPath = "lib/awesome.properties";

	/**
	 * @param args
	 * @throws InterruptedException 
	 * @throws AgentException 
	 */
	public static void main(String[] args) throws InterruptedException {
		try {
			Manager.setUpManager(propertiesPath);
//			int count = 0;
//			while(!Transformer.agentRunning() || count < 10) {
//				System.out.println("Agent is not running: " + count);
//				count++;
//				Thread.sleep(2000);
//			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PropertiesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Paper p1 = new Paper();
		p1.setId("123.456");
		Paper p2 = new Paper();
		p2.setId("123.789");
		Paper p3 = new Paper();
		p3.setId("123.123");
		Paper p4 = new Paper();
		p4.setId("123.234");

		Student stud1 = new Student();
		stud1.setId("1234");
		stud1.setActive(true);
		stud1.enrol(p1);
		stud1.enrol(p2);

		Student stud2 = new Student();
		stud2.setId("4567");
		stud2.setActive(true);
		stud2.enrol(p2);
		stud2.enrol(p3);

		Student stud3 = new Student();
		stud3.setId("3456");
		stud3.setActive(true);
		stud3.enrol(p4);
		stud3.enrol(p3);

		try {
			System.out.println("OK");
			Manager.persist(p1);
			Manager.persist(p2);
			Manager.persist(p3);
			Manager.persist(p4);

			Manager.persist(stud1);
			Manager.persist(stud2);
			Manager.persist(stud3);
		} catch (NotAEntity e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			System.out.println("Papers:");
			List<Object> papers = Manager.queryDB("Fetch "
					+ Paper.class.getName());
			for (Object paper : papers) {
				String id = ((Paper) paper).getId();
				System.out.println("Paper: " + id);
				List<Enrolment> enrolments = ((Paper) paper).getEnrolments();
				System.out.println("Enrolments: " + enrolments.size());
				for (Enrolment enrol : enrolments) {
					System.out.println("Date: " + enrol.getDate()
							+ ", Student: " + enrol.getStudent().getId());
				}
			}

			System.out.println("Students:");
			List<Object> students = Manager.queryDB("Fetch "
					+ Student.class.getName());
			for (Object student : students) {
				String id = ((Student) student).getId();
				boolean active = ((Student) student).getActive();
				System.out.println("Student: " + id + ", Active: " + active);
				List<Enrolment> enrolments = ((Student) student)
						.getEnrolments();
				System.out.println("Enrolments: " + enrolments.size());
				for (Enrolment enrol : enrolments) {
					System.out.println("Date: " + enrol.getDate() + ", Paper: "
							+ enrol.getPaper().getId());
				}
			}

			System.out.println("Enrolments:");
			List<Object> enrolments = Manager.queryDB("Fetch "
					+ Enrolment.class.getName());
			if (enrolments != null) {
				System.out.println("Enrolments:");
				for (Object enrol : enrolments) {
					System.out.println("Enrolments:");
					int id = ((Enrolment) enrol).getId();
					System.out.println("f Enrolments:");
					Student stud = ((Enrolment) enrol).getStudent();
					if(stud == null) {
						System.out.println("Enrolment Student is null fo enrolment id of " + id);
					}
					String studid = stud.getId();
					System.out.println("g Enrolments:");
					String paperid = ((Enrolment) enrol).getPaper().getId();
					System.out.println("h Enrolments:");
					Date date = ((Enrolment) enrol).getDate();
					System.out.println("i Enrolments:");
					System.out.println("Enrolment id: " + id + ", Date: "
							+ date + ", Student: " + studid + ", Paper: "
							+ paperid);

				}
			}

		} catch (AQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
