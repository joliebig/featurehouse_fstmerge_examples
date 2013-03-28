


package net.sf.freecol.client.gui.panel;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Logger;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.TransferHandler;

import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.Specification;
import net.sf.freecol.common.model.AbstractGoods;
import net.sf.freecol.common.model.EquipmentType;
import net.sf.freecol.common.model.Goods;
import net.sf.freecol.common.model.GoodsType;
import net.sf.freecol.common.model.Modifier;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.Unit.UnitState;


public final class DefaultTransferHandler extends TransferHandler {

    private static Logger logger = Logger.getLogger(DefaultTransferHandler.class.getName());

    public static final DataFlavor flavor = new DataFlavor(ImageSelection.class, "ImageSelection");

    private final Canvas canvas;
    private final FreeColPanel parentPanel;

    
    public DefaultTransferHandler(Canvas canvas, FreeColPanel parentPanel) {
        this.canvas = canvas;
        this.parentPanel = parentPanel;
    }

    
    public int getSourceActions(JComponent comp) {
        return COPY_OR_MOVE;
    }


    
    public boolean canImport(JComponent comp, DataFlavor[] flavor) {
        if (!(comp instanceof UnitLabel) &&
            !(comp instanceof GoodsLabel) &&
            !(comp instanceof MarketLabel) &&
            !(comp instanceof JPanel) &&
            !(comp instanceof JLabel)) {
            return false;
        }
        for (int i = 0; i < flavor.length; i++) {
            if (flavor[i].equals(DefaultTransferHandler.flavor)) {
                return true;
            }
        }
        return false;
    }

    
    public Transferable createTransferable(JComponent comp) {
        if (comp instanceof UnitLabel) {
            return new ImageSelection((UnitLabel)comp);
        } else if (comp instanceof GoodsLabel) {
            return new ImageSelection((GoodsLabel)comp);
        } else if (comp instanceof MarketLabel) {
            return new ImageSelection((MarketLabel)comp);
        }
        return null;
    }

    
    public boolean importData(JComponent comp, Transferable t) {
        try {
            JLabel data;
            
            
            UnitLabel oldSelectedUnit = null;

            
            if (t.isDataFlavorSupported(DefaultTransferHandler.flavor)) {
                data = (JLabel)t.getTransferData(DefaultTransferHandler.flavor);
            } else {
                logger.warning("Data flavor is not supported!");
                return false;
            }

            
            if (comp == data) {
                return false;
            }

            
            if (comp instanceof UnitLabel) {
                UnitLabel unitLabel = (UnitLabel) comp;
                
                if (unitLabel.getUnit().isCarrier()
                    && unitLabel.getParent() instanceof EuropePanel.InPortPanel) {
                    if (data instanceof UnitLabel
                        && ((UnitLabel) data).getUnit().isOnCarrier()
                        || data instanceof GoodsLabel
                        && ((GoodsLabel) data).getGoods().getLocation() instanceof Unit) {
                        oldSelectedUnit = ((EuropePanel) parentPanel).getSelectedUnitLabel();
                    }
                    ((EuropePanel) parentPanel).setSelectedUnitLabel(unitLabel);
                    comp = ((EuropePanel) parentPanel).getCargoPanel();
                } else if (unitLabel.getUnit().isCarrier()
                           && unitLabel.getParent() instanceof ColonyPanel.InPortPanel) {
                    if (data instanceof UnitLabel
                        && ((UnitLabel) data).getUnit().isOnCarrier()
                        || data instanceof GoodsLabel
                        && ((GoodsLabel) data).getGoods().getLocation() instanceof Unit) {
                        oldSelectedUnit = ((ColonyPanel) parentPanel).getSelectedUnitLabel();
                    }
                    ((ColonyPanel) parentPanel).setSelectedUnitLabel(unitLabel);
                    comp = ((ColonyPanel) parentPanel).getCargoPanel();
                } else if (unitLabel.canUnitBeEquipedWith(data)) {
                    
                } else {
                    try {
                        comp = (JComponent)comp.getParent();
                    } catch (ClassCastException e) {
                        return false;
                    }

                    
                    
                    
                    try {
                        if ((JComponent)comp.getParent() instanceof ColonyPanel.BuildingsPanel.ASingleBuildingPanel) {
                            comp = (JComponent)comp.getParent();
                        }
                    } catch (ClassCastException e) {}
                }
            } else if ((comp instanceof GoodsLabel) || (comp instanceof MarketLabel)) {
                try {
                    comp = (JComponent)comp.getParent();
                } catch (ClassCastException e) {
                    return false;
                }
            }

            
            if (data.getParent() == comp) {
                return false;
            }

            if (data instanceof UnitLabel) {

                

                Unit unit = ((UnitLabel)data).getUnit();

                if (unit.isUnderRepair()) {
                    return false;
                }
                
                if ((unit.getState() == UnitState.TO_AMERICA) && (!(comp instanceof EuropePanel.ToEuropePanel))) {
                    return false;
                }

                if ((unit.getState() == UnitState.TO_EUROPE) && (!(comp instanceof EuropePanel.ToAmericaPanel))) {
                    return false;
                }

                if ((unit.getState() != UnitState.TO_AMERICA) && ((comp instanceof EuropePanel.ToEuropePanel))) {
                    return false;
                }

                if (!unit.isNaval() && (comp instanceof EuropePanel.InPortPanel
                        || comp instanceof ColonyPanel.InPortPanel
                        || comp instanceof EuropePanel.ToEuropePanel
                        || comp instanceof EuropePanel.ToAmericaPanel)) {
                    return false;
                }

                if (comp instanceof EuropePanel.MarketPanel || comp instanceof ColonyPanel.WarehousePanel) {
                    return false;
                }

                if (unit.isNaval() && (comp instanceof EuropePanel.DocksPanel
                        || comp instanceof ColonyPanel.OutsideColonyPanel
                        || comp instanceof ColonyPanel.BuildingsPanel.ASingleBuildingPanel
                        || comp instanceof ColonyPanel.TilePanel.ASingleTilePanel
                        || comp instanceof CargoPanel)) {
                    return false;
                }

                if (comp instanceof JLabel) {
                    logger.warning("Oops, I thought we didn't have to write this part.");
                    return true;
                } else if (comp instanceof JPanel) {
                    
                    

                    if (comp instanceof EuropePanel.ToEuropePanel) {
                        ((EuropePanel.ToEuropePanel)comp).add(data, true);
                    } else if (comp instanceof EuropePanel.ToAmericaPanel) {
                        ((EuropePanel.ToAmericaPanel)comp).add(data, true);
                    } else if (comp instanceof EuropePanel.DocksPanel) {
                        ((EuropePanel.DocksPanel)comp).add(data, true);
                    } else if (comp instanceof ColonyPanel.BuildingsPanel.ASingleBuildingPanel) {
                        ((ColonyPanel.BuildingsPanel.ASingleBuildingPanel) comp).add(data, true);
                    } else if (comp instanceof ColonyPanel.OutsideColonyPanel) {
                        ColonyPanel.OutsideColonyPanel outside = ((ColonyPanel.OutsideColonyPanel) comp);
                        if (outside.getColony().canReducePopulation()) {
                            outside.add(data, true);
                        } else {
                            String message = "";
                            Set<Modifier> modifierSet = outside.getColony().getFeatureContainer()
                                .getModifierSet("model.modifier.minimumColonySize");
                            for (Modifier modifier : modifierSet) {
                                message += Messages.message("colonyPanel.minimumColonySize",
                                                            "%object%", modifier.getSource().getName())
                                    + "\n";
                            }
                            canvas.showInformationMessage(message);
                        }
                    } else if (comp instanceof CargoPanel) {
                        ((CargoPanel)comp).add(data, true);
                    } else if (comp instanceof ColonyPanel.TilePanel.ASingleTilePanel) {
                        ((ColonyPanel.TilePanel.ASingleTilePanel)comp).add(data, true);
                    } else {
                        logger.warning("The receiving component is of an invalid type.");
                        return false;
                    }

                    comp.revalidate();

                    if (oldSelectedUnit != null) {
                        if ((oldSelectedUnit).getParent() instanceof EuropePanel.InPortPanel) {
                            ((EuropePanel) parentPanel).setSelectedUnit(oldSelectedUnit.getUnit());
                        } else {
                            ((ColonyPanel) parentPanel).setSelectedUnit(oldSelectedUnit.getUnit());
                        }
                    }

                    return true;
                }
            } else if (data instanceof GoodsLabel) {

                

                GoodsLabel label = ((GoodsLabel)data);

                

                if (label.isPartialChosen()) {
                    int amount = getAmount(label.getGoods().getType(), label.getGoods().getAmount(), false);
                    if (amount == -1) {
                        return false;
                    }
                    label.getGoods().setAmount(amount);
                } else if (label.getGoods().getAmount() > 100) {
                    label.getGoods().setAmount(100);
                }

                

                if (comp instanceof UnitLabel) {
                    UnitLabel unitLabel = ((UnitLabel) comp);
                    Unit unit = unitLabel.getUnit();
                    if (unit.hasAbility("model.ability.canBeEquipped")) {
                        Goods goods = label.getGoods();
                        for (EquipmentType equipment : Specification.getSpecification()
                                 .getEquipmentTypeList()) {
                            if (unit.canBeEquippedWith(equipment) && equipment.getGoodsRequired().size() == 1) {
                                AbstractGoods requiredGoods = equipment.getGoodsRequired().get(0);
                                if (requiredGoods.getType().equals(goods.getType())
                                    && requiredGoods.getAmount() <= goods.getAmount()) {
                                    int amount = Math.min(goods.getAmount() / requiredGoods.getAmount(),
                                                          equipment.getMaximumCount());
                                    unitLabel.getCanvas().getClient().getInGameController()
                                        .equipUnit(unit, equipment, amount);
                                    unitLabel.updateIcon();
                                    return true;
                                }
                            }
                        }
                    }
                    return false;
                } else if (comp instanceof JLabel) {
                    logger.warning("Oops, I thought we didn't have to write this part.");
                    return true;
                } else if (comp instanceof JPanel) {
                    

                    if (comp instanceof ColonyPanel.WarehousePanel) {
                        ((ColonyPanel.WarehousePanel)comp).add(data, true);
                    } else if (comp instanceof CargoPanel) {
                        ((CargoPanel)comp).add(data, true);
                    } else if (comp instanceof EuropePanel.MarketPanel) {
                        ((EuropePanel.MarketPanel)comp).add(data, true);
                    } else {
                        logger.warning("The receiving component is of an invalid type.");
                        return false;
                    }

                    comp.revalidate();

                    if (oldSelectedUnit != null) {
                        if (oldSelectedUnit.getParent() instanceof EuropePanel.InPortPanel) {
                            ((EuropePanel) parentPanel).setSelectedUnit(oldSelectedUnit.getUnit());
                        } else {
                            ((ColonyPanel) parentPanel).setSelectedUnit(oldSelectedUnit.getUnit());
                        }
                    }

                    return true;
                }
            } else if (data instanceof MarketLabel) {

                

                MarketLabel label = ((MarketLabel)data);

                

                if (label.isPartialChosen()) {
                    int amount = getAmount(label.getType(), label.getAmount(), true);
                    if (amount == -1) {
                        return false;
                    }
                    label.setAmount(amount);
                }


                if (comp instanceof UnitLabel) {
                    UnitLabel unitLabel = (UnitLabel) comp;
                    Unit unit = unitLabel.getUnit();
                    if (unit.hasAbility("model.ability.canBeEquipped")) {
                        GoodsType goodsType = label.getType();
                        for (EquipmentType equipment : Specification.getSpecification()
                                 .getEquipmentTypeList()) {
                            if (unit.canBeEquippedWith(equipment) && equipment.getGoodsRequired().size() == 1) {
                                AbstractGoods requiredGoods = equipment.getGoodsRequired().get(0);
                                if (requiredGoods.getType().equals(label.getType())
                                    && requiredGoods.getAmount() <= label.getAmount()) {
                                    int amount = Math.min(label.getAmount() / requiredGoods.getAmount(),
                                                          equipment.getMaximumCount());
                                    unitLabel.getCanvas().getClient().getInGameController()
                                        .equipUnit(unit, equipment, amount);
                                    unitLabel.updateIcon();
                                    return true;
                                }
                            }
                        }
                    }
                    return false;
                } else if (comp instanceof JLabel) {
                    logger.warning("Oops, I thought we didn't have to write this part.");
                    return true;
                } else if (comp instanceof JPanel) {
                    
                    

                    if (comp instanceof CargoPanel) {
                        ((CargoPanel)comp).add(data, true);
                    } else {
                        logger.warning("The receiving component is of an invalid type.");
                        return false;
                    }

                    comp.revalidate();
                    return true;
                }
            }

            logger.warning("The dragged component is of an invalid type.");

        } catch (Exception e) {
            
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.warning(sw.toString());
        }

        return false;
    }

    
    
