package de.snaggly.bossmodellerfx.view.factory.nodetype;

import de.snaggly.bossmodellerfx.guiLogic.GUIMethods;
import de.snaggly.bossmodellerfx.guiLogic.SelectionHandler;
import de.snaggly.bossmodellerfx.model.view.Comment;
import de.snaggly.bossmodellerfx.view.CommentView;
import de.snaggly.bossmodellerfx.view.factory.ViewFactory;
import javafx.scene.layout.Region;

import java.io.IOException;

/**
 * Builds the view to display a Comment on workbench.
 *
 * @author Omar Emshani
 */
public class CommentBuilder implements ViewFactory<Comment, CommentView> {

    private final SelectionHandler selectionHandler;
    private final Region parentRegion;

    private CommentBuilder(Region parentRegion, SelectionHandler selectionHandler) {
        this.selectionHandler = selectionHandler;
        this.parentRegion = parentRegion;
    }

    /**
     * Use this method to build a new view.
     * @param commentModel Existing model to load on view.
     * @param parentRegion Required to make the view movable across given region.
     * @param selectionHandler Required to make the view controllable.
     * @return Returns the new view class.
     * @throws IOException
     */
    public static CommentView buildComment(Comment commentModel, Region parentRegion, SelectionHandler selectionHandler) throws IOException {
        var builder = new CommentBuilder(parentRegion, selectionHandler);

        return builder.buildView(commentModel);
    }

    @Override
    public CommentView buildView(Comment model) throws IOException {
        return new CommentView(model) {
            @Override
            public void setOnClick() {
                GUIMethods.enableClick(this, selectionHandler);
            }

            @Override
            public void setDraggable() {
                GUIMethods.enableDrag(this, parentRegion, model);
            }

            @Override
            public void makeResizable() {
                GUIMethods.enableResizeable(this, model);
            }
        };
    }
}
