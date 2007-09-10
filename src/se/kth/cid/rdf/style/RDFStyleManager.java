/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.rdf.style;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

import se.kth.cid.component.ComponentException;
import se.kth.cid.component.ResourceStore;
import se.kth.cid.concept.Concept;
import se.kth.cid.layout.DrawerLayout;
import se.kth.cid.layout.StatementLayout;
import se.kth.cid.rdf.CV;
import se.kth.cid.rdf.RDFAttributeEntry;
import se.kth.cid.rdf.RDFComponent;
import se.kth.cid.rdf.RDFConcept;
import se.kth.cid.style.StyleManager;
import se.kth.cid.util.Tracer;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDFS;

//TODO, fix default stuff
/** 
 *  @author Matthias Palmer
 *  @version $Revision$
 */
public class RDFStyleManager implements StyleManager {
    ResourceStore store;

    /** For each type there is a stack of types....*/
    Hashtable type2styles;
    RDFConcept resourcestyle;
    RDFConcept propertystyle;

    public RDFStyleManager(ResourceStore store) {
        this.store = store;

        //Initialization.
        type2styles = new Hashtable();
    }
    
    public List getStylesFromClass(String type) {
        return excavateStyleFromType(
                                    type,
                                    RDFS.subClassOf,
                                    new Stack());
    }

    public List getStylesFromProperty(String type) {
        
        return excavateStyleFromType(
                                    type,
                                    RDFS.subPropertyOf,
                                    new Stack());
    }

    public List getStylesForDrawer(DrawerLayout ol) {
        Vector styles = new Vector();

        if (ol instanceof RDFComponent) {
            //LAYOUT instance styling
            styles.addAll(
                excavateStyles(
                    ((RDFComponent) ol).getProperties(CV.styleInstance)));

            //Fetch CONCEPT for given LAYOUT.
            RDFComponent co = getRDFResource(ol.getConceptURI());
            if (co != null && co instanceof RDFConcept) {
                //CONCEPT Instance styling
                styles.addAll(
                    excavateStyles(co.getProperties(CV.styleInstance)));

                //CONCEPT Class styling
                //First if LineStyle is asked, if the concept is a statement 
                //then use the predicate (propoerty) as type and check the 
                //class hierarchy for properties.
                //Otherwise (else) check the type of the concept as a class
                //with a belonging class hierarchy.
                //---------------------
                Vector nstyles = null;

                if (ol instanceof StatementLayout
                    && ((RDFConcept) co).getTriple() != null) {

                    Vector nnstyles =
                        excavateStyleFromType(
                            ((RDFConcept) co).getTriple().predicateURI(),
                            RDFS.subPropertyOf,
                            new Stack());

										//Class styling from representationtype (reificationtype) unless it is RDF.Statement...
                    String tripleRT =
                        ((RDFConcept) co).getTripleRepresentationType();
                    if (tripleRT != null) {
                        nstyles =
                            excavateStyleFromType(
                                tripleRT,
                                RDFS.subClassOf,
                                new Stack());
                        nstyles.addAll(nnstyles);
                    } else
                        nstyles = nnstyles;
                } else if (co.getType() != null)
                    nstyles =
                        excavateStyleFromType(
                            co.getType(),
                            RDFS.subClassOf,
                            new Stack());

                if (nstyles != null)
                    if (styles.isEmpty())
                        styles = nstyles;
                    else
                        styles.addAll(nstyles);
            }
        }

        //    if (styles.isEmpty())
        //      styles.add(getDefaultFor(ol));

        return styles;
    }

    public Object getAttributeValue(
        List styles,
        String attribute,
        Object def) {
        Iterator it = styles.iterator();
        while (it.hasNext()) {
            List attributes =
                ((RDFComponent) it.next()).getAttributeEntry(attribute);
            Iterator it2 = attributes.iterator();
            while (it2.hasNext()) {
                RDFAttributeEntry rae = (RDFAttributeEntry) it2.next();
                Object o;
                if (def != null)
                    o = rae.getValue(def.getClass());
                else
                    o = rae.getValueObject();
                if (o != null)
                    return o;
            }
        }

        return def;
    }

    /** Help function that extracts the first style it can find from a
     *  stmtIterator with style resources as objects of the statements therein.
     *
     *  @see #getStyle(String)
     */
    protected Vector excavateStyles(StmtIterator stmts) {
    	Vector styles = new Vector();
    	if (stmts != null) {
    		while (stmts.hasNext()) {
    			String str = stmts.nextStatement().getResource().getURI();
    			Tracer.debug("found one style has string, " + str);
    			RDFComponent style = getStyle(str);
    			if (style != null) {
    				styles.add(style);
    			}
    		}
    	}
        return styles;
    }

    /** Retrieves a style given a identifier for it, that is from
     *  a URI it ask the ResourceStore for a ResourceStyle.
     *  It might already exist in the cache.
     *  @see se.kth.cid.component.ResourceStore#getAndReferenceStyle(URI)
     */
    protected RDFComponent getStyle(String strStyle) {
        try {
            RDFComponent resource = (RDFComponent) store.getAndReferenceConcept(new URI(strStyle));
            return resource;
        } catch (ComponentException ce) {} catch (URISyntaxException mue) {}
        return null;
    }

