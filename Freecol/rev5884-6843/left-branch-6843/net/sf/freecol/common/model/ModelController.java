


package net.sf.freecol.common.model;

import java.util.ArrayList;

import net.sf.freecol.common.PseudoRandom;
import net.sf.freecol.common.model.Player.Stance;


public interface ModelController {

    
    public Unit createUnit(String taskID, Location location, Player owner, UnitType type);

    
    public Building createBuilding(String taskID, Colony colony, BuildingType type);

    
    public Location setToVacantEntryLocation(Unit unit);

    
    
    

    
    public void exploreTiles(Player player, ArrayList<Tile> tiles);
    
    
    public void tileImprovementFinished(Unit unit, TileImprovement improvement);
    
    
    public void setStance(Player first, Player second, Stance stance);
    
    
    
    public int getRandom(String taskID, int n);

    
    PseudoRandom getPseudoRandom();

    
    public TradeRoute getNewTradeRoute(Player player);

}
