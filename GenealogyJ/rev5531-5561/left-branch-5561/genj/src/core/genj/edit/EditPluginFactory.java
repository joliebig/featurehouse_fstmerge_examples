
package genj.edit;

import genj.app.PluginFactory;
import genj.app.Workbench;
import genj.app.WorkbenchListener;
import genj.app.Workbench.ToolLocation;
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
import genj.gedcom.Context;
import genj.gedcom.Entity;
import genj.gedcom.Fam;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomDirectory;
import genj.gedcom.GedcomException;
import genj.gedcom.GedcomListener;
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
import genj.util.swing.Action2;
import genj.util.swing.MenuHelper;
import genj.util.swing.NestedBlockLayout;
import genj.view.ActionProvider;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JPanel;

import spin.Spin;

public class EditPluginFactory implements PluginFactory {
  
  public Object createPlugin(Workbench workbench) {
    return new EditPlugin(workbench);
  }

  private class EditPlugin implements ActionProvider, WorkbenchListener {
    
    private Workbench workbench;
    private List<Action2> workbenchActions = new ArrayList<Action2>();
    
    
    private EditPlugin(Workbench workbench) {
      this.workbench = workbench;
      workbench.addWorkbenchListener(this);
    }
    
    public int getPriority() {
      return HIGH;
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
        fireSelection(new Context(e), false);
      }
    
    }

    
    public List<Action2> createActions(Property[] properties) {
      List<Action2> result = new ArrayList<Action2>();
      
      for (int i = 0; i < properties.length; i++) 
        if (properties[i] instanceof Entity) return result;
      
      if (Enigma.isAvailable())
        result.add(new TogglePrivate(properties[0].getGedcom(), Arrays.asList(properties)));
      
      result.add(new DelProperty(properties));
      
      return result;
    }

    
    public List<Action2> createActions(Property property) {
      
      
      List<Action2> result = new ArrayList<Action2>();
      
      
      if (property instanceof PropertyFile)  
        createActions(result, (PropertyFile)property); 
        
      
      if (property instanceof PropertyPlace)  
        result.add(new SetPlaceHierarchy((PropertyPlace)property)); 
        
      
      MetaProperty[] subs = property.getNestedMetaProperties(0);
      for (int s=0;s<subs.length;s++) {
        
        Class<? extends Property> type = subs[s].getType();
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

    
      public List<Action2> createActions(Entity entity) {
        
        
        List<Action2> result = new ArrayList<Action2>();
        
        
        if (entity instanceof Indi) createActions(result, (Indi)entity);
        
        if (entity instanceof Fam) createActions(result, (Fam)entity);
        
        if (entity instanceof Submitter) createActions(result, (Submitter)entity);
        
        
        result.add(MenuHelper.NOOP);
    
        
        MetaProperty[] subs = entity.getNestedMetaProperties(0);
        for (int s=0;s<subs.length;s++) {
          
          Class<? extends Property> type = subs[s].getType();
          if (type==PropertyNote.class||
              type==PropertyRepository.class||
              type==PropertySource.class||
              type==PropertySubmitter.class||
              type==PropertyMedia.class
              ) {
            result.add(new CreateXReference(entity,subs[s].getTag()));
          }
        }
    
        
        result.add(MenuHelper.NOOP);
        result.add(new DelEntity(entity));
        
        
        if (null==workbench.getView(new EditViewFactory()))
          result.add(new OpenForEdit(workbench, new Context(entity)));
        
        
        return result;
      }

    
    public List<Action2> createActions(Gedcom gedcom) {
      
      List<Action2> result = new ArrayList<Action2>();
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
    
      result.add(MenuHelper.NOOP);
      result.add(new Undo(gedcom, gedcom.canUndo()));
      result.add(new Redo(gedcom, gedcom.canRedo()));
    
      
      return result;
    }

    
    private void createActions(List<Action2> result, Indi indi) {
      result.add(new CreateChild(indi, true));
      result.add(new CreateChild(indi, false));
      result.add(new CreateParent(indi));
      result.add(new CreateSpouse(indi));
      result.add(new CreateSibling(indi, true));
      result.add(new CreateSibling(indi, false));
      result.add(new CreateAlias(indi));
    }

    
    private void createActions(List<Action2> result, Fam fam) {
      result.add(new CreateChild(fam, true));
      result.add(new CreateChild(fam, false));
      if (fam.getNoOfSpouses()<2)
        result.add(new CreateParent(fam));
      if (fam.getNoOfSpouses()!=0)
        result.add(new SwapSpouses(fam));
    }

    
    private void createActions(List<Action2> result, Submitter submitter) {
      result.add(new SetSubmitter(submitter));
    }

    
    private void createActions(List<Action2> result, PropertyFile file) {
    
      
      String suffix = file.getSuffix();
        
      
      List<FileAssociation> assocs = FileAssociation.getAll(suffix);
      if (assocs.isEmpty()) {
        result.add(new RunExternal(file));
      } else {
        for (FileAssociation fa : assocs) {
          result.add(new RunExternal(file,fa));
        }
      }
      
    }

    public void commitRequested() {
    }

    public void gedcomClosed(Gedcom gedcom) {
      for (Action2 action : workbenchActions) {
        workbench.uninstallTool(action);
        if (action instanceof GedcomListener)
          gedcom.removeGedcomListener((GedcomListener)Spin.over(action));
      }
      workbenchActions.clear();
    }

    public void gedcomOpened(Gedcom gedcom) {
      workbenchActions.add(new Undo(gedcom));
      workbenchActions.add(new Redo(gedcom));
      for (Action2 action : workbenchActions) {
        workbench.installTool(action, ToolLocation.TOOLBAR);
        if (action instanceof GedcomListener)
          gedcom.addGedcomListener((GedcomListener)Spin.over(action));
      }
    }

    public void selectionChanged(Context context, boolean isActionPerformed) {
      
      
    }

    public boolean workbenchClosing() {
      
      return false;
    }
    
  }
  
}
