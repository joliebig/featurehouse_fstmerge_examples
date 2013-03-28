
package genj.edit;

import genj.common.SelectEntityWidget;
import genj.crypto.Enigma;
import genj.edit.actions.AbstractChange;
import genj.edit.actions.CreateAlias;
import genj.edit.actions.CreateAssociation;
import genj.edit.actions.CreateChild;
import genj.edit.actions.CreateEntity;
import genj.edit.actions.CreateParent;
import genj.edit.actions.CreateSibling;
import genj.edit.actions.CreateSpouse;
import genj.edit.actions.CreateXReference;
import genj.edit.actions.DelEntity;
import genj.edit.actions.DelProperty;
import genj.edit.actions.OpenForEdit;
import genj.edit.actions.Redo;
import genj.edit.actions.RunExternal;
import genj.edit.actions.SetPlaceHierarchy;
import genj.edit.actions.SetSubmitter;
import genj.edit.actions.SwapSpouses;
import genj.edit.actions.TogglePrivate;
import genj.edit.actions.Undo;
import genj.gedcom.Entity;
import genj.gedcom.Fam;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomDirectory;
import genj.gedcom.GedcomException;
import genj.gedcom.Indi;
import genj.gedcom.MetaProperty;
import genj.gedcom.Property;
import genj.gedcom.PropertyEvent;
import genj.gedcom.PropertyFamilyChild;
import genj.gedcom.PropertyFile;
import genj.gedcom.PropertyMedia;
import genj.gedcom.PropertyNote;
import genj.gedcom.PropertyPlace;
import genj.gedcom.PropertyRepository;
import genj.gedcom.PropertySource;
import genj.gedcom.PropertySubmitter;
import genj.gedcom.Submitter;
import genj.gedcom.TagPath;
import genj.io.FileAssociation;
import genj.util.Registry;
import genj.util.swing.Action2;
import genj.util.swing.ImageIcon;
import genj.util.swing.NestedBlockLayout;
import genj.view.ActionProvider;
import genj.view.ContextSelectionEvent;
import genj.view.ViewContext;
import genj.view.ViewFactory;
import genj.window.WindowManager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;


public class EditViewFactory implements ViewFactory, ActionProvider {
    
    
  private final static Action2 aNOOP = Action2.NOOP;
  
  
  public JComponent createView(String title, Gedcom gedcom, Registry registry) {
    return new EditView(title, gedcom, registry);
  }

  
  public ImageIcon getImage() {
    return Images.imgView;
  }
  
  
  public String getTitle() {
    return EditView.resources.getString("title");
  }

























  
  
  public List createActions(Property[] properties) {
    List result = new ArrayList();
    
    for (int i = 0; i < properties.length; i++) 
      if (properties[i] instanceof Entity) return result;
    
    if (Enigma.isAvailable())
      result.add(new TogglePrivate(properties[0].getGedcom(), Arrays.asList(properties)));
    
    result.add(new DelProperty(properties));
    
    return result;
  }

  
  public List createActions(Property property) {
    
    
    List result = new ArrayList();
    
    
    if (property instanceof PropertyFile)  
      createActions(result, (PropertyFile)property); 
      
    
    if (property instanceof PropertyPlace)  
      result.add(new SetPlaceHierarchy((PropertyPlace)property)); 
      
    
    MetaProperty[] subs = property.getNestedMetaProperties(0);
    for (int s=0;s<subs.length;s++) {
      
      Class type = subs[s].getType();
      if (type==PropertyNote.class||
          type==PropertyRepository.class||
          type==PropertySource.class||
          type==PropertySubmitter.class||
          type==PropertyFamilyChild.class||
          type==PropertyMedia.class 
        ) {
        
        result.add(new CreateXReference(property,subs[s].getTag()));
        
        continue;
      }
    }
    
    
    
    if ( property instanceof PropertyEvent
        && ( (property.getEntity() instanceof Indi)
            || property.getGedcom().getGrammar().getMeta(new TagPath("INDI:ASSO")).allows("TYPE"))  )
      result.add(new CreateAssociation(property));
    
    
    if (Enigma.isAvailable())
      result.add(new TogglePrivate(property.getGedcom(), Collections.singletonList(property)));
    
    
    if (!property.isTransient()) 
      result.add(new DelProperty(property));

    
    return result;
  }

  
  public List createActions(Entity entity) {
    
    List result = new ArrayList();
    
    
    if (entity instanceof Indi) createActions(result, (Indi)entity);
    
    if (entity instanceof Fam) createActions(result, (Fam)entity);
    
    if (entity instanceof Submitter) createActions(result, (Submitter)entity);
    
    
    result.add(Action2.NOOP);

    
    MetaProperty[] subs = entity.getNestedMetaProperties(0);
    for (int s=0;s<subs.length;s++) {
      
      Class type = subs[s].getType();
      if (type==PropertyNote.class||
          type==PropertyRepository.class||
          type==PropertySource.class||
          type==PropertySubmitter.class||
          type==PropertyMedia.class
          ) {
        result.add(new CreateXReference(entity,subs[s].getTag()));
      }
    }

    
    result.add(Action2.NOOP);
    result.add(new DelEntity(entity));
    
    
    EditView[] edits = EditView.getInstances(entity.getGedcom());
    if (edits.length==0) {
      result.add(Action2.NOOP);
      result.add(new OpenForEdit(new ViewContext(entity)));
    }
    
    return result;
  }

  
  public List createActions(Gedcom gedcom) {
    
    List result = new ArrayList();
    result.add(new CreateEntity(gedcom, Gedcom.INDI));
    result.add(new CreateEntity(gedcom, Gedcom.FAM));
    result.add(new CreateEntity(gedcom, Gedcom.NOTE));
    result.add(new CreateEntity(gedcom, Gedcom.OBJE));
    result.add(new CreateEntity(gedcom, Gedcom.REPO));
    result.add(new CreateEntity(gedcom, Gedcom.SOUR));
    result.add(new CreateEntity(gedcom, Gedcom.SUBM));

    for (Gedcom other : GedcomDirectory.getInstance().getGedcoms()) {
      if (other!=gedcom && other.getEntities(Gedcom.INDI).size()>0)
        result.add(new CopyIndividual(gedcom, other));
    }

    result.add(Action2.NOOP);
    result.add(new Undo(gedcom, gedcom.canUndo()));
    result.add(new Redo(gedcom, gedcom.canRedo()));

    
    return result;
  }
  
  
  private class CopyIndividual extends AbstractChange {
    
