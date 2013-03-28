

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
        this.showTitleCheckBox = new Button(general, SWT.CHECK);
        this.showTitleCheckBox.setSelection(this.showTitle);
        this.showTitleCheckBox.setLayoutData(new GridData(SWT.CENTER, 
                SWT.CENTER, false, false));
        this.showTitleCheckBox.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        SWTTitleEditor.this.showTitle = SWTTitleEditor.this
                                .showTitleCheckBox.getSelection();
                    }
                });
        
        new Label(general, SWT.NONE).setText(localizationResources.getString(
                "Text"));
        this.titleField = new Text(general, SWT.BORDER);
        this.titleField.setText(t.getText());
        this.titleField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, 
                false));
        new Label(general, SWT.NONE).setText("");
        
        new Label(general, SWT.NONE).setText(localizationResources.getString(
                "Font"));
        this.fontField = new Text(general, SWT.BORDER);
        this.fontField.setText(this.titleFont.toString());
        this.fontField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, 
                false));
        this.selectFontButton = new Button(general, SWT.PUSH);
        this.selectFontButton.setText(localizationResources.getString(
                "Select..."));
        this.selectFontButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        
                        FontDialog dlg = new FontDialog(getShell());
                        dlg.setText(localizationResources.getString(
                                "Font_Selection"));
                        dlg.setFontList(new FontData[] { 
                                SWTTitleEditor.this.titleFont });
                        if (dlg.open() != null) {
                            
                            if (SWTTitleEditor.this.font != null) {
                                SWTTitleEditor.this.font.dispose();
                            }
                            
                            
                            SWTTitleEditor.this.font = new Font(getShell()
                                    .getDisplay(), dlg.getFontList());
                            
                            SWTTitleEditor.this.fontField.setText(
                                    SWTTitleEditor.this.font.getFontData()[0]
                                    .toString());
                            SWTTitleEditor.this.titleFont 
                                    = SWTTitleEditor.this.font.getFontData()[0];
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
        this.selectColorButton = new Button(general, SWT.PUSH);
        this.selectColorButton.setText(localizationResources.getString(
                "Select..."));
        this.selectColorButton.addSelectionListener(
                new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent event) {
                        
                        ColorDialog dlg = new ColorDialog(getShell());
                        dlg.setText(localizationResources.getString(
                                "Title_Color"));
                        dlg.setRGB(SWTTitleEditor.this.titleColor.getRGB());
                        RGB rgb = dlg.open();
                        if (rgb != null) {
                            
                            
                            SWTTitleEditor.this.titleColor = new Color(
                                    getDisplay(), rgb);
                            colorCanvas.setColor(
                                    SWTTitleEditor.this.titleColor);
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
