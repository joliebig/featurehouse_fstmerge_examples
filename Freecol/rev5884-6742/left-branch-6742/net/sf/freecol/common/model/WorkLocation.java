

package net.sf.freecol.common.model;


public interface WorkLocation extends Location {

    public void newTurn();

    
    public int getProductionOf(GoodsType goodsType);

    
    public Colony getColony();
}
