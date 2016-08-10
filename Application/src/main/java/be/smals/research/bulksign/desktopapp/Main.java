package be.smals.research.bulksign.desktopapp;


import be.smals.research.bulksign.desktopapp.controllers.MainController;
import be.smals.research.bulksign.desktopapp.ui.StatusBar;
import be.smals.research.bulksign.desktopapp.utilities.Settings;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.security.Security;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);

//        SigningService signer = null;
//        Controller controller = null;
//        try {
//            signer = new SigningService();
//
//
//            controller = new Controller(new View() {
//                @Override
//                public void addDetailMessage(String detailMessage) {
//                    System.out.println("*detail message: "+detailMessage);
//                }
//
//                @Override
//                public void setStatusMessage(Status status, Message.MESSAGE_ID messageId) {
//                    System.out.print("*status message: "+status);
//                    System.out.println(" / " + messageId);
//                }
//
//                @Override
//                public boolean privacyQuestion(boolean includeAddress, boolean includePhoto, String identityDataUsage) {
//                    System.out.println("*called privacyquestion");
//                    return false;
//                }
//
//                @Override
//                public Component getParentComponent() {
//                    return null;
//                }
//
//                @Override
//                public void setProgressIndeterminate() {
//                    System.out.println("*progress indeterminate");
//                }
//
//                @Override
//                public void resetProgress(int max) {
//                    System.out.println("*reset progress: " + max);
//                }
//
//                @Override
//                public void increaseProgress() {
//                    System.out.println("*progress increased");
//                }
//
//                @Override
//                public void confirmAuthenticationSignature(String message) {
//                    System.out.println("*confirm: " + message);
//                }
//
//                @Override
//                public int confirmSigning(String description, String digestAlgo) {
//                    int result = 0;
//                    System.out.println("*confirming signing of [" + description + "] and algo [" + digestAlgo + "] with answer " + result);
//                    return result;
//                }
//            }, new Runtime() {
//                @Override
//                public void gotoTargetPage() {
//                    System.out.println(">called gotoTargetPage");
//                }
//
//                @Override
//                public boolean gotoCancelPage() {
//                    System.out.println(">called gotoCancelPage");
//                    return false;
//                }
//
//                @Override
//                public void gotoAuthorizationErrorPage() {
//                    System.out.println(">called gotoAuthorizationErrorPage");
//                }
//
//                @Override
//                public URL getDocumentBase() {
//                    System.out.println(">called getDocumentBase");
//                    return null;
//                }
//
//                @Override
//                public String getParameter(String name) {
//                    System.out.println(">called getParameter: "+name);
//                    return null;
//                }
//
//                @Override
//                public Applet getApplet() {
//                    System.out.println(">called getApplet");
//                    return null;
//                }
//            }, new Message(Locale.US));
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (PKCS11Exception e) {
//            e.printStackTrace();
//        }
//
//        /*PcscEid eidReader = new PcscEid(new View() {
//            @Override
//            public void addDetailMessage(String detailMessage) {
//                System.out.println(detailMessage);
//            }
//
//            @Override
//            public void setStatusMessage(Status status, Message.MESSAGE_ID messageId) {
//                System.out.print(status);
//                System.out.println("  "+messageId);
//            }
//
//            @Override
//            public boolean privacyQuestion(boolean includeAddress, boolean includePhoto, String identityDataUsage) {
//                System.out.println("called provicyquestion");
//                return false;
//            }
//
//            @Override
//            public Component getParentComponent() {
//                return null;
//            }
//
//            @Override
//            public void setProgressIndeterminate() {
//                System.out.println("progress indeterminate");
//            }
//
//            @Override
//            public void resetProgress(int max) {
//                System.out.println("reset progress: "+max);
//            }
//
//            @Override
//            public void increaseProgress() {
//                System.out.println("progress increased");
//            }
//
//            @Override
//            public void confirmAuthenticationSignature(String message) {
//                System.out.println("confirm: "+message);
//            }
//
//            @Override
//            public int confirmSigning(String description, String digestAlgo) {
//                int result = 0;
//                System.out.println("confirming signing of ["+description+"] and algo ["+digestAlgo+"] with answer "+result);
//                return result;
//            }
//        }, new Message(Locale.US));*/
//        int inputlength = 160;
//        byte[] input = new byte[inputlength];
//        IntStream.range(0,inputlength).forEach(i-> input[i] = (byte)i);
//        byte[] digest = getSha1(input);
//        System.out.println("INPUT: "+Arrays.toString(digest));
//        try {
//            System.out.println("TRYING TO SIGN! ");
//            InputStream[] inputs = new InputStream[1];
//            AuthSignRequestMessage request = new AuthSignRequestMessage(digest,"SHA-1","<-please sign->",false);
//            SignRequestMessage request1 = new SignRequestMessage(digest,"SHA-1","<--now sign for real-->",false,false,false);
//            //AuthSignResponseMessage authSignResponseMessage = (AuthSignResponseMessage) controller.performAuthnSignOperation(request);
//            //inputs[0] = new ByteArrayInputStream(bts);
//            //Settings.getInstance().setSigner(Settings.Signer.EID);
//            byte[] result = null;//authSignResponseMessage.signatureValue;//signer.sign(inputs);
//            //byte[] result = eidReader.sign(bts, "SHA-1-PSS",false);
//            System.out.println("OUTPUT: "+Arrays.toString(result));
//            //System.out.println("LENGTH: "+result.length);
//
//            SignatureDataMessage signatureDataMessage = (SignatureDataMessage) controller.performEidSignOperation(request1);
//            result = signatureDataMessage.signatureValue;
//            System.out.println("OUTPUT: "+Arrays.toString(result));
//            System.out.println("LENGTH: "+result.length);
//            System.exit(0);
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.exit(-1);
//        } /*catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (CardException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (UserCancelledException e) {
//            e.printStackTrace();
//        } */
//
//        /*
//         62 84 88 3 89 -99 103 -89 35 -33 87 -80 -126 -56 -89 56 47 -43 30 -35 -71 -8 9 -116 -75 5 -55 -63 -81 -107 -24 -2 19 58 91 -18 -73 103 -107 -108 46 10 14 100 -127 91 -107 -39 95 -126 -109 120 -113 -51 -74 0 -10 34 15 82 105 92 10 -73 125 16 63 75 26 119 -46 16 -87 -112 -37 13 -122 25 -53 67 15 50 -99 -80 -89 24 52 -126 108 -39 -56 -39 -11 -83 58 -22 -101 8 -74 1 -126 -67 98 125 -35 61 50 42 -94 -64 93 59 -49 -121 -32 -103 -58 -6 -25 18 36 -118 -81 61 -122 -103 13 -1 117 -58 37 -74 26 -105 7 119 -107 63 125 26 40 -23 39 -13 30 46 -100 -27 122 97 -8 -12 73 -39 -65 -29 118 -12 -124 41 127 114 94 85 107 79 -12 -15 4 17 -107 -54 -127 61 -36 -33 10 -43 -92 -8 -116 -15 123 113 58 105 73 51 -33 110 7 11 -51 87 -80 49 -80 -119 72 -17 68 -75 -78 -5 97 52 -111 -126 -3 108 -101 -88 70 -6 24 12 -29 63 -49 -27 -75 -116 -100 75 -26 81 17 34 -38 -76 13 123 -105 53 -16 85 -83 102 -23 34 69 59 -45 -32 75 -71 23 -45 30 82 -127 -62 112 -55 75 69
//
//
//         COMMAND: [0, 42, -98, -102, -81, 48, 33, 48, 9, 6, 5, 43, 14, 3, 2, 26, 5, 0, 4, 20, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127, -128, -127, -126, -125, -124, -123, -122, -121, -120, -119, -118, -117, -116, -115, -114, -113, -112, -111, -110, -109, -108, -107, -106, -105, -104, -103, -102, -101, -100, -99, -98, -97]
//
//         COMMAND: [0, 42, -98, -102, -81, 48, 33, 48, 9, 6, 5, 43, 14, 3, 2, 26, 5, 0, 4, 20, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127, -128, -127, -126, -125, -124, -123, -122, -121, -120, -119, -118, -117, -116, -115, -114, -113, -112, -111, -110, -109, -108, -107, -106, -105, -104, -103, -102, -101, -100, -99, -98, -97]
//
//
//
//         */
    }

