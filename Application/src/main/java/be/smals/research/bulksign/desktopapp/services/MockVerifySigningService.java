package be.smals.research.bulksign.desktopapp.services;

class MockVerifySigningService {

    private static MockVerifySigningService instance = new MockVerifySigningService();
    private MockVerifySigningService () {}
    public static MockVerifySigningService getInstance () {
        return instance;
    }
}
