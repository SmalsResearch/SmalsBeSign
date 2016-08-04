package be.smals.research.bulksign.desktopapp.utilities;

public class SigningOutput {

    public String masterDigest;
    public byte[] signature;

    public SigningOutput () {}
    public SigningOutput (String masterDigest, byte[] signature) {
        this.masterDigest   = masterDigest;
        this.signature      = signature;
    }
}
