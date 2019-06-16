package services;

import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import domain.Item;
import forms.FormObjectItem;
import utilities.AbstractTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
public class ItemServiceTest extends AbstractTest{
	
	@Autowired
	private	ItemService	itemService;
	
	/**
	 * We are going to test the Requirement 
	 * 
	 * 10. An actor who is authenticated as a provider must be able to:
	 * 
	 * 
	 *		1. Manage his or her catalogue of items, which includes listing, showing, creating, updating, and deleting them.
	 * 
	 * 
	 * COVERAGE:
	 * 
	 * 		-Data Coverage: 100%
	 * 
	 * 		-Sentence Coverage:
	 * 				ItemService: 64.8%

	 */
	
	@Test
	public void driverCreateItem() {
		
		
		Object testingData[][] = {
				
				{
					//Positive test: A provider is creating a standard item
					"provider1", "name1", "description1", "link1, link2", "picture1, picture2", null},
				{
					//Negative test: A provider is trying to create a an item with no name
					"provider1", "", "description1", "link1, link2", "picture1, picture2", ConstraintViolationException.class},
				{
					//Negative test: A provider is trying to create a an item with no description
					"provider1", "name1", "", "link1, link2", "picture1, picture2", ConstraintViolationException.class},
				{
					//Positive test: A provider is creating a an item with no links
					"provider1", "name1", "description1", "", "picture1, picture2", null},	
				{
					//Positive test: A provider is creating a an item with no links
					"provider1", "name1", "description1", "link1, link2", "", null}
					
				/**
				 * Data coverage: 100%
				 * 
				 */
					
		};
		
		for (int i = 0; i < testingData.length; i++)
			this.templatCreateItem((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3],  (String) testingData[i][4], (Class<?>) testingData[i][5]);
		
		
	}
	
	@Test
	public void driverEditItem() {
		
		
		Object testingData[][] = {
				
				{
					//Positive test: A provider is editing his or her item 
					"provider1", "item1", "name1" ,"description1", "link1, link2", "picture1, picture2", null},	
				{
					//Negative test: provider2 is trying to edit a provider1's item
					"provider2", "item1", "name1", "description1", "link1", "picture1, picture2", IllegalArgumentException.class},
				{
					//Negative test: provider1 is trying to edit an item with putting a void names
					"provider1", "item1", "", "description1", "link1", "picture1, picture2",  ConstraintViolationException.class},
				{
					//Negative test: provider1 is trying to edit an item with putting a void names
					"provider1", "item1", "name1", "", "link1", "picture1, picture2",  ConstraintViolationException.class},
				{
					//Positive test: provider1 is editing an item putting no links
					"provider1", "item1", "name1", "description1", "", "picture1, picture2",  null},
				{
					//Positive test: provider1 is editing an item putting no links
					"provider1", "item1", "name1", "description1", "link1", "",  null}
					
					/**
					 *  Data coverage: 100%
					 * 
					 */
					
		};
		
		for (int i = 1; i < testingData.length; i++)
			this.templatEditItem((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4],  (String) testingData[i][5], (Class<?>) testingData[i][6]);
		
	}
	

	@Test
	public void driverDeleteItem() {
		
		
		Object testingData[][] = {
				
				{
					//Positive test: A provider is deleting his or her item
					"provider1", "item1", null},	
				{
					//Negative test: provider2 is trying to delete a provider1's item
					"provider2", "item1", IllegalArgumentException.class},
			/**
			 *  Data coverage: 100%
			 * 	
			 */
		};
		
		for (int i = 1; i < testingData.length; i++)
			this.templatDeleteItem((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2]);
		
	}
	
	private void templatCreateItem(String username, String name, String description, String links, String pictures, Class<?> expected) {
		
		this.startTransaction();
		super.authenticate(username);
		
		Class<?> caught = null;
		
		FormObjectItem form = new FormObjectItem();
		form.setDescription(description);
		form.setName(name);
		form.setLinks(links);
		form.setPictures(pictures);
		form.setId(0);
		
		try {
			Item item = this.itemService.reconstructItem(form);
			this.itemService.addOrUpdateItem(item);
			this.flushTransaction();
		} catch (Throwable oops) {
			caught = oops.getClass();
		}
		
		
		
		super.checkExceptions(expected, caught);
		super.unauthenticate();

		this.rollbackTransaction();
	}
	
	private void templatEditItem(String username, String itemId,  String name, String description, String links, String pictures, Class<?> expected) {
		
		this.startTransaction();
		super.authenticate(username);
		
		Class<?> caught = null;
		
		
		try {
			
			FormObjectItem form = this.itemService.prepareFormObject(this.getEntityId(itemId));
			form.setDescription(description);
			form.setLinks(links);
			form.setName(name);
			form.setPictures(pictures);
			
			Item item = this.itemService.reconstructItem(form);
			this.itemService.addOrUpdateItem(item);

			this.flushTransaction();
			
		} catch (Throwable oops) {
			caught = oops.getClass();
		}
		
		
		
		super.checkExceptions(expected, caught);
		super.unauthenticate();

		this.rollbackTransaction();
	}

	private void templatDeleteItem(String username, String itemName, Class<?> expected) {
			
			this.startTransaction();
			super.authenticate(username);
			
			Class<?> caught = null;
			
			try {
				this.itemService.deleteItem(this.getEntityId(itemName));
				this.flushTransaction();
			} catch (Throwable oops) {
				caught = oops.getClass();
			}
			
			
			
			super.checkExceptions(expected, caught);
			super.unauthenticate();
	
			this.rollbackTransaction();
		}
	}
