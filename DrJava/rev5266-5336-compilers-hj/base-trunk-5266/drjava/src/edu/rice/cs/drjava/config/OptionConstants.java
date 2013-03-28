

package edu.rice.cs.drjava.config;

import java.io.File;
import java.util.Vector;
import java.util.ArrayList;
import java.util.Arrays;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import edu.rice.cs.drjava.platform.PlatformFactory;

import edu.rice.cs.util.FileOps;

import static java.awt.Event.*;



public interface OptionConstants {
  
  
  
  
  
  
  public static final FileOption BROWSER_FILE = new FileOption("browser.file", FileOps.NULL_FILE);
  
  
  public static final StringOption BROWSER_STRING = new StringOption("browser.string", "");
  
  
  public static final VectorOption<String> INTERACTIONS_AUTO_IMPORT_CLASSES =
    new VectorOption<String>("interactions.auto.import.classes", new StringOption("",""), new Vector<String>());
  
  
  public static final NonNegativeIntegerOption AUTO_STEP_RATE = new NonNegativeIntegerOption("auto.step.rate", 1000);
  
  
  public static final String OLD_PROJECT_FILE_EXTENSION = ".pjt";
  
  
  public static final String PROJECT_FILE_EXTENSION = ".drjava";
  
  
  public static final String PROJECT_FILE_EXTENSION2 = ".xml";
  
  
  public static final String EXTPROCESS_FILE_EXTENSION = ".djapp";

  
  public static final String JAVA_FILE_EXTENSION = ".java";

  
  public static final String DJ_FILE_EXTENSION = ".dj";

  
  public static final String OLD_DJ0_FILE_EXTENSION = ".dj0";

  
  public static final String OLD_DJ1_FILE_EXTENSION = ".dj1";

  
  public static final String OLD_DJ2_FILE_EXTENSION = ".dj2";
    
  
  public static final int FULL_JAVA = 0;
  public static final int ELEMENTARY_LEVEL = 1;
  public static final int INTERMEDIATE_LEVEL = 2;
  public static final int ADVANCED_LEVEL = 3;
  public static final int FUNCTIONAL_JAVA_LEVEL = 4;
  public static final String[] LANGUAGE_LEVEL_EXTENSIONS = new String[] {
    JAVA_FILE_EXTENSION, 
      OLD_DJ0_FILE_EXTENSION, 
      OLD_DJ1_FILE_EXTENSION, 
      OLD_DJ2_FILE_EXTENSION, 
      DJ_FILE_EXTENSION }; 
  
  
  public static final String EXTPROCESS_FILE_NAME_INSIDE_JAR = "process" + EXTPROCESS_FILE_EXTENSION;

  
  public static final String TEXT_FILE_EXTENSION = ".txt";
  
  
  public static final FileOption JAVAC_LOCATION = new FileOption("javac.location", FileOps.NULL_FILE);
  
  
  public static final VectorOption<File> EXTRA_CLASSPATH = new ClassPathOption().evaluate("extra.classpath");
  
  public static final VectorOption<String> EXTRA_COMPILERS =
    new VectorOption<String>("extra.compilers", new StringOption("",""), new Vector<String>());
  
  
  public static final BooleanOption DISPLAY_ALL_COMPILER_VERSIONS = 
    new BooleanOption("all.compiler.versions", Boolean.FALSE);
  
  
  
  
  public static final ColorOption DEFINITIONS_NORMAL_COLOR = new ColorOption("definitions.normal.color", Color.black);
  public static final ColorOption DEFINITIONS_KEYWORD_COLOR = new ColorOption("definitions.keyword.color", Color.blue);
  public static final ColorOption DEFINITIONS_TYPE_COLOR =
    new ColorOption("definitions.type.color", Color.blue.darker().darker());
  public static final ColorOption DEFINITIONS_COMMENT_COLOR =
    new ColorOption("definitions.comment.color", Color.green.darker().darker());
  public static final ColorOption DEFINITIONS_DOUBLE_QUOTED_COLOR =
    new ColorOption("definitions.double.quoted.color", Color.red.darker());
  public static final ColorOption DEFINITIONS_SINGLE_QUOTED_COLOR =
    new ColorOption("definitions.single.quoted.color", Color.magenta);
  public static final ColorOption DEFINITIONS_NUMBER_COLOR =
    new ColorOption("definitions.number.color", Color.cyan.darker());
  public static final ColorOption SYSTEM_OUT_COLOR = new ColorOption("system.out.color", Color.green.darker().darker());
  public static final ColorOption SYSTEM_ERR_COLOR = new ColorOption("system.err.color", Color.red);
  public static final ColorOption SYSTEM_IN_COLOR = new ColorOption("system.in.color", Color.magenta.darker().darker());
  public static final ColorOption INTERACTIONS_ERROR_COLOR =
    new ColorOption("interactions.error.color", Color.red.darker());
  public static final ColorOption DEBUG_MESSAGE_COLOR = new ColorOption("debug.message.color", Color.blue.darker());
  
  
  public static final ColorOption DEFINITIONS_BACKGROUND_COLOR =
    new ColorOption("definitions.background.color", Color.white);
  
  
  public static final ColorOption DEFINITIONS_LINE_NUMBER_BACKGROUND_COLOR =
    new ColorOption("definitions.line.number.background.color",new Color(250, 250, 250));
  
  
  public static final ColorOption DEFINITIONS_LINE_NUMBER_COLOR =
    new ColorOption("definitions.line.number.color", Color.black);
  
  
  public static final ColorOption DEFINITIONS_MATCH_COLOR =
    new ColorOption("definitions.match.color", new Color(190, 255, 230));
  
  
  public static final ColorOption COMPILER_ERROR_COLOR = new ColorOption("compiler.error.color", Color.yellow);
  
  
  public static final ColorOption BOOKMARK_COLOR = new ColorOption("bookmark.color", Color.green);
  
  
  public static final ColorOption FIND_RESULTS_COLOR1 = 
    new ColorOption("find.results.color1", new Color(0xFF, 0x99, 0x33));
  public static final ColorOption FIND_RESULTS_COLOR2 = 
    new ColorOption("find.results.color2", new Color(0x30, 0xC9, 0x96));
  public static final ColorOption FIND_RESULTS_COLOR3 = 
    new ColorOption("find.results.color3", Color.ORANGE);
  public static final ColorOption FIND_RESULTS_COLOR4 = 
    new ColorOption("find.results.color4", Color.MAGENTA);
  public static final ColorOption FIND_RESULTS_COLOR5 = 
    new ColorOption("find.results.color5", new Color(0xCD, 0x5C, 0x5C));
  public static final ColorOption FIND_RESULTS_COLOR6 = 
    new ColorOption("find.results.color6", Color.DARK_GRAY);
  public static final ColorOption FIND_RESULTS_COLOR7 = 
    new ColorOption("find.results.color7", Color.GREEN);
  public static final ColorOption FIND_RESULTS_COLOR8 = 
    new ColorOption("find.results.color8", Color.BLUE);
  
