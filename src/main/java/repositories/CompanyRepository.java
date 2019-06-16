
package repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Application;
import domain.Audit;
import domain.Company;
import domain.Curriculum;
import domain.Position;
import domain.Rookie;
import domain.Sponsorship;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Integer> {

	@Query("select m from Company m join m.userAccount u where u.username = ?1")
	public Company getCompanyByUsername(String username);

	@Query("select p from Company c join c.positions p where p.isDraftMode = false AND p.isCancelled = false")
	public List<Position> positionsInFinal();

	@Query("select c from Company c where ?1 MEMBER OF c.positions")
	public Company companyByPosition(int idPosition);

	@Query("select c from Company c")
	public List<Company> allCompanies();

	@Query("select c.positions from Company c where c.id = ?1")
	public List<Position> positionsOfCompany(int idCompany);

	@Query("select p from Company c join c.positions p where c.id = ?1 AND p.isCancelled = false AND p.isDraftMode = false")
	public List<Position> positionsOfCompanyInFinalNotCancelled(int idCompany);

	@Query("select distinct p.applications from Company c join c.positions p where c.id = ?1")
	public List<Application> applicationsOfCompany(int idCompany);

	@Query("select distinct a.rookie from Company c join c.positions p join p.applications a where c.id = ?1")
	public List<Rookie> rookiesOfCompany(int idCompany);

	@Query("select distinct a.curriculum from Company c join c.positions p join p.applications a where c.id = ?1")
	public List<Curriculum> curriculumsOfApplicationssOfCompany(int idCompany);

	@Query("select p.audits from Company c join c.positions p where c.id = ?1")
	public List<Audit> auditsOfCompany(int idCompany);

	@Query("select p.sponsorships from Company c join c.positions p where c.id = ?1")
	public List<Sponsorship> sponsorshipsOfCompany(int idCompany);

}
