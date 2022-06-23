package eu.arrowhead.core.mismatchAnalysis.service.modules;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

import eu.arrowhead.core.service.entity.Analysis;
import eu.arrowhead.core.service.modules.decision.DecisionMain;

public class MismatchAnalysisDecisionModuleTest {

	@Test
	public void testEmptyAnalysis() {
		ArrayList<Analysis> analysisList = new ArrayList<Analysis>();
		analysisList.add(new Analysis());
		
		assertEquals(DecisionMain.main(analysisList).getFlag(), "NOT_OK");
	}

}
