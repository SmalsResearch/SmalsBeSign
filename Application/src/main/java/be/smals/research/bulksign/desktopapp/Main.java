package be.smals.research.bulksign.desktopapp;


import be.smals.research.bulksign.desktopapp.signverify.BatchSignature;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sun.security.pkcs11.wrapper.PKCS11Exception;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Created by kova on 26/07/2016.
 */
public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        GridPane root = FXMLLoader.load(getClass().getClassLoader().getResource("views/main.fxml"));
        primaryStage.setTitle("BulkSign Desktop");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a file");

        Button selectFileButton = new Button("Select a file...");

        root.getChildren().addAll(selectFileButton);

        selectFileButton.setOnAction(
                e -> {
                    File file = fileChooser.showOpenDialog(primaryStage);
                    if (file != null) {
                        FileInputStream[] input = new FileInputStream[1];
                        try {
                            input[0] = new FileInputStream(file);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }

                        try {
                            byte[] signature = BatchSignature.main(input);
                            outputSignature (signature);

                        } catch (IOException e1) {
                            e1.printStackTrace();
                        } catch (PKCS11Exception e1) {
                            e1.printStackTrace();
                        }

                    } else {
                        System.out.println("ERROR - No file found.");
                    }
                });

        primaryStage.setScene(new Scene(root, 800, 480));
        primaryStage.show();
    }

    private void outputSignature (byte[] signature) {
        /* Display the signature length and value */
        System.out.print("Length of generated signature (in bytes):");
        System.out.println(signature.length);
        System.out.println("");
        System.out.print("Value of generated signature: ");

        for (int i = 0; i < signature.length; i++) {
            System.out.print(signature[i]);
            System.out.print(" ");
        }
        System.out.println("");
    }
}