    private int getAmount(GoodsType goodsType, int available, boolean needToPay) {
        return canvas.showFreeColDialog(new SelectAmountDialog(canvas, goodsType, available, needToPay));
    }




    


    private static FreeColDragGestureRecognizer recognizer = null;

    public void exportAsDrag(JComponent comp, InputEvent e, int action) {
        int srcActions = getSourceActions(comp);
        int dragAction = srcActions & action;
        if (!(e instanceof MouseEvent)) {
            dragAction = NONE;
        }

        if (dragAction != NONE && !GraphicsEnvironment.isHeadless()) {
            if (recognizer == null) {
                recognizer = new FreeColDragGestureRecognizer(new FreeColDragHandler());
            }

            recognizer.gestured(comp, (MouseEvent) e , srcActions, dragAction);
        } else {
            exportDone(comp, null, NONE);
        }
    }


    
    private static class FreeColDragHandler implements DragGestureListener, DragSourceListener {

        private boolean scrolls;


        

        
        public void dragGestureRecognized(DragGestureEvent dge) {
            JComponent c = (JComponent) dge.getComponent();
            DefaultTransferHandler th = (DefaultTransferHandler) c.getTransferHandler();
            Transferable t = th.createTransferable(c);

            if (t != null) {
                scrolls = c.getAutoscrolls();
                c.setAutoscrolls(false);
                try {
                    if (c instanceof JLabel && ((JLabel) c).getIcon() instanceof ImageIcon) {
                        Toolkit tk = Toolkit.getDefaultToolkit();
                        ImageIcon imageIcon = ((ImageIcon) ((JLabel) c).getIcon());
                        Dimension bestSize = tk.getBestCursorSize(imageIcon.getIconWidth(), imageIcon.getIconHeight());

                        if (bestSize.width == 0 || bestSize.height == 0) {
                            dge.startDrag(null, t, this);
                            return;
                        }

                        Image image;
                        if (bestSize.width > bestSize.height) {
                            bestSize.height = (int) ((((double) bestSize.width) / ((double) imageIcon.getIconWidth())) * imageIcon.getIconHeight());
                        } else {
                            bestSize.width = (int) ((((double) bestSize.height) / ((double) imageIcon.getIconHeight())) * imageIcon.getIconWidth());                            
                        }
                        image = imageIcon.getImage().getScaledInstance(bestSize.width, bestSize.height, Image.SCALE_DEFAULT);

                        
                        MediaTracker mt = new MediaTracker(c);
                        mt.addImage(image, 0, bestSize.width, bestSize.height);
                        try {
                            mt.waitForID(0);
                        } catch (InterruptedException e) {
                            dge.startDrag(null, t, this);
                            return;
                        }
                        
                        Point point = new Point(bestSize.width / 2, bestSize.height / 2);
                        Cursor cursor;
                        try {
                            cursor = tk.createCustomCursor(image, point, "freeColDragIcon");                            
                        } catch (RuntimeException re) {
                            cursor = null;
                        }
                        
                        dge.startDrag(cursor, t, this);                    
                    } else {
                        dge.startDrag(null, t, this);
                    }

                    return;
                } catch (RuntimeException re) {
                    c.setAutoscrolls(scrolls);
                }
            }

            th.exportDone(c, null, NONE);
        }

        

        
        public void dragEnter(DragSourceDragEvent dsde) {
        }


        
        public void dragOver(DragSourceDragEvent dsde) {
        }


        
        public void dragExit(DragSourceEvent dsde) {
        }


        
        public void dragDropEnd(DragSourceDropEvent dsde) {
            DragSourceContext dsc = dsde.getDragSourceContext();
            JComponent c = (JComponent)dsc.getComponent();
            
            if (dsde.getDropSuccess()) {
                ((DefaultTransferHandler) c.getTransferHandler()).exportDone(c, dsc.getTransferable(), dsde.getDropAction());
            } else {
                ((DefaultTransferHandler) c.getTransferHandler()).exportDone(c, null, NONE);
            }
            c.setAutoscrolls(scrolls);
        }


        public void dropActionChanged(DragSourceDragEvent dsde) {
            DragSourceContext dsc = dsde.getDragSourceContext();
            JComponent comp = (JComponent)dsc.getComponent();
            updatePartialChosen(comp, dsde.getUserAction() == MOVE);
        }
        
        
        private void updatePartialChosen(JComponent comp, boolean partialChosen) {
            if (comp instanceof GoodsLabel) {
                ((GoodsLabel) comp).setPartialChosen(partialChosen);
            } else if (comp instanceof MarketLabel) {
                ((MarketLabel) comp).setPartialChosen(partialChosen);
            }
        }
    }


    private static class FreeColDragGestureRecognizer extends DragGestureRecognizer {

        FreeColDragGestureRecognizer(DragGestureListener dgl) {
            super(DragSource.getDefaultDragSource(), null, NONE, dgl);
        }

        void gestured(JComponent c, MouseEvent e, int srcActions, int action) {
            setComponent(c);
            setSourceActions(srcActions);
            appendEvent(e);

            fireDragGestureRecognized(action, e.getPoint());
        }


        
        protected void registerListeners() {
        }


        
        protected void unregisterListeners() {
        }
    }
}
