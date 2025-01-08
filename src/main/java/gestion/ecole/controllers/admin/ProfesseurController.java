package gestion.ecole.controllers.admin;

import gestion.ecole.controllers.BundleAware;
import gestion.ecole.controllers.MainController;
import gestion.ecole.controllers.MainControllerAware;
import gestion.ecole.controllers.professeur.ModulesController;
import gestion.ecole.dao.ModuleDAO;
import gestion.ecole.dao.ProfesseurDAO;
import gestion.ecole.models.Module;
import gestion.ecole.models.Professeur;
import gestion.ecole.models.Utilisateur;
import gestion.ecole.services.UtilisateurService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;

public class ProfesseurController implements MainControllerAware, BundleAware {

    @FXML
    private TextField nomField, prenomField, specialiteField, searchField;

    @FXML
    private TableView<Professeur> professeurTable;

    @FXML
    private TableColumn<Professeur, Integer> idColumn;

    @FXML
    private TableColumn<Professeur, String> nomColumn, prenomColumn, specialiteColumn;

    private final ProfesseurDAO professeurDAO = new ProfesseurDAO();
    private final ObservableList<Professeur> professeurList = FXCollections.observableArrayList();
    private ResourceBundle bundle;
    private MainController mainController;

    private final ModuleDAO moduleDAO = new ModuleDAO();
    private Module selectedModule;

    @Override
    public void setResourceBundle(ResourceBundle bundle) {
        this.bundle = bundle;
        updateTexts();
    }

