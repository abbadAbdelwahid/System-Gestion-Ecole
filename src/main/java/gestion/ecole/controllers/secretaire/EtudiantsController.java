package gestion.ecole.controllers.secretaire;

import gestion.ecole.controllers.BundleAware;
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
import java.util.ResourceBundle;

public class EtudiantsController implements BundleAware {

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
    private ResourceBundle bundle;

    public void setResourceBundle(ResourceBundle bundle){
        this.bundle = bundle;
        staticTableEtudiants = tableEtudiants;
        columnNom.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNom()));
        columnPrenom.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPrenom()));
        columnEmail.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEmail()));
        columnMatricule.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getMatricule()));
        columnPromotion.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPromotion()));
        columnDateNaissance.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getDateNaissance()));

        updateTexts();
        listeEtudiants = FXCollections.observableArrayList();
        loadStudentsData();
    }
    @FXML
    public void initialize() {

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

        if (filteredList.isEmpty()) {
            lblMessage.setText(bundle.getString("message.noStudentFound"));
        } else {
            lblMessage.setText(filteredList.size() + " " + bundle.getString("message.studentFound"));
        }
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
            alert.setTitle(bundle.getString("alert.delete.title"));
            alert.setHeaderText(bundle.getString("alert.delete.header"));
            alert.setContentText(bundle.getString("alert.delete.content") +
                    selectedEtudiant.getNom() + " " + selectedEtudiant.getPrenom());

            if (alert.showAndWait().get() == ButtonType.OK) {
                etudiantService.delete(selectedEtudiant.getId());
                lblMessage.setText(bundle.getString("message.studentDeleted"));
                loadStudentsData();
            }
        } else {
            lblMessage.setText(bundle.getString("message.selectStudent"));
        }
    }


    private void ouvrirFormulaire(Etudiant etudiant) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gestion/ecole/secretaire/EtudiantForm.fxml"));
            loader.setResources(bundle);
            Stage stage = new Stage();

            stage.initModality(Modality.APPLICATION_MODAL);

            stage.setTitle(etudiant == null ? bundle.getString("form.title.add.student") : bundle.getString("form.title.edit.student"));

            stage.setScene(new Scene(loader.load()));
            EtudiantFormController controller = loader.getController();
            controller.setResourceBundle(bundle);
            if (etudiant != null) {
                controller.setEtudiant(etudiant);
            }

            stage.showAndWait();
            loadStudentsData();
        } catch (Exception e) {
            lblMessage.setText(bundle.getString("message.formError") + e.getMessage());
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
            refreshListeEtudiants();

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(bundle.getString("filechooser.title.pdf"));
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(bundle.getString("filechooser.filter.pdf"), "*.pdf")
            );

            File file = fileChooser.showSaveDialog(tableEtudiants.getScene().getWindow());

            if (file != null) {
                exportService.generatePDF(listeEtudiants, file.getAbsolutePath());
                lblMessage.setText(bundle.getString("message.pdfSuccess"));
            }
        } catch (Exception e) {
            lblMessage.setText(bundle.getString("message.pdfError") + e.getMessage());
        }
    }

    @FXML
    private void handleExportCSV() {
        try {
            refreshListeEtudiants();

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(bundle.getString("filechooser.title.excel"));
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(bundle.getString("filechooser.filter.excel"), "*.xlsx")
            );

            String userHome = System.getProperty("user.home");
            fileChooser.setInitialDirectory(new File(userHome + "/Documents"));

            File file = fileChooser.showSaveDialog(tableEtudiants.getScene().getWindow());

            if (file != null) {
                ExcelExportService excelService = new ExcelExportService();
                excelService.generateExcel(listeEtudiants, file.getAbsolutePath());
                lblMessage.setText(bundle.getString("message.excelSuccess"));
            }
        } catch (Exception e) {
            lblMessage.setText(bundle.getString("message.excelError") + e.getMessage());
        }
    }

    private void updateTexts() {
        columnNom.setText(bundle.getString("column.nom"));
        columnPrenom.setText(bundle.getString("column.prenom"));
        columnEmail.setText(bundle.getString("column.email"));
        columnMatricule.setText(bundle.getString("column.matricule"));
        columnPromotion.setText(bundle.getString("column.promotion"));
        columnDateNaissance.setText(bundle.getString("column.dateNaissance"));
    }
}