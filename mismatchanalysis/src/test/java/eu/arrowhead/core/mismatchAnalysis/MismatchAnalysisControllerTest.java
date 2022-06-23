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

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.dto.shared.AnalysisRequestDTO;
import eu.arrowhead.core.service.MismatchAnalysisService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MismatchAnalysisMain.class)
@ContextConfiguration(classes = { MismatchAnalysisServiceTestContext.class })
public class MismatchAnalysisControllerTest {

	// =================================================================================================
	// members

	private static final String MISMATCHANALYSIS_PROCESS_URI = CommonConstants.MISMATCHANALYSIS_URI
			+ CommonConstants.OP_MISMATCHANALYSIS_ANALYSIS_URI;

	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private ObjectMapper objectMapper;

	private MockMvc mockMvc;

	@MockBean(name = "mockMismatchAnalysisService")
	private MismatchAnalysisService mismatchAnalysisService;

	// =================================================================================================
	// methods

	// -------------------------------------------------------------------------------------------------
	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	// -------------------------------------------------------------------------------------------------
	@Test
	public void testAnalyseServiceOK() throws Exception {
		final ArrayList<String> consumerList = new ArrayList<String>();
		consumerList.add("temperature-test-controller-1");

		final ArrayList<String> providerList = new ArrayList<String>();
		providerList.add("temperature-test-sensor-1");

		final HashMap<String, ArrayList<String>> systems = new HashMap<String, ArrayList<String>>();
		systems.put("consumer", consumerList);
		systems.put("providers", providerList);

		final String serviceDefinition = "get-temperature";

		final AnalysisRequestDTO request = new AnalysisRequestDTO(serviceDefinition, systems);
		postMismatchAnalysisProcess(request, status().isOk());
	}
	
	// -------------------------------------------------------------------------------------------------
	@Test
	public void testAnalyseServiceBadRequest() throws Exception {
		int request = 0;
		postMismatchAnalysisProcess(request, status().isBadRequest());
	}

	// -------------------------------------------------------------------------------------------------
	public void testAnalyseServiceUnauthorized() throws Exception {
		// TODO Add security
	}

	// =================================================================================================
	// assistant methods

	// -------------------------------------------------------------------------------------------------
	private MvcResult postMismatchAnalysisProcess(final Object form, final ResultMatcher matcher)
			throws Exception {
		return this.mockMvc
				.perform(post(MISMATCHANALYSIS_PROCESS_URI)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(form))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(matcher)
				.andReturn();
	}
};