package eu.arrowhead.core.interfaceGenerator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import eu.arrowhead.common.ApplicationInitListener;

@Component
public class InterfaceGeneratorApplicationInitListener  extends ApplicationInitListener{

	//=================================================================================================
	// members
	
	@Autowired
	private ApplicationContext applicationContext;

}
