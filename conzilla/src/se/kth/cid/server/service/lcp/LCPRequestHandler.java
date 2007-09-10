/*  $Id$
 *
 *  Copyright (c) 1999, KMR group at KTH (Royal Institute of Technology)
 *  Licensed under the GNU GPL. For full terms see the file COPYRIGHT.
 */

package se.kth.cid.server.service.lcp;

//import java.io.*;
import java.io.IOException;
import java.io.InputStream;
//import java.io.OutputStream;
//import java.net.Socket;
import java.util.Iterator;
import java.util.StringTokenizer;

import com.microstar.xml.XmlException;

import se.kth.cid.component.ComponentException;
import se.kth.cid.component.ResourceStore;
import se.kth.cid.component.Container;
import se.kth.cid.rdf.RDFContainerManager;
//import se.kth.cid.component.DefaultComponentHandler;
import se.kth.cid.component.cache.ComponentCache;
import se.kth.cid.component.cache.SoftCache;
import se.kth.cid.conzilla.identity.ResolverManager;
import se.kth.cid.conzilla.install.Installer;
import se.kth.cid.conzilla.map.MapStoreManager;
import se.kth.cid.conzilla.properties.GlobalConfig;
import se.kth.cid.identity.MalformedURIException;
import se.kth.cid.identity.URI;
import se.kth.cid.identity.URIClassifier;
import se.kth.cid.identity.pathurn.ResolveException;
import se.kth.cid.identity.pathurn.ResolverTable;
//import se.kth.cid.rdf.RDFFormatHandler;
import se.kth.cid.component.tmp.TmpFormatHandler;
import se.kth.cid.server.connector.http.*;
import se.kth.cid.server.service.Request;
import se.kth.cid.server.service.RequestHandler;
import se.kth.cid.style.StyleManager;
import se.kth.cid.util.Tracer;

public class LCPRequestHandler implements RequestHandler {

    private Request request;

    private LCPResponse response;

    private HttpConnector connector;

    private int processNr;

    private static ResourceStore store;

    private static TmpFormatHandler rh;

    private static StyleManager styleManager;

    public static StyleManager getStyleManager() {
        return styleManager;
    }

    public static MapStoreManager getMapStoreManager(String URI)
        throws ComponentException {
        MapStoreManager ms =
            new MapStoreManager(
                URIClassifier.parseValidURI(URI),
                getComponentStore(),
                getStyleManager(),
                null);
        Container lc =
            getComponentStore().getAndReferenceContainer(
                URIClassifier.parseValidURI(
                    ms.getConceptMap().getLoadContainer()));
        Iterator it = lc.getURIsWithRequestedContainers().iterator();
        while (it.hasNext()) {
            Iterator itt =
                lc.getRequestedContainersForURI((String) it.next()).iterator();
            while (itt.hasNext()) {
                try {
                    getComponentStore().getAndReferenceContainer(
                        URIClassifier.parseValidURI((String) itt.next()));
                } catch (ComponentException ce) {
                    System.out.println("Fick ComponentException!");
                    //ce.printStackTrace();
                }
            }
        }
        return ms;
    }

    public LCPRequestHandler(LCPHeader lcpheader) {
    }

    public static ResourceStore getComponentStore() {
        return store;
    }

    public static TmpFormatHandler getTmpFormatHandler() {
        return rh;
    }

    public static void initConzilla()
        throws ComponentException, MalformedURIException, ResolveException {
        GlobalConfig config = GlobalConfig.getGlobalConfig();
        ResolverManager resolverManager = new ResolverManager();
        //DefaultComponentHandler handler = new DefaultComponentHandler(resolverManager.getResolver());

        ComponentCache cache = new SoftCache();
        RDFContainerManager containerManager = new RDFContainerManager(cache);
        store =
            new ResourceStore(
                resolverManager.getResolver(),
                cache,
                containerManager);
        styleManager = new se.kth.cid.rdf.style.RDFStyleManager(store);
        rh = new TmpFormatHandler();
        rh.setComponentStore(store);
        //handler.addFormatHandler(rh);
        //System.out.println("kollar om rh == null i initConzilla");
        //System.out.println(rh == null);
        try {
            URI configURI;
            configURI = Installer.getConfigURI();
            //System.out.println("configURI="+configURI.toString());
            InputStream is =
                configURI.getJavaURL().openConnection().getInputStream();
            config.loadConfig(is);
            is.close();
        } catch (IOException e) {
            //throw new ConzillaAppEnv.InstallException();
            System.out.println("Det verkar inte fungera");
        }

        try {
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
            String strtcont =
                GlobalConfig.getGlobalConfig().getProperty(
                    "conzilla.startcontainers");
            String tmpString;
            StringTokenizer st = new StringTokenizer(strtcont, ",");
            //System.out.println("antal tokens = "+st.countTokens());
            while (st.hasMoreTokens()) {
                tmpString = st.nextToken();
                store.getAndReferenceContainer(
                    URIClassifier.parseValidURI(tmpString));
            }
            //store.getAndReferenceConcept(URIClassifier.parseValidURI("urn:path:/org/conzilla/builtin/maps/test"));

            /*resolvURI = config.getURI("conzilla.startcontainer");
              if (resolvURI != null){
              System.out.println("Inne i if:en 3");
              resolverManager.addTable(new ResolverTable(resolvURI));
              }*/

        } catch (ResolveException e) {
            System.out.println(
                "Ignoring resolver file: "
                    + config.getURI("conzilla.resolver.global")
                    + ", as:\n "
                    + e.getMessage());
        }

        StringTokenizer st =
            new StringTokenizer(
                GlobalConfig.getGlobalConfig().getProperty(
                    "conzilla.style.containers"),
                ",");

        while (st.hasMoreTokens()) {
            try {
                String container = st.nextToken();
                URI containerURL = URIClassifier.parseURI(container);
                //containers.add(containerURL);
                store.getAndReferenceContainer(containerURL);
            } catch (MalformedURIException me) {
                Tracer.debug("Error in StyleURI...");
                /*ErrorMessage.showError(
                		       "Invalid style container URI",
                		       "Invalid style container URI in conzilla.properties\n"
                		       + container
                		       + ":\n "
                		       + me.getMessage(),
                		       me,
                		       null);*/
            }
        }
    }

    public Object process(Request request) {
        String resp = "";
        String req = request.getLCPHeader().getTypeOfRequest().toLowerCase();
        try {
            if ("getmap".equals(req)) {
                response =
                    new LCPMapResponse(
                        LCPRequestHandler.getMapStoreManager(request.getURI()),
                        request);
            } else if ("getneighbourhood".equals(req)) {
                response = new LCPNeighbourhoodResponse(request);
            }
            resp = (String) response.createResponse();
        } catch (Exception e) {
            Tracer.debug("Failed to process request! Cause:" + e.getMessage());
            e.printStackTrace();
            return "ERROR!!!";
        }
        Tracer.debug("Responding with:\r\n" + resp);
        return resp;
    }
}
