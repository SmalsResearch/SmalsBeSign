package be.smals.research.bulksign.desktopapp.eid;

import be.smals.research.bulksign.desktopapp.eid.external.Controller;
import be.smals.research.bulksign.desktopapp.eid.external.Messages;
import be.smals.research.bulksign.desktopapp.eid.external.shared.SignRequestMessage;
import be.smals.research.bulksign.desktopapp.eid.external.shared.SignatureDataMessage;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.IntStream;

/**
 * Created by kova on 08/08/2016.
 */
public class EidService {

    //todo add code to make observable and add EidServiceObserver

    //todo move to test code
    public static byte[] getMockInput(int length) {
        byte[] input = new byte[length];
        IntStream.range(0, length).forEach(i -> input[i] = (byte) i);
        return input;
    }



    //todo make nicer wrapper class for the result
    public static SignatureDataMessage doSignature(byte[] digest) {
        Controller controller = new Controller(new ViewAdapter(), new MockRuntime(), new Messages(Locale.US));

        System.out.println("INPUT: " + Arrays.toString(digest));
        try {
            System.out.println("TRYING TO SIGN! ");
            SignRequestMessage request1 = new SignRequestMessage(digest, "SHA-1", "<--now sign !!!!-->", false, false, false);
            SignatureDataMessage signatureDataMessage = (SignatureDataMessage) controller.performEidSignOperation(request1);
            return signatureDataMessage;
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        return null;
    }

    //todo move to util class
    public static byte[] getSha1(byte[] input) {
        System.out.println("INPUT: " + Arrays.toString(input));
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-1");
            byte[] result = digest.digest(input);
            System.out.println("HASH: " + Arrays.toString(result));
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    //todo call to be refactored code in PcscEid
    public boolean isCardReaderConnected() {
        return true;
    }

}
