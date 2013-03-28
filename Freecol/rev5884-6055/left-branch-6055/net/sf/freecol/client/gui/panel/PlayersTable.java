

package net.sf.freecol.client.gui.panel;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import net.sf.freecol.FreeCol;
import net.sf.freecol.client.control.PreGameController;
import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.ImageLibrary;
import net.sf.freecol.client.gui.panel.ColopediaPanel.PanelType;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.Specification;
import net.sf.freecol.common.model.EuropeanNationType;
import net.sf.freecol.common.model.Nation;
import net.sf.freecol.common.model.NationOptions;
import net.sf.freecol.common.model.NationOptions.Advantages;
import net.sf.freecol.common.model.NationOptions.NationState;
import net.sf.freecol.common.model.NationType;
import net.sf.freecol.common.model.Player;


public final class PlayersTable extends JTable {

    private static final Logger logger = Logger.getLogger(PlayersTable.class.getName());

    public static final int NATION_COLUMN = 0, AVAILABILITY_COLUMN = 1, ADVANTAGE_COLUMN = 2,
        COLOR_COLUMN = 3, PLAYER_COLUMN = 4;

    private static final String[] columnNames = {
        Messages.message("nation"),
        Messages.message("availability"),
        Messages.message("advantage"),
        Messages.message("color"),
        Messages.message("player")
    };


    private static final EuropeanNationType[] europeans = 
        FreeCol.getSpecification().getEuropeanNationTypes().toArray(new EuropeanNationType[0]);

    private static final NationState[] allStates = new NationState[] {
        NationState.AVAILABLE,
        NationState.AI_ONLY,
        NationState.NOT_AVAILABLE
    };

    private static final NationState[] aiStates = new NationState[] {
        NationState.AI_ONLY,
        NationState.NOT_AVAILABLE
    };

