

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
import javax.swing.JTextArea;
import javax.swing.JToolTip;

import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.model.AbstractGoods;
import net.sf.freecol.common.model.BuildableType;
import net.sf.freecol.common.model.Building;
import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.resources.ResourceManager;

import net.miginfocom.swing.MigLayout;


public class BuildingSiteToolTip extends JToolTip {

    private static final Font arrowFont = new Font("Dialog", Font.BOLD, 24);

    
    public BuildingSiteToolTip(Colony colony, Canvas parent) {

        setLayout(new MigLayout("fill", "", ""));

        BuildableType buildable = colony.getCurrentlyBuilding();
        if (buildable == null) {
            add(FreeColPanel.getDefaultTextArea(Messages.message("colonyPanel.clickToBuild")),
                "span, align center");
        } else {
            int turnsToComplete = colony.getTurnsToComplete(buildable);
            String turns = Messages.message("notApplicable.short");
            if (turnsToComplete >= 0) {
                turns = Integer.toString(turnsToComplete);
            }
            else if(turnsToComplete != Integer.MIN_VALUE){
                turns = ">" + Integer.toString(turnsToComplete*-1);
            }
            add(new JLabel(Messages.message("colonyPanel.currentlyBuilding",
                                            "%buildable%", buildable.getName())),
                "span, align center");

            add(new JLabel(Messages.message("turnsToComplete.long",
                                            "%number%", turns)),
                "span, align center");
            add(new JLabel(new ImageIcon(ResourceManager.getImage(buildable.getId() + ".image"))));

            List<FreeColProgressBar> progressBars = new ArrayList<FreeColProgressBar>();
            for (AbstractGoods requiredGoods : buildable.getGoodsRequired()) {
                int amountNeeded = requiredGoods.getAmount();
                int amountAvailable = colony.getGoodsCount(requiredGoods.getType());
                int amountProduced = colony.getProductionNetOf(requiredGoods.getType());
                progressBars.add(new FreeColProgressBar(parent, requiredGoods.getType(), 0,
                                                        amountNeeded, amountAvailable, amountProduced));
            }

            int size = progressBars.size();
            if (size == 1) {
                add(progressBars.get(0));
            } else if (size > 1) {
                add(progressBars.get(0), "flowy, split " + size);
                for (int index = 1; index < size; index++) {
                    add(progressBars.get(index));
                }
            }
        }

    }

    public Dimension getPreferredSize() {
        return new Dimension(350, 200);
    }
}


