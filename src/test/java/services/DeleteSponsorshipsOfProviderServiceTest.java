
package services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import utilities.AbstractTest;
import domain.Provider;
import domain.Sponsorship;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
public class DeleteSponsorshipsOfProviderServiceTest extends AbstractTest {

	@Autowired
	private SponsorshipService	sponsorshipService;

	@Autowired
	private ProviderService		providerService;


	/**
	 * Coverage:
	 * Positive test + Asserts (Not logged as the correct actor and acces to data that is not yours) = 3
	 * Number of test = 3
	 * 
	 * Coverage = 3/3 = 100%
	 * */
	
	/**
	 * Sentence Coverage:
	 * 		SponosrhipService: 12.5%
	 * 		ProviderService: 3.6%
	 * 
	 */

	@Test
	public void driverDeleteSponsorshipsOfProvider() {

		Object testingData[][] = {

			{
				//Positive test: delete a sponsorship that you own
				"provider1", "sponsorship1", null
			}, {
				//Negative test: delete a sponsorhip that you don't own
				"provider1", "sponsorship2", IllegalArgumentException.class
			}, {
				//Negative test: An admin is trying to delete a sponshorship
				"admin1", "sponsorship1", IllegalArgumentException.class
			}
		};

		for (int i = 0; i < testingData.length; i++)
			this.templateDeleteSponsorshipsOfProvider((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2]);

	}

	private void templateDeleteSponsorshipsOfProvider(String username, String sponsorshipRe, Class<?> expected) {
		Class<?> caught = null;

		try {
			this.startTransaction();
			super.authenticate(username);

			this.providerService.loggedAsProvider();
			Sponsorship sponsorshipRep = this.sponsorshipService.findOne(super.getEntityId(sponsorshipRe));

			Provider provider = this.providerService.loggedProvider();
			this.sponsorshipService.deleteSponsorship(sponsorshipRep.getId());

		} catch (Throwable oops) {
			caught = oops.getClass();
		}

		super.checkExceptions(expected, caught);
		super.unauthenticate();

		this.rollbackTransaction();
	}
}
