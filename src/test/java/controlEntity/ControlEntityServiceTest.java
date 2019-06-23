package controlEntity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import domain.ControlEntity;
import services.ControlEntityService;
import utilities.AbstractTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/junit.xml" })
@Transactional
public class ControlEntityServiceTest extends AbstractTest {
	
	@Autowired
	private ControlEntityService controlEntityService;

	@Test
	public void driverCreateControlEntity() {

		Object testingData[][] = {

				{
						// POSITIVE CASE
						"rookie1", super.getEntityId("application1"), "body", true, null },

				{
						// NEGATIVE CASE
						"rookie2", super.getEntityId("application1"), "body", true, IllegalArgumentException.class } };

		for (int i = 0; i < testingData.length; i++)
			this.createControlEntityTemplate((String) testingData[i][0], (Integer) testingData[i][1], (String) testingData[i][2], (Boolean) testingData[i][3], (Class<?>) testingData[i][4]);

	}

	private void createControlEntityTemplate(String rookie, Integer applicationId, String body, Boolean isDraftMode, Class<?> expected) {

		ControlEntity controlEntity = this.controlEntityService.create();
		controlEntity.setBody(body);
		controlEntity.setIsDraftMode(isDraftMode);
		
		Class<?> caught = null;

		try {
			super.startTransaction();
			super.authenticate(rookie);
			
			Integer count1 = this.controlEntityService.findAll().size();

			this.controlEntityService.addControlEntity(controlEntity, applicationId);
			super.flushTransaction();
			
			Integer count2 = this.controlEntityService.findAll().size();
			
			Assert.isTrue(count1 == count2 - 1);
			
			super.unauthenticate();
		} catch (Throwable oops) {
			caught = oops.getClass();
		} finally {
			this.rollbackTransaction();
		}

		super.checkExceptions(expected, caught);
	}

}