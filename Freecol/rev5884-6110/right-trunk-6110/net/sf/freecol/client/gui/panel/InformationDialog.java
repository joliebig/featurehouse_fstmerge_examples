

package net.sf.freecol.client.gui.panel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.common.resources.ResourceManager;

import net.miginfocom.swing.MigLayout;


public class InformationDialog extends FreeColDialog<Boolean> {

    private static Image bgImage = ResourceManager.getImage("InformationDialog.backgroundImage");

    
    public InformationDialog(Canvas canvas, String text, ImageIcon image) {
        this(canvas, new String[] { text }, new ImageIcon[] { image });
    }

    
    public InformationDialog(Canvas parent, String[] texts, ImageIcon[] images) {
        super(parent);
        setLayout(new MigLayout("wrap 1, insets 200 10 10 10", "[510]", "[242]20[20]"));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        if (images != null) {
            textPanel.setLayout(new MigLayout("wrap 2", "", ""));
        }

        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                setResponse(Boolean.FALSE);
            }
        });

        if (images == null) {
            for (String text : texts) {
                textPanel.add(getDefaultTextArea(text, 30));
            }
        } else {
            for (int i = 0; i < texts.length; i++) {
                if (images[i] == null) {
                    textPanel.add(getDefaultTextArea(texts[i], 30), "skip");
                } else {
                    textPanel.add(new JLabel(images[i]));
                    textPanel.add(getDefaultTextArea(texts[i], 30));
                }
            }
        }

        JScrollPane scrollPane = new JScrollPane(textPanel,
                                                 JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                                 JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        setBorder(null);

        add(scrollPane);
        add(okButton, "tag ok");

    }

    public void requestFocus() {
        okButton.requestFocus();
    }

    
    public void paintComponent(Graphics g) {
        if (bgImage != null) {
            g.drawImage(bgImage, 0, 0, this);
        } else {
            int width = getWidth();
            int height = getHeight();
            Image tempImage = ResourceManager.getImage("BackgroundImage");
            if (tempImage != null) {
                for (int x = 0; x < width; x += tempImage.getWidth(null)) {
                    for (int y = 0; y < height; y += tempImage.getHeight(null)) {
                        g.drawImage(tempImage, x, y, null);
                    }
                }
            } else {
                g.setColor(getBackground());
                g.fillRect(0, 0, width, height);
            }
        }
    }

}