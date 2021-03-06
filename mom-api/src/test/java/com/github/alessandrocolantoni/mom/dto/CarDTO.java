package com.github.alessandrocolantoni.mom.dto;

import java.io.Serializable;

public class CarDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3225179018863333735L;
	
	private String brand;
	private String model;
	private String engine;
	private String gear;
	
	private GroupDTO groupDTO;
	private Integer year;
	
	
	
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getEngine() {
		return engine;
	}
	public void setEngine(String engine) {
		this.engine = engine;
	}
	public String getGear() {
		return gear;
	}
	public void setGear(String gear) {
		this.gear = gear;
	}
	public GroupDTO getGroupDTO() {
		return groupDTO;
	}
	public void setGroupDTO(GroupDTO groupDTO) {
		this.groupDTO = groupDTO;
	}
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	
	
	
	
	
	
	

}
