package gestion.ecole.controllers.secretaire;

import gestion.ecole.models.Inscription;
import gestion.ecole.models.Module;
import gestion.ecole.models.Etudiant;
import gestion.ecole.services.InscriptionService;
import gestion.ecole.services.ModuleService;
import gestion.ecole.services.EtudiantService;
import gestion.ecole.services.InscriptionSecretairePDFService;
import gestion.ecole.services.InscriptionSecretaireExportService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import java.io.File;
import java.time.LocalDate;
import java.util.List;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;

public class InscriptionsController {
    @FXML private TableView<Inscription> tableInscriptions;
    @FXML private ComboBox<Module> moduleComboBox;
    @FXML private ComboBox<Etudiant> etudiantComboBox;
    @FXML private DatePicker datePicker;
    @FXML private Label messageLabel;

    private final InscriptionService inscriptionService = new InscriptionService();
    private final ModuleService moduleService = new ModuleService();
    private final EtudiantService etudiantService = new EtudiantService();
    private final InscriptionSecretairePDFService pdfService = new InscriptionSecretairePDFService();
    private final InscriptionSecretaireExportService exportService = new InscriptionSecretaireExportService();

    @FXML private TableColumn<Inscription, Integer> columnId;
    @FXML private TableColumn<Inscription, String> columnEtudiant;
    @FXML private TableColumn<Inscription, String> columnModule;
    @FXML private TableColumn<Inscription, LocalDate> columnDate;

    @FXML
    public void initialize() {
        // Configuration des colonnes
        columnId.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getId()));

        columnEtudiant.setCellValueFactory(cellData -> {
            Etudiant etudiant = etudiantService.get(cellData.getValue().getEtudiantId());
            return new SimpleStringProperty(
                    etudiant != null ? etudiant.getNom() + " " + etudiant.getPrenom() : ""
            );
        });

        columnModule.setCellValueFactory(cellData -> {
            Module module = moduleService.get(cellData.getValue().getModuleId());
            return new SimpleStringProperty(
                    module != null ? module.getNomModule() : ""
            );
        });

        columnDate.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getDateInscription()));

        // Initialisation des ComboBox
        moduleComboBox.setItems(FXCollections.observableArrayList(moduleService.getAll()));
        etudiantComboBox.setItems(FXCollections.observableArrayList(etudiantService.getAll()));

        // Configuration de la date par défaut
        datePicker.setValue(LocalDate.now());

        // Chargement initial des données
        refreshInscriptions();
    }

    @FXML
    private void handleAjouterInscription() {
        if (validateInputs()) {
            Inscription newInscription = new Inscription(
                    0,
                    etudiantComboBox.getValue().getId(),
                    moduleComboBox.getValue().getId(),
                    datePicker.getValue()
            );

            if (inscriptionService.insert(newInscription)) {
                messageLabel.setText("Inscription ajoutée avec succès");
                refreshInscriptions();
                clearInputs();
            } else {
                messageLabel.setText("Erreur lors de l'ajout de l'inscription");
            }
        }
    }

    @FXML
    private void handleExportPDF() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le PDF");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf")
        );

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                pdfService.generatePDF(
                        FXCollections.observableArrayList(inscriptionService.getAll()),
                        file.getAbsolutePath()
                );
                messageLabel.setText("PDF généré avec succès");
            } catch (Exception e) {
                messageLabel.setText("Erreur lors de la génération du PDF: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleExportCSV() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer en Excel");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Fichiers Excel (*.xlsx)", "*.xlsx")
            );

            File file = fileChooser.showSaveDialog(null);
            if (file != null) {
                List<Inscription> inscriptions = inscriptionService.getAll(); // Assurez-vous d'avoir les données à jour
                exportService.generateExcel(inscriptions, file.getAbsolutePath());
                messageLabel.setText("Le fichier Excel a été généré avec succès !");
            }
        } catch (Exception e) {
            messageLabel.setText("Erreur lors de la génération du fichier Excel : " + e.getMessage());
        }
    }

    private boolean validateInputs() {
        if (etudiantComboBox.getValue() == null) {
            messageLabel.setText("Veuillez sélectionner un étudiant");
            return false;
        }
        if (moduleComboBox.getValue() == null) {
            messageLabel.setText("Veuillez sélectionner un module");
            return false;
        }
        if (datePicker.getValue() == null) {
            messageLabel.setText("Veuillez sélectionner une date");
            return false;
        }
        return true;
    }

    private void clearInputs() {
        etudiantComboBox.setValue(null);
        moduleComboBox.setValue(null);
        datePicker.setValue(LocalDate.now());
    }

    private void refreshInscriptions() {
        var inscriptions = inscriptionService.getAll();
        System.out.println("Nombre d'inscriptions chargées: " + inscriptions.size());
        tableInscriptions.setItems(FXCollections.observableArrayList(inscriptions));
    }

    @FXML
    private void handleSupprimerInscription() {
        Inscription selectedInscription = tableInscriptions.getSelectionModel().getSelectedItem();
        if (selectedInscription == null) {
            messageLabel.setText("Veuillez sélectionner une inscription à supprimer");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer l'inscription");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cette inscription ?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            if (inscriptionService.delete(selectedInscription.getId())) {
                messageLabel.setText("Inscription supprimée avec succès");
                refreshInscriptions();
            } else {
                messageLabel.setText("Erreur lors de la suppression de l'inscription");
            }
        }
    }

    @FXML
    private TextField searchField;

    // Méthode de recherche
    @FXML
    private void handleRechercher() {
        String searchTerm = searchField.getText().toLowerCase();
        if (searchTerm.isEmpty()) {
            refreshInscriptions();
            return;
        }

        ObservableList<Inscription> allInscriptions = FXCollections.observableArrayList(inscriptionService.getAll());
        ObservableList<Inscription> filteredList = allInscriptions.filtered(inscription -> {
            Etudiant etudiant = etudiantService.get(inscription.getEtudiantId());
            Module module = moduleService.get(inscription.getModuleId());

            return (etudiant.getNom().toLowerCase().contains(searchTerm) ||
                    etudiant.getPrenom().toLowerCase().contains(searchTerm) ||
                    module.getNomModule().toLowerCase().contains(searchTerm));
        });

        tableInscriptions.setItems(filteredList);
        messageLabel.setText(filteredList.isEmpty() ? "Aucune inscription trouvée."
                : filteredList.size() + " inscription(s) trouvée(s).");
    }

    // Méthode de rafraîchissement
    @FXML
    private void handleRefresh() {
        searchField.clear();
        refreshInscriptions();
        messageLabel.setText("Liste des inscriptions mise à jour.");
    }

}