  public static final ColorOption[] FIND_RESULTS_COLORS = new ColorOption[] {
    FIND_RESULTS_COLOR1,
      FIND_RESULTS_COLOR2,
      FIND_RESULTS_COLOR3,
      FIND_RESULTS_COLOR4,
      FIND_RESULTS_COLOR5,
      FIND_RESULTS_COLOR6,
      FIND_RESULTS_COLOR7,
      FIND_RESULTS_COLOR8
  };
  
  
  public static final ColorOption DEBUG_BREAKPOINT_COLOR = new ColorOption("debug.breakpoint.color", Color.red);
  
  
  public static final ColorOption DEBUG_BREAKPOINT_DISABLED_COLOR = 
    new ColorOption("debug.breakpoint.disabled.color", new Color(128,0,0));
  
  
  public static final ColorOption DEBUG_THREAD_COLOR = new ColorOption("debug.thread.color", new Color(100,255,255));
  
  
  public static final ColorOption DRJAVA_ERRORS_BUTTON_COLOR = new ColorOption("drjava.errors.button.color", Color.red);

  
  public static final ColorOption RIGHT_MARGIN_COLOR = new ColorOption("right.margin.color", new Color(204,204,204));
  
  
  
  
  public static final FontOption FONT_MAIN = new FontOption("font.main", DefaultFont.getDefaultMainFont());
  
  
  static class DefaultFont {
    public static Font getDefaultMainFont() {
      if (PlatformFactory.ONLY.isMacPlatform())  return Font.decode("Monaco-12");
      else return Font.decode("Monospaced-12");
    }
    public static Font getDefaultLineNumberFont() {
      if (PlatformFactory.ONLY.isMacPlatform()) return Font.decode("Monaco-12");
      else return Font.decode("Monospaced-12");
    }
    public static Font getDefaultDocListFont() {
      if (PlatformFactory.ONLY.isMacPlatform()) return Font.decode("Monaco-10");
      else return Font.decode("Monospaced-10");
    }
  }
  
  
  public static final FontOption FONT_LINE_NUMBERS =
    new FontOption("font.line.numbers", DefaultFont.getDefaultLineNumberFont());
  
  
  public static final FontOption FONT_DOCLIST = new FontOption("font.doclist", DefaultFont.getDefaultDocListFont());
  
  
  public static final FontOption FONT_TOOLBAR = new FontOption("font.toolbar", Font.decode("dialog-10"));
  
  
  public static final BooleanOption TEXT_ANTIALIAS = new BooleanOption("text.antialias", Boolean.TRUE);

  
  public static final BooleanOption DISPLAY_RIGHT_MARGIN = new BooleanOption("display.right.margin", Boolean.TRUE);

  
  public static final NonNegativeIntegerOption RIGHT_MARGIN_COLUMNS =
    new NonNegativeIntegerOption("right.margin.columns", 120);
  
  
  
  
  
  public static final BooleanOption TOOLBAR_ICONS_ENABLED =
    new BooleanOption("toolbar.icons.enabled", Boolean.TRUE);
  
  
  public static final BooleanOption TOOLBAR_TEXT_ENABLED = new BooleanOption("toolbar.text.enabled", Boolean.TRUE);
  
  
  public static final BooleanOption TOOLBAR_ENABLED = new BooleanOption("toolbar.enabled", Boolean.TRUE);
  
  
  public static final BooleanOption LINEENUM_ENABLED = new BooleanOption("lineenum.enabled", Boolean.FALSE);
  
  
  public static final BooleanOption WINDOW_STORE_POSITION = new BooleanOption("window.store.position", Boolean.TRUE);
  
  
  public static final BooleanOption SHOW_SOURCE_WHEN_SWITCHING = 
    new BooleanOption("show.source.for.fast.switch", Boolean.TRUE);
  
  
  public static final ForcedChoiceOption LOOK_AND_FEEL =
    new ForcedChoiceOption("look.and.feel", LookAndFeels.getDefaultLookAndFeel(), LookAndFeels.getLookAndFeels());
  
  
  static class LookAndFeels {
    private static String[][] _registerLAFs = {
      {"Plastic 3D", "com.jgoodies.looks.plastic.Plastic3DLookAndFeel"},
      {"Plastic XP", "com.jgoodies.looks.plastic.PlasticXPLookAndFeel"},
      {"Plastic Windows", "com.jgoodies.looks.windows.Plastic3DLookAndFeel"},
      {"Plastic", "com.jgoodies.looks.plastic.PlasticLookAndFeel"}
    };
    
    private static boolean _registered = false;
    
    
    public static String getDefaultLookAndFeel() {
      if (PlatformFactory.ONLY.isMacPlatform())
        return UIManager.getSystemLookAndFeelClassName();
      else
        return UIManager.getCrossPlatformLookAndFeelClassName();
    }
    
    
    public static ArrayList<String> getLookAndFeels() {
      if(!_registered && !PlatformFactory.ONLY.isMacPlatform()) {
        for(String[] newLaf : _registerLAFs) {
          try {
            Class.forName(newLaf[1]);
          } catch(ClassNotFoundException ex) {
            continue;
          }
          UIManager.installLookAndFeel(newLaf[0], newLaf[1]);
        }
      }
      ArrayList<String> lookAndFeels = new ArrayList<String>();
      LookAndFeelInfo[] lafis = UIManager.getInstalledLookAndFeels();
      if (lafis != null) {
        for (int i = 0; i < lafis.length; i++) {
          try {
            String currName = lafis[i].getClassName();
            LookAndFeel currLAF = (LookAndFeel) Class.forName(currName).newInstance();
            if (currLAF.isSupportedLookAndFeel()) lookAndFeels.add(currName);
          }
          
          catch (ClassNotFoundException e) {  }
          catch (InstantiationException e) {  }
          catch (IllegalAccessException e) {  }
        }
      }
      return lookAndFeels;
    }
  }
  
  public static final ForcedChoiceOption PLASTIC_THEMES =
    new ForcedChoiceOption("plastic.theme", PlasticThemes.getDefaultTheme(), PlasticThemes.getThemes());
  
  
  static class PlasticThemes {
    public static ArrayList<String> getThemes() {
      ArrayList<String> al = new ArrayList<String>();
      String[] themes = new String[] {
        "BrownSugar", "DarkStar",
          "SkyBlue", "SkyGreen", "SkyKrupp", "SkyPink", "SkyRed", "SkyYellow",
          "DesertBluer", "DesertBlue", "DesertGreen", "DesertRed", "DesertYellow",
          "ExperienceBlue", "ExperienceGreen", "LightGray", "Silver",
          "ExperienceRoyale"
      };
      for(String theme : themes) {
        al.add(theme);
      }
      return al;
    }
    
    public static String getDefaultTheme() {
      return "DesertBlue";
    }
  }
  
  
  public static int MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
  
