
package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
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

import services.ActorService;
import services.ApplicationService;
import services.AuditService;
import services.CompanyService;
import services.ConfigurationService;
import services.ItemService;
import services.PositionService;
import services.ProblemService;
import services.ProviderService;
import services.RookieService;
import services.SponsorshipService;
import domain.Actor;
import domain.Application;
import domain.Audit;
import domain.Company;
import domain.Configuration;
import domain.Curriculum;
import domain.Item;
import domain.Position;
import domain.Problem;
import domain.Provider;
import domain.Rookie;
import domain.SocialProfile;
import domain.Sponsorship;
import forms.FormObjectCompany;
import forms.FormObjectProvider;
import forms.FormObjectRookie;

@Controller
@RequestMapping("/anonymous")
public class AnonymousController extends AbstractController {

	@Autowired
	private RookieService			rookieService;

	@Autowired
	private ConfigurationService	configurationService;

	@Autowired
	private CompanyService			companyService;

	@Autowired
	private PositionService			positionService;

	@Autowired
	private ApplicationService		applicationService;

	@Autowired
	private ProblemService			problemService;

	@Autowired
	private ActorService			actorService;

	@Autowired
	private AuditService			auditService;

	@Autowired
	private ProviderService			providerService;

	@Autowired
	private ItemService				itemService;

	@Autowired
	private SponsorshipService		sponsorshipService;


	public AnonymousController() {
		super();
	}

	@RequestMapping(value = "/termsAndConditionsEN", method = RequestMethod.GET)
	public ModelAndView listEN(HttpServletRequest request) {
		try {
			ModelAndView result;

			Boolean isMessageBroadcasted = this.configurationService.isRebrandingBroadcasted();
			String imageURL = this.configurationService.getConfiguration().getImageURL();

			request.getSession().setAttribute("imageURL", imageURL);
			request.getSession().setAttribute("isMessageBroadcasted", isMessageBroadcasted);
			result = new ModelAndView("termsAndConditionsEN");

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}
	}

	@RequestMapping(value = "/termsAndConditionsES", method = RequestMethod.GET)
	public ModelAndView listES(HttpServletRequest request) {
		try {
			ModelAndView result;

			Boolean isMessageBroadcasted = this.configurationService.isRebrandingBroadcasted();
			String imageURL = this.configurationService.getConfiguration().getImageURL();

			request.getSession().setAttribute("imageURL", imageURL);
			request.getSession().setAttribute("isMessageBroadcasted", isMessageBroadcasted);
			result = new ModelAndView("termsAndConditionsES");

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}
	}

	// CREATE
	// ROOKIE-----------------------------------------------------------------------

	@RequestMapping(value = "/rookie/create", method = RequestMethod.GET)
	public ModelAndView createAdmin(HttpServletRequest request) {
		try {
			ModelAndView result;

			FormObjectRookie formObjectRookie = new FormObjectRookie();
			formObjectRookie.setTermsAndConditions(false);

			Boolean isMessageBroadcasted = this.configurationService.isRebrandingBroadcasted();
			String imageURL = this.configurationService.getConfiguration().getImageURL();

			request.getSession().setAttribute("imageURL", imageURL);
			request.getSession().setAttribute("isMessageBroadcasted", isMessageBroadcasted);

			result = new ModelAndView("anonymous/rookie/create");

			result = this.createEditModelAndView(formObjectRookie);

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/anonymous/rookie/create.do");
		}
	}

	@RequestMapping(value = "/rookie/create", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@Valid FormObjectRookie formObjectRookie, BindingResult binding) {
		try {
			ModelAndView result;

			Rookie rookie = new Rookie();
			rookie = this.rookieService.createRookie();

			Configuration configuration = this.configurationService.getConfiguration();
			String prefix = configuration.getSpainTelephoneCode();

			// Reconstruccion
			rookie = this.rookieService.reconstruct(formObjectRookie, binding);

			if (binding.hasErrors())
				result = this.createEditModelAndView(formObjectRookie);
			else
				try {

					if (rookie.getPhone().matches("([0-9]{4,})$"))
						rookie.setPhone(prefix + rookie.getPhone());
					this.rookieService.save(rookie);

					result = new ModelAndView("redirect:/security/login.do");

				} catch (Throwable oops) {
					result = this.createEditModelAndView(formObjectRookie, "company.duplicated.user");

				}
			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/anonymous/rookie/create.do");
		}
	}

