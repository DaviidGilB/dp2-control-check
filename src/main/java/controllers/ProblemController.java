
package controllers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.CompanyService;
import services.ProblemService;
import domain.Company;
import domain.Problem;

@Controller
@RequestMapping("/problem")
public class ProblemController extends AbstractController {

	@Autowired
	private ProblemService	problemService;

	@Autowired
	private CompanyService	companyService;


	public ProblemController() {
		super();
	}

	@RequestMapping(value = "/company/list", method = RequestMethod.GET)
	public ModelAndView listProblems() {
		try {
			ModelAndView result;
			Boolean assignable = true;

			this.companyService.loggedAsCompany();

			List<Problem> problems = new ArrayList<Problem>();

			problems = this.problemService.showProblems();

			result = new ModelAndView("problem/company/list");
			Boolean sameActorLogged = true;
			result.addObject("problems", problems);
			result.addObject("assignable", assignable);
			result.addObject("sameActorLogged", sameActorLogged);
			result.addObject("requestURI", "problem/company/list.do");

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}
	}

	@RequestMapping(value = "/company/listAttachments", method = RequestMethod.GET)
	public ModelAndView listAttachments(@RequestParam(required = false) String problemId) {
		try {
			Assert.isTrue(StringUtils.isNumeric(problemId));
			int problemIdInt = Integer.parseInt(problemId);

			ModelAndView result;

			Problem problem = this.problemService.findOne(problemIdInt);
			Boolean sameActorLogged = true;
			result = new ModelAndView("problem/company/listAttachments");
			result.addObject("attachments", problem.getAttachments());
			result.addObject("problemId", problemIdInt);
			result.addObject("sameActorLogged", sameActorLogged);
			result.addObject("canEdit", problem.getIsDraftMode());

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/problem/company/list.do");
		}
	}

	@RequestMapping(value = "/company/addAttachment", method = RequestMethod.GET)
	public ModelAndView addAttachment(@RequestParam(required = false) String problemId) {
		try {
			Assert.isTrue(StringUtils.isNumeric(problemId));
			int problemIdInt = Integer.parseInt(problemId);

			ModelAndView result;

			result = new ModelAndView("problem/company/addAttachment");
			result.addObject("problemId", problemIdInt);

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/problem/company/list.do");
		}
	}

	@RequestMapping(value = "/company/create", method = RequestMethod.GET)
	public ModelAndView createProblem() {
		try {
			ModelAndView result;

			Problem problem = this.problemService.create();

			result = this.createEditModelAndView(problem);
			result.addObject("problem", problem);

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/problem/company/list.do");
		}
	}

	@RequestMapping(value = "/company/edit", method = RequestMethod.GET)
	public ModelAndView editProblem(@RequestParam(required = false) String problemId) {
		try {
			Assert.isTrue(StringUtils.isNumeric(problemId));
			int problemIdInt = Integer.parseInt(problemId);

			ModelAndView result;

			Problem problem = this.problemService.findOne(problemIdInt);
			Company company = this.companyService.loggedCompany();

			if (problem != null && company.getProblems().contains(problem))
				result = this.createEditModelAndView(problem);
			else
				result = new ModelAndView("redirect:list.do");

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/problem/company/list.do");
		}
	}

	@RequestMapping(value = "/company/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(Problem problem, BindingResult binding) {
		try {
			ModelAndView result;

			Problem p = this.problemService.create();

			p = this.problemService.reconstruct(problem, binding);

			if (binding.hasErrors())
				result = this.createEditModelAndView(problem);
			else
				try {
					this.companyService.addProblem(p);

					result = new ModelAndView("redirect:list.do");
				} catch (Throwable oops) {
					result = this.createEditModelAndView(problem, "company.commit.error");

				}
			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/problem/company/list.do");
		}
	}

	@RequestMapping(value = "/company/edit", method = RequestMethod.POST, params = "edit")
	public ModelAndView edit(Problem problem, BindingResult binding) {
		try {
			ModelAndView result;

			Problem p = this.problemService.create();

			p = this.problemService.reconstruct(problem, binding);

			if (binding.hasErrors())
				result = this.createEditModelAndView(problem);
			else
				try {
					this.problemService.updateProblem(p);

					result = new ModelAndView("redirect:list.do");
				} catch (Throwable oops) {
					result = this.createEditModelAndView(problem, "company.commit.error");

				}
			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/problem/company/list.do");
		}
	}

	@RequestMapping(value = "/company/addAttachment", method = RequestMethod.POST, params = "save")
	public ModelAndView addAttachment(@RequestParam(required = false) String problemId, String attachment) {
		try {
			Assert.isTrue(StringUtils.isNumeric(problemId));
			int problemIdInt = Integer.parseInt(problemId);

			ModelAndView result;

			Problem problem = this.problemService.findOne(problemIdInt);

			if (!this.problemService.isUrl(attachment.trim())) {
				result = new ModelAndView("problem/company/addAttachment");
				result.addObject("problemId", problemIdInt);
				result.addObject("message", "company.notValidUrl");
			} else
				try {
					this.problemService.addAttachment(attachment, problem);

					result = new ModelAndView("redirect:listAttachments.do");
					result.addObject("problemId", problemIdInt);
				} catch (Throwable oops) {
					result = new ModelAndView("problem/company/addAttachment");
					result.addObject("problemId", problemIdInt);
					result.addObject("message", "company.commit.error");
				}

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/problem/company/list.do");
		}
	}

	@RequestMapping(value = "/company/edit", method = RequestMethod.POST, params = "delete")
	public ModelAndView delete(Problem problem2) {
		try {
			ModelAndView result;

			Problem problem = this.problemService.findOne(problem2.getId());

			try {
				this.problemService.deleteProblem(problem);

				result = new ModelAndView("redirect:list.do");
			} catch (Throwable oops) {
				result = this.createEditModelAndView(problem, "company.commit.error");

			}
			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/problem/company/list.do");
		}
	}

	@RequestMapping(value = "/company/deleteAttachment", method = RequestMethod.GET)
	public ModelAndView copy(@RequestParam(required = false) String problemId, @RequestParam(required = false) String attachmentNumber) {
		try {
			Assert.isTrue(StringUtils.isNumeric(problemId));
			int problemIdInt = Integer.parseInt(problemId);

			Assert.isTrue(StringUtils.isNumeric(attachmentNumber));
			int attachmentNumberInt = Integer.parseInt(attachmentNumber);

			ModelAndView result;

			try {
				Problem problem = this.problemService.findOne(problemIdInt);
				this.problemService.removeAttachment(problem, attachmentNumberInt);

			} catch (Throwable oops) {

			}

			result = new ModelAndView("redirect:listAttachments.do");
			result.addObject("problemId", problemIdInt);
			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/problem/company/list.do");
		}
	}

	protected ModelAndView createEditModelAndView(Problem problem) {
		ModelAndView result;

		result = this.createEditModelAndView(problem, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(Problem problem, String messageCode) {
		ModelAndView result;

		result = new ModelAndView("problem/company/edit");

		result.addObject("problem", problem);
		result.addObject("message", messageCode);

		return result;
	}
}
