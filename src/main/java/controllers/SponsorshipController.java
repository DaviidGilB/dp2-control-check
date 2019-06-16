
package controllers;

import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.ConfigurationService;
import services.CreditCardService;
import services.PositionService;
import services.ProviderService;
import services.SponsorshipService;
import domain.CreditCard;
import domain.Position;
import domain.Provider;
import domain.Sponsorship;
import forms.FormObjectSponsorshipCreditCard;

@Controller
@RequestMapping("/sponsorship/provider/")
public class SponsorshipController {

	@Autowired
	private SponsorshipService		sponsorshipService;

	@Autowired
	private ProviderService			providerService;

	@Autowired
	private PositionService			positionService;

	@Autowired
	private CreditCardService		creditCardService;

	@Autowired
	private ConfigurationService	configurationService;


	public SponsorshipController() {
		super();
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list() {
		try {
			ModelAndView result;

			Provider provider = this.providerService.loggedProvider();

			List<Sponsorship> sponsorships = this.sponsorshipService.findProviderSponsorships(provider);

			result = new ModelAndView("provider/sponsorships");

			result.addObject("sponsorships", sponsorships);
			result.addObject("requestURI", "sponsorship/provider/list.do");

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/sponsorship/provider/list.do");
		}
	}

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create() {
		try {
			ModelAndView result;

			this.providerService.loggedAsProvider();
			FormObjectSponsorshipCreditCard formObject = new FormObjectSponsorshipCreditCard();

			List<Position> positions = this.positionService.getFinalPositionsAndNotCancelled();

			result = this.createEditModelAndView("sponsorship/create", formObject);
			result.addObject("positions", positions);

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/sponsorship/provider/list.do");
		}
	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam(required = false) String sponsorshipId) {
		try {
			Assert.isTrue(StringUtils.isNumeric(sponsorshipId));
			int sponsorshipIdInt = Integer.parseInt(sponsorshipId);

			ModelAndView result;

			Provider provider = this.providerService.loggedProvider();

			Sponsorship sponsorship = this.sponsorshipService.findOne(sponsorshipIdInt);

			if (provider.getSponsorships().contains(sponsorship)) {

				List<Position> positions = this.positionService.getFinalPositionsAndNotCancelled();

				FormObjectSponsorshipCreditCard formObject = new FormObjectSponsorshipCreditCard();

				formObject.setId(sponsorship.getId());
				formObject.setBanner(sponsorship.getBanner());
				formObject.setTargetURL(sponsorship.getTargetUrl());

				result = this.createEditModelAndView("sponsorship/edit", formObject);
				result.addObject("positions", positions);

			} else
				result = this.list();

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/sponsorship/provider/list.do");
		}
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@ModelAttribute("formObject") @Valid FormObjectSponsorshipCreditCard formObject, BindingResult binding, @RequestParam Position position) {
		try {
			ModelAndView result;

			this.providerService.loggedAsProvider();

			Sponsorship sponsorship = new Sponsorship();
			CreditCard creditCard = new CreditCard();

			List<Position> positions = this.positionService.getFinalPositionsAndNotCancelled();

			Position pos;
			if (position.getId() > 0)
				pos = this.positionService.findOne(position.getId());
			else
				pos = null;

			creditCard = this.creditCardService.reconstruct(formObject, binding);
			sponsorship = this.sponsorshipService.reconstruct(formObject, binding, creditCard, pos);

			if (creditCard.getNumber() != null)
				if (!this.creditCardService.validateNumberCreditCard(creditCard))
					if (LocaleContextHolder.getLocale().getLanguage().toUpperCase().contains("ES"))
						binding.addError(new FieldError("formObject", "number", formObject.getNumber(), false, null, null, "El numero de la tarjeta es invalido"));
					else
						binding.addError(new FieldError("formObject", "number", formObject.getNumber(), false, null, null, "The card number is invalid"));

			if (creditCard.getExpirationMonth() != null && creditCard.getExpirationYear() != null)
				if (!this.creditCardService.validateDateCreditCard(creditCard))
					if (LocaleContextHolder.getLocale().getLanguage().toUpperCase().contains("ES"))
						binding.addError(new FieldError("formObject", "expirationMonth", creditCard.getExpirationMonth(), false, null, null, "La tarjeta no puede estar caducada"));
					else
						binding.addError(new FieldError("formObject", "expirationMonth", creditCard.getExpirationMonth(), false, null, null, "The credit card can not be expired"));

			List<String> cardType = this.configurationService.getConfiguration().getCardType();

			if (!cardType.contains(sponsorship.getCreditCard().getBrandName()))
				if (LocaleContextHolder.getLocale().getLanguage().toUpperCase().contains("ES"))
					binding.addError(new FieldError("formObject", "brandName", creditCard.getBrandName(), false, null, null, "Tarjeta no admitida"));
				else
					binding.addError(new FieldError("formObject", "brandName", creditCard.getBrandName(), false, null, null, "The credit card is not accepted"));

			if (binding.hasErrors()) {
				result = this.createEditModelAndView("sponsorship/create", formObject);
				result.addObject("positions", positions);
			} else
				try {
					this.sponsorshipService.addOrUpdateSponsorship(sponsorship);

					result = new ModelAndView("redirect:/sponsorship/provider/list.do");
				} catch (Throwable oops) {
					result = this.createEditModelAndView("sponsorship/create", formObject, "sponsorship.commit.error");
					result.addObject("positions", positions);
				}

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/sponsorship/provider/list.do");
		}
	}

	@RequestMapping(value = "/delete", method = RequestMethod.GET)
	public ModelAndView delete(@RequestParam(required = false) String sponsorshipId) {
		try {
			Assert.isTrue(StringUtils.isNumeric(sponsorshipId));
			int sponsorshipIdInt = Integer.parseInt(sponsorshipId);

			ModelAndView result;
			this.providerService.loggedAsProvider();

			try {
				this.sponsorshipService.deleteSponsorship(sponsorshipIdInt);
				result = new ModelAndView("redirect:list.do");
			} catch (Throwable oops) {
				result = this.list();
				result.addObject("message", "sponsorship.delete.error");
			}

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/sponsorship/provider/list.do");
		}
	}

	private ModelAndView createEditModelAndView(String tiles, FormObjectSponsorshipCreditCard formObject) {
		ModelAndView result;

		List<String> cardType = this.configurationService.getConfiguration().getCardType();

		result = new ModelAndView(tiles);
		result.addObject("formObject", formObject);
		result.addObject("cardType", cardType);

		return result;
	}

	private ModelAndView createEditModelAndView(String tiles, FormObjectSponsorshipCreditCard formObject, String message) {
		ModelAndView result = this.createEditModelAndView(tiles, formObject);

		result.addObject("message", message);

		return result;
	}

}
