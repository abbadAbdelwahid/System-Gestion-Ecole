package gestion.ecole.controllers;

import gestion.ecole.services.UtilisateurService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class MainController {

    @FXML
    private VBox sidebar;

    @FXML
    private Pane contentPane;

    @FXML
    private Button logoutButton;

    @FXML
    private ChoiceBox<String> languageSelector;

    @FXML
    private Label userLabel;

    private final UtilisateurService utilisateurService = new UtilisateurService();
    private String userRole;
    private int userId;
    private String userName;
    private ResourceBundle bundle;
    private Locale currentLocale;
    private String currentFxmlPath; // Tracks the current FXML path for view reloading

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUserRole(String role) {
        this.userRole = role;
        if (userLabel != null) {
            userLabel.setText(bundle.getString("header.welcome") + ", " + userName);
        }

        updateSidebarForRole(userRole);
        loadDefaultView();
    }

    @FXML
    public void initialize() {
        currentLocale = Locale.FRENCH; // Default language
        this.bundle = ResourceBundle.getBundle("texts.messages", currentLocale);

        // Setup language selector
        languageSelector.setItems(FXCollections.observableArrayList("Français", "English","Español"));
        languageSelector.setValue("Français");
        languageSelector.setOnAction(event -> handleLanguageChange());

        updateTexts();
    }

    private void handleLanguageChange() {
        String selectedLanguage = languageSelector.getValue();

        // Determine the selected language and set the appropriate locale
        if ("English".equals(selectedLanguage)) {
            currentLocale = Locale.ENGLISH;
        } else if ("Français".equals(selectedLanguage)) {
            currentLocale = Locale.FRENCH;
        } else if ("Español".equals(selectedLanguage)) {
            currentLocale = new Locale("es", "ES"); // Spanish locale
        }

        // Load the resource bundle for the selected locale
        bundle = ResourceBundle.getBundle("texts.messages", currentLocale);

        // Update UI texts and refresh the current view
        updateTexts();
        refreshCurrentView();
    }


    private void updateTexts() {
        if (userLabel != null) {
            userLabel.setText(bundle.getString("header.welcome") + ", " + (userName != null ? userName : ""));
        }

        logoutButton.setText(bundle.getString("sidebar.logout"));
        updateSidebarForRole(userRole);
    }

    private void updateSidebarForRole(String role) {
        if (role == null) return;

        String normalizedRole = role.toLowerCase();
        sidebar.getChildren().clear();

        switch (normalizedRole) {
            case "admin":
                sidebar.getChildren().addAll(
                        createButton(bundle.getString("sidebar.dashboard"), "/gestion/ecole/admin/DashboardView.fxml","/icons/dashboard-icon.png"),
                        createButton(bundle.getString("sidebar.etudiants"), "/gestion/ecole/secretaire/EtudiantsView.fxml","/icons/students-icon.png"),
                        createButton(bundle.getString("sidebar.inscriptions"), "/gestion/ecole/secretaire/InscriptionsView.fxml","/icons/registrations-icon.png"),
                        createButton(bundle.getString("sidebar.modules"), "/gestion/ecole/admin/ModulesView.fxml","/icons/modules-icon.png"),
                        createButton(bundle.getString("sidebar.professeurs"), "/gestion/ecole/admin/ProfesseursView.fxml","/icons/professors-icon.png"),
                        logoutButton
                );
                break;

            case "secretary":
                sidebar.getChildren().addAll(
                        createButton(bundle.getString("sidebar.etudiants"), "/gestion/ecole/secretaire/EtudiantsView.fxml","/icons/students-icon.png"),
                        createButton(bundle.getString("sidebar.inscriptions"), "/gestion/ecole/secretaire/InscriptionsView.fxml", "/icons/registrations-icon.png"),
                        logoutButton
                );
                break;

            case "professor":
                sidebar.getChildren().addAll(
                        createButton(bundle.getString("sidebar.mesCours"), "/gestion/ecole/professeur/ModulesView.fxml","/icons/modules-icon.png"),
                        logoutButton
                );
                break;

            default:
                throw new IllegalArgumentException("Unknown role: " + role);
        }
    }

    private Button createButton(String text, String fxmlPath,String iconPath) {
        Button button = new Button(text);
        Image iconImage = new Image(getClass().getResourceAsStream(iconPath));
        ImageView icon = new ImageView(iconImage);
        icon.setFitHeight(20);
        icon.setFitWidth(20);
        button.setGraphic(icon);
        button.setContentDisplay(ContentDisplay.LEFT);
        button.setStyle("-fx-background-color: rgba(57,84,103,0); -fx-text-fill: #ffffff; -fx-pref-width: 200; -fx-alignment: center-left;");

        button.setOnAction(event -> {
            loadView(fxmlPath);
            highlightButton(button);
        });
        return button;
    }

    private void highlightButton(Button button) {
        sidebar.getChildren().forEach(node -> {
            if (node == logoutButton) {
                node.setStyle("-fx-background-color: #8c220a; -fx-text-fill: #ffffff; -fx-pref-width: 200;");
            } else if (node instanceof Button) {
                node.setStyle("-fx-background-color: rgba(74,105,131,0); -fx-text-fill: #ffffff; -fx-pref-width: 200; -fx-alignment: center-left;");
            }
        });
        button.setStyle("-fx-background-color: #395467; -fx-text-fill: #ffffff; -fx-pref-width: 200; -fx-alignment: center-left;");
    }

    public void setContentPane(Node node) {
        contentPane.getChildren().clear();
        contentPane.getChildren().add(node);
    }

    private void loadView(String fxmlPath) {
        try {
            currentFxmlPath = fxmlPath; // Track the current view path

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath), bundle);
            Pane newView = loader.load();

            Object controller = loader.getController();
            if (controller instanceof UserAwareController) {
                ((UserAwareController) controller).setUserId(userId);
            }
            if (controller instanceof MainControllerAware) {
                ((MainControllerAware) controller).setMainController(this);
            }
            if (controller instanceof BundleAware) {
                ((BundleAware) controller).setResourceBundle(bundle);
            }

            contentPane.getChildren().clear();
            contentPane.getChildren().add(newView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void refreshCurrentView() {
        if (currentFxmlPath != null) {
            loadView(currentFxmlPath); // Reload the current view with the updated bundle
        }
    }

    @FXML
    private void handleLogoutClick() {
        utilisateurService.logout();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gestion/ecole/login.fxml"), bundle);
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(fxmlLoader.load()));
            stage.setTitle(bundle.getString("login.title"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadDefaultView() {
        if ("admin".equals(userRole)) {
            triggerButtonClick(sidebar.getChildren().get(0));
        } else if ("secretary".equals(userRole)) {
            triggerButtonClick(sidebar.getChildren().get(0));
        } else if ("professor".equals(userRole)) {
            triggerButtonClick(sidebar.getChildren().get(0));
        }
    }

    private void triggerButtonClick(Node node) {
        if (node instanceof Button) {
            ((Button) node).fire();
        }
    }
}
