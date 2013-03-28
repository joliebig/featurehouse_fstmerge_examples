

package net.sf.freecol.server.model;

import java.io.File;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import net.sf.freecol.common.model.FreeColGameObject;
import net.sf.freecol.common.model.FreeColGameObjectListener;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.GameOptions;
import net.sf.freecol.common.model.SimpleCombatModel;
import net.sf.freecol.common.model.ModelController;
import net.sf.freecol.common.model.Player;


public class ServerGame extends Game {

    private static final Logger logger = Logger.getLogger(ServerGame.class.getName());

    
    public ServerGame(ModelController modelController) {
        super(null);

        this.modelController = modelController;
        this.combatModel = new SimpleCombatModel(modelController.getPseudoRandom());
        

        gameOptions = new GameOptions();

        currentPlayer = null;
        
    }

    
    public ServerGame(FreeColGameObjectListener freeColGameObjectListener, ModelController modelController,
                XMLStreamReader in, FreeColGameObject[] fcgos) throws XMLStreamException {
        super(null, in);

        setFreeColGameObjectListener(freeColGameObjectListener);
        this.modelController = modelController;
        if (modelController != null) {
            
            this.combatModel = new SimpleCombatModel(modelController.getPseudoRandom());
        }
        this.viewOwner = null;

        for (FreeColGameObject object : fcgos) {
            object.setGame(this);
            object.updateID();

            if (object instanceof Player) {
                players.add((Player) object);
            }
        }

        readFromXML(in);
    }

    

    
    public String getNextID() {
        String id = Integer.toString(nextId);
        nextId++;
        return id;
    }


    
    public void newTurn() {
        getTurn().increase();
        logger.info("Turn is now " + getTurn().toString());

        for (Player player : players) {
            logger.info("Calling newTurn for player " + player.getName());
            player.newTurn();
        }
    }
}
