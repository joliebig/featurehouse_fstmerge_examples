

package net.sf.freecol.client.gui.panel;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.model.Goods;
import net.sf.freecol.common.model.Unit;


public final class CaptureGoodsDialog extends FreeColDialog<List<Goods>> implements ActionListener {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(CaptureGoodsDialog.class.getName());


    @SuppressWarnings("unused") 
    private JButton allButton;
    private JButton noneButton;
    private JButton acceptButton;
    private JList goodsList;


    private int maxCargo;

    public CaptureGoodsDialog(Canvas parent) {
        super(parent);

        setBorder(null);
        setOpaque(false);

        allButton = new JButton(Messages.message("all"));
        noneButton = new JButton(Messages.message("none"));
        acceptButton = new JButton(Messages.message("accept"));


        goodsList = new JList();
        goodsList.setCellRenderer(new CheckBoxRenderer());

        goodsList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                int selectedIndex = goodsList.locationToIndex(me.getPoint());
                if (selectedIndex < 0)
                    return;
                GoodsItem item = (GoodsItem) goodsList.getModel().getElementAt(selectedIndex);
                if (item.isEnabled())
                    item.setSelected(!item.isSelected());
                updateComponents();
            }
        });

        JScrollPane goodsListScroll = new JScrollPane(goodsList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        goodsListScroll.setSize(140, 150);
        goodsListScroll.setLocation(10, 10);

        allButton.setLocation(10, 170);
        noneButton.setLocation(10 + 60 + 15, 170);
        acceptButton.setLocation(35, 10 + 140 + 10 + 20 + 20);




        allButton.setSize(65, 20);
        noneButton.setSize(64, 20);
        acceptButton.setSize(80, 20);
        
        enterPressesWhenFocused(allButton);
        enterPressesWhenFocused(noneButton);
        enterPressesWhenFocused(acceptButton);

        add(goodsListScroll);
        add(allButton);
        add(noneButton);
        add(acceptButton);

        this.setLayout(null);


        allButton.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < goodsList.getModel().getSize() && i < maxCargo; i++) {
                    GoodsItem gi = (GoodsItem) goodsList.getModel().getElementAt(i);
                    gi.setSelected(true);
                    updateComponents();
                }
            }
        });
        noneButton.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < goodsList.getModel().getSize(); i++) {
                    GoodsItem gi = (GoodsItem) goodsList.getModel().getElementAt(i);
                    gi.setSelected(false);
                    updateComponents();
                }
            }
        });
        noneButton.setMnemonic('n');
        allButton.setMnemonic('l');
        acceptButton.setMnemonic('a');
    }

    public void requestFocus() {
        acceptButton.requestFocus();
    }

    private void updateComponents() {
        int selectedCount = 0;
        for (int i = 0; i < goodsList.getModel().getSize(); i++) {
            GoodsItem gi = (GoodsItem) goodsList.getModel().getElementAt(i);
            if (gi.isSelected())
                selectedCount++;
        }

        if (selectedCount >= maxCargo) {
            allButton.setEnabled(false);
            for (int i = 0; i < goodsList.getModel().getSize(); i++) {
                GoodsItem gi = (GoodsItem) goodsList.getModel().getElementAt(i);
                if (!gi.isSelected())
                    gi.setEnabled(false);
            }
        }
        else {
            allButton.setEnabled(true);
            for (int i = 0; i < goodsList.getModel().getSize(); i++) {
                GoodsItem gi = (GoodsItem) goodsList.getModel().getElementAt(i);
                if (!gi.isSelected())
                    gi.setEnabled(true);
            }
        }

        goodsList.repaint();
    }

    
    public void initialize(Unit capturedUnit, Unit capturingUnit) {
        maxCargo = capturingUnit.getType().getSpace();
        GoodsItem[] goods = new GoodsItem[capturedUnit.getGoodsCount()];
        if (goods.length > 0) {
            Iterator<Goods> iter = capturedUnit.getGoodsIterator();
            for (int i = 0; iter.hasNext(); i++) {
                Goods g = iter.next();
                goods[i] = new GoodsItem(g);
            }
        }
        goodsList.setListData(goods);


    }

    
    public void actionPerformed(ActionEvent e) {
        ArrayList<Goods> list = new ArrayList<Goods>(4);

        for (int i = 0; i < goodsList.getModel().getSize(); i++) {
            GoodsItem gi = (GoodsItem) goodsList.getModel().getElementAt(i);
            if(gi.isSelected())
                list.add(gi.getGoods());
        }
        setResponse(list);
    }

    private class CheckBoxRenderer extends JCheckBox implements ListCellRenderer {

        public CheckBoxRenderer() {
            setBackground(UIManager.getColor("List.textBackground"));
            setForeground(UIManager.getColor("List.textForeground"));
        }

        public Component getListCellRendererComponent(JList listBox, Object obj, int currentindex,
                                                      boolean isChecked, boolean hasFocus) {
            setSelected(((GoodsItem) obj).isSelected());
            setText(((GoodsItem) obj).toString());
            setEnabled(((GoodsItem) obj).isEnabled());
            return this;
        }
    }

    class GoodsItem extends JCheckBox {
        private Goods good;

        public GoodsItem(Goods good) {
            this.good = good;
        }

        public Goods getGoods() {
            return good;
        }

        public String toString() {
            return good.getAmount() + " " + Messages.getName(good.getType());
        }
    }
}
