

package net.sf.freecol.client.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import net.sf.freecol.FreeCol;
import net.sf.freecol.client.gui.panel.ImageProvider;
import net.sf.freecol.common.FreeColException;
import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.FoundingFather;
import net.sf.freecol.common.model.GoodsType;
import net.sf.freecol.common.model.IndianSettlement;
import net.sf.freecol.common.model.Nation;
import net.sf.freecol.common.model.ResourceType;
import net.sf.freecol.common.model.Settlement;
import net.sf.freecol.common.model.Tension;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.TileType;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.UnitType;
import net.sf.freecol.common.model.Map.Direction;
import net.sf.freecol.common.model.Settlement.SettlementType;
import net.sf.freecol.common.model.Unit.Role;
import net.sf.freecol.common.resources.ResourceManager;


public final class ImageLibrary extends ImageProvider {

    private static final Logger logger = Logger.getLogger(ImageLibrary.class.getName());    
    
    public static final int RIVER_STYLES = 81;
    public static final int BEACH_STYLES = 256;
 
    public static final String UNIT_SELECT = "unitSelect.image",
                               DELETE = "delete.image",
                               PLOWED = "model.improvement.Plow.image",
                               TILE_TAKEN = "tileTaken.image",
                               TILE_OWNED_BY_INDIANS = "nativeLand.image",
                               LOST_CITY_RUMOUR = "lostCityRumour.image",
                               DARKNESS = "halo.dark.image";

    public static final int UNIT_BUTTON_WAIT = 0, UNIT_BUTTON_DONE = 1, UNIT_BUTTON_FORTIFY = 2,
            UNIT_BUTTON_SENTRY = 3, UNIT_BUTTON_CLEAR = 4, UNIT_BUTTON_PLOW = 5, UNIT_BUTTON_ROAD = 6,
            UNIT_BUTTON_BUILD = 7, UNIT_BUTTON_DISBAND = 8, UNIT_BUTTON_ZOOM_IN = 9, UNIT_BUTTON_ZOOM_OUT = 10,
            UNIT_BUTTON_COUNT = 11;

    private static final String path = new String("images/"),
        extension = new String(".png"),
        terrainDirectory = new String("terrain/"),
        beachDirectory = new String("beach/"),
        beachName = new String("beach"),
        tileName = new String("center"),
        borderName = new String("border"),
        unexploredDirectory = new String("unexplored/"),
        unexploredName = new String("unexplored"),
        riverDirectory = new String("river/"),
        riverName = new String("river"),
        unitButtonDirectory = new String("order-buttons/"),
        unitButtonName = new String("button");

    private final String dataDirectory;

    private static final String deltaName = "delta_";
    private static final String small = "_small";
    private static final String large = "_large";

    
    private List<ImageIcon> rivers;

    private List<ImageIcon> beaches;

    private Map<String, ImageIcon> terrain1, terrain2, overlay1, overlay2,
        forests, deltas;

    private Map<String, ArrayList<ImageIcon>> border1, border2, coast1, coast2;

    
    private List<ArrayList<ImageIcon>> unitButtons; 

    private EnumMap<Tension.Level, Image> alarmChips;
    private EnumMap<Tension.Level, Image> alarmChipsUnvisited;

    private Map<Color, Image> colorChips;

    private Map<Color, Image> missionChips;

    private Map<Color, Image> expertMissionChips;

    
    private final float scalingFactor;


    
    public ImageLibrary() throws FreeColException {
        
        
        
        
        this("");
    }

    
    public ImageLibrary(String freeColHome) throws FreeColException {
        this.scalingFactor = 1;
        
        
        if ("".equals(freeColHome)) {
            dataDirectory = "data/";
        } else {
            dataDirectory = freeColHome;
        }
        init();
    }

