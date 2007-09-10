/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.rdf;
import java.net.URI;
import java.net.URISyntaxException;

import se.kth.cid.component.AttributeEntry;
import se.kth.cid.component.ReadOnlyException;
import se.kth.cid.concept.InvalidTypeException;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFException;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;


public class RDFAttributeEntry implements AttributeEntry
{
  RDFModel model;
  RDFComponent component;
  Statement s;
  Object o;

  public RDFAttributeEntry(RDFModel model, RDFComponent component, Statement stmt)
  {
    this.component = component;
    this.model = model;
    this.s = stmt;
  }

  public RDFComponent getComponent() {
      return component;
  }
  
  public se.kth.cid.component.Container getContainer()
  {
    return model;
  }

  public String getAttribute()
  {
      return s.getPredicate().getURI();
  }

  public Object getValue(Class cls)
  {
    try {
      if (cls == Integer.class)
	return new Integer(s.getInt());
      else if (cls == Double.class)
	return new Double(s.getDouble());
      else if (cls == Boolean.class)
	return new Boolean(s.getBoolean());
      else if (cls == String.class)
	return s.getString();
      else if (cls == URI.class)
	  {
	      String uri = s.getResource().getURI();
	      if (uri != null)
		  return new URI(uri);
	  }
      else if (cls == Resource.class)
	  return s.getResource();
    } catch (RDFException re) {
    } catch (URISyntaxException muri) {}
    return null;
  }

  public String getValue() {
      RDFNode object = s.getObject();
      return object instanceof Resource ? ((Resource) object).getURI() : ((Literal) object).getString();
  }

  public RDFNode getObject() {
      return s.getObject();
  }
  
  public Statement getStatement() {
	  return s;
  }
  
  public void setValueObject(Object o) {
      if (o instanceof String) {
          RDFNode node = s.getObject();
          if (node instanceof Literal) {
              String lang = ((Literal) node).getLanguage();
              if (lang != null && lang.length() != 0) {
                s = s.changeObject(s.getModel().createLiteral((String) o, lang));
                component.setEdited(true);
                model.setEdited(true);
                return;
              }
          }
      }
      s = s.changeObject(o); 
      component.setEdited(true);
      model.setEdited(true);
  }
 
  public void remove() throws ReadOnlyException{
      try {
          if (model.isEditable()) {
              model.remove(s);
              model.setEdited(true);
              component.setEdited(true);
              return;
          } else {
              throw new ReadOnlyException("Container is read only!");
          }
        } catch (RDFException re) {
            re.printStackTrace();
        }
        throw new RuntimeException("Something bad happend... in RDFAttributeEntry");
  }
  
  public Object getValueObject()
  {
    if (o != null)
      return o;
    
    try {
      o = new Double(s.getDouble());
      return o;
    } catch (RDFException re) {
    } catch (NumberFormatException ne) {}
    
    try {
      o = new Integer(s.getInt());
      return o;
    } catch (RDFException re) {
    } catch (NumberFormatException ne) {}

    try {
      o = new Boolean(s.getBoolean());
      return o;
    } catch (RDFException re) {}

    try {
      o = s.getString();
      return o;
    } catch (RDFException re) {}      

    try {
	String uri = s.getResource().getURI();
	if (uri != null)
	    return new URI(uri);
    } catch (RDFException re) {
    } catch (URISyntaxException mui) {}     

    try {
	o = s.getResource();
	return o;
    } catch (RDFException re) {}      

    return null;
  }
  
  public int getIntValue() throws InvalidTypeException
  {
    try {
      return s.getInt();
    } catch (RDFException re)
      {
          throw new InvalidTypeException(re.getMessage());
      } catch (NumberFormatException ne) {
          throw new InvalidTypeException(ne.getMessage());
      }
  }

  public double getDoubleValue() throws InvalidTypeException
  {
    try {
      return s.getDouble();
    } catch (RDFException re)
      {
    	throw new InvalidTypeException(re.getMessage());
      } catch (NumberFormatException ne) {
            throw new InvalidTypeException(ne.getMessage());
      }
  }

  public boolean getBooleanValue() throws InvalidTypeException
  {
    try {
      return s.getBoolean();
    } catch (RDFException re)
      {
	throw new InvalidTypeException(re.getMessage());
      }
  }
  public String getStringValue() throws InvalidTypeException
  {
    try {
      return s.getString();
    } catch (RDFException re)
      {
	throw new InvalidTypeException(re.getMessage());
      }
  }

  public URI getURIValue() throws InvalidTypeException
  {
    try {
	String uri = s.getResource().getURI();
	if (uri != null)
	    return new URI(uri);
    } catch (RDFException re) {
    } catch (URISyntaxException mui) {}

    throw new InvalidTypeException("This attribute doesn't have an URI as value.");
  }

  public Resource getResourceValue() throws InvalidTypeException
  {
    try {
	return s.getResource();
    } catch (RDFException re) {
      throw new InvalidTypeException(re.getMessage());
    }
  }

  public static void removeStatement(RDFModel m, RDFComponent c, Resource r, String attribute, Object v)
  {
    try {
      Property p = r.getModel().getProperty(attribute);

      if (v instanceof Integer)
	m.remove(m.createStatement(r, p, ((Integer) v).intValue()));
      else if (v instanceof Double)
	m.remove(m.createStatement(r, p, ((Double) v).doubleValue()));
      else if (v instanceof Boolean)
	m.remove(m.createStatement(r, p, ((Boolean) v).booleanValue())); 
      else if (v instanceof String)
	m.remove(m.createStatement(r, p, ((String) v)));
      else if (v instanceof URI)
	m.remove(m.createStatement(r, p, m.getResource(((URI) v).toString())));
      else if (v instanceof RDFNode)
	m.remove(m.createStatement(r, p, (Resource) v));
      c.setEdited(true);
      m.setEdited(true);

    } catch (RDFException re) {}
  }
  
  public static RDFAttributeEntry addStatement(RDFModel m, RDFComponent c, Resource r, String attribute, Object v)
  {
      Statement s = null;
    try {
      Property p = m.createProperty(attribute);
      Resource resource = m.createResource(r.getURI());
      if (v instanceof se.kth.cid.component.Resource) {
        Resource object = null;
        
        if (v instanceof RDFComponent) {
            object = ((RDFComponent) v).getResource(); 
        } else {
            object = m.createResource(((se.kth.cid.component.Resource) v).getURI());
        }
          s = m.createStatement(resource, p, object);
      } else if (v instanceof Integer)
	       s = m.createStatement(resource, p, ((Integer) v).intValue());
      else if (v instanceof Double)
	       s = m.createStatement(resource, p, ((Double) v).doubleValue());
      else if (v instanceof Boolean)
	       s = m.createStatement(resource, p, ((Boolean) v).booleanValue()); 
      else if (v instanceof String)
	       s = m.createStatement(resource, p, ((String) v));
      else if (v instanceof URI)
	       s = m.createStatement(resource, p, m.getResource(((URI) v).toString()));
      else if (v instanceof Resource)
	       s = m.createStatement(resource, p, (Resource) v);

    } catch (RDFException re) {}
    if (s != null) {
        m.add(s);
        c.setEdited(true);
        m.setEdited(true);
        return new RDFAttributeEntry(m,c,s);
    }
    return null;
  }
}
       
