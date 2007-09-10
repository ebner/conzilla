/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.rdf;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;

/**
 * FOAF vocabulary class. Taken from W3C.
 * 
 * @author Hannes Ebner
 */
public class FOAF {

	static final String ns = "http://xmlns.com/foaf/0.1/";

	public static Property depicts;

	public static Property mbox;

	public static Property regionDepicts;

	public static Property name;

	public static Property firstName;

	public static Property surname;

	public static Resource person;

	static {
		depicts = new PropertyImpl(ns + "depicts");
		regionDepicts = new PropertyImpl(ns + "regionDepicts");
		mbox = new PropertyImpl(ns + "mbox");
		name = new PropertyImpl(ns + "name");
		firstName = new PropertyImpl(ns + "firstName");
		surname = new PropertyImpl(ns + "surname");
		person = new ResourceImpl(ns + "Person");
	}

	public static String getURI() {
		return ns;
	}

}