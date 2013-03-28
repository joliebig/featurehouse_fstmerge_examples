

package net.sf.freecol.common.option;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.FreeCol;
import net.sf.freecol.client.gui.i18n.Messages;


public class LanguageOption extends AbstractOption {

    private static Logger logger = Logger.getLogger(LanguageOption.class.getName());

    private static final Map<String, Language> languages = new HashMap<String, Language>();

    public static final String AUTO = "automatic";

    private Language DEFAULT = new Language(AUTO, getLocale(AUTO));

    private Map<String, String> languageNames = new HashMap<String, String>();

    private static final String[][] languageNamesHelper = {
        {"arz", "\u\u\u\u"},
        {"hsb", "Serb\u\una"},
        {"nds", "Plattd\u\usch"},
        {"pms", "Piemont\u"}
    };


    private static Comparator<Language> languageComparator = new Comparator<Language>() {
        public int compare(Language l1, Language l2) {
            if (l1.getKey().equals(AUTO)) {
                if (l2.getKey().equals(AUTO)) {
                    return 0;
                } else {
                    return -1;
                }
            } else if (l2.getKey().equals(AUTO)) {
                return 1;
            } else {
                return l1.toString().compareTo(l2.toString());
            }
        }
    };


    private Language value;
    
    
     public LanguageOption(XMLStreamReader in) throws XMLStreamException {
         super(NO_ID);
         if (languages.size() == 0) {
             prepareLanguages();
         }
         readFromXML(in);
     }   

    
    public LanguageOption(String id) {
        this(id, null);
    }

    public LanguageOption(String id, OptionGroup optionGroup) {
        super(id, optionGroup);
        if (languages.size() == 0) {
            prepareLanguages();
        }
        value = DEFAULT;
    }


    private void prepareLanguages() {
        for (String[] pair : languageNamesHelper) {
            languageNames.put(pair[0], pair[1]);
        }
    }

    
    public final Language getValue() {
        return value;
    }

    
    public final void setValue(final Language newValue) {
        final Language oldValue = this.value;
        this.value = newValue;

        if (!newValue.equals(oldValue)) {
            firePropertyChange("value", oldValue, value);
        }
    }

    
    public Language[] getOptions() {
        findLanguages();
        List<Language> names = new ArrayList<Language>(languages.values());
        Collections.sort(names, languageComparator);
        return names.toArray(new Language[0]);
    }

    
    private void findLanguages() {

        languages.put(AUTO, DEFAULT);
        File i18nDirectory = new File(FreeCol.getDataDirectory(), Messages.STRINGS_DIRECTORY);
        File[] files = i18nDirectory.listFiles();
        if (files == null) {
            throw new RuntimeException("No language files could be found in the <" + i18nDirectory + 
                                       "> folder. Make sure you ran the ant correctly.");
        }
        for (File file : files) {
            if (file.getName() == null) {
                continue;
            }
            if (file.getName().startsWith(Messages.FILE_PREFIX + "_")) {
                try {
                    final String languageID = file.getName().substring(16, file.getName().indexOf("."));
                    languages.put(languageID, new Language(languageID, getLocale(languageID)));
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Exception in findLanguages()", e);
                    continue;
                }
            }
        }
    }


    
    public static Locale getLocale(String languageID) {
        if (languageID == null || AUTO.equals(languageID)) {
            return Locale.getDefault();
        }
         
        try {
            String language, country = "", variant = "";
            StringTokenizer st = new StringTokenizer(languageID, "_", true);
            language = st.nextToken();
            if (st.hasMoreTokens()) {
                
                st.nextToken();
            }
            if (st.hasMoreTokens()) {
                String token = st.nextToken();
                if (!token.equals("_")) {
                    country = token;
                }
                if (st.hasMoreTokens()) {
                    token = st.nextToken();
                    if (token.equals("_") && st.hasMoreTokens()) {
                        token = st.nextToken();
                    }
                    if (!token.equals("_")) {
                        variant = token;
                    }
                }
            }
            return new Locale(language, country, variant);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Cannot choose locale: " + languageID, e);
            return Locale.getDefault();
        }
    }


    
    protected void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        
        out.writeStartElement(getXMLElementTagName());

        out.writeAttribute("id", getId());
        out.writeAttribute("value", getValue().getKey());

        out.writeEndElement();
     }
    
    
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        final String id = in.getAttributeValue(null, "id");
        findLanguages();
        
        if (id == null && getId().equals("NO_ID")){
            throw new XMLStreamException("invalid <" + getXMLElementTagName() + "> tag : no id attribute found.");
        } else if(getId() == NO_ID) {
            setId(id);
        }

        Language newValue = languages.get(in.getAttributeValue(null, "value"));
        if (newValue == null) {
            newValue = languages.get(AUTO);
        }
        setValue(newValue);
        in.nextTag();
    }


    
    public static String getXMLElementTagName() {
        return "languageOption";
    }


    public class Language {

        
        private String key;

        
        private Locale locale;


        public Language(String key, Locale locale) {
            this.key = key;
            this.locale = locale;
        }

        
        public final String getKey() {
            return key;
        }

        
        public final void setKey(final String newKey) {
            this.key = newKey;
        }

        
        public final Locale getLocale() {
            return locale;
        }

        
        public final void setLocale(final Locale newLocale) {
            this.locale = newLocale;
        }

        public String toString() {
            if (getKey().equals(AUTO)) {
                return Messages.message("clientOptions.gui.languageOption.autoDetectLanguage");
            } else {
                String name = locale.getDisplayName(locale);
                if (name.equals(key) && languageNames.containsKey(key)) {
                    name = languageNames.get(key);
                }
                return name.substring(0, 1).toUpperCase(locale) + name.substring(1);
            }
        }

        public boolean equals(Object o) {
            if ((o instanceof Language) &&
                ((Language) o).getKey().equals(key)) {
                return true;
            } else {
                return false;
            }
        }

    }

}
