


package net.sf.freecol.client.gui.panel;

import java.awt.Color;
import java.awt.Component;
import java.util.List;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import net.sf.freecol.common.Specification;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.model.EuropeanNationType;
import net.sf.freecol.common.model.Nation;
import net.sf.freecol.common.model.NationType;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.NationOptions.Advantages;


public final class AdvantageCellRenderer implements TableCellRenderer {

    private static Vector<EuropeanNationType> europeans = 
        new Vector<EuropeanNationType>(Specification.getSpecification().getEuropeanNationTypes());

    private Advantages advantages;

    
    public AdvantageCellRenderer(Advantages advantages) {
        this.advantages = advantages;
    }

    
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        Player player = (Player) table.getValueAt(row, PlayersTable.PLAYER_COLUMN);
        NationType nationType = ((Nation) table.getValueAt(row, PlayersTable.NATION_COLUMN)).getType();

        JLabel label;
        switch(advantages) {
        case FIXED:
            label = new JLabel(Messages.getName(nationType));
            break;
        case SELECTABLE:
            if (player == null) {
                return new JLabel(Messages.getName(nationType));
            } else {
                return new JLabel(Messages.getName(player.getNationType()));
            }
        case NONE:
        default:
            label = new JLabel(Messages.message("model.nationType.none.name"));
            break;
        }
        if (player != null && player.isReady()) {
            label.setForeground(Color.GRAY);
        } else {
            label.setForeground(table.getForeground());
        }
        label.setBackground(table.getBackground());

        return label;
    }
}
