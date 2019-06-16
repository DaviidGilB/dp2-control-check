
package domain;

import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

@Entity
@Access(AccessType.PROPERTY)
public class Auditor extends Actor {

	private List<Audit>	audits;


	@OneToMany(mappedBy = "auditor")
	public List<Audit> getAudits() {
		return this.audits;
	}

	public void setAudits(List<Audit> audits) {
		this.audits = audits;
	}

}
