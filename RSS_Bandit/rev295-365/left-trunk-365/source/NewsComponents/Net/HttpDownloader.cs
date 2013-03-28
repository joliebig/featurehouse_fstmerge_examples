using System;
using System.IO;
using System.Net;
using log4net;
using NewsComponents.Utils;
using RssBandit.Common.Logging;
namespace NewsComponents.Net
{
    public sealed class HttpDownloader : IDownloader, IDisposable
    {
        private static readonly ILog Logger = Log.GetLogger(typeof (HttpDownloader));
        private DownloadTask currentTask = null;
        private RequestState state = null;
        public event DownloadTaskProgressEventHandler DownloadProgress;
        public event DownloadTaskCompletedEventHandler DownloadCompleted;
        public event DownloadTaskErrorEventHandler DownloadError;
        public event DownloadTaskStartedEventHandler DownloadStarted;
        private void OnDownloadStarted(TaskEventArgs e)
        {
            if (DownloadStarted != null)
            {
                DownloadStarted(this, e);
            }
        }
        private void OnDownloadProgress(DownloadTaskProgressEventArgs e)
        {
            if (DownloadProgress != null)
            {
                DownloadProgress(this, e);
            }
        }
        private void OnDownloadCompleted(TaskEventArgs e)
        {
            if (DownloadCompleted != null)
            {
                DownloadCompleted(this, e);
            }
        }
        private void OnDownloadError(DownloadTaskErrorEventArgs e)
        {
            if (DownloadError != null)
            {
                DownloadError(this, e);
            }
        }
        public void Download(DownloadTask task, TimeSpan maxWaitTime)
        {
            currentTask = task;
            WebResponse response = AsyncWebRequest.GetSyncResponse(task.DownloadItem.Enclosure.Url,
                                                                   task.DownloadItem.Credentials,
                                                                   FeedSource.UserAgentString(String.Empty),
                                                                   task.DownloadItem.Proxy,
                                                                   DateTime.MinValue,
                                                                   null,
                                                                   Convert.ToInt32(maxWaitTime.TotalSeconds));
            OnRequestComplete(new Uri(task.DownloadItem.Enclosure.Url), response.GetResponseStream(), null, null,
                              DateTime.MinValue, RequestResult.OK, 0);
        }
        public void BeginDownload(DownloadTask task)
        {
            currentTask = task;
            Uri reqUri = new Uri(task.DownloadItem.Enclosure.Url);
            int priority = 10;
            RequestParameter reqParam = RequestParameter.Create(reqUri, FeedSource.UserAgentString(String.Empty),
                                                                task.DownloadItem.Proxy, task.DownloadItem.Credentials,
                                                                DateTime.MinValue, null);
            reqParam.SetCookies = FeedSource.SetCookies;
            state = BackgroundDownloadManager.AsyncWebRequest.QueueRequest(reqParam,
                                                                           null
                                                                           ,
                                                                           OnRequestStart,
                                                                           OnRequestComplete,
                                                                           OnRequestException,
                                                                           new RequestProgressCallback(OnRequestProgress),
                                                                           priority);
        }
        public bool CancelDownload(DownloadTask task)
        {
            currentTask = task;
            Uri requestUri = new Uri(task.DownloadItem.Enclosure.Url);
            if (state.InitialRequestUri.Equals(requestUri))
            {
                BackgroundDownloadManager.AsyncWebRequest.FinalizeWebRequest(state);
            }
            return true;
        }
        public void Dispose()
        {
            Dispose(true);
            GC.SuppressFinalize(this);
        }
        private void Dispose(bool isDisposing)
        {
            if (isDisposing)
            {
                if (currentTask.State == DownloadTaskState.Downloading)
                {
                    try
                    {
                        CancelDownload(currentTask);
                    }
                    catch (Exception e)
                    {
                        Logger.Error(e.Message, e);
                    }
                }
            }
        }
        ~HttpDownloader()
        {
            Dispose(false);
        }
        public void OnRequestStart(Uri requestUri, ref bool cancel)
        {
            OnDownloadStarted(new TaskEventArgs(currentTask));
        }
        public void OnRequestException(Uri requestUri, Exception e, int priority)
        {
            OnDownloadError(new DownloadTaskErrorEventArgs(currentTask, e));
        }
        public void OnRequestComplete(Uri requestUri, Stream response, Uri newUri, string eTag, DateTime lastModified,
                                      RequestResult result, int priority)
        {
            string fileLocation = Path.Combine(currentTask.DownloadFilesBase, currentTask.DownloadItem.File.LocalName);
            FileHelper.WriteStreamWithRename(fileLocation, response);
            response.Close();
            OnDownloadCompleted(new TaskEventArgs(currentTask));
        }
        public void OnRequestProgress(Uri requestUri, long bytesTransferred)
        {
            long size;
            if (currentTask.DownloadItem.File.FileSize > currentTask.DownloadItem.Enclosure.Length)
            {
                size = currentTask.DownloadItem.File.FileSize;
            }
            else
            {
                size = currentTask.DownloadItem.Enclosure.Length;
            }
            OnDownloadProgress(new DownloadTaskProgressEventArgs(size, bytesTransferred, 1, 0, currentTask));
        }
    }
}
