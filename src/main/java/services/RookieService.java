
package services;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import repositories.RookieRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import domain.Actor;
import domain.Admin;
import domain.Application;
import domain.CreditCard;
import domain.Curriculum;
import domain.EducationData;
import domain.Finder;
import domain.Rookie;
import domain.MiscellaneousData;
import domain.PersonalData;
import domain.PositionData;
import domain.Problem;
import domain.Message;
import domain.SocialProfile;
import forms.FormObjectEditAdmin;
import forms.FormObjectEditRookie;
import forms.FormObjectRookie;

@Service
@Transactional
public class RookieService {

	@Autowired
	private RookieRepository		rookieRepository;
	@Autowired
	private CurriculumService curriculumService;
	@Autowired
	private ActorService actorService;
	@Autowired
	private PersonalDataService personalDataService;

	@Autowired
	private ApplicationService		applicationService;

	@Autowired
	private CreditCardService		creditCardService;

	@Autowired
	private ConfigurationService	configurationService;

	@Autowired
	private FinderService			finderService;


	// Auxiliar methods
	public Rookie securityAndRookie() {
		UserAccount userAccount = LoginService.getPrincipal();
		String username = userAccount.getUsername();

		Rookie loggedRookie = this.rookieRepository.getRookieByUsername(username);
		Assert.notNull(loggedRookie);
		List<Authority> authorities = (List<Authority>) loggedRookie.getUserAccount().getAuthorities();
		Assert.isTrue(authorities.get(0).toString().equals("ROOKIE"));

		return loggedRookie;
	}
	
	public Boolean isRookie(Actor actor) {
		List<Authority> authorities = (List<Authority>) actor.getUserAccount().getAuthorities();
		return authorities.get(0).toString().equals("ROOKIE");
	}

	public Rookie loggedRookie() {
		UserAccount userAccount;
		userAccount = LoginService.getPrincipal();
		return this.rookieRepository.getRookieByUsername(userAccount.getUsername());
	}

	public void loggedAsRookie() {
		UserAccount userAccount;
		userAccount = LoginService.getPrincipal();
		List<Authority> authorities = (List<Authority>) userAccount.getAuthorities();
		Assert.isTrue(authorities.get(0).toString().equals("ROOKIE"));

	}

	public void addApplication(Application p) {
		Rookie rookie = this.loggedRookie();
		Assert.isTrue(p.getId() == 0);

		p.setRookie(rookie);
		Application application = this.applicationService.save(p);
		List<Application> applications = rookie.getApplications();
		applications.add(application);
		rookie.setApplications(applications);
		this.save(rookie);
	}
	
	public Rookie getRookieByUsername(String username) {
		return this.rookieRepository.getRookieByUsername(username);
	}

	public Rookie save(Rookie rookie) {
		return this.rookieRepository.save(rookie);
	}

	public void addOrUpdateCurriculum(Curriculum curriculum) {
		Rookie rookie = this.securityAndRookie();

		if(curriculum.getId() > 0) {
			Assert.notNull(this.curriculumService.getCurriculumOfRookie(rookie.getId(), curriculum.getId()));
			this.curriculumService.save(curriculum);
			this.curriculumService.flush();
		} else {			
			List<Curriculum> curriculums = rookie.getCurriculums();
			curriculums.add(curriculum);
			rookie.setCurriculums(curriculums);
			this.save(rookie);
			this.flush();
		}
	}

	public void flush() {
		this.rookieRepository.flush();
	}

	public Rookie createRookie() {
		Rookie rookie = new Rookie();
		CreditCard card = new CreditCard();
		Finder finder = new Finder();
		// Se crean las listas vacias
		// ACTOR
		List<SocialProfile> socialProfiles = new ArrayList<SocialProfile>();
		List<Message> messages = new ArrayList<Message>();

		UserAccount userAccount = new UserAccount();
		userAccount.setUsername("");
		userAccount.setPassword("");

		// Actor
		rookie.setAddress("");
		rookie.setEmail("");
		rookie.setCreditCard(card);
		rookie.setMessages(messages);
		rookie.setHasSpam(false);
		rookie.setName("");
		rookie.setPhone("");
		rookie.setPhoto("");
		rookie.setSocialProfiles(socialProfiles);
		rookie.setSurname("");
		rookie.setVATNumber("");
		rookie.setFinder(finder);

		List<Authority> authorities = new ArrayList<Authority>();

		Authority authority = new Authority();
		authority.setAuthority(Authority.ROOKIE);
		authorities.add(authority);

		userAccount.setAuthorities(authorities);
		userAccount.setIsNotLocked(true);

		rookie.setUserAccount(userAccount);

		return rookie;

	}

