/**
 * Copyright 2007 The JA-SIG Collaborative.  All rights reserved.
 * See license distributed with this file and
 * available online at http://www.uportal.org/license.html
 */
package edu.wisc.my.redirect.url;

/**
 * @author Eric Dalquist
 * @version $Revision: 1.1 $
 */
interface InternalPortalUrlProvider extends PortalUrlProvider {
    public String toString(InternalPortalUrl portalUrl);
}
