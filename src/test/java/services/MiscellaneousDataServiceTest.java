package services;

import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import domain.MiscellaneousData;
import utilities.AbstractTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/junit.xml" })
@Transactional
public class MiscellaneousDataServiceTest extends AbstractTest {

	@Autowired
	private CurriculumService curriculumService;
	@Autowired
	private RookieService rookieService;
	@Autowired
	private MiscellaneousDataService miscellaneousDataService;

	/**
	 * R17. An actor who is authenticated as a rookie must be able to:
	 *
	 * 1. Manage his or her curricula, which includes deleting them.
	 *
	 * Ratio of data coverage: 100% - Access as a rookie or not. - Delete a
	 * miscellaneous data that does belongs to the rookie logged in or not.
	 *
	 **/
	
	/**
	 * Sentence Coverage:
	 * 		MiscellaneousDataService: 79%
	 * 
	 */
	@Test
	public void driverDeleteMiscellaneousData() {

		Object testingData[][] = {

				/**
				 * POSITIVE TEST: Rookie is deleting one of his miscellaneous data
				 **/
				{ "rookie1", super.getEntityId("miscellaneousData1"), null },
				/**
				 * NEGATIVE TEST: Rookie is trying to delete an miscellaneous data from other
				 * rookie
				 **/
				{ "rookie2", super.getEntityId("miscellaneousData1"), IllegalArgumentException.class },
				/**
				 * NEGATIVE TEST: Another user is trying to delete an miscellaneous data
				 **/
				{ "company1", super.getEntityId("miscellaneousData1"), IllegalArgumentException.class } };

		for (int i = 0; i < testingData.length; i++)
			this.deleteMiscellaneousDataTemplate((String) testingData[i][0], (Integer) testingData[i][1],
					(Class<?>) testingData[i][2]);

	}

	private void deleteMiscellaneousDataTemplate(String rookie, int miscellaneousDataId, Class<?> expected) {

		Class<?> caught = null;

		try {
			super.startTransaction();
			super.authenticate(rookie);

			this.miscellaneousDataService.deleteMiscellaneousDataAsRookie(miscellaneousDataId);

			super.unauthenticate();
		} catch (Throwable oops) {
			caught = oops.getClass();
		} finally {
			this.rollbackTransaction();
		}

		super.checkExceptions(expected, caught);

	}

	/**
	 * R17. An actor who is authenticated as a rookie must be able to:
	 *
	 * 1. Manage his or her curricula, which includes creating them.
	 *
	 * Ratio of data coverage: 4/4 = 100% - Access as a rookie or not. - Create a
	 * miscellaneous data in a curriculum that does belongs to the rookie logged in
	 * or not - 1 attribute with domain restrictions.
	 *
	 **/
	@Test
	public void driverCreateMiscellaneousData() {

		Object testingData[][] = {

				/**
				 * POSITIVE TEST: Rookie is creating a miscellaneous data
				 **/
				{ "rookie1", super.getEntityId("curriculum1"), "FreeText", null },
				/**
				 * NEGATIVE TEST: Another user is trying to create a miscellaneous data
				 **/
				{ "company", super.getEntityId("curriculum1"), "FreeText", IllegalArgumentException.class },
				/**
				 * NEGATIVE TEST: Rookie is trying to create a miscellaneous data in a
				 * curriculum of other rookie
				 **/
				{ "rookie2", super.getEntityId("curriculum1"), "FreeText", IllegalArgumentException.class },
				/**
				 * NEGATIVE TEST: Rookie is creating a miscellaneous data with a degree in blank
				 **/
				{ "rookie1", super.getEntityId("curriculum1"), "", ConstraintViolationException.class }, };

		for (int i = 0; i < testingData.length; i++)
			this.createMiscellaneousDataTemplate((String) testingData[i][0], (Integer) testingData[i][1],
					(String) testingData[i][2], (Class<?>) testingData[i][3]);
	}

	private void createMiscellaneousDataTemplate(String rookie, Integer curriculumId, String freeText,
			Class<?> expected) {

		MiscellaneousData miscellaneousData = new MiscellaneousData();
		miscellaneousData.setFreeText(freeText);

		Class<?> caught = null;

		try {
			super.startTransaction();
			super.authenticate(rookie);

			this.miscellaneousDataService.addOrUpdateMiscellaneousDataAsRookie(miscellaneousData, curriculumId);

			super.unauthenticate();
		} catch (Throwable oops) {
			caught = oops.getClass();
		} finally {
			this.rollbackTransaction();
		}

		super.checkExceptions(expected, caught);

	}

