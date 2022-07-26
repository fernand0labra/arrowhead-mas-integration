package eu.arrowhead.core.service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import eu.arrowhead.core.service.entity.Analysis;
import eu.arrowhead.core.service.entity.DTOConverter;
import eu.arrowhead.core.service.modules.decision.DecisionMain;
import eu.arrowhead.core.service.modules.input.InputMain;
import eu.arrowhead.core.service.modules.mismatchCheck.MismatchCheckMain;

@Service
public class MismatchAnalysisService {

	// =================================================================================================
	// members
	private final Logger logger = LogManager.getLogger(MismatchAnalysisService.class);

	// =================================================================================================
	// methods
	// -------------------------------------------------------------------------------------------------
	public void start() {
		logger.debug("Starting MismatchAnalysisService");
	}

	public Analysis analyseService(String serviceDefinition, HashMap<String, ArrayList<String>> systems) {
		Analysis analysis = new Analysis();

		Instant beforeService = Instant.now();
		
		try {
			analysis = DecisionMain.main(MismatchCheckMain.main(InputMain.main(serviceDefinition, systems)));
		} catch (Exception e) {
			System.err.println(e.getMessage());
			analysis.setFlag("NOT_OK");
		}
		
		Instant afterService = Instant.now();
		
		long elapsedTime = Duration.between(beforeService, afterService).toMillis();
		
		System.out.println(
				"***********************************************************************************************************\n\n" +
				"SERVICE ELAPSED TIME: \n"
						+ "\t" + String.valueOf(elapsedTime) + "\n");
		
		return analysis;
	}

}
