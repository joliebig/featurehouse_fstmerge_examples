
package org.jmol.i18n;

import java.text.MessageFormat;
import java.util.*;
import org.jmol.util.Logger;


public class GT {

  private static boolean ignoreApplicationBundle = false;
  private static GT getTextWrapper;
  private ResourceBundle[] translationResources = null;
  private int translationResourcesCount = 0;
  private boolean doTranslate = true;
  private String language;

  public GT(String la) {
    getTranslation(la);
  }
  
  private GT() {
    getTranslation(null);
  }

  
  
  

  public static class Language {
    public final String code;
    public final String language;
    public final boolean display;
    public Language(String code, String language, boolean display) {
      this.code = code;
      this.language = language;
      this.display = display;
    }
  }

  private static Language[] languageList;
  
  
  public static Language[] getLanguageList() {
    return (languageList != null ? languageList : getTextWrapper().createLanguageList());
  }

  
  synchronized private Language[] createLanguageList() {
    boolean wasTranslating = doTranslate;
    doTranslate = false;
    languageList = new Language[] {
      new Language("ar",    GT._("Arabic"),                   false),
      new Language("ca",    GT._("Catalan"),                  true),
      new Language("zh_CN", GT._("Simplified Chinese"),       false),
      new Language("zh_TW", GT._("Traditional Chinese"),      true),
      new Language("cs",    GT._("Czech"),                    true),
      new Language("da",    GT._("Danish"),                   false),
      new Language("nl",    GT._("Dutch"),                    true),
      new Language("en_GB", GT._("British English"),          true),
      new Language("en_US", GT._("American English"),         true), 
      new Language("et",    GT._("Estonian"),                 false),
      new Language("fr",    GT._("French"),                   true),
      new Language("de",    GT._("German"),                   true),
      new Language("el",    GT._("Greek"),                    false),
      new Language("hu",    GT._("Hungarian"),                true),
      new Language("it",    GT._("Italian"),                  true),
      new Language("ko",    GT._("Korean"),                   true),
      new Language("nb",    GT._("Norwegian Bokmal"),         false),
      new Language("pl",    GT._("Polish"),                   false),
      new Language("pt_BR", GT._("Brazilian Portuguese"),     true),
      new Language("pt",    GT._("Portuguese"),               false),
      new Language("ru",    GT._("Russian"),                  false),
      new Language("sl",    GT._("Slovenian"),                false),
      new Language("es",    GT._("Spanish"),                  true),
      new Language("sv",    GT._("Swedish"),                  false),
      new Language("tr",    GT._("Turkish"),                  true),
      new Language("uk",    GT._("Ukrainian"),                false),
    };
    doTranslate = wasTranslating;
    return languageList;
  }

  private String getSupported(String languageCode, boolean isExact) {
    if (languageCode == null)
      return null;
    if (languageList == null)
      createLanguageList();
    for (int i = 0; i < languageList.length; i++) {
      if (languageList[i].code.equalsIgnoreCase(languageCode))
        return languageList[i].code;
    }
    return (isExact ? null : findClosest(languageCode));
  }
 
  
  private String findClosest(String la) {
    for (int i = languageList.length; --i >= 0; ) {
      if (languageList[i].code.startsWith(la))
        return languageList[i].code;
    }
    return null;    
  }
  
  public static String getLanguage() {
    return getTextWrapper().language;
  }
  
  synchronized private void getTranslation(String langCode) {
    Locale locale;
    translationResources = null;
    translationResourcesCount = 0;
    getTextWrapper = this;
    if (langCode != null && langCode.length() == 0)
      langCode="none";
    if (langCode != null)
      language = langCode;
    if ("none".equals(language))
      language = null;
    if (language == null && (locale = Locale.getDefault()) != null) {
      language = locale.getLanguage();
      if (locale.getCountry() != null) {
        language += "_" + locale.getCountry();
        if (locale.getVariant() != null && locale.getVariant().length() > 0)
          language += "_" + locale.getVariant();
      }
    }
    if (language == null)
      language = "en";

    int i;
    String la = language;
    String la_co = language;
    String la_co_va = language;
    if ((i = language.indexOf("_")) >= 0) {
      la = la.substring(0, i);
      if ((i = language.indexOf("_", ++i)) >= 0) {
        la_co = language.substring(0, i);
      } else {
        la_co_va = null;
      }
    } else {
      la_co = null;
      la_co_va = null;
    }

    
    if ((language = getSupported(la_co_va, false)) == null
        && (language = getSupported(la_co, false)) == null
        && (language = getSupported(la, false)) == null) {
      language = "en";
      Logger.debug(language + " not supported -- using en");
      return;
    }
    la_co_va = null;
    la_co = null;
    switch (language.length()) {
    case 2:
      la = language;
      break;
    case 5:
      la_co = language;
      la = language.substring(0, 2);
      break;
    default:
      la_co_va = language;
      la_co = language.substring(0, 5);
      la = language.substring(0, 2);
    }

    

    la_co = getSupported(la_co, false);
    la = getSupported(la, false);

    if (la == la_co || "en_US".equals(la))
      la = null;
    if (la_co == la_co_va)
      la_co = null;
    if ("en_US".equals(la_co))
      return;
    if (Logger.debugging)
      Logger.debug("Instantiating gettext wrapper for " + language
          + " using files for language:" + la + " country:" + la_co
          + " variant:" + la_co_va);
    if (!ignoreApplicationBundle)
      addBundles("Jmol", la_co_va, la_co, la);
    addBundles("JmolApplet", la_co_va, la_co, la);
  }
  
  private void addBundles(String type, String la_co_va, String la_co, String la) {
    try {
        String className = "org.jmol.translation." + type + ".";
        if (la_co_va != null)
          addBundle(className, la_co_va);
        if (la_co != null)
          addBundle(className, la_co);
        if (la != null)
          addBundle(className, la);
    } catch (Exception exception) {
      Logger.error("Some exception occurred!", exception);
      translationResources = null;
      translationResourcesCount = 0;
    }
  }

  private void addBundle(String className, String name) {
    Class bundleClass = null;
    className += name + ".Messages_" + name;
    
    
    
    try {
      bundleClass = Class.forName(className);
    } catch (Throwable e) {
      Logger.error("GT could not find the class " + className);
    }
    if (bundleClass == null
        || !ResourceBundle.class.isAssignableFrom(bundleClass))
      return;
    try {
      ResourceBundle myBundle = (ResourceBundle) bundleClass.newInstance();
      if (myBundle != null) {
        if (translationResources == null) {
          translationResources = new ResourceBundle[8];
          translationResourcesCount = 0;
        }
        translationResources[translationResourcesCount] = myBundle;
        translationResourcesCount++;
        Logger.debug("GT adding " + className);
      }
    } catch (IllegalAccessException e) {
      Logger.warn("Illegal Access Exception: " + e.getMessage());
    } catch (InstantiationException e) {
      Logger.warn("Instantiation Excaption: " + e.getMessage());
    }
  }

  private static GT getTextWrapper() {
    return (getTextWrapper == null ? getTextWrapper = new GT() : getTextWrapper);
  }

  public static void ignoreApplicationBundle() {
    ignoreApplicationBundle = true;
  }

  public static void setDoTranslate(boolean TF) {
    getTextWrapper().doTranslate = TF;
  }

  public static boolean getDoTranslate() {
    return getTextWrapper().doTranslate;
  }

  public static String _(String string) {
    return getTextWrapper().getString(string);
  }

  public static String _(String string, String item) {
    return getTextWrapper().getString(string, new Object[] { item });
  }

  public static String _(String string, int item) {
    return getTextWrapper().getString(string,
        new Object[] { new Integer(item) });
  }

  public static String _(String string, Object[] objects) {
    return getTextWrapper().getString(string, objects);
  }

  
  
  public static String _(String string, boolean t) {
    return _(string, (Object[]) null, t);
  }

  public static String _(String string, String item, boolean t) {
    return _(string, new Object[] { item });
  }

  public static String _(String string, int item, boolean t) {
    return _(string, new Object[] { new Integer(item) });
  }

  public static synchronized String _(String string, Object[] objects, boolean t) {
    boolean wasTranslating;
    if (!(wasTranslating = getTextWrapper().doTranslate))
      setDoTranslate(true);
    String str = (objects == null ? _(string) : _(string, objects));
    if (!wasTranslating)
      setDoTranslate(false);
    return str;
  }

  private String getString(String string) {
    if (!doTranslate || translationResourcesCount == 0)
      return string;
    for (int bundle = 0; bundle < translationResourcesCount; bundle++) {
      try {
        String trans = translationResources[bundle].getString(string);
        return trans;
      } catch (MissingResourceException e) {
        
      }
    }
    if (Logger.debugging) {
      Logger.info("No trans, using default: " + string);
    }
    return string;
  }

  private String getString(String string, Object[] objects) {
    String trans = null;
    if (!doTranslate)
      return MessageFormat.format(string, objects);
    for (int bundle = 0; bundle < translationResourcesCount; bundle++) {
      try {
        trans = MessageFormat.format(translationResources[bundle]
            .getString(string), objects);
        return trans;
      } catch (MissingResourceException e) {
        
      }
    }
    trans = MessageFormat.format(string, objects);
    if (translationResourcesCount > 0) {
      if (Logger.debugging) {
        Logger.debug("No trans, using default: " + trans);
      }
    }
    return trans;
  }

  public static String escapeHTML(String msg) {
    char ch;
    for (int i = msg.length(); --i >= 0;)
      if ((ch = msg.charAt(i)) > 0x7F) {
        msg = msg.substring(0, i) 
            + "&#" + ((int)ch) + ";" + msg.substring(i + 1);
      }
    return msg;   
  }

  public static void setLanguagePath(String languagePath) {
    
  }
}
