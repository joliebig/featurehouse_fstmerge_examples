package net.sf.jabref; 

import java.awt.BorderLayout; 
import java.awt.event.ItemEvent; 
import java.awt.event.ItemListener; 

import javax.swing.*; 

import net.sf.jabref.external.ExternalFileTypeEditor; 

import com.jgoodies.forms.builder.DefaultFormBuilder; 
import com.jgoodies.forms.layout.FormLayout; 
import java.awt.event.ActionListener; 
import java.awt.event.ActionEvent; 

import net.sf.jabref.external.*; 
import net.sf.jabref.plugin.core.JabRefPlugin; 

public  class  ExternalTab  extends JPanel implements  PrefsTab {
	

	JabRefPreferences _prefs;

	

	JabRefFrame _frame;

	

	

	
            
    JCheckBox runAutoFileSearch;

	
    JButton editFileTypes;

	
    ItemListener regExpListener;

	

	JRadioButton useRegExpComboBox;

	
    JRadioButton matchExactKeyOnly = new JRadioButton(Globals.lang("Autolink only files that match the BibTeX key")),
        matchStartsWithKey = new JRadioButton(Globals.lang("Autolink files with names starting with the BibTeX key"));

	

    public ExternalTab(JabRefFrame frame, PrefsDialog3 prefsDiag, JabRefPreferences prefs,
                       HelpDialog helpDialog) {
		_prefs = prefs;
		_frame = frame;
		setLayout(new BorderLayout());

<<<<<<< C:\Dokumente und Einstellungen\Sven Apel\Eigene Dateien\Uni\fstmerge\fstmerge_tmp\fstmerge_var1_40597
		psDir = new JTextField(25);
		pdfDir = new JTextField(25);
        fileDir = new JTextField(25);
        pdf = new JTextField(25);
		ps = new JTextField(25);
		html = new JTextField(25);
		editFileTypes = new JButton(Globals.lang("Manage external file types"));
        runAutoFileSearch = new JCheckBox(Globals.lang("When opening file link, search for matching file if no link is defined"));
        regExpTextField = new JTextField(25);
        useRegExpComboBox = new JRadioButton(Globals.lang("Use Regular Expression Search"));
=======
		psDir = new JTextField(30);
		pdfDir = new JTextField(30);
        fileDir = new JTextField(30);
        pdf = new JTextField(30);
		ps = new JTextField(30);
		html = new JTextField(30);
		lyx = new JTextField(30);
		winEdt = new JTextField(30);
		vim = new JTextField(30);
		vimServer = new JTextField(30);
        citeCommand = new JTextField(30);
        led = new JTextField(30);
        editFileTypes = new JButton(Globals.lang("Manage external file types"));
        runAutoFileSearch = new JCheckBox("When opening file link, search for matching file if no link is defined");
        regExpTextField = new JTextField(30);
        useRegExpComboBox = new JRadioButton(Globals.lang("Use Regular Expression Search"));
>>>>>>> C:\Dokumente und Einstellungen\Sven Apel\Eigene Dateien\Uni\fstmerge\fstmerge_tmp\fstmerge_var2_40599
		regExpListener = new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				regExpTextField.setEditable(useRegExpComboBox.isSelected());
			}
		};
		useRegExpComboBox.addItemListener(regExpListener);

        editFileTypes.addActionListener(ExternalFileTypeEditor.getAction(prefsDiag));

        ButtonGroup bg = new ButtonGroup();
        bg.add(matchExactKeyOnly);
        bg.add(matchStartsWithKey);
        bg.add(useRegExpComboBox);

        BrowseAction browse;

		FormLayout layout = new FormLayout(
			"1dlu, 8dlu, left:pref, 4dlu, fill:150dlu, 4dlu, fill:pref","");

		DefaultFormBuilder builder = new DefaultFormBuilder(layout);

		builder.appendSeparator(Globals.lang("External file links"));
		JPanel pan = new JPanel();
		builder.append(pan);
		
		JLabel lab = new JLabel(Globals.lang("Main file directory") + ":");
		builder.append(lab);
		builder.append(fileDir);
		browse = new BrowseAction(_frame, fileDir, true);
		builder.append(new JButton(browse));
		builder.nextLine();

