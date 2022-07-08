/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.arrowhead.core.entity;

import java.util.ArrayList;

/**
 *
 * @author cripan
 */
public class InterfaceMetadata {

	// =================================================================================================
	// members
	String protocol;
	String pathResource;
	String method;

	String mediatypeRequest;
	String mediatypeResponse;
	String id;

	String complextypeRequest;
	String complextypeResponse;

	ArrayList<PayloadElements> elementsRequest;
	ArrayList<PayloadElements> elementsResponse;

	boolean request;
	boolean response;
	boolean param;

	ArrayList<Parameters> parameters;
	ArrayList<String> subpaths;

	// =================================================================================================
	// assistant methods

	// -------------------------------------------------------------------------------------------------
	public InterfaceMetadata(String protocol, String pathResource, String method, String mediatypeRequest,
			String mediatypeResponse, String id, String complextypeRequest, String complextypeResponse,
			ArrayList<PayloadElements> elementsRequest, ArrayList<PayloadElements> elementsResponse, boolean request,
			boolean response, boolean param, ArrayList<Parameters> parameters, ArrayList<String> subpaths) {
		this.protocol = protocol;
		this.pathResource = pathResource;
		this.method = method;
		this.mediatypeRequest = mediatypeRequest;
		this.mediatypeResponse = mediatypeResponse;
		this.id = id;
		this.complextypeResponse = complextypeResponse;
		this.complextypeRequest = complextypeRequest;
		this.elementsRequest = elementsRequest;
		this.elementsResponse = elementsResponse;
		this.request = request;
		this.response = response;
		this.param = param;
		this.parameters = parameters;
		this.subpaths = subpaths;
	}

	// -------------------------------------------------------------------------------------------------
	public String getId() {	return id; }
	public String getProtocol() { return protocol; }
	public String getPathResource() { return pathResource; }
	public String getMethod() { return method; }
	public String getMediatypeRequest() { return mediatypeRequest; }
	public String getMediatypeResponse() { return mediatypeResponse; }
	public ArrayList<PayloadElements> getElementsRequest() { return elementsRequest; }
	public ArrayList<PayloadElements> getElementsResponse() { return elementsResponse; }
	public String getComplextypeResponse() { return complextypeResponse; }
	public String getComplextypeRequest() { return complextypeRequest; }
	public boolean getRequest() { return request; }
	public boolean getResponse() { return response; }
	public ArrayList<Parameters> getParameters() { return parameters; }
	public ArrayList<String> getSubpaths() { return subpaths; }
	
	// -------------------------------------------------------------------------------------------------
	public void setId(String id) { this.id = id; }
	public void setProtocol(String Protocol) { this.protocol = Protocol; }
	public void setPathResource(String PathResource) { this.pathResource = PathResource; }
	public void setMethod(String Method) { this.method = Method; }
	public void setMediatypeRequest(String Mediatype_request) { this.mediatypeRequest = Mediatype_request; }
	public void setMediatypeResponse(String Mediatype_response) { this.mediatypeResponse = Mediatype_response; }
	public void setElementsRequest(ArrayList<PayloadElements> elements_request) { this.elementsRequest = elements_request; }
	public void setElementsResponse(ArrayList<PayloadElements> elements_response) { this.elementsResponse = elements_response; }
	public void setComplextypeResponse(String complexType_response) { this.complextypeResponse = complexType_response; }
	public void setComplextypeRequest(String complexType_request) { this.complextypeRequest = complexType_request; }
	public void setRequest(boolean request) { this.request = request; }
	public void setResponse(boolean response) { this.response = response; }
	public void setParam(boolean param) { this.param = param; }
	public void setParameters(ArrayList<Parameters> parameters) { this.parameters = parameters; }
	public void setSubpaths(ArrayList<String> subpaths) { this.subpaths = subpaths; }
	
	// -------------------------------------------------------------------------------------------------
	public boolean isParam() { return param; }

	// -------------------------------------------------------------------------------------------------
	@Override
	public String toString() {
		return "InterfaceMetadata{" + "Protocol=" + protocol + ", PathResource=" + pathResource + ", Method=" + method
				+ ", Mediatype_request=" + mediatypeRequest + ", Mediatype_response=" + mediatypeResponse + ", ID="
				+ id + ", complexType_response=" + complextypeResponse + ", complexType_request=" + complextypeRequest
				+ ", elements_request=" + elementsRequest + ", elements_response=" + elementsResponse + ", request="
				+ request + ", response=" + response + ", param=" + param + ", parameters=" + parameters + ", subpaths="
				+ subpaths + '}';
	}

}
