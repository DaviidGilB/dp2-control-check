
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

import services.AuditService;
import services.AuditorService;
import services.PositionService;
import domain.Audit;
import domain.Auditor;
import domain.Position;

@Controller
@RequestMapping("/audit/auditor")
public class AuditController extends AbstractController {

	@Autowired
	private AuditorService	auditorService;

	@Autowired
	private AuditService	auditService;

	@Autowired
	private PositionService	positionService;


	public AuditController() {
		super();
	}

	// -------------------------------------------------------------------
	// ---------------------------LIST------------------------------------

	// Listar MIS Audits en vista privada, NO en public data
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list() {
		try {

			ModelAndView result;
			List<Audit> audits;

			Auditor loggedAuditor = this.auditorService.loggedAuditor();
			audits = loggedAuditor.getAudits();

			result = new ModelAndView("audit/auditor/list");
			result.addObject("audits", audits);
			result.addObject("requestURI", "audit/auditor/list.do");

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}
	}

	// CREATE AUDIT
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView createAudit(@RequestParam(required = false) String positionId) {
		try {

			Assert.isTrue(StringUtils.isNumeric(positionId));
			int positionIdInt = Integer.parseInt(positionId);

			ModelAndView result;

			Position position = this.positionService.findOne(positionIdInt);

			//Si la position no esta cancelada ni en draft y no tiene ya un audit del auditor logueado
			if (!this.auditorService.showAssignablePositions().contains(position))
				return this.list();

			Audit audit = this.auditService.create(position);

			result = this.createEditModelAndView(audit);
			result.addObject("audit", audit);

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/audit/auditor/list.do");
		}
	}

	// EDIT AUDIT
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam(required = false) String auditId) {
		try {
			Assert.isTrue(StringUtils.isNumeric(auditId));
			int auditIdInt = Integer.parseInt(auditId);

			ModelAndView result;
			Position position;
			Audit audit = this.auditService.findOne(auditIdInt);
			Auditor loggedAuditor = this.auditorService.loggedAuditor();

			if (audit == null)
				return this.list();

			position = audit.getPosition();

			if (position == null || audit == null || position.getIsDraftMode() || position.getIsCancelled() || (!audit.getIsDraftMode()) || !(loggedAuditor.getAudits().contains(audit)))
				return this.list();

			result = this.createEditModelAndView(audit);
			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/audit/auditor/list.do");
		}
	}

	//SAVE AUDIT
	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(Audit audit, BindingResult binding) {
		try {
			ModelAndView result;

			Audit a = new Audit();

			a = this.auditService.reconstruct(audit, binding);

			if (binding.hasErrors())
				result = this.createEditModelAndView(audit);
			else
				try {
					this.auditorService.saveAudit(a);

					result = new ModelAndView("redirect:list.do");

				} catch (Throwable oops) {
					result = this.createEditModelAndView(audit, "commit.error");
				}

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}
	}

	//DELETE AUDIT
	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "delete")
	public ModelAndView delete(Audit audit2) {
		try {
			ModelAndView result;

			Audit audit = this.auditService.findOne(audit2.getId());

			try {
				this.auditService.deleteAudit(audit);

				result = new ModelAndView("redirect:list.do");
			} catch (Throwable oops) {
				result = this.createEditModelAndView(audit, "commit.error");

			}
			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}
	}

	//PROTECTED MODEL AND VIEW
	protected ModelAndView createEditModelAndView(Audit audit) {
		ModelAndView result;

		result = this.createEditModelAndView(audit, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(Audit audit, String messageCode) {
		ModelAndView result;

		result = new ModelAndView("audit/auditor/edit");

		result.addObject("audit", audit);
		result.addObject("message", messageCode);

		return result;
	}

}
