package de.snaggly.bossmodellerfx.view.controller;

import de.bossmodeler.logicalLayer.elements.DBInterfaceCommunication;
import de.bossmodeler.logicalLayer.elements.DBLogicalAdministration;
import de.snaggly.bossmodellerfx.BOSS_Strings;
import de.snaggly.bossmodellerfx.guiLogic.GUIActionListener;
import de.snaggly.bossmodellerfx.guiLogic.GUIMethods;
import de.snaggly.bossmodellerfx.model.adapter.DBLAHolder;
import de.snaggly.bossmodellerfx.model.adapter.ProjectDataAdapter;
import de.snaggly.bossmodellerfx.model.adapter.SQLInterface;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableNumberValue;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.LinkedList;

/**
 * Controller for DB Import Tables Window
 *
 * @author Omar Emshani
 */
public class ChooseDBImportWindowController implements ModelController<DBLAHolder> {
    public GUIActionListener<ProjectDataAdapter> parentObserver;

    private String projectName = "";

    @FXML
    private Label schemeNameLabel;
    @FXML
    private ChoiceBox<String> chooseDBNameChoiceBox;
    @FXML
    private ChoiceBox<String> chooseSchemaNameChoiceBox;
    @FXML
    private ProgressIndicator progressIndicator;

    private DBLAHolder localDBLA;
    private boolean selectionTrigger = false;

    @FXML
    public VBox entityListVBox;
    @FXML
    private CheckBox checkAllChkbox;
    @FXML
    private Button cancelBtn;
    @FXML
    private Button createBtn;

    @FXML
    private void initialize() {
        createBtn.setDisable(true);
        progressIndicator.setVisible(false);
    }

    @FXML
    private void onCheckAllChkBox(ActionEvent actionEvent) {
        selectionTrigger = true;
        for (var checkBox : entityListVBox.getChildren()) {
            if (checkBox instanceof CheckBox) {
                ((CheckBox) checkBox).setSelected(checkAllChkbox.isSelected());
            }
        }
        selectionTrigger = false;
    }

    @FXML
    private void onCancelClick(ActionEvent actionEvent) {
        GUIMethods.closeWindow(actionEvent);
    }

    @FXML
    private void onCreateClick(ActionEvent actionEvent) {
        var selectedEntities = new LinkedList<String>();
        for (var checkBox : entityListVBox.getChildren()) {
            if (checkBox instanceof CheckBox && ((CheckBox)checkBox).isSelected()) {
                selectedEntities.add(((CheckBox) checkBox).getText());
            }
        }

        try {
            localDBLA.getDbla().initializeSome(selectedEntities);
            localDBLA.getDbla().initializeRelations();

            parentObserver.notify(ProjectDataAdapter.convertLegacyToFXModel(
                    projectName,
                    localDBLA.getDbla().getTables(),
                    localDBLA.getDbla().getRelations()));
        } catch (SQLException e) {
            GUIMethods.showError(BOSS_Strings.DB_CONNECTOR, BOSS_Strings.DBINTERFACE_ERROR_READING_TABLES, e.getLocalizedMessage());
        }
    }

    @Override
    public void loadModel(DBLAHolder model) {
        localDBLA = model;
        entityListVBox.getChildren().clear();
        try {
            if (SQLInterface.getSQLInterfaceDescriptor(model.getLanguage()).isSchemaCompatible()) {
                chooseDBNameChoiceBox.getSelectionModel().selectedItemProperty().addListener(chooseDBNameChoiceBoxListener);
                chooseSchemaNameChoiceBox.getSelectionModel().selectedItemProperty().addListener(chooseSchemaNameChoiceBoxListener);
            } else {
                chooseSchemaNameChoiceBox.setVisible(false);
                schemeNameLabel.setVisible(false);
                chooseDBNameChoiceBox.getSelectionModel().selectedItemProperty().addListener(chooseDBNameChoiceBoxListener_ForSchemeIncompatibles);
            }
            var databaseList = localDBLA.getDbla().getAllDatabase();
            if (databaseList.size() <= 0) {
                chooseDBNameChoiceBox.getItems().add(localDBLA.getDb());
                chooseDBNameChoiceBox.setDisable(true);
            } else {
                chooseDBNameChoiceBox.getItems().addAll(databaseList);
            }
            chooseDBNameChoiceBox.getSelectionModel().selectFirst();
        } catch (SQLException e) {
            GUIMethods.showError(BOSS_Strings.DB_CONNECTOR, BOSS_Strings.DBINTERFACE_ERROR_READING_TABLES, e.getLocalizedMessage());
            GUIMethods.closeWindow(entityListVBox.getScene().getWindow());
        }
    }

