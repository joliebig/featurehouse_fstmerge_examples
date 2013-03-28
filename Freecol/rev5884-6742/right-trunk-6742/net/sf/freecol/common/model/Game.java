

package net.sf.freecol.common.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.FreeCol;
import net.sf.freecol.common.model.NationOptions.NationState;


public class Game extends FreeColGameObject {

    public static final String CIBOLA_TAG = "cibola";

    private static final Logger logger = Logger.getLogger(Game.class.getName());

    
    private Player unknownEnemy;

    
    protected List<Player> players = new ArrayList<Player>();

    private Map map;

    protected GameOptions gameOptions;

    
    protected Player currentPlayer = null;

    
    protected Player viewOwner = null;

    
    protected HashMap<String, FreeColGameObject> freeColGameObjects = new HashMap<String, FreeColGameObject>(10000);

    
    protected int nextId = 1;

    private Turn turn = new Turn(1);

    
    private NationOptions nationOptions = NationOptions.getDefaults();

    
    private boolean spanishSuccession = false;

    
    private DifficultyLevel difficultyLevel;

    protected ModelController modelController;

    protected FreeColGameObjectListener freeColGameObjectListener;

    
    private List<String> citiesOfCibola = null;

    
    protected CombatModel combatModel;

    
    protected Game(Game game) {
        super(game);
    }
    
    
    protected Game(Game game, XMLStreamReader in) throws XMLStreamException {
        super(game, in);
    }

    

    
    public Game(ModelController modelController, XMLStreamReader in, String viewOwnerUsername)
        throws XMLStreamException {
        super(null, in);

        this.modelController = modelController;
        this.combatModel = new SimpleCombatModel(modelController.getPseudoRandom());
        readFromXML(in);
        this.viewOwner = getPlayerByName(viewOwnerUsername);
    }

    
    public ModelController getModelController() {
        return modelController;
    }

    
    public Player getUnknownEnemy() {
    	return unknownEnemy;
    }

    
    public void setUnknownEnemy(Player player) {
        this.unknownEnemy = player;
    }
    
    
    public final List<Nation> getVacantNations() {
        List<Nation> result = new ArrayList<Nation>();
        for (Entry<Nation, NationState> entry : nationOptions.getNations().entrySet()) {
            if (entry.getValue() == NationState.AVAILABLE) {
                result.add(entry.getKey());
            }
        }
        return result;
    }

    
    public boolean isClientTrusted() {
        
        return false;
    }

    
    public Player getViewOwner() {
        return viewOwner;
    }

    
    public Settlement getSettlement(String name) {
        Iterator<Player> pit = getPlayerIterator();
        while (pit.hasNext()) {
            Player p = pit.next();
            Settlement settlement = p.getSettlement(name);
            if (settlement != null) return settlement;
        }
        return null;
    }

    public Turn getTurn() {
        return turn;
    }

    void setTurn(Turn newTurn) {
        turn = newTurn;
    }
    
    
    public final CombatModel getCombatModel() {
        return combatModel;
    }

    
    public final void setCombatModel(final CombatModel newCombatModel) {
        this.combatModel = newCombatModel;
    }

    
    public final DifficultyLevel getDifficultyLevel() {
        return difficultyLevel;
    }

    
    public final void setDifficultyLevel(final DifficultyLevel newDifficultyLevel) {
        this.difficultyLevel = newDifficultyLevel;
    }

    
    public void addPlayer(Player player) {
        if (player.isAI() || canAddNewPlayer()) {
            players.add(player);
            Nation nation = FreeCol.getSpecification().getNation(player.getNationID());
            nationOptions.getNations().put(nation, NationState.NOT_AVAILABLE);
            if (currentPlayer == null) {
                currentPlayer = player;
            }
        } else {
            logger.warning("Tried to add a new player, but the game was already full.");
        }
    }

    
    public void removePlayer(Player player) {
        boolean updateCurrentPlayer = (currentPlayer == player);

        players.remove(players.indexOf(player));
        Nation nation = FreeCol.getSpecification().getNation(player.getNationID());
        nationOptions.getNations().put(nation, NationState.AVAILABLE);
        player.dispose();

        if (updateCurrentPlayer) {
            currentPlayer = getFirstPlayer();
        }
    }

    
    public void setFreeColGameObject(String id, FreeColGameObject freeColGameObject) {
        if (id == null || id.equals("")) {
            throw new IllegalArgumentException("Parameter 'id' must not be 'null' or empty string.");
        } else if (freeColGameObject == null) {
            throw new IllegalArgumentException("Parameter 'freeColGameObject' must not be 'null'.");
        }

        FreeColGameObject old = freeColGameObjects.put(id, freeColGameObject);
        if (old != null) {
            throw new IllegalArgumentException("Replacing FreeColGameObject: " + old.getClass() + " with "
                                               + freeColGameObject.getClass());
        }

        if (freeColGameObjectListener != null) {
            freeColGameObjectListener.setFreeColGameObject(id, freeColGameObject);
        }
    }

