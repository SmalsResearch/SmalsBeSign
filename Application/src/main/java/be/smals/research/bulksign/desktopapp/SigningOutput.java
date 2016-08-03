package be.smals.research.bulksign.desktopapp;

/**
 * Created by cea on 03/08/2016.
 */
public class SigningOutput {

    public String masterDigest;
    public byte[] signature;

    public SigningOutput () {}
    public SigningOutput (String masterDigest, byte[] signature) {
        this.masterDigest   = masterDigest;
        this.signature      = signature;
    }
}
