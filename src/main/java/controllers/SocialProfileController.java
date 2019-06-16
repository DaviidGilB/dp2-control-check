
package controllers;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import security.Authority;
import security.LoginService;
import security.UserAccount;
import services.ActorService;
import services.AdminService;
import services.AuditorService;
import services.CompanyService;
import services.ConfigurationService;
import services.ProviderService;
import services.RookieService;
import services.SocialProfileService;
import domain.Actor;
import domain.Admin;
import domain.Auditor;
import domain.Company;
import domain.Configuration;
import domain.Provider;
import domain.Rookie;
import domain.SocialProfile;
import forms.FormObjectEditAdmin;
import forms.FormObjectEditAuditor;
import forms.FormObjectEditCompany;
import forms.FormObjectEditProvider;
import forms.FormObjectEditRookie;

@Controller
@RequestMapping("/authenticated")
public class SocialProfileController extends AbstractController {

	@Autowired
	private ActorService			actorService;

	@Autowired
	private SocialProfileService	socialProfileService;

	@Autowired
	private CompanyService			companyService;

	@Autowired
	private ConfigurationService	configurationService;

	@Autowired
	private AdminService			adminService;

	@Autowired
	private RookieService			rookieService;

	@Autowired
	private AuditorService			auditorService;

	@Autowired
	private ProviderService			providerService;


	// -------------------------------------------------------------------
	// ---------------------------LIST
	// BROTHERHOOD------------------------------------
	@RequestMapping(value = "/showProfile", method = RequestMethod.GET)
	public ModelAndView list() {
		try {
			ModelAndView result;
			UserAccount userAccount;
			userAccount = LoginService.getPrincipal();
			Actor logguedActor = new Actor();
			List<SocialProfile> socialProfiles = new ArrayList<SocialProfile>();
			List<Authority> authorities = (List<Authority>) userAccount.getAuthorities();
			Boolean score = false;
			result = new ModelAndView("authenticated/showProfile");

			logguedActor = this.actorService.getActorByUsername(userAccount.getUsername());
			socialProfiles = logguedActor.getSocialProfiles();
			Boolean trueValue = true;

			if (authorities.get(0).toString().equals("COMPANY"))
				score = true;

			result.addObject("socialProfiles", socialProfiles);
			result.addObject("actor", logguedActor);
			result.addObject("trueValue", trueValue);
			result.addObject("score", score);
			result.addObject("requestURI", "authenticated/showProfile.do");

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}
	}

	// ---------------------------------------------------------------------
	@RequestMapping(value = "/socialProfile/create", method = RequestMethod.GET)
	public ModelAndView create() {
		try {
			ModelAndView result;
			SocialProfile socialProfile;

			socialProfile = this.socialProfileService.create();
			result = this.createEditModelAndView(socialProfile);

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/authenticated/showProfile.do");
		}
	}

	// ------------------------------------------------------------------------------------
	// ---------------------------EDIT SOCIAL
	// PROFILE--------------------------------------
	@RequestMapping(value = "/socialProfile/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam(required = false) String socialProfileId) {
		try {
			Assert.isTrue(StringUtils.isNumeric(socialProfileId));
			int socialProfileIdInt = Integer.parseInt(socialProfileId);

			ModelAndView result;
			SocialProfile socialProfile;

			Actor logged = this.actorService.getActorByUsername(LoginService.getPrincipal().getUsername());

			List<SocialProfile> socialProfiles = logged.getSocialProfiles();

			socialProfile = this.socialProfileService.findOne(socialProfileIdInt);
			Assert.notNull(socialProfile);
			result = this.createEditModelAndView(socialProfile);

			if (!(socialProfiles.contains(socialProfile)))
				result = this.list();
			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/authenticated/showProfile.do");
		}
	}

	// ---------------------------------------------------------------------
	// ---------------------------SAVE --------------------------------------
	@RequestMapping(value = "/socialProfile/create", method = RequestMethod.POST, params = "save")
	public ModelAndView save(SocialProfile socialProfile, BindingResult binding) {
		try {
			ModelAndView result;
			Actor logguedActor = this.actorService.getActorByUsername(LoginService.getPrincipal().getUsername());

			socialProfile = this.socialProfileService.reconstruct(socialProfile, binding);

			System.out.println(binding);
			if (binding.hasErrors())
				result = this.createEditModelAndView(socialProfile);
			else
				try {

					SocialProfile saved = this.socialProfileService.save(socialProfile);
					List<SocialProfile> socialProfiles = logguedActor.getSocialProfiles();

					if (socialProfiles.contains(socialProfile)) {
						socialProfiles.remove(saved);
						socialProfiles.add(saved);
					} else
						socialProfiles.add(saved);

					logguedActor.setSocialProfiles(socialProfiles);

					this.actorService.save(logguedActor);

					result = new ModelAndView("redirect:/authenticated/showProfile.do");
				} catch (Throwable oops) {
					result = this.createEditModelAndView(socialProfile, "socialProfile.commit.error");
				}
			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/authenticated/showProfile.do");
		}
	}

