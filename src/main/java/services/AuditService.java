
package services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import domain.Audit;
import domain.Auditor;
import domain.Reckon;
import domain.Position;
import repositories.AuditRepository;

@Service
@Transactional
public class AuditService {

	@Autowired
	private AuditRepository auditRepository;

	@Autowired
	private AuditorService auditorService;

	@Autowired
	private Validator validator;

	@Autowired
	private MessageService messageService;
	
	@Autowired
	private ReckonService reckonService;

	public List<Audit> getFinalAuditsByPosition(int positionId) {
		return this.auditRepository.getFinalAuditsByPosition(positionId);
	}

	public Audit save(Audit audit) {
		return this.auditRepository.save(audit);
	}

	public Audit findOne(int auditId) {
		return this.auditRepository.findOne(auditId);
	}

	public List<Audit> getDraftAuditsByPosition(int positionId) {
		return this.auditRepository.getDraftAuditsByPosition(positionId);
	}

	// CREATE
	public Audit create(Position position) {
		Audit audit = new Audit();

		audit.setAuditor(null);
		audit.setFreeText("");
		audit.setIsDraftMode(true);
		audit.setMomentCreation(null);
		audit.setPosition(position);
		audit.setScore(0);
		
		// CONTROL_CHECK
		audit.setReckon(new ArrayList<Reckon>());

		return audit;
	}

	public Audit reconstruct(Audit audit, BindingResult binding) {
		this.auditorService.loggedAsAuditor();
		Auditor auditor = this.auditorService.loggedAuditor();
		Audit result = new Audit();

		if (audit.getId() == 0) {
			result = audit;
			result.setAuditor(auditor);

			Date thisMoment = new Date();
			thisMoment.setTime(thisMoment.getTime() - 1);

			result.setMomentCreation(thisMoment);

		} else {
			Audit copy = this.findOne(audit.getId());

			result.setFreeText(audit.getFreeText());
			result.setIsDraftMode(audit.getIsDraftMode());
			result.setScore(audit.getScore());

			result.setPosition(copy.getPosition());
			result.setMomentCreation(copy.getMomentCreation());
			result.setAuditor(copy.getAuditor());

			result.setId(copy.getId());
			result.setVersion(copy.getVersion());
		}
		this.validator.validate(result, binding);
		return result;
	}

	public void deleteAudit(Audit audit) {
		this.auditorService.loggedAsAuditor();
		Auditor loggedAuditor = this.auditorService.loggedAuditor();

		Assert.isTrue(loggedAuditor.getAudits().contains(audit));
		Assert.isTrue(this.findOne(audit.getId()).getIsDraftMode());

		this.auditRepository.delete(audit);

	}

	public void delete(Audit audit) {
		this.messageService.notificationAuditDeleted(audit);
		this.auditRepository.delete(audit);

	}

	public void deleteAllAudits() {

		Auditor auditor = new Auditor();

		auditor = this.auditorService.loggedAuditor();

		List<Audit> audits = new ArrayList<Audit>();
		audits = auditor.getAudits();
		
		// CONTROL_CHECK
		List<Reckon> toDelete = new ArrayList<> ();
		for(Audit a:audits) {
			for(Reckon c:a.getReckon()) {
				toDelete.add(c);
			}
		}
		for(Audit a:audits) {
			a.setReckon(new ArrayList<Reckon> ());
			this.save(a);
		}
		for(Reckon c:toDelete) {
			this.reckonService.delete(c);
		}

		this.auditRepository.deleteInBatch(audits);
	}

	public void deleteInBatch(List<Audit> audits) {
		this.auditRepository.deleteInBatch(audits);

	}
}
