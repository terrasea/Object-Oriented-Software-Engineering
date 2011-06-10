package entity;

import java.util.ArrayList;
import java.util.List;

import awesome.persistence.annotations.OneToMany;

public class Test {
	@OneToMany(mappedBy = Coffee.class)
	private List<Coffee> coffees = new ArrayList<Coffee>();
	
	
	public List<Coffee> getCoffees() {
		return this.coffees;
	}
	
	
	public void setCoffees(List<Coffee> coffees) {
		this.coffees = coffees;
	}
	
}
