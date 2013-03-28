

package net.sf.freecol.client.gui.panel;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sf.freecol.FreeCol;
import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.GoodsType;
import net.sf.freecol.common.model.Location;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Settlement;
import net.sf.freecol.common.model.TradeRoute;
import net.sf.freecol.common.model.TradeRoute.Stop;

import net.miginfocom.swing.MigLayout;



public final class TradeRouteInputDialog extends FreeColDialog<Boolean> implements ActionListener {

    private static final Logger logger = Logger.getLogger(TradeRouteInputDialog.class.getName());

    private static final int OK = 0, CANCEL = 1;

    private TradeRoute originalRoute;

    private final JButton ok = new JButton(Messages.message("ok"));

    private final JButton cancel = new JButton(Messages.message("cancel"));

    private final JButton addStopButton = new JButton(Messages.message("traderouteDialog.addStop"));

    private final JButton removeStopButton = new JButton(Messages.message("traderouteDialog.removeStop"));

    private final CargoHandler cargoHandler = new CargoHandler();

    private final MouseListener dragListener = new DragListener(this);

    private final MouseListener dropListener = new DropListener();

    private final GoodsPanel goodsPanel;

    private final CargoPanel cargoPanel;

    private final JComboBox destinationSelector = new JComboBox();

    private final JTextField tradeRouteName = new JTextField(Messages.message("traderouteDialog.newRoute"));

    private final DefaultListModel listModel = new DefaultListModel();

    private final JList stopList = new JList(listModel);

    private final JScrollPane tradeRouteView = new JScrollPane(stopList);

    private final JLabel nameLabel = new JLabel(Messages.message("traderouteDialog.nameLabel"));

