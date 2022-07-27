package eu.arrowhead.core.mismatchAnalysis.service;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit4.SpringRunner;
import eu.arrowhead.core.service.MismatchAnalysisService;
import eu.arrowhead.core.service.entity.Analysis;

@RunWith(SpringRunner.class)
public class MismatchAnalysisServiceTest {

	// =================================================================================================
	// members

	@InjectMocks
	private MismatchAnalysisService testingObject;

	// =================================================================================================
	// methods

	// -------------------------------------------------------------------------------------------------
	// @Test
	public void testAnalyseServiceSensor1() {
		final ArrayList<String> consumerList = new ArrayList<String>();
		consumerList.add("temperature-controller-2");
		
		final ArrayList<String> providerList = new ArrayList<String>();
		providerList.add("temperature-sensor-2");
		
		final HashMap<String, ArrayList<String>> systems = new HashMap<String, ArrayList<String>>();
		systems.put("consumer", consumerList);
		systems.put("providers", providerList);
		
		final String serviceDefinition = "getTemperature";

		final Analysis analysis = testingObject.analyseService(serviceDefinition, systems);
		Assert.assertEquals("ALTER_G", analysis.getFlag());
		Assert.assertEquals("temperature-sensor-2", analysis.getSystem());
	}
	
	// -------------------------------------------------------------------------------------------------
	// @Test
	public void testAnalyseServiceSensor2() {
		final ArrayList<String> consumerList = new ArrayList<String>();
		consumerList.add("temperature-test-controller-2");
		
		final ArrayList<String> providerList = new ArrayList<String>();
		providerList.add("temperature-test-sensor-2");
		
		final HashMap<String, ArrayList<String>> systems = new HashMap<String, ArrayList<String>>();
		systems.put("consumer", consumerList);
		systems.put("providers", providerList);
		
		final String serviceDefinition = "getTemperature";

		final Analysis analysis = testingObject.analyseService(serviceDefinition, systems);
		Assert.assertEquals("ALTER_T", analysis.getFlag());
		Assert.assertEquals("temperature-test-sensor-2", analysis.getSystem());
	}
	
	// -------------------------------------------------------------------------------------------------
	// @Test
	public void testAnalyseServiceSensor3() {
		final ArrayList<String> consumerList = new ArrayList<String>();
		consumerList.add("temperature-test-controller-3");
		
		final ArrayList<String> providerList = new ArrayList<String>();
		providerList.add("temperature-test-sensor-3");
		
		final HashMap<String, ArrayList<String>> systems = new HashMap<String, ArrayList<String>>();
		systems.put("consumer", consumerList);
		systems.put("providers", providerList);
		
		final String serviceDefinition = "get-temperature";

		final Analysis analysis = testingObject.analyseService(serviceDefinition, systems);
		//Assert.assertEquals("OK", analysis.getFlag());
		//Assert.assertEquals("temperature-test-sensor-1", analysis.getSystem());
	}
	
	// -------------------------------------------------------------------------------------------------
	// @Test
	public void testAnalyseServiceSensor4() {
		final ArrayList<String> consumerList = new ArrayList<String>();
		consumerList.add("temperature-test-controller-4");
		
		final ArrayList<String> providerList = new ArrayList<String>();
		providerList.add("temperature-test-sensor-4");
		
		final HashMap<String, ArrayList<String>> systems = new HashMap<String, ArrayList<String>>();
		systems.put("consumer", consumerList);
		systems.put("providers", providerList);
		
		final String serviceDefinition = "get-temperature";

		final Analysis analysis = testingObject.analyseService(serviceDefinition, systems);
		//Assert.assertEquals("OK", analysis.getFlag());
		//Assert.assertEquals("temperature-test-sensor-1", analysis.getSystem());
	}
	
	// -------------------------------------------------------------------------------------------------
	// @Test
	public void testAnalyseServiceSensor5() {
		final ArrayList<String> consumerList = new ArrayList<String>();
		consumerList.add("temperature-test-controller-5");
		
		final ArrayList<String> providerList = new ArrayList<String>();
		providerList.add("temperature-test-sensor-5");
		providerList.add("temperature-test-sensor-5b");
		
		final HashMap<String, ArrayList<String>> systems = new HashMap<String, ArrayList<String>>();
		systems.put("consumer", consumerList);
		systems.put("providers", providerList);
		
		final String serviceDefinition = "get-temperature";

		final Analysis analysis = testingObject.analyseService(serviceDefinition, systems);
		//Assert.assertEquals("OK", analysis.getFlag());
		//Assert.assertEquals("temperature-test-sensor-1", analysis.getSystem());
	}
	
	// -------------------------------------------------------------------------------------------------
	// @Test
	public void testAnalyseServiceSensor6() {
		final ArrayList<String> consumerList = new ArrayList<String>();
		consumerList.add("temperature-test-controller-6");
		
		final ArrayList<String> providerList = new ArrayList<String>();
		providerList.add("temperature-test-sensor-6");
		providerList.add("temperature-test-sensor-6b");
		
		final HashMap<String, ArrayList<String>> systems = new HashMap<String, ArrayList<String>>();
		systems.put("consumer", consumerList);
		systems.put("providers", providerList);
		
		final String serviceDefinition = "get-temperature";

		final Analysis analysis = testingObject.analyseService(serviceDefinition, systems);
		//Assert.assertEquals("OK", analysis.getFlag());
		//Assert.assertEquals("temperature-test-sensor-1", analysis.getSystem());
	}
	
	// -------------------------------------------------------------------------------------------------
	// @Test
	public void testAnalyseServiceSensor7() {
		final ArrayList<String> consumerList = new ArrayList<String>();
		consumerList.add("temperature-test-controller-7");
		
		final ArrayList<String> providerList = new ArrayList<String>();
		providerList.add("temperature-test-sensor-7");
		
		final HashMap<String, ArrayList<String>> systems = new HashMap<String, ArrayList<String>>();
		systems.put("consumer", consumerList);
		systems.put("providers", providerList);
		
		final String serviceDefinition = "get-temperature";

		final Analysis analysis = testingObject.analyseService(serviceDefinition, systems);
		//Assert.assertEquals("OK", analysis.getFlag());
		//Assert.assertEquals("temperature-test-sensor-1", analysis.getSystem());
	}
	
	// -------------------------------------------------------------------------------------------------
	// @Test
	public void testAnalyseServiceSensorA() {
		final ArrayList<String> consumerList = new ArrayList<String>();
		consumerList.add("temperature-test-controller-A");
		
		final ArrayList<String> providerList = new ArrayList<String>();
		providerList.add("temperature-test-sensor-A1");
		providerList.add("temperature-test-sensor-A2");
		providerList.add("temperature-test-sensor-A3");
		providerList.add("temperature-test-sensor-A4");
		
		final HashMap<String, ArrayList<String>> systems = new HashMap<String, ArrayList<String>>();
		systems.put("consumer", consumerList);
		systems.put("providers", providerList);
		
		final String serviceDefinition = "get-temperature";

		final Analysis analysis = testingObject.analyseService(serviceDefinition, systems);
		//Assert.assertEquals("OK", analysis.getFlag());
		//Assert.assertEquals("temperature-test-sensor-1", analysis.getSystem());
	}
}
