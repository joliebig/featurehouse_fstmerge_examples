

package com.jgoodies.uif_lite.panel;

import com.jgoodies.looks.LookUtils;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.AbstractBorder;




public class SimpleInternalFrame extends JPanel {

    private JLabel          titleLabel;
    private GradientPanel   gradientPanel;
    private JPanel          headerPanel;
    private boolean        isSelected;
    
    
    

    
    public SimpleInternalFrame(String title) {
        this(null, title, null, null);
    }
    
    
    
    public SimpleInternalFrame(Icon icon, String title) {
        this(icon, title, null, null);
    }

    
    
    public SimpleInternalFrame(String title, JToolBar bar, JComponent content) {
        this(null, title, bar, content);
    }
    

    
    public SimpleInternalFrame(
        Icon icon,
        String title,
        JToolBar bar,
        JComponent content) {
        super(new BorderLayout());
        this.isSelected = false;
        this.titleLabel = new JLabel(title, icon, SwingConstants.LEADING);
        JPanel top = buildHeader(titleLabel, bar);

        add(top, BorderLayout.NORTH);
        if (content != null) {
            setContent(content);
        }
        setBorder(new ShadowBorder());
        setSelected(true);
        updateHeader();
    }

    
    

    
    public Icon getFrameIcon() {
        return titleLabel.getIcon();
    }
    

    
    public void setFrameIcon(Icon newIcon) {
        Icon oldIcon = getFrameIcon();
        titleLabel.setIcon(newIcon);
        firePropertyChange("frameIcon", oldIcon, newIcon);
    }
    

    
    public String getTitle() {
        return titleLabel.getText();
    }
    
    
    
    public void setTitle(String newText) {
        String oldText = getTitle();
        titleLabel.setText(newText);
        firePropertyChange("title", oldText, newText);
    }
    
    
    
    public JToolBar getToolBar() {
        return headerPanel.getComponentCount() > 1
            ? (JToolBar) headerPanel.getComponent(1)
            : null;
    }
    

    
    public void setToolBar(JToolBar newToolBar) {
        JToolBar oldToolBar = getToolBar();
        if (oldToolBar == newToolBar) {
            return;
        }
        if (oldToolBar != null) {
            headerPanel.remove(oldToolBar);
        }
        if (newToolBar != null) {
            newToolBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            headerPanel.add(newToolBar, BorderLayout.EAST);
        }
        updateHeader();
        firePropertyChange("toolBar", oldToolBar, newToolBar);
    }

    
    
    public Component getContent() {
        return hasContent() ? getComponent(1) : null;
    }
    
    
    
    public void setContent(Component newContent) {
        Component oldContent = getContent();
        if (hasContent()) {
            remove(oldContent);
        }
        add(newContent, BorderLayout.CENTER);
        firePropertyChange("content", oldContent, newContent);
    }
    

    
    public boolean isSelected() {
        return isSelected;
    }
    
    
    
