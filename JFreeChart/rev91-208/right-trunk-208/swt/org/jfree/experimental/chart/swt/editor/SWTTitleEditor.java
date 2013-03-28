

package org.jfree.experimental.chart.swt.editor;

import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.title.Title;
import org.jfree.experimental.swt.SWTPaintCanvas;
import org.jfree.experimental.swt.SWTUtils;


class SWTTitleEditor extends Composite {
    
    
    private boolean showTitle;

    
    private Button showTitleCheckBox;

    
    private Text titleField;

    
    private FontData titleFont;

    
    private Text fontField;

    
    private Button selectFontButton;

    
    private Color titleColor;

    
    private Button selectColorButton;

    
    protected static ResourceBundle localizationResources 
        = ResourceBundle.getBundle("org.jfree.chart.editor.LocalizationBundle");

    
    private Font font;
    
    
    SWTTitleEditor(Composite parent, int style, Title title) {
        super(parent, style);
        FillLayout layout = new FillLayout();
        layout.marginHeight = layout.marginWidth = 4;
        setLayout( layout );
        
        TextTitle t = (title != null ? (TextTitle) title 
                : new TextTitle(localizationResources.getString("Title")));
        this.showTitle = (title != null);
        this.titleFont = SWTUtils.toSwtFontData(getDisplay(), t.getFont(), 
                true);
        this.titleColor = SWTUtils.toSwtColor(getDisplay(), t.getPaint());
        
        Group general = new Group(this, SWT.NONE);
        general.setLayout(new GridLayout(3, false));
        general.setText(localizationResources.getString("General"));
        
        Label label = new Label(general, SWT.NONE);
        label.setText(localizationResources.getString("Show_Title"));
        GridData gridData = new GridData();
        gridData.horizontalSpan = 2;
        label.setLayoutData(gridData);
        showTitleCheckBox = new Button(general, SWT.CHECK);
        showTitleCheckBox.setSelection(this.showTitle);
        showTitleCheckBox.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, 
                false, false));
        showTitleCheckBox.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        showTitle = showTitleCheckBox.getSelection();
                    }
                });
        
        new Label(general, SWT.NONE).setText(localizationResources.getString(
                "Text"));
        titleField = new Text(general, SWT.BORDER);
        titleField.setText(t.getText());
        titleField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, 
                false));
        new Label(general, SWT.NONE).setText("");
        
        new Label(general, SWT.NONE).setText(localizationResources.getString(
                "Font"));
        fontField = new Text(general, SWT.BORDER);
        fontField.setText(titleFont.toString());
        fontField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, 
                false));
        selectFontButton = new Button(general, SWT.PUSH);
        selectFontButton.setText(localizationResources.getString("Select..."));
        selectFontButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        
                        FontDialog dlg = new FontDialog(getShell());
                        dlg.setText(localizationResources.getString(
                                "Font_Selection"));
                        dlg.setFontList(new FontData[] { titleFont });
                        if (dlg.open() != null) {
                            
                            if (font != null) font.dispose();
                            
                            
                            font = new Font(getShell().getDisplay(), 
                                    dlg.getFontList());
                            
                            fontField.setText(font.getFontData()[0].toString());
                            titleFont = font.getFontData()[0];
                        }
                    }
                }
        );
        
        new Label(general, SWT.NONE).setText(localizationResources.getString(
                "Color"));
        
        
        final SWTPaintCanvas colorCanvas = new SWTPaintCanvas(general, 
                SWT.NONE, this.titleColor);
        GridData canvasGridData = new GridData(SWT.FILL, SWT.CENTER, true, 
                false);
        canvasGridData.heightHint = 20;
        colorCanvas.setLayoutData(canvasGridData);
        selectColorButton = new Button(general, SWT.PUSH);
        selectColorButton.setText(localizationResources.getString("Select..."));
        selectColorButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        
                        ColorDialog dlg = new ColorDialog(getShell());
                        dlg.setText(localizationResources.getString(
                                "Title_Color"));
                        dlg.setRGB(titleColor.getRGB());
                        RGB rgb = dlg.open();
                        if (rgb != null) {
                            
                            
                            titleColor = new Color(getDisplay(), rgb);
                            colorCanvas.setColor( titleColor );
                        }
                    }
                }
        );
    }

    
    public String getTitleText() {
        return this.titleField.getText();
    }

    
    public FontData getTitleFont() {
        return this.titleFont;
    }

    
    public Color getTitleColor() {
        return this.titleColor;
    }

    
    public void setTitleProperties(JFreeChart chart) {
        if (this.showTitle) {
            TextTitle title = chart.getTitle();
            if (title == null) {
                title = new TextTitle();
                chart.setTitle(title);
            }
            title.setText(getTitleText());
            title.setFont(SWTUtils.toAwtFont(getDisplay(), getTitleFont(), 
                    true));
            title.setPaint(SWTUtils.toAwtColor(getTitleColor()));
        }
        else {
            chart.setTitle((TextTitle) null);
        }
    }
}