    /** Tries to find a class-styling from the given style, that is
     *  it first checks the type itself, then ask all it's superclasses
     *  in a recursive manner.
     *  This function is also responsible from caching a type2style hashtable.
     *
     *  @param strtype the type given as a String, should be a URI.
     *  @param superClassProperty tells which property to use when looking for 
     *          superclasses of this type, (typically rdfs:subClassOf or rdfs:subPropertyOf).
     *  @return a ResourceStyle telling which class-styling this type has.
     */
    protected Vector excavateStyleFromType(
        String strtype,
        Property superClassProperty,
        Stack stack) {
        if (strtype == null) {
            return new Vector();
        }
        if (stack.contains(strtype))
            return new Vector();

        //Check if we've cached the class-styling-stack for this type
        //--------------------
        Vector styles = (Vector) type2styles.get(strtype);
        if (styles != null)
            return styles;

        //Check this type for class styling and recursively it's superclasses.
        //The result is registered in type2styles.
        //------------------
        RDFComponent ty = getRDFResource(strtype);

        stack.push(strtype);
        styles = new Vector();

        if (ty != null) {
            //Check for class styling of this type.
            //--------------------
            Vector nstyles = excavateStyles(ty.getProperties(CV.styleClass));
            if (nstyles != null)
                styles.addAll(nstyles);

            //Check for superclasses for this type(class),
            //add all their style-stacks to this types style-stack.
            //--------------------------
            StmtIterator si = ty.getProperties(superClassProperty);
            if (si != null) {
            	while (si.hasNext()) {
            		String st = si.nextStatement().getResource().getURI();
            		nstyles = excavateStyleFromType(st, superClassProperty, stack);
            		styles.addAll(nstyles);
            	}
            }
        }

        type2styles.put(strtype, styles);
        stack.pop();

        return styles;
    }

    protected RDFComponent getRDFResource(String uri) {
        try {
            Concept concept =
                store.getAndReferenceConcept(new URI(uri));
            if (concept instanceof RDFComponent)
                return (RDFComponent) concept;
        } catch (ComponentException ce) {} catch (URISyntaxException mue) {}

        return null;
    }

    //Do something about defaults, not needed since defaults controlled in code....
    //i.e. there is a default value in the getAttribute call.
    public void installDefaults() {
        try {
            URI reuri =	URI.create("urn:path:/org/conzilla/builtin/types/rdf/rdfs/Resource");
            //resourcestyle = (RDFConcept) store.getContainerManager().createConcept(reuri, null);
            resourcestyle = (RDFConcept) store.getComponentManager().createConcept(reuri);
            store.getCache().referenceComponent(resourcestyle);

            resourcestyle.addAttributeEntry(
                CV.boxStyle.getURI(),
                "rectangle");
            resourcestyle.addAttributeEntry(
                CV.boxFilled.getURI(),
                new Boolean("false"));
            resourcestyle.addAttributeEntry(
                CV.boxBorderStyle.getURI(),
                "continuous");
            resourcestyle.addAttributeEntry(
                CV.boxBorderThickness.getURI(),
                new Integer("1"));

            URI pruri = URI.create("urn:path:/org/conzilla/builtin/types/rdf/rdfs/Property");
            //propertystyle = (RDFConcept) store.getContainerManager().createConcept(pruri, null);
            propertystyle = (RDFConcept) store.getComponentManager().createConcept(pruri);
            store.getCache().referenceComponent(propertystyle);

            propertystyle.addAttributeEntry(
                CV.lineStyle.getURI(),
                "continuous");
            propertystyle.addAttributeEntry(
                CV.lineThickness.getURI(),
                new Integer(1));
            propertystyle.addAttributeEntry(
                CV.lineHeadStyle.getURI(),
                "varrow");
            propertystyle.addAttributeEntry(
                CV.lineHeadFilled.getURI(),
                new Boolean(false));
            propertystyle.addAttributeEntry(
                CV.lineHeadWidth.getURI(),
                new Integer(6));
            propertystyle.addAttributeEntry(
                CV.lineHeadLength.getURI(),
                new Integer(8));
        } catch (ComponentException ce) {}
    }
}

/*
  public BoxStyle getBoxStyleForDrawer(DrawerLayout rl)
  {
      return (BoxStyle) getResourceStyleForLayout(rl, BoxStyle.class);
  }

  public LineStyle getLineStyleForStatement(StatementLayout sl)
  {
      return (LineStyle) getResourceStyleForLayout(sl, LineStyle.class);
  }

  }*/

/*  protected String getDefaultType()
{
  return "urn:path:/org/conzilla/builtin/types/rdf/rdfs/Resource";
  }*/

/*
protected String getType(Resource r)
{
if (r.equals(RDFS.Resource))
    {
	Tracer.debug("The resource is the Resource resource in RDFS.");		
	return "urn:path:/org/conzilla/builtin/types/rdf/rdfs/Resource";
    }
if (r.equals(RDFS.Class))
    {
	Tracer.debug("The resource is the Class resource in RDFS.");		
	return "urn:path:/org/conzilla/builtin/types/rdf/rdfs/Class";
    }
try {
    //	    Tracer.debug("Found type "+res1.getURI());
    return r.getProperty(CV.layout).getString();
	} catch (RDFException re) {}
return null;
}
*/
/*
public String getType()
{
try {
    if (type == null)
	type = getRecursedType(totalModel.getTotalRDFModel().getResource(getResource().getURI()));
} catch (RDFException rex)
    {}
return type;
} 

public String getRecursedType(Resource res)
{
//	return "urn:path:/org/conzilla/builtin/types/UML/ClassD/concept";
//	    RDFUtil.printStatements(model, System.out);
try {
    String typ = getType(res);
    if (typ != null)
	return typ;
    Resource r = res.getProperty(RDF.type).getResource();
    while (true)
	{
	    typ = getType(r);
	    if (typ != null)
		return typ;
	    r = r.getProperty(RDFS.subClassOf).getResource();
	}
} catch (RDFException re) {}
Tracer.debug("No type found, choosing resource as default");
return getDefaultType();
}
*/
