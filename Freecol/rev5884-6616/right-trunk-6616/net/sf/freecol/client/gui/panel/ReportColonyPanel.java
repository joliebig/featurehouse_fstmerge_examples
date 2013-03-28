

package net.sf.freecol.client.gui.panel;

import java.util.Collections;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSeparator;

import net.sf.freecol.FreeCol;
import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.model.Specification;
import net.sf.freecol.common.model.BuildableType;
import net.sf.freecol.common.model.Building;
import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.GoodsType;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.StringTemplate;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.resources.ResourceManager;

import net.miginfocom.swing.MigLayout;


public final class ReportColonyPanel extends ReportPanel {

    private List<Colony> colonies;

    private final int ROWS_PER_COLONY = 4;

    
    public ReportColonyPanel(Canvas parent) {

        super(parent, Messages.message("menuBar.report.colony"));
        Player player = getMyPlayer();
        colonies = player.getColonies();

        
        Collections.sort(colonies, getClient().getClientOptions().getColonyComparator());

        reportPanel.setLayout(new MigLayout("fill, wrap 16"));

        for (Colony colony : colonies) {

            
            JButton button = getLinkButton(colony.getName(), null, colony.getId());
            button.addActionListener(this);
            reportPanel.add(button, "newline 20, span, split 2");
            reportPanel.add(new JSeparator(JSeparator.HORIZONTAL), "growx");

            
            List<Unit> unitList = colony.getUnitList();
            Collections.sort(unitList, getUnitTypeComparator());
            for (Unit unit : unitList) {
                UnitLabel unitLabel = new UnitLabel(unit, getCanvas(), true, true);
                reportPanel.add(unitLabel, "sg units");
            }
            if (unitList.size() % 16 != 0) {
                reportPanel.add(new JLabel(), "wrap");
            }

            unitList = colony.getTile().getUnitList();
            Collections.sort(unitList, getUnitTypeComparator());
            for (Unit unit : unitList) {
                UnitLabel unitLabel = new UnitLabel(unit, getCanvas(), true, true);
                reportPanel.add(unitLabel, "span 2");
            }
            if (unitList.size() % 8 != 0) {
                reportPanel.add(new JLabel(), "wrap");
            }

            
            GoodsType food = Specification.getSpecification().getGoodsType("model.goods.food");
            GoodsType horses = Specification.getSpecification().getGoodsType("model.goods.horses");
            int count = 0;
            int netFood = colony.getFoodProduction() - colony.getFoodConsumption();
            if (netFood != 0) {
                ProductionLabel productionLabel = new ProductionLabel(food, netFood, getCanvas());
                productionLabel.setStockNumber(colony.getFoodCount());
                reportPanel.add(productionLabel, "span 2, top");
                count++;
            }
            for (GoodsType goodsType : FreeCol.getSpecification().getGoodsTypeList()) {
                if (goodsType.isFoodType()) {
                    continue;
                }
                int newValue = colony.getProductionNetOf(goodsType);
                int stockValue = colony.getGoodsCount(goodsType);
                if (newValue != 0 || stockValue > 0) {
                    Building building = colony.getBuildingForProducing(goodsType);
                    ProductionLabel productionLabel = new ProductionLabel(goodsType, newValue, getCanvas());
                    if (building != null) {
                        productionLabel.setMaximumProduction(building.getMaximumProduction());
                    }
                    if (goodsType == horses) {
                        productionLabel.setMaxGoodsIcons(1);
                    }
                    productionLabel.setStockNumber(stockValue);   
                    reportPanel.add(productionLabel, "span 2, top");
                    count++;
                }
            }
            if (count % 8 != 0) {
                reportPanel.add(new JLabel(), "wrap");
            }

            List<Building> buildingList = colony.getBuildings();
            Collections.sort(buildingList);
            for (Building building : buildingList) {
                JLabel buildingLabel =
                    new JLabel(new ImageIcon(ResourceManager.getImage(building.getType().getId()
                                                                      + ".image", 0.66)));
                buildingLabel.setToolTipText(Messages.message(building.getNameKey()));
                reportPanel.add(buildingLabel, "span 2");
            }

            
            BuildableType currentType = colony.getCurrentlyBuilding();
            if (currentType != null) {
                JLabel buildableLabel =
                    new JLabel(new ImageIcon(ResourceManager.getImage(currentType.getId()
                                                                      + ".image", 0.66)));
                buildableLabel.setToolTipText(Messages.message(StringTemplate.template("colonyPanel.currentlyBuilding")
                                                               .add("%buildable%", currentType.getNameKey())));
                buildableLabel.setIcon(buildableLabel.getDisabledIcon());
                reportPanel.add(buildableLabel, "span 2");
            }
        }

    }
}
