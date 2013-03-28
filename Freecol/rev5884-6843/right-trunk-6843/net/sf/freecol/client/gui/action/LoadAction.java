

package net.sf.freecol.client.gui.action;

import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.swing.KeyStroke;

import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.GUI;
import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.Goods;
import net.sf.freecol.common.model.Unit;


public class LoadAction extends MapboardAction {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(LoadAction.class.getName());

    public static final String id = "loadAction";

    
    public LoadAction(FreeColClient freeColClient) {
        super(freeColClient, "menuBar.orders.load", null, KeyStroke.getKeyStroke('L', 0));
    }

    
    public String getId() {
        return id;
    }

    
    protected boolean shouldBeEnabled() {
        if (super.shouldBeEnabled()) {
            GUI gui = getFreeColClient().getGUI();
            if (gui != null) {
                Unit unit = getFreeColClient().getGUI().getActiveUnit();
                return (unit != null && unit.isCarrier()
                        && unit.getGoodsCount() > 0
                        && unit.getColony() != null);
            }
        }
        return false;
    }    
    
        
    public void actionPerformed(ActionEvent e) {
        Unit unit = getFreeColClient().getGUI().getActiveUnit();
        if (unit != null) {
            Colony colony = unit.getColony();
            if (colony != null) {
                Iterator<Goods> goodsIterator = unit.getGoodsIterator();
                while (goodsIterator.hasNext()) {
                    Goods goods = goodsIterator.next();
                    if (goods.getAmount() < 100 && colony.getGoodsCount(goods.getType()) > 0) {
                        int amount = Math.min(100 - goods.getAmount(), colony.getGoodsCount(goods.getType()));
                        Goods newGoods = new Goods(goods.getGame(), colony, goods.getType(), amount);
                        getFreeColClient().getInGameController().loadCargo(newGoods, unit);
                    }
                }
            }
        }
    }

}
