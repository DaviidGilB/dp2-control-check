
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

import domain.Audit;
import domain.Auditor;
import domain.CreditCard;
import domain.Message;
import domain.Position;
import domain.SocialProfile;
import forms.FormObjectAuditor;
import forms.FormObjectEditAuditor;
import repositories.AuditorRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;

@Service
@Transactional
public class AuditorService {

	@Autowired
	private AuditorRepository auditorRepository;

	@Autowired
	private AuditService auditService;

	@Autowired
	private CreditCardService creditCardService;

	@Autowired
	private ConfigurationService configurationService;

	@Autowired
	private AdminService adminService;

	@Autowired
	private PositionService positionService;

	// -----------------------------------------SECURITY-----------------------------
	// ------------------------------------------------------------------------------

	public Auditor save(Auditor auditor) {
		return this.auditorRepository.save(auditor);
	}

	/**
	 * LoggedCompany now contains the security of loggedAsCompany
	 *
	 * @return
	 */
	public Auditor loggedAuditor() {

		UserAccount userAccount;
		userAccount = LoginService.getPrincipal();
		return this.auditorRepository.getAuditorByUsername(userAccount.getUsername());
	}

	public void loggedAsAuditor() {
		UserAccount userAccount;
		userAccount = LoginService.getPrincipal();
		List<Authority> authorities = (List<Authority>) userAccount.getAuthorities();
		Assert.isTrue(authorities.get(0).toString().equals("AUDITOR"));

	}

	public void saveAudit(Audit a) {
		this.loggedAsAuditor();
		Auditor loggedAuditor = this.loggedAuditor();

		Assert.isTrue(!a.getPosition().getIsCancelled());
		Assert.isTrue(!a.getPosition().getIsDraftMode());

		if (a.getId() == 0)
			Assert.isTrue(
					this.auditorRepository.getAssignablePositions(loggedAuditor.getId()).contains(a.getPosition()));
		else
			Assert.isTrue(loggedAuditor.getAudits().contains(a));
		this.auditService.save(a);
	}

	public Auditor createAuditor() {

		Auditor auditor = new Auditor();
		CreditCard card = new CreditCard();

		// Se crean las listas vacias
		// ACTOR
		List<SocialProfile> socialProfiles = new ArrayList<SocialProfile>();
		List<Message> messages = new ArrayList<Message>();
		List<Audit> audits = new ArrayList<Audit>();

		UserAccount userAccount = new UserAccount();
		userAccount.setUsername("");
		userAccount.setPassword("");

		// Actor
		auditor.setAddress("");
		auditor.setEmail("");
		auditor.setCreditCard(card);
		auditor.setMessages(messages);
		auditor.setHasSpam(false);
		auditor.setName("");
		auditor.setPhone("");
		auditor.setPhoto("");
		auditor.setSocialProfiles(socialProfiles);
		auditor.setSurname("");
		auditor.setVATNumber("");
		auditor.setAudits(audits);

		List<Authority> authorities = new ArrayList<Authority>();

		Authority authority = new Authority();
		authority.setAuthority(Authority.AUDITOR);
		authorities.add(authority);

		userAccount.setAuthorities(authorities);
		userAccount.setIsNotLocked(true);

		auditor.setUserAccount(userAccount);

		return auditor;
	}

