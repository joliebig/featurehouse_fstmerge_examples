

package net.sf.freecol.client.gui.panel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.dnd.Autoscroll;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ComponentInputMap;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;

import net.miginfocom.swing.MigLayout;
import net.sf.freecol.FreeCol;
import net.sf.freecol.client.ClientOptions;
import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.GUI;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.model.BuildableType;
import net.sf.freecol.common.model.Building;
import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.ColonyTile;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.Goods;
import net.sf.freecol.common.model.GoodsType;
import net.sf.freecol.common.model.IndianSettlement;
import net.sf.freecol.common.model.ModelMessage;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Settlement;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.TileType;
import net.sf.freecol.common.model.TradeRoute;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.Colony.ColonyChangeEvent;
import net.sf.freecol.common.resources.ResourceManager;


public final class ColonyPanel extends FreeColPanel implements ActionListener,PropertyChangeListener {


    private static Logger logger = Logger.getLogger(ColonyPanel.class.getName());

    
    public static final int SCROLL_AREA_HEIGHT = 40;

    
    public static final int SCROLL_SPEED = 40;

    private static final int EXIT = 0, UNLOAD = 2, WAREHOUSE = 4, FILL = 5;

    private final JLabel rebelShield = new JLabel();
    private final JLabel rebelLabel = new JLabel();
    private final JLabel bonusLabel = new JLabel();
    private final JLabel royalistLabel = new JLabel();
    private final JLabel royalistShield = new JLabel();
    private final JLabel rebelMemberLabel = new JLabel();
    private final JLabel popLabel = new JLabel();
    private final JLabel royalistMemberLabel = new JLabel();

    private final JPanel productionPanel = new JPanel();
    private final JPanel populationPanel = new JPanel();

    private final JComboBox nameBox;

    private final OutsideColonyPanel outsideColonyPanel;

    private final InPortPanel inPortPanel;

    private final ColonyCargoPanel cargoPanel;

    private final WarehousePanel warehousePanel;

    private final TilePanel tilePanel;

    private final BuildingsPanel buildingsPanel;

    private final DefaultTransferHandler defaultTransferHandler;

    private final MouseListener pressListener;

    private final MouseListener releaseListener;

    private Colony colony;

    private UnitLabel selectedUnit;

    private JButton exitButton = new JButton(Messages.message("close"));

    private JButton unloadButton = new JButton(Messages.message("unload"));

    private JButton fillButton = new JButton(Messages.message("fill"));

    private JButton warehouseButton = new JButton(Messages.message("warehouseDialog.name"));

