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
public class DeleteAuditorServiceTest extends AbstractTest {

	@Autowired
	private AuditorService auditorService;

	/*
	 * Positive test + asserts = 2 Total test = 2 Total coverage of the problem =
	 * 100%
	 * data coverage: 100%
	 */
	/**
	 * Sentence Coverage:
	 * 		AuditService: 12.4%
	 * 		AuditorService: 2.9%
	 * 
	 */

	@Test
	public void driverDeleteAuditor() {

		Object testingData[][] = {

				{
						// Positive case, a rookie delete his user account
						"auditor1", null },

				{
						// Negative case, a company delete a rookie account
						"company1", NullPointerException.class } };

		for (int i = 0; i < testingData.length; i++)
			this.DeleteAuditorTemplate((String) testingData[i][0], (Class<?>) testingData[i][1]);

	}

	private void DeleteAuditorTemplate(String auditor, Class<?> expected) {

		Class<?> caught = null;

		try {
			super.startTransaction();
			super.authenticate(auditor);

			this.auditorService.deleteAuditor();

			super.unauthenticate();
		} catch (Throwable oops) {
			caught = oops.getClass();
		} finally {
			this.rollbackTransaction();
		}

		super.checkExceptions(expected, caught);

	}

}
