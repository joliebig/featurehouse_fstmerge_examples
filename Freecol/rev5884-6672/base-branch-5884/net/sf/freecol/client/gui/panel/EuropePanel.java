

package net.sf.freecol.client.gui.panel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
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

        
        exitButton = new JButton(Messages.message("close"));
        exitButton.setActionCommand(EuropeAction.EXIT.toString());
        exitButton.addActionListener(this);
        InputMap closeInputMap = new ComponentInputMap(exitButton);
        closeInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false), "pressed");
        closeInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, true), "released");
        SwingUtilities.replaceUIInputMap(exitButton, JComponent.WHEN_IN_FOCUSED_WINDOW, closeInputMap);
        enterPressesWhenFocused(exitButton);

        
        JButton trainButton = new JButton(Messages.message("train"));
        trainButton.setActionCommand(EuropeAction.TRAIN.toString());
        trainButton.addActionListener(this);
        InputMap trainInputMap = new ComponentInputMap(trainButton);
        trainInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_T, 0, false), "pressed");
        trainInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_T, 0, true), "released");
        SwingUtilities.replaceUIInputMap(trainButton, JComponent.WHEN_IN_FOCUSED_WINDOW, trainInputMap);
        enterPressesWhenFocused(trainButton);

        
        JButton purchaseButton = new JButton(Messages.message("purchase"));
        purchaseButton.setActionCommand(EuropeAction.PURCHASE.toString());
        purchaseButton.addActionListener(this);
        InputMap purchaseInputMap = new ComponentInputMap(purchaseButton);
        purchaseInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_P, 0, false), "pressed");
        purchaseInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_P, 0, true), "released");
        SwingUtilities.replaceUIInputMap(purchaseButton, JComponent.WHEN_IN_FOCUSED_WINDOW, purchaseInputMap);
        enterPressesWhenFocused(purchaseButton);

        
        JButton recruitButton = new JButton(Messages.message("recruit"));
        recruitButton.setActionCommand(EuropeAction.RECRUIT.toString());
        recruitButton.addActionListener(this);
        InputMap recruitInputMap = new ComponentInputMap(recruitButton);
        recruitInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_R, 0, false), "pressed");
        recruitInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_R, 0, true), "released");
        SwingUtilities.replaceUIInputMap(recruitButton, JComponent.WHEN_IN_FOCUSED_WINDOW, recruitInputMap);
        enterPressesWhenFocused(recruitButton);

        
        JButton unloadButton = new JButton(Messages.message("unload"));
        unloadButton.setActionCommand(EuropeAction.UNLOAD.toString());
        unloadButton.addActionListener(this);
        InputMap unloadInputMap = new ComponentInputMap(unloadButton);
        unloadInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_U, 0, false), "pressed");
        unloadInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_U, 0, true), "released");
        SwingUtilities.replaceUIInputMap(unloadButton, JComponent.WHEN_IN_FOCUSED_WINDOW, unloadInputMap);
        enterPressesWhenFocused(unloadButton);

        
        JButton sailButton = new JButton(Messages.message("sail"));
        sailButton.setActionCommand(EuropeAction.SAIL.toString());
        sailButton.addActionListener(this);
        InputMap sailInputMap = new ComponentInputMap(sailButton);
        sailInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, false), "pressed");
        sailInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, true), "released");
        SwingUtilities.replaceUIInputMap(sailButton, JComponent.WHEN_IN_FOCUSED_WINDOW, sailInputMap);
        enterPressesWhenFocused(sailButton);

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
        StyleConstants.setForeground(attributes, Color.WHITE);
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

        toAmericaPanel.setLayout(new GridLayout(0, 4));
        toEuropePanel.setLayout(new GridLayout(0, 4));
        inPortPanel.setLayout(new GridLayout(0, 4));
        docksPanel.setLayout(new GridLayout(0, 5));
        cargoPanel.setLayout(new GridLayout(1, 0));

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

        setLayout(new MigLayout("wrap 3, fill, align center, insets 30",
                                "[fill][fill][fill, grow 0]", 
                                "[align top][align top][align top]"));
        add(toAmericaScroll, "width 315:, height 150:, grow");
        add(toEuropeScroll, "width 315:, height 150:, grow");
        add(recruitButton, "split 4, flowy");
        add(purchaseButton);
        add(trainButton);
        add(unloadButton);
        add(inPortScroll, "split 2, flowy, grow, width 315:");
        add(cargoScroll, "grow, width 315:");
        add(docksScroll, "grow, width 315:, height 150:");
        add(sailButton);
        add(marketScroll, "span 2, growx");
        add(exitButton);
        add(logScroll, "span, height 40:120");

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
        cargoPanel.removeAll();
        marketPanel.removeAll();
        docksPanel.initialize();
        log.setText("");

        
        
        

        UnitLabel lastCarrier = null;
        for (Unit unit : europe.getUnitList()) {
            UnitLabel unitLabel = new UnitLabel(unit, getCanvas());
            unitLabel.setTransferHandler(defaultTransferHandler);
            unitLabel.addMouseListener(pressListener);

            if (!unit.isNaval()) {
                
                
            } else {
                
                
                switch (unit.getState()) {
                case ACTIVE:
                default:
                    lastCarrier = unitLabel;
                    
                    break;
                case TO_EUROPE:
                    toEuropePanel.add(unitLabel, false);
                    break;
                case TO_AMERICA:
                    toAmericaPanel.add(unitLabel, false);
                    break;
                }
            }
        }
        
        setSelectedUnitLabel(lastCarrier);

        if (lastCarrier != null) {
            cargoPanel.setCarrier(lastCarrier.getUnit());
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

        String newLandName = getMyPlayer().getNewLandName();
        ((TitledBorder) toAmericaPanel.getBorder()).setTitle(Messages.message("sailingTo", 
                "%location%", newLandName));
    }

    
    public void setSelectedUnit(Unit unit) {
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
                    getController().moveToAmerica(unit);
                    UnitLabel unitLabel = getSelectedUnitLabel();
                    inPortPanel.remove(unitLabel);
                    toAmericaPanel.add(unitLabel, false);
                    revalidate();
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
                        boolean leaveColonists = getCanvas().showConfirmDialog(
                                "europe.leaveColonists",
                                "yes",
                                "no",
                                "%newWorld%", unit.getOwner().getNewLandName());
                        if (!leaveColonists) {
                            
                            return null;
                        }
                    }
                    comp.getParent().remove(comp);

                    getController().moveToAmerica(unit);
                    docksPanel.removeAll();
                    for (Unit u : europe.getUnitList()) {
                        UnitLabel unitLabel = new UnitLabel(u, getCanvas());
                        unitLabel.setTransferHandler(defaultTransferHandler);
                        unitLabel.addMouseListener(pressListener);

                        if (!u.isNaval()) {
                            
                            docksPanel.add(unitLabel, false);
                        }
                    }
                    docksPanel.revalidate();
                } else {
                    logger.warning("An invalid component got dropped on this ToAmericaPanel.");
                    return null;
                }
            }
            setSelectedUnitLabel(null);
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
            setSelectedUnitLabel(null);
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
            for (Unit unit : units) {
                if ((unit.getState() == UnitState.ACTIVE || unit.getState() == UnitState.SENTRY)
                        && (unit.isNaval())) {
                    UnitLabel unitLabel = new UnitLabel(unit, getCanvas());
                    unitLabel.setTransferHandler(defaultTransferHandler);
                    unitLabel.addMouseListener(pressListener);
                    add(unitLabel);
                }
            }

            if (!units.isEmpty()) {
                setSelectedUnit(units.get(units.size() - 1));
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
                            getController().unloadCargo(goods);
                            break;
                        case CANCEL:
                        default:
                        }
                    }
                    europePanel.getCargoPanel().revalidate();
                    revalidate();
                    getController().nextModelMessage();
                    europePanel.refresh();

                    
                    UnitLabel t = selectedUnit;
                    selectedUnit = null;
                    setSelectedUnitLabel(t);

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
            String text = Messages.message("transaction.purchase",
                    "%goods%", goodsType.getName(),
                    "%amount%", String.valueOf(amount),
                    "%gold%", String.valueOf(price))
                + "\n" + Messages.message("transaction.price",
                    "%gold%", String.valueOf(total));
            add(text);
        }

        public void logSale(GoodsType goodsType, int amount, int price, int tax) {
            int totalBeforeTax = amount * price;
            int totalTax = totalBeforeTax * tax / 100;
            int totalAfterTax = totalBeforeTax - totalTax;
            
            String text = Messages.message("transaction.sale",
                    "%goods%", goodsType.getName(),
                    "%amount%", String.valueOf(amount),
                    "%gold%", String.valueOf(price))
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
}
