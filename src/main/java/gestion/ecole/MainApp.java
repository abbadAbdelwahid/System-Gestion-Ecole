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


        ResourceBundle bundle = ResourceBundle.getBundle("texts.messages", Locale.FRENCH);

        System.out.println(bundle.getString("app.title"));



        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gestion/ecole/login.fxml"));


        Scene scene = new Scene(fxmlLoader.load());


        primaryStage.setTitle("Gestion d'École");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
