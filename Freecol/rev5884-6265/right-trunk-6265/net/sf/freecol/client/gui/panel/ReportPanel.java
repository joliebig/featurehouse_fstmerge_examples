

package net.sf.freecol.client.gui.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Comparator;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.Border;

import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.common.model.AbstractUnit;
import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.Europe;
import net.sf.freecol.common.model.FreeColGameObject;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.Unit.Role;
import net.sf.freecol.common.model.UnitType;

import net.miginfocom.swing.MigLayout;


public class ReportPanel extends FreeColPanel implements ActionListener {

    protected static final Logger logger = Logger.getLogger(ReportPanel.class.getName());

    protected JPanel reportPanel;

    protected JLabel header;

    protected JScrollPane scrollPane;

    
    private static Dimension savedSize = new Dimension(850, 600);

    public static final Comparator<Unit> unitTypeComparator = new Comparator<Unit>() {
        public int compare(Unit unit1, Unit unit2) {
            int deltaType = unit2.getType().getIndex() - unit1.getType().getIndex();
            if (deltaType == 0) {
                return unit2.getRole().ordinal() - unit1.getRole().ordinal();
            } else {
                return deltaType;
            }
        }
    };


    
    public ReportPanel(Canvas parent, String title) {
        super(parent);

        setLayout(new MigLayout("wrap 1", "[fill]", "[]30[fill]30[]"));

        header = getDefaultHeader(title);
        add(header, "align center");

        reportPanel = new JPanel();
        reportPanel.setOpaque(true);
        reportPanel.setBorder(createBorder());

        scrollPane = new JScrollPane(reportPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                     JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement( 16 );
        add(scrollPane, "height 100%, width 100%");

        add(okButton, "tag ok");

        setPreferredSize(savedSize);
    }
    
    protected Border createBorder() {
        return new EmptyBorder(20, 20, 20, 20);
    }

    
    public final Dimension getSavedSize() {
        return savedSize;
    }

    
    public final void setSavedSize(final Dimension newSavedSize) {
        this.savedSize = newSavedSize;
    }

    
    public void initialize() {
        reportPanel.removeAll();
        reportPanel.doLayout();
    }

    
    public static Comparator<Unit> getUnitTypeComparator() {
        return unitTypeComparator;
    }

    public JLabel createUnitTypeLabel(AbstractUnit unit) {
        return createUnitTypeLabel(unit.getUnitType(), unit.getRole(), unit.getNumber());
    }

    public JLabel createUnitTypeLabel(UnitType unitType, Role role, int count) {
        ImageIcon unitIcon = getLibrary().getUnitImageIcon(unitType, role, count == 0);
        JLabel unitLabel = new JLabel(getLibrary().getScaledImageIcon(unitIcon, 0.66f));
        unitLabel.setText(String.valueOf(count));
        if (count == 0) {
            unitLabel.setForeground(Color.GRAY);
        }
        unitLabel.setToolTipText(Unit.getName(unitType, role));
        return unitLabel;
    }

    
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        if (OK.equals(command)) {
            getCanvas().remove(this);
        } else {
            FreeColGameObject object = getGame().getFreeColGameObject(command);
            if (object instanceof Colony) {
                getCanvas().showColonyPanel((Colony) object);
            } else if (object instanceof Europe) {
                getCanvas().showEuropePanel();
            } else if (object instanceof Tile) {
                getCanvas().getGUI().setFocus(((Tile) object).getPosition());
            }
        }
    }
}
