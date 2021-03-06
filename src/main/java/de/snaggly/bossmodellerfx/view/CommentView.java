package de.snaggly.bossmodellerfx.view;

import de.snaggly.bossmodellerfx.BOSS_Strings;
import de.snaggly.bossmodellerfx.model.view.Comment;
import de.snaggly.bossmodellerfx.view.controller.CommentController;
import de.snaggly.bossmodellerfx.view.viewtypes.Controllable;
import de.snaggly.bossmodellerfx.view.viewtypes.CustomNode;
import de.snaggly.bossmodellerfx.view.viewtypes.Draggable;
import de.snaggly.bossmodellerfx.view.viewtypes.Resizable;
import javafx.fxml.FXMLLoader;

import java.io.IOException;

/**
 * Comment View-Class to be used in a Workbench. Displays a Comment from a Project.
 * This class is realized by a Factory.
 *
 * TODO: Try to redesign this like at Post-It Note
 *
 * @author Omar Emshani
 */
public abstract class CommentView extends CustomNode<Comment> implements Draggable, Controllable, Resizable {
    private final Comment model;

    private final CommentController controller;

    public CommentView(Comment comment) throws IOException {
        this.model = comment;
        FXMLLoader fxmlLoader = new FXMLLoader(EntityView.class.getResource("Comment.fxml"), BOSS_Strings.resourceBundle);
        fxmlLoader.setRoot(this);
        fxmlLoader.load();
        controller = fxmlLoader.getController();
        controller.loadModel(comment);

        this.setMinHeight(model.getHeight());
        this.setMinWidth(model.getWidth());

        setOnClick();
        setDraggable();
        makeResizable();

        this.setLayoutX(model.getXCoordinate());
        this.setLayoutY(model.getYCoordinate());
    }

    @Override
    public Comment getModel() {
        return model;
    }

    @Override
    public CommentController getController() {
        return controller;
    }

    @Override
    public void setFocusStyle() {
        if (!controller.isEditable())
            this.setStyle("-fx-opacity: 1.0; -fx-effect: dropshadow(three-pass-box, rgba(3,158,211,0.8), 17, 0, 0, 0);");
    }

    @Override
    public void setDeFocusStyle() {
        if (!controller.isEditable())
            this.setStyle("-fx-opacity: 1.0;");
    }
}
