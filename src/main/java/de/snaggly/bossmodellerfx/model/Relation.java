package de.snaggly.bossmodellerfx.model;

import de.snaggly.bossmodellerfx.struct.relations.ConnectingOrientation;

import java.util.ArrayList;

public class Relation extends DataModel{
    private Entity tableA;
    private Entity tableB;
    private Cardinality tableA_Cardinality;
    private Cardinality tableB_Cardinality;
    private Obligation tableA_Obligation;
    private Obligation tableB_Obligation;
    public ConnectingOrientation orientation;

    public Relation(String name, Entity tableA, Entity tableB, Cardinality tableA_Cardinality, Cardinality tableB_Cardinality, Obligation tableA_Obligation, Obligation tableB_Obligation) {
        super(name, 0.0, 0.0);
        this.tableA = tableA;
        this.tableB = tableB;
        this.tableA_Cardinality = tableA_Cardinality;
        this.tableB_Cardinality = tableB_Cardinality;
        this.tableA_Obligation = tableA_Obligation;
        this.tableB_Obligation = tableB_Obligation;
    }

    public Relation(String name, double xCoordinate, double yCoordinate, Entity tableA, Entity tableB, Cardinality tableA_Cardinality, Cardinality tableB_Cardinality, Obligation tableA_Obligation, Obligation tableB_Obligation) {
        super(name, xCoordinate, yCoordinate);
        this.tableA = tableA;
        this.tableB = tableB;
        this.tableA_Cardinality = tableA_Cardinality;
        this.tableB_Cardinality = tableB_Cardinality;
        this.tableA_Obligation = tableA_Obligation;
        this.tableB_Obligation = tableB_Obligation;
    }

    public Entity getTableA() {
        return tableA;
    }

    public void setTableA(Entity tableA) {
        this.tableA = tableA;
    }

    public Entity getTableB() {
        return tableB;
    }

    public void setTableB(Entity tableB) {
        this.tableB = tableB;
    }

    public Cardinality getTableA_Cardinality() {
        return tableA_Cardinality;
    }

    public void setTableA_Cardinality(Cardinality tableA_Cardinality) {
        this.tableA_Cardinality = tableA_Cardinality;
    }

    public Cardinality getTableB_Cardinality() {
        return tableB_Cardinality;
    }

    public void setTableB_Cardinality(Cardinality tableB_Cardinality) {
        this.tableB_Cardinality = tableB_Cardinality;
    }

    public Obligation getTableA_Obligation() {
        return tableA_Obligation;
    }

    public void setTableA_Obligation(Obligation tableA_Obligation) {
        this.tableA_Obligation = tableA_Obligation;
    }

    public Obligation getTableB_Obligation() {
        return tableB_Obligation;
    }

    public void setTableB_Obligation(Obligation tableB_Obligation) {
        this.tableB_Obligation = tableB_Obligation;
    }

    public Attribute getFkAttributeA() {
        return getFkAttribute(tableA.getAttributes(), tableB.getPrimaryKey());
    }

    public Attribute getFkAttributeB() {
        return getFkAttribute(tableB.getAttributes(), tableA.getPrimaryKey());
    }

    public Attribute getFkAttributeA(Attribute foreignPrimaryKey) {
        return getFkAttribute(tableA.getAttributes(), foreignPrimaryKey);
    }

    public Attribute getFkAttributeB(Attribute foreignPrimaryKey) {
        return getFkAttribute(tableB.getAttributes(), foreignPrimaryKey);
    }

    private Attribute getFkAttribute(ArrayList<Attribute> attributes, Attribute primaryKey) {
        for (var attribute : attributes) {
            if (attribute.getFkTableColumn() != null && attribute.getFkTableColumn().equals(primaryKey))
                return attribute;
        }
        return null;
    }

    public enum Cardinality {
        ONE,
        MANY
    }

    public enum Obligation {
        CAN,
        MUST
    }
}
