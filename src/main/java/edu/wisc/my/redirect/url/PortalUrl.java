/**
 * Copyright 2007 The JA-SIG Collaborative.  All rights reserved.
 * See license distributed with this file and
 * available online at http://www.uportal.org/license.html
 */
package edu.wisc.my.redirect.url;

import java.util.Map;

/**
 * @author Eric Dalquist
 * @version $Revision: 1.2 $
 */
public interface PortalUrl {
    public enum RequestType {
        ACTION,
        RENDER;
    }
    
    public void setTabIndex(int tabIndex);
    
    public void setTargetPortlet(String functionalName);
    
    public void setWindowState(String windowState);
    
    public void setPortletMode(String portletMode);
    
    public void setType(RequestType action);
    
    public void setParameter(String name, String... values);
    
    public void setParameters(Map<String, String[]> parameters);
    
    public void setPublic(boolean isPublic);
    
    public String toString();
}
