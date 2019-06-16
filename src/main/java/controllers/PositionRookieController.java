
package controllers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.AuditService;
import services.PositionService;
import domain.Audit;
import domain.Position;

@Controller
@RequestMapping("/position/rookie")
public class PositionRookieController extends AbstractController {

	@Autowired
	private PositionService	positionService;

	@Autowired
	private AuditService	auditService;


	public PositionRookieController() {
		super();
	}

	@RequestMapping(value = "/listSkills", method = RequestMethod.GET)
	public ModelAndView listSkills(@RequestParam(required = false) String positionId) {
		try {
			ModelAndView result;

			try {
				Assert.isTrue(StringUtils.isNumeric(positionId));
				int positionIdInt = Integer.parseInt(positionId);

				List<String> skills = this.positionService.getSkillsAsRookie(positionIdInt);

				result = new ModelAndView("rookie/skills");
				result.addObject("skills", skills);
			} catch (Throwable oops) {
				result = new ModelAndView("redirect:/finder/rookie/list.do");
			}

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/finder/rookie/list.do");
		}
	}

	@RequestMapping(value = "/listTechnologies", method = RequestMethod.GET)
	public ModelAndView listTechnologies(@RequestParam(required = false) String positionId) {
		try {
			Assert.isTrue(StringUtils.isNumeric(positionId));
			int positionIdInt = Integer.parseInt(positionId);
			ModelAndView result;

			try {
				List<String> technologies = this.positionService.getTechnologiesAsRookie(positionIdInt);

				result = new ModelAndView("rookie/technologies");
				result.addObject("technologies", technologies);
			} catch (Throwable oops) {
				result = new ModelAndView("redirect:/finder/rookie/list.do");
			}

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/finder/rookie/list.do");
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
				result = new ModelAndView("redirect:/finder/rookie/list.do");
			else {
				result = new ModelAndView("position/rookie/audit/list");

				result.addObject("finalAudits", finalAudits);
				result.addObject("requestURI", "position/rookie/audit/list.do");
				result.addObject("positionId", positionIdInt);

			}

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/finder/rookie/list.do");
		}
	}
}
