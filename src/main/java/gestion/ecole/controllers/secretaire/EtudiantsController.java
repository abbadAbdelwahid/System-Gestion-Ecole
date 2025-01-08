package gestion.ecole.controllers.secretaire;

import gestion.ecole.models.Etudiant;
import gestion.ecole.services.EtudiantService;
import gestion.ecole.services.ExcelExportService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import gestion.ecole.services.SecretaireExportService;  // Ajoutez cet import
import javafx.stage.FileChooser;
import java.io.File;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class EtudiantsController  {

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
    private static TableView<Etudiant> staticTableEtudiants;

    @FXML
    public void initialize() {
        // Initialiser la référence statique
        staticTableEtudiants = tableEtudiants;

        // Initialisation des colonnes de la TableView
        columnNom.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNom()));
        columnPrenom.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPrenom()));
        columnEmail.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEmail()));
        columnMatricule.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getMatricule()));
        columnPromotion.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPromotion()));
        columnDateNaissance.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getDateNaissance()));

        // Initialiser la liste et charger les données
        listeEtudiants = FXCollections.observableArrayList();
        loadStudentsData();
    }

    // Méthode pour charger les étudiants dans la TableView
    public static void loadStudentsData() {
        try {
            EtudiantService service = new EtudiantService();
            ObservableList<Etudiant> newList = FXCollections.observableArrayList(service.getAll());

            if (staticTableEtudiants != null) {
                // Utiliser Platform.runLater pour assurer la mise à jour dans le thread UI
                javafx.application.Platform.runLater(() -> {
                    staticTableEtudiants.setItems(newList);
                    staticTableEtudiants.refresh();
                });
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des étudiants : " + e.getMessage());
        }
    }

    // Méthode pour la recherche
    @FXML
    private void handleRechercheEtudiant() {
        String keyword = searchField.getText().toLowerCase();
        List<Etudiant> filteredList = etudiantService.search(keyword);
        listeEtudiants = FXCollections.observableArrayList(filteredList);
        tableEtudiants.setItems(listeEtudiants);
        lblMessage.setText(filteredList.isEmpty() ? "Aucun étudiant trouvé." : filteredList.size() + " étudiant(s) trouvé(s).");
    }

    // Méthode pour rafraîchir
    @FXML
    private void handleRefreshTable() {
        loadStudentsData();
        lblMessage.setText("Table mise à jour.");
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


    // Ajoutez cette propriété
    private final SecretaireExportService exportService = new SecretaireExportService();

    private void refreshListeEtudiants() {
        // Met à jour la liste globale
        listeEtudiants = FXCollections.observableArrayList(etudiantService.getAll());
        tableEtudiants.setItems(listeEtudiants);
        tableEtudiants.refresh();
    }

    @FXML
    private void handleExportPDF() {
        try {
            // Rafraîchir d'abord la liste
            refreshListeEtudiants();

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer le PDF");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Fichiers PDF (*.pdf)", "*.pdf")
            );

            File file = fileChooser.showSaveDialog(tableEtudiants.getScene().getWindow());

            if (file != null) {
                exportService.generatePDF(listeEtudiants, file.getAbsolutePath());
                lblMessage.setText("Le PDF a été généré avec succès !");
            }
        } catch (Exception e) {
            lblMessage.setText("Erreur lors de la génération du PDF : " + e.getMessage());
        }
    }

    @FXML
    private void handleExportCSV() {
        try {
            // Rafraîchir d'abord la liste
            refreshListeEtudiants();

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer en Excel");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Fichiers Excel (*.xlsx)", "*.xlsx")
            );

            // Utilisez le dossier Documents par défaut
            String userHome = System.getProperty("user.home");
            fileChooser.setInitialDirectory(new File(userHome + "/Documents"));

            File file = fileChooser.showSaveDialog(tableEtudiants.getScene().getWindow());

            if (file != null) {
                // Créer une instance du nouveau service Excel
                ExcelExportService excelService = new ExcelExportService();
                excelService.generateExcel(listeEtudiants, file.getAbsolutePath());
                lblMessage.setText("Le fichier Excel a été généré avec succès !");
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors de la génération du fichier Excel : " + e.getMessage());
            alert.showAndWait();
        }
    }
}