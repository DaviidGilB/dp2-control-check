
package controllers;

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

import services.CurriculumService;
import services.MiscellaneousDataService;
import services.RookieService;
import domain.Curriculum;
import domain.MiscellaneousData;

@Controller
@RequestMapping("/miscellaneousData/rookie")
public class MiscellaneousDataRookieController extends AbstractController {

	@Autowired
	private CurriculumService			curriculumService;
	@Autowired
	private MiscellaneousDataService	miscellaneousDataService;
	@Autowired
	private RookieService				rookieService;


	public MiscellaneousDataRookieController() {
		super();
	}

	@RequestMapping(value = "/listAttachments", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam(required = false) String miscellaneousDataId) {
		ModelAndView result;

		try {
			Assert.isTrue(StringUtils.isNumeric(miscellaneousDataId));
			int miscellaneousDataIdInt = Integer.parseInt(miscellaneousDataId);

			List<String> attachments = this.miscellaneousDataService.getAttachmentsOfMiscellaneousDataOfLoggedRookie(miscellaneousDataIdInt);

			result = new ModelAndView("rookie/attachments");
			result.addObject("attachments", attachments);
			result.addObject("miscellaneousDataId", miscellaneousDataId);
			result.addObject("curriculumId", this.curriculumService.getCurriculumOfMiscellaneousData(miscellaneousDataIdInt).getId());
		} catch (Throwable oops) {
			result = new ModelAndView("redirect:/curriculum/rookie/list.do");
		}

		return result;
	}

	@RequestMapping(value = "/newAttachment", method = RequestMethod.GET)
	public ModelAndView newAttachment(@RequestParam(required = false) String miscellaneousDataId) {
		try {

			Assert.isTrue(StringUtils.isNumeric(miscellaneousDataId));
			int miscellaneousDataIdInt = Integer.parseInt(miscellaneousDataId);
			ModelAndView result;

			result = new ModelAndView("rookie/createAttachment");
			result.addObject("miscellaneousDataId", miscellaneousDataIdInt);
			result.addObject("attachment", "");

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}
	}

	@RequestMapping(value = "/deleteAttachment", method = RequestMethod.GET)
	public ModelAndView newAttachment(@RequestParam(required = false) String miscellaneousDataId, @RequestParam(required = false) String attachmentIndex) {
		ModelAndView result;

		try {
			Assert.isTrue(StringUtils.isNumeric(miscellaneousDataId));
			int miscellaneousDataIdInt = Integer.parseInt(miscellaneousDataId);

			Assert.isTrue(StringUtils.isNumeric(attachmentIndex));
			int attachmentIndexInt = Integer.parseInt(attachmentIndex);

			this.miscellaneousDataService.deleteAttachmentAsRookie(miscellaneousDataIdInt, attachmentIndexInt);
			result = new ModelAndView("redirect:listAttachments.do?miscellaneousDataId=" + miscellaneousDataIdInt);
		} catch (Throwable oops) {
			result = new ModelAndView("redirect:/curriculum/rookie/list.do");
		}

		return result;
	}

