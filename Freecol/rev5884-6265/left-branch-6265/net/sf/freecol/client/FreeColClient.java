

package net.sf.freecol.client;

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.Random;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.SwingUtilities;

import net.sf.freecol.FreeCol;
import net.sf.freecol.client.control.ClientModelController;
import net.sf.freecol.client.control.ConnectController;
import net.sf.freecol.client.control.InGameController;
import net.sf.freecol.client.control.InGameInputHandler;
import net.sf.freecol.client.control.MapEditorController;
import net.sf.freecol.client.control.PreGameController;
import net.sf.freecol.client.control.PreGameInputHandler;
import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.FreeColMenuBar;
import net.sf.freecol.client.gui.FullScreenFrame;
import net.sf.freecol.client.gui.GUI;
import net.sf.freecol.client.gui.ImageLibrary;
import net.sf.freecol.client.gui.WindowedFrame;
import net.sf.freecol.client.gui.action.ActionManager;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.client.gui.sound.MusicLibrary;
import net.sf.freecol.client.gui.sound.SfxLibrary;
import net.sf.freecol.client.gui.sound.SoundLibrary;
import net.sf.freecol.client.gui.sound.SoundPlayer;
import net.sf.freecol.client.networking.Client;
import net.sf.freecol.common.PseudoRandom;
import net.sf.freecol.common.io.FreeColModFile;
import net.sf.freecol.common.io.FreeColModFile.ModInfo;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.networking.Message;
import net.sf.freecol.common.option.AudioMixerOption;
import net.sf.freecol.common.option.LanguageOption;
import net.sf.freecol.common.option.ListOption;
import net.sf.freecol.common.option.Option;
import net.sf.freecol.common.option.PercentageOption;
import net.sf.freecol.common.option.LanguageOption.Language;
import net.sf.freecol.common.resources.ResourceManager;
import net.sf.freecol.common.resources.ResourceMapping;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.FreeColServer.GameState;

import org.w3c.dom.Element;


public final class FreeColClient {

    private static final Logger logger = Logger.getLogger(FreeColClient.class.getName());

    
    private ConnectController connectController;

    private PreGameController preGameController;

    private PreGameInputHandler preGameInputHandler;

    private InGameController inGameController;

    private InGameInputHandler inGameInputHandler;

    private ClientModelController modelController;
    
    private MapEditorController mapEditorController;
    

    
    private GraphicsDevice gd;

    private JFrame frame;

    private Canvas canvas;

    private GUI gui;

    private ImageLibrary imageLibrary;

    private MusicLibrary musicLibrary;

    private SfxLibrary sfxLibrary;

    private SoundPlayer musicPlayer;

    private SoundPlayer sfxPlayer;

    
    
    private Client client;

    
    private Game game;

    private final PseudoRandom _random = new ClientPseudoRandom();

    
    private Player player;

    
    private FreeColServer freeColServer = null;

    private boolean windowed;
    
    private boolean mapEditor;

    private boolean singleplayer;

    private final ActionManager actionManager;
    
    private ClientOptions clientOptions;

    public final Worker worker;

    
    private boolean loggedIn = false;
    
