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
public class BeanFieldComparatorTest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3073759906884320462L;

	@Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
            .addClass(BeanFieldComparator.class);
    }
	
	@Test
	public void compareWithEmptyFieldArray(){
		BeanFieldComparator<CarDTO> beanFieldComparator= new BeanFieldComparator<CarDTO>(new String[]{},CarDTO.class);
		compareEmptyAndNull(beanFieldComparator);
	}
	
	
	@Test
	public void compareWithNullFieldArray(){
		BeanFieldComparator<CarDTO> beanFieldComparator= new BeanFieldComparator<CarDTO>((String[])null,CarDTO.class);
		compareEmptyAndNull(beanFieldComparator);
	}
	
	
	@Test
	public void compareWithEmptyFieldList(){
		try {
			BeanFieldComparator<CarDTO> beanFieldComparator= new BeanFieldComparator<CarDTO>(new ArrayList<String>(),CarDTO.class);
			compareEmptyAndNull(beanFieldComparator);
		} catch (Exception e) {
			Assert.fail(e.toString());
		}
	}
	
	@Test
	public void compareWithNullFieldList(){
		try {
			BeanFieldComparator<CarDTO> beanFieldComparator= new BeanFieldComparator<CarDTO>((List<String>)null,CarDTO.class);
			compareEmptyAndNull(beanFieldComparator);
		} catch (Exception e) {
			Assert.fail(e.toString());
		}
		
	}
	
	private void compareEmptyAndNull(BeanFieldComparator<CarDTO> beanFieldComparator ){
		int  compare1 =  beanFieldComparator.compare("whatever", new CarDTO());
		assertEquals("failure on compare", 0, compare1);
		
		int  compare2 =  beanFieldComparator.compare(new CarDTO(),"whatever");
		assertEquals("failure on compare", 0, compare2);
	}
	
	@Test
	public void compareOneField(){
		CarDTO golf =  new CarDTO();
		golf.setModel("golf");
		
		BeanFieldComparator<CarDTO> beanFieldComparator = new BeanFieldComparator<CarDTO>("model",CarDTO.class);
		int compare01 = beanFieldComparator.compare(golf, "golf");
		assertEquals("failure on compare", 0, compare01);
		
		int compare02 = beanFieldComparator.compare("golf", golf);
		assertEquals("failure on compare", 0, compare02);
		
		int compare1 = beanFieldComparator.compare("tuareg", golf);
		Assert.assertTrue("failure on compare", compare1>0);
		
		int compare2 = beanFieldComparator.compare(golf,"tuareg");
		Assert.assertTrue("failure on compare", compare2<0);
	}
	
	@Test
	public void compareWithTwoFieldArray(){
		CarDTO carDTO1 =  new CarDTO();
		carDTO1.setModel("golf");
		carDTO1.setYear(1970);
		
		BeanFieldComparator<CarDTO> beanFieldComparator= new BeanFieldComparator<CarDTO>(new String[]{"model","year"},CarDTO.class);
		
		int compare1 = beanFieldComparator.compare(carDTO1, new Object[]{"golf",1980});
		Assert.assertTrue("failure on compare", compare1<0);
		
		int compare2 = beanFieldComparator.compare(new Object[]{"golf",1980}, carDTO1);
		Assert.assertTrue("failure on compare", compare2>0);
		
		int compare01 = beanFieldComparator.compare(carDTO1, new Object[]{"golf",1970});
		assertEquals("failure on compare", 0, compare01);
		
		int compare02 = beanFieldComparator.compare(new Object[]{"golf",1970},carDTO1 );
		assertEquals("failure on compare", 0, compare02);
	}
	
	@Test
	public void compareWithOneFieldNullValue(){
		CarDTO carDTO1 =  new CarDTO();
		carDTO1.setModel("golf");
		
		
		BeanFieldComparator<CarDTO> beanFieldComparator = new BeanFieldComparator<CarDTO>("model",CarDTO.class);
		
		int compare1 = beanFieldComparator.compare(carDTO1, null);
		Assert.assertTrue("failure on compare", compare1<0);
		
		int compare2 = beanFieldComparator.compare(null, carDTO1);
		Assert.assertTrue("failure on compare", compare2>0);
		
		int compare01 = beanFieldComparator.compare(new CarDTO(), null);
		assertEquals("failure on compare", 0, compare01);
		
		int compare02 = beanFieldComparator.compare(null,new CarDTO());
		assertEquals("failure on compare", 0, compare02);
	}
	
	@Test
	public void compareWithNestedField(){
		CarDTO golf =  new CarDTO();
		golf.setModel("golf");
		GroupDTO vaesa = new GroupDTO();
		vaesa.setName("vaesa");
		golf.setGroupDTO(vaesa);
		
		
		BeanFieldComparator<CarDTO> beanFieldComparator = new BeanFieldComparator<CarDTO>("groupDTO.name",CarDTO.class);
		
		int compare1 = beanFieldComparator.compare(golf, "grupo fiat");
		Assert.assertTrue("failure on compare", compare1>0);
		
		int compare2 = beanFieldComparator.compare("grupo fiat",golf);
		Assert.assertTrue("failure on compare", compare2<0);
		
		int compare01 = beanFieldComparator.compare(golf, "vaesa");
		assertEquals("failure on compare", 0, compare01);
		
		int compare02 = beanFieldComparator.compare("vaesa", golf);
		assertEquals("failure on compare", 0, compare02);
	}
	
	@Test
	public void compareWithNestedNullException(){
		
		
		CarDTO alfetta =  new CarDTO();
		alfetta.setModel("alfetta");
		
		BeanFieldComparator<CarDTO> beanFieldComparator = new BeanFieldComparator<CarDTO>("groupDTO.name",CarDTO.class);
		
		int compare1 = beanFieldComparator.compare("vaesa", alfetta);
		Assert.assertTrue("failure on compare", compare1<0);
		
		int compare2 = beanFieldComparator.compare(alfetta, "vaesa");
		Assert.assertTrue("failure on compare", compare2>0);
		
		int compare01 = beanFieldComparator.compare(alfetta, null);
		assertEquals("failure on compare", 0, compare01);
		
		int compare02 = beanFieldComparator.compare(null, alfetta);
		assertEquals("failure on compare", 0, compare02);
	}
	
	@Test
	public void compareWithBadField1(){
		try {
			BeanFieldComparator<CarDTO> beanFieldComparator = new BeanFieldComparator<CarDTO>("badField",CarDTO.class);
			beanFieldComparator.compare("badField", new CarDTO());
		} catch (Exception e) {
			assertEquals("failure on Exception of compareWithBadField", "java.lang.RuntimeException: java.lang.NoSuchMethodException: Unknown property 'badField' on class 'class com.github.alessandrocolantoni.mom.dto.CarDTO'",e.toString());
		}
	}
	
	@Test
	public void compareWithBadField2(){
		try {
			BeanFieldComparator<CarDTO> beanFieldComparator = new BeanFieldComparator<CarDTO>("badField",CarDTO.class);
			beanFieldComparator.compare(new CarDTO(),"badField");
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
			
			BeanFieldComparator<CarDTO> beanFieldComparator = new BeanFieldComparator<CarDTO>(fieldList,CarDTO.class);
			
			int compare01 = beanFieldComparator.compare(golf,new Object[]{"golf","manual"});
			assertEquals("failure on compare", 0, compare01);
			
			int compare02 = beanFieldComparator.compare(new Object[]{"golf","manual"},golf);
			assertEquals("failure on compare", 0, compare02);
		} catch (Exception e) {
			Assert.fail(e.toString());
		}
	}
}
