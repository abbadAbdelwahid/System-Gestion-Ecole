package gestion.ecole.controllers.admin;


import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import gestion.ecole.dao.ModuleDAO;
import gestion.ecole.dao.ProfesseurDAO;
import gestion.ecole.models.Module;
import gestion.ecole.models.Professeur;


import java.io.IOException;
import java.util.List;

public class ModuleController {
    @FXML
    private TextField nomField, codeField, professeurIdField;

    @FXML
    private TextField searchField;

    @FXML
    private TableView<Module> moduleTable;

    @FXML
    private TableColumn<Module, Integer> idColumn, professeurIdColumn;
    @FXML
    private TableColumn<Module, String> nomColumn, codeColumn;
    @FXML
    private TableColumn<Module, Void> actionColumn;

    @FXML
    private TableColumn<Module, Void> detailColumn;

    private final ModuleDAO moduleDAO = new ModuleDAO();
    private final ObservableList<Module> moduleList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        nomColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getNomModule()));
        codeColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCodeModule()));
        professeurIdColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getProfesseurId()).asObject());
        addButtonToTable();
        addDetailButtonToTable();

        loadModules();
    }
    @FXML
    private void loadModules() {
        moduleList.setAll(moduleDAO .getAll());
        moduleTable.setItems(moduleList);
    }

    @FXML
    private void addModule() {
        Module module = new Module(0, nomField.getText(), codeField.getText(), Integer.parseInt(professeurIdField.getText()));
        if (moduleDAO .insert(module)) {
            loadModules();
            clearFields();
        }
    }

    @FXML
    private void updateModule() {
        Module selected = moduleTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Mise à jour des champs du module
            selected.setNomModule(nomField.getText());
            selected.setCodeModule(codeField.getText());
            selected.setProfesseurId(Integer.parseInt(professeurIdField.getText())); // Inclure professeur_id

            // Appel à la méthode update du DAO
            if (moduleDAO.update(selected)) {
                loadModules();
                clearFields();
                showAlert("Succès", "Le module a été mis à jour avec succès.");
            } else {
                showAlert("Erreur", "Impossible de mettre à jour le module.");
            }
        } else {
            showAlert("Attention", "Veuillez sélectionner un module.");
        }
    }


    @FXML
    private void deleteModule() {
        Module selected = moduleTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Appel à la méthode delete du DAO
            if (moduleDAO.delete(selected.getId())) {
                loadModules();
                clearFields();
                showAlert("Succès", "Le module a été supprimé avec succès.");
            } else {
                showAlert("Erreur", "Impossible de supprimer le module.");
            }
        } else {
            showAlert("Attention", "Veuillez sélectionner un module.");
        }
    }


    private void clearFields() {
        nomField.clear();
        codeField.clear();
        professeurIdField.clear();
    }

    @FXML
    private void searchModules() {
        String keyword = searchField.getText();
        if (!keyword.isEmpty()) {
            List<Module> searchResults = moduleDAO.searchModulesByName(keyword);
            moduleList.setAll(searchResults);
            moduleTable.setItems(moduleList);
        } else {
            loadModules(); // Recharge tous les modules si le champ de recherche est vide
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }


    private void addButtonToTable() {
        Callback<TableColumn<Module, Void>, TableCell<Module, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Module, Void> call(final TableColumn<Module, Void> param) {
                return new TableCell<>() {
                    private final Button btn = new Button("Ajouter un professeur");

                    {
                        btn.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-weight: bold;");
                        btn.setOnAction(event -> {
                            Module module = getTableView().getItems().get(getIndex());
                            openProfesseurSelection(module);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
            }
        };

        actionColumn.setCellFactory(cellFactory);
    }


    private void openProfesseurSelection(Module module) {
        if (module != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/gestion/ecole/admin/ProfesseursView.fxml"));
                Parent root = loader.load();

                ProfesseurController professeurController = loader.getController();
                professeurController.setSelectedModule(module);

                Stage stage = new Stage();
                stage.setTitle("Sélectionner un professeur");
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                System.out.println("Erreur lors de l'ouverture de la vue des professeurs : " + e.getMessage());
            }
        }
    }

    private void addDetailButtonToTable() {
        Callback<TableColumn<Module, Void>, TableCell<Module, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Module, Void> call(final TableColumn<Module, Void> param) {
                return new TableCell<>() {
                    private final Button btn = new Button("Détails du professeur");

                    {
                        btn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
                        btn.setOnAction(event -> {
                            Module module = getTableView().getItems().get(getIndex());
                            showProfesseurDetails(module);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
            }
        };

        detailColumn.setCellFactory(cellFactory);
    }

    private void showProfesseurDetails(Module module) {
        if (module.getProfesseurId() == 0) {
            showAlert("Information", "Aucun professeur n'est assigné à ce module.");
            return;
        }

        try {
            ProfesseurDAO professeurDAO = new ProfesseurDAO(); // Utilisation de ProfesseurDAO
            Professeur professeur = professeurDAO.get(module.getProfesseurId()); // Appel à la méthode get()

            if (professeur != null) {
                // Fenêtre de détails
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Détails du professeur");
                alert.setHeaderText("Informations sur le professeur");
                alert.setContentText(
                        "Nom : " + professeur.getNom() + "\n" +
                                "Prénom : " + professeur.getPrenom() + "\n" +
                                "Spécialité : " + professeur.getSpecialite() + "\n" +
                                "Utilisateur ID : " + professeur.getUtilisateur_id() // Ajout de utilisateur_id
                );
                alert.showAndWait();
            } else {
                showAlert("Erreur", "Impossible de récupérer les détails du professeur.");
            }
        } catch (Exception e) {
            showAlert("Erreur", "Une erreur est survenue : " + e.getMessage());
        }
    }




}
