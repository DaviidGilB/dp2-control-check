
package controllers;

import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import services.AdminService;
import services.AuditorService;
import services.CompanyService;
import services.CurriculumService;
import services.ProviderService;
import services.RookieService;
import domain.Admin;
import domain.Auditor;
import domain.Company;
import domain.Provider;
import domain.Rookie;

@Controller
@RequestMapping("/export")
public class ExportDataController {

	@Autowired
	RookieService		rookieService;

	@Autowired
	CurriculumService	curriculumService;

	@Autowired
	AdminService		adminService;

	@Autowired
	CompanyService		companyService;

	@Autowired
	AuditorService		auditorService;

	@Autowired
	ProviderService		providerService;


	@RequestMapping(value = "/rookie", method = RequestMethod.GET)
	public @ResponseBody
	ModelAndView export(@RequestParam(value = "id", defaultValue = "-1", required = false) String id, HttpServletResponse response) throws IOException {
		
		ModelAndView result;
		
		try {
			this.rookieService.loggedAsRookie();

			Assert.isTrue(StringUtils.isNumeric(id));
			int idInt = Integer.parseInt(id);

			Rookie rookie = new Rookie();
			rookie = this.rookieService.findOne(idInt);

			// Defines un StringBuilder para construir tu string
			StringBuilder sb = new StringBuilder();

			// linea
			sb.append("Personal data:").append(System.getProperty("line.separator"));
			sb.append("Name: " + rookie.getName()).append(System.getProperty("line.separator"));
			sb.append("Surname: " + rookie.getSurname()).append(System.getProperty("line.separator"));
			sb.append("Address: " + rookie.getAddress()).append(System.getProperty("line.separator"));
			sb.append("Email: " + rookie.getEmail()).append(System.getProperty("line.separator"));
			sb.append("Photo: " + rookie.getPhoto()).append(System.getProperty("line.separator"));
			sb.append("VAT number: " + rookie.getVATNumber()).append(System.getProperty("line.separator"));

			sb.append(System.getProperty("line.separator"));
			sb.append("SocialProfiles: ").append(System.getProperty("line.separator"));
			sb.append(System.getProperty("line.separator"));
			// Este metodo te muestra los socialProfiles de la misma manera que el resto del
			// documento
			sb.append(this.rookieService.SocialProfilesToString()).append(System.getProperty("line.separator"));

			sb.append(System.getProperty("line.separator"));
			sb.append("Curriculums: ").append(System.getProperty("line.separator"));
			sb.append(System.getProperty("line.separator"));
			sb.append(this.curriculumService.curriculumToStringExport()).append(System.getProperty("line.separator"));

			if (rookie == null || this.rookieService.loggedRookie().getId() != idInt)
				return new ModelAndView("redirect:/");

			// Defines el nombre del archivo y la extension
			response.setContentType("text/txt");
			response.setHeader("Content-Disposition", "attachment;filename=exportDataRookie.txt");

			// Con estos comandos permites su descarga cuando clickas
			ServletOutputStream outStream = response.getOutputStream();
			outStream.println(sb.toString());
			outStream.flush();
			outStream.close();

			// El return no llega nunca, es del metodo viejo
			result = new ModelAndView("redirect:/");
		} catch(Throwable oops) {
			result = new ModelAndView("redirect:/");
		}
		return result;
	}

