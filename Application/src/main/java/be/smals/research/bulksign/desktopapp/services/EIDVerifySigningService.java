package be.smals.research.bulksign.desktopapp.services;

class EIDVerifySigningService {

    private static EIDVerifySigningService instance = new EIDVerifySigningService();
    private EIDVerifySigningService() {}
    public static EIDVerifySigningService getInstance () {
        return instance;
    }
}
