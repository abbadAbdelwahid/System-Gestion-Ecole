package gestion.ecole.controllers;

import gestion.ecole.services.UtilisateurService;
import gestion.ecole.models.Utilisateur;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

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
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gestion/ecole/main-layout.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1100, 600); // Set preferred width and height

            // Pass the role to the MainController
            MainController controller = fxmlLoader.getController();
            System.out.println(utilisateurService.getLoggedInUserRole());
            controller.setUserId(utilisateurService.getLoggedInUser().getId());
            controller.setUserName(utilisateurService.getLoggedInUser().getUsername());
            controller.setUserRole(utilisateurService.getLoggedInUserRole());


            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Gestion École");
            stage.setResizable(false); // Allow resizing if needed
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
