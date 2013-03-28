

package edu.rice.cs.drjava.config;

import java.io.File;
import java.util.Vector;
import java.util.ArrayList;
import java.util.Arrays;
import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import edu.rice.cs.drjava.platform.PlatformFactory;



public interface OptionConstants {

  

  

  
  public static final FileOption BROWSER_FILE = new FileOption("browser.file", FileOption.NULL_FILE);

  
  public static final StringOption BROWSER_STRING = new StringOption("browser.string", "");
  
  
  public static final String PROJECT_FILE_EXTENSION = ".pjt";
  
  public static final FileOption JAVAC_LOCATION = new FileOption("javac.location", FileOption.NULL_FILE);

  public static final VectorOption<File> EXTRA_CLASSPATH = new ClassPathOption().evaluate("extra.classpath");

  public static final VectorOption<String> EXTRA_COMPILERS =
    new VectorOption<String>("extra.compilers", new StringOption("",""), new Vector<String>());

  

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

  
  public static final ColorOption DEFINITIONS_MATCH_COLOR =
    new ColorOption("definitions.match.color", new Color(190, 255, 230));

  
  public static final ColorOption COMPILER_ERROR_COLOR = new ColorOption("compiler.error.color", Color.yellow);

  
  public static final ColorOption DEBUG_BREAKPOINT_COLOR = new ColorOption("debug.breakpoint.color", Color.red);

  
  public static final ColorOption DEBUG_BREAKPOINT_DISABLED_COLOR = new ColorOption("debug.breakpoint.disabled.color", new Color(128,0,0));

  
  public static final ColorOption DEBUG_THREAD_COLOR = new ColorOption("debug.thread.color", new Color(100,255,255));


  

  
  public static final FontOption FONT_MAIN =
    new FontOption("font.main", DefaultFont.getDefaultMainFont());

  
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
      if (PlatformFactory.ONLY.isMacPlatform()) {
        return Font.decode("Monaco-10");
      }
      else {
        return Font.decode("Monospaced-10");
      }
    }
  }

  
  public static final FontOption FONT_LINE_NUMBERS =
    new FontOption("font.line.numbers", DefaultFont.getDefaultLineNumberFont());

  
  public static final FontOption FONT_DOCLIST =
    new FontOption("font.doclist", DefaultFont.getDefaultDocListFont());

 
  public static final FontOption FONT_TOOLBAR =
    new FontOption("font.toolbar", Font.decode("dialog-10"));

  
  public static final BooleanOption TEXT_ANTIALIAS =
    new BooleanOption("text.antialias", Boolean.FALSE);


  

  
  public static final BooleanOption TOOLBAR_ICONS_ENABLED =
    new BooleanOption("toolbar.icons.enabled", Boolean.TRUE);

  
  public static final BooleanOption TOOLBAR_TEXT_ENABLED =
    new BooleanOption("toolbar.text.enabled", Boolean.TRUE);

  
   public static final BooleanOption TOOLBAR_ENABLED = 
     new BooleanOption("toolbar.enabled", Boolean.TRUE);

  
  public static final BooleanOption LINEENUM_ENABLED =
    new BooleanOption("lineenum.enabled", Boolean.FALSE);

  
  public static final BooleanOption WINDOW_STORE_POSITION =
    new BooleanOption("window.store.position", Boolean.TRUE);

  
  public static final BooleanOption SHOW_SOURCE_WHEN_SWITCHING = 
    new BooleanOption("show.source.for.fast.switch", Boolean.TRUE);
  
  
  public static final ForcedChoiceOption LOOK_AND_FEEL =
    new ForcedChoiceOption("look.and.feel",
                           LookAndFeels.getDefaultLookAndFeel(),
                           LookAndFeels.getLookAndFeels());

  
  static class LookAndFeels {
    
    
    public static String getDefaultLookAndFeel() {
      if (PlatformFactory.ONLY.isMacPlatform()) return UIManager.getSystemLookAndFeelClassName();
      else return UIManager.getCrossPlatformLookAndFeelClassName();
    }
    
    public static ArrayList<String> getLookAndFeels() {
      ArrayList<String> lookAndFeels = new ArrayList<String>();
      LookAndFeelInfo[] lafis = UIManager.getInstalledLookAndFeels();
      if (lafis != null) {
        for (int i = 0; i < lafis.length; i++) {
          try {
            String currName = lafis[i].getClassName();
            LookAndFeel currLAF = (LookAndFeel) Class.forName(currName).newInstance();
            if (currLAF.isSupportedLookAndFeel()) {
              lookAndFeels.add(currName);
            }
          }
          catch (Exception ex) {
            
            
          }
        }
      }
      return lookAndFeels;
    }
  }

  
  static int mask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
  
  public static final KeyStrokeOption KEY_NEW_FILE =
    new KeyStrokeOption("key.new.file",
                        KeyStroke.getKeyStroke(KeyEvent.VK_N, mask));
  
  public static final KeyStrokeOption KEY_OPEN_PROJECT =
    new KeyStrokeOption("key.open.project",
                        KeyStroke.getKeyStroke(KeyEvent.VK_I, mask));
  
  public static final KeyStrokeOption KEY_NEW_TEST =
    new KeyStrokeOption("key.new.test",
                        KeyStrokeOption.NULL_KEYSTROKE);

  
  public static final KeyStrokeOption KEY_OPEN_FOLDER =
    new KeyStrokeOption("key.open.folder",
                        KeyStrokeOption.NULL_KEYSTROKE);
  
  public static final KeyStrokeOption KEY_OPEN_FILE =
    new KeyStrokeOption("key.open.file",
                        KeyStroke.getKeyStroke(KeyEvent.VK_O, mask));
  
  public static final KeyStrokeOption KEY_SAVE_FILE =
    new KeyStrokeOption("key.save.file",
                        KeyStroke.getKeyStroke(KeyEvent.VK_S, mask));
  
  public static final KeyStrokeOption KEY_SAVE_FILE_AS =
    new KeyStrokeOption("key.save.file.as",
                        KeyStroke.getKeyStroke(KeyEvent.VK_S, mask |
                                               InputEvent.SHIFT_MASK));
  
  public static final KeyStrokeOption KEY_SAVE_ALL_FILES =
    new KeyStrokeOption("key.save.all.files",
                        KeyStroke.getKeyStroke(KeyEvent.VK_S, mask |
                                               InputEvent.ALT_MASK));
  
  public static final KeyStrokeOption KEY_REVERT_FILE =
    new KeyStrokeOption("key.revert.file",
                        KeyStroke.getKeyStroke(KeyEvent.VK_R, mask));
  
  public static final KeyStrokeOption KEY_CLOSE_FILE =
    new KeyStrokeOption("key.close.file",
                        KeyStroke.getKeyStroke(KeyEvent.VK_W, mask));
  
  public static final KeyStrokeOption KEY_CLOSE_ALL_FILES =
    new KeyStrokeOption("key.close.all.files",
                        KeyStroke.getKeyStroke(KeyEvent.VK_W, mask |
                                               InputEvent.ALT_MASK));
  
  public static final KeyStrokeOption KEY_CLOSE_PROJECT =
    new KeyStrokeOption("key.close.project",
                        KeyStroke.getKeyStroke(KeyEvent.VK_W, mask |
                                               InputEvent.SHIFT_MASK));
  
  public static final KeyStrokeOption KEY_PAGE_SETUP =
    new KeyStrokeOption("key.page.setup",
                        KeyStrokeOption.NULL_KEYSTROKE);
  
  public static final KeyStrokeOption KEY_PRINT_PREVIEW =
    new KeyStrokeOption("key.print.preview",
                        KeyStroke.getKeyStroke(KeyEvent.VK_P, mask |
                                               InputEvent.SHIFT_MASK));
  
  public static final KeyStrokeOption KEY_PRINT =
    new KeyStrokeOption("key.print",
                        KeyStroke.getKeyStroke(KeyEvent.VK_P, mask));
  
  public static final KeyStrokeOption KEY_QUIT =
    new KeyStrokeOption("key.quit",
                        KeyStroke.getKeyStroke(KeyEvent.VK_Q, mask));
  
  public static final KeyStrokeOption KEY_UNDO =
    new KeyStrokeOption("key.undo",
                        KeyStroke.getKeyStroke(KeyEvent.VK_Z, mask));
  
  public static final KeyStrokeOption KEY_REDO =
    new KeyStrokeOption("key.redo",
                        KeyStroke.getKeyStroke(KeyEvent.VK_Z, mask |
                                               InputEvent.SHIFT_MASK));
  
  public static final KeyStrokeOption KEY_CUT =
    new KeyStrokeOption("key.cut",
                        KeyStroke.getKeyStroke(KeyEvent.VK_X, mask));
  
  public static final KeyStrokeOption KEY_COPY =
    new KeyStrokeOption("key.copy",
                        KeyStroke.getKeyStroke(KeyEvent.VK_C, mask));
  
  public static final KeyStrokeOption KEY_PASTE =
    new KeyStrokeOption("key.paste",
                        KeyStroke.getKeyStroke(KeyEvent.VK_V, mask));
  
  public static final KeyStrokeOption KEY_SELECT_ALL =
    new KeyStrokeOption("key.select.all",
                        KeyStroke.getKeyStroke(KeyEvent.VK_A, mask));
  
  public static final KeyStrokeOption KEY_FIND_NEXT =
    new KeyStrokeOption("key.find.next",
                        KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
  
  public static final KeyStrokeOption KEY_FIND_PREV =
    new KeyStrokeOption("key.find.prev",
                        KeyStroke.getKeyStroke(KeyEvent.VK_F3,  InputEvent.SHIFT_MASK));
  
  public static final KeyStrokeOption KEY_FIND_REPLACE =
    new KeyStrokeOption("key.find.replace",
                        KeyStroke.getKeyStroke(KeyEvent.VK_F, mask));
  
  public static final KeyStrokeOption KEY_GOTO_LINE =
    new KeyStrokeOption("key.goto.line",
                        KeyStroke.getKeyStroke(KeyEvent.VK_G, mask));

  
  public static final KeyStrokeOption KEY_GOTO_FILE =
    new KeyStrokeOption("key.goto.file",
                        KeyStroke.getKeyStroke(KeyEvent.VK_G, mask|KeyEvent.SHIFT_MASK));

  
  public static final KeyStrokeOption KEY_GOTO_FILE_UNDER_CURSOR =
    new KeyStrokeOption("key.goto.file.under.cursor",
                        KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0));

  
  public static final KeyStrokeOption KEY_COMPLETE_FILE =
    new KeyStrokeOption("key.complete.file",
                        KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, mask|KeyEvent.SHIFT_MASK));

  

  
  public static final KeyStrokeOption KEY_COMMENT_LINES =
    new KeyStrokeOption("key.comment.lines",
                        KeyStroke.getKeyStroke(KeyEvent.VK_SLASH, mask));

  
  public static final KeyStrokeOption KEY_UNCOMMENT_LINES =
    new KeyStrokeOption("key.uncomment.lines",
                        KeyStroke.getKeyStroke(KeyEvent.VK_SLASH,
                                               (mask | InputEvent.SHIFT_MASK)));

  
  public static final KeyStrokeOption KEY_PREVIOUS_DOCUMENT =
    new KeyStrokeOption("key.previous.document",
                        KeyStroke.getKeyStroke(KeyEvent.VK_COMMA, mask));
  
  public static final KeyStrokeOption KEY_NEXT_DOCUMENT =
    new KeyStrokeOption("key.next.document",
                        KeyStroke.getKeyStroke(KeyEvent.VK_PERIOD, mask));

  
  public static final KeyStrokeOption KEY_PREVIOUS_PANE =
    new KeyStrokeOption("key.previous.pane",
                        KeyStroke.getKeyStroke(KeyEvent.VK_OPEN_BRACKET, mask));

  
  public static final KeyStrokeOption KEY_NEXT_PANE =
    new KeyStrokeOption("key.next.pane",
                        KeyStroke.getKeyStroke(KeyEvent.VK_CLOSE_BRACKET, mask));

  
  public static final KeyStrokeOption KEY_PREFERENCES =
    new KeyStrokeOption("key.preferences",
                        KeyStroke.getKeyStroke(KeyEvent.VK_SEMICOLON, mask));

  
  public static final KeyStrokeOption KEY_COMPILE =
    new KeyStrokeOption("key.compile", KeyStroke.getKeyStroke(KeyEvent.VK_F5, InputEvent.SHIFT_MASK));

  
  public static final KeyStrokeOption KEY_COMPILE_ALL =
    new KeyStrokeOption("key.compile.all", KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));

  
  public static final KeyStrokeOption KEY_RUN =
    new KeyStrokeOption("key.run", KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
  
  
  public static final KeyStrokeOption KEY_RUN_MAIN =
    new KeyStrokeOption("key.run.main", KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0));

  
  public static final KeyStrokeOption KEY_TEST =
    new KeyStrokeOption("key.test",
                        KeyStroke.getKeyStroke(KeyEvent.VK_T, mask | InputEvent.SHIFT_MASK));

  
  public static final KeyStrokeOption KEY_TEST_ALL =
    new KeyStrokeOption("key.test.all", KeyStroke.getKeyStroke(KeyEvent.VK_T, mask));

  
  public static final KeyStrokeOption KEY_JAVADOC_ALL =
    new KeyStrokeOption("key.javadoc.all", KeyStroke.getKeyStroke(KeyEvent.VK_J, mask));

  
  public static final KeyStrokeOption KEY_JAVADOC_CURRENT =
    new KeyStrokeOption("key.javadoc.current",
                        KeyStroke.getKeyStroke(KeyEvent.VK_J, mask | InputEvent.SHIFT_MASK));

  
  public static final KeyStrokeOption KEY_EXECUTE_HISTORY =
    new KeyStrokeOption("key.execute.history", KeyStrokeOption.NULL_KEYSTROKE);

  
  public static final KeyStrokeOption KEY_LOAD_HISTORY_SCRIPT =
    new KeyStrokeOption("key.load.history.script", KeyStrokeOption.NULL_KEYSTROKE);

  
  public static final KeyStrokeOption KEY_SAVE_HISTORY =
    new KeyStrokeOption("key.save.history", KeyStrokeOption.NULL_KEYSTROKE);

  
  public static final KeyStrokeOption KEY_CLEAR_HISTORY =
    new KeyStrokeOption("key.clear.history", KeyStrokeOption.NULL_KEYSTROKE);

  
  public static final KeyStrokeOption KEY_RESET_INTERACTIONS =
    new KeyStrokeOption("key.reset.interactions", KeyStrokeOption.NULL_KEYSTROKE);

  
  public static final KeyStrokeOption KEY_VIEW_INTERACTIONS_CLASSPATH =
    new KeyStrokeOption("key.view.interactions.classpath", KeyStrokeOption.NULL_KEYSTROKE);
  
  
  public static final KeyStrokeOption KEY_PRINT_INTERACTIONS =
    new KeyStrokeOption("key.view.print.interactions", KeyStrokeOption.NULL_KEYSTROKE);

  
  public static final KeyStrokeOption KEY_LIFT_CURRENT_INTERACTION =
    new KeyStrokeOption("key.lift.current.interaction", KeyStrokeOption.NULL_KEYSTROKE);

  

  
  public static final KeyStrokeOption KEY_CLEAR_CONSOLE =
    new KeyStrokeOption("key.clear.console", KeyStrokeOption.NULL_KEYSTROKE);
  
  
  public static final KeyStrokeOption KEY_PRINT_CONSOLE =
    new KeyStrokeOption("key.view.print.console", KeyStrokeOption.NULL_KEYSTROKE);

  
  public static final KeyStrokeOption KEY_BACKWARD =
    new KeyStrokeOption("key.backward", KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0));

  
  public static final KeyStrokeOption KEY_BEGIN_DOCUMENT =
    new KeyStrokeOption("key.begin.document", KeyStroke.getKeyStroke(KeyEvent.VK_HOME, mask));

  
  public static final KeyStrokeOption KEY_BEGIN_LINE =
    new KeyStrokeOption("key.begin.line", KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0));

  

  
  public static final KeyStrokeOption KEY_PREVIOUS_WORD =
    new KeyStrokeOption("key.previous.word", KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, mask));

 
  public static final KeyStrokeOption KEY_DELETE_NEXT =
    new KeyStrokeOption("key.delete.next",
                        KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
  
  public static final KeyStrokeOption KEY_DELETE_PREVIOUS =
    new KeyStrokeOption("key.delete.previous", KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0));

  
  public static final KeyStrokeOption KEY_SHIFT_DELETE_NEXT =
    new KeyStrokeOption("key.delete.next",
                        KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, InputEvent.SHIFT_MASK));
  
  public static final KeyStrokeOption KEY_SHIFT_DELETE_PREVIOUS =
    new KeyStrokeOption("key.delete.previous", KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, InputEvent.SHIFT_MASK));

  
  public static final KeyStrokeOption KEY_DOWN =
    new KeyStrokeOption("key.down", KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0));

  
  public static final KeyStrokeOption KEY_UP =
    new KeyStrokeOption("key.up", KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0));

  
  public static final KeyStrokeOption KEY_END_DOCUMENT =
    new KeyStrokeOption("key.end.document",
                        KeyStroke.getKeyStroke(KeyEvent.VK_END, mask));
  
  public static final KeyStrokeOption KEY_END_LINE =
    new KeyStrokeOption("key.end.line",
                        KeyStroke.getKeyStroke(KeyEvent.VK_END, 0));
  




  
  public static final KeyStrokeOption KEY_NEXT_WORD =
    new KeyStrokeOption("key.next.word", KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, mask));
  
  
  public static final KeyStrokeOption KEY_FORWARD =
    new KeyStrokeOption("key.forward", KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0));
  
  
  public static final KeyStrokeOption KEY_PAGE_DOWN =
    new KeyStrokeOption("key.page.down", KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0));
  
  
  public static final KeyStrokeOption KEY_PAGE_UP =
    new KeyStrokeOption("key.page.up", KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0));

  
  public static final KeyStrokeOption KEY_CUT_LINE =
    new KeyStrokeOption("key.cut.line", KeyStroke.getKeyStroke(KeyEvent.VK_K, (mask | InputEvent.ALT_MASK)));

  
  public static final KeyStrokeOption KEY_CLEAR_LINE =
    new KeyStrokeOption("key.clear.line", KeyStroke.getKeyStroke(KeyEvent.VK_K, mask));

  
  public static final KeyStrokeOption KEY_DEBUG_MODE_TOGGLE =
    new KeyStrokeOption("key.debug.mode.toggle", KeyStroke.getKeyStroke(KeyEvent.VK_D, mask));





  
  public static final KeyStrokeOption KEY_DEBUG_RESUME =
    new KeyStrokeOption("key.debug.resume", KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0));
  
  
  public static final KeyStrokeOption KEY_DEBUG_STEP_INTO =
    new KeyStrokeOption("key.debug.step.into", KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0));
  
  
  public static final KeyStrokeOption KEY_DEBUG_STEP_OVER =
    new KeyStrokeOption("key.debug.step.over", KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0));
  
  
  public static final KeyStrokeOption KEY_DEBUG_STEP_OUT =
    new KeyStrokeOption("key.debug.step.out", KeyStroke.getKeyStroke(KeyEvent.VK_F12, InputEvent.SHIFT_MASK));
  
  
  public static final KeyStrokeOption KEY_DEBUG_BREAKPOINT_TOGGLE =
    new KeyStrokeOption("key.debug.breakpoint.toggle", KeyStroke.getKeyStroke(KeyEvent.VK_B, mask));

  
  public static final KeyStrokeOption KEY_DEBUG_BREAKPOINT_PANEL =
    new KeyStrokeOption("key.debug.breakpoint.panel", KeyStroke.getKeyStroke(KeyEvent.VK_B, mask | InputEvent.SHIFT_MASK));

  
  public static final KeyStrokeOption KEY_DEBUG_CLEAR_ALL_BREAKPOINTS =
    new KeyStrokeOption("key.debug.clear.all.breakpoints", KeyStrokeOption.NULL_KEYSTROKE);

  
  public static final KeyStrokeOption KEY_HELP =
    new KeyStrokeOption("key.help", KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));

  
  public static final KeyStrokeOption KEY_QUICKSTART = 
    new KeyStrokeOption("key.quickstart", KeyStrokeOption.NULL_KEYSTROKE);
  
  
  public static final KeyStrokeOption KEY_ABOUT = 
    new KeyStrokeOption("key.about", KeyStrokeOption.NULL_KEYSTROKE);

  
  
  
  public static final BooleanOption FIND_MATCH_CASE = 
    new BooleanOption("find.replace.match.case", Boolean.TRUE);
  
  public static final BooleanOption FIND_SEARCH_BACKWARDS = 
    new BooleanOption("find.replace.search.backwards", Boolean.FALSE);
  
  public static final BooleanOption FIND_WHOLE_WORD = 
    new BooleanOption("find.replace.whole.word", Boolean.FALSE);
  
  public static final BooleanOption FIND_ALL_DOCUMENTS = 
    new BooleanOption("find.replace.all.documents", Boolean.FALSE);
  
  public static final BooleanOption FIND_NO_COMMENTS_STRINGS =
    new BooleanOption("find.replace.no.comments.strings", Boolean.FALSE);
  

  

  
  public static final VectorOption<File> DEBUG_SOURCEPATH =
    new ClassPathOption().evaluate("debug.sourcepath");

  
  public static final BooleanOption DEBUG_STEP_JAVA =
    new BooleanOption("debug.step.java", Boolean.FALSE);

  
  public static final BooleanOption DEBUG_STEP_INTERPRETER =
    new BooleanOption("debug.step.interpreter", Boolean.FALSE);

  
  public static final BooleanOption DEBUG_STEP_DRJAVA =
    new BooleanOption("debug.step.drjava", Boolean.FALSE);

  
  public static final StringOption DEBUG_STEP_EXCLUDE =
    new StringOption("debug.step.exclude", "");



  

  
  static final ArrayList<String> accessLevelChoices =
    AccessLevelChoices.evaluate();
  static class AccessLevelChoices {
    public static ArrayList<String> evaluate() {
      ArrayList<String> aList = new ArrayList<String>(4);
      aList.add("public");
      aList.add("protected");
      aList.add("package");
      aList.add("private");
      return aList;
    }
  }

  
  public static final ForcedChoiceOption JAVADOC_ACCESS_LEVEL =
    new ForcedChoiceOption("javadoc.access.level", "package", accessLevelChoices);

  
  static final String JAVADOC_NONE_TEXT = "none";
  static final String JAVADOC_1_3_TEXT = "1.3";
  static final String JAVADOC_1_4_TEXT = "1.4";
  static final String JAVADOC_1_5_TEXT = "1.5";
  
  static final String[] choices = new String[]{JAVADOC_NONE_TEXT, JAVADOC_1_3_TEXT, JAVADOC_1_4_TEXT, JAVADOC_1_5_TEXT};
  
  static final ArrayList<String> linkVersionChoices = new ArrayList<String>(Arrays.asList(choices));

  
  public static final StringOption JAVADOC_1_3_LINK =
    new StringOption("javadoc.1.3.link", "http://java.sun.com/j2se/1.3/docs/api");
  public static final StringOption JAVADOC_1_4_LINK =
    new StringOption("javadoc.1.4.link", "http://java.sun.com/j2se/1.4/docs/api");
  public static final StringOption JAVADOC_1_5_LINK =
    new StringOption("javadoc.1.5.link", "http://java.sun.com/j2se/1.5/docs/api");

  
  public static final ForcedChoiceOption JAVADOC_LINK_VERSION =
    new ForcedChoiceOption("javadoc.link.version",
                           (System.getProperty("java.specification.version").equals("1.3") ? JAVADOC_1_3_TEXT :
                              (System.getProperty("java.specification.version").equals("1.4") ? JAVADOC_1_4_TEXT : 
                                 JAVADOC_1_5_TEXT)),
                           linkVersionChoices);

  
  public static final BooleanOption JAVADOC_FROM_ROOTS = new BooleanOption("javadoc.from.roots", Boolean.FALSE);

  
  public static final StringOption JAVADOC_CUSTOM_PARAMS = 
    new StringOption("javadoc.custom.params", "-author -version");

  
  public static final FileOption JAVADOC_DESTINATION = new FileOption("javadoc.destination", FileOption.NULL_FILE);

  
  public static final BooleanOption JAVADOC_PROMPT_FOR_DESTINATION =
    new BooleanOption("javadoc.prompt.for.destination", Boolean.TRUE);

  

  
  public static final BooleanOption INTERACTIONS_EXIT_PROMPT =
    new BooleanOption("interactions.exit.prompt", Boolean.TRUE);

  
  public static final BooleanOption QUIT_PROMPT =
    new BooleanOption("quit.prompt", Boolean.TRUE);

  
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

  
  public static final BooleanOption ALWAYS_SAVE_BEFORE_DEBUG =
    new BooleanOption("save.before.debug", Boolean.FALSE);

  
  public static final BooleanOption WARN_BREAKPOINT_OUT_OF_SYNC =
    new BooleanOption("warn.breakpoint.out.of.sync", Boolean.TRUE);

  
  public static final BooleanOption WARN_DEBUG_MODIFIED_FILE =
    new BooleanOption("warn.debug.modified.file", Boolean.TRUE);

  
  public static final BooleanOption WARN_CHANGE_LAF = new BooleanOption("warn.change.laf", Boolean.TRUE);

  
  public static final BooleanOption WARN_PATH_CONTAINS_POUND =
    new BooleanOption("warn.path.contains.pound", Boolean.TRUE);

  

  
  public static final BooleanOption PROMPT_BEFORE_CLEAN = new BooleanOption("prompt.before.clean", Boolean.TRUE);
  
  
  public static final BooleanOption OPEN_FOLDER_RECURSIVE =  new BooleanOption("open.folder.recursive", Boolean.FALSE);
  
  
  public static final FileOption WORKING_DIRECTORY = new FileOption("working.directory", FileOption.NULL_FILE);

  
  public static final NonNegativeIntegerOption INDENT_LEVEL = 
    new NonNegativeIntegerOption("indent.level",new Integer(2));

  
  public static final NonNegativeIntegerOption HISTORY_MAX_SIZE =
    new NonNegativeIntegerOption("history.max.size", new Integer(500));

  
  public static final NonNegativeIntegerOption RECENT_FILES_MAX_SIZE =
    new NonNegativeIntegerOption("recent.files.max.size", new Integer(5));

  
  public static final BooleanOption AUTO_CLOSE_COMMENTS =
    new BooleanOption("auto.close.comments", Boolean.FALSE);

  
  public static final BooleanOption RESET_CLEAR_CONSOLE =
    new BooleanOption("reset.clear.console", Boolean.TRUE);

  
  public static final BooleanOption JAVAC_ALLOW_ASSERT =
    new BooleanOption("javac.allow.assert", Boolean.FALSE);

  
  public static final BooleanOption BACKUP_FILES = new BooleanOption("files.backup", Boolean.TRUE);

  
  public static final BooleanOption ALLOW_PRIVATE_ACCESS = new BooleanOption("allow.private.access", Boolean.FALSE);
  
  
  public static final BooleanOption FORCE_TEST_SUFFIX = new BooleanOption("force.test.suffix", Boolean.FALSE);

  
  
  
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
  
  

  
  public static final IntegerOption LANGUAGE_LEVEL = new IntegerOption("language.level", new Integer(0));
  
  
  public static final VectorOption<File> RECENT_FILES =
    new VectorOption<File>("recent.files",new FileOption("",null),new Vector<File>());

  
  public static final VectorOption<File> RECENT_PROJECTS =
    new VectorOption<File>("recent.projects",new FileOption("",null),new Vector<File>());
  
  
  public static final BooleanOption SHOW_DEBUG_CONSOLE = new BooleanOption("show.debug.console", Boolean.FALSE);

  
  public static final NonNegativeIntegerOption WINDOW_HEIGHT =
    new NonNegativeIntegerOption("window.height",new Integer(700));

  
  public static final NonNegativeIntegerOption WINDOW_WIDTH =
    new NonNegativeIntegerOption("window.width",new Integer(800));

  
  public static final IntegerOption WINDOW_X = new IntegerOption("window.x", new Integer(Integer.MAX_VALUE));

  
  public static final IntegerOption WINDOW_Y = new IntegerOption("window.y", new Integer(Integer.MAX_VALUE));

  
  public static final NonNegativeIntegerOption DOC_LIST_WIDTH =
    new NonNegativeIntegerOption("doc.list.width",new Integer(150));

  
  public static final NonNegativeIntegerOption TABS_HEIGHT =
    new NonNegativeIntegerOption("tabs.height",new Integer(120));

  
  public static final NonNegativeIntegerOption DEBUG_PANEL_HEIGHT =
    new NonNegativeIntegerOption("debug.panel.height",new Integer(0));

  
  public static final FileOption LAST_DIRECTORY = new FileOption("last.dir", FileOption.NULL_FILE);

  
  public static final StringOption JVM_ARGS = new StringOption("jvm.args", "");

  
  public static final StringOption DIALOG_GOTOFILE_STATE = new StringOption("dialog.gotofile.state", "default");

  
  public static final BooleanOption DIALOG_GOTOFILE_STORE_POSITION =
    new BooleanOption("dialog.gotofile.store.position", Boolean.TRUE);

  
  public static final BooleanOption DIALOG_GOTOFILE_FULLY_QUALIFIED =
    new BooleanOption("dialog.gotofile.fully.qualified", Boolean.FALSE);

  
  public static final StringOption DIALOG_COMPLETE_FILE_STATE = new StringOption("dialog.completefile.state", "default");

  
  public static final BooleanOption DIALOG_COMPLETE_FILE_STORE_POSITION =
    new BooleanOption("dialog.completefile.store.position", Boolean.TRUE);
  
  
  public static final BooleanOption DIALOG_COMPLETE_SCAN_CLASS_FILES =
    new BooleanOption("dialog.completefile.scan.class.files", Boolean.FALSE);

  
  public static final StringOption DIALOG_JAROPTIONS_STATE = new StringOption("dialog.jaroptions.state", "default");

  
  public static final BooleanOption DIALOG_JAROPTIONS_STORE_POSITION =
    new BooleanOption("dialog.jaroptions.store.position", Boolean.TRUE);
}
