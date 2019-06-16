
package controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import services.AdminService;
import services.ConfigurationService;
import services.MessageService;
import domain.Message;

@Controller
@RequestMapping("/broadcast/administrator")
public class AdministratorBroadcastMessageController extends AbstractController {

	@Autowired
	private AdminService			adminService;

	@Autowired
	private MessageService			messageService;

	@Autowired
	private ConfigurationService	configurationService;


	public AdministratorBroadcastMessageController() {

		super();
	}

	@RequestMapping(value = "/send", method = RequestMethod.GET)
	public ModelAndView create() {
		try {
			this.adminService.loggedAsAdmin();
			ModelAndView result;
			Message message;

			message = this.messageService.create();
			message.setTags("SYSTEM");
			result = new ModelAndView("broadcast/administrator/send");
			result.addObject("messageSend", message);
			result.addObject("targetUri", "broadcast/administrator/send.do");

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}
	}

	//Save
	@RequestMapping(value = "/send", method = RequestMethod.POST, params = "send")
	public ModelAndView send(@ModelAttribute("messageSend") Message message, BindingResult binding) {
		ModelAndView result;
		try {
			message = this.adminService.reconstruct(message, binding);

			if (binding.hasErrors())
				result = this.createEditModelAndView(message);
			else
				try {
					this.adminService.broadcastMessage(message);
					result = new ModelAndView("redirect:/");

				} catch (Throwable oops) {
					result = this.createEditModelAndView(message, "company.commit.error");
				}

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}
	}

	protected ModelAndView createEditModelAndView(Message message) {
		ModelAndView result;

		result = this.createEditModelAndView(message, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(Message message, String messageCode) {
		ModelAndView result;

		result = new ModelAndView("broadcast/administrator/send");

		result.addObject("targetUri", "broadcast/administrator/send.do");
		result.addObject("messageSend", message);
		result.addObject("message", messageCode);

		return result;
	}

	@RequestMapping(value = "/sendRebranding", method = RequestMethod.GET)
	public ModelAndView createRebrandingo() {
		try {
			ModelAndView result;

			Message message;

			message = this.messageService.create();
			message.setBody("We inform that we changed our name from 'Acme-Hacker-Rank' to 'Acme-Rookie' for legal reasons/ Se informa que nuestra empresa ha pasado de llamarse 'Acme-Hacker-Rank' a 'Acme-Rookie' por temas legales.");
			message.setSubject("REBRANDING NOTIFICATION / NOTIFICACION DE CAMBIO DE NOMBRE");
			message.setTags("NOTIFICATION, SYSTEM, IMPORTANT");

			result = this.createEditModelAndViewRebranding(message);

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}

	}
	@RequestMapping(value = "/sendRebranding", method = RequestMethod.POST, params = "send")
	public ModelAndView sendRebranding(@ModelAttribute("messageSend") Message message, BindingResult binding) {
		try {
			ModelAndView result;

			message = this.adminService.reconstruct(message, binding);

			if (binding.hasErrors())
				result = this.createEditModelAndViewRebranding(message);
			else
				try {
					this.adminService.broadcastMessageRebranding(message);
					result = new ModelAndView("redirect:/");

				} catch (Throwable oops) {
					result = this.createEditModelAndViewRebranding(message, "company.commit.error");

				}
			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}
	}

	protected ModelAndView createEditModelAndViewRebranding(Message message) {
		ModelAndView result;

		result = this.createEditModelAndViewRebranding(message, null);

		return result;
	}

	protected ModelAndView createEditModelAndViewRebranding(Message message, String messageCode) {
		ModelAndView result;

		Boolean isMessageBroadcasted = this.configurationService.isRebrandingBroadcasted();

		if (isMessageBroadcasted)
			result = new ModelAndView("redirect:/");
		else {
			result = new ModelAndView("broadcast/administrator/sendRebranding");
			result.addObject("messageSend", message);
			result.addObject("message", messageCode);
		}
		return result;
	}

}
