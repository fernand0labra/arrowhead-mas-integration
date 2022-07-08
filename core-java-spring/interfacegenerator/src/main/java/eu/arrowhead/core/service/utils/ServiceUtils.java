package eu.arrowhead.core.service.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceConfigurationError;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.ws.rs.Produces;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.Provider;

import org.springframework.http.MediaType;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeVariableName;

public class ServiceUtils {

	// =================================================================================================
	// members

	private static final ObjectMapper mapper = JacksonJsonProviderAtRest.getMapper();
	private static final HostnameVerifier allHostsValid = (hostname, session) -> { return true; }; // Decide whether to allow the connection...

	// =================================================================================================
	// methods

	/**
	 * Fetch the request payload directly from the InputStream without a JSON serializer
	 * 
	 * @param is
	 * @return
	 */
	// -------------------------------------------------------------------------------------------------
	public static String getRequestPayload(InputStream is) {
		StringBuilder sb = new StringBuilder();
		String line;
		
		try (BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
			while ((line = br.readLine()) != null)
				sb.append(line);
		} catch (UnsupportedEncodingException e) {
			throw new AssertionError("getRequestPayload InputStreamReader has unsupported character set! Code needs to be changed!", e);
		} catch (IOException e) {
			throw new RuntimeException("IOException occured while reading an incoming request payload", e);
		}

		if (!sb.toString().isEmpty()) {
			String payload = toPrettyJson(sb.toString(), null);
			return payload != null ? payload : "";
		} else
			return "";
	}

	// -------------------------------------------------------------------------------------------------
	public static String getUri(String address, int port, String serviceUri, boolean isSecure, boolean serverStart) {
		if (address == null)
			throw new NullPointerException("Address can not be null (Utility:getUri throws NPE)");

		UriBuilder builder = UriBuilder.fromPath("").host(address);
				
		if (isSecure)
			builder.scheme("https");
		else
			builder.scheme("http");
		
		if (port > 0)
			builder.port(port);
		
		if (serviceUri != null)
			builder.path(serviceUri);

		String url = builder.toString();
		try {
			new URI(url);
		} catch (URISyntaxException e) {
			if (serverStart)
				throw new ServiceConfigurationError(url + " is not a valid URL to start a HTTP server! Please fix the address field in the properties file.");
			else
				System.err.println(url + " is not a valid URL!");
		}
		
		return url;
	}

	// -------------------------------------------------------------------------------------------------
	public static String stripEndSlash(String uri) {
		return uri != null && uri.endsWith("/") ? uri.substring(0, uri.length() - 1) : uri;
	}

	// -------------------------------------------------------------------------------------------------
	public static String toPrettyJson(String jsonString, Object obj) {
		try {
			if (jsonString != null) {
				jsonString = jsonString.trim();
				
				if (jsonString.startsWith("{")) {
					Object tempObj = mapper.readValue(jsonString, Object.class);
					return mapper.writeValueAsString(tempObj);
				} else {
					Object[] tempObj = mapper.readValue(jsonString, Object[].class);
					return mapper.writeValueAsString(tempObj);
				}
			}
			
			if (obj != null)
				return mapper.writeValueAsString(obj);
		} catch (IOException e) {
			System.err.println("Jackson library threw IOException during JSON serialization! Wrapping it in RuntimeException. Exception message: " + e.getMessage());
		}
		return null;
	}

	// -------------------------------------------------------------------------------------------------
	public static String loadJsonFromFile(String pathName) {
		StringBuilder sb;
		try {
			File file = new File(pathName);
			FileInputStream is = new FileInputStream(file);

			BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));
			sb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null)
				sb.append(line).append("\n");
			br.close();
		} catch (IOException e) {
			throw new RuntimeException(e.getClass().toString() + ": " + e.getMessage(), e);
		}

		return !sb.toString().isEmpty() ? sb.toString() : null;
	}

	// -------------------------------------------------------------------------------------------------
	public static TypeSafeProperties getTypeProperties(String fileName) {
		TypeSafeProperties properties = new TypeSafeProperties();
		try {
			properties.load(new FileInputStream(new File("config" + File.separator + fileName)));
		} catch (FileNotFoundException ex) {
			throw new ServiceConfigurationError(fileName + " file not found, make sure you have the correct working directory set! (directory where the config folder can be found)", ex);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return properties;
	}

	// -------------------------------------------------------------------------------------------------
	public static void checkProperties(Set<String> propertyNames, List<String> mandatoryProperties) {
		if (mandatoryProperties == null || mandatoryProperties.isEmpty())
			return;

		// Arrays.asList() returns immutable lists, so we have to copy it first
		List<String> properties = new ArrayList<>(mandatoryProperties);
		if (!propertyNames.containsAll(mandatoryProperties)) {
			properties.removeIf(propertyNames::contains);
			throw new ServiceConfigurationError("Missing field(s) from app.properties file: " + properties.toString());
		}
	}

	// -------------------------------------------------------------------------------------------------
	public static Type getType(String type) { // TODO: ADD MORE TYPES
		if (type.equalsIgnoreCase("String"))
			return String.class;
		else if (type.equalsIgnoreCase("Boolean"))
			return Boolean.class;
		else if (type.equalsIgnoreCase("Integer"))
			return int.class;
		else if (type.equalsIgnoreCase("Byte"))
			return Byte.class;
		else if (type.equalsIgnoreCase("Double"))
			return double.class;
		else if (type.equalsIgnoreCase("Float"))
			return float.class;
		else if (type.equalsIgnoreCase("Short"))
			return short.class;
		else if (type.equalsIgnoreCase("Long"))
			return long.class;
		else if (type.equalsIgnoreCase("Single"))
			return Object.class;
		else if (type.startsWith("List"))
			return List.class;
		else
			return Object.class;
	}

	// -------------------------------------------------------------------------------------------------
	public static TypeName getComplexType(String name, String type) { // TODO: ADD MORE COMPLEX TYPES
		String Name = name.substring(0, 1).toUpperCase() + name.substring(1, name.length());

		if (type.equalsIgnoreCase("List"))
			return ParameterizedTypeName.get(ClassName.get(List.class), TypeVariableName.get(name));
		else if (type.equalsIgnoreCase("single"))
			return TypeVariableName.get(Name);
		else
			return TypeVariableName.get(Name);
	}

	// -------------------------------------------------------------------------------------------------
	public static void readList(ArrayList<String[]> list) {
		for (int i = 0; i < list.size(); i++) {
			String[] element = list.get(i);
			for (int j = 0; j < element.length; j++) {
				System.out.println(i + "." + j + " :" + list.get(i)[j]);
			}
		}
	}
	
	
	// =================================================================================================
	// assistant methods
	
	// -------------------------------------------------------------------------------------------------
	private ServiceUtils() throws AssertionError {
		throw new AssertionError("Arrowhead Common: Utility is a non-instantiable class");
	}
}


@Provider
@Produces(MediaType.APPLICATION_JSON_VALUE)
class JacksonJsonProviderAtRest extends JacksonJaxbJsonProvider {

	// =================================================================================================
	// members
	private static final ObjectMapper mapper = new ObjectMapper();

	// Customize the properties of the JSON serializer/deserializer
	static {
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		mapper.registerModule(new JavaTimeModule());
		mapper.setDefaultPropertyInclusion(JsonInclude.Value.construct(Include.ALWAYS, Include.NON_NULL));
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
	}

	// =================================================================================================
	// assistant methods
	
	// -------------------------------------------------------------------------------------------------
	public static ObjectMapper getMapper() {
		return mapper;
	}

}