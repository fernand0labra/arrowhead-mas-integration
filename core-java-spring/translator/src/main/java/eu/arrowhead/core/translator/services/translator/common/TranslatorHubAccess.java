package eu.arrowhead.core.translator.services.translator.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TranslatorHubAccess {
	private static final long serialVersionUID = -5363562707054976998L;
	
    private final int translatorId;
    private final String ip;
    private final int port;
    
    @JsonCreator
    public TranslatorHubAccess(
    		@JsonProperty("translatorId") int translatorId, 
    		@JsonProperty("ip") String ip, 
    		@JsonProperty("port") int port) {
    	this.translatorId = translatorId;
        this.ip = ip;
        this.port = port;
    }
    
    public int getTranslatorId() {
        return translatorId;
    }
    
    public String getIp() {
        return ip;
    }
    
    public int getPort() {
        return port;
    }
}
