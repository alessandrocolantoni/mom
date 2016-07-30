package com.github.alessandrocolantoni.mom.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DTOBuilder implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8988117773233519041L;

	
	public static List<CarDTO> buildBasicCarList(){
		List<CarDTO> carDTOs = new ArrayList<CarDTO>();
		
		CarDTO car1 = new CarDTO();
		car1.setBrand("volkswagen");
		car1.setModel("golf");
		car1.setEngine("1400 TD");
		
		CarDTO car2 = new CarDTO();
		car2.setBrand("volkswagen");
		car2.setModel("golf");
		car2.setEngine("1600 TD");
		
		CarDTO car3 = new CarDTO();
		car3.setBrand("audi");
		
		carDTOs.add(car1);
		carDTOs.add(car2);
		carDTOs.add(car3);
		
		return carDTOs;
	}
}
