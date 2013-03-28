

package net.sf.freecol.client.gui.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import net.sf.freecol.client.ClientOptions;
import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.common.model.Goods;
import net.sf.freecol.common.model.GoodsType;


public final class ProductionLabel extends JComponent {

    private static Logger logger = Logger.getLogger(ProductionLabel.class.getName());

    private final Canvas parent;

    
    private int maxIcons = 7;

    
    private boolean drawPlus = false;

    
    private boolean centered = true;

    
    private int compressedWidth = -1;

    
    private GoodsType goodsType;

    
    private ImageIcon goodsIcon;

    
    private int production;

    
    private int maximumProduction = -1;

    
    private int displayNumber;

    
    private int stockNumber = -1;

    
    private String toolTipPrefix = null;

    
    public ProductionLabel(Goods goods, Canvas parent) {
        this(goods.getType(), goods.getAmount(), -1, parent);
    }

    
    public ProductionLabel(GoodsType goodsType, int amount, Canvas parent) {
        this(goodsType, amount, -1, parent);
    }

    
    public ProductionLabel(GoodsType goodsType, int amount, int maximumProduction, Canvas parent) {
        super();
        this.parent = parent;
        this.production = amount;
        this.goodsType = goodsType;
        this.maximumProduction = maximumProduction;
        ClientOptions options = parent.getClient().getClientOptions();
        maxIcons = options.getInteger(ClientOptions.MAX_NUMBER_OF_GOODS_IMAGES);
        displayNumber = options.getInteger(ClientOptions.MIN_NUMBER_FOR_DISPLAYING_GOODS_COUNT);

        
        setFont(new Font("Dialog", Font.BOLD, 12));
        if (amount < 0) {
            setForeground(Color.RED);
        } else {
            setForeground(Color.WHITE);
        }
        if (goodsType != null) {
            setGoodsIcon(parent.getImageLibrary().getGoodsImageIcon(goodsType));
            updateToolTipText();
        }
    }

    
    public String getToolTipPrefix() {
        return toolTipPrefix;
    }

    
    public void setToolTipPrefix(final String newToolTipPrefix) {
        this.toolTipPrefix = newToolTipPrefix;
        updateToolTipText();
    }

    
    public Canvas getCanvas() {
        return parent;
    }

    
    public int getDisplayNumber() {
        return displayNumber;
    }

    
    public void setDisplayNumber(final int newDisplayNumber) {
        this.displayNumber = newDisplayNumber;
    }

    
    public ImageIcon getGoodsIcon() {
        return goodsIcon;
    }

    
    public void setGoodsIcon(final ImageIcon newGoodsIcon) {
        this.goodsIcon = newGoodsIcon;
        compressedWidth = goodsIcon.getIconWidth()*2;
    }

    
    public int getProduction() {
        return production;
    }

    
    public void setProduction(final int newProduction) {
        this.production = newProduction;
        updateToolTipText();
    }

    private void updateToolTipText() {
        if (goodsType == null || production == 0) {
            setToolTipText(null);
        } else {
            String text = Goods.toString(goodsType, production);
            if (toolTipPrefix != null) {
                text = toolTipPrefix + " " + text;
            }
            setToolTipText(text);
        }
    }

    
    public int getMaximumProduction() {
        return maximumProduction;
    }

    
    public void setMaximumProduction(final int newMaximumProduction) {
        this.maximumProduction = newMaximumProduction;
    }

    
    public int getMaxGoodsIcons() {
        return maxIcons;
    }

    
    public void setMaxGoodsIcons(final int newMaxGoodsIcons) {
        this.maxIcons = newMaxGoodsIcons;
    }

    
    public int getStockNumber() {
        return stockNumber;
    }

    
    public void setStockNumber(final int newStockNumber) {
        this.stockNumber = newStockNumber;
    }

    
    public boolean drawPlus() {
        return drawPlus;
    }

    
    public void setDrawPlus(final boolean newDrawPlus) {
        this.drawPlus = newDrawPlus;
    }

    
    public boolean isCentered() {
        return centered;
    }

    
    public void setCentered(final boolean newCentered) {
        this.centered = newCentered;
    }

    
    public int getCompressedWidth() {
        return compressedWidth;
    }

    
    public void setCompressedWidth(final int newCompressedWidth) {
        this.compressedWidth = newCompressedWidth;
    }

    
    public Dimension getPreferredSize() {

        if (goodsIcon == null || production == 0) {
            return new Dimension(0, 0);
        } else {
            return new Dimension(getPreferredWidth(), goodsIcon.getImage().getHeight(null));
        }
    }


    
    
    public int getPreferredWidth() {

        if (goodsIcon == null || production == 0) {
            return 0;
        }

        int drawImageCount = Math.min(Math.abs(production), maxIcons);

        int iconWidth = goodsIcon.getIconWidth();
        int pixelsPerIcon = iconWidth / 2;
        if (pixelsPerIcon - iconWidth < 0) {
            pixelsPerIcon = (compressedWidth - iconWidth) / drawImageCount;
        }
        int maxSpacing = iconWidth;

        
        boolean iconsTooFarApart = pixelsPerIcon > maxSpacing;
        if (iconsTooFarApart) {
            pixelsPerIcon = maxSpacing;
        }

        return pixelsPerIcon * (drawImageCount - 1) + iconWidth;

    }

    
    public void paintComponent(Graphics g) {

        if (goodsIcon == null || (production == 0 && stockNumber<0) ) {
            return;
        }

        BufferedImage stringImage = null;
        int stringWidth = 0;
        if (production >= displayNumber || production < 0 || maxIcons < production || stockNumber > 0
            || (maximumProduction > production && production > 0)) {
            String number = "";
            if (stockNumber >= 0 ) {
                number = Integer.toString(stockNumber);  
                drawPlus = true;
            }
            if (production >=0 && drawPlus ) {
                number = number + "+" + Integer.toString(production);
            } else {
                number = number + Integer.toString(production);
            }
            if (maximumProduction > production && production > 0) {
                number = number + "/" + String.valueOf(maximumProduction);
            }
            stringImage = parent.getGUI().createStringImage(this, number, getForeground(), -1, 12);
            stringWidth = stringImage.getWidth(null);
        }

        int drawImageCount = Math.min(Math.abs(production), maxIcons);
        if (drawImageCount==0) {
            drawImageCount=1;
        }

        int iconWidth = goodsIcon.getIconWidth();
        int pixelsPerIcon = iconWidth / 2;
        if (pixelsPerIcon - iconWidth < 0) {
            pixelsPerIcon = (compressedWidth - iconWidth) / drawImageCount;
        }
        int maxSpacing = iconWidth;

        
        boolean iconsTooFarApart = pixelsPerIcon > maxSpacing;
        if (iconsTooFarApart) {
            pixelsPerIcon = maxSpacing;
        }
        int coverage = pixelsPerIcon * (drawImageCount - 1) + iconWidth;
        int leftOffset = 0;

        int width = Math.max(getWidth(), Math.max(stringWidth, coverage));

        if (centered && coverage < width) {
            leftOffset = (width - coverage)/2;
        }

        int height = Math.max(getHeight(), goodsIcon.getImage().getHeight(null));
        setSize(new Dimension(width, height));


        
        for (int i = 0; i < drawImageCount; i++) {
            goodsIcon.paintIcon(null, g, leftOffset + i*pixelsPerIcon, 0);
        }

        if (stringImage != null) {
            int textOffset = width > stringWidth ? (width - stringWidth)/2 : 0;
            textOffset = (textOffset >= 0) ? textOffset : 0;
            g.drawImage(stringImage, textOffset,
                        goodsIcon.getIconHeight()/2 - stringImage.getHeight()/2, null);
        }
    }

}
