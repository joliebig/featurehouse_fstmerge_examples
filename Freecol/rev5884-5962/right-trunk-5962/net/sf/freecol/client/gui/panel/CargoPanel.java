

package net.sf.freecol.client.gui.panel;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;
import javax.swing.JPanel;

import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.model.Goods;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Unit;


public class CargoPanel extends FreeColPanel implements PropertyChangeListener {

    
    private Unit carrier;

    private final DefaultTransferHandler defaultTransferHandler;

    private final MouseListener pressListener;

    private final TitledBorder border;

    
    private boolean editable = true;

    
    private JPanel parentPanel;

    
    public CargoPanel(Canvas parent, boolean withTitle) {
        super(parent);

        defaultTransferHandler = new DefaultTransferHandler(parent, this);
        pressListener = new DragListener(this);

        if (withTitle) {
            border = BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(),
                                                      Messages.message("cargoOnCarrier"));
        } else {
            border = null;
        }

        setBorder(border);
        initialize();
    }

    @Override
    public String getUIClassID() {
        return "CargoPanelUI";
    }

    
    public final JPanel getParentPanel() {
        return parentPanel;
    }

    
    public final void setParentPanel(final JPanel newParentPanel) {
        this.parentPanel = newParentPanel;
    }

    
    public Unit getCarrier() {
        return carrier;
    }

    
    public void setCarrier(final Unit newCarrier) {
        if (carrier != null) {
            carrier.removePropertyChangeListener(this);
        }
        this.carrier = newCarrier;
        if (carrier != null) {
            carrier.addPropertyChangeListener(Unit.CARGO_CHANGE, this);
        }
        initialize();
    }

    
    public boolean isEditable() {
        return editable;
    }

    
    public void setEditable(final boolean newEditable) {
        this.editable = newEditable;
    }

    public void initialize() {
        removeAll();

        if (carrier != null) {

            Iterator<Unit> unitIterator = carrier.getUnitIterator();
            while (unitIterator.hasNext()) {
                Unit unit = unitIterator.next();

                UnitLabel label = new UnitLabel(unit, getCanvas());
                if (isEditable()) {
                    label.setTransferHandler(defaultTransferHandler);
                    label.addMouseListener(pressListener);
                }

                add(label);
            }

            Iterator<Goods> goodsIterator = carrier.getGoodsIterator();
            while (goodsIterator.hasNext()) {
                Goods g = goodsIterator.next();

                GoodsLabel label = new GoodsLabel(g, getCanvas());
                if (isEditable()) {
                    label.setTransferHandler(defaultTransferHandler);
                    label.addMouseListener(pressListener);
                }

                add(label);
            }
        }
        updateTitle();
        revalidate();
        repaint();
    }

    private void updateTitle() {
        
        if (border == null) {
            return;
        }
        
        if (carrier == null) {
            border.setTitle(Messages.message("cargoOnCarrier"));
        } else {
            int spaceLeft = carrier.getSpaceLeft();
            border.setTitle(Messages.message("cargoOnCarrierLong", 
                                             "%name%", carrier.getName(),
                                             "%space%", String.valueOf(spaceLeft)));
        }
    }

    public boolean isActive() {
        return (carrier != null);
    }

    
    public Component add(Component comp, boolean editState) {
        if (carrier == null) {
            return null;
        }
        
        if (editState) {
            if (comp instanceof UnitLabel) {
                Unit unit = ((UnitLabel) comp).getUnit();
                if (carrier.canAdd(unit)) {
                    Container oldParent = comp.getParent();
                    if (getController().boardShip(unit, carrier)) {
                        ((UnitLabel) comp).setSmall(false);
                        oldParent.remove(comp);
                        initialize();
                        return comp;
                    }
                }
            } else if (comp instanceof GoodsLabel) {
                Goods goods = ((GoodsLabel) comp).getGoods();

                int loadableAmount = carrier.getLoadableAmount(goods.getType());
                if (loadableAmount == 0) {
                    return null;
                } else if (loadableAmount > goods.getAmount()) {
                    loadableAmount = goods.getAmount();
                }
                Goods goodsToAdd = new Goods(goods.getGame(), goods.getLocation(),
                                             goods.getType(), loadableAmount);
                goods.setAmount(goods.getAmount() - loadableAmount);
                getController().loadCargo(goodsToAdd, carrier);
                initialize();
                return comp;
            } else if (comp instanceof MarketLabel) {
                MarketLabel label = (MarketLabel) comp;
                Player player = carrier.getOwner();
                if (player.canTrade(label.getType())) {
                    getController().buyGoods(label.getType(), label.getAmount(), carrier);
                    getController().nextModelMessage();
                    initialize();
                    return comp;
                } else {
                    getController().payArrears(label.getType());
                    return null;
                }
            } else {
                return null;
            }
        } else {
            super.add(comp);
        }

        return null;
    }


    @Override
    public void remove(Component comp) {
        if (comp instanceof UnitLabel) {
            Unit unit = ((UnitLabel) comp).getUnit();
            getController().leaveShip(unit);
            initialize();
        } else if (comp instanceof GoodsLabel) {
            Goods g = ((GoodsLabel) comp).getGoods();
            getController().unloadCargo(g);
            initialize();
        }
    }

    public void propertyChange(PropertyChangeEvent event) {
        initialize();
    }



}

