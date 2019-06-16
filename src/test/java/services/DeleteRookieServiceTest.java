
package services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import utilities.AbstractTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@Transactional
public class DeleteRookieServiceTest extends AbstractTest {

	@Autowired
	private RookieService	rookieService;


	/*
	 * Positive test + asserts = 2
	 * Total test = 2
	 * Total coverage of the problem = 100%
	 */
	
	/**
	 * Sentence Coverage:
	 * 		RookieService: 4.9%
	 * 		ApplicationService: 14.4%
	 * 		
	 * 
	 */

	@Test
	public void driverDeleteRookie() {

		Object testingData[][] = {

			{
				//Positive case, a rookie delete his user account
				"rookie1", null
			},

			{
				//Negative case, a company delete a rookie account
				"company1", IllegalArgumentException.class
			}
		};

		for (int i = 0; i < testingData.length; i++)
			this.DeleteRookieTemplate((String) testingData[i][0], (Class<?>) testingData[i][1]);

	}

	private void DeleteRookieTemplate(String rookie, Class<?> expected) {

		Class<?> caught = null;

		try {
			super.startTransaction();
			super.authenticate(rookie);

			this.rookieService.deleteRookie();

			super.unauthenticate();
		} catch (Throwable oops) {
			caught = oops.getClass();
		} finally {
			this.rollbackTransaction();
		}

		super.checkExceptions(expected, caught);

	}

}
