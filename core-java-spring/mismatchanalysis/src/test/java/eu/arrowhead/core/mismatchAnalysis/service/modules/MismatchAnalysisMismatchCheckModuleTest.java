package eu.arrowhead.core.mismatchAnalysis.service.modules;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;

import eu.arrowhead.core.service.entity.Analysis;
import eu.arrowhead.core.service.entity.serviceContract.ServiceContract;
import eu.arrowhead.core.service.modules.mismatchCheck.MismatchCheckMain;

public class MismatchAnalysisMismatchCheckModuleTest {
	
	//@Test
	public void testEmptyServiceContract() {		
		ArrayList<ServiceContract> consumer = new ArrayList<ServiceContract>();
		consumer.add(new ServiceContract());
		
		ArrayList<ServiceContract> providers = new ArrayList<ServiceContract>();
		providers.add(new ServiceContract());
		
		HashMap<String, ArrayList<ServiceContract>> serviceContracts = new HashMap<String, ArrayList<ServiceContract>>();
		serviceContracts.put("consumer", consumer);
		serviceContracts.put("provider", providers);

		ArrayList<Analysis> analysisList = MismatchCheckMain.main(serviceContracts);
		
		assertEquals(analysisList.get(0).getQuantitativeM(), 100.0, 0); // Compatibility
		assertEquals(analysisList.get(0).getQuantitativeU(), 100.0, 0); // Uncertainty
		
		assertEquals(analysisList.get(0).getQualitativeM(), "absolute"); // Compatibility
		assertEquals(analysisList.get(0).getQualitativeU(), "absolute"); // Uncertainty
	}
}
