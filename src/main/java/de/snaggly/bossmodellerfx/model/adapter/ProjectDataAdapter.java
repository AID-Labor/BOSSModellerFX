package de.snaggly.bossmodellerfx.model.adapter;

import de.bossmodeler.logicalLayer.elements.DBColumn;
import de.bossmodeler.logicalLayer.elements.DBRelation;
import de.bossmodeler.logicalLayer.elements.DBTable;
import de.snaggly.bossmodellerfx.model.BOSSModel;
import de.snaggly.bossmodellerfx.model.subdata.Attribute;
import de.snaggly.bossmodellerfx.model.subdata.AttributeCombination;
import de.snaggly.bossmodellerfx.model.subdata.Relation;
import de.snaggly.bossmodellerfx.model.subdata.UniqueCombination;
import de.snaggly.bossmodellerfx.model.view.Entity;
import de.snaggly.bossmodellerfx.relation_logic.CrowsFootOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Structure for Project Data. This class is to be used for the DBInterface.
 *
 * TODO: Check redundancy with class "Project"
 *
 * @author Omar Emshani
 */
public class ProjectDataAdapter implements BOSSModel {
    public String projectName;
    public ArrayList<Entity> entities;
    public ArrayList<Relation> relations;

    /**
     * Converts legacy DBTable and DBRelation model to the new model used by the Views.
     * @param name ProjectName
     * @param dbTables Legacy DBTables
     * @param dbRelations Legacy DBRelations
     * @return ProjectData Holder
     */
    public static ProjectDataAdapter convertLegacyToFXModel(String name, LinkedList<DBTable> dbTables, LinkedList<DBRelation> dbRelations) {
        var projectData = new ProjectDataAdapter();
        projectData.projectName = name;
        projectData.entities = new ArrayList<>();
        projectData.relations = new ArrayList<>();

        //To map old dataset to new
        var entityMap = new HashMap<DBTable, Entity>();
        var attributeMap = new HashMap<DBColumn, Attribute>();

        for (var dbTable : dbTables) {
            //Create new entity
            var entity = new Entity();
            entity.setName(dbTable.getdBTName());
            entity.setWeakType(dbTable.isdBTWeakEntity());

            for (var dbColumn : dbTable.getdBTColumns()) {
                //Create new attribute for current entity
                var attribute = new Attribute();
                attribute.setName(dbColumn.getdBCName());
                attribute.setType(dbColumn.getdBCType());
                attribute.setCheckName(dbColumn.getdBCCheck());
                attribute.setNonNull(dbColumn.isdBCNotNull());
                attribute.setDefaultName(dbColumn.getdBCDefault());

                attributeMap.put(dbColumn, attribute);
                entity.addAttribute(attribute);
            }

            //Map unique attributes
            for (var dbColumn : dbTable.getdBTUniqueList()) {
                var attribute = attributeMap.get(dbColumn);
                if (attribute != null) {
                    attribute.setUnique(true);
                }
            }

            //Map primary attributes
            for (var dbColumn : dbTable.getdBTPKeyList()) {
                var attribute = attributeMap.get(dbColumn);
                if (attribute != null) {
                    attribute.setPrimary(true);
                }
            }

            var primaryKeys = entity.getPrimaryKeys();
            var alreadyHasPrimaryCombination = false;
            var uniqueCombo = new UniqueCombination();
            for (var legacyUniqueCombo : dbTable.getuniqueCombinations()) {
                //Create new UniqueCombination for current entity
                boolean isPrimaryUniqueList = true;
                var attributeCombo = new AttributeCombination();
                attributeCombo.setCombinationName(legacyUniqueCombo.getShortForm());
                if (attributeCombo.getCombinationName() == null)
                    attributeCombo.setCombinationName("");
                for (var dbColumn : legacyUniqueCombo.getColumns()) {
                    attributeCombo.addAttribute(attributeMap.get(dbColumn));
                }
                for (var attribute : attributeCombo.getAttributes()) {
                    if (!primaryKeys.contains(attribute)) {
                        isPrimaryUniqueList = false;
                        break;
                    }
                }
                if (isPrimaryUniqueList) {
                    if (alreadyHasPrimaryCombination)
                        continue;
                    alreadyHasPrimaryCombination = true;
                }
                attributeCombo.setPrimaryCombination(isPrimaryUniqueList);
                uniqueCombo.addCombination(attributeCombo);

            }
            entity.setUniqueCombination(uniqueCombo);

            entityMap.put(dbTable, entity);
            projectData.entities.add(entity);
        }

        for (var dbRelation : dbRelations) {
            //Create new relation
            var tableA = entityMap.get(dbRelation.getTableA());
            var tableB = entityMap.get(dbRelation.getTableB());
            var relation = new Relation(
                    tableA,
                    tableB,
                    dbRelation.getCardinalityA() == 0 ? CrowsFootOptions.Cardinality.MANY : CrowsFootOptions.Cardinality.ONE,
                    dbRelation.getCardinalityB() == 0 ? CrowsFootOptions.Cardinality.MANY : CrowsFootOptions.Cardinality.ONE,
                    dbRelation.getRelationTypeA() == 0 ? CrowsFootOptions.Obligation.MUST : CrowsFootOptions.Obligation.CAN,
                    dbRelation.getRelationTypeB() == 0 ? CrowsFootOptions.Obligation.MUST : CrowsFootOptions.Obligation.CAN
            );

            relation.setStrongRelation(dbRelation.getStrongTable() != null);
            if (dbRelation.getFkTable() == dbRelation.getTableA()) { //TableA got FKs
                for (var dbColumn : dbRelation.getFkColumn()) {
                    var fk = attributeMap.get(dbColumn);
                    fk.setFkTable(relation.getTableB()); //Adjust Table and Column, Entity already houses same Object
                    for (var foreignColumn : dbRelation.getTableB().getdBTColumns()) {
                        if (foreignColumn.getdBCName().equals(dbColumn.getdBCFKRefName())) {
                            fk.setFkTableColumn(attributeMap.get(foreignColumn));
                        }
                    }
                    if (!relation.getFkAttributesA().contains(fk))
                        relation.getFkAttributesA().add(fk);
                }
            }
            else if (dbRelation.getFkTable() == dbRelation.getTableB()) {
                for (var dbColumn : dbRelation.getFkColumn()) {
                    var fk = attributeMap.get(dbColumn);
                    fk.setFkTable(relation.getTableA()); //Adjust Table and Column, Entity already houses same Object
                    for (var foreignColumn : dbRelation.getTableA().getdBTColumns()) {
                        if (foreignColumn.getdBCName().equals(dbColumn.getdBCFKRefName())) {
                            fk.setFkTableColumn(attributeMap.get(foreignColumn));
                        }
                    }
                    if (!relation.getFkAttributesB().contains(fk))
                        relation.getFkAttributesB().add(fk);
                }
            }

            projectData.relations.add(relation);
        }

        return projectData;
    }

