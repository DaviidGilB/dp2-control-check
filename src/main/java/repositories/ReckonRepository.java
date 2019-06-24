
package repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Application;
import domain.Audit;
import domain.Company;
import domain.Reckon;
import domain.Curriculum;
import domain.Position;
import domain.Rookie;
import domain.Sponsorship;

@Repository
public interface ReckonRepository extends JpaRepository<Reckon, Integer> {

	@Query("select c from Audit a join a.reckon c where a.id = ?1 and c.isDraftMode = false and a.isDraftMode = false")
	List<Reckon> getFinalReckonOfAudit(Integer auditId);
	
	@Query("select c from Audit a join a.reckon c where a.id = ?1 and a.isDraftMode = false")
	List<Reckon> getAllReckonOfAudit(Integer auditId);

	@Query("select c from Company co join co.positions p join p.audits a join a.reckon c where co.id = ?1 and a.isDraftMode = false")
	List<Reckon> getAllReckonOfCompany(Integer companyId);

	@Query("select a from Company c join c.positions p join p.audits a where c.id = ?1 and a.id = ?2 and a.isDraftMode = false")
	Audit checkCompanyAndAudit(Integer companyId, Integer auditId);
	
	@Query("select c.ticker from Reckon c")
	List<String> getAllTickers();

	@Query("select c from Company co join co.positions p join p.audits a join a.reckon c where co.id = ?1 and a.id = ?2")
	List<Reckon> getAllReckonOfCompanyAndAudit(Integer companyId, Integer auditId);

	@Query("select c from Company co join co.positions p join p.audits a join a.reckon c where co.id = ?1 and c.id = ?2 and c.isDraftMode = true")
	Reckon checkCompanyAndReckon(Integer companyId, Integer reckonId);

	@Query("select a from Audit a join a.reckon c where c.id = ?1")
	Audit getAuditOfReckon(Integer reckonId);

}
