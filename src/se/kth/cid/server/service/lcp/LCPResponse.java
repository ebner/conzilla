/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.server.service.lcp;

import se.kth.cid.server.service.Response;

public interface LCPResponse extends Response{
   public Object createResponse() throws Exception;
   public String getProtocolVersion();
}