	@RequestMapping(value = "/admin", method = RequestMethod.GET)
	public @ResponseBody
	ModelAndView exportAdmin(@RequestParam(value = "id", defaultValue = "-1", required = false) String id, HttpServletResponse response) throws IOException {

		try {
			this.adminService.loggedAsAdmin();

			Assert.isTrue(StringUtils.isNumeric(id));
			int idInt = Integer.parseInt(id);
			
			Admin admin = new Admin();
			admin = this.adminService.findOne(idInt);

			// Defines un StringBuilder para construir tu string
			StringBuilder sb = new StringBuilder();

			// linea
			sb.append("Personal data:").append(System.getProperty("line.separator"));
			sb.append("Name: " + admin.getName()).append(System.getProperty("line.separator"));
			sb.append("Surname: " + admin.getSurname()).append(System.getProperty("line.separator"));
			sb.append("Address: " + admin.getAddress()).append(System.getProperty("line.separator"));
			sb.append("Email: " + admin.getEmail()).append(System.getProperty("line.separator"));
			sb.append("Photo: " + admin.getPhoto()).append(System.getProperty("line.separator"));
			sb.append("VAT number: " + admin.getVATNumber()).append(System.getProperty("line.separator"));

			sb.append(System.getProperty("line.separator"));
			sb.append("SocialProfiles: ").append(System.getProperty("line.separator"));
			sb.append(System.getProperty("line.separator"));
			// Este metodo te muestra los socialProfiles de la misma manera que el resto del
			// documento
			sb.append(this.rookieService.SocialProfilesToString()).append(System.getProperty("line.separator"));

			if (admin == null || this.adminService.loggedAdmin().getId() != idInt)
				return new ModelAndView("redirect:/");;

			// Defines el nombre del archivo y la extension
			response.setContentType("text/txt");
			response.setHeader("Content-Disposition", "attachment;filename=exportDataAdmin.txt");

			// Con estos comandos permites su descarga cuando clickas
			ServletOutputStream outStream = response.getOutputStream();
			outStream.println(sb.toString());
			outStream.flush();
			outStream.close();

			// El return no llega nunca, es del metodo viejo
			return new ModelAndView("redirect:/");
		} catch(Throwable oops) {
			return new ModelAndView("redirect:/");
		}
		
	}

	@RequestMapping(value = "/company", method = RequestMethod.GET)
	public @ResponseBody
	ModelAndView exportCompany(@RequestParam(value = "id", defaultValue = "-1", required = false) String id, HttpServletResponse response) throws IOException {

		try {
			Assert.isTrue(StringUtils.isNumeric(id));
			int idInt = Integer.parseInt(id);
			
			this.companyService.loggedCompany();

			Company company = new Company();
			company = this.companyService.findOne(idInt);

			// Defines un StringBuilder para construir tu string
			StringBuilder sb = new StringBuilder();

			// linea
			sb.append("Personal data:").append(System.getProperty("line.separator"));
			sb.append("Name: " + company.getName()).append(System.getProperty("line.separator"));
			sb.append("Surname: " + company.getSurname()).append(System.getProperty("line.separator"));
			sb.append("Address: " + company.getAddress()).append(System.getProperty("line.separator"));
			sb.append("Email: " + company.getEmail()).append(System.getProperty("line.separator"));
			sb.append("Photo: " + company.getPhoto()).append(System.getProperty("line.separator"));
			sb.append("VAT number: " + company.getVATNumber()).append(System.getProperty("line.separator"));

			sb.append(System.getProperty("line.separator"));
			sb.append("SocialProfiles: ").append(System.getProperty("line.separator"));
			sb.append(System.getProperty("line.separator"));
			// Este metodo te muestra los socialProfiles de la misma manera que el resto del
			// documento
			sb.append(this.rookieService.SocialProfilesToString()).append(System.getProperty("line.separator"));

			if (company == null || this.companyService.loggedCompany().getId() != idInt)
				return new ModelAndView("redirect:/");

			// Defines el nombre del archivo y la extension
			response.setContentType("text/txt");
			response.setHeader("Content-Disposition", "attachment;filename=exportDataCompany.txt");

			// Con estos comandos permites su descarga cuando clickas
			ServletOutputStream outStream = response.getOutputStream();
			outStream.println(sb.toString());
			outStream.flush();
			outStream.close();

			// El return no llega nunca, es del metodo viejo
			return new ModelAndView("redirect:/");
		} catch(Throwable oops) {
			return new ModelAndView("redirect:/");
		}
	}

