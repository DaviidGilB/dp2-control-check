
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

	@Query("select c from Position p join p.controlEntity c where p.id = ?1 and c.isDraftMode = false and p.isDraftMode = false")
	List<ControlEntity> getFinalControlEntityOfPosition(Integer positionId);
	
	@Query("select c from Position p join p.controlEntity c where p.id = ?1 and p.isDraftMode = false")
	List<ControlEntity> getAllControlEntityOfPosition(Integer positionId);

//	@Query("select c from Company co join co.positions p join p.applications a join a.controlEntity c where co.id = ?1 and (a.status ='ACCEPTED' or a.status = 'REJECTED')")
//	List<ControlEntity> getAllControlEntityOfCompany(Integer companyId);
//
//	@Query("select a from Company c join c.positions p join p.applications a where c.id = ?1 and a.id = ?2")
//	Application checkCompanyAndApplication(Integer companyId, Integer applicationId);
//	
//	@Query("select c.ticker from ControlEntity c")
//	List<String> getAllTickers();
//
//	@Query("select c from Company co join co.positions p join p.applications a join a.controlEntity c where co.id = ?1 and a.id = ?2")
//	List<ControlEntity> getAllControlEntityOfCompanyAndApplication(Integer companyId, Integer applicationId);
//
//	@Query("select c from Company co join co.positions p join p.applications a join a.controlEntity c where co.id = ?1 and c.id = ?2 and c.isDraftMode = true")
//	ControlEntity checkCompanyAndControlEntity(Integer companyId, Integer controlEntityId);
//
//	@Query("select a from Application a join a.controlEntity c where c.id = ?1")
//	Application getApplicationOfControlEntity(Integer controlEntityId);

}
