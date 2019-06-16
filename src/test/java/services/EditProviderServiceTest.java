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
public class EditProviderServiceTest extends AbstractTest {

	@Autowired
	private ProviderService providerService;

	/*
	 * Positive test + asserts = 2 Total test = 2 Total coverage of the problem =
	 * 100% 
	 * Data coverage: 94,1%
	 */
	
	/**
	 * Sentence Coverage:
	 * 		ProviderService: 6.2%
	 * 
	 */

	@Test
	public void driverUpdateProvider() {

		Object testingData[][] = {

				{
						// Positive case, a provider delete his user account
						"provider1", null },

				{
						// Negative case, a company delete a provider account
						"rookie1", IllegalArgumentException.class } };

		for (int i = 0; i < testingData.length; i++)
			this.updateProviderTemplate((String) testingData[i][0], (Class<?>) testingData[i][1]);

	}

	private void updateProviderTemplate(String provider, Class<?> expected) {

		Class<?> caught = null;

		try {
			super.startTransaction();
			super.authenticate(provider);

			this.providerService.updateProvider(this.providerService.findOne(super.getEntityId("provider1")));

			super.unauthenticate();
		} catch (Throwable oops) {
			caught = oops.getClass();
		} finally {
			this.rollbackTransaction();
		}

		super.checkExceptions(expected, caught);

	}

}
