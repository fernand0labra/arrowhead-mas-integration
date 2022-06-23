/********************************************************************************
 * Copyright (c) 2019 AITIA
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   AITIA - implementation
 *   Arrowhead Consortia - conceptualization
 ********************************************************************************/

package eu.arrowhead.core.mismatchAnalysis;

import java.util.List;

import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.CoreCommonConstants;
import eu.arrowhead.common.Defaults;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.dto.internal.OrchestratorStoreFlexibleListResponseDTO;
import eu.arrowhead.common.dto.internal.OrchestratorStoreFlexibleRequestDTO;
import eu.arrowhead.common.dto.internal.QoSReservationListResponseDTO;
import eu.arrowhead.common.dto.internal.QoSReservationRequestDTO;
import eu.arrowhead.common.dto.internal.QoSTemporaryLockRequestDTO;
import eu.arrowhead.common.dto.internal.QoSTemporaryLockResponseDTO;
import eu.arrowhead.common.dto.shared.CloudRequestDTO;
import eu.arrowhead.common.dto.shared.OrchestrationFlags.Flag;
import eu.arrowhead.common.dto.shared.OrchestrationFormRequestDTO;
import eu.arrowhead.common.dto.shared.OrchestrationResponseDTO;
import eu.arrowhead.common.dto.shared.PreferredProviderDataDTO;
import eu.arrowhead.common.dto.shared.SystemRequestDTO;
import eu.arrowhead.common.exception.BadPayloadException;
import eu.arrowhead.common.verifier.CommonNamePartVerifier;
import eu.arrowhead.common.verifier.ServiceInterfaceNameVerifier;
import eu.arrowhead.core.service.MismatchAnalysisService;
import eu.arrowhead.core.service.entity.Analysis;
import eu.arrowhead.core.service.entity.DTOConverter;
import eu.arrowhead.common.dto.shared.AnalysisRequestDTO;
import eu.arrowhead.common.dto.shared.AnalysisResponseDTO;
import eu.arrowhead.core.service.modules.decision.DecisionMain;
import eu.arrowhead.core.service.modules.input.InputMain;
import eu.arrowhead.core.service.modules.mismatchCheck.MismatchCheckMain;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(tags = { CoreCommonConstants.SWAGGER_TAG_ALL })
@CrossOrigin(maxAge = Defaults.CORS_MAX_AGE, allowCredentials = Defaults.CORS_ALLOW_CREDENTIALS, allowedHeaders = {
		HttpHeaders.ORIGIN, HttpHeaders.CONTENT_TYPE, HttpHeaders.ACCEPT, HttpHeaders.AUTHORIZATION })
@RestController
@RequestMapping(CommonConstants.MISMATCHANALYSIS_URI)
public class MismatchAnalysisController {

	// =================================================================================================
	// members
	
	private static final String PATH_MISMATCHANALYSIS_ANALYSIS = "/analysis";
	
	/*
	private static final String NULL_PARAMETER_ERROR_MESSAGE = " is null.";
	private static final String NULL_OR_BLANK_PARAMETER_ERROR_MESSAGE = " is null or blank.";
	private static final String ID_NOT_VALID_ERROR_MESSAGE = " Id must be greater than 0. ";
	private static final String SYSTEM_NAME_WRONG_FORMAT_ERROR_MESSAGE = "System name has invalid format. System names only contain letters (english alphabet), numbers and dash (-), and have to start with a letter (also cannot end with dash).";
	private static final String SERVICE_DEFINITION_WRONG_FORMAT_ERROR_MESSAGE = "Service definition has invalid format. Service definition only contains letters (english alphabet), numbers and dash (-), and has to start with a letter (also cannot ends with dash).";
	*/

	private final Logger logger = LogManager.getLogger(MismatchAnalysisController.class);

	@Autowired
	private MismatchAnalysisService mismatchAnalysisService;

	// =================================================================================================
	// methods

	// -------------------------------------------------------------------------------------------------
	@ApiOperation(value = "SC Analysis & Provider Compatible Selection", tags = {CoreCommonConstants.SWAGGER_TAG_CLIENT})
	@ApiResponses(value = {
		        @ApiResponse(code = HttpStatus.SC_OK, message = CoreCommonConstants.SWAGGER_HTTP_200_MESSAGE),
		        @ApiResponse(code = HttpStatus.SC_BAD_REQUEST, message = CoreCommonConstants.SWAGGER_HTTP_400_MESSAGE),
		        @ApiResponse(code = HttpStatus.SC_UNAUTHORIZED, message = CoreCommonConstants.SWAGGER_HTTP_401_MESSAGE)
		    })
	@PostMapping(path = PATH_MISMATCHANALYSIS_ANALYSIS, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public AnalysisResponseDTO analyseService(@RequestBody final AnalysisRequestDTO analysisRequestDTO) {
		String request = "\n\n***********************************************************************************************************\n"
				+ "***********************************************************************************************************\n\n"
				+ "REQUEST\n" + "\tServiceDefinition: " + analysisRequestDTO.getServiceDefinition().toString() + "\n"
				+ "\tSystems: \n" + "\t\t" + analysisRequestDTO.getSystems().toString();

		System.out.print(request);

		return DTOConverter.convertAnalysisToAnalysisResponseDTO(mismatchAnalysisService
				.analyseService(analysisRequestDTO.getServiceDefinition(), analysisRequestDTO.getSystems()));
	}

	// =================================================================================================
	// assistant methods

	/*
	 * //---------------------------------------------------------------------------
	 * ---------------------- private void checkCloudRequestDTO(final
	 * CloudRequestDTO cloud, final String origin) {
	 * logger.debug("checkCloudRequestDTO started...");
	 * 
	 * if (cloud == null) { throw new BadPayloadException("Cloud" +
	 * NULL_PARAMETER_ERROR_MESSAGE, HttpStatus.SC_BAD_REQUEST, origin); }
	 * 
	 * if (Utilities.isEmpty(cloud.getOperator())) { throw new
	 * BadPayloadException("Cloud operator" + NULL_OR_BLANK_PARAMETER_ERROR_MESSAGE,
	 * HttpStatus.SC_BAD_REQUEST, origin); }
	 * 
	 * if (Utilities.isEmpty(cloud.getName())) { throw new
	 * BadPayloadException("Cloud name" + NULL_OR_BLANK_PARAMETER_ERROR_MESSAGE,
	 * HttpStatus.SC_BAD_REQUEST, origin); } }
	 */

}