  static class to {
    public static Vector<KeyStroke> vector(KeyStroke... ks) {
      Vector<KeyStroke> v = new Vector<KeyStroke>();
      for(KeyStroke k: ks) { v.add(k); }
      return v;
    }
  }
  
  
  public static final VectorOption<KeyStroke> KEY_NEW_FILE =
    new VectorOption<KeyStroke>("key.new.file", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_N, MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_NEW_CLASS_FILE =
    new VectorOption<KeyStroke>("key.new.javafile", new KeyStrokeOption("",null), to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_N, MASK|SHIFT_MASK))); 
  
  
  public static final VectorOption<KeyStroke> KEY_OPEN_PROJECT =
    new VectorOption<KeyStroke>("key.open.project", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_I, MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_NEW_TEST = 
    new VectorOption<KeyStroke>("key.new.test", new KeyStrokeOption("",null), to.vector());
  
  
  public static final VectorOption<KeyStroke> KEY_OPEN_FOLDER =
    new VectorOption<KeyStroke>("key.open.folder", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_O, MASK|SHIFT_MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_OPEN_FILE =
    new VectorOption<KeyStroke>("key.open.file", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_O, MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_SAVE_FILE =
    new VectorOption<KeyStroke>("key.save.file", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_S, MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_SAVE_FILE_AS =
    new VectorOption<KeyStroke>("key.save.file.as", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_S, MASK | SHIFT_MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_SAVE_FILE_COPY =
    new VectorOption<KeyStroke>("key.save.file.copy", 
                                new KeyStrokeOption("",null), 
                                to.vector());
  
  
  public static final VectorOption<KeyStroke> KEY_SAVE_ALL_FILES =
    new VectorOption<KeyStroke>("key.save.all.files", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_S, MASK | ALT_MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_EXPORT_OLD = 
    new VectorOption<KeyStroke>("key.export.old", new KeyStrokeOption("",null), to.vector());
  
  
  public static final VectorOption<KeyStroke> KEY_RENAME_FILE = 
    new VectorOption<KeyStroke>("key.rename.file", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_R, MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_REVERT_FILE =
    new VectorOption<KeyStroke>("key.revert.file",
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_R, MASK|SHIFT_MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_CLOSE_FILE =
    new VectorOption<KeyStroke>("key.close.file", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_W, MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_CLOSE_ALL_FILES =
    new VectorOption<KeyStroke>("key.close.all.files", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_W, MASK|ALT_MASK)));
  
  public static final VectorOption<KeyStroke> KEY_CLOSE_PROJECT =
    new VectorOption<KeyStroke>("key.close.project", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_W, MASK|SHIFT_MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_PAGE_SETUP =
    new VectorOption<KeyStroke>("key.page.setup", new KeyStrokeOption("",null), to.vector());
  
  
  public static final VectorOption<KeyStroke> KEY_PRINT_PREVIEW =
    new VectorOption<KeyStroke>("key.print.preview", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_P, MASK | SHIFT_MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_PRINT =
    new VectorOption<KeyStroke>("key.print", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_P, MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_QUIT =
    new VectorOption<KeyStroke>("key.quit", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_Q, MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_FORCE_QUIT =
    new VectorOption<KeyStroke>("key.force.quit", new KeyStrokeOption("",null), to.vector());
  
  
  public static final VectorOption<KeyStroke> KEY_UNDO =
    new VectorOption<KeyStroke>("key.undo", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_Z, MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_REDO =
    new VectorOption<KeyStroke>("key.redo", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_Z, MASK|SHIFT_MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_CUT =
    new VectorOption<KeyStroke>("key.cut", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_X, MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_COPY =
    new VectorOption<KeyStroke>("key.copy", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_C, MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_PASTE =
    new VectorOption<KeyStroke>("key.paste", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_V, MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_PASTE_FROM_HISTORY =
    new VectorOption<KeyStroke>("key.paste.from.history", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_V , MASK|SHIFT_MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_SELECT_ALL =
    new VectorOption<KeyStroke>("key.select.all", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_A, MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_FIND_NEXT =
    new VectorOption<KeyStroke>("key.find.next", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0)));
  
  
  public static final VectorOption<KeyStroke> KEY_FIND_PREV =
    new VectorOption<KeyStroke>("key.find.prev", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_F3, SHIFT_MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_FIND_REPLACE =
    new VectorOption<KeyStroke>("key.find.replace", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_F, MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_GOTO_LINE =
    new VectorOption<KeyStroke>("key.goto.line", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_G, MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_GOTO_FILE =
    new VectorOption<KeyStroke>("key.goto.file", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_G, MASK|KeyEvent.SHIFT_MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_GOTO_FILE_UNDER_CURSOR =
    new VectorOption<KeyStroke>("key.goto.file.under.cursor", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0)));
  
  
  public static final VectorOption<KeyStroke> KEY_OPEN_JAVADOC =
    new VectorOption<KeyStroke>("key.open.javadoc", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_F6, KeyEvent.SHIFT_MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_OPEN_JAVADOC_UNDER_CURSOR =
    new VectorOption<KeyStroke>("key.open.javadoc.under.cursor", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_F6, MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_COMPLETE_FILE =
    new VectorOption<KeyStroke>("key.complete.file", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, MASK|KeyEvent.SHIFT_MASK)));
  





  
  
  public static final VectorOption<KeyStroke> KEY_COMMENT_LINES =
    new VectorOption<KeyStroke>("key.comment.lines", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_SLASH, MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_UNCOMMENT_LINES =
    new VectorOption<KeyStroke>("key.uncomment.lines", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_SLASH, MASK|SHIFT_MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_PREVIOUS_DOCUMENT =
    new VectorOption<KeyStroke>("key.previous.document", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_COMMA, MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_NEXT_DOCUMENT =
    new VectorOption<KeyStroke>("key.next.document",
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_PERIOD, MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_PREVIOUS_PANE =
    new VectorOption<KeyStroke>("key.previous.pane", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_OPEN_BRACKET, MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_NEXT_PANE =
    new VectorOption<KeyStroke>("key.next.pane", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_CLOSE_BRACKET, MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_OPENING_BRACE =
    new VectorOption<KeyStroke>("key.goto.opening.brace", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_OPEN_BRACKET, MASK|SHIFT_MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_CLOSING_BRACE =
    new VectorOption<KeyStroke>("key.goto.closing.brace", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_CLOSE_BRACKET, MASK|SHIFT_MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_BROWSE_FORWARD =
    new VectorOption<KeyStroke>("key.browse.forward", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, ALT_MASK|SHIFT_MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_BROWSE_BACK =
    new VectorOption<KeyStroke>("key.browse.back", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, ALT_MASK|SHIFT_MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_TABBED_NEXT_REGION =
    new VectorOption<KeyStroke>("key.tabbed.next.region", 
                                new KeyStrokeOption("",null),
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, ALT_MASK|SHIFT_MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_TABBED_PREV_REGION =
    new VectorOption<KeyStroke>("key.tabbed.prev.region", 
                                new KeyStrokeOption("",null),
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_UP, ALT_MASK|SHIFT_MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_PREFERENCES =
    new VectorOption<KeyStroke>("key.preferences", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_SEMICOLON, MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_COMPILE =
    new VectorOption<KeyStroke>("key.compile", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_F5, SHIFT_MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_COMPILE_ALL =
    new VectorOption<KeyStroke>("key.compile.all", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0)));
  
  
  public static final VectorOption<KeyStroke> KEY_RUN =
    new VectorOption<KeyStroke>("key.run", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0)));
  
  
  public static final VectorOption<KeyStroke> KEY_RUN_APPLET =
    new VectorOption<KeyStroke>("key.run.applet", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_F2, SHIFT_MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_TEST =
    new VectorOption<KeyStroke>("key.test", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_T, MASK|SHIFT_MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_TEST_ALL =
    new VectorOption<KeyStroke>("key.test.all", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_T, MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_JAVADOC_ALL =
    new VectorOption<KeyStroke>("key.javadoc.all", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_J, MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_JAVADOC_CURRENT =
    new VectorOption<KeyStroke>("key.javadoc.current", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_J, MASK | SHIFT_MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_SAVE_INTERACTIONS_COPY =
    new VectorOption<KeyStroke>("key.save.interactions.copy", new KeyStrokeOption("",null), to.vector());
  
  
  public static final VectorOption<KeyStroke> KEY_EXECUTE_HISTORY =
    new VectorOption<KeyStroke>("key.execute.history", new KeyStrokeOption("",null), to.vector());
  
  
  public static final VectorOption<KeyStroke> KEY_LOAD_HISTORY_SCRIPT =
    new VectorOption<KeyStroke>("key.load.history.script", new KeyStrokeOption("",null), to.vector());
  
  
  public static final VectorOption<KeyStroke> KEY_SAVE_HISTORY =
    new VectorOption<KeyStroke>("key.save.history", new KeyStrokeOption("",null), to.vector());
  
  
  public static final VectorOption<KeyStroke> KEY_CLEAR_HISTORY =
    new VectorOption<KeyStroke>("key.clear.history", new KeyStrokeOption("",null), to.vector());
  
  
  public static final VectorOption<KeyStroke> KEY_RESET_INTERACTIONS =
    new VectorOption<KeyStroke>("key.reset.interactions", new KeyStrokeOption("",null), to.vector());
  
  
  public static final VectorOption<KeyStroke> KEY_VIEW_INTERACTIONS_CLASSPATH =
    new VectorOption<KeyStroke>("key.view.interactions.classpath", new KeyStrokeOption("",null), to.vector());
  
  
  public static final VectorOption<KeyStroke> KEY_PRINT_INTERACTIONS =
    new VectorOption<KeyStroke>("key.view.print.interactions", new KeyStrokeOption("",null), to.vector());
  
  
  public static final VectorOption<KeyStroke> KEY_LIFT_CURRENT_INTERACTION =
    new VectorOption<KeyStroke>("key.lift.current.interaction", new KeyStrokeOption("",null), to.vector());
  





  
  
  public static final VectorOption<KeyStroke> KEY_SAVE_CONSOLE_COPY =
    new VectorOption<KeyStroke>("key.save.console.copy", new KeyStrokeOption("",null), to.vector());

  
  public static final VectorOption<KeyStroke> KEY_CLEAR_CONSOLE =
    new VectorOption<KeyStroke>("key.clear.console", new KeyStrokeOption("",null), to.vector());
  
  
  public static final VectorOption<KeyStroke> KEY_PRINT_CONSOLE =
    new VectorOption<KeyStroke>("key.view.print.console", new KeyStrokeOption("",null), to.vector());
  
  
  public static final VectorOption<KeyStroke> KEY_BACKWARD =
    new VectorOption<KeyStroke>("key.backward", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0)));
  
  
  public static final VectorOption<KeyStroke> KEY_BACKWARD_SELECT =
    new VectorOption<KeyStroke>("key.backward.select", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, SHIFT_MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_BEGIN_DOCUMENT =
    new VectorOption<KeyStroke>("key.begin.document",
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_BEGIN_DOCUMENT_SELECT =
    new VectorOption<KeyStroke>("key.begin.document.select", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, SHIFT_MASK|MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_BEGIN_LINE =
    new VectorOption<KeyStroke>("key.begin.line",
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0)));
  
  
  public static final VectorOption<KeyStroke> KEY_BEGIN_LINE_SELECT =
    new VectorOption<KeyStroke>("key.begin.line.select", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, SHIFT_MASK)));
  





  
  
  public static final VectorOption<KeyStroke> KEY_PREVIOUS_WORD =
    new VectorOption<KeyStroke>("key.previous.word", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_PREVIOUS_WORD_SELECT =
    new VectorOption<KeyStroke>("key.previous.word.select", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, SHIFT_MASK|MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_DELETE_NEXT =
    new VectorOption<KeyStroke>("key.delete.next", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0)));
  
  
  public static final VectorOption<KeyStroke> KEY_DELETE_PREVIOUS =
    new VectorOption<KeyStroke>("key.delete.previous", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0)));
  
  
  public static final VectorOption<KeyStroke> KEY_SHIFT_DELETE_NEXT =
    new VectorOption<KeyStroke>("key.delete.next", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, SHIFT_MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_SHIFT_DELETE_PREVIOUS =
    new VectorOption<KeyStroke>("key.delete.previous", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, SHIFT_MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_DOWN =
    new VectorOption<KeyStroke>("key.down", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0)));
  
  
  public static final VectorOption<KeyStroke> KEY_DOWN_SELECT =
    new VectorOption<KeyStroke>("key.down.select", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, SHIFT_MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_UP =
    new VectorOption<KeyStroke>("key.up", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0)));
  
  
  public static final VectorOption<KeyStroke> KEY_UP_SELECT =
    new VectorOption<KeyStroke>("key.up.select",
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_UP, SHIFT_MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_END_DOCUMENT =
    new VectorOption<KeyStroke>("key.end.document", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_END, MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_END_DOCUMENT_SELECT =
    new VectorOption<KeyStroke>("key.end.document.select", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_END, SHIFT_MASK|MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_END_LINE =
    new VectorOption<KeyStroke>("key.end.line", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_END, 0)));
  
  
  public static final VectorOption<KeyStroke> KEY_END_LINE_SELECT =
    new VectorOption<KeyStroke>("key.end.line.select", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_END, SHIFT_MASK)));
  





  
  
  public static final VectorOption<KeyStroke> KEY_NEXT_WORD =
    new VectorOption<KeyStroke>("key.next.word", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_NEXT_WORD_SELECT =
    new VectorOption<KeyStroke>("key.next.word.select",
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, SHIFT_MASK|MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_FORWARD =
    new VectorOption<KeyStroke>("key.forward", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0)));
  
  
  public static final VectorOption<KeyStroke> KEY_FORWARD_SELECT =
    new VectorOption<KeyStroke>("key.forward.select", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, SHIFT_MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_PAGE_DOWN =
    new VectorOption<KeyStroke>("key.page.down",
                                new KeyStrokeOption("",null),
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0)));
  
  
  public static final VectorOption<KeyStroke> KEY_PAGE_UP =
    new VectorOption<KeyStroke>("key.page.up", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0)));
  




  




  
  
  public static final VectorOption<KeyStroke> KEY_CUT_LINE =
    new VectorOption<KeyStroke>("key.cut.line", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_K, MASK|ALT_MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_CLEAR_LINE =
    new VectorOption<KeyStroke>("key.clear.line", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_K, MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_DEBUG_MODE_TOGGLE =
    new VectorOption<KeyStroke>("key.debug.mode.toggle", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_D, MASK | SHIFT_MASK)));
  





  
  
  public static final VectorOption<KeyStroke> KEY_DEBUG_RESUME =
    new VectorOption<KeyStroke>("key.debug.resume", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0)));
  
  
  public static final VectorOption<KeyStroke> KEY_DEBUG_AUTOMATIC_TRACE = 
    new VectorOption<KeyStroke>("key.debug.automatic.trace", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0)));
  
  
  public static final VectorOption<KeyStroke> KEY_DEBUG_STEP_INTO =
    new VectorOption<KeyStroke>("key.debug.step.into", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0)));
  
  
  public static final VectorOption<KeyStroke> KEY_DEBUG_STEP_OVER =
    new VectorOption<KeyStroke>("key.debug.step.over", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0)));
  
  
  public static final VectorOption<KeyStroke> KEY_DEBUG_STEP_OUT =
    new VectorOption<KeyStroke>("key.debug.step.out", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_F12, SHIFT_MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_DEBUG_BREAKPOINT_TOGGLE =
    new VectorOption<KeyStroke>("key.debug.breakpoint.toggle",
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_B, MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_DEBUG_BREAKPOINT_PANEL =
    new VectorOption<KeyStroke>("key.debug.breakpoint.panel", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_B, MASK | SHIFT_MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_DEBUG_CLEAR_ALL_BREAKPOINTS =
    new VectorOption<KeyStroke>("key.debug.clear.all.breakpoints", 
                                new KeyStrokeOption("",null), 
                                to.vector());
  
  
  public static final VectorOption<KeyStroke> KEY_BOOKMARKS_TOGGLE =
    new VectorOption<KeyStroke>("key.bookmarks.toggle", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_M, MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_BOOKMARKS_PANEL =
    new VectorOption<KeyStroke>("key.bookmarks.panel", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_M, MASK | SHIFT_MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_HELP =
    new VectorOption<KeyStroke>("key.help",
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0)));
  
  
  public static final VectorOption<KeyStroke> KEY_QUICKSTART = 
    new VectorOption<KeyStroke>("key.quickstart", new KeyStrokeOption("",null), to.vector());
  
  
  public static final VectorOption<KeyStroke> KEY_ABOUT = 
    new VectorOption<KeyStroke>("key.about", new KeyStrokeOption("",null), to.vector());
  
  
  public static final VectorOption<KeyStroke> KEY_CHECK_NEW_VERSION = 
    new VectorOption<KeyStroke>("key.check.new.version", new KeyStrokeOption("",null), to.vector());
  
  
  public static final VectorOption<KeyStroke> KEY_DRJAVA_SURVEY = 
    new VectorOption<KeyStroke>("key.drjava.survey", new KeyStrokeOption("",null), to.vector());
  
  
  public static final VectorOption<KeyStroke> KEY_DRJAVA_ERRORS = 
    new VectorOption<KeyStroke>("key.drjava.errors", new KeyStrokeOption("",null), to.vector());
  
  
  public static final VectorOption<KeyStroke> KEY_FOLLOW_FILE =
    new VectorOption<KeyStroke>("key.follow.file",
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_L, MASK | SHIFT_MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_EXEC_PROCESS =
    new VectorOption<KeyStroke>("key.exec.process", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_X, MASK | SHIFT_MASK)));
  
  
  public static final VectorOption<KeyStroke> KEY_DETACH_TABBEDPANES = 
    new VectorOption<KeyStroke>("key.detach.tabbedpanes", new KeyStrokeOption("",null), to.vector());
  
  
  public static final VectorOption<KeyStroke> KEY_DETACH_DEBUGGER = 
    new VectorOption<KeyStroke>("key.detach.debugger", new KeyStrokeOption("",null), to.vector());
  
  
  public static final VectorOption<KeyStroke> KEY_CLOSE_SYSTEM_IN = 
    new VectorOption<KeyStroke>("key.close.system.in", 
                                new KeyStrokeOption("",null), 
                                to.vector(KeyStroke.getKeyStroke(KeyEvent.VK_D, CTRL_MASK)));
  
  
  public static final KeyStrokeOption KEY_FOR_UNIT_TESTS_ONLY = 
    new KeyStrokeOption("key.for.unit.tests.only", KeyStroke.getKeyStroke(KeyEvent.VK_N, CTRL_MASK|SHIFT_MASK|MASK));

  
  public static final VectorOption<KeyStroke> KEY_GENERATE_CUSTOM_DRJAVA = 
    new VectorOption<KeyStroke>("key.generate.custom.drjava", new KeyStrokeOption("",null), to.vector());
  
  
  
  public static final BooleanOption FIND_MATCH_CASE = 
    new BooleanOption("find.replace.match.case", Boolean.TRUE);
  
  public static final BooleanOption FIND_SEARCH_BACKWARDS = 
    new BooleanOption("find.replace.search.backwards", Boolean.FALSE);
  
  public static final BooleanOption FIND_WHOLE_WORD = 
    new BooleanOption("find.replace.whole.word", Boolean.FALSE);
  
  public static final BooleanOption FIND_ALL_DOCUMENTS = 
    new BooleanOption("find.replace.all.documents", Boolean.FALSE);
  
  public static final BooleanOption FIND_ONLY_SELECTION = 
    new BooleanOption("find.replace.only.selection", Boolean.FALSE);
  
  public static final BooleanOption FIND_NO_COMMENTS_STRINGS =
    new BooleanOption("find.replace.no.comments.strings", Boolean.FALSE);
  
  public static final BooleanOption FIND_NO_TEST_CASES =
    new BooleanOption("find.replace.no.test.cases", Boolean.FALSE);
  
  
  
  
  public static final VectorOption<File> DEBUG_SOURCEPATH =
    new ClassPathOption().evaluate("debug.sourcepath");
  
  
  public static final BooleanOption DEBUG_STEP_JAVA = new BooleanOption("debug.step.java", Boolean.FALSE);
  
  
  public static final BooleanOption DEBUG_STEP_INTERPRETER =
    new BooleanOption("debug.step.interpreter", Boolean.FALSE);
  
  
  public static final BooleanOption DEBUG_STEP_DRJAVA =
    new BooleanOption("debug.step.drjava", Boolean.FALSE);
  
  
  public static final VectorOption<String> DEBUG_STEP_EXCLUDE =
    new VectorOption<String>("debug.step.exclude", new StringOption("",null), new Vector<String>());
  
  
  public static final BooleanOption DEBUG_AUTO_IMPORT =
    new BooleanOption("debug.auto.import", Boolean.TRUE);
  
  
  public static final BooleanOption DEBUG_EXPRESSIONS_AND_METHODS_IN_WATCHES =
    new BooleanOption("debug.expressions.and.methods.in.watches", Boolean.FALSE);
  
  
  
  
  
  static final ArrayList<String> accessLevelChoices =
    AccessLevelChoices.evaluate();
  public static class AccessLevelChoices {
    public static final String PUBLIC = "public";
    public static final String PROTECTED = "protected";
    public static final String PACKAGE = "package";
    public static final String PRIVATE = "private";
    public static ArrayList<String> evaluate() {
      ArrayList<String> aList = new ArrayList<String>(4);
      aList.add(PUBLIC);
      aList.add(PROTECTED);
      aList.add(PACKAGE);
      aList.add(PRIVATE);
      return aList;
    }
  }
  
  
  public static final ForcedChoiceOption JAVADOC_ACCESS_LEVEL =
    new ForcedChoiceOption("javadoc.access.level", AccessLevelChoices.PACKAGE, accessLevelChoices);
  
  
  static final String JAVADOC_NONE_TEXT = "none";
  static final String JAVADOC_1_3_TEXT = "1.3";
  static final String JAVADOC_1_4_TEXT = "1.4";
  static final String JAVADOC_1_5_TEXT = "1.5";
  static final String JAVADOC_1_6_TEXT = "1.6";
  static final String JAVADOC_AUTO_TEXT = "use compiler version"; 
  
  static final String[] linkChoices = new String[]{
    JAVADOC_NONE_TEXT, JAVADOC_1_5_TEXT, JAVADOC_1_6_TEXT };
  static final ArrayList<String> linkVersionChoices = new ArrayList<String>(Arrays.asList(linkChoices));

  static final String[] linkDeprecated = new String[]{
    JAVADOC_1_3_TEXT, JAVADOC_1_4_TEXT };
  static final ArrayList<String> linkVersionDeprecated = new ArrayList<String>(Arrays.asList(linkDeprecated));  
  
  
  public static final StringOption JAVADOC_1_3_LINK =
    new StringOption("javadoc.1.3.link", "http://java.sun.com/j2se/1.3/docs/api");
  public static final StringOption JAVADOC_1_4_LINK =
    new StringOption("javadoc.1.4.link", "http://java.sun.com/j2se/1.4/docs/api");
  public static final StringOption JAVADOC_1_5_LINK =
    new StringOption("javadoc.1.5.link", "http://java.sun.com/j2se/1.5/docs/api");
  public static final StringOption JAVADOC_1_6_LINK =
    new StringOption("javadoc.1.6.link", "http://java.sun.com/javase/6/docs/api");
  
  
  public static final ForcedChoiceOption JAVADOC_LINK_VERSION =
    new ForcedChoiceOption("javadoc.link.version",
                           (System.getProperty("java.specification.version").startsWith("1.5") ? JAVADOC_1_5_TEXT : 
                              JAVADOC_1_6_TEXT),
                           linkVersionChoices, linkVersionDeprecated);
  
  static final String[] apiJavadocChoices = new String[] {
    JAVADOC_1_5_TEXT, JAVADOC_1_6_TEXT, JAVADOC_AUTO_TEXT};
  static final ArrayList<String> apiJavadocVersionChoices = new ArrayList<String>(Arrays.asList(apiJavadocChoices));

  static final String[] apiJavadocDeprecated = new String[] {
    JAVADOC_1_3_TEXT, JAVADOC_1_4_TEXT}; 
  static final ArrayList<String> apiJavadocVersionDeprecated = new ArrayList<String>(Arrays.asList(apiJavadocDeprecated));  
  
  
  public static final ForcedChoiceOption JAVADOC_API_REF_VERSION =
    new ForcedChoiceOption("javadoc.api.ref.version", JAVADOC_AUTO_TEXT,
                           apiJavadocVersionChoices, apiJavadocVersionDeprecated);
  
  
  public static final StringOption JUNIT_LINK =
    new StringOption("junit.link", "http://www.cs.rice.edu/~javaplt/javadoc/concjunit4.7");
  
  
  public static final VectorOption<String> JAVADOC_ADDITIONAL_LINKS =
    new VectorOption<String>("javadoc.additional.links", new StringOption("",null), new Vector<String>());
  
  
  public static final BooleanOption JAVADOC_FROM_ROOTS = new BooleanOption("javadoc.from.roots", Boolean.FALSE);
  
  
  public static final StringOption JAVADOC_CUSTOM_PARAMS = 
    new StringOption("javadoc.custom.params", "-author -version");
  
  
  public static final FileOption JAVADOC_DESTINATION = new FileOption("javadoc.destination", FileOps.NULL_FILE);
  
  
  public static final BooleanOption JAVADOC_PROMPT_FOR_DESTINATION =
    new BooleanOption("javadoc.prompt.for.destination", Boolean.TRUE);
  
  
  
  
  public static final BooleanOption INTERACTIONS_EXIT_PROMPT =
    new BooleanOption("interactions.exit.prompt", Boolean.TRUE);
  
  
  public static final BooleanOption QUIT_PROMPT = new BooleanOption("quit.prompt", Boolean.TRUE);
  
  
  public static final BooleanOption INTERACTIONS_RESET_PROMPT =
    new BooleanOption("interactions.reset.prompt", Boolean.TRUE);
  
  
  public static final BooleanOption ALWAYS_SAVE_BEFORE_COMPILE =
    new BooleanOption("save.before.compile", Boolean.FALSE);
  
  
  public static final BooleanOption ALWAYS_SAVE_BEFORE_RUN =
    new BooleanOption("save.before.run", Boolean.FALSE);
  
  
  public static final BooleanOption ALWAYS_COMPILE_BEFORE_JUNIT =
    new BooleanOption("compile.before.junit", Boolean.FALSE);
  
  
  public static final BooleanOption ALWAYS_SAVE_BEFORE_JAVADOC =
    new BooleanOption("save.before.javadoc", Boolean.FALSE);

  
  public static final BooleanOption ALWAYS_COMPILE_BEFORE_JAVADOC =
    new BooleanOption("compile.before.javadoc", Boolean.FALSE);
  
  
  public static final BooleanOption ALWAYS_SAVE_BEFORE_DEBUG =
    new BooleanOption("save.before.debug", Boolean.FALSE);
  
  
  public static final BooleanOption WARN_BREAKPOINT_OUT_OF_SYNC =
    new BooleanOption("warn.breakpoint.out.of.sync", Boolean.TRUE);
  
  
  public static final BooleanOption WARN_DEBUG_MODIFIED_FILE =
    new BooleanOption("warn.debug.modified.file", Boolean.TRUE);
  
  
  public static final BooleanOption WARN_CHANGE_LAF = new BooleanOption("warn.change.laf", Boolean.TRUE);
  
  
  public static final BooleanOption WARN_CHANGE_THEME = new BooleanOption("warn.change.theme", Boolean.TRUE);
  
  
  public static final BooleanOption WARN_CHANGE_MISC = new BooleanOption("warn.change.misc", Boolean.TRUE);
  
  
  public static final BooleanOption WARN_CHANGE_INTERACTIONS = new BooleanOption("warn.change.interactions", Boolean.TRUE);
  
  
  public static final BooleanOption WARN_PATH_CONTAINS_POUND =
    new BooleanOption("warn.path.contains.pound", Boolean.TRUE);
  
  
  public static final BooleanOption WARN_CHANGE_DCP = new BooleanOption("warn.change.dcp", Boolean.TRUE);

  
  public static final BooleanOption PROMPT_RENAME_LL_FILES = new BooleanOption("prompt.rename.ll.files", Boolean.TRUE);
  
  
  
  
  public static final BooleanOption PROMPT_BEFORE_CLEAN = new BooleanOption("prompt.before.clean", Boolean.TRUE);
  
  
  public static final BooleanOption OPEN_FOLDER_RECURSIVE =  new BooleanOption("open.folder.recursive", Boolean.FALSE);
  
  
  public static final NonNegativeIntegerOption INDENT_LEVEL = 
    new NonNegativeIntegerOption("indent.level", Integer.valueOf(2));
  
  
  public static final NonNegativeIntegerOption HISTORY_MAX_SIZE =
    new NonNegativeIntegerOption("history.max.size", Integer.valueOf(500));
  
  
  public static final NonNegativeIntegerOption RECENT_FILES_MAX_SIZE =
    new NonNegativeIntegerOption("recent.files.max.size", Integer.valueOf(5));
  
  
  public static final BooleanOption AUTO_CLOSE_COMMENTS = new BooleanOption("auto.close.comments", Boolean.FALSE);
  
  
  public static final BooleanOption RESET_CLEAR_CONSOLE = new BooleanOption("reset.clear.console", Boolean.TRUE);
  
  
  public static final BooleanOption RUN_WITH_ASSERT = new BooleanOption("run.with.assert", Boolean.TRUE);

  
  public static final BooleanOption SMART_RUN_FOR_APPLETS_AND_PROGRAMS =
    new BooleanOption("smart.run.for.applets.and.programs", Boolean.TRUE);
  
  
  public static final BooleanOption BACKUP_FILES = new BooleanOption("files.backup", Boolean.TRUE);
  
  
  @Deprecated public static final BooleanOption ALLOW_PRIVATE_ACCESS = new BooleanOption("allow.private.access", Boolean.FALSE);
  
  
  public static final BooleanOption FORCE_TEST_SUFFIX = new BooleanOption("force.test.suffix", Boolean.FALSE);
  
  
  public static final BooleanOption REMOTE_CONTROL_ENABLED = new BooleanOption("remote.control.enabled", Boolean.TRUE);
  
  
  public static final IntegerOption REMOTE_CONTROL_PORT = new IntegerOption("remote.control.port", Integer.valueOf(4444));
  
  
  public static final BooleanOption WARN_IF_COMPIZ = new BooleanOption("warn.if.compiz", Boolean.TRUE);
  
  
  
  
  public static final BooleanOption SHOW_UNCHECKED_WARNINGS = 
    new BooleanOption("show.unchecked.warnings", Boolean.TRUE);
  
  
  public static final BooleanOption SHOW_DEPRECATION_WARNINGS = 
    new BooleanOption("show.deprecation.warnings", Boolean.TRUE);
  
  
  public static final BooleanOption SHOW_FINALLY_WARNINGS = new BooleanOption("show.finally.warnings", Boolean.FALSE);
  
  
  public static final BooleanOption SHOW_SERIAL_WARNINGS = 
    new BooleanOption("show.serial.warnings", Boolean.FALSE);
  
  
  public static final BooleanOption SHOW_FALLTHROUGH_WARNINGS = 
    new BooleanOption("show.fallthrough.warnings", Boolean.FALSE);
  
  
  public static final BooleanOption SHOW_PATH_WARNINGS = 
    new BooleanOption("show.path.warnings", Boolean.FALSE);
  
  
  public static final StringOption DEFAULT_COMPILER_PREFERENCE = 
    new StringOption("default.compiler.preference", COMPILER_PREFERENCE_CONTROL.NO_PREFERENCE);
  
  
  public static final class COMPILER_PREFERENCE_CONTROL
  {
    public static final String NO_PREFERENCE = "No Preference";
    public static ArrayList<String> _list = new ArrayList<String>();
    
    public static void setList(ArrayList<String> list) {_list = list;}
    public static ForcedChoiceOption evaluate() 
    {
      _list.add(NO_PREFERENCE);
      
      ForcedChoiceOption fco;
      String defaultC = edu.rice.cs.drjava.DrJava.getConfig().getSetting(DEFAULT_COMPILER_PREFERENCE);
 
      if(_list.contains(defaultC)) 
      {
        fco = new ForcedChoiceOption("default.compiler.preference.control", defaultC, _list);
      }
      else
      {
        fco = new ForcedChoiceOption("compiler.preference.control", NO_PREFERENCE, _list);
        edu.rice.cs.drjava.DrJava.getConfig().setSetting(DEFAULT_COMPILER_PREFERENCE,NO_PREFERENCE);
      }
      
      edu.rice.cs.drjava.DrJava.getConfig().setSetting(fco, edu.rice.cs.drjava.DrJava.getConfig().getSetting(DEFAULT_COMPILER_PREFERENCE));
      return fco;
    }
  }
  
  
  
  
  public static final IntegerOption LANGUAGE_LEVEL = new IntegerOption("language.level", Integer.valueOf(0));
  
  
  public static final VectorOption<File> RECENT_FILES =
    new VectorOption<File>("recent.files",new FileOption("",null),new Vector<File>());
  
  
  public static final VectorOption<File> RECENT_PROJECTS =
    new VectorOption<File>("recent.projects",new FileOption("",null),new Vector<File>());
  
  
  public static final BooleanOption SHOW_DEBUG_CONSOLE = new BooleanOption("show.debug.console", Boolean.FALSE);
  
  
  public static final NonNegativeIntegerOption WINDOW_HEIGHT =
    new NonNegativeIntegerOption("window.height", Integer.valueOf(700));
  
  
  public static final NonNegativeIntegerOption WINDOW_WIDTH =
    new NonNegativeIntegerOption("window.width", Integer.valueOf(800));
  
  
  public static final IntegerOption WINDOW_X = new IntegerOption("window.x",  Integer.valueOf(Integer.MAX_VALUE));
  
  
  public static final IntegerOption WINDOW_Y = new IntegerOption("window.y", Integer.valueOf(Integer.MAX_VALUE));
  
  
  public static final IntegerOption WINDOW_STATE =
    new IntegerOption("window.state", Integer.valueOf(Frame.NORMAL));
  
  
  public static final NonNegativeIntegerOption DOC_LIST_WIDTH =
    new NonNegativeIntegerOption("doc.list.width", Integer.valueOf(150));
  
  
  public static final NonNegativeIntegerOption TABS_HEIGHT =
    new NonNegativeIntegerOption("tabs.height", Integer.valueOf(120));
  
  
  public static final NonNegativeIntegerOption DEBUG_PANEL_HEIGHT =
    new NonNegativeIntegerOption("debug.panel.height", Integer.valueOf(0));
  
  
  public static final FileOption LAST_DIRECTORY = new FileOption("last.dir", FileOps.NULL_FILE);
  
  
  public static final FileOption LAST_INTERACTIONS_DIRECTORY = new FileOption("last.interactions.dir", FileOps.NULL_FILE);
  
  
  public static final FileOption FIXED_INTERACTIONS_DIRECTORY = new FileOption("fixed.interactions.dir", FileOps.NULL_FILE);
  
  
  public static final BooleanOption STICKY_INTERACTIONS_DIRECTORY =
    new BooleanOption("sticky.interactions.dir", Boolean.TRUE);
  
  
  public static final BooleanOption DYNAMICJAVA_REQUIRE_SEMICOLON =
    new BooleanOption("dynamicjava.require.semicolon", Boolean.FALSE);
  
  
  public static final BooleanOption DYNAMICJAVA_REQUIRE_VARIABLE_TYPE =
    new BooleanOption("dynamicjava.require.variable.type", Boolean.TRUE);
  
  
  
  public static final ArrayList<String> DYNAMICJAVA_ACCESS_CONTROL_CHOICES =
    DynamicJavaAccessControlChoices.evaluate();
  public static class DynamicJavaAccessControlChoices {
    public static final String DISABLED = "disabled";
    public static final String PRIVATE = "private only";
    public static final String PRIVATE_AND_PACKAGE = "private and package only";
    public static ArrayList<String> evaluate() {
      ArrayList<String> aList = new ArrayList<String>(4);
      aList.add(DISABLED);
      aList.add(PRIVATE);
      
      
      
      aList.add(PRIVATE_AND_PACKAGE); 
      return aList;
    }
  }
  
  
  public static final ForcedChoiceOption DYNAMICJAVA_ACCESS_CONTROL =
    new ForcedChoiceOption("dynamicjava.access.control", DynamicJavaAccessControlChoices.PRIVATE_AND_PACKAGE,
                           DYNAMICJAVA_ACCESS_CONTROL_CHOICES);
  
  
  public static final StringOption MASTER_JVM_ARGS = new StringOption("master.jvm.args", "");
  
  
  public static final StringOption SLAVE_JVM_ARGS = new StringOption("slave.jvm.args", "");
  
  
  public static final ArrayList<String> heapSizeChoices = HeapSizeChoices.evaluate();
  static class HeapSizeChoices {
    public static ArrayList<String> evaluate() {
      ArrayList<String> aList = new ArrayList<String>(4);
      aList.add("default");
      aList.add("64");
      aList.add("128");
      aList.add("256");
      aList.add("512");
      aList.add("768");
      aList.add("1024");
      aList.add("1536");
      aList.add("2048");
      aList.add("2560");
      aList.add("3072");
      aList.add("3584");
      aList.add("4096");
      return aList;
    }
  }
  
  
  public static final ForcedChoiceOption MASTER_JVM_XMX =
    new ForcedChoiceOption("master.jvm.xmx", "default", heapSizeChoices);
  
  
  public static final ForcedChoiceOption SLAVE_JVM_XMX =
    new ForcedChoiceOption("slave.jvm.xmx", "default", heapSizeChoices);
  
  
  public static final StringOption DIALOG_CLIPBOARD_HISTORY_STATE = new StringOption("dialog.clipboard.history.state", "default");
  
  
  public static final BooleanOption DIALOG_CLIPBOARD_HISTORY_STORE_POSITION =
    new BooleanOption("dialog.clipboardhistory.store.position", Boolean.TRUE);
  
  
  public static final NonNegativeIntegerOption CLIPBOARD_HISTORY_SIZE =
    new NonNegativeIntegerOption("clipboardhistory.store.size", 10);
  
  
  public static final StringOption DIALOG_GOTOFILE_STATE = new StringOption("dialog.gotofile.state", "default");
  
  
  public static final BooleanOption DIALOG_GOTOFILE_STORE_POSITION =
    new BooleanOption("dialog.gotofile.store.position", Boolean.TRUE);
  
  
  public static final StringOption DIALOG_OPENJAVADOC_STATE = new StringOption("dialog.openjavadoc.state", "default");
  
  
  public static final BooleanOption DIALOG_OPENJAVADOC_STORE_POSITION =
    new BooleanOption("dialog.openjavadoc.store.position", Boolean.TRUE);
  
  
  public static final StringOption DIALOG_AUTOIMPORT_STATE = new StringOption("dialog.autoimport.state", "default");
  
  
  public static final BooleanOption DIALOG_AUTOIMPORT_STORE_POSITION =
    new BooleanOption("dialog.autoimport.store.position", Boolean.TRUE);
  
  
  public static final NonNegativeIntegerOption BROWSER_HISTORY_MAX_SIZE =
    new NonNegativeIntegerOption("browser.history.max.size", Integer.valueOf(50));
  
  
  public static final BooleanOption DIALOG_GOTOFILE_FULLY_QUALIFIED =
    new BooleanOption("dialog.gotofile.fully.qualified", Boolean.FALSE);
  
  
  public static final StringOption DIALOG_COMPLETE_WORD_STATE = new StringOption("dialog.completeword.state", "default");
  
  
  public static final BooleanOption DIALOG_COMPLETE_WORD_STORE_POSITION =
    new BooleanOption("dialog.completeword.store.position", Boolean.TRUE);
  
  
  public static final BooleanOption DIALOG_COMPLETE_SCAN_CLASS_FILES =
    new BooleanOption("dialog.completeword.scan.class.files", Boolean.FALSE);
  
  
  public static final BooleanOption DIALOG_COMPLETE_JAVAAPI =
    new BooleanOption("dialog.completeword.javaapi", Boolean.FALSE);
  

  
  public static final BooleanOption LIGHTWEIGHT_PARSING_ENABLED =
    new BooleanOption("lightweight.parsing.enabled", Boolean.FALSE);
  
  
  public static final NonNegativeIntegerOption DIALOG_LIGHTWEIGHT_PARSING_DELAY =
    new NonNegativeIntegerOption("lightweight.parsing.delay", Integer.valueOf(500));
  
  
  public static final StringOption DIALOG_TABBEDPANES_STATE = new StringOption("tabbedpanes.state", "default");
  
  
  public static final BooleanOption DIALOG_TABBEDPANES_STORE_POSITION =
    new BooleanOption("tabbedpanes.store.position", Boolean.TRUE);
  
  
  public static final BooleanOption DETACH_TABBEDPANES =
    new BooleanOption("tabbedpanes.detach", Boolean.FALSE);
  
  
  public static final StringOption DIALOG_DEBUGFRAME_STATE = new StringOption("debugger.state", "default");
  
  
  public static final BooleanOption DIALOG_DEBUGFRAME_STORE_POSITION =
    new BooleanOption("debugger.store.position", Boolean.TRUE);
  
  
  public static final BooleanOption DETACH_DEBUGGER =
    new BooleanOption("debugger.detach", Boolean.FALSE);
  
  
  public static final StringOption DIALOG_JAROPTIONS_STATE = new StringOption("dialog.jaroptions.state", "default");
  
  
  public static final BooleanOption DIALOG_JAROPTIONS_STORE_POSITION =
    new BooleanOption("dialog.jaroptions.store.position", Boolean.TRUE);
  
  
  public static final StringOption DIALOG_EXTERNALPROCESS_STATE = new StringOption("dialog.externalprocess.state", "default");
  
  
  public static final BooleanOption DIALOG_EXTERNALPROCESS_STORE_POSITION =
    new BooleanOption("dialog.externalprocess.store.position", Boolean.TRUE);
  
  
  public static final StringOption DIALOG_EDITEXTERNALPROCESS_STATE = new StringOption("dialog.editexternalprocess.state", "default");
  
  
  public static final BooleanOption DIALOG_EDITEXTERNALPROCESS_STORE_POSITION =
    new BooleanOption("dialog.editexternalprocess.store.position", Boolean.TRUE);
  
  
  public static final BooleanOption FIND_REPLACE_FOCUS_IN_DEFPANE =
    new BooleanOption("find.replace.focus.in.defpane", Boolean.FALSE);
  
  
  public static final BooleanOption DIALOG_DRJAVA_ERROR_POPUP_ENABLED =
    new BooleanOption("dialog.drjava.error.popup.enabled", Boolean.TRUE);
  
  
  public static final BooleanOption DIALOG_DRJAVA_SURVEY_ENABLED =
    new BooleanOption("dialog.drjava.survey.enabled", Boolean.TRUE);
  
  
  public static final BooleanOption SHOW_CODE_PREVIEW_POPUPS =
    new BooleanOption("show.code.preview.popups", Boolean.TRUE);
  
  
  public static final BooleanOption DRJAVA_USE_FORCE_QUIT =
    new BooleanOption("drjava.use.force.quit", Boolean.FALSE);
  
  
  public static final BooleanOption DIALOG_AUTOIMPORT_ENABLED =
    new BooleanOption("dialog.autoimport.enabled", Boolean.TRUE);
  
  
  public static final NonNegativeIntegerOption FOLLOW_FILE_DELAY =
    new NonNegativeIntegerOption("follow.file.delay", Integer.valueOf(300));
  
  
  public static final NonNegativeIntegerOption FOLLOW_FILE_LINES =
    new NonNegativeIntegerOption("follow.file.lines", Integer.valueOf(1000));
  
  
  public static final String EXTERNAL_SAVED_PREFIX = "external.saved.";
  
  
  public static final NonNegativeIntegerOption EXTERNAL_SAVED_COUNT =
    new NonNegativeIntegerOption(EXTERNAL_SAVED_PREFIX + "count", Integer.valueOf(0));
  
  
  public static final VectorOption<String> EXTERNAL_SAVED_NAMES =
    new VectorOption<String>(EXTERNAL_SAVED_PREFIX + "names",
                             new StringOption("",""),
                             new Vector<String>());
  
  
  public static final VectorOption<String> EXTERNAL_SAVED_CMDLINES =
    new VectorOption<String>(EXTERNAL_SAVED_PREFIX + "cmdlines",
                             new StringOption("",""),
                             new Vector<String>());
  
  
  public static final VectorOption<String> EXTERNAL_SAVED_WORKDIRS =
    new VectorOption<String>(EXTERNAL_SAVED_PREFIX + "workdirs",
                             new StringOption("",""),
                             new Vector<String>());
  
  
  public static final VectorOption<String> EXTERNAL_SAVED_ENCLOSING_DJAPP_FILES =
    new VectorOption<String>(EXTERNAL_SAVED_PREFIX + "enclosingdjappfiles",
                             new StringOption("",""),
                             new Vector<String>());
  
  
  public static final ArrayList<String> NEW_VERSION_NOTIFICATION_CHOICES =
    VersionNotificationChoices.evaluate();
  public static class VersionNotificationChoices {
    public static final String STABLE = "stable versions only";
    public static final String BETA = "stable and beta versions only";
    public static final String ALL_RELEASES = "all release versions";
    public static final String EXPERIMENTAL = "weekly experimental builds";
    public static final String DISABLED = "none (disabled)";
    public static ArrayList<String> evaluate() {
      ArrayList<String> aList = new ArrayList<String>(4);
      aList.add(STABLE);
      aList.add(BETA);
      aList.add(ALL_RELEASES);
      aList.add(EXPERIMENTAL);
      aList.add(DISABLED);
      return aList;
    }
  }
  
  
  public static final ForcedChoiceOption NEW_VERSION_NOTIFICATION =
    new ForcedChoiceOption("new.version.notification", VersionNotificationChoices.BETA, NEW_VERSION_NOTIFICATION_CHOICES);

  
  public static final BooleanOption NEW_VERSION_ALLOWED = new BooleanOption("new.version.allowed", Boolean.TRUE);
  
  
  public static final LongOption LAST_NEW_VERSION_NOTIFICATION = new LongOption("new.version.notification.last", (long)0);  
  
  
  public static final NonNegativeIntegerOption NEW_VERSION_NOTIFICATION_DAYS =
    new NonNegativeIntegerOption("new.version.notification.days", 7);  
  
  
  public static final NonNegativeIntegerOption DRJAVA_SURVEY_DAYS =
    new NonNegativeIntegerOption("drjava.survey.days", 91); 
  
  
  public static final LongOption LAST_DRJAVA_SURVEY = new LongOption("drjava.survey.notification.last", (long)0);  
  
  
  public static final StringOption LAST_DRJAVA_SURVEY_RESULT = new StringOption("drjava.survey.result.last", "");
  
  
  public static final ArrayList<String> DELETE_LL_CLASS_FILES_CHOICES =
    DeleteLLClassFileChoices.evaluate();
  public static class DeleteLLClassFileChoices {
    public static final String NEVER = "never";
    public static final String ASK_ME = "ask me at startup";
    public static final String ALWAYS = "always";
    public static ArrayList<String> evaluate() {
      ArrayList<String> aList = new ArrayList<String>(3);
      aList.add(NEVER);
      aList.add(ASK_ME);
      aList.add(ALWAYS);
      return aList;
    }
  }
  
  
  public static final ForcedChoiceOption DELETE_LL_CLASS_FILES =
    new ForcedChoiceOption("delete.ll.class.files", DeleteLLClassFileChoices.ALWAYS, DELETE_LL_CLASS_FILES_CHOICES);
  
  
  public static final ArrayList<String> FILE_EXT_REGISTRATION_CHOICES =
    FileExtRegistrationChoices.evaluate();
  public static class FileExtRegistrationChoices {
    public static final String NEVER = "never";
    public static final String ASK_ME = "ask me at startup";
    public static final String ALWAYS = "always";
    public static ArrayList<String> evaluate() {
      ArrayList<String> aList = new ArrayList<String>(4);
      aList.add(NEVER);
      aList.add(ASK_ME);
      aList.add(ALWAYS);
      return aList;
    }
  }
  
  
  public static final ForcedChoiceOption FILE_EXT_REGISTRATION =
    new ForcedChoiceOption("file.ext.registration", FileExtRegistrationChoices.ASK_ME,
                           FILE_EXT_REGISTRATION_CHOICES);
  
  
  
  
  public static final FileOption JUNIT_LOCATION = new FileOption("junit.location", FileOps.NULL_FILE);
  
  
  public static final BooleanOption JUNIT_LOCATION_ENABLED = new BooleanOption("junit.location.enabled", Boolean.FALSE);
  
  
  public static final FileOption RT_CONCJUNIT_LOCATION = new FileOption("rt.concjunit.location", FileOps.NULL_FILE);
  
  
  static final ArrayList<String> concJUnitCheckChoices =
    ConcJUnitCheckChoices.evaluate();
  public static class ConcJUnitCheckChoices {
    public static final String ALL = "all-threads, no-join, lucky";
    public static final String NO_LUCKY = "all-threads, no-join";
    public static final String ONLY_THREADS = "all-threads";
    public static final String NONE = "none (use JUnit)";
    public static ArrayList<String> evaluate() {
      ArrayList<String> aList = new ArrayList<String>(4);
      aList.add(ALL);
      aList.add(NO_LUCKY);
      aList.add(ONLY_THREADS);
      aList.add(NONE);
      return aList;
    }
  }
  
  
  public static final ForcedChoiceOption CONCJUNIT_CHECKS_ENABLED =
    new ForcedChoiceOption("concjunit.checks.enabled", ConcJUnitCheckChoices.NONE, concJUnitCheckChoices);

  
  public static final StringOption CUSTOM_DRJAVA_JAR_VERSION_SUFFIX = new StringOption("custom.drjava.jar.version.suffix", "");
}
