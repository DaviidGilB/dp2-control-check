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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import security.Authority;
import services.ActorService;
import services.AuditorService;
import services.CompanyService;
import services.ProviderService;
import services.RookieService;
import domain.Actor;

@Controller
@RequestMapping(value = "/authenticated")
public class DeleteUserController extends AbstractController {

	@Autowired
	private ActorService	actorService;
	@Autowired
	private RookieService	rookieService;
	@Autowired
	private CompanyService	companyService;
	@Autowired
	private AuditorService	auditorService;
	@Autowired
	private ProviderService	providerService;


	// Constructors -----------------------------------------------------------

	public DeleteUserController() {
		super();
	}

	@RequestMapping(value = "/deleteUser", method = RequestMethod.GET)
	public ModelAndView deleteUser() {
		try {
			ModelAndView result;

			Actor actor = this.actorService.loggedActor();

			List<Authority> authorities = (List<Authority>) actor.getUserAccount().getAuthorities();

			try {
				if (authorities.get(0).toString().equals("ROOKIE"))
					this.rookieService.deleteRookie();
				else if (authorities.get(0).toString().equals("COMPANY"))
					this.companyService.deleteCompany();
				else if (authorities.get(0).toString().equals("AUDITOR"))
					this.auditorService.deleteAuditor();
				else if (authorities.get(0).toString().equals("PROVIDER"))
					this.providerService.deleteProvider();

				result = new ModelAndView("redirect:/j_spring_security_logout");
			} catch (Throwable oops) {
				result = new ModelAndView("redirect:/");
			}

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}
	}
}
