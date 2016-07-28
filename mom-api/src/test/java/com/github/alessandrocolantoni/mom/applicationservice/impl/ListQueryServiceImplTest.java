package com.github.alessandrocolantoni.mom.applicationservice.impl;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;



import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.alessandrocolantoni.mom.applicationservice.ListQueryService;
import com.github.alessandrocolantoni.mom.dto.CarDTO;

@RunWith(Arquillian.class)
public class ListQueryServiceImplTest {

	@Inject private ListQueryService listQueryService;
	
	
	@Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
            .addClasses(ListQueryService.class, ListQueryServiceImpl.class)
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }
	
	@Test
	public void selectFieldFromCollection(){
		try {
			List<CarDTO> carDTOs = buildBasicCarList();
		
			List<String> carBrands = listQueryService.selectFieldFromCollection(carDTOs, "brand");
			assertEquals("failure on carDTOs size", 3, carBrands.size());
			assertEquals("failure on 1st element carBrands", "volkswagen", carBrands.get(0));
			assertEquals("failure on 2nd element carBrands", "volkswagen", carBrands.get(1));
			assertEquals("failure on 3rd element carBrands", "audi", carBrands.get(2));
		} catch (Exception e) {
			Assert.fail(e.toString());
		}
	}
	
	@Test
	public void selectFieldFromNullCollection(){
		try {
			List<String> emptyList =  listQueryService.selectFieldFromCollection(null, "brand");
			assertTrue("failure - should be true", emptyList.isEmpty());
		} catch (Exception e) {
			Assert.fail(e.toString());
		}
	}
	
	
	@Test
	public void selectBadFieldFromCollection(){
		try {
			List<CarDTO> carDTOs = buildBasicCarList();
			listQueryService.selectFieldFromCollection(carDTOs, "badField");
		} catch (Exception e) {
			assertEquals("failure on Exception of select badField", "java.lang.NoSuchMethodException: Unknown property 'badField' on class 'class com.github.alessandrocolantoni.mom.dto.CarDTO'",e.toString());
		}
	}
	
	@Test
	public void selectDistinctOneField(){
		try {
			List<CarDTO> carDTOs = buildBasicCarList();
			List<CarDTO> distinctCarDTOs = listQueryService.selectDistinct(carDTOs,"brand" ) ;
			assertEquals("failure on carDTOs size", 2, distinctCarDTOs.size());
			assertEquals("failure on 1st element carBrands", "volkswagen", distinctCarDTOs.get(0).getBrand());
			assertEquals("failure on 2nd element carBrands", "audi", distinctCarDTOs.get(1).getBrand());
		} catch (Exception e) {
			Assert.fail(e.toString());
		}
	}
	
	@Test
	public void selectDistinctArrayField(){
		try {
			List<CarDTO> carDTOs = buildBasicCarList();
			List<CarDTO> distinctCarDTOs = listQueryService.selectDistinct(carDTOs,new String[]{"brand", "model"} ) ;
			assertEquals("failure on carDTOs size", 2, distinctCarDTOs.size());
			assertEquals("failure on 1st element carBrands", "volkswagen", distinctCarDTOs.get(0).getBrand());
			assertEquals("failure on 2nd element carBrands", "audi", distinctCarDTOs.get(1).getBrand());
		} catch (Exception e) {
			Assert.fail(e.toString());
		}
	}
	
	private List<CarDTO> buildBasicCarList(){
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
