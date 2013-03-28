
package genj.edit;

import genj.app.Priority;
import genj.app.Workbench;
import genj.app.WorkbenchAdapter;
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
import genj.edit.beans.PropertyBean;
import genj.gedcom.Context;
import genj.gedcom.Entity;
import genj.gedcom.Fam;
import genj.gedcom.Gedcom;
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
import genj.gedcom.PropertySex;
import genj.gedcom.PropertySource;
import genj.gedcom.PropertySubmitter;
import genj.gedcom.Submitter;
import genj.gedcom.TagPath;
import genj.gedcom.UnitOfWork;
import genj.util.EnvironmentChecker;
import genj.util.Resources;
import genj.util.swing.Action2;
import genj.util.swing.DialogHelper;
import genj.util.swing.NestedBlockLayout;
import genj.util.swing.Action2.Group;
import genj.view.ActionProvider;
import genj.view.SelectionSink;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JPanel;


@Priority(priority=Priority.HIGH)
public class EditPlugin extends WorkbenchAdapter implements ActionProvider {
  
  private final static Resources RESOURCES = Resources.get(EditPlugin.class);
  private final static Logger LOG = Logger.getLogger("genj.edit");
  
  private String ACC_UNDO = "ctrl Z", ACC_REDO = "ctrl Y";
  private Workbench workbench;
  
  
   EditPlugin(Workbench workbench) {
    this.workbench = workbench;
    workbench.addWorkbenchListener(this);
    
    
    PropertyBean.getAvailableBeans();

  }
  
  @Override
  public void gedcomOpened(Workbench workbench, final Gedcom gedcom) {

    
    if (!gedcom.getEntities(Gedcom.INDI).isEmpty()) 
      return;

    if (0!=DialogHelper.openDialog(RESOURCES.getString("wizard.first", gedcom.getName()), DialogHelper.QUESTION_MESSAGE, RESOURCES.getString("wizard.empty", gedcom.getName()), Action2.okCancel(), workbench)) 
      return;
    
    try {
      wizardFirst(workbench, gedcom);
    } catch (Throwable t) {
      LOG.log(Level.WARNING, "problem in wizard", t);
    }
    
  }
  
  private Indi adamOrEve;
  
