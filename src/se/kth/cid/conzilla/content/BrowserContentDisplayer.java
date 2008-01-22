/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.conzilla.content;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.kth.cid.component.Resource;

public abstract class BrowserContentDisplayer extends AbstractContentDisplayer {
	
	Log log = LogFactory.getLog(BrowserContentDisplayer.class);

	public void setContent(Resource c) throws ContentException {
		if (c == null) {
			super.setContent(null);
			return;
		}

		log.debug("Browser will show " + c.getURI());

		// URI baseuri=URIClassifier.parseValidURI(c.getURI());
		try {
			showDocument(new URL(c.getURI()));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ContentException e) {
			e.printStackTrace();
		}
		/*
		 * URL[] urls =
		 * extractUsableURLs(MetaDataUtils.getLocations(c.getMetaData().get_technical_location(),
		 * baseuri)); boolean fail = true; for(int i = 0; i < urls.length &&
		 * fail; i++) { try { fail = ! showDocument(urls[i]); }
		 * catch(ContentException e) { throw new
		 * ContentException(e.getMessage(), c); } } if(fail) throw new
		 * ContentException("No valid location for content found!", c); else
		 * super.setContent(c);
		 */
	}

	protected abstract boolean showDocument(URL url) throws ContentException;

}
