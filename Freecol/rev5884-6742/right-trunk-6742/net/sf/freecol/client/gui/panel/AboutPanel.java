


package net.sf.freecol.client.gui.panel;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import net.sf.freecol.FreeCol;
import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.resources.ResourceManager;


public final class AboutPanel extends FreeColPanel {

    private static final Logger logger = Logger.getLogger(AboutPanel.class.getName());

    
    public AboutPanel(Canvas parent) {
        super(parent);
        
        setLayout(new BorderLayout());

        
        JPanel header = new JPanel();
        this.add(header, BorderLayout.NORTH);
        Image tempImage = ResourceManager.getImage("TitleImage");
        if (tempImage != null) {
            JLabel logoLabel = new JLabel(new ImageIcon(tempImage));
            logoLabel.setBorder(new CompoundBorder(new EmptyBorder(2,2,2,2), new BevelBorder(BevelBorder.LOWERED)));
            header.add(logoLabel,JPanel.CENTER_ALIGNMENT);
        }
        
        
        JPanel infoPanel = new JPanel(new GridLayout(3, 1));
        this.add(infoPanel,BorderLayout.CENTER);
        infoPanel.setBorder(new EmptyBorder(10, 10, 0, 10));
        
        JPanel table = new JPanel(new GridLayout(3, 2));
        infoPanel.add(table);
        table.add(new JLabel(Messages.message("aboutPanel.version")));
        table.add(new JLabel(FreeCol.getRevision()));
        table.add(new JLabel(Messages.message("aboutPanel.officialSite")));
        String siteURL = "http://www.freecol.org";
        JLabel site = new JLabel("<html><font color='Blue'>"+siteURL+"</font></html>");
        site.setFocusable(true);
        site.addMouseListener(new URLMouseListener(siteURL));
        table.add(site);
        table.add(new JLabel(Messages.message("aboutPanel.sfProject")));
        String projectURL = "http://sourceforge.net/projects/freecol/";
        JLabel project = new JLabel("<html><font color='Blue'>"+projectURL+"</font></html>");
        project.setFocusable(true);
        project.addMouseListener(new URLMouseListener(projectURL));
        table.add(project);
        
        String disclaimer = Messages.message("aboutPanel.legalDisclaimer");
        JTextArea textarea = new JTextArea();
        textarea.setOpaque(false);
        textarea.setText(disclaimer);
        textarea.setLineWrap(true);
        textarea.setWrapStyleWord(true);
        textarea.setEditable(false);
        textarea.setFocusable(false);
        infoPanel.add(textarea);
        
        infoPanel.add(new JLabel(Messages.message("aboutPanel.copyright"),JLabel.CENTER),BorderLayout.CENTER);
        
        this.add(okButton, BorderLayout.SOUTH);

        setSize(getPreferredSize());
    }

    
    public class URLMouseListener implements MouseListener {
        private String url;
        public URLMouseListener(String url) {
        	this.url = url;
        }
        public void mouseEntered(MouseEvent e) {
        }
        public void mouseExited(MouseEvent e) {
        }
        public void mousePressed(MouseEvent e) {
        }
        public void mouseReleased(MouseEvent e) {
        }
        public void mouseClicked(MouseEvent e) {
            if (e.getButton()==MouseEvent.BUTTON1) {
                
                openBrowserURL();
            }
        }
        public void openBrowserURL() {
            String os = System.getProperty("os.name");
            String[] cmd = null;
            if (os==null) {
                
                return;
            } else if (os.toLowerCase().contains("mac")) {
                
                cmd = new String[] { "open" , "-a", "Safari", url };
            } else if (os.toLowerCase().contains("windows")) {
                
                cmd = new String[] { "rundll32.exe", "url.dll,FileProtocolHandler", url};
            } else if (os.toLowerCase().contains("linux")) {
                
                cmd = new String[] {"xdg-open", url};
            } else {
                
                
                
                cmd = new String[] { "firefox", url};
            }
            try {
                Runtime.getRuntime().exec(cmd);
            } catch(IOException x) {
                
            }
        }
    }
}
