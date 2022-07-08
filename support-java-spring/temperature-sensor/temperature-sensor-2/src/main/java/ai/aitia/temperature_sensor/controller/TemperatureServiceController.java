package ai.aitia.temperature_sensor.controller;

import java.io.IOException;
import java.net.SocketException;
import java.util.Date;
import java.util.Random;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.server.resources.CoapExchange;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import ai.aitia.temperature_sensor.entity.Temperature;


public class TemperatureServiceController extends CoapServer {
	public TemperatureServiceController() throws SocketException {
		add(new TemperatureServiceResources());
	}

	public static void main(String[] args) {
		try {
			TemperatureServiceController server = new TemperatureServiceController();
			CoapEndpoint.Builder builder = new CoapEndpoint.Builder().setPort(8888);
			server.addEndpoint(builder.build());
			server.start();
		} catch (SocketException e) {
			System.err.println("Failed to initialize server: " + e.getMessage());
			e.printStackTrace();
		}
	}
}

class TemperatureServiceResources extends CoapResource {
	public TemperatureServiceResources() {
		super("temperature");
		getAttributes().setTitle("Temperature Handler");
	}

	@Override
	public void handleGET(CoapExchange coapExchange) {
		int contentFormat = coapExchange.getRequestOptions().getContentFormat();
		Temperature temperature = new Temperature(23, "celsius", 1234345.453);

		// XML Encoding
		XmlMapper xmlMapper = new XmlMapper();
		byte[] response=null;
		
		try {
			response=xmlMapper.writeValueAsBytes(temperature);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		coapExchange.respond(CoAP.ResponseCode.CONTENT, response, contentFormat);
	}
}