	// ---------------------------------------------------------------------
	// ---------------------------DELETE------------------------------------
	@RequestMapping(value = "/socialProfile/create", method = RequestMethod.POST, params = "delete")
	public ModelAndView delete(SocialProfile socialProfile, BindingResult binding) {
		try {
			ModelAndView result;

			socialProfile = this.socialProfileService.reconstruct(socialProfile, binding);

			try {

				this.socialProfileService.deleteSocialProfile(socialProfile);
				result = new ModelAndView("redirect:/authenticated/showProfile.do");

			} catch (Throwable oops) {
				result = this.createEditModelAndView(socialProfile, "socialProfile.commit.error");
			}
			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/authenticated/showProfile.do");
		}
	}
	// ---------------------------------------------------------------------
	// ---------------------------EDIT PERSONAL DAT-------------------------

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView editPersonalData() {
		try {
			ModelAndView result = null;

			UserAccount userAccount;
			userAccount = LoginService.getPrincipal();

			List<Authority> authorities = (List<Authority>) userAccount.getAuthorities();

			if (authorities.get(0).toString().equals("COMPANY")) {
				Company company = this.companyService.loggedCompany();
				Assert.notNull(company);
				FormObjectEditCompany formCompany = this.companyService.getFormObjectEditCompany(company);
				formCompany.setId(company.getId());
				result = this.createEditModelAndView(formCompany);

			} else if (authorities.get(0).toString().equals("ADMIN")) {
				Admin admin = this.adminService.loggedAdmin();
				Assert.notNull(admin);
				FormObjectEditAdmin formAdmin = this.adminService.getFormObjectEditadmin(admin);
				formAdmin.setId(admin.getId());
				result = this.createEditModelAndView(formAdmin);
			} else if (authorities.get(0).toString().equals("ROOKIE")) {
				Rookie rookie = this.rookieService.loggedRookie();
				Assert.notNull(rookie);
				FormObjectEditRookie formRookie = this.rookieService.getFormObjectEditRookie(rookie);
				formRookie.setId(rookie.getId());
				result = this.createEditModelAndView(formRookie);
			} else if (authorities.get(0).toString().equals("AUDITOR")) {
				Auditor auditor = this.auditorService.loggedAuditor();
				Assert.notNull(auditor);
				FormObjectEditAuditor formAuditor = this.auditorService.getFormObjectEditAuditor(auditor);
				formAuditor.setId(auditor.getId());
				result = this.createEditModelAndView(formAuditor);
			} else if (authorities.get(0).toString().equals("PROVIDER")) {
				Provider provider = this.providerService.loggedProvider();
				Assert.notNull(provider);
				FormObjectEditProvider formProvider = this.providerService.getFormObjectEditProvider(provider);
				formProvider.setId(provider.getId());
				result = this.createEditModelAndView(formProvider);
			}

			if (result == null)
				result = this.list();

			result.addObject("cardType", this.configurationService.getConfiguration().getCardType());

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/authenticated/showProfile.do");
		}
	}
	// ---------------------------------------------------------------------
	// ---------------------------SAVE PERSONAL DATA------------------------

	// Company
	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView saveCompany(@Valid FormObjectEditCompany companyForm, BindingResult binding) {
		try {
			ModelAndView result;

			Company company = this.companyService.reconstructCompanyPersonalData(companyForm, binding);
			Configuration configuration = this.configurationService.getConfiguration();

			String prefix = configuration.getSpainTelephoneCode();

			if (binding.hasErrors()) {
				result = this.createEditModelAndView(companyForm);
				result.addObject("cardType", this.configurationService.getConfiguration().getCardType());
			} else
				try {
					if (company.getPhone().matches("(\\+[0-9]{1,3})(\\([0-9]{1,3}\\))([0-9]{4,})$") || company.getPhone().matches("(\\+[0-9]{1,3})([0-9]{4,})$"))
						this.companyService.updateCompany(company);
					else if (company.getPhone().matches("([0-9]{4,})$")) {
						company.setPhone(prefix + company.getPhone());
						this.companyService.updateCompany(company);
					} else
						this.companyService.updateCompany(company);
					result = new ModelAndView("redirect:/authenticated/showProfile.do");
				} catch (Throwable oops) {
					result = this.createEditModelAndView(companyForm, "socialProfile.commit.error");
					result.addObject("cardType", this.configurationService.getConfiguration().getCardType());
				}

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/authenticated/showProfile.do");
		}
	}

