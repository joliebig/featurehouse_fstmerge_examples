

package edu.rice.cs.drjava.plugins.eclipse;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


public class DrJavaPreferencePage extends FieldEditorPreferencePage
  implements IWorkbenchPreferencePage {
  
  
  public DrJavaPreferencePage() {
    super("DrJava", GRID);  
  }
  
  
  public void init(IWorkbench workbench) {
  }
  
  
  protected void createFieldEditors() {
    addField(new BooleanFieldEditor(DrJavaConstants.INTERACTIONS_RESET_PROMPT,
                                    "Prompt Before Resetting Interactions Pane",
                                    SWT.NONE, getFieldEditorParent()));
    addField(new BooleanFieldEditor(DrJavaConstants.ALLOW_PRIVATE_ACCESS,
                                    "Allow Access to Private and Protected Members of Classes",
                                    SWT.NONE, getFieldEditorParent()));
    addField(new BooleanFieldEditor(DrJavaConstants.INTERACTIONS_EXIT_PROMPT,
                                    "Prompt if Interactions Pane Exits Unexpectedly",
                                    SWT.NONE, getFieldEditorParent()));
    addField(new IntegerFieldEditor(DrJavaConstants.HISTORY_MAX_SIZE,
                                    "Size of Interactions History",
                                    getFieldEditorParent()));
    addField(new StringFieldEditor(DrJavaConstants.JVM_ARGS,
                                   "Arguments to the Interactions JVM",
                                   getFieldEditorParent()));
  }

  
  protected IPreferenceStore doGetPreferenceStore() {
    return EclipsePlugin.getDefault().getPreferenceStore();
  }
}
