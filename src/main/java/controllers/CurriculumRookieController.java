
package controllers;

import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.CurriculumService;
import services.PersonalDataService;
import services.RookieService;
import domain.Curriculum;
import domain.PersonalData;
import forms.FormObjectCurriculumPersonalData;

@Controller
@RequestMapping("/curriculum/rookie")
public class CurriculumRookieController extends AbstractController {

	@Autowired
	private CurriculumService	curriculumService;
	@Autowired
	private RookieService		rookieService;
	@Autowired
	private PersonalDataService	personalDataService;


	public CurriculumRookieController() {
		super();
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list() {
		ModelAndView result;

		try {
			List<Curriculum> curriculums = this.curriculumService.getCurriculumsOfLoggedRookie();

			result = new ModelAndView("rookie/curriculums");
			result.addObject("curriculums", curriculums);
		} catch (Throwable oops) {
			result = new ModelAndView("redirect:/");
		}

		return result;
	}

	@RequestMapping(value = "/show", method = RequestMethod.GET)
	public ModelAndView show(@RequestParam(required = false) String curriculumId) {
		ModelAndView result;

		try {
			Assert.isTrue(StringUtils.isNumeric(curriculumId));
			int curriculumIdInt = Integer.parseInt(curriculumId);

			Curriculum curriculum = this.curriculumService.getCurriculumOfLoggedRookie(curriculumIdInt);

			result = new ModelAndView("rookie/curriculum");
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

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public ModelAndView newCurriculum() {
		ModelAndView result;

		FormObjectCurriculumPersonalData formObject = new FormObjectCurriculumPersonalData();

		result = this.createEditModelAndView("rookie/createCurriculum", formObject);

		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView editCurriculum(@RequestParam(required = false) String curriculumId) {
		ModelAndView result;

		try {

			Assert.isTrue(StringUtils.isNumeric(curriculumId));
			int curriculumIdInt = Integer.parseInt(curriculumId);

			FormObjectCurriculumPersonalData formObject = new FormObjectCurriculumPersonalData();

			Curriculum curriculum = this.curriculumService.getCurriculumOfLoggedRookie(curriculumIdInt);

			formObject.setId(curriculum.getId());
			formObject.setTitle(curriculum.getTitle());
			formObject.setFullName(curriculum.getPersonalData().getFullName());
			formObject.setPhoneNumber(curriculum.getPersonalData().getPhoneNumber());
			formObject.setStatement(curriculum.getPersonalData().getStatement());
			formObject.setGitHubProfile(curriculum.getPersonalData().getGitHubProfile());
			formObject.setLinkedInProfile(curriculum.getPersonalData().getLinkedinProfile());

			result = this.createEditModelAndView("rookie/editCurriculum", formObject);
		} catch (Throwable oops) {
			result = new ModelAndView("redirect:list.do");
		}

		return result;
	}

	@RequestMapping(value = "/delete", method = RequestMethod.GET)
	public ModelAndView deleteCurriculum(@RequestParam(required = false) String curriculumId) {
		ModelAndView result;

		try {
			Assert.isTrue(StringUtils.isNumeric(curriculumId));
			int curriculumIdInt = Integer.parseInt(curriculumId);

			this.curriculumService.deleteCurriculumAsRookie(curriculumIdInt);

			result = new ModelAndView("redirect:list.do");
		} catch (Throwable oops) {
			result = new ModelAndView("redirect:list.do");
		}

		return result;
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST, params = "save")
	public ModelAndView saveCurriculum(@ModelAttribute("formObject") @Valid FormObjectCurriculumPersonalData formObject, BindingResult binding) {
		try {
			ModelAndView result;

			String tiles;
			if (formObject.getId() > 0)
				tiles = "rookie/editCurriculum";
			else
				tiles = "rookie/createCurriculum";

			PersonalData personalData = new PersonalData();
			Curriculum curriculum = new Curriculum();

			personalData = this.personalDataService.reconstruct(formObject, binding);
			curriculum = this.curriculumService.reconstruct(formObject, binding, personalData);

			if (binding.hasErrors())
				result = this.createEditModelAndView(tiles, formObject);
			else
				try {
					this.rookieService.addOrUpdateCurriculum(curriculum);
					result = new ModelAndView("redirect:list.do");
				} catch (Throwable oops) {
					result = this.createEditModelAndView(tiles, formObject, "commit.error");
				}
			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}
	}

	private ModelAndView createEditModelAndView(String tiles, FormObjectCurriculumPersonalData formObject, String message) {
		ModelAndView result;

		result = new ModelAndView(tiles);
		result.addObject("formObject", formObject);
		result.addObject("message", message);

		return result;
	}

	private ModelAndView createEditModelAndView(String tiles, FormObjectCurriculumPersonalData formObject) {
		return this.createEditModelAndView(tiles, formObject, null);
	}

}