	@RequestMapping(value = "/auditor", method = RequestMethod.GET)
	public @ResponseBody
	ModelAndView exportAuditor(@RequestParam(value = "id", defaultValue = "-1", required = false) String id, HttpServletResponse response) throws IOException {

		try {
			Assert.isTrue(StringUtils.isNumeric(id));
			int idInt = Integer.parseInt(id);
			
			this.auditorService.loggedAsAuditor();

			Auditor auditor = new Auditor();
			auditor = this.auditorService.findOne(idInt);

			// Defines un StringBuilder para construir tu string
			StringBuilder sb = new StringBuilder();

			// linea
			sb.append("Personal data:").append(System.getProperty("line.separator"));
			sb.append("Name: " + auditor.getName()).append(System.getProperty("line.separator"));
			sb.append("Surname: " + auditor.getSurname()).append(System.getProperty("line.separator"));
			sb.append("Address: " + auditor.getAddress()).append(System.getProperty("line.separator"));
			sb.append("Email: " + auditor.getEmail()).append(System.getProperty("line.separator"));
			sb.append("Photo: " + auditor.getPhoto()).append(System.getProperty("line.separator"));
			sb.append("VAT number: " + auditor.getVATNumber()).append(System.getProperty("line.separator"));

			sb.append(System.getProperty("line.separator"));
			sb.append("SocialProfiles: ").append(System.getProperty("line.separator"));
			sb.append(System.getProperty("line.separator"));
			// Este metodo te muestra los socialProfiles de la misma manera que el resto del
			// documento
			sb.append(this.rookieService.SocialProfilesToString()).append(System.getProperty("line.separator"));

			if (auditor == null || this.auditorService.loggedAuditor().getId() != idInt)
				return new ModelAndView("redirect:/");

			// Defines el nombre del archivo y la extension
			response.setContentType("text/txt");
			response.setHeader("Content-Disposition", "attachment;filename=exportDataAuditor.txt");

			// Con estos comandos permites su descarga cuando clickas
			ServletOutputStream outStream = response.getOutputStream();
			outStream.println(sb.toString());
			outStream.flush();
			outStream.close();

			// El return no llega nunca, es del metodo viejo
			return new ModelAndView("redirect:/");
		} catch(Throwable oops) {
			return new ModelAndView("redirect:/");
		}
	}

	@RequestMapping(value = "/provider", method = RequestMethod.GET)
	public @ResponseBody
	ModelAndView exportProvider(@RequestParam(value = "id", defaultValue = "-1", required = false) String id, HttpServletResponse response) throws IOException {

		try {
			Assert.isTrue(StringUtils.isNumeric(id));
			int idInt = Integer.parseInt(id);
			
			this.providerService.loggedAsProvider();

			Provider provider = new Provider();
			provider = this.providerService.findOne(idInt);

			// Defines un StringBuilder para construir tu string
			StringBuilder sb = new StringBuilder();

			// linea
			sb.append("Personal data:").append(System.getProperty("line.separator"));
			sb.append("Name: " + provider.getName()).append(System.getProperty("line.separator"));
			sb.append("Surname: " + provider.getSurname()).append(System.getProperty("line.separator"));
			sb.append("Address: " + provider.getAddress()).append(System.getProperty("line.separator"));
			sb.append("Email: " + provider.getEmail()).append(System.getProperty("line.separator"));
			sb.append("Photo: " + provider.getPhoto()).append(System.getProperty("line.separator"));
			sb.append("VAT number: " + provider.getVATNumber()).append(System.getProperty("line.separator"));
			sb.append("Provider: " + provider.getMake()).append(System.getProperty("line.separator"));

			sb.append(System.getProperty("line.separator"));
			sb.append("SocialProfiles: ").append(System.getProperty("line.separator"));
			sb.append(System.getProperty("line.separator"));
			// Este metodo te muestra los socialProfiles de la misma manera que el resto del
			// documento
			sb.append(this.rookieService.SocialProfilesToString()).append(System.getProperty("line.separator"));

			if (provider == null || this.providerService.loggedProvider().getId() != idInt)
				return new ModelAndView("redirect:/");

			// Defines el nombre del archivo y la extension
			response.setContentType("text/txt");
			response.setHeader("Content-Disposition", "attachment;filename=exportDataProvider.txt");

			// Con estos comandos permites su descarga cuando clickas
			ServletOutputStream outStream = response.getOutputStream();
			outStream.println(sb.toString());
			outStream.flush();
			outStream.close();

			// El return no llega nunca, es del metodo viejo
			return new ModelAndView("redirect:/");
		} catch(Throwable oops)	{
			return new ModelAndView("redirect:/");
		}
	}
}
