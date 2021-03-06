
package genj.renderer;

import genj.common.PathTreeWidget;
import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomException;
import genj.gedcom.Grammar;
import genj.gedcom.MetaProperty;
import genj.gedcom.Property;
import genj.gedcom.PropertySimpleReadOnly;
import genj.gedcom.PropertyXRef;
import genj.gedcom.TagPath;
import genj.util.Resources;
import genj.util.swing.Action2;
import genj.util.swing.DialogHelper;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class BlueprintEditor extends JSplitPane {

  
  private JTextArea html;
  
  
  private Preview preview;
  
  
  private final static Resources resources = Resources.get(BlueprintEditor.class);
  
  
  private Grammar grammar = Grammar.V55;
  
  
  private Blueprint blueprint;

  
  private AbstractButton bInsert;
  
  
  private Example example = new Example(); 
  
  
  private boolean isChanged = false;
  
  
  private BlueprintManager blueprintManager = BlueprintManager.getInstance();
    
    public BlueprintEditor(Entity recipient) { 
    
    preview = new Preview();
    preview.setBorder(BorderFactory.createTitledBorder(resources.getString("blueprint.preview")));
    
    JPanel edit = new JPanel(new BorderLayout());
      
      html = new JTextArea(3,32);
      html.setFont(new Font("Monospaced", Font.PLAIN, 12));
      JScrollPane scroll = new JScrollPane(html);
      scroll.setBorder(BorderFactory.createTitledBorder("HTML"));
      
      bInsert = new JButton(new ActionInsert());
    edit.setMinimumSize(new Dimension(0,0));
    edit.add(scroll, BorderLayout.CENTER);
    edit.add(bInsert, BorderLayout.SOUTH);
    
    setLeftComponent(preview);
    setRightComponent(edit);
    setDividerLocation(Integer.MAX_VALUE);
    setOrientation(JSplitPane.VERTICAL_SPLIT);
    setOneTouchExpandable(true);
    
    html.getDocument().addDocumentListener(preview);
    
    set(null);
    
  }
  
  
  public int getLastDividerLocation() {
    return getSize().height/2;
  }
  
  
  public void set(Blueprint setBlueprint) {
    
    if (setBlueprint==null) {
      blueprint = null;
      html.setText("");
    } else {
      blueprint = setBlueprint;
      html.setText(blueprint.getHTML());
      html.setCaretPosition(0);
    }
    boolean edit = blueprint!=null&&!blueprint.isReadOnly();
    bInsert.setEnabled(edit);
    html.setEditable(edit);
    html.setToolTipText(edit?resources.getString("blueprint.readonly", blueprint.getName()):null);
    
    isChanged = false;
    
    preview.repaint();
    
  }
  
    public void commit() {
    if (blueprint!=null&&isChanged) {
      blueprint.setHTML(html.getText());
      try {
        blueprintManager.saveBlueprint(blueprint);
        
        isChanged = false;
      } catch (IOException e) {
        Logger.getLogger("genj.renderer").log(Level.WARNING, "can't save blueprint", e);
      }
    }
  }
  
  
  public void setHTMLVisible(boolean v) {
    setDividerLocation( v ? 0.5D : 1.0D);
  }
  
    private class Preview extends JComponent implements DocumentListener {
    
    public void changedUpdate(DocumentEvent e) {
      isChanged = true;
      repaint();
    }
    
    public void insertUpdate(DocumentEvent e) {
      isChanged = true;
      repaint();
    }
    
    public void removeUpdate(DocumentEvent e) {
      isChanged = true;
      repaint();
    }
    
    protected void paintComponent(Graphics g) {
      
      if (html.getText().length()==0) return; 
      
      Rectangle bounds = getBounds();
      Insets insets = getInsets();
      bounds.x += insets.left;
      bounds.y += insets.top ;
      bounds.width -= insets.left+insets.right;
      bounds.height-= insets.top +insets.bottom;
      
      g.setColor(Color.white);
      g.fillRect(bounds.x,bounds.y,bounds.width,bounds.height);
      
      EntityRenderer renderer = new EntityRenderer(new Blueprint(html.getText()), getFont());
      renderer.setDebug(isChanged);
      renderer.render(g, example, bounds);
      
    }
  } 

    private class ActionInsert extends Action2 {
    
    private ActionInsert() {
      super.setText(resources.getString("prop.insert"));
      super.setTip(resources.getString("prop.insert.tip"));
    }
    
    public void actionPerformed(ActionEvent event) {
      
      PathTreeWidget tree = new PathTreeWidget();
      TagPath[] paths = grammar.getAllPaths(blueprint.getTag(), Property.class);
      tree.setPaths(paths, new TagPath[0]);
      
      int option = DialogHelper.openDialog(resources.getString("prop.insert.tip"),DialogHelper.QUESTION_MESSAGE,tree,Action2.okCancel(),BlueprintEditor.this);        
      
      if (option!=0) return;
      
      paths = tree.getSelection();
      for (int p=0;p<paths.length; p++) {
        html.insert(
          "<prop path="+paths[p].toString()+">"+(p==paths.length-1?"":"\n"), 
          html.getCaretPosition()
        );
      }
      
      html.requestFocusInWindow();
      
    }
  } 

  
  private class Example extends Entity  {
    
    
    private Map<String,String> tag2value = new HashMap<String, String>();
    
    
    private Example() {
      tag2value.put("NAME", "John /Doe/");
      tag2value.put("SEX" , "M");
      tag2value.put("DATE", "01 JAN 1900");
      tag2value.put("PLAC", "Nice Place");
      tag2value.put("ADDR", "Long Address");
      tag2value.put("CITY", "Big City");
      tag2value.put("POST", "12345");
    }
    
    public String getId() {
      String prefix;
      if (blueprint==null) prefix = "X";
      else prefix = Gedcom.getEntityPrefix(blueprint.getTag());
      return prefix+"999";
    }
    
    public String getTag() {
      return blueprint==null ? "INDI" : blueprint.getTag();
    }
    
    public Property getProperty(TagPath path) {
      
      if (!path.get(0).equals(getTag())) 
        return null;
      
      if (path.length()==1)
        return this;
      
      String value = tag2value.get(path.getLast());
      if (value==null) 
        value = "Something";
      MetaProperty meta = grammar.getMeta(path, false);
      if (PropertyXRef.class.isAssignableFrom(meta.getType()))
        value = "@...@";
      try {
        return meta.create(value);
      } catch (GedcomException e) {
        return new PropertySimpleReadOnly(path.getLast(), value.toString());
      }
    }
    
  } 
  
} 
