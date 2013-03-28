

package net.sf.freecol.client.gui.panel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.GUI;
import net.sf.freecol.client.gui.ViewMode;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.client.gui.panel.MapEditorTransformPanel.MapTransform;
import net.sf.freecol.common.model.AbstractGoods;
import net.sf.freecol.common.model.EquipmentType;
import net.sf.freecol.common.model.Goods;
import net.sf.freecol.common.model.StringTemplate;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.TileImprovement;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.resources.ResourceManager;

import net.miginfocom.swing.MigLayout;


public final class InfoPanel extends FreeColPanel {
    
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(InfoPanel.class.getName());
    
    private static final int PANEL_WIDTH = 256;
    
    private static final int PANEL_HEIGHT = 128;
    
    private final EndTurnPanel endTurnPanel = new EndTurnPanel();
    
    private final UnitInfoPanel unitInfoPanel;
    
    private final TileInfoPanel tileInfoPanel = new TileInfoPanel();
    
    private final JPanel mapEditorPanel;
    
    
    
    public InfoPanel(final FreeColClient freeColClient) {
        super(freeColClient.getCanvas());
        
        unitInfoPanel = new UnitInfoPanel();
        setLayout(null);
        
        int internalPanelTop = 0;
        int internalPanelHeight = 128;
        Image skin = ResourceManager.getImage("InfoPanel.skin");
        if (skin == null) {
            setSize(PANEL_WIDTH, PANEL_HEIGHT);
        } else {
            setBorder(null);
            setSize(skin.getWidth(null), skin.getHeight(null));
            setOpaque(false);
            internalPanelTop = 75;
            internalPanelHeight = 100;
        }
        
        mapEditorPanel = new JPanel(null);
        mapEditorPanel.setSize(130, 100);
        mapEditorPanel.setOpaque(false);
        
        add(unitInfoPanel, internalPanelTop, internalPanelHeight);
        add(endTurnPanel, internalPanelTop, internalPanelHeight);
        add(tileInfoPanel, internalPanelTop, internalPanelHeight);
        add(mapEditorPanel, internalPanelTop, internalPanelHeight);

        addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    GUI gui = getClient().getGUI();
                    Unit activeUnit = gui.getActiveUnit();
                    if (activeUnit != null && activeUnit.getTile() != null) {
                        gui.setFocus(activeUnit.getTile().getPosition());
                    }
                }
            });
    }
    
    
    private void add(JPanel panel, int internalPanelTop, int internalPanelHeight) {
        panel.setVisible(false);
        panel.setLocation((getWidth() - panel.getWidth()) / 2, internalPanelTop
                + (internalPanelHeight - panel.getHeight()) / 2);
        add(panel);
    }
    
    
    public void update(Unit unit) {
        unitInfoPanel.update(unit);
    }
    
    
    public void update(MapTransform mapTransform) {
        if (mapTransform != null) {
            final JPanel p = mapTransform.getDescriptionPanel();
            if (p != null) {
                p.setOpaque(false);
                final Dimension d = p.getPreferredSize();
                p.setBounds(0, (mapEditorPanel.getHeight() - d.height)/2, mapEditorPanel.getWidth(), d.height);
                mapEditorPanel.removeAll();
                mapEditorPanel.add(p, BorderLayout.CENTER);
                mapEditorPanel.validate();
                mapEditorPanel.revalidate();
                mapEditorPanel.repaint();
            }
        }
    }
    
    
    
    public void update(Tile tile) {
        tileInfoPanel.update(tile);
    }
    
    
    public Unit getUnit() {
        return unitInfoPanel.getUnit();
    }
    
    
    public Tile getTile() {
        return tileInfoPanel.getTile();
    }
    
    
    public void paintComponent(Graphics graphics) {
        int viewMode = getClient().getGUI().getViewMode().getView();
        if (!getClient().isMapEditor()) {
            if (mapEditorPanel.isVisible()) {
                mapEditorPanel.setVisible(false);
            }
            switch (viewMode) {
            case ViewMode.MOVE_UNITS_MODE:
                if (unitInfoPanel.getUnit() != null) {
                    if (!unitInfoPanel.isVisible()) {
                        unitInfoPanel.setVisible(true);
                        endTurnPanel.setVisible(false);
                    }
                } else if (getMyPlayer() != null
                        && !getMyPlayer().hasNextActiveUnit()) {
                    if (!endTurnPanel.isVisible()) {
                        endTurnPanel.setVisible(true);
                        unitInfoPanel.setVisible(false);
                    }
                }
                tileInfoPanel.setVisible(false);
                break;
            case ViewMode.VIEW_TERRAIN_MODE:
                unitInfoPanel.setVisible(false);
                endTurnPanel.setVisible(false);
                tileInfoPanel.setVisible(true);
                break;
            }
        } else {
            if (!mapEditorPanel.isVisible()) {
                mapEditorPanel.setVisible(true);
                unitInfoPanel.setVisible(false);
                endTurnPanel.setVisible(false);
                tileInfoPanel.setVisible(false);
            }
        }
        
        Image skin = ResourceManager.getImage("InfoPanel.skin");
        if (skin != null) {
            graphics.drawImage(skin, 0, 0, null);
        }
        
        super.paintComponent(graphics);
    }
    
    
    
    public class TileInfoPanel extends JPanel {
        
        private Tile tile;
        private Font font = new JLabel().getFont().deriveFont(9f);
        
        public TileInfoPanel() {
            super(null);
            
            setSize(226, 100);
            setOpaque(false);
            setLayout(new MigLayout("fill, wrap 2, gap 0 0", "", "[][][][][nogrid]"));
        }
        
        
        public void update(Tile tile) {

            this.tile = tile;
            
            removeAll();

            if (tile != null) {
                int width = getLibrary().getTerrainImageWidth(tile.getType());
                int height = getLibrary().getTerrainImageHeight(tile.getType());
                BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                getClient().getGUI().displayTerrain(image.createGraphics(), 
                                                      getClient().getGame().getMap(),
                                                      tile, 0, 0);
                if (tile.isExplored()) {
                    StringTemplate items = StringTemplate.label(", ");
                    items.add(tile.getNameKey());
                    for (TileImprovement tileImprovement : tile.getCompletedTileImprovements()) {
                        items.add(tileImprovement.getType().getDescriptionKey());
                    }
                    add(new JLabel(Messages.message(items)), "span, align center");

                    add(new JLabel(new ImageIcon(image)), "span 1 3");
                    if (tile.getOwner() == null) {
                        add(new JLabel());
                    } else {
                        JLabel ownerLabel = localizedLabel(tile.getOwner().getNationName());
                        ownerLabel.setFont(font);
                        add(ownerLabel);
                    }

                    int defenceBonus = (int) tile.getType().getFeatureContainer()
                        .applyModifier(100, "model.modifier.defence") - 100;
                    JLabel defenceLabel = new JLabel(Messages.message("colopedia.terrain.defenseBonus") +
                                                     " " + defenceBonus + "%");
                    defenceLabel.setFont(font);
                    add(defenceLabel);
                    JLabel moveLabel = new JLabel(Messages.message("colopedia.terrain.movementCost") +
                                                  " " + String.valueOf(tile.getType().getBasicMoveCost()/3));
                    moveLabel.setFont(font);
                    add(moveLabel);

                    List<AbstractGoods> production = tile.getType().getProduction();
                    for (AbstractGoods goods : production) {
                        JLabel goodsLabel = new JLabel(String.valueOf(tile.potential(goods.getType(), null)),
                                                       getLibrary().getScaledGoodsImageIcon(goods.getType(), 0.50f),
                                                       JLabel.RIGHT);
                        goodsLabel.setToolTipText(Messages.message(goods.getType().getNameKey()));
                        goodsLabel.setFont(font);
                        add(goodsLabel);
                    }
                } else {
                    add(new JLabel(Messages.message("unexplored")), "span, align center");
                    add(new JLabel(new ImageIcon(image)), "spany");
                }
                revalidate();
                repaint();
            }
        }
        
        
        public Tile getTile() {
            return tile;
        }
    }
    
    
    public class UnitInfoPanel extends JPanel {
        
        private Unit unit;
        
        public UnitInfoPanel() {

            super(new MigLayout("wrap 6, fill, gap 0 0", "", ""));
            
            setSize(226, 100);
            setOpaque(false);
        }
        
        
        public void update(Unit unit) {
            this.unit = unit;
            
            removeAll();
            if (unit != null) {
                add(new JLabel(getLibrary().getUnitImageIcon(unit)), "spany, gapafter 5px");
                String name = Messages.message(Messages.getLabel(unit));
                
                int index = name.indexOf(" (");
                if (index < 0) {
                    add(new JLabel(name), "span");
                } else {
                    add(new JLabel(name.substring(0, index)), "span");
                    add(new JLabel(name.substring(index + 1)), "span");
                }
                add(new JLabel(Messages.message("moves") + " " + unit.getMovesAsString()), "span");
                
                
                if (unit.canCarryTreasure()) {
                    add(new JLabel(unit.getTreasureAmount() + " " + Messages.message("gold")), "span");
                } else if (unit.isCarrier()) {
                    for (Goods goods : unit.getGoodsList()) {
                        JLabel goodsLabel = new JLabel(getLibrary().getScaledGoodsImageIcon(goods.getType(), 0.66f));
                        goodsLabel.setToolTipText(Messages.message(StringTemplate.template("model.goods.goodsAmount")
                                                                   .addAmount("%amount%", goods.getAmount())
                                                                   .add("%goods%", goods.getNameKey())));
                        add(goodsLabel);
                    }
                    for (Unit carriedUnit : unit.getUnitList()) {
                        ImageIcon unitIcon = getLibrary().getUnitImageIcon(carriedUnit);
                        JLabel unitLabel = new JLabel(getLibrary().getScaledImageIcon(unitIcon, 0.5f));
                        unitLabel.setToolTipText(Messages.message(carriedUnit.getLabel()));
                        add(unitLabel);
                    }
                } else {
                    for (EquipmentType equipment : unit.getEquipment().keySet()) {
                        for (AbstractGoods goods : equipment.getGoodsRequired()) {
                            int amount = goods.getAmount() * unit.getEquipment().getCount(equipment);
                            JLabel equipmentLabel = 
                                new JLabel(Integer.toString(amount),
                                           getLibrary().getScaledGoodsImageIcon(goods.getType(), 0.66f),
                                           JLabel.CENTER);
                            equipmentLabel
                                .setToolTipText(Messages.message(StringTemplate.template("model.goods.goodsAmount")
                                                                 .addAmount("%amount%", amount)
                                                                 .add("%goods%", goods.getNameKey())));
                            add(equipmentLabel);
                        }
                    }
                }
            }
            revalidate();
            repaint();
        }
        
        
        public Unit getUnit() {
            return unit;
        }
        
    }
    
    
    public class EndTurnPanel extends JPanel {
        
        private JButton endTurnButton = new JButton(Messages.message("infoPanel.endTurnPanel.endTurnButton"));
        
        public EndTurnPanel() {
            super(new MigLayout("wrap 1, center", "[center]", ""));

            String labelString = Messages.message("infoPanel.endTurnPanel.text");
            int width = getFontMetrics(getFont()).stringWidth(labelString);
            if (width > 150 ) {
                int index = getCanvas().getGUI().getBreakingPoint(labelString);
                if (index > 0) {
                    add(new JLabel(labelString.substring(0, index)));
                    add(new JLabel(labelString.substring(index + 1)));
                } else {
                    add(new JLabel(labelString));
                }
            } else {
                add(new JLabel(labelString));
            }

            add(endTurnButton);
            setOpaque(false);
            setSize(getPreferredSize());
            
            
            
            
            endTurnButton.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    getController().endTurn();
                }
            });
        }
    }
}
