

package net.sf.freecol.client.gui.i18n;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Logger;

import net.sf.freecol.FreeCol;
import net.sf.freecol.common.PseudoRandom;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Region.RegionType;
import net.sf.freecol.common.model.StringTemplate;
import net.sf.freecol.common.model.Unit;
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

        for (String fileName : getFileNames(language, country, variant)) {
            File resourceFile = new File(getI18nDirectory(), fileName);
            loadResources(resourceFile);
        }
    }

    
    public static String[] getFileNames(String language, String country, String variant) {

       if (!language.equals("")) {
            language = "_" + language;
        }
        if (!country.equals("")) {
            country = "_" + country;
        }
        if (!variant.equals("")) {
            variant = "_" + variant;
        }
        return new String[] {
            FILE_PREFIX + FILE_SUFFIX,
            FILE_PREFIX + language + FILE_SUFFIX,
            FILE_PREFIX + language + country + FILE_SUFFIX,
            FILE_PREFIX + language + country + variant + FILE_SUFFIX
        };
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

    
    public static String message(StringTemplate template) {
        String result = "";
        switch (template.getTemplateType()) {
        case LABEL:
            if (template.getReplacements() == null) {
                return message(template.getId());
            } else {
                for (StringTemplate other : template.getReplacements()) {
                    result += template.getId() + message(other);
                }
                if (result.length() > template.getId().length()) {
                    return result.substring(template.getId().length());
                } else {
                    logger.warning("Incorrect use of template with id " + template.getId());
                    return result;
                }
            }
        case TEMPLATE:
            if (containsKey(template.getId())) {
                result = message(template.getId());
            } else if (template.getDefaultId() != null) {
                result = message(template.getDefaultId());
            }
	    for (int index = 0; index < template.getKeys().size(); index++) {
                result = result.replace(template.getKeys().get(index),
                                        message(template.getReplacements().get(index)));
	    }
	    return result;
        case KEY:
            return message(template.getId());
        case NAME:
        default:
            return template.getId();
        }
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

    
    public static StringTemplate getLabel(Unit unit) {
        String typeKey = null;
        String infoKey = null;

        if (unit.canCarryTreasure()) {
            typeKey = unit.getType().getNameKey();
            infoKey = Integer.toString(unit.getTreasureAmount());
        } else {
            String key = (unit.getRole() == Unit.Role.DEFAULT) ? "name"
                : unit.getRole().toString().toLowerCase();
            String messageID = unit.getType().getId() + "." + key;
            if (containsKey(messageID)) {
                typeKey = messageID;
                if ((unit.getEquipment() == null || unit.getEquipment().isEmpty()) &&
                    unit.getType().getDefaultEquipmentType() != null) {
                    infoKey = unit.getType().getDefaultEquipmentType().getId() + ".none";
                }
            } else {
                typeKey = "model.unit.role." + key;
                infoKey = unit.getType().getNameKey();
            }
        }

        StringTemplate result = StringTemplate.label(" ")
            .add(typeKey);
        if (unit.getName() != null) {
            result.addName(unit.getName());
        }
        if (infoKey != null) {
            result.addStringTemplate(StringTemplate.label("")
                                     .addName("(")
                                     .add(infoKey)
                                     .addName(")"));
        }
        return result;
    }


     
    public static String getLabel(UnitType someType, Unit.Role someRole) {
        String key = someRole.toString().toLowerCase();
        if (someRole == Unit.Role.DEFAULT) {
            key = "name";
        }
        String messageID = someType.getId() +  "." + key;
        if (containsKey(messageID)) {
            return message(messageID);
        } else {
            return message("model.unit." + key + ".name", "%unit%",
                           Messages.message(someType.getNameKey()));
        }
    }

    
    public static String getStanceAsString(Player.Stance stance) {
        return message("model.stance." + stance.toString().toLowerCase());
    }

    public static String getNewLandName(Player player) {
        if (player.getNewLandName() == null) {
            return message(player.getNationID() + ".newLandName");
        } else {
            return player.getNewLandName();
        }
    }


    
    public static String getDefaultSettlementName(Player player, boolean capital) {
        int settlementNameIndex = 0;
        String prefix = player.getNationID() + ".settlementName.";
        String name;

        if (capital) return message(prefix + "0");

        if (player.isIndian()) {
            
            
            
            PseudoRandom random = player.getGame().getModelController().getPseudoRandom();
            int upper = 100;
            int lower = 1;
            int i, n = 0;

            for (i = 0; i < 5; i++) { 
                n = random.nextInt(upper - lower) + lower;
                if (!containsKey(prefix + Integer.toString(n))) {
                    if (n == lower) break;
                    upper = n;
                    continue;
                }
                name = message(prefix + Integer.toString(n));
                if (player.getSettlement(name) == null) return name;
            }
            for (i = n+1; i < upper; i++) { 
                if (!containsKey(prefix + Integer.toString(i))) break;
                name = message(prefix + Integer.toString(i));
                if (player.getSettlement(name) == null) return name;
            }
            for (i = n-1; i > 0; i--) { 
                if (!containsKey(prefix + Integer.toString(i))) continue;
                name = message(prefix + Integer.toString(i));
                if (player.getSettlement(name) == null) return name;
            }
        } else {
            while (containsKey(prefix + Integer.toString(settlementNameIndex))) {
                name = message(prefix + Integer.toString(settlementNameIndex));
                settlementNameIndex++;
                if (player.getGame().getSettlement(name) == null) return name;
            }
        }

        
        String fallback = (player.isIndian()) ? "Settlement" : "Colony";
        do {
            name = message(fallback) + settlementNameIndex;
            settlementNameIndex++;
        } while (player.getGame().getSettlement(name) != null);
        return name;
    }

    
    public static String getDefaultRegionName(Player player, RegionType regionType) {
        int index = 1;
        String prefix = player.getNationID() + ".region." + regionType.toString().toLowerCase() + ".";
        String name;
        do {
            name = null;
            if (containsKey(prefix + Integer.toString(index))) {
                name = Messages.message(prefix + Integer.toString(index));
                index++;
            }
        } while (name != null && player.getGame().getMap().getRegionByName(name) != null);
        if (name == null) {
            do {
                name = message(StringTemplate.template("model.region.default")
                               .addStringTemplate("%nation%", player.getNationName())
                               .add("%type%", "model.region." + regionType.toString().toLowerCase() + ".name")
                               .addAmount("%index%", index));
                index++;
            } while (player.getGame().getMap().getRegionByName(name) != null);
        }
        return name;
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

    
    public static void loadResources(InputStream input) {
        try {
            messageBundle.load(input);
        } catch (Exception e) {
            logger.warning("Unable to load resource into message bundle.");
        }
    }

}
