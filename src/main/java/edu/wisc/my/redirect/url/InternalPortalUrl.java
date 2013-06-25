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
public interface InternalPortalUrl extends PortalUrl {
    public String getTargetPortlet();
    
    public Integer getTabIndex();
    
    public String getWindowState();
    
    public String getPortletMode();
    
    public RequestType getType();
    
    public Map<String, String[]> getParameters();
    
    public boolean isPublic();
}
