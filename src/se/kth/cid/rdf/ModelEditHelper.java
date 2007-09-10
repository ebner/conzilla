/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.rdf;

import se.kth.cid.util.Tracer;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

public class ModelEditHelper {
    public static boolean setStringToModel(
        Model model,
        String uri,
        Property prop,
        String newValue) {
        return setToModel(model, uri, prop, String.class, newValue);
    }

    public static boolean setIntegerToModel(
        Model model,
        String uri,
        Property prop,
        int i) {
        return setToModel(model, uri, prop, Integer.class, new Integer(i));
    }

    public static boolean setBooleanToModel(
        Model model,
        String uri,
        Property prop,
        boolean b) {
        return setToModel(model, uri, prop, Boolean.class, new Boolean(b));
    }

    public static boolean setToModel(
        Model model,
        String uri,
        Property prop,
        Class cls,
        Object newValue) {
        try {
            Resource subject = model.getResource(uri);

            try {
                Statement st = subject.getProperty(prop);
                //throws exception whenever there isn't one.

                if (st != null)
                    st.remove();
            } catch (Exception re) {
                Tracer.debug(
                    "Failed removing old value of property "
                        + prop.getURI()
                        + " in model."
                        + re.getMessage());
                return false;
            }

            try {
                if (cls == String.class)
                    subject.addProperty(prop, (String) newValue);
                else if (cls == Integer.class)
                    subject.addProperty(prop, ((Integer) newValue).toString());
                else if (cls == Boolean.class)
                    subject.addProperty(prop, ((Boolean) newValue).toString());
            } catch (Exception re) {
                Tracer.debug(
                    "Failed inserting new value with property "
                        + prop.getURI()
                        + " in model."
                        + re.getMessage());
                return false;
            }
        } catch (Exception re) {
            Tracer.debug(
                "Failed to fetch subject to edit the property"
                    + prop.getURI()
                    + " for in model."
                    + re.getMessage());
            return false;
        }
        return true;
    }

}