package de.snaggly.bossmodellerfx.view;

import de.snaggly.bossmodellerfx.model.subdata.Relation;
import de.snaggly.bossmodellerfx.view.controller.ModelController;
import de.snaggly.bossmodellerfx.view.viewtypes.CustomNode;
import javafx.scene.Node;

import java.util.ArrayList;

public class RelationViewNode extends CustomNode<Relation> {
    private final Relation model;
    public RelationLineView line1;
    public RelationLineView line2;
    public RelationLineView line3;
    public CrowsFootShape crowsFootA;
    public CrowsFootShape crowsFootB;

    public RelationViewNode(Relation model) {
        this.model = model;
    }

    public ArrayList<Node> getAllNodes() {
        var result = new ArrayList<Node>();
        if (line1 != null)
            result.add(line1);
        if (line2 != null)
            result.add(line2);
        if (line3 != null)
            result.add(line3);
        if (crowsFootA != null) {
            result.add(crowsFootA.multiplicityLineOne);
            result.add(crowsFootA.mandatoryLine);
            result.add(crowsFootA.optionalCircle);
            result.add(crowsFootA.multiplicityLineMultiple1);
            result.add(crowsFootA.multiplicityLineMultiple2);
        }
        if (crowsFootB != null) {
            result.add(crowsFootB.multiplicityLineOne);
            result.add(crowsFootB.mandatoryLine);
            result.add(crowsFootB.optionalCircle);
            result.add(crowsFootB.multiplicityLineMultiple1);
            result.add(crowsFootB.multiplicityLineMultiple2);
        }

        return result;
    }

    @Override
    public void toBack() {
        for (var node : crowsFootA.getAllNodes())
            node.toBack();

        for (var node : crowsFootB.getAllNodes())
            node.toBack();

        line1.toBack();
        line2.toBack();
        line3.toBack();
    }

    @Override
    public Relation getModel() {
        return model;
    }

    @Override
    public ModelController<Relation> getController() {
        return null;
    }

    @Override
    public void setFocusStyle() {
        if (crowsFootA != null)
            crowsFootA.highlight();
        if (crowsFootB != null)
            crowsFootB.highlight();
        if (line1 != null)
            line1.highlight();
        if (line2 != null)
            line2.highlight();
        if (line3 != null)
            line3.highlight();
    }

    @Override
    public void setDeFocusStyle() {
        if (crowsFootA != null)
            crowsFootA.deHighlight();
        if (crowsFootB != null)
            crowsFootB.deHighlight();
        if (line1 != null)
            line1.deHighlight();
        if (line2 != null)
            line2.deHighlight();
        if (line3 != null)
            line3.deHighlight();
    }
}
