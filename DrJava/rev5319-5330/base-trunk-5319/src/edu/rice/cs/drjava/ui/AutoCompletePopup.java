package edu.rice.cs.drjava.ui;



import java.awt.event.*;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Color;
import java.awt.EventQueue;
import java.io.IOException;
import java.io.File;
import java.net.URL;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.EmptyBorder;
import java.awt.Dimension;
import javax.swing.text.JTextComponent;
import javax.swing.text.BadLocationException;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.model.SingleDisplayModel;
import edu.rice.cs.drjava.config.OptionConstants;
import edu.rice.cs.drjava.ui.predictive.PredictiveInputFrame;
import edu.rice.cs.drjava.ui.predictive.PredictiveInputModel;
import edu.rice.cs.drjava.model.DummyOpenDefDoc;
import edu.rice.cs.drjava.platform.PlatformFactory;
import edu.rice.cs.drjava.model.OpenDefinitionsDocument;

import edu.rice.cs.plt.lambda.*;
import edu.rice.cs.plt.iter.*;
import edu.rice.cs.plt.collect.UnionSet;

import edu.rice.cs.util.swing.SwingFrame;
import edu.rice.cs.util.swing.Utilities;

import static edu.rice.cs.drjava.ui.MainFrameStatics.*;
import static edu.rice.cs.drjava.ui.predictive.PredictiveInputModel.*;
import static edu.rice.cs.drjava.ui.predictive.PredictiveInputFrame.FrameState;


public class AutoCompletePopup {
  
