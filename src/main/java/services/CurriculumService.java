package services;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;

import domain.Curriculum;
import domain.EducationData;
import domain.MiscellaneousData;
import domain.PersonalData;
import domain.PositionData;
import domain.Rookie;
import forms.FormObjectCurriculumPersonalData;
import repositories.CurriculumRepository;

@Service
@Transactional
public class CurriculumService {

	@Autowired
	private CurriculumRepository curriculumRepository;

	@Autowired
	private PersonalDataService personalDataService;

	@Autowired
	private PositionDataService positionDataService;

	@Autowired
	private MiscellaneousDataService miscellaneousDataService;

	@Autowired
	private EducationDataService educationDataService;

	@Autowired
	private RookieService rookieService;

	public Curriculum findOne(int id) {
		return this.curriculumRepository.findOne(id);
	}

	public Curriculum copyCurriculum(Curriculum curriculum) {
		Curriculum copy = new Curriculum();
		PersonalData copyP = new PersonalData();
		List<PositionData> copyPos = new ArrayList<PositionData>();
		List<MiscellaneousData> copyMis = new ArrayList<MiscellaneousData>();
		List<EducationData> copyEd = new ArrayList<EducationData>();

		PositionData pos = new PositionData();
		MiscellaneousData mis = new MiscellaneousData();
		EducationData edu = new EducationData();

		copyP.setFullName(curriculum.getPersonalData().getFullName());
		copyP.setGitHubProfile(curriculum.getPersonalData().getGitHubProfile());
		copyP.setLinkedinProfile(curriculum.getPersonalData().getLinkedinProfile());
		copyP.setPhoneNumber(curriculum.getPersonalData().getPhoneNumber());
		copyP.setStatement(curriculum.getPersonalData().getStatement());
		this.personalDataService.save(copyP);

		for (PositionData p : curriculum.getPositionData()) {
			pos.setDescription(p.getDescription());
			pos.setEndDate(p.getEndDate());
			pos.setStartDate(p.getStartDate());
			pos.setTitle(p.getTitle());

			this.positionDataService.save(pos);
			copyPos.add(pos);
			pos = new PositionData();
		}

		for (MiscellaneousData p : curriculum.getMiscellaneousData()) {
			mis.setAttachments(p.getAttachments());
			mis.setFreeText(p.getFreeText());

			this.miscellaneousDataService.save(mis);
			copyMis.add(mis);
			mis = new MiscellaneousData();
		}

		for (EducationData p : curriculum.getEducationData()) {
			edu.setDegree(p.getDegree());
			edu.setEndDate(p.getEndDate());
			edu.setInstitution(p.getInstitution());
			edu.setStartDate(p.getStartDate());
			edu.setMark(p.getMark());

			this.educationDataService.save(edu);
			copyEd.add(edu);
			edu = new EducationData();
		}

		copy.setTitle(curriculum.getTitle());
		copy.setPositionData(copyPos);
		copy.setPersonalData(copyP);
		copy.setMiscellaneousData(copyMis);
		copy.setEducationData(copyEd);

		Curriculum saved = this.curriculumRepository.save(copy);

		return saved;
	}

	public List<Curriculum> getCurriculumsOfRookie(int rookieId) {
		return this.curriculumRepository.getCurriculumsOfRookie(rookieId);
	}

	public List<Curriculum> getCurriculumsOfLoggedRookie() {
		Rookie rookie = this.rookieService.securityAndRookie();
		List<Curriculum> curriculums = this.getCurriculumsOfRookie(rookie.getId());

		return curriculums;
	}

	public Curriculum getCurriculumOfRookie(int rookieId, int curriculumId) {
		return this.curriculumRepository.getCurriculumOfRookie(rookieId, curriculumId);
	}

	public Curriculum getCurriculumOfLoggedRookie(int curriculumId) {
		Rookie rookie = this.rookieService.securityAndRookie();
		Curriculum curriculum = this.getCurriculumOfRookie(rookie.getId(), curriculumId);
		Assert.notNull(curriculum);
		return curriculum;
	}

