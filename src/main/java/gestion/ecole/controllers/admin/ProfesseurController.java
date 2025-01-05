package gestion.ecole.controllers.admin;


import gestion.ecole.controllers.UserAwareController;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class ProfesseurController {

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





    private final ProfesseurDAO professeurDAO = new ProfesseurDAO();
    private final ObservableList<Professeur> professeurList = FXCollections.observableArrayList();



    @FXML
    public void initialize() {
        // Utilisation de SimpleIntegerProperty pour la colonne ID
        idColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getId()).asObject()
        );

        // Utilisation de SimpleStringProperty pour les colonnes de type String
        nomColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getNom())
        );
        prenomColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPrenom())
        );
        specialiteColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getSpecialite())
        );

        // Charger les données dans la TableView
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
        int utilisateurId = Integer.parseInt(utilisateurIdField.getText());

        if (!nom.isEmpty() && !prenom.isEmpty() && !specialite.isEmpty()) {
            Professeur professeur = new Professeur(0, nom, prenom, specialite, utilisateurId);
            if (professeurDAO.insert(professeur)) {
                loadProfesseurs();
                clearFields();
            } else {
                showAlert("Erreur", "Impossible d'ajouter le professeur.");
            }
        } else {
            showAlert("Attention", "Veuillez remplir tous les champs.");
        }
    }


    @FXML
    private void updateProfesseur() {
        Professeur selected = professeurTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Mise à jour des champs du professeur
            selected.setNom(nomField.getText());
            selected.setPrenom(prenomField.getText());
            selected.setSpecialite(specialiteField.getText());
            selected.setUtilisateur_id(Integer.parseInt(utilisateurIdField.getText())); // Inclure utilisateur_id

            // Appel à la méthode update du DAO
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
            // Appel à la méthode delete du DAO
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
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
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
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try (PDDocument document = new PDDocument()) {
                // Création d'une page PDF
                PDPage page = new PDPage();
                document.addPage(page);

                // Initialisation du flux de contenu
                PDPageContentStream contentStream = new PDPageContentStream(document, page);

                // Configuration des styles
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                contentStream.beginText();
                contentStream.setLeading(15f);
                contentStream.newLineAtOffset(50, 750);

                // En-tête
                contentStream.showText("Liste des Professeurs");
                contentStream.newLine();
                contentStream.newLine();

                // Ajout des colonnes
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

                // Enregistrement du document
                document.save(file);
                showAlert("Succès", "La liste des professeurs a été exportée en PDF avec succès.");
            } catch (IOException e) {
                showAlert("Erreur", "Une erreur s'est produite lors de l'exportation : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }





}
