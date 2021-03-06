

package net.sf.freecol.client.gui.panel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import net.sf.freecol.FreeCol;
import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.model.Building;
import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.ColonyTile;
import net.sf.freecol.common.model.GoodsType;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.UnitType;

import net.miginfocom.swing.MigLayout;


public final class ReportRequirementsPanel extends ReportPanel {

    
    private List<Colony> colonies;

    
    private int[][] unitCount;

    
    private boolean[][] canTrain; 

    
    private int[][] surplus;


    
    public ReportRequirementsPanel(Canvas parent) {
        super(parent, Messages.message("menuBar.report.requirements"));
        Player player = getMyPlayer();
        colonies = player.getColonies();
        Collections.sort(colonies, getClient().getClientOptions().getColonyComparator());

        

        
        JTextPane textPane = getDefaultTextPane();
        StyledDocument doc = textPane.getStyledDocument();

        int numberUnitTypes = FreeCol.getSpecification().numberOfUnitTypes();
        int numberGoodsTypes = FreeCol.getSpecification().numberOfGoodsTypes();
        unitCount = new int[colonies.size()][numberUnitTypes];
        canTrain = new boolean[colonies.size()][numberUnitTypes];
        surplus = new int[colonies.size()][numberGoodsTypes];

        List<GoodsType> goodsTypes = FreeCol.getSpecification().getGoodsTypeList();
        
        for (int index = 0; index < colonies.size(); index++) {
            Colony colony = colonies.get(index);
            for (Unit unit : colony.getUnitList()) {
                unitCount[index][unit.getType().getIndex()]++;
                canTrain[index][unit.getType().getIndex()] = colony.canTrain(unit.getType());
            }
            for (GoodsType goodsType : goodsTypes) {
                surplus[index][goodsType.getIndex()] = colony.getProductionNetOf(goodsType);
            }
        }

        for (int index = 0; index < colonies.size(); index++) {

            Colony colony = colonies.get(index);

            
            try {
                StyleConstants.setComponent(doc.getStyle("button"), createColonyButton(colony, true));
                doc.insertString(doc.getLength(), " ", doc.getStyle("button"));
                doc.insertString(doc.getLength(), "\n\n", doc.getStyle("regular"));
            } catch(Exception e) {
                logger.warning(e.toString());
            }

            boolean[] expertWarning = new boolean[numberUnitTypes];
            boolean[] productionWarning = new boolean[numberGoodsTypes];
            boolean hasWarning = false;

            
            for (ColonyTile colonyTile : colony.getColonyTiles()) {
                Unit unit = colonyTile.getUnit();
                if (unit != null) {
                    GoodsType workType = unit.getWorkType();
                    UnitType expert = FreeCol.getSpecification().getExpertForProducing(workType);
                    int expertIndex = expert.getIndex();
                    if (unitCount[index][expertIndex] == 0 && !expertWarning[expertIndex]) {
                        addExpertWarning(doc, index, Messages.getName(workType), expert);
                        expertWarning[expertIndex] = true;
                        hasWarning = true;
                    }
                }
            } 
            for (Building building : colony.getBuildings()) {
                GoodsType goodsType = building.getGoodsOutputType();
                UnitType expert = building.getExpertUnitType();
                    
                if (goodsType != null && expert != null) {
                    
                    int expertIndex = expert.getIndex();
                    if (building.getFirstUnit() != null &&
                        !expertWarning[expertIndex] &&
                        unitCount[index][expertIndex] == 0) {
                        addExpertWarning(doc, index, Messages.getName(goodsType), expert);
                        expertWarning[expertIndex] = true;
                        hasWarning = true;
                    }
                }
                if (goodsType != null) {
                    
                    int goodsIndex = goodsType.getIndex();
                    if (building.getProductionNextTurn() < building.getMaximumProduction() &&
                        !productionWarning[goodsIndex]) {
                        addProductionWarning(doc, index, goodsType, building.getGoodsInputType());
                        productionWarning[goodsIndex] = true;
                        hasWarning = true;
                    }
                }
            }

            if (!hasWarning) {
                try {
                    doc.insertString(doc.getLength(), Messages.message("report.requirements.met") + "\n\n",
                                     doc.getStyle("regular"));
                } catch(Exception e) {
                    logger.warning(e.toString());
                }
            }

            
            int width = ((JViewport) reportPanel.getParent()).getWidth();
            reportPanel.setLayout(new MigLayout("width " + width + "!"));
            reportPanel.add(textPane);

        }
        textPane.setCaretPosition(0);

    }

