package gestion.ecole.controllers;

import gestion.ecole.services.UtilisateurService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    @FXML
    private VBox sidebar;

    @FXML
    private Pane contentPane;

    @FXML
    private Button logoutButton;

    private final UtilisateurService utilisateurService = new UtilisateurService();
    private String userRole;
    private int userId; // To store the user ID
    @FXML
    private Label userLabel;

    private String userName;

    public void setUserName(String userName) {
        this.userName = userName;

    }


    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * This method is called by the LoginController to set the role of the logged-in user.
     */
    public void setUserRole(String role) {
        this.userRole = role;
        if (userLabel != null) {
            userLabel.setText("Bienvenue, " + userName);
        }

        // Dynamically update the sidebar based on the user's role
        updateSidebarForRole(userRole);

        // Load the default view based on the user's role
        loadDefaultView();
    }

    @FXML
    public void initialize() {
        // Placeholder for further initialization if needed.
    }

    /**
     * Dynamically updates the sidebar based on the user's role.
     */
    private void updateSidebarForRole(String role) {
        // Normalize role to lowercase
        String normalizedRole = role.toLowerCase();

        sidebar.getChildren().clear();

        switch (normalizedRole) {
            case "admin":
                sidebar.getChildren().addAll(
                        createButton("Dashboard", "/gestion/ecole/admin/DashboardView.fxml"),
                        createButton("Étudiants", "/gestion/ecole/admin/EtudiantsView.fxml"),
                        createButton("Modules", "/gestion/ecole/admin/ModulesView.fxml"),
                        createButton("Professeurs", "/gestion/ecole/admin/ProfesseursView.fxml"),
                        logoutButton
                );
                break;

            case "secretary":
                sidebar.getChildren().addAll(
                        createButton("Étudiants", "/gestion/ecole/secretaire/EtudiantsView.fxml"),
                        createButton("Modules", "/gestion/ecole/secretaire/ModulesView.fxml"),
                        logoutButton
                );
                break;

            case "professor":
                sidebar.getChildren().addAll(
                        createButton("Mes Cours", "/gestion/ecole/professeur/ModulesView.fxml"),
//                        createButton("Mes Étudiants", "/gestion/ecole/professeur/EtudiantsView.fxml"),
                        logoutButton
                );
                break;

            default:
                throw new IllegalArgumentException("Unknown role: " + role);
        }
    }
    /**
     * Handles the click on the Dashboard button.
     * Loads the appropriate view based on the user's role.
     */
    @FXML
    private void handleDashboardClick() {
        if (userRole.equals("admin")) {
            loadView("/gestion/ecole/admin/DashboardView.fxml");
        }
    }

    /**
     * Handles the click on the Étudiants button.
     */
    @FXML
    private void handleEtudiantsClick() {
        switch (userRole) {
            case "admin":
                loadView("/gestion/ecole/admin/EtudiantsView.fxml");
                break;
            case "secretaire":
                loadView("/gestion/ecole/secretaire/EtudiantsView.fxml");
                break;
            case "professeur":
                loadView("/gestion/ecole/professeur/MesEtudiantsView.fxml");
                break;
        }
    }

    /**
     * Handles the click on the Modules button.
     */
    @FXML
    private void handleModulesClick() {
        switch (userRole) {
            case "admin":
                loadView("/gestion/ecole/admin/ModulesView.fxml");
                break;
            case "secretaire":
                loadView("/gestion/ecole/secretaire/ModulesView.fxml");
                break;
            case "professeur":
                loadView("/gestion/ecole/professeur/ModulesView.fxml");
                break;
        }
    }

    /**
     * Handles the click on the Professeurs button (Admin only).
     */
    @FXML
    private void handleProfesseursClick() {
        if (userRole.equals("admin")) {
            loadView("/gestion/ecole/admin/ProfesseursView.fxml");
        }
    }


    /**
     * Creates a button for the sidebar with an action to load a specific view.
     */
    private Button createButton(String text, String fxmlPath) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: #34495e; -fx-text-fill: #ffffff; -fx-pref-width: 200;");
        button.setOnAction(event -> {
            loadView(fxmlPath);
            highlightButton(button); // Highlight the clicked button
        });
        return button;
    }

    /**
     * Highlights the clicked button in the sidebar.
     */
    private void highlightButton(Button button) {
        sidebar.getChildren().forEach(node -> {
            if (node instanceof Button) {
                node.setStyle("-fx-background-color: #34495e; -fx-text-fill: #ffffff; -fx-pref-width: 200;");
            }
        });
        button.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: #ffffff; -fx-pref-width: 200;");
    }

    /**
     * Loads a specific view into the content pane.
     */
    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Pane newView = loader.load();

            // Dynamically pass userId to controllers implementing UserAwareController
            Object controller = loader.getController();
            if (controller instanceof UserAwareController) {
                System.out.println("hi "+userId);
                ((UserAwareController) controller).setUserId(userId);
            }

            // Replace content pane with the new view
            contentPane.getChildren().clear();
            contentPane.getChildren().add(newView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /**
     * Handles the logout button click and redirects to the login page.
     */
    @FXML
    private void handleLogoutClick() {
        utilisateurService.logout(); // Clear the logged-in user state
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gestion/ecole/login.fxml"));
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(fxmlLoader.load()));
            stage.setTitle("Login - Gestion École");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the default view based on the user's role.
     */
    private void loadDefaultView() {
        // Trigger the appropriate button for the default view based on the user role
        if ("admin".equals(userRole)) {
            triggerButtonClick(sidebar.getChildren().get(0)); // Dashboard button for admin
        } else if ("secretary".equals(userRole)) {
            triggerButtonClick(sidebar.getChildren().get(0)); // Étudiants button for secretary
        } else if ("professor".equals(userRole)) {
            triggerButtonClick(sidebar.getChildren().get(0)); // Mes Cours button for professor
        }
    }

    /**
     * Programmatically triggers a button click to load the default view.
     */
    private void triggerButtonClick(javafx.scene.Node node) {
        if (node instanceof Button) {
            ((Button) node).fire();
        }
    }
}