    private ImageLibrary(String dataDirectory, float scalingFactor) {
        this.dataDirectory = dataDirectory;
        this.scalingFactor = scalingFactor;
    }


    
    public void init() throws FreeColException {
        
        boolean doLookup = false;
        if ("data/".equals(dataDirectory)) {
            doLookup = true;
        }
        logger.info("initializing image library");
        GraphicsConfiguration gc = null;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        if (!GraphicsEnvironment.isHeadless()) {
            gc = ge.getDefaultScreenDevice() .getDefaultConfiguration();
        }

        Class<FreeCol> resourceLocator = net.sf.freecol.FreeCol.class;

        loadTerrain(gc, resourceLocator, doLookup);
        loadForests(gc, resourceLocator, doLookup);
        loadBeaches(gc, resourceLocator, doLookup);
        loadRivers(gc, resourceLocator, doLookup);
        loadRiverMouths(gc, resourceLocator, doLookup);
        loadUnitButtons(gc, resourceLocator, doLookup);

        alarmChips = new EnumMap<Tension.Level, Image>(Tension.Level.class);
        alarmChipsUnvisited = new EnumMap<Tension.Level, Image>(Tension.Level.class);
        colorChips = new HashMap<Color, Image>();
        missionChips = new HashMap<Color, Image>();
        expertMissionChips = new HashMap<Color, Image>();
    }

    
    public float getScalingFactor() {
        return scalingFactor;
    }

    
    public ImageLibrary getScaledImageLibrary(float scalingFactor) throws FreeColException {
        ImageLibrary scaledLibrary = new ImageLibrary("", scalingFactor);
        scaledLibrary.beaches = scaleImages(beaches, scalingFactor);
        scaledLibrary.rivers = scaleImages(rivers, scalingFactor);
        scaledLibrary.deltas = scaleImages(deltas, scalingFactor);

        scaledLibrary.terrain1 = scaleImages(terrain1, scalingFactor);
        scaledLibrary.terrain2 = scaleImages(terrain2, scalingFactor);
        scaledLibrary.overlay1 = scaleImages(overlay1, scalingFactor);
        scaledLibrary.overlay2 = scaleImages(overlay2, scalingFactor);
        scaledLibrary.forests = scaleImages(forests, scalingFactor);
        
        scaledLibrary.border1 = scaleImages2(border1, scalingFactor);
        scaledLibrary.border2 = scaleImages2(border2, scalingFactor);
        scaledLibrary.coast1 = scaleImages2(coast1, scalingFactor);
        scaledLibrary.coast2 = scaleImages2(coast2, scalingFactor);
        
        scaledLibrary.unitButtons = new ArrayList<ArrayList<ImageIcon>>(unitButtons);
        
        scaledLibrary.alarmChips = new EnumMap<Tension.Level, Image>(alarmChips);
        scaledLibrary.alarmChipsUnvisited = new EnumMap<Tension.Level, Image>(alarmChipsUnvisited);
        scaledLibrary.colorChips = new HashMap<Color, Image>(colorChips);
        scaledLibrary.missionChips = new HashMap<Color, Image>(missionChips);
        scaledLibrary.expertMissionChips = new HashMap<Color, Image>(expertMissionChips);

        return scaledLibrary;
    }

    public Image scaleImage(Image image, float scale) {
        return image.getScaledInstance(Math.round(image.getWidth(null) * scale),
                                       Math.round(image.getHeight(null) * scale),
                                       Image.SCALE_SMOOTH);
    }

    public ImageIcon scaleIcon(ImageIcon icon, float scale) {
        return new ImageIcon(scaleImage(icon.getImage(), scale));
    }

    private Map<Color, Image> scaleChips(Map<Color, Image> input, float scale) {
        HashMap<Color, Image> output = new HashMap<Color, Image>();
        for (Entry<Color, Image> entry : input.entrySet()) {
            output.put(entry.getKey(), scaleImage(entry.getValue(), scale));
        }
        return output;
    }

    private Map<String, ImageIcon> scaleImages(Map<String, ImageIcon> input, float scale) {
        HashMap<String, ImageIcon> output = new HashMap<String, ImageIcon>();
        for (Entry<String, ImageIcon> entry : input.entrySet()) {
            output.put(entry.getKey(), scaleIcon(entry.getValue(), scale));
        }
        return output;
    }

    private ArrayList<ImageIcon> scaleImages(List<ImageIcon> input, float scale) {
        ArrayList<ImageIcon> output = new ArrayList<ImageIcon>();
        for (ImageIcon icon : input) {
            if (icon == null) {
                output.add(null);
            } else {
                output.add(scaleIcon(icon, scale));
            }
        }
        return output;
    }

