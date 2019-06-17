
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
import domain.ControlEntity;
import forms.FormObjectCurriculumPersonalData;
import services.ActorService;
import services.AuditorService;
import services.CompanyService;
import services.ControlEntityService;

@Controller
@RequestMapping("/controlEntity")
public class ControlEntityController extends AbstractController {
	
	@Autowired
	private ControlEntityService controlEntityService;
	
	@Autowired
	private CompanyService companyService;
	
	@Autowired
	private ActorService actorService;
	
	@Autowired
	private AuditorService auditorService;
	
	// ACCESO PUBLICO Y ROOKIE

	@RequestMapping(value = "/anonymous/list", method = RequestMethod.GET)
	public ModelAndView listControlEntityAsAnonymous(@RequestParam(required = false) String auditId) {
		ModelAndView result;
		
		try {
			Assert.isTrue(StringUtils.isNumeric(auditId));
			Integer auditIdInt = Integer.parseInt(auditId);
			
			List<ControlEntity> controlEntity = this.controlEntityService.getFinalControlEntityOfAudit(auditIdInt);
			
			result = new ModelAndView("controlEntity/list");
			result.addObject("controlEntity", controlEntity);
			result.addObject("requestURI", "/controlEntity/anonymous/list.do");
			
			String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();
			result.addObject("locale", locale);
		} catch(Throwable oops) {
			result = new ModelAndView("redirect:/");
		}
		return result;
	}
	
	@RequestMapping(value = "rookie/list", method = RequestMethod.GET)
	public ModelAndView listControlEntityAsRookie(@RequestParam(required = false) String auditId) {
		ModelAndView result;
		
		try {
			Assert.isTrue(StringUtils.isNumeric(auditId));
			Integer auditIdInt = Integer.parseInt(auditId);
			
			List<ControlEntity> controlEntity = this.controlEntityService.getFinalControlEntityOfAudit(auditIdInt);
			
			result = new ModelAndView("controlEntity/list");
			result.addObject("controlEntity", controlEntity);
			result.addObject("requestURI", "/controlEntity/rookie/list.do");
			
			String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();
			result.addObject("locale", locale);
		} catch(Throwable oops) {
			result = new ModelAndView("redirect:/");
		}
		return result;
	}
	
	// OPERACIONES RELEVANTES
	
	@RequestMapping(value = "/company/list", method = RequestMethod.GET)
	public ModelAndView listControlEntityAsCompany(@RequestParam(required = false) String auditId) {
		ModelAndView result;
		
		try {
			Assert.isTrue(StringUtils.isNumeric(auditId));
			Integer auditIdInt = Integer.parseInt(auditId);
			
			List<ControlEntity> controlEntity = this.controlEntityService.getFinalControlEntityOfAudit(auditIdInt);
			
			result = new ModelAndView("controlEntity/list");
			result.addObject("controlEntity", controlEntity);
			result.addObject("requestURI", "/controlEntity/company/list.do");
			
			String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();
			result.addObject("locale", locale);
		} catch(Throwable oops) {
			result = new ModelAndView("redirect:/");
		}
		return result;
	}
	
	@RequestMapping(value = "auditor/list", method = RequestMethod.GET)
	public ModelAndView listControlEntityAsAuditor(@RequestParam(required = false) String auditId) {
		ModelAndView result;
		
		try {
			result = new ModelAndView("controlEntity/list");
			List<ControlEntity> controlEntity;
			
			if(auditId == null || auditId.contentEquals("")) {
				Auditor auditor = this.auditorService.securityAndAuditor();
				
				controlEntity = this.controlEntityService.getAllControlEntityOfAuditor(auditor.getId());
				result.addObject("editOption", true);
				
			} else {
				Assert.isTrue(StringUtils.isNumeric(auditId));
				Integer auditIdInt = Integer.parseInt(auditId);
				
				result.addObject("auditId", auditIdInt);
				
				if(this.controlEntityService.checkAuditorAndAudit(this.actorService.loggedActor().getId(), auditIdInt) != null) {
					result.addObject("createOption", true);
					controlEntity = this.controlEntityService.getAllControlEntityOfAuditorAndAudit(this.actorService.loggedActor().getId(), auditIdInt);
				} else {
					controlEntity = this.controlEntityService.getFinalControlEntityOfAudit(auditIdInt);
				}
			}
			
			result.addObject("requestURI", "/controlEntity/auditor/list.do");
			result.addObject("controlEntity", controlEntity);
			
			String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();
			result.addObject("locale", locale);
		} catch(Throwable oops) {
			result = new ModelAndView("redirect:/");
		}
		return result;
	}
	