	@RequestMapping(value = "/admin/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView saveCompany(@Valid FormObjectEditAdmin adminForm, BindingResult binding) {
		try {
			ModelAndView result;

			Admin admin = this.adminService.reconstructCompanyPersonalData(adminForm, binding);
			Configuration configuration = this.configurationService.getConfiguration();

			String prefix = configuration.getSpainTelephoneCode();

			if (binding.hasErrors()) {
				result = this.createEditModelAndView(adminForm);
				result.addObject("cardType", this.configurationService.getConfiguration().getCardType());
			} else
				try {
					if (admin.getPhone().matches("(\\+[0-9]{1,3})(\\([0-9]{1,3}\\))([0-9]{4,})$") || admin.getPhone().matches("(\\+[0-9]{1,3})([0-9]{4,})$"))
						this.adminService.save(admin);
					else if (admin.getPhone().matches("([0-9]{4,})$")) {
						admin.setPhone(prefix + admin.getPhone());
						this.adminService.save(admin);
					} else
						this.adminService.save(admin);
					result = new ModelAndView("redirect:/authenticated/showProfile.do");
				} catch (Throwable oops) {
					result = this.createEditModelAndView(adminForm, "socialProfile.commit.error");
					result.addObject("cardType", this.configurationService.getConfiguration().getCardType());
				}

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/authenticated/showProfile.do");
		}
	}

	@RequestMapping(value = "/rookie/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView saveRookie(@Valid FormObjectEditRookie rookieForm, BindingResult binding) {
		try {
			ModelAndView result;

			Rookie rookie = this.rookieService.reconstructRookiePersonalData(rookieForm, binding);
			Configuration configuration = this.configurationService.getConfiguration();

			String prefix = configuration.getSpainTelephoneCode();

			if (binding.hasErrors()) {
				result = this.createEditModelAndView(rookieForm);
				result.addObject("cardType", this.configurationService.getConfiguration().getCardType());
			} else
				try {
					if (rookie.getPhone().matches("(\\+[0-9]{1,3})(\\([0-9]{1,3}\\))([0-9]{4,})$") || rookie.getPhone().matches("(\\+[0-9]{1,3})([0-9]{4,})$"))
						this.rookieService.save(rookie);
					else if (rookie.getPhone().matches("([0-9]{4,})$")) {
						rookie.setPhone(prefix + rookie.getPhone());
						this.rookieService.save(rookie);
					} else
						this.rookieService.save(rookie);
					result = new ModelAndView("redirect:/authenticated/showProfile.do");
				} catch (Throwable oops) {
					result = this.createEditModelAndView(rookieForm, "socialProfile.commit.error");
					result.addObject("cardType", this.configurationService.getConfiguration().getCardType());
				}

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/authenticated/showProfile.do");
		}
	}

	@RequestMapping(value = "/auditor/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView saveAuditor(@Valid FormObjectEditAuditor auditorForm, BindingResult binding) {
		try {
			ModelAndView result;

			Auditor auditor = this.auditorService.reconstructAuditorPersonalData(auditorForm, binding);
			Configuration configuration = this.configurationService.getConfiguration();

			String prefix = configuration.getSpainTelephoneCode();

			if (binding.hasErrors()) {
				result = this.createEditModelAndView(auditorForm);
				result.addObject("cardType", this.configurationService.getConfiguration().getCardType());
			} else
				try {
					if (auditor.getPhone().matches("(\\+[0-9]{1,3})(\\([0-9]{1,3}\\))([0-9]{4,})$") || auditor.getPhone().matches("(\\+[0-9]{1,3})([0-9]{4,})$"))
						this.auditorService.save(auditor);
					else if (auditor.getPhone().matches("([0-9]{4,})$")) {
						auditor.setPhone(prefix + auditor.getPhone());
						this.auditorService.save(auditor);
					} else
						this.auditorService.save(auditor);
					result = new ModelAndView("redirect:/authenticated/showProfile.do");
				} catch (Throwable oops) {
					result = this.createEditModelAndView(auditorForm, "socialProfile.commit.error");
					result.addObject("cardType", this.configurationService.getConfiguration().getCardType());
				}

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/authenticated/showProfile.do");
		}
	}

