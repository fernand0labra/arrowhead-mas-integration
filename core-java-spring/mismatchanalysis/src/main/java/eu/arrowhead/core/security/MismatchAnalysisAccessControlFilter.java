package eu.arrowhead.core.security;

import java.util.Map;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.security.CoreSystemAccessControlFilter;

@Component
@ConditionalOnProperty(name = CommonConstants.SERVER_SSL_ENABLED, matchIfMissing = true)
public class MismatchAnalysisAccessControlFilter extends CoreSystemAccessControlFilter {

    //=================================================================================================
    // members
    //=================================================================================================
    // assistant methods
    //-------------------------------------------------------------------------------------------------
}
