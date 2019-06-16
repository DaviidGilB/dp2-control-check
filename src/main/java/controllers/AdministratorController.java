/*
 * AdministratorController.java
 * 
 * Copyright (C) 2019 Universidad de Sevilla
 * 
 * The use of this project is hereby constrained to the conditions of the
 * TDG Licence, a copy of which you may download from
 * http://www.tdg-seville.info/License.html
 */

package controllers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import security.LoginService;
import security.UserAccount;
import services.ActorService;
import services.AdminService;
import services.AuditorService;
import services.ConfigurationService;
import domain.Actor;
import domain.Admin;
import domain.Auditor;
import domain.Configuration;
import forms.FormObjectAdmin;
import forms.FormObjectAuditor;

@Controller
@RequestMapping("/administrator")
public class AdministratorController extends AbstractController {

	@Autowired
	private AdminService			adminService;

	@Autowired
	private ConfigurationService	configurationService;

	@Autowired
	private ActorService			actorService;

	@Autowired
	AuditorService					auditorService;


	// Constructors -----------------------------------------------------------

	public AdministratorController() {
		super();
	}

	@RequestMapping(value = "/administrator/computeScore", method = RequestMethod.GET)
	public ModelAndView computeScore() {
		try {
			ModelAndView result;

			this.adminService.computeScore();
			SimpleDateFormat formatter;
			String moment;
			Boolean isMessageBroadcasted = this.configurationService.isRebrandingBroadcasted();

			formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			moment = formatter.format(new Date());

			String welcomeMessage;
			String systemName = this.configurationService.getConfiguration().getSystemName();
			UserAccount userAccount;
			String username;

			try {
				userAccount = LoginService.getPrincipal();
				username = userAccount.getUsername();
			} catch (Exception oops) {
				username = "";
			}

			String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();
			if (locale.equals("EN"))
				welcomeMessage = this.configurationService.getConfiguration().getWelcomeMessageEnglish();
			else
				welcomeMessage = this.configurationService.getConfiguration().getWelcomeMessageSpanish();

			result = new ModelAndView("welcome/index");
			result.addObject("username", username);
			result.addObject("isMessageBroadcasted", isMessageBroadcasted);
			result.addObject("moment", moment);
			result.addObject("welcomeMessage", welcomeMessage);
			result.addObject("systemName", systemName);
			result.addObject("confirmation", true);

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}
	}

	@RequestMapping(value = "/administrator/create", method = RequestMethod.GET)
	public ModelAndView createAdmin() {
		try {
			ModelAndView result;

			FormObjectAdmin formObjectAdmin = new FormObjectAdmin();
			formObjectAdmin.setTermsAndConditions(false);

			result = this.createEditModelAndView(formObjectAdmin);

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/administrator/administrator/create.do");
		}
	}

	@RequestMapping(value = "/administrator/create", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@Valid FormObjectAdmin formObjectAdmin, BindingResult binding) {
		try {
			ModelAndView result;

			Admin admin = new Admin();
			admin = this.adminService.createAdmin();

			Configuration configuration = this.configurationService.getConfiguration();
			String prefix = configuration.getSpainTelephoneCode();

			//Reconstruccion
			admin = this.adminService.reconstruct(formObjectAdmin, binding);

			if (binding.hasErrors())
				result = this.createEditModelAndView(formObjectAdmin);
			else
				try {

					if (admin.getPhone().matches("([0-9]{4,})$"))
						admin.setPhone(prefix + admin.getPhone());
					this.adminService.saveNewAdmin(admin);

					result = new ModelAndView("redirect:/");

				} catch (Throwable oops) {
					result = this.createEditModelAndView(formObjectAdmin, "company.duplicated.user");

				}
			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/administrator/administrator/create.do");
		}
	}

	protected ModelAndView createEditModelAndView(FormObjectAdmin formObjectAdmin) {
		ModelAndView result;

		result = this.createEditModelAndView(formObjectAdmin, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(FormObjectAdmin formObjectAdmin, String messageCode) {
		ModelAndView result;

		String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();
		List<String> cardType = this.configurationService.getConfiguration().getCardType();

		result = new ModelAndView("administrator/administrator/create");
		result.addObject("formObjectAdmin", formObjectAdmin);
		result.addObject("message", messageCode);
		result.addObject("locale", locale);
		result.addObject("cardType", cardType);

		return result;
	}

	//LIST SUSPICIOUS ACTORS
	@RequestMapping(value = "/suspicious/list", method = RequestMethod.GET)
	public ModelAndView suspicious() {
		try {
			ModelAndView result;
			List<Actor> actors = new ArrayList<Actor>();
			actors = this.actorService.findAll();

			result = new ModelAndView("suspicious/administrator/list");

			result.addObject("actors", actors);

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}

	}

	//BAN
	@RequestMapping(value = "/suspicious/ban", method = RequestMethod.GET)
	public ModelAndView ban(@RequestParam(required = false) String actorId) {
		try {
			Assert.isTrue(StringUtils.isNumeric(actorId));
			int actorIdInt = Integer.parseInt(actorId);

			ModelAndView result;

			Actor actor;
			actor = this.actorService.findOne(actorIdInt);

			this.adminService.banSuspiciousActor(actor);
			result = new ModelAndView("redirect:list.do");

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}

	}

	//UNBAN
	@RequestMapping(value = "/suspicious/unban", method = RequestMethod.GET)
	public ModelAndView unban(@RequestParam(required = false) String actorId) {
		try {
			Assert.isTrue(StringUtils.isNumeric(actorId));
			int actorIdInt = Integer.parseInt(actorId);

			ModelAndView result;

			Actor actor;
			actor = this.actorService.findOne(actorIdInt);

			this.adminService.unBanSuspiciousActor(actor);
			result = new ModelAndView("redirect:list.do");

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}

	}

	@RequestMapping(value = "/auditor/create", method = RequestMethod.GET)
	public ModelAndView createAuditor() {
		try {
			ModelAndView result;

			FormObjectAuditor formObjectAuditor = new FormObjectAuditor();
			formObjectAuditor.setTermsAndConditions(false);

			result = this.createEditModelAndView(formObjectAuditor);

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/administrator/auditor/create.do");
		}
	}

	@RequestMapping(value = "/auditor/create", method = RequestMethod.POST, params = "save")
	public ModelAndView saveAuditor(@Valid FormObjectAuditor formObjectAuditor, BindingResult binding) {
		try {
			ModelAndView result;

			Auditor auditor = new Auditor();
			auditor = this.auditorService.createAuditor();

			Configuration configuration = this.configurationService.getConfiguration();
			String prefix = configuration.getSpainTelephoneCode();

			//Reconstruccion
			auditor = this.auditorService.reconstruct(formObjectAuditor, binding);

			if (binding.hasErrors())
				result = this.createEditModelAndView(formObjectAuditor);
			else
				try {

					if (auditor.getPhone().matches("([0-9]{4,})$"))
						auditor.setPhone(prefix + auditor.getPhone());
					this.auditorService.saveNewAuditor(auditor);

					result = new ModelAndView("redirect:/");

				} catch (Throwable oops) {
					result = this.createEditModelAndView(formObjectAuditor, "company.duplicated.user");

				}
			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/administrator/auditor/create.do");
		}
	}

	protected ModelAndView createEditModelAndView(FormObjectAuditor formObjectAuditor) {
		ModelAndView result;

		result = this.createEditModelAndView(formObjectAuditor, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(FormObjectAuditor formObjectAuditor, String messageCode) {
		ModelAndView result;

		String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();
		List<String> cardType = this.configurationService.getConfiguration().getCardType();

		result = new ModelAndView("administrator/auditor/create");
		result.addObject("formObjectAuditor", formObjectAuditor);
		result.addObject("message", messageCode);
		result.addObject("locale", locale);
		result.addObject("cardType", cardType);

		return result;
	}

}
