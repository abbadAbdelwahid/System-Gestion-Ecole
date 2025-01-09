package gestion.ecole.controllers.secretaire;

import gestion.ecole.controllers.BundleAware;
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
import java.util.ResourceBundle;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class InscriptionsController implements BundleAware {
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
    private ResourceBundle bundle;

    public void setResourceBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    @FXML
    public void initialize() {
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

        columnDate.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDateInscription()));

        moduleComboBox.setItems(FXCollections.observableArrayList(moduleService.getAll()));
        etudiantComboBox.setItems(FXCollections.observableArrayList(etudiantService.getAll()));
        datePicker.setValue(LocalDate.now());

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
                messageLabel.setText(bundle.getString("message.addSuccess"));
                refreshInscriptions();
                clearInputs();
            } else {
                messageLabel.setText(bundle.getString("message.addError"));
            }
        }
    }

    @FXML
    private void handleExportPDF() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(bundle.getString("filechooser.title.pdf"));
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(bundle.getString("filechooser.filter.pdf"), "*.pdf")
        );

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                pdfService.generatePDF(
                        FXCollections.observableArrayList(inscriptionService.getAll()),
                        file.getAbsolutePath()
                );
                messageLabel.setText(bundle.getString("message.pdfSuccess"));
            } catch (Exception e) {
                messageLabel.setText(bundle.getString("message.pdfError") + e.getMessage());
            }
        }
    }

    @FXML
    private void handleExportCSV() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(bundle.getString("filechooser.title.excel"));
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(bundle.getString("filechooser.filter.excel"), "*.xlsx")
            );

            File file = fileChooser.showSaveDialog(null);
            if (file != null) {
                List<Inscription> inscriptions = inscriptionService.getAll();
                exportService.generateExcel(inscriptions, file.getAbsolutePath());
                messageLabel.setText(bundle.getString("message.excelSuccess"));
            }
        } catch (Exception e) {
            messageLabel.setText(bundle.getString("message.excelError") + e.getMessage());
        }
    }

    private boolean validateInputs() {
        if (etudiantComboBox.getValue() == null) {
            messageLabel.setText(bundle.getString("message.selectStudent"));
            return false;
        }
        if (moduleComboBox.getValue() == null) {
            messageLabel.setText(bundle.getString("message.selectModule"));
            return false;
        }
        if (datePicker.getValue() == null) {
            messageLabel.setText(bundle.getString("message.selectDate"));
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
        tableInscriptions.setItems(FXCollections.observableArrayList(inscriptions));
    }

    @FXML
    private void handleSupprimerInscription() {
        Inscription selectedInscription = tableInscriptions.getSelectionModel().getSelectedItem();
        if (selectedInscription == null) {
            messageLabel.setText(bundle.getString("message.selectInscription"));
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(bundle.getString("alert.delete.title"));
        alert.setHeaderText(bundle.getString("alert.delete.header"));
        alert.setContentText(bundle.getString("alert.delete.content"));

        if (alert.showAndWait().get() == ButtonType.OK) {
            if (inscriptionService.delete(selectedInscription.getId())) {
                messageLabel.setText(bundle.getString("message.deleteSuccess"));
                refreshInscriptions();
            } else {
                messageLabel.setText(bundle.getString("message.deleteError"));
            }
        }
    }

    @FXML
    private TextField searchField;

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
        messageLabel.setText(filteredList.isEmpty() ? bundle.getString("message.noResults")
                : filteredList.size() + " " + bundle.getString("message.resultsFound"));
    }

    @FXML
    private void handleRefresh() {
        searchField.clear();
        refreshInscriptions();
        messageLabel.setText(bundle.getString("message.listUpdated"));
    }
}
