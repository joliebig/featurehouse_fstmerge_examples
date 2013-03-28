

package net.sf.freecol.client.gui.panel;

import java.awt.Color;
import java.awt.Image;

import javax.swing.ImageIcon;

import net.sf.freecol.common.model.GoodsType;
import net.sf.freecol.common.model.TileType;


public abstract class ImageProvider {




    
    public abstract Image getTerrainImage(TileType type, int x, int y);

    
    public abstract Image getGoodsImage(GoodsType type);

    
    public abstract ImageIcon getGoodsImageIcon(GoodsType type);

    
    public abstract Image getMiscImage(String type);

    
    public abstract Image getColorChip(Color color);

    
    
    
    
    

    
    

    
    

    
    public abstract ImageIcon getUnitButtonImageIcon(int index, int state);

    
    public abstract int getTerrainImageWidth(TileType type);

    
    public abstract int getTerrainImageHeight(TileType type);

    
    

    
    

    
    
}
