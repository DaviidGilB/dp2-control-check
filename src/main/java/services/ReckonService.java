
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

import repositories.ReckonRepository;
import repositories.PositionRepository;
import domain.Actor;
import domain.Application;
import domain.Audit;
import domain.Company;
import domain.Reckon;
import domain.Position;
import domain.Problem;
import domain.Status;
import forms.FormObjectPositionProblemCheckbox;

@Service
@Transactional
public class ReckonService {

	@Autowired
	private ReckonRepository	reckonRepository;
	
	@Autowired
	private AuditService auditService;
	
	@Autowired
	private CompanyService companyService;
	
	@Autowired
	private Validator validator;

	// CRUDS
	
	public List<Reckon> findAll() {
		return this.reckonRepository.findAll();
	}

	public Reckon save(Reckon reckon) {
		return this.reckonRepository.save(reckon);
	}

	public Reckon findOne(int reckonId) {
		return this.reckonRepository.findOne(reckonId);
	}

	public void delete(Reckon reckon) {
		this.reckonRepository.delete(reckon);
	}

	// OTHER OPERATIONS
	
	public List<Reckon> getFinalReckonOfAudit(Integer auditId) {
		List<Reckon> reckon = this.reckonRepository.getFinalReckonOfAudit(auditId);
		Assert.notNull(reckon);
		Assert.notEmpty(reckon);
		return reckon;
	}

	public List<Reckon> getAllReckonOfCompany(Integer companyId) {
		Company company = this.companyService.securityAndCompany();
		Assert.isTrue(company.getId() == companyId);
		return this.reckonRepository.getAllReckonOfCompany(companyId);
	}
	
	public List<Reckon> getAllReckonOfCompanyAndAudit(Integer companyId, Integer auditId) {
		Company company = this.companyService.securityAndCompany();
		Assert.isTrue(company.getId() == companyId);
		return this.reckonRepository.getAllReckonOfCompanyAndAudit(companyId, auditId);
	}

	public Audit checkCompanyAndAudit(Integer companyId, Integer auditId) {
		return this.reckonRepository.checkCompanyAndAudit(companyId, auditId);
	}
	
	public Reckon checkCompanyAndReckon(Integer companyId, Integer reckonId) {
		return this.reckonRepository.checkCompanyAndReckon(companyId, reckonId);
	}

	public Reckon create() {
		Reckon reckon = new Reckon();
		
		reckon.setIsDraftMode(true);
		reckon.setBody("");
		reckon.setPicture("");
		reckon.setPublicationMoment(null);
		reckon.setTicker(this.generateTicker());
		
		return reckon;
	}
	
	public String generateTicker() {
		String res = "";
		Date date = null;
		String date1;
		String date2 = LocalDate.now().toString();
		String gen = RandomStringUtils.randomNumeric(5);
		List<String> tickers = this.reckonRepository.getAllTickers();
		SimpleDateFormat df_in = new SimpleDateFormat("yyMM/dd");
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

	public void validate(Reckon reckon, BindingResult binding) {
		this.validator.validate(reckon, binding);
	}

	public void addReckon(Reckon reckon, Integer auditId) {
		Company company = this.companyService.securityAndCompany();
		Audit audit = this.reckonRepository.checkCompanyAndAudit(company.getId(), auditId);
		Assert.notNull(audit);
		Assert.isTrue(!audit.getIsDraftMode());
		
		if(!reckon.getIsDraftMode()) {
			Calendar c1 = Calendar.getInstance();
			c1.add(Calendar.MILLISECOND, -1);
			Date date = c1.getTime();
			reckon.setPublicationMoment(date);
		}
		
		List<Reckon> list = audit.getReckon();
		list.add(reckon);
		audit.setReckon(list);
		
		this.auditService.save(audit);
	}
	
	public void updateReckon(Reckon reckon) {
		Company company = this.companyService.securityAndCompany();
		Audit audit = this.reckonRepository.checkCompanyAndAudit(company.getId(), this.reckonRepository.getAuditOfReckon(reckon.getId()).getId());
		Assert.notNull(audit);
		Assert.isTrue(!audit.getIsDraftMode());
		
		if(!reckon.getIsDraftMode()) {
			Calendar c1 = Calendar.getInstance();
			c1.add(Calendar.MILLISECOND, -1);
			Date date = c1.getTime();
			reckon.setPublicationMoment(date);
		}

		Reckon reckonFounded = this.findOne(reckon.getId());
		Assert.isTrue(reckonFounded.getIsDraftMode());
		
		reckon.setVersion(reckonFounded.getVersion());
		
		this.save(reckon);
	}

	public void deleteReckon(Reckon reckon) {
		Company company = this.companyService.securityAndCompany();
		Reckon reckonFounded = this.checkCompanyAndReckon(company.getId(), reckon.getId());
		Assert.notNull(reckonFounded);
		Assert.isTrue(reckonFounded.getIsDraftMode());
		
		Audit audit = this.reckonRepository.getAuditOfReckon(reckonFounded.getId());
		List<Reckon> list = audit.getReckon();
		list.remove(reckonFounded);
		audit.setReckon(list);
		this.auditService.save(audit);
		
		this.delete(reckonFounded);
	}
	

}
