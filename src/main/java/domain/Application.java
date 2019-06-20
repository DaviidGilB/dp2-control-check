
package domain;

import java.util.Date;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

import org.hibernate.validator.constraints.URL;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Access(AccessType.PROPERTY)
@Table(indexes = { @Index(columnList = "status") })
public class Application extends DomainEntity {

	private Date creationMoment;
	private String link;
	private String explication;
	private Date submitMoment;
	private Status status;

	private Problem problem;
	private Position position;
	private Curriculum curriculum;
	private Rookie rookie;
	
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
	
	public Boolean hasAnyFinalControlEntity() {
		Boolean res = false;
		if(this.controlEntity != null && !this.controlEntity.isEmpty()) {
			for(ControlEntity c:this.controlEntity) {
				if(!c.getIsDraftMode()) {
					res = true;
					break;
				}
			}
		}
		return res;
	}
	
	//

	@Past
	@DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
	@NotNull
	public Date getCreationMoment() {
		return this.creationMoment;
	}

	public void setCreationMoment(Date creationMoment) {
		this.creationMoment = creationMoment;
	}

	@URL
	@Valid
	public String getLink() {
		return this.link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	@Valid
	public String getExplication() {
		return this.explication;
	}

	public void setExplication(String explication) {
		this.explication = explication;
	}

	@Past
	@DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
	public Date getSubmitMoment() {
		return this.submitMoment;
	}

	public void setSubmitMoment(Date submitMoment) {
		this.submitMoment = submitMoment;
	}

	@NotNull
	@Enumerated(EnumType.STRING)
	public Status getStatus() {
		return this.status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	@ManyToOne
	public Problem getProblem() {
		return this.problem;
	}

	public void setProblem(Problem problem) {
		this.problem = problem;
	}

	@ManyToOne
	public Position getPosition() {
		return this.position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	@OneToOne(cascade = CascadeType.ALL)
	public Curriculum getCurriculum() {
		return this.curriculum;
	}

	public void setCurriculum(Curriculum curriculum) {
		this.curriculum = curriculum;
	}

	@ManyToOne
	public Rookie getRookie() {
		return this.rookie;
	}

	public void setRookie(Rookie rookie) {
		this.rookie = rookie;
	}
}
