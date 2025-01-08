package gestion.ecole;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Set default locale to French

        ResourceBundle bundle = ResourceBundle.getBundle("texts.messages", Locale.FRENCH);

        System.out.println(bundle.getString("app.title"));



        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gestion/ecole/login.fxml"));
//        fxmlLoader.setResources(bundle); // Pass the resource bundle

        Scene scene = new Scene(fxmlLoader.load());

        // Configure the stage
        primaryStage.setTitle("Gestion d'École"); // Translate the app title
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
