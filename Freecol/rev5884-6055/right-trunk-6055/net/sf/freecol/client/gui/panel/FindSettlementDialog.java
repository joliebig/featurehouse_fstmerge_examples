

package net.sf.freecol.client.gui.panel;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.client.gui.plaf.FreeColComboBoxRenderer;
import net.sf.freecol.common.model.Settlement;
import net.sf.freecol.common.model.Player;

import net.miginfocom.swing.MigLayout;



public final class FindSettlementDialog extends FreeColDialog implements ListSelectionListener {

    private static final Logger logger = Logger.getLogger(FindSettlementDialog.class.getName());

    private List<Settlement> knownSettlements = new ArrayList<Settlement>();

    private JList settlementList;


    private static Comparator<Settlement> settlementComparator = new Comparator<Settlement>() {
        public int compare(Settlement s1, Settlement s2) {
            return s1.getName().compareTo(s2.getName());
        }
    };


    
    public FindSettlementDialog(Canvas parent) {
        super(parent);

        for (Player player : getGame().getPlayers()) {
            knownSettlements.addAll(player.getSettlements());
        }

        Collections.sort(knownSettlements, settlementComparator);

        MigLayout layout = new MigLayout("wrap 1, fill", "[align center]", "[]30[]30[]");
        setLayout(layout);

        JLabel header = new JLabel(Messages.message("findSettlementDialog.name"));
        header.setFont(smallHeaderFont);
        add(header);

        settlementList = new JList(knownSettlements.toArray(new Settlement[knownSettlements.size()]));
        settlementList.setCellRenderer(new SettlementRenderer());
        settlementList.setFixedCellHeight(48);
        JScrollPane listScroller = new JScrollPane(settlementList);
        listScroller.setPreferredSize(new Dimension(250, 250));
        settlementList.addListSelectionListener(this);
        add(listScroller, "growx, growy");

        add(okButton, "tag ok");

        setSize(getPreferredSize());
    }

    
    public void valueChanged(ListSelectionEvent e) {
        Settlement settlement = (Settlement) settlementList.getSelectedValue();
        getCanvas().getGUI().setFocus(settlement.getTile().getPosition());
    }

    private class SettlementRenderer extends FreeColComboBoxRenderer {

        @Override
        public void setLabelValues(JLabel label, Object value) {
            Settlement settlement = (Settlement) value;
            label.setText(settlement.getName()
                          + (settlement.isCapital() ? "* (" : " (")
                          + settlement.getOwner().getNationAsString() + ")");
            label.setIcon(new ImageIcon(getLibrary().getSettlementImage(settlement)
                                        .getScaledInstance(64, -1, Image.SCALE_SMOOTH)));
        }
    }

} 

