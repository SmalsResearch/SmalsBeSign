package be.smals.research.bulksign.desktopapp.eid;

import be.smals.research.bulksign.desktopapp.eid.external.Controller;
import be.smals.research.bulksign.desktopapp.eid.external.Messages;
import be.smals.research.bulksign.desktopapp.eid.external.shared.SignRequestMessage;
import be.smals.research.bulksign.desktopapp.eid.external.shared.SignatureDataMessage;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.IntStream;

/**
 * Created by kova on 08/08/2016.
 */
public class EidService {

    //todo add code to make observable and add EIDServiceObserver

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

    //todo call to be refactored code in PcscEid
    public boolean isCardReaderConnected() {
        return true;
    }

}
