


package net.sf.freecol.common.model;



public interface TransactionListener {

    
    
    public void logPurchase(GoodsType goodsType, int amount, int price);

    
    public void logSale(GoodsType goodsType, int amount, int price, int tax);
}
