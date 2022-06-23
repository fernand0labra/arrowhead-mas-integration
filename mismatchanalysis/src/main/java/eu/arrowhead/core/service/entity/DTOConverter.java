package eu.arrowhead.core.service.entity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.Assert;

import eu.arrowhead.common.dto.shared.AnalysisResponseDTO;

/**
 * DTOConverter :: Mismatch Analysis System Course 'Project in Computer Science'
 * :: Luleå Tekniska Universitet
 * 
 * @author Fernando Labra Caso (fernandolabracaso@gmail.com)
 * @version 1.0.0
 */

public class DTOConverter {

	// =================================================================================================
	// methods

	// -------------------------------------------------------------------------------------------------
	public static AnalysisResponseDTO convertAnalysisToAnalysisResponseDTO(final Analysis analysis) {
		// Assert.notNull(analysis, "analysis is null");
		return analysis != null
				? new AnalysisResponseDTO(analysis.getMismatch(), analysis.getUncertainty(), analysis.getNotation(),
						analysis.getTagMeaning(), analysis.getQuantitativeM(), analysis.getQualitativeM(),
						analysis.getQuantitativeU(), analysis.getQualitativeU(), analysis.getFlag(),
						analysis.getSystem(), analysis.getService())
				: new AnalysisResponseDTO(null, null, null,null, 0, "", 0, "", "", "", "");
	}

	// -------------------------------------------------------------------------------------------------
	public static List<AnalysisResponseDTO> convertAnalysisListToAnalysisResponseDTOList(
			final List<Analysis> analysis) {
		// Assert.notNull(analysis, "analysis list is null");
		final List<AnalysisResponseDTO> analysisResponse = new ArrayList<>(analysis.size());
		for (final Analysis temperature : analysis) {
			analysisResponse.add(convertAnalysisToAnalysisResponseDTO(temperature));
		}
		return analysisResponse;
	}

	// =================================================================================================
	// assistant methods

	// -------------------------------------------------------------------------------------------------
	public DTOConverter() {
		throw new UnsupportedOperationException();
	}
}
