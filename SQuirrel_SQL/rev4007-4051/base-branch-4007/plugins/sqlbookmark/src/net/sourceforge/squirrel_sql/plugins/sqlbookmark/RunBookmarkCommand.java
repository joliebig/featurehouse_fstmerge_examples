

package net.sourceforge.squirrel_sql.plugins.sqlbookmark;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.PropertyPanel;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;


public class RunBookmarkCommand implements ICommand {

	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(RunBookmarkCommand.class);

	
	private final Frame frame;

    
    private final ISession session;

    
    private SQLBookmarkPlugin plugin;
    
    
    private Bookmark bookmark;
   private ISQLEntryPanel _sqlEntryPanel;

   
    public RunBookmarkCommand(Frame frame, ISession session,
                              Bookmark bookmark, SQLBookmarkPlugin plugin, ISQLEntryPanel sqlEntryPanel)
        throws IllegalArgumentException {
        super();
      _sqlEntryPanel = sqlEntryPanel;
      if (session == null) {
            throw new IllegalArgumentException("Null ISession passed");
        }
        if (plugin == null) {
            throw new IllegalArgumentException("Null IPlugin passed");
        }
        this.frame = frame;
        this.session = session;
        this.plugin = plugin;
	this.bookmark = bookmark;

    }

    
	 public void execute()
	 {
		 if (session != null)
		 {
			 String sql = parseAndLoadSql(bookmark.getSql());

			 if (null != sql)
			 {

				 int caretPosition = _sqlEntryPanel.getCaretPosition();
				 _sqlEntryPanel.replaceSelection(sql);
				 _sqlEntryPanel.setCaretPosition(caretPosition + sql.length());
			 }
		 }
	 }


	
    protected String parseAndLoadSql(String sql) {

        
        
        
        
        
        
        
    	ArrayList itemsInSql = new ArrayList();
    	HashMap<String,Parameter> paramsById = new HashMap<String,Parameter>();
    	ArrayList<Parameter> parameters = new ArrayList<Parameter>();

    
        HashMap<String, Parameter> lookup = new HashMap<String, Parameter>();
        
        
        
        
        int start = 0;
        int idx = 0;
        while ((idx = sql.indexOf("${", start)) >= 0) {
            int ridx = sql.indexOf("}", idx);
            if (ridx < 0) break;

            String arg = sql.substring(idx + 2, ridx);
            itemsInSql.add(sql.substring(start, idx));
            start = ridx + 1;

            StringTokenizer st = new StringTokenizer(arg, ",");
            Parameter parameter = new Parameter();
            if (arg.startsWith("ref=")) {
                String ref = st.nextToken();
                parameter.reference = ref.substring(4);
            }
            else if (arg.startsWith("id=")) {
                String id = st.nextToken();
                String prompt = st.nextToken();
                parameter.id = id.substring(3);
                parameter.prompt = prompt;
                if (st.countTokens() > 0) {
                    String tip = st.nextToken();
                    parameter.tip = tip;
                }
            }
            else {
                String prompt = st.nextToken();
                parameter.prompt = prompt;
                if (st.countTokens() > 0) {
                    String tip = st.nextToken();
                    parameter.tip = tip;
                }
            }

            if (parameter.reference == null) {

                
                
                
                
                if (lookup.containsKey(parameter.prompt)) {
                    parameter = lookup.get(parameter.prompt);
                } else {
                    lookup.put(parameter.prompt, parameter);
                    parameters.add(parameter);
                }

            }
            if (parameter.id != null) {
                paramsById.put(parameter.id, parameter);
            }
            itemsInSql.add(parameter);
        }
        itemsInSql.add(sql.substring(start));

        DoneAction doneAction = null;
        
        
        
        
        if (parameters.size() > 0) {
            
            JDialog dialog = new JDialog(frame, s_stringMgr.getString("sqlbookmark.qureyParams"), true);
            Container contentPane = dialog.getContentPane();
            contentPane.setLayout(new BorderLayout());

            PropertyPanel propPane = new PropertyPanel();
            contentPane.add(propPane, BorderLayout.CENTER);

            for (idx = 0; idx < parameters.size(); idx++) {
                Parameter parameter = parameters.get(idx);

                JLabel label = new JLabel(parameter.prompt + ":",
                        SwingConstants.RIGHT);
                if (parameter.tip != null)
                    label.setToolTipText(parameter.tip);

                JTextField value = new JTextField(20);
                propPane.add(label, value);

                parameter.value = value;
            }

            JPanel actionPane = new JPanel();
            contentPane.add(actionPane, BorderLayout.SOUTH);

            
            JButton done = new JButton(s_stringMgr.getString("sqlbookmark.btnOk"));
            actionPane.add(done);
            doneAction = new DoneAction(dialog);
            done.addActionListener(doneAction);
            dialog.getRootPane().setDefaultButton(done);
            dialog.setLocationRelativeTo(frame);
            dialog.pack();
            dialog.setVisible(true);
        }

        if(null == doneAction || doneAction.actionExecuted())
        {
            
            
            
            
            StringBuffer sqlbuf = new StringBuffer();
            for (idx = 0; idx < itemsInSql.size(); idx++) {
                Object item = itemsInSql.get(idx);
                if (item instanceof String)
                    sqlbuf.append((String) item);
                if (item instanceof Parameter) {
                    Parameter parameter = (Parameter) item;
                    if (parameter.reference != null)
                        parameter = paramsById.get(parameter.reference);

                    sqlbuf.append(parameter.value.getText());
                }
            }

            return sqlbuf.toString();

        }
        else
        {
            return null;
        }

    }

    
    static class Parameter {
    	String reference;
    	String id;
    	String prompt;
    	String tip;
    	JTextField value;
    }

    
    static class DoneAction implements ActionListener {

	JDialog dialog = null;
       private boolean _actionExecuted;

       public DoneAction(JDialog dialog) {
	    super();
	    this.dialog = dialog;
	}
	 
	public void actionPerformed(ActionEvent e) {
	   _actionExecuted = true;
      dialog.dispose();
	}

    public boolean actionExecuted(){
       return _actionExecuted;
    }
   }
	
}
