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
public class DeleteProviderServiceTest extends AbstractTest {

	@Autowired
	private ProviderService providerService;

	/*
	 * Positive test + asserts = 2 Total test = 2 Total coverage of the problem =
	 * 100%
	 * Data coverage: 100%
	 */
	
	/**
	 * Sentence Coverage:
	 * 		ProviderService: 4.5%
	 * 		SponsorshipService: 12.5%
	 * 
	 */

	@Test
	public void driverDeleteProvider() {

		Object testingData[][] = {

				{
						// Positive case, a provider delete his user account
						"provider1", null },

				{
						// Negative case, a company delete a provider account
						"rookie1", NullPointerException.class } };

		for (int i = 0; i < testingData.length; i++)
			this.DeleteProviderTemplate((String) testingData[i][0], (Class<?>) testingData[i][1]);

	}

	private void DeleteProviderTemplate(String provider, Class<?> expected) {

		Class<?> caught = null;

		try {
			super.startTransaction();
			super.authenticate(provider);

			this.providerService.deleteProvider();

			super.unauthenticate();
		} catch (Throwable oops) {
			caught = oops.getClass();
		} finally {
			this.rollbackTransaction();
		}

		super.checkExceptions(expected, caught);

	}

}
