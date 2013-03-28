
package genj.edit.beans;

import genj.edit.Images;
import genj.edit.actions.RunExternal;
import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomException;
import genj.gedcom.Media;
import genj.gedcom.Property;
import genj.gedcom.PropertyBlob;
import genj.gedcom.PropertyComparator;
import genj.gedcom.PropertyFile;
import genj.gedcom.PropertyXRef;
import genj.gedcom.TagPath;
import genj.io.InputSource;
import genj.io.InputSource.FileInput;
import genj.util.DefaultValueMap;
import genj.util.Origin;
import genj.util.Resources;
import genj.util.swing.Action2;
import genj.util.swing.DialogHelper;
import genj.util.swing.FileChooserWidget;
import genj.util.swing.NestedBlockLayout;
import genj.util.swing.TextFieldWidget;
import genj.util.swing.ThumbnailWidget;
import genj.view.ContextProvider;
import genj.view.ViewContext;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public class MediaBean extends PropertyBean implements ContextProvider {
  
  private final static Resources RES = Resources.get(MediaBean.class);
  
  private Set<Property> OBJEsToRemove = new HashSet<Property>();
  
  private Map<FileInput,Set<Property>> propsNeedingOBJEs = 
    new DefaultValueMap<FileInput,Set<Property>>(new HashMap<FileInput,Set<Property>>(), new HashSet<Property>());
  
  private Map<InputSource,Set<Property>> currentOBJEs = 
    new DefaultValueMap<InputSource,Set<Property>>(new HashMap<InputSource,Set<Property>>(), new HashSet<Property>());
  
  private ThumbnailWidget thumbs = new ThumbnailWidget() {
    public String getToolTipText(InputSource source) {
      StringBuffer result = new StringBuffer();
      result.append("<html><body>");
      result.append("<b>");
      result.append(source.getName());
      result.append("</b><br/>");
      int i=0; for (Property obje : currentOBJEs.get(source)) {
        if (i++>0) result.append("<br/>");
        result.append(obje.getParent().toString());
      }
      for (Property prop : propsNeedingOBJEs.get(source)) {
        if (i++>0) result.append("<br/>");
        result.append(prop.toString());
      }
      result.append("</body></html>");
      return result.toString();
    }
    @Override
    protected void handleDrop(List<File> files) {
      if (files.size()==1) {
        new Add(files.get(0)).actionPerformed(null);
      }
    }
  };
  private JToolBar actions = new JToolBar();
  private Action2 add = new Add(), del = new Del();
  
  
  public MediaBean() {
    
    setLayout(new BorderLayout());

    setBorder(BorderFactory.createLoweredBevelBorder());

    add(BorderLayout.NORTH , actions);
    add(BorderLayout.CENTER, thumbs);
    
    setPreferredSize(new Dimension(128,128));
    actions.setFloatable(false);
    
    
    add(add);
    add(del);
    actions.addSeparator();
    add(thumbs.getFitAction());
    add(thumbs.getOneAction());
    add(thumbs.getAllAction());

    
  }
  
  @Override
  public ViewContext getContext() {
    Property p = getProperty();
    if (p==null)
      return null; 
    InputSource source = thumbs.getSelection();
    if (!(source instanceof InputSource.FileInput))
      return null;
    ViewContext result = new ViewContext(p);
    result.addAction(new RunExternal(((InputSource.FileInput)source).getFile()));
    return result;
  }
  
  private void add(Action2 action) {
    JButton b = new JButton(action);
    b.setFocusable(false);
    actions.add(b);
  }
  
  @Override
  protected void commitImpl(Property property) {

    
    for (Property obje : OBJEsToRemove)
      obje.getParent().delProperty(obje);
    OBJEsToRemove.clear();
    
    
    Gedcom gedcom = property.getGedcom();
    boolean inline = !property.getGedcom().getGrammar().getMeta(new TagPath("OBJE")).allows("FILE");
    for (FileInput source : propsNeedingOBJEs.keySet()) {
      Media media = null;
      for (Property prop : propsNeedingOBJEs.get(source))  {
        if (inline)
          prop.addFile(source.getFile());
        else 
          prop.addMedia(media==null ? media=createMedia(gedcom, source) : media);
      }
    }
    propsNeedingOBJEs.clear();
    
    
  }
  
  private Media createMedia(Gedcom gedcom, FileInput source) {
    
    
    for (Entity e : gedcom.getEntities(Gedcom.OBJE)) {
      Media media = (Media)e;
      
      if (source.getName().length()>0&&!source.getName().equals(media.getTitle()))
        continue;
      
      if (!source.getFile().equals(media.getFile()))
        continue;
      return media;
    }
    
    
    Media media;
    try {
      media = (Media)gedcom.createEntity(Gedcom.OBJE);
    } catch (GedcomException e) {
      throw new Error("unexpected problem creating OBJE record", e);
    }
    media.addFile(source.getFile());
    media.setTitle(source.getName());
    
    return media;
  }

  @Override
  protected void setPropertyImpl(Property prop) {
    
    OBJEsToRemove.clear();
    propsNeedingOBJEs.clear();
    currentOBJEs.clear();
    
    
    if (prop==null) {
      thumbs.clear();
      add.setEnabled(false);
      del.setEnabled(false);
    } else {
      
      scan(prop);
      
      thumbs.setSources(new ArrayList<InputSource>(currentOBJEs.keySet()));
      
      add.setEnabled(true);
      del.setEnabled(true);
    }
  }
  
  private void scan(Property root) {
    
    
    for (int i=0;i<root.getNoOfProperties(); i++) {
      Property child = root.getProperty(i);
      if (!"OBJE".equals(child.getTag()))
        scan(child);
      else
        scan(root, child);
    }
    
    
  }
  
  private void scan(Property parent, Property OBJE) {

    
    
    
    if (OBJE instanceof PropertyXRef && ((PropertyXRef)OBJE).getTargetEntity() instanceof Media) {
      Media media = (Media)((PropertyXRef)OBJE).getTargetEntity();
      File file = media.getFile();
      if (file!=null){
        currentOBJEs.get(InputSource.get(media.getTitle(), file)).add(OBJE);
        return;
      }
      PropertyBlob blob = media.getBlob();
      if (blob!=null) 
        currentOBJEs.get(InputSource.get(media.getTitle(), blob.getBlobData())).add(OBJE);
      return;
    }
      
    
    Property FILE = OBJE.getProperty("FILE");
    if (FILE instanceof PropertyFile) {
      File file = ((PropertyFile)FILE).getFile();
      if (file!=null) 
        currentOBJEs.get(InputSource.get(OBJE.getPropertyValue("TITL"), file)).add(OBJE);
      return;
    }
    
    
  }
  
  private Property[] list(Collection<Property> props) {
    Property[] result = props.toArray(new Property[0]);
    Arrays.sort(result, new PropertyComparator(".:DATE"));
    return result;
  }
  
  private class Add extends Action2 implements ListSelectionListener, ChangeListener {
    
    private JList to;
    private Action ok;
    private FileChooserWidget chooser = new FileChooserWidget();
    
    Add(File file) {
      chooser.setFile(file);
    }
    Add() {
      setImage(ThumbnailWidget.IMG_THUMBNAIL.getOverLayed(Images.imgNew));
      
      if (getProperty()!=null) {
        Origin origin = getProperty().getGedcom().getOrigin();
        chooser.setDirectory(origin.getFile()!=null ? origin.getFile().getParent() : null);
      }
    }
    @Override
    public void setEnabled(boolean set) {
      
      if (set&&candidates().length==0)
        set = false;
      
      super.setEnabled(set);
      
      if (set)
        setTip(RES.getString("file.add", getProperty().getPropertyName()));
      else
        setTip("");
    }
    
    private Property[] candidates() {
      List<Property> result = new ArrayList<Property>();
      Property p = getProperty(); 
      if (p!=null) {
        if (p.getMetaProperty().allows("OBJE"))
          result.add(p);
        for (Property c : p.getProperties())
          if (c.getMetaProperty().allows("OBJE"))
            result.add(c);
      }
      return list(result);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
      
      
      ThumbnailWidget preview = new ThumbnailWidget();
      preview.setPreferredSize(new Dimension(128,128));
      chooser.setAccessory(preview);
      
      to = new JList(candidates());
      to.setVisibleRowCount(5);
      
      TextFieldWidget title = new TextFieldWidget();

      JPanel options = new JPanel(new NestedBlockLayout("<col><l1/><file gx=\"1\"/><l2/><title gx=\"1\"/><l3/><targets gx=\"1\" gy=\"1\"/></col>"));
      options.add(new JLabel(RES.getString("file.choose")));
      options.add(chooser);
      options.add(new JLabel(RES.getString("file.title")));
      options.add(title);
      options.add(new JLabel(RES.getString("file.add", "...")));
      options.add(new JScrollPane(to));

      ok = Action2.ok();

      to.addListSelectionListener(this);
      chooser.addChangeListener(this);
      
      if (to.getModel().getSize()>0)
        to.setSelectedIndex(0);
      
      validate();
      
      if (0!=DialogHelper.openDialog(getTip(), DialogHelper.QUESTION_MESSAGE, options, Action2.andCancel(ok), MediaBean.this))
        return;

      
      FileInput source = new FileInput(title.getText(), getFile());
      if (!currentOBJEs.containsKey(source)&&!propsNeedingOBJEs.containsKey(source))
        thumbs.addSource(source);
      
      
      Set<Property> props = propsNeedingOBJEs.get(source); 
      for (Object prop : to.getSelectedValues())
        props.add((Property)prop);
      
      
      MediaBean.this.changeSupport.fireChangeEvent();
      
      
    }
    
    private File getFile() {
      Origin origin = getProperty().getGedcom().getOrigin();
      return origin.getFile(chooser.getFile().toString());
    }
    
    private void validate() {
      File file = getFile();
      ok.setEnabled(to.getSelectedIndices().length>0 && file!=null && file.exists());
    }
    
    public void valueChanged(ListSelectionEvent e) {
      validate();
    }
    
    public void stateChanged(ChangeEvent e) {
      validate();
    }
  } 
  
  private class Del extends Action2 implements PropertyChangeListener,ListSelectionListener {
    
    private JList from;
    private Action ok;
    
    public Del() {
      setImage(ThumbnailWidget.IMG_THUMBNAIL.getGrayedOut().getOverLayed(Images.imgDel));
      thumbs.addPropertyChangeListener(this);
    }
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
      setEnabled(getProperty()!=null);
    }
    @Override
    public void setEnabled(boolean set) {
      if (thumbs.getSelection()==null)
        set = false;
      super.setEnabled(set);
      if (set)
        setTip(RES.getString("file.del", getProperty().getPropertyName()));
      else
        setTip("");
    }
    @Override
    public void actionPerformed(ActionEvent e) {
      
      InputSource source = thumbs.getSelection();
      if (source==null)
        return;

      
      List<Property> properties = new ArrayList<Property>();
      properties.addAll(currentOBJEs.get(source));
      properties.addAll(propsNeedingOBJEs.get(source));
      
      Collections.sort(properties, new PropertyComparator(".:DATE") {
        @Override
        public int compare(Property a, Property b) {
          if ("OBJE".equals(a.getTag())) a = a.getParent();
          if ("OBJE".equals(b.getTag())) b = b.getParent();
          return super.compare(a, b);
        }
      });
      
      List<Property> choices = new ArrayList<Property>();
      for (Property prop : properties) {
        if ("OBJE".equals(prop.getTag())) choices.add(prop.getParent());
        else choices.add(prop);
      }
        
      
      from = new JList(choices.toArray());
      from.setVisibleRowCount(5);
      if (!choices.isEmpty()) 
        from.setSelectionInterval(0, choices.size()-1);

      JPanel options = new JPanel(new NestedBlockLayout("<col><l1 gx=\"1\"/><targets gx=\"1\" gy=\"1\"/></col>"));
      options.add(new JLabel(RES.getString("file.del", "...")));
      options.add(new JScrollPane(from));

      ok = Action2.ok();

      from.addListSelectionListener(this);
        
      if (0!=DialogHelper.openDialog(getTip(), DialogHelper.QUESTION_MESSAGE, options, Action2.andCancel(ok), DialogHelper.getComponent(e)))
        return;
      
      
      Set<Property> objes = currentOBJEs.get(source); 
      Set<Property> needing = propsNeedingOBJEs.get(source);
      int[] is = from.getSelectedIndices();
      for (int i=0;i<is.length;i++) {
        Property prop = properties.get(is[i]);
        if (objes.remove(prop))
          OBJEsToRemove.add(prop);
        needing.remove(prop);
      }

      
      if (objes.isEmpty()&&needing.isEmpty()) 
        thumbs.removeSource(source);

      
      MediaBean.this.changeSupport.fireChangeEvent();
      
      
    }
    
    public void valueChanged(ListSelectionEvent e) {
      ok.setEnabled(from.getSelectedIndices().length>0);
    }
    
  }
}
