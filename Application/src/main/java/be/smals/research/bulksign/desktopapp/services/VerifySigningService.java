package be.smals.research.bulksign.desktopapp.services;

import java.io.FileInputStream;
import java.security.PublicKey;

public class VerifySigningService {

    public VerifySigningService () {}

    public boolean verifySigning (FileInputStream file, byte[] signature, String masterDigest, PublicKey key) {

        return false;
    }
}