    private void fillTables() {
        progressIndicator.setVisible(true);
        createBtn.setDisable(true);
        checkAllChkbox.setSelected(false);
        entityListVBox.getChildren().clear();
        new Thread(() -> {
            try {
                localDBLA.setDbla(new DBLogicalAdministration(new DBInterfaceCommunication(SQLInterface.getDbDriverInterface(
                        localDBLA.getLanguage(),
                        localDBLA.getHost(),
                        localDBLA.getPort(),
                        localDBLA.getDb(),
                        localDBLA.getUser(),
                        localDBLA.getPass(),
                        localDBLA.getSchema()
                ))));
                var tableNames = localDBLA.getDbla().getTableNames();
                Platform.runLater(() -> {
                    createBtn.setDisable(false);
                    for (var tableName : tableNames) {
                        var checkBox = new CheckBox(tableName);
                        checkBox.selectedProperty().addListener((observableValue2, aBoolean, newValue) -> {
                            if (selectionTrigger)
                                return;
                            boolean hasSelectedAll = true;
                            for (var vboxCheckBox : entityListVBox.getChildren()) {
                                if (vboxCheckBox instanceof CheckBox && !((CheckBox)vboxCheckBox).isSelected()) {
                                    hasSelectedAll = false;
                                    break;
                                }
                            }
                            checkAllChkbox.setSelected(hasSelectedAll);
                        });
                        entityListVBox.getChildren().add(checkBox);
                    }
                });
            }
            catch (SQLException e) {
                GUIMethods.showError(BOSS_Strings.DB_CONNECTOR, BOSS_Strings.DBINTERFACE_ERROR_READING_TABLES, e.getLocalizedMessage());
                GUIMethods.closeWindow(entityListVBox.getScene().getWindow());
            }
            finally {
                Platform.runLater(() -> progressIndicator.setVisible(false));
            }
        }).start();
    }

    private void fillSchemes() {
        progressIndicator.setVisible(true);
        createBtn.setDisable(true);
        entityListVBox.getChildren().clear();
        new Thread(() -> {
            try {
                localDBLA.setDbla(new DBLogicalAdministration(new DBInterfaceCommunication(SQLInterface.getDbDriverInterface(
                        localDBLA.getLanguage(),
                        localDBLA.getHost(),
                        localDBLA.getPort(),
                        localDBLA.getDb(),
                        localDBLA.getUser(),
                        localDBLA.getPass(),
                        ""
                ))));

                var dbSchemas = localDBLA.getDbla().getAllDBSchemata(localDBLA.getDb());
                Platform.runLater(() -> {
                    chooseSchemaNameChoiceBox.getItems().clear();
                    chooseSchemaNameChoiceBox.getItems().addAll(dbSchemas);
                    chooseSchemaNameChoiceBox.getSelectionModel().selectFirst();
                });
            }
            catch (SQLException e) {
                GUIMethods.showError(BOSS_Strings.DB_CONNECTOR, BOSS_Strings.DBINTERFACE_ERROR_READING_TABLES, e.getLocalizedMessage());
                GUIMethods.closeWindow(entityListVBox.getScene().getWindow());
            } finally {
                Platform.runLater(() -> progressIndicator.setVisible(false));
            }
        }).start();
    }

    private final ChangeListener<String> chooseSchemaNameChoiceBoxListener = new ChangeListener<String>() {
        @Override
        public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
            if (t1 == null)
                return;
            projectName = t1;
            localDBLA.setSchema(t1);
            fillTables();
        }
    };

    private final ChangeListener<String> chooseDBNameChoiceBoxListener_ForSchemeIncompatibles = new ChangeListener<>() {
        @Override
        public void changed(ObservableValue<? extends String> observableValue, String string, String t1) {
            projectName = t1;
            localDBLA.setDb(t1);
            localDBLA.setSchema("");
            fillTables();
        }
    };

    private final ChangeListener<String> chooseDBNameChoiceBoxListener = new ChangeListener<>() {
        @Override
        public void changed(ObservableValue<? extends String> observableValue, String string, String t1) {
            projectName = t1;
            localDBLA.setDb(t1);
            fillSchemes();
        }
    };
}
