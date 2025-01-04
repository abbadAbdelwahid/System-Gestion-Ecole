package gestion.ecole.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class MainController {
    @FXML
    private VBox sidebar;

    private String userRole;

    public void setUserRole(String role) {
        this.userRole = role;
        updateSidebar();
    }

    private void updateSidebar() {
        try {
            FXMLLoader loader = new FXMLLoader();
            if ("admin".equals(userRole)) {
                loader.setLocation(getClass().getResource("/gestion/ecole/views/sidebar_admin.fxml"));
            } else if ("secretaire".equals(userRole)) {
                loader.setLocation(getClass().getResource("/gestion/ecole/views/sidebar_secretaire.fxml"));
            } else if ("professeur".equals(userRole)) {
                loader.setLocation(getClass().getResource("/gestion/ecole/views/sidebar_professeur.fxml"));
            }
            VBox newSidebar = loader.load();
            sidebar.getChildren().setAll(newSidebar);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
