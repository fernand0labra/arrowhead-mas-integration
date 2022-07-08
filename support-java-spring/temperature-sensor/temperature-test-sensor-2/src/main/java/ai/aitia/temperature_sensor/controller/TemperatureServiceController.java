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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import ai.aitia.temperature_common.dto.TemperatureResponseDTO;
import ai.aitia.temperature_sensor.TemperatureProviderConstants;
import ai.aitia.temperature_sensor.entity.DTOConverter;
import ai.aitia.temperature_sensor.entity.Temperature;

/* ********************************************************************************************************** */
/* 												HTTP PROVIDER												  */
/* ********************************************************************************************************** */

@RestController
@RequestMapping(TemperatureProviderConstants.TEMPERATURE_URI)
public class TemperatureServiceController {

	//=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody public TemperatureResponseDTO getTemperature(@RequestParam(name = TemperatureProviderConstants.REQUEST_PARAM_UNIT, required = true) final String unit){
		Temperature temperature = null;
		
		Date date = new Date();
		Random random = new Random();
		
		if(unit.equals("celsius")) {
			random.setSeed(0);
			temperature = new Temperature(random.nextInt(), "Celsius", date.getTime());	
		}
		else if(unit.equals("kelvin")) {
			random.setSeed(273);
			temperature = new Temperature(random.nextInt(), "Kelvin", date.getTime());
		}
		
		final TemperatureResponseDTO response = DTOConverter.convertTemperatureToTemperatureResponseDTO(temperature);			
		return response;
	}
}