    public void setFreeColGameObjectListener(FreeColGameObjectListener freeColGameObjectListener) {
        this.freeColGameObjectListener = freeColGameObjectListener;
    }

    public FreeColGameObjectListener getFreeColGameObjectListener() {
        return freeColGameObjectListener;
    }

    
    public FreeColGameObject getFreeColGameObject(String id) {
        if (id == null || id.equals("")) {
            throw new IllegalArgumentException("Parameter 'id' must not be null or empty string.");
        }

        return freeColGameObjects.get(id);
    }

    
    public FreeColGameObject getFreeColGameObjectSafely(String id) {
        if (id != null && id.length()>0) {
            return freeColGameObjects.get(id);
        } else {
            return null;
        }
    }

    
    public FreeColGameObject removeFreeColGameObject(String id) {
        if (id == null || id.equals("")) {
            throw new IllegalArgumentException("Parameter 'id' must not be null or empty string.");
        }

        if (freeColGameObjectListener != null) {
            freeColGameObjectListener.removeFreeColGameObject(id);
        }

        return freeColGameObjects.remove(id);
    }

    
    public Map getMap() {
        return map;
    }

    
    public void setMap(Map map) {
        this.map = map;
    }

    
    public final NationOptions getNationOptions() {
        return nationOptions;
    }

    
    public final void setNationOptions(final NationOptions newNationOptions) {
        this.nationOptions = newNationOptions;
    }

    
    public Nation getVacantNation() {
        for (Entry<Nation, NationState> entry : nationOptions.getNations().entrySet()) {
            if (entry.getValue() == NationState.AVAILABLE) {
                return entry.getKey();
            }
        }
        return null;
    }

    
    public Player getPlayer(String nationID) {
        Iterator<Player> playerIterator = getPlayerIterator();
        while (playerIterator.hasNext()) {
            Player player = playerIterator.next();
            if (player.getNationID().equals(nationID)) {
                return player;
            }
        }

        return null;
    }

    
    public void setCurrentPlayer(Player newCp) {
        if (newCp != null) {
            if (currentPlayer != null) {
                currentPlayer.endTurn();
            }
        } else {
            logger.info("Current player set to 'null'.");
        }

        currentPlayer = newCp;
    }

    
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    
    public Player getNextPlayer() {
        return getPlayerAfter(currentPlayer);
    }

    
    public Player getPlayerAfter(Player beforePlayer) {
        if (players.size() == 0) {
            return null;
        }

        int index = players.indexOf(beforePlayer) + 1;

        if (index >= players.size()) {
            index = 0;
        }

        
        while (true) {
            Player player = players.get(index);
            if (!player.isDead()) {
                return player;
            }

            index++;

            if (index >= players.size()) {
                index = 0;
            }
        }
    }

    
    public boolean isNextPlayerInNewTurn() {
        return (players.indexOf(currentPlayer) > players.indexOf(getNextPlayer())
                || currentPlayer == getNextPlayer());
    }

    
    public Player getFirstPlayer() {
        if (players.isEmpty()) {
            return null;
        } else {
            return players.get(0);
        }
    }

    
    public Iterator<FreeColGameObject> getFreeColGameObjectIterator() {
        return freeColGameObjects.values().iterator();
    }

    
    public Player getPlayerByName(String name) {
        Iterator<Player> playerIterator = getPlayerIterator();

        while (playerIterator.hasNext()) {
            Player player = playerIterator.next();
            if (player.getName().equals(name)) {
                return player;
            }
        }

        return null;
    }

    
    public boolean playerNameInUse(String username) {
        
        for (Player player : players) {
            if (player.getName().equals(username)) {
                return true;
            }
        }
        return false;
    }

    
    public Iterator<Player> getPlayerIterator() {
        return players.iterator();
    }

    
    public List<Player> getPlayers() {
        return players;
    }

