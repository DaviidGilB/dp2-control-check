
package repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Audit;

@Repository
public interface AuditRepository extends JpaRepository<Audit, Integer> {

	@Query("select a from Position p join p.audits a where a.isDraftMode = false and p.id = ?1")
	public List<Audit> getFinalAuditsByPosition(int positionId);

	@Query("select a from Position p join p.audits a where a.isDraftMode = true and p.id =?1")
	public List<Audit> getDraftAuditsByPosition(int positionId);

}
