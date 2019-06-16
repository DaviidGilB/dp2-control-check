
package services;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import utilities.AbstractTest;
import domain.Actor;
import domain.Message;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/junit.xml" })
@Transactional
public class BroadcastAdminServiceTest extends AbstractTest {

	@Autowired
	private MessageService messageService;

	@Autowired
	private AdminService adminService;

	@Autowired
	private ActorService actorService;

	/**
	 * We are going to test Requirement 24.1
	 * 
	 * 24. An actor who is authenticated as an administrator must be able to:
	 * 
	 * 1. Broadcast a message to all actors
	 * 
	 */

	/**
	 * Coverage: In broadcastMessage, we have the positive case and the Assert to
	 * check that you are logged as admin The Message constrains are checked in
	 * SendMessageTest Positive tes + Constratins = 2 Total test = 3 Coverage = 3/2
	 * = 1.5 = 150%
	 */
	
	/**
	 * Sentence Coverage:
	 * 		AdminService: 14.4%
	 * 
	 */

	@Test
	public void driverUpdateMessage() {

		Object testingData[][] = { {
				// Positive test
				"admin1", "admin1", "subject", "body", null },
				{
						// Negative test, broadcasting with a non admin actor
						"member1", "member1", "subject", "body", IllegalArgumentException.class },
				{
						// Negative test, not logged actor
						"", "member1", "subject", "body", IllegalArgumentException.class } };

		for (int i = 0; i < testingData.length; i++)
			this.templateSendMessage((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2],
					(String) testingData[i][3], (Class<?>) testingData[i][4]);
	}

	protected void templateSendMessage(String username, String usernameVerification, String subject, String body,
			Class<?> expected) {

		Class<?> caught = null;

		try {

			// En cada iteracion comenzamos una transaccion, de esta manera, no se toman
			// valores residuales de otros test
			this.startTransaction();

			super.authenticate(username);

			Date thisMoment = new Date();
			thisMoment.setTime(thisMoment.getTime() - 1000);

			Message message = this.messageService.create();
			Actor sender = this.actorService.getActorByUsername(usernameVerification);
			Actor receiverActor = this.actorService.getActorByUsername(usernameVerification);

			message.setMoment(thisMoment);
			message.setSubject(subject);
			message.setBody(body);
			message.setReceiver(receiverActor.getUserAccount().getUsername());

			message.setSender(sender.getUserAccount().getUsername());

			this.adminService.broadcastMessage(message);
			this.messageService.flush();

			super.authenticate(null);

		} catch (Throwable oops) {
			caught = oops.getClass();
		} finally {
			// Se fuerza el rollback para que no de ningun problema la siguiente iteracion
			this.rollbackTransaction();
		}

		super.checkExceptions(expected, caught);

	}

	
	/**
	 * We are going to test Requirement 4.1
	 * 
	 * 4.An actor who is authenticated as an administrator must be able to:
	 * 		1.Run  a  procedure  to  notify  the  existing  users  of  the  rebranding. The  system  must guarantee that the process is run only once.
	 * 
	 */

	/**
	 * Coverage: BroadcastRebrandingTest Positive test + Constraints = 2 Total test = 2 Coverage = 2/2 = 100%
	 * Data coverage: 92.9%
	 */
	@Test
	public void driverBroadcastRebranding() {

		Object testingData[][] = {
				// Positive test
				{ "admin1", null },
				// Negative test: Trying to send a broadcast rebranding with a different role
				{ "company1", IllegalArgumentException.class } };

		for (int i = 0; i < testingData.length; i++)
			this.templateBroadcastRebranding((String) testingData[i][0], (Class<?>) testingData[i][1]);
	}

	private void templateBroadcastRebranding(String username, Class<?> expected) {
		Class<?> caught = null;

		try {
			this.startTransaction();
			this.authenticate(username);

			Message message = this.messageService.create();
			message.setBody(
					"We inform that we changed our name from 'Acme-Hacker-Rank' to 'Acme-Rookie' for legal reasons/ Se informa que nuestra empresa ha pasado de llamarse 'Acme-Hacker-Rank' a 'Acme-Rookie' por temas legales.");
			message.setSubject("REBRANDING NOTIFICATION / NOTIFICACION DE CAMBIO DE NOMBRE");
			message.setTags("NOTIFICATION, SYSTEM, IMPORTANT");
			this.adminService.broadcastMessageRebranding(message);

		} catch (Throwable oops) {
			caught = oops.getClass();
		}

		super.checkExceptions(expected, caught);
		this.unauthenticate();
		this.rollbackTransaction();
	}
}
