/**
 * Copyright 2007 The JA-SIG Collaborative.  All rights reserved.
 * See license distributed with this file and
 * available online at http://www.uportal.org/license.html
 */
package edu.wisc.my.redirect.url;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.Validate;

/**
 * @author Eric Dalquist
 * @version $Revision: 1.2 $
 */
class PortalUrlImpl implements InternalPortalUrl {
    private final String serverName;
    private final InternalPortalUrlProvider portalUrlProvider;
    
    private Map<String, String[]> parameters = new LinkedHashMap<String, String[]>();
    private String portletMode = "view";
    private String windowState = "normal";
    private boolean isPublic = false;
    private Integer tabIndex;
    private String portletFunctionalName;
    private RequestType requestType = RequestType.RENDER;
    

    public PortalUrlImpl(String serverName, InternalPortalUrlProvider portalUrlProvider) {
        Validate.notNull(serverName, "serverName can not be null");
        Validate.notNull(portalUrlProvider, "portalUrlProvider can not be null");
        this.serverName = serverName;
        this.portalUrlProvider = portalUrlProvider;
    }
    
    public String getServerName() {
        return this.serverName;
    }
    
    
    /* (non-Javadoc)
     * @see edu.wisc.my.redirect.url.PortalUrl#setParameter(java.lang.String, java.lang.String[])
     */
    public void setParameter(String name, String... values) {
        this.parameters.put(name, values);
    }
    /* (non-Javadoc)
     * @see edu.wisc.my.redirect.url.PortalUrl#setParameters(java.util.Map)
     */
    public void setParameters(Map<String, String[]> parameters) {
        this.parameters = new LinkedHashMap<String, String[]>(parameters);        
    }
    /* (non-Javadoc)
     * @see edu.wisc.my.redirect.url.PortalUrl#setPortletMode(java.lang.String)
     */
    public void setPortletMode(String portletMode) {
        this.portletMode = portletMode;
    }
    /* (non-Javadoc)
     * @see edu.wisc.my.redirect.url.PortalUrl#setPublic(boolean)
     */
    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }
    /* (non-Javadoc)
     * @see edu.wisc.my.redirect.url.PortalUrl#setTargetPortlet(java.lang.String)
     */
    public void setTargetPortlet(String functionalName) {
        this.portletFunctionalName = functionalName;
    }
    /* (non-Javadoc)
     * @see edu.wisc.my.redirect.url.PortalUrl#setTabIndex(int)
     */
    public void setTabIndex(int tabIndex) {
        this.tabIndex = tabIndex;
    }
    /* (non-Javadoc)
     * @see edu.wisc.my.redirect.url.PortalUrl#setType(edu.wisc.my.redirect.url.PortalUrl.RequestType)
     */
    public void setType(RequestType action) {
        this.requestType = action;
    }
    /* (non-Javadoc)
     * @see edu.wisc.my.redirect.url.PortalUrl#setWindowState(java.lang.String)
     */
    public void setWindowState(String windowState) {
        this.windowState = windowState;
    }
    
    
    /* (non-Javadoc)
     * @see edu.wisc.my.redirect.url.InternalPortalUrl#getParameters()
     */
    public Map<String, String[]> getParameters() {
        return Collections.unmodifiableMap(this.parameters);
    }
    /* (non-Javadoc)
     * @see edu.wisc.my.redirect.url.InternalPortalUrl#getPortletMode()
     */
    public String getPortletMode() {
        return this.portletMode;
    }
    /* (non-Javadoc)
     * @see edu.wisc.my.redirect.url.InternalPortalUrl#getTargetPortlet()
     */
    public String getTargetPortlet() {
        return this.portletFunctionalName;
    }
    /* (non-Javadoc)
     * @see edu.wisc.my.redirect.url.InternalPortalUrl#getTabIndex()
     */
    public Integer getTabIndex() {
        return this.tabIndex;
    }
    /* (non-Javadoc)
     * @see edu.wisc.my.redirect.url.InternalPortalUrl#getType()
     */
    public RequestType getType() {
        return this.requestType;
    }
    /* (non-Javadoc)
     * @see edu.wisc.my.redirect.url.InternalPortalUrl#getWindowState()
     */
    public String getWindowState() {
        return this.windowState;
    }
    /* (non-Javadoc)
     * @see edu.wisc.my.redirect.url.InternalPortalUrl#isPublic()
     */
    public boolean isPublic() {
        return this.isPublic;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.portalUrlProvider.toString(this);
    }
}
