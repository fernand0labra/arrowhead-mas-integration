package eu.arrowhead.core.interfaceGenerator.service;

import java.util.HashMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit4.SpringRunner;

import eu.arrowhead.core.service.InterfaceGeneratorService;
import eu.arrowhead.core.service.utils.GenerationException;

//@RunWith(SpringRunner.class)
public class InterfaceGeneratorServiceTest {

	// =================================================================================================
	// members

	@InjectMocks
	private InterfaceGeneratorService testingObject;

	// =================================================================================================
	// methods

	// -------------------------------------------------------------------------------------------------
//	@Test
	public void testGenerateService() {
		final HashMap<String, String> systems = new HashMap<String, String>();
		systems.put("consumer", "temperature-controller-2");
		systems.put("provider", "temperature-sensor-2");

		final String serviceDefinition = "getTemperature";
		final String serviceUri = "/temperature";

		try {
			testingObject.generateInterface(serviceDefinition, systems, serviceUri);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
