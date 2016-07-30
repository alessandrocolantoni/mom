package com.github.alessandrocolantoni.mom.applicationservice.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
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
import com.github.alessandrocolantoni.mom.dto.DTOBuilder;


@RunWith(Arquillian.class)
public class SelectFieldFromCollectionTest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4851789893706594909L;
	
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
			List<CarDTO> carDTOs = DTOBuilder.buildBasicCarList();
		
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
			List<CarDTO> carDTOs = DTOBuilder.buildBasicCarList();
			listQueryService.selectFieldFromCollection(carDTOs, "badField");
		} catch (Exception e) {
			assertEquals("failure on Exception of select badField", "java.lang.NoSuchMethodException: Unknown property 'badField' on class 'class com.github.alessandrocolantoni.mom.dto.CarDTO'",e.toString());
		}
	}

}