    /**
     * Converts Entities and Relations into the legacy DBTables and DBRelations model to be used for DBLogicalAdministration.
     * @param entities Entity list
     * @param relations Relation list
     * @return Structure for legacy models
     */
    public static DBProjectHolder convertFXToLegacyModel(ArrayList<Entity> entities, ArrayList<Relation> relations) {
        var projectHolder = new DBProjectHolder();

        //Map new dataset to old
        var columnMap = new HashMap<Attribute, DBColumn>();
        var tableMap = new HashMap<Entity, DBTable>();

        //Build Tables
        for (var entity : entities) {
            var dbTable = new DBTable(entity.getName());
            dbTable.setdBTWeakEntity(entity.isWeakType());

            //Build Columns with Primary,Unique,Foreign-List
            for (var attribute : entity.getAttributes()) {
                var dbColumn = new DBColumn(
                        attribute.getName(),
                        attribute.getType()
                );
                dbColumn.setdBCCheck(attribute.getCheckName());
                dbColumn.setdBCNotNull(attribute.isNonNull());
                dbColumn.setdBCDefault(attribute.getDefaultName());
                dbTable.addColumn(dbColumn);
                if (attribute.isUnique()) {
                    dbTable.addUniqueList(dbColumn);
                }
                if (attribute.isPrimary()) {
                    dbTable.addPKey(dbColumn);
                }
                if (attribute.getFkTable() != null) {
                    dbColumn.setdBCFKConstraintName(entity.getName()+"_");
                    dbColumn.setdBCFKRefTableName(attribute.getFkTable().getName());
                    dbColumn.setdBCFKRefName(attribute.getFkTableColumn().getName());
                    dbTable.addFKey(dbColumn);
                }

                columnMap.put(attribute, dbColumn);
            }

            //Build UniqueList
            for (var attributeCombination : entity.getUniqueCombination().getCombinations()) {
                var columnList = new LinkedList<DBColumn>();
                for (var attribute : attributeCombination.getAttributes()) {
                    columnList.add(columnMap.get(attribute));
                }
                if (attributeCombination.isPrimaryCombination()) {
                    attributeCombination.setCombinationName(null);
                }
                dbTable.addUniqueCombination(
                        new de.bossmodeler.logicalLayer.elements.UniqueCombination(columnList, attributeCombination.getCombinationName())
                );
            }

            tableMap.put(entity, dbTable);
            projectHolder.addDbTable(dbTable);
        }

        //Build Relations
        for (var relation : relations) {
            //Set Tables
            var dbRelation = new DBRelation(
                    tableMap.get(relation.getTableA()),
                    tableMap.get(relation.getTableB())
            );

            //Set Cardinality and Relation
            dbRelation.setCardinalityA(relation.getTableA_Cardinality() == CrowsFootOptions.Cardinality.ONE ? 1 : 0);
            dbRelation.setCardinalityB(relation.getTableB_Cardinality() == CrowsFootOptions.Cardinality.ONE ? 1 : 0);
            dbRelation.setRelationTypeA(relation.getTableA_Obligation() == CrowsFootOptions.Obligation.CAN ? 1 : 0);
            dbRelation.setRelationTypeB(relation.getTableB_Obligation() == CrowsFootOptions.Obligation.CAN ? 1 : 0);

            //Set strong table
            if (relation.getTableA().isWeakType())
                dbRelation.setStrongTable(dbRelation.getTableB());
            else if (relation.getTableB().isWeakType())
                dbRelation.setStrongTable(dbRelation.getTableA());

            //Set foreign Table and Keys
            var fkColumns = new LinkedList<DBColumn>();
            var constraintName = "";
            var fKeys = relation.getFkAttributesA();
            if (fKeys.size() <= 0) {
                fKeys = relation.getFkAttributesB();
            }
            for (var fk : fKeys) {
                constraintName = constraintName.concat(fk.getName()+"_");
            }
            dbRelation.setFkTable(dbRelation.getTableA());
            for (var fk : fKeys) {
                var dbColumn = columnMap.get(fk);
                dbColumn.setdBCFKConstraintName(dbColumn.getdBCFKConstraintName()+constraintName+"_fkey");
                fkColumns.add(dbColumn);
            }
            dbRelation.setFkColumn(fkColumns);

            projectHolder.addDbRelation(dbRelation);
        }

        return projectHolder;
    }
}
