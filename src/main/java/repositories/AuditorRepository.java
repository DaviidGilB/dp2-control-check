
package repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Auditor;
import domain.Position;

@Repository
public interface AuditorRepository extends JpaRepository<Auditor, Integer> {

	@Query("select m from Auditor m join m.userAccount u where u.username = ?1")
	public Auditor getAuditorByUsername(String username);

	@Query("select o from Position o where o not in (select p from Auditor a join a.audits b join b.position p where a.id = ?1) and o.isDraftMode = false and o.isCancelled = false")
	public List<Position> getAssignablePositions(int auditorId);

}
