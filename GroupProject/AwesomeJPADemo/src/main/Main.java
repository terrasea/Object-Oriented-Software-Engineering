package main;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import entity.Coffee;
import entity.Tea;

import awesome.persistence.agent.AgentException;
import awesome.persistence.agent.LazyInitAgent;
import awesome.persistence.agent.Transformer;
import awesome.persistence.manager.AQLException;
import awesome.persistence.manager.EntityException;
import awesome.persistence.manager.Manager;
import awesome.persistence.manager.NotAEntity;
import awesome.persistence.manager.PropertiesException;
import awesome.persistence.test.Primatives;

public class Main {

	private static String propertiesPath = "lib/awesome.properties";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LazyInitAgent agent = new LazyInitAgent();
		agent.addEntity("Coffee");
		agent.addEntity("Tea");
		Transformer.addTransformer(agent);
		try {
			Transformer.startAgent();
		} catch (AgentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Manager.setProperties(propertiesPath);
			Coffee coffee = new Coffee();
			coffee.setName("Long black");
			coffee.setStrength(5);
			//coffee.setMilk(0);
			
			Tea tea = new Tea();
			tea.setName("Earl gray");
			tea.setStrength(4);
			tea.setMilk(1);
			
			
			Manager.persist(coffee);
			Manager.persist(tea);
			
			
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
		}
		
		
		List<Object> results = null;
		try {
			results = Manager.queryDB("FETCH " + Coffee.class.getName());
			for(int index = 0; index < results.size(); index++){
				Primatives res = (Primatives) results.get(index);
				System.out.println(res.getPString());
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
		
		
				
		try {
			results = Manager.queryDB("FETCH " + Tea.class.getName());
			for(int index = 0; index < results.size(); index++){
				Primatives res = (Primatives) results.get(index);
				System.out.println(res.getPString());
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
