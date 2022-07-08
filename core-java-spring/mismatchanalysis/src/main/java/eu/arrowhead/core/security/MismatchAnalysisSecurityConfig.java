package eu.arrowhead.core.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import eu.arrowhead.common.security.DefaultSecurityConfig;

@Configuration
@EnableWebSecurity
public class MismatchAnalysisSecurityConfig extends DefaultSecurityConfig {

}