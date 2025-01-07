package gestion.ecole.controllers.admin;


import gestion.ecole.controllers.MainController;
import gestion.ecole.controllers.MainControllerAware;
import gestion.ecole.controllers.UserAwareController;
import gestion.ecole.controllers.professeur.ModulesController;
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
import gestion.ecole.models.Module;
import gestion.ecole.dao.ModuleDAO;
import gestion.ecole.dao.ProfesseurDAO;
import gestion.ecole.models.Professeur;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.cert.PolicyNode;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class ProfesseurController implements MainControllerAware {

    @FXML
    private TextField nomField;

    @FXML
    private TextField prenomField;

    @FXML
    private TextField specialiteField;

    @FXML
    private TextField utilisateurIdField;



    @FXML
    private TableView<Professeur> professeurTable;

    @FXML
    private TableColumn<Professeur, Integer> idColumn;

    @FXML
    private TableColumn<Professeur, String> nomColumn;

    @FXML
    private TableColumn<Professeur, String> prenomColumn;

    @FXML
    private TableColumn<Professeur, String> specialiteColumn;

    @FXML
    private Button addButton;

    @FXML
    private Button updateButton;

    @FXML
    private Button deleteButton;

    @FXML
    private TextField searchField;


    private MainController mainController;




    private final ProfesseurDAO professeurDAO = new ProfesseurDAO();
    private final ObservableList<Professeur> professeurList = FXCollections.observableArrayList();



    @FXML
    public void initialize() {

        idColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getId()).asObject()
        );

        nomColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getNom())
        );
        prenomColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPrenom())
        );
        specialiteColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getSpecialite())
        );

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
                String username = nom + prenom;
                String password = "password";
                String role = "professor";

                UtilisateurService utilisateurService = new UtilisateurService();
                Utilisateur utilisateur = utilisateurService.addUser(username, password, role);

                if (utilisateur != null) {
                    int utilisateurId = utilisateur.getId();
                    Professeur professeur = new Professeur(0, nom, prenom, specialite, utilisateurId);
                    if (professeurDAO.insert(professeur)) {
                        loadProfesseurs();
                        clearFields();
                        showAlert("Succès", "Le professeur a été ajouté avec succès.");
                    } else {
                        showAlert("Erreur", "Impossible d'ajouter le professeur.");
                    }
                } else {
                    showAlert("Erreur", "Impossible de créer l'utilisateur.");
                }
            } catch (Exception e) {
                showAlert("Erreur", "Une erreur s'est produite : " + e.getMessage());
            }
        } else {
            showAlert("Attention", "Veuillez remplir tous les champs.");
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
                showAlert("Succès", "Le professeur a été mis à jour avec succès.");
            } else {
                showAlert("Erreur", "Impossible de mettre à jour le professeur.");
            }
        } else {
            showAlert("Attention", "Veuillez sélectionner un professeur.");
        }
    }


    @FXML
    private void deleteProfesseur() {
        Professeur selected = professeurTable.getSelectionModel().getSelectedItem();
        if (selected != null) {

            if (professeurDAO.delete(selected.getId())) {
                loadProfesseurs();
                clearFields();
                showAlert("Succès", "Le professeur a été supprimé avec succès.");
            } else {
                showAlert("Erreur", "Impossible de supprimer le professeur.");
            }
        } else {
            showAlert("Attention", "Veuillez sélectionner un professeur.");
        }
    }


    private void clearFields() {
        nomField.clear();
        prenomField.clear();
        specialiteField.clear();
    }

    private void showAlert(String title, String content) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle(title);

        Label contentLabel = new Label(content);
        contentLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495e;");

        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType);

        dialog.getDialogPane().setContent(contentLabel);
        dialog.getDialogPane().setStyle("-fx-background-color: #ffffff; -fx-border-color: #dcdcdc; -fx-border-width: 1;");
        dialog.showAndWait();
    }


    @Override
    public void setMainController(MainController mainController) {
         this.mainController = mainController;
    }

    @FXML
    private void handleRowClick(MouseEvent event) throws IOException {
        if (event.getClickCount() == 2) {
            Professeur selectedProfesseur = professeurTable.getSelectionModel().getSelectedItem();
            if (selectedProfesseur != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/gestion/ecole/professeur/ModulesView.fxml"));
                Pane newView = loader.load();

                Object controller = loader.getController();
                if (controller instanceof ModulesController) {
                    ((ModulesController) controller).setProfessorId(selectedProfesseur.getId());
                }

                if (mainController != null) {
                    mainController.setContentPane(newView);
                }
            }
        }
    }

    @FXML
    private void searchProfesseurs() {
        String keyword = searchField.getText();
        if (!keyword.isEmpty()) {
            List<Professeur> searchResults = professeurDAO.searchProfesseurs(keyword);
            professeurList.setAll(searchResults);
            professeurTable.setItems(professeurList);
        } else {
            loadProfesseurs();
        }
    }



    private Module selectedModule;

    public void setSelectedModule(Module module) {
        this.selectedModule = module;
    }


    private final ModuleDAO moduleDAO = new ModuleDAO();

    @FXML
    private void assignProfesseurToModule() {
        Professeur selectedProfesseur = professeurTable.getSelectionModel().getSelectedItem();
        if (selectedProfesseur != null && selectedModule != null) {
            selectedModule.setProfesseurId(selectedProfesseur.getId());
            if (moduleDAO.update(selectedModule)) {
                showAlert("Succès", "Professeur assigné avec succès !");
                Stage stage = (Stage) professeurTable.getScene().getWindow();
                stage.close();
            } else {
                showAlert("Erreur", "Impossible d'assigner le professeur.");
            }
        } else {
            showAlert("Avertissement", "Veuillez sélectionner un professeur.");
        }
    }


    @FXML
    private void exportProfesseursToCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer la liste des professeurs");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers CSV", "*.csv"));
        fileChooser.setInitialFileName("Liste_Professeurs"  + ".pdf");
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                // En-tête du CSV
                writer.write("ID,Nom,Prénom,Spécialité,Utilisateur ID");
                writer.newLine();

                for (Professeur professeur : professeurList) {
                    writer.write(professeur.getId() + "," + professeur.getNom() + "," + professeur.getPrenom() + "," + professeur.getSpecialite() + "," + professeur.getUtilisateur_id());
                    writer.newLine();
                }


                showAlert("Succès", "La liste des professeurs a été exportée avec succès.");
            } catch (IOException e) {
                showAlert("Erreur", "Une erreur s'est produite lors de l'exportation : " + e.getMessage());
            }
        }
    }

    @FXML
    private void exportProfesseursToPDF() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer la liste des professeurs");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));
        fileChooser.setInitialFileName("Liste_Professeurs"  + ".pdf");

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

                contentStream.showText("Liste des Professeurs");
                contentStream.newLine();
                contentStream.newLine();

                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.showText("ID      Nom           Prénom       Spécialité    Utilisateur ID");
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
                showAlert("Succès", "La liste des professeurs a été exportée en PDF avec succès.");
            } catch (IOException e) {
                showAlert("Erreur", "Une erreur s'est produite lors de l'exportation : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }





}
