
package services;

import java.util.Date;

import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import utilities.AbstractTest;
import domain.Audit;
import domain.Auditor;
import domain.Position;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@Transactional
public class AuditTest extends AbstractTest {

	@Autowired
	private AuditorService	auditorService;

	@Autowired
	private AuditService	auditService;

	@Autowired
	private PositionService	positionService;


	/**
	 * 3. An actor who is authenticated as an auditor must be able to:
	 * 
	 * 1. Self-assign a position to audit it
	 */

	/**
	 * Ratio of data coverage: 9/9 = 100%
	 */
	
	/**
	 * 
	 * Sentence Coverage:	
	 * 		Audit Service: 34.9%
	 * 		Auditor service 8.7%
	 * 
	 */
	@Test
	public void driverAssignPosition() {

		Object testingData[][] = {
			{
				/**
				 * Positive test: Listing all applications of the rookie
				 */
				"auditor1", "position1", "freeText1", 9, true, null
			}, {
				/**
				 * Negative test: The user tries to create an audit with a blank
				 * freeText and an Constraint Violation Exception is thrown
				 */
				"auditor1", "position1", "", 9, true, ConstraintViolationException.class
			}, {
				/**
				 * Negative test: The user tries to create an audit with a score
				 * bigger than 10 and an Constraint Violation Exception is thrown
				 */
				"auditor1", "position1", "freeText1", 11, true, ConstraintViolationException.class
			}, {
				/**
				 * Negative test: The user tries to create an audit with a
				 * score smaller than 0 and an Constraint Violation Exception is thrown
				 */
				"auditor1", "position1", "freeText1", -1, true, ConstraintViolationException.class
			}, {
				/**
				 * Negative test: The user tries to create an audit without
				 * specifying the mode (draft or final) and a Constraint Violation Exception is thrown
				 */
				"auditor1", "position1", "freeText1", 9, null, ConstraintViolationException.class
			}, {
				/**
				 * Negative test: A user that is not identified as auditor tries to
				 * create an audit and an Illegal Argument Exception is thrown
				 */
				"company1", "position1", "freeText1", 9, true, IllegalArgumentException.class
			}, {
				/**
				 * Negative test: An auditor tries to create an audit to a position that
				 * is in Draft Mode and an Illegal Argument Exception is thrown
				 */
				"auditor1", "position3", "freeText1", 9, true, IllegalArgumentException.class
			}, {
				/**
				 * Negative test: An auditor tries to create an audit to a position that is
				 * Cancelled and an Illegal Argument Exception is thrown
				 */
				"auditor1", "position4", "freeText1", 9, true, IllegalArgumentException.class
			}, {
				/**
				 * Negative test: An auditor tries to create an audit to a position that already has
				 * an audit created by him and an Illegal Argument Exception is thrown
				 */
				"auditor1", "position2", "freeText1", 9, true, IllegalArgumentException.class
			}
		};

		for (int i = 0; i < testingData.length; i++)
			this.templateassignPosition((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Integer) testingData[i][3], (Boolean) testingData[i][4], (Class<?>) testingData[i][5]);
	}
	private void templateassignPosition(String username, String position, String text, Integer score, Boolean draftMode, Class<?> expected) {

		Class<?> caught = null;

		this.startTransaction();
		super.authenticate(username);
		try {

			Auditor auditor = this.auditorService.loggedAuditor();

			Position positionA = this.positionService.findOne(super.getEntityId(position));

			Audit audit = this.auditService.create(positionA);

			//Reconstruct
			audit.setAuditor(auditor);
			audit.setFreeText(text);
			audit.setIsDraftMode(draftMode);
			audit.setScore(score);

			Date thisMoment = new Date();
			thisMoment.setTime(thisMoment.getTime() - 1);
			audit.setMomentCreation(thisMoment);

			this.auditorService.saveAudit(audit);

			this.auditorService.flush();

		} catch (Throwable oops) {
			caught = oops.getClass();
		}
		super.unauthenticate();

		super.checkExceptions(expected, caught);

		this.rollbackTransaction();
	}

	//-------------------------------------------------------EDIT AUDIT--------------------------------------------------------
	//-------------------------------------------------------------------------------------------------------------------------

	/**
	 * 3. An actor who is authenticated as an auditor must be able to:
	 * 
	 * 2. Manage his or her audits, which includes listing them, showing them, creating them,
	 * updating, and deleting them. An audit can be updated or deleted as long as it’s
	 * saved in draft mode.
	 */

