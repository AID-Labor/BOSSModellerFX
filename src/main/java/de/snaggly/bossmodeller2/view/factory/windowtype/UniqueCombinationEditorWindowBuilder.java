package de.snaggly.bossmodeller2.view.factory.windowtype;

import de.snaggly.bossmodeller2.Main;
import de.snaggly.bossmodeller2.model.Attribute;
import de.snaggly.bossmodeller2.model.Entity;
import de.snaggly.bossmodeller2.model.UniqueCombination;
import de.snaggly.bossmodeller2.view.controller.EditEntityWindowController;
import de.snaggly.bossmodeller2.view.controller.EditUniqueCombinationWindowController;
import de.snaggly.bossmodeller2.view.factory.WindowFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;

public class UniqueCombinationEditorWindowBuilder implements WindowFactory<UniqueCombination, EditUniqueCombinationWindowController> {
    private static UniqueCombinationEditorWindowBuilder instance;

    private UniqueCombinationEditorWindowBuilder() { }

    @Override
    public Map.Entry<Scene, EditUniqueCombinationWindowController> buildWindow(UniqueCombination model) throws IOException {
        return buildWindow(model, null);
    }

    public Map.Entry<Scene, EditUniqueCombinationWindowController> buildWindow(ArrayList<Attribute> attributes) throws IOException {
        return buildWindow(null, attributes);
    }

    public Map.Entry<Scene, EditUniqueCombinationWindowController> buildWindow(UniqueCombination model, ArrayList<Attribute> attributes) throws IOException {
        var fxmlLoader = new FXMLLoader(Main.class.getResource("view/EditUniqueCombinationWindow.fxml"));
        var scene = new Scene(fxmlLoader.load());
        var controller = (EditUniqueCombinationWindowController)(fxmlLoader.getController());
        if (model != null) {
            controller.loadModel(model);
        }
        if (attributes != null) {
            controller.loadAttributes(attributes);
        }
        return new AbstractMap.SimpleEntry<>(scene, controller);
    }

    public static Map.Entry<Scene, EditUniqueCombinationWindowController> buildEntityEditor(UniqueCombination model, ArrayList<Attribute> attributes) throws IOException {
        if (instance == null)
            instance = new UniqueCombinationEditorWindowBuilder();
        return instance.buildWindow(model, attributes);
    }
}