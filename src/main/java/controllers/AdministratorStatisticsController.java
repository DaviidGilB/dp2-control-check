
package controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import services.AdminService;
import domain.Company;
import domain.Position;
import domain.Provider;
import domain.Rookie;

@Controller
@RequestMapping("/statistics/administrator")
public class AdministratorStatisticsController {

	@Autowired
	private AdminService	adminService;


	public AdministratorStatisticsController() {
		super();
	}

	@RequestMapping(value = "/show", method = RequestMethod.GET)
	public ModelAndView statistics() {
		try {
			ModelAndView result;
			this.adminService.loggedAsAdmin();

			String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();

			//-------------------------------ACME-HACKER-RANK---------------------
			List<Float> statisticsCurriculum = this.adminService.showStatisticsOfCurriculum();
			List<Float> statisticsFinder = this.adminService.showStatisticsOfFinder();
			List<Float> statisticsPositionsCompany = this.adminService.showStatisticsOfPositionsPerCompany();
			List<Float> statisticsApplicationsRookie = this.adminService.showStatisticsOfApplicationsPerRookie();
			List<Float> statisticsSalaries = this.adminService.showStatisticsOfSalaries();

			List<Company> companiesMorePositions = this.adminService.companiesMorePositions();
			List<Rookie> rookiesMoreApplications = this.adminService.rookiesMoreApplications();
			List<Position> bestPositionsSalary = this.adminService.bestSalaryPositions();
			List<Position> worstPositionsSalary = this.adminService.worstSalaryPositions();

			//------------------------------ACME-ROOKIES--------------------------
			//LEVEL C
			List<Company> companiesOrderedByScore = this.adminService.companiesOrderedByScore();
			Double averageSalaryOfferedByThePositionsThatHaveTheHighestAverageAuditScoreAndKnuckles = this.adminService.averageSalaryOfferedByThePositionsThatHaveTheHighestAverageAuditScoreAndKnuckles();

			//Position
			Double positionScoreMax = this.adminService.positionScoreMax();
			Double positionScoreMin = this.adminService.positionScoreMin();
			Double positionScoreStddev = this.adminService.positionScoreStddev();
			Double positionScoreAvg = this.adminService.positionScoreAvg();
			//Company
			Double companyScoreMax = this.adminService.companyScoreMax();
			Double companyScoreMin = this.adminService.companyScoreMin();
			Double companyScoreAvg = this.adminService.companyScoreAvg();
			Double companyScoreStddev = this.adminService.companyScoreStddev();

			//LEVEL B
			List<Double> itemsPerProvider = this.adminService.itemsPerProvider();
			List<Provider> providerTermsofItemsOrdered = this.adminService.providerTermsofItemsOrdered();

			//LEVEL A
			List<Double> sponsorshipsPerProvider = this.adminService.sponsorshipsPerProvider();

			List<Double> sponsorshipsPerPosition = this.adminService.sponsorshipsPerPosition();

			List<Provider> providers10PercentMoreSponsorships = this.adminService.providers10PercentMoreSponsorships();

			result = new ModelAndView("statistics/administrator/show");

			//ACME-HACKER-RANK
			result.addObject("statisticsPositionsCompany", statisticsPositionsCompany);
			result.addObject("statisticsApplicationsRookie", statisticsApplicationsRookie);
			result.addObject("statisticsSalaries", statisticsSalaries);
			result.addObject("companiesMorePositions", companiesMorePositions);
			result.addObject("rookiesMoreApplications", rookiesMoreApplications);
			result.addObject("bestPositionsSalary", bestPositionsSalary);
			result.addObject("worstPositionsSalary", worstPositionsSalary);
			result.addObject("statisticsCurriculum", statisticsCurriculum);
			result.addObject("statisticsFinder", statisticsFinder);
			result.addObject("locale", locale);

			//ACME-ROOKIES

			//LEVEL C
			result.addObject("companiesOrderedByScore", companiesOrderedByScore);
			result.addObject("averageSalaryOfferedByThePositionsThatHaveTheHighestAverageAuditScoreAndKnuckles", averageSalaryOfferedByThePositionsThatHaveTheHighestAverageAuditScoreAndKnuckles);

			//Position
			result.addObject("positionScoreMax", positionScoreMax);
			result.addObject("positionScoreMin", positionScoreMin);
			result.addObject("positionScoreStddev", positionScoreStddev);
			result.addObject("positionScoreAvg", positionScoreAvg);

			//Company
			result.addObject("companyScoreMax", companyScoreMax);
			result.addObject("companyScoreMin", companyScoreMin);
			result.addObject("companyScoreAvg", companyScoreAvg);
			result.addObject("companyScoreStddev", companyScoreStddev);

			//LEVEL B
			result.addObject("itemsPerProvider", itemsPerProvider);
			result.addObject("providerTermsofItemsOrdered", providerTermsofItemsOrdered);

			//LEVEL A
			result.addObject("sponsorshipsPerProvider", sponsorshipsPerProvider);
			result.addObject("sponsorshipsPerPosition", sponsorshipsPerPosition);
			result.addObject("providers10PercentMoreSponsorships", providers10PercentMoreSponsorships);

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}
	}
}