    private Gedcom source;
    private Indi existing;

    public CopyIndividual(Gedcom dest, Gedcom source) {
      super(dest, Gedcom.getEntityImage(Gedcom.INDI), "Copy individual from "+source);
      this.source = source;
    }
    
    
    @Override
    protected JPanel getDialogContent() {
      
      JPanel result = new JPanel(new NestedBlockLayout("<col><row><select wx=\"1\"/></row><row><text wx=\"1\" wy=\"1\"/></row><row><check/><text/></row></col>"));

      
      final SelectEntityWidget select = new SelectEntityWidget(source, Gedcom.INDI, null);
   
      
      result.add(select);
      result.add(getConfirmComponent());

      
      select.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          
          existing = (Indi)select.getSelection();
          refresh();
        }
      });
      
      existing = (Indi)select.getSelection();
      refresh();
      
      
      return result;
    }
    
    @Override
    protected void refresh() {
      
      super.refresh();
    }
    
    private boolean dupe() {
      return gedcom.getEntity(existing.getId())!=null;
    }
    
    @Override
    protected String getConfirmMessage() {
      if (existing==null)
        return "Please select an individual";
      String result = "Copying individual "+existing+" from "+source.getName()+" to "+gedcom.getName();
      if (dupe())
        result += "\n\nNote: Duplicate ID - a new ID will be assigned";
      return result;
    }
    
    @Override
    public void perform(Gedcom gedcom) throws GedcomException {
      Entity e = gedcom.createEntity(Gedcom.INDI, dupe() ? null : existing.getId());
      e.copyProperties(existing.getProperties(), true);
      WindowManager.broadcast(new ContextSelectionEvent(new ViewContext(e), getTarget()));
    }
  
  }

  
  private void createActions(List result, Indi indi) {
    result.add(new CreateChild(indi, true));
    result.add(new CreateChild(indi, false));
    result.add(new CreateParent(indi));
    result.add(new CreateSpouse(indi));
    result.add(new CreateSibling(indi, true));
    result.add(new CreateSibling(indi, false));
    result.add(new CreateAlias(indi));
  }
  
  
  private void createActions(List result, Fam fam) {
    result.add(new CreateChild(fam, true));
    result.add(new CreateChild(fam, false));
    if (fam.getNoOfSpouses()<2)
      result.add(new CreateParent(fam));
    if (fam.getNoOfSpouses()!=0)
      result.add(new SwapSpouses(fam));
  }
  
  
  private void createActions(List result, Submitter submitter) {
    result.add(new SetSubmitter(submitter));
  }
  
  
  public static void createActions(List result, PropertyFile file) {

    
    String suffix = file.getSuffix();
      
    
    List assocs = FileAssociation.getAll(suffix);
    if (assocs.isEmpty()) {
      result.add(new RunExternal(file));
    } else {
      for (Iterator it = assocs.iterator(); it.hasNext(); ) {
        FileAssociation fa = (FileAssociation)it.next(); 
        result.add(new RunExternal(file,fa));
      }
    }
    
  }

} 
