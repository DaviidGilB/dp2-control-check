package forms;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

public class FormObjectItem {
	
	private Integer			id;
	private String			name;
	private String			description;
	private String			links;
	private String			pictures;
	
	public FormObjectItem() {
		this.id = 0;
	}
	
	@NotNull
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@NotBlank
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@NotBlank
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Valid
	public String getLinks() {
		return links;
	}
	public void setLinks(String links) {
		this.links = links;
	}
	
	@Valid
	public String getPictures() {
		return pictures;
	}
	
	public void setPictures(String pictures) {
		this.pictures = pictures;
	}

}
