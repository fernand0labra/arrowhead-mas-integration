package eu.arrowhead.common.dto.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class GenerationRequestDTO implements Serializable {

	//=================================================================================================
	// members
	
	private static final long serialVersionUID = 5518448064100057548L;
	
	private final String serviceDefinition;
	private final HashMap<String, String> systems;
	private final String providerURL;
	
	//-------------------------------------------------------------------------------------------------
	public GenerationRequestDTO(String serviceDefinition, HashMap<String, String> systems, String providerURL) {
		this.serviceDefinition = serviceDefinition;
		this.systems = systems;
		this.providerURL = providerURL;
	}

	//=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------
	public String getServiceDefinition() { return serviceDefinition; }
	public HashMap<String, String> getSystems() { return systems; }
	public String getProviderURL() { return providerURL; }
}
