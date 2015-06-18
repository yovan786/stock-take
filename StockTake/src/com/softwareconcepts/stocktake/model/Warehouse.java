package com.softwareconcepts.stocktake.model;

public class Warehouse {
	private String name;

	public Warehouse() {
		super();
	}

	public Warehouse(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Warehouse [name=" + name + "]";
	}

}
