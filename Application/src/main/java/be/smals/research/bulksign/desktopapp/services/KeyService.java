package be.smals.research.bulksign.desktopapp.services;

import java.security.PrivateKey;

public abstract class KeyService {

    public KeyService() {}

    public abstract  long getKey ();
    public abstract PrivateKey getPrivateKey ();

}
