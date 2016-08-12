package be.smals.research.bulksign.desktopapp.services;

import be.smals.research.bulksign.desktopapp.abstracts.Service;

/**
 * Service to access eID card and reader operations
 */
public class EIDService extends Service {
    private static EIDService instance = new EIDService();

    private EIDService () {}
    public static EIDService getInstance(){
        return instance;
    }
}
