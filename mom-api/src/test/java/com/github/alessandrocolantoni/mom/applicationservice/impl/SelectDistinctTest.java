package com.github.alessandrocolantoni.mom.applicationservice.impl;

import static org.junit.Assert.assertEquals;

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
public class SelectDistinctTest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -688936168778890417L;

	
@Inject private ListQueryService listQueryService;
	
	
	@Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
            .addClasses(ListQueryService.class, ListQueryServiceImpl.class)
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }
	
	@Test
	public void selectDistinctOneField(){
		try {
			List<CarDTO> carDTOs = DTOBuilder.buildBasicCarList();
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
			List<CarDTO> carDTOs = DTOBuilder.buildBasicCarList();
			List<CarDTO> distinctCarDTOs = listQueryService.selectDistinct(carDTOs,new String[]{"brand", "model"} ) ;
			assertEquals("failure on carDTOs size", 2, distinctCarDTOs.size());
			assertEquals("failure on 1st element carBrands", "volkswagen", distinctCarDTOs.get(0).getBrand());
			assertEquals("failure on 2nd element carBrands", "audi", distinctCarDTOs.get(1).getBrand());
		} catch (Exception e) {
			Assert.fail(e.toString());
		}
	}
}
