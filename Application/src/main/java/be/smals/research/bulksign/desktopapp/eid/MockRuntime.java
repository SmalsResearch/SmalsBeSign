package be.smals.research.bulksign.desktopapp.eid;

import be.fedict.eid.applet.Applet;
import be.smals.research.bulksign.desktopapp.eid.external.Runtime;

import java.net.URL;

/**
 * Created by kova on 08/08/2016.
 */
public class MockRuntime implements Runtime {

    @Override
    public void gotoTargetPage() {
        System.out.println(">called gotoTargetPage");
    }

    @Override
    public boolean gotoCancelPage() {
        System.out.println(">called gotoCancelPage");
        return false;
    }

    @Override
    public void gotoAuthorizationErrorPage() {
        System.out.println(">called gotoAuthorizationErrorPage");
    }

    @Override
    public URL getDocumentBase() {
        System.out.println(">called getDocumentBase");
        return null;
    }

    @Override
    public String getParameter(String name) {
        System.out.println(">called getParameter: " + name);
        return null;
    }

    @Override
    public Applet getApplet() {
        System.out.println(">called getApplet");
        return null;
    }

}
