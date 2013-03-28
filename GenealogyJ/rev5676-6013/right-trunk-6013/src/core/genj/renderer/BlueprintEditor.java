
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
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
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
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class BlueprintEditor extends JSplitPane {

  
  private JTextArea source;
  
  
  private Preview preview;
  
  
  private final static Resources resources = Resources.get(BlueprintEditor.class);
  
  
  private Grammar grammar = Grammar.V55;
  
  
  private Blueprint blueprint;

  
  private AbstractButton bInsert;
  
  
  private Example example; 
  
  
  private boolean isChanged = false;
  
  
  private BlueprintManager blueprintManager = BlueprintManager.getInstance();
    
    public BlueprintEditor(Entity recipient) { 
    example = new Example(recipient);
    grammar = recipient.getGedcom().getGrammar();
    
    preview = new Preview();
    preview.setBorder(BorderFactory.createTitledBorder(resources.getString("blueprint.preview")));
    
    JPanel edit = new JPanel(new BorderLayout());
      
      source = new JTextArea(3,32);
      source.setFont(new Font("Monospaced", Font.PLAIN, 12));
      JScrollPane scroll = new JScrollPane(source);
      scroll.setBorder(BorderFactory.createTitledBorder("HTML"));
      
      bInsert = new JButton(new Insert());
    edit.setMinimumSize(new Dimension(0,0));
    edit.add(scroll, BorderLayout.CENTER);
    edit.add(bInsert, BorderLayout.SOUTH);
    
    setLeftComponent(preview);
    setRightComponent(edit);
    setDividerLocation(Integer.MAX_VALUE);
    setOrientation(JSplitPane.VERTICAL_SPLIT);
    setOneTouchExpandable(true);
    
    source.getDocument().addDocumentListener(preview);
    
    set(null);
    
  }
  
  
  public int getLastDividerLocation() {
    return getSize().height/2;
  }
  
  
  public void set(Blueprint setBlueprint) {
    
    if (setBlueprint==null) {
      blueprint = null;
      source.setText("");
    } else {
      blueprint = setBlueprint;
      source.setText(blueprint.getHTML());
      source.setCaretPosition(0);
    }
    boolean edit = blueprint!=null&&!blueprint.isReadOnly();
    bInsert.setEnabled(edit);
    source.setEditable(edit);
    source.setToolTipText(blueprint!=null&&blueprint.isReadOnly() ? resources.getString("blueprint.readonly", blueprint.getName()) : null);
    if (edit)
      setSourceVisible(true);
    
    isChanged = false;
    
    preview.repaint();
    
  }
  
    public void commit() {
    if (blueprint!=null&&isChanged) {
      blueprint.setSource(source.getText());
      try {
        blueprintManager.saveBlueprint(blueprint);
        
        isChanged = false;
      } catch (IOException e) {
        Logger.getLogger("genj.renderer").log(Level.WARNING, "can't save blueprint", e);
      }
    }
  }
  
  
  public void setSourceVisible(boolean v) {
    
    SwingUtilities.invokeLater(new ShowHTML(v));
  }
  
  private class ShowHTML implements Runnable {
    private boolean visible;
    public ShowHTML(boolean visible) {
      this.visible = visible;
    }
    public void run() {
      setDividerLocation( visible ? 0.5D : 1.0D);
    }
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
      
      ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      
      
      if (source.getText().length()==0) return; 
      
      Rectangle bounds = getBounds();
      Insets insets = getInsets();
      bounds.x += insets.left;
      bounds.y += insets.top ;
      bounds.width -= insets.left+insets.right;
      bounds.height-= insets.top +insets.bottom;
      
      g.setColor(Color.white);
      g.fillRect(bounds.x,bounds.y,bounds.width,bounds.height);
      
      g.setFont(getFont());
      EntityRenderer renderer = new EntityRenderer(new Blueprint(source.getText()));
      renderer.setDebug(isChanged);
      renderer.render(g, example, bounds);
      
    }
  } 

    private class Insert extends Action2 {
    
    private Insert() {
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
        source.insert(
          "<prop path="+paths[p].toString()+">"+(p==paths.length-1?"":"\n"), 
          source.getCaretPosition()
        );
      }
      
      source.requestFocusInWindow();
      
    }
  } 

  
  private class Example extends Entity  {
    
    private Entity proxied;
    
    
    private Map<String,String> tag2value = new HashMap<String, String>();
    
    
    private Example(Entity proxied) {
      this.proxied = proxied;
      
      tag2value.put("NAME", "John /Doe/");
      tag2value.put("SEX" , "M");
      tag2value.put("DATE", "01 JAN 1900");
      tag2value.put("PLAC", "Nice Place");
      tag2value.put("ADDR", "Long Address");
      tag2value.put("CITY", "Big City");
      tag2value.put("POST", "12345");
    }
    
    public String getId() {
      return proxied.getId();
    }
    
    public String getTag() {
      return proxied.getTag();
    }
    
    public Property getProperty(TagPath path) {
      
      if (!path.get(0).equals(getTag())) 
        return null;
      
      if (path.length()==1)
        return proxied;
      
      Property result = proxied.getProperty(path);
      if (result!=null&&result.getValue().length()>0)
        return result;
      
      String value = tag2value.get(path.getLast());
      if (value==null) 
        value = Gedcom.getName(path.getLast());
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