    public int getNumberOfPlayers() {
        return players.size();
    }

    
    public List<Player> getEuropeanPlayers() {
        List<Player> europeans = new ArrayList<Player>();
        for (Player player : players) {
            if (player.isEuropean()) {
                europeans.add(player);
            }
        }
        return europeans;
    }

    
    public boolean canAddNewPlayer() {
        return (getVacantNation() != null);
    }

    
    public boolean isAllPlayersReadyToLaunch() {
        for (Player player : players) {
            if (!player.isReady()) {
                return false;
            }
        }
        return true;
    }

    
    public final boolean getSpanishSuccession() {
        return spanishSuccession;
    }

    
    public final void setSpanishSuccession(final boolean newSpanishSuccession) {
        this.spanishSuccession = newSpanishSuccession;
    }

    
    @SuppressWarnings("unchecked")
    public boolean checkIntegrity() {
    	List<String> brokenObjects = new ArrayList<String>();
        boolean ok = true;
        Iterator<FreeColGameObject> iterator = ((HashMap<String, FreeColGameObject>) freeColGameObjects.clone())
            .values().iterator();
        while (iterator.hasNext()) {
            FreeColGameObject fgo = iterator.next();
            if (fgo.isUninitialized()) {
            	brokenObjects.add(fgo.getId());
                logger.warning("Uninitialized object: " + fgo.getId() + " (" + fgo.getClass() + ")");
                ok = false;
            }
        }
        if (ok) {
            logger.info("Game integrity ok.");
        } else {
            logger.warning("Game integrity test failed.");
            fixIntegrity(brokenObjects);
        }
        return ok;
    }
    
    
    private boolean fixIntegrity(List<String> list){
    	
    	for(Player player : this.getPlayers()){
    		for(Unit unit : player.getUnits()){
    			if(unit.getOwner() == null){
    				logger.warning("Fixing " + unit.getId() + ": owner missing");
    				unit.setOwner(player);
    			}
    		}
    	}
    	return false;
    }


    
    public GameOptions getGameOptions() {
        return gameOptions;
    }

    
    public String getCityOfCibola() {
        if (citiesOfCibola == null) {
            
            
            citiesOfCibola = new ArrayList<String>();
            for (int index = 0; index < 7; index++) {
                citiesOfCibola.add("lostCityRumour.cityName." + index);
            }
            Collections.shuffle(citiesOfCibola);
        }
        return (citiesOfCibola == null || citiesOfCibola.size() == 0) ? null
            : citiesOfCibola.remove(0);
    }

    
    public FreeColGameObject getMessageSource(ModelMessage message) {
        return getFreeColGameObjectSafely(message.getSourceId());
    }

    
    public FreeColObject getMessageDisplay(ModelMessage message) {
        String id = message.getDisplayId();
        if (id == null) id = message.getSourceId();
        FreeColObject o = getFreeColGameObjectSafely(id);
        if (o == null) {
            try {
                o = FreeCol.getSpecification().getType(id);
            } catch (Exception e) {
                o = null; 
            }
        }
        return o;
    }


    
    protected void toXMLImpl(XMLStreamWriter out, Player player, boolean showAll, boolean toSavedGame)
        throws XMLStreamException {
        
        out.writeStartElement(getXMLElementTagName());

        if (toSavedGame && !showAll) {
            throw new IllegalArgumentException("showAll must be set to true when toSavedGame is true.");
        }

        out.writeAttribute("ID", getId());
        out.writeAttribute("turn", Integer.toString(getTurn().getNumber()));
        out.writeAttribute("spanishSuccession", Boolean.toString(spanishSuccession));

        writeAttribute(out, "currentPlayer", currentPlayer);

        if (toSavedGame) {
            out.writeAttribute("nextID", Integer.toString(nextId));
        }

        if (citiesOfCibola != null) {
            for (String cityName : citiesOfCibola) {
                out.writeStartElement(CIBOLA_TAG);
                out.writeAttribute(ID_ATTRIBUTE_TAG, cityName);
                out.writeEndElement();
            }
        }
        gameOptions.toXML(out);
        nationOptions.toXML(out);

        
        Iterator<Player> playerIterator = getPlayerIterator();
        while (playerIterator.hasNext()) {
            Player p = playerIterator.next();
            p.toXML(out, player, showAll, toSavedGame);
        }
        writeFreeColGameObject(getUnknownEnemy(), out, player, showAll, toSavedGame);

        
        writeFreeColGameObject(map, out, player, showAll, toSavedGame);

        

        
        if (difficultyLevel != null) {
            difficultyLevel.toXML(out);
        }

        out.writeEndElement();
    }

    
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        setId(in.getAttributeValue(null, "ID"));

