package eu.arrowhead.core.service.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import javax.lang.model.element.Modifier;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import com.fasterxml.jackson.dataformat.csv.CsvFactory;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import eu.arrowhead.core.entity.InterfaceMetadata;
import generatedinterface.src.main.java.RequestDTO_C0;
import generatedinterface.src.main.java.RequestDTO_P0;
import generatedinterface.src.main.java.ResponseDTO_C0;
import generatedinterface.src.main.java.ResponseDTO_P0;

public class GenerationUtils {

	// =================================================================================================
	// members
	
	private static ArrayList<String> listOfDeclarations = new ArrayList<>();
	private static ArrayList<String> stringObjects = new ArrayList<String>();
	
	// =================================================================================================
	// methods
	
	// -------------------------------------------------------------------------------------------------	
	public static ArrayList<String> generateClass(ArrayList<String[]> elements, String className) {

		// ListDeclarations.clear();

		String formattedClassName = className.substring(0, 1).toUpperCase() + className.substring(1, className.length());

		MethodSpec constructorNoPayloadBuilder = buildConstructorNoPayload();
		MethodSpec constructorComplexPayloadBuilder = buildConstructorComplexPayload(elements, formattedClassName);
		MethodSpec toStringBuilder = buildToString(elements);

		AnnotationSpec jsonBuilder = AnnotationSpec
				.builder(JsonIgnoreProperties.class)
				.addMember("ignoreUnknown", "true")
				.build();

		TypeSpec.Builder typeBuilder = TypeSpec
				.classBuilder(formattedClassName)
				.addModifiers(Modifier.PUBLIC)
				.addAnnotation(jsonBuilder)
				.addMethod(constructorNoPayloadBuilder)
				.addMethod(constructorComplexPayloadBuilder)
				.addMethod(toStringBuilder);

		for (int i = 0; i < elements.size(); i++) {
			String name = elements.get(i)[0];
			String type = elements.get(i)[1];

			if (name.equals("Newclass")) {
				name = elements.get(i)[1];
				type = elements.get(i)[2];

				MethodSpec getBuilder = buildGet(name, type);
				MethodSpec setBuilder = buildSet(name, type);

				if (type.equalsIgnoreCase("single") || type.startsWith("List"))
					typeBuilder
						.addField(ServiceUtils.getComplexType(name, type), name, Modifier.PRIVATE)
						.addMethod(getBuilder)
						.addMethod(setBuilder);
				else
					typeBuilder
						.addField(ServiceUtils.getType(type), name, Modifier.PRIVATE)
						.addMethod(getBuilder)
						.addMethod(setBuilder);
			} 
			
			else if (name.equals("child") || name.equals("child:Newclass") || name.equals("StopClass")) { } 
			
			else
				typeBuilder
					.addField(ServiceUtils.getType(type), name, Modifier.PRIVATE)
					.addMethod(buildGet(name, type))
					.addMethod(buildSet(name, type));
		}

		TypeSpec generatedClass = typeBuilder.build();

		String packageName = "";
		
		JavaFile javaFile = JavaFile.builder(packageName, generatedClass).addFileComment("Auto generated").build();
		try {
			javaFile.writeTo(Paths.get("src/main/java/generatedinterface/src/main/java"));
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return listOfDeclarations;

	}

	// -------------------------------------------------------------------------------------------------		
	public static void generateProviderInterpreter(InterfaceMetadata metadata) {
		final ClassLoader oldContextClassLoader = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(GenerationUtils.class.getClassLoader());
	
		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.init();
	
		try {
			if (metadata.getProtocol().equalsIgnoreCase("COAP")) {
				VelocityContext context = new VelocityContext();
				context.put("method", metadata.getMethod());
				context.put("encoding", metadata.getMediatypeResponse());
	
				Writer writer = new FileWriter(new File("src/main/java/generatedinterface/src/main/java/ProviderInterpreter.java"));
				
				velocityEngine.getTemplate("src/main/resources/templates/consumeServiceCoap.vm").merge(context, writer);
				
				writer.flush();
				writer.close();
			} else { // HTTP
				VelocityContext context = new VelocityContext();
				context.put("method", metadata.getMethod());
				context.put("encoding", metadata.getMediatypeResponse());
	
				Writer writer = new FileWriter(new File("src/main/java/generatedinterface/src/main/java/ProviderInterpreter.java"));
				
				velocityEngine.getTemplate("src/main/resources/templates/consumeServiceHttp.vm").merge(context, writer);
				
				writer.flush();
				writer.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		// Set back default class loader
		Thread.currentThread().setContextClassLoader(oldContextClassLoader);
	}

	// -------------------------------------------------------------------------------------------------	
	public static HashMap<String, String> generateServer(String protocol) {
		final ClassLoader oldContextClassLoader = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(GenerationUtils.class.getClassLoader());

		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.init();

		HashMap<String, String> metadataEndpoint = new HashMap<String, String>();
		
		try {
			if (protocol.equalsIgnoreCase("COAP")) {
				VelocityContext context = new VelocityContext();
				context.put("port", "64130");
				
				metadataEndpoint.put("protocol", "COAP");
				metadataEndpoint.put("port", "64130");
				metadataEndpoint.put("address", "192.168.1.77");
				
				Writer writer = new FileWriter(new File( "src/main/java/generatedinterface/src/main/java/ServerApplication.java"));
				
				velocityEngine.getTemplate("src/main/resources/templates/serverCoap.vm").merge(context, writer);
				
				writer.flush();
				writer.close();
			} else { // HTTP
				VelocityContext context = new VelocityContext();
				context.put("port", "8466");

				metadataEndpoint.put("protocol", "HTTP");
				metadataEndpoint.put("port", "8466");
				metadataEndpoint.put("address", "192.168.1.77");
				
				Writer writer = new FileWriter(new File( "src/main/java/generatedinterface/src/main/java/ServerApplication.java"));
				
				velocityEngine.getTemplate("src/main/resources/templates/serverHttp.vm").merge(context, writer);
				
				writer.flush();
				writer.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		// Set back default class loader
		Thread.currentThread().setContextClassLoader(oldContextClassLoader);
		
		return metadataEndpoint;
	}

	// -------------------------------------------------------------------------------------------------	
	public static void generateResources(InterfaceMetadata consumerMetadata, InterfaceMetadata providerMetadata, String systemURL) {
		final ClassLoader oldContextClassLoader = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(GenerationUtils.class.getClassLoader());
		
		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.init();
		
		try {
			Template template = consumerMetadata.getProtocol().equalsIgnoreCase("COAP") 
					? velocityEngine.getTemplate("src/main/resources/templates/RESTResourceCoap.vm")
					: velocityEngine.getTemplate("src/main/resources/templates/RESTResourceHttp.vm");

			VelocityContext context = new VelocityContext();
			context.put("id", consumerMetadata.getId());
			context.put("url", systemURL + providerMetadata.getPathResource()); // TODO GET THE ADDRESS
			context.put("path", consumerMetadata.getPathResource());
			context.put("method", consumerMetadata.getMethod());
			context.put("encoding_consumer", consumerMetadata.getMediatypeResponse());
			context.put("encoding_provider", providerMetadata.getMediatypeResponse());

			Writer writer = new FileWriter(new File("src/main/java/generatedinterface/src/main/java/RESTResources.java"));
			
			template.merge(context, writer);
			
			writer.flush();
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		// Set back default class loader
		Thread.currentThread().setContextClassLoader(oldContextClassLoader);

	}
	
	// -------------------------------------------------------------------------------------------------
	public static void generatePayloadTranslator(InterfaceMetadata consumerMetadata, InterfaceMetadata providerMetadata) throws GenerationException, ClassNotFoundException {
		MethodSpec constructor = buildConstructorNoPayload();
		MethodSpec requestAdaptor = null;
		MethodSpec responseAdaptor = null;

		if (providerMetadata.getResponse() && consumerMetadata.getResponse())
			responseAdaptor = buildResponseAdaptor(consumerMetadata, providerMetadata);

		if (consumerMetadata.getRequest() && providerMetadata.getRequest())
			requestAdaptor = buildRequestAdaptor(consumerMetadata, providerMetadata);

		TypeSpec.Builder builder = TypeSpec
			.classBuilder("PayloadTranslator")
			.addModifiers(Modifier.PUBLIC)
			.addMethod(constructor);

		if (!consumerMetadata.getResponse() && !providerMetadata.getResponse())
			builder.addMethod(requestAdaptor);

		else if (!consumerMetadata.getRequest() && !providerMetadata.getRequest())
			builder.addMethod(responseAdaptor);

		else
			builder
				.addMethod(requestAdaptor)
				.addMethod(responseAdaptor);

		TypeSpec generatedClass = builder.build();

		JavaFile javaFile = JavaFile
				.builder("", generatedClass)
				.addFileComment("Auto generated")
				.build();
		
		try {
			javaFile.writeTo(Paths.get("src/main/java/generatedinterface/src/main/java"));
			
			/* ************************************************************************************ */
			
			File tempFile = new File("PayloadTranslator.java");
			
			BufferedReader reader = new BufferedReader(new FileReader("src/main/java/generatedinterface/src/main/java/PayloadTranslator.java"));
			BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

			String lineToRemove = "generatedinterface";
			String currentLine;
			
			while((currentLine = reader.readLine()) != null) {
			    String trimmedLine = currentLine.trim();
			    if(trimmedLine.contains(lineToRemove)) continue;
			    writer.write(currentLine + System.getProperty("line.separator"));
			}
			
			writer.close(); 
			reader.close();
			
			new File("src/main/java/generatedinterface/src/main/java/PayloadTranslator.java").delete();
			tempFile.renameTo(new File("src/main/java/generatedinterface/src/main/java/PayloadTranslator.java"));
			
			/* ************************************************************************************ */
			
		} catch (IOException ex) {
			System.err.print("Exception:" + ex.getMessage());
			ex.printStackTrace();
		}
	}
		
	/**
	 * Creates a Jackson object mapper based on a media type. It supports JSON, JSON
	 * Smile, XML, YAML and CSV.
	 * 
	 * @return The Jackson object mapper.
	 */
	// -------------------------------------------------------------------------------------------------	
	public static CodeBlock createObjectMapper(String MediaType, String MapperName) {

		CodeBlock.Builder builder = CodeBlock.builder();

		if (MediaType.equalsIgnoreCase("JSON"))
			builder
				.addStatement("$T jsonFactory_$L = new JsonFactory()", JsonFactory.class, MapperName)
				.addStatement("$T $L=new ObjectMapper(jsonFactory_$L)", ObjectMapper.class, MapperName, MapperName);

		else if (MediaType.equalsIgnoreCase("JSON_SMILE"))
			builder
				.addStatement("$T smileFactory = new SmileFactory()", SmileFactory.class)
				.addStatement("ObjectMapper $L=new ObjectMapper(smileFactory)", ObjectMapper.class, MapperName);

		else if (MediaType.equalsIgnoreCase("CBOR"))
			builder
				.addStatement("$T cborFactory = new CBORFactory()", CBORFactory.class)
				.addStatement("$T $L=new ObjectMapper(cborFactory)", ObjectMapper.class, MapperName);

		else if (MediaType.equalsIgnoreCase("XML")) // TODO: CHECK IF I NEED THE XMLFACTORY OR NOT
			builder.addStatement("$T $L=new $T()", ObjectMapper.class, MapperName, XmlMapper.class);

		else if (MediaType.equalsIgnoreCase("YAML"))
			builder
				.addStatement(" $T yamlFactory = new YAMLFactory()", YAMLFactory.class)
				.addStatement("$T $L=new ObjectMapper(yamlFactory)", ObjectMapper.class, MapperName);

		else if (MediaType.equalsIgnoreCase("CSV"))
			builder
				.addStatement("$T csvFactory = new CsvFactory()", CsvFactory.class)
				.addStatement("$T $L=new $T(csvFactory))", ObjectMapper.class, CsvMapper.class, MapperName);

		else
			builder.addStatement("$T $L=new ObjectMapper()", ObjectMapper.class, MapperName);

		return builder.build();
		
		//javax.xml.stream.XMLInputFactory xif = XmlFactoryProvider.newInputFactory();
		//javax.xml.stream.XMLOutputFactory xof = XmlFactoryProvider.newOutputFactory();
		//
		//xif.setProperty(javax.xml.stream.XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, isExpandingEntityRefs());
		//xif.setProperty(javax.xml.stream.XMLInputFactory.SUPPORT_DTD, isExpandingEntityRefs());
		//xif.setProperty(javax.xml.stream.XMLInputFactory.IS_VALIDATING, isValidatingDtd());
		//
		//XmlFactory xmlFactory = new XmlFactory(xif, xof);
		//xmlFactory.configure(Feature.AUTO_CLOSE_TARGET, false);
		//result = new XmlMapper(xmlFactory);
	}

	// =================================================================================================
	// assistant methods
	
	// -------------------------------------------------------------------------------------------------	
	public GenerationUtils(ArrayList<String> listOfDeclarations) {
		GenerationUtils.listOfDeclarations = listOfDeclarations;
	}

	// -------------------------------------------------------------------------------------------------
	public ArrayList<String> getListOfDeclarations() { return listOfDeclarations; }

	// -------------------------------------------------------------------------------------------------
	public void setListOfDeclarations(ArrayList<String> ListofDeclarations) { GenerationUtils.listOfDeclarations = ListofDeclarations; }
	
	// -------------------------------------------------------------------------------------------------	
	private static ArrayList<String> getStringObjects(ArrayList<String[]> elements, String parentClass) {

		for (int i = 0; i < elements.size(); i++) {
			String e = elements.get(i)[0];

			if (e != null)
				if (e.equals("Newclass")) {
					if (("StopClass".equals(elements.get(i + 1)[0])))
						i = elements.size() + 1;
					else
						i = parseComplexElements(elements, i, parentClass);
				}
		}
		
		return stringObjects;
	}
	
	// -------------------------------------------------------------------------------------------------	
	private static int parseComplexElements(ArrayList<String[]> elements, int i, String parentClass) {
		int j = i + 1;
		boolean out = false;
		ArrayList<String[]> newClass = new ArrayList<String[]>();

		String className = elements.get(i)[1];
		String[] elementAttributes = new String[2];
		
		while (out == false) {
			if ("StopClass".equals(elements.get(j)[0]))
				out = true;
			else {

				if ("child:Newclass".equals(elements.get(j)[0])) {
					int current_j = parseComplexElements(elements, j, parentClass);
					
					if ("single".equals(elements.get(j)[2])) {
						elementAttributes[0] = elements.get(j)[1];
						elementAttributes[1] = elements.get(j)[1];
						newClass.add(elementAttributes);
					} else if ("list".equals(elements.get(j)[2])) {
						elementAttributes[0] = elements.get(j)[1];
						elementAttributes[1] = "List";
						newClass.add(elementAttributes);
					}

					if ("StopClass".equals(elements.get(current_j)[0]))
						out = true;
					else
						j++;

				} else {
					String value = elements.get(j)[0];
					while ("child".equals(value)) {
						elementAttributes[0] = elements.get(j)[1];
						elementAttributes[1] = elements.get(j)[2];
						newClass.add(elementAttributes);

						j++;
						value = elements.get(j)[0];
					}
				}
			}
		}

		String stringObject = buildStringObject(className, newClass);
		stringObjects.add(stringObject);
		generateNestedClass(newClass, className, parentClass);
		
		return j;
	}
	
	// -------------------------------------------------------------------------------------------------	
	private static void generateNestedClass(ArrayList<String[]> elements, String className, String parentClass) {
		String formatedClassName = className.substring(0, 1).toUpperCase() + className.substring(1, className.length());

		MethodSpec constructorNoPayloadBuilder = buildConstructorNoPayload();
		MethodSpec constructorSimplePayloadBuilder = buildConstructorSimplePayload(elements, formatedClassName);
		MethodSpec toStringBuilder = buildToString(elements);

		TypeSpec.Builder typeBuilder = TypeSpec
				.classBuilder(formatedClassName)
				.addModifiers(Modifier.PUBLIC)
				.addMethod(constructorNoPayloadBuilder)
				.addMethod(constructorSimplePayloadBuilder)
				.addMethod(toStringBuilder);

		for (int i = 0; i < elements.size(); i++) {
			String name = elements.get(i)[0];
			String type = elements.get(i)[1];
			
			MethodSpec getBuilder = buildGet(name, type);
			MethodSpec setBuilder = buildSet(name, type);
			
			if (type.equalsIgnoreCase("single") || type.startsWith("List"))
				typeBuilder
					.addField(ServiceUtils.getComplexType(name, type), name, Modifier.PRIVATE)
					.addMethod(getBuilder)
					.addMethod(setBuilder);
			else
				typeBuilder
					.addField(ServiceUtils.getType(type), name, Modifier.PRIVATE)
					.addMethod(getBuilder)
					.addMethod(setBuilder);
		}

		TypeSpec generatedClass = typeBuilder.build();

		String packageName = "eu.generator.resources"; // TODO Change path

		if (parentClass.contains("_P"))
			packageName = "eu.generator.provider";
		else if (parentClass.contains("_C"))
			packageName = "eu.generator.consumer";
		
		JavaFile javaFile = JavaFile.builder(packageName, generatedClass).addFileComment("Auto generated").build();
		try {
			javaFile.writeTo(Paths.get( // TODO Change path
					"C:\\Users\\cripan\\Desktop\\Code_generation\\InterfaceTranslatorSystem\\GenInterface\\src\\main\\java"));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	// -------------------------------------------------------------------------------------------------	
	private static String buildStringObject(String name, ArrayList<String[]> var) {
		int index = 0;
		boolean listFlag = false;
		String s = "" + name + " OBJ" + name + " = new " + name + "( ";

		if (var.size() > 1) {
			for (int i = 0; i < var.size(); i++) {

				if (var.get(i)[1].equalsIgnoreCase("String"))
					s = s + " \"" + var.get(i)[0] + "\"";
				else if (var.get(i)[1].equalsIgnoreCase("Double") || var.get(i)[1].equalsIgnoreCase("Float"))
					s = s + "" + 0.0 + "";
				else if (var.get(i)[1].equalsIgnoreCase("Integer") || var.get(i)[1].equalsIgnoreCase("Short")	|| var.get(i)[1].equalsIgnoreCase("Long"))
					s += "" + 0 + "";
				else if (var.get(i)[1].equalsIgnoreCase("Boolean"))
					s += "" + true + "";
				else if (var.get(i)[1].startsWith("List")) {
					s += "ListObject";
					listFlag = true;
					index = i;
				} else
					s += "OBJ" + var.get(i)[0] + "";

				if ((i + 1) < var.size())
					s += " , ";
			}
		}
		
		s += ")";

		if (listFlag)
			s = "List<" + var.get(index)[0] + "> ListObject=new ArrayList<>(); \n ListObject.add(OBJ" + var.get(index)[0] + "); \n" + s;

		return s;
	}
	
	// -------------------------------------------------------------------------------------------------
	private static MethodSpec buildGet(String name, String type) {
		MethodSpec.Builder builder = MethodSpec
				.methodBuilder("get" + name)
				.addModifiers(Modifier.PUBLIC);

		if (type.equalsIgnoreCase("single") || type.startsWith("List"))
			builder
				.returns(ServiceUtils.getComplexType(name, type))
				.addStatement("return " + name);
		else
			builder
				.returns(ServiceUtils.getType(type))
				.addStatement("return " + name);

		return builder.build();
	}
	
	// -------------------------------------------------------------------------------------------------
	private static MethodSpec buildSet(String name, String type) {

		MethodSpec.Builder builder = MethodSpec
				.methodBuilder("set" + name)
				.addModifiers(Modifier.PUBLIC);

		if (type.equalsIgnoreCase("single") || type.startsWith("List"))
			builder
				.addParameter(ServiceUtils.getComplexType(name, type), name)
				.addStatement("this." + name + "=" + name);
		else
			builder
				.addParameter(ServiceUtils.getType(type), name)
				.addStatement("this." + name + "=" + name);

		return builder.build();
	}
		
	// -------------------------------------------------------------------------------------------------
	private static MethodSpec buildToString(ArrayList<String[]> elements) {
		String s = "";

		for (int i = 0; i < elements.size(); i++) {
			String name = elements.get(i)[0];
			
			if (name.equals("Newclass"))
				s += "+ \"" + elements.get(i)[1] + "=\" + " + elements.get(i)[1];
			
			else if (name.equals("child") || name.equals("child:Newclass") || name.equals("StopClass")) { } 
			
			else
				s +=  "+ \"" + name + "=\" + " + name + "+ \",  \"";
		}

		MethodSpec builder = MethodSpec
				.methodBuilder("toString")
				.addModifiers(Modifier.PUBLIC)
				.addAnnotation(Override.class)
				.returns(String.class)
				.addStatement("return \"ProviderPayload{\" $L +\"}\"", s)
				.build();

		return builder;
	}
	
	// -------------------------------------------------------------------------------------------------
	private static MethodSpec buildConstructorNoPayload() {
		return MethodSpec
				.constructorBuilder()
				.addModifiers(Modifier.PUBLIC)
				.build();
	}
					
	// -------------------------------------------------------------------------------------------------	
	private static MethodSpec buildConstructorSimplePayload(ArrayList<String[]> elements, String className) {
		MethodSpec.Builder builder = MethodSpec
				.constructorBuilder()
				.addModifiers(Modifier.PUBLIC);

		for (int i = 0; i < elements.size(); i++) {
			String name = elements.get(i)[0];
			String type = elements.get(i)[1];

			if (type.equalsIgnoreCase("single") || type.startsWith("List"))
				builder
					.addParameter(ServiceUtils.getComplexType(name, type), name)
					.addStatement("this.$N = $N", name, name);
			else
				builder
					.addParameter(ServiceUtils.getType(type), name)
					.addStatement("this.$N = $N", name, name);
		}
		
		return builder.build();
	}
	
	// -------------------------------------------------------------------------------------------------	
	private static MethodSpec buildConstructorComplexPayload(ArrayList<String[]> elements, String className) {

		listOfDeclarations = getStringObjects(elements, className);
		ArrayList<String[]> var = new ArrayList<>();

		String[] elementAttributes = new String[2];
		MethodSpec.Builder builder = MethodSpec
				.constructorBuilder()
				.addModifiers(Modifier.PUBLIC);

		for (int i = 0; i < elements.size(); i++) {
			String name = elements.get(i)[0];
			String type = elements.get(i)[1];

			if (name.equals("Newclass")) {
				name = elements.get(i)[1];
				type = elements.get(i)[2];
				
				elementAttributes[0] = name;
				elementAttributes[1] = type;
				
				var.add(elementAttributes);

				if (type.equalsIgnoreCase("single") || type.startsWith("List"))
					builder
						.addParameter(ServiceUtils.getComplexType(name, type), name)
						.addStatement("this.$N = $N", name, name);
				else 
					builder
						.addParameter(ServiceUtils.getType(type), name)
						.addStatement("this.$N = $N", name, name);
			} 
			
			else if (name.equals("child") || name.equals("child:Newclass") || name.equals("StopClass")) { } 
			
			else {
				elementAttributes = new String[2];
				elementAttributes[0] = name;
				elementAttributes[1] = type;
				
				var.add(elementAttributes);
				
				builder
					.addParameter(ServiceUtils.getType(type), name)
					.addStatement("this.$N = $N", name, name);
			}
		}

		String stringPayload = buildStringObject(className, var);
		listOfDeclarations.add(stringPayload);

		return builder.build();
	}

	// -------------------------------------------------------------------------------------------------
	private static MethodSpec buildRequestAdaptor(InterfaceMetadata consumerMetadata, InterfaceMetadata providerMetadata) throws ClassNotFoundException {

		MethodSpec.Builder builder = MethodSpec
				.methodBuilder("requestAdaptor")
				.addModifiers(Modifier.PUBLIC)
				.returns(RequestDTO_P0.class)
				.addParameter(RequestDTO_C0.class, "payload_C")
				.addStatement(" $T payload_P = new RequestDTO_P0()", RequestDTO_P0.class);

		Boolean match = false;
		ArrayList<String[]> consumerRequestElements = consumerMetadata.getElementsRequest().get(0).getElements();
		ArrayList<String[]> providerRequestElements = providerMetadata.getElementsRequest().get(0).getElements();
		ArrayList<String[]> consumerRequestMetadata = consumerMetadata.getElementsRequest().get(0).getMetadata();
		ArrayList<String[]> providerRequestMetadata = providerMetadata.getElementsRequest().get(0).getMetadata();

		Boolean nestedProvider = false;
		Boolean nestedConsumer = false;
		
		String providerGeneratedClass = null;
		String consumerGeneratedClass = null;
		
		String providerName = "";
		String providerType = "";
		String consumerName = "";
		String consumerType = "";
		
		int providerChildNumber = 0;
		int consumerChildNumber = 0;

//		System.out.println("Provider Request Metadata:");
//		ServiceUtils.readList(providerRequestMetadata);
//		System.out.println("Provider List of Request Elements:");
//		ServiceUtils.readList(providerRequestElements);
//		System.out.println("Consumer Request Metadata:");
//		ServiceUtils.readList(consumerRequestMetadata);
//		System.out.println("Consumer List of Request Elements:");
//		ServiceUtils.readList(consumerRequestElements);

		for (int i = 0; i < consumerRequestElements.size() - 1; i++) {
			consumerName = consumerRequestElements.get(i)[0];
			consumerType = consumerRequestElements.get(i)[1];
			match = false;

			if (consumerName.equals("Newclass")) {
				consumerGeneratedClass = consumerType;
				nestedConsumer = true;
			} 
			
			else if (consumerName.equals("StopClass")) {
				nestedConsumer = false;
				consumerChildNumber = 0;
			} 
			
			else {

				if (consumerName.equals("child")) {
					consumerChildNumber++;
					consumerName = consumerRequestElements.get(i)[1];
					consumerType = consumerRequestElements.get(i)[2];
				}

//				System.out.println("Consumer Response: " + consumerName + " - " + consumerType);

				for (int j = 0; j < providerRequestElements.size(); j++) {
					providerName = providerRequestElements.get(j)[0];
					providerType = providerRequestElements.get(j)[1];

					if (providerName.equals("Newclass")) {
						providerGeneratedClass = providerType;
						nestedProvider = true;
					} 
					
					else if (providerName.equals("StopClass"))
						nestedProvider = false;

					else {

						if (providerName.equals("child")) {
							providerChildNumber++;
							providerName = providerRequestElements.get(j)[1];
							providerType = providerRequestElements.get(j)[2];
						}

//						System.out.println("Provider Response: " + providerName + " - " + providerType);

						if (consumerName.equals(providerName)) {
							j = providerRequestElements.size() + 1;
							match = true;
//							System.out.println("NAME MATCH '''''''''''''''''''''''");
						} else {
							String consumerVariant = consumerRequestMetadata.get(i)[2];
							String providerVariant = providerRequestMetadata.get(j)[2];
							
							providerName = providerRequestMetadata.get(j)[0];
//							System.out.println("Variant: " + providerVariant);
							
							if (consumerName.equalsIgnoreCase(providerVariant) && !providerVariant.equals(" ")) {
								providerType = providerRequestMetadata.get(j)[1];
								match = true;

//								System.out.println("VARIANT-NAME MATCH '''''''''''''''''''''''");
//								System.out.println("Provider Response_variant: " + providerVariant + " -" + providerName + " - " + providerType);
							} 
							
							else if (providerName.equalsIgnoreCase(consumerVariant) && !consumerVariant.equals(" ")) {
								providerType = providerRequestMetadata.get(j)[1];
								match = true;

//								System.out.println("NAME-VARIANT MATCH '''''''''''''''''''''''");
//								System.out.println("Consumer Response_variant: " + consumerVariant + " -" + consumerName + " - " + consumerType);
							} 
							
							else if (consumerVariant.equalsIgnoreCase(providerVariant) && !providerVariant.equals(" ") && !consumerVariant.equals(" ")) {
								providerType = providerRequestMetadata.get(j)[1];
								match = true;

//								System.out.println("VARIANT-VARIANT MATCH '''''''''''''''''''''''");
//								System.out.println("Consumer Response_variant: " + consumerVariant + " -" + consumerName + " - " + consumerType);
//								System.out.println("Provider reponse_variant: " + providerVariant + " -" + providerName + " - " + providerType);
							}
						}
					}

					if (match) {
//						System.out.println("MATCH --- Provider: " + providerName + " - Consumer: " + consumerName);

						if (nestedConsumer)
							builder.addStatement("$T $L=payload_C.get$L().get$L() ", ServiceUtils.getType(consumerType), consumerName, consumerGeneratedClass, consumerName);
						else
							builder.addStatement("$T $L=payload_C.get$L() ", ServiceUtils.getType(consumerType), consumerName, consumerName);

						if (!consumerType.equalsIgnoreCase(providerType)) {

							if (consumerType.equalsIgnoreCase("String")) {
								if (providerType.equalsIgnoreCase("Boolean"))

									builder.addStatement("$T $L_P", ServiceUtils.getType(providerType), consumerName)
											.beginControlFlow("if($L)", consumerName)
											.addStatement("$L_P= true", consumerName)
											.endControlFlow().beginControlFlow("else")
											.addStatement("$L_P= false", consumerName)
											.endControlFlow();
								else
									builder.addStatement("$T $L_P= $L.parse$L($L)", ServiceUtils.getType(providerType), consumerName, capitalize(providerType), capitalize(ServiceUtils.getType(providerType).toString()), consumerName);

							} 
							
							else if (providerType.equalsIgnoreCase("String")) {
								if (consumerType.equalsIgnoreCase("Boolean")) {

									builder.addStatement("$T $L_P", ServiceUtils.getType(providerType), consumerName)
											.beginControlFlow("if($L.equalsIgnoreCase(\"true\"))", consumerName)
											.addStatement("$L_P= \"true\"", consumerName)
											.endControlFlow()
											.beginControlFlow("else")
											.addStatement("$L_P= \"false\"", consumerName)
											.endControlFlow();
								} else
									builder.addStatement("$T $L_P= $L +\"\"", ServiceUtils.getType(providerType), consumerName, consumerName);

							} 
							
							else if (consumerType.equalsIgnoreCase("Boolean")) {

								builder.addStatement("$T $L_P", ServiceUtils.getType(providerType), consumerName)
										.beginControlFlow("if($L)", consumerName)
										.addStatement("$L_P= 1", consumerName)
										.endControlFlow().beginControlFlow("else")
										.addStatement("$L_P= 0", consumerName)
										.endControlFlow();
							} 
							
							else {
								if ((getNumberType(providerType) > getNumberType(consumerType)))
									builder.addStatement("$T $L_P= $L", ServiceUtils.getType(providerType), consumerName, consumerName);

								else if ((getNumberType(providerType) < getNumberType(consumerType)))
									builder.addStatement("$T $L_P=($T)$L", ServiceUtils.getType(providerType), consumerName, ServiceUtils.getType(providerType), consumerName);
							}

						} else {
							builder.addStatement(" $T $L_P=$L", ServiceUtils.getType(providerType), consumerName, consumerName);
						}

						if (nestedProvider) {
							if (providerChildNumber < 2)
								builder.addStatement(" eu.generator.consumer.$L $L = new  eu.generator.consumer.$L ()", capitalize(providerGeneratedClass), providerGeneratedClass, capitalize(providerGeneratedClass));
								
							builder
								.addStatement(" $L.set$L($L_P)", providerGeneratedClass, providerName, consumerName)
								.addStatement("payload_P.set$L($L)", providerGeneratedClass, providerGeneratedClass);
						} else
							builder.addStatement("payload_P.set$L($L_P)", providerName, consumerName);

						match = false;
						j = providerRequestElements.size() + 1;

					} 
					
//					else
//						System.out.println("NO MATCH--Provider: " + providerName + " - Consumer: " + consumerName);
				}
			}
		}

		builder.addStatement("return payload_P");
		
		return builder.build();
	}

	// -------------------------------------------------------------------------------------------------
	private static MethodSpec buildResponseAdaptor(InterfaceMetadata consumerMetadata, InterfaceMetadata providerMetadata) throws ClassNotFoundException {

		MethodSpec.Builder builder = MethodSpec
			.methodBuilder("responseAdaptor")
			.addModifiers(Modifier.PUBLIC)
			.returns(ResponseDTO_C0.class)
			.addParameter(ResponseDTO_P0.class, "payload_P")
			.addStatement(" $T payload_C= new ResponseDTO_C0()", ResponseDTO_C0.class);

		Boolean match = false;
		ArrayList<String[]> consumerResponseElements = consumerMetadata.getElementsResponse().get(0).getElements();
		ArrayList<String[]> providerResponseElements = providerMetadata.getElementsResponse().get(0).getElements();
		ArrayList<String[]> consumerResponseMetadata = consumerMetadata.getElementsResponse().get(0).getMetadata();
		ArrayList<String[]> providerResponseMetadata = providerMetadata.getElementsResponse().get(0).getMetadata();
		
		Boolean nestedProvider = false;
		Boolean nestedConsumer = false;
		
		String providerGeneratedClass = null;
		String consumerGeneratedClass = null;
		
		String providerName = "";
		String providerType = "";
		String consumerName = "";
		String consumerType = "";
		
		int providerChildNumber = 0;
		int consumerChildNumber = 0;

//		System.out.print("Provider Response Metadata:\n\t");
//		ServiceUtils.readList(providerResponseMetadata);
//		System.out.print("Provider List of Response Elements:\n\t");
//		ServiceUtils.readList(providerResponseElements);
//		System.out.print("Consumer Response Metadata:\n\t");
//		ServiceUtils.readList(consumerResponseMetadata);
//		System.out.print("Consumer List of Response Elements:\n\t");
//		ServiceUtils.readList(consumerResponseElements);

		for (int consumerIndex = 0; consumerIndex < consumerResponseElements.size(); consumerIndex++) {
			consumerName = consumerResponseElements.get(consumerIndex)[0];
			consumerType = consumerResponseElements.get(consumerIndex)[1];

			if (consumerName.equals("Newclass")) {
				consumerGeneratedClass = consumerType;
				nestedConsumer = true;

			} else if (consumerName.equals("StopClass")) {
				nestedConsumer = false;
				consumerChildNumber = 0;
			} else {

				if (consumerName.equals("child")) {
					consumerChildNumber++;
					consumerName = consumerResponseElements.get(consumerIndex)[1];
					consumerType = consumerResponseElements.get(consumerIndex)[2];
				}

//				System.out.println("Consumer reponse: " + consumerName + " - " + consumerType);

				for (int providerIndex = 0; providerIndex < providerResponseElements.size(); providerIndex++) {
					providerName = providerResponseElements.get(providerIndex)[0];
					providerType = providerResponseElements.get(providerIndex)[1];

					if (providerName.equals("Newclass")) {
						providerGeneratedClass = providerType;
						nestedProvider = true;
					} 
					
					else if (providerName.equals("StopClass")) {
						nestedProvider = false;
						providerChildNumber = 0;
					} 
					
					else {

						if (providerName.equals("child")) {
							providerChildNumber++;
							providerName = providerResponseElements.get(providerIndex)[1];
							providerType = providerResponseElements.get(providerIndex)[2];
						}

//						System.out.println("Provider Response: " + providerName + " - " + providerType);

						if (consumerName.equals(providerName)) {
							match = true;
							providerIndex = providerResponseElements.size() + 1;
//							System.out.println("NAME MATCH '''''''''''''''''''''''");
						} else {
							int metadataIndex = providerIndex;
							
							String consumerVariant = consumerResponseMetadata.get(consumerIndex)[2];
							String providerVariant = providerResponseMetadata.get(metadataIndex)[2];
							
							providerName = providerResponseMetadata.get(metadataIndex)[0];
//							System.out.println("Variant: " + providerVariant);
							
							if (consumerName.equalsIgnoreCase(providerVariant) && !providerVariant.equals(" ")) {
								providerType = providerResponseMetadata.get(metadataIndex)[1];
								match = true;
								
//								System.out.println("NAME-VARIANT MATCH '''''''''''''''''''''''");
//								System.out.println("Provider Response Variant: " + providerVariant + " -" + providerName + " - " + providerType);
							} 
							
							else if (providerName.equalsIgnoreCase(consumerVariant) && !consumerVariant.equals(" ")) {
								providerType = providerResponseMetadata.get(metadataIndex)[1];
								match = true;
								
//								System.out.println("VARIANT-NAME MATCH '''''''''''''''''''''''");
//								System.out.println("Consumer Response Variant: " + consumerVariant + " -" + consumerName + " - " + consumerType);
							}
							
							else if (providerVariant.equalsIgnoreCase(consumerVariant) && !consumerVariant.equals(" ") && !providerVariant.equals(" ")) {
								providerType = providerResponseMetadata.get(metadataIndex)[1];
								match = true;
								
//								System.out.println("VARIANT-VARIANT MATCH '''''''''''''''''''''''");
//								System.out.println("Consumer Response Variant: " + consumerVariant + " -" + consumerName + " - " + consumerType);
//								System.out.println("Provider Response Variant: " + providerVariant + " -" + providerName + " - " + providerType);
							}
						}
					}

					if (match) {
//						System.out.println("MATCH --- Provider: " + providerName + " - Consumer: " + consumerName);
						
						if (nestedProvider)
							builder.addStatement("$T $L=payload_P.get$L().get$L() ", ServiceUtils.getType(providerType), providerName, providerGeneratedClass, providerName);
						else
							builder.addStatement("$T $L=payload_P.get$L() ", ServiceUtils.getType(providerType), providerName, providerName);

						if (!providerType.equalsIgnoreCase(consumerType)) {

							if (providerType.equalsIgnoreCase("String")) {
								if (consumerType.equalsIgnoreCase("Boolean")) {
									
									builder.addStatement("$T $L_C", ServiceUtils.getType(consumerType), providerName)
											.beginControlFlow("if($L)", providerName)
											.addStatement("$L_C= true", providerName)
											.endControlFlow()
											.beginControlFlow("else")
											.addStatement("$L_C= false", providerName)
											.endControlFlow();
								} else
									builder.addStatement("$T $L_C= $L.parse$L($L)", ServiceUtils.getType(consumerType), providerName, capitalize(consumerType), capitalize(consumerType), providerName);
							} 
							
							else if (consumerType.equalsIgnoreCase("String")) {
								if (providerType.equalsIgnoreCase("Boolean"))
									
									builder.addStatement("$T $L_C", ServiceUtils.getType(consumerType), providerName)
											.beginControlFlow("if($L.equalsIgnoreCase(\"true\"))", providerName)
											.addStatement("$L_C= \"true\"", providerName)
											.endControlFlow()
											.beginControlFlow("else")
											.addStatement("$L_C= \"false\"", providerName)
											.endControlFlow();
								else
									builder.addStatement("$T $L_C= $L +\"\"", ServiceUtils.getType(consumerType), providerName, providerName);
							} 
							
							else if (providerType.equalsIgnoreCase("Boolean")) {

								builder.addStatement("$T $L_C", ServiceUtils.getType(consumerType), providerName)
										.beginControlFlow("if($L)", providerName)
										.addStatement("$L_C= 1", providerName)
										.endControlFlow()
										.beginControlFlow("else")
										.addStatement("$L_C= 0", providerName)
										.endControlFlow();
							} 
							
							else {
								if ((getNumberType(consumerType) > getNumberType(providerType)))
									builder.addStatement("$T $L_C= $L", ServiceUtils.getType(consumerType), providerName, providerName);
								else if ((getNumberType(consumerType) < getNumberType(providerType)))
									builder.addStatement("$T $L_C=($T)$L", ServiceUtils.getType(consumerType), providerName, ServiceUtils.getType(consumerType), providerName);
							}

						} else
							builder.addStatement(" $T $L_C=$L", ServiceUtils.getType(consumerType), providerName, providerName);

						if (nestedConsumer) {
							if (consumerChildNumber < 2)
								builder.addStatement(" eu.generator.consumer.$L $L = new  eu.generator.consumer.$L ()", capitalize(consumerGeneratedClass), consumerGeneratedClass, capitalize(consumerGeneratedClass));
							
							builder
								.addStatement(" $L.set$L($L_C)", consumerGeneratedClass, consumerName, providerName)
								.addStatement("payload_C.set$L($L)", consumerGeneratedClass, consumerGeneratedClass);
						} else
							builder.addStatement("payload_C.set$L($L_C)", consumerName, providerName);

						match = false;
						providerIndex = consumerResponseElements.size() + 1;

					} 
					
//					else
//						System.out.println("NO MATCH--Provider: " + providerName + " - Consumer: " + consumerName);
				}
			}
		}

		builder.addStatement("return payload_C");

		return builder.build();
	}

	// -------------------------------------------------------------------------------------------------
	private static String capitalize(String name) {
		name = name.substring(0, 1).toUpperCase() + name.substring(1, name.length());
		return name;
	}

	// -------------------------------------------------------------------------------------------------
	private static int getNumberType(String type) {
		int typeNumber;

		if (type.equalsIgnoreCase("Integer") || type.equalsIgnoreCase("int"))
			typeNumber = 2;
		else if (type.equalsIgnoreCase("Byte"))
			typeNumber = 0;
		else if (type.equalsIgnoreCase("Double"))
			typeNumber = 5;
		else if (type.equalsIgnoreCase("Float"))
			typeNumber = 4;
		else if (type.equalsIgnoreCase("Short"))
			typeNumber = 1;
		else if (type.equalsIgnoreCase("Long"))
			typeNumber = 3;
		else
			typeNumber = -100;

		return typeNumber;
	}

}
