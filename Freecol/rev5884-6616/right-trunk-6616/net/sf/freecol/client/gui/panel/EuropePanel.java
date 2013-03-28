

package net.sf.freecol.client.gui.panel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ComponentInputMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import net.sf.freecol.FreeCol;
import net.sf.freecol.client.ClientOptions;
import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.model.Europe;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.Goods;
import net.sf.freecol.common.model.GoodsType;
import net.sf.freecol.common.model.StringTemplate;
import net.sf.freecol.common.model.TransactionListener;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.Unit.UnitState;
import net.sf.freecol.common.resources.ResourceManager;

import net.miginfocom.swing.MigLayout;



public final class EuropePanel extends FreeColPanel {

    private static Logger logger = Logger.getLogger(EuropePanel.class.getName());

    public static enum EuropeAction { EXIT, RECRUIT, PURCHASE, TRAIN, UNLOAD, SAIL }

    private final ToAmericaPanel toAmericaPanel;

    private final ToEuropePanel toEuropePanel;

    private final InPortPanel inPortPanel;

    private final DocksPanel docksPanel;

    private final EuropeCargoPanel cargoPanel;

    private final MarketPanel marketPanel;
    
    private final TransactionLog log;

    private final DefaultTransferHandler defaultTransferHandler;

    private final MouseListener pressListener;

    private Europe europe;

    private UnitLabel selectedUnit;

    private JButton exitButton;


    
    public EuropePanel(Canvas parent) {
        super(parent);

        setFocusCycleRoot(true);

        
        exitButton = new EuropeButton(Messages.message("close"), KeyEvent.VK_ESCAPE,
                                      EuropeAction.EXIT.toString(), this);
        EuropeButton trainButton = new EuropeButton(Messages.message("train"), KeyEvent.VK_T,
                                                    EuropeAction.TRAIN.toString(), this);
        EuropeButton purchaseButton = new EuropeButton(Messages.message("purchase"), KeyEvent.VK_P,
                                                       EuropeAction.PURCHASE.toString(), this);
        EuropeButton recruitButton = new EuropeButton(Messages.message("recruit"), KeyEvent.VK_R,
                                                      EuropeAction.RECRUIT.toString(), this);
        EuropeButton unloadButton = new EuropeButton(Messages.message("unload"), KeyEvent.VK_U,
                                                     EuropeAction.UNLOAD.toString(), this);
        EuropeButton sailButton = new EuropeButton(Messages.message("sail"), KeyEvent.VK_S,
                                                   EuropeAction.SAIL.toString(), this);

        toAmericaPanel = new ToAmericaPanel(this);
        toEuropePanel = new ToEuropePanel(this);
        inPortPanel = new InPortPanel();
        docksPanel = new DocksPanel(this);
        cargoPanel = new EuropeCargoPanel(parent);
        cargoPanel.setParentPanel(this);
        marketPanel = new MarketPanel(this);
        
        log = new TransactionLog();
        SimpleAttributeSet attributes = new SimpleAttributeSet();
        StyleConstants.setAlignment(attributes, StyleConstants.ALIGN_RIGHT);
        
        StyleConstants.setBold(attributes, true);
        log.setParagraphAttributes(attributes, true);

        defaultTransferHandler = new DefaultTransferHandler(parent, this);
        toAmericaPanel.setTransferHandler(defaultTransferHandler);
        toEuropePanel.setTransferHandler(defaultTransferHandler);
        inPortPanel.setTransferHandler(defaultTransferHandler);
        docksPanel.setTransferHandler(defaultTransferHandler);
        cargoPanel.setTransferHandler(defaultTransferHandler);
        marketPanel.setTransferHandler(defaultTransferHandler);

        pressListener = new DragListener(this);
        MouseListener releaseListener = new DropListener();
        toAmericaPanel.addMouseListener(releaseListener);
        toEuropePanel.addMouseListener(releaseListener);
        inPortPanel.addMouseListener(releaseListener);
        docksPanel.addMouseListener(releaseListener);
        marketPanel.addMouseListener(releaseListener);
        cargoPanel.addMouseListener(releaseListener);

        toAmericaPanel.setLayout(new GridLayout(1, 0));
        toEuropePanel.setLayout(new GridLayout(1, 0));
        inPortPanel.setLayout(new GridLayout(0, 4));
        docksPanel.setLayout(new GridLayout(0, 5));
        cargoPanel.setLayout(new GridLayout(0, 4));

        JScrollPane toAmericaScroll = new JScrollPane(toAmericaPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        toAmericaScroll.getVerticalScrollBar().setUnitIncrement( 16 );
        JScrollPane toEuropeScroll = new JScrollPane(toEuropePanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        toEuropeScroll.getVerticalScrollBar().setUnitIncrement( 16 );
        JScrollPane inPortScroll = new JScrollPane(inPortPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        inPortScroll.getVerticalScrollBar().setUnitIncrement( 16 );
        JScrollPane docksScroll = new JScrollPane(docksPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        docksScroll.getVerticalScrollBar().setUnitIncrement( 16 );
        JScrollPane cargoScroll = new JScrollPane(cargoPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        JScrollPane marketScroll = new JScrollPane(marketPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        JScrollPane logScroll = new JScrollPane(log, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        logScroll.getVerticalScrollBar().setUnitIncrement( 16 );

        toAmericaPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), Messages
                .message("goingToAmerica")));
        toEuropePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), Messages
                .message("goingToEurope")));
        docksPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), Messages
                .message("docks")));
        inPortPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), Messages
                .message("inPort")));
        logScroll.setBorder(BorderFactory.createEmptyBorder());

        
        JPanel whitePanel = new JPanel(new MigLayout("fill", "", "")) {
                public String getUIClassID() {
                    return "MarketPanelUI";
                }
            };
        whitePanel.setOpaque(false);
        whitePanel.setBorder(BorderFactory.createEtchedBorder(Color.WHITE, Color.BLACK));
        whitePanel.add(logScroll, "grow");

        marketScroll.getViewport().setOpaque(false);
        marketPanel.setOpaque(false);
        cargoScroll.getViewport().setOpaque(false);
        cargoPanel.setOpaque(false);
        toAmericaScroll.getViewport().setOpaque(false);
        toAmericaPanel.setOpaque(false);
        toEuropeScroll.getViewport().setOpaque(false);
        toEuropePanel.setOpaque(false);
        docksScroll.getViewport().setOpaque(false);
        docksPanel.setOpaque(false);
        inPortScroll.getViewport().setOpaque(false);
        inPortPanel.setOpaque(false);
        logScroll.getViewport().setOpaque(false);
        log.setOpaque(false);

        setLayout(new MigLayout("wrap 3, insets 30",
                                "push[fill, :380:480][fill, :380:480][fill, 150:200:]push", 
                                "push[fill, 124:][fill, 124:][fill, 124:][fill, 100:][fill, ::160][::40]push"));
        add(toAmericaScroll);
        add(docksScroll, "spany 4");
        add(whitePanel, "spany 4");
        add(toEuropeScroll);
        add(inPortScroll);
        add(cargoScroll);
        add(marketScroll, "span");

        add(recruitButton, "span, split 6");
        add(purchaseButton);
        add(trainButton);
        add(unloadButton);
        add(sailButton);
        add(exitButton, "tag ok");

        setBorder(null);

        selectedUnit = null;

        
        
        addMouseListener(new MouseAdapter() {
        });

        setSize(parent.getWidth(), parent.getHeight());

    }

    public void requestFocus() {
        exitButton.requestFocus();
    }

    
    public void refresh() {
        repaint(0, 0, getWidth(), getHeight());
    }

    
    public void paintComponent(Graphics g) {
        int width = getWidth();
        int height = getHeight();

        Image bgImage = ResourceManager.getImage("EuropeBackgroundImage", getCanvas().getSize());
        if (bgImage != null) {
            g.drawImage(bgImage, 0, 0, this);
        } else {
            Image tempImage = ResourceManager.getImage("BackgroundImage");

            if (tempImage != null) {
                for (int x = 0; x < width; x += tempImage.getWidth(null)) {
                    for (int y = 0; y < height; y += tempImage.getHeight(null)) {
                        g.drawImage(tempImage, x, y, null);
                    }
                }
            } else {
                g.setColor(getBackground());
                g.fillRect(0, 0, width, height);
            }
        }
    }

    
    public void initialize(Europe europe, Game game) {
        this.europe = europe;

        getMyPlayer().getMarket().addTransactionListener(log);
        
        
        
        
        toAmericaPanel.removeAll();
        toEuropePanel.removeAll();
        
        inPortPanel.initialize();
        marketPanel.removeAll();
        docksPanel.initialize();
        log.setText("");

        
        
        
        
        
        for (Unit unit : europe.getUnitList()) {
            UnitLabel unitLabel = new UnitLabel(unit, getCanvas());
            unitLabel.setTransferHandler(defaultTransferHandler);
            unitLabel.addMouseListener(pressListener);

            if (!unit.isNaval()) {
                
                continue;
            }
            
            
            
            switch (unit.getState()) {
            	case TO_EUROPE:
            		toEuropePanel.add(unitLabel, false);
            		break;
            	case TO_AMERICA:
            		toAmericaPanel.add(unitLabel, false);
            		break;
            }       
        }

        List<GoodsType> goodsTypes = FreeCol.getSpecification().getGoodsTypeList();
        for (GoodsType goodsType : goodsTypes) {
            if (goodsType.isStorable()) {
                MarketLabel marketLabel = new MarketLabel(goodsType, getMyPlayer().getMarket(), getCanvas());
                marketLabel.setTransferHandler(defaultTransferHandler);
                marketLabel.addMouseListener(pressListener);
                marketPanel.add(marketLabel);
            }
        }

        String newLandName = Messages.getNewLandName(getMyPlayer());
        ((TitledBorder) toAmericaPanel.getBorder()).setTitle(Messages.message("sailingTo", 
                "%location%", newLandName));
    }

    
    public void setSelectedUnit(Unit unit) {
    	
    	if(unit == null){
    		setSelectedUnitLabel(null);
    		return;
    	}
        Component[] components = inPortPanel.getComponents();
        for (int i = 0; i < components.length; i++) {
            if (components[i] instanceof UnitLabel && ((UnitLabel) components[i]).getUnit() == unit) {
                setSelectedUnitLabel((UnitLabel) components[i]);
                break;
            }
        }
    }

    
    public void setSelectedUnitLabel(UnitLabel unitLabel) {
        if (selectedUnit == unitLabel) {
            return;
        }
        if (selectedUnit != null) {
            selectedUnit.setSelected(false);
        }
        selectedUnit = unitLabel;
        if (unitLabel == null) {
            cargoPanel.setCarrier(null);
        } else {
            cargoPanel.setCarrier(unitLabel.getUnit());
            unitLabel.setSelected(true);
        }
        
        inPortPanel.revalidate();
        inPortPanel.repaint();
    }

    
    public Unit getSelectedUnit() {
        if (selectedUnit == null) {
            return null;
        } else {
            return selectedUnit.getUnit();
        }
    }

    
    public UnitLabel getSelectedUnitLabel() {
        return selectedUnit;
    }

    
    private void unload() {
        Unit unit = getSelectedUnit();
        if (unit != null && unit.isCarrier()) {
            Iterator<Goods> goodsIterator = unit.getGoodsIterator();
            while (goodsIterator.hasNext()) {
                Goods goods = goodsIterator.next();
                if (getMyPlayer().canTrade(goods)) {
                    getController().sellGoods(goods);
                } else {
                    getController().payArrears(goods);
                }
            }
            Iterator<Unit> unitIterator = unit.getUnitIterator();
            while (unitIterator.hasNext()) {
                Unit newUnit = unitIterator.next();
                getController().leaveShip(newUnit);
            }
            cargoPanel.initialize(); 
            docksPanel.update();
        }
    }

    
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        try {
            
            EuropeAction europeAction = Enum.valueOf(EuropeAction.class, command);
            
            getCanvas().showEuropeDialog(europeAction);
            
            switch (europeAction) {
            case EXIT:
                getMyPlayer().getMarket().removeTransactionListener(log);
                europe.removePropertyChangeListener(docksPanel);
                europe.removePropertyChangeListener(inPortPanel);
                getCanvas().remove(this);
                getController().nextModelMessage();
                break;
            case RECRUIT:
            case PURCHASE:
            case TRAIN:
                
                requestFocus();
                break;
            case UNLOAD:
                unload();
                requestFocus();
                break;
            case SAIL:
                Unit unit = getSelectedUnit();
                if (unit != null && unit.isNaval()) {
                    UnitLabel unitLabel = getSelectedUnitLabel();
                    toAmericaPanel.add(unitLabel, true);
                }
                requestFocus();
                break;
            default:
                logger.warning("Invalid action command");
            }
        } catch (NumberFormatException e) {
            logger.warning("Invalid action number");
        }
    }

    
    public void payArrears(GoodsType goodsType) {
        if (getMyPlayer().getArrears(goodsType) > 0) {
            getController().payArrears(goodsType);
            getMarketPanel().revalidate();
            refresh();
        }
    }

    public final class EuropeCargoPanel extends CargoPanel {

        public EuropeCargoPanel(Canvas canvas) {
            super(canvas, true);
        }
        
        @Override
        public String getUIClassID() {
            return "EuropeCargoPanelUI";
        }
    }

    
    public final class ToAmericaPanel extends JPanel {
        private final EuropePanel europePanel;


        
        public ToAmericaPanel(EuropePanel europePanel) {
            this.europePanel = europePanel;
        }

        
        public Component add(Component comp, boolean editState) {
            if (editState) {
                if (comp instanceof UnitLabel) {
                    final Unit unit = ((UnitLabel) comp).getUnit();
                    final ClientOptions co = getClient().getClientOptions();
                    boolean autoload = co.getBoolean(ClientOptions.AUTOLOAD_EMIGRANTS);
                    if (!autoload
                            && docksPanel.getComponentCount() > 0
                            && unit.getSpaceLeft() > 0) {
                        boolean leave = getCanvas()
                            .showConfirmDialog(null, "europe.leaveColonists",
                                               "yes", "no",
                                               "%newWorld%", Messages.getNewLandName(unit.getOwner()));
                        if (!leave) { 
                            return null;
                        }
                    }
                    comp.getParent().remove(comp);

                    getController().moveToAmerica(unit);
                    
                    inPortPanel.update();
                    docksPanel.update();
                } else {
                    logger.warning("An invalid component got dropped on this ToAmericaPanel.");
                    return null;
                }
            }
            Component c = add(comp);
            toAmericaPanel.revalidate();
            europePanel.refresh();
            return c;
        }

        public String getUIClassID() {
            return "ToAmericaPanelUI";
        }
    }

    
    public final class ToEuropePanel extends JPanel {
        private final EuropePanel europePanel;


        
        public ToEuropePanel(EuropePanel europePanel) {
            this.europePanel = europePanel;
        }

        
        public Component add(Component comp, boolean editState) {
            if (editState) {
                if (comp instanceof UnitLabel) {
                    comp.getParent().remove(comp);
                    Unit unit = ((UnitLabel) comp).getUnit();
                    getController().moveToEurope(unit);
                } else {
                    logger.warning("An invalid component got dropped on this ToEuropePanel.");
                    return null;
                }
            }
            Component c = add(comp);
            europePanel.refresh();
            return c;
        }

        public String getUIClassID() {
            return "ToEuropePanelUI";
        }
    }

    
    public final class InPortPanel extends JPanel implements PropertyChangeListener {

        public void initialize() {
            europe.addPropertyChangeListener(this);
            update();
        }

        public void update() {
            removeAll();

            List<Unit> units = europe.getUnitList();
            UnitLabel lastCarrier = null;
            for (Unit unit : units) {
            	if(!unit.isNaval()){
            		continue;
            	}
                if ((unit.getState() == UnitState.ACTIVE || unit.getState() == UnitState.SENTRY)) {
                    UnitLabel unitLabel = new UnitLabel(unit, getCanvas());
                    unitLabel.setTransferHandler(defaultTransferHandler);
                    unitLabel.addMouseListener(pressListener);
                    add(unitLabel);
                    lastCarrier = unitLabel;
                    
                    
                    
                    if(selectedUnit != null && selectedUnit.getUnit() == unit){
                    	selectedUnit = unitLabel;
                    	selectedUnit.setSelected(true);
                    }
                }
            }
            
            if (selectedUnit == null && lastCarrier != null) {
                setSelectedUnitLabel(lastCarrier);
            }
            
            boolean isSelectedUnitBoundToAmerica = selectedUnit != null 
    				&& selectedUnit.getUnit().getState() == UnitState.TO_AMERICA;
            if(isSelectedUnitBoundToAmerica){
            	setSelectedUnitLabel(lastCarrier);
            }
            revalidate();
            repaint();
        }

        public void propertyChange(PropertyChangeEvent event) {
            update();
        }

        public String getUIClassID() {
            return "EuropeInPortPanelUI";
        }
    }

    
    public final class DocksPanel extends JPanel implements PropertyChangeListener {

        private final EuropePanel europePanel;


        
        public DocksPanel(EuropePanel europePanel) {
            this.europePanel = europePanel;
        }
        
        public void initialize() {
            europe.addPropertyChangeListener(this);
            update();
        }

        public Component add(Component comp, boolean editState) {
            Component c = add(comp);
            update();
            return c;
        }

        @Override
        public void remove(Component comp) {
            update();
        }

        public void update() {
            removeAll();

            List<Unit> units = europe.getUnitList();
            for (Unit unit : units) {
                if (!unit.isNaval()) {
                    UnitLabel unitLabel = new UnitLabel(unit, getCanvas());
                    unitLabel.setTransferHandler(defaultTransferHandler);
                    unitLabel.addMouseListener(pressListener);
                    add(unitLabel);
                }
            }

            revalidate();
            repaint();
        }

        public void propertyChange(PropertyChangeEvent event) {
            update();
        }

        public String getUIClassID() {
            return "DocksPanelUI";
        }
    }



    
    public final class MarketPanel extends JPanel {
        private final EuropePanel europePanel;


        
        public MarketPanel(EuropePanel europePanel) {
            this.europePanel = europePanel;
            setLayout(new GridLayout(2, 8));
        }

        
        public Component add(Component comp, boolean editState) {
            if (editState) {
                if (comp instanceof GoodsLabel) {
                    
                    Goods goods = ((GoodsLabel) comp).getGoods();
                    if (getMyPlayer().canTrade(goods)) {
                        getController().sellGoods(goods);
                    } else {
                        switch (getCanvas().showBoycottedGoodsDialog(goods, europe)) {
                        case PAY_ARREARS:
                            getController().payArrears(goods);
                            break;
                        case DUMP_CARGO:
                            getController().unloadCargo(goods, true);
                            break;
                        case CANCEL:
                        default:
                        }
                    }
                    europePanel.getCargoPanel().revalidate();
                    revalidate();
                    getController().nextModelMessage();
                    europePanel.refresh();

                    return comp;
                }

                logger.warning("An invalid component got dropped on this MarketPanel.");
                return null;
            }
            europePanel.refresh();
            return comp;
        }

        public void remove(Component comp) {
            
        }

        public String getUIClassID() {
            return "MarketPanelUI";
        }
    }
    
    
    public class TransactionLog extends JTextPane implements TransactionListener {
        public TransactionLog() {
            setEditable(false);
        }
        
        private void add(String text) {
            StyledDocument doc = getStyledDocument();
            try {
                if (doc.getLength() > 0) {
                    text = "\n\n" + text;
                }
                doc.insertString(doc.getLength(), text, null);
            } catch(Exception e) {
                logger.warning("Failed to update transaction log: " + e.toString());
            }
        }
        
        public void logPurchase(GoodsType goodsType, int amount, int price) {
            int total = amount * price;
            String text = Messages.message(StringTemplate.template("transaction.purchase")
                                           .add("%goods%", goodsType.getNameKey())
                                           .addAmount("%amount%", amount)
                                           .addAmount("%gold%", price))
                + "\n" + Messages.message("transaction.price",
                    "%gold%", String.valueOf(total));
            add(text);
        }

        public void logSale(GoodsType goodsType, int amount, int price, int tax) {
            int totalBeforeTax = amount * price;
            int totalTax = totalBeforeTax * tax / 100;
            int totalAfterTax = totalBeforeTax - totalTax;
            
            String text = Messages.message(StringTemplate.template("transaction.sale")
                                           .add("%goods%", goodsType.getNameKey())
                                           .addAmount("%amount%", amount)
                                           .addAmount("%gold%", price))
                + "\n" + Messages.message("transaction.price",
                    "%gold%", String.valueOf(totalBeforeTax))
                + "\n" + Messages.message("transaction.tax",
                    "%tax%", String.valueOf(tax),
                    "%gold%", String.valueOf(totalTax))
                + "\n" + Messages.message("transaction.net",
                    "%gold%", String.valueOf(totalAfterTax));
            add(text);
        }
    }


    
    public final CargoPanel getCargoPanel() {
        return cargoPanel;
    }

    
    public final MarketPanel getMarketPanel() {
        return marketPanel;
    }

    public class EuropeButton extends JButton {

        public EuropeButton(String text, int keyEvent, String command, ActionListener listener) {
            setOpaque(true);
            setText(text);
            setActionCommand(command);
            addActionListener(listener);
            InputMap closeInputMap = new ComponentInputMap(this);
            closeInputMap.put(KeyStroke.getKeyStroke(keyEvent, 0, false), "pressed");
            closeInputMap.put(KeyStroke.getKeyStroke(keyEvent, 0, true), "released");
            SwingUtilities.replaceUIInputMap(this, JComponent.WHEN_IN_FOCUSED_WINDOW, closeInputMap);
            enterPressesWhenFocused(this);
        }
        
    }

}