    private void addExpertWarning(StyledDocument doc, int colonyIndex, String goods, UnitType workType) {
        String expertName = Messages.getName(workType);
        String colonyName = colonies.get(colonyIndex).getName();
        String newMessage = Messages.message("report.requirements.noExpert", "%colony%", colonyName, "%goods%", goods,
                "%unit%", expertName);

        try {
            doc.insertString(doc.getLength(), newMessage + "\n\n", doc.getStyle("regular"));

            ArrayList<Colony> severalExperts = new ArrayList<Colony>();
            ArrayList<Colony> canTrainExperts = new ArrayList<Colony>();
            for (int index = 0; index < colonies.size(); index++) {
                if (unitCount[index][workType.getIndex()] > 1) {
                    severalExperts.add(colonies.get(index));
                }
                if (canTrain[index][workType.getIndex()]) {
                    canTrainExperts.add(colonies.get(index));
                }
            }

            if (!severalExperts.isEmpty()) {
                doc.insertString(doc.getLength(), 
                        Messages.message("report.requirements.severalExperts", "%unit%", expertName), 
                        doc.getStyle("regular"));
                for (int index = 0; index < severalExperts.size() - 1; index++) {
                    Colony colony = severalExperts.get(index);
                    StyleConstants.setComponent(doc.getStyle("button"), createColonyButton(colony, false));
                    doc.insertString(doc.getLength(), " ", doc.getStyle("button"));
                    doc.insertString(doc.getLength(), ", ", doc.getStyle("regular"));
                }
                Colony colony = severalExperts.get(severalExperts.size() - 1);
                StyleConstants.setComponent(doc.getStyle("button"), createColonyButton(colony, false));
                doc.insertString(doc.getLength(), " ", doc.getStyle("button"));
                doc.insertString(doc.getLength(), "\n\n", doc.getStyle("regular"));
            }

            if (!canTrainExperts.isEmpty()) {
                doc.insertString(doc.getLength(), 
                        Messages.message("report.requirements.canTrainExperts", "%unit%", expertName), 
                        doc.getStyle("regular"));
                for (int index = 0; index < canTrainExperts.size() - 1; index++) {
                    Colony colony = canTrainExperts.get(index);
                    StyleConstants.setComponent(doc.getStyle("button"), createColonyButton(colony, false));
                    doc.insertString(doc.getLength(), " ", doc.getStyle("button"));
                    doc.insertString(doc.getLength(), ", ", doc.getStyle("regular"));
                }
                Colony colony = canTrainExperts.get(canTrainExperts.size() - 1);
                StyleConstants.setComponent(doc.getStyle("button"), createColonyButton(colony, false));
                doc.insertString(doc.getLength(), " ", doc.getStyle("button"));
                doc.insertString(doc.getLength(), "\n\n", doc.getStyle("regular"));
            }
  
        } catch(Exception e) {
            logger.warning(e.toString());
        }
        
    }

    private void addProductionWarning(StyledDocument doc, int colonyIndex, GoodsType output, GoodsType input) {
        String colonyName = colonies.get(colonyIndex).getName();
        String newMessage = Messages.message("report.requirements.missingGoods",
                "%colony%", colonyName,
                "%goods%", Messages.getName(output),
                "%input%", Messages.getName(input));

        try {
            doc.insertString(doc.getLength(), newMessage + "\n\n", doc.getStyle("regular"));

            ArrayList<Colony> withSurplus = new ArrayList<Colony>();
            ArrayList<Integer> theSurplus = new ArrayList<Integer>();
            for (int index = 0; index < colonies.size(); index++) {
                if (surplus[index][input.getIndex()] > 0) {
                    withSurplus.add(colonies.get(index));
                    theSurplus.add(new Integer(surplus[index][input.getIndex()]));
                }
            }

            if (!withSurplus.isEmpty()) {
                doc.insertString(doc.getLength(),
                        Messages.message("report.requirements.surplus", "%goods%", Messages.getName(input)) + " ",
                        doc.getStyle("regular"));
                for (int index = 0; index < withSurplus.size() - 1; index++) {
                    Colony colony = withSurplus.get(index);
                    String amount = " (" + theSurplus.get(index) + ")";
                    StyleConstants.setComponent(doc.getStyle("button"), createColonyButton(colony, amount, false));
                    doc.insertString(doc.getLength(), " ", doc.getStyle("button"));
                    doc.insertString(doc.getLength(), ", ", doc.getStyle("regular"));
                }
                Colony colony = withSurplus.get(withSurplus.size() - 1);
                String amount = " (" + theSurplus.get(theSurplus.size() - 1) + ")";
                StyleConstants.setComponent(doc.getStyle("button"), createColonyButton(colony, amount, false));
                doc.insertString(doc.getLength(), " ", doc.getStyle("button"));
                doc.insertString(doc.getLength(), "\n\n", doc.getStyle("regular"));
            }


        } catch(Exception e) {
            logger.warning(e.toString());
        }
        
    }

    private JButton createColonyButton(Colony colony, boolean headline) {
        return createColonyButton(colony, "", headline);
    }

    private JButton createColonyButton(Colony colony, String info, boolean headline) {
        JButton button = getLinkButton(colony.getName() + info, null, colony.getId());
        if (headline) {
            button.setFont(smallHeaderFont);
        }
        button.addActionListener(this);
        return button;
    }


}
