package ai.aitia.temperature_consumer;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.Instant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.elements.exception.ConnectorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpMethod;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import ai.aitia.arrowhead.application.library.ArrowheadService;
import ai.aitia.temperature_common.dto.TemperatureResponseDTO;
import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.SSLProperties;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.dto.shared.OrchestrationFlags.Flag;
import eu.arrowhead.common.dto.shared.OrchestrationFormRequestDTO;
import eu.arrowhead.common.dto.shared.OrchestrationFormRequestDTO.Builder;
import eu.arrowhead.common.dto.shared.OrchestrationResponseDTO;
import eu.arrowhead.common.dto.shared.OrchestrationResultDTO;
import eu.arrowhead.common.dto.shared.ServiceInterfaceResponseDTO;
import eu.arrowhead.common.dto.shared.ServiceQueryFormDTO;
import eu.arrowhead.common.exception.InvalidParameterException;

@SpringBootApplication
@ComponentScan(basePackages = { CommonConstants.BASE_PACKAGE, TemperatureConsumerConstants.BASE_PACKAGE })
public class TemperatureConsumerMain implements ApplicationRunner {

	// =================================================================================================
	// members

	@Autowired
	private ArrowheadService arrowheadService;

	@Autowired
	protected SSLProperties sslProperties;

	private final Logger logger = LogManager.getLogger(TemperatureConsumerMain.class);

	// =================================================================================================
	// methods

	// ------------------------------------------------------------------------------------------------
	public static void main(final String[] args) {
		SpringApplication.run(TemperatureConsumerMain.class, args);
	}

	// -------------------------------------------------------------------------------------------------
	@Override
	public void run(final ApplicationArguments args) throws Exception {
		getTemperatureServiceOrchestrationAndConsumption();
	}

