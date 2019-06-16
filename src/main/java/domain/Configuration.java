
package domain;

import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

@Entity
@Access(AccessType.PROPERTY)
public class Configuration extends DomainEntity {

	private List<String>	spamWords;
	private String			spainTelephoneCode;
	private Integer			minFinderResults;
	private Integer			maxFinderResults;
	private Integer			finderResult;
	private Integer			minTimeFinder;
	private Integer			maxTimeFinder;
	private Integer			timeFinder;
	private List<String>	priorityLvl;
	private List<String>	priorityLvlSpa;

	private String			goodWords;
	private String			badWords;

	// New parameters
	private String			welcomeMessageEnglish;
	private String			welcomeMessageSpanish;
	private String			systemName;
	private String			imageURL;
	private Integer			VAT;
	private Float			fare;
	private List<String>	cardType;

	private Boolean			isRebrandingBroadcasted;


	@Valid
	@ElementCollection(targetClass = String.class)
	public List<String> getPriorityLvlSpa() {
		return this.priorityLvlSpa;
	}

	public void setPriorityLvlSpa(List<String> priorityLvlSpa) {
		this.priorityLvlSpa = priorityLvlSpa;
	}

	@Valid
	@ElementCollection(targetClass = String.class)
	public List<String> getPriorityLvl() {
		return this.priorityLvl;
	}

	public void setPriorityLvl(List<String> priorityLvl) {
		this.priorityLvl = priorityLvl;
	}

	// Name of the System
	// Banner
	// Message at the welcome page
	@NotBlank
	@URL
	public String getImageURL() {
		return this.imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	@NotBlank
	public String getSystemName() {
		return this.systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	@NotBlank
	public String getWelcomeMessageEnglish() {
		return this.welcomeMessageEnglish;
	}

	public void setWelcomeMessageEnglish(String welcomeMessageEnglish) {
		this.welcomeMessageEnglish = welcomeMessageEnglish;
	}

	@NotBlank
	public String getWelcomeMessageSpanish() {
		return this.welcomeMessageSpanish;
	}

	public void setWelcomeMessageSpanish(String welcomeMessageSpanish) {
		this.welcomeMessageSpanish = welcomeMessageSpanish;
	}

	@NotNull
	public Integer getmaxFinderResults() {
		return this.maxFinderResults;
	}

	public void setmaxFinderResults(Integer maxFinderResults) {
		this.maxFinderResults = maxFinderResults;
	}

	@NotNull
	public Integer getMinTimeFinder() {
		return this.minTimeFinder;
	}

	public void setMinTimeFinder(Integer minTimeFinder) {
		this.minTimeFinder = minTimeFinder;
	}

	@NotNull
	public Integer getMaxTimeFinder() {
		return this.maxTimeFinder;
	}

	public void setMaxTimeFinder(Integer maxTimeFinder) {
		this.maxTimeFinder = maxTimeFinder;
	}

	@NotNull
	public Integer getMinFinderResults() {
		return this.minFinderResults;
	}

	public void setMinFinderResults(Integer minFinderResults) {
		this.minFinderResults = minFinderResults;
	}

	@ElementCollection(targetClass = String.class)
	public List<String> getSpamWords() {
		return this.spamWords;
	}

	public void setSpamWords(List<String> spamWords) {
		this.spamWords = spamWords;
	}

	@NotBlank
	public String getSpainTelephoneCode() {
		return this.spainTelephoneCode;
	}

	public void setSpainTelephoneCode(String spainTelephoneCode) {
		this.spainTelephoneCode = spainTelephoneCode;
	}

	@NotBlank
	public String getGoodWords() {
		return this.goodWords;
	}

	public void setGoodWords(String goodWords) {
		this.goodWords = goodWords;
	}

	@NotBlank
	public String getBadWords() {
		return this.badWords;
	}

	public void setBadWords(String badWords) {
		this.badWords = badWords;
	}

	@NotNull
	public Integer getFinderResult() {
		return this.finderResult;
	}

	public void setFinderResult(Integer finderResult) {
		this.finderResult = finderResult;
	}

	@NotNull
	public Integer getTimeFinder() {
		return this.timeFinder;
	}

	public void setTimeFinder(Integer timeFinder) {
		this.timeFinder = timeFinder;
	}

	@NotNull
	@Max(100)
	public Integer getVAT() {
		return this.VAT;
	}

	public void setVAT(Integer vAT) {
		this.VAT = vAT;
	}

	@NotNull
	@Digits(fraction = 2, integer = 9)
	public Float getFare() {
		return this.fare;
	}

	public void setFare(java.lang.Float fare) {
		this.fare = fare;
	}

	@ElementCollection(targetClass = String.class)
	public List<String> getCardType() {
		return this.cardType;
	}

	public void setCardType(List<String> cardType) {
		this.cardType = cardType;
	}

	@NotNull
	public Boolean getIsRebrandingBroadcasted() {
		return this.isRebrandingBroadcasted;
	}

	public void setIsRebrandingBroadcasted(Boolean isRebrandingBroadcasted) {
		this.isRebrandingBroadcasted = isRebrandingBroadcasted;
	}

}
