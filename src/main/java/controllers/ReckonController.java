
package controllers;

import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import domain.Audit;
import domain.Auditor;
import domain.Company;
import domain.Reckon;
import forms.FormObjectCurriculumPersonalData;
import services.ActorService;
import services.CompanyService;
import services.ReckonService;

@Controller
@RequestMapping("/reckon")
public class ReckonController extends AbstractController {
	
	@Autowired
	private ReckonService reckonService;
	
	@Autowired
	private CompanyService companyService;
	
	@Autowired
	private ActorService actorService;
	
	// ACCESO PUBLICO Y ROOKIE

	@RequestMapping(value = "/anonymous/list", method = RequestMethod.GET)
	public ModelAndView listReckonAsAnonymous(@RequestParam(required = false) String auditId) {
		ModelAndView result;
		
		try {
			Assert.isTrue(StringUtils.isNumeric(auditId));
			Integer auditIdInt = Integer.parseInt(auditId);
			
			List<Reckon> reckon = this.reckonService.getFinalReckonOfAudit(auditIdInt);
			
			result = new ModelAndView("reckon/list");
			result.addObject("reckon", reckon);
			result.addObject("requestURI", "/reckon/anonymous/list.do");
			
			String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();
			result.addObject("locale", locale);
		} catch(Throwable oops) {
			result = new ModelAndView("redirect:/");
		}
		return result;
	}
	
	@RequestMapping(value = "rookie/list", method = RequestMethod.GET)
	public ModelAndView listReckonAsRookie(@RequestParam(required = false) String auditId) {
		ModelAndView result;
		
		try {
			Assert.isTrue(StringUtils.isNumeric(auditId));
			Integer auditIdInt = Integer.parseInt(auditId);
			
			List<Reckon> reckon = this.reckonService.getFinalReckonOfAudit(auditIdInt);
			
			result = new ModelAndView("reckon/list");
			result.addObject("reckon", reckon);
			result.addObject("requestURI", "/reckon/rookie/list.do");
			
			String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();
			result.addObject("locale", locale);
		} catch(Throwable oops) {
			result = new ModelAndView("redirect:/");
		}
		return result;
	}
	
	// OPERACIONES RELEVANTES
	
	@RequestMapping(value = "/auditor/list", method = RequestMethod.GET)
	public ModelAndView listReckonAsAuditor(@RequestParam(required = false) String auditId) {
		ModelAndView result;
		
		try {
			Assert.isTrue(StringUtils.isNumeric(auditId));
			Integer auditIdInt = Integer.parseInt(auditId);
			
			List<Reckon> reckon = this.reckonService.getFinalReckonOfAudit(auditIdInt);
			
			result = new ModelAndView("reckon/list");
			result.addObject("reckon", reckon);
			result.addObject("requestURI", "/reckon/auditor/list.do");
			
			String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();
			result.addObject("locale", locale);
		} catch(Throwable oops) {
			result = new ModelAndView("redirect:/");
		}
		return result;
	}
	
	@RequestMapping(value = "company/list", method = RequestMethod.GET)
	public ModelAndView listReckonAsCompany(@RequestParam(required = false) String auditId) {
		ModelAndView result;
		
		try {
			result = new ModelAndView("reckon/list");
			List<Reckon> reckon;
			
			if(auditId == null || auditId.contentEquals("")) {
				Company company = this.companyService.securityAndCompany();
				reckon = this.reckonService.getAllReckonOfCompany(company.getId());
				result.addObject("editOption", true);
			} else {
				Assert.isTrue(StringUtils.isNumeric(auditId));
				Integer auditIdInt = Integer.parseInt(auditId);
				
				result.addObject("auditId", auditIdInt);
				
				if(this.reckonService.checkCompanyAndAudit(this.actorService.loggedActor().getId(), auditIdInt) != null) {
					result.addObject("createOption", true);
					reckon = this.reckonService.getAllReckonOfCompanyAndAudit(this.actorService.loggedActor().getId(), auditIdInt);
				} else {
					reckon = this.reckonService.getFinalReckonOfAudit(auditIdInt);
				}
			}
			
			result.addObject("requestURI", "/reckon/company/list.do");
			result.addObject("reckon", reckon);
			
			String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();
			result.addObject("locale", locale);
		} catch(Throwable oops) {
			result = new ModelAndView("redirect:/");
		}
		return result;
	}
	
