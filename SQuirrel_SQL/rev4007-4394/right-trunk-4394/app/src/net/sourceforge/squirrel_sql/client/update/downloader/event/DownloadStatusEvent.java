
package net.sourceforge.squirrel_sql.client.update.downloader.event;


public class DownloadStatusEvent {
   
   private DownloadEventType _type = null;
   private String _filename = null;
   private int _fileCountTotal = 0; 
   private Exception _exception = null;
   
   public DownloadStatusEvent(DownloadEventType type) {
      this._type = type;
   }
   
   public DownloadEventType getType() {
      return this._type;
   }

   
   public String getFilename() {
      return _filename;
   }

   
   public void setFilename(String _filename) {
      this._filename = _filename;
   }

   
   public Exception getException() {
      return _exception;
   }

   
   public void setException(Exception _exception) {
      this._exception = _exception;
   }

   
   public int getFileCountTotal() {
      return _fileCountTotal;
   }

   
   public void setFileCountTotal(int countTotal) {
      _fileCountTotal = countTotal;
   }
   
   
}
