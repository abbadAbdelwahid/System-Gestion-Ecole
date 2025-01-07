package gestion.ecole.controllers.secretaire;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class InscriptionsController {

    @FXML
    private Label testLabel;

    @FXML
    public void initialize() {
        // Affiche un message si le chargement du FXML est correct
        testLabel.setText("Path inscription correct");
        System.out.println("Path inscription correct");
    }
}
