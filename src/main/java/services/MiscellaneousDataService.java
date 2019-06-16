
package services;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.MiscellaneousDataRepository;
import domain.Curriculum;
import domain.MiscellaneousData;
import domain.Rookie;
import repositories.MiscellaneousDataRepository;


@Service
@Transactional
public class MiscellaneousDataService {

	@Autowired
	private MiscellaneousDataRepository	miscellaneousDataRepository;
	@Autowired
	private RookieService				rookieService;
	@Autowired
	private CurriculumService			curriculumService;
	@Autowired
	private Validator validator;

	public void save(MiscellaneousData m) {
		this.miscellaneousDataRepository.save(m);
	}

	public MiscellaneousData findOne(int miscellaneousDataId) {
		return this.miscellaneousDataRepository.findOne(miscellaneousDataId);
	}

	public MiscellaneousData getMiscellaneousDataOfLoggedRookie(int miscellaneousDataId) {
		Rookie rookie = this.rookieService.securityAndRookie();
		MiscellaneousData miscellaneousData = this.miscellaneousDataRepository
				.getMiscellaneousDataOfRookie(rookie.getId(), miscellaneousDataId);
		Assert.notNull(miscellaneousData);
		return miscellaneousData;
	}

	public void deleteInBatch(Iterable<MiscellaneousData> entities) {
		this.miscellaneousDataRepository.deleteInBatch(entities);
	}

	public void addOrUpdateMiscellaneousDataAsRookie(MiscellaneousData miscellaneousData, int curriculumId) {
		Rookie rookie = this.rookieService.securityAndRookie();

		if (miscellaneousData.getId() == 0) {
			Curriculum curriculum = this.curriculumService.getCurriculumOfRookie(rookie.getId(), curriculumId);
			Assert.notNull(curriculum);
			List<MiscellaneousData> miscellaneoussData = curriculum.getMiscellaneousData();
			miscellaneoussData.add(miscellaneousData);
			this.curriculumService.save(curriculum);
			this.curriculumService.flush();
		} else {
			Assert.notNull(this.miscellaneousDataRepository.getMiscellaneousDataOfRookie(rookie.getId(),
					miscellaneousData.getId()));
			this.save(miscellaneousData);
			this.flush();
		}
	}

	private void flush() {
		this.miscellaneousDataRepository.flush();
	}

	public MiscellaneousData reconstruct(MiscellaneousData miscellaneousData, BindingResult binding) {
		MiscellaneousData miscellaneousDataReconstruct = new MiscellaneousData();

		if (miscellaneousData.getId() == 0) {
			miscellaneousDataReconstruct = miscellaneousData;
			miscellaneousDataReconstruct.setAttachments(new ArrayList<String>());
		} else {
			MiscellaneousData miscellaneousDataFounded = this.findOne(miscellaneousData.getId());

			miscellaneousDataReconstruct = miscellaneousData;
			miscellaneousDataReconstruct.setAttachments(miscellaneousDataFounded.getAttachments());
		}

		this.validator.validate(miscellaneousDataReconstruct, binding);

		return miscellaneousDataReconstruct;
	}

	public void deleteMiscellaneousDataAsRookie(int miscellaneousDataId) {
		Rookie rookie = this.rookieService.securityAndRookie();
		Assert.notNull(
				this.miscellaneousDataRepository.getMiscellaneousDataOfRookie(rookie.getId(), miscellaneousDataId));
		MiscellaneousData miscellaneousData = this.findOne(miscellaneousDataId);
		Curriculum curriculum = this.curriculumService.getCurriculumOfMiscellaneousData(miscellaneousDataId);
		List<MiscellaneousData> miscellaneoussData = curriculum.getMiscellaneousData();
		miscellaneoussData.remove(miscellaneousData);
		curriculum.setMiscellaneousData(miscellaneoussData);
		this.curriculumService.save(curriculum);
		this.miscellaneousDataRepository.delete(miscellaneousData);
	}

	public void addAttachmentAsRookie(int miscellaneousDataId, String attachment) {
		Assert.isTrue(this.isUrl(attachment));

		Rookie rookie = this.rookieService.securityAndRookie();
		MiscellaneousData miscellaneousData = this.miscellaneousDataRepository
				.getMiscellaneousDataOfRookie(rookie.getId(), miscellaneousDataId);
		Assert.notNull(miscellaneousData);
		Assert.isTrue(!attachment.contentEquals(""));
		Assert.notNull(attachment);
		List<String> attachments = miscellaneousData.getAttachments();
		attachments.add(attachment);
		this.save(miscellaneousData);
	}

	public void deleteAttachmentAsRookie(int miscellaneousDataId, int attachmentIndex) {
		Rookie rookie = this.rookieService.securityAndRookie();
		MiscellaneousData miscellaneousData = this.miscellaneousDataRepository
				.getMiscellaneousDataOfRookie(rookie.getId(), miscellaneousDataId);
		Assert.notNull(miscellaneousData);
		List<String> attachments = miscellaneousData.getAttachments();
		Assert.isTrue(attachmentIndex < attachments.size());
		attachments.remove(attachmentIndex);
		miscellaneousData.setAttachments(attachments);
		this.save(miscellaneousData);
	}

	public List<String> getAttachmentsOfMiscellaneousDataOfLoggedRookie(int miscellaneousDataId) {
		return this.getMiscellaneousDataOfLoggedRookie(miscellaneousDataId).getAttachments();
	}

	public Boolean isUrl(String url) {
		try {
			new URL(url).toURI();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public MiscellaneousData create() {
		MiscellaneousData result = new MiscellaneousData();
		List<String> attachments = new ArrayList<String>();

		result.setFreeText("");
		result.setAttachments(attachments);

		return result;
	}
}
