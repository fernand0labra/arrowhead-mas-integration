package eu.arrowhead.core.service.utils;

import static java.lang.System.out;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import eu.arrowhead.core.entity.InterfaceMetadata;
import eu.arrowhead.core.entity.Parameters;
import eu.arrowhead.core.entity.PayloadElements;

public class CDLUtils {

	// =================================================================================================
	// members
	
	private static ArrayList<PayloadElements> elements_request = new ArrayList<PayloadElements>();
	private static  ArrayList<PayloadElements> elements_response = new ArrayList<PayloadElements>();

	private static  ArrayList<String[]> payload_request = new ArrayList<String[]>();
	private static  ArrayList<String[]> payload_response = new ArrayList<String[]>();
	private static  ArrayList<String[]> metadata_request = new ArrayList<String[]>();
	private static  ArrayList<String[]> metadata_response = new ArrayList<String[]>();

	private static  boolean request;
	private static  boolean response;
	private static  boolean param;

	private static  ArrayList<Parameters> parameters = new ArrayList<>();
	private static  ArrayList<String> subpaths = new ArrayList<>();
	
	// =================================================================================================
	// methods
	
	// -------------------------------------------------------------------------------------------------
	public static InterfaceMetadata parseFile(String service, String system) throws GenerationException {
		String ID = "", sec = "", protocol = "", path = "", method = "", 
			mediatype_request = "", mediatype_response = "", 
			complexType_response = "", complexType_request = "";

		reset();

		Map<String, String> serviceContracts = new HashMap<>();
		
		serviceContracts.put("temperature-sensor-2", "src/main/resources/serviceContracts/cdl_provider2.xml");
		serviceContracts.put("temperature-controller-2", "src/main/resources/serviceContracts/cdl_consumer.xml");

		String fileName = serviceContracts.get(system);

		try {

			File file = new File(fileName);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(file);

			boolean serviceFound = false;
			int interfaceNumber = 0;
			int methodNumber = 0;
			
			NodeList interfaces = doc.getElementsByTagName("interface"), methods;
			Node interfaceItem, nMethod;
			Element element, eMethod;
			
			for (int interfaceIndex = 0; interfaceIndex < interfaces.getLength(); interfaceIndex++) {
				
				interfaceItem = interfaces.item(interfaceIndex);
				element = (Element) interfaceItem;
				methods = element.getElementsByTagName("method");
				
				for (int methodIndex = 0; methodIndex < methods.getLength(); methodIndex++) {
					
					nMethod = methods.item(methodIndex);
					eMethod = (Element) nMethod;
					
					if (eMethod.getAttribute("id").equalsIgnoreCase(service)) { // If the interface contains the service that we want to consume
						serviceFound = true;
						interfaceNumber = interfaceIndex;
						methodNumber = methodIndex;
						
						interfaceIndex = interfaces.getLength();
						methodIndex = methods.getLength();
					}
				}
			}

			// TODO what happens if we have two interfaces for the same service, the service name is different?
			if (serviceFound == true) { // If the interface analyzed is the one that has the service. 
				interfaceItem = interfaces.item(interfaceNumber);
				
				if (interfaceItem.getNodeType() == Node.ELEMENT_NODE) {
					element = (Element) interfaceItem;
					methods = element.getElementsByTagName("method");

					// Protocol
					protocol = element.getAttribute("protocol");

					// Method 
					nMethod = methods.item(methodNumber);
					if (nMethod.getNodeType() == Node.ELEMENT_NODE) {
						eMethod = (Element) nMethod;
						ID = eMethod.getAttribute("id");
						method = eMethod.getAttribute("name");
						path = ((Element) 
							((Element) eMethod.getElementsByTagName("path").item(0))
							.getElementsByTagName("option").item(0)).getAttribute("value"); //

						// Query Parameters
						NodeList nlParam = eMethod.getElementsByTagName("param");
						if (nlParam.getLength() == 0) { // No parameters defined
							param = false;

							/* ********************************************************************************************** */
							/* 											REQUEST												  */
							/* ********************************************************************************************** */

							NodeList nlReq = eMethod.getElementsByTagName("request");

							if (nlReq.getLength() == 0)
								request = false;
							else {
								request = true;

								System.out.println("insidenlLength"); //
								
								Node nReq = nlReq.item(0);
								if (nReq.getNodeType() == Node.ELEMENT_NODE) {
									System.out.println("insideElementNode"); //
									Element eReq = (Element) nReq;

									//Encoding
									Node nEncode = eReq.getElementsByTagName("encode").item(0);
									Element eEncode = (Element) nEncode;

									mediatype_request = eEncode.getAttribute("name");

									// Payload
									NodeList nlPayload = eReq.getElementsByTagName("payload");

									if (nlPayload.getLength() == 0)
										out.println("No payload Request defined");
									else {

										Node nPayload = nlPayload.item(0);
										Element ePayload = (Element) nPayload;
										NodeList nlComplex = ePayload.getElementsByTagName("complextype");
										
										// if(!complex1.equals(null)){}
										Node nComplex = nlComplex.item(0);
										Element eComplex = (Element) nComplex;
										complexType_request = eComplex.getAttribute("type");

										Node childNode = ePayload.getFirstChild();

										while (childNode.getNextSibling() != null) {
											childNode = childNode.getNextSibling();
											
											if (childNode.getNodeType() == Node.ELEMENT_NODE) {
												Element childElement = (Element) childNode;
												String[] childElementAttributes = new String[2]; // [0] attributeName [1] attributeType
												String tagname = childElement.getTagName();
												String[] metadata;
												
												if ("complexelement".equals(tagname))
													parseComplexElement("REQUEST", childElement, "Newclass");
												else {
													childElementAttributes[0] = childElement.getAttribute("name");
													childElementAttributes[1] = childElement.getAttribute("type");

													metadata = new String[4];
													metadata[2] = childElement.getAttribute("variation");
													metadata[3] = childElement.getAttribute("unit");
													metadata[0] = childElementAttributes[0];
													metadata[1] = childElementAttributes[1];
													
													if (!childElementAttributes[1].equals("null")) {
														payload_request.add(childElementAttributes);
														metadata_request.add(metadata);
													}
												}
											}
										}

										PayloadElements payloadRequest = new PayloadElements(payload_request, metadata_request);
										elements_request.add(payloadRequest);
									}
								}
							}
							
							// CodgenUtil.readList(metadata_request);

							/* ********************************************************************************************** */
							/* 											RESPONSE											  */
							/* ********************************************************************************************** */

							NodeList nlRes = eMethod.getElementsByTagName("response");

							if (nlRes.getLength() == 0)
								response = false;
							else {
								response = true;

								Node nRes = nlRes.item(0);
								if (nRes.getNodeType() == Node.ELEMENT_NODE) {
									Element eRes = (Element) nRes;

									// Encoding
									Node nEncode = eRes.getElementsByTagName("encode").item(0);
									Element eEncode = (Element) nEncode;

									mediatype_response = eEncode.getAttribute("name");

									// Payload
									NodeList nlPayload = eRes.getElementsByTagName("payload");

									if (nlPayload.getLength() == 0) {
										out.println("No payload Response defined");

									} else {

										Node nPayload = nlPayload.item(0);
										Element ePayload = (Element) nPayload;
										NodeList nlComplex = ePayload.getElementsByTagName("complextype");

										Node nComplex = nlComplex.item(0);
										Element eComplex = (Element) nComplex;
										complexType_response = eComplex.getAttribute("type");

										Node childNode = ePayload.getFirstChild();

										while (childNode.getNextSibling() != null) {
											childNode = childNode.getNextSibling();
											
											if (childNode.getNodeType() == Node.ELEMENT_NODE) {
												Element childElement = (Element) childNode;
												String[] childElementAttributes;

												String tagname = childElement.getTagName();

												if ("complexelement".equals(tagname))
													parseComplexElement("RESPONSE", childElement, "Newclass");
												else {
													childElementAttributes = new String[2];
													childElementAttributes[0] = childElement.getAttribute("name");
													childElementAttributes[1] = childElement.getAttribute("type");
													
													String[] metadata;
													metadata = new String[4];
													metadata[2] = childElement.getAttribute("variation");
													metadata[3] = childElement.getAttribute("unit");
													metadata[0] = childElementAttributes[0];
													metadata[1] = childElementAttributes[1];
													
													if (!childElementAttributes[1].equals("null")) {
														payload_response.add(childElementAttributes);
														metadata_response.add(metadata);
													}
												}
											}
										}
										
										PayloadElements payloadResponse = new PayloadElements(payload_response, metadata_response);
										elements_response.add(payloadResponse);
									}
								}
							}
						}

						else { // Parameters defined
							param = true;

							for (int p = 0; p < nlParam.getLength(); p++) {
								Node nParam = nlParam.item(p);
								
								if (nParam.getNodeType() == Node.ELEMENT_NODE) {
									Element eParam = (Element) nParam;
									String name = eParam.getAttribute("name");
									String type = eParam.getAttribute("type");
									String style = eParam.getAttribute("style");
									String required = eParam.getAttribute("required");

									Parameters parameter = new Parameters(name, type, style, required);
									parameters.add(parameter);

								}
							}

							/* ********************************************************************************************** */
							/* 											REQUEST												  */
							/* ********************************************************************************************** */

							NodeList nlReq = eMethod.getElementsByTagName("request");

							if (nlReq.getLength() == 0) {
								request = false;
							} else {
								request = true;

								Node nReq = nlReq.item(0);
								if (nReq.getNodeType() == Node.ELEMENT_NODE) {
									Element eReq = (Element) nReq;

									//Encoding
									Node nEncode = ((Element)
										((Element) eReq.getElementsByTagName("format").item(0))
											.getElementsByTagName("encode").item(0))
											.getElementsByTagName("option").item(0); //
									
									Element eEncode = (Element) nEncode;

									mediatype_request = eEncode.getAttribute("name");

									// Payload
									NodeList nlPayload = eReq.getElementsByTagName("payload");
									
									if (nlPayload.getLength() == 0)
										out.println("No payload Request defined");
									else {
										Node nPayload = nlPayload.item(0);
										Element ePayload = (Element) nPayload;

										// Payload with options
//										NodeList options = ePayload.getElementsByTagName("option");

//										for (int optionsIndex = 0; optionsIndex < options.getLength(); optionsIndex++) {

											ArrayList<String[]> arrayPayload = new ArrayList<String[]>();
											// payload_request.clear();

//											Node nOption = options.item(optionsIndex);
//											Element eOption = (Element) nOption;
//											String subpath = eOption.getAttribute("value");
//											subpaths.add(subpath);

											NodeList nlComplex = ePayload.getElementsByTagName("complextype"); //
											
											Node nComplex = nlComplex.item(0);
											Element eComplex = (Element) nComplex;
											complexType_request = eComplex.getAttribute("type");

											Node childNode = ePayload.getFirstChild();

											while (childNode.getNextSibling() != null) {

												childNode = childNode.getNextSibling();
												if (childNode.getNodeType() == Node.ELEMENT_NODE) {

													Element childElement = (Element) childNode;
													String[] childElementAttributes;
													String[] metadata;
													String tagname = childElement.getTagName();

													if ("complexelement".equals(tagname))
														arrayPayload = parseComplexElement("REQUEST", childElement, "Newclass");
													else {
														childElementAttributes = new String[2];
														childElementAttributes[0] = childElement.getAttribute("name");
														childElementAttributes[1] = childElement.getAttribute("type");

														metadata = new String[4];
														metadata[2] = childElement.getAttribute("variation");
														metadata[3] = childElement.getAttribute("unit");
														metadata[0] = childElementAttributes[0];
														metadata[1] = childElementAttributes[1];
														
														if (!childElementAttributes[1].equals("null")) {
															arrayPayload.add(childElementAttributes);
															metadata_request.add(metadata);
														}
													}
												}
											}

											PayloadElements payloadRequest = new PayloadElements(arrayPayload, metadata_request);
											elements_request.add(payloadRequest);
//										}
									}
								}
							}

							/* ********************************************************************************************** */
							/* 											RESPONSE											  */
							/* ********************************************************************************************** */
							
							NodeList nlRes = eMethod.getElementsByTagName("response");

							if (nlRes.getLength() == 0) {
								response = false;
							} else {
								response = true;

								Node nRes = nlRes.item(0);
								if (nRes.getNodeType() == Node.ELEMENT_NODE) {
									Element eRes = (Element) nRes;

									//Encoding
									Node nEncode = ((Element)
										((Element) eRes.getElementsByTagName("format").item(0))
											.getElementsByTagName("encode").item(0))
											.getElementsByTagName("option").item(0); //
									
									Element eEncode = (Element) nEncode;
									
									mediatype_response = eEncode.getAttribute("name");

									// Payload
									NodeList nlPayload = eRes.getElementsByTagName("payload");

									if (nlPayload.getLength() == 0)
										out.println("No payload Request defined");
									else {

										Node nPayload = nlPayload.item(0);
										Element ePayload = (Element) nPayload;

										// Payload with options
//										NodeList options = ePayload.getElementsByTagName("option");
//
//										for (int optionsIndex = 0; optionsIndex < options.getLength(); optionsIndex++) {
											ArrayList<String[]> arrayPayload = new ArrayList<>();

//											Node nOption = options.item(optionsIndex);
//											Element eOption = (Element) nOption;
//											String subpath = eOption.getAttribute("value");
//
//											if (!subpaths.contains(subpath))
//												subpaths.add(subpath);

											NodeList nlComplex = ePayload.getElementsByTagName("complextype");
											
											// if(!complex1.equals(null)){}
											Node nComplex = nlComplex.item(0);
											Element ecomplex2 = (Element) nComplex;
											complexType_response = ecomplex2.getAttribute("type");

											Node childNode = ePayload.getFirstChild();

											while (childNode.getNextSibling() != null) {
												childNode = childNode.getNextSibling();
												
												if (childNode.getNodeType() == Node.ELEMENT_NODE) {
													Element childElement = (Element) childNode;
													String[] childElementAttributes;
													String[] metadata;
													String tagname = childElement.getTagName();

													if ("complexelement".equals(tagname))
														arrayPayload = parseComplexElement("RESPONSE", childElement, "Newclass");
													else {
														childElementAttributes = new String[2];
														childElementAttributes[0] = childElement.getAttribute("name");
														childElementAttributes[1] = childElement.getAttribute("type");

														metadata = new String[4];
														metadata[2] = childElement.getAttribute("variation");
														metadata[3] = childElement.getAttribute("unit");
														metadata[0] = childElementAttributes[0];
														metadata[1] = childElementAttributes[1];
														
														if (!childElementAttributes[1].equals("null")) {
															arrayPayload.add(childElementAttributes);
															metadata_response.add(metadata);
														}
													}
												}
											}

											PayloadElements payloadResponse = new PayloadElements(arrayPayload, metadata_response);
											elements_response.add(payloadResponse);
//										}
									}
								}
							}
						}
					}
				}
			} else { // The service was not found
				out.println("ERROR: Service interface not found");
				throw new GenerationException(" SERVICE INTERFACE NOT FOUND ");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!response && request)
			mediatype_response = mediatype_request;
		else if (response && !request)
			mediatype_request = mediatype_response;

		// printElements(elements_request.get(0).getElements());
		// CodgenUtil.readList(metadata_request);
		
		return new InterfaceMetadata(protocol, path, method, mediatype_request, mediatype_response, ID,
				complexType_request, complexType_response, elements_request, elements_response, request, response,
				param, parameters, subpaths);
	}

	// -------------------------------------------------------------------------------------------------
	private static ArrayList<String[]> parseComplexElement(String messageType, Element element, String level) {
		ArrayList<String[]> complexPayload = new ArrayList<>();
		
		String[] c = new String[3];
		c[0] = level;
		c[1] = element.getAttribute("name");
		c[2] = element.getAttribute("type");
		
		if (messageType.equalsIgnoreCase("REQUEST")) {
			payload_request.add(c);
			metadata_request.add(c);
		} else {
			payload_response.add(c);
			metadata_response.add(c);
		}
		complexPayload.add(c);
		Node childElement = element.getFirstChild();

		while (childElement.getNextSibling() != null) {
			childElement = childElement.getNextSibling();
			
			if (childElement.getNodeType() == Node.ELEMENT_NODE) {
				Element childElementCopy = (Element) childElement;
				String tagname = childElementCopy.getTagName();

				if ("complexelement".equals(tagname)) {
					parseComplexElement(messageType, childElementCopy, "child:Newclass");
					childElement = childElement.getNextSibling();
					/*
					 * if(elechild!=null) while(elechild.getNodeType() != Node.ELEMENT_NODE) {
					 * elechild=elechild.getNextSibling(); }
					 */
				} else {
					String[] f = new String[3];
					f[0] = "child";
					f[1] = childElementCopy.getAttribute("name");
					f[2] = childElementCopy.getAttribute("type");

					String[] metadata = new String[4];
					metadata[2] = childElementCopy.getAttribute("variation");
					metadata[3] = childElementCopy.getAttribute("unit");
					metadata[0] = f[1];
					metadata[1] = f[2];
					
					if (messageType.equalsIgnoreCase("REQUEST")) {
						payload_request.add(f);
						metadata_request.add(metadata);
					} else {
						payload_response.add(f);
						metadata_response.add(metadata);
					}

					complexPayload.add(f);
				}
			}
		}
		
		// elements_response.add(c);

		String[] stopClass = new String[2];
		stopClass[0] = "StopClass";

		if (messageType.equalsIgnoreCase("REQUEST")) {
			payload_request.add(stopClass);
			metadata_request.add(stopClass);
		} else {
			payload_response.add(stopClass);
			metadata_response.add(stopClass);
		}
		complexPayload.add(stopClass);

		/*
		 * if(r.equalsIgnoreCase("REQUEST")) return payload_request; else return
		 * payload_response;
		 */
		
		return complexPayload;
	}

	// =================================================================================================
	// assistant methods

	// -------------------------------------------------------------------------------------------------
	private static void printElements(ArrayList<String[]> elements) {
		for (int i = 0; i > elements.size(); i++) {
			String[] list = elements.get(i);
			for (int j = 0; j > list.length; j++) {
				out.println("   :" + list[j]);
			}
		}
	}
	
	// -------------------------------------------------------------------------------------------------
	private static void reset() {
		elements_request.clear();
		elements_response.clear();
		payload_request.clear();
		payload_response.clear();
		parameters.clear();
		subpaths.clear();
	}
}
