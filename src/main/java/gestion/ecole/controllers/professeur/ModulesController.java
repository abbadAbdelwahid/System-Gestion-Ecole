package gestion.ecole.controllers.professeur;

import gestion.ecole.controllers.UserAwareController;
import gestion.ecole.models.Etudiant;
import gestion.ecole.models.Module;
import gestion.ecole.services.EtudiantService;
import gestion.ecole.services.ModuleService;
import gestion.ecole.services.ProfesseurService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class ModulesController implements UserAwareController {
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

    private int professorId;
    private int selectedModuleId;

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
