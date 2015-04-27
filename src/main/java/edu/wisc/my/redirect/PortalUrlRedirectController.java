/**
 * Copyright 2007 The JA-SIG Collaborative.  All rights reserved.
 * See license distributed with this file and
 * available online at http://www.uportal.org/license.html
 */
package edu.wisc.my.redirect;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.view.RedirectView;

import edu.wisc.my.redirect.url.PortalUrl;
import edu.wisc.my.redirect.url.PortalUrlProvider;
import edu.wisc.my.redirect.url.PortalUrl.RequestType;

/**
 * @author Eric Dalquist
 */
public class PortalUrlRedirectController extends AbstractController implements InitializingBean {
    private PortalUrlProvider portalUrlProvider;
    private String portletFunctionalName;
    private Integer tabIndex;
    private String windowState;
    private String portletMode;
    private Map<String, Set<String>> parameterMappings = Collections.emptyMap();
    private Map<String, List<String>> staticParameters = Collections.emptyMap();
    private Map<String, Map<String, List<String>>> conditionalParameterMappings = Collections.emptyMap();
    private boolean strictParameterMatching = true;
    

    public PortalUrlProvider getPortalUrlProvider() {
        return this.portalUrlProvider;
    }
    /**
     * @param portalUrlProvider the portalUrlProvider to set
     */
    @Required
    public void setPortalUrlProvider(PortalUrlProvider portalUrlProvider) {
        this.portalUrlProvider = portalUrlProvider;
    }

    public String getPortletFunctionalName() {
        return this.portletFunctionalName;
    }
    /**
     * @param portletFunctionalName the portletFunctionalName to set
     */
    public void setPortletFunctionalName(String portletFunctionalName) {
        this.portletFunctionalName = portletFunctionalName;
    }

    public Map<String, Set<String>> getParameterMappings() {
        return this.parameterMappings;
    }
    /**
     * @return the tabIndex
     */
    public Integer getTabIndex() {
        return tabIndex;
    }
    /**
     * @param tabIndex the tabIndex to set
     */
    public void setTabIndex(Integer tabIndex) {
        this.tabIndex = tabIndex;
    }
    /**
     * @param parameterMappings the parameterMappings to set
     */
    public void setParameterMappings(Map<String, Set<String>> parameterMappings) {
        this.parameterMappings = parameterMappings;
    }

    public String getWindowState() {
        return this.windowState;
    }
    /**
     * @param windowState the windowState to set
     */
    public void setWindowState(String windowState) {
        this.windowState = windowState;
    }

    public String getPortletMode() {
        return this.portletMode;
    }
    /**
     * @param portletMode the portletMode to set
     */
    public void setPortletMode(String portletMode) {
        this.portletMode = portletMode;
    }

    public Map<String, List<String>> getStaticParameters() {
        return this.staticParameters;
    }
    /**
     * Parameters that are always added to the generated URL
     */
    public void setStaticParameters(Map<String, List<String>> staticParameters) {
        this.staticParameters = staticParameters;
    }

    public boolean isStrictParameterMatching() {
        return strictParameterMatching;
    }
    /**
     * If true all mapped parameters must be specified on the incoming URL
     */
    public void setStrictParameterMatching(boolean strictParameterMatching) {
        this.strictParameterMatching = strictParameterMatching;
    }

    public Map<String, Map<String, List<String>>> getConditionalParameterMappings() {
        return conditionalParameterMappings;
    }
    /**
     * Parameters added to the URL only if the matching dynamic parameter is specified on the incoming URL
     */
    public void setConditionalParameterMappings(Map<String, Map<String, List<String>>> conditionalParameterMappings) {
        this.conditionalParameterMappings = conditionalParameterMappings;
    }
    
    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception {
        if (this.portletFunctionalName == null && this.tabIndex == null) {
            throw new BeanCreationException("Either portletFunctionalName or tabIndex must be specified");
        }
    }
    
    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.AbstractController#handleRequestInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final String serverName = request.getServerName();
        final PortalUrl portalUrl = this.portalUrlProvider.getPortalUrl(serverName);
        
        if (this.portletFunctionalName != null) {
            portalUrl.setTargetPortlet(this.portletFunctionalName);
        }
        
        if (this.tabIndex != null) {
            portalUrl.setTabIndex(this.tabIndex);
        }
        
        //If strict param matching only run if the request parameter keyset matches the mapped parameter keyset
        final Set<?> requestParameterKeys = request.getParameterMap().keySet();
        if (this.strictParameterMatching && !requestParameterKeys.equals(this.parameterMappings.keySet())) {
            if (this.logger.isInfoEnabled()) {
                this.logger.info("Sending not found error, requested parameter key set " + requestParameterKeys + " does not match mapped parameter key set " + this.parameterMappings.keySet());
            }
            
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
        
        //Map static parameters
        logger.debug("Mapping " + staticParameters.size() + " static parameters");
        for (final Map.Entry<String, List<String>> parameterMappingEntry : this.staticParameters.entrySet()) {
            final String name = parameterMappingEntry.getKey();
            final List<String> values = parameterMappingEntry.getValue();
            
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Adding static parameter '" + name + "' with values: " + values);
            }
            
            portalUrl.setParameter(name, values.toArray(new String[values.size()]));
        }
        
        //Map request parameters
        logger.debug("Mapping " + parameterMappings.entrySet().size() + " request parameters");
        for (final Map.Entry<String, Set<String>> parameterMappingEntry : this.parameterMappings.entrySet()) {
            final String name = parameterMappingEntry.getKey();
	        logger.debug("Mapping parameter " + name);
	        final String[] values = request.getParameterValues(name);
            
            if (values != null) {
                for (final String mappedName : parameterMappingEntry.getValue()) {
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug("Mapping parameter '" + name + "' to portal parameter '" + mappedName + "' with values: " + Arrays.asList(values));
                    }
                    
                    portalUrl.setParameter(mappedName, values);
                }
                
                //Add any conditional parameters for the URL parameter
                final Map<String, List<String>> conditionalParameters = this.conditionalParameterMappings.get(name);
                if (conditionalParameters != null) {
                    for (final Map.Entry<String, List<String>> conditionalParameterEntry : conditionalParameters.entrySet()) {
                        final String condName = conditionalParameterEntry.getKey();
                        final List<String> condValues = conditionalParameterEntry.getValue();
                        
                        if (this.logger.isDebugEnabled()) {
                            this.logger.debug("Adding conditional parameter '" + condName + "' with values: " + condValues);
                        }
                        
                        portalUrl.setParameter(condName, condValues.toArray(new String[condValues.size()]));
                    }
                }
            }
            else if (this.logger.isDebugEnabled()) {
                this.logger.debug("Skipping mapped parameter '" + name + "' since it was not specified on the original URL");
            }
        }
        
        //Set public based on if remoteUser is set
        final String remoteUser = request.getRemoteUser();
        final boolean isAuthenticated = StringUtils.isNotBlank(remoteUser);
        portalUrl.setPublic(!isAuthenticated);
        
        if (this.windowState != null) {
            portalUrl.setWindowState(this.windowState);
        }
        
        if (this.portletMode != null) {
            portalUrl.setWindowState(this.portletMode);
        }
        
        portalUrl.setType(RequestType.ACTION);
        
        
        final String redirectUrl = portalUrl.toString();
        if (this.logger.isInfoEnabled()) {
            this.logger.info("Redirection URL: " + redirectUrl);
        }
        
        return new ModelAndView(new RedirectView(redirectUrl, false));
    }

}
