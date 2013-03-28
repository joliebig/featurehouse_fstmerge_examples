using System;
using System.Collections.Specialized;
using System.Globalization;
using System.Net;
using System.IO;
using System.Runtime.InteropServices;
using System.Runtime.Serialization;
using System.Security.Permissions;
using System.Threading;
using NewsComponents.Utils;
namespace NewsComponents.Net
{
 public sealed class BITSDownloader :
  IDownloader, IBackgroundCopyCallback, IDisposable {
  private static readonly log4net.ILog Logger = RssBandit.Common.Logging.Log.GetLogger(typeof(BITSDownloader));
  private const int TimeToWaitDuringSynchronousDownload = 200;
  private const int BitsNoProgressTimeout = 5;
  private const int BitsMinimumRetryDelay = 0;
  private const int ExceptionCodeNotAnError = -2145386481;
  private readonly int CultureIdForGettingComErrorMessages = CultureInfo.CurrentUICulture.LCID;
  private HybridDictionary bitsDownloaderJobs = new HybridDictionary();
  private string cumulativeErrorMessage = String.Empty;
  private const string TASK_JOBID_KEY = "jobId";
  public BITSDownloader() {
  }
  public event DownloadTaskProgressEventHandler DownloadProgress;
  public event DownloadTaskCompletedEventHandler DownloadCompleted;
  public event DownloadTaskErrorEventHandler DownloadError;
  public event DownloadTaskStartedEventHandler DownloadStarted;
  private void OnDownloadStarted( TaskEventArgs e ) {
   if ( DownloadStarted != null ) {
    DownloadStarted( this, e );
   }
  }
  private void OnDownloadProgress( DownloadTaskProgressEventArgs e ) {
   if ( DownloadProgress != null ) {
    DownloadProgress( this, e );
   }
  }
  private void OnDownloadCompleted( TaskEventArgs e ) {
   if ( DownloadCompleted != null ) {
    DownloadCompleted( this, e );
   }
  }
  private void OnDownloadError( DownloadTaskErrorEventArgs e ) {
   if ( DownloadError != null ) {
    DownloadError( this, e );
   }
  }
  [FileIOPermission( SecurityAction.Demand )]
  public void Download(DownloadTask task, TimeSpan maxWaitTime) {
   IBackgroundCopyManager backGroundCopyManager = null;
   IBackgroundCopyJob backgroundCopyJob = null;
   Guid jobID = Guid.Empty;
   try {
    backGroundCopyManager = (IBackgroundCopyManager) new BackgroundCopyManager();
    if ( CheckForResumeAndProceed( backGroundCopyManager, task, out backgroundCopyJob ) ) {
     return;
    }
    if ( backgroundCopyJob == null ) {
     CreateCopyJob(
      backGroundCopyManager,
      out backgroundCopyJob,
      ref jobID,
      task.DownloadItem.OwnerItemId,
      task.DownloadItem.Enclosure.Description, task );
     task[TASK_JOBID_KEY] = jobID;
     PrepareJob( backgroundCopyJob, task );
    }
    WaitForDownload( task, backgroundCopyJob, maxWaitTime );
   }
   catch( Exception e ) {
    OnJobError( task, backgroundCopyJob, null, e );
   }
   finally {
    if( null != backgroundCopyJob ) {
     Marshal.ReleaseComObject( backgroundCopyJob );
    }
    if( null != backGroundCopyManager ) {
     Marshal.ReleaseComObject( backGroundCopyManager );
    }
   }
  }
  [FileIOPermission( SecurityAction.Demand )]
  public void BeginDownload(DownloadTask task) {
   IBackgroundCopyManager backGroundCopyManager = null;
   IBackgroundCopyJob backgroundCopyJob = null;
   Guid jobID = Guid.Empty;
   try {
    backGroundCopyManager = (IBackgroundCopyManager) new BackgroundCopyManager();
    if ( CheckForResumeAndProceed( backGroundCopyManager, task, out backgroundCopyJob ) ) {
     return;
    }
    if ( backgroundCopyJob != null ) {
     backgroundCopyJob.SetNotifyInterface( this );
     backgroundCopyJob.SetNotifyFlags( (uint)(
      BG_JOB_NOTIFICATION_TYPE.BG_NOTIFY_JOB_ERROR |
      BG_JOB_NOTIFICATION_TYPE.BG_NOTIFY_JOB_MODIFICATION |
      BG_JOB_NOTIFICATION_TYPE.BG_NOTIFY_JOB_TRANSFERRED )
      );
    }
    else {
     CreateCopyJob(
      backGroundCopyManager,
      out backgroundCopyJob,
      ref jobID,
      task.DownloadItem.OwnerItemId,
      task.DownloadItem.Enclosure.Description, task );
     task[TASK_JOBID_KEY] = jobID;
     PrepareJob( backgroundCopyJob, task );
     backgroundCopyJob.SetNotifyInterface( this );
     backgroundCopyJob.SetNotifyFlags( (uint)(
      BG_JOB_NOTIFICATION_TYPE.BG_NOTIFY_JOB_ERROR |
      BG_JOB_NOTIFICATION_TYPE.BG_NOTIFY_JOB_MODIFICATION |
      BG_JOB_NOTIFICATION_TYPE.BG_NOTIFY_JOB_TRANSFERRED )
      );
     OnDownloadStarted( new TaskEventArgs( task ) );
     backgroundCopyJob.Resume();
    }
   }
   catch( Exception e ) {
    OnJobError( task, backgroundCopyJob, null, e );
   }
   finally {
    if( null != backgroundCopyJob ) {
     Marshal.ReleaseComObject( backgroundCopyJob );
    }
    if( null != backGroundCopyManager ) {
     Marshal.ReleaseComObject( backGroundCopyManager );
    }
   }
  }
  public bool CancelDownload(DownloadTask task) {
   IBackgroundCopyManager copyManager = null;
   IBackgroundCopyJob pJob = null;
   if ( task[TASK_JOBID_KEY] != null ) {
    try {
     Guid jobID = (Guid)task[ TASK_JOBID_KEY ];
     copyManager = (IBackgroundCopyManager)new BackgroundCopyManager();
     copyManager.GetJob( ref jobID, out pJob );
     if ( pJob != null ) {
      pJob.Cancel();
     }
    }catch(COMException){
    }finally {
     if ( copyManager != null ) {
      Marshal.ReleaseComObject( copyManager );
     }
     if ( pJob != null ) {
      Marshal.ReleaseComObject( pJob );
     }
    }
   }
   return true;
  }
  private bool CheckForResumeAndProceed( IBackgroundCopyManager copyManager, DownloadTask task, out IBackgroundCopyJob copyJob ) {
   copyJob = null;
   if ( task[TASK_JOBID_KEY] != null ) {
    Guid jobId = (Guid)task[TASK_JOBID_KEY];
    BG_JOB_STATE jobState;
    try {
     copyManager.GetJob( ref jobId, out copyJob );
     if ( copyJob != null ) {
      copyJob.GetState( out jobState );
      if ( jobState == BG_JOB_STATE.BG_JOB_STATE_TRANSFERRED ) {
       OnJobTransferred( task, copyJob );
       return true;
      }
     }
    }
    catch(Exception ex) {
     Logger.Error( new DownloaderException( String.Format("The BITSDownloader cannot connect to the job '{0}' for the task '{1}' so a new BITS job will be created.", jobId, task.TaskId ), ex ));
    }
   }
   return false;
  }
  private DownloadTask FindTask(IBackgroundCopyJob pJob) {
   Guid jobID = Guid.Empty;
   pJob.GetId( out jobID );
   foreach( DownloadTask task in BackgroundDownloadManager.GetTasks() ) {
    if ( Guid.Equals( task[ TASK_JOBID_KEY ], jobID ) ) {
     return task;
    }
   }
   return null;
  }
  private void WaitForDownload( DownloadTask task, IBackgroundCopyJob backgroundCopyJob, TimeSpan maxWaitTime ) {
   Guid jobID = Guid.Empty;
   bool isCompleted = false;
   bool isSuccessful = false;
   BG_JOB_STATE state;
   try {
    backgroundCopyJob.GetId( out jobID );
    double endTime = Environment.TickCount + maxWaitTime.TotalMilliseconds;
    while ( !isCompleted ) {
     backgroundCopyJob.GetState( out state );
     switch( state ) {
      case BG_JOB_STATE.BG_JOB_STATE_SUSPENDED: {
       OnDownloadStarted( new TaskEventArgs( task ) );
       backgroundCopyJob.Resume();
       break;
      }
      case BG_JOB_STATE.BG_JOB_STATE_ERROR: {
       OnJobError( task, backgroundCopyJob, null, null );
       isCompleted = true;
       break;
      }
      case BG_JOB_STATE.BG_JOB_STATE_TRANSIENT_ERROR: {
       OnJobError( task, backgroundCopyJob, null, null );
       isCompleted = true;
       break;
      }
      case BG_JOB_STATE.BG_JOB_STATE_TRANSFERRING: {
       OnJobModification( task, backgroundCopyJob );
       break;
      }
      case BG_JOB_STATE.BG_JOB_STATE_TRANSFERRED: {
       OnJobTransferred( task, backgroundCopyJob );
       isCompleted = true;
       isSuccessful = true;
       break;
      }
      default:
       break;
     }
     if ( isCompleted ) {
      break;
     }
     if( endTime < Environment.TickCount ) {
      DownloaderException ex = new DownloaderException("Download attempt timed out");
      OnJobError( task, backgroundCopyJob, null, ex );
      break;
     }
     Thread.Sleep( TimeToWaitDuringSynchronousDownload );
    }
    if( !isSuccessful ) {
     DownloaderException ex = new DownloaderException(String.Format("Download attempt for {0} failed", task.DownloadItem.ItemId));
     OnJobError( task, backgroundCopyJob, null, ex );
    }
   }
   catch( ThreadInterruptedException tie ) {
    OnJobError( task, backgroundCopyJob, null, tie );
   }
  }
  private void PrepareJob( IBackgroundCopyJob backgroundCopyJob, DownloadTask task) {
   Guid jobID = Guid.Empty;
   backgroundCopyJob.GetId( out jobID );
   task[ TASK_JOBID_KEY ] = jobID;
   DownloadFile sourceFile = task.DownloadItem.File;
   string src = sourceFile.Source;
   if( FileHelper.IsUncPath( src ) ) {
    Exception ex = new DownloaderException("Download location must be HTTP or HTTPS URL" );
    Logger.Error( ex );
    throw ex;
   }
   string dest = Path.Combine(task.DownloadFilesBase, sourceFile.LocalName);
   if ( !Directory.Exists( Path.GetDirectoryName( dest ) ) ) {
    Directory.CreateDirectory( Path.GetDirectoryName( dest ) );
   }
   backgroundCopyJob.AddFile( src, dest );
  }
  private void CreateCopyJob(
   IBackgroundCopyManager copyManager,
   out IBackgroundCopyJob copyJob,
   ref Guid jobID,
   string jobName,
   string jobDesc,
   DownloadTask task) {
   copyManager.CreateJob(
    jobName,
    BG_JOB_TYPE.BG_JOB_TYPE_DOWNLOAD,
    out jobID,
    out copyJob );
   copyJob.SetDescription( jobDesc );
   copyJob.SetMinimumRetryDelay( (uint)BitsMinimumRetryDelay );
   copyJob.SetNoProgressTimeout( (uint)BitsNoProgressTimeout );
   copyJob.SetPriority( BG_JOB_PRIORITY.BG_JOB_PRIORITY_HIGH );
   VerifyAndSetBackgroundCopyJobCredentials(copyJob, task);
   VerifyAndSetBackgroundCopyJobProxy(copyJob, task);
   lock( bitsDownloaderJobs.SyncRoot ) {
    bitsDownloaderJobs.Add( jobID, jobName );
   }
  }
  private void VerifyAndSetBackgroundCopyJobProxy(IBackgroundCopyJob backgroundCopyJob, DownloadTask task) {
   try{
    IWebProxy proxy = task.DownloadItem.Proxy;
    Uri sourceUri = new Uri(task.DownloadItem.File.Source);
    Uri proxyUri = proxy.GetProxy(sourceUri);
    if (!proxy.IsBypassed(proxyUri)) {
     string proxyUriStr = proxyUri.ToString().TrimEnd('/');
     backgroundCopyJob.SetProxySettings(BG_JOB_PROXY_USAGE.BG_JOB_PROXY_USAGE_OVERRIDE, proxyUriStr, null);
    }
    if(proxy.Credentials != null){
     ICredentials creds = proxy.Credentials;
     IBackgroundCopyJob2 copyJob = (IBackgroundCopyJob2) backgroundCopyJob;
     BG_AUTH_CREDENTIALS credentials = new BG_AUTH_CREDENTIALS();
     credentials.Credentials.Basic.UserName =
      string.IsNullOrEmpty(creds.GetCredential(sourceUri, "NTLM").Domain) ?
      creds.GetCredential(sourceUri, "NTLM").UserName :
      creds.GetCredential(sourceUri, "NTLM").Domain + "\\" + creds.GetCredential(sourceUri, "NTLM").UserName ;
     credentials.Credentials.Basic.Password = creds.GetCredential(sourceUri, "NTLM").Password;
     credentials.Scheme = BG_AUTH_SCHEME.BG_AUTH_SCHEME_NTLM;
     credentials.Target = BG_AUTH_TARGET.BG_AUTH_TARGET_PROXY;
     copyJob.SetCredentials(ref credentials);
    }
   }catch(Exception e){
    Logger.Error("Error in VerifyAndSetBackgroundCopyJobProxy():", e);
   }
  }
  private void VerifyAndSetBackgroundCopyJobCredentials(IBackgroundCopyJob backgroundCopyJob, DownloadTask task) {
   try{
   IBackgroundCopyJob2 copyJob = (IBackgroundCopyJob2) backgroundCopyJob;
   ICredentials creds = task.DownloadItem.Credentials;
   Uri uri = new Uri(task.DownloadItem.File.Source);
   if(creds != null){
    BG_AUTH_CREDENTIALS credentials = new BG_AUTH_CREDENTIALS();
    credentials.Credentials.Basic.UserName = creds.GetCredential(uri, "Basic").UserName;
    credentials.Credentials.Basic.Password = creds.GetCredential(uri, "Basic").Password;
    credentials.Scheme = BG_AUTH_SCHEME.BG_AUTH_SCHEME_BASIC;
    credentials.Target = BG_AUTH_TARGET.BG_AUTH_TARGET_SERVER;
    copyJob.SetCredentials(ref credentials);
    credentials = new BG_AUTH_CREDENTIALS();
    credentials.Credentials.Basic.UserName = creds.GetCredential(uri, "Digest").UserName;
    credentials.Credentials.Basic.Password = creds.GetCredential(uri, "Digest").Password;
    credentials.Scheme = BG_AUTH_SCHEME.BG_AUTH_SCHEME_DIGEST;
    credentials.Target = BG_AUTH_TARGET.BG_AUTH_TARGET_SERVER;copyJob.SetCredentials(ref credentials);
    credentials = new BG_AUTH_CREDENTIALS();
    credentials.Credentials.Basic.UserName =
     string.IsNullOrEmpty(creds.GetCredential(uri, "NTLM").Domain) ?
     creds.GetCredential(uri, "NTLM").UserName :
     creds.GetCredential(uri, "NTLM").Domain + "\\" + creds.GetCredential(uri, "NTLM").UserName ;
    credentials.Credentials.Basic.Password = creds.GetCredential(uri, "NTLM").Password;
    credentials.Scheme = BG_AUTH_SCHEME.BG_AUTH_SCHEME_NTLM;
    credentials.Target = BG_AUTH_TARGET.BG_AUTH_TARGET_SERVER;
    copyJob.SetCredentials(ref credentials);
   }
   }catch(Exception e){
    Logger.Error("Error in VerifyAndSetBackgroundCopyJobCredentials():", e);
   }
  }
  private void RemoveCopyJobEntry( Guid jobID ) {
   lock( bitsDownloaderJobs.SyncRoot ) {
    bitsDownloaderJobs.Remove( jobID );
   }
  }
  private void OnJobModification( DownloadTask task, IBackgroundCopyJob pJob ) {
   _BG_JOB_PROGRESS progress;
   pJob.GetProgress( out progress );
   DownloadTaskProgressEventArgs args = new DownloadTaskProgressEventArgs( (long)progress.BytesTotal,
    (long)progress.BytesTransferred, (int)progress.FilesTotal, (int)progress.FilesTransferred, task );
   OnDownloadProgress( args );
  }
  private void OnJobError( DownloadTask task, IBackgroundCopyJob pJob, IBackgroundCopyError pError, Exception ex ) {
   string jobDesc = "";
   string jobName = "";
   Guid jobID = Guid.Empty;
   Exception finalException = ex;
   if ( pJob != null ) {
    pJob.GetDescription( out jobDesc );
    pJob.GetDisplayName( out jobName );
    pJob.GetId( out jobID );
    try {
     if ( pError == null ) {
      pJob.GetError( out pError );
     }
    }
    catch( COMException e ) {
     Logger.Error( e );
     if( e.ErrorCode != ExceptionCodeNotAnError ) {
      throw e;
     }
    }
    if ( pError != null ) {
     BitsDownloadErrorException BitsEx = new BitsDownloadErrorException( pError, (uint)CultureIdForGettingComErrorMessages );
     cumulativeErrorMessage += BitsEx.Message + Environment.NewLine;
     finalException = BitsEx;
    }
    BG_JOB_STATE state;
    pJob.GetState(out state);
    if( state != BG_JOB_STATE.BG_JOB_STATE_ACKNOWLEDGED
     && state != BG_JOB_STATE.BG_JOB_STATE_CANCELLED ) {
     pJob.Cancel();
    }
    RemoveCopyJobEntry( jobID );
   }
   OnDownloadError( new DownloadTaskErrorEventArgs( task, finalException ) );
   Logger.Error( finalException );
  }
  private void OnJobTransferred( DownloadTask task, IBackgroundCopyJob pJob ) {
   pJob.Complete();
   OnDownloadCompleted( new TaskEventArgs( task ) );
  }
  public void Dispose() {
   Dispose(true);
   GC.SuppressFinalize(this);
  }
  private void Dispose(bool isDisposing) {
   uint BG_JOB_ENUM_ALL_USERS = 0x0001;
   uint numJobs;
   uint fetched;
   Guid jobID;
   IBackgroundCopyManager mgr = null;
   IEnumBackgroundCopyJobs jobs = null;
   IBackgroundCopyJob job = null;
   if (isDisposing) {
    try {
     mgr = (IBackgroundCopyManager)( new BackgroundCopyManager() );
     mgr.EnumJobs( BG_JOB_ENUM_ALL_USERS, out jobs );
     jobs.GetCount( out numJobs );
     lock( bitsDownloaderJobs.SyncRoot) {
      for( int i = 0; i < numJobs; i++ ) {
       jobs.Next( (uint)1, out job, out fetched );
       job.GetId( out jobID );
       if( bitsDownloaderJobs.Contains( jobID ) ) {
        job.TakeOwnership();
        job.Cancel();
        bitsDownloaderJobs.Remove( jobID );
       }
      }
     }
    }
    finally {
     if( null != mgr ) {
      Marshal.ReleaseComObject( mgr );
      mgr = null;
     }
     if( null != jobs ) {
      Marshal.ReleaseComObject( jobs );
      jobs = null;
     }
     if( null != job ) {
      Marshal.ReleaseComObject( job );
      job = null;
     }
    }
   }
  }
  ~BITSDownloader() {
   Dispose(false);
  }
  void IBackgroundCopyCallback.JobTransferred(IBackgroundCopyJob pJob) {
   OnJobTransferred( FindTask( pJob ), pJob );
  }
  void IBackgroundCopyCallback.JobError(IBackgroundCopyJob pJob, IBackgroundCopyError pError) {
   OnJobError( FindTask( pJob ), pJob, pError, null );
  }
  void IBackgroundCopyCallback.JobModification(IBackgroundCopyJob pJob, uint dwReserved) {
   OnJobModification( FindTask( pJob ), pJob );
  }
 }
 [Serializable]
 public class BitsDownloadErrorException : Exception {
  private BG_ERROR_CONTEXT contextForError;
  private int errorCode;
  private string contextDescription;
  private string errorDescription;
  private string protocol;
  private string fileLocalName;
  private string fileRemoteName;
  public BitsDownloadErrorException() : base() {
  }
  internal BitsDownloadErrorException( IBackgroundCopyError error, uint langID ) {
   IBackgroundCopyFile file;
   error.GetError(out contextForError, out errorCode );
   error.GetErrorContextDescription( langID, out contextDescription );
   error.GetErrorDescription( langID, out errorDescription );
   error.GetFile( out file );
   error.GetProtocol( out protocol );
   file.GetLocalName( out fileLocalName );
   file.GetRemoteName( out fileRemoteName );
  }
  public BitsDownloadErrorException( string message ) : base( message ) {
   errorDescription = message;
  }
  public BitsDownloadErrorException( string message, Exception innerException ) : base( message, innerException ) {
   errorDescription = message;
  }
  [SecurityPermissionAttribute(SecurityAction.Demand, SerializationFormatter=true)]
  protected BitsDownloadErrorException(SerializationInfo info, StreamingContext context) : base( info, context ) {
  }
  public int Code {
   get { return errorCode; }
  }
  public int Context {
   get { return (int)contextForError; }
  }
  public string ContextDescription {
   get { return contextDescription; }
  }
  public override string Message {
   get{ return errorDescription; }
  }
  public string Protocol {
   get { return protocol; }
  }
  public string LocalFileName {
   get { return fileLocalName; }
  }
  public string RemoteFileName {
   get { return fileRemoteName; }
  }
  [SecurityPermissionAttribute(SecurityAction.Demand, SerializationFormatter=true)]
  public override void GetObjectData(SerializationInfo info, StreamingContext context) {
   base.GetObjectData ( info, context );
  }
 }
}
