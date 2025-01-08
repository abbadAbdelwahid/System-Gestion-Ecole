package gestion.ecole.controllers.professeur;

import gestion.ecole.controllers.BundleAware;
import gestion.ecole.controllers.UserAwareController;
import gestion.ecole.models.Etudiant;
import gestion.ecole.models.Module;
import gestion.ecole.services.EtudiantService;
import gestion.ecole.services.ModuleService;
import gestion.ecole.services.PDFService;
import gestion.ecole.services.ProfesseurService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;
import java.util.ResourceBundle;

public class ModulesController implements UserAwareController, BundleAware {
    @FXML
    private ListView<Module> moduleListView;

    @FXML
    private TableView<Etudiant> studentTableView;

    @FXML
    private TableColumn<Etudiant, String> colMatricule;

    @FXML
    private TableColumn<Etudiant, String> colNom;

    @FXML
    private TableColumn<Etudiant, String> colPrenom;

    @FXML
    private TableColumn<Etudiant, String> colDateNaissance;

    @FXML
    private TableColumn<Etudiant, String> colEmail;

    @FXML
    private TableColumn<Etudiant, String> colPromotion;


    @FXML
    private TextField searchField; // TextField for the search term

    private final ModuleService moduleService = new ModuleService();
    private final EtudiantService etudiantService = new EtudiantService();
    private final ProfesseurService professeurService = new ProfesseurService();
    private final PDFService pdfService = new PDFService();

    private int professorId;
    private int selectedModuleId;
    private ResourceBundle bundle;
    @Override
    public void setResourceBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    @Override
    public void setUserId(int userId) {
        System.out.println("User ID in ModulesController: " + userId);
        setProfessorId(professeurService.getProfesseurIdByUserId(userId));
    }

    public void setProfessorId(int professorId) {
        this.professorId = professorId;
        loadModules();
    }

    private void loadModules() {
        List<Module> modules = moduleService.getModulesByProfessor(professorId);
        ObservableList<Module> moduleList = FXCollections.observableArrayList(modules);
        moduleListView.setItems(moduleList);
    }

    private void loadStudents(int moduleId) {
        this.selectedModuleId = moduleId; // Save the currently selected module ID
        List<Etudiant> students = etudiantService.getStudentsByModule(moduleId);
        ObservableList<Etudiant> studentList = FXCollections.observableArrayList(students);
        studentTableView.setItems(studentList);
    }
    @FXML
    private void exportToCSV() {
        Module selectedModule = moduleListView.getSelectionModel().getSelectedItem();
        if (selectedModule == null) {
            showAlert("Erreur", "Aucun module sélectionné", "Veuillez sélectionner un module pour exporter les étudiants.", Alert.AlertType.ERROR);
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le fichier CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        // Set a default file name
        fileChooser.setInitialFileName("Etudiants_" + selectedModule.getNomModule().replaceAll(" ", "_") + ".csv");

        File file = fileChooser.showSaveDialog(studentTableView.getScene().getWindow());

        if (file != null) {
            List<Etudiant> students = etudiantService.getStudentsByModule(selectedModule.getId());
            pdfService.generateCSV(students, file.getAbsolutePath());
        }
    }

    @FXML
    private void exportToPDF() {
        Module selectedModule = moduleListView.getSelectionModel().getSelectedItem();
        if (selectedModule == null) {
            showAlert("Erreur", "Aucun module sélectionné", "Veuillez sélectionner un module pour exporter les étudiants.", Alert.AlertType.ERROR);
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le fichier PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        fileChooser.setInitialFileName("Etudiants_" + selectedModule.getNomModule().replaceAll(" ", "_") + ".pdf");

        File file = fileChooser.showSaveDialog(studentTableView.getScene().getWindow());

        if (file != null) {
            List<Etudiant> students = etudiantService.getStudentsByModule(selectedModule.getId());
            pdfService.generatePDF(students, selectedModule.getNomModule(), file.getAbsolutePath());
        }
    }

    private void showAlert(String title, String header, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void searchStudents() {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty() || selectedModuleId == 0) {
            return; // No search term or no module selected
        }

        List<Etudiant> students = etudiantService.searchInStudentsByModule(selectedModuleId, searchText);
        ObservableList<Etudiant> studentList = FXCollections.observableArrayList(students);
        studentTableView.setItems(studentList);
    }

    @FXML
    private void resetStudentSearch() {
        searchField.clear();
        if (selectedModuleId != 0) {
            loadStudents(selectedModuleId); // Reload students for the selected module
        }
    }

    @FXML
    public void initialize() {
        colMatricule.setCellValueFactory(new PropertyValueFactory<>("matricule"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colDateNaissance.setCellValueFactory(new PropertyValueFactory<>("dateNaissance"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPromotion.setCellValueFactory(new PropertyValueFactory<>("promotion"));

        moduleListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                loadStudents(newSelection.getId());
            }
        });
    }
}
