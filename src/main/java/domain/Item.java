
package domain;

import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.validation.Valid;

import org.hibernate.validator.constraints.NotBlank;

@Entity
@Access(AccessType.PROPERTY)
public class Item extends DomainEntity {

	private String			name;
	private String			description;
	private List<String>	links;
	private List<String>	pictures;


	@NotBlank
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@NotBlank
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Valid
	@ElementCollection(targetClass = String.class)
	public List<String> getLinks() {
		return this.links;
	}

	public void setLinks(List<String> links) {
		this.links = links;
	}

	@Valid
	@ElementCollection(targetClass = String.class)
	public List<String> getPictures() {
		return this.pictures;
	}

	public void setPictures(List<String> pictures) {
		this.pictures = pictures;
	}
}
