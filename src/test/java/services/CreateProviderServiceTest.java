
package services;

import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import domain.CreditCard;
import domain.Provider;
import utilities.AbstractTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/junit.xml" })
@Transactional
public class CreateProviderServiceTest extends AbstractTest {

	@Autowired
	private ActorService actorService;

	@Autowired
	private ProviderService providerService;

	@Autowired
	private CreditCardService creditCardService;

	/**
	 *
	 * R9. An actor who is not authenticated must be able to:
	 *
	 * 3. Register to the system as a provider.
	 *
	 * Ratio of data coverage: 100%. 14 attributes with restrictions + Positive
	 * case.
	 **/
	
	/**
	 * Sentence Coverage:	
	 * 			ProviderService: 12.7%
	 * 			CreditCardService: 70%
	 * 
	 */

	@Test
	public void driverCreateProvider() {
		Object testingData[][] = {

				{
						// Positive case: All data is correct
						"name", "surname", "ATU00000024", "holderName", 4164810248953065L, 12, 25, 111, "VISA",
						"https://www.photo.com/", "email@gmail.com", "666555444", "address", "username", "password",
						"make", null },
				{
						// Negative test, Blank name
						"", "surname", "ATU00000024", "holderName", 4164810248953065L, 12, 25, 111, "VISA",
						"https://www.photo.com/", "email@gmail.com", "666555444", "address", "username", "password",
						"make", ConstraintViolationException.class },
				{
						// Negative test, Blank surname
						"name", "", "ATU00000024", "holderName", 4164810248953065L, 12, 25, 111, "VISA",
						"https://www.photo.com/", "email@gmail.com", "666555444", "address", "username", "password",
						"make", ConstraintViolationException.class },
				{
						// Negative test, invalid VAT number
						"name", "surname", "POEAU00000024", "holderName", 4164810248953065L, 12, 25, 111, "VISA",
						"https://www.photo.com/", "email@gmail.com", "666555444", "address", "username", "password",
						"make", ConstraintViolationException.class },
				{
						// Negative test, Blank Holder Name
						"name", "surname", "ATU00000024", "", 4164810248953065L, 12, 25, 111, "VISA",
						"https://www.photo.com/", "email@gmail.com", "666555444", "address", "username", "password",
						"make", ConstraintViolationException.class },
				{
						// Negative test, invalid credit card number
						"name", "surname", "ATU00000024", "holderName", 4164810248953000L, 12, 25, 111, "VISA",
						"https://www.photo.com/", "email@gmail.com", "666555444", "address", "username", "password",
						"make", IllegalArgumentException.class },
				{
						// Negative test, invalid Month
						"name", "surname", "ATU00000024", "holderName", 4164810248953065L, 99, 25, 111, "VISA",
						"https://www.photo.com/", "email@gmail.com", "666555444", "address", "username", "password",
						"make", ConstraintViolationException.class },
				{
						// Negative test, invalid Year
						"name", "surname", "ATU00000024", "holderName", 4164810248953065L, 12, 10, 111, "VISA",
						"https://www.photo.com/", "email@gmail.com", "666555444", "address", "username", "password",
						"make", IllegalArgumentException.class },
				{
						// Negative test, invalid CVV
						"name", "surname", "ATU00000024", "holderName", 4164810248953065L, 12, 20, 0, "VISA",
						"https://www.photo.com/", "email@gmail.com", "666555444", "address", "username", "password",
						"make", ConstraintViolationException.class },
				{
						// Negative test, Blank card type
						"name", "surname", "ATU00000024", "holderName", 4164810248953065L, 12, 20, 111, "",
						"https://www.photo.com/", "email@gmail.com", "666555444", "address", "username", "password",
						"make", ConstraintViolationException.class },
				{
						// Negative test, invalid photo format
						"name", "surname", "ATU00000024", "holderName", 4164810248953065L, 12, 20, 111, "VISA",
						"invalidPhoto", "email@gmail.com", "666555444", "address", "username", "password", "make",
						ConstraintViolationException.class },
				{
						// Negative test, Blank email
						"name", "surname", "ATU00000024", "holderName", 4164810248953065L, 12, 20, 111, "VISA",
						"https://www.photo.com/", "", "666555444", "address", "username", "password", "make",
						ConstraintViolationException.class },
				{
						// Negative test, Blank username
						"name", "surname", "ATU00000024", "holderName", 4164810248953065L, 12, 20, 111, "VISA",
						"https://www.photo.com/", "email@gmail.com", "666555444", "address", "", "password", "make",
						ConstraintViolationException.class },
				{
						// Negative test, Blank password
						"name", "surname", "ATU00000024", "holderName", 4164810248953065L, 12, 20, 111, "VISA",
						"https://www.photo.com/", "email@gmail.com", "666555444", "address", "username", "", "make",
						ConstraintViolationException.class },
				{
						// Negative case, Blank make
						"name", "surname", "ATU00000024", "holderName", 4164810248953065L, 12, 25, 111, "VISA",
						"https://www.photo.com/", "email@gmail.com", "666555444", "address", "username", "password", "",
						ConstraintViolationException.class } };

		for (int i = 0; i < testingData.length; i++)
			this.templateCreateProvider((String) testingData[i][0], (String) testingData[i][1],
					(String) testingData[i][2], (String) testingData[i][3], (Long) testingData[i][4],
					(Integer) testingData[i][5], (Integer) testingData[i][6], (Integer) testingData[i][7],
					(String) testingData[i][8], (String) testingData[i][9], (String) testingData[i][10],
					(String) testingData[i][11], (String) testingData[i][12], (String) testingData[i][13],
					(String) testingData[i][14], (String) testingData[i][15], (Class<?>) testingData[i][16]);

	}

	private void templateCreateProvider(String name, String surname, String VAT, String holderName, Long number,
			Integer month, Integer year, Integer CVV, String cardType, String photo, String email, String phone,
			String address, String username, String password, String make, Class<?> expected) {
		Class<?> caught = null;
		this.startTransaction();

		try {

			Provider provider = this.providerService.create();
			CreditCard creditCard = new CreditCard();

			creditCard.setBrandName(cardType);
			creditCard.setCvvCode(CVV);
			creditCard.setExpirationMonth(month);
			creditCard.setExpirationYear(year);
			creditCard.setHolderName(holderName);
			creditCard.setNumber(number);

			provider.setName(name);
			provider.setSurname(surname);
			provider.setVATNumber(VAT);
			provider.setCreditCard(creditCard);
			provider.setPhoto(photo);
			provider.setEmail(email);
			provider.setPhone(phone);
			provider.setAddress(address);
			provider.setMake(make);
			provider.getUserAccount().setUsername(username);
			provider.getUserAccount().setPassword(password);

			Assert.isTrue(this.creditCardService.validateDateCreditCard(creditCard));
			Assert.isTrue(this.creditCardService.validateDateCreditCard(creditCard));
			Assert.isTrue(this.creditCardService.validateNumberCreditCard(creditCard));

			this.providerService.save(provider);
			this.flushTransaction();

		} catch (Throwable oops) {
			caught = oops.getClass();
		}
		super.checkExceptions(expected, caught);

		this.rollbackTransaction();

	}

}
