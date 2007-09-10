/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.test;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import javax.swing.JFrame;

import se.kth.cid.component.ComponentException;
import se.kth.cid.component.InvalidURIException;
import se.kth.cid.component.ResourceStore;
import se.kth.cid.component.cache.ComponentCache;
import se.kth.cid.component.cache.SoftCache;
import se.kth.cid.concept.Concept;
import se.kth.cid.conzilla.identity.ResolverManager;
import se.kth.cid.conzilla.map.MapDisplayer;
import se.kth.cid.conzilla.map.MapScrollPane;
import se.kth.cid.conzilla.map.MapStoreManager;
import se.kth.cid.conzilla.properties.GlobalConfig;
import se.kth.cid.identity.MalformedURIException;
import se.kth.cid.identity.URI;
import se.kth.cid.identity.URIClassifier;
import se.kth.cid.identity.pathurn.ResolveException;
import se.kth.cid.identity.pathurn.ResolverTable;
import se.kth.cid.identity.pathurn.TableResolver;
import se.kth.cid.layout.ConceptLayout;
import se.kth.cid.layout.ContextMap;
import se.kth.cid.layout.StatementLayout;
import se.kth.cid.rdf.CV;
import se.kth.cid.rdf.RDFComponent;
import se.kth.cid.rdf.RDFContainerManager;
import se.kth.cid.rdf.RDFModel;
import se.kth.cid.rdf.style.RDFStyleManager;
import se.kth.cid.style.BoxStyle;
import se.kth.cid.util.Tracer;

import com.hp.hpl.jena.rdf.model.RDFException;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class RDFLoadTest {
    public static void main(String[] argv) throws ResolveException, MalformedURIException, ComponentException {
        TableResolver resolver = new TableResolver();
        //	resolver.addPath("/org/conzilla/builtin", 
        //			 URIClassifier.parseValidURI("res:/components/"),
        //			 MIMEType.XML);
        GlobalConfig config = GlobalConfig.getGlobalConfig();
        ResolverManager resolverManager = new ResolverManager();
        //DefaultComponentHandler handler = new DefaultComponentHandler(resolverManager.getResolver());

        ComponentCache cache = new SoftCache();
        RDFContainerManager containerManager = new RDFContainerManager(cache);
        ResourceStore store =
            new ResourceStore(
                resolverManager.getResolver(),
                cache,
                containerManager);
        RDFStyleManager styleManager = new RDFStyleManager(store);
        
        URI resolvURI = config.getURI("conzilla.resolver.local");
        if (resolvURI != null) {
            //System.out.println("inne i if:en:URI= "+resolvURI.toString());
            resolverManager.addTable(new ResolverTable(resolvURI));
        }
        resolvURI = config.getURI("conzilla.resolver.global");
        if (resolvURI != null) {
            //System.out.println("Inne i if:en 2");
            resolverManager.addTable(new ResolverTable(resolvURI));
        }

        URI muri = URIClassifier.parseValidURI("file:///tmp/model1");
        RDFModel model = (RDFModel) containerManager.createContainer(muri, muri);

        URI muri2 = URIClassifier.parseValidURI("file:///tmp/model2");
        RDFModel model2 = (RDFModel) containerManager.createContainer(muri2, muri2);

        containerManager.setCurrentConceptContainer(model);

        try {
            ContextMap cMap =
                (ContextMap) containerManager.createContextMap(URIClassifier.
                        parseValidURI("http://www.nada.kth.se/~matthias/conceptmap1"));

            Concept ne1 = containerManager.createConcept(
                    URIClassifier.parseValidURI("http://www.nada.kth.se/~matthias/concept1"), null);
            Concept ne2 = containerManager.createConcept(
                    URIClassifier.parseValidURI("http://www.nada.kth.se/~amb"),null);

            Concept rel = containerManager.createConcept(
                    URIClassifier.parseValidURI(
                        "http://www.nada.kth.se/~matthias/relation1"),
                    null);
            rel.createTriple(
                ne2.getURI(),
                "http://www.conzilla.org/relations/supervisorfor",
                ne1.getURI(),
                false);
            cache.referenceComponent(ne1);
            cache.referenceComponent(ne2);
            cache.referenceComponent(rel);
            cache.referenceComponent(cMap);

            ConceptLayout ns1 = cMap.addConceptLayout(ne1.getURI());
            ConceptLayout ns2 = cMap.addConceptLayout(ne2.getURI());
            StatementLayout as =
                cMap.addStatementLayout(
                    rel.getURI(),
                    ne2.getURI(),
                    ne1.getURI());

            try {
                Resource style = model2.createResource("http://blaj/boxstyle");
                model2.add(
                    model2.createStatement(
                        style,
                        CV.boxStyle,
                        BoxStyle.boxTypeNames[BoxStyle.UPPER_FIVE]));
                model2.add(
                    model2.createStatement(
                        style,
                        CV.lineStyle,
                        model2.createLiteral("dashed")));

                Resource hepp = ((RDFComponent) ne2).getResource();
                Resource cls1 = model2.createResource("http://blaj/cls1");
                Resource cls2 = model2.createResource("http://blaj/cls1");
                model2.add(model2.createStatement(cls1, RDFS.subClassOf, cls2));
                model2.add(model2.createStatement(hepp, RDF.type, cls1));
                model2.add(
                    model2.createStatement(
                        cls2,
                        se.kth.cid.rdf.CV.styleClass,
                        style));
                Resource prop =
                    model2.getProperty(rel.getTriple().predicateURI());
                model2.add(
                    model2.createStatement(
                        prop,
                        se.kth.cid.rdf.CV.styleClass,
                        style));
                model2.setEdited(true);
            } catch (Exception e) {
                e.printStackTrace();
                Tracer.debug("oj nu gick det fel, " + e.getMessage());
            }

            ns1.setBoundingBox(new ContextMap.BoundingBox(10, 10, 50, 20));
            ns1.setBodyVisible(true);
            if (!ns1.getBodyVisible())
                Tracer.debug("What the fuck!!!!!");
            ns2.setBoundingBox(new ContextMap.BoundingBox(10, 100, 50, 20));
            ns2.setBodyVisible(true);
            ContextMap.Position[] pos = new ContextMap.Position[2];
            pos[0] = new ContextMap.Position(35, 30);
            pos[1] = new ContextMap.Position(35, 100);
            as.setLine(pos);

            final MapDisplayer mapDisp =
                new MapDisplayer(
                    new MapStoreManager(
                        URIClassifier.parseValidURI(cMap.getURI()),
                        store,
                        styleManager, null));

            JFrame frame = new JFrame("MapDisplayer");

            final MapScrollPane pane = new MapScrollPane(mapDisp);

            mapDisp.setScale(2);
            frame.getContentPane().add(pane);
            frame.setSize(440, 440);
            frame.setLocation(100, 100);
            frame.show();

            model.write(
                new PrintWriter(new FileOutputStream("/tmp/model1.xml")));
        } catch (RDFException re) {
            Tracer.debug("hmm3, " + re.getMessage());
        } catch (FileNotFoundException fos) {
            Tracer.debug("hmm4, " + fos.getMessage());
        } catch (ComponentException ce) {
            Tracer.debug("hmm, " + ce.getMessage());
        } catch (InvalidURIException iue) {
            Tracer.debug("hmm2, " + iue.getMessage());
        }

    }
}
