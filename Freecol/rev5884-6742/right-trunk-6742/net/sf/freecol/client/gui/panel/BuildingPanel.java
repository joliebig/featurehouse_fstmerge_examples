

package net.sf.freecol.client.gui.panel;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.awt.Graphics2D;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolTip;

import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.common.model.Building;
import net.sf.freecol.common.model.GoodsType;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.resources.ResourceManager;

import net.miginfocom.swing.MigLayout;


public class BuildingPanel extends JPanel implements PropertyChangeListener {

    private final Canvas parent;

    private final Building building;

    private ProductionLabel productionOutput = null;;

    private List<UnitLabel> unitLabels = new ArrayList<UnitLabel>();

    
    public BuildingPanel(Building building, Canvas parent) {

        this.building = building;
        this.parent = parent;

        building.addPropertyChangeListener(this);
        GoodsType inputType = building.getGoodsInputType();
        if (inputType != null) {
            
            building.getColony().addPropertyChangeListener(inputType.getId(), this);
        }

        setToolTipText(" ");
        setLayout(new MigLayout("", "[32][32][32]", "[32][44]"));
        initialize();
    }

    public void initialize() {
   
        removeAll();
        unitLabels.clear();

        if (building.getProductionNextTurn() == 0) {
            add(new JLabel(), "span");
        } else {
            productionOutput = new ProductionLabel(building.getGoodsOutputType(),
                                                   building.getProductionNextTurn(),
                                                   building.getMaximumProduction(), parent);
            add(productionOutput, "span, align center");
        }

        for (Unit unit : building.getUnitList()) {
            UnitLabel unitLabel = new UnitLabel(unit, parent, true);
            unitLabels.add(unitLabel);
            add(unitLabel);
        }


        setSize(new Dimension(96,76));
        revalidate();
        repaint();
    }

    public void removePropertyChangeListeners() {
        building.removePropertyChangeListener(this);
        GoodsType inputType = building.getGoodsInputType();
        if (inputType != null) {
            building.getColony().removePropertyChangeListener(inputType.getId(), this);
        }
    }

    
    public void paintComponent(Graphics g) {
        int width = 128;
        int height = 96;

        BufferedImage bgImage = fadeImage(ResourceManager.getImage(building.getType().getId() + ".image"), 0.6f, 192.0f);
        if (bgImage != null) {
            g.drawImage(bgImage, 0, 0, this);
        } else {
            Image tempImage = ResourceManager.getImage("BackgroundImage");

            if (tempImage != null) {
                for (int x = 0; x < width; x += tempImage.getWidth(null)) {
                    for (int y = 0; y < height; y += tempImage.getHeight(null)) {
                        g.drawImage(tempImage, x, y, null);
                    }
                }
            } else {
                g.setColor(getBackground());
                g.fillRect(0, 0, width, height);
            }
        }
    }

    public BufferedImage fadeImage(Image img, float fade, float target) {
        int w = img.getWidth(null);
        int h = img.getHeight(null);
        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.getGraphics();
        g.drawImage(img, 0, 0, null);

        float offset = target * (1.0f - fade);
        float[] scales = { fade, fade, fade, 1.0f };
        float[] offsets = { offset, offset, offset, 0.0f };
        RescaleOp rop = new RescaleOp(scales, offsets, null);

        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(bi, rop, 0, 0);
		return bi;
    }

    public Building getBuilding() {
        return building;
    }

    public void updateProductionLabel() {
        initialize();
    }

    public List<UnitLabel> getUnitLabels() {
        return unitLabels;
    }

    public JToolTip createToolTip() {
        return new BuildingToolTip(building, parent);
    }

    public void propertyChange(PropertyChangeEvent event) {
        initialize();
    }

}


