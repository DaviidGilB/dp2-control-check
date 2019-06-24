package reckon;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import domain.Reckon;
import services.ReckonService;
import utilities.AbstractTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/junit.xml" })
@Transactional
public class ReckonServiceTest extends AbstractTest {
	
	@Autowired
	private ReckonService reckonService;

	@Test
	public void driverCreateReckon() {

		Object testingData[][] = {

				{
						// POSITIVE CASE
						"company1", super.getEntityId("audit1"), "body", true, null },

				{
						// NEGATIVE CASE
						"company2", super.getEntityId("audit1"), "body", true, IllegalArgumentException.class } };

		for (int i = 0; i < testingData.length; i++)
			this.createReckonTemplate((String) testingData[i][0], (Integer) testingData[i][1], (String) testingData[i][2], (Boolean) testingData[i][3], (Class<?>) testingData[i][4]);

	}

	private void createReckonTemplate(String company, Integer auditId, String body, Boolean isDraftMode, Class<?> expected) {

		Reckon reckon = this.reckonService.create();
		reckon.setBody(body);
		reckon.setIsDraftMode(isDraftMode);
		
		Class<?> caught = null;

		try {
			super.startTransaction();
			super.authenticate(company);
			
			Integer count1 = this.reckonService.findAll().size();

			this.reckonService.addReckon(reckon, auditId);
			super.flushTransaction();
			
			Integer count2 = this.reckonService.findAll().size();
			
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
