package services;

import java.util.List;

import javax.transaction.Transactional;

import org.aspectj.weaver.patterns.ThisOrTargetAnnotationPointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import domain.Curriculum;
import domain.Rookie;
import domain.PersonalData;
import domain.PositionData;
import repositories.PositionDataRepository;

@Service
@Transactional
public class PositionDataService {
	
	@Autowired
	private PositionDataRepository positionDataRepository;
	@Autowired
	private RookieService rookieService;
	@Autowired
	private CurriculumService curriculumService;
	
	public void save(PositionData p) {
		this.positionDataRepository.save(p);
	}
	
	public void deleteInBatch(Iterable<PositionData> entities) {
		this.positionDataRepository.deleteInBatch(entities);
	}

	public void addOrUpdatePositionDataAsRookie(PositionData positionData, int curriculumId) {
		Rookie rookie = this.rookieService.securityAndRookie();
		
		if(positionData.getId()==0) {
			Curriculum curriculum = this.curriculumService.getCurriculumOfRookie(rookie.getId(), curriculumId);
			Assert.notNull(curriculum);
			List<PositionData> positionsData = curriculum.getPositionData();
			positionsData.add(positionData);
			this.curriculumService.save(curriculum);
			this.curriculumService.flush();
		} else {
			Assert.notNull(this.positionDataRepository.getPositionDataOfRookie(rookie.getId(), positionData.getId()));
			this.save(positionData);
			this.flush();
		}
	}

	private void flush() {
		this.positionDataRepository.flush();
	}

	public PositionData findOne(int positionDataId) {
		return this.positionDataRepository.findOne(positionDataId);
	}

	public void deletePositionDataAsRookie(int positionDataId) {
		Rookie rookie = this.rookieService.securityAndRookie();
		Assert.notNull(this.positionDataRepository.getPositionDataOfRookie(rookie.getId(), positionDataId));
		PositionData positionData = this.findOne(positionDataId);
		Curriculum curriculum = this.curriculumService.getCurriculumOfPositionData(positionDataId);
		List<PositionData> positionsData = curriculum.getPositionData();
		positionsData.remove(positionData);
		curriculum.setPositionData(positionsData);
		this.curriculumService.save(curriculum);
		this.positionDataRepository.delete(positionData);
	}

}
