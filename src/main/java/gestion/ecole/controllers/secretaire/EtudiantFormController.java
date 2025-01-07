package gestion.ecole.controllers.secretaire;

import gestion.ecole.models.Etudiant;
import gestion.ecole.services.EtudiantService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.LocalDate;

public class EtudiantFormController {
    @FXML
    private TextField matriculeField;
    @FXML
    private TextField nomField;
    @FXML
    private TextField prenomField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField promotionField;
    @FXML
    private DatePicker dateNaissanceField;
    @FXML
    private Label lblMessage;

    private final EtudiantService etudiantService = new EtudiantService();
    private Etudiant etudiantToEdit;

    public void setEtudiant(Etudiant etudiant) {
        this.etudiantToEdit = etudiant;
        if (etudiant != null) {
            matriculeField.setText(etudiant.getMatricule());
            nomField.setText(etudiant.getNom());
            prenomField.setText(etudiant.getPrenom());
            dateNaissanceField.setValue(etudiant.getDateNaissance());
            emailField.setText(etudiant.getEmail());
            promotionField.setText(etudiant.getPromotion());
        }
    }

    @FXML
    private void validerEtudiant() {
        try {
            if (isInputValid()) {
                Etudiant etudiant = new Etudiant(
                        etudiantToEdit != null ? etudiantToEdit.getId() : 0,
                        matriculeField.getText(),
                        nomField.getText(),
                        prenomField.getText(),
                        dateNaissanceField.getValue(),
                        emailField.getText(),
                        promotionField.getText()
                );

                if (etudiantToEdit != null) {
                    if (etudiantService.update(etudiant)) {
                        // Appeler directement le rechargement des données
                        EtudiantsController.loadStudentsData();
                        closeWindow();
                    }
                } else {
                    if (etudiantService.addStudent(etudiant)) {
                        // Appeler directement le rechargement des données
                        EtudiantsController.loadStudentsData();
                        closeWindow();
                    }
                }
            }
        } catch (Exception e) {
            lblMessage.setText("Erreur: " + e.getMessage());
            lblMessage.setStyle("-fx-text-fill: red;");
        }
    }

    private boolean isInputValid() {
        if (matriculeField.getText().isEmpty() || nomField.getText().isEmpty() ||
                prenomField.getText().isEmpty() || dateNaissanceField.getValue() == null) {
            lblMessage.setText("Veuillez remplir tous les champs obligatoires");
            lblMessage.setStyle("-fx-text-fill: red;");
            return false;
        }
        return true;
    }

    @FXML
    private void annulerAction() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) matriculeField.getScene().getWindow();
        stage.close();
    }

}