package de.snaggly.bossmodellerfx.view.factory.windowtype;

import de.snaggly.bossmodellerfx.Main;
import de.snaggly.bossmodellerfx.model.adapter.DBLAHolder;
import de.snaggly.bossmodellerfx.view.controller.ChooseDBEntityWindowController;
import de.snaggly.bossmodellerfx.view.controller.ChooseDBExportWindowController;
import de.snaggly.bossmodellerfx.view.factory.WindowFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map;

public class ChooseDBExportWindowBuilder implements WindowFactory<DBLAHolder, ChooseDBExportWindowController> {
    private final static ChooseDBExportWindowBuilder instance = new ChooseDBExportWindowBuilder();

    private ChooseDBExportWindowBuilder() {}

    @Override
    public Map.Entry<Scene, ChooseDBExportWindowController> buildWindow(DBLAHolder model) throws IOException {
        var fxmlLoader = new FXMLLoader(Main.class.getResource("view/ChooseDBExportWindow.fxml"));
        var scene = new Scene(fxmlLoader.load());
        var controller = (ChooseDBExportWindowController)(fxmlLoader.getController());
        controller.loadModel(model);

        return new AbstractMap.SimpleEntry<>(scene, controller);
    }

    public static Map.Entry<Scene, ChooseDBExportWindowController> buildDBChooserWindow(DBLAHolder model) throws IOException {
        return instance.buildWindow(model);
    }
}