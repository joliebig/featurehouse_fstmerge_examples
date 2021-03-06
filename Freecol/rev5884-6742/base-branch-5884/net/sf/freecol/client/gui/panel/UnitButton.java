


package net.sf.freecol.client.gui.panel;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

import net.sf.freecol.client.gui.action.FreeColAction;


public final class UnitButton extends JButton {
    
    
    public UnitButton(FreeColAction a) {
        super(a);
    }

    protected void configurePropertiesFromAction(Action a) {
        super.configurePropertiesFromAction(a);
        
        if (a != null) {
            setRolloverEnabled(true);
            Icon bi = (Icon) a.getValue(FreeColAction.BUTTON_IMAGE);
            setIcon(bi);
            setRolloverIcon((Icon) a.getValue(FreeColAction.BUTTON_ROLLOVER_IMAGE));
            setPressedIcon((Icon) a.getValue(FreeColAction.BUTTON_PRESSED_IMAGE));
            setDisabledIcon((Icon) a.getValue(FreeColAction.BUTTON_DISABLED_IMAGE));
            setToolTipText((String) a.getValue(FreeColAction.NAME));
            setText(null);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            
            if (bi == null) {
                throw new IllegalArgumentException("The given action is missing \"BUTTON_IMAGE\".");
            } 
            setSize(bi.getIconWidth(), bi.getIconHeight());
        }
    }
   
    protected PropertyChangeListener createActionPropertyChangeListener(Action a) {
        return new UnitButtonActionPropertyChangeListener(this);
    }
    
    private static class UnitButtonActionPropertyChangeListener implements PropertyChangeListener {
        private AbstractButton button;
        
        UnitButtonActionPropertyChangeListener(AbstractButton button) {
            this.button = button;
        }
        
        public void propertyChange(PropertyChangeEvent e) {     
            String propertyName = e.getPropertyName();
            if (e.getPropertyName().equals(Action.NAME)
                    || e.getPropertyName().equals(Action.SHORT_DESCRIPTION)) {
                String text = (String) e.getNewValue();
                button.setToolTipText(text);
            } else if (propertyName.equals("enabled")) {
                Boolean enabledState = (Boolean) e.getNewValue();
                button.setEnabled(enabledState.booleanValue());
                button.repaint();
            } else if (e.getPropertyName().equals(Action.SMALL_ICON)) {
                Icon icon = (Icon) e.getNewValue();
                button.setIcon(icon);
                button.repaint();
            } else if (e.getPropertyName().equals(FreeColAction.BUTTON_IMAGE)) {
                Icon icon = (Icon) e.getNewValue();
                button.setIcon(icon);
                button.repaint();
            } else if (e.getPropertyName().equals(FreeColAction.BUTTON_ROLLOVER_IMAGE)) {
                Icon icon = (Icon) e.getNewValue();
                button.setRolloverIcon(icon);
                button.repaint();
            } else if (e.getPropertyName().equals(FreeColAction.BUTTON_PRESSED_IMAGE)) {
                Icon icon = (Icon) e.getNewValue();
                button.setPressedIcon(icon);
                button.repaint();                
            } else if (e.getPropertyName().equals(FreeColAction.BUTTON_DISABLED_IMAGE)) {
                Icon icon = (Icon) e.getNewValue();
                button.setDisabledIcon(icon);
                button.repaint();                  
            } else if (e.getPropertyName().equals(Action.MNEMONIC_KEY)) {
                Integer mn = (Integer) e.getNewValue();
                button.setMnemonic(mn.intValue());
                button.repaint();
            } else if (e.getPropertyName().equals(Action.ACTION_COMMAND_KEY)) {
                button.setActionCommand((String)e.getNewValue());
            }
        }
    }
}
