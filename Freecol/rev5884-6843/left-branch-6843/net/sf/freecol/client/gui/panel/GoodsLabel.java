

package net.sf.freecol.client.gui.panel;

import java.awt.Color;
import java.awt.Image;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.GameOptions;
import net.sf.freecol.common.model.Goods;
import net.sf.freecol.common.model.Location;
import net.sf.freecol.common.model.Ownable;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.resources.ResourceManager;


public final class GoodsLabel extends JLabel {
    
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(GoodsLabel.class.getName());
    
    private final Goods goods;
    
    private final Canvas parent;
    
    private boolean selected;
    
    private boolean partialChosen;
    
    private boolean toEquip;
    
    
    
    public GoodsLabel(Goods goods, Canvas parent) {
        super(parent.getImageLibrary().getGoodsImageIcon(goods.getType()));
        this.goods = goods;
        setToolTipText(Messages.getName(goods));
        this.parent = parent;
        selected = false;
        partialChosen = false;
        toEquip = false;
        initializeDisplay();
    }
    
    
    public GoodsLabel(Goods goods, Canvas parent, boolean isSmall) {
        this(goods, parent);
        setSmall(true);
    }
    
    
    private void initializeDisplay() {
        Player player = null;
        Location location = goods.getLocation();
        
        if (location instanceof Ownable) {
            player = ((Ownable) location).getOwner();
        }
        if (player == null
            || !goods.getType().isStorable()
            || player.canTrade(goods)
            || (location instanceof Colony
                && player.getGameOptions().getBoolean(GameOptions.CUSTOM_IGNORE_BOYCOTT)
                && ((Colony) location).hasAbility("model.ability.export"))) {
            setToolTipText(Messages.getName(goods));
        } else {
            setToolTipText(Messages.message("model.goods.boycotted",
                                            "%goods%", Messages.getName(goods)));
            setIcon(getDisabledIcon());
        }
        
        if (!goods.getType().limitIgnored()
            && location instanceof Colony
            && ((Colony) location).getWarehouseCapacity() < goods.getAmount()) {
            setForeground(ResourceManager.getColor("goodsLabel.capacityExceeded.color"));
        } else if (location instanceof Colony
                   && goods.getType().isStorable()
                   && ((Colony) location).getExportData(goods.getType()).isExported()) {
            setForeground(ResourceManager.getColor("goodsLabel.exported.color"));
        } else if (goods.getAmount() == 0) {
            setForeground(ResourceManager.getColor("goodsLabel.zeroAmount.color"));
        } else if (goods.getAmount() < 0) {
            setForeground(ResourceManager.getColor("goodsLabel.negativeAmount.color"));
        } else {
            setForeground(ResourceManager.getColor("goodsLabel.positiveAmount.color"));
        }
        
        super.setText(String.valueOf(goods.getAmount()));
    }
    
    public boolean isPartialChosen() {
        return partialChosen;
    }
    
    public void setPartialChosen(boolean partialChosen) {
        this.partialChosen = partialChosen;
    }
    
    public boolean isToEquip() {
        return toEquip;
    }
    
    public void toEquip(boolean toEquip) {
        this.toEquip = toEquip;
    }
    
    
    
    public Canvas getCanvas() {
        return parent;
    }
    
    
    public Goods getGoods() {
        return goods;
    }
    
    
    public void setSelected(boolean b) {
        selected = b;
    }
    
    
    public void setSmall(boolean isSmall) {
        if (isSmall) {
            ImageIcon imageIcon = parent.getImageLibrary().getGoodsImageIcon(goods.getType());
            setIcon(new ImageIcon(imageIcon.getImage().getScaledInstance(imageIcon.getIconWidth() / 2,
                    imageIcon.getIconHeight() / 2, Image.SCALE_DEFAULT)));
        } else {
            setIcon(parent.getImageLibrary().getGoodsImageIcon(goods.getType()));
        }
    }
}
