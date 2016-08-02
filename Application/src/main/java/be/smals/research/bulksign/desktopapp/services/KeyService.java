package be.smals.research.bulksign.desktopapp.services;

import java.security.PrivateKey;

/**
 * Created by cea on 02/08/2016.
 */
public abstract class KeyService {

    public KeyService() {}

    public abstract  long getKey ();
    public abstract PrivateKey getPrivateKey ();

}
