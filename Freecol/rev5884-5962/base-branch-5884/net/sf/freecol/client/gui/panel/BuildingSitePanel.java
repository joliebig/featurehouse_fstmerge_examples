

package net.sf.freecol.client.gui.panel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolTip;

import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.model.AbstractGoods;
import net.sf.freecol.common.model.BuildableType;
import net.sf.freecol.common.model.Building;
import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.Colony.ColonyChangeEvent;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.resources.ResourceManager;

import net.miginfocom.swing.MigLayout;


public class BuildingSitePanel extends JPanel implements PropertyChangeListener {

    private final Canvas parent;

    private final Colony colony;

    private BuildableType buildable;

    
    public BuildingSitePanel(final Colony colony, final Canvas parent) {

        this.colony = colony;
        this.parent = parent;

        
        
        colony.addPropertyChangeListener(this);
        addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    BuildQueuePanel queuePanel = new BuildQueuePanel(colony, parent);
                    parent.showSubPanel(queuePanel);
                }
            });

        setToolTipText(" ");
        setLayout(new MigLayout("fill", "", ""));

        initialize();
    }

    public void initialize() {
   
        removeAll();

        buildable = colony.getCurrentlyBuilding();

        if (buildable != null) {
            JLabel turnsLabel = new JLabel();
            turnsLabel.setBackground(Color.WHITE);
            turnsLabel.setOpaque(true);
            String turnsStr = Messages.message("notApplicable.short");
            int turnsLeft = colony.getTurnsToComplete(buildable);
            if (turnsLeft >= 0) {
                turnsStr = Integer.toString(turnsLeft);
            }
            else if(turnsLeft != Integer.MIN_VALUE){
                turnsStr = ">" + Integer.toString(turnsLeft*-1);
            }
            turnsLabel.setText(turnsStr);
          
            add(turnsLabel, "align center, wrap");
        }

        revalidate();
        repaint();
    }

    
    public void paintComponent(Graphics g) {
        int width = 128;
        int height = 96;

 
        Image bgImage = ResourceManager.getImage("model.building.BuildingSite.image");
        if (buildable != null) {
            bgImage = ResourceManager.getImage(buildable.getId() + ".image");
        }
 
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

    public JToolTip createToolTip() {
        return new BuildingSiteToolTip(colony, parent);
    }

    public void propertyChange(PropertyChangeEvent event) {
        initialize();
    }

    public void removePropertyChangeListeners() {
        colony.removePropertyChangeListener(this);
    }

}


