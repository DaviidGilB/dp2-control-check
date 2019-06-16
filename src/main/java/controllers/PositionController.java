
package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import services.ActorService;
import services.ApplicationService;
import services.AuditService;
import services.CompanyService;
import services.CurriculumService;
import services.FinderService;
import services.PositionService;
import services.ProblemService;
import services.SponsorshipService;
import domain.Actor;
import domain.Application;
import domain.Audit;
import domain.Company;
import domain.Curriculum;
import domain.Position;
import domain.Problem;
import domain.Sponsorship;
import domain.Status;
import forms.FormObjectPositionProblemCheckbox;

@Controller
@RequestMapping("/position/company")
public class PositionController extends AbstractController {

	@Autowired
	private CompanyService		companyService;

	@Autowired
	private PositionService		positionService;

	@Autowired
	private ProblemService		problemService;

	@Autowired
	private ApplicationService	applicationService;

	@Autowired
	private ActorService		actorService;

	@Autowired
	private FinderService		finderService;

	@Autowired
	private CurriculumService	curriculumService;

	@Autowired
	private SponsorshipService	sponsorshipService;

	@Autowired
	private AuditService		auditService;


	public PositionController() {
		super();
	}

	// -------------------------------------------------------------------
	// ---------------------------LIST------------------------------------

	// Listar Positions
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list() {
		try {
			ModelAndView result;
			List<Position> positions;

			Company loggedCompany = this.companyService.loggedCompany();

			positions = loggedCompany.getPositions();

			Map<Integer, Sponsorship> randomSpo = new HashMap<Integer, Sponsorship>();
			for (Position p : positions)
				if (!p.getSponsorships().isEmpty()) {
					Sponsorship spo = this.sponsorshipService.getRandomSponsorship(p.getId());
					this.sponsorshipService.sendMessageToProvider(spo.getProvider());
					randomSpo.put(p.getId(), spo);
				}

			result = new ModelAndView("position/company/list");
			result.addObject("randomSpo", randomSpo);
			result.addObject("positions", positions);
			result.addObject("requestURI", "position/company/list.do");

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/position/company/list.do");
		}
	}

	// --------------------------LISTA DE PROBLEMAS----------------------------
	// ------------------------------------------------------------------------
	@RequestMapping(value = "/problem/list", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam(required = false) String positionId) {
		ModelAndView result;
		try {
			Assert.isTrue(StringUtils.isNumeric(positionId));
			int positionIdInt = Integer.parseInt(positionId);

			Company loggedCompany = this.companyService.loggedCompany();

			List<Problem> allProblems = new ArrayList<>();

			Position position = this.positionService.findOne(positionIdInt);

			allProblems = position.getProblems();

			result = new ModelAndView("problemPosition/company/list");

			result.addObject("allProblems", allProblems);
			result.addObject("requestURI", "problem/company/list.do");
			result.addObject("positionId", positionIdInt);

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/position/company/list.do");
		}
	}

	// --------------------------LISTA DE APPLICATIONS----------------------------
	// ---------------------------------------------------------------------------
	@RequestMapping(value = "/application/list", method = RequestMethod.GET)
	public ModelAndView listApplication(@RequestParam(required = false) String positionId) {
		ModelAndView result;
		try {
			Assert.isTrue(StringUtils.isNumeric(positionId));
			int positionIdInt = Integer.parseInt(positionId);

			Company loggedCompany = this.companyService.loggedCompany();

			List<Application> allApplications = new ArrayList<>();

			Position position = this.positionService.findOne(positionIdInt);

			if (position.getIsDraftMode())
				return this.list();

			allApplications = this.applicationService.getApplicationsCompany(positionIdInt);
			Actor actor = this.positionService.getActorWithPosition(position.getId());

			Actor loggedActor = this.actorService.loggedActor();
			Boolean sameActorLogged;

			if (loggedActor.equals(actor))
				sameActorLogged = true;
			else
				sameActorLogged = false;

			result = new ModelAndView("applicationPosition/company/list");

			result.addObject("allApplications", allApplications);
			result.addObject("sameActorLogged", sameActorLogged);
			result.addObject("requestURI", "application/company/list.do");
			result.addObject("positionId", positionIdInt);

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/position/company/list.do");
		}
	}

