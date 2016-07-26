package com.github.alessandrocolantoni.mom.applicationservice.impl;

import static org.junit.Assert.assertEquals;

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
	public void selectFieldFromCollectionTest(){
		try {
			CarDTO car1 = new CarDTO();
			car1.setBrand("volkswagen");
			
			CarDTO car2 = new CarDTO();
			car2.setBrand("volkswagen");
			
			CarDTO car3 = new CarDTO();
			car3.setBrand("audi");
			
			List<CarDTO> carDTOs = new ArrayList<CarDTO>();
			
			carDTOs.add(car1);
			carDTOs.add(car2);
			carDTOs.add(car3);
		
			
		
			List<String> carBrands = listQueryService.selectFieldFromCollection(carDTOs, "brand");
			assertEquals("failure on carDTOs size", 3, carBrands.size());
		} catch (Exception e) {
			
			Assert.fail(e.toString());
			
		}
		
		
	}
	
	
	
	
}
