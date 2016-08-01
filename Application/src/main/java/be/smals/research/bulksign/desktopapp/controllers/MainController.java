package be.smals.research.bulksign.desktopapp.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileInputStream;

public class MainController {

    private GridPane root;
    private FileChooser fileChooser;
    private Button signFileButton;

    public MainController () {

    }

    @FXML
    private void handleSignFileButtonAction(ActionEvent event) {
        FileInputStream[] input = new FileInputStream[1];
    }
}