	@RequestMapping(value = "company/create", method = RequestMethod.GET)
	public ModelAndView createReckonAsCompany(@RequestParam(required = false) String auditId) {
		ModelAndView result;
		
		try {
			Assert.isTrue(StringUtils.isNumeric(auditId));
			Integer auditIdInt = Integer.parseInt(auditId);
			
			Company company = this.companyService.securityAndCompany();
			
			Assert.notNull(this.reckonService.checkCompanyAndAudit(company.getId(), auditIdInt));
			
			Reckon reckon = this.reckonService.create();
			
			result = new ModelAndView("reckon/create");
			result.addObject("auditId", auditIdInt);
			result.addObject("reckon", reckon);
		} catch(Throwable oops) {
			result = new ModelAndView("redirect:/reckon/company/list.do?auditId=" + auditId);
		}
		return result;
	}
	
	@RequestMapping(value = "company/edit", method = RequestMethod.GET)
	public ModelAndView editReckonAsCompany(@RequestParam(required = false) String reckonId) {
		ModelAndView result;
		
		try {
			Assert.isTrue(StringUtils.isNumeric(reckonId));
			Integer reckonIdInt = Integer.parseInt(reckonId);
			
			Company company = this.companyService.securityAndCompany();
			
			Reckon reckon = this.reckonService.checkCompanyAndReckon(company.getId(), reckonIdInt);
			Assert.notNull(reckon);
			
			result = new ModelAndView("reckon/edit");
			result.addObject("reckon", reckon);
		} catch(Throwable oops) {
			result = new ModelAndView("redirect:/reckon/company/list.do");
		}
		return result;
	}
	
	@RequestMapping(value = "company/save", method = RequestMethod.POST, params = "save")
	public ModelAndView saveReckonAsCompany(@ModelAttribute("reckon") Reckon reckon, BindingResult binding, @RequestParam(required = false) Integer auditId) {
		ModelAndView result;
		
		try {
			String tiles;
			if (reckon.getId() > 0)
				tiles = "reckon/edit";
			else
				tiles = "reckon/create";
			
			reckon.setTicker(this.reckonService.generateTicker());
			
			this.reckonService.validate(reckon, binding);
			
			if(binding.hasErrors()) {
				result = new ModelAndView(tiles);
				result.addObject("reckon", reckon);
				
				if(auditId != null) {
					result.addObject("auditId", auditId);
				}
			} else {
				try {
					if(reckon.getId() == 0) {
						Assert.notNull(auditId);
						this.reckonService.addReckon(reckon, auditId);
					} else {
						Assert.isNull(auditId);
						this.reckonService.updateReckon(reckon);
					}
					
					result = new ModelAndView("redirect:/reckon/company/list.do");
				} catch(Throwable oops) {
					result = new ModelAndView(tiles);
					result.addObject("reckon", reckon);
					result.addObject("message", "reckon.commit.error");
					
					if(auditId != null) {
						result.addObject("auditId", auditId);
					}
				}
			}
			
		} catch(Throwable oops) {
			result = new ModelAndView("redirect:/");
		}
		
		return result;
	}
	
	@RequestMapping(value = "company/save", method = RequestMethod.POST, params = "delete")
	public ModelAndView deleteReckonAsCompany(Reckon reckon) {
		ModelAndView result;
		
		try {
			this.reckonService.deleteReckon(reckon);
			result = new ModelAndView("redirect:/reckon/company/list.do");
		} catch(Throwable oops) {
			result = new ModelAndView("redirect:/reckon/company/list.do");
		}
		
		return result;
	}

}
