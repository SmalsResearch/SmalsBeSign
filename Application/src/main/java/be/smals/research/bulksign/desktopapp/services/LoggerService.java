package be.smals.research.bulksign.desktopapp.services;

import be.fedict.commons.eid.client.spi.Logger;

/**
 * Created by cea on 25/08/2016.
 */
public class LoggerService implements Logger {
    @Override
    public void error(String s) {
        System.out.println("ERROR // "+s);
    }

    @Override
    public void debug(String s) {
        System.out.println("DEBUG // "+s);
    }
}
