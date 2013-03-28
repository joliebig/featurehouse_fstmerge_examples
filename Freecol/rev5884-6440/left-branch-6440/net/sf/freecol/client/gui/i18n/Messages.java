

package net.sf.freecol.client.gui.i18n;

import java.io.File;
import java.io.FileInputStream;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Logger;

import net.sf.freecol.FreeCol;
import net.sf.freecol.common.model.Building;
import net.sf.freecol.common.model.ColonyTile;
import net.sf.freecol.common.model.Europe;
import net.sf.freecol.common.model.Feature;
import net.sf.freecol.common.model.FoundingFather;
import net.sf.freecol.common.model.FreeColGameObject;
import net.sf.freecol.common.model.FreeColGameObjectType;
import net.sf.freecol.common.model.FreeColObject;
import net.sf.freecol.common.model.Goods;
import net.sf.freecol.common.model.GoodsType;
import net.sf.freecol.common.model.Location;
import net.sf.freecol.common.model.Map.CircleIterator;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Resource;
import net.sf.freecol.common.model.Settlement;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.TileImprovement;
import net.sf.freecol.common.model.TileItem;
import net.sf.freecol.common.model.TileItemContainer;
import net.sf.freecol.common.model.Typed;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.Unit.Role;
import net.sf.freecol.common.model.UnitType;


public class Messages {

    private static final Logger logger = Logger.getLogger(Messages.class.getName());

    public static final String STRINGS_DIRECTORY = "strings";

    public static final String FILE_PREFIX = "FreeColMessages";

    public static final String FILE_SUFFIX = ".properties";

