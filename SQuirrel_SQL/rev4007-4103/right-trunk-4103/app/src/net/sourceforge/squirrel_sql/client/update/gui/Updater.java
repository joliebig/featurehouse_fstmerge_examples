
package net.sourceforge.squirrel_sql.client.update.gui;

import java.io.File;

import javax.swing.JOptionPane;

import net.sourceforge.squirrel_sql.client.ApplicationArguments;
import net.sourceforge.squirrel_sql.client.SquirrelLoggerFactory;
import net.sourceforge.squirrel_sql.client.update.ArtifactInstaller;
import net.sourceforge.squirrel_sql.client.update.InstallEventType;
import net.sourceforge.squirrel_sql.client.update.InstallStatusEvent;
import net.sourceforge.squirrel_sql.client.update.InstallStatusListener;
import net.sourceforge.squirrel_sql.client.update.UpdateUtil;
import net.sourceforge.squirrel_sql.client.update.UpdateUtilImpl;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class Updater {

   private static String MESSAGE;
   private static String TITLE;
   
   
   private static StringManager s_stringMgr;
   
   private static UpdateUtil util = null;
   
   
   private static ILogger s_log;
   
   static {
      ApplicationArguments.initialize(new String[0]);
      LoggerController.registerLoggerFactory(new SquirrelLoggerFactory(false));
      s_log = LoggerController.createLogger(Updater.class);
      
      util = new UpdateUtilImpl();
      
      s_stringMgr = 
         StringManagerFactory.getStringManager(Updater.class);

      
      
      MESSAGE = s_stringMgr.getString("Updater.message");

      
      TITLE = s_stringMgr.getString("Updater.title");
   
   }
   
   
   public static boolean showConfirmDialog() {
      int choice = 
         JOptionPane.showConfirmDialog(
         null, MESSAGE, TITLE, JOptionPane.YES_NO_OPTION,
         JOptionPane.QUESTION_MESSAGE);
      return choice == JOptionPane.YES_OPTION;
   }
   
   public static void installUpdates(File changeList) throws Exception {
      ArtifactInstaller installer = new ArtifactInstaller(util, changeList);
      installer.addListener(new MyInstallStatusListener());      
      installer.backupFiles();
      installer.installFiles();      
   }
   
   
   public static void main(String[] args) {
      boolean prompt = false;
      if (args != null && args.length > 0){
         if (args[0].equals("-prompt")) {
            prompt=true;
         }
      }
      try {
         File changeListFile = util.getChangeListFile();
         if (changeListFile.exists()) {
            if (s_log.isInfoEnabled()) {
               s_log.info("Updater detected a changeListFile to be processed");
            }            
            if (prompt) {
               if (showConfirmDialog()) {
                  installUpdates(changeListFile);
               } else {
                  if (s_log.isInfoEnabled()) {
                     s_log.info("User cancelled update installation");
                  }
               }
            } else {
               installUpdates(changeListFile);            
            }
         }
      } catch (Throwable e) {
         s_log.error("Unexpected error while attempting to install updates: "
               + e.getMessage(), e);         
      } finally {
         if (s_log.isInfoEnabled()) {
            s_log.info("Updater finished");
         }
         LoggerController.shutdown();
         System.exit(0);
      }
   }
   
   private static class MyInstallStatusListener implements InstallStatusListener {

      
      public void handleInstallStatusEvent(InstallStatusEvent evt) {
         if (evt.getType() == InstallEventType.BACKUP_STARTED) {}
         if (evt.getType() == InstallEventType.FILE_BACKUP_STARTED) {}
         if (evt.getType() == InstallEventType.FILE_BACKUP_COMPLETE) {}
         if (evt.getType() == InstallEventType.FILE_INSTALL_STARTED) {}
         if (evt.getType() == InstallEventType.FILE_INSTALL_COMPLETE) {}
         if (evt.getType() == InstallEventType.BACKUP_COMPLETE) {}
      }
      
   }
}
