package eu.arrowhead.core.interfaceGenerator;

import javax.ws.rs.Produces;
import javax.ws.rs.ext.Provider;

import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.CoreCommonConstants;
import eu.arrowhead.common.Defaults;
import eu.arrowhead.common.dto.shared.AnalysisRequestDTO;
import eu.arrowhead.common.dto.shared.AnalysisResponseDTO;
import eu.arrowhead.common.dto.shared.GenerationRequestDTO;
import eu.arrowhead.common.dto.shared.GenerationResponseDTO;
import eu.arrowhead.core.service.InterfaceGeneratorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(tags = { CoreCommonConstants.SWAGGER_TAG_ALL })
@CrossOrigin(maxAge = Defaults.CORS_MAX_AGE, allowCredentials = Defaults.CORS_ALLOW_CREDENTIALS, allowedHeaders = {
		HttpHeaders.ORIGIN, HttpHeaders.CONTENT_TYPE, HttpHeaders.ACCEPT, HttpHeaders.AUTHORIZATION })
@RestController
@RequestMapping(CommonConstants.INTERFACEGENERATOR_URI)
public class InterfaceGeneratorController {

	// =================================================================================================
	// members
		
	private static final String PATH_INTERFACEGENERATOR_GENERATEINTERFACE = "/generate";

	
	private final Logger logger = LogManager.getLogger(InterfaceGeneratorController.class);

	@Autowired
	private InterfaceGeneratorService interfaceGenerationService;

	// =================================================================================================
	// methods

	// -------------------------------------------------------------------------------------------------
	@ApiOperation(value = "Consumer Interface Generation", tags = {CoreCommonConstants.SWAGGER_TAG_CLIENT})
	@ApiResponses(value = {
		        @ApiResponse(code = HttpStatus.SC_OK, message = CoreCommonConstants.SWAGGER_HTTP_200_MESSAGE),
		        @ApiResponse(code = HttpStatus.SC_BAD_REQUEST, message = CoreCommonConstants.SWAGGER_HTTP_400_MESSAGE),
		        @ApiResponse(code = HttpStatus.SC_UNAUTHORIZED, message = CoreCommonConstants.SWAGGER_HTTP_401_MESSAGE)
		    })
	@PostMapping(path = PATH_INTERFACEGENERATOR_GENERATEINTERFACE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public GenerationResponseDTO generateInterface(@RequestBody final GenerationRequestDTO generationRequestDTO) {
		try {
			return new GenerationResponseDTO(interfaceGenerationService.generateInterface(
				generationRequestDTO.getServiceDefinition(),
				generationRequestDTO.getSystems(), 
				generationRequestDTO.getProviderURL()));
		} catch (Exception e) {
			e.printStackTrace();
			return new GenerationResponseDTO(null);
		}
	}

}