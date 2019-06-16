
package domain;

import java.util.Date;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Access(AccessType.PROPERTY)
@Table(indexes = { @Index(columnList = "isDraftMode") })
public class Audit extends DomainEntity {

	private Date momentCreation;
	private String freeText;
	private int score;
	private Position position;
	private Auditor auditor;
	private Boolean isDraftMode;
	
	// CONTROL_CHECK
	private List<ControlEntity> controlEntity;
	
	@OneToMany(cascade = CascadeType.ALL)
	@Valid
	public List<ControlEntity> getControlEntity() {
		return controlEntity;
	}

	public void setControlEntity(List<ControlEntity> controlEntity) {
		this.controlEntity = controlEntity;
	}
	

	@NotNull
	public Boolean getIsDraftMode() {
		return this.isDraftMode;
	}

	public void setIsDraftMode(Boolean isDraftMode) {
		this.isDraftMode = isDraftMode;
	}

	@Past
	@DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
	@NotNull
	public Date getMomentCreation() {
		return this.momentCreation;
	}

	public void setMomentCreation(Date momentCreation) {
		this.momentCreation = momentCreation;
	}

	@NotBlank
	public String getFreeText() {
		return this.freeText;
	}

	public void setFreeText(String freeText) {
		this.freeText = freeText;
	}

	@NotNull
	@Min(0)
	@Max(10)
	public int getScore() {
		return this.score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	@ManyToOne
	public Position getPosition() {
		return this.position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	@ManyToOne
	public Auditor getAuditor() {
		return this.auditor;
	}

	public void setAuditor(Auditor auditor) {
		this.auditor = auditor;
	}

}
