package com.github.alessandrocolantoni.mom.common;

import static org.junit.Assert.assertEquals;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.alessandrocolantoni.mom.dto.CarDTO;
import com.github.alessandrocolantoni.mom.dto.GroupDTO;


@RunWith(Arquillian.class)
public class FieldComparatorTest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8134798559473050952L;

	@Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
            .addClass(FieldComparator.class);
    }
	
	@Test
	public void compareWithEmptyFieldArray(){
		FieldComparator<CarDTO> fieldComparator= new FieldComparator<CarDTO>(new String[]{});
		
		int  compare =  fieldComparator.compare(new CarDTO(), new CarDTO());
		assertEquals("failure on compare", 0, compare);
	}
	
	@Test
	public void compareWithEmptyFieldList(){
		try {
			FieldComparator<CarDTO> fieldComparator= new FieldComparator<CarDTO>(new ArrayList<String>());
			
			int  compare =  fieldComparator.compare(new CarDTO(), new CarDTO());
			assertEquals("failure on compare", 0, compare);
		} catch (Exception e) {
			Assert.fail(e.toString());
		}
	}
	
	@Test
	public void compareWithNullFieldArray(){
		
		FieldComparator<CarDTO> fieldComparator= new FieldComparator<CarDTO>((String[])null);
		
		int  compare =  fieldComparator.compare(new CarDTO(), new CarDTO());
		assertEquals("failure on compare", 0, compare);
	}
	
	@Test
	public void compareWithNullFieldList(){
		
		try {
			FieldComparator<CarDTO> fieldComparator= new FieldComparator<CarDTO>((List<String>)null);
			
			int  compare =  fieldComparator.compare(new CarDTO(), new CarDTO());
			assertEquals("failure on compare", 0, compare);
		} catch (Exception e) {
			Assert.fail(e.toString());
		}
	}
	
	@Test
	public void compareWithTwoFieldArray(){
		CarDTO carDTO1 =  new CarDTO();
		carDTO1.setModel("golf");
		carDTO1.setYear(1970);
		
		CarDTO carDTO2 =  new CarDTO();
		carDTO2.setModel("golf");
		carDTO2.setYear(1980);
		
		FieldComparator<CarDTO> fieldComparator= new FieldComparator<CarDTO>(new String[]{"model","year"});
		
		int compare1 = fieldComparator.compare(carDTO1, carDTO2);
		Assert.assertTrue("failure on compare", compare1<0);
		
		int compare2 = fieldComparator.compare(carDTO2, carDTO1);
		Assert.assertTrue("failure on compare", compare2>0);
		
		int compare0 = fieldComparator.compare(carDTO1, carDTO1);
		assertEquals("failure on compare", 0, compare0);
	}
	
	@Test
	public void compareWithOneFieldNullValue(){
		CarDTO carDTO1 =  new CarDTO();
		carDTO1.setModel("golf");
		
		CarDTO carDTO2 =  new CarDTO();
		FieldComparator<CarDTO> fieldComparator= new FieldComparator<CarDTO>("model");
		
		int compare1 = fieldComparator.compare(carDTO1, carDTO2);
		Assert.assertTrue("failure on compare", compare1<0);
		
		int compare2 = fieldComparator.compare(carDTO2, carDTO1);
		Assert.assertTrue("failure on compare", compare2>0);
		
		int compare0 = fieldComparator.compare(carDTO2, carDTO2);
		assertEquals("failure on compare", 0, compare0);

	}
	
	@Test
	public void compareWithNestedField(){
		CarDTO golf =  new CarDTO();
		golf.setModel("golf");
		GroupDTO vaesa = new GroupDTO();
		vaesa.setName("vaesa");
		golf.setGroupDTO(vaesa);
		
		CarDTO alfetta =  new CarDTO();
		alfetta.setModel("alfetta");
		GroupDTO gruppoFiat = new GroupDTO();
		gruppoFiat.setName("grupo fiat");
		alfetta.setGroupDTO(gruppoFiat);
		FieldComparator<CarDTO> fieldComparator= new FieldComparator<CarDTO>("groupDTO.name");
		
		int compare1 = fieldComparator.compare(golf, alfetta);
		Assert.assertTrue("failure on compare", compare1>0);
		
		int compare2 = fieldComparator.compare(alfetta,golf);
		Assert.assertTrue("failure on compare", compare2<0);
		
		int compare0 = fieldComparator.compare(golf, golf);
		assertEquals("failure on compare", 0, compare0);
	}
	
	@Test
	public void compareWithNestedNullException(){
		CarDTO golf =  new CarDTO();
		golf.setModel("golf");
		GroupDTO vaesa = new GroupDTO();
		vaesa.setName("vaesa");
		golf.setGroupDTO(vaesa);
		
		CarDTO alfetta =  new CarDTO();
		alfetta.setModel("alfetta");
		
		FieldComparator<CarDTO> fieldComparator= new FieldComparator<CarDTO>("groupDTO.name");
		
		int compare1 = fieldComparator.compare(golf, alfetta);
		Assert.assertTrue("failure on compare", compare1<0);
		
		int compare2 = fieldComparator.compare(alfetta, golf);
		Assert.assertTrue("failure on compare", compare2>0);
		
		int compare0 = fieldComparator.compare(alfetta, alfetta);
		assertEquals("failure on compare", 0, compare0);
	}
	
	
	@Test
	public void compareWithBadField(){
		try {
			CarDTO carDTO1 =  new CarDTO();
			CarDTO carDTO2 =  new CarDTO();
			FieldComparator<CarDTO> fieldComparator= new FieldComparator<CarDTO>("badField");
			fieldComparator.compare(carDTO1, carDTO2);
		} catch (Exception e) {
			assertEquals("failure on Exception of compareWithBadField", "java.lang.RuntimeException: java.lang.NoSuchMethodException: Unknown property 'badField' on class 'class com.github.alessandrocolantoni.mom.dto.CarDTO'",e.toString());
		}
	}
	
	@Test
	public void compareWithFieldList(){
		try {
			CarDTO golf =  new CarDTO();
			golf.setModel("golf");
			golf.setGear("manual");
			
			List<String> fieldList =  new ArrayList<String>();
			fieldList.add("model");
			fieldList.add("gear");
			
			FieldComparator<CarDTO> fieldComparator= new FieldComparator<CarDTO>(fieldList);
			
			int compare0 = fieldComparator.compare(golf,golf);
			assertEquals("failure on compare", 0, compare0);
		} catch (Exception e) {
			Assert.fail(e.toString());
		}
	}
}
