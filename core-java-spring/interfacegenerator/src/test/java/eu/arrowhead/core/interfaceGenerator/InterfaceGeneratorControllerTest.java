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

package eu.arrowhead.core.interfaceGenerator;

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
import eu.arrowhead.common.dto.shared.GenerationRequestDTO;
import eu.arrowhead.core.service.InterfaceGeneratorService;

//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = InterfaceGeneratorMain.class)
//@ContextConfiguration(classes = { InterfaceGeneratorServiceTestContext.class })
public class InterfaceGeneratorControllerTest {

	// =================================================================================================
	// members

	private static final String INTERFACEGENERATOR_PROCESS_URI = CommonConstants.INTERFACEGENERATOR_URI
			+ CommonConstants.OP_INTERFACEGENERATOR_GENERATE_URI;

	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private ObjectMapper objectMapper;

	private MockMvc mockMvc;

	@MockBean(name = "mockInterfaceGeneratorService")
	private InterfaceGeneratorService interfaceGeneratorService;

	// =================================================================================================
	// methods

	// -------------------------------------------------------------------------------------------------
	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	// -------------------------------------------------------------------------------------------------
//	@Test
	public void testAnalyseServiceOK() throws Exception {

		final HashMap<String, String> systems = new HashMap<String, String>();
		systems.put("consumer", "temperature-controller-2");
		systems.put("provider", "temperature-sensor-2");

		final String serviceDefinition = "getTemperature";
		final String serviceUri = "/temperature";

		GenerationRequestDTO request = new GenerationRequestDTO(serviceDefinition, systems, serviceUri);
		postInterfaceGeneratorProcess(request, status().isOk());
	}
	
	// -------------------------------------------------------------------------------------------------
	// @Test
	public void testAnalyseServiceBadRequest() throws Exception {
		int request = 0;
		postInterfaceGeneratorProcess(request, status().isBadRequest());
	}

	// -------------------------------------------------------------------------------------------------
	public void testAnalyseServiceUnauthorized() throws Exception {
		// TODO Add security
	}

	// =================================================================================================
	// assistant methods

	// -------------------------------------------------------------------------------------------------
	private MvcResult postInterfaceGeneratorProcess(final Object form, final ResultMatcher matcher)
			throws Exception {
		return this.mockMvc
				.perform(post(INTERFACEGENERATOR_PROCESS_URI)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(form))
				.accept(MediaType.TEXT_PLAIN))
				.andExpect(matcher)
				.andReturn();
	}
};