    private Rectangle windowBounds;

    
    private boolean headless;


    
    public FreeColClient(boolean windowed, final Dimension innerWindowSize, 
                         ImageLibrary imageLibrary, MusicLibrary musicLibrary,
                         SfxLibrary sfxLibrary, final boolean showOpeningVideo) {
        headless = "true".equals(System.getProperty("java.awt.headless", "false"));
        this.windowed = windowed;
        this.imageLibrary = imageLibrary;
        this.musicLibrary = musicLibrary;
        this.sfxLibrary = sfxLibrary;
        
        mapEditor = false;
        
        clientOptions = new ClientOptions();
        if (FreeCol.getClientOptionsFile() != null
                && FreeCol.getClientOptionsFile().exists()) {
            clientOptions.load(FreeCol.getClientOptionsFile());
        }
        
        
        final List<ModInfo> mods = new ArrayList<ModInfo>();
        final List<ResourceMapping> modResources = new ArrayList<ResourceMapping>(mods.size());
        for (Object object : ((ListOption) getClientOptions()
                              .getObject(ClientOptions.USER_MODS)).getValue()) {
            mods.add((ModInfo) object);
            modResources.add(new FreeColModFile((ModInfo) object).getResourceMapping());
        }
        ResourceManager.setModMappings(modResources);
        ResourceManager.preload(innerWindowSize);
        
        actionManager = new ActionManager(this);
        if (!headless) {
            actionManager.initializeActions();
        }
        
        connectController = new ConnectController(this);
        preGameController = new PreGameController(this);
        preGameInputHandler = new PreGameInputHandler(this);
        inGameController = new InGameController(this);
        inGameInputHandler = new InGameInputHandler(this);
        modelController = new ClientModelController(this);
        mapEditorController = new MapEditorController(this);
        
        
        if (!headless) {
            SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        startGUI(innerWindowSize, showOpeningVideo);
                    }
                });
        }
        worker = new Worker();
        worker.start();
        
        if (FreeCol.getClientOptionsFile() != null
                && FreeCol.getClientOptionsFile().exists()) {
            if (!headless) {
                Option o = clientOptions.getObject(ClientOptions.LANGUAGE);
                o.addPropertyChangeListener(new PropertyChangeListener() {
                        public void propertyChange(PropertyChangeEvent e) {
                            if (((Language) e.getNewValue()).getKey().equals(LanguageOption.AUTO)) {
                                canvas.showInformationMessage("autodetectLanguageSelected");
                            } else {
                                Locale l = ((Language) e.getNewValue()).getLocale();
                                Messages.setMessageBundle(l);
                                canvas.showInformationMessage("newLanguageSelected", "%language%", l.getDisplayName());
                            }
                        }
                    });
            }
        }
    }

    
    private void startGUI(Dimension innerWindowSize, final boolean showOpeningVideo) {
        final AudioMixerOption amo = (AudioMixerOption) getClientOptions().getObject(ClientOptions.AUDIO_MIXER);
        if (musicLibrary != null) {
            musicPlayer = new SoundPlayer(amo,
                    (PercentageOption) getClientOptions().getObject(ClientOptions.MUSIC_VOLUME),
                    false,
                    true);
            
        } else {
            musicPlayer = null;
        }
        if (sfxLibrary != null) {
            sfxPlayer = new SoundPlayer(amo,
                    (PercentageOption) getClientOptions().getObject(ClientOptions.SFX_VOLUME),
                    true,
                    false);
        } else {
            sfxPlayer = null;
        }
        
        if (GraphicsEnvironment.isHeadless()) {
            logger.info("It seems that the GraphicsEnvironment is headless!");
        }
        gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        if (!windowed) {
            if (!gd.isFullScreenSupported()) {
                String fullscreenNotSupported = "\nIt seems that full screen mode is not fully supported for this\nGraphicsDevice. Please try the \"--windowed\" option if you\nexperience any graphical problems while running FreeCol.";
                logger.info(fullscreenNotSupported);
                System.out.println(fullscreenNotSupported);
                
            }
            Rectangle bounds = gd.getDefaultConfiguration().getBounds();
            innerWindowSize = new Dimension(bounds.width - bounds.x, bounds.height - bounds.y);
        }
        gui = new GUI(this, innerWindowSize, imageLibrary);
        canvas = new Canvas(this, innerWindowSize, gui);
        changeWindowedMode(windowed);

        frame.setIconImage(ResourceManager.getImage("FrameIcon.image"));
        if (showOpeningVideo) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    canvas.showOpeningVideoPanel();
                }
            });
        } else {
            canvas.showMainPanel();
            playMusic("intro");
        }
        gui.startCursorBlinking();
    }

    
    public boolean isHeadless() {
        return headless;
    }

    
    public void setHeadless(final boolean newHeadless) {
        this.headless = newHeadless;
    }
    
    
    public boolean canSaveCurrentGame(){
        if (getFreeColServer() == null) {
            return false;
        } else if (getMyPlayer() == null) {
            return false;
        } else if (getFreeColServer().getGameState() == GameState.IN_GAME
                   && !getMyPlayer().isAdmin()) {
            return false;
        }
        return true;
    }

    
    public JFrame getFrame() {
        return frame;
    }

    
    public void updateMenuBar() {
        if (frame != null && frame.getJMenuBar() != null) {
            ((FreeColMenuBar) frame.getJMenuBar()).update();
        }
    }

    
    public void changeWindowedMode(boolean windowed) {
        JMenuBar menuBar = null;
        if (frame != null) {
            menuBar = frame.getJMenuBar();
            if (frame instanceof WindowedFrame) {
                windowBounds = frame.getBounds();
            }
            frame.setVisible(false);
            frame.dispose();
        }
        this.windowed = windowed;
        if (windowed) {
            frame = new WindowedFrame();
        } else {
            frame = new FullScreenFrame(gd);
        }
        frame.setJMenuBar(menuBar);
        if (frame instanceof WindowedFrame) {
            ((WindowedFrame) frame).setCanvas(canvas);
            frame.getContentPane().add(canvas);
            if (windowBounds != null) {
                frame.setBounds(windowBounds);
            } else {
                frame.pack();
            }
        } else if (frame instanceof FullScreenFrame) {
            ((FullScreenFrame) frame).setCanvas(canvas);
            frame.getContentPane().add(canvas);
        }
        gui.forceReposition();
        canvas.updateSizes();
        frame.setVisible(true);
    }
    
    
    public boolean isWindowed() {
        return windowed;
    }

    
    public void saveClientOptions() {
        saveClientOptions(FreeCol.getClientOptionsFile());
    }

    public void setMapEditor(boolean mapEditor) {
        this.mapEditor = mapEditor;
    }
    
    public boolean isMapEditor() {
        return mapEditor;
    }
    
    
    public void saveClientOptions(File saveFile) {
        getClientOptions().save(saveFile);
    }

    
    public ImageLibrary getImageLibrary() {
        return imageLibrary;
    }

    
    public void loadClientOptions() {
        loadClientOptions(FreeCol.getClientOptionsFile());
    }

    
    public void loadClientOptions(File loadFile) {
        getClientOptions().load(loadFile);
    }

    
    public ActionManager getActionManager() {
        return actionManager;
    }

    
    public ClientOptions getClientOptions() {
        return clientOptions;
    }
    
    public MapEditorController getMapEditorController() {
        return mapEditorController;
    }

    
    public Player getMyPlayer() {
        return player;
    }

    
    public void setMyPlayer(Player player) {
        this.player = player;
    }

    
    public void setFreeColServer(FreeColServer freeColServer) {
        this.freeColServer = freeColServer;
    }

    
    public FreeColServer getFreeColServer() {
        return freeColServer;
    }

    
    public void setGame(Game game) {
        this.game = game;
    }

    
    public Game getGame() {
        return game;
    }

    
    public Canvas getCanvas() {
        return canvas;
    }

    
    public GUI getGUI() {
        return gui;
    }

    
    public void quit() {
        getConnectController().quitGame(true);
        if (!windowed) {
            try {
                gd.setFullScreenWindow(null);
            } catch(Exception e) {
                
                
            }
        }
        System.exit(0);
    }

    
    public boolean retire() {
        Element retireElement = Message.createNewRootElement("retire");
        Element reply = client.ask(retireElement);
        return ("true".equals(reply.getAttribute("highScore")));
    }


    
    public void continuePlaying() {
        Element continueElement = Message.createNewRootElement("continuePlaying");
        client.send(continueElement);
    }

    
    public boolean isAdmin() {
        if (getMyPlayer() == null) {
            return false;
        }
        return getMyPlayer().isAdmin();
    }

    
    public void setSingleplayer(boolean singleplayer) {
        this.singleplayer = singleplayer;
    }

    
    public boolean isSingleplayer() {
        return singleplayer;
    }

    
    public ConnectController getConnectController() {
        return connectController;
    }

    
    public PreGameController getPreGameController() {
        return preGameController;
    }

    
    public PreGameInputHandler getPreGameInputHandler() {
        return preGameInputHandler;
    }

    
    public InGameController getInGameController() {
        return inGameController;
    }

    
    public InGameInputHandler getInGameInputHandler() {
        return inGameInputHandler;
    }

    
    public ClientModelController getModelController() {
        return modelController;
    }

    
    public void setClient(Client client) {
        this.client = client;
    }

    
    public Client getClient() {
        return client;
    }

    
    public void playMusic(String music) {
        if (musicPlayer != null) {
            musicPlayer.play(musicLibrary.get(music));
        }
    }
    
    
    public void playMusicOnce(String music) {
        if (musicPlayer != null) {
            musicPlayer.playOnce(musicLibrary.get(music));
        }
    }
    
    
    public void playMusicOnce(String music, int delay) {
        if (musicPlayer != null) {
            musicPlayer.playOnce(musicLibrary.get(music), delay);
        }
    }
    
    
    public void playSound(String sound) {
        if (sfxPlayer != null) {
            sfxPlayer.play(sfxLibrary.get(sound));
        }
    }

    
    public void playSound(SoundLibrary.SoundEffect sound) {
        if (sfxPlayer != null) {
            sfxPlayer.play(sfxLibrary.get(sound));
        }
    }
    
    
    public boolean canPlayMusic(){
        return musicPlayer != null;
    }

    
    public boolean isLoggedIn() {
        return loggedIn;
    }

    
    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    
    public PseudoRandom getPseudoRandom() {
        return _random;
    }


    
    private class ClientPseudoRandom implements PseudoRandom {
        ClientPseudoRandom() {
            values = new LinkedList<Integer>();
            offlineRandom = new Random();
        }

        
        public int nextInt(int n) {
            if (n <= 0) {
                throw new IllegalArgumentException("n must be positive!");
            }
            
            
            
            
            return Math.abs(nextInt() % n);
        }

        
        private int nextInt() {
            Integer i = pop();
            while (i == null) {
                getNewNumbers();
                i = pop();
            }
            return i.intValue();
        }

        
        private void getNewNumbers() {
            int valuesAdded = 0;
            if (isLoggedIn()) {
                Element query = Message.createNewRootElement("getRandomNumbers");
                query.setAttribute("n", String.valueOf(VALUES_PER_CALL));
                
                Element answer = getClient().ask(query);
                if (answer != null && "getRandomNumbersConfirmed".equals(answer.getTagName())) {
                    for (String s : answer.getAttribute("result").split(",")) {
                        push(new Integer(s));
                        ++valuesAdded;
                    }
                } else {
                    logger.warning("Expected getRandomNumbersConfirmed, got "
                            + (answer != null ? answer.getTagName() : "null"));
                }
            }
            
            if (valuesAdded < 1) {
                logger.fine("Generating random number on client side");
                push(Integer.valueOf(offlineRandom.nextInt()));
            }
        }

        private synchronized void push(Integer i) {
            values.offer(i);
        }

        private synchronized Integer pop() {
            return values.poll();
        }


        private final Random offlineRandom;

        private final Queue<Integer> values;

        private static final int VALUES_PER_CALL = 100;
    }
}

