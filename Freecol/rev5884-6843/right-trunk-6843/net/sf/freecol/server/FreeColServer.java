

package net.sf.freecol.server;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.logging.Logger;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.FreeCol;
import net.sf.freecol.common.FreeColException;
import net.sf.freecol.common.PseudoRandom;
import net.sf.freecol.common.io.FreeColSavegameFile;
import net.sf.freecol.common.model.FreeColGameObject;
import net.sf.freecol.common.model.DifficultyLevel;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.GameOptions;
import net.sf.freecol.common.model.HighScore;
import net.sf.freecol.common.model.IndianSettlement;
import net.sf.freecol.common.model.Nation;
import net.sf.freecol.common.model.NationOptions;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Settlement;
import net.sf.freecol.common.model.Specification;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.UnitType;
import net.sf.freecol.common.model.Player.PlayerType;
import net.sf.freecol.common.model.Unit.UnitState;
import net.sf.freecol.common.networking.Connection;
import net.sf.freecol.common.networking.Message;
import net.sf.freecol.common.networking.NoRouteToServerException;
import net.sf.freecol.common.util.XMLStream;
import net.sf.freecol.server.ai.AIInGameInputHandler;
import net.sf.freecol.server.ai.AIMain;
import net.sf.freecol.server.control.Controller;
import net.sf.freecol.server.control.InGameController;
import net.sf.freecol.server.control.InGameInputHandler;
import net.sf.freecol.server.control.PreGameController;
import net.sf.freecol.server.control.PreGameInputHandler;
import net.sf.freecol.server.control.ServerModelController;
import net.sf.freecol.server.control.UserConnectionHandler;
import net.sf.freecol.server.generator.IMapGenerator;
import net.sf.freecol.server.generator.MapGenerator;
import net.sf.freecol.server.model.ServerGame;
import net.sf.freecol.server.model.ServerModelObject;
import net.sf.freecol.server.model.ServerPlayer;
import net.sf.freecol.server.networking.DummyConnection;
import net.sf.freecol.server.networking.Server;

import org.w3c.dom.Element;


public final class FreeColServer {

    private static final Logger logger = Logger.getLogger(FreeColServer.class.getName());

    private static final int META_SERVER_UPDATE_INTERVAL = 60000;

    private static final int NUMBER_OF_HIGH_SCORES = 10;
    private static final String HIGH_SCORE_FILE = "HighScores.xml";

    
    public static final int SAVEGAME_VERSION = 8;

    
    public static final int MINIMUM_SAVEGAME_VERSION = 1;

    
    public static enum GameState {STARTING_GAME, IN_GAME, ENDING_GAME}

    
    private GameState gameState = GameState.STARTING_GAME;

    
    private Server server;

    
    private final UserConnectionHandler userConnectionHandler;

    private final PreGameController preGameController;

    private final PreGameInputHandler preGameInputHandler;

    private final InGameInputHandler inGameInputHandler;

    private final ServerModelController modelController;

    private final InGameController inGameController;

    private ServerGame game;

    private AIMain aiMain;

    private IMapGenerator mapGenerator;

    private boolean singleplayer;

    
    private String owner;

    private boolean publicServer = false;

    private final int port;

    
    private String name;

    
    private final ServerPseudoRandom _pseudoRandom = new ServerPseudoRandom();

    
    private boolean integrity = false;

    
    private List<HighScore> highScores = null;


    public static final Comparator<HighScore> highScoreComparator = new Comparator<HighScore>() {
        public int compare(HighScore score1, HighScore score2) {
            return score2.getScore() - score1.getScore();
        }
    };


    
    public FreeColServer(boolean publicServer, boolean singleplayer, int port, String name)
        throws IOException, NoRouteToServerException {
        this(publicServer, singleplayer, port, name, NationOptions.getDefaults(),
             FreeCol.getSpecification().getDifficultyLevel("model.difficulty.medium"));
    }

