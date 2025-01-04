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
            Utilisateur utilisateur = utilisateurService.getUserByUsername(username);
            if (utilisateur != null) {
                loadMainView(utilisateur.getRole());
            } else {
                errorLabel.setText("Erreur interne: utilisateur introuvable.");
            }
        } else {
            errorLabel.setText("Nom d'utilisateur ou mot de passe incorrect.");
        }
    }

    private void loadMainView(String role) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gestion/ecole/admin-interface.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Dashboard - Gestion École");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
