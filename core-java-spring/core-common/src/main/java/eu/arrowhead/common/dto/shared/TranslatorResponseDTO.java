package eu.arrowhead.common.dto.shared;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TranslatorResponseDTO {

	// =================================================================================================
	// members

	private static final long serialVersionUID = -5363562707054976998L;

	private final int translatorId;
    private final String ip;
    private final int port;

	// =================================================================================================
	// methods

	// -------------------------------------------------------------------------------------------------    
    @JsonCreator
    public TranslatorResponseDTO(
    		@JsonProperty("translatorId") int translatorId, 
    		@JsonProperty("ip") String ip, 
    		@JsonProperty("port") int port) {
    	this.translatorId = translatorId;
        this.ip = ip;
        this.port = port;
    }
	// -------------------------------------------------------------------------------------------------    
    public int getTranslatorId() { return translatorId; }
    public String getIp() { return ip; }
    public int getPort() { return port; }
	
}
