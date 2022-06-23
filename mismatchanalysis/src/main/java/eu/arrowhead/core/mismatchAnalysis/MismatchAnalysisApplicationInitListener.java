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

import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponents;

import eu.arrowhead.common.ApplicationInitListener;
import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.CoreCommonConstants;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.core.CoreSystemService;

@Component
public class MismatchAnalysisApplicationInitListener extends ApplicationInitListener {
	
	//=================================================================================================
	// members
	
	@Autowired
	private ApplicationContext applicationContext;
	
	//=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------

	//=================================================================================================
	// assistant methods
	
	//-------------------------------------------------------------------------------------------------
	@Override
	protected void customInit(final ContextRefreshedEvent event) {
		logger.debug("customInit started...");

		Connection conn = null;
		
		try {
			// Obtain the DB driver
			Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
	
			// Create the connection
			conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/mismatch_analysis?"
					+ "serverTimezone=UTC&user=root&password=root");
	
			// Initialize the script runner
		    ScriptRunner sr = new ScriptRunner(conn);
		    
		    // Creating a reader object
	        InputStreamReader reader = new InputStreamReader(MismatchAnalysisApplicationInitListener.class.getResourceAsStream("/mismatchAnalysis/dbUtils/setup.sql"));
	     	        
		    // Running the script
		    sr.setLogWriter(null);
		    sr.runScript(reader);
	
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

}