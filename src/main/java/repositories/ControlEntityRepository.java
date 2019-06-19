
package repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Application;
import domain.Audit;
import domain.Company;
import domain.ControlEntity;
import domain.Curriculum;
import domain.Position;
import domain.Rookie;
import domain.Sponsorship;

@Repository
public interface ControlEntityRepository extends JpaRepository<ControlEntity, Integer> {

	@Query("select c from Audit a join a.controlEntity c where a.id = ?1 and c.isDraftMode = false and a.isDraftMode = false")
	List<ControlEntity> getFinalControlEntityOfAudit(Integer auditId);
	
	@Query("select c from Audit a join a.controlEntity c where a.id = ?1 and a.isDraftMode = false")
	List<ControlEntity> getAllControlEntityOfAudit(Integer auditId);

	@Query("select c from Auditor a join a.audits ads join ads.controlEntity c where a.id = ?1 and ads.isDraftMode = false")
	List<ControlEntity> getAllControlEntityOfAuditor(Integer auditorId);

	@Query("select a from Auditor aud join aud.audits a where aud.id = ?1 and a.id = ?2 and a.isDraftMode = false")
	Audit checkAuditorAndAudit(Integer auditorId, Integer auditId);
	
	@Query("select c.ticker from ControlEntity c")
	List<String> getAllTickers();

	@Query("select c from Auditor aud join aud.audits a join a.controlEntity c where aud.id = ?1 and a.id = ?2")
	List<ControlEntity> getAllControlEntityOfAuditorAndAudit(Integer auditorId, Integer auditId);

	@Query("select c from Auditor aud join aud.audits a join a.controlEntity c where aud.id = ?1 and c.id = ?2 and c.isDraftMode = true")
	ControlEntity checkAuditorAndControlEntity(Integer auditorId, Integer controlEntityId);

	@Query("select a from Audit a join a.controlEntity c where c.id = ?1")
	Audit getAuditOfControlEntity(Integer controlEntityId);

}
