
package domain;

import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.hibernate.validator.constraints.NotBlank;

@Entity
@Access(AccessType.PROPERTY)
public class Provider extends Actor {

	private String				make;
	private List<Item>			items;
	private List<Sponsorship>	sponsorships;


	@NotBlank
	public String getMake() {
		return this.make;
	}

	public void setMake(String make) {
		this.make = make;
	}

	@OneToMany
	public List<Item> getItems() {
		return this.items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

	@OneToMany(mappedBy = "provider")
	public List<Sponsorship> getSponsorships() {
		return this.sponsorships;
	}

	public void setSponsorships(List<Sponsorship> sponsorships) {
		this.sponsorships = sponsorships;
	}
}
