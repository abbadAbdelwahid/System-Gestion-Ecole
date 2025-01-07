package gestion.ecole.controllers.secretaire;

import gestion.ecole.models.Etudiant;
import gestion.ecole.services.EtudiantService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class EtudiantsController {

    // TableView et ses colonnes
    @FXML
    private TableView<Etudiant> tableEtudiants;
    @FXML
    private TableColumn<Etudiant, String> columnNom, columnPrenom, columnEmail, columnMatricule, columnPromotion;
    @FXML
    private TableColumn<Etudiant, LocalDate> columnDateNaissance;

    // Champs de recherche et messages
    @FXML
    private TextField searchField;
    @FXML
    private Label lblMessage;

    // Champs du formulaire d'étudiant
    @FXML
    private TextField matriculeField, nomField, prenomField, emailField, promotionField;
    @FXML
    private DatePicker dateNaissanceField;

    private final EtudiantService etudiantService = new EtudiantService();
    private ObservableList<Etudiant> listeEtudiants;

    @FXML
    public void initialize() {
        // Initialisation des colonnes de la TableView
        columnNom.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNom()));
        columnPrenom.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPrenom()));
        columnEmail.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEmail()));
        columnMatricule.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getMatricule()));
        columnPromotion.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPromotion()));
        columnDateNaissance.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getDateNaissance()));

        loadStudentsData();  // Chargement des données dans la TableView
    }

    // Méthode pour charger les étudiants dans la TableView
    private void loadStudentsData() {
        listeEtudiants = FXCollections.observableArrayList(etudiantService.getAll());
        tableEtudiants.setItems(listeEtudiants);
    }

    @FXML
    public void handleAjouterEtudiant() {
        ouvrirFormulaire(null);  // Ouvrir le formulaire pour un nouvel étudiant
    }

    @FXML
    public void handleModifierEtudiant() {
        Etudiant selectedEtudiant = tableEtudiants.getSelectionModel().getSelectedItem();
        if (selectedEtudiant != null) {
            ouvrirFormulaire(selectedEtudiant);  // Ouvrir le formulaire pour modification
        } else {
            lblMessage.setText("Veuillez sélectionner un étudiant.");
        }
    }

    @FXML
    public void handleSupprimerEtudiant() {
        Etudiant selectedEtudiant = tableEtudiants.getSelectionModel().getSelectedItem();
        if (selectedEtudiant != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation de suppression");
            alert.setHeaderText("Voulez-vous vraiment supprimer cet étudiant ?");
            alert.setContentText("Étudiant : " + selectedEtudiant.getNom() + " " + selectedEtudiant.getPrenom());

            if (alert.showAndWait().get() == ButtonType.OK) {
                etudiantService.delete(selectedEtudiant.getId());
                lblMessage.setText("Étudiant supprimé avec succès !");
                loadStudentsData();  // Recharger la liste après suppression
            }
        } else {
            lblMessage.setText("Veuillez sélectionner un étudiant.");
        }
    }

    private void ouvrirFormulaire(Etudiant etudiant) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gestion/ecole/secretaire/EtudiantForm.fxml"));
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(etudiant == null ? "Ajouter un étudiant" : "Modifier un étudiant");
            stage.setScene(new Scene(loader.load()));

            // Get the controller and set the student if editing
            EtudiantFormController controller = loader.getController();
            if (etudiant != null) {
                controller.setEtudiant(etudiant);
            }

            stage.showAndWait();
            loadStudentsData(); // Refresh the table after closing the form
        } catch (Exception e) {
            e.printStackTrace();
            lblMessage.setText("Erreur lors de l'ouverture du formulaire : " + e.getMessage());
        }
    }

    // Remplir les champs du formulaire pour la modification
    private void remplirChamps(Etudiant etudiant) {
        matriculeField.setText(etudiant.getMatricule());
        nomField.setText(etudiant.getNom());
        prenomField.setText(etudiant.getPrenom());
        dateNaissanceField.setValue(etudiant.getDateNaissance());
        emailField.setText(etudiant.getEmail());
        promotionField.setText(etudiant.getPromotion());
    }

    // Vider les champs du formulaire
    private void viderChamps() {
        matriculeField.clear();
        nomField.clear();
        prenomField.clear();
        dateNaissanceField.setValue(null);
        emailField.clear();
        promotionField.clear();
    }

    @FXML
    private void validerEtudiant() {
        try {
            Etudiant nouvelEtudiant = new Etudiant(
                    0,
                    matriculeField.getText(),
                    nomField.getText(),
                    prenomField.getText(),
                    dateNaissanceField.getValue(),
                    emailField.getText(),
                    promotionField.getText()
            );

            if (nouvelEtudiant.getMatricule().isEmpty() || nouvelEtudiant.getNom().isEmpty()) {
                lblMessage.setText("Veuillez remplir tous les champs obligatoires.");
                return;
            }

            etudiantService.addStudent(nouvelEtudiant);
            lblMessage.setText("Étudiant ajouté ou modifié avec succès !");
            Stage stage = (Stage) matriculeField.getScene().getWindow();
            stage.close();  // Fermer le formulaire
        } catch (Exception e) {
            e.printStackTrace();
            lblMessage.setText("Erreur lors de la validation des données.");
        }
    }

    @FXML
    private void annulerAction() {
        Stage stage = (Stage) matriculeField.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleRechercheEtudiant() {
        String keyword = searchField.getText().toLowerCase();
        List<Etudiant> filteredList = etudiantService.search(keyword);
        listeEtudiants.setAll(filteredList);
        lblMessage.setText(filteredList.isEmpty() ? "Aucun étudiant trouvé." : filteredList.size() + " étudiant(s) trouvé(s).");
    }

    @FXML
    private void handleRefreshTable() {
        loadStudentsData();
        lblMessage.setText("Table mise à jour.");
    }
}