	protected ModelAndView createEditModelAndView(FormObjectRookie formObjectRookie) {
		ModelAndView result;

		result = this.createEditModelAndView(formObjectRookie, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(FormObjectRookie formObjectRookie, String messageCode) {
		ModelAndView result;

		String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();
		List<String> cardType = this.configurationService.getConfiguration().getCardType();

		result = new ModelAndView("anonymous/rookie/create");
		result.addObject("formObjectRookie", formObjectRookie);
		result.addObject("message", messageCode);
		result.addObject("locale", locale);
		result.addObject("cardType", cardType);

		return result;
	}

	// END OF CREATE
	// ROOKIE-----------------------------------------------------------------------

	// CREATE
	// COMPANY-----------------------------------------------------------------------

	@RequestMapping(value = "/company/create", method = RequestMethod.GET)
	public ModelAndView createCompany(HttpServletRequest request) {
		try {
			ModelAndView result;

			FormObjectCompany formObjectCompany = new FormObjectCompany();
			formObjectCompany.setTermsAndConditions(false);

			Boolean isMessageBroadcasted = this.configurationService.isRebrandingBroadcasted();
			String imageURL = this.configurationService.getConfiguration().getImageURL();

			request.getSession().setAttribute("imageURL", imageURL);
			request.getSession().setAttribute("isMessageBroadcasted", isMessageBroadcasted);

			result = new ModelAndView("anonymous/company/create");

			result = this.createEditModelAndView(formObjectCompany);

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/anonymous/company/create.do");
		}
	}

	@RequestMapping(value = "/company/create", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@Valid FormObjectCompany formObjectCompany, BindingResult binding) {
		try {
			ModelAndView result;

			Company company = new Company();
			company = this.companyService.createCompany();

			Configuration configuration = this.configurationService.getConfiguration();
			String prefix = configuration.getSpainTelephoneCode();

			// Reconstruccion
			company = this.companyService.reconstruct(formObjectCompany, binding);

			if (binding.hasErrors())
				result = this.createEditModelAndView(formObjectCompany);
			else
				try {

					if (company.getPhone().matches("([0-9]{4,})$"))
						company.setPhone(prefix + company.getPhone());
					this.companyService.save(company);

					result = new ModelAndView("redirect:/security/login.do");

				} catch (Throwable oops) {
					result = this.createEditModelAndView(formObjectCompany, "company.duplicated.user");

				}
			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/anonymous/company/create.do");
		}
	}

	protected ModelAndView createEditModelAndView(FormObjectCompany formObjectCompany) {
		ModelAndView result;

		result = this.createEditModelAndView(formObjectCompany, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(FormObjectCompany formObjectCompany, String messageCode) {
		ModelAndView result;

		String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();
		List<String> cardType = this.configurationService.getConfiguration().getCardType();

		result = new ModelAndView("anonymous/company/create");
		result.addObject("formObjectCompany", formObjectCompany);
		result.addObject("message", messageCode);
		result.addObject("locale", locale);
		result.addObject("cardType", cardType);

		return result;
	}

	// END OF CREATE
	// COMPANY-----------------------------------------------------------------------

	// CREATE
	// PROVIDER-----------------------------------------------------------------------------

	@RequestMapping(value = "/provider/create", method = RequestMethod.GET)
	public ModelAndView createProvider(HttpServletRequest request) {
		try {
			ModelAndView result;

			FormObjectProvider formObjectProvider = new FormObjectProvider();
			formObjectProvider.setTermsAndConditions(false);

			Boolean isMessageBroadcasted = this.configurationService.isRebrandingBroadcasted();
			String imageURL = this.configurationService.getConfiguration().getImageURL();

			request.getSession().setAttribute("imageURL", imageURL);
			request.getSession().setAttribute("isMessageBroadcasted", isMessageBroadcasted);

			result = new ModelAndView("anonymous/provider/create");

			result = this.createEditModelAndView(formObjectProvider);

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/anonymous/provider/create.do");
		}
	}

	@RequestMapping(value = "/provider/create", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@Valid FormObjectProvider formObjectProvider, BindingResult binding) {
		try {
			ModelAndView result;

			Provider provider = new Provider();
			provider = this.providerService.create();

			Configuration configuration = this.configurationService.getConfiguration();
			String prefix = configuration.getSpainTelephoneCode();

			// Reconstruccion
			provider = this.providerService.reconstruct(formObjectProvider, binding);

			if (binding.hasErrors())
				result = this.createEditModelAndView(formObjectProvider);
			else
				try {

					if (provider.getPhone().matches("([0-9]{4,})$"))
						provider.setPhone(prefix + provider.getPhone());
					this.providerService.save(provider);

					result = new ModelAndView("redirect:/security/login.do");

				} catch (Throwable oops) {
					result = this.createEditModelAndView(formObjectProvider, "company.duplicated.user");

				}
			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/anonymous/provider/create.do");
		}
	}

	protected ModelAndView createEditModelAndView(FormObjectProvider formObjectProvider) {
		ModelAndView result;

		result = this.createEditModelAndView(formObjectProvider, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(FormObjectProvider formObjectProvider, String messageCode) {
		ModelAndView result;

		String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();
		List<String> cardType = this.configurationService.getConfiguration().getCardType();

		result = new ModelAndView("anonymous/provider/create");
		result.addObject("formObjectProvider", formObjectProvider);
		result.addObject("message", messageCode);
		result.addObject("locale", locale);
		result.addObject("cardType", cardType);

		return result;
	}

	// --END CREATE PROVIDER
	// --------------------------------------------------------------------

	@RequestMapping(value = "/position/list", method = RequestMethod.GET)
	public ModelAndView listPositions(HttpServletRequest request) {
		try {
			ModelAndView result;

			Boolean isMessageBroadcasted = this.configurationService.isRebrandingBroadcasted();
			String imageURL = this.configurationService.getConfiguration().getImageURL();

			request.getSession().setAttribute("imageURL", imageURL);
			request.getSession().setAttribute("isMessageBroadcasted", isMessageBroadcasted);

			List<Position> publicPositions = new ArrayList<Position>();

			publicPositions = this.companyService.AllPositionsInFinal();

			Map<Integer, Sponsorship> randomSpo = new HashMap<Integer, Sponsorship>();
			for (Position p : publicPositions)
				if (!p.getSponsorships().isEmpty()) {
					Sponsorship spo = this.sponsorshipService.getRandomSponsorship(p.getId());
					if (this.actorService.loggedAsActorBoolean()) {
						if (spo.getProvider() != null && (this.actorService.loggedActor() != spo.getProvider()))
							this.sponsorshipService.sendMessageToProvider(spo.getProvider());
					} else
						this.sponsorshipService.sendMessageToProvider(spo.getProvider());
					randomSpo.put(p.getId(), spo);
				}

			result = new ModelAndView("anonymous/position/list");
			result.addObject("randomSpo", randomSpo);
			result.addObject("publicPositions", publicPositions);
			result.addObject("requestURI", "anonymous/position/list.do");

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}
	}

	@RequestMapping(value = "/curriculum/list", method = RequestMethod.GET)
	public ModelAndView show(@RequestParam(required = false) String applicationId, HttpServletRequest request) {
		ModelAndView result;

		try {

			Assert.isTrue(StringUtils.isNumeric(applicationId));
			Boolean isMessageBroadcasted = this.configurationService.isRebrandingBroadcasted();
			String imageURL = this.configurationService.getConfiguration().getImageURL();

			request.getSession().setAttribute("imageURL", imageURL);
			request.getSession().setAttribute("isMessageBroadcasted", isMessageBroadcasted);

			int applicationIdInt = Integer.parseInt(applicationId);

			Application application = this.applicationService.findOne(applicationIdInt);

			Curriculum curriculum = application.getCurriculum();
			Assert.isTrue(application.getCurriculum().equals(curriculum));
			Boolean publicData = true;
			Boolean assignable = true;

			result = new ModelAndView("anonymous/curriculum/list");
			result.addObject("assignable", assignable);
			result.addObject("curriculum", curriculum);
			result.addObject("publicData", publicData);
			result.addObject("personalData", curriculum.getPersonalData());
			result.addObject("positionData", curriculum.getPositionData());
			result.addObject("educationData", curriculum.getEducationData());
			result.addObject("miscellaneousData", curriculum.getMiscellaneousData());
			result.addObject("requestURI", "/anonymous/curriculum/list.do");
		} catch (Throwable oops) {
			result = new ModelAndView("redirect:/anonymous/position/list.do");
		}

		return result;
	}

	@RequestMapping(value = "/problem/list", method = RequestMethod.GET)
	public ModelAndView listProblems(@RequestParam(required = false) String positionId, HttpServletRequest request) {
		try {

			Assert.isTrue(StringUtils.isNumeric(positionId));

			int positionIdInt = Integer.parseInt(positionId);

			Boolean isMessageBroadcasted = this.configurationService.isRebrandingBroadcasted();
			String imageURL = this.configurationService.getConfiguration().getImageURL();

			request.getSession().setAttribute("imageURL", imageURL);
			request.getSession().setAttribute("isMessageBroadcasted", isMessageBroadcasted);

			ModelAndView result;

			Boolean assignable = false;

			List<Problem> allProblems = new ArrayList<>();
			allProblems = this.positionService.getProblemsOfPosition(positionIdInt);
			Actor actor = this.positionService.getActorWithPosition(positionIdInt);
			Position position = this.positionService.findOne(positionIdInt);
			Assert.isTrue(position.getProblems().containsAll(allProblems));
			Assert.isTrue(position.getIsCancelled() == false && position.getIsDraftMode() == false);

			Actor loggedActor = this.actorService.loggedActor();
			Boolean sameActorLogged;
			Boolean publicData = true;

			if (loggedActor.equals(actor))
				sameActorLogged = true;
			else
				sameActorLogged = false;

			if (position.getIsCancelled() == true && position.getIsDraftMode() == false)
				result = new ModelAndView("redirect:/anonymous/position/list.do");
			else {

				result = new ModelAndView("anonymous/problem/list");

				result.addObject("problems", allProblems);
				result.addObject("publicData", publicData);
				result.addObject("sameActorLogged", sameActorLogged);
				result.addObject("requestURI", "anonymous/problem/list.do");
				result.addObject("positionId", positionId);
				result.addObject("assignable", assignable);
			}
			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/anonymous/position/list.do");
		}
	}
	@RequestMapping(value = "/attachement/list", method = RequestMethod.GET)
	public ModelAndView listAttachement(@RequestParam(required = false) String problemId, HttpServletRequest request) {
		try {

			Assert.isTrue(StringUtils.isNumeric(problemId));
			int problemIdInt = Integer.parseInt(problemId);

			Boolean isMessageBroadcasted = this.configurationService.isRebrandingBroadcasted();
			String imageURL = this.configurationService.getConfiguration().getImageURL();

			request.getSession().setAttribute("imageURL", imageURL);
			request.getSession().setAttribute("isMessageBroadcasted", isMessageBroadcasted);

			ModelAndView result;

			List<String> list = new ArrayList<String>();

			Problem problem = this.problemService.findOne(problemIdInt);

			Boolean publicData = true;
			list = problem.getAttachments();

			result = new ModelAndView("anonymous/attachement/list");

			result.addObject("attachments", list);
			result.addObject("publicData", publicData);
			result.addObject("requestURI", "anonymous/attachement/list.do");
			result.addObject("problemId", problemId);

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/anonymous/position/list.do");
		}
	}

	@RequestMapping(value = "/application/list", method = RequestMethod.GET)
	public ModelAndView listAplications(@RequestParam(required = false) String positionId, HttpServletRequest request) {
		try {

			Assert.isTrue(StringUtils.isNumeric(positionId));
			int positionIdInt = Integer.parseInt(positionId);

			Boolean isMessageBroadcasted = this.configurationService.isRebrandingBroadcasted();
			String imageURL = this.configurationService.getConfiguration().getImageURL();

			request.getSession().setAttribute("imageURL", imageURL);
			request.getSession().setAttribute("isMessageBroadcasted", isMessageBroadcasted);

			ModelAndView result;
			Boolean assignable = false;

			List<Application> allApplications = new ArrayList<Application>();
			allApplications = this.applicationService.getApplicationsCompany(positionIdInt);
			Actor actor = this.positionService.getActorWithPosition(positionIdInt);
			Position position = this.positionService.findOne(positionIdInt);
			Assert.isTrue(position.getIsCancelled() == false && position.getIsDraftMode() == false);
			Actor loggedActor = this.actorService.loggedActor();
			Boolean sameActorLogged;

			if (loggedActor.equals(actor))
				sameActorLogged = true;
			else
				sameActorLogged = false;

			if (position.getIsCancelled() == true && position.getIsDraftMode() == false)
				result = new ModelAndView("redirect:/anonymous/position/list.do");
			else {

				result = new ModelAndView("anonymous/application/list");

				result.addObject("allApplications", allApplications);
				result.addObject("sameActorLogged", sameActorLogged);
				result.addObject("requestURI", "anonymous/application/list.do");
				result.addObject("positionId", positionId);
				result.addObject("assignable", assignable);
			}
			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/anonymous/position/list.do");
		}
	}

	@RequestMapping(value = "/audit/list", method = RequestMethod.GET)
	public ModelAndView listAudits(@RequestParam(required = false) String positionId, HttpServletRequest request) {
		try {

			Assert.isTrue(StringUtils.isNumeric(positionId));
			int positionIdInt = Integer.parseInt(positionId);

			Boolean isMessageBroadcasted = this.configurationService.isRebrandingBroadcasted();
			String imageURL = this.configurationService.getConfiguration().getImageURL();

			request.getSession().setAttribute("imageURL", imageURL);
			request.getSession().setAttribute("isMessageBroadcasted", isMessageBroadcasted);

			ModelAndView result;
			Boolean assignable = true;

			List<Audit> finalAudits = new ArrayList<Audit>();
			finalAudits = this.auditService.getFinalAuditsByPosition(positionIdInt);
			Position position = this.positionService.findOne(positionIdInt);
			Assert.isTrue(position.getAudits().containsAll(finalAudits));
			Assert.isTrue(position.getIsCancelled() == false && position.getIsDraftMode() == false);

			if (position.getIsCancelled() == true && position.getIsDraftMode() == false)
				result = new ModelAndView("redirect:/anonymous/position/list.do");
			else {
				result = new ModelAndView("anonymous/audit/list");

				result.addObject("finalAudits", finalAudits);
				result.addObject("requestURI", "anonymous/audit/list.do");
				result.addObject("positionId", positionId);
				result.addObject("assignable", assignable);
			}

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/anonymous/position/list.do");
		}
	}

	@RequestMapping(value = "/company/listOne", method = RequestMethod.GET)
	public ModelAndView listCompany(@RequestParam(required = false) String positionId, HttpServletRequest request) {
		try {

			Assert.isTrue(StringUtils.isNumeric(positionId));
			int positionIdInt = Integer.parseInt(positionId);

			Boolean isMessageBroadcasted = this.configurationService.isRebrandingBroadcasted();
			String imageURL = this.configurationService.getConfiguration().getImageURL();

			request.getSession().setAttribute("imageURL", imageURL);
			request.getSession().setAttribute("isMessageBroadcasted", isMessageBroadcasted);

			ModelAndView result;
			Boolean assignable = false;
			List<SocialProfile> socialProfiles = new ArrayList<SocialProfile>();

			Company company = this.companyService.companyOfRespectivePosition(positionIdInt);
			Actor actor = this.positionService.getActorWithPosition(positionIdInt);
			Position position = this.positionService.findOne(positionIdInt);
			Assert.isTrue(position.getIsCancelled() == false && position.getIsDraftMode() == false);
			Boolean score = true;

			Actor loggedActor = this.actorService.loggedActor();
			Boolean sameActorLogged;
			socialProfiles = actor.getSocialProfiles();

			if (loggedActor.equals(actor))
				sameActorLogged = true;
			else
				sameActorLogged = false;

			Boolean publicValue = true;

			if (position.getIsCancelled() == true && position.getIsDraftMode() == false)
				result = new ModelAndView("redirect:/anonymous/position/list.do");
			else {
				result = new ModelAndView("anonymous/company/listOne");
				result.addObject("actor", company);
				result.addObject("socialProfiles", socialProfiles);
				result.addObject("score", score);
				result.addObject("publicValue", publicValue);
				result.addObject("sameActorLogged", sameActorLogged);
				result.addObject("requestURI", "anonymous/company/listOne.do");
				result.addObject("assignable", assignable);
			}
			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/anonymous/position/list.do");
		}
	}

	// SIGUIENTE DESPLEGABLE

	@RequestMapping(value = "/company/list", method = RequestMethod.GET)
	public ModelAndView listCompanies(HttpServletRequest request) {
		try {
			Boolean isMessageBroadcasted = this.configurationService.isRebrandingBroadcasted();
			String imageURL = this.configurationService.getConfiguration().getImageURL();

			request.getSession().setAttribute("imageURL", imageURL);
			request.getSession().setAttribute("isMessageBroadcasted", isMessageBroadcasted);

			ModelAndView result;

			List<Company> companies = this.companyService.allCompanies();

			result = new ModelAndView("anonymous/company/list");
			result.addObject("companies", companies);
			result.addObject("requestURI", "anonymous/company/list.do");

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/anonymous/company/list.do");
		}
	}

	@RequestMapping(value = "/company/positions", method = RequestMethod.GET)
	public ModelAndView listPositionsOfCompanies(@RequestParam(required = false) String idCompany, HttpServletRequest request) {
		try {

			Assert.isTrue(StringUtils.isNumeric(idCompany));
			int idCompanyInt = Integer.parseInt(idCompany);

			Boolean isMessageBroadcasted = this.configurationService.isRebrandingBroadcasted();
			String imageURL = this.configurationService.getConfiguration().getImageURL();

			request.getSession().setAttribute("imageURL", imageURL);
			request.getSession().setAttribute("isMessageBroadcasted", isMessageBroadcasted);

			ModelAndView result;

			List<Position> positions = this.companyService.positionsOfCompanyInFinalNotCancelled(idCompanyInt);

			Map<Integer, Sponsorship> randomSpo = new HashMap<Integer, Sponsorship>();
			for (Position p : positions)
				if (!p.getSponsorships().isEmpty()) {
					Sponsorship spo = this.sponsorshipService.getRandomSponsorship(p.getId());
					if (this.actorService.loggedAsActorBoolean())
						if (spo.getProvider() != null && (this.providerService.loggedProvider() != spo.getProvider()))
							this.sponsorshipService.sendMessageToProvider(spo.getProvider());
					randomSpo.put(p.getId(), spo);
				}

			result = new ModelAndView("anonymous/company/positions");
			result.addObject("publicPositionsSize", positions.size());
			result.addObject("publicPositions", positions);
			result.addObject("randomSpo", randomSpo);
			result.addObject("requestURI", "anonymous/company/positions.do");

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/anonymous/company/list.do");
		}
	}

	@RequestMapping(value = "/filtered/create", method = RequestMethod.GET)
	public ModelAndView newWord(HttpServletRequest request) {
		try {
			Boolean isMessageBroadcasted = this.configurationService.isRebrandingBroadcasted();
			String imageURL = this.configurationService.getConfiguration().getImageURL();

			request.getSession().setAttribute("imageURL", imageURL);
			request.getSession().setAttribute("isMessageBroadcasted", isMessageBroadcasted);

			ModelAndView result;

			List<Position> filteredPositions = new ArrayList<Position>();
			filteredPositions = this.companyService.AllPositionsInFinal();

			result = new ModelAndView("anonymous/filtered/positions");
			result.addObject("publicPositions", filteredPositions);
			result.addObject("requestURI", "anonymous/filtered/positions.do");

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/anonymous/filtered/positions.do");
		}
	}

	@RequestMapping(value = "/filtered/positions", method = RequestMethod.POST, params = "save")
	public ModelAndView listPositionsOfCompany(String word, HttpServletRequest request) {
		try {
			Boolean isMessageBroadcasted = this.configurationService.isRebrandingBroadcasted();
			String imageURL = this.configurationService.getConfiguration().getImageURL();

			request.getSession().setAttribute("imageURL", imageURL);
			request.getSession().setAttribute("isMessageBroadcasted", isMessageBroadcasted);

			ModelAndView result;
			List<Position> filteredPositions = new ArrayList<Position>();

			filteredPositions = this.positionService.positionsFiltered(word);

			Map<Integer, Sponsorship> randomSpo = new HashMap<Integer, Sponsorship>();
			for (Position p : filteredPositions) {
				Assert.isTrue(p.getIsCancelled() == false && p.getIsDraftMode() == false);
				if (!p.getSponsorships().isEmpty()) {
					Sponsorship spo = this.sponsorshipService.getRandomSponsorship(p.getId());
					if (this.actorService.loggedAsActorBoolean())
						if (spo.getProvider() != null && (this.providerService.loggedProvider() != spo.getProvider()))
							this.sponsorshipService.sendMessageToProvider(spo.getProvider());
					randomSpo.put(p.getId(), spo);
				}
			}

			result = new ModelAndView("anonymous/filtered/positions");
			result.addObject("publicPositions", filteredPositions);
			result.addObject("randomSpo", randomSpo);
			result.addObject("requestURI", "anonymous/filtered/positions.do");

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/anonymous/filtered/positions.do");
		}
	}

	// PROVIDERS
	@RequestMapping(value = "/provider/list", method = RequestMethod.GET)
	public ModelAndView listProviders(HttpServletRequest request) {
		try {
			Boolean isMessageBroadcasted = this.configurationService.isRebrandingBroadcasted();
			String imageURL = this.configurationService.getConfiguration().getImageURL();

			request.getSession().setAttribute("imageURL", imageURL);
			request.getSession().setAttribute("isMessageBroadcasted", isMessageBroadcasted);

			ModelAndView result;

			List<Provider> providers = this.providerService.findAll();

			result = new ModelAndView("anonymous/provider/list");
			result.addObject("providers", providers);
			result.addObject("requestURI", "anonymous/provider/list.do");

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/anonymous/provider/list.do");
		}
	}

	// Items
	@RequestMapping(value = "/item/list", method = RequestMethod.GET)
	public ModelAndView itemList(@RequestParam(required = false) Integer providerId, HttpServletRequest request) {
		try {

			Boolean isMessageBroadcasted = this.configurationService.isRebrandingBroadcasted();
			String imageURL = this.configurationService.getConfiguration().getImageURL();

			request.getSession().setAttribute("imageURL", imageURL);
			request.getSession().setAttribute("isMessageBroadcasted", isMessageBroadcasted);

			ModelAndView result;
			List<Item> items;
			Boolean publicData = false;

			if (providerId == null)
				items = this.itemService.findAll();
			else {
				items = this.itemService.getItemsFromProvider(providerId);
				Provider provider = this.providerService.findOne(providerId);
				Assert.isTrue(provider.getItems().containsAll(items));
				publicData = true;
			}
			Map<Item, Provider> providersByItem = this.itemService.getProvidersByItem(items);

			result = new ModelAndView("anonymous/item/list");
			result.addObject("publicData", publicData);
			result.addObject("items", items);
			result.addObject("providersByItem", providersByItem);
			result.addObject("requestURI", "anonymous/item/list.do");

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/anonymous/provider/list.do");
		}
	}

	@RequestMapping(value = "/item/listLinks", method = RequestMethod.GET)
	public ModelAndView listLinks(@RequestParam(required = false) String itemId, HttpServletRequest request) {
		try {

			Assert.isTrue(StringUtils.isNumeric(itemId));
			int itemIdInt = Integer.parseInt(itemId);

			Boolean isMessageBroadcasted = this.configurationService.isRebrandingBroadcasted();
			String imageURL = this.configurationService.getConfiguration().getImageURL();

			request.getSession().setAttribute("imageURL", imageURL);
			request.getSession().setAttribute("isMessageBroadcasted", isMessageBroadcasted);

			ModelAndView result;

			List<String> links = this.itemService.findOne(itemIdInt).getLinks();

			result = new ModelAndView("anonymous/item/listLinks");
			result.addObject("links", links);

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/anonymous/provider/list.do");
		}
	}

	@RequestMapping(value = "/item/listPictures", method = RequestMethod.GET)
	public ModelAndView listPictures(@RequestParam(required = false) String itemId, HttpServletRequest request) {
		try {

			Assert.isTrue(StringUtils.isNumeric(itemId));
			int itemIdInt = Integer.parseInt(itemId);

			Boolean isMessageBroadcasted = this.configurationService.isRebrandingBroadcasted();
			String imageURL = this.configurationService.getConfiguration().getImageURL();

			request.getSession().setAttribute("imageURL", imageURL);
			request.getSession().setAttribute("isMessageBroadcasted", isMessageBroadcasted);
			ModelAndView result;

			List<String> pictures = this.itemService.findOne(itemIdInt).getPictures();

			result = new ModelAndView("anonymous/item/listPictures");
			result.addObject("pictures", pictures);

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/anonymous/provider/list.do");
		}
	}

	@RequestMapping(value = "/provider/listOne", method = RequestMethod.GET)
	public ModelAndView listProvider(@RequestParam(required = false) String providerId, HttpServletRequest request) {
		try {

			Assert.isTrue(StringUtils.isNumeric(providerId));
			int providerIdInt = Integer.parseInt(providerId);

			Boolean isMessageBroadcasted = this.configurationService.isRebrandingBroadcasted();
			String imageURL = this.configurationService.getConfiguration().getImageURL();

			request.getSession().setAttribute("imageURL", imageURL);
			request.getSession().setAttribute("isMessageBroadcasted", isMessageBroadcasted);

			ModelAndView result;
			List<SocialProfile> socialProfiles = new ArrayList<SocialProfile>();

			Provider provider = this.providerService.findOne(providerIdInt);
			List<Item> items = provider.getItems();

			Actor loggedActor = this.actorService.loggedActor();
			Boolean sameActorLogged;
			socialProfiles = provider.getSocialProfiles();

			if (loggedActor.equals(provider))
				sameActorLogged = true;
			else
				sameActorLogged = false;

			Boolean itemValues = true;

			result = new ModelAndView("anonymous/provider/listOne");
			result.addObject("actor", provider);
			result.addObject("socialProfiles", socialProfiles);
			result.addObject("itemValues", itemValues);
			result.addObject("sameActorLogged", sameActorLogged);
			result.addObject("items", items);
			result.addObject("requestURI", "anonymous/provider/listOne.do");

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/anonymous/provider/list");
		}
	}

}