    private static final Font hugeFont = new Font("Dialog", Font.BOLD, 24);


    
    public ColonyPanel(Canvas parent) {
        super(parent);

        setFocusCycleRoot(true);

        
        InputMap closeInputMap = new ComponentInputMap(exitButton);
        closeInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false), "pressed");
        closeInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, true), "released");
        SwingUtilities.replaceUIInputMap(exitButton, JComponent.WHEN_IN_FOCUSED_WINDOW, closeInputMap);

        InputMap unloadInputMap = new ComponentInputMap(unloadButton);
        unloadInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_U, 0, false), "pressed");
        unloadInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_U, 0, true), "released");
        SwingUtilities.replaceUIInputMap(unloadButton, JComponent.WHEN_IN_FOCUSED_WINDOW, unloadInputMap);

        InputMap fillInputMap = new ComponentInputMap(fillButton);
        fillInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_L, 0, false), "pressed");
        fillInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_L, 0, true), "released");
        SwingUtilities.replaceUIInputMap(fillButton, JComponent.WHEN_IN_FOCUSED_WINDOW, fillInputMap);

        productionPanel.setOpaque(false);

        populationPanel.setOpaque(false);
        populationPanel.setLayout(new MigLayout("wrap 5, fill, insets 0",
                                                "[][]:push[center]:push[right][]", ""));
        populationPanel.add(rebelShield, "bottom");
        populationPanel.add(rebelLabel, "split 2, flowy");
        populationPanel.add(rebelMemberLabel);
        populationPanel.add(popLabel, "split 2, flowy");
        populationPanel.add(bonusLabel);
        populationPanel.add(royalistLabel, "split 2, flowy");
        populationPanel.add(royalistMemberLabel);
        populationPanel.add(royalistShield, "bottom");

        outsideColonyPanel = new OutsideColonyPanel();
        outsideColonyPanel.setToolTipText(Messages.message("outsideColony"));
        outsideColonyPanel.setLayout(new GridLayout(0, 8));

        inPortPanel = new InPortPanel();
        inPortPanel.setToolTipText(Messages.message("inPort"));
        inPortPanel.setLayout(new GridLayout(0, 2));

        warehousePanel = new WarehousePanel(this);
        warehousePanel.setToolTipText(Messages.message("goods"));
        warehousePanel.setLayout(new GridLayout(1, 0));

        tilePanel = new TilePanel(this);
        tilePanel.setToolTipText(Messages.message("surroundingArea"));

        buildingsPanel = new BuildingsPanel(this);
        buildingsPanel.setToolTipText(Messages.message("buildings"));

        cargoPanel = new ColonyCargoPanel(parent);
        cargoPanel.setParentPanel(this);
        cargoPanel.setToolTipText(Messages.message("cargoOnCarrier"));
        cargoPanel.setLayout(new GridLayout(1, 0));

        defaultTransferHandler = new DefaultTransferHandler(parent, this);
        pressListener = new DragListener(this);
        releaseListener = new DropListener();

        JScrollPane outsideColonyScroll = new JScrollPane(outsideColonyPanel,
                                                          ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                                                          ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        outsideColonyScroll.getVerticalScrollBar().setUnitIncrement( 16 );
        JScrollPane inPortScroll = new JScrollPane(inPortPanel,
                                                   ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                                                   ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        inPortScroll.getVerticalScrollBar().setUnitIncrement( 16 );
        JScrollPane cargoScroll = new JScrollPane(cargoPanel,
                                                  ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
                                                  ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        JScrollPane warehouseScroll = new JScrollPane(warehousePanel,
                                                      ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
                                                      ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        JScrollPane tilesScroll = new JScrollPane(tilePanel,
                                                  ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                                                  ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        JScrollPane buildingsScroll = new JScrollPane(buildingsPanel,
                                                      ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                                                      ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        buildingsScroll.getVerticalScrollBar().setUnitIncrement( 16 );

        
        nameBox = new JComboBox();
        nameBox.setFont(smallHeaderFont);

        buildingsScroll.setAutoscrolls(true);

        
        tilesScroll.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        buildingsScroll.setBorder(BorderFactory.createEtchedBorder());
        warehouseScroll.setBorder(BorderFactory.createEtchedBorder());
        cargoScroll.setBorder(BorderFactory.createEtchedBorder());
        inPortScroll.setBorder(BorderFactory.createEtchedBorder());
        outsideColonyScroll.setBorder(BorderFactory.createEtchedBorder());

        exitButton.setActionCommand(String.valueOf(EXIT));
        enterPressesWhenFocused(exitButton);
        exitButton.addActionListener(this);

        unloadButton.setActionCommand(String.valueOf(UNLOAD));
        enterPressesWhenFocused(unloadButton);
        unloadButton.addActionListener(this);

        fillButton.setActionCommand(String.valueOf(FILL));
        enterPressesWhenFocused(fillButton);
        fillButton.addActionListener(this);
        
        warehouseButton.setActionCommand(String.valueOf(WAREHOUSE));
        enterPressesWhenFocused(warehouseButton);
        warehouseButton.addActionListener(this);

        selectedUnit = null;

        
        
        addMouseListener(new MouseAdapter() {
            });

        setLayout(new MigLayout("fill, wrap 2", "[390!][fill]", ""));

        add(nameBox, "growx, height 48:");
        add(productionPanel, "align center");
        add(tilesScroll, "width 390!, height 200!, top");
        add(buildingsScroll, "span 1 3, grow 200");
        add(populationPanel, "grow");
        add(inPortScroll, "grow, height 60:100");
        add(cargoScroll, "grow, height 60:100");
        add(outsideColonyScroll, "grow, height 60:100");
        add(warehouseScroll, "span, height 40:60:80, growx");
        add(unloadButton, "span, split 5, align center");
        add(fillButton);
        add(warehouseButton);
        add(exitButton);

        setSize(parent.getWidth(), parent.getHeight());

    }

    @Override
    public void requestFocus() {
        exitButton.requestFocus();
    }

    
    public void initialize(Colony colony) {
        initialize(colony, null);
    }

    
    public void initialize(final Colony colony, Unit preSelectedUnit) {
        setColony(colony);

        rebelShield.setIcon(new ImageIcon(ResourceManager.getImage(colony.getOwner().getNation().getId()
                                                                   + ".coat-of-arms.image", 0.5)));
        royalistShield.setIcon(new ImageIcon(ResourceManager.getImage(colony.getOwner().getNation().getRefNation().getId()
                                                                      + ".coat-of-arms.image", 0.5)));
        popLabel.setText(Messages.message("colonyPanel.populationLabel", "%number%",
                                          Integer.toString(colony.getUnitCount())));

        
        outsideColonyPanel.removeMouseListener(releaseListener);
        inPortPanel.removeMouseListener(releaseListener);
        cargoPanel.removeMouseListener(releaseListener);
        warehousePanel.removeMouseListener(releaseListener);
        if (isEditable()) {
            outsideColonyPanel.setTransferHandler(defaultTransferHandler);
            inPortPanel.setTransferHandler(defaultTransferHandler);
            cargoPanel.setTransferHandler(defaultTransferHandler);
            warehousePanel.setTransferHandler(defaultTransferHandler);

            outsideColonyPanel.addMouseListener(releaseListener);
            inPortPanel.addMouseListener(releaseListener);
            cargoPanel.addMouseListener(releaseListener);
            warehousePanel.addMouseListener(releaseListener);
        } else {
            outsideColonyPanel.setTransferHandler(null);
            inPortPanel.setTransferHandler(null);
            cargoPanel.setTransferHandler(null);
            warehousePanel.setTransferHandler(null);
        }

        
        unloadButton.setEnabled(isEditable());
        fillButton.setEnabled(isEditable());
        warehouseButton.setEnabled(isEditable());
        nameBox.setEnabled(isEditable());

        
        
        

        cargoPanel.removeAll();
        warehousePanel.removeAll();
        tilePanel.removeAll();

        
        
        
        
        inPortPanel.initialize(preSelectedUnit);
        
        
        
        

        warehousePanel.initialize();

        
        
        

        buildingsPanel.initialize();

        
        
        

        tilePanel.initialize();

        updateNameBox();
        updateProductionPanel();
        updateSoLLabel();


        outsideColonyPanel.setColony(colony);

    }

    
    private void updateCarrierButtons() {
        unloadButton.setEnabled(false);
        fillButton.setEnabled(false);
        if (isEditable() && selectedUnit != null) {
            Unit unit = selectedUnit.getUnit();
            if (unit != null && unit.isCarrier() && unit.getSpaceLeft() < unit.getType().getSpace()) {
                unloadButton.setEnabled(true);
                for (Goods goods : unit.getGoodsList()) {
                    if (getColony().getGoodsCount(goods.getType()) > 0) {
                        fillButton.setEnabled(true);
                        return;
                    }
                }
            }
        }
    }

    
    private void updateSoLLabel() {
        if (getColony() == null) {
            
            logger.warning("Colony panel has 'null' colony.");
            return;
        }
        int members = getColony().getMembers();
        int rebels = getColony().getSoL();
        rebelLabel.setText(Messages.message("colonyPanel.rebelLabel", "%number%",
                                            Integer.toString(members)));
        rebelMemberLabel.setText(Integer.toString(rebels) + "%");
        bonusLabel.setText(Messages.message("colonyPanel.bonusLabel", "%number%",
                                            Integer.toString(getColony().getProductionBonus())));
        royalistLabel.setText(Messages.message("colonyPanel.royalistLabel", "%number%",
                                               Integer.toString(getColony().getUnitCount() - members)));
        royalistMemberLabel.setText(Integer.toString(getColony().getTory()) + "%");
    }
    
    public void updateInPortPanel() {
        inPortPanel.initialize(null);
    }

    public void updateWarehousePanel() {
        warehousePanel.update();
    }

    public void updateTilePanel() {
        tilePanel.initialize();
    }

    public void updateNameBox() {
        if (getColony() == null) {
            
            return;
        } else if (((DefaultComboBoxModel) nameBox.getModel()).getSize() == 0) {
            List<Colony> settlements = getColony().getOwner().getColonies();
            sortColonies(settlements);
            for (Colony colony : settlements) {
                nameBox.addItem(colony);
            }
            nameBox.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        initialize((Colony) nameBox.getSelectedItem());
                    }
                });
        }
    }

    private void sortBuildings(List<Building> buildings) {
        Collections.sort(buildings, Building.getBuildingComparator());
    }
    
    private void sortColonies(List<Colony> colonies) {
        Collections.sort(colonies, getClient().getClientOptions().getColonyComparator());
    }

    public void updateProductionPanel() {
        productionPanel.removeAll();

        final int foodFarmsProduction = colony.getProductionOf(Goods.FOOD);
        final int foodFishProduction = colony.getProductionOf(Goods.FISH);
        final int humanFoodConsumption = colony.getFoodConsumption();
        final int horsesProduced = colony.getGoodsCount(Goods.HORSES) <= 0 ?
            0 : colony.getProductionOf(Goods.HORSES);
        final int bells = colony.getProductionOf(Goods.BELLS);
        final int crosses = colony.getProductionOf(Goods.CROSSES);
        
        int foodProduction = foodFarmsProduction + foodFishProduction;
        
        
        
        
        
        
        final int usedFood = Math.min(humanFoodConsumption, foodProduction) + horsesProduced;
        final int usedFish = colony.getFoodConsumptionByType(Goods.FISH);
        final int usedCorn = Math.max(usedFood - usedFish, 0);
        
        if (usedFish == 0) {
            ProductionLabel label = new ProductionLabel(Goods.FOOD, usedFood, getCanvas());
            label.setToolTipPrefix(Messages.message("totalProduction"));
            productionPanel.add(label);
        } else {
            ProductionMultiplesLabel label = new ProductionMultiplesLabel(Goods.FOOD, usedCorn,
                                                                          Goods.FISH, usedFish, getCanvas());
            label.setToolTipPrefix(Messages.message("totalProduction"));
            productionPanel.add(label);
        }
        
        int remainingCorn = foodFarmsProduction - usedCorn;
        final int remainingFish = foodFishProduction  - usedFish;

        int surplusFood = foodProduction - humanFoodConsumption - horsesProduced;
        remainingCorn = Math.min(surplusFood, remainingCorn);
        ProductionMultiplesLabel surplusLabel =
            new ProductionMultiplesLabel(Goods.FOOD, remainingCorn, Goods.FISH, remainingFish, getCanvas());
        surplusLabel.setDrawPlus(true);
        surplusLabel.setToolTipPrefix(Messages.message("surplusProduction"));
        productionPanel.add(surplusLabel);

        if (horsesProduced != 0) {
            
            ProductionLabel horseLabel = new ProductionLabel(Goods.HORSES, horsesProduced, getCanvas());
            horseLabel.setMaxGoodsIcons(1);
            productionPanel.add(horseLabel);
        }

        ProductionLabel bellsLabel = new ProductionLabel(Goods.BELLS, bells, getCanvas());
        bellsLabel.setToolTipPrefix(Messages.message("totalProduction"));
        productionPanel.add(bellsLabel);
        int surplusBells = bells - colony.getConsumption(Goods.BELLS);
        ProductionLabel bellsSurplusLabel = new ProductionLabel(Goods.BELLS, surplusBells, getCanvas());
        bellsSurplusLabel.setToolTipPrefix(Messages.message("surplusProduction"));
        productionPanel.add(bellsSurplusLabel);
        
        productionPanel.add(new ProductionLabel(Goods.CROSSES, crosses, getCanvas()));
        productionPanel.revalidate();
    }
    
    
    public Unit getSelectedUnit() {
        if (selectedUnit == null)
            return null;
        else
            return selectedUnit.getUnit();
    }

    
    public UnitLabel getSelectedUnitLabel() {
        return selectedUnit;
    }

    
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        try {
            switch (Integer.valueOf(command).intValue()) {
            case EXIT:
                closeColonyPanel();
                break;
            case UNLOAD:
                unload();
                break;
            case WAREHOUSE:
                if (getCanvas().showFreeColDialog(new WarehouseDialog(getCanvas(), colony))) {
                    warehousePanel.update();
                }
                break;
            case FILL:
                fill();
                break;
            default:
                logger.warning("Invalid action");
            }
        } catch (NumberFormatException e) {
            logger.warning("Invalid action number: " + command);
        }
    }

    
    private void unload() {
        Unit unit = getSelectedUnit();
        if (unit != null && unit.isCarrier()) {
            Iterator<Goods> goodsIterator = unit.getGoodsIterator();
            while (goodsIterator.hasNext()) {
                Goods goods = goodsIterator.next();
                getController().unloadCargo(goods, false);
            }
            Iterator<Unit> unitIterator = unit.getUnitIterator();
            while (unitIterator.hasNext()) {
                Unit newUnit = unitIterator.next();
                getController().leaveShip(newUnit);
            }
            cargoPanel.initialize();
            outsideColonyPanel.initialize();
        }
        unloadButton.setEnabled(false);
        fillButton.setEnabled(false);
    }

    
    private void fill() {
        Unit unit = getSelectedUnit();
        if (unit != null && unit.isCarrier()) {
            Iterator<Goods> goodsIterator = unit.getGoodsIterator();
            while (goodsIterator.hasNext()) {
                Goods goods = goodsIterator.next();
                if (goods.getAmount() < 100 && colony.getGoodsCount(goods.getType()) > 0) {
                    int amount = Math.min(100 - goods.getAmount(), colony.getGoodsCount(goods.getType()));
                    getController().loadCargo(new Goods(goods.getGame(), colony, goods.getType(), amount), unit);
                }
            }
        }
    }

    
    public void closeColonyPanel() {
        if (getColony().getUnitCount() == 0) {
            if (getCanvas().showConfirmDialog("abandonColony.text",
                                              "abandonColony.yes",
                                              "abandonColony.no")) {
                getController().abandonColony(getColony());
                getCanvas().remove(this);
            }
        } else {
            BuildableType buildable = colony.getCurrentlyBuilding();
            if (buildable != null
                && buildable.getPopulationRequired() > colony.getUnitCount()
                && !getCanvas().showConfirmDialog("colonyPanel.reducePopulation",
                                                  "ok", "cancel",
                                                  "%colony%", colony.getName(),
                                                  "%number%", String.valueOf(buildable.getPopulationRequired()),
                                                  "%buildable%", buildable.getName())) {
                return;
            }
            getCanvas().remove(this);
            
            if (colony != null) {
                colony.removePropertyChangeListener(this);
                colony.getTile().removePropertyChangeListener(Tile.UNIT_CHANGE, outsideColonyPanel);
                colony.getGoodsContainer().removePropertyChangeListener(warehousePanel);
            }
            if (getSelectedUnit() != null) {
                getSelectedUnit().removePropertyChangeListener(this);
            }
            buildingsPanel.removePropertyChangeListeners();
            tilePanel.removePropertyChangeListeners();

            if (getGame().getCurrentPlayer() == getMyPlayer()) {
                getController().nextModelMessage();
                Unit activeUnit = getCanvas().getGUI().getActiveUnit();
                if (activeUnit == null || activeUnit.getTile() == null || activeUnit.getMovesLeft() <= 0
                    || (!(activeUnit.getLocation() instanceof Tile) && !(activeUnit.isOnCarrier()))) {
                    getCanvas().getGUI().setActiveUnit(null);
                    getController().nextActiveUnit();
                }
            }
            getClient().getGUI().restartBlinking();
        }
    }

    
    public void setSelectedUnit(Unit unit) {
        Component[] components = inPortPanel.getComponents();
        for (int i = 0; i < components.length; i++) {
            if (components[i] instanceof UnitLabel && ((UnitLabel) components[i]).getUnit() == unit) {
                setSelectedUnitLabel((UnitLabel) components[i]);
                break;
            }
        }
        updateCarrierButtons();
    }

    
    public void setSelectedUnitLabel(UnitLabel unitLabel) {
        if (selectedUnit != unitLabel) {
            if (selectedUnit != null) {
                selectedUnit.setSelected(false);
                selectedUnit.getUnit().removePropertyChangeListener(this);
            }
            selectedUnit = unitLabel;
            if (unitLabel == null) {
                cargoPanel.setCarrier(null);
            } else {
                cargoPanel.setCarrier(unitLabel.getUnit());
                unitLabel.setSelected(true);
                unitLabel.getUnit().addPropertyChangeListener(this);
            }
        }
    }

    
    public final CargoPanel getCargoPanel() {
        return cargoPanel;
    }

    
    public final WarehousePanel getWarehousePanel() {
        return warehousePanel;
    }

    
    public final TilePanel getTilePanel() {
        return tilePanel;
    }

    
    public synchronized final Colony getColony() {
        return colony;
    }

    
    private synchronized void setColony(Colony colony) {
        if (this.colony != null){
            this.colony.removePropertyChangeListener(this);
        }
        this.colony = colony;
        if (this.colony != null){
            this.colony.addPropertyChangeListener(this);
        }
        editable = (colony.getOwner() == getMyPlayer());
    }

    
    public final class ColonyCargoPanel extends CargoPanel {

        public ColonyCargoPanel(Canvas canvas) {
            super(canvas, true);
        }

        @Override
        public String getUIClassID() {
            return "CargoPanelUI";
        }
    }

    
    public final class BuildingsPanel extends JPanel {

        private final ColonyPanel colonyPanel;


        
        public BuildingsPanel(ColonyPanel colonyPanel) {
            setLayout(new GridLayout(0, 4));
            this.colonyPanel = colonyPanel;
        }

        @Override
        public String getUIClassID() {
            return "BuildingsPanelUI";
        }

        
        public void initialize() {
            removeAll();

            MouseAdapter mouseAdapter = new MouseAdapter() {
                    public void mousePressed(MouseEvent e) {
                        getCanvas().showSubPanel(new BuildQueuePanel(colony, getCanvas()));
                    }
                };
            ASingleBuildingPanel aSingleBuildingPanel;

            List<Building> buildings = getColony().getBuildings();
            sortBuildings(buildings);
            for (Building building : buildings) {
                aSingleBuildingPanel = new ASingleBuildingPanel(building);
                if (colonyPanel.isEditable()) {
                    aSingleBuildingPanel.addMouseListener(releaseListener);
                    aSingleBuildingPanel.setTransferHandler(defaultTransferHandler);
                }
                aSingleBuildingPanel.setOpaque(false);
                aSingleBuildingPanel.addMouseListener(mouseAdapter);
                add(aSingleBuildingPanel);
            }
            add(new BuildingSitePanel(colony, getCanvas()));

        }

        public void removePropertyChangeListeners() {
            for (Component component : getComponents()) {
                if (component instanceof ASingleBuildingPanel) {
                    ((ASingleBuildingPanel) component).removePropertyChangeListeners();
                } else if (component instanceof BuildingSitePanel) {
                    ((BuildingSitePanel) component).removePropertyChangeListeners();
                }
            }
        }


        
        public final class ASingleBuildingPanel extends BuildingPanel implements Autoscroll {

            
            public ASingleBuildingPanel(Building building) {
                super(building, getCanvas());
            }

            public void initialize() {
                super.initialize();
                if (colonyPanel.isEditable()) {
                    for (UnitLabel unitLabel : getUnitLabels()) {
                        unitLabel.setTransferHandler(defaultTransferHandler);
                        unitLabel.addMouseListener(pressListener);
                    }
                }
            }

            public void autoscroll(Point p) {
                JViewport vp = (JViewport) colonyPanel.buildingsPanel.getParent();
                if (getLocation().y + p.y - vp.getViewPosition().y < SCROLL_AREA_HEIGHT) {
                    vp.setViewPosition(new Point(vp.getViewPosition().x,
                                                 Math.max(vp.getViewPosition().y - SCROLL_SPEED, 0)));
                } else if (getLocation().y + p.y - vp.getViewPosition().y >= vp.getHeight() - SCROLL_AREA_HEIGHT) {
                    vp.setViewPosition(new Point(vp.getViewPosition().x,
                                                 Math.min(vp.getViewPosition().y + SCROLL_SPEED,
                                                          colonyPanel.buildingsPanel.getHeight()
                                                          - vp.getHeight())));
                }
            }

            public Insets getAutoscrollInsets() {
                Rectangle r = getBounds();
                return new Insets(r.x, r.y, r.width, r.height);
            }


            
            public Component add(Component comp, boolean editState) {
                Component c;
                Container oldParent = comp.getParent();

                if (editState) {
                    if (comp instanceof UnitLabel) {
                        Unit unit = ((UnitLabel) comp).getUnit();

                        if (getBuilding().canAdd(unit)) {
                            oldParent.remove(comp);
                            getController().work(unit, getBuilding());
                        } else {
                            return null;
                        }
                    } else {
                        logger.warning("An invalid component got dropped on this BuildingsPanel.");
                        return null;
                    }
                }
                initialize();
                return null;
            }

        }
    }

    
    public final class OutsideColonyPanel extends JPanel implements PropertyChangeListener {

        private Colony colony;

        public OutsideColonyPanel() {
            super();
            setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(),
                                                       Messages.message("outsideColony")));
        }

        public void setColony(Colony newColony) {
            if (colony != null) {
                colony.getTile().removePropertyChangeListener(Tile.UNIT_CHANGE, this);
            }
            this.colony = newColony;
            if (colony != null) {
                colony.getTile().addPropertyChangeListener(Tile.UNIT_CHANGE, this);
            }
            initialize();
        }

        public void initialize() {
            
            removeAll();
            if (colony == null) {
                return;
            }

            Tile colonyTile = colony.getTile();
            for (Unit unit : colonyTile.getUnitList()) {

                UnitLabel unitLabel = new UnitLabel(unit, getCanvas());
                if (isEditable()) {
                    unitLabel.setTransferHandler(defaultTransferHandler);
                    unitLabel.addMouseListener(pressListener);
                }

                if (!unit.isCarrier()) {
                    add(unitLabel, false);
                }
            }
            revalidate();
            repaint();
        }

        public Colony getColony() {
            return colony;
        }

        @Override
        public String getUIClassID() {
            return "OutsideColonyPanelUI";
        }

        
        public Component add(Component comp, boolean editState) {
            Container oldParent = comp.getParent();
            if (editState) {
                if (comp instanceof UnitLabel) {
                    UnitLabel unitLabel = ((UnitLabel) comp);
                    Unit unit = unitLabel.getUnit();

                    if (!unit.isOnCarrier()) {
                        getController().putOutsideColony(unit);
                    }

                    if (unit.getColony() == null) {
                        closeColonyPanel();
                        return null;
                    } else if (!(unit.getLocation() instanceof Tile) && !unit.isOnCarrier()) {
                        return null;
                    }

                    oldParent.remove(comp);
                    initialize();
                    return comp;
                } else {
                    logger.warning("An invalid component got dropped on this ColonistsPanel.");
                    return null;
                }
            } else {
                ((UnitLabel) comp).setSmall(false);
                Component c = add(comp);
                return c;
            }
        }

        public void propertyChange(PropertyChangeEvent event) {
            initialize();
        }

    }

    
    public final class InPortPanel extends JPanel {

        public InPortPanel() {
            super();
            setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(),
                                                       Messages.message("inPort")));
        }

        @Override
        public String getUIClassID() {
            return "InPortPanelUI";
        }
        
        public void initialize(Unit selectedUnit) {
            
            UnitLabel oldSelectedUnitLabel = ColonyPanel.this.getSelectedUnitLabel();
            if(oldSelectedUnitLabel != null){
                if(selectedUnit == null){
                    selectedUnit = oldSelectedUnitLabel.getUnit();
                }
                ColonyPanel.this.setSelectedUnit(null);
            }
            
            removeAll();
            if (colony == null) {
                return;
            }

            Tile colonyTile = colony.getTile();
            Unit lastCarrier = null;
            for (Unit unit : colonyTile.getUnitList()) {
                if(!unit.isCarrier()){
                    continue;
                }
                
                lastCarrier = unit;
                UnitLabel unitLabel = new UnitLabel(unit, getCanvas());
                TradeRoute tradeRoute = unit.getTradeRoute();
                if (tradeRoute != null) {
                    unitLabel.setDescriptionLabel(unit.getName() + " ("
                                                  + tradeRoute.getName() + ")");
                }
                if (isEditable()) {
                    unitLabel.setTransferHandler(defaultTransferHandler);
                    unitLabel.addMouseListener(pressListener);
                }
                add(unitLabel, false);
            }
            revalidate();
            repaint();
            
            
            if(selectedUnit == null && lastCarrier != null){
                selectedUnit = lastCarrier;
            }
            
            if(selectedUnit != null){
                ColonyPanel.this.setSelectedUnit(selectedUnit);
            }
        }
    }

    
    public final class WarehousePanel extends JPanel implements PropertyChangeListener {

        private final ColonyPanel colonyPanel;

        
        public WarehousePanel(ColonyPanel colonyPanel) {
            this.colonyPanel = colonyPanel;
        }

        public void initialize() {
            
            colony.getGoodsContainer().addPropertyChangeListener(this);
            update();
            revalidate();
            repaint();
        }

        private void update() {
            removeAll();
            for (GoodsType goodsType : FreeCol.getSpecification().getGoodsTypeList()) {
                if (goodsType.isStorable()) {
                    Goods goods = colony.getGoodsContainer().getGoods(goodsType);
                    if (goods.getAmount() >= getClient().getClientOptions()
                        .getInteger(ClientOptions.MIN_NUMBER_FOR_DISPLAYING_GOODS)) {
                        GoodsLabel goodsLabel = new GoodsLabel(goods, getCanvas());
                        if (colonyPanel.isEditable()) {
                            goodsLabel.setTransferHandler(defaultTransferHandler);
                            goodsLabel.addMouseListener(pressListener);
                        }
                        add(goodsLabel, false);
                    }
                }
            }
        }

        @Override
        public String getUIClassID() {
            return "WarehousePanelUI";
        }


        
        public Component add(Component comp, boolean editState) {
            if (editState) {
                if (comp instanceof GoodsLabel) {
                    comp.getParent().remove(comp);
                    ((GoodsLabel) comp).setSmall(false);
                    return comp;
                }
                logger.warning("An invalid component got dropped on this WarehousePanel.");
                return null;
            }

            Component c = add(comp);

            return c;
        }

        public void propertyChange(PropertyChangeEvent event) {
            update();
        }

    }


    
    public final class TilePanel extends FreeColPanel {

        private final ColonyPanel colonyPanel;

        
        public TilePanel(ColonyPanel colonyPanel) {
            super(colonyPanel.getCanvas());
            this.colonyPanel = colonyPanel;
            setBackground(Color.BLACK);
            setBorder(null);
            setLayout(null);
        }

        public void initialize() {
            int layer = 2;

            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 3; y++) {
                    ColonyTile tile = getColony().getColonyTile(x, y);
                    if (tile==null)
                        continue;
                    ASingleTilePanel p = new ASingleTilePanel(tile, x, y);
                    add(p, new Integer(layer));
                    layer++;
                }
            }
        }


        public void removePropertyChangeListeners() {
            for (Component component : getComponents()) {
                if (component instanceof ASingleTilePanel) {
                    ((ASingleTilePanel) component).removePropertyChangeListeners();
                }
            }
        }

        @Override
        public void paintComponent(Graphics g) {
            GUI colonyTileGUI = getCanvas().getColonyTileGUI();
            Game game = colony.getGame();

            g.setColor(Color.black);
            g.fillRect(0, 0, getWidth(), getHeight());

            if (getColony() != null) {
                for (int x = 0; x < 3; x++) {
                    for (int y = 0; y < 3; y++) {
                        TileType tileType = getColony().getTile().getType();
                        Tile tile = getColony().getTile(x, y);
                        if (tile==null)
                            continue;
                        colonyTileGUI.displayColonyTile((Graphics2D) g, game.getMap(), tile, ((2 - x) + y)
                                                        * getLibrary().getTerrainImageWidth(tileType) / 2,
                                                        (x + y) * getLibrary().getTerrainImageHeight(tileType) / 2,
                                                        getColony());

                    }
                }
            }
        }


        
        public final class ASingleTilePanel extends JPanel implements PropertyChangeListener {

            private ColonyTile colonyTile;

            public ASingleTilePanel(ColonyTile colonyTile, int x, int y) {
                setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
                this.colonyTile = colonyTile;

                colonyTile.addPropertyChangeListener(this);

                setOpaque(false);
                TileType tileType = colonyTile.getTile().getType();
                
                setSize(getLibrary().getTerrainImageWidth(tileType), getLibrary().getTerrainImageHeight(tileType));
                setLocation(((2 - x) + y) * getLibrary().getTerrainImageWidth(tileType) / 2,
                            (x + y) * getLibrary().getTerrainImageHeight(tileType) / 2);
                initialize();
            }
                
            private void initialize() {

                removeAll();
                UnitLabel unitLabel = null;
                if (colonyTile.getUnit() != null) {
                    Unit unit = colonyTile.getUnit();
                    unitLabel = new UnitLabel(unit, getCanvas());
                    if (colonyPanel.isEditable()) {
                        unitLabel.setTransferHandler(defaultTransferHandler);
                        unitLabel.addMouseListener(pressListener);
                    }
                    super.add(unitLabel);

                }
                updateDescriptionLabel(unitLabel, true);

                if (colonyTile.isColonyCenterTile()) {
                    initializeAsCenterTile();
                }

                if (colonyPanel.isEditable()) {
                    setTransferHandler(defaultTransferHandler);
                    addMouseListener(releaseListener);
                }

                revalidate();
                repaint();
            }

            
            private void initializeAsCenterTile() {

                setLayout(new GridLayout(2, 1));

                TileType tileType = colonyTile.getTile().getType();
                
                GoodsType primaryGood = colonyTile.getTile().primaryGoods();
                ImageIcon goodsIcon = getLibrary().getGoodsImageIcon(primaryGood);
                ProductionLabel pl = new ProductionLabel(primaryGood, colonyTile.getProductionOf(primaryGood), getCanvas());
                pl.setSize(getLibrary().getTerrainImageWidth(tileType), goodsIcon.getIconHeight());
                add(pl);

                
                GoodsType secondaryGood = colonyTile.getTile().secondaryGoods();
                if (colonyTile.getProductionOf(secondaryGood) != 0) {
                    goodsIcon = getLibrary().getGoodsImageIcon(secondaryGood);
                    ProductionLabel sl = new ProductionLabel(secondaryGood, colonyTile.getProductionOf(secondaryGood), getCanvas());
                    sl.setSize(getLibrary().getTerrainImageWidth(tileType), goodsIcon.getIconHeight());
                    add(sl);
                }
            }

            public void removePropertyChangeListeners() {
                colonyTile.removePropertyChangeListener(this);
            }

            
            private void updateDescriptionLabel() {
                updateDescriptionLabel(null, false);
            }

            
            private void updateDescriptionLabel(UnitLabel unit, boolean toAdd) {
                String tileDescription = this.colonyTile.getLabel();

                if (unit == null) {
                    setToolTipText(tileDescription);
                } else {
                    String unitDescription = unit.getUnit().getName();
                    if (toAdd) {
                        unitDescription = tileDescription + " [" + unitDescription + "]";
                    }
                    unit.setDescriptionLabel(unitDescription);
                }
            }

            
            public Component add(Component comp, boolean editState) {
                Container oldParent = comp.getParent();
                if (editState) {
                    if (comp instanceof UnitLabel) {
                        Unit unit = ((UnitLabel) comp).getUnit();
                        Tile tile = colonyTile.getWorkTile();
                        Player player = unit.getOwner();

                        logger.info("Colony " + colony.getName()
                                    + " claims tile " + tile.toString()
                                    + " with unit " + unit.getId());
                        if ((tile.getOwner() != player
                             || tile.getOwningSettlement() != colony)
                            && !getController().claimLand(tile, colony, 0)) {
                            logger.warning("Colony " + colony.getName()
                                           + " could not claim tile " + tile.toString()
                                           + " with unit " + unit.getId());
                            return null;
                        }

                        if (colonyTile.canAdd(unit)) {
                            oldParent.remove(comp);

                            GoodsType workType = colonyTile.getWorkType(unit);
                            ColonyTile bestTile = colony.getVacantColonyTileFor(unit, workType, false);

                            getController().work(unit, colonyTile);
                            
                            if (workType != unit.getWorkType()) {
                                getController().changeWorkType(unit, workType);
                            }

                            ((UnitLabel) comp).setSmall(false);

                            if (colonyTile != bestTile
                                && (colonyTile.getProductionOf(unit, workType)
                                    < bestTile.getProductionOf(unit, workType))) {
                                getCanvas().showInformationMessage("colonyPanel.notBestTile",
                                                                   "%unit%", unit.getName(),
                                                                   "%goods%", workType.getName(),
                                                                   "%tile%", bestTile.getLabel());
                            }
                        } else {
                            
                            Canvas canvas = getCanvas();
                            Tile workTile = colonyTile.getWorkTile();
                            Settlement s = workTile.getOwningSettlement();

                            if (s != null && s != getColony()) {
                                if (s.getOwner() == player) {
                                    
                                    canvas.errorMessage("tileTakenSelf");
                                } else if (s.getOwner().isEuropean()) {
                                    
                                    canvas.errorMessage("tileTakenEuro");
                                } else if (s instanceof IndianSettlement) {
                                    
                                    canvas.errorMessage("tileTakenInd");
                                }
                            } else {
                                if (!workTile.isLand()) { 
                                    canvas.errorMessage("tileNeedsDocks");
                                } else if (workTile.hasLostCityRumour()) {
                                    canvas.errorMessage("tileHasRumour");
                                }
                            }
                            return null;
                        }
                    } else {
                        logger.warning("An invalid component got dropped on this CargoPanel.");
                        return null;
                    }
                }

                
                return comp;
            }
    
            public void propertyChange(PropertyChangeEvent event) {
                initialize();
            }

            
            @Override
            public boolean contains(int px, int py) {
                

                int activePixels;

                
                if (!super.contains(px, py)) {
                    return false;
                }

                if (py >= 32) {
                    py = 32 - (py - 31);
                }

                
                activePixels = (py * 128) / 64; 
                
                return ((px >= 63 - activePixels) && (px <= 63 + activePixels));
            }
        }
    }
    
    public void propertyChange(PropertyChangeEvent e) {
        if (!isShowing() || getColony() == null) {
            return;
        }
        String property = e.getPropertyName();

        if (Unit.CARGO_CHANGE.equals(property)) {
            updateInPortPanel();
        } else if (ColonyChangeEvent.POPULATION_CHANGE.toString().equals(property)) {
            updateSoLLabel();
        } else if (ColonyChangeEvent.BONUS_CHANGE.toString().equals(property)) {
            ModelMessage msg = getColony().checkForGovMgtChangeMessage();
            if (msg != null) {
                getCanvas().showInformationMessage(msg.getId(), msg.getDisplay(), msg.getData());
            }
            updateSoLLabel();
        } else if (ColonyTile.UNIT_CHANGE.toString().equals(property)) {
            updateTilePanel();
            updateProductionPanel();
        } else if (property.startsWith("model.goods.")) {
            updateProductionPanel();
            updateWarehousePanel();
        } else if (Building.UNIT_CHANGE.equals(property)) {
            
        } else {
            logger.warning("Unknown property change event: " + e.getPropertyName());
        }
    }

}