  final protected MainFrame _mainFrame;
    
  
  JCheckBox _completeJavaAPICheckbox = new JCheckBox("Java API");
  
  
  protected FrameState _lastState = null;
  
  
  final protected Set<AutoCompletePopupEntry> _allEntries;  
  
  
  final protected Set<AutoCompletePopupEntry> _docEntries;
  
  
  final protected Set<AutoCompletePopupEntry> _apiEntries;
  
  
  public AutoCompletePopup(MainFrame mf) { this(mf, null); }

  
  public AutoCompletePopup(MainFrame mf, String frameState) {
    _mainFrame = mf;
    if (frameState!=null) _lastState = new FrameState(frameState);
    _docEntries = new HashSet<AutoCompletePopupEntry>();
    _apiEntries = new HashSet<AutoCompletePopupEntry>();
    _allEntries = new UnionSet<AutoCompletePopupEntry>(_apiEntries,
                                                       new UnionSet<AutoCompletePopupEntry>(mf.getCompleteClassSet(),
                                                                                            _docEntries));
  }
  
  
  public void show(final Component parent,
                   final String title,
                   final String initial,
                   final int loc,
                   final Runnable canceledAction,
                   final Runnable3<AutoCompletePopupEntry,Integer,Integer> acceptedAction) {
    show(parent, title, initial, loc, IterUtil.make("OK"), 
         IterUtil.make(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0)), 0, canceledAction, IterUtil.make(acceptedAction));
  }
  
  
  public void show(final Component parent,
                   final String title,
                   final String initial,
                   final int loc,
                   final SizedIterable<String> actionNames,
                   final Runnable canceledAction,
                   final SizedIterable<Runnable3<AutoCompletePopupEntry,Integer,Integer>> acceptedActions) {
    SizedIterable<KeyStroke> actionKeyStrokes = 
      IterUtil.compose(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                       IterUtil.copy((KeyStroke)null, acceptedActions.size()-1));
    show(parent, title, initial, loc, actionNames, actionKeyStrokes, 
         0, canceledAction, acceptedActions);
  }
    
  
  public void show(final Component parent,
                   final String title,
                   final String initial,
                   final int loc,
                   final SizedIterable<String> actionNames,
                   final SizedIterable<KeyStroke> actionKeyStrokes,
                   final int oneMatchActionIndex,
                   final Runnable canceledAction,
                   final SizedIterable<Runnable3<AutoCompletePopupEntry,Integer,Integer>> acceptedActions) {
    assert actionNames.size() == acceptedActions.size();
    assert actionNames.size() == actionKeyStrokes.size();
    
    _completeJavaAPICheckbox.setSelected(DrJava.getConfig().getSetting(OptionConstants.DIALOG_COMPLETE_JAVAAPI));
    _completeJavaAPICheckbox.setEnabled(true);
    
    new Thread() {
      public void run() {
        List<OpenDefinitionsDocument> docs = _mainFrame.getModel().getOpenDefinitionsDocuments();
        if ((docs == null) || (docs.size() == 0)) {
          Utilities.invokeAndWait(canceledAction);
          return; 
        }
        
        AutoCompletePopupEntry currentEntry = null;
        _docEntries.clear();
        for(OpenDefinitionsDocument d: docs) {
          if (d.isUntitled()) continue;
          String str = d.toString();
          if (str.lastIndexOf('.')>=0) {
            str = str.substring(0, str.lastIndexOf('.'));
          }
          GoToFileListEntry entry = new GoToFileListEntry(d, str);
          if (d.equals(_mainFrame.getModel().getActiveDocument())) currentEntry = entry;
          _docEntries.add(entry);
        }
        
        if (DrJava.getConfig().getSetting(OptionConstants.DIALOG_COMPLETE_JAVAAPI)) {
          addJavaAPI();
        }
        
        final PredictiveInputModel<AutoCompletePopupEntry> pim = 
          new PredictiveInputModel<AutoCompletePopupEntry>(true, new PrefixStrategy<AutoCompletePopupEntry>(), _allEntries);
        String mask = "";
        String s = initial;
        
        
        if ((loc<s.length()) && (!Character.isWhitespace(s.charAt(loc))) &&
            ("()[]{}<>.,:;/*+-!~&|%".indexOf(s.charAt(loc)) == -1)) {
          
          Utilities.invokeAndWait(canceledAction);
          return;
        }
        
        
        int start = loc;
        while(start > 0) {
          if (!Character.isJavaIdentifierPart(s.charAt(start-1))) { break; }
          --start;
        }
        while((start<s.length()) && (!Character.isJavaIdentifierStart(s.charAt(start))) && (start < loc)) {
          ++start;
        }
        
        int end = loc-1;
        
        if ((start>=0) && (end < s.length())) {
          mask = s.substring(start, end + 1);
          pim.setMask(mask);
        }
        
        if ((pim.getMatchingItems().size() == 1) && (oneMatchActionIndex >= 0)) {
          if (pim.getCurrentItem() != null) {
            
            final int finalStart = start;
            Utilities.invokeAndWait(new Runnable() {
              public void run() {
                Iterator<Runnable3<AutoCompletePopupEntry,Integer,Integer>> actionIt =
                  acceptedActions.iterator();
                Runnable3<AutoCompletePopupEntry,Integer,Integer> action;
                int i = oneMatchActionIndex;
                do {
                  action = actionIt.next();
                } while(i<0);
                action.run(pim.getCurrentItem(), finalStart, loc);
              }
            });
            return;
          }
        }
        
        
        pim.setMask(mask);
        if (pim.getMatchingItems().size() == 0) {
          
          mask = pim.getMask();
          while(mask.length() > 0) {
            mask = mask.substring(0, mask.length() - 1);
            pim.setMask(mask);
            if (pim.getMatchingItems().size() > 0) { break; }
          }
        }       
        final PredictiveInputFrame<AutoCompletePopupEntry> completeWordDialog = 
          createCompleteWordDialog(title, start, loc, actionNames, actionKeyStrokes,
                                   canceledAction, acceptedActions);
        final AutoCompletePopupEntry finalCurrentEntry = currentEntry;
        Utilities.invokeLater(new Runnable() {
          public void run() {
            completeWordDialog.setModel(true, pim); 
            completeWordDialog.selectStrategy();
            if (finalCurrentEntry != null) {
              completeWordDialog.setCurrentItem(finalCurrentEntry);
            }
            completeWordDialog.setLocationRelativeTo(parent);
            
            if (_lastState != null) {
              completeWordDialog.setFrameState(_lastState);
            }
            
            completeWordDialog.setVisible(true);
          }
        });
      }
    }.start();
  }
  
  
  public FrameState getFrameState() { return _lastState; }
  
  
  public void setFrameState(FrameState ds) {
    _lastState = ds;
  }  
  
  
  public void setFrameState(String s) {
    try { _lastState = new FrameState(s); }
    catch(IllegalArgumentException e) { _lastState = null; }
  }
  
  protected PredictiveInputFrame<AutoCompletePopupEntry>
    createCompleteWordDialog(final String title,
                             final int start,
                             final int loc,
                             final SizedIterable<String> actionNames,
                             final SizedIterable<KeyStroke> actionKeyStrokes,
                             final Runnable canceledAction,
                             final SizedIterable<Runnable3<AutoCompletePopupEntry,Integer,Integer>> acceptedActions) {
    final SimpleBox<PredictiveInputFrame<AutoCompletePopupEntry>> dialogThunk =
      new SimpleBox<PredictiveInputFrame<AutoCompletePopupEntry>>();
    
    _completeJavaAPICheckbox.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String curMask = dialogThunk.value().getMask();
        DrJava.getConfig().setSetting(OptionConstants.DIALOG_COMPLETE_JAVAAPI, _completeJavaAPICheckbox.isSelected());
        if (_completeJavaAPICheckbox.isSelected()) addJavaAPI(); else removeJavaAPI();
        dialogThunk.value().setItems(true,_allEntries);
        dialogThunk.value().setMask(curMask);
        dialogThunk.value().resetFocus();
      }
    });
    PlatformFactory.ONLY.setMnemonic(_completeJavaAPICheckbox,'j');
    PredictiveInputFrame.InfoSupplier<AutoCompletePopupEntry> info = 
      new PredictiveInputFrame.InfoSupplier<AutoCompletePopupEntry>() {
      public String value(AutoCompletePopupEntry entry) {
        
        StringBuilder sb = new StringBuilder();
        sb.append(entry.getFullPackage());
        sb.append(entry.getClassName());
        return sb.toString();
      }
    };
    
    List<PredictiveInputFrame.CloseAction<AutoCompletePopupEntry>> actions
      = new ArrayList<PredictiveInputFrame.CloseAction<AutoCompletePopupEntry>>();

    Iterator<String> nameIt = actionNames.iterator();
    Iterator<Runnable3<AutoCompletePopupEntry,Integer,Integer>> actionIt =
      acceptedActions.iterator();
    Iterator<KeyStroke> ksIt = actionKeyStrokes.iterator();
    for(int i=0; i<acceptedActions.size(); ++i) {
      final int acceptedActionIndex = i;
      final String name = nameIt.next();
      final Runnable3<AutoCompletePopupEntry,Integer,Integer> runnable = actionIt.next();
      final KeyStroke ks = ksIt.next();
      
      PredictiveInputFrame.CloseAction<AutoCompletePopupEntry> okAction =
        new PredictiveInputFrame.CloseAction<AutoCompletePopupEntry>() {
        public String getName() { return name; }
        public KeyStroke getKeyStroke() { return ks; }
        public String getToolTipText() { return "Complete the identifier"; }
        public Object value(final PredictiveInputFrame<AutoCompletePopupEntry> p) {
          _lastState = p.getFrameState();
          if (p.getItem() != null) {
            Utilities.invokeAndWait(new Runnable() {
              public void run() {
                runnable.run(p.getItem(), start, loc);
              }
            });
          }
          else {
            Utilities.invokeAndWait(canceledAction);
          }
          return null;
        }
      };
      actions.add(okAction);
    }

    PredictiveInputFrame.CloseAction<AutoCompletePopupEntry> cancelAction = 
      new PredictiveInputFrame.CloseAction<AutoCompletePopupEntry>() {
      public String getName() { return "Cancel"; }
      public KeyStroke getKeyStroke() { return KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0); }
      public String getToolTipText() { return null; }
      public Object value(PredictiveInputFrame<AutoCompletePopupEntry> p) {
        _lastState = p.getFrameState();
        Utilities.invokeAndWait(canceledAction);
        return null;
      }
    };    
    actions.add(cancelAction);

    
    java.util.ArrayList<MatchingStrategy<AutoCompletePopupEntry>> strategies =
      new java.util.ArrayList<MatchingStrategy<AutoCompletePopupEntry>>();
    strategies.add(new FragmentStrategy<AutoCompletePopupEntry>());
    strategies.add(new PrefixStrategy<AutoCompletePopupEntry>());
    strategies.add(new RegExStrategy<AutoCompletePopupEntry>());
    
    GoToFileListEntry entry = new GoToFileListEntry(new DummyOpenDefDoc() {
      public String getPackageNameFromDocument() { return ""; }
    }, "dummyComplete");
    dialogThunk.set(new PredictiveInputFrame<AutoCompletePopupEntry>(null,
                                                                     title,
                                                                     true, 
                                                                     true, 
                                                                     info,
                                                                     strategies,
                                                                     actions,
                                                                     actions.size()-1, 
                                                                     entry) {
      protected JComponent[] makeOptions() {
        return new JComponent[] { _completeJavaAPICheckbox };
      }
    });
    dialogThunk.value().setSize(dialogThunk.value().getSize().width, 500);
    dialogThunk.value().setLocationRelativeTo(_mainFrame);
    return dialogThunk.value();
  }
  
  private void addJavaAPI() {
    Set<JavaAPIListEntry> apiSet = _mainFrame.getJavaAPISet();
    if (apiSet == null) {
      DrJava.getConfig().setSetting(OptionConstants.DIALOG_COMPLETE_JAVAAPI, Boolean.FALSE);
      _completeJavaAPICheckbox.setSelected(false);
      _completeJavaAPICheckbox.setEnabled(false);
    }
    else {
      _apiEntries.clear();
      _apiEntries.addAll(apiSet);
    }
  }
  
  private void removeJavaAPI() {
    _apiEntries.clear();
  }
}
