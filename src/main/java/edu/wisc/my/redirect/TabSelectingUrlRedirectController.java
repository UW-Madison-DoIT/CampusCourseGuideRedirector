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
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.view.RedirectView;

import edu.wisc.my.redirect.url.PortalUrl;
import edu.wisc.my.redirect.url.PortalUrlProvider;
import edu.wisc.my.redirect.url.PortalUrl.RequestType;

/**
 * @author Eric Dalquist
 * @version $Revision: 1.1 $
 */
public class TabSelectingUrlRedirectController extends AbstractController {
    private PortalUrlProvider portalUrlProvider;
    private Integer publicTabIndex;
    private Integer privateTabIndex;
    private Map<String, Set<String>> parameterMappings = Collections.emptyMap();
    private Map<String, List<String>> staticParameters = Collections.emptyMap();
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

    public Map<String, Set<String>> getParameterMappings() {
        return this.parameterMappings;
    }
    /**
     * @return the publicTabIndex
     */
    public Integer getPublicTabIndex() {
        return publicTabIndex;
    }
    /**
     * @param publicTabIndex the publicTabIndex to set
     */
    @Required
    public void setPublicTabIndex(Integer publicTabIndex) {
        this.publicTabIndex = publicTabIndex;
    }
    /**
     * @return the privateTabIndex
     */
    public Integer getPrivateTabIndex() {
        return privateTabIndex;
    }
    /**
     * @param privateTabIndex the privateTabIndex to set
     */
    @Required
    public void setPrivateTabIndex(Integer privateTabIndex) {
        this.privateTabIndex = privateTabIndex;
    }
    /**
     * @param parameterMappings the parameterMappings to set
     */
    public void setParameterMappings(Map<String, Set<String>> parameterMappings) {
        this.parameterMappings = parameterMappings;
    }

    /**
     * @return the staticParameters
     */
    public Map<String, List<String>> getStaticParameters() {
        return this.staticParameters;
    }
    /**
     * @param staticParameters the staticParameters to set
     */
    public void setStaticParameters(Map<String, List<String>> staticParameters) {
        this.staticParameters = staticParameters;
    }
    /**
     * @return the strictParameterMatching
     */
    public boolean isStrictParameterMatching() {
        return strictParameterMatching;
    }
    /**
     * @param strictParameterMatching the strictParameterMatching to set
     */
    public void setStrictParameterMatching(boolean strictParameterMatching) {
        this.strictParameterMatching = strictParameterMatching;
    }
    
    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.AbstractController#handleRequestInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final String serverName = request.getServerName();
        final PortalUrl portalUrl = this.portalUrlProvider.getPortalUrl(serverName);
        
        
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
        for (final Map.Entry<String, List<String>> parameterMappingEntry : this.staticParameters.entrySet()) {
            final String name = parameterMappingEntry.getKey();
            final List<String> values = parameterMappingEntry.getValue();
            
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Adding static parameter '" + name + "' with values: " + values);
            }
            
            portalUrl.setParameter(name, values.toArray(new String[values.size()]));
        }
        
        //Map request parameters
        for (final Map.Entry<String, Set<String>> parameterMappingEntry : this.parameterMappings.entrySet()) {
            final String name = parameterMappingEntry.getKey();
            final String[] values = request.getParameterValues(name);
            
            if (values != null) {
                for (final String mappedName : parameterMappingEntry.getValue()) {
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug("Mapping parameter '" + name + "' to portal parameter '" + mappedName + "' with values: " + Arrays.asList(values));
                    }
                    
                    portalUrl.setParameter(mappedName, values);
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
        
        if (isAuthenticated) {
            portalUrl.setTabIndex(this.privateTabIndex);
        }
        else {
            portalUrl.setTabIndex(this.publicTabIndex);
        }
        
        portalUrl.setType(RequestType.ACTION);
        
        
        final String redirectUrl = portalUrl.toString();
        if (this.logger.isInfoEnabled()) {
            this.logger.info("Redirecting to: " + redirectUrl);
        }
        
        return new ModelAndView(new RedirectView(redirectUrl, false));
    }

}
