package gestion.ecole;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the login view
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gestion/ecole/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load()); // No need for dimensions here; they're set in FXML

        // Configure the primary stage
        primaryStage.setTitle("Gestion École");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false); // Lock the size
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
