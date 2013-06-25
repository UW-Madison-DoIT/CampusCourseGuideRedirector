/**
 * Copyright (c) 2000-2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package edu.wisc.my.redirect.url;

import java.text.MessageFormat;

import junit.framework.TestCase;

/**
 * @author Eric Dalquist
 * @version $Revision: 1.3 $
 */
public class LocalPortalUrlProviderTest extends TestCase {
    public void testFormatter() throws Exception {
        final String formatString = "public.{1}.foo.{0}.bar";
        
        final MessageFormat messageFormat = new MessageFormat(formatString);
        
        assertEquals("public.{1}.foo.0.bar", messageFormat.format(new Object[] { "0" }));
        assertEquals("public.1.foo.0.bar", messageFormat.format(new Object[] { "0", "1" }));
        assertEquals("public.1.foo.0.bar", messageFormat.format(new Object[] { "0", "1", "2" }));
    }
    
    public void testPortletUrlToString() throws Exception {
        final LocalPortalUrlProvider portalUrlProvider = new LocalPortalUrlProvider();
        portalUrlProvider.setPublicRedirectFormat("public.{1}");
        portalUrlProvider.setPublicRedirectProtocol("http");
        portalUrlProvider.setPrivateRedirectFormat("{1}");
        portalUrlProvider.setPrivateRedirectProtocol("https");
        
        
        final PortalUrlImpl portalUrl = new PortalUrlImpl("my-test.doit.wisc.edu", portalUrlProvider);
        portalUrl.setTargetPortlet("fname");
        
        
        portalUrl.setPublic(true);
        final String publicUrl = portalUrl.toString();
        assertEquals("http://public.my-test.doit.wisc.edu/portal/render.userLayoutRootNode.uP?uP_fname=fname&pltc_type=RENDER&pltc_state=normal&pltc_mode=view", publicUrl);
        
        portalUrl.setPublic(false);
        final String privateUrl = portalUrl.toString();
        assertEquals("https://my-test.doit.wisc.edu/portal/render.userLayoutRootNode.uP?uP_fname=fname&pltc_type=RENDER&pltc_state=normal&pltc_mode=view", privateUrl);
    }
    
    public void testTabUrlToString() throws Exception {
        final LocalPortalUrlProvider portalUrlProvider = new LocalPortalUrlProvider();
        portalUrlProvider.setPublicRedirectFormat("public.{1}");
        portalUrlProvider.setPublicRedirectProtocol("http");
        portalUrlProvider.setPrivateRedirectFormat("{1}");
        portalUrlProvider.setPrivateRedirectProtocol("https");
        
        
        final PortalUrlImpl portalUrl = new PortalUrlImpl("my-test.doit.wisc.edu", portalUrlProvider);
        portalUrl.setTabIndex(3);
        
        
        portalUrl.setPublic(true);
        final String publicUrl = portalUrl.toString();
        assertEquals("http://public.my-test.doit.wisc.edu/portal/render.userLayoutRootNode.uP?root=uP_root&uP_sparam=activeTab&activeTab=3", publicUrl);
        
        portalUrl.setPublic(false);
        final String privateUrl = portalUrl.toString();
        assertEquals("https://my-test.doit.wisc.edu/portal/render.userLayoutRootNode.uP?root=uP_root&uP_sparam=activeTab&activeTab=3", privateUrl);
    }
}