	/**
	 * R17. An actor who is authenticated as a rookie must be able to:
	 *
	 * 1. Manage his or her curricula, which includes updating them.
	 *
	 * Ratio of data coverage: 4/4 = 100% - Access as a rookie or not. - Edit a
	 * miscellaneous data that does belongs to the rookie logged in or not. - 1
	 * attribute with domain restrictions.
	 *
	 **/
	@Test
	public void driverUpdateMiscellaneousData() {

		Object testingData[][] = {

				/**
				 * POSITIVE TEST: Rookie is updating a miscellaneous data
				 **/
				{ "rookie1", super.getEntityId("miscellaneousData1"), super.getEntityId("curriculum1"), "FreeText",
						null },
				/**
				 * NEGATIVE TEST: Another user is trying to update a miscellaneous data
				 **/
				{ "company", super.getEntityId("miscellaneousData1"), super.getEntityId("curriculum1"), "FreeText",
						IllegalArgumentException.class },
				/**
				 * NEGATIVE TEST: Rookie is trying to update a miscellaneous data of other
				 * rookie
				 **/
				{ "rookie2", super.getEntityId("miscellaneousData1"), super.getEntityId("curriculum3"), "FreeText",
						IllegalArgumentException.class },
				/**
				 * NEGATIVE TEST: Rookie is updating an miscellaneous data with the free text in
				 * blank
				 **/
				{ "rookie1", super.getEntityId("miscellaneousData1"), super.getEntityId("curriculum1"), "",
						ConstraintViolationException.class }, };

		for (int i = 0; i < testingData.length; i++)
			this.updateMiscellaneousDataTemplate((String) testingData[i][0], (Integer) testingData[i][1],
					(Integer) testingData[i][2], (String) testingData[i][3], (Class<?>) testingData[i][4]);
	}

	private void updateMiscellaneousDataTemplate(String rookie, Integer miscellaneousDataId, Integer curriculumId,
			String freeText, Class<?> expected) {

		MiscellaneousData miscellaneousData = this.miscellaneousDataService.findOne(miscellaneousDataId);
		miscellaneousData.setFreeText(freeText);

		Class<?> caught = null;

		try {
			super.startTransaction();
			super.authenticate(rookie);

			this.miscellaneousDataService.addOrUpdateMiscellaneousDataAsRookie(miscellaneousData, curriculumId);

			super.unauthenticate();
		} catch (Throwable oops) {
			caught = oops.getClass();
		} finally {
			this.rollbackTransaction();
		}

		super.checkExceptions(expected, caught);

	}

	/**
	 * R17. An actor who is authenticated as a rookie must be able to:
	 *
	 * 1. Manage his or her curricula, which includes showing them.
	 *
	 * Ratio of data coverage: 100% - Access as a rookie or not. - Show the
	 * attachments of a miscellaneous data that does belongs to the rookie logged in
	 * or not.
	 *
	 **/
	@Test
	public void driverListAttachmentsMiscellaneousData() {

		Object testingData[][] = {

				/**
				 * POSITIVE TEST: Rookie is listing the attachments one of his miscellaneous
				 * data
				 **/
				{ "rookie1", super.getEntityId("miscellaneousData1"), null },
				/**
				 * NEGATIVE TEST: Rookie is trying to list the attachments of a miscellaneous
				 * data from other rookie
				 **/
				{ "rookie2", super.getEntityId("miscellaneousData1"), IllegalArgumentException.class },
				/**
				 * NEGATIVE TEST: Another user is trying to list the attachments of a
				 * miscellaneous data
				 **/
				{ "company1", super.getEntityId("miscellaneousData1"), IllegalArgumentException.class } };

		for (int i = 0; i < testingData.length; i++)
			this.listAttachmentsMiscellaneousDataTemplate((String) testingData[i][0], (Integer) testingData[i][1],
					(Class<?>) testingData[i][2]);

	}

	private void listAttachmentsMiscellaneousDataTemplate(String rookie, int miscellaneousDataId, Class<?> expected) {

		Class<?> caught = null;

		try {
			super.startTransaction();
			super.authenticate(rookie);

			this.miscellaneousDataService.getAttachmentsOfMiscellaneousDataOfLoggedRookie(miscellaneousDataId);

			super.unauthenticate();
		} catch (Throwable oops) {
			caught = oops.getClass();
		} finally {
			this.rollbackTransaction();
		}

		super.checkExceptions(expected, caught);

	}

