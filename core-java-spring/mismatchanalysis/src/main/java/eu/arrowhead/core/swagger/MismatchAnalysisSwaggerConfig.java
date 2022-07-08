package eu.arrowhead.core.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.swagger.DefaultSwaggerConfig;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
public class MismatchAnalysisSwaggerConfig extends DefaultSwaggerConfig {

    //=================================================================================================
    // methods
    //-------------------------------------------------------------------------------------------------
    public MismatchAnalysisSwaggerConfig() {
        super(CommonConstants.CORE_SYSTEM_MISMATCHANALYSIS);
    }

    //-------------------------------------------------------------------------------------------------
    @Bean
    public Docket customizeSwagger() {
        return configureSwaggerForCoreSystem(this.getClass().getPackageName());
    }
}
