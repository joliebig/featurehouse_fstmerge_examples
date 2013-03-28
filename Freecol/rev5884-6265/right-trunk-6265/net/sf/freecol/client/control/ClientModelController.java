

package net.sf.freecol.client.control;

import java.util.ArrayList;
import java.util.logging.Logger;

import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.networking.Client;
import net.sf.freecol.common.PseudoRandom;
import net.sf.freecol.common.model.Building;
import net.sf.freecol.common.model.BuildingType;
import net.sf.freecol.common.model.FreeColGameObject;
import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.Location;
import net.sf.freecol.common.model.ModelController;
import net.sf.freecol.common.model.Ownable;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.TileImprovement;
import net.sf.freecol.common.model.TileImprovementType;
import net.sf.freecol.common.model.TileType;
import net.sf.freecol.common.model.Player.Stance;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.TradeRoute;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.UnitType;
import net.sf.freecol.common.networking.Message;

import org.w3c.dom.Element;


public class ClientModelController implements ModelController {

    private static final Logger logger = Logger.getLogger(ClientModelController.class.getName());


    private final FreeColClient freeColClient;


    
    public ClientModelController(FreeColClient freeColClient) {
        this.freeColClient = freeColClient;
    }

    
    public int getRandom(String taskID, int n) {
        Client client = freeColClient.getClient();

        Element getRandomElement = Message.createNewRootElement("getRandom");
        getRandomElement.setAttribute("taskID", taskID);
        getRandomElement.setAttribute("n", Integer.toString(n));

        
        Element reply = client.ask(getRandomElement);
        

        if (!reply.getTagName().equals("getRandomConfirmed")) {
            logger.warning("Wrong tag name.");
            throw new IllegalStateException();
        }

        return Integer.parseInt(reply.getAttribute("result"));
    }

    
    public Unit createUnit(String taskID, Location location, Player owner, UnitType type) {

        Element createUnitElement = Message.createNewRootElement("createUnit");
        createUnitElement.setAttribute("taskID", taskID);
        createUnitElement.setAttribute("location", location.getId());
        createUnitElement.setAttribute("owner", owner.getId());
        createUnitElement.setAttribute("type", type.getId());

        logger.info("Waiting for the server to reply...");
        Element reply = freeColClient.getClient().ask(createUnitElement);
        logger.info("Reply received from server.");

        if (!reply.getTagName().equals("createUnitConfirmed")) {
            logger.warning("Wrong tag name.");
            throw new IllegalStateException();
        }

        Element unitElement = (Element) reply.getElementsByTagName(Unit.getXMLElementTagName()).item(0);
        Unit unit = new Unit(freeColClient.getGame(), unitElement);
        
        unit.setLocationNoUpdate(null);
        unit.setLocation(location);

        return unit;
    }

    
    public Building createBuilding(String taskID, Colony colony, BuildingType type) {

        Element createBuildingElement = Message.createNewRootElement("createBuilding");
        createBuildingElement.setAttribute("taskID", taskID);
        createBuildingElement.setAttribute("colony", colony.getId());
        createBuildingElement.setAttribute("type", type.getId());

        logger.info("Waiting for the server to reply...");
        Element reply = freeColClient.getClient().ask(createBuildingElement);
        logger.info("Reply received from server.");

        if (!reply.getTagName().equals("createBuildingConfirmed")) {
            logger.warning("Wrong tag name.");
            throw new IllegalStateException();
        }
        
        Element buildingElement = (Element) reply.getElementsByTagName(Building.getXMLElementTagName()).item(0);
        Building building = new Building(freeColClient.getGame(), buildingElement);
        return building;
    }

    
    public Location setToVacantEntryLocation(Unit unit) {
        Element createUnitElement = Message.createNewRootElement("getVacantEntryLocation");
        createUnitElement.setAttribute("unit", unit.getId());

        Element reply = freeColClient.getClient().ask(createUnitElement);
        if (reply == null) {
            throw new IllegalStateException("No reply for getVacantEntryLocation!");
        } else if (!"getVacantEntryLocationConfirmed".equals(reply.getTagName())) {
            throw new IllegalStateException("Unexpected reply type for getVacantEntryLocation: " + reply.getTagName());
        }

        Location entryLocation = (Location) freeColClient.getGame()
                .getFreeColGameObject(reply.getAttribute("location"));
        unit.setLocation(entryLocation);

        return entryLocation;
    }

    
    public void setStance(Player first, Player second, Stance stance) {
        
    }

    
    public void exploreTiles(Player player, ArrayList<Tile> tiles) {
        
    }

    
    public void update(Tile tile) {
        
    }
    
    
    public void tileImprovementFinished(Unit unit, TileImprovement improvement){
        
        Tile tile = unit.getTile();
        TileType changeType = improvement.getChange(tile.getType());
        if (changeType != null) {
            
            tile.setType(changeType);
        } else {
            
            tile.add(improvement);
            
        }
    }

    
    public PseudoRandom getPseudoRandom() {
        return freeColClient.getPseudoRandom();
    }

    
    public TradeRoute getNewTradeRoute(Player player) {
        Game game = freeColClient.getGame();
        Client client = freeColClient.getClient();

        Element getNewTradeRouteElement = Message.createNewRootElement("getNewTradeRoute");
        Element reply = client.ask(getNewTradeRouteElement);

        if (!reply.getTagName().equals("getNewTradeRouteConfirmed")) {
            logger.warning("Wrong tag name.");
            throw new IllegalStateException();
        }

        Element routeElement = (Element) reply.getElementsByTagName(TradeRoute.getXMLElementTagName()).item(0);
        TradeRoute tradeRoute = new TradeRoute(game, routeElement);

        return tradeRoute;
    }

}
