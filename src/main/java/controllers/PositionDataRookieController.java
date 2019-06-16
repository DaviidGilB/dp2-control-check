
package controllers;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.CurriculumService;
import services.PositionDataService;
import services.RookieService;
import domain.Curriculum;
import domain.PositionData;

@Controller
@RequestMapping("/positionData/rookie")
public class PositionDataRookieController extends AbstractController {

	@Autowired
	private CurriculumService	curriculumService;
	@Autowired
	private PositionDataService	positionDataService;
	@Autowired
	private RookieService		rookieService;


	public PositionDataRookieController() {
		super();
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public ModelAndView newPositionData(@RequestParam(required = false) String curriculumId) {
		ModelAndView result;
		try {
			Assert.isTrue(StringUtils.isNumeric(curriculumId));
			int curriculumIdInt = Integer.parseInt(curriculumId);

			PositionData positionData = new PositionData();

			result = this.createEditModelAndView("rookie/createPositionData", positionData, curriculumIdInt);

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}
	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView editPositionData(@RequestParam(required = false) String positionDataId) {
		ModelAndView result;

		try {
			Assert.isTrue(StringUtils.isNumeric(positionDataId));
			int positionDataIdInt = Integer.parseInt(positionDataId);

			PositionData positionData = this.positionDataService.findOne(positionDataIdInt);
			Curriculum curriculum = this.curriculumService.getCurriculumOfPositionData(positionDataIdInt);
			Assert.notNull(this.curriculumService.getCurriculumOfLoggedRookie(curriculum.getId()));

			result = this.createEditModelAndView("rookie/editPositionData", positionData, curriculum.getId());
		} catch (Throwable oops) {
			result = new ModelAndView("redirect:/curriculum/rookie/list.do");
		}

		return result;
	}

	@RequestMapping(value = "/delete", method = RequestMethod.GET)
	public ModelAndView deletePositionData(@RequestParam(required = false) String positionDataId) {
		ModelAndView result = null;

		try {
			Assert.isTrue(StringUtils.isNumeric(positionDataId));
			int positionDataIdInt = Integer.parseInt(positionDataId);

			Curriculum curriculum = this.curriculumService.getCurriculumOfPositionData(positionDataIdInt);
			this.positionDataService.deletePositionDataAsRookie(positionDataIdInt);
			result = new ModelAndView("redirect:/curriculum/rookie/show.do?curriculumId=" + curriculum.getId());
		} catch (Throwable oops) {
			result = new ModelAndView("redirect:/curriculum/rookie/list.do");
		}

		return result;
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST, params = "save")
	public ModelAndView savePositionData(@Valid PositionData positionData, BindingResult binding, @Valid String curriculumId) {
		ModelAndView result;
		try {
			Assert.isTrue(StringUtils.isNumeric(curriculumId));
			int curriculumIdInt = Integer.parseInt(curriculumId);

			String tiles;
			if (positionData.getId() == 0)
				tiles = "rookie/createPositionData";
			else
				tiles = "rookie/editPositionData";

			if (positionData.getEndDate() != null && positionData.getStartDate() != null) {
				String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();

				if (positionData.getStartDate().after(positionData.getEndDate()))
					if (locale.contains("ES"))
						binding.addError(new FieldError("positionData", "startDate", positionData.getStartDate(), false, null, null, "La fecha de fin no puede ser anterior a la de inicio"));
					else
						binding.addError(new FieldError("positionData", "startDate", positionData.getStartDate(), false, null, null, "The end date can not be before the start date"));
			}

			if (binding.hasErrors())
				result = this.createEditModelAndView(tiles, positionData, curriculumIdInt);
			else
				try {
					this.positionDataService.addOrUpdatePositionDataAsRookie(positionData, curriculumIdInt);
					result = new ModelAndView("redirect:/curriculum/rookie/show.do?curriculumId=" + curriculumIdInt);
				} catch (Throwable oops) {
					result = this.createEditModelAndView(tiles, positionData, curriculumIdInt, "commit.error");
				}

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}
	}

	private ModelAndView createEditModelAndView(String tiles, PositionData positionData, int curriculumId) {
		ModelAndView result = new ModelAndView(tiles);
		result.addObject("positionData", positionData);
		result.addObject("curriculumId", curriculumId);
		return result;
	}

	private ModelAndView createEditModelAndView(String tiles, PositionData positionData, int curriculumId, String message) {
		ModelAndView result = this.createEditModelAndView(tiles, positionData, curriculumId);
		result.addObject("message", message);
		return result;
	}

}