	@RequestMapping(value = "/saveAttachment", method = RequestMethod.POST, params = "save")
	public ModelAndView saveAttachment(@RequestParam String miscellaneousDataId, @RequestParam String attachment) {
		ModelAndView result;

		try {

			Assert.isTrue(StringUtils.isNumeric(miscellaneousDataId));
			int miscellaneousDataIdInt = Integer.parseInt(miscellaneousDataId);

			this.miscellaneousDataService.addAttachmentAsRookie(miscellaneousDataIdInt, attachment);
			result = new ModelAndView("redirect:listAttachments.do?miscellaneousDataId=" + miscellaneousDataIdInt);
		} catch (Throwable oops) {
			result = new ModelAndView("rookie/createAttachment");
			result.addObject("miscellaneousDataId", miscellaneousDataId);
			result.addObject("attachment", attachment);
			result.addObject("message", "commit.error.url");
		}

		return result;
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public ModelAndView newMiscellaneousData(@RequestParam(required = false) String curriculumId) {
		try {

			Assert.isTrue(StringUtils.isNumeric(curriculumId));
			int curriculumIdInt = Integer.parseInt(curriculumId);
			ModelAndView result;

			MiscellaneousData miscellaneousData = this.miscellaneousDataService.create();

			result = this.createEditModelAndView("rookie/createMiscellaneousData", miscellaneousData, curriculumIdInt);

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/curriculum/rookie/list.do");
		}
	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView editMiscellaneousData(@RequestParam(required = false) String miscellaneousDataId) {
		ModelAndView result;

		try {
			Assert.isTrue(StringUtils.isNumeric(miscellaneousDataId));
			int miscellaneousDataIdInt = Integer.parseInt(miscellaneousDataId);
			MiscellaneousData miscellaneousData = this.miscellaneousDataService.getMiscellaneousDataOfLoggedRookie(miscellaneousDataIdInt);
			Curriculum curriculum = this.curriculumService.getCurriculumOfMiscellaneousData(miscellaneousDataIdInt);

			result = this.createEditModelAndView("rookie/editMiscellaneousData", miscellaneousData, curriculum.getId());
		} catch (Throwable oops) {
			result = new ModelAndView("redirect:/curriculum/rookie/list.do");
		}

		return result;
	}

	@RequestMapping(value = "/delete", method = RequestMethod.GET)
	public ModelAndView deleteMiscellaneousData(@RequestParam(required = false) String miscellaneousDataId) {
		ModelAndView result = null;

		try {
			Assert.isTrue(StringUtils.isNumeric(miscellaneousDataId));
			int miscellaneousDataIdInt = Integer.parseInt(miscellaneousDataId);

			Curriculum curriculum = this.curriculumService.getCurriculumOfMiscellaneousData(miscellaneousDataIdInt);
			this.miscellaneousDataService.deleteMiscellaneousDataAsRookie(miscellaneousDataIdInt);
			result = new ModelAndView("redirect:/curriculum/rookie/show.do?curriculumId=" + curriculum.getId());
		} catch (Throwable oops) {
			result = new ModelAndView("redirect:/curriculum/rookie/list.do");
		}

		return result;
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST, params = "save")
	public ModelAndView saveMiscellaneousData(MiscellaneousData miscellaneousData, BindingResult binding, String curriculumId) {
		ModelAndView result;
		try {

			Assert.isTrue(StringUtils.isNumeric(curriculumId));
			int curriculumIdInt = Integer.parseInt(curriculumId);

			String tiles;
			if (miscellaneousData.getId() == 0)
				tiles = "rookie/createMiscellaneousData";
			else
				tiles = "rookie/editMiscellaneousData";

			MiscellaneousData miscellaneousDataReconstructed = this.miscellaneousDataService.reconstruct(miscellaneousData, binding);

			if (binding.hasErrors())
				result = this.createEditModelAndView(tiles, miscellaneousDataReconstructed, curriculumIdInt);
			else
				try {
					this.miscellaneousDataService.addOrUpdateMiscellaneousDataAsRookie(miscellaneousDataReconstructed, curriculumIdInt);
					result = new ModelAndView("redirect:/curriculum/rookie/show.do?curriculumId=" + curriculumIdInt);
				} catch (Throwable oops) {
					result = this.createEditModelAndView(tiles, miscellaneousDataReconstructed, curriculumIdInt, "commit.error");
				}

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}
	}

	private ModelAndView createEditModelAndView(String tiles, MiscellaneousData miscellaneousData, int curriculumId) {
		ModelAndView result = new ModelAndView(tiles);
		result.addObject("miscellaneousData", miscellaneousData);
		result.addObject("curriculumId", curriculumId);
		return result;
	}

	private ModelAndView createEditModelAndView(String tiles, MiscellaneousData miscellaneousData, int curriculumId, String message) {
		ModelAndView result = this.createEditModelAndView(tiles, miscellaneousData, curriculumId);
		result.addObject("message", message);
		return result;
	}
}
