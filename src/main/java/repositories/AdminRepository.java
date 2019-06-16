
package repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Admin;
import domain.Company;
import domain.Position;
import domain.Provider;
import domain.Rookie;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer> {

	@Query("select m from Admin m join m.userAccount u where u.username = ?1")
	public Admin getAdminByUsername(String username);

	//The minimum, the maximum, the average, and the standard deviation of the
	//number of curricula per rookie.

	@Query("select max(h.curriculums.size) from Rookie h")
	public Float maxCurriculumPerRookie();

	@Query("select min(h.curriculums.size) from Rookie h")
	public Float minCurriculumPerRookie();

	@Query("select avg(h.curriculums.size) from Rookie h")
	public Float avgCurriculumPerRookie();

	@Query("select stddev(h.curriculums.size) from Rookie h")
	public Float stddevCurriculumPerRookie();

	/*
	 * The minimum, the maximum, the average, and the standard deviation of the
	 * number of results in the finders.
	 */

	@Query("select min(a.positions.size) from Finder a where a.lastEdit > ?1")
	public Float minResultFinders(Date date);

	@Query("select max(a.positions.size) from Finder a where a.lastEdit > ?1")
	public Float maxResultFinders(Date date);

	@Query("select avg(a.positions.size) from Finder a where a.lastEdit > ?1")
	public Float avgResultFinders(Date date);

	@Query("select stddev(a.positions.size) from Finder a where a.lastEdit > ?1")
	public Float stddevResultFinders(Date date);

	@Query("select (cast((select count(a) from Finder a where a.positions.size = 0 AND a.lastEdit > ?1) as float)/(select count(c) from Finder c where c.positions.size > 0 AND c.lastEdit > ?1)) from Configuration b")
	public Float ratioEmptyFinder(Date date);

	@Query("select count(c) from Finder c where c.lastEdit > ?1")
	public Integer numberNonEmptyFinders(Date date);

	/*
	 * The minimum, the maximum, the average, and the standard deviation of the
	 * number of positions per company.
	 */

	@Query("select min(a.positions.size) from Company a")
	public Float minPositionsCompany();

	@Query("select max(a.positions.size) from Company a")
	public Float maxPositionsCompany();

	@Query("select avg(a.positions.size) from Company a")
	public Float avgPositionsCompany();

	@Query("select stddev(a.positions.size) from Company a")
	public Float stddevPositionsCompany();

	/*
	 * The minimum, the maximum, the average, and the standard deviation of the
	 * number of applications per rookie.
	 */

	@Query("select min(a.applications.size) from Rookie a")
	public Float minApplicationsRookie();

	@Query("select max(a.applications.size) from Rookie a")
	public Float maxApplicationsRookie();

	@Query("select avg(a.applications.size) from Rookie a")
	public Float avgApplicationsRookie();

	@Query("select stddev(a.applications.size) from Rookie a")
	public Float stddevApplicationsRookie();

	/*
	 * Companies that have offered more positions.
	 */

	@Query("select b from Company b where b.positions.size = (select max(a.positions.size) from Company a)")
	public List<Company> companiesMorePositions();

	/*
	 * Rookies who have made more applications.
	 */

	@Query("select b from Rookie b where b.applications.size = (select max(a.applications.size) from Rookie a)")
	public List<Rookie> rookiesMoreApplications();

	/*
	 * The minimum, the maximum, the average, and the standard deviation of the
	 * salaries offered.
	 */

	@Query("select min(a.offeredSalary) from Position a")
	public Float minSalary();

	@Query("select max(a.offeredSalary) from Position a")
	public Float maxSalary();

	@Query("select avg(a.offeredSalary) from Position a")
	public Float avgSalaries();

	@Query("select stddev(a.offeredSalary) from Position a")
	public Float stddevSalaries();

	/*
	 * Best and Worst Position in terms od salary.
	 */

	@Query("select b from Position b where b.offeredSalary = (select max(a.offeredSalary) from Position a)")
	public List<Position> bestSalaryPositions();

	@Query("select b from Position b where b.offeredSalary = (select min(a.offeredSalary) from Position a)")
	public List<Position> worstSalaryPositions();

	@Query("select a from Admin a join a.userAccount u where u.username = 'system'")
	public Admin getSystem();

	@Query("select round((((select avg(l.score) from Company c join c.positions p join p.audits l where c = d and l.isDraftMode = false and p.isCancelled = false and p.isDraftMode = false)- (select min(m.score) from Company e join e.positions q join q.audits m where e = d and m.isDraftMode = false and q.isCancelled = false and q.isDraftMode = false))/((select max(n.score) from Company f join f.positions r join r.audits n where f = d and n.isDraftMode = false and r.isCancelled = false and r.isDraftMode = false)- (select min(o.score) from Company g join g.positions s join s.audits o where g = d and o.isDraftMode = false and s.isCancelled = false and s.isDraftMode = false))),2) from Company d")
	public List<Double> computeScore();

	@Query("select (select max(n.score) from Company f join f.positions r join r.audits n where f = d and n.isDraftMode = false) from Company d")
	public List<Integer> maxScoreAuditByCompany();

	@Query("select (select min(n.score) from Company f join f.positions r join r.audits n where f = d and n.isDraftMode = false) from Company d")
	public List<Integer> minScoreAuditByCompany();

	/**
	 * LEVEL C 4/4
	 */
	@Query("select c from Company c order by c.score DESC")
	public List<Company> companiesOrderedByScore();

	//@Query("select max(cast((select avg(a.score) from Position p join p.audits a where p = d and a.isDraftMode=false) as float)), min(cast((select avg(a.score) from Position p join p.audits a where p = d and a.isDraftMode=false) as float)), sum(cast((select avg(a.score) from Position p join p.audits a where p = d and a.isDraftMode=false) as float))/(select count(b) from Position b where b.isDraftMode = false and b.isCancelled = false),stddev(cast((select avg(a.score) from Position p join p.audits a where p = d and a.isDraftMode=false) as float)) from Position d")
	//public Double[] positionsScore();

	@Query("select max(cast((select avg(a.score) from Position p join p.audits a where p = d and a.isDraftMode=false) as float)) from Position d")
	public Double positionScoreMax();

	@Query("select min(cast((select avg(a.score) from Position p join p.audits a where p = d and a.isDraftMode=false) as float)) from Position d")
	public Double positionScoreMin();

	@Query("select stddev(cast((select avg(a.score) from Position p join p.audits a where p = d and a.isDraftMode=false) as float)) from Position d")
	public Double positionScoreStddev();

	@Query("select sum(cast((select avg(a.score) from Position p join p.audits a where p = d and a.isDraftMode=false) as float))/(select count(b) from Position b where b.isDraftMode = false and b.isCancelled = false) from Position d")
	public Double positionScoreAvg();

	@Query("select max(c.score) from Company c")
	public Double companyScoreMax();

	@Query("select min(c.score) from Company c")
	public Double companyScoreMin();

	@Query("select avg(c.score) from Company c")
	public Double companyScoreAvg();

	@Query("select stddev(c.score) from Company c")
	public Double companyScoreStddev();

	@Query("select avg(u.offeredSalary) from Position u where (select avg(j.score) from Position o join o.audits j where o = u) = (select max(cast((select avg(a.score) from Position p join p.audits a where p = d and a.isDraftMode=false) as float)) from Position d)")
	public Double averageSalaryOfferedByThePositionsThatHaveTheHighestAverageAuditScoreAndKnuckles();

	/**
	 * LEVEL B 2/2
	 */
	@Query("select avg(p.items.size), min(p.items.size), max(p.items.size), (sqrt(sum(p.items.size * p.items.size) / count(p.items.size) - (avg(p.items.size) * avg(p.items.size)))) from Provider p")
	public Double[] itemsPerProvider();

	@Query("select a from Provider a order by a.items.size desc")
	public List<Provider> providerTermsofItemsOrdered();

	/**
	 * LEVEL A 3/3
	 */
	@Query("select avg(p.sponsorships.size),min(p.sponsorships.size),max(p.sponsorships.size), (sqrt(sum(p.sponsorships.size * p.sponsorships.size) / count(p.sponsorships.size) - (avg(p.sponsorships.size) * avg(p.sponsorships.size)))) from Provider p")
	public Double[] sponsorshipsPerProvider();

	@Query("select avg(p.sponsorships.size),min(p.sponsorships.size),max(p.sponsorships.size), (sqrt(sum(p.sponsorships.size * p.sponsorships.size) / count(p.sponsorships.size) - (avg(p.sponsorships.size) * avg(p.sponsorships.size)))) from Position p")
	public Double[] sponsorshipsPerPosition();

	@Query("select distinct c from Provider c, Sponsorship d where c.sponsorships.size >= 1.1 * (select avg(c.sponsorships.size) from Provider c)")
	public List<Provider> providers10PercentMoreSponsorships();
}
