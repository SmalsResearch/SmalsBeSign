package be.smals.research.bulksign.desktopapp.services;

import be.smals.research.bulksign.desktopapp.abstracts.Service;

/**
 * Created by cea on 09/08/2016.
 */
public class EIDService extends Service {
    private static EIDService instance = new EIDService();

    private EIDService () {}
    public static EIDService getInstance(){
        return instance;
    }
}