    private Map<String, ArrayList<ImageIcon>> scaleImages2(Map<String, ArrayList<ImageIcon>> input, float scale) {
        HashMap<String, ArrayList<ImageIcon>> output = new HashMap<String, ArrayList<ImageIcon>>();
        for (Entry<String, ArrayList<ImageIcon>> entry : input.entrySet()) {
            if (entry.getValue() == null) {
                output.put(entry.getKey(), null);
            } else {
                output.put(entry.getKey(), scaleImages(entry.getValue(), scale));
            }
        }
        return output;
    }


    private ArrayList<ArrayList<ImageIcon>> scaleImages2(ArrayList<ArrayList<ImageIcon>> input, float scale) {
        ArrayList<ArrayList<ImageIcon>> output = new ArrayList<ArrayList<ImageIcon>>();
        for (ArrayList<ImageIcon> list : input) {
            if (list == null) {
                output.add(null);
            } else {
                output.add(scaleImages(list, scale));
            }
        }
        return output;
    }


    private EnumMap<Role, Map<UnitType, ImageIcon>> scaleUnitImages(EnumMap<Role, Map<UnitType, ImageIcon>> input,
                                                                    float f) {
        EnumMap<Role, Map<UnitType, ImageIcon>> result = new EnumMap<Role, Map<UnitType, ImageIcon>>(Role.class);
        for (Role role : Role.values()) {
            Map<UnitType, ImageIcon> oldMap = input.get(role);
            Map<UnitType, ImageIcon> newMap = new HashMap<UnitType, ImageIcon>();
            for (Entry<UnitType, ImageIcon> entry : oldMap.entrySet()) {
                ImageIcon oldIcon = entry.getValue();
                ImageIcon newIcon = new ImageIcon(oldIcon.getImage()
                                                  .getScaledInstance(Math.round(oldIcon.getIconWidth() * f),
                                                                     Math.round(oldIcon.getIconHeight() * f),
                                                                     Image.SCALE_SMOOTH));
                newMap.put(entry.getKey(), newIcon);
            }
            result.put(role, newMap);
        }
        return result;
    }

    
    private ImageIcon findImage(String filePath, Class<FreeCol> resourceLocator, boolean doLookup)
            throws FreeColException {
        if (doLookup) {
            URL url = resourceLocator.getResource(filePath);
            if (url != null) {
                return new ImageIcon(url);
            }
        }

        File tmpFile = new File(filePath);
        if (!tmpFile.exists() || !tmpFile.isFile() || !tmpFile.canRead()) {
            throw new FreeColException("The data file \"" + filePath + "\" could not be found.");
        }

        return new ImageIcon(filePath);
    }

    
    private void loadTerrain(GraphicsConfiguration gc, Class<FreeCol> resourceLocator, boolean doLookup)
            throws FreeColException {
        logger.fine("loading terrain images");
        terrain1 = new HashMap<String, ImageIcon>();
        terrain2 = new HashMap<String, ImageIcon>();
        overlay1 = new HashMap<String, ImageIcon>();
        overlay2 = new HashMap<String, ImageIcon>();
        border1 = new HashMap<String, ArrayList<ImageIcon>>();
        border2 = new HashMap<String, ArrayList<ImageIcon>>();
        coast1 = new HashMap<String, ArrayList<ImageIcon>>();
        coast2 = new HashMap<String, ArrayList<ImageIcon>>();
        
        for (TileType type : FreeCol.getSpecification().getTileTypeList()) {
            String filePath = dataDirectory + path + type.getArtBasic() + tileName;
            terrain1.put(type.getId(), findImage(filePath + "0" + extension, resourceLocator, doLookup));
            terrain2.put(type.getId(), findImage(filePath + "1" + extension, resourceLocator, doLookup));

            if (type.getArtOverlay() != null) {
                filePath = dataDirectory + path + type.getArtOverlay();
                overlay1.put(type.getId(), findImage(filePath + "0" + extension, resourceLocator, doLookup));
                overlay2.put(type.getId(), findImage(filePath + "1" + extension, resourceLocator, doLookup));
            }
            
            ArrayList<ImageIcon> tempArrayList1 = new ArrayList<ImageIcon>();
            ArrayList<ImageIcon> tempArrayList2 = new ArrayList<ImageIcon>();
            for (Direction direction : Direction.values()) {
                filePath = dataDirectory + path + type.getArtBasic() + borderName + "_" +
                    direction.toString();
                tempArrayList1.add(findImage(filePath + "_even" + extension, resourceLocator, doLookup));
                tempArrayList2.add(findImage(filePath + "_odd" + extension, resourceLocator, doLookup));
            }

            border1.put(type.getId(), tempArrayList1);
            border2.put(type.getId(), tempArrayList2);
            
            if (type.getArtCoast() != null) {
                tempArrayList1 = new ArrayList<ImageIcon>();
                tempArrayList2 = new ArrayList<ImageIcon>();
                for (Direction direction : Direction.values()) {
                    filePath = dataDirectory + path + type.getArtCoast() + borderName + "_" +
                        direction.toString();
                    tempArrayList1.add(findImage(filePath + "_even" + extension, resourceLocator, doLookup));
                    tempArrayList2.add(findImage(filePath + "_odd" + extension, resourceLocator, doLookup));
                }
                
                coast1.put(type.getId(), tempArrayList1);
                coast2.put(type.getId(), tempArrayList2);
            }
        }
        
        String unexploredPath = dataDirectory + path + terrainDirectory + unexploredDirectory + tileName;
        terrain1.put(unexploredName, findImage(unexploredPath + "0" + extension, resourceLocator, doLookup));
        terrain2.put(unexploredName, findImage(unexploredPath + "1" + extension, resourceLocator, doLookup));
        
        ArrayList<ImageIcon> unexploredArrayList1 = new ArrayList<ImageIcon>();
        ArrayList<ImageIcon> unexploredArrayList2 = new ArrayList<ImageIcon>();
        for (Direction direction : Direction.values()) {
            unexploredPath = dataDirectory + path + terrainDirectory + unexploredDirectory + borderName + 
                "_" + direction.toString();
            unexploredArrayList1.add(findImage(unexploredPath + "_even" + extension, resourceLocator, doLookup));
            unexploredArrayList2.add(findImage(unexploredPath + "_odd" + extension, resourceLocator, doLookup));
        }

        border1.put(unexploredName, unexploredArrayList1);
        border2.put(unexploredName, unexploredArrayList2);
    }

    
    private void loadBeaches(GraphicsConfiguration gc, Class<FreeCol> resourceLocator, boolean doLookup)
            throws FreeColException {
        logger.fine("loading beach images");
        beaches = new ArrayList<ImageIcon>(BEACH_STYLES);
        for (int i = 1; i < BEACH_STYLES; i++) {
            String filePath = dataDirectory + path + terrainDirectory + beachDirectory
                + beachName + i + extension;
            beaches.add(findImage(filePath, resourceLocator, doLookup));
        }
        
        beaches.add(0, beaches.get(0));

    }

    
    private void loadRivers(GraphicsConfiguration gc, Class<FreeCol> resourceLocator, boolean doLookup)
            throws FreeColException {
        logger.fine("loading river images");
        rivers = new ArrayList<ImageIcon>(RIVER_STYLES);
        for (int i = 0; i < RIVER_STYLES; i++) {
            String filePath = dataDirectory + path + riverDirectory + riverName + i + extension;
            rivers.add(findImage(filePath, resourceLocator, doLookup));
        }
    }

    
    private void loadRiverMouths(GraphicsConfiguration gc, Class<FreeCol> resourceLocator, boolean doLookup)
            throws FreeColException {
        logger.fine("loading river mouth images");
        deltas = new HashMap<String, ImageIcon>();
        for (Direction d : Direction.longSides) {
            String key = deltaName + d + small;
            String filePath = dataDirectory + path + riverDirectory + key + extension;
            deltas.put(key, findImage(filePath, resourceLocator, doLookup));
            key = deltaName + d + large;
            filePath = dataDirectory + path + riverDirectory + key + extension;
            deltas.put(key, findImage(filePath, resourceLocator, doLookup));
        }
    }

    
    private void loadForests(GraphicsConfiguration gc, Class<FreeCol> resourceLocator, boolean doLookup)
            throws FreeColException {
        logger.fine("loading forest images");
        forests = new HashMap<String, ImageIcon>();
        
        for (TileType type : FreeCol.getSpecification().getTileTypeList()) {
            if (type.getArtForest() != null) {
                String filePath = dataDirectory + path + type.getArtForest();
                forests.put(type.getId(), findImage(filePath, resourceLocator, doLookup));
            }
        }
    }

    
    private void loadUnitButtons(GraphicsConfiguration gc, Class<FreeCol> resourceLocator, boolean doLookup)
            throws FreeColException {
        logger.fine("loading unit buttons");
        unitButtons = new ArrayList<ArrayList<ImageIcon>>(4);
        for (int i = 0; i < 4; i++) {
            unitButtons.add(new ArrayList<ImageIcon>(UNIT_BUTTON_COUNT));
        }

        for (int i = 0; i < 4; i++) {
            String subDirectory;
            switch (i) {
            case 0:
                subDirectory = new String("order-buttons00/");
                break;
            case 1:
                subDirectory = new String("order-buttons01/");
                break;
            case 2:
                subDirectory = new String("order-buttons02/");
                break;
            case 3:
                subDirectory = new String("order-buttons03/");
                break;
            default:
                subDirectory = new String("");
                break;
            }
            for (int j = 0; j < UNIT_BUTTON_COUNT; j++) {
                String filePath = dataDirectory + path + unitButtonDirectory + subDirectory + unitButtonName + j
                        + extension;
                unitButtons.get(i).add(findImage(filePath, resourceLocator, doLookup));
            }
        }
    }

    
    private void loadColorChip(GraphicsConfiguration gc, Color c) {
        logger.fine("creating color chips");
        BufferedImage tempImage = gc.createCompatibleImage(11, 17);
        Graphics g = tempImage.getGraphics();
        if (c.equals(Color.BLACK)) {
            g.setColor(Color.WHITE);
        } else {
            g.setColor(Color.BLACK);
        }
        g.drawRect(0, 0, 10, 16);
        g.setColor(c);
        g.fillRect(1, 1, 9, 15);
        colorChips.put(c, tempImage);
    }

    
    private void loadMissionChip(GraphicsConfiguration gc, Color c, boolean expertMission) {
        logger.fine("creating mission chips");
        BufferedImage tempImage = gc.createCompatibleImage(10, 17);
        Graphics2D g = (Graphics2D) tempImage.getGraphics();

        if (expertMission) {
            g.setColor(Color.BLACK);
        } else {
            g.setColor(Color.DARK_GRAY);
        }
        g.fillRect(0, 0, 10, 17);

        GeneralPath cross = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
        cross.moveTo(4, 1);
        cross.lineTo(6, 1);
        cross.lineTo(6, 4);
        cross.lineTo(9, 4);
        cross.lineTo(9, 6);
        cross.lineTo(6, 6);
        cross.lineTo(6, 16);
        cross.lineTo(4, 16);
        cross.lineTo(4, 6);
        cross.lineTo(1, 6);
        cross.lineTo(1, 4);
        cross.lineTo(4, 4);
        cross.closePath();

        if (expertMission && c.equals(Color.BLACK)) {
            g.setColor(Color.DARK_GRAY);
        } else if ((!expertMission) && c.equals(Color.DARK_GRAY)) {
            g.setColor(Color.BLACK);
        } else {
            g.setColor(c);
        }
        g.fill(cross);

        if (expertMission) {
            expertMissionChips.put(c, tempImage);
        } else {
            missionChips.put(c, tempImage);
        }
    }

    
    private void loadAlarmChip(GraphicsConfiguration gc, Tension.Level alarm, final boolean visited) {
        logger.fine("creating alarm chips");
        BufferedImage tempImage = gc.createCompatibleImage(10, 17);
        Graphics2D g = (Graphics2D) tempImage.getGraphics();

        g.setColor(Color.BLACK);
        g.drawRect(0, 0, 10, 16);

        switch(alarm) {
        case HAPPY:
            g.setColor(Color.GREEN);
            break;
        case CONTENT:
            g.setColor(Color.BLUE);
            break;
        case DISPLEASED:
            g.setColor(Color.YELLOW);
            break;
        case ANGRY:
            g.setColor(Color.ORANGE);
            break;
        case HATEFUL:
            g.setColor(Color.RED);
            break;
        }

        g.fillRect(1, 1, 8, 15);
        g.setColor(Color.BLACK);

        if (visited) {
        	g.fillRect(4, 3, 2, 7);
        } else {
        	g.fillRect(3, 3, 4, 2);
        	g.fillRect(6, 4, 2, 2);
        	g.fillRect(4, 6, 3, 1);
        	g.fillRect(4, 7, 2, 3);
        }
        g.fillRect(4, 12, 2, 2);

        (visited?alarmChips:alarmChipsUnvisited).put(alarm, tempImage);
    }


    
    public Image getFoundingFatherImage(FoundingFather father) {
        return ResourceManager.getImage(father.getId() + ".image");
    }

    
    public Image getMonarchImage(Nation nation) {
        return ResourceManager.getImage(nation.getId() + ".monarch.image");
    }

    
    public ImageIcon getMonarchImageIcon(Nation nation) {
        return ResourceManager.getImageIcon(nation.getId() + ".monarch.image");
    }

    
    public ImageIcon getCoatOfArmsImageIcon(Nation nation) {
        return ResourceManager.getImageIcon(nation.getId() + ".coat-of-arms.image");
    }

    
    public Image getCoatOfArmsImage(Nation nation) {
        return ResourceManager.getImage(nation.getId() + ".coat-of-arms.image");
    }

    
    public Image getBonusImage(Tile tile) {
        if (tile.hasResource()) {
            return getBonusImage(tile.getTileItemContainer().getResource().getType());
        } else {
            return null;
        }
    }

    public Image getBonusImage(ResourceType type) {
        return ResourceManager.getImage(type.getId() + ".image", scalingFactor);
    }

    
    public ImageIcon getBonusImageIcon(ResourceType type) {
        return new ImageIcon(getBonusImage(type));
    }

    public ImageIcon getScaledBonusImageIcon(ResourceType type, float scale) {
        return getScaledImageIcon(getBonusImageIcon(type), scale);
    }


    
    private ImageIcon convertToGrayscale(Image image) {
        int width = image.getWidth(null);
        int height = image.getHeight(null);

        ColorConvertOp filter = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
        BufferedImage srcImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        srcImage.createGraphics().drawImage(image, 0, 0, null);
        return new ImageIcon(filter.filter(srcImage, null));
    }


    
    public Image getScaledTerrainImage(TileType type, float scale) {
        
        GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
                .getDefaultConfiguration();
        Image terrainImage = getTerrainImage(type, 0, 0);
        int width = getTerrainImageWidth(type);
        int height = getCompoundTerrainImageHeight(type);
        
        if (type.getArtOverlay() != null) {
            Image overlayImage = getOverlayImage(type, 0, 0);
            BufferedImage compositeImage = gc.createCompatibleImage(width, height, Transparency.TRANSLUCENT);
            Graphics2D g = compositeImage.createGraphics();
            g.drawImage(terrainImage, 0, height - terrainImage.getHeight(null), null);
            g.drawImage(overlayImage, 0, height - overlayImage.getHeight(null), null);
            g.dispose();
            terrainImage = compositeImage;
        }
        if (type.isForested()) {
            Image forestImage = getForestImage(type);
            BufferedImage compositeImage = gc.createCompatibleImage(width, height, Transparency.TRANSLUCENT);
            Graphics2D g = compositeImage.createGraphics();
            g.drawImage(terrainImage, 0, height - terrainImage.getHeight(null), null);
            g.drawImage(forestImage, 0, height - forestImage.getHeight(null), null);
            g.dispose();
            terrainImage = compositeImage;
        }
        if (scale == 1f) {
            return terrainImage;
        } else {
            return terrainImage.getScaledInstance((int) (width * scale), (int) (height * scale), Image.SCALE_SMOOTH);
        }
    }

    
    public Image getOverlayImage(TileType type, int x, int y) {
        if ((x + y) % 2 == 0) {
            return overlay1.get(type.getId()).getImage();
        } else {
            return overlay2.get(type.getId()).getImage();
        }
    }

    
    public Image getTerrainImage(TileType type, int x, int y) {
        String key;
        if (type != null) {
            key = type.getId();
        } else {
            key = unexploredName;
        }
        if (( y % 8 <= 2) || ( (x+y) % 2 == 0 )) {
            
            
            return terrain1.get(key).getImage();
        } else {
            return terrain2.get(key).getImage();
        }
    }

    
    public Image getBorderImage(TileType type, Direction direction, int x, int y) {

        int borderType = direction.ordinal();
        
        String key;
        if (type != null) {
            key = type.getId();
        } else {
            key = unexploredName;
        }

        if ((x + y) % 2 == 0) {
            return border1.get(key).get(borderType).getImage();
        } else {
            return border2.get(key).get(borderType).getImage();
        }
    }

    
    public Image getRiverMouthImage(Direction direction, int magnitude, int x, int y) {

        String key = deltaName + direction + (magnitude == 1 ? small : large);
        return deltas.get(key).getImage();
    }

    
    public Image getCoastImage(TileType type, Direction direction, int x, int y) {

        int borderType = direction.ordinal();
        
        String key;
        if (type != null) {
            key = type.getId();
        } else {
            key = unexploredName;
        }

        if ((x + y) % 2 == 0) {
            return coast1.get(key).get(borderType).getImage();
        } else {
            return coast2.get(key).get(borderType).getImage();
        }
    }

    
    public Image getRiverImage(int index) {
        return rivers.get(index).getImage();
    }

    
    public Image getBeachImage(int index) {
        return beaches.get(index).getImage();
    }

    
    public Image getForestImage(TileType type) {
        return forests.get(type.getId()).getImage();
    }

    
    public Image getMiscImage(String id) {
        return ResourceManager.getImage(id, scalingFactor);
    }

    
    public ImageIcon getMiscImageIcon(String id) {
        return new ImageIcon(getMiscImage(id));
    }

    
    public ImageIcon getUnitButtonImageIcon(int index, int state) {
        return unitButtons.get(state).get(index);
    }

    
    public Image getGoodsImage(GoodsType goodsType) {
        return ResourceManager.getImage(goodsType.getId() + ".image");
    }

    
    public ImageIcon getGoodsImageIcon(GoodsType goodsType) {
        return ResourceManager.getImageIcon(goodsType.getId() + ".image");
    }

    
    public ImageIcon getScaledGoodsImageIcon(GoodsType type, float scale) {
        return getScaledImageIcon(getGoodsImageIcon(type), scale);
    }

    
    public Image getColorChip(Color color) {
        Image colorChip = colorChips.get(color);
        if (colorChip == null) {
            GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
                    .getDefaultConfiguration();
            loadColorChip(gc, color);
            colorChip = colorChips.get(color);
        }
        return colorChip;
    }

    
    public Image getMissionChip(Color color, boolean expertMission) {
        Image missionChip;
        if (expertMission) {
            missionChip = expertMissionChips.get(color);
        } else {
            missionChip = missionChips.get(color);
        }

        if (missionChip == null) {
            GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
                    .getDefaultConfiguration();
            loadMissionChip(gc, color, expertMission);

            if (expertMission) {
                missionChip = expertMissionChips.get(color);
            } else {
                missionChip = missionChips.get(color);
            }
        }
        return missionChip;
    }

    
    public Image getAlarmChip(Tension.Level alarm, final boolean visited) {
        Image alarmChip = (visited?alarmChips:alarmChipsUnvisited).get(alarm);

        if (alarmChip == null) {
            GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
                    .getDefaultConfiguration();
            loadAlarmChip(gc, alarm, visited);
            alarmChip = (visited?alarmChips:alarmChipsUnvisited).get(alarm);
        }
        return alarmChip;
    }

    
    public int getTerrainImageWidth(TileType type) {
        String key;
        if (type != null) {
            key = type.getId();
        } else {
            key = unexploredName;
        }
        return terrain1.get(key).getIconWidth();
    }

    
    public int getTerrainImageHeight(TileType type) {
        String key;
        if (type != null) {
            key = type.getId();
        } else {
            key = unexploredName;
        }
        return terrain1.get(key).getIconHeight();
    }

    
    public int getCompoundTerrainImageHeight(TileType type) {
        if (type == null) {
            return terrain1.get(unexploredName).getIconHeight();
        } else {
            int height = terrain1.get(type.getId()).getIconHeight();
            if (type.getArtOverlay() != null) {
                height = Math.max(height, getOverlayImage(type, 0, 0).getHeight(null));
            }
            if (type.isForested()) {
                height = Math.max(height, getForestImage(type).getHeight(null));
            }
            return height;
        }
    }

    
    public Image getSettlementImage(SettlementType settlementType) {
        return ResourceManager.getImage(settlementType.toString() + ".image", scalingFactor);
    }

    
    public Image getSettlementImage(Settlement settlement) {

        if (settlement instanceof Colony) {
            Colony colony = (Colony) settlement;

            
            if (colony.isUndead()) {
                return getSettlementImage(SettlementType.UNDEAD);
            } else {
                int stockadeLevel = 0;
                if (colony.getStockade() != null) {
                    stockadeLevel = colony.getStockade().getLevel();
                }
                int unitCount = colony.getUnitCount();
                switch(stockadeLevel) {
                case 0:
                    if (unitCount <= 3) {
                        return getSettlementImage(SettlementType.SMALL);
                    } else if (unitCount <= 7) {
                        return getSettlementImage(SettlementType.MEDIUM);
                    } else {
                        return getSettlementImage(SettlementType.LARGE);
                    }
                case 1:
                    if (unitCount > 7) {
                        return getSettlementImage(SettlementType.LARGE_STOCKADE);
                    } else if (unitCount > 3) {
                        return getSettlementImage(SettlementType.MEDIUM_STOCKADE);
                    } else {
                        return getSettlementImage(SettlementType.SMALL_STOCKADE);
                    }
                case 2:
                    if (unitCount > 7) {
                        return getSettlementImage(SettlementType.LARGE_FORT);
                    } else {
                        return getSettlementImage(SettlementType.MEDIUM_FORT);
                    }
                case 3:
                    return getSettlementImage(SettlementType.LARGE_FORTRESS);
                default:
                    return getSettlementImage(SettlementType.SMALL);
                }
            }

        } else { 
            return getSettlementImage(((IndianSettlement) settlement).getTypeOfSettlement());
        }
    }

    
    public ImageIcon getUnitImageIcon(Unit unit) {
        return getUnitImageIcon(unit.getType(), unit.getRole());
    }

    
    public ImageIcon getUnitImageIcon(UnitType unitType) {
        final Image im = ResourceManager.getImage(unitType.getId() + ".image", scalingFactor);
        return (im != null) ? new ImageIcon(im) : null;
    }
    
    
    public ImageIcon getUnitImageIcon(UnitType unitType, Role role) {
        final String roleStr = (role != Role.DEFAULT) ? "." + role.getId() : "";
        final Image im = ResourceManager.getImage(unitType.getId() + roleStr + ".image", scalingFactor);
        return (im != null) ? new ImageIcon(im) : null;
    }

    
    public ImageIcon getUnitImageIcon(Unit unit, boolean grayscale) {
        return getUnitImageIcon(unit.getType(), unit.getRole(), grayscale);
    }

    
    public ImageIcon getUnitImageIcon(UnitType unitType, boolean grayscale) {
        return getUnitImageIcon(unitType, Role.DEFAULT, grayscale);
    }

    
    public ImageIcon getUnitImageIcon(UnitType unitType, Role role, boolean grayscale) {
        if (grayscale) {
            final String roleStr = (role != Role.DEFAULT) ? "." + role.getId() : "";
            final Image im = ResourceManager.getGrayscaleImage(unitType.getId() + roleStr + ".image", scalingFactor);
            return (im != null) ? new ImageIcon(im) : null;
        } else {
            return getUnitImageIcon(unitType, role);
        }
    }

    
    public ImageIcon getScaledImageIcon(ImageIcon inputIcon, float scale) {
        Image image = inputIcon.getImage();
        return new ImageIcon(image.getScaledInstance(Math.round(image.getWidth(null) * scale),
                                                     Math.round(image.getHeight(null) * scale),
                                                     Image.SCALE_SMOOTH));
    }

    
    public ImageIcon getScaledImageIcon(Image image, float scale) {
        return new ImageIcon(image.getScaledInstance(Math.round(image.getWidth(null) * scale),
                                                     Math.round(image.getHeight(null) * scale),
                                                     Image.SCALE_SMOOTH));
    }
    

}
