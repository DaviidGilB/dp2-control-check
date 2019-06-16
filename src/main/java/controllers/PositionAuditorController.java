
package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.ActorService;
import services.ApplicationService;
import services.AuditService;
import services.AuditorService;
import services.CompanyService;
import services.PositionService;
import services.SponsorshipService;
import domain.Actor;
import domain.Application;
import domain.Audit;
import domain.Company;
import domain.Position;
import domain.Problem;
import domain.SocialProfile;
import domain.Sponsorship;

@Controller
@RequestMapping("/position/auditor")
public class PositionAuditorController extends AbstractController {

	@Autowired
	private AuditorService		auditorService;

	@Autowired
	private SponsorshipService	sponsorshipService;

	@Autowired
	private AuditService		auditService;

	@Autowired
	private PositionService		positionService;

	@Autowired
	private ActorService		actorService;

	@Autowired
	private ApplicationService	applicationService;

	@Autowired
	private CompanyService		companyService;


	public PositionAuditorController() {
		super();
	}

	// -------------------------------------------------------------------
	// ---------------------------LIST------------------------------------

	// Listar Positions
	@RequestMapping(value = "/listAssignablePositions", method = RequestMethod.GET)
	public ModelAndView list() {
		try {
			ModelAndView result;
			List<Position> positions;

			positions = this.auditorService.showAssignablePositions();

			Map<Integer, Sponsorship> randomSpo = new HashMap<Integer, Sponsorship>();
			for (Position p : positions)
				if (!p.getSponsorships().isEmpty()) {
					Sponsorship spo = this.sponsorshipService.getRandomSponsorship(p.getId());
					this.sponsorshipService.sendMessageToProvider(spo.getProvider());
					randomSpo.put(p.getId(), spo);
				}

			result = new ModelAndView("position/auditor/listAssignablePositions");
			result.addObject("randomSpo", randomSpo);
			result.addObject("positions", positions);
			result.addObject("requestURI", "position/auditor/listAssignablePositions.do");

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/position/auditor/listAssignablePositions.do");
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
			Boolean assignable = false;

			if (position.getIsCancelled() == true && position.getIsDraftMode() == false)
				result = new ModelAndView("redirect:/position/auditor/listAssignablePositions.do");
			else {
				result = new ModelAndView("position/auditor/audit/list");

				result.addObject("finalAudits", finalAudits);
				result.addObject("requestURI", "position/auditor/audit/list.do");
				result.addObject("positionId", positionIdInt);
				result.addObject("assignable", assignable);

			}

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/position/auditor/audit/list.do");
		}
	}

	@RequestMapping(value = "/problem/list", method = RequestMethod.GET)
	public ModelAndView listProblems(@RequestParam(required = false) String positionId, HttpServletRequest request) {
		try {

			Assert.isTrue(StringUtils.isNumeric(positionId));
			int positionIdInt = Integer.parseInt(positionId);

			ModelAndView result;

			Boolean assignable = true;

			List<Problem> allProblems = new ArrayList<>();
			allProblems = this.positionService.getProblemsOfPosition(positionIdInt);
			Actor actor = this.positionService.getActorWithPosition(positionIdInt);
			Position position = this.positionService.findOne(positionIdInt);
			Assert.isTrue(position.getProblems().containsAll(allProblems));

			Actor loggedActor = this.actorService.loggedActor();
			Boolean sameActorLogged;
			Boolean publicData = true;

			if (loggedActor.equals(actor))
				sameActorLogged = true;
			else
				sameActorLogged = false;

			if (position.getIsCancelled() == true && position.getIsDraftMode() == false)
				result = new ModelAndView("redirect:/position/auditor/listAssignablePositions.do");
			else {

				result = new ModelAndView("position/auditor/problem/list");

				result.addObject("problems", allProblems);
				result.addObject("publicData", publicData);
				result.addObject("sameActorLogged", sameActorLogged);
				result.addObject("requestURI", "position/auditor/problem/list.do");
				result.addObject("positionId", positionIdInt);
				result.addObject("assignable", assignable);
			}
			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/position/auditor/audit/list.do");
		}
	}

	@RequestMapping(value = "/application/list", method = RequestMethod.GET)
	public ModelAndView listAplications(@RequestParam(required = false) String positionId, HttpServletRequest request) {
		try {

			Assert.isTrue(StringUtils.isNumeric(positionId));
			int positionIdInt = Integer.parseInt(positionId);

			ModelAndView result;
			Boolean assignable = false;

			List<Application> allApplications = new ArrayList<Application>();
			allApplications = this.applicationService.getApplicationsCompany(positionIdInt);
			Actor actor = this.positionService.getActorWithPosition(positionIdInt);
			Position position = this.positionService.findOne(positionIdInt);
			Actor loggedActor = this.actorService.loggedActor();
			Boolean sameActorLogged;

			if (loggedActor.equals(actor))
				sameActorLogged = true;
			else
				sameActorLogged = false;

			if (position.getIsCancelled() == true && position.getIsDraftMode() == false)
				result = new ModelAndView("redirect:/position/auditor/listAssignablePositions.do");
			else {

				result = new ModelAndView("position/auditor/application/list");

				result.addObject("allApplications", allApplications);
				result.addObject("sameActorLogged", sameActorLogged);
				result.addObject("requestURI", "position/auditor/application/list.do");
				result.addObject("positionId", positionIdInt);
				result.addObject("assignable", assignable);
			}
			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/position/auditor/audit/list.do");
		}
	}

	@RequestMapping(value = "/company/listOne", method = RequestMethod.GET)
	public ModelAndView listCompany(@RequestParam(required = false) String positionId, HttpServletRequest request) {
		try {

			Assert.isTrue(StringUtils.isNumeric(positionId));
			int positionIdInt = Integer.parseInt(positionId);

			ModelAndView result;
			Boolean assignable = true;
			List<SocialProfile> socialProfiles = new ArrayList<SocialProfile>();

			Company company = this.companyService.companyOfRespectivePosition(positionIdInt);
			Actor actor = this.positionService.getActorWithPosition(positionIdInt);
			Position position = this.positionService.findOne(positionIdInt);
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
				result = new ModelAndView("redirect:/position/auditor/listAssignablePositions.do");
			else {
				result = new ModelAndView("position/auditor/company/listOne");
				result.addObject("actor", company);
				result.addObject("socialProfiles", socialProfiles);
				result.addObject("score", score);
				result.addObject("publicValue", publicValue);
				result.addObject("sameActorLogged", sameActorLogged);
				result.addObject("requestURI", "position/auditor/company/listOne.do");
				result.addObject("assignable", assignable);
			}
			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/position/auditor/audit/list.do");
		}
	}

}
