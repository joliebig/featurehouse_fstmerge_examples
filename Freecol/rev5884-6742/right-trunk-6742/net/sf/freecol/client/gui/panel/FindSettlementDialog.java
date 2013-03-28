

package net.sf.freecol.client.gui.panel;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.client.gui.plaf.FreeColComboBoxRenderer;
import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.IndianSettlement;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Settlement;
import net.sf.freecol.common.model.StringTemplate;

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

        Action selectAction = new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    selectSettlement();      
                }
            };

        Action quitAction = new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    getCanvas().remove(FindSettlementDialog.this);
                }
            };

        settlementList.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "select");
        settlementList.getActionMap().put("select", selectAction);
        settlementList.getInputMap().put(KeyStroke.getKeyStroke("ESCAPE"), "quit");
        settlementList.getActionMap().put("quit", quitAction);

        MouseListener mouseListener = new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        selectSettlement();
                    }
                }
            };
        settlementList.addMouseListener(mouseListener);

        add(listScroller, "growx, growy");

        add(okButton, "tag ok");

        setSize(getPreferredSize());
    }

    private void selectSettlement() {
        Settlement settlement = (Settlement) settlementList.getSelectedValue();
        Canvas canvas = getCanvas();
        if (settlement instanceof Colony
            && settlement.getOwner() == getMyPlayer()) {
            canvas.remove(FindSettlementDialog.this);
            canvas.showColonyPanel((Colony) settlement);
        } else if (settlement instanceof IndianSettlement) {
            canvas.remove(FindSettlementDialog.this);
            canvas.showIndianSettlementPanel((IndianSettlement) settlement);
        }
    }

    @Override
    public void requestFocus() {
        settlementList.requestFocus();
    }

    
    public void valueChanged(ListSelectionEvent e) {
        Settlement settlement = (Settlement) settlementList.getSelectedValue();
        getCanvas().getGUI().setFocus(settlement.getTile().getPosition());
    }

    private class SettlementRenderer extends FreeColComboBoxRenderer {

        @Override
        public void setLabelValues(JLabel label, Object value) {
            Settlement settlement = (Settlement) value;
            String messageId = settlement.isCapital()
                ? "indianCapitalOwner"
                : "indianSettlementOwner";
            label.setText(Messages.message(StringTemplate.template(messageId)
                                           .addName("%name%", settlement.getName())
                                           .addStringTemplate("%nation%", settlement.getOwner().getNationName())));
            label.setIcon(new ImageIcon(getLibrary().getSettlementImage(settlement)
                                        .getScaledInstance(64, -1, Image.SCALE_SMOOTH)));
        }
    }

} 

