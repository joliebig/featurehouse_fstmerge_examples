

package edu.rice.cs.drjava.plugins.eclipse;

import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;


public class DrJavaPerspective implements IPerspectiveFactory {
  
  
  public void createInitialLayout(IPageLayout layout) {
    String editorArea = layout.getEditorArea();
    
    
    IFolderLayout bottom =
      layout.createFolder("bottom", IPageLayout.BOTTOM, 0.75f, editorArea);
    bottom.addView("edu.rice.cs.drjava.plugins.eclipse.views.InteractionsView");
    bottom.addPlaceholder(IConsoleConstants.ID_CONSOLE_VIEW);
    bottom.addPlaceholder(NewSearchUI.SEARCH_VIEW_ID);
    
    
    IFolderLayout left =
      layout.createFolder("left", IPageLayout.LEFT, 0.25f, editorArea);
    left.addView(JavaUI.ID_PACKAGES);
    
    
    left.addPlaceholder(JavaUI.ID_TYPE_HIERARCHY);
    left.addPlaceholder(IPageLayout.ID_RES_NAV);
    
    
    
    layout.addActionSet(IDebugUIConstants.LAUNCH_ACTION_SET);
    layout.addActionSet(JavaUI.ID_ELEMENT_CREATION_ACTION_SET);
    
    
    layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewPackageCreationWizard");
    layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewClassCreationWizard");
    layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewInterfaceCreationWizard");
    
    
  }
}
