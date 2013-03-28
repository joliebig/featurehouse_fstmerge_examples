

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
import net.sf.freecol.common.util.Utils;


public final class ProductionMultiplesLabel extends JComponent {
    private static Logger logger = Logger.getLogger(ProductionMultiplesLabel.class.getName());

    private final Canvas parent;

    
    private int maxIcons = 7;

    
    private boolean drawPlus = false;

    
    private boolean centered = true;

    
    private int compressedWidth = -1;

    
    private GoodsType goodsType[];

    
    private ImageIcon goodsIcon[];

    
    private int production[];
    private int totalProduction;

    
    private int maximumProduction = -1;

    
    private int displayNumber;

    
    private int stockNumber = -1;

    
    private String toolTipPrefix = null;

    
    public ProductionMultiplesLabel(Goods goods, Canvas parent) {
        this(goods.getType(), goods.getAmount(), -1, parent);
    }

    
    public ProductionMultiplesLabel(GoodsType goodsType, int amount, Canvas parent) {
        this(goodsType, amount, -1, parent);
    }

    
    public ProductionMultiplesLabel(GoodsType goodsType, int amount, int maximumProduction, Canvas parent) {
    	this( new GoodsType[]{goodsType}, new int[]{amount}, maximumProduction, parent);
    }
    
    public ProductionMultiplesLabel(GoodsType goodsType, int amount, GoodsType goodsType2, int amount2, Canvas parent) {
    	this( new GoodsType[]{goodsType, goodsType2}, new int[]{amount, amount2}, -1, parent);
    }
    
    
    public ProductionMultiplesLabel(GoodsType[] goodsType, int[] amount, int maximumProduction, Canvas parent) {
        super();
        this.parent = parent;
        this.production = amount;
        this.goodsType = goodsType;
        this.maximumProduction = maximumProduction;
        ClientOptions options = parent.getClient().getClientOptions();
        maxIcons = options.getInteger(ClientOptions.MAX_NUMBER_OF_GOODS_IMAGES);
        displayNumber = options.getInteger(ClientOptions.MIN_NUMBER_FOR_DISPLAYING_GOODS_COUNT);

        setFont(new Font("Dialog", Font.BOLD, 12));
        totalProduction = 0;
    	
        if (goodsType != null) {
            goodsIcon = new ImageIcon[goodsType.length];
            for (int ii=0; ii < goodsType.length; ii++) {
                goodsIcon[ii] = parent.getGUI().getImageLibrary().getGoodsImageIcon(goodsType[ii]);
                totalProduction += amount[ii];
            }
            compressedWidth = getMaximumIconWidth()*2;
            updateToolTipText();
        }
        
        if (totalProduction < 0) {
            setForeground(Color.RED);
        } else {
            setForeground(Color.WHITE);
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
    	logger.warning("GETTING ProductionMultiplesLabel's getGoodsIcon...");
        return goodsIcon[0];
    }

    
    public void setGoodsIcon(final ImageIcon newGoodsIcon) {
    	logger.warning("RESETTING ProductionMultiplesLabel's setGoodsIcon generally instead of specifically...");
        this.goodsIcon[0] = newGoodsIcon;
    }

    
    public int getProduction() {
        return totalProduction;
    }

    
    public void setProduction(final int newProduction) {
    	logger.warning("RESETTING ProductionMultiplesLabel's value generally instead of specifically to: "+ newProduction);
        this.totalProduction = newProduction;
        updateToolTipText();
    }

    private void updateToolTipText() {
        if (goodsType == null || goodsType.length == 0 || totalProduction == 0) {
            setToolTipText(null);
        } else {
            String[] parts = new String[goodsType.length];
            for (int index = 0; index < goodsType.length; index++) {
                parts[index] = Goods.toString(goodsType[index], production[index]);
            }
            String text = Utils.join(", ", parts);
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

        if (goodsIcon == null || totalProduction == 0) {
            return new Dimension(0, 0);
        } else {
            return new Dimension(getPreferredWidth(), getMaximumIconHeight());
        }
    }


    
    
    public int getPreferredWidth() {

        if (goodsIcon == null || totalProduction == 0) {
            return 0;
        }

        int drawImageCount = Math.min(Math.abs(totalProduction), maxIcons);

        int iconWidth = getMaximumIconWidth();
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
    
    
    public int getMaximumIconWidth() {
    	int width = 0;
    	for( int ii=0; ii < goodsIcon.length; ii++ ) {
    		if( goodsIcon[ii].getIconWidth() > width ) {
    			width = goodsIcon[ii].getIconWidth();
    		}
    	}
    	return width;
    }

    
    public int getMaximumIconHeight() {
    	int height = 0;
    	for( int ii=0; ii < goodsIcon.length; ii++ ) {
    		if( goodsIcon[ii].getImage().getHeight(null) > height ) {
    			height = goodsIcon[ii].getImage().getHeight(null);
    		}
    	}
    	return height;
    }

    
    public void paintComponent(Graphics g) {

        if (goodsIcon == null || (totalProduction == 0 && stockNumber<0) ) {
            return;
        }

        int drawImageCount = Math.min(Math.abs(totalProduction), maxIcons);
        if (drawImageCount==0) {
            drawImageCount=1;
        }

        int iconWidth = getMaximumIconWidth();
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

        boolean needToCenterImages = centered && coverage < getWidth();
        if (needToCenterImages) {
            leftOffset = (getWidth() - coverage)/2;
        }

        int width = Math.max(getWidth(), coverage);
        int height = Math.max(getHeight(), getMaximumIconHeight());
        setSize(new Dimension(width, height));

        
        int countImages = 0;
        int leftImageOffset = 0;
        for( int indexGoods = 0; indexGoods < goodsIcon.length; indexGoods++ ) {
            
            for (int i = 0; i < Math.abs(production[indexGoods]); i++) {

                goodsIcon[indexGoods].paintIcon(null, g, leftOffset + leftImageOffset, 0);
            	leftImageOffset += pixelsPerIcon;
                if( ++countImages >= drawImageCount ) {
                	indexGoods = goodsIcon.length;
                	break;
                }
            }
        }
        

        if (totalProduction >= displayNumber || totalProduction < 0 || maxIcons < totalProduction || stockNumber>0) {
            String number = "";
            if (stockNumber >= 0 ) {
                number = Integer.toString(stockNumber);  
                drawPlus = true;
            }
            if (totalProduction >=0 && drawPlus ) {
                number = number + "+" + Integer.toString(totalProduction);
            } else {
                number = number + Integer.toString(totalProduction);
            }
            if (maximumProduction > totalProduction && totalProduction > 0) {
                number = number + "/" + String.valueOf(maximumProduction);
            }
            BufferedImage stringImage = parent.getGUI().createStringImage(this, number, getForeground(), width, 12);
            int textOffset = leftOffset + (coverage - stringImage.getWidth())/2;
            textOffset = (textOffset >= 0) ? textOffset : 0;
            g.drawImage(stringImage, textOffset,

                    getMaximumIconHeight()/2 - stringImage.getHeight()/2, null);
        }
    }

}
