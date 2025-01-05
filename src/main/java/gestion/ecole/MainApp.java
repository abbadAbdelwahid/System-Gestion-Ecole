package gestion.ecole;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;




public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the login view as the initial scene
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gestion/ecole/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load()); // Set initial dimensions for the window

        // Configure the stage
        primaryStage.setTitle("Gestion École");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false); // Disable resizing for a cleaner interface
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