	public Rookie reconstruct(FormObjectRookie formObjectRookie, BindingResult binding) {

		Rookie result = this.createRookie();

		result.setAddress(formObjectRookie.getAddress());
		result.setEmail(formObjectRookie.getEmail());
		result.setHasSpam(false);
		result.setName(formObjectRookie.getName());
		result.setPhone(formObjectRookie.getPhone());
		result.setPhoto(formObjectRookie.getPhoto());
		result.setSurname(formObjectRookie.getSurname());
		result.setVATNumber(formObjectRookie.getVATNumber());

		CreditCard card = new CreditCard();

		card.setBrandName(formObjectRookie.getBrandName());
		card.setHolderName(formObjectRookie.getHolderName());
		card.setNumber(formObjectRookie.getNumber());
		card.setExpirationMonth(formObjectRookie.getExpirationMonth());
		card.setExpirationYear(formObjectRookie.getExpirationYear());
		card.setCvvCode(formObjectRookie.getCvvCode());

		result.setCreditCard(card);

		// USER ACCOUNT
		UserAccount userAccount = new UserAccount();

		// Authorities
		List<Authority> authorities = new ArrayList<Authority>();
		Authority authority = new Authority();
		authority.setAuthority(Authority.ROOKIE);
		authorities.add(authority);
		userAccount.setAuthorities(authorities);

		// locked
		userAccount.setIsNotLocked(true);

		// Username
		userAccount.setUsername(formObjectRookie.getUsername());

		// Password
		Md5PasswordEncoder encoder;
		encoder = new Md5PasswordEncoder();
		userAccount.setPassword(encoder.encodePassword(formObjectRookie.getPassword(), null));

		result.setUserAccount(userAccount);

		String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();

		//Confirmacion contrasena
		if (!formObjectRookie.getPassword().equals(formObjectRookie.getConfirmPassword()))
			if (locale.contains("ES"))
				binding.addError(new FieldError("formObjectAdmin", "password", formObjectRookie.getPassword(), false, null, null, "Las contrasenas no coinciden"));
			else
				binding.addError(new FieldError("formObjectAdmin", "password", formObjectRookie.getPassword(), false, null, null, "Passwords don't match"));

		//Confirmacion terminos y condiciones
		if (!formObjectRookie.getTermsAndConditions())
			if (locale.contains("ES"))
				binding.addError(new FieldError("formObjectAdmin", "termsAndConditions", formObjectRookie.getTermsAndConditions(), false, null, null, "Debe aceptar los terminos y condiciones"));
			else
				binding.addError(new FieldError("formObjectAdmin", "termsAndConditions", formObjectRookie.getTermsAndConditions(), false, null, null, "You must accept the terms and conditions"));

		if (card.getNumber() != null)
			if (!this.creditCardService.validateNumberCreditCard(card))
				if (LocaleContextHolder.getLocale().getLanguage().toUpperCase().contains("ES"))
					binding.addError(new FieldError("formObject", "number", formObjectRookie.getNumber(), false, null, null, "El numero de la tarjeta es invalido"));
				else
					binding.addError(new FieldError("formObject", "number", formObjectRookie.getNumber(), false, null, null, "The card number is invalid"));

		if (card.getExpirationMonth() != null && card.getExpirationYear() != null)
			if (!this.creditCardService.validateDateCreditCard(card))
				if (LocaleContextHolder.getLocale().getLanguage().toUpperCase().contains("ES"))
					binding.addError(new FieldError("formObject", "expirationMonth", card.getExpirationMonth(), false, null, null, "La tarjeta no puede estar caducada"));
				else
					binding.addError(new FieldError("formObject", "expirationMonth", card.getExpirationMonth(), false, null, null, "The credit card can not be expired"));

		List<String> cardType = this.configurationService.getConfiguration().getCardType();

		if (!cardType.contains(result.getCreditCard().getBrandName()))
			if (LocaleContextHolder.getLocale().getLanguage().toUpperCase().contains("ES"))
				binding.addError(new FieldError("formObject", "brandName", card.getBrandName(), false, null, null, "Tarjeta no admitida"));
			else
				binding.addError(new FieldError("formObject", "brandName", card.getBrandName(), false, null, null, "The credit card is not accepted"));

		if (result.getEmail().matches("[\\w.%-]+\\<[\\w.%-]+\\@+\\>|[\\w.%-]+"))
			if (LocaleContextHolder.getLocale().getLanguage().toUpperCase().contains("ES"))
				binding.addError(new FieldError("member", "email", result.getEmail(), false, null, null, "No sigue el patron ejemplo@dominio.asd o alias <ejemplo@dominio.asd>"));
			else
				binding.addError(new FieldError("member", "email", result.getEmail(), false, null, null, "Dont follow the pattern example@domain.asd or alias <example@domain.asd>"));

		return result;
	}

