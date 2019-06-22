
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
	private ApplicationService applicationService;
	
	@Autowired
	private CompanyService companyService;
	
	@Autowired
	private Validator validator;

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
	
	public List<ControlEntity> getFinalControlEntityOfApplication(Integer applicationId) {
		List<ControlEntity> controlEntity = this.controlEntityRepository.getFinalControlEntityOfApplication(applicationId);
		Assert.notNull(controlEntity);
		Assert.notEmpty(controlEntity);
		return controlEntity;
	}

	public List<ControlEntity> getAllControlEntityOfCompany(Integer companyId) {
		Company company = this.companyService.securityAndCompany();
		Assert.isTrue(company.getId() == companyId);
		return this.controlEntityRepository.getAllControlEntityOfCompany(companyId);
	}
	
	public List<ControlEntity> getAllControlEntityOfCompanyAndApplication(Integer companyId, Integer applicationId) {
		Company company = this.companyService.securityAndCompany();
		Assert.isTrue(company.getId() == companyId);
		return this.controlEntityRepository.getAllControlEntityOfCompanyAndApplication(companyId, applicationId);
	}

	public Application checkCompanyAndApplication(Integer companyId, Integer applicationId) {
		return this.controlEntityRepository.checkCompanyAndApplication(companyId, applicationId);
	}
	
	public ControlEntity checkCompanyAndControlEntity(Integer companyId, Integer controlEntityId) {
		return this.controlEntityRepository.checkCompanyAndControlEntity(companyId, controlEntityId);
	}

	public ControlEntity create() {
		ControlEntity controlEntity = new ControlEntity();
		
		controlEntity.setIsDraftMode(true);
		controlEntity.setBody("");
		controlEntity.setPicture("");
		controlEntity.setPublicationMoment(null);
		controlEntity.setTicker(this.generateTicker());
		
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

	public void addControlEntity(ControlEntity controlEntity, Integer applicationId) {
		Company company = this.companyService.securityAndCompany();
		Application application = this.controlEntityRepository.checkCompanyAndApplication(company.getId(), applicationId);
		Assert.notNull(application);
		Assert.isTrue(application.getStatus().equals(Status.ACCEPTED));
		
		if(!controlEntity.getIsDraftMode()) {
			Calendar c1 = Calendar.getInstance();
			c1.add(Calendar.MILLISECOND, -1);
			Date date = c1.getTime();
			controlEntity.setPublicationMoment(date);
		}
		
		List<ControlEntity> list = application.getControlEntity();
		list.add(controlEntity);
		application.setControlEntity(list);
		
		this.applicationService.save(application);
	}
	
	public void updateControlEntity(ControlEntity controlEntity) {
		Company company = this.companyService.securityAndCompany();
		Application application = this.controlEntityRepository.checkCompanyAndApplication(company.getId(), this.controlEntityRepository.getApplicationOfControlEntity(controlEntity.getId()).getId());
		Assert.notNull(application);
		Assert.isTrue(application.getStatus().equals(Status.ACCEPTED));
		
		if(!controlEntity.getIsDraftMode()) {
			Calendar c1 = Calendar.getInstance();
			c1.add(Calendar.MILLISECOND, -1);
			Date date = c1.getTime();
			controlEntity.setPublicationMoment(date);
		}

		ControlEntity controlEntityFounded = this.findOne(controlEntity.getId());
		Assert.isTrue(controlEntityFounded.getIsDraftMode());
		
		controlEntity.setVersion(controlEntityFounded.getVersion());
		
		this.save(controlEntity);
	}

	public void deleteControlEntity(ControlEntity controlEntity) {
		Company company = this.companyService.securityAndCompany();
		ControlEntity controlEntityFounded = this.checkCompanyAndControlEntity(company.getId(), controlEntity.getId());
		Assert.notNull(controlEntityFounded);
		Assert.isTrue(controlEntityFounded.getIsDraftMode());
		
		Application application = this.controlEntityRepository.getApplicationOfControlEntity(controlEntityFounded.getId());
		List<ControlEntity> list = application.getControlEntity();
		list.remove(controlEntityFounded);
		application.setControlEntity(list);
		this.applicationService.save(application);
		
		this.delete(controlEntityFounded);
	}
	

}
