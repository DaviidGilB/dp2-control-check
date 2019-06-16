
package services;

import java.util.List;

import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.BindingResult;

import utilities.AbstractTest;
import domain.CreditCard;
import domain.Position;
import domain.Provider;
import domain.Sponsorship;
import forms.FormObjectSponsorshipCreditCard;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
public class CreateSponsorshipsOfProviderServiceTest extends AbstractTest {

	@Autowired
	private SponsorshipService		sponsorshipService;

	@Autowired
	private ProviderService			providerService;

	@Autowired
	private PositionService			positionService;

	@Autowired
	private CreditCardService		creditCardService;

	@Autowired
	private ConfigurationService	configurationService;


	/**
	 * Coverage:
	 * Positive test (1) + Asserts (4) + constraints (7) = 12
	 * Number of test = 16
	 * 
	 * Coverage = 16/12 = 100%
	 * */
	
	/**
	 * Sentence Coverage:
	 * 		SponsorhipService: 27.2%
	 * 		CreditCardService: 87.8%
	 * 
	 */

	@Test
	public void driverCreateSponsorshipsOfProvider() {

		Object testingData[][] = {

			{
				//Positive test: Creating a sponsorship with everything correct
				"provider1", "https://url.com/", "https://url.com/", "position2", "holderName", "VISA", 4228311658179741L, 12, 20, 123, null
			}, {
				//Negative test: Blank Banner
				"provider1", "", "https://url.com/", "position2", "holderName", "VISA", 4228311658179741L, 12, 20, 123, ConstraintViolationException.class
			}, {
				//Negative test: Blank targer URL
				"provider1", "https://url.com/", "", "position2", "holderName", "VISA", 4228311658179741L, 12, 20, 123, ConstraintViolationException.class
			}, {
				//Negative test: Not url banner
				"provider1", "notValid", "https://url.com/", "position2", "holderName", "VISA", 4228311658179741L, 12, 20, 123, ConstraintViolationException.class
			}, {
				//Negative test: Not url Target URL
				"provider1", "https://url.com/", "notValid", "position2", "holderName", "VISA", 4228311658179741L, 12, 20, 123, ConstraintViolationException.class
			}, {
				//Negative test: No position
				"provider1", "https://url.com/", "https://url.com/", "", "holderName", "VISA", 4228311658179741L, 12, 20, 123, AssertionError.class
			}, {
				//Negative test: Blank holder name
				"provider1", "https://url.com/", "https://url.com/", "position2", "", "VISA", 4228311658179741L, 12, 20, 123, ConstraintViolationException.class
			}, {
				//Negative test: Not valid type card
				"provider1", "https://url.com/", "https://url.com/", "position2", "holderName", "SA", 4228311658179741L, 12, 20, 123, IllegalArgumentException.class
			}, {
				//Negative test: Blank card type
				"provider1", "https://url.com/", "https://url.com/", "position2", "holderName", "", 4228311658179741L, 12, 20, 123, IllegalArgumentException.class
			}, {
				//Negative test: No valid credit card number
				"provider1", "https://url.com/", "https://url.com/", "position2", "holderName", "VISA", 42283116179741L, 12, 20, 123, IllegalArgumentException.class
			}, {
				//Negative test: No valid credit card number
				"provider1", "https://url.com/", "https://url.com/", "position2", "holderName", "VISA", 1128311658179741L, 12, 20, 123, IllegalArgumentException.class
			}, {
				//Negative test: No valid month
				"provider1", "https://url.com/", "https://url.com/", "position2", "holderName", "VISA", 4228311658179741L, 99, 20, 123, ConstraintViolationException.class
			}, {
				//Negative test: No valid year, is past
				"provider1", "https://url.com/", "https://url.com/", "position2", "holderName", "VISA", 4228311658179741L, 12, 12, 123, IllegalArgumentException.class
			}, {
				//Negative test: Invalid CVV
				"provider1", "https://url.com/", "https://url.com/", "position2", "holderName", "VISA", 4228311658179741L, 12, 20, 1234, ConstraintViolationException.class
			}, {
				//Negative test: Invalid CVV
				"admin1", "https://url.com/", "https://url.com/", "position2", "holderName", "VISA", 4228311658179741L, 12, 20, 1234, IllegalArgumentException.class
			}, {
				//Negative test: Invalid CVV
				"", "https://url.com/", "https://url.com/", "position2", "holderName", "VISA", 4228311658179741L, 12, 20, 1234, IllegalArgumentException.class
			}
		};

		for (int i = 0; i < testingData.length; i++)
			this.templateCreateSponsorshipsOfProvider((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (Long) testingData[i][6],
				(Integer) testingData[i][7], (Integer) testingData[i][8], (Integer) testingData[i][9], (Class<?>) testingData[i][10]);

	}
	private void templateCreateSponsorshipsOfProvider(String username, String banner, String targetURL, String position, String holderName, String type, Long number, Integer month, Integer year, Integer cvv, Class<?> expected) {
		Class<?> caught = null;

		try {
			this.startTransaction();
			super.authenticate(username);

			this.providerService.loggedAsProvider();
			Position positionRe = this.positionService.findOne(super.getEntityId(position));
			List<String> cardType = this.configurationService.getConfiguration().getCardType();

			Provider provider = this.providerService.loggedProvider();
			CreditCard creditCard = new CreditCard();

			FormObjectSponsorshipCreditCard formObj = new FormObjectSponsorshipCreditCard();
			Sponsorship sponsorship = new Sponsorship();
			BindingResult binding = null;

			formObj.setBanner(banner);
			formObj.setBrandName(type);
			formObj.setCvvCode(cvv);
			formObj.setExpirationMonth(month);
			formObj.setExpirationYear(year);
			formObj.setHolderName(holderName);
			formObj.setNumber(number);
			formObj.setPosition(positionRe);
			formObj.setTargetURL(targetURL);

			creditCard = this.creditCardService.reconstruct(formObj, binding);
			sponsorship = this.sponsorshipService.reconstruct(formObj, binding, creditCard, positionRe);

			this.creditCardService.validateDateCreditCard(creditCard);
			this.creditCardService.validateNumberCreditCard(creditCard);
			cardType.contains(sponsorship.getCreditCard().getBrandName());

			this.sponsorshipService.addOrUpdateSponsorship(sponsorship);

		} catch (Throwable oops) {
			caught = oops.getClass();
		}

		super.checkExceptions(expected, caught);
		super.unauthenticate();

		this.rollbackTransaction();
	}
}
