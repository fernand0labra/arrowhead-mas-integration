package eu.arrowhead.common.dto.shared;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TranslatorRequestDTO {

	// =================================================================================================
	// members

	private static final long serialVersionUID = -5363562707054976998L;

	private String producerName;
    private String producerAddress;
    private String consumerName;
    private String consumerAddress;

	// =================================================================================================
	// methods

	// -------------------------------------------------------------------------------------------------
    @JsonCreator
    public TranslatorRequestDTO( @JsonProperty("producerName") String producerName,
            @JsonProperty("producerAddress") String producerAddress,
            @JsonProperty("consumerName") String consumerName,
            @JsonProperty("consumerAddress") String consumerAddress) {
    	this.producerName = producerName;
        this.producerAddress = producerAddress;
        this.consumerName = consumerName;
        this.consumerAddress = consumerAddress;
	}

	// -------------------------------------------------------------------------------------------------
    public String getProducerName() { return producerName; }
    public String getProducerAddress() { return producerAddress; }
    public String getConsumerName() { return consumerName; }
    public String getConsumerAddress() { return consumerAddress; }
    
}