	@RequestMapping(value = "/provider/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView saveProvider(@Valid FormObjectEditProvider providerForm, BindingResult binding) {
		try {
			ModelAndView result;

			Provider provider = this.providerService.reconstructAuditorPersonalData(providerForm, binding);
			Configuration configuration = this.configurationService.getConfiguration();

			String prefix = configuration.getSpainTelephoneCode();

			if (binding.hasErrors()) {
				result = this.createEditModelAndView(providerForm);
				result.addObject("cardType", this.configurationService.getConfiguration().getCardType());
			} else
				try {
					if (provider.getPhone().matches("(\\+[0-9]{1,3})(\\([0-9]{1,3}\\))([0-9]{4,})$") || provider.getPhone().matches("(\\+[0-9]{1,3})([0-9]{4,})$"))
						this.providerService.updateProvider(provider);
					else if (provider.getPhone().matches("([0-9]{4,})$")) {
						provider.setPhone(prefix + provider.getPhone());
						this.providerService.updateProvider(provider);
					} else
						this.providerService.updateProvider(provider);
					result = new ModelAndView("redirect:/authenticated/showProfile.do");
				} catch (Throwable oops) {
					result = this.createEditModelAndView(providerForm, "socialProfile.commit.error");
					result.addObject("cardType", this.configurationService.getConfiguration().getCardType());
				}

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/authenticated/showProfile.do");
		}
	}

	// ---------------------------------------------------------------------
	// ---------------------------CREATEEDITMODELANDVIEW--------------------

	protected ModelAndView createEditModelAndView(SocialProfile socialProfile) {

		ModelAndView result;

		result = this.createEditModelAndView(socialProfile, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(SocialProfile socialProfile, String messageCode) {

		ModelAndView result;

		result = new ModelAndView("authenticated/socialProfile/create");
		result.addObject("socialProfile", socialProfile);
		result.addObject("message", messageCode);

		return result;
	}

	// ---------------------------------------------------------------------
	// -------------------CREATEEDITMODELANDVIEW ACTOR----------------------

	// Company
	protected ModelAndView createEditModelAndView(FormObjectEditCompany company) {

		ModelAndView result;

		result = this.createEditModelAndView(company, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(FormObjectEditCompany company, String messageCode) {

		ModelAndView result;

		result = new ModelAndView("authenticated/edit");
		result.addObject("formObjectEditCompany", company);
		result.addObject("message", messageCode);

		return result;
	}

	protected ModelAndView createEditModelAndView(FormObjectEditAdmin admin) {

		ModelAndView result;

		result = this.createEditModelAndView(admin, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(FormObjectEditAdmin admin, String messageCode) {

		ModelAndView result;

		result = new ModelAndView("authenticated/edit");
		result.addObject("formObjectEditAdmin", admin);
		result.addObject("message", messageCode);

		return result;
	}

	protected ModelAndView createEditModelAndView(FormObjectEditRookie rookie) {

		ModelAndView result;

		result = this.createEditModelAndView(rookie, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(FormObjectEditRookie rookie, String messageCode) {

		ModelAndView result;

		result = new ModelAndView("authenticated/edit");
		result.addObject("formObjectEditRookie", rookie);
		result.addObject("message", messageCode);

		return result;
	}

	protected ModelAndView createEditModelAndView(FormObjectEditAuditor auditor) {

		ModelAndView result;

		result = this.createEditModelAndView(auditor, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(FormObjectEditAuditor auditor, String messageCode) {

		ModelAndView result;

		result = new ModelAndView("authenticated/edit");
		result.addObject("formObjectEditAuditor", auditor);
		result.addObject("message", messageCode);

		return result;
	}

	protected ModelAndView createEditModelAndView(FormObjectEditProvider provider) {

		ModelAndView result;

		result = this.createEditModelAndView(provider, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(FormObjectEditProvider provider, String messageCode) {

		ModelAndView result;

		result = new ModelAndView("authenticated/edit");
		result.addObject("formObjectEditProvider", provider);
		result.addObject("message", messageCode);

		return result;
	}

}