	/**
	 * Ratio of data coverage: 8/8 100%
	 */
	@Test
	public void driverEditAudit() {

		Object testingData[][] = {
			{
				/**
				 * Positive test: A user logged as an auditor edits successfully one of his audits
				 */
				"auditor1", "audit1", "newFreeText", 9, true, null
			}, {
				/**
				 * Negative test: The user tries to edit one of his audits with a blank
				 * freeText and an Constraint Violation Exception is thrown
				 */
				"auditor1", "audit1", "", 9, true, ConstraintViolationException.class
			}, {
				/**
				 * Negative test: The user tries to edit one of his audits with an score bigger
				 * than 10 and an Constraint Violation Exception is thrown
				 */
				"auditor1", "audit1", "newFreeText", 11, true, ConstraintViolationException.class
			}, {
				/**
				 * Negative test: The user tries to edit one of his audits with an score smaller
				 * than 0 and an Constraint Violation Exception is thrown
				 */
				"auditor1", "audit1", "newFreeText", -1, true, ConstraintViolationException.class
			}, {
				/**
				 * Negative test: The user tries to edit one of his audits that is in Final Mode
				 */
				"auditor2", "audit2", "newFreeText", 9, true, IllegalArgumentException.class
			}, {
				/**
				 * Negative test: An Auditor tries to edit an Audit that belongs to another auditor
				 * and an Illegal Argument Exception is thrown
				 */
				"auditor2", "audit1", "newFreeText", 9, true, IllegalArgumentException.class
			}, {
				/**
				 * Negative test: An user not authorised as an Auditor tries to edit an Audit and
				 * an Illegal Argument Exception is thrown
				 */
				"company1", "audit1", "newFreeText", 9, true, IllegalArgumentException.class
			}, {
				/**
				 * Negative test: An anonymous user tries to edit an Audit and
				 * an Illegal Argument Exception is thrown
				 */
				null, "audit1", "newFreeText", 9, true, IllegalArgumentException.class
			}
		};

		for (int i = 0; i < testingData.length; i++)
			this.templateEditAudit((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Integer) testingData[i][3], (Boolean) testingData[i][4], (Class<?>) testingData[i][5]);
	}

	private void templateEditAudit(String username, String audit, String newFreeText, Integer score, Boolean draftMode, Class<?> expected) {

		Class<?> caught = null;

		this.startTransaction();
		super.authenticate(username);
		try {

			Audit auditA = this.auditService.findOne(super.getEntityId(audit));

			//Reconstruct
			Assert.isTrue(auditA.getIsDraftMode());
			auditA.setFreeText(newFreeText);
			auditA.setIsDraftMode(draftMode);
			auditA.setScore(score);

			auditA.setPosition(auditA.getPosition());
			auditA.setMomentCreation(auditA.getMomentCreation());
			auditA.setAuditor(auditA.getAuditor());
			//Fin Reconstruct

			this.auditorService.saveAudit(auditA);

			this.auditorService.flush();

		} catch (Throwable oops) {
			caught = oops.getClass();
		}
		super.unauthenticate();

		super.checkExceptions(expected, caught);

		this.rollbackTransaction();
	}

	//-------------------------------------------------------DELETE AUDIT--------------------------------------------------------
	//---------------------------------------------------------------------------------------------------------------------------

	/**
	 * 3. An actor who is authenticated as an auditor must be able to:
	 * 
	 * 2. Manage his or her audits, which includes listing them, showing them, creating them,
	 * updating, and DELETING them. An audit can be updated or deleted as long as it’s
	 * saved in draft mode.
	 */

	/**
	 * Ratio of data coverage: 5/5 100%
	 */
	@Test
	public void driverDeleteAudit() {

		Object testingData[][] = {
			{
				/**
				 * Positive test: An user logged as an auditor deletes successfully one of his audits
				 */
				"auditor1", "audit1", null
			}, {
				/**
				 * Negative test: An user logged as an auditor tries to delete an audit in Final Mode
				 * and an Illegal Argument Exception is thrown
				 */
				"auditor2", "audit2", IllegalArgumentException.class
			}, {
				/**
				 * Negative test: An user logged as an auditor tries to delete an audit
				 * that belongs to another user and an Illegal Argument Exception is thrown
				 */
				"auditor2", "audit1", IllegalArgumentException.class
			}, {
				/**
				 * Negative test: An user that is not an Auditor tries to delete
				 * an Audit and an Illegal Argument Exception is thrown
				 */
				"company1", "audit1", IllegalArgumentException.class
			}, {
				/**
				 * Negative test: An anonymous user tries to delete an Audit
				 * and an Illegal Argument Exception is thrown
				 */
				null, "audit1", IllegalArgumentException.class
			}
		};

		for (int i = 0; i < testingData.length; i++)
			this.templateDeleteAudit((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2]);
	}
	private void templateDeleteAudit(String username, String audit, Class<?> expected) {

		Class<?> caught = null;

		this.startTransaction();
		super.authenticate(username);
		try {

			Audit auditA = this.auditService.findOne(super.getEntityId(audit));

			this.auditService.deleteAudit(auditA);

			this.auditorService.flush();

		} catch (Throwable oops) {
			caught = oops.getClass();
		}
		super.unauthenticate();

		super.checkExceptions(expected, caught);

		this.rollbackTransaction();
	}
}
