package eu.arrowhead.core.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import eu.arrowhead.core.entity.InterfaceMetadata;
import eu.arrowhead.core.service.utils.CDLUtils;
import eu.arrowhead.core.service.utils.GenerationException;
import eu.arrowhead.core.service.utils.GenerationUtils;

@Service
public class InterfaceGeneratorService {

	// =================================================================================================
	// members
	private final Logger logger = LogManager.getLogger(InterfaceGeneratorService.class);

	private static InterfaceMetadata metadataProvider = null;
	private static InterfaceMetadata metadataConsumer = null;

//	private static TypeSafeProperties request = CodgenUtil.getProp("request_config");

	private static ArrayList<ArrayList<String>> providerRequestClasses = new ArrayList<ArrayList<String>>();
	private static ArrayList<String> providerResponseClasses = new ArrayList<String>();
	
	private static ArrayList<ArrayList<String>> consumerRequestClasses = new ArrayList<ArrayList<String>>();
	private static ArrayList<String> consumerResponseClasses = new ArrayList<String>();
	
	// =================================================================================================
	// methods
	
	// -------------------------------------------------------------------------------------------------
	public void start() {
		logger.debug("Starting InterfaceGenerationService");
	}

	// -------------------------------------------------------------------------------------------------
	public HashMap<String, String> generateInterface(String serviceDefinition, HashMap<String, String> systems, String providerURL) throws GenerationException, ClassNotFoundException {

		Instant beforeService = Instant.now();
		
		metadataConsumer = CDLUtils.parseFile(serviceDefinition, systems.get("consumer"));
		Boolean validConsumerMetadata = metadataValidation(metadataConsumer);
		
		metadataProvider = CDLUtils.parseFile(serviceDefinition, systems.get("provider"));
		Boolean validProviderMetadata = metadataValidation(metadataProvider);

		HashMap<String,String> metadataEndpoint = null;
		
		if (validConsumerMetadata && validProviderMetadata) {

			if (metadataProvider.getRequest())
				for (int i = 0; i < metadataProvider.getElementsRequest().size(); i++) {
					ArrayList<String[]> elements_requestP = metadataProvider.getElementsRequest().get(i).getElements();
					providerRequestClasses.add(GenerationUtils.generateClass(elements_requestP, "RequestDTO_P" + i));
				}

			if (metadataProvider.getResponse())
				for (int j = 0; j < metadataProvider.getElementsResponse().size(); j++) {
					ArrayList<String[]> elements_responseP = metadataProvider.getElementsResponse().get(j).getElements();
					providerResponseClasses = GenerationUtils.generateClass(elements_responseP, "ResponseDTO_P" + j);
				}

			if (metadataConsumer.getRequest())
				for (int i = 0; i < metadataConsumer.getElementsRequest().size(); i++) {
					ArrayList<String[]> elements_requestC = metadataConsumer.getElementsRequest().get(i).getElements();
					consumerRequestClasses.add(GenerationUtils.generateClass(elements_requestC, "RequestDTO_C" + i));
				}

			if (metadataConsumer.getResponse())
				for (int j = 0; j < metadataConsumer.getElementsResponse().size(); j++) {
					ArrayList<String[]> elements_responseC = metadataConsumer.getElementsResponse().get(j).getElements();
					consumerResponseClasses = GenerationUtils.generateClass(elements_responseC, "ResponseDTO_C" + j);
				}

			// Server generation.. Consumer Side
			metadataEndpoint = GenerationUtils.generateServer(metadataConsumer.getProtocol());
			GenerationUtils.generateResources(metadataConsumer, metadataProvider, providerURL);
			GenerationUtils.generateProviderInterpreter(metadataProvider);
			
			// Payload Translator
			GenerationUtils.generatePayloadTranslator(metadataConsumer, metadataProvider);
		} else
			throw new GenerationException(" ONE OR MORE OF THE CDLs ARE MALFORMED ");
		try {
			execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Instant afterService = Instant.now();
		
		long elapsedTime = Duration.between(beforeService, afterService).toMillis();
		
		System.out.println(
				"***********************************************************************************************************\n\n" +
				"GENERATED INTERFACE: \n" 
						+ "\tADDRESS: " + metadataEndpoint.get("address") + "\n"
						+ "\tPORT: " + metadataEndpoint.get("port") + "\n"
						+ "\tCONSUMER\n" 
							+ "\t\tSYSTEM: " + systems.get("consumer") + "\n"
							+ "\t\tADDRESS: " + metadataConsumer.getProtocol() + "://192.168.1.44:8080" + "\n"
						+ "\tPROVIDER\n" 
							+ "\t\tSYSTEM: " + systems.get("provider") + "\n"
							+ "\t\tADDRESS: " + metadataProvider.getProtocol() + "://192.168.1.55:8888" + "\n");
		
		System.out.println(
				"***********************************************************************************************************\n\n" +
				"SERVICE ELAPSED TIME: \n"
						+ "\t" + String.valueOf(elapsedTime) + "\n");
						
		
		return metadataEndpoint;
	}

	// =================================================================================================
	// assistant methods

	// -------------------------------------------------------------------------------------------------
	public static void execute() throws InterruptedException, IOException {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		ProcessBuilder processBuilder = new ProcessBuilder();
		
		processBuilder
			.directory(null)
			.command(("src/main/resources/execution/init.sh"));
		
		try {
			Process process = processBuilder.start();
			executor.submit(new ProcessTask(process.getInputStream()));
			
			System.out.println(new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8));
		} finally {
			executor.shutdown();
		}
	}
	
	// -------------------------------------------------------------------------------------------------
	public static Boolean metadataValidation(InterfaceMetadata metadata) {
		Boolean validation = true;
		if (metadata.getProtocol().equals(""))
			validation = false;
		
		else if (metadata.getMethod().equals(""))
			validation = false;
		
		else if (metadata.getId() == null)
			validation = false;
		
		else if (metadata.getPathResource().equals(""))
			validation = false;
		
		else if (metadata.getRequest()) {
			if (metadata.getMediatypeRequest().equals(""))
				validation = false;
			if (metadata.getElementsRequest().isEmpty()) // TODO Is an empty request possible?
				validation = false;
		}
		
		else if (metadata.getResponse()) { // Added fernand0labra
			if (metadata.getMediatypeResponse().equals(""))
				validation = false;
			if (metadata.getElementsResponse().isEmpty()) // TODO Is an empty response possible?
				validation = false;
		}
		
		return validation;
	}

	// =================================================================================================
	// nested classes
	
	// -------------------------------------------------------------------------------------------------
	private static class ProcessTask implements Callable<List<String>> {

		private InputStream inputStream;

		public ProcessTask(InputStream inputStream) {
			this.inputStream = inputStream;
		}

		@Override
		public List<String> call() {
			return new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.toList());
		}
	}
}