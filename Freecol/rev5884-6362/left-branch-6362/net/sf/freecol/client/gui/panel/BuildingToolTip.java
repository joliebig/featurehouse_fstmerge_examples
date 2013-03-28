

package net.sf.freecol.client.gui.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JToolTip;

import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.common.model.Building;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.resources.ResourceManager;

import net.miginfocom.swing.MigLayout;


public class BuildingToolTip extends JToolTip {

    private static final Font arrowFont = new Font("Dialog", Font.BOLD, 24);

    
    public BuildingToolTip(Building building, Canvas parent) {

        setLayout(new MigLayout("fill", "", ""));

        String buildingName = building.getName();
        if (building.getMaxUnits() == 0) {
            buildingName = "(" + building.getName() + ")";
        }

        boolean canTeach = building.getType().hasAbility("model.ability.teach");

        add(new JLabel(buildingName), "span, align center");

        if (building.getProductionNextTurn() == 0) {
            add(new JLabel(), "span");
        } else {
            ProductionLabel productionOutput = new ProductionLabel(building.getGoodsOutputType(),
                                                                   building.getProductionNextTurn(),
                                                                   building.getMaximumProduction(), parent);
            if (building.getGoodsInputNextTurn() == 0) {
                add(productionOutput, "span, align center");
            } else {
                ProductionLabel productionInput = new ProductionLabel(building.getGoodsInputType(),
                                                                      building.getGoodsInputNextTurn(),
                                                                      building.getMaximumGoodsInput(), parent);
                JLabel arrow = new JLabel("\u");
                arrow.setFont(arrowFont);
                add(productionInput, "span, split 3, align center");
                add(arrow);
                add(productionOutput);
            }
        }

        add(new JLabel(new ImageIcon(ResourceManager.getImage(building.getType().getId() + ".image"))));

        for (Unit unit : building.getUnitList()) {
            UnitLabel unitLabel = new UnitLabel(unit, parent, false);
            if (canTeach && unit.getStudent() != null) {
                JLabel progress = new JLabel(unit.getTurnsOfTraining() + "/" +
                                             unit.getNeededTurnsOfTraining());
                progress.setBackground(Color.WHITE);
                progress.setOpaque(true);
                UnitLabel studentLabel = new UnitLabel(unit.getStudent(), parent, true);
                studentLabel.setIgnoreLocation(true);
                add(unitLabel);
                add(progress, "split 2, flowy");
                add(studentLabel);
            } else  {
                add(unitLabel, "span 2");
            }
        }

    }

    public Dimension getPreferredSize() {
        return new Dimension(400, 200);
    }
}


