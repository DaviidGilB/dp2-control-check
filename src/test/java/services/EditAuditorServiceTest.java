package services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import utilities.AbstractTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/junit.xml" })
@Transactional
public class EditAuditorServiceTest extends AbstractTest {

	@Autowired
	private AuditorService auditorService;

	/*
	 * Positive test + asserts = 2 Total test = 2 Total coverage of the problem =
	 * 100% 
	 * Data coverage: 94,1%
	 */
	
	/**
	 * Sentence Coverage:
	 * 		AuditorService: 6.2%
	 * 				
	 * 
	 */

	@Test
	public void driverUpdateAuditor() {

		Object testingData[][] = {

				{
						// Positive case, a provider delete his user account
						"auditor1", null },

				{
						// Negative case, a company delete a provider account
						"rookie1", IllegalArgumentException.class } };

		for (int i = 0; i < testingData.length; i++)
			this.updateAuditorTemplate((String) testingData[i][0], (Class<?>) testingData[i][1]);

	}

	private void updateAuditorTemplate(String provider, Class<?> expected) {

		Class<?> caught = null;

		try {
			super.startTransaction();
			super.authenticate(provider);

			this.auditorService.updateAuditor(this.auditorService.findOne(super.getEntityId("auditor1")));

			super.unauthenticate();
		} catch (Throwable oops) {
			caught = oops.getClass();
		} finally {
			this.rollbackTransaction();
		}

		super.checkExceptions(expected, caught);

	}

}