<<<<<<< C:\Dokumente und Einstellungen\Sven Apel\Eigene Dateien\Uni\fstmerge\fstmerge_tmp\fstmerge_var1_40597

		builder.append(new JPanel());
        builder.append(matchStartsWithKey, 3);
        builder.nextLine();
        builder.append(new JPanel());
        builder.append(matchExactKeyOnly, 3);
        builder.nextLine();
        builder.append(new JPanel());
        builder.append(useRegExpComboBox);
		builder.append(regExpTextField);
		HelpAction helpAction = new HelpAction(helpDialog, GUIGlobals.regularExpressionSearchHelp,
			Globals.lang("Help on Regular Expression Search"), GUIGlobals.getIconUrl("helpSmall"));
		builder.append(helpAction.getIconButton());
		builder.nextLine();
        builder.append(new JPanel());
        builder.append(runAutoFileSearch, 3);
        builder.nextLine();
		builder.appendSeparator(Globals.lang("Legacy file fields"));
		pan = new JPanel();
		builder.append(pan);		
		builder.append(new JLabel("<html>"+Globals.lang("Note that these settings are used for the legacy "
			+"<b>pdf</b> and <b>ps</b> fields only.<br>For most users, setting the <b>Main file directory</b> "
			+"above should be sufficient.")+"</html>"), 5);
		builder.nextLine();
		pan = new JPanel();
=======

		builder.append(new JPanel());
        builder.append(matchStartsWithKey, 3);
        builder.nextLine();
        builder.append(new JPanel());
        builder.append(matchExactKeyOnly, 3);
        builder.nextLine();
        builder.append(new JPanel());
        builder.append(useRegExpComboBox);
		builder.append(regExpTextField);
		HelpAction helpAction = new HelpAction(helpDialog, GUIGlobals.regularExpressionSearchHelp,
			Globals.lang("Help on Regular Expression Search"), GUIGlobals.getIconUrl("helpSmall"));
		builder.append(helpAction.getIconButton());
		builder.nextLine();
        builder.append(new JPanel());
        builder.append(runAutoFileSearch);
        builder.nextLine();
		builder.appendSeparator(Globals.lang("Legacy file fields"));
		pan = new JPanel();
		builder.append(pan);		
		builder.append(new JLabel("<html>"+Globals.lang("Note that these settings are used for the legacy "
			+"<b>pdf</b> and <b>ps</b> fields only.<br>For most users, setting the <b>Main file directory</b> "
			+"above should be sufficient.")+"</html>"), 5);
		builder.nextLine();
		pan = new JPanel();
