
package controllers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.ApplicationService;
import services.MessageService;
import services.PositionService;
import services.RookieService;
import domain.Application;
import domain.Curriculum;
import domain.Position;
import domain.Rookie;
import domain.Status;

@Controller
@RequestMapping("/application/rookie")
public class ApplicationRookieController extends AbstractController {

	@Autowired
	private ApplicationService	applicationService;

	@Autowired
	private RookieService		rookieService;

	@Autowired
	private PositionService		positionService;

	@Autowired
	private MessageService		messageService;


	public ApplicationRookieController() {
		super();
	}

	// ----------------------------LIST------------------------------------

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list() {
		try {
			ModelAndView result;

			Rookie rookie = this.rookieService.loggedRookie();

			List<Application> applications = new ArrayList<Application>();

			applications = (List<Application>) this.applicationService.getApplicationsByRookie(rookie);

			result = new ModelAndView("application/rookie/list");

			result.addObject("applications", applications);
			result.addObject("requestURI", "application/rookie/list.do");

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}

	}

	@RequestMapping(value = "/filter", method = {
		RequestMethod.POST, RequestMethod.GET
	}, params = "refresh")
	public ModelAndView applicationsFilter(@RequestParam String fselect) {
		ModelAndView result;
		try {
			if (fselect.equals("ALL"))
				result = new ModelAndView("redirect:list.do");
			else {

				Status status = Status.ACCEPTED;
				if (fselect.equals("PENDING"))
					status = Status.PENDING;
				else if (fselect.equals("REJECTED"))
					status = Status.REJECTED;
				else if (fselect.equals("SUBMITTED"))
					status = Status.SUBMITTED;

				Rookie loggedRookie = this.rookieService.loggedRookie();
				List<Application> applications = (List<Application>) this.applicationService.getApplicationsByRookieAndStatus(loggedRookie, status);

				result = new ModelAndView("application/rookie/list");

				result.addObject("applications", applications);
				result.addObject("requestURI", "application/rookie/filter.do");
			}

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}
	}

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView createApplication() {
		try {
			ModelAndView result = null;
			Boolean res = false;

			Rookie rookie = this.rookieService.loggedRookie();

			if (rookie.getCurriculums().isEmpty() || this.positionService.getFinalPositions().isEmpty()) {
				result = this.list();
				res = true;
				result.addObject("res", res);
				return result;
			} else {

				List<Position> positions = this.positionService.getFinalPositionsAndNotCancelled();

				Application application = new Application();

				result = this.createEditModelAndView(application);
				result.addObject("application", application);
				result.addObject("curriculums", rookie.getCurriculums());
				result.addObject("positions", positions);

				return result;
			}
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}
	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView editApplication(@RequestParam(required = false) String applicationId) {
		try {

			Assert.isTrue(StringUtils.isNumeric(applicationId));
			int applicationIdInt = Integer.parseInt(applicationId);

			ModelAndView result;

			Rookie h = this.rookieService.loggedRookie();

			Application application = this.applicationService.findOne(applicationIdInt);

			if (h.getApplications().contains(application) && application.getStatus().equals(Status.PENDING))
				result = this.createEditModelAndView(application);
			else
				result = new ModelAndView("redirect:list.do");

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/application/rookie/list.do");
		}
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@ModelAttribute("application") Application application, BindingResult binding) {
		try {
			ModelAndView result;

			Application p = new Application();

			Rookie rookie = this.rookieService.loggedRookie();

			List<Position> positions = this.positionService.findAll();

			List<Curriculum> curriculums = rookie.getCurriculums();

			p = this.applicationService.reconstruct(application, binding);

			if (binding.hasErrors()) {
				result = this.createEditModelAndView(p);
				result.addObject("curriculums", curriculums);
				result.addObject("positions", positions);
			} else
				try {
					this.rookieService.addApplication(p);

					result = new ModelAndView("redirect:list.do");
					result.addObject("curriculums", curriculums);
					result.addObject("positions", positions);
				} catch (Throwable oops) {
					result = this.createEditModelAndView(application, "rookie.commit.error");
					result.addObject("curriculums", curriculums);
					result.addObject("positions", positions);
				}
			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "edit")
	public ModelAndView edit(@ModelAttribute("application") Application application, BindingResult binding) {
		try {
			ModelAndView result;

			Application p = new Application();

			Rookie rookie = this.rookieService.loggedRookie();

			p = this.applicationService.reconstruct(application, binding);

			String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();

			if (p.getExplication().contentEquals(""))
				if (locale.contains("ES"))
					binding.addError(new FieldError("application", "explication", application.getExplication(), false, null, null, "La explicacion no se puede dejar en blanco"));
				else
					binding.addError(new FieldError("application", "explication", application.getExplication(), false, null, null, "Explication can not be blank"));

			if (binding.hasErrors()) {
				result = this.createEditModelAndView(p);
				result.addObject("message", "rookie.fields.error");
			} else
				try {
					this.applicationService.save(p);
					this.messageService.notificationStatusApplicationSubmitted(p);
					result = new ModelAndView("redirect:list.do");
				} catch (Throwable oops) {
					result = this.createEditModelAndView(application, "rookie.commit.error");
				}
			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}
	}

	protected ModelAndView createEditModelAndView(Application application) {
		ModelAndView result;

		result = this.createEditModelAndView(application, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(Application application, String messageCode) {
		ModelAndView result;

		result = new ModelAndView("application/rookie/edit");

		result.addObject("application", application);
		result.addObject("message", messageCode);

		return result;
	}

}