    public void setSelected(boolean newValue) {
        boolean oldValue = isSelected();
        isSelected = newValue;
        updateHeader();
        firePropertyChange("selected", oldValue, newValue);
    }
    

    

    
    private JPanel buildHeader(JLabel label, JToolBar bar) {
        gradientPanel =
            new GradientPanel(new BorderLayout(), getHeaderBackground());
        label.setOpaque(false);

        gradientPanel.add(label, BorderLayout.WEST);
        gradientPanel.setBorder(BorderFactory.createEmptyBorder(3, 4, 3, 1));

        headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(gradientPanel, BorderLayout.CENTER);
        setToolBar(bar);
        headerPanel.setBorder(new RaisedHeaderBorder());
        headerPanel.setOpaque(false);
        return headerPanel;
    }

    
    private void updateHeader() {
        gradientPanel.setBackground(getHeaderBackground());
        gradientPanel.setOpaque(isSelected());
        titleLabel.setForeground(getTextForeground(isSelected()));
        headerPanel.repaint();
    }
    

    
    public void updateUI() {
        super.updateUI();
        if (titleLabel != null) {
            updateHeader();
        }
    }


    

    
    private boolean hasContent() {
        return getComponentCount() > 1;
    }
    
    
    protected Color getTextForeground(boolean selected) {
        Color c =
            UIManager.getColor(
                selected
                    ? "SimpleInternalFrame.activeTitleForeground"
                    : "SimpleInternalFrame.inactiveTitleForeground");
        if (c != null) {
            return c;
        }
        return UIManager.getColor(
            selected 
                ? "InternalFrame.activeTitleForeground" 
                : "Label.foreground");

    }

    
    protected Color getHeaderBackground() {
        
        Color c =
            UIManager.getColor("SimpleInternalFrame.activeTitleBackground");
        if (c != null)
            return c;
        if (LookUtils.IS_LAF_WINDOWS_XP_ENABLED)
            c = UIManager.getColor("InternalFrame.activeTitleGradient");
        return c != null
            ? c
            : UIManager.getColor("InternalFrame.activeTitleBackground");
    }


    

    
    private static class RaisedHeaderBorder extends AbstractBorder {

        private static final Insets INSETS = new Insets(1, 1, 1, 0);

        public Insets getBorderInsets(Component c) { return INSETS; }

        public void paintBorder(Component c, Graphics g,
            int x, int y, int w, int h) {
                
            g.translate(x, y);
            g.setColor(UIManager.getColor("controlLtHighlight"));
            g.fillRect(0, 0,   w, 1);
            g.fillRect(0, 1,   1, h-1);
            g.setColor(UIManager.getColor("controlShadow"));
            g.fillRect(0, h-1, w, 1);
            g.translate(-x, -y);
        }
    }

    
    private static class ShadowBorder extends AbstractBorder {

        private static final Insets INSETS = new Insets(1, 1, 3, 3);

        public Insets getBorderInsets(Component c) { return INSETS; }

        public void paintBorder(Component c, Graphics g,
            int x, int y, int w, int h) {
                
            Color shadow        = UIManager.getColor("controlShadow");
            if (shadow == null) {
                shadow = Color.GRAY;
            }
            Color lightShadow   = new Color(shadow.getRed(), 
                                            shadow.getGreen(), 
                                            shadow.getBlue(), 
                                            170);
            Color lighterShadow = new Color(shadow.getRed(),
                                            shadow.getGreen(),
                                            shadow.getBlue(),
                                            70);
            g.translate(x, y);
            
            g.setColor(shadow);
            g.fillRect(0, 0, w - 3, 1);
            g.fillRect(0, 0, 1, h - 3);
            g.fillRect(w - 3, 1, 1, h - 3);
            g.fillRect(1, h - 3, w - 3, 1);
            
            g.setColor(lightShadow);
            g.fillRect(w - 3, 0, 1, 1);
            g.fillRect(0, h - 3, 1, 1);
            g.fillRect(w - 2, 1, 1, h - 3);
            g.fillRect(1, h - 2, w - 3, 1);
            
            g.setColor(lighterShadow);
            g.fillRect(w - 2, 0, 1, 1);
            g.fillRect(0, h - 2, 1, 1);
            g.fillRect(w-2, h-2, 1, 1);
            g.fillRect(w - 1, 1, 1, h - 2);
            g.fillRect(1, h - 1, w - 2, 1);
            g.translate(-x, -y);
        }
    }

    
    private static class GradientPanel extends JPanel {
        
        private GradientPanel(LayoutManager lm, Color background) {
            super(lm);
            setBackground(background);
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (!isOpaque()) {
                return;
            }
            Color control = UIManager.getColor("control");
            int width  = getWidth();
            int height = getHeight();

            Graphics2D g2 = (Graphics2D) g;
            Paint storedPaint = g2.getPaint();
            g2.setPaint(
                new GradientPaint(0, 0, getBackground(), width, 0, control));
            g2.fillRect(0, 0, width, height);
            g2.setPaint(storedPaint);
        }
    }

}