    private final JLabel destinationLabel = new JLabel(Messages.message("traderouteDialog.destinationLabel"));


    
    public TradeRouteInputDialog(final Canvas parent, TradeRoute newRoute) {
        super(parent);

        originalRoute = newRoute;

        ok.setActionCommand(String.valueOf(OK));
        ok.addActionListener(this);
        enterPressesWhenFocused(ok);

        cancel.setActionCommand(String.valueOf(CANCEL));
        cancel.addActionListener(this);
        enterPressesWhenFocused(cancel);
        setCancelComponent(cancel);

        goodsPanel = new GoodsPanel();
        goodsPanel.setTransferHandler(cargoHandler);
        cargoPanel = new CargoPanel();
        cargoPanel.setTransferHandler(cargoHandler);

        stopList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                updateButtons();
            }
        });

        
        addStopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int startIndex = -1;
                int endIndex = -1;
                if (destinationSelector.getSelectedIndex() == 0 ) {   
                    startIndex = 1;
                    endIndex = destinationSelector.getItemCount() - 1;  
                } else {
                    startIndex = destinationSelector.getSelectedIndex();  
                    endIndex = startIndex;
                }
                for (int i = startIndex; i <= endIndex; i++) {
                    Stop stop = originalRoute.new Stop((Location) destinationSelector.getItemAt(i) );
                    for (Component comp : cargoPanel.getComponents()) {
                        CargoLabel label = (CargoLabel) comp;
                        stop.addCargo(label.getType());
                    }
                    if (stopList.getSelectedIndex() == -1) {
                        listModel.addElement(stop);
                    } else {
                        listModel.add(stopList.getSelectedIndex() + 1, stop);
                    }
                }
            }
        });

        
        removeStopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                listModel.removeElement(stopList.getSelectedValue());
            }
        });

        
        stopList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        stopList.setDragEnabled(true);
        stopList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                cargoPanel.initialize((Stop) stopList.getSelectedValue());
            }
        });

        setLayout(new MigLayout("wrap 3", "[][fill][fill]", ""));

        add(getDefaultHeader(Messages.message("traderouteDialog.editRoute")),
            "span, align center");
        add(tradeRouteView, "span 1 5, grow");
        add(nameLabel);
        add(tradeRouteName);        
        add(destinationLabel);
        add(destinationSelector);
        add(goodsPanel, "span");
        add(cargoPanel, "span, height 80:");
        add(addStopButton);
        add(removeStopButton);
        add(ok, "newline 20, span, split 2, tag ok");
        add(cancel, "tag cancel");

        TradeRoute tradeRoute = newRoute.clone();

        Player player = getMyPlayer();

        
        destinationSelector.addItem(Messages.message("report.allColonies", "%number%", ""));
        if (player.getEurope() != null) {
            destinationSelector.addItem(player.getEurope());
        }
        List<Settlement> settlements = player.getSettlements();
        final Comparator<Colony> comparator = getClient().getClientOptions().getColonyComparator();
        Collections.sort(settlements, new Comparator<Settlement>() {
                public int compare(final Settlement s1, final Settlement s2) {
                    return comparator.compare((Colony) s1, (Colony) s2);
                }
            });
        for (Settlement settlement : settlements) {
            destinationSelector.addItem(settlement);
        }

        
        for (Stop stop : tradeRoute.getStops()) {
            listModel.addElement(stop);
        }

        
        if (listModel.getSize() > 0) {
            stopList.setSelectedIndex(0);
            Stop selectedStop = (Stop) listModel.firstElement();
            cargoPanel.initialize(selectedStop);
        }

        
        updateButtons();

        
        tradeRouteName.setText(tradeRoute.getName());

        setSize(getPreferredSize());

    }

    
    public void updateButtons() {
        if (stopList.getSelectedIndex() == -1) {
            removeStopButton.setEnabled(false);
        } else {
            removeStopButton.setEnabled(true);
        }
    }

    public void requestFocus() {
        ok.requestFocus();
    }
    
    private boolean verifyNewTradeRoute(){
    	
    	if(listModel.getSize() < 2){
    		 getCanvas().errorMessage("traderouteDialog.notEnoughStops");
    		 return false;
    	}
    	
    	Player player = getCanvas().getClient().getMyPlayer();
        for (int index = 0; index < listModel.getSize(); index++) {
            Stop stop = (Stop) listModel.getElementAt(index);
            if(!TradeRoute.isStopValid(player, stop)){
            	return false;
            }
        }
        
    	return true;
    }

    
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        try {
            switch (Integer.valueOf(command).intValue()) {
            case OK:
            	
            	if(! verifyNewTradeRoute())
            		break;
            	
                getCanvas().remove(this);
                originalRoute.setName(tradeRouteName.getText());
                ArrayList<Stop> stops = new ArrayList<Stop>();
                for (int index = 0; index < listModel.getSize(); index++) {
                    stops.add((Stop) listModel.getElementAt(index));
                }
                originalRoute.setStops(stops);
                
                getController().updateTradeRoute(originalRoute);
                setResponse(new Boolean(true));
                break;
            case CANCEL:
                getCanvas().remove(this);
                setResponse(new Boolean(false));
                break;
            default:
                logger.warning("Invalid ActionCommand: invalid number.");
            }
        } catch (NumberFormatException e) {
            logger.warning("Invalid Actioncommand: not a number.");
        }
    }


    
    public class CargoLabel extends JLabel {
        private final GoodsType goodsType;


        public CargoLabel(GoodsType type) {
            super(getLibrary().getGoodsImageIcon(type));
            setTransferHandler(cargoHandler);
            addMouseListener(dragListener);
            this.goodsType = type;
        }

        public GoodsType getType() {
            return this.goodsType;
        }

    }

    
    public class GoodsPanel extends JPanel {

        public GoodsPanel() {
            super(new GridLayout(0, 5, margin, margin));
            for (GoodsType goodsType : FreeCol.getSpecification().getGoodsTypeList()) {
                if (goodsType.isStorable()) {
                    CargoLabel label = new CargoLabel(goodsType);
                    add(label);
                }
            }
            setOpaque(false);
            setBorder(BorderFactory.createTitledBorder(Messages.message("goods")));
            addMouseListener(dropListener);
        }

    }

    
    public class CargoPanel extends JPanel {

        

        public CargoPanel() {
            super();
            setOpaque(false);
            setBorder(BorderFactory.createTitledBorder(Messages.message("cargoOnCarrier")));
            addMouseListener(dropListener);
        }

        public void initialize(Stop newStop) {
            removeAll();
            if (newStop != null) {
                
                for (GoodsType goodsType : newStop.getCargo()) {
                    add(new CargoLabel(goodsType));
                }
            }
            revalidate();
            repaint();
        }
    }

    
    public class CargoHandler extends TransferHandler {

        protected Transferable createTransferable(JComponent c) {
            return new ImageSelection((CargoLabel) c);
        }

        public int getSourceActions(JComponent c) {
            return COPY_OR_MOVE;
        }

        public boolean importData(JComponent target, Transferable data) {
            if (canImport(target, data.getTransferDataFlavors())) {
                try {
                    CargoLabel label = (CargoLabel) data.getTransferData(DefaultTransferHandler.flavor);
                    if (target instanceof CargoPanel) {
                        CargoLabel newLabel = new CargoLabel(label.getType());
                        cargoPanel.add(newLabel);
                        cargoPanel.revalidate();
                        Stop stop = (Stop) stopList.getSelectedValue();
                        if (stop != null) {
                            stop.addCargo(label.getType());
                            stop.setModified(true);
                        }
                    }
                    return true;
                } catch (UnsupportedFlavorException ufe) {
                    logger.warning(ufe.toString());
                } catch (IOException ioe) {
                    logger.warning(ioe.toString());
                }
            }

            return false;
        }

        protected void exportDone(JComponent source, Transferable data, int action) {
            try {
                CargoLabel label = (CargoLabel) data.getTransferData(DefaultTransferHandler.flavor);
                if (source.getParent() instanceof CargoPanel) {
                    cargoPanel.remove(label);
                    Stop stop = (Stop) stopList.getSelectedValue();
                    if (stop != null) {
                        ArrayList<GoodsType> cargo = new ArrayList<GoodsType>(stop.getCargo());
                        for (int index = 0; index < cargo.size(); index++) {
                            if (cargo.get(index) == label.getType()) {
                                cargo.remove(index);
                                stop.setModified(true);
                                break;
                            }
                        }
                        stop.setCargo(cargo);
                    }
                    cargoPanel.revalidate();
                    cargoPanel.repaint();
                }
            } catch (UnsupportedFlavorException ufe) {
                logger.warning(ufe.toString());
            } catch (IOException ioe) {
                logger.warning(ioe.toString());
            }
        }

        public boolean canImport(JComponent c, DataFlavor[] flavors) {
            for (int i = 0; i < flavors.length; i++) {
                if (flavors[i].equals(DefaultTransferHandler.flavor)) {
                    return true;
                }
            }
            return false;
        }
    }

    
}