	@RequestMapping(value = "auditor/create", method = RequestMethod.GET)
	public ModelAndView createControlEntityAsAuditor(@RequestParam(required = false) String auditId) {
		ModelAndView result;
		
		try {
			Assert.isTrue(StringUtils.isNumeric(auditId));
			Integer auditIdInt = Integer.parseInt(auditId);
			
			Auditor auditor = this.auditorService.securityAndAuditor();
			
			Assert.notNull(this.controlEntityService.checkAuditorAndAudit(auditor.getId(), auditIdInt));
			
			ControlEntity controlEntity = this.controlEntityService.create();
			
			result = new ModelAndView("controlEntity/create");
			result.addObject("auditId", auditIdInt);
			result.addObject("controlEntity", controlEntity);
		} catch(Throwable oops) {
			result = new ModelAndView("redirect:/controlEntity/auditor/list.do?auditId=" + auditId);
		}
		return result;
	}
	
	@RequestMapping(value = "auditor/edit", method = RequestMethod.GET)
	public ModelAndView editControlEntityAsAuditor(@RequestParam(required = false) String controlEntityId) {
		ModelAndView result;
		
		try {
			Assert.isTrue(StringUtils.isNumeric(controlEntityId));
			Integer controlEntityIdInt = Integer.parseInt(controlEntityId);
			
			Auditor auditor = this.auditorService.securityAndAuditor();
			
			ControlEntity controlEntity = this.controlEntityService.checkAuditorAndControlEntity(auditor.getId(), controlEntityIdInt);
			Assert.notNull(controlEntity);
			
			result = new ModelAndView("controlEntity/edit");
			result.addObject("controlEntity", controlEntity);
		} catch(Throwable oops) {
			result = new ModelAndView("redirect:/controlEntity/auditor/list.do");
		}
		return result;
	}
	
	@RequestMapping(value = "auditor/save", method = RequestMethod.POST, params = "save")
	public ModelAndView saveControlEntityAsAuditor(@ModelAttribute("controlEntity") ControlEntity controlEntity, BindingResult binding, @RequestParam(required = false) Integer auditId) {
		ModelAndView result;
		
		try {
			String tiles;
			if (controlEntity.getId() > 0)
				tiles = "controlEntity/edit";
			else
				tiles = "controlEntity/create";
			
			controlEntity.setTicker(this.controlEntityService.generateTicker());
			
			this.controlEntityService.validate(controlEntity, binding);
			
			if(binding.hasErrors()) {
				result = new ModelAndView(tiles);
				result.addObject("controlEntity", controlEntity);
				
				if(auditId != null) {
					result.addObject("auditId", auditId);
				}
			} else {
				try {
					if(controlEntity.getId() == 0) {
						Assert.notNull(auditId);
						
						// CONTROL_CHECK_PUNTERO
						
						this.controlEntityService.addControlEntity(controlEntity, auditId);
					} else {
						Assert.isNull(auditId);
						this.controlEntityService.updateControlEntity(controlEntity);
					}
					
					result = new ModelAndView("redirect:/controlEntity/company/list.do");
				} catch(Throwable oops) {
					result = new ModelAndView(tiles);
					result.addObject("controlEntity", controlEntity);
					result.addObject("message", "controlEntity.commit.error");
					
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
	public ModelAndView deleteControlEntityAsCompany(ControlEntity controlEntity) {
		ModelAndView result;
		
		try {
			this.controlEntityService.deleteControlEntity(controlEntity);
			result = new ModelAndView("redirect:/controlEntity/company/list.do");
		} catch(Throwable oops) {
			result = new ModelAndView("redirect:/controlEntity/company/list.do");
		}
		
		return result;
	}

}