	public Auditor reconstruct(FormObjectAuditor formObjectAuditor, BindingResult binding) {

		Auditor result = this.createAuditor();

		result.setAddress(formObjectAuditor.getAddress());
		result.setEmail(formObjectAuditor.getEmail());
		result.setHasSpam(false);
		result.setName(formObjectAuditor.getName());
		result.setPhone(formObjectAuditor.getPhone());
		result.setPhoto(formObjectAuditor.getPhoto());
		result.setSurname(formObjectAuditor.getSurname());
		result.setVATNumber(formObjectAuditor.getVATNumber());

		CreditCard card = new CreditCard();

		card.setBrandName(formObjectAuditor.getBrandName());
		card.setHolderName(formObjectAuditor.getHolderName());
		card.setNumber(formObjectAuditor.getNumber());
		card.setExpirationMonth(formObjectAuditor.getExpirationMonth());
		card.setExpirationYear(formObjectAuditor.getExpirationYear());
		card.setCvvCode(formObjectAuditor.getCvvCode());

		result.setCreditCard(card);

		// USER ACCOUNT
		UserAccount userAccount = new UserAccount();

		// Authorities
		List<Authority> authorities = new ArrayList<Authority>();
		Authority authority = new Authority();
		authority.setAuthority(Authority.AUDITOR);
		authorities.add(authority);
		userAccount.setAuthorities(authorities);

		// locked
		userAccount.setIsNotLocked(true);

		// Username
		userAccount.setUsername(formObjectAuditor.getUsername());

		// Password
		Md5PasswordEncoder encoder;
		encoder = new Md5PasswordEncoder();
		userAccount.setPassword(encoder.encodePassword(formObjectAuditor.getPassword(), null));

		result.setUserAccount(userAccount);

		String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();

		// Confirmacion contrasena
		if (!formObjectAuditor.getPassword().equals(formObjectAuditor.getConfirmPassword()))
			if (locale.contains("ES"))
				binding.addError(new FieldError("formObjectAuditor", "password", formObjectAuditor.getPassword(), false,
						null, null, "Las contrasenas no coinciden"));
			else
				binding.addError(new FieldError("formObjectAuditor", "password", formObjectAuditor.getPassword(), false,
						null, null, "Passwords don't match"));

		// Confirmacion terminos y condiciones
		if (!formObjectAuditor.getTermsAndConditions())
			if (locale.contains("ES"))
				binding.addError(new FieldError("formObjectAuditor", "termsAndConditions",
						formObjectAuditor.getTermsAndConditions(), false, null, null,
						"Debe aceptar los terminos y condiciones"));
			else
				binding.addError(new FieldError("formObjectAuditor", "termsAndConditions",
						formObjectAuditor.getTermsAndConditions(), false, null, null,
						"You must accept the terms and conditions"));

		if (card.getNumber() != null)
			if (!this.creditCardService.validateNumberCreditCard(card))
				if (LocaleContextHolder.getLocale().getLanguage().toUpperCase().contains("ES"))
					binding.addError(new FieldError("formObjectAuditor", "number", formObjectAuditor.getNumber(), false,
							null, null, "El numero de la tarjeta es invalido"));
				else
					binding.addError(new FieldError("formObjectAuditor", "number", formObjectAuditor.getNumber(), false,
							null, null, "The card number is invalid"));

		if (card.getExpirationMonth() != null && card.getExpirationYear() != null)
			if (!this.creditCardService.validateDateCreditCard(card))
				if (LocaleContextHolder.getLocale().getLanguage().toUpperCase().contains("ES"))
					binding.addError(new FieldError("formObjectAuditor", "expirationMonth", card.getExpirationMonth(),
							false, null, null, "La tarjeta no puede estar caducada"));
				else
					binding.addError(new FieldError("formObjectAuditor", "expirationMonth", card.getExpirationMonth(),
							false, null, null, "The credit card can not be expired"));

		List<String> cardType = this.configurationService.getConfiguration().getCardType();

		if (!cardType.contains(result.getCreditCard().getBrandName()))
			if (LocaleContextHolder.getLocale().getLanguage().toUpperCase().contains("ES"))
				binding.addError(new FieldError("formObjectAuditor", "brandName", card.getBrandName(), false, null,
						null, "Tarjeta no admitida"));
			else
				binding.addError(new FieldError("formObjectAuditor", "brandName", card.getBrandName(), false, null,
						null, "The credit card is not accepted"));

		if (result.getEmail().matches("[\\w.%-]+\\<[\\w.%-]+\\@+\\>|[\\w.%-]+"))
			if (LocaleContextHolder.getLocale().getLanguage().toUpperCase().contains("ES"))
				binding.addError(new FieldError("formObjectAuditor", "email", result.getEmail(), false, null, null,
						"No sigue el patron ejemplo@dominio.asd o alias <ejemplo@dominio.asd>"));
			else
				binding.addError(new FieldError("formObjectAuditor", "email", result.getEmail(), false, null, null,
						"Dont follow the pattern example@domain.asd or alias <example@domain.asd>"));

		return result;
	}

	public void saveNewAuditor(Auditor auditor) {
		this.adminService.loggedAsAdmin();
		this.auditorRepository.save(auditor);
	}