    public FreeColServer(boolean publicServer, boolean singleplayer, int port, String name,
                         NationOptions nationOptions, DifficultyLevel level)
        throws IOException, NoRouteToServerException {
        this.publicServer = publicServer;
        this.singleplayer = singleplayer;
        this.port = port;
        this.name = name;

        modelController = new ServerModelController(this);
        game = new ServerGame(modelController);
        game.setNationOptions(nationOptions);
        game.setDifficultyLevel(level);
        FreeCol.getSpecification().applyDifficultyLevel(level);
        mapGenerator = new MapGenerator();
        userConnectionHandler = new UserConnectionHandler(this);
        preGameController = new PreGameController(this);
        preGameInputHandler = new PreGameInputHandler(this);
        inGameInputHandler = new InGameInputHandler(this);
        inGameController = new InGameController(this);
        try {
            server = new Server(this, port);
            server.start();
        } catch (IOException e) {
            logger.warning("Exception while starting server: " + e);
            throw e;
        }
        updateMetaServer(true);
        startMetaServerUpdateThread();
    }

    
    public FreeColServer(final FreeColSavegameFile savegame, boolean publicServer, 
                         boolean singleplayer, int port, String name)
            throws IOException, FreeColException, NoRouteToServerException {
        this.publicServer = publicServer;
        this.singleplayer = singleplayer;
        this.port = port;
        this.name = name;
        

        mapGenerator = new MapGenerator();
        modelController = new ServerModelController(this);
        userConnectionHandler = new UserConnectionHandler(this);
        preGameController = new PreGameController(this);
        preGameInputHandler = new PreGameInputHandler(this);
        inGameInputHandler = new InGameInputHandler(this);
        inGameController = new InGameController(this);

        try {
            server = new Server(this, port);
            server.start();
        } catch (IOException e) {
            logger.warning("Exception while starting server: " + e);
            throw e;
        }
        try {
            owner = loadGame(savegame);
        } catch (FreeColException e) {
            server.shutdown();
            throw e;
        } catch (Exception e) {
            server.shutdown();
            FreeColException fe = new FreeColException("couldNotLoadGame");
            fe.initCause(e);
            throw fe;
        }

        
        if (game.getDifficultyLevel() == null) {
            Specification.getSpecification().applyDifficultyLevel("model.difficulty.medium");
        } else {
            Specification.getSpecification().applyDifficultyLevel(game.getDifficultyLevel());
        }

        getModelController().updateModelListening();
        updateMetaServer(true);
        startMetaServerUpdateThread();
    }

    
    public void startMetaServerUpdateThread() {
        if (!publicServer) {
            return;
        }
        Timer t = new Timer(true);
        t.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                try {
                    updateMetaServer();
                } catch (NoRouteToServerException e) {}
            }
        }, META_SERVER_UPDATE_INTERVAL, META_SERVER_UPDATE_INTERVAL);
    }

    
    public void enterRevengeMode(String username) {
        if (!singleplayer) {
            throw new IllegalStateException("Cannot enter revenge mode when not singleplayer.");
        }
        final ServerPlayer p = (ServerPlayer) getGame().getPlayerByName(username);
        synchronized (p) {
            List<UnitType> undeads = FreeCol.getSpecification().getUnitTypesWithAbility("model.ability.undead");
            ArrayList<UnitType> navalUnits = new ArrayList<UnitType>();
            ArrayList<UnitType> landUnits = new ArrayList<UnitType>();
            for (UnitType undead : undeads) {
                if (undead.hasAbility("model.ability.navalUnit")) {
                    navalUnits.add(undead);
                } else if (undead.getId().equals("model.unit.revenger")) { 
                    landUnits.add(undead);
                }
            }
            if (navalUnits.size() > 0) {
                UnitType navalType = navalUnits.get(getPseudoRandom().nextInt(navalUnits.size()));
                Unit theFlyingDutchman = new Unit(game, p.getEntryLocation(), p, navalType, UnitState.ACTIVE);
                if (landUnits.size() > 0) {
                    UnitType landType = landUnits.get(getPseudoRandom().nextInt(landUnits.size()));
                    new Unit(game, theFlyingDutchman, p, landType, UnitState.SENTRY);
                }
                p.setDead(false);
                p.setPlayerType(PlayerType.UNDEAD);
                p.setColor(Color.BLACK);
                Element updateElement = Message.createNewRootElement("update");
                updateElement.appendChild(((FreeColGameObject) p.getEntryLocation()).toXMLElement(p, updateElement
                        .getOwnerDocument()));
                updateElement.appendChild(p.toXMLElement(p, updateElement.getOwnerDocument()));
                try {
                    p.getConnection().sendAndWait(updateElement);
                } catch (IOException e) {
                    logger.warning("Could not send update");
                }
            }
        }
    }

    
    public IMapGenerator getMapGenerator() {
        return mapGenerator;
    }
    
    
    public void setMapGenerator(IMapGenerator mapGenerator) {
        this.mapGenerator = mapGenerator;
    }

    
    public void updateMetaServer() throws NoRouteToServerException {
        updateMetaServer(false);
    }

    
    public String getName() {
        return name;
    }

    
    public void setName(String name) {
        this.name = name;
    }

    
    public void updateMetaServer(boolean firstTime) throws NoRouteToServerException {
        if (!publicServer) {
            return;
        }
        Connection mc;
        try {
            mc = new Connection(FreeCol.META_SERVER_ADDRESS, FreeCol.META_SERVER_PORT, null, FreeCol.SERVER_THREAD);
        } catch (IOException e) {
            logger.warning("Could not connect to meta-server.");
            return;
        }
        try {
            Element element;
            if (firstTime) {
                element = Message.createNewRootElement("register");
            } else {
                element = Message.createNewRootElement("update");
            }
            
            if (name != null) {
                element.setAttribute("name", name);
            } else {
                element.setAttribute("name", mc.getSocket().getLocalAddress().getHostAddress() + ":"
                        + Integer.toString(port));
            }
            element.setAttribute("port", Integer.toString(port));
            element.setAttribute("slotsAvailable", Integer.toString(getSlotsAvailable()));
            element.setAttribute("currentlyPlaying", Integer.toString(getNumberOfLivingHumanPlayers()));
            element.setAttribute("isGameStarted", Boolean.toString(gameState != GameState.STARTING_GAME));
            element.setAttribute("version", FreeCol.getVersion());
            element.setAttribute("gameState", Integer.toString(getGameState().ordinal()));
            Element reply = mc.ask(element);
            if (reply != null && reply.getTagName().equals("noRouteToServer")) {
                throw new NoRouteToServerException();
            }
        } catch (IOException e) {
            logger.warning("Network error while communicating with the meta-server.");
            return;
        } finally {
            try {
                
                mc.close();
            } catch (IOException e) {
                logger.warning("Could not close connection to meta-server.");
                return;
            }
        }
    }

    
    public void removeFromMetaServer() {
        if (!publicServer) {
            return;
        }
        Connection mc;
        try {
            mc = new Connection(FreeCol.META_SERVER_ADDRESS, FreeCol.META_SERVER_PORT, null, FreeCol.SERVER_THREAD);
        } catch (IOException e) {
            logger.warning("Could not connect to meta-server.");
            return;
        }
        try {
            Element element = Message.createNewRootElement("remove");
            element.setAttribute("port", Integer.toString(port));
            mc.send(element);
        } catch (IOException e) {
            logger.warning("Network error while communicating with the meta-server.");
            return;
        } finally {
            try {
                
                mc.close();
            } catch (IOException e) {
                logger.warning("Could not close connection to meta-server.");
                return;
            }
        }
    }

    
    public int getSlotsAvailable() {
        List<Player> players = game.getPlayers();
        int n = 0;
        for (int i = 0; i < players.size(); i++) {
            ServerPlayer p = (ServerPlayer) players.get(i);
            if (!p.isEuropean() || p.isREF()) {
                continue;
            }
            if (!(p.isDead() || p.isConnected() && !p.isAI())) {
                n++;
            }
        }
        return n;
    }

    
    public int getNumberOfLivingHumanPlayers() {
        List<Player> players = game.getPlayers();
        int n = 0;
        for (int i = 0; i < players.size(); i++) {
            if (!((ServerPlayer) players.get(i)).isAI() && !((ServerPlayer) players.get(i)).isDead()
                    && ((ServerPlayer) players.get(i)).isConnected()) {
                n++;
            }
        }
        return n;
    }

    
    public String getOwner() {
        return owner;
    }

    
    public void saveGame(File file, String username) throws IOException {
        final Game game = getGame();
        XMLOutputFactory xof = XMLOutputFactory.newInstance();
        JarOutputStream fos = null;
        try {
            XMLStreamWriter xsw;
            fos = new JarOutputStream(new FileOutputStream(file));
            fos.putNextEntry(new JarEntry("specification.xml"));
            InputStream in = FreeCol.getSpecificationInputStream();
            int len;
            byte[] buf = new byte[1024];
            while ((len = in.read(buf)) > 0) {
                fos.write(buf, 0, len);
            }
            fos.closeEntry();
            in.close();


            fos.putNextEntry(new JarEntry(FreeColSavegameFile.SAVEGAME_FILE));
            xsw = xof.createXMLStreamWriter(fos, "UTF-8");

            xsw.writeStartDocument("UTF-8", "1.0");
            xsw.writeComment("Game version: "+FreeCol.getRevision());
            xsw.writeStartElement("savedGame");
            
            
            xsw.writeAttribute("owner", username);
            xsw.writeAttribute("publicServer", Boolean.toString(publicServer));
            xsw.writeAttribute("singleplayer", Boolean.toString(singleplayer));
            xsw.writeAttribute("version", Integer.toString(SAVEGAME_VERSION));
            xsw.writeAttribute("randomState", _pseudoRandom.getState());
            
            xsw.writeStartElement("serverObjects");
            Iterator<FreeColGameObject> fcgoIterator = game.getFreeColGameObjectIterator();
            while (fcgoIterator.hasNext()) {
                FreeColGameObject fcgo = fcgoIterator.next();
                if (fcgo instanceof ServerModelObject) {
                    ((ServerModelObject) fcgo).toServerAdditionElement(xsw);
                }
            }
            xsw.writeEndElement();
            
            game.toSavedXML(xsw);
            
            if (aiMain != null) {
                aiMain.toXML(xsw);
            }
            xsw.writeEndElement();
            xsw.writeEndDocument();
            xsw.flush();
            xsw.close();
        } catch (XMLStreamException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.warning(sw.toString());
            throw new IOException("XMLStreamException.");
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.warning(sw.toString());
            throw new IOException(e.toString());
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                
            }
        }
    }

    
    public static XMLStream createXMLStreamReader(FreeColSavegameFile fis) throws IOException {
        return new XMLStream(fis.getSavegameInputStream());
    }

    
    public String loadGame(final FreeColSavegameFile fis) throws IOException, FreeColException {
        boolean doNotLoadAI = false;
        XMLStream xs = null;
        try {
            xs = createXMLStreamReader(fis);
            final XMLStreamReader xsr = xs.getXMLStreamReader();
            xsr.nextTag();
            
            checkSavegameVersion(xsr);
            
            String randomState = xsr.getAttributeValue(null, "randomState");
            if (randomState != null && randomState.length() > 0) {
                try {
                    _pseudoRandom.restoreState(randomState);
                } catch (IOException e) {
                    logger.warning("Failed to restore random state, ignoring!");
                }
            }
            final String owner = xsr.getAttributeValue(null, "owner");
            ArrayList<Object> serverObjects = null;
            aiMain = null;
            while (xsr.nextTag() != XMLStreamConstants.END_ELEMENT) {
                if (xsr.getLocalName().equals("serverObjects")) {
                    
                    serverObjects = new ArrayList<Object>();
                    while (xsr.nextTag() != XMLStreamConstants.END_ELEMENT) {
                        if (xsr.getLocalName().equals(ServerPlayer.getServerAdditionXMLElementTagName())) {
                            serverObjects.add(new ServerPlayer(xsr));
                        } else {
                            throw new XMLStreamException("Unknown tag: " + xsr.getLocalName());
                        }
                    }
                } else if (xsr.getLocalName().equals(Game.getXMLElementTagName())) {
                    
                    game = new ServerGame(null, getModelController(), xsr, serverObjects
                            .toArray(new FreeColGameObject[0]));
                    game.setCurrentPlayer(null);
                    gameState = GameState.IN_GAME;
                    integrity = game.checkIntegrity();
                } else if (xsr.getLocalName().equals(AIMain.getXMLElementTagName())) {
                    if (doNotLoadAI) {
                        aiMain = new AIMain(this);
                        game.setFreeColGameObjectListener(aiMain);
                        break;
                    }
                    
                    aiMain = new AIMain(this, xsr);
                    if (!aiMain.checkIntegrity()) {
                        aiMain = new AIMain(this);
                        logger.info("Replacing AIMain.");
                    }
                    game.setFreeColGameObjectListener(aiMain);
                } else if (xsr.getLocalName().equals("marketdata")) {
                    logger.info("Ignoring market data for compatibility.");
                } else {
                    throw new XMLStreamException("Unknown tag: " + xsr.getLocalName());
                }
            }
            Collections.sort(game.getPlayers(), Player.playerComparator);
            if (aiMain == null) {
                aiMain = new AIMain(this);
                game.setFreeColGameObjectListener(aiMain);
            }
            
            Iterator<Player> playerIterator = game.getPlayerIterator();
            while (playerIterator.hasNext()) {
                ServerPlayer player = (ServerPlayer) playerIterator.next();
                if (player.isAI()) {
                    DummyConnection theConnection = new DummyConnection(
                            "Server-Server-" + player.getName(),
                            getInGameInputHandler());
                    DummyConnection aiConnection = new DummyConnection(
                            "Server-AI-" + player.getName(),                            
                            new AIInGameInputHandler(this, player, aiMain));
                    aiConnection.setOutgoingMessageHandler(theConnection);
                    theConnection.setOutgoingMessageHandler(aiConnection);
                    getServer().addDummyConnection(theConnection);
                    player.setConnection(theConnection);
                    player.setConnected(true);
                }
            }
            xs.close();
            
            return owner;
        } catch (XMLStreamException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.warning(sw.toString());
            throw new IOException("XMLStreamException.");
        } catch (FreeColException fe) {
            StringWriter sw = new StringWriter();
            fe.printStackTrace(new PrintWriter(sw));
            logger.warning(sw.toString());
            throw fe;
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.warning(sw.toString());
            throw new IOException(e.toString());
        } finally {
            xs.close();
        }
    }

	public static void checkSavegameVersion(final XMLStreamReader xsr)
			throws FreeColException {
		final String version = xsr.getAttributeValue(null, "version");
		int savegameVersion = 0;
		try {
		    savegameVersion = Integer.parseInt(version);
		} catch(Exception e) {
		    throw new FreeColException("incompatibleVersions");
		}
		if (savegameVersion < MINIMUM_SAVEGAME_VERSION) {
		    throw new FreeColException("incompatibleVersions");
		}
	}

    
    public void setSingleplayer(boolean singleplayer) {
        this.singleplayer = singleplayer;
    }

    
    public boolean isSingleplayer() {
        return singleplayer;
    }

    
    public void revealMapForAllPlayers() {
        Iterator<Player> playerIterator = getGame().getPlayerIterator();
        while (playerIterator.hasNext()) {
            ServerPlayer player = (ServerPlayer) playerIterator.next();
            player.revealMap();
        }
        playerIterator = getGame().getPlayerIterator();
        while (playerIterator.hasNext()) {
            ServerPlayer player = (ServerPlayer) playerIterator.next();
            Element reconnect = Message.createNewRootElement("reconnect");
            try {
                player.getConnection().send(reconnect);
            } catch (IOException ex) {
                logger.warning("Could not send reconnect message!");
            }
        }
    }

    
    public ServerPlayer getPlayer(Connection connection) {
        Iterator<Player> playerIterator = getGame().getPlayerIterator();
        while (playerIterator.hasNext()) {
            ServerPlayer player = (ServerPlayer) playerIterator.next();
            if (player.getConnection() == connection) {
                return player;
            }
        }
        return null;
    }

    
    public UserConnectionHandler getUserConnectionHandler() {
        return userConnectionHandler;
    }

    
    public Controller getController() {
        if (getGameState() == GameState.IN_GAME) {
            return inGameController;
        } else {
            return preGameController;
        }
    }

    
    public PreGameInputHandler getPreGameInputHandler() {
        return preGameInputHandler;
    }

    
    public InGameInputHandler getInGameInputHandler() {
        return inGameInputHandler;
    }

    
    public InGameController getInGameController() {
        return inGameController;
    }

    
    public ServerModelController getModelController() {
        return modelController;
    }

    
    public ServerGame getGame() {
        return game;
    }

    
    public void setAIMain(AIMain aiMain) {
        this.aiMain = aiMain;
    }

    
    public AIMain getAIMain() {
        return aiMain;
    }

    
    public GameState getGameState() {
        return gameState;
    }

    
    public void setGameState(GameState state) {
        gameState = state;
    }

    
    public Server getServer() {
        return server;
    }

    
    public boolean getIntegrity() {
        return integrity;
    }

    
    public PseudoRandom getPseudoRandom() {
        return _pseudoRandom;
    }

    
    public int[] getRandomNumbers(int n) {
        return _pseudoRandom.getRandomNumbers(n);
    }


    
    private static class ServerPseudoRandom implements PseudoRandom {
        private static final String HEX_DIGITS = "0123456789ABCDEF";

        private Random _random;


        
        public ServerPseudoRandom() {
            _random = new Random(new SecureRandom().nextLong());
        }

        
        public synchronized int nextInt(int n) {
            return _random.nextInt(n);
        }

        
        public synchronized int[] getRandomNumbers(int size) {
            int[] numbers = new int[size];
            for (int i = 0; i < size; i++) {
                numbers[i] = _random.nextInt();
            }
            return numbers;
        }

        
        public synchronized String getState() {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try {
                ObjectOutputStream oos = new ObjectOutputStream(bos);
                oos.writeObject(_random);
                oos.flush();
            } catch (IOException e) {
                throw new IllegalStateException("IO exception in memory!?!", e);
            }
            byte[] bytes = bos.toByteArray();
            StringBuffer sb = new StringBuffer(bytes.length * 2);
            for (byte b : bytes) {
                sb.append(HEX_DIGITS.charAt((b >> 4) & 0x0F));
                sb.append(HEX_DIGITS.charAt(b & 0x0F));
            }
            return sb.toString();
        }

        
        public synchronized void restoreState(String state) throws IOException {
            byte[] bytes = new byte[state.length() / 2];
            int pos = 0;
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = (byte) HEX_DIGITS.indexOf(state.charAt(pos++));
                bytes[i] <<= 4;
                bytes[i] |= (byte) HEX_DIGITS.indexOf(state.charAt(pos++));
            }
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bis);
            try {
                _random = (Random) ois.readObject();
            } catch (ClassNotFoundException e) {
                throw new IOException("Failed to restore random!");
            }
        }
    }

    
    public Unit getUnitSafely(String unitId, ServerPlayer serverPlayer)
        throws IllegalStateException {
        Game game = serverPlayer.getGame();
        FreeColGameObject obj;
        Unit unit;

        if (unitId == null || unitId.length() == 0) {
            throw new IllegalStateException("ID must not be empty.");
        }
        obj = game.getFreeColGameObjectSafely(unitId);
        if (!(obj instanceof Unit)) {
            throw new IllegalStateException("Not a unit ID: " + unitId);
        }
        unit = (Unit) obj;
        if (unit.getOwner() != serverPlayer) {
            throw new IllegalStateException("Not the owner of unit: " + unitId);
        }
        return unit;
    }

    
    public Settlement getAdjacentSettlementSafely(String settlementId, Unit unit)
        throws IllegalStateException {
        Game game = unit.getOwner().getGame();
        Settlement settlement;

        if (settlementId == null || settlementId.length() == 0) {
            throw new IllegalStateException("ID must not be empty.");
        } else if (!(game.getFreeColGameObject(settlementId) instanceof Settlement)) {
            throw new IllegalStateException("Not a settlement ID: " + settlementId);
        }
        settlement = (Settlement) game.getFreeColGameObject(settlementId);
        if (settlement.getTile() == null) {
            throw new IllegalStateException("Settlement is not on the map: "
                                            + settlementId);
        }
        if (unit.getTile() == null) {
            throw new IllegalStateException("Unit is not on the map: "
                                            + unit.getId());
        }
        if (unit.getTile().getDistanceTo(settlement.getTile()) > 1) {
            throw new IllegalStateException("Unit " + unit.getId()
                                            + " is not adjacent to settlement: " + settlementId);
        }
        if (unit.getOwner() == settlement.getOwner()) {
            throw new IllegalStateException("Unit: " + unit.getId()
                                            + " and settlement: " + settlementId
                                            + " are both owned by player: "
                                            + unit.getOwner().getId());
        }
        return settlement;
    }

    
    public IndianSettlement getAdjacentIndianSettlementSafely(String settlementId, Unit unit)
        throws IllegalStateException {
        Settlement settlement = getAdjacentSettlementSafely(settlementId, unit);
        if (!(settlement instanceof IndianSettlement)) {
            throw new IllegalStateException("Not an indianSettlement: " + settlementId);
        }
        if (!unit.getOwner().hasContacted(settlement.getOwner())) {
            throw new IllegalStateException("Player has not established contact with the "
                                            + settlement.getOwner().getNation());
        }
        return (IndianSettlement) settlement;
    }


    
    public ServerPlayer addAIPlayer(Nation nation) {
        String name = nation.getRulerNameKey();
        DummyConnection theConnection = 
            new DummyConnection("Server connection - " + name, getInGameInputHandler());
        ServerPlayer aiPlayer = 
            new ServerPlayer(getGame(), name, false, true, null, theConnection, nation);
        DummyConnection aiConnection = 
            new DummyConnection("AI connection - " + name,
                                new AIInGameInputHandler(this, aiPlayer, getAIMain()));
            
        aiConnection.setOutgoingMessageHandler(theConnection);
        theConnection.setOutgoingMessageHandler(aiConnection);

        getServer().addDummyConnection(theConnection);

        getGame().addPlayer(aiPlayer);

        
        Element addNewPlayer = Message.createNewRootElement("addPlayer");
        addNewPlayer.appendChild(aiPlayer.toXMLElement(null, addNewPlayer.getOwnerDocument()));
        getServer().sendToAll(addNewPlayer, theConnection);
        return aiPlayer;
    }

    
    public List<HighScore> getHighScores() {
        if (highScores == null) {
            try {
                loadHighScores();
            } catch (Exception e) {
                logger.warning(e.toString());
                highScores = new ArrayList<HighScore>();
            }
        }
        return highScores;
    }

    
    public boolean newHighScore(Player player) {
        getHighScores();
        if (!highScores.isEmpty() && player.getScore() <= highScores.get(highScores.size() - 1).getScore()) {
            return false;
        } else {
            highScores.add(new HighScore(player, new Date()));
            Collections.sort(highScores, highScoreComparator);
            if (highScores.size() == NUMBER_OF_HIGH_SCORES) {
                highScores.remove(NUMBER_OF_HIGH_SCORES - 1);
            }
            return true;
        }
    }

    
    public void saveHighScores() throws IOException {
        if (highScores == null || highScores.isEmpty()) {
            return;
        }
        Collections.sort(highScores, highScoreComparator);
        XMLOutputFactory xof = XMLOutputFactory.newInstance();
        FileOutputStream fos = null;
        try {
            XMLStreamWriter xsw;
            fos = new FileOutputStream(new File(FreeCol.getDataDirectory(), HIGH_SCORE_FILE));

            xsw = xof.createXMLStreamWriter(fos, "UTF-8");
            xsw.writeStartDocument("UTF-8", "1.0");
            xsw.writeStartElement("highScores");
            int count = 0;
            for (HighScore score : highScores) {
                score.toXML(xsw);
                count++;
                if (count == NUMBER_OF_HIGH_SCORES) {
                    break;
                }
            }
            xsw.writeEndElement();
            xsw.writeEndDocument();
            xsw.flush();
            xsw.close();
        } catch (XMLStreamException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.warning(sw.toString());
            throw new IOException("XMLStreamException.");
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.warning(sw.toString());
            throw new IOException(e.toString());
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                
            }
        }
    }

    
    public void loadHighScores() throws IOException, FreeColException {
        highScores = new ArrayList<HighScore>();
        XMLInputFactory xif = XMLInputFactory.newInstance();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(new File(FreeCol.getDataDirectory(), HIGH_SCORE_FILE));
            XMLStreamReader xsr = xif.createXMLStreamReader(fis, "UTF-8");
            xsr.nextTag();
            while (xsr.nextTag() != XMLStreamConstants.END_ELEMENT) {
                if (xsr.getLocalName().equals("highScore")) {
                    highScores.add(new HighScore(xsr));
                }
            }
            xsr.close();
            Collections.sort(highScores, highScoreComparator);
        } catch (XMLStreamException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.warning(sw.toString());
            throw new IOException("XMLStreamException.");
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.warning(sw.toString());
            throw new IOException(e.toString());
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
    }

}
