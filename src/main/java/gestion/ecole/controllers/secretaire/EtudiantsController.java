package gestion.ecole.controllers.secretaire;

import gestion.ecole.controllers.BundleAware;
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
import java.util.ResourceBundle;

public class EtudiantsController implements BundleAware {

    public Button btnAjouter;
    public Button btnModifier;
    public Button btnSupprimer;
    @FXML
    private TableView<Etudiant> tableEtudiants;
    @FXML
    private TableColumn<Etudiant, String> columnNom, columnPrenom, columnEmail, columnMatricule, columnPromotion;
    @FXML
    private TableColumn<Etudiant, LocalDate> columnDateNaissance;

    @FXML
    private TextField searchField;
    @FXML
    private Label lblMessage;

    @FXML
    private TextField matriculeField, nomField, prenomField, emailField, promotionField;
    @FXML
    private DatePicker dateNaissanceField;

    private final EtudiantService etudiantService = new EtudiantService();
    private ObservableList<Etudiant> listeEtudiants;
    private ResourceBundle bundle;

    @Override
    public void setResourceBundle(ResourceBundle bundle) {
        this.bundle = bundle;
        updateTexts();
    }

    @FXML
    public void initialize() {
        columnNom.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNom()));
        columnPrenom.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPrenom()));
        columnEmail.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEmail()));
        columnMatricule.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getMatricule()));
        columnPromotion.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPromotion()));
        columnDateNaissance.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getDateNaissance()));

        loadStudentsData();
    }

    private void updateTexts() {
        searchField.setPromptText(bundle.getString("search.placeholder"));
        lblMessage.setText(""); // Reset message for consistency
    }

    private void loadStudentsData() {
        listeEtudiants = FXCollections.observableArrayList(etudiantService.getAll());
        tableEtudiants.setItems(listeEtudiants);
    }

    @FXML
    public void handleAjouterEtudiant() {
        ouvrirFormulaire(null);
    }

    @FXML
    public void handleModifierEtudiant() {
        Etudiant selectedEtudiant = tableEtudiants.getSelectionModel().getSelectedItem();
        if (selectedEtudiant != null) {
            ouvrirFormulaire(selectedEtudiant);
        } else {
            lblMessage.setText(bundle.getString("student.select"));
        }
    }

    @FXML
    public void handleSupprimerEtudiant() {
        Etudiant selectedEtudiant = tableEtudiants.getSelectionModel().getSelectedItem();
        if (selectedEtudiant != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(bundle.getString("student.delete.confirm.title"));
            alert.setHeaderText(bundle.getString("student.delete.confirm.header"));
            alert.setContentText(String.format(bundle.getString("student.delete.confirm.content"),
                    selectedEtudiant.getNom(), selectedEtudiant.getPrenom()));

            if (alert.showAndWait().get() == ButtonType.OK) {
                etudiantService.delete(selectedEtudiant.getId());
                lblMessage.setText(bundle.getString("student.delete.success"));
                loadStudentsData();
            }
        } else {
            lblMessage.setText(bundle.getString("student.select"));
        }
    }

    private void ouvrirFormulaire(Etudiant etudiant) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gestion/ecole/secretaire/EtudiantForm.fxml"), bundle);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(etudiant == null ? bundle.getString("student.add.title") : bundle.getString("student.modify.title"));
            stage.setScene(new Scene(loader.load()));

            EtudiantFormController controller = loader.getController();
            if (etudiant != null) {
                controller.setEtudiant(etudiant);
            }

            stage.showAndWait();
            loadStudentsData();
        } catch (Exception e) {
            e.printStackTrace();
            lblMessage.setText(bundle.getString("form.open.error") + e.getMessage());
        }
    }

    private void remplirChamps(Etudiant etudiant) {
        matriculeField.setText(etudiant.getMatricule());
        nomField.setText(etudiant.getNom());
        prenomField.setText(etudiant.getPrenom());
        dateNaissanceField.setValue(etudiant.getDateNaissance());
        emailField.setText(etudiant.getEmail());
        promotionField.setText(etudiant.getPromotion());
    }

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
                lblMessage.setText(bundle.getString("student.fill.fields"));
                return;
            }

            etudiantService.addStudent(nouvelEtudiant);
            lblMessage.setText(bundle.getString("student.add.success"));
            Stage stage = (Stage) matriculeField.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            e.printStackTrace();
            lblMessage.setText(bundle.getString("student.add.error") + e.getMessage());
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
        lblMessage.setText(filteredList.isEmpty() ? bundle.getString("student.search.notfound")
                : bundle.getString("student.search.found"));
    }

    @FXML
    private void handleRefreshTable() {
        loadStudentsData();
        lblMessage.setText(bundle.getString("table.refresh.success"));
    }
}
