/**
 * Copyright 2007 The JA-SIG Collaborative.  All rights reserved.
 * See license distributed with this file and
 * available online at http://www.uportal.org/license.html
 */
package edu.wisc.my.redirect.url;

import java.text.MessageFormat;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Eric Dalquist
 * @version $Revision: 1.4 $
 */
public class LocalPortalUrlProvider implements InternalPortalUrlProvider {
    private Pattern serverNamePattern = Pattern.compile("(.*)");
    private String publicRedirectFormat = "{1}";
    private String publicRedirectProtocol = "http";
    private String privateRedirectFormat = "{1}";
    private String privateRedirectProtocol = "https";
    
    private String path = "/portal/render.userLayoutRootNode.uP";
    
    private String layoutRootParam = "uP_root";
    private String layoutRootKey = "root";
    
    private String structParam = "uP_sparam";
    private String structTabIndexKey = "activeTab";
    
    private String fnameParam = "uP_fname";
    
    private String requestTypeParam = "pltc_type";
    private String stateParam = "pltc_state";
    private String modeParam = "pltc_mode";
    
    private String portletParamPrefix = "pltp_";
    
    
    /**
     * Pattern used to match the server name of the current request. Defaults to "(.*)"
     */
    public void setServerNamePattern(Pattern serverNamePattern) {
        this.serverNamePattern = serverNamePattern;
    }

    /**
     * {@link java.text.MessageFormat} pattern for the server name for a public URL. The RegEx groups are passed as
     * format arguments.
     */
    public void setPublicRedirectFormat(String publicRedirectFormat) {
        this.publicRedirectFormat = publicRedirectFormat;
    }

    /**
     * Protocol to use for the public URL.
     * Defaults to HTTP
     */
    public void setPublicRedirectProtocol(String publicRedirectProtocol) {
        this.publicRedirectProtocol = publicRedirectProtocol;
    }

    /**
     * {@link java.text.MessageFormat} pattern for the server name for a public URL. The RegEx groups are passed as
     * format arguments.
     */
    public void setPrivateRedirectFormat(String privateRedirectFormat) {
        this.privateRedirectFormat = privateRedirectFormat;
    }

    /**
     * Protocol to use for the public URL.
     * Defaults to HTTPS
     */
    public void setPrivateRedirectProtocol(String privateRedirectProtocol) {
        this.privateRedirectProtocol = privateRedirectProtocol;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @param fnameParam the fnameParam to set
     */
    public void setFnameParam(String fnameParam) {
        this.fnameParam = fnameParam;
    }

    /**
     * @param requestTypeParam the requestTypeParam to set
     */
    public void setRequestTypeParam(String requestTypeParam) {
        this.requestTypeParam = requestTypeParam;
    }

    /**
     * @param stateParam the stateParam to set
     */
    public void setStateParam(String stateParam) {
        this.stateParam = stateParam;
    }

    /**
     * @param modeParam the modeParam to set
     */
    public void setModeParam(String modeParam) {
        this.modeParam = modeParam;
    }

    /**
     * @param portletParamPrefix the portletParamPrefix to set
     */
    public void setPortletParamPrefix(String portletParamPrefix) {
        this.portletParamPrefix = portletParamPrefix;
    }
    
    /**
     * @param layoutRootParam the layoutRootParam to set
     */
    public void setLayoutRootParam(String layoutRootParam) {
        this.layoutRootParam = layoutRootParam;
    }

    /**
     * @param layoutRootKey the layoutRootKey to set
     */
    public void setLayoutRootKey(String layoutRootKey) {
        this.layoutRootKey = layoutRootKey;
    }

    /**
     * @param structParam the structParam to set
     */
    public void setStructParam(String structParam) {
        this.structParam = structParam;
    }

    /**
     * @param structTabIndexKey the structTabIndexKey to set
     */
    public void setStructTabIndexKey(String structTabIndexKey) {
        this.structTabIndexKey = structTabIndexKey;
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.redirect.PortalUrlProvider#getPortalUrl(java.lang.String)
     */
    public PortalUrl getPortalUrl(String serverName) {
        return new PortalUrlImpl(serverName, this);
    }

    /* (non-Javadoc)
     * @see edu.wisc.my.redirect.url.InternalPortalUrlProvider#toUrl(edu.wisc.my.redirect.url.InternalPortalUrl)
     */
    public String toString(InternalPortalUrl portalUrl) {
        final String serverName = ((PortalUrlImpl)portalUrl).getServerName();
        
        final Matcher serverNameMatcher = this.serverNamePattern.matcher(serverName);
        if (!serverNameMatcher.matches()) {
            throw new IllegalArgumentException("Configured serverNamePattern '" + this.serverNamePattern.pattern() + "' does not match server name '" + serverName + "'");
        }
        
        
        final int groupCount = serverNameMatcher.groupCount();
        final Object[] groupValues = new Object[groupCount + 1];
        for (int groupIndex = 0; groupIndex <= groupCount; groupIndex++) {
            groupValues[groupIndex] = serverNameMatcher.group(groupIndex);
        }
        
        final String protocol;
        final String redirectServerName;
        if (portalUrl.isPublic()) {
            protocol = this.publicRedirectProtocol;
            redirectServerName = MessageFormat.format(this.publicRedirectFormat, groupValues);
        }
        else {
            protocol = this.privateRedirectProtocol;
            redirectServerName = MessageFormat.format(this.privateRedirectFormat, groupValues);
        }
        
        final StringBuilder url = new StringBuilder();
        
        url.append(protocol);
        url.append("://");
        url.append(redirectServerName);
        
        url.append(this.path);
        
        /*
         * render.userLayoutRootNode.uP?uP_root=root&uP_sparam=activeTab&activeTab=1
         */
        
        final String targetPortlet = portalUrl.getTargetPortlet();
        final Integer tabIndex = portalUrl.getTabIndex();
        
        if (targetPortlet == null && tabIndex == null) {
            throw new IllegalArgumentException("Target portlet fname or portal tab index must be set on PortletUrl");
        }
        
        char paramJoin = '?';
        
        if (tabIndex != null) {
            url.append(paramJoin).append(this.layoutRootKey).append("=").append(this.layoutRootParam);
            paramJoin = '&';
            url.append(paramJoin).append(this.structParam).append("=").append(this.structTabIndexKey);
            url.append(paramJoin).append(this.structTabIndexKey).append("=").append(tabIndex);
        }
        
        if (targetPortlet != null) {
            url.append(paramJoin).append(this.fnameParam).append("=").append(targetPortlet);
            paramJoin = '&';
            url.append(paramJoin).append(this.requestTypeParam).append("=").append(portalUrl.getType());
            url.append(paramJoin).append(this.stateParam).append("=").append(portalUrl.getWindowState());
            url.append(paramJoin).append(this.modeParam).append("=").append(portalUrl.getPortletMode());
        }
        
        for (final Map.Entry<String, String[]> parameterEntry : portalUrl.getParameters().entrySet()) {
            final String name = parameterEntry.getKey();
            for (final String value : parameterEntry.getValue()) {
                url.append(paramJoin).append(this.portletParamPrefix).append(name).append("=").append(value);
            }
        }
        
        return url.toString();
    }
}
