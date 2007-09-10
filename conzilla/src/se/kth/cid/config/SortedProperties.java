/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Based on java.util.Properties, provides sorted keys.
 * 
 * @author Hannes Ebner
 * @version $Id$
 */
public class SortedProperties extends Properties {

	public SortedProperties() {
		super();
	}

	public SortedProperties(Properties defaults) {
		super(defaults);
	}

	public synchronized Enumeration keys() {
		ArrayList keyList = Collections.list(super.keys());
		Collections.sort(keyList);
		return Collections.enumeration(keyList);
	}

}