    private static Properties messageBundle = null;

    
    public static void setMessageBundle(Locale locale) {
        if (locale == null) {
            throw new NullPointerException("Parameter locale must not be null");
        } else {
            if (!Locale.getDefault().equals(locale)) {
                Locale.setDefault(locale);
            }
            setMessageBundle(locale.getLanguage(), locale.getCountry(), locale.getVariant());
        }
    }

    
    private static void setMessageBundle(String language, String country, String variant) {

        messageBundle = new Properties();

        if (!language.equals("")) {
            language = "_" + language;
        }
        if (!country.equals("")) {
            country = "_" + country;
        }
        if (!variant.equals("")) {
            variant = "_" + variant;
        }
        String[] fileNames = { FILE_PREFIX + FILE_SUFFIX, FILE_PREFIX + language + FILE_SUFFIX,
                               FILE_PREFIX + language + country + FILE_SUFFIX,
                               FILE_PREFIX + language + country + variant + FILE_SUFFIX };

        for (String fileName : fileNames) {
            File resourceFile = new File(getI18nDirectory(), fileName);
            loadResources(resourceFile);
        }
    }

    
    public static File getI18nDirectory() {
        return new File(FreeCol.getDataDirectory(), STRINGS_DIRECTORY);
    }


    
    public static String message(String messageId, String... data) {
        
        if (messageId == null) {
            throw new NullPointerException();
        }
        if (data!=null && data.length % 2 != 0) {
            throw new IllegalArgumentException("Programming error, the data should consist of only pairs.");
        }
        if (messageBundle == null) {
            setMessageBundle(Locale.getDefault());
        }
 
        String message = messageBundle.getProperty(messageId);
        if (message == null) {
            return messageId;
        }

        if (data!=null && data.length > 0) {
            for (int i = 0; i < data.length; i += 2) {
                if (data[i] == null || data[i+1] == null) {
                    throw new IllegalArgumentException("Programming error, no data should be <null>.");
                }
                
                
                String escapedStr = data[i+1].replaceAll("\\$","\\\\\\$");
                message = message.replaceAll(data[i], escapedStr);
            }
        }
        return message.trim();
    }

    
    public static boolean containsKey(String key) {
        if (messageBundle == null) {
            setMessageBundle(Locale.getDefault());
        }
        return (messageBundle.getProperty(key) != null);
    }


    
    public static String getKey(String preferredKey, String defaultKey) {
        if (containsKey(preferredKey)) {
            return preferredKey;
        } else {
            return defaultKey;
        }
    }


    
    public static void loadResources(File resourceFile) {

        if ((resourceFile != null) && resourceFile.exists() && resourceFile.isFile() && resourceFile.canRead()) {
            try {
                messageBundle.load(new FileInputStream(resourceFile));
            } catch (Exception e) {
                logger.warning("Unable to load resource file " + resourceFile.getPath());
            }
        }
    }


    
    public static final String getName(FreeColGameObjectType object) {
        return message(object.getId() + ".name");
    }

    
    public static final String getName(Typed object) {
        return message(object.getType().getId() + ".name");
    }

    
    public static final String getName(Feature object) {
        return message(object.getId() + ".name");
    }

    
    public static String getName(Tile tile) {
        if (tile.isViewShared()) {
            if (tile.isExplored()) {
                return getName(tile.getType());
            } else {
                return message("unexplored");
            }
        } else {
            Player player = tile.getGame().getCurrentPlayer();
            if (player != null) {
                if (tile.getPlayerExploredTile(player) != null
                    && tile.getPlayerExploredTile(player).isExplored()) {
                    return getName(tile.getType());
                }
                return message("unexplored");
            } else {
                logger.warning("player == null");
                return "";
            }
        }
    }


    
    public static String getName(FoundingFather.FoundingFatherType type) {
        return message("model.foundingFather." + type.toString().toLowerCase());
    }

    
    public static final String getDescription(FreeColObject object) {
        return message(object.getId() + ".description");
    }

    
    public static String getLabel(Unit unit) {
        String completeName = "";
        String customName = "";
        String name = unit.getName();
        if (name != null) {
            customName = " " + name;
        }

        
        if (unit.canCarryTreasure()) {
            completeName = message(unit.getType().getId() + ".gold", "%gold%",
                                   String.valueOf(unit.getTreasureAmount()));
        } else if ((unit.getEquipment() == null || unit.getEquipment().isEmpty()) &&
                   unit.getType().getDefaultEquipmentType() != null) {
            completeName = getLabel(unit.getType(), unit.getRole()) + " (" +
                message(unit.getType().getDefaultEquipmentType().getId() + ".none") + ")";
        } else {
            completeName = Messages.getLabel(unit.getType(), unit.getRole());
        }

        
        
        int index = completeName.lastIndexOf(" (");
        if (index < 0) {
            completeName = completeName + customName;
        } else {
            completeName = completeName.substring(0, index) + customName + 
                completeName.substring(index);
        }

        return completeName;
    }

    
    public static String getLabel(UnitType someType, Role someRole) {
        String key = someRole.toString().toLowerCase();
        if (someRole == Role.DEFAULT) {
            key = "name";
        }
        String messageID = someType.getId() +  "." + key;
        if (containsKey(messageID)) {
            return message(messageID);
        } else {
            return message("model.unit." + key + ".name", "%unit%",
                           getName(someType));
        }
    }

    
    public static String getLabel(Tile tile) {
        if (tile.getTileItemContainer() == null) {
            return getName(tile);
        } else {
            return getName(tile) + getLabel(tile.getTileItemContainer());
        }
    }
    
    
    public static String getLabel(ColonyTile colonyTile) {
        return getLabel(colonyTile.getWorkTile());
    }

    
    public static String getLabel(Goods goods) {
        return getLabel(goods.getType(), goods.getAmount());
    }

    
    public static String getLabel(GoodsType type, int amount) {
        return message("model.goods.goodsAmount",
                       "%goods%", getName(type),
                       "%amount%", Integer.toString(amount));
    }

    
    public static String getLabel(TileItemContainer tic) {
        String label = new String();
        for (TileItem item : tic.getTileItems()) {
            if (item instanceof Resource) {
                label += "/" + getName((Resource) item);
            } else if (item instanceof TileImprovement
                       && ((TileImprovement) item).isComplete()) {
                label += "/" + getName((TileImprovement) item);
            }
        }
        return label;
    }

    
    public static String getNationAsString(Player player) {
        return (player.getPlayerType() == Player.PlayerType.REBEL
                || player.getPlayerType() == Player.PlayerType.INDEPENDENT)
            ? player.getIndependentNationName()
            : getName(player.getNation());
    }

    

    
    public static String getLocationName(Tile tile) {
        Settlement settlement = tile.getSettlement();
        if (settlement == null) {
            String name = getName(tile);
            int radius = 8; 
            CircleIterator mapIterator = tile.getMap().getCircleIterator(tile.getPosition(), true, radius);
            while (mapIterator.hasNext()) {
                settlement = tile.getMap().getTile(mapIterator.nextPosition()).getSettlement();
                if (settlement != null) {
                    return name + " ("
                        + Messages.message("nearLocation", "%location%",
                                           settlement.getName()) + ")";
                }
            }
            if (tile.getRegion() != null && tile.getRegion().getName() != null) {
                return name + " (" + tile.getRegion().getName() + ")";
            } else {
                return name;
            }
        } else {
            return settlement.getName();
        }
    }

    
    public static String getLocationName(Building building) {
        return message("inLocation", "%location%", getName(building));
    }

    
    public static String getLocationName(ColonyTile colonyTile) {
        String name = colonyTile.getColony().getName();
        if (colonyTile.isColonyCenterTile()) {
            return name;
        } else {
            return message("nearLocation", "%location%", name);
        }
    }
    
    
    public static String getLocationName(Unit unit) {
        return message("onBoard", "%unit%", getLabel(unit));
    }

    
    public static String getLocationName(Location location) {
        if (location instanceof Settlement) {
            return ((Settlement) location).getName();
        } else if (location instanceof Europe) {
            return ((Europe) location).getName();
        } else if (location instanceof Tile) {
            return getLocationName((Tile) location);
        } else if (location instanceof Unit) {
            return getLocationName((Unit) location);
        } else if (location instanceof ColonyTile) {
            return getLocationName((ColonyTile) location);
        } else if (location instanceof Building) {
            return getLocationName((Building) location);
        } else {
            return location.toString();
        }
    }

}
