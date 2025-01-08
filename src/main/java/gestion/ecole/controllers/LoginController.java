package gestion.ecole.controllers;

import gestion.ecole.services.UtilisateurService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button loginButton;

    private final UtilisateurService utilisateurService = new UtilisateurService();

    // Default ResourceBundle (French)
    private final ResourceBundle resourceBundle = ResourceBundle.getBundle("texts.messages", Locale.FRENCH);

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Veuillez remplir tous les champs.");
            return;
        }

        boolean isAuthenticated = utilisateurService.authenticateUser(username, password);
        if (isAuthenticated) {
            loadMainView();
        } else {
            errorLabel.setText("Nom d'utilisateur ou mot de passe incorrect.");
        }
    }

    private void loadMainView() {
        try {
            // Pass the resource bundle while loading the FXML
            FXMLLoader fxmlLoader = new FXMLLoader(
                    getClass().getResource("/gestion/ecole/main-layout.fxml"),
                    resourceBundle
            );

            Scene scene = new Scene(fxmlLoader.load(), 1100, 600); // Set preferred width and height

            // Pass the logged-in user details to the MainController
            MainController controller = fxmlLoader.getController();
//            controller.setResourceBundle(resourceBundle);
            controller.setUserId(utilisateurService.getLoggedInUser().getId());
            controller.setUserName(utilisateurService.getLoggedInUser().getUsername());
            controller.setUserRole(utilisateurService.getLoggedInUserRole());
             // Pass the ResourceBundle

            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle(resourceBundle.getString("app.title")); // Use the translated title
            stage.setResizable(false); // Allow resizing if needed
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
