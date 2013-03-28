

package com.jgoodies.uif_lite.component;

import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.SplitPaneUI;
import javax.swing.plaf.basic.BasicSplitPaneUI;



public final class UIFSplitPane extends JSplitPane {
    
    
    public static final String PROPERTYNAME_DIVIDER_BORDER_VISIBLE = 
        "dividerBorderVisible";
    
    
    private static final Border EMPTY_BORDER = new EmptyBorder(0, 0, 0, 0);
    
    
    private boolean dividerBorderVisible;

   
    
    
    
    public UIFSplitPane() {
        this(JSplitPane.HORIZONTAL_SPLIT, false,
                new JButton(UIManager.getString("SplitPane.leftButtonText")),
                new JButton(UIManager.getString("SplitPane.rightButtonText")));
    }


    
    public UIFSplitPane(int newOrientation) {
        this(newOrientation, false);
    }


    
    public UIFSplitPane(int newOrientation,
                      boolean newContinuousLayout) {
        this(newOrientation, newContinuousLayout, null, null);
    }


    
    public UIFSplitPane(int orientation,
                         Component leftComponent,
                         Component rightComponent) {
        this(orientation, false, leftComponent, rightComponent);
    }
    
    
    
    public UIFSplitPane(int orientation,
                      boolean continuousLayout,
                      Component leftComponent,
                      Component rightComponent){
        super(orientation, continuousLayout, leftComponent, rightComponent);
        dividerBorderVisible = false;
    }
    
    
    
    public static UIFSplitPane createStrippedSplitPane(
             int orientation,
             Component leftComponent,
             Component rightComponent) {
        UIFSplitPane split = new UIFSplitPane(orientation, leftComponent, rightComponent);
        split.setBorder(EMPTY_BORDER);
        split.setOneTouchExpandable(false);
        return split;
    }
    
    
    
    
    
    public boolean isDividerBorderVisible() {
        return dividerBorderVisible;
    }
    
    
    
    public void setDividerBorderVisible(boolean newVisibility) {
        boolean oldVisibility = isDividerBorderVisible();
        if (oldVisibility == newVisibility)
            return;
        dividerBorderVisible = newVisibility;
        firePropertyChange(PROPERTYNAME_DIVIDER_BORDER_VISIBLE, 
                           oldVisibility, 
                           newVisibility);
    }
    

    
    
    
    public void updateUI() {
        super.updateUI();
        if (!isDividerBorderVisible())
            setEmptyDividerBorder();
    }
    

    
    private void setEmptyDividerBorder() {
        SplitPaneUI splitPaneUI = getUI();
        if (splitPaneUI instanceof BasicSplitPaneUI) {
            BasicSplitPaneUI basicUI = (BasicSplitPaneUI) splitPaneUI;
            basicUI.getDivider().setBorder(EMPTY_BORDER);
        }
    }
    
    
}