        getTurn().setNumber(getAttribute(in, "turn", 1));
        setSpanishSuccession(getAttribute(in, "spanishSuccession", false));

        final String nextIDStr = in.getAttributeValue(null, "nextID");
        if (nextIDStr != null) {
            nextId = Integer.parseInt(nextIDStr);
        }

        final String currentPlayerStr = in.getAttributeValue(null, "currentPlayer");
        if (currentPlayerStr != null) {
            currentPlayer = (Player) getFreeColGameObject(currentPlayerStr);
            if (currentPlayer == null) {
                currentPlayer = new Player(this, currentPlayerStr);
                players.add(currentPlayer);
            }
        } else {
            currentPlayer = null;
        }

        gameOptions = null;
        citiesOfCibola = new ArrayList<String>(7);
        while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
            if (in.getLocalName().equals(GameOptions.getXMLElementTagName())
                || in.getLocalName().equals("game-options")) {
                gameOptions = new GameOptions(in);
            } else if (in.getLocalName().equals(NationOptions.getXMLElementTagName())) {
                if (nationOptions == null) {
                    nationOptions = new NationOptions();
                }
                nationOptions.readFromXML(in);
            } else if (in.getLocalName().equals(Player.getXMLElementTagName())) {
                Player player = (Player) getFreeColGameObject(in.getAttributeValue(null, "ID"));
                if (player != null) {
                    player.readFromXML(in);
                } else {
                    player = new Player(this, in);
                    if (player.getName().equals(Player.UNKNOWN_ENEMY)) {
                        setUnknownEnemy(player);
                    } else {
                        players.add(player);
                    }
                }
            } else if (in.getLocalName().equals(Map.getXMLElementTagName())) {
                String mapId = in.getAttributeValue(null, "ID");
                map = (Map) getFreeColGameObject(mapId);
                if (map != null) {
                    map.readFromXML(in);
                } else {
                    map = new Map(this, mapId);
                    map.readFromXML(in);
                }
            } else if (in.getLocalName().equals(ModelMessage.getXMLElementTagName())) {
                
                ModelMessage m = new ModelMessage();
                m.readFromXML(in);
                
                String owner = m.getOwnerId();
                if (owner != null) {
                    Player player = (Player) getFreeColGameObjectSafely(owner);
                    player.addModelMessage(m);
                }
            } else if (in.getLocalName().equals("citiesOfCibola")) {
                
                citiesOfCibola = readFromListElement("citiesOfCibola", in, String.class);
            } else if (CIBOLA_TAG.equals(in.getLocalName())) {
                citiesOfCibola.add(in.getAttributeValue(null, ID_ATTRIBUTE_TAG));
                in.nextTag();
            } else if (DifficultyLevel.getXMLElementTagName().equals(in.getLocalName())) {
                difficultyLevel = new DifficultyLevel();
                difficultyLevel.readFromXML(in, null);
            } else {
                logger.warning("Unknown tag: " + in.getLocalName() + " loading game");
                in.nextTag();
            }
        }
        
        if (!in.getLocalName().equals(Game.getXMLElementTagName())) {
            logger.warning("Error parsing xml: expecting closing tag </" + Game.getXMLElementTagName() + "> "+
                           "found instead: " +in.getLocalName());
        }
        
        if (gameOptions == null) {
            gameOptions = new GameOptions();
        }
    }

    
    public static String getXMLElementTagName() {
        return "game";
    }
    
    
    public boolean equals(Object o) {
        return this == o;
    }
    
}
