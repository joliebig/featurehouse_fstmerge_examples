

package net.sf.freecol.client.gui.panel;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.model.AbstractGoods;
import net.sf.freecol.common.model.BuildableType;
import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.StringTemplate;
import net.sf.freecol.common.resources.ResourceManager;


public class ConstructionPanel extends JPanel implements PropertyChangeListener {

    private final Canvas parent;

    private Colony colony;

    private BuildableType buildable;

    public static final String EVENT = Colony.ColonyChangeEvent.BUILD_QUEUE_CHANGE.toString();

    
    private StringTemplate defaultLabel = StringTemplate.key("colonyPanel.clickToBuild");

    
    public ConstructionPanel(final Canvas parent, Colony colony) {

        this.parent = parent;
        setLayout(new MigLayout("fill", "push[]10[]push", "0[]0"));
        setColony(colony);
    }

    public void setColony(Colony newColony) {
        if(newColony != colony){
            if (colony != null) {
                colony.removePropertyChangeListener(EVENT, this);
            }
            this.colony = newColony;

            
            
            colony.addPropertyChangeListener(EVENT, this);
            addMouseListener(new MouseAdapter() {
                    public void mousePressed(MouseEvent e) {
                        BuildQueuePanel queuePanel = new BuildQueuePanel(colony, parent);
                        parent.showSubPanel(queuePanel);
                    }
                });
        }
        initialize(colony.getCurrentlyBuilding());
    }

    private void initialize(BuildableType buildable) {
   
        removeAll();

        if (buildable == null) {
            add(new JLabel(Messages.message(getDefaultLabel())),
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
            add(new JLabel(new ImageIcon(ResourceManager.getImage(buildable.getId() + ".image", 0.75))));
            add(new JLabel(Messages.message("colonyPanel.currentlyBuilding",
                                            "%buildable%", Messages.message(buildable.getNameKey()))),
                "span, align center, flowy, split " + (2 + buildable.getGoodsRequired().size()));

            add(new JLabel(Messages.message("turnsToComplete.long",
                                            "%number%", turns)),
                "span, align center");

            for (AbstractGoods requiredGoods : buildable.getGoodsRequired()) {
                int amountNeeded = requiredGoods.getAmount();
                int amountAvailable = colony.getGoodsCount(requiredGoods.getType());
                int amountProduced = colony.getProductionNetOf(requiredGoods.getType());
                add(new FreeColProgressBar(parent, requiredGoods.getType(), 0,
                                           amountNeeded, amountAvailable, amountProduced),
                    "height 20:");
            }
        }

        revalidate();
        repaint();
    }

    
    public final StringTemplate getDefaultLabel() {
        return defaultLabel;
    }

    
    public final void setDefaultLabel(final StringTemplate newDefaultLabel) {
        this.defaultLabel = newDefaultLabel;
    }

    public void propertyChange(PropertyChangeEvent event) {
        List buildQueue = (List) event.getNewValue();
        if (buildQueue == null || buildQueue.isEmpty()) {
            initialize(null);
        } else {
            initialize((BuildableType) buildQueue.get(0));
        }
    }

    public void removePropertyChangeListeners() {
        colony.removePropertyChangeListener(this);
    }
}


