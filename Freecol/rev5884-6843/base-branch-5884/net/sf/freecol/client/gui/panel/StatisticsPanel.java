


package net.sf.freecol.client.gui.panel;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.networking.StatisticsMessage;


public final class StatisticsPanel extends FreeColPanel implements ActionListener {

    private static final Logger logger = Logger.getLogger(StatisticsPanel.class.getName());

    class StatisticsModel extends AbstractTableModel {

        private static final int NAME_COLUMN = 0, VALUE_COLUMN = 1;
        private final String[] columnNames = { "Name", "Value" };
        
        private Object data[][] = null;

        
        public StatisticsModel() {
        }

        
        public void setData(HashMap<String,Long> statsData) {
            this.data = new Object[2][statsData.size()];
            int i=0;
            for (String s : statsData.keySet()) {
                data[NAME_COLUMN][i] = s;
                data[VALUE_COLUMN][i] = statsData.get(s);
                i++;
            }
        }

        
        public int getColumnCount() {
            return columnNames.length;
        }

        
        public String getColumnName(int column) {
            return columnNames[column];
        }

        
        public int getRowCount() {
            return data[NAME_COLUMN].length;
        }

        
        public Object getValueAt(int row, int column) {
            if ((row < getRowCount()) && (column < getColumnCount()) && (row >= 0) && (column >= 0)) {
                switch (column) {
                case StatisticsModel.NAME_COLUMN:
                    return data[NAME_COLUMN][row];
                case StatisticsModel.VALUE_COLUMN:
                    return data[VALUE_COLUMN][row];
                }
            }
            return null;
        }

        
        public boolean isCellEditable(int row, int column) {
            return false;
        }
        
        
        public Class<?> getColumnClass(int column) {
            return getValueAt(0, column).getClass();
        }
    }
    
    
    public StatisticsPanel(Canvas parent) {
        super(parent, new BorderLayout());
        
        
        StatisticsMessage serverStatistics = getController().getServerStatistics();
        StatisticsMessage clientStatistics = new StatisticsMessage(getGame(), null);

        
        JPanel header = new JPanel();
        this.add(header, BorderLayout.NORTH);
        header.add(new JLabel("Statistics"),JPanel.CENTER_ALIGNMENT);
        
        
        JPanel statsPanel = new JPanel(new GridLayout(1,2));
        JScrollPane scrollPane = new JScrollPane(statsPanel,
                                                 JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                                 JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);

        this.add(scrollPane,BorderLayout.CENTER);
        statsPanel.add(displayStatsMessage("Client", clientStatistics));
        statsPanel.add(displayStatsMessage("Server", serverStatistics));

        add(okButton, BorderLayout.SOUTH);

        setSize(getPreferredSize());
    }
    
    private JPanel displayStatsMessage(String title, StatisticsMessage statistics) {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder(title));
        Box b = new Box(BoxLayout.Y_AXIS);
        panel.add(b);
        b.add(createStatsTable("Memory", statistics.getMemoryStatistics()));
        b.add(createStatsTable("Game", statistics.getGameStatistics()));
        if (statistics.getAIStatistics()!=null) {
            b.add(createStatsTable("AI", statistics.getAIStatistics()));
        } else {
            b.add(new JLabel());
        }
        return panel;
    }
    
    private JPanel createStatsTable(String title, HashMap<String,Long> data) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(new JLabel(title), BorderLayout.NORTH);
        StatisticsModel model = new StatisticsModel();
        model.setData(data);
        JTable table = new JTable(model);
        table.setAutoCreateColumnsFromModel(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        JScrollPane scrollPane = new JScrollPane(table);
        table.addNotify();
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getColumnHeader().setOpaque(false);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.setPreferredSize(new Dimension(300, (data.size()+2)*17));
        return panel;
    }
    
}
