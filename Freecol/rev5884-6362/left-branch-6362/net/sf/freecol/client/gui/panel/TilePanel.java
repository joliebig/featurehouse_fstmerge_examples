


package net.sf.freecol.client.gui.panel;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.ComponentInputMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import net.sf.freecol.FreeCol;
import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.model.AbstractGoods;
import net.sf.freecol.common.model.GoodsType;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.TileType;
import net.sf.freecol.common.model.UnitType;

import net.miginfocom.swing.MigLayout;



public final class TilePanel extends FreeColPanel {

    private static final Logger logger = Logger.getLogger(TilePanel.class.getName());

    private static final String COLOPEDIA = "COLOPEDIA";

    private TileType tileType;


    
    public TilePanel(Canvas parent, Tile tile) {
        super(parent);

        tileType = tile.getType();

        setLayout(new MigLayout("wrap 1, insets 20 30 10 30", "[center]", ""));

        JButton colopediaButton = new JButton(Messages.message("menuBar.colopedia"));
        colopediaButton.setActionCommand(String.valueOf(COLOPEDIA));
        colopediaButton.addActionListener(this);
        enterPressesWhenFocused(colopediaButton);

        
        InputMap inputMap = new ComponentInputMap(okButton);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false), "pressed");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, true), "released");
        SwingUtilities.replaceUIInputMap(okButton, JComponent.WHEN_IN_FOCUSED_WINDOW, inputMap);        

        String name = tile.getLabel() + " (" + tile.getX() + ", " + tile.getY() + ")";
        add(new JLabel(name));

        int width = getLibrary().getTerrainImageWidth(tileType);
        int height = getLibrary().getCompoundTerrainImageHeight(tileType);
        int baseHeight = getLibrary().getTerrainImageHeight(tileType);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        getCanvas().getGUI().displayColonyTile((Graphics2D) image.getGraphics(), tile.getMap(),
                                          tile, 0, height - baseHeight, null);
        add(new JLabel(new ImageIcon(image)));

        if (tile.getRegion() != null) {
            add(new JLabel(tile.getRegion().getDisplayName()));
        }
        if (tile.getOwner() != null) {
            String ownerName = tile.getOwner().getNationAsString();
            if (ownerName != null) {
                add(new JLabel(ownerName));
            }
        }

        if (tileType != null) {
            
            UnitType colonist = FreeCol.getSpecification().getUnitType("model.unit.freeColonist");

            JLabel label = null;
            boolean first = true;
            for (GoodsType goodsType : FreeCol.getSpecification().getFarmedGoodsTypeList()) {
                int potential = tile.potential(goodsType, colonist);
                UnitType expert = FreeCol.getSpecification().getExpertForProducing(goodsType);
                int expertPotential = tile.potential(goodsType, expert);
                if (potential > 0) {
                    label = new JLabel(String.valueOf(potential),
                                       getLibrary().getGoodsImageIcon(goodsType),
                                       JLabel.CENTER);
                    if (first) {
                        add(label, "split");
                        first = false;
                    } else {
                        add(label);
                    }
                }
                if (expertPotential > potential) {
                    if (label == null) {
                        
                        
                        label = new JLabel(String.valueOf(expertPotential),
                                           getLibrary().getGoodsImageIcon(goodsType),
                                           JLabel.CENTER);
                        label.setToolTipText(expert.getName());
                        if (first) {
                            add(label, "split");
                            first = false;
                        } else {
                            add(new JLabel("/"));
                            add(label);
                        }
                    } else {
                        label.setText(String.valueOf(potential) + "/" +
                                      String.valueOf(expertPotential));
                        label.setToolTipText(colonist.getName() + "/" +
                                             expert.getName());
                    }
                }
            }
        }

        add(okButton, "newline 30, split 2, align center, tag ok");
        add(colopediaButton, "tag help");

        setSize(getPreferredSize());

    }


    
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        if (OK.equals(command)) {
            getCanvas().remove(this);
        } else {
            getCanvas().showPanel(new ColopediaPanel(getCanvas(),
                                                     ColopediaPanel.PanelType.TERRAIN, tileType));
        }
    }
}