	/**
	 * R17. An actor who is authenticated as a rookie must be able to:
	 *
	 * 1. Manage his or her curricula, which includes creating and updating them.
	 *
	 * Ratio of data coverage: 100% - Access as a rookie or not. - Create an
	 * attachment in a miscellaneous data that does belongs to the rookie logged in
	 * or not. - 1 attribute with domain restrictions.
	 *
	 **/
	@Test
	public void driverCreateAttachmentsMiscellaneousData() {

		Object testingData[][] = {

				/**
				 * POSITIVE TEST: Rookie is creating an attachment to one of his miscellaneous
				 * data
				 **/
				{ "rookie1", super.getEntityId("miscellaneousData1"), "http://www.url.com", null },
				/**
				 * NEGATIVE TEST: Rookie is trying to create an attachment to a miscellaneous
				 * data from other rookie
				 **/
				{ "rookie2", super.getEntityId("miscellaneousData1"), "http://www.url.com",
						IllegalArgumentException.class },
				/**
				 * NEGATIVE TEST: Another user is trying to create an attachment
				 **/
				{ "company1", super.getEntityId("miscellaneousData1"), "http://www.url.com",
						IllegalArgumentException.class },
				/**
				 * NEGATIVE TEST: Rookie is trying to create an attachment in blank
				 **/
				{ "rookie1", super.getEntityId("miscellaneousData1"), "", IllegalArgumentException.class } };

		for (int i = 0; i < testingData.length; i++)
			this.createAttachmentsMiscellaneousDataTemplate((String) testingData[i][0], (Integer) testingData[i][1],
					(String) testingData[i][2], (Class<?>) testingData[i][3]);

	}

	private void createAttachmentsMiscellaneousDataTemplate(String rookie, int miscellaneousDataId, String attachment,
			Class<?> expected) {

		Class<?> caught = null;

		try {
			super.startTransaction();
			super.authenticate(rookie);

			this.miscellaneousDataService.addAttachmentAsRookie(miscellaneousDataId, attachment);

			super.unauthenticate();
		} catch (Throwable oops) {
			caught = oops.getClass();
		} finally {
			this.rollbackTransaction();
		}

		super.checkExceptions(expected, caught);

	}

	/**
	 * R17. An actor who is authenticated as a rookie must be able to:
	 *
	 * 1. Manage his or her curricula, which includes updating them.
	 *
	 * Ratio of data coverage: 100% - Access as a rookie or not. - Delete an
	 * attachment in a miscellaneous data that does belongs to the rookie logged in
	 * or not. - Delete an attachment that exists or not.
	 *
	 **/
	@Test
	public void driverDeleteAttachmentMiscellaneousData() {

		Object testingData[][] = {

				/**
				 * POSITIVE TEST: Rookie is deleting an attachment of one of his miscellaneous
				 * data
				 **/
				{ "rookie1", super.getEntityId("miscellaneousData1"), 0, null },
				/**
				 * NEGATIVE TEST: Rookie is trying to delete an attachment of a miscellaneous
				 * data from other rookie
				 **/
				{ "rookie2", super.getEntityId("miscellaneousData1"), 0, IllegalArgumentException.class },
				/**
				 * NEGATIVE TEST: Another user is trying to delete an attachment of a
				 * miscellaneous data
				 **/
				{ "company1", super.getEntityId("miscellaneousData1"), 0, IllegalArgumentException.class },
				/**
				 * NEGATIVE TEST: Rookie is trying to delete an attachment that doesn't exist
				 **/
				{ "rookie1", super.getEntityId("miscellaneousData1"), 3, IllegalArgumentException.class }, };

		for (int i = 0; i < testingData.length; i++)
			this.deleteAttachmentMiscellaneousDataTemplate((String) testingData[i][0], (Integer) testingData[i][1],
					(Integer) testingData[i][2], (Class<?>) testingData[i][3]);

	}

	private void deleteAttachmentMiscellaneousDataTemplate(String rookie, int miscellaneousDataId, int attachmentIndex,
			Class<?> expected) {

		Class<?> caught = null;

		try {
			super.startTransaction();
			super.authenticate(rookie);

			this.miscellaneousDataService.deleteAttachmentAsRookie(miscellaneousDataId, attachmentIndex);

			super.unauthenticate();
		} catch (Throwable oops) {
			caught = oops.getClass();
		} finally {
			this.rollbackTransaction();
		}

		super.checkExceptions(expected, caught);

	}

}
