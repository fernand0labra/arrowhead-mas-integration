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

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import eu.arrowhead.core.service.MismatchAnalysisService;

@Configuration
public class MismatchAnalysisServiceTestContext {

	//=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------
	@Bean
	@Primary // This bean is primary only in test context
	public MismatchAnalysisService mockOrchestratorService() {
		return Mockito.mock(MismatchAnalysisService.class);
	}
}