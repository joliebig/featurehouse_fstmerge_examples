
package net.sourceforge.squirrel_sql.client.gui.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.StringTokenizer;

import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class FileEditorDropTargetListener extends DropTargetAdapter 
                                          implements DropTargetListener {

    
    private static final ILogger s_log = 
        LoggerController.createLogger(FileEditorDropTargetListener.class);    
    
    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(FileEditorDropTargetListener.class); 
    
    private static interface i18n {
        
        
        String ONE_FILE_DROP_MESSAGE = 
            s_stringMgr.getString("FileEditorDropTargetListener.oneFileDropMessage");
    }
    
    
    private ISession _session;
    
    public FileEditorDropTargetListener(ISession session) {
        this._session = session;
    }
    
    
    @SuppressWarnings("unchecked")
    public void drop(DropTargetDropEvent dtde) {
        try {
            DropTargetContext context = dtde.getDropTargetContext();
            dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
            Transferable t = dtde.getTransferable();
            File fileToOpen = null;
            
            if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                fileToOpen = handleJavaFileList(t);
            } else if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                fileToOpen = handleUriListString(t);
            } else {
                s_log.error("drop: flavors fileList and UriListString "
                        + "are not supported");
            }
            if (fileToOpen != null) {
                if (s_log.isInfoEnabled()) {
                    s_log.info("drop: path="+fileToOpen.getAbsolutePath());
                }            
                ISQLPanelAPI api = 
                    _session.getSQLPanelAPIOfActiveSessionWindow(); 
                api.fileOpen(fileToOpen);
            }            
            context.dropComplete(true);
        } catch (Exception e) {
            s_log.error("drop: Unexpected exception "+e.getMessage(),e);
        }

    }

        
    private File handleUriListString(Transferable t) 
        throws UnsupportedFlavorException, IOException
    {
        File result = null;
        
        @SuppressWarnings("unchecked")
        String transferData = 
            (String)t.getTransferData(DataFlavor.stringFlavor);
        
        if (transferData != null) {
            
            if (transferData.startsWith("file://")) {
                try {
                    
                    
                    
                    
                    StringTokenizer st = new StringTokenizer(transferData);
                    if (st.countTokens() > 1) {
                        _session.showErrorMessage(i18n.ONE_FILE_DROP_MESSAGE);
                    } else {
                        if (st.hasMoreTokens()) {
                            String fileUrlStr = st.nextToken();
                            URI uri = new URI(fileUrlStr);
                            result = new File(uri);
                        }
                    }
                } catch (URISyntaxException e) {
                    s_log.error("handleUriListString: encountered an "
                            + "invalid URI: " + transferData, e);
                }
            } else {
                
                result = new File(transferData);
            }
        }
        return result;        
    }
    
    
    
    private File handleJavaFileList(Transferable t) 
        throws UnsupportedFlavorException, IOException 
    {
        File result = null;
        
        @SuppressWarnings("unchecked")
        List<File> transferData = 
            (List<File>)t.getTransferData(DataFlavor.javaFileListFlavor);
        
        if (transferData.size() > 1) {
            _session.showErrorMessage(i18n.ONE_FILE_DROP_MESSAGE);
        } else {
            result = transferData.get(0);
            if (s_log.isInfoEnabled()) {
                s_log.info("drop: path="+result.getAbsolutePath());
            }
            
        }
        return result;
    }
}