	public void deleteRookie() {
		Rookie rookie = new Rookie();

		this.loggedAsRookie();
		rookie = this.loggedRookie();

		Finder finder = rookie.getFinder();

		//Finder se borra solo, hay que quitar la lista de positions
		finder.getPositions().clear();
		//Curriculum se borra solo

		//Mensajes se borran solos

		//Socialprofile se borra solo

		this.applicationService.deleteAllApplication();

		this.rookieRepository.delete(rookie);

	}

	public Rookie findOne(int id) {
		return this.rookieRepository.findOne(id);
	}

	public String SocialProfilesToString() {
		String res = "";
		Actor actor = this.actorService.loggedActor();
		List<SocialProfile> socialProfiles = new ArrayList<SocialProfile>();
		StringBuilder sb = new StringBuilder();
		socialProfiles = actor.getSocialProfiles();

		Integer cont = 1;

		for (SocialProfile f : socialProfiles) {
			sb.append("Profile" + cont + " Name: " + f.getName() + " Nick: " + f.getNick() + " Profile link: " + f.getProfileLink()).append(System.getProperty("line.separator"));
			cont++;
		}
		return sb.toString();
	}
	
	public Rookie reconstructRookiePersonalData(FormObjectEditRookie formObjectRookie, BindingResult binding) {
		Rookie res = new Rookie();

		Rookie adminDB = this.findOne(formObjectRookie.getId());

		CreditCard card = new CreditCard();

		//Credit Card
		card.setBrandName(formObjectRookie.getBrandName());
		card.setCvvCode(formObjectRookie.getCvvCode());
		card.setExpirationMonth(formObjectRookie.getExpirationMonth());
		card.setExpirationYear(formObjectRookie.getExpirationYear());
		card.setHolderName(formObjectRookie.getHolderName());
		card.setNumber(formObjectRookie.getNumber());

		res.setAddress(formObjectRookie.getAddress());

		res.setEmail(formObjectRookie.getEmail());
		res.setPhone(formObjectRookie.getPhone());
		res.setPhoto(formObjectRookie.getPhoto());
		res.setSurname(formObjectRookie.getSurname());
		res.setName(formObjectRookie.getName());
		res.setId(formObjectRookie.getId());
		res.setVATNumber(formObjectRookie.getVATNumber());
		res.setCreditCard(card);
		res.setHasSpam(adminDB.getHasSpam());
		res.setMessages(adminDB.getMessages());
		res.setSocialProfiles(adminDB.getSocialProfiles());
		res.setUserAccount(adminDB.getUserAccount());
		res.setVersion(adminDB.getVersion());

		if (card.getNumber() != null)
			if (!this.creditCardService.validateNumberCreditCard(card))
				if (LocaleContextHolder.getLocale().getLanguage().toUpperCase().contains("ES"))
					binding.addError(new FieldError("formObject", "number", formObjectRookie.getNumber(), false, null, null, "El numero de la tarjeta es invalido"));
				else
					binding.addError(new FieldError("formObject", "number", formObjectRookie.getNumber(), false, null, null, "The card number is invalid"));

		if (card.getExpirationMonth() != null && card.getExpirationYear() != null)
			if (!this.creditCardService.validateDateCreditCard(card))
				if (LocaleContextHolder.getLocale().getLanguage().toUpperCase().contains("ES"))
					binding.addError(new FieldError("formObject", "expirationMonth", card.getExpirationMonth(), false, null, null, "La tarjeta no puede estar caducada"));
				else
					binding.addError(new FieldError("formObject", "expirationMonth", card.getExpirationMonth(), false, null, null, "The credit card can not be expired"));

		List<String> cardType = this.configurationService.getConfiguration().getCardType();

		if (!cardType.contains(res.getCreditCard().getBrandName()))
			if (LocaleContextHolder.getLocale().getLanguage().toUpperCase().contains("ES"))
				binding.addError(new FieldError("formObject", "brandName", card.getBrandName(), false, null, null, "Tarjeta no admitida"));
			else
				binding.addError(new FieldError("formObject", "brandName", card.getBrandName(), false, null, null, "The credit card is not accepted"));

		return res;

	}
	
	public FormObjectEditRookie getFormObjectEditRookie(Rookie rookie) {

		FormObjectEditRookie res = new FormObjectEditRookie();

		//Company
		res.setAddress(rookie.getAddress());
		res.setName(rookie.getName());
		res.setVATNumber(rookie.getVATNumber());
		res.setPhoto(rookie.getPhoto());
		res.setEmail(rookie.getEmail());
		res.setAddress(rookie.getAddress());
		res.setSurname(rookie.getSurname());
		res.setPhone(rookie.getPhone());

		//Credit Card
		CreditCard c = rookie.getCreditCard();

		res.setHolderName(c.getHolderName());
		res.setBrandName(c.getBrandName());
		res.setNumber(c.getNumber());
		res.setExpirationMonth(c.getExpirationMonth());
		res.setExpirationYear(c.getExpirationYear());
		res.setCvvCode(c.getCvvCode());

		return res;

	}

}
