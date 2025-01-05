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

    private final ModuleService moduleService = new ModuleService();
    private final EtudiantService etudiantService = new EtudiantService();
    private final ProfesseurService professeurService = new ProfesseurService();
    private int professorId;
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

        moduleListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                loadStudents(newSelection.getId());
            }
        });
    }

    private void loadStudents(int moduleId) {
        List<Etudiant> students = etudiantService.getStudentsByModule(moduleId);
        ObservableList<Etudiant> studentList = FXCollections.observableArrayList(students);
        studentTableView.setItems(studentList);
    }

    @FXML
    public void initialize() {
        colMatricule.setCellValueFactory(new PropertyValueFactory<>("matricule"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
    }
}
