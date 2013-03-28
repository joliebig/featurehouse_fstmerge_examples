

package net.sf.freecol.client.gui.panel;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import net.sf.freecol.FreeCol;
import net.sf.freecol.client.control.ConnectController;
import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.ServerInfo;

import net.miginfocom.swing.MigLayout;



public final class ServerListPanel extends FreeColPanel implements ActionListener {

    private static final Logger logger = Logger.getLogger(ServerListPanel.class.getName());

    private static final int CONNECT = 0, CANCEL = 1;

    private final ConnectController connectController;

    private final JTable table;

    private final ServerListTableModel tableModel;

    private String username;

    private JButton connect;


    
    public ServerListPanel(Canvas parent, ConnectController connectController) {
        super(parent);
        this.connectController = connectController;

        JButton cancel = new JButton("Cancel");
        JScrollPane tableScroll;

        setCancelComponent(cancel);

        connect = new JButton(Messages.message("connect"));

        tableModel = new ServerListTableModel(new ArrayList<ServerInfo>());
        table = new JTable(tableModel);

        DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object o, boolean isSelected, boolean hasFocus,
                    int row, int column) {
                setOpaque(isSelected);
                return super.getTableCellRendererComponent(t, o, isSelected, hasFocus, row, column);
            }
        };
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(dtcr);
        }

        table.setRowHeight(22);

        table.setCellSelectionEnabled(false);
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tableScroll = new JScrollPane(table);
        table.addNotify();
        tableScroll.getViewport().setOpaque(false);
        tableScroll.getColumnHeader().setOpaque(false);

        connect.setActionCommand(String.valueOf(CONNECT));
        connect.addActionListener(this);

        cancel.setActionCommand(String.valueOf(CANCEL));
        cancel.addActionListener(this);

        setLayout(new MigLayout("", "", ""));
        add(tableScroll, "width 400:, height 350:");
        add(connect, "newline 20, split 2");
        add(cancel, "tag cancel");

        setSize(getPreferredSize());
    }

    public void requestFocus() {
        connect.requestFocus();
    }

    
    public void initialize(String username, ArrayList<ServerInfo> arrayList) {
        this.username = username;

        
        
        Iterator<ServerInfo> it = arrayList.iterator();
        while (it.hasNext()) {
            ServerInfo si = it.next();
            if (!si.getVersion().equals(FreeCol.getVersion())) {
                it.remove();
            }
        }

        tableModel.setItems(arrayList);
        setEnabled(true);
        if (arrayList.size() == 0) {
            connect.setEnabled(false);
        } else {
            table.setRowSelectionInterval(0, 0);
        }
    }

    
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        Component[] components = getComponents();
        for (int i = 0; i < components.length; i++) {
            components[i].setEnabled(enabled);
        }

        table.setEnabled(enabled);
    }

    
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();

        try {
            switch (Integer.valueOf(command).intValue()) {
            case CONNECT:
                ServerInfo si = tableModel.getItem(table.getSelectedRow());
                connectController.joinMultiplayerGame(username, si.getAddress(), si.getPort());
                break;
            case CANCEL:
                getCanvas().remove(this);
                getCanvas().showPanel(new NewPanel(getCanvas()));
                break;
            default:
                logger.warning("Invalid Actioncommand: invalid number.");
            }
        } catch (NumberFormatException e) {
            logger.warning("Invalid Actioncommand: not a number.");
        }
    }

    
    public void refreshTable() {
        tableModel.fireTableDataChanged();
    }
}

class ServerListTableModel extends AbstractTableModel {

    private static final String[] columnNames = { Messages.message("name"), Messages.message("host"),
            Messages.message("port"), Messages.message("players"), Messages.message("gameState"), };

    private List<ServerInfo> items;


    public ServerListTableModel(List<ServerInfo> items) {
        this.items = items;
    }

    
    public void setItems(List<ServerInfo> items) {
        this.items = items;
    }

    
    public ServerInfo getItem(int row) {
        return items.get(row);
    }

    
    public int getColumnCount() {
        return columnNames.length;
    }

    
    public String getColumnName(int column) {
        return columnNames[column];
    }

    
    public int getRowCount() {
        return items.size();
    }

    
    public Object getValueAt(int row, int column) {
        if ((row < getRowCount()) && (column < getColumnCount()) && (row >= 0) && (column >= 0)) {
            ServerInfo si = items.get(row);
            switch (column) {
            case 0:
                return si.getName();
            case 1:
                return si.getAddress();
            case 2:
                return Integer.toString(si.getPort());
            case 3:
                return Integer.toString(si.getCurrentlyPlaying()) + "/"
                        + Integer.toString(si.getCurrentlyPlaying() + si.getSlotsAvailable());
            case 4:
                return Messages.message("gameState." + Integer.toString(si.getGameState()));
            default:
                return null;
            }
        }
        return null;
    }
}