	// -------------------------------------------------------------------------------------------------
	public void getTemperatureServiceOrchestrationAndConsumption() {
		logger.info("Orchestration request for " + TemperatureConsumerConstants.GET_TEMPERATURE_SERVICE_DEFINITION
				+ " service:");
		final ServiceQueryFormDTO serviceQueryForm = new ServiceQueryFormDTO.Builder(
				TemperatureConsumerConstants.GET_TEMPERATURE_SERVICE_DEFINITION).interfaces(getInterface()).build();	

		final Builder orchestrationFormBuilder = arrowheadService.getOrchestrationFormBuilder();
		final OrchestrationFormRequestDTO orchestrationFormRequest = orchestrationFormBuilder.requestedService(serviceQueryForm)
				.flag(Flag.MATCHMAKING, false)
				.flag(Flag.OVERRIDE_STORE, true)
				.flag(Flag.ENABLE_MISMATCH_ANALYSIS, true).build();

		printOut(orchestrationFormRequest);

		final OrchestrationResponseDTO orchestrationResponse = arrowheadService
				.proceedOrchestration(orchestrationFormRequest);

		logger.info("Orchestration response:");
		printOut(orchestrationResponse);

		if (orchestrationResponse == null) {
			logger.info("No orchestration response received");
		} else if (orchestrationResponse.getResponse().isEmpty()) {
			logger.info("No provider found during the orchestration");
		} else {
			final OrchestrationResultDTO orchestrationResult = orchestrationResponse.getResponse().get(0);
			validateOrchestrationResult(orchestrationResult,
					TemperatureConsumerConstants.GET_TEMPERATURE_SERVICE_DEFINITION);

			logger.info("Get the temperature:");
			
			final String[] queryParamUnit = {
					orchestrationResult.getMetadata().get(TemperatureConsumerConstants.REQUEST_PARAM_KEY_UNIT),
					"kelvin" };
			final String token = orchestrationResult.getAuthorizationTokens() == null ? null
					: orchestrationResult.getAuthorizationTokens().get(getInterface());

			/* ********************************************************************************************************** */
			/* 												COAP CONSUMER												  */
			/* ********************************************************************************************************** */
			
			System.out.println(
					"***********************************************************************************************************\n\n" +
					"PROVIDER SYSTEM: " + orchestrationResult.getProvider().getSystemName() + "\n" 
							+ "\tPROTOCOL: COAP \n"
							+ "\tADDRESS: " + orchestrationResult.getProvider().getAddress() + "\n"
							+ "\tPORT: " + orchestrationResult.getProvider().getPort() + "\n"
							+ "\tSERVICE: " + orchestrationResult.getServiceUri() + "\n");
			
			NetworkConfig config = NetworkConfig.createWithFile(TemperatureConsumerConstants.CONFIG_FILE, TemperatureConsumerConstants.CONFIG_HEADER, TemperatureConsumerConstants.DEFAULTS);
			NetworkConfig.setStandard(config);
			URI uri = null;

			try {
				uri = new URI("coap://" 
							+ orchestrationResult.getProvider().getAddress() 
							+ ":"
							+ orchestrationResult.getProvider().getPort() 
							+ orchestrationResult.getServiceUri()
							+ "/?unit=kelvin");
			} catch (URISyntaxException e) {
				e.printStackTrace();
				System.exit(-1);
			}
			
			CoapClient coapClient = new CoapClient(uri);
			CoapResponse coapResponse = null;

			Instant beforeRequest = Instant.now();
			
			try {
				coapResponse = coapClient.get();
			} catch (ConnectorException | IOException e) {
				e.printStackTrace();
			}

			if (coapResponse != null) {
				String responseText = coapResponse.getResponseText();
				byte[] responseByte = coapResponse.getPayload();
				
				if(responseByte != null) {
					JsonFactory jsonFactory = new JsonFactory();
					ObjectMapper objectMapper = new ObjectMapper(jsonFactory);
					
					try {
						TemperatureResponseDTO temperatureResponse = objectMapper.readValue(responseByte, TemperatureResponseDTO.class);
						
						Instant afterRequest = Instant.now();
						
						long elapsedTime = Duration.between(beforeRequest, afterRequest).toMillis();
						
						System.out.println(
								"***********************************************************************************************************\n\n" +
								"REQUEST ELAPSED TIME: \n"
										+ "\t" + String.valueOf(elapsedTime) + "\n");
						
						logger.info("RESPONSE: \n\t" + responseText 
								+ "\n\t temperature: " + temperatureResponse.getTemperature()
								+ "\n\t time: " + temperatureResponse.getTime()
								+ "\n\t unit: " + temperatureResponse.getUnit());
					} catch (Exception e) {
						e.printStackTrace();
					}	
				} else
					logger.info("RESPONSE: " + responseText);
			} else
				logger.info("No response received.");
			
			coapClient.shutdown();
		}
	}

	// =================================================================================================
	// assistant methods

	// -------------------------------------------------------------------------------------------------
	private String getInterface() {
		return sslProperties.isSslEnabled() ? TemperatureConsumerConstants.INTERFACE_SECURE : TemperatureConsumerConstants.INTERFACE_INSECURE;
	}

	// -------------------------------------------------------------------------------------------------
	private void validateOrchestrationResult(final OrchestrationResultDTO orchestrationResult,
			final String serviceDefinition) {
		if (!orchestrationResult.getService().getServiceDefinition().equalsIgnoreCase(serviceDefinition)) {
			throw new InvalidParameterException("Requested and orchestrated service definition do not match");
		}

//		boolean hasValidInterface = false;
//		for (final ServiceInterfaceResponseDTO serviceInterface : orchestrationResult.getInterfaces()) {
//			if (serviceInterface.getInterfaceName().equalsIgnoreCase(getInterface())) {
//				hasValidInterface = true;
//				break;
//			}
//		}
//		if (!hasValidInterface) {
//			throw new InvalidParameterException("Requested and orchestrated interface do not match");
//		}
	}

	// -------------------------------------------------------------------------------------------------
	private void printOut(final Object object) {
		System.out.println(Utilities.toPrettyJson(Utilities.toJson(object)));
	}
}
