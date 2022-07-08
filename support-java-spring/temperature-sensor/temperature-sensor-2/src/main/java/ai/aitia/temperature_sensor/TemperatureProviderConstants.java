package ai.aitia.temperature_sensor;

public class TemperatureProviderConstants {
	
	//=================================================================================================
	// members
	
	public static final String BASE_PACKAGE = "ai.aitia";
	
	public static final String GET_TEMPERATURE_SERVICE_DEFINITION = "getTemperature";
	
	public static final String HTTP_JSON_INTERFACE_SECURE = "HTTP-SECURE-JSON";
	public static final String HTTP_JSON_INTERFACE_INSECURE = "HTTP-INSECURE-JSON";
	
	public static final String COAP_JSON_INTERFACE_SECURE = "COAP-SECURE-JSON";
	public static final String COAP_JSON_INTERFACE_INSECURE = "COAP-INSECURE-JSON";
	
	public static final String COAP_XML_INTERFACE_SECURE = "COAP-SECURE-XML";
	public static final String COAP_XML_INTERFACE_INSECURE = "COAP-INSECURE-XML";
	
	public static final String HTTP_METHOD = "http-method";
	
	public static final String TEMPERATURE_URI = "/temperature";
	public static final String PATH_VARIABLE_UNIT = "unit";
	public static final String REQUEST_PARAM_KEY_UNIT = "request-param-unit";
	public static final String REQUEST_PARAM_UNIT = "unit";
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private TemperatureProviderConstants() {
		throw new UnsupportedOperationException();
	}
}