	// ------------------------------SHOW
	// CURRICULUM------------------------------------
	// ---------------------------------------------------------------------------------
	@RequestMapping(value = "/curriculum/list", method = RequestMethod.GET)
	public ModelAndView show(@RequestParam(required = false) String applicationId) {
		ModelAndView result;

		try {
			Assert.isTrue(StringUtils.isNumeric(applicationId));
			int applicationIdInt = Integer.parseInt(applicationId);

			Company loggedCompany = this.companyService.loggedCompany();
			Application application = this.applicationService.findOne(applicationIdInt);
			Position position = application.getPosition();
			if (!loggedCompany.getPositions().contains(position))
				return this.list();

			Application application2 = this.applicationService.findOneWithAssert(applicationIdInt);
			Curriculum curriculum = application2.getCurriculum();

			result = new ModelAndView("company/curriculum");
			result.addObject("curriculum", curriculum);
			result.addObject("personalData", curriculum.getPersonalData());
			result.addObject("positionData", curriculum.getPositionData());
			result.addObject("educationData", curriculum.getEducationData());
			result.addObject("miscellaneousData", curriculum.getMiscellaneousData());
			result.addObject("requestURI", "/curriculum/rookie/show.do");
		} catch (Throwable oops) {
			result = new ModelAndView("redirect:list.do");
		}

		return result;
	}

	// ------------------------------EDIT APPLICATION
	// STATUS----------------------------
	// ---------------------------------------------------------------------------------
	// ACCEPT APPLICATION
	@RequestMapping(value = "/application/accept", method = RequestMethod.GET)
	public ModelAndView acceptApplication(@RequestParam(required = false) String applicationId) {
		ModelAndView result;
		try {
			Assert.isTrue(StringUtils.isNumeric(applicationId));
			int applicationIdInt = Integer.parseInt(applicationId);

			Application application;
			application = this.applicationService.findOne(applicationIdInt);
			Position position = application.getPosition();
			Company company = this.companyService.loggedCompany();
			List<Position> positions = company.getPositions();

			if (application.getStatus() != Status.SUBMITTED)
				return this.list();

			if (!(company.getPositions().contains(position)))
				return this.list();

			this.applicationService.editApplicationCompany(application, true);

			return this.list();
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}
	}

	// REJECT APPLICATION
	@RequestMapping(value = "/application/reject", method = RequestMethod.GET)
	public ModelAndView rejectApplication(@RequestParam(required = false) String applicationId) {
		ModelAndView result;
		try {
			Assert.isTrue(StringUtils.isNumeric(applicationId));
			int applicationIdInt = Integer.parseInt(applicationId);

			Application application;
			application = this.applicationService.findOne(applicationIdInt);
			Position position = application.getPosition();
			Company company = this.companyService.loggedCompany();
			List<Position> positions = company.getPositions();

			if (application.getStatus() != Status.SUBMITTED)
				return this.list();

			if (!(company.getPositions().contains(position)))
				return this.list();

			this.applicationService.editApplicationCompany(application, false);

			return this.list();
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}
	}

	// --------------------------LISTA DE ATTACHEMENTS----------------------------
	// ---------------------------------------------------------------------------
	@RequestMapping(value = "/attachement/list", method = RequestMethod.GET)
	public ModelAndView listAttachement(@RequestParam(required = false) String problemId) {
		try {

			Assert.isTrue(StringUtils.isNumeric(problemId));
			int problemIdInt = Integer.parseInt(problemId);

			ModelAndView result;

			List<String> list;

			Company loggedCompany = this.companyService.loggedCompany();

			Problem problem = this.problemService.findOne(problemIdInt);

			if (!loggedCompany.getProblems().contains(problem))
				return this.list();

			list = problem.getAttachments();

			result = new ModelAndView("problemPosition/company/attachement/list");

			result.addObject("list", list);
			result.addObject("requestURI", "problemPosition/company/attachement/list.do");
			result.addObject("problemId", problemIdInt);

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/position/company/list.do");
		}
	}