    private final ImageLibrary library;

    
    public PlayersTable(final Canvas canvas, NationOptions nationOptions, Player myPlayer) {
        super();

        library = canvas.getImageLibrary();

        setModel(new PlayersTableModel(canvas.getClient().getPreGameController(), nationOptions, myPlayer));
        setRowHeight(47);

        JButton nationButton = new JButton(Messages.message("nation"));
        JLabel availabilityLabel = new JLabel(Messages.message("availability"));
        JButton advantageButton = new JButton(Messages.message("advantage"));
        JLabel colorLabel = new JLabel(Messages.message("color"));
        JLabel playerLabel = new JLabel(Messages.message("player"));

        nationButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    canvas.showColopediaPanel(PanelType.NATIONS);
                }
            });

        advantageButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    canvas.showColopediaPanel(PanelType.NATION_TYPES);
                }
            });

        DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
        dtcr.setOpaque(false);

        HeaderRenderer renderer = new HeaderRenderer(nationButton, availabilityLabel,
                                                     advantageButton, colorLabel, playerLabel);
        JTableHeader header = getTableHeader();
        header.addMouseListener(new HeaderListener(header, renderer));

        TableColumn nationColumn = getColumnModel().getColumn(NATION_COLUMN);
        nationColumn.setCellRenderer(new NationCellRenderer());
        nationColumn.setHeaderRenderer(renderer);

        TableColumn availableColumn = getColumnModel().getColumn(AVAILABILITY_COLUMN);
        availableColumn.setCellRenderer(new AvailableCellRenderer());
        availableColumn.setCellEditor(new AvailableCellEditor());
        
        TableColumn advantagesColumn = getColumnModel().getColumn(ADVANTAGE_COLUMN);
        if (nationOptions.getNationalAdvantages() == NationOptions.Advantages.SELECTABLE) {
            advantagesColumn.setCellEditor(new AdvantageCellEditor());
        }
        advantagesColumn.setCellRenderer(new AdvantageCellRenderer(nationOptions.getNationalAdvantages()));
        advantagesColumn.setHeaderRenderer(renderer);

        TableColumn colorsColumn = getColumnModel().getColumn(COLOR_COLUMN);
        ColorCellEditor colorCellEditor = new ColorCellEditor(canvas);
        colorsColumn.setCellEditor(colorCellEditor);
        colorsColumn.setCellRenderer(new ColorCellRenderer(true));

        TableColumn playerColumn = getColumnModel().getColumn(PLAYER_COLUMN);
        playerColumn.setCellEditor(new PlayerCellEditor());
        playerColumn.setCellRenderer(new PlayerCellRenderer());
        
    }

    public void update() {
        ((PlayersTableModel) getModel()).update();
    }

    private class HeaderRenderer implements TableCellRenderer {

        private static final int NO_COLUMN = -1;
        private int pressedColumn = NO_COLUMN;
        private Component[] components;

        public HeaderRenderer(Component... components) {
            this.components = components;
        }

        public Component getTableCellRendererComponent(JTable table,
                                                       Object value,
                                                       boolean isSelected,
                                                       boolean hasFocus,
                                                       int row,
                                                       int column) {
            if (components[column] instanceof JButton) {
                boolean isPressed = (column == pressedColumn);
                ((JButton) components[column]).getModel().setPressed(isPressed);
                ((JButton) components[column]).getModel().setArmed(isPressed);
            }
            return components[column];
        }

        public void setPressedColumn(int column) {
            pressedColumn = column;
        }
    }

    private class HeaderListener extends MouseAdapter {
        JTableHeader header;

        HeaderRenderer renderer;

        HeaderListener(JTableHeader header, HeaderRenderer renderer) {
            this.header = header;
            this.renderer = renderer;
        }

        public void mousePressed(MouseEvent e) {
            int col = header.columnAtPoint(e.getPoint());
            renderer.setPressedColumn(col);
            header.repaint();
        }

        public void mouseReleased(MouseEvent e) {
            renderer.setPressedColumn(HeaderRenderer.NO_COLUMN);
            header.repaint();
        }
    }


    class NationCellRenderer extends JLabel implements TableCellRenderer {

        
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {

            Nation nation = (Nation) value;
            setText(nation.getName());
            setIcon(library.getScaledImageIcon(library.getCoatOfArmsImageIcon(nation), 0.5f));
            return this;
        }
    }

    class AvailableCellRenderer extends JLabel implements TableCellRenderer {

        
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            setText(((NationState) value).getName());
            return this;
        }
    }

    class NationStateRenderer extends JLabel implements ListCellRenderer {
        public Component getListCellRendererComponent(JList list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            setText(((NationState) value).getName());
            return this;
        }
    }

    public final class AvailableCellEditor extends AbstractCellEditor implements TableCellEditor {

        private JComboBox aiStateBox = new JComboBox(aiStates);
        private JComboBox allStateBox = new JComboBox(allStates);
        private JComboBox activeBox;

        private ActionListener listener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    stopCellEditing();
                }
            };

        public AvailableCellEditor() {
            aiStateBox.setRenderer(new NationStateRenderer());
            aiStateBox.addActionListener(listener);
            allStateBox.setRenderer(new NationStateRenderer());
            allStateBox.addActionListener(listener);
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
                                                     int row, int column) {
            NationType nationType = ((Nation) getValueAt(row, NATION_COLUMN)).getType();
            if (nationType instanceof EuropeanNationType) {
                activeBox = allStateBox;
            } else {
                activeBox = aiStateBox;
            }
            return activeBox;
        }

        public Object getCellEditorValue() {
            return activeBox.getSelectedItem();
        }
    }

    class PlayerCellRenderer implements TableCellRenderer {

        JLabel label = new JLabel();
        JButton button = new JButton(Messages.message("select"));

        public PlayerCellRenderer() {
            label.setHorizontalAlignment(JLabel.CENTER);
            button.setBorder(BorderFactory
                             .createCompoundBorder(BorderFactory
                                                   .createEmptyBorder(5, 10, 5, 10),
                                                   button.getBorder()));
        }

        
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {

            Player player = (Player) value;
            if (player == null) {
                NationType nationType = (NationType) table.getValueAt(row, ADVANTAGE_COLUMN);
                if (nationType instanceof EuropeanNationType) {
                    NationState nationState = (NationState) table
                        .getValueAt(row, AVAILABILITY_COLUMN);
                    if (nationState == NationState.AVAILABLE) {
                        return button;
                    }
                }
                Nation nation = (Nation) table.getValueAt(row, NATION_COLUMN);
                label.setText(nation.getRulerName());
            } else {
                label.setText(player.getName());
            }
            return label;
        }
    }

    public final class PlayerCellEditor extends AbstractCellEditor implements TableCellEditor {

        private JButton button = new JButton(Messages.message("select"));

        public PlayerCellEditor() {
            button.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        fireEditingStopped();
                    }
                });
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
                                                     int row, int column) {
            return button;
        }

        public Object getCellEditorValue() {
            return true;
        }

    }


    
    private class PlayersTableModel extends AbstractTableModel {

        private List<Nation> nations;

        private Map<Nation, Player> players;

        private Player thisPlayer;

        private final PreGameController preGameController;

        private NationOptions nationOptions;

        
        public PlayersTableModel(PreGameController pgc, NationOptions nationOptions, Player owningPlayer) {
            nations = new ArrayList<Nation>();
            players = new HashMap<Nation, Player>();
            for (Nation nation : Specification.getSpecification().getNations()) {
                NationState state = nationOptions.getNations().get(nation);
                if (state != null) {
                    nations.add(nation);
                    players.put(nation, null);
                }
            }
            thisPlayer = owningPlayer;
            players.put(thisPlayer.getNation(), thisPlayer);
            preGameController = pgc;
            this.nationOptions = nationOptions;
        }

        public void update() {
            for (Nation nation : nations) {
                players.put(nation, null);
            }
            for (Player player : thisPlayer.getGame().getPlayers()) {
                players.put(player.getNation(), player);
            }
            fireTableDataChanged();
        }

        
        public Class<?> getColumnClass(int column) {
            switch(column) {
            case NATION_COLUMN:
                return Nation.class;
            case AVAILABILITY_COLUMN:
                return NationOptions.NationState.class;
            case ADVANTAGE_COLUMN:
                return NationType.class;
            case COLOR_COLUMN:
                return Color.class;
            case PLAYER_COLUMN:
                return Player.class;
            }
            return String.class;
        }

        
        public int getColumnCount() {
            return columnNames.length;
        }

        
        public String getColumnName(int column) {
            return columnNames[column];
        }

        
        public int getRowCount() {
            return nations.size();
        }

        
        public Object getValueAt(int row, int column) {
            if ((row < getRowCount()) && (column < getColumnCount()) && (row >= 0) && (column >= 0)) {
                Nation nation = nations.get(row);
                switch (column) {
                case NATION_COLUMN:
                    return nation;
                case AVAILABILITY_COLUMN:
                    return nationOptions.getNationState(nation);
                case ADVANTAGE_COLUMN:
                    if (players.get(nation) == null) {
                        return nation.getType();
                    } else {
                        return players.get(nation).getNationType();
                    }
                case COLOR_COLUMN:
                    if (players.get(nation) == null) {
                        return nation.getColor();
                    } else {
                        return players.get(nation).getColor();
                    }
                case PLAYER_COLUMN:
                    return players.get(nation);
                }
            }
            return null;
        }

        
        public boolean isCellEditable(int row, int column) {
            if ((row >= 0) && (row < nations.size())) {
                Nation nation = nations.get(row);
                boolean ownRow = (thisPlayer == players.get(nation) && !thisPlayer.isReady());
                switch(column) {
                case AVAILABILITY_COLUMN:
                    return (!ownRow && thisPlayer.isAdmin());
                case ADVANTAGE_COLUMN:
                case COLOR_COLUMN:
                    return (nation.getType() instanceof EuropeanNationType && ownRow);
                case PLAYER_COLUMN:
                    return (nation.getType() instanceof EuropeanNationType && players.get(nation) == null);
                }
            }
            return false;
        }

        
        public void setValueAt(Object value, int row, int column) {
            if ((row < getRowCount()) && (column < getColumnCount()) && (row >= 0) && (column >= 0)) {
                

                switch(column) {
                case ADVANTAGE_COLUMN:
                    preGameController.setNationType((NationType) value);
                    break;
                case AVAILABILITY_COLUMN:
                    preGameController.setAvailable(nations.get(row), (NationState) value);
                    update();
                    break;
                case COLOR_COLUMN:
                    preGameController.setColor((Color) value);
                    break;
                case PLAYER_COLUMN:
                    Nation nation = nations.get(row);
                    if (nationOptions.getNationState(nation) == NationState.AVAILABLE) {
                        preGameController.setNation(nation);
                        preGameController.setColor(nation.getColor());
                        preGameController.setNationType(nation.getType());
                        update();
                    }
                    break;
                }

                fireTableCellUpdated(row, column);
            }
        }
    }
}