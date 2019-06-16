
package repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Curriculum;
import domain.Rookie;

@Repository
public interface CurriculumRepository extends JpaRepository<Curriculum, Integer> {

	@Query("select c from Rookie h join h.curriculums c where h.id = ?1")
	public List<Curriculum> getCurriculumsOfRookie(int rookieId);

	@Query("select c from Rookie h join h.curriculums c where h.id = ?1 and c.id = ?2")
	public Curriculum getCurriculumOfRookie(int rookieId, int curriculumId);

	@Query("select c from Curriculum c join c.positionData p where p.id = ?1")
	public Curriculum getCurriculumOfPositionData(int positionDataId);

	@Query("select c from Curriculum c join c.educationData e where e.id = ?1")
	public Curriculum getCurriculumOfEducationData(int educationDataId);

	@Query("select c from Curriculum c join c.miscellaneousData m where m.id = ?1")
	public Curriculum getCurriculumOfMiscellaneousData(int miscellaneousDataId);

	@Query("select p.title from Rookie h join h.curriculums c join c.positionData p where h = ?1")
	List<String> getTitlesOfPositionDatas(Rookie rookie);

	@Query("select e.degree from Rookie h join h.curriculums c join c.educationData e where h = ?1")
	List<String> getDegreesOfEducationalData(Rookie rookie);

	@Query("select m.freeText from Rookie h join h.curriculums c join c.miscellaneousData m where h = ?1")
	List<String> getFreeTestOfMiscellaneousData(Rookie rookie);

}