	// ---------------------------REQUIRED TECH-----------------------------------
	// ---------------------------------------------------------------------------
	// LIST
	@RequestMapping(value = "/technology/list", method = RequestMethod.GET)
	public ModelAndView listTech(@RequestParam(required = false) String positionId) {

		ModelAndView result;
		try {
			Assert.isTrue(StringUtils.isNumeric(positionId));
			int positionIdInt = Integer.parseInt(positionId);

			List<String> list;

			Company loggedCompany = this.companyService.loggedCompany();

			Position position = this.positionService.findOne(positionIdInt);

			if (!loggedCompany.getPositions().contains(position))
				return this.list();

			list = position.getRequiredTecnologies();

			result = new ModelAndView("position/company/technology/list");

			result.addObject("list", list);
			result.addObject("requestURI", "position/company/technology/list.do");
			result.addObject("positionId", positionIdInt);

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/position/company/list.do");
		}
	}

	@RequestMapping(value = "/audit/list", method = RequestMethod.GET)
	public ModelAndView listAudits(@RequestParam(required = false) String positionId) {
		try {
			Assert.isTrue(StringUtils.isNumeric(positionId));
			int positionIdInt = Integer.parseInt(positionId);

			ModelAndView result;

			List<Audit> finalAudits = new ArrayList<Audit>();
			finalAudits = this.auditService.getFinalAuditsByPosition(positionIdInt);
			Position position = this.positionService.findOne(positionIdInt);
			Assert.isTrue(position.getAudits().containsAll(finalAudits));

			if (position.getIsCancelled() == true && position.getIsDraftMode() == false)
				result = new ModelAndView("redirect:/position/company/application/list.do");
			else {
				result = new ModelAndView("position/company/audit/list");

				result.addObject("finalAudits", finalAudits);
				result.addObject("requestURI", "position/company/audit/list.do");
				result.addObject("positionId", positionIdInt);

			}

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/position/company/list.do");
		}
	}

	// ---------------------------REQUIRED SKILL-----------------------------
	// ----------------------------------------------------------------------
	// LIST
	@RequestMapping(value = "/skill/list", method = RequestMethod.GET)
	public ModelAndView listSkill(@RequestParam(required = false) String positionId) {

		ModelAndView result;
		try {

			Assert.isTrue(StringUtils.isNumeric(positionId));
			int positionIdInt = Integer.parseInt(positionId);
			List<String> list;

			Company loggedCompany = this.companyService.loggedCompany();

			Position position = this.positionService.findOne(positionIdInt);

			if (!loggedCompany.getPositions().contains(position))
				return this.list();

			list = position.getRequiredSkills();

			result = new ModelAndView("position/company/skill/list");

			result.addObject("list", list);
			result.addObject("requestURI", "position/company/skill/list.do");
			result.addObject("positionId", positionIdInt);

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/position/company/list.do");
		}
	}

	// CREATE POSITION
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView createPosition() {
		try {
			ModelAndView result;
			FormObjectPositionProblemCheckbox formObjectPositionProblemCheckbox = new FormObjectPositionProblemCheckbox();

			List<Integer> problems = new ArrayList<>();
			formObjectPositionProblemCheckbox.setProblems(problems);

			result = this.createEditModelAndView(formObjectPositionProblemCheckbox);

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/position/company/list.do");
		}
	}