	public FormObjectEditAuditor getFormObjectEditAuditor(Auditor auditor) {

		FormObjectEditAuditor res = new FormObjectEditAuditor();

		// Company
		res.setAddress(auditor.getAddress());
		res.setName(auditor.getName());
		res.setVATNumber(auditor.getVATNumber());
		res.setPhoto(auditor.getPhoto());
		res.setEmail(auditor.getEmail());
		res.setAddress(auditor.getAddress());
		res.setSurname(auditor.getSurname());
		res.setPhone(auditor.getPhone());

		// Credit Card
		CreditCard c = auditor.getCreditCard();

		res.setHolderName(c.getHolderName());
		res.setBrandName(c.getBrandName());
		res.setNumber(c.getNumber());
		res.setExpirationMonth(c.getExpirationMonth());
		res.setExpirationYear(c.getExpirationYear());
		res.setCvvCode(c.getCvvCode());

		return res;

	}

	public Auditor findOne(int id) {
		return this.auditorRepository.findOne(id);
	}

	public Auditor reconstructAuditorPersonalData(FormObjectEditAuditor formObjectauditor, BindingResult binding) {
		Auditor res = new Auditor();

		Auditor auditor = this.findOne(formObjectauditor.getId());

		CreditCard card = new CreditCard();

		// Credit Card
		card.setBrandName(formObjectauditor.getBrandName());
		card.setCvvCode(formObjectauditor.getCvvCode());
		card.setExpirationMonth(formObjectauditor.getExpirationMonth());
		card.setExpirationYear(formObjectauditor.getExpirationYear());
		card.setHolderName(formObjectauditor.getHolderName());
		card.setNumber(formObjectauditor.getNumber());

		res.setAddress(formObjectauditor.getAddress());

		res.setEmail(formObjectauditor.getEmail());
		res.setPhone(formObjectauditor.getPhone());
		res.setPhoto(formObjectauditor.getPhoto());
		res.setSurname(formObjectauditor.getSurname());
		res.setName(formObjectauditor.getName());
		res.setId(formObjectauditor.getId());
		res.setVATNumber(formObjectauditor.getVATNumber());
		res.setCreditCard(card);
		res.setHasSpam(auditor.getHasSpam());
		res.setMessages(auditor.getMessages());
		res.setSocialProfiles(auditor.getSocialProfiles());
		res.setUserAccount(auditor.getUserAccount());
		res.setVersion(auditor.getVersion());
		res.setAudits(auditor.getAudits());

		if (card.getNumber() != null)
			if (!this.creditCardService.validateNumberCreditCard(card))
				if (LocaleContextHolder.getLocale().getLanguage().toUpperCase().contains("ES"))
					binding.addError(new FieldError("formObject", "number", formObjectauditor.getNumber(), false, null,
							null, "El numero de la tarjeta es invalido"));
				else
					binding.addError(new FieldError("formObject", "number", formObjectauditor.getNumber(), false, null,
							null, "The card number is invalid"));

		if (card.getExpirationMonth() != null && card.getExpirationYear() != null)
			if (!this.creditCardService.validateDateCreditCard(card))
				if (LocaleContextHolder.getLocale().getLanguage().toUpperCase().contains("ES"))
					binding.addError(new FieldError("formObject", "expirationMonth", card.getExpirationMonth(), false,
							null, null, "La tarjeta no puede estar caducada"));
				else
					binding.addError(new FieldError("formObject", "expirationMonth", card.getExpirationMonth(), false,
							null, null, "The credit card can not be expired"));

		List<String> cardType = this.configurationService.getConfiguration().getCardType();

		if (!cardType.contains(res.getCreditCard().getBrandName()))
			if (LocaleContextHolder.getLocale().getLanguage().toUpperCase().contains("ES"))
				binding.addError(new FieldError("formObject", "brandName", card.getBrandName(), false, null, null,
						"Tarjeta no admitida"));
			else
				binding.addError(new FieldError("formObject", "brandName", card.getBrandName(), false, null, null,
						"The credit card is not accepted"));

		return res;

	}

	public void flush() {
		this.auditorRepository.flush();
	}

	public void deleteAuditor() {

		Auditor auditor = new Auditor();

		auditor = this.loggedAuditor();

		this.auditService.deleteAllAudits();

		this.auditorRepository.delete(auditor);
	}

	public List<Position> showAssignablePositions() {
		this.loggedAsAuditor();
		Auditor auditor = this.loggedAuditor();
		return this.auditorRepository.getAssignablePositions(auditor.getId());
	}

	public void updateAuditor(Auditor auditor) {
		this.loggedAsAuditor();
		Assert.isTrue(auditor.getId() == this.loggedAuditor().getId());

		this.save(auditor);
	}

}
