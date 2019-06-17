
package services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.transaction.Transactional;

import org.apache.commons.lang.RandomStringUtils;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.ControlEntityRepository;
import repositories.PositionRepository;
import domain.Actor;
import domain.Application;
import domain.Audit;
import domain.Auditor;
import domain.Company;
import domain.ControlEntity;
import domain.Position;
import domain.Problem;
import domain.Status;
import forms.FormObjectPositionProblemCheckbox;

@Service
@Transactional
public class ControlEntityService {

	@Autowired
	private ControlEntityRepository	controlEntityRepository;
	
	@Autowired
	private AuditService auditService;
	
	@Autowired
	private CompanyService companyService;
	
	@Autowired
	private Validator validator;
	
	@Autowired
	private AuditorService auditorService;

	// CRUDS
	
	public List<ControlEntity> findAll() {
		return this.controlEntityRepository.findAll();
	}

	public ControlEntity save(ControlEntity controlEntity) {
		return this.controlEntityRepository.save(controlEntity);
	}

	public ControlEntity findOne(int controlEntityId) {
		return this.controlEntityRepository.findOne(controlEntityId);
	}

	public void delete(ControlEntity controlEntity) {
		this.controlEntityRepository.delete(controlEntity);
	}

	// OTHER OPERATIONS
	
	public List<ControlEntity> getFinalControlEntityOfAudit(Integer auditId) {
		List<ControlEntity> controlEntity = this.controlEntityRepository.getFinalControlEntityOfAudit(auditId);
		Assert.notNull(controlEntity);
		Assert.notEmpty(controlEntity);
		return controlEntity;
	}

	public List<ControlEntity> getAllControlEntityOfAuditor(Integer auditorId) {
		Auditor auditor = this.auditorService.securityAndAuditor();
		Assert.isTrue(auditor.getId() == auditorId);
		return this.controlEntityRepository.getAllControlEntityOfAuditor(auditorId);
	}
	
	public List<ControlEntity> getAllControlEntityOfAuditorAndAudit(Integer auditorId, Integer auditId) {
		Auditor auditor = this.auditorService.securityAndAuditor();
		Assert.isTrue(auditor.getId() == auditorId);
		return this.controlEntityRepository.getAllControlEntityOfAuditorAndAudit(auditorId, auditId);
	}

	public Audit checkAuditorAndAudit(Integer auditorId, Integer auditId) {
		return this.controlEntityRepository.checkAuditorAndAudit(auditorId, auditId);
	}
	
	public ControlEntity checkAuditorAndControlEntity(Integer auditorId, Integer controlEntityId) {
		return this.controlEntityRepository.checkAuditorAndControlEntity(auditorId, controlEntityId);
	}

	public ControlEntity create() {
		ControlEntity controlEntity = new ControlEntity();
		
		controlEntity.setIsDraftMode(true);
		controlEntity.setBody("");
		controlEntity.setPicture("");
		controlEntity.setPublicationMoment(null);
		controlEntity.setTicker("");
		
		return controlEntity;
	}
	
	public String generateTicker() {
		String res = "";
		Date date = null;
		String date1;
		String date2 = LocalDate.now().toString();
		String gen = RandomStringUtils.randomAlphanumeric(6);
		List<String> tickers = this.controlEntityRepository.getAllTickers();
		SimpleDateFormat df_in = new SimpleDateFormat("yyMMdd");
		SimpleDateFormat df_output = new SimpleDateFormat("yyyy-MM-dd");
		try {
			date = df_output.parse(date2);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		date1 = df_in.format(date);
		res = res + date1 + "-" + gen;

		if (tickers.contains(res)) {
			return this.generateTicker();
		}
		return res;
	}

	public void validate(ControlEntity controlEntity, BindingResult binding) {
		this.validator.validate(controlEntity, binding);
	}

	public void addControlEntity(ControlEntity controlEntity, Integer auditId) {
		Auditor auditor = this.auditorService.securityAndAuditor();
		Audit audit = this.controlEntityRepository.checkAuditorAndAudit(auditor.getId(), auditId);
		Assert.notNull(audit);
		Assert.isTrue(!audit.getIsDraftMode());
		
		if(!controlEntity.getIsDraftMode()) {
			Calendar c1 = Calendar.getInstance();
			c1.add(Calendar.MILLISECOND, -1);
			Date date = c1.getTime();
			controlEntity.setPublicationMoment(date);
		}
		
		List<ControlEntity> list = audit.getControlEntity();
		list.add(controlEntity);
		audit.setControlEntity(list);
		
		this.auditService.save(audit);
	}
	
	public void updateControlEntity(ControlEntity controlEntity) {
		Auditor auditor = this.auditorService.securityAndAuditor();
		Audit audit = this.controlEntityRepository.checkAuditorAndAudit(auditor.getId(), this.controlEntityRepository.getAuditOfControlEntity(controlEntity.getId()).getId());
		Assert.notNull(audit);
		Assert.isTrue(!audit.getIsDraftMode());
		
		if(!controlEntity.getIsDraftMode()) {
			Calendar c1 = Calendar.getInstance();
			c1.add(Calendar.MILLISECOND, -1);
			Date date = c1.getTime();
			controlEntity.setPublicationMoment(date);
		}

		ControlEntity controlEntityFounded = this.findOne(controlEntity.getId());
		Assert.isTrue(controlEntityFounded.getIsDraftMode());
		
		this.save(controlEntity);
	}

	public void deleteControlEntity(ControlEntity controlEntity) {
		Auditor auditor = this.auditorService.securityAndAuditor();
		ControlEntity controlEntityFounded = this.checkAuditorAndControlEntity(auditor.getId(), controlEntity.getId());
		Assert.notNull(controlEntityFounded);
		Assert.isTrue(controlEntityFounded.getIsDraftMode());
		
		Audit audit = this.controlEntityRepository.getAuditOfControlEntity(controlEntityFounded.getId());
		List<ControlEntity> list = audit.getControlEntity();
		list.remove(controlEntityFounded);
		audit.setControlEntity(list);
		this.auditService.save(audit);
		
		this.delete(controlEntityFounded);
	}
	

}