	// EDIT POSITION
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam(required = false) String positionId) {
		ModelAndView result;
		try {
			Assert.isTrue(StringUtils.isNumeric(positionId));
			int positionIdInt = Integer.parseInt(positionId);

			Position position;
			position = this.positionService.findOne(positionIdInt);
			Company company = this.companyService.loggedCompany();

			if (company.getPositions().contains(position)) {

				if (!position.getIsDraftMode() || position.getIsCancelled())
					return this.list();

				if (!(company.getPositions().contains(position)))
					return this.list();

				FormObjectPositionProblemCheckbox formObjectPositionProblemCheckbox = this.positionService.prepareFormObjectPositionProblemCheckbox(positionIdInt);

				result = this.createEditModelAndView(formObjectPositionProblemCheckbox);

			} else
				result = new ModelAndView("redirect:list.do");

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/position/company/list.do");
		}
	}

	// CANCEL POSITION
	@RequestMapping(value = "/cancel", method = RequestMethod.GET)
	public ModelAndView cancel(@RequestParam(required = false) String positionId) {
		ModelAndView result;
		try {
			Assert.isTrue(StringUtils.isNumeric(positionId));
			int positionIdInt = Integer.parseInt(positionId);

			Position position;
			position = this.positionService.findOne(positionIdInt);
			Company company = this.companyService.loggedCompany();

			if (position.getIsDraftMode() || position.getIsCancelled())
				return this.list();

			if (!(company.getPositions().contains(position)))
				return this.list();

			this.positionService.cancelPosition(position);

			return this.list();
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/position/company/list.do");
		}
	}

	// SAVE POSITION
	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@Valid FormObjectPositionProblemCheckbox formObjectPositionProblemCheckbox, BindingResult binding) {

		ModelAndView result;
		try {
			Position position = new Position();
			position = this.positionService.createPosition();
			List<Problem> problems = new ArrayList<>();
			Position positionSaved = new Position();

			problems = this.problemService.reconstructList(formObjectPositionProblemCheckbox);
			position = this.positionService.reconstructCheckBox(formObjectPositionProblemCheckbox, binding);
			Boolean errorProblems = false;

			if (formObjectPositionProblemCheckbox.getIsDraftMode() != null)
				if (!formObjectPositionProblemCheckbox.getIsDraftMode())
					errorProblems = !(problems.size() >= 2);

			if (binding.hasErrors() || errorProblems) {
				result = this.createEditModelAndView(position);
				if (errorProblems)
					result.addObject("message", "position.problemsError");
			} else
				try {
					positionSaved = this.positionService.saveAssignList(position, problems);
					if (positionSaved.getIsDraftMode() == false)
						this.finderService.sendNotificationPosition(position);
					result = new ModelAndView("redirect:/position/company/list.do");

				} catch (Throwable oops) {

					result = this.createEditModelAndView(position, "commit.error");

				}
			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/position/company/list.do");
		}
	}

	// MODEL AND VIEW POSITION CHECKBOX
	protected ModelAndView createEditModelAndView(FormObjectPositionProblemCheckbox formObjectPositionProblemCheckbox) {
		ModelAndView result;

		result = this.createEditModelAndView(formObjectPositionProblemCheckbox, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(FormObjectPositionProblemCheckbox formObjectPositionProblemCheckbox, String messageCode) {
		ModelAndView result;

		Map<Integer, String> map = new HashMap<>();

		map = this.positionService.getMapAvailableProblems();

		result = new ModelAndView("position/company/create");
		result.addObject("formObjectPositionProblemCheckbox", formObjectPositionProblemCheckbox);
		result.addObject("message", messageCode);
		result.addObject("map", map);
		result.addObject("positionId", formObjectPositionProblemCheckbox.getId());

		return result;
	}

	// MODEL AND VIEW POSITION
	protected ModelAndView createEditModelAndView(Position position) {
		ModelAndView result;

		result = this.createEditModelAndView(position, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(Position position, String messageCode) {
		ModelAndView result;

		Map<Integer, String> map = new HashMap<>();

		map = this.positionService.getMapAvailableProblems();

		result = new ModelAndView("position/company/create");
		result.addObject("position", position);
		result.addObject("message", messageCode);
		result.addObject("map", map);
		result.addObject("positionId", position.getId());

		return result;
	}

	// -------------------------------------------------------------------
	// ---------------------------DELETE----------------------------------
	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "delete")
	public ModelAndView delete(FormObjectPositionProblemCheckbox formObjectPositionProblemCheckbox, BindingResult binding) {

		ModelAndView result;

		try {
			this.positionService.deletePositionWithId(formObjectPositionProblemCheckbox.getId());
			result = new ModelAndView("redirect:/position/company/list.do");
		} catch (Throwable oops) {
			result = this.createEditModelAndView(formObjectPositionProblemCheckbox, "commit.error");
		}
		return result;
	}

}
