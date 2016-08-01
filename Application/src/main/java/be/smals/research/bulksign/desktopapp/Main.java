package be.smals.research.bulksign.desktopapp;


import java.util.stream.IntStream;

/**
 * Created by kova on 26/07/2016.
 */
public class Main extends Application{

    public static void main(String[] args) {
        printCount(10);
        System.out.println("Hello Carlos!");
        printCount(10);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        GridPane root = FXMLLoader.load(getClass().getResource("be/smals/research/bulksign/desktopapp/views/main.fxml"));
        primaryStage.setTitle("BulkSign Desktop");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a file");

        Button selectFileButton = new Button("Select a file...");

        root.getChildren().addAll(selectFileButton);

        selectFileButton.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(final ActionEvent e) {
                        File file = fileChooser.showOpenDialog(primaryStage);
                        if (file != null) {
                            System.out.println("File Selected !");
                        }
                    }
                });

        primaryStage.setScene(new Scene(root, 800, 480));
        primaryStage.show();
    }
}
