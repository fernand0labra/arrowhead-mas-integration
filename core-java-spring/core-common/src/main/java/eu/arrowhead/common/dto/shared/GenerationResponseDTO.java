package eu.arrowhead.common.dto.shared;

import java.io.Serializable;
import java.util.HashMap;

public class GenerationResponseDTO implements Serializable{

	//=================================================================================================
	// members
	
	private static final long serialVersionUID = -6982678339259420024L;
	private HashMap<String, String> metadataEndpoint;
	
	//-------------------------------------------------------------------------------------------------
	public GenerationResponseDTO() {}
	
	//-------------------------------------------------------------------------------------------------
	public GenerationResponseDTO(HashMap<String, String> metadataEndpoint) {
		this.metadataEndpoint = metadataEndpoint;
	}

	//=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------
	public HashMap<String, String> getMetadataEndpoint() { return metadataEndpoint; }

}