//    private static byte[] getSha1(byte[] input) {
//        System.out.println("HASH: "+Arrays.toString(input));
//        MessageDigest digest = null;
//        try {
//            digest = MessageDigest.getInstance("SHA-1");
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//        byte[] result = digest.digest(input);
//        System.out.println("HASH: "+Arrays.toString(result));
//        return result;
//    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        FXMLLoader loader   = new FXMLLoader(getClass().getClassLoader().getResource("views/main.fxml"));
        StackPane masterPane       = loader.load();
        primaryStage.setTitle("BulkSign Desktop");

        MainController controller = loader.getController();
        controller.setStage(primaryStage);
        BorderPane root         = controller.getRoot();

        MenuBar menuBar         = new MenuBar();
        Menu fileMenu           = new Menu("File");
        Menu taskMenu           = new Menu("Task");
        Menu signerMenu         = new Menu("Signer");
        Menu helpMenu           = new Menu("Help");
        menuBar.getMenus().addAll(fileMenu, taskMenu, signerMenu, helpMenu);
        MenuItem homeMenuItem   = new MenuItem("Home");
        MenuItem exitMenuItem   = new MenuItem("Exit...");
        MenuItem signMenuItem   = new MenuItem("Sign");
        MenuItem verifyMenuItem = new MenuItem("Verify");
        MenuItem aboutMenuItem  = new MenuItem("About");
        final ToggleGroup signerGroup   = new ToggleGroup();
        RadioMenuItem eidMenuItem       = new RadioMenuItem("eID");
        RadioMenuItem mockMenuItem      = new RadioMenuItem("Mock");
        eidMenuItem.setUserData(Settings.Signer.EID);
        mockMenuItem.setUserData(Settings.Signer.MOCK);
        eidMenuItem.setToggleGroup(signerGroup);
        mockMenuItem.setToggleGroup(signerGroup);
        signerGroup.selectToggle(mockMenuItem);
        fileMenu.getItems().addAll(homeMenuItem, exitMenuItem);
        taskMenu.getItems().addAll(signMenuItem, verifyMenuItem);
        signerMenu.getItems().addAll(mockMenuItem, eidMenuItem);
        helpMenu.getItems().addAll(aboutMenuItem);
        root.setTop(menuBar);

        exitMenuItem.setOnAction(event -> {
            controller.exitMenuItemAction ();
        });
        signMenuItem.setOnAction( event -> {
            controller.signMenuItemAction ();
        });
        verifyMenuItem.setOnAction( event -> {
            controller.verifyMenuItemAction ();
        });
        signerGroup.selectedToggleProperty().addListener((ov, old_toggle, new_toggle) -> {
            if (signerGroup.getSelectedToggle() != null) {
                Settings.Signer signer = (Settings.Signer) signerGroup.getSelectedToggle().getUserData();
                Settings.getInstance().setSigner(signer);
            }
        });

        root.setBottom(new StatusBar());

        primaryStage.setScene(new Scene(masterPane, 800, 480));
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        System.exit(0);
        Platform.exit();
    }
}
