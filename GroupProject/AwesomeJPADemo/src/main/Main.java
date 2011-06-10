package main;

import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import awesome.persistence.agent.AgentException;
import awesome.persistence.agent.LazyInitAgent;
import awesome.persistence.agent.Transformer;
import awesome.persistence.manager.AQLException;
import awesome.persistence.manager.EntityException;
import awesome.persistence.manager.Manager;
import awesome.persistence.manager.NotAEntity;
import awesome.persistence.manager.PropertiesException;
import entity.Coffee;
import entity.Tea;
import entity.Test;

public class Main {

	private static String propertiesPath = "lib/awesome.properties";

	/**
	 * @param args
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		LazyInitAgent agent = new LazyInitAgent();
		agent.addEntity("Coffee");
		agent.addEntity("Tea");
		agent.addEntity("Test");
		Transformer.addTransformer(agent);
		try {
			Transformer.startAgent();
		} catch (AgentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Test t = new Test();
		Coffee coffee = new Coffee();
		coffee.setName("Long black");
		coffee.setStrength(5);
		coffee.setMilk(false);
		coffee.setTest(t);
		Coffee coffee2 = new Coffee();
		coffee2.setName("Short black");
		coffee2.setStrength(5);
		coffee2.setMilk(false);
		coffee2.setTest(t);
		//t.getCoffees().add(coffee);
//		System.out.println(t.getCoffees());
		

		try {
			Manager.setProperties(propertiesPath);
			

//			Tea tea = new Tea();
//			tea.setName("Earl gray");
//			tea.setStrength(4);
//			tea.setMilk(true);

			
			System.out.println("Persist");
			Manager.persist(coffee);
			Manager.persist(coffee2);
			Manager.persist(t);
			System.out.println("Persist done");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PropertiesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotAEntity e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
//		System.out.println(t.getCoffees());
		
		System.out.println("Coffee");
		List<Object> results = null;
		try {
			results = Manager.queryDB("FETCH " + Coffee.class.getName());
			for (Object obj : results) {
				Coffee res = (Coffee) obj;
				System.out.println(res.getName() + ", " + res.getStrength()
						+ ", " + res.getMilk() + ", " +  res.getTest());
				System.out.println("Test entities are: " + res.getTest().getId());
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

		System.out.println("Now for tea");

		try {
			results = Manager.queryDB("FETCH " + Tea.class.getName());
			for (int index = 0; index < results.size(); index++) {
				Tea res = (Tea) results.get(index);
				System.out.println(res.getName() + ", " + res.getStrength()
						+ ", " + res.getMilk());
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
		
		
		System.out.println("Now to show the entity Test");
		try {
			results = Manager.queryDB("FETCH " + Test.class.getName());
			for (Object obj : results) {
				Test res = (Test) obj;
				System.out.println(res.getId() + ", " + res.getCoffees());
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
