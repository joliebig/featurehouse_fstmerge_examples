using System; namespace  NewsComponents.Net {
	
 public class  TaskEventArgs  : EventArgs {
		
  private  DownloadTask currentDownloadTask;
 
  public  TaskEventArgs( DownloadTask task ) : base() {
   currentDownloadTask = task;
  }
 
  public  DownloadTask Task {
   get { return currentDownloadTask; }
  }

	}
	
 public class  BaseDownloadProgressEventArgs  : EventArgs {
		
  private  long bytesTotal;
 
  private  long bytesTransferred;
 
  private  int filesTotal;
 
  private  int filesTransferred;
 
  private  bool cancel = false;
 
  public  BaseDownloadProgressEventArgs( long bytesTotal, long bytesTransferred, int filesTotal, int filesTransferred ) {
   this.bytesTotal = bytesTotal;
   this.bytesTransferred = bytesTransferred;
   this.filesTotal = filesTotal;
   this.filesTransferred = filesTransferred;
  }
 
  public  long BytesTotal {
   get { return bytesTotal; }
  }
 
  public  long BytesTransferred {
   get { return bytesTransferred; }
  }
 
  public  int FilesTotal {
   get { return filesTotal; }
  }
 
  public  int FilesTransferred {
   get { return filesTransferred; }
  }
 
  public  bool Cancel {
   get { return cancel; }
   set { cancel = cancel || value; }
  }

	}
	
 public class  DownloadTaskProgressEventArgs  : BaseDownloadProgressEventArgs {
		
  private  DownloadTask task;
 
  public  DownloadTaskProgressEventArgs( long bytesTotal, long bytesTransferred, int filesTotal, int filesTransferred,
   DownloadTask task ) : base( bytesTotal, bytesTransferred, filesTotal, filesTransferred ) {
   this.task = task;
  }
 
  public  DownloadTask Task {
   get { return task; }
  }

	}
	
 public class  DownloadTaskErrorEventArgs : TaskEventArgs {
		
  private  Exception exception;
 
  public  DownloadTaskErrorEventArgs( DownloadTask task, Exception exception ) : base( task ) {
   this.exception = exception;
  }
 
  public  Exception Exception {
   get { return exception; }
  }

	}
	
 public delegate  void  DownloadTaskProgressEventHandler ( object sender, DownloadTaskProgressEventArgs e);
	
 public delegate  void  DownloadTaskStartedEventHandler ( object sender, TaskEventArgs e);
	
 public delegate  void  DownloadTaskCompletedEventHandler ( object sender, TaskEventArgs e);
	
 public delegate  void  DownloadTaskErrorEventHandler ( object sender, DownloadTaskErrorEventArgs e);
	
 public class  DownloadItemEventArgs  : EventArgs {
		
  private  DownloadItem manifestInEventsArgs;
 
  public  DownloadItemEventArgs( DownloadItem manifest ) {
   manifestInEventsArgs = manifest;
  }
 
  public  DownloadItem DownloadItem {
   get { return manifestInEventsArgs; }
  }

	}
	
 public class  DownloadItemErrorEventArgs  : DownloadItemEventArgs {
		
  private  Exception exceptionContainedInManifestErrorEventArgs;
 
  public  DownloadItemErrorEventArgs( DownloadItem manifest, Exception exception ) : base( manifest ) {
   exceptionContainedInManifestErrorEventArgs = exception;
  }
 
  public  Exception Exception {
   get { return exceptionContainedInManifestErrorEventArgs; }
  }

	}
	
 public class  DownloadProgressEventArgs  : BaseDownloadProgressEventArgs {
		
  private  DownloadItem downloadItem;
 
  public  DownloadProgressEventArgs( long bytesTotal, long bytesTransferred, int filesTotal, int filesTransferred,
   DownloadItem item ) : base( bytesTotal, bytesTransferred, filesTotal, filesTransferred ) {
   downloadItem = item;
  }
 
  public  DownloadItem DownloadItem {
   get { return downloadItem; }
  }

	}
	
 public class  DownloadStartedEventArgs  : DownloadItemEventArgs {
		
  private  bool cancel;
 
  public  DownloadStartedEventArgs( DownloadItem item ) : base( item ) {
   cancel = false;
  }
 
  public  bool Cancel {
   get { return cancel; }
   set { cancel = cancel || value; }
  }

	}
	
 public class  PendingDownloadsDetectedEventArgs  : EventArgs {
		
  private  DownloadItem[] pendingDownloads;
 
  internal  PendingDownloadsDetectedEventArgs( DownloadItem[] items ) {
   pendingDownloads = items;
  }
 
  public  DownloadItem[] DownloadItems {
   get { return pendingDownloads; }
  }

	}
	
 public delegate  void  PendingDownloadsDetectedEventHandler ( object sender, PendingDownloadsDetectedEventArgs e);
	
 public delegate  void  DownloadProgressEventHandler ( object sender, DownloadProgressEventArgs e);
	
 public delegate  void  DownloadStartedEventHandler ( object sender, DownloadStartedEventArgs e);
	
 public delegate  void  DownloadCompletedEventHandler ( object sender, DownloadItemEventArgs e);
	
 public delegate  void  DownloadErrorEventHandler ( object sender, DownloadItemErrorEventArgs e);

}
