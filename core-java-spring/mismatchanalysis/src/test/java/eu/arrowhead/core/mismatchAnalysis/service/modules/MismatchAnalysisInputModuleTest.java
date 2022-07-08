package eu.arrowhead.core.mismatchAnalysis.service.modules;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;

import eu.arrowhead.core.service.modules.input.InputMain;

public class MismatchAnalysisInputModuleTest {

	@Test
	public void testWrongConsumerSystemName() {
		String serviceDefinition = "get-temperature";
		HashMap<String, ArrayList<String>> systems = new HashMap<String, ArrayList<String>>();
		
		ArrayList<String> consumer = new ArrayList<String>();
		consumer.add("consumer-not-in-system");
		
		ArrayList<String> providers = new ArrayList<String>();
		providers.add("temperature-sensor-1");
		
		systems.put("consumer", consumer);
		systems.put("providers", providers);
		
		assertEquals(InputMain.main(serviceDefinition, systems), null);
	}
	
	@Test
	public void testWrongProviderSystemName() {
		String serviceDefinition = "get-temperature";
		HashMap<String, ArrayList<String>> systems = new HashMap<String, ArrayList<String>>();
		
		ArrayList<String> consumer = new ArrayList<String>();
		consumer.add("temperature-controller-1");
		
		ArrayList<String> providers = new ArrayList<String>();
		providers.add("provider-not-in-system");
		
		systems.put("consumer", consumer);
		systems.put("providers", providers);
		
		assertEquals(InputMain.main(serviceDefinition, systems), null);
	}
	
	@Test
	public void testWrongServiceDefinition() {
		String serviceDefinition = "service-not-defined";
		HashMap<String, ArrayList<String>> systems = new HashMap<String, ArrayList<String>>();
		
		ArrayList<String> consumer = new ArrayList<String>();
		consumer.add("temperature-controller-1");
		
		ArrayList<String> providers = new ArrayList<String>();
		providers.add("temperature-sensor-1");
		
		systems.put("consumer", consumer);
		systems.put("providers", providers);
		
		assertEquals(InputMain.main(serviceDefinition, systems), null);
	}
	
}