>>>>>>> C:\Dokumente und Einstellungen\Sven Apel\Eigene Dateien\Uni\fstmerge\fstmerge_tmp\fstmerge_var2_40599
		builder.append(pan);
		lab = new JLabel(Globals.lang("Main PDF directory") + ":");
		builder.append(lab);
		builder.append(pdfDir);
		browse = new BrowseAction(_frame, pdfDir, true);
		builder.append(new JButton(browse));
		builder.nextLine();

        pan = new JPanel();
		builder.append(pan);
		lab = new JLabel(Globals.lang("Main PS directory") + ":");
		builder.append(lab);
		builder.append(psDir);
		browse = new BrowseAction(_frame, psDir, true);
		builder.append(new JButton(browse));
		builder.nextLine();
		builder.appendSeparator(Globals.lang("External programs"));

		builder.nextLine();
		lab = new JLabel(Globals.lang("Path to HTML viewer") + ":");
		builder.append(pan);
		builder.append(lab);
		builder.append(html);
		browse = new BrowseAction(_frame, html, false);
		if (Globals.ON_WIN)
			browse.setEnabled(false);
		builder.append(new JButton(browse));
		builder.nextLine();

        addSettingsButton(new PushToLyx(), builder);
        addSettingsButton(new PushToEmacs(), builder);
        addSettingsButton(new PushToWinEdt(), builder);
        addSettingsButton(new PushToVim(), builder);
        addSettingsButton(new PushToLatexEditor(), builder);

        
        builder.append(pan);
        builder.append(editFileTypes);
        
        pan = builder.getPanel();
		pan.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		add(pan, BorderLayout.CENTER);

    }


	

	public void setValues() {
		pdfDir.setText(_prefs.get("pdfDirectory"));
		psDir.setText(_prefs.get("psDirectory"));
        fileDir.setText(_prefs.get(GUIGlobals.FILE_FIELD+"Directory"));
        if (!Globals.ON_WIN) {
			pdf.setText(_prefs.get("pdfviewer"));
			ps.setText(_prefs.get("psviewer"));
			html.setText(_prefs.get("htmlviewer"));
		} else {
			pdf.setText(Globals.lang("Uses default application"));
			ps.setText(Globals.lang("Uses default application"));
			html.setText(Globals.lang("Uses default application"));
			pdf.setEnabled(false);
			ps.setEnabled(false);
			html.setEnabled(false);
		}

<<<<<<< C:\Dokumente und Einstellungen\Sven Apel\Eigene Dateien\Uni\fstmerge\fstmerge_tmp\fstmerge_var1_40600

        runAutoFileSearch.setSelected(_prefs.getBoolean("runAutomaticFileSearch"));
=======
		lyx.setText(_prefs.get("lyxpipe"));
		winEdt.setText(_prefs.get("winEdtPath"));
        vim.setText(_prefs.get("vim"));
		vimServer.setText(_prefs.get("vimServer"));
        led.setText(_prefs.get("latexEditorPath"));
        citeCommand.setText(_prefs.get("citeCommand"));
        runAutoFileSearch.setSelected(_prefs.getBoolean("runAutomaticFileSearch"));
>>>>>>> C:\Dokumente und Einstellungen\Sven Apel\Eigene Dateien\Uni\fstmerge\fstmerge_tmp\fstmerge_var2_40602
		regExpTextField.setText(_prefs.get(JabRefPreferences.REG_EXP_SEARCH_EXPRESSION_KEY));

        if (_prefs.getBoolean(JabRefPreferences.USE_REG_EXP_SEARCH_KEY))
            useRegExpComboBox.setSelected(true);
        else if (_prefs.getBoolean("autolinkExactKeyOnly"))
            matchExactKeyOnly.setSelected(true);
        else
            matchStartsWithKey.setSelected(true);
    }


	

	public void storeSettings() {

		_prefs.putBoolean(JabRefPreferences.USE_REG_EXP_SEARCH_KEY, useRegExpComboBox.isSelected());
		if (useRegExpComboBox.isSelected()) {
			_prefs.put(JabRefPreferences.REG_EXP_SEARCH_EXPRESSION_KEY, regExpTextField.getText());
		}

		
		_prefs.put("pdfDirectory", pdfDir.getText());
		_prefs.put("psDirectory", psDir.getText());
        _prefs.put(GUIGlobals.FILE_FIELD+"Directory", fileDir.getText());
        _prefs.put("pdfviewer", pdf.getText());
		_prefs.put("psviewer", ps.getText());
		_prefs.put("htmlviewer", html.getText());
<<<<<<< C:\Dokumente und Einstellungen\Sven Apel\Eigene Dateien\Uni\fstmerge\fstmerge_tmp\fstmerge_var1_40603
		_prefs.putBoolean("autolinkExactKeyOnly", matchExactKeyOnly.isSelected());
        _prefs.putBoolean("runAutomaticFileSearch", runAutoFileSearch.isSelected());
=======
		_prefs.put("lyxpipe", lyx.getText());
		_prefs.put("winEdtPath", winEdt.getText());
        _prefs.put("vim", vim.getText());
        _prefs.put("vimServer", vimServer.getText());
        _prefs.put("latexEditorPath", led.getText());
        _prefs.put("citeCommand", citeCommand.getText());
        _prefs.putBoolean("autolinkExactKeyOnly", matchExactKeyOnly.isSelected());
        _prefs.putBoolean("runAutomaticFileSearch", runAutoFileSearch.isSelected());
>>>>>>> C:\Dokumente und Einstellungen\Sven Apel\Eigene Dateien\Uni\fstmerge\fstmerge_tmp\fstmerge_var2_40605
    }


	

	public boolean readyToClose() {
		return true;
	}


	

	public String getTabName() {
		return Globals.lang("External programs");
	}


	

	JTextField pdfDir, regExpTextField, fileDir, psDir, pdf, ps, html;

	

    private void addSettingsButton(final PushToApplication pt, DefaultFormBuilder b) {
        b.append(new JPanel());
        b.append(Globals.lang("Settings for %0", pt.getName())+":");
        JButton button = new JButton(pt.getIcon());
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                PushToApplicationButton.showSettingsDialog(_frame, pt, pt.getSettingsPanel());
            }
        });

        b.append(button);
        b.nextLine();
    }


}
