
package domain;

import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

@Entity
@Access(AccessType.PROPERTY)
public class Problem extends DomainEntity {

	private String			title;
	private String			statement;
	private String			hint;
	private List<String>	attachments;
	private Boolean			isDraftMode;
	
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

	@NotBlank
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@NotBlank
	public String getStatement() {
		return this.statement;
	}

	public void setStatement(String statement) {
		this.statement = statement;
	}

	@Valid
	public String getHint() {
		return this.hint;
	}

	public void setHint(String hint) {
		this.hint = hint;
	}

	@Valid
	@ElementCollection(targetClass = String.class)
	public List<String> getAttachments() {
		return this.attachments;
	}

	public void setAttachments(List<String> attachments) {
		this.attachments = attachments;
	}

	@NotNull
	public Boolean getIsDraftMode() {
		return this.isDraftMode;
	}

	public void setIsDraftMode(Boolean isDraftMode) {
		this.isDraftMode = isDraftMode;
	}

}