  private void wizardFirst(Workbench workbench, Gedcom gedcom) throws GedcomException {
    
    
    gedcom.doUnitOfWork(new UnitOfWork() {
      public void perform(Gedcom gedcom) throws GedcomException {
        adamOrEve = (Indi)gedcom.createEntity("INDI");
        adamOrEve.setSex(PropertySex.MALE);
      }
    });
    
    
    final BeanPanel panel = new BeanPanel();
    panel.setRoot(adamOrEve);
    if (0!=DialogHelper.openDialog(RESOURCES.getString("wizard.first", gedcom.getName()), DialogHelper.QUESTION_MESSAGE, panel, Action2.okCancel(), workbench)) {
      gedcom.undoUnitOfWork();
      return;
    }      
    
    gedcom.doMuteUnitOfWork(new UnitOfWork() {
      public void perform(Gedcom gedcom) throws GedcomException {
        
        panel.commit();
        
        
        Submitter submitter = (Submitter) gedcom.createEntity(Gedcom.SUBM);
        submitter.setName(EnvironmentChecker.getProperty("user.name", "?", "user name used as submitter in new gedcom"));
      }
    });
    
    SelectionSink.Dispatcher.fireSelection(workbench, new Context(gedcom.getFirstEntity(Gedcom.INDI)), true);

    
  }

  
  private static class CopyIndividual extends AbstractChange {
    
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
    protected Context execute(Gedcom gedcom, ActionEvent event) throws GedcomException {
      Entity e = gedcom.createEntity(Gedcom.INDI, dupe() ? null : existing.getId());
      e.copyProperties(existing.getProperties(), true);
      return new Context(e);
    }
  
  }

  
  private void createActions(List<? extends Property> properties, Action2.Group group) {
    
    
    if (Enigma.isAvailable())
      group.add(new TogglePrivate(properties.get(0).getGedcom(), properties));
    
    
    group.add(new DelProperty(properties));
    
    
  }

  
  private void createActions(Property property, Action2.Group group) {
    
    
    if (property instanceof PropertyFile)  
      createActions(group, (PropertyFile)property); 
      
    
    if (property instanceof PropertyPlace)  
      group.add(new SetPlaceHierarchy((PropertyPlace)property)); 
      
    
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
        
        group.add(new CreateXReference(property,subs[s].getTag()));
        
        continue;
      }
    }
    
    
    
    if ( property instanceof PropertyEvent
        && ( (property.getEntity() instanceof Indi)
            || property.getGedcom().getGrammar().getMeta(new TagPath("INDI:ASSO")).allows("TYPE"))  )
      group.add(new CreateAssociation(property));
    
    
    if (Enigma.isAvailable())
      group.add(new TogglePrivate(property.getGedcom(), Collections.singletonList(property)));
    
    
    if (!property.isTransient()) 
      group.add(new DelProperty(property));
  
    
  }
  
  
  public void createActions(Context context, Purpose purpose, Group result) {

    
    Action2.uninstall(workbench, ACC_REDO);
    Action2.uninstall(workbench, ACC_UNDO);
    
    
    if (context.getGedcom()==null)
      return;
    
    
    switch (purpose) {
      case MENU:
    	  
        
        Action2.Group edit = new EditActionGroup();
        if (context.getEntity()==null)
          createActions(context.getGedcom(), edit);
        else if (context.getEntities().size()==1)
          createActions(context.getEntity(), edit);
        result.add(edit);
          
        edit.add(new ActionProvider.SeparatorAction());
        edit.add(new Undo(context.getGedcom()).install(workbench, ACC_UNDO));
        edit.add(new Redo(context.getGedcom()).install(workbench, ACC_REDO));
        
        break;
        
      case CONTEXT:
        
        
        if (context.getProperties().size()>1) {
          Action2.Group group = new ActionProvider.PropertiesActionGroup(context.getProperties());
          createActions(context.getProperties(), group);
          if (group.size()>0)
            result.add(group);
        } else if (context.getProperties().size()==1) {
          
          Property cursor = context.getProperty();
          while (!(cursor instanceof Entity)) {
            Action2.Group group = new ActionProvider.PropertyActionGroup(cursor);
            createActions(cursor, group);
            if (group.size()>0)
              result.add(group);
            cursor = cursor.getParent();
          }
        }
     
        
        if (context.getEntities().size()==1) {
          Action2.Group group = new ActionProvider.EntityActionGroup(context.getEntity());
          createActions(context.getEntity(), group);
          if (group.size()>0)
            result.add(group);
          
          
          if (null==workbench.getView(EditViewFactory.class))
            result.add(new OpenForEdit(workbench, context));


        }
        
        
        Action2.Group group = new ActionProvider.GedcomActionGroup(context.getGedcom());
        createActions(context.getGedcom(), group);
        if (group.size()>0)
          result.add(group);
        
        result.add(new ActionProvider.SeparatorAction());
        result.add(new Undo(context.getGedcom()));
        result.add(new Redo(context.getGedcom()));
        
        break;
        
      case TOOLBAR:
        result.add(new Undo(context.getGedcom()));
        result.add(new Redo(context.getGedcom()));
        result.add(new ActionProvider.SeparatorAction());
        break;
    }

    
    
  }

  
  private void createActions(Entity entity, Action2.Group group) {
    
    
    if (entity instanceof Indi) 
      createActions((Indi)entity, group);
      
    
    if (entity instanceof Fam) createActions(group, (Fam)entity);
    
    if (entity instanceof Submitter) createActions(group, (Submitter)entity);
    
    
    group.add(new ActionProvider.SeparatorAction());

    
    MetaProperty[] subs = entity.getNestedMetaProperties(0);
    for (int s=0;s<subs.length;s++) {
      
      Class<? extends Property> type = subs[s].getType();
      if (type==PropertyNote.class||
          type==PropertyRepository.class||
          type==PropertySource.class||
          type==PropertySubmitter.class||
          type==PropertyMedia.class
          ) {
        group.add(new CreateXReference(entity,subs[s].getTag()));
      }
    }

    
    group.add(new ActionProvider.SeparatorAction());
    group.add(new DelEntity(entity));
    
    
  }

  
  private void createActions(Gedcom gedcom, Action2.Group group) {
    
    
    group.add(new CreateEntity(gedcom, Gedcom.INDI));
    group.add(new CreateEntity(gedcom, Gedcom.FAM));
    group.add(new CreateEntity(gedcom, Gedcom.NOTE));
    group.add(new CreateEntity(gedcom, Gedcom.OBJE));
    group.add(new CreateEntity(gedcom, Gedcom.REPO));
    group.add(new CreateEntity(gedcom, Gedcom.SOUR));
    group.add(new CreateEntity(gedcom, Gedcom.SUBM));
  
    
  }

  
  private void createActions(Indi indi, Action2.Group group) {
    
    Action2.Group more = new Action2.Group(Resources.get(this).getString("add.more"));
    
    if (indi.getParents().size()<2)
      group.add(new CreateParent(indi));
    else
      more.add(new CreateParent(indi));

    if (indi.getPartners().length==0)
      group.add(new CreateSpouse(indi));
    else
      more.add(new CreateSpouse(indi));

    group.add(new CreateChild(indi, true));
    group.add(new CreateChild(indi, false));
    group.add(new CreateSibling(indi, true));
    group.add(new CreateSibling(indi, false));
    
    more.add(new CreateAlias(indi));
    
    group.add(more);
  }

  
  private void createActions(Action2.Group group, Fam fam) {
    group.add(new CreateChild(fam, true));
    group.add(new CreateChild(fam, false));
    if (fam.getNoOfSpouses()<2)
      group.add(new CreateParent(fam));
    if (fam.getNoOfSpouses()!=0)
      group.add(new SwapSpouses(fam));
  }

  
  private void createActions(Action2.Group group, Submitter submitter) {
    group.add(new SetSubmitter(submitter));
  }

  
  private void createActions(Action2.Group group, PropertyFile file) {
    if (file.getFile()!=null)
      group.add(new RunExternal(file.getFile()));
  }

}

