package be.smals.research.bulksign.desktopapp.eid;

import be.fedict.eid.applet.Messages;
import be.fedict.eid.applet.Status;
import be.fedict.eid.applet.View;

import java.awt.*;

/**
 * Created by kova on 08/08/2016.
 */
public class ViewAdapter implements View {

    @Override
    public void addDetailMessage(String detailMessage) {
        System.out.println("*detail message: " + detailMessage);
    }


    @Override
    public void setStatusMessage(Status status, Messages.MESSAGE_ID messageId) {
        System.out.print("*status message: " + status);
        System.out.println(" / " + messageId);
    }

    @Override
    public boolean privacyQuestion(boolean includeAddress, boolean includePhoto, String identityDataUsage) {
        System.out.println("*called privacyquestion");
        return false;
    }

    @Override
    public Component getParentComponent() {
        return null;
    }

    @Override
    public void setProgressIndeterminate() {
        System.out.println("*progress indeterminate");
    }

    @Override
    public void resetProgress(int max) {
        System.out.println("*reset progress: " + max);
    }

    @Override
    public void increaseProgress() {
        System.out.println("*progress increased");
    }

    @Override
    public void confirmAuthenticationSignature(String message) {
        System.out.println("*confirm: " + message);
    }

    @Override
    public int confirmSigning(String description, String digestAlgo) {
        int result = 0;
        System.out.println("*confirming signing of [" + description + "] and algo [" + digestAlgo + "] with answer " + result);
        return result;
    }
}