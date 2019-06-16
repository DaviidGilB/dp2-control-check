
package services;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import utilities.AbstractTest;
import domain.Provider;
import domain.Sponsorship;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
public class ListSponsorshipsOfProviderServiceTest extends AbstractTest {

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
	 * 			SponsorhipService: 3.5%
	 * 			ProviderService: 2.9%
	 * 
	 */

	@Test
	public void driverListSponsorshipsOfProvider() {

		Object testingData[][] = {

			{
				//Positive test: list a sponsorship that you own
				"provider1", "sponsorship1", null
			}, {
				//Negative test: list a sponsorhip that you don't own
				"provider1", "sponsorship2", IllegalArgumentException.class
			}, {
				//Negative test: An admin is trying to list a sponshorship
				"admin1", "sponsorship1", IllegalArgumentException.class
			}
		};

		for (int i = 0; i < testingData.length; i++)
			this.templatListSponsorshipsOfProvider((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2]);

	}

	private void templatListSponsorshipsOfProvider(String username, String sponsorshipRe, Class<?> expected) {
		Class<?> caught = null;

		try {
			this.startTransaction();
			super.authenticate(username);

			this.providerService.loggedAsProvider();
			Sponsorship sponsorshipRep = this.sponsorshipService.findOne(super.getEntityId(sponsorshipRe));

			Provider provider = this.providerService.loggedProvider();
			List<Sponsorship> sponsorship = this.sponsorshipService.findProviderSponsorships(provider);

			Assert.isTrue(sponsorship.contains(sponsorshipRep));

		} catch (Throwable oops) {
			caught = oops.getClass();
		}

		super.checkExceptions(expected, caught);
		super.unauthenticate();

		this.rollbackTransaction();
	}
}
