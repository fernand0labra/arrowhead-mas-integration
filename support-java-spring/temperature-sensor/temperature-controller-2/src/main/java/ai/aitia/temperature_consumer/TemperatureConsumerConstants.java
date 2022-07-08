package ai.aitia.temperature_consumer;

import java.io.File;

import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.core.network.config.NetworkConfigDefaultHandler;
import org.eclipse.californium.core.network.config.NetworkConfig.Keys;

public class TemperatureConsumerConstants {
	
	//=================================================================================================
	// members
	
	public static final String BASE_PACKAGE = "ai.aitia";
	
	public static final String INTERFACE_SECURE = "HTTP-SECURE-JSON";
	public static final String INTERFACE_INSECURE = "HTTP-INSECURE-JSON";
	public static final String HTTP_METHOD = "http-method";
	
	public static final String GET_TEMPERATURE_SERVICE_DEFINITION = "getTemperature";
	public static final String REQUEST_PARAM_KEY_UNIT = "request-param-unit";
	
	public static final File CONFIG_FILE = new File("Californium.properties");
	public static final String CONFIG_HEADER = "Californium CoAP Properties file for Fileclient";
	public static final int DEFAULT_MAX_RESOURCE_SIZE = 2 * 1024 * 1024; // 2 MB
	public static final int DEFAULT_BLOCK_SIZE = 512;
	
	public static NetworkConfigDefaultHandler DEFAULTS = new NetworkConfigDefaultHandler() {
		@Override
		public void applyDefaults(NetworkConfig config) {
			config.setInt(Keys.MAX_RESOURCE_BODY_SIZE, DEFAULT_MAX_RESOURCE_SIZE);
			config.setInt(Keys.MAX_MESSAGE_SIZE, DEFAULT_BLOCK_SIZE);
			config.setInt(Keys.PREFERRED_BLOCK_SIZE, DEFAULT_BLOCK_SIZE);
		}
	};
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private TemperatureConsumerConstants() {
		throw new UnsupportedOperationException();
	}

}