    @Override
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        nomColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNom()));
        prenomColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPrenom()));
        specialiteColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSpecialite()));

        loadProfesseurs();
    }

    @FXML
    private void loadProfesseurs() {
        professeurList.setAll(professeurDAO.getAll());
        professeurTable.setItems(professeurList);
    }

    @FXML
    private void addProfesseur() {
        String nom = nomField.getText();
        String prenom = prenomField.getText();
        String specialite = specialiteField.getText();

        if (!nom.isEmpty() && !prenom.isEmpty() && !specialite.isEmpty()) {
            try {
                UtilisateurService utilisateurService = new UtilisateurService();
                String username = nom + prenom;
                String password = "password";
                String role = "professor";

                Utilisateur utilisateur = utilisateurService.addUser(username, password, role);
                if (utilisateur != null) {
                    Professeur professeur = new Professeur(0, nom, prenom, specialite, utilisateur.getId());
                    if (professeurDAO.insert(professeur)) {
                        loadProfesseurs();
                        clearFields();
                        showAlert(bundle.getString("alert.success"), bundle.getString("professor.add.success"));
                    } else {
                        showAlert(bundle.getString("alert.error"), bundle.getString("professor.add.error"));
                    }
                } else {
                    showAlert(bundle.getString("alert.error"), bundle.getString("user.creation.error"));
                }
            } catch (Exception e) {
                showAlert(bundle.getString("alert.error"), bundle.getString("generic.error") + e.getMessage());
            }
        } else {
            showAlert(bundle.getString("alert.warning"), bundle.getString("professor.fill.fields"));
        }
    }

    @FXML
    private void updateProfesseur() {
        Professeur selected = professeurTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setNom(nomField.getText());
            selected.setPrenom(prenomField.getText());
            selected.setSpecialite(specialiteField.getText());

            if (professeurDAO.update(selected)) {
                loadProfesseurs();
                clearFields();
                showAlert(bundle.getString("alert.success"), bundle.getString("professor.update.success"));
            } else {
                showAlert(bundle.getString("alert.error"), bundle.getString("professor.update.error"));
            }
        } else {
            showAlert(bundle.getString("alert.warning"), bundle.getString("professor.select"));
        }
    }

    @FXML
    private void deleteProfesseur() {
        Professeur selected = professeurTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (professeurDAO.delete(selected.getId())) {
                loadProfesseurs();
                clearFields();
                showAlert(bundle.getString("alert.success"), bundle.getString("professor.delete.success"));
            } else {
                showAlert(bundle.getString("alert.error"), bundle.getString("professor.delete.error"));
            }
        } else {
            showAlert(bundle.getString("alert.warning"), bundle.getString("professor.select"));
        }
    }

    @FXML
    private void searchProfesseurs() {
        String keyword = searchField.getText();
        if (!keyword.isEmpty()) {
            professeurList.setAll(professeurDAO.searchProfesseurs(keyword));
            professeurTable.setItems(professeurList);
        } else {
            loadProfesseurs();
        }
    }

    @FXML
    private void assignProfesseurToModule() {
        Professeur selectedProfesseur = professeurTable.getSelectionModel().getSelectedItem();
        if (selectedProfesseur != null && selectedModule != null) {
            selectedModule.setProfesseurId(selectedProfesseur.getId());
            if (moduleDAO.update(selectedModule)) {
                showAlert(bundle.getString("alert.success"), bundle.getString("professor.assign.success"));
                Stage stage = (Stage) professeurTable.getScene().getWindow();
                stage.close();
            } else {
                showAlert(bundle.getString("alert.error"), bundle.getString("professor.assign.error"));
            }
        } else {
            showAlert(bundle.getString("alert.warning"), bundle.getString("professor.select"));
        }
    }

    @FXML
    private void exportProfesseursToCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(bundle.getString("filechooser.save.csv"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fileChooser.setInitialFileName(bundle.getString("professor.list.filename.csv"));

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(bundle.getString("professor.list.csv.header"));
                writer.newLine();
                for (Professeur professeur : professeurList) {
                    writer.write(professeur.getId() + "," + professeur.getNom() + "," + professeur.getPrenom() + "," + professeur.getSpecialite() + "," + professeur.getUtilisateur_id());
                    writer.newLine();
                }
                showAlert(bundle.getString("alert.success"), bundle.getString("professor.export.success.csv"));
            } catch (IOException e) {
                showAlert(bundle.getString("alert.error"), bundle.getString("professor.export.error.csv") + e.getMessage());
            }
        }
    }


    public void setSelectedModule(Module module) {
        this.selectedModule = module;
    }
    @FXML
    private void exportProfesseursToPDF() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(bundle.getString("filechooser.save.pdf"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        fileChooser.setInitialFileName(bundle.getString("professor.list.filename.pdf"));

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try (PDDocument document = new PDDocument()) {
                PDPage page = new PDPage();
                document.addPage(page);

                PDPageContentStream contentStream = new PDPageContentStream(document, page);
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                contentStream.beginText();
                contentStream.setLeading(15f);
                contentStream.newLineAtOffset(50, 750);

                contentStream.showText(bundle.getString("professor.list.pdf.title"));
                contentStream.newLine();
                contentStream.newLine();

                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.showText(bundle.getString("professor.list.pdf.header"));
                contentStream.newLine();

                for (Professeur professeur : professeurList) {
                    String line = String.format("%-8s %-15s %-15s %-20s %-10s",
                            professeur.getId(),
                            professeur.getNom(),
                            professeur.getPrenom(),
                            professeur.getSpecialite(),
                            professeur.getUtilisateur_id());
                    contentStream.showText(line);
                    contentStream.newLine();
                }

                contentStream.endText();
                contentStream.close();

                document.save(file);
                showAlert(bundle.getString("alert.success"), bundle.getString("professor.export.success.pdf"));
            } catch (IOException e) {
                showAlert(bundle.getString("alert.error"), bundle.getString("professor.export.error.pdf") + e.getMessage());
            }
        }
    }

    private void clearFields() {
        nomField.clear();
        prenomField.clear();
        specialiteField.clear();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void updateTexts() {
        searchField.setPromptText(bundle.getString("professor.search.prompt"));
        nomField.setPromptText(bundle.getString("professor.name.prompt"));
        prenomField.setPromptText(bundle.getString("professor.firstname.prompt"));
        specialiteField.setPromptText(bundle.getString("professor.speciality.prompt"));
    }

    @FXML
    private void handleRowClick(MouseEvent event) throws IOException {
        if (event.getClickCount() == 2) {
            Professeur selectedProfesseur = professeurTable.getSelectionModel().getSelectedItem();
            if (selectedProfesseur != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/gestion/ecole/professeur/ModulesView.fxml"), bundle);
                Pane newView = loader.load();

                ModulesController controller = loader.getController();
                controller.setProfessorId(selectedProfesseur.getId());
                controller.setResourceBundle(bundle);

                if (mainController != null) {
                    mainController.setContentPane(newView);
                }
            }
        }
    }
}

