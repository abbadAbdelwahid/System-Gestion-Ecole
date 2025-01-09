package gestion.ecole.controllers.admin;

import gestion.ecole.controllers.BundleAware;
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
import java.util.Optional;
import java.util.ResourceBundle;

public class ModuleController implements BundleAware {

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
    private ResourceBundle bundle;

    @Override
    public void setResourceBundle(ResourceBundle bundle) {
        this.bundle = bundle;
        updateTexts();
    }

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
        moduleList.setAll(moduleDAO.getAll());
        moduleTable.setItems(moduleList);
    }
    @FXML
    private void addModule() {
        // Vérification des champs vides
        String nom = nomField.getText();
        String code = codeField.getText();
        String professeurIdText = professeurIdField.getText();

        if (nom.isEmpty() || code.isEmpty() || professeurIdText.isEmpty()) {
            showAlert(bundle.getString("alert.warning"), bundle.getString("module.fill.fields"));
            return; // Arrêter l'exécution si des champs sont vides
        }

        try {
            int professeurId = Integer.parseInt(professeurIdText);
            Module module = new Module(0, nom, code, professeurId);

            // Afficher une boîte de dialogue de confirmation
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle(bundle.getString("alert.confirmation"));
            confirmationAlert.setHeaderText(bundle.getString("module.add.confirm"));
            confirmationAlert.setContentText(bundle.getString("module.add.prompt"));

            ButtonType okButton = new ButtonType(bundle.getString("button.ok"), ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButton = new ButtonType(bundle.getString("button.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);
            confirmationAlert.getButtonTypes().setAll(okButton, cancelButton);

            Optional<ButtonType> result = confirmationAlert.showAndWait();
            if (result.isPresent() && result.get() == okButton) {
                // Effectuer l'ajout si confirmé
                if (moduleDAO.insert(module)) {
                    loadModules();
                    clearFields();
                    showAlert(bundle.getString("alert.success"), bundle.getString("module.add.success"));
                } else {
                    showAlert(bundle.getString("alert.error"), bundle.getString("module.add.error"));
                }
            }
        } catch (NumberFormatException e) {
            showAlert(bundle.getString("alert.error"), bundle.getString("module.invalid.professor.id"));
        } catch (Exception e) {
            showAlert(bundle.getString("alert.error"), bundle.getString("generic.error") + e.getMessage());
        }
    }


    @FXML
    private void updateModule() {
        Module selected = moduleTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setNomModule(nomField.getText());
            selected.setCodeModule(codeField.getText());
            selected.setProfesseurId(Integer.parseInt(professeurIdField.getText()));

            if (moduleDAO.update(selected)) {
                loadModules();
                clearFields();
                showAlert(bundle.getString("alert.success"), bundle.getString("module.update.success"));
            } else {
                showAlert(bundle.getString("alert.error"), bundle.getString("module.update.error"));
            }
        } else {
            showAlert(bundle.getString("alert.warning"), bundle.getString("module.select"));
        }
    }

    @FXML
    private void deleteModule() {
        Module selected = moduleTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (moduleDAO.delete(selected.getId())) {
                loadModules();
                clearFields();
                showAlert(bundle.getString("alert.success"), bundle.getString("module.delete.success"));
            } else {
                showAlert(bundle.getString("alert.error"), bundle.getString("module.delete.error"));
            }
        } else {
            showAlert(bundle.getString("alert.warning"), bundle.getString("module.select"));
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
            loadModules();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void addButtonToTable() {
        Callback<TableColumn<Module, Void>, TableCell<Module, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Module, Void> call(final TableColumn<Module, Void> param) {
                return new TableCell<>() {
                    private final Button btn = new Button(bundle.getString("module.action.addProfesseur"));

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
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/gestion/ecole/admin/ProfesseursView.fxml"), bundle);
                Parent root = loader.load();

                ProfesseurController professeurController = loader.getController();
                professeurController.setSelectedModule(module);
                professeurController.setResourceBundle(bundle);

                Stage stage = new Stage();
                stage.setTitle(bundle.getString("professor.select.title"));
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                showAlert(bundle.getString("alert.error"), bundle.getString("professor.select.error"));
            }
        }
    }

    private void addDetailButtonToTable() {
        Callback<TableColumn<Module, Void>, TableCell<Module, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Module, Void> call(final TableColumn<Module, Void> param) {
                return new TableCell<>() {
                    private final Button btn = new Button(bundle.getString("module.action.details"));

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
            showAlert(bundle.getString("alert.info"), bundle.getString("module.no.professor"));
            return;
        }

        try {
            ProfesseurDAO professeurDAO = new ProfesseurDAO();
            Professeur professeur = professeurDAO.get(module.getProfesseurId());

            if (professeur != null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(bundle.getString("professor.details.title"));
                alert.setHeaderText(bundle.getString("professor.details.header"));
                alert.setContentText(
                        bundle.getString("professor.details.name") + ": " + professeur.getNom() + "\n" +
                                bundle.getString("professor.details.prenom") + ": " + professeur.getPrenom() + "\n" +
                                bundle.getString("professor.details.specialite") + ": " + professeur.getSpecialite()
                );
                alert.showAndWait();
            } else {
                showAlert(bundle.getString("alert.error"), bundle.getString("professor.details.error"));
            }
        } catch (Exception e) {
            showAlert(bundle.getString("alert.error"), bundle.getString("professor.details.error") + e.getMessage());
        }
    }

    private void updateTexts() {
        // Update any text in the UI dynamically
        searchField.setPromptText(bundle.getString("module.search.prompt"));
        nomField.setPromptText(bundle.getString("module.name.prompt"));
        codeField.setPromptText(bundle.getString("module.code.prompt"));
        professeurIdField.setPromptText(bundle.getString("module.professorId.prompt"));
    }
}