	public Curriculum reconstruct(FormObjectCurriculumPersonalData formObject, BindingResult binding,
			PersonalData personalData) {
		Curriculum curriculum = new Curriculum();

		if (formObject.getId() > 0) {
			Curriculum curriculumFounded = this.findOne(formObject.getId());

			curriculum.setId(curriculumFounded.getId());
			curriculum.setVersion(curriculumFounded.getVersion());

			curriculum.setPositionData(curriculumFounded.getPositionData());
			curriculum.setMiscellaneousData(curriculumFounded.getMiscellaneousData());
			curriculum.setEducationData(curriculumFounded.getEducationData());
		}
		curriculum.setTitle(formObject.getTitle());
		curriculum.setPersonalData(personalData);

		return curriculum;
	}

	public Curriculum save(Curriculum curriculum) {
		return this.curriculumRepository.save(curriculum);
	}

	public void delete(Curriculum curriculum) {
		this.curriculumRepository.delete(curriculum);
	}

	public void deleteCurriculumAsRookie(int curriculumId) {
		Rookie rookie = this.rookieService.securityAndRookie();

		Curriculum curriculum = this.getCurriculumOfRookie(rookie.getId(), curriculumId);
		Assert.notNull(curriculum);

		List<Curriculum> curriculums = rookie.getCurriculums();

		curriculums.remove(curriculum);
		rookie.setCurriculums(curriculums);
		this.rookieService.save(rookie);

		this.delete(curriculum);
	}

	public Curriculum getCurriculumOfPositionData(int positionDataId) {
		return this.curriculumRepository.getCurriculumOfPositionData(positionDataId);
	}

	public Curriculum getCurriculumOfEducationData(int educationDataId) {
		return this.curriculumRepository.getCurriculumOfEducationData(educationDataId);
	}

	public List<Curriculum> findAll() {
		return this.curriculumRepository.findAll();
	}

	public Curriculum getCurriculumOfMiscellaneousData(int miscellaneousDataId) {
		return this.curriculumRepository.getCurriculumOfMiscellaneousData(miscellaneousDataId);
	}

	public String curriculumToStringExport() {
		String res = "";
		Rookie rookie = this.rookieService.loggedRookie();
		List<Curriculum> curriculums = new ArrayList<Curriculum>();
		StringBuilder sb = new StringBuilder();
		curriculums = rookie.getCurriculums();

		/*
		 * + c.getTitle() + " EducationalData: " + c.getEducationData().toString() +
		 * " Miscellaneous data: " + c.getMiscellaneousData().toString() +
		 * "Position data" + c.getPositionData().toString(
		 */

		Integer cont = 1;

		for (Curriculum c : curriculums) {
			sb.append("Curriculum" + cont + ": ").append(System.getProperty("line.separator"));
			sb.append("Title: " + c.getTitle()).append(System.getProperty("line.separator"));
			sb.append("Full name: " + c.getPersonalData().getFullName()).append(System.getProperty("line.separator"));
			sb.append("GitHub profile: " + c.getPersonalData().getGitHubProfile())
					.append(System.getProperty("line.separator"));
			sb.append("Linkedin profile: " + c.getPersonalData().getLinkedinProfile())
					.append(System.getProperty("line.separator"));
			sb.append("Statement: " + c.getPersonalData().getStatement()).append(System.getProperty("line.separator"));
			sb.append("Phone number: " + c.getPersonalData().getPhoneNumber())
					.append(System.getProperty("line.separator"));
			sb.append(System.getProperty("line.separator"));

			sb.append("Titles of Positions data: " + this.curriculumRepository.getTitlesOfPositionDatas(rookie))
					.append(System.getProperty("line.separator"));

			sb.append(System.getProperty("line.separator"));

			sb.append("Degrees of Educations data: " + this.curriculumRepository.getDegreesOfEducationalData(rookie))
					.append(System.getProperty("line.separator"));

			sb.append(System.getProperty("line.separator"));

			sb.append("Free texts of Miscellaneous data: "
					+ this.curriculumRepository.getFreeTestOfMiscellaneousData(rookie))
					.append(System.getProperty("line.separator"));

			sb.append(System.getProperty("line.separator"));

			cont++;
		}
		return sb.toString();
	}

	public void flush() {
		this.curriculumRepository.flush();
	}

	public void deleteInBatch(List<Curriculum> curriculums) {
		this.curriculumRepository.deleteInBatch(curriculums);
	}
}
