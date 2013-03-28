


package net.sf.freecol.client.gui.panel;

import java.awt.Color;
import java.awt.Component;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.model.Nation;
import net.sf.freecol.common.model.Player;


public final class NationCellRenderer implements TableCellRenderer {


    private final Nation[] nations;
    private final JComboBox comboBox;
    private List<Player> players;
    private Player thisPlayer;

    
    public NationCellRenderer(Nation[] nations) {
        this.nations = nations;
        this.comboBox = new JComboBox(nations);
    }


    
    public void setData(List<Player> players, Player owningPlayer) {
        this.players = players;
        thisPlayer = owningPlayer;
    }

    private Player getPlayer(int i) {
        if (i == 0) {
            return thisPlayer;
        } else if (players.get(i) == thisPlayer) {
            return players.get(0);
        } else {
            return players.get(i);
        }
    }

    
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        Player player = getPlayer(row);

        Component component;
        if (player == thisPlayer) {
            for (int index = 0; index < nations.length; index++) {
                if (nations[index].getId().equals(player.getNationID())) {
                    comboBox.setSelectedIndex(index);
                    break;
                }
            }
            component = comboBox;
        } else {
            component = new JLabel(Messages.message(player.getNationName()));
        }

        if (player.isReady()) {
            component.setForeground(Color.GRAY);
        } else {
            component.setForeground(table.getForeground());
        }
        component.setBackground(table.getBackground());

        return component;
    }
}
