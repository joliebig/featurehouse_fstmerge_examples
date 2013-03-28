using System;
using System.Collections;
using System.Collections.Generic;
using System.ComponentModel;
using System.Diagnostics;
using System.IO;
using System.Net;
using System.Runtime.InteropServices;
using System.Security.Cryptography.X509Certificates;
using System.Text;
using System.Threading;
using ICSharpCode.SharpZipLib;
using ICSharpCode.SharpZipLib.GZip;
using ICSharpCode.SharpZipLib.Zip.Compression;
using ICSharpCode.SharpZipLib.Zip.Compression.Streams;
using log4net;
using NewsComponents.News;
using NewsComponents.Utils;
using RssBandit.Common;
using RssBandit.Common.Logging;
using System.Net.Security;
namespace NewsComponents.Net
{
    public enum HttpExtendedStatusCode
    {
        IMUsed = 226
    }
    public sealed class AsyncWebRequest
    {
        private static readonly DateTime MinValue = new DateTime(1981, 1, 1);
        private static readonly ILog _log = Log.GetLogger(typeof (AsyncWebRequest));
        public static event CertificateIssueHandler OnCertificateIssue = null;
        private static Dictionary<string, IList<CertificateIssue> > trustedCertificateIssues =
            new Dictionary<string, IList<CertificateIssue> >(5);
        public delegate void RequestAllCompleteCallback();
        public event RequestAllCompleteCallback OnAllRequestsComplete = null;
        private const int DefaultTimeout = 2*60*1000;
        private readonly Hashtable queuedRequests;
        private readonly RequestThread requestThread;
        public AsyncWebRequest()
        {
            queuedRequests = Hashtable.Synchronized(new Hashtable(17));
            requestThread = new RequestThread(this);
        }
        static AsyncWebRequest()
        {
   ServicePointManager.CertificatePolicy = new TrustSelectedCertificatePolicy();
        }
        internal RequestThread RequestThread
        {
            get
            {
                return requestThread;
            }
        }
        public int PendingRequests
        {
            get
            {
                return queuedRequests.Count;
            }
        }
        public static Dictionary<string, IList<CertificateIssue> > TrustedCertificateIssues
        {
            set
            {
                trustedCertificateIssues = value;
            }
            get
            {
                return trustedCertificateIssues;
            }
        }
        internal RequestState QueueRequest(RequestParameter requestParameter,
                                           RequestQueuedCallback webRequestQueued,
                                           RequestStartCallback webRequestStart,
                                           RequestCompleteCallback webRequestComplete,
                                           RequestExceptionCallback webRequestException,
                                           int priority)
        {
            return
                QueueRequest(requestParameter, webRequestQueued, webRequestStart, webRequestComplete,
                             webRequestException, null, priority, null);
        }
        internal RequestState QueueRequest(RequestParameter requestParameter,
                                           RequestQueuedCallback webRequestQueued,
                                           RequestStartCallback webRequestStart,
                                           RequestCompleteCallback webRequestComplete,
                                           RequestExceptionCallback webRequestException,
                                           RequestProgressCallback webRequestProgress,
                                           int priority)
        {
            return
                QueueRequest(requestParameter, webRequestQueued, webRequestStart, webRequestComplete,
                             webRequestException, webRequestProgress, priority, null);
        }
        internal RequestState QueueRequest(RequestParameter requestParameter,
                                           RequestQueuedCallback webRequestQueued,
                                           RequestStartCallback webRequestStart,
                                           RequestCompleteCallback webRequestComplete,
                                           RequestExceptionCallback webRequestException,
                                           RequestProgressCallback webRequestProgress,
                                           int priority, RequestState prevState)
        {
            if (requestParameter == null)
                throw new ArgumentNullException("requestParameter");
            if (prevState == null && queuedRequests.Contains(requestParameter.RequestUri.CanonicalizedUri()))
                return null;
            WebRequest webRequest = WebRequest.Create(requestParameter.RequestUri);
            HttpWebRequest httpRequest = webRequest as HttpWebRequest;
            FileWebRequest fileRequest = webRequest as FileWebRequest;
            NntpWebRequest nntpRequest = webRequest as NntpWebRequest;
            if (httpRequest != null)
            {
                if (webRequestProgress != null)
                {
                    httpRequest.Timeout = DefaultTimeout*30;
                }
                else
                {
                    httpRequest.Timeout = DefaultTimeout;
                }
                httpRequest.UserAgent = FullUserAgent(requestParameter.UserAgent);
                httpRequest.Proxy = requestParameter.Proxy;
                httpRequest.AllowAutoRedirect = false;
                httpRequest.Headers.Add("Accept-Encoding", "gzip, deflate");
                if (requestParameter.LastModified > MinValue)
                {
                    httpRequest.IfModifiedSince = requestParameter.LastModified;
                }
                if (httpRequest.Proxy == null)
                {
                    httpRequest.KeepAlive = false;
                    httpRequest.Proxy = WebRequest.DefaultWebProxy;
                    httpRequest.Proxy.Credentials = CredentialCache.DefaultCredentials;
                }
                if (requestParameter.ETag != null)
                {
                    httpRequest.Headers.Add("If-None-Match", requestParameter.ETag);
                    httpRequest.Headers.Add("A-IM", "feed");
                }
                if (requestParameter.Credentials != null)
                {
                    httpRequest.KeepAlive = true;
                    httpRequest.ProtocolVersion = HttpVersion.Version11;
                    httpRequest.Credentials = requestParameter.Credentials;
                }
                if (requestParameter.SetCookies)
                {
                    HttpCookieManager.SetCookies(httpRequest);
                }
                httpRequest.Pipelined = false;
            }
            else if (fileRequest != null)
            {
                fileRequest.Timeout = DefaultTimeout;
                if (requestParameter.Credentials != null)
                {
                    fileRequest.Credentials = requestParameter.Credentials;
                }
            }
            else if (nntpRequest != null)
            {
                nntpRequest.Timeout = DefaultTimeout*5;
                if (requestParameter.Credentials != null)
                {
                    nntpRequest.Credentials = requestParameter.Credentials;
                }
                if (requestParameter.LastModified > MinValue)
                {
                    nntpRequest.IfModifiedSince = requestParameter.LastModified;
                }
            }
            else
            {
                Debug.Assert(false, "QueueRequest(): unsupported WebRequest type: " + webRequest.GetType());
            }
            RequestState state;
            if (prevState != null)
            {
                state = prevState;
                IDisposable dispResponse = state.Response;
                if (dispResponse != null)
                {
                    dispResponse.Dispose();
                    state.Response = null;
                }
                if (state.ResponseStream != null)
                {
                    state.ResponseStream.Close();
                }
                if (state.Request != null)
                {
                    if (state.Request.Credentials != null)
                    {
                        state.Request.Credentials = null;
                    }
                    if (state.Request is HttpWebRequest)
                        state.Request.Abort();
                }
            }
            else
            {
                state = new RequestState(this);
                state.WebRequestQueued += webRequestQueued;
                state.WebRequestStarted += webRequestStart;
                state.WebRequestCompleted += webRequestComplete;
                state.WebRequestException += webRequestException;
                state.WebRequestProgress += webRequestProgress;
                state.Priority = priority;
                state.InitialRequestUri = webRequest.RequestUri;
            }
            state.Request = webRequest;
            state.RequestParams = requestParameter;
            if (prevState == null)
            {
                queuedRequests.Add(requestParameter.RequestUri.CanonicalizedUri(), null);
                state.OnRequestQueued(requestParameter.RequestUri);
            }
            RequestThread.QueueRequest(state, priority);
            return state;
        }
        public static string FullUserAgent(string userAgent)
        {
            return NewsHandler.UserAgentString(userAgent);
        }
        public void TimeoutCallback(object input, bool timedOut)
        {
            if (timedOut)
            {
                RequestState state = (RequestState) input;
                _log.Info("Request Timeout: " + state.RequestUri);
                state.OnRequestException(new WebException("Request timeout", WebExceptionStatus.Timeout));
                FinalizeWebRequest(state);
            }
        }
        internal void RequestStartCancelled(RequestState state)
        {
            if (state != null && !state.requestFinalized)
            {
                _log.Info("RequestStart cancelled: " + state.RequestUri);
                state.OnRequestCompleted(state.RequestParams.ETag, state.RequestParams.LastModified,
                                         RequestResult.NotModified);
                queuedRequests.Remove(state.InitialRequestUri.CanonicalizedUri());
                state.requestFinalized = true;
                if (queuedRequests.Count == 0 && RequestThread.RunningRequests <= 0)
                    RaiseOnAllRequestsComplete();
            }
        }
        internal void FinalizeWebRequest(RequestState state)
        {
            if (state != null && !state.requestFinalized)
            {
                _log.Debug("Request finalized. Request of '" + state.InitialRequestUri.CanonicalizedUri() + "' took " +
                           DateTime.Now.Subtract(state.StartTime) + " seconds");
                try
                {
                    if (state.ResponseStream != null)
                    {
                        state.ResponseStream.Close();
                    }
                    IDisposable dispResponse = state.Response;
                    if (dispResponse != null)
                    {
                        dispResponse.Dispose();
                        state.Response = null;
                    }
                    if (state.Request != null)
                    {
                        if (state.Request.Credentials != null)
                        {
                            state.Request.Credentials = null;
                        }
                        if (state.Request is HttpWebRequest)
                            state.Request.Abort();
                    }
                }
                catch
                {
                }
                queuedRequests.Remove(state.InitialRequestUri.CanonicalizedUri());
                RequestThread.EndRequest(state);
                state.requestFinalized = true;
                if (queuedRequests.Count == 0 && RequestThread.RunningRequests <= 0)
                    RaiseOnAllRequestsComplete();
            }
        }
        internal void ResponseCallback(IAsyncResult result)
        {
            RequestState state = null;
            try
            {
                state = result.AsyncState as RequestState;
            }
            catch
            {
            }
            if (state == null)
                return;
            HttpWebResponse httpResponse;
            FileWebResponse fileResponse;
            NntpWebResponse nntpResponse;
            try
            {
                try
                {
                    state.Response = state.Request.EndGetResponse(result);
                }
                catch (Exception exception)
                {
                    WebException we = exception as WebException;
                    if (we != null && we.Response != null)
                    {
                        state.Response = we.Response;
                    }
                    else
                    {
                        throw;
                    }
                }
                httpResponse = state.Response as HttpWebResponse;
                fileResponse = state.Response as FileWebResponse;
                nntpResponse = state.Response as NntpWebResponse;
                if (httpResponse != null)
                {
                    if (httpResponse.ResponseUri != state.RequestUri)
                    {
                        _log.Debug(
                            String.Format("httpResponse.ResponseUri != state.RequestUri: \r\n'{0}'\r\n'{1}'",
                                          httpResponse.ResponseUri, state.RequestUri));
                    }
                    if (HttpStatusCode.OK == httpResponse.StatusCode ||
                        HttpExtendedStatusCode.IMUsed == (HttpExtendedStatusCode) httpResponse.StatusCode)
                    {
                        HttpCookieManager.GetCookies(httpResponse);
                        state.RequestParams.ETag = httpResponse.Headers.Get("ETag");
                        try
                        {
                            state.RequestParams.LastModified = httpResponse.LastModified;
                        }
                        catch (Exception lmEx)
                        {
                            _log.Debug("httpResponse.LastModified() parse failure: " + lmEx.Message);
                            try
                            {
                                state.RequestParams.LastModified =
                                    DateTimeExt.Parse(httpResponse.Headers.Get("Last-Modified"));
                            }
                            catch
                            {
                            }
                        }
                        state.ResponseStream = httpResponse.GetResponseStream();
                        state.ResponseStream.BeginRead(state.BufferRead, 0, RequestState.BUFFER_SIZE,
                                                       ReadCallback, state);
                        _log.Debug("ResponseCallback() web response OK: " + state.RequestUri);
                        return;
                    }
                    else if (httpResponse.StatusCode == HttpStatusCode.NotModified)
                    {
                        HttpCookieManager.GetCookies(httpResponse);
                        string eTag = httpResponse.Headers.Get("ETag");
                        state.OnRequestCompleted(state.InitialRequestUri, state.RequestParams.RequestUri, eTag, MinValue,
                                                 RequestResult.NotModified);
                        FinalizeWebRequest(state);
                    }
                    else if ((httpResponse.StatusCode == HttpStatusCode.MovedPermanently)
                             || (httpResponse.StatusCode == HttpStatusCode.Moved))
                    {
                        state.RetryCount++;
                        if (state.RetryCount > RequestState.MAX_RETRIES)
                        {
                            throw new WebException("Repeated HTTP httpResponse: " + httpResponse.StatusCode,
                                                   null, WebExceptionStatus.RequestCanceled, httpResponse);
                        }
                        string url2 = httpResponse.Headers["Location"];
                        HttpCookieManager.GetCookies(httpResponse);
                        state.movedPermanently = true;
                        queuedRequests.Remove(state.InitialRequestUri.CanonicalizedUri());
                        _log.Debug("ResponseCallback() Moved: '" + state.InitialRequestUri + " to " + url2);
                        Uri req;
                        if (!Uri.TryCreate(url2, UriKind.Absolute, out req))
                        {
                            if (!Uri.TryCreate(httpResponse.ResponseUri, url2, out req))
                                throw new WebException(
                                    string.Format(
                                        "Original resource temporary redirected. Request new resource at '{0}{1}' failed: ",
                                        httpResponse.ResponseUri, url2));
                        }
                        RequestParameter rqp = RequestParameter.Create(req, state.RequestParams);
                        QueueRequest(rqp, null, null, null, null, null, state.Priority + 1, state);
                        RequestThread.EndRequest(state);
                    }
                    else if (IsRedirect(httpResponse.StatusCode))
                    {
                        state.RetryCount++;
                        if (state.RetryCount > RequestState.MAX_RETRIES)
                        {
                            throw new WebException("Repeated HTTP httpResponse: " + httpResponse.StatusCode,
                                                   null, WebExceptionStatus.RequestCanceled, httpResponse);
                        }
                        string url2 = httpResponse.Headers["Location"];
                        HttpCookieManager.GetCookies(httpResponse);
                        queuedRequests.Remove(state.InitialRequestUri.CanonicalizedUri());
                        _log.Debug("ResponseCallback() Redirect: '" + state.InitialRequestUri + " to " + url2);
                        Uri req;
                        if (!Uri.TryCreate(url2, UriKind.Absolute, out req))
                        {
                            if (!Uri.TryCreate(httpResponse.ResponseUri, url2, out req))
                                throw new WebException(
                                    string.Format(
                                        "Original resource temporary redirected. Request new resource at '{0}{1}' failed: ",
                                        httpResponse.ResponseUri, url2));
                        }
                        RequestParameter rqp =
                            RequestParameter.Create(req, RebuildCredentials(state.RequestParams.Credentials, url2),
                                                    state.RequestParams);
                        QueueRequest(rqp, null, null, null, null, null, state.Priority + 1, state);
                        RequestThread.EndRequest(state);
                    }
                    else if (IsUnauthorized(httpResponse.StatusCode))
                    {
                        if (state.RequestParams.Credentials == null)
                        {
                            state.RetryCount++;
                            queuedRequests.Remove(state.InitialRequestUri.CanonicalizedUri());
                            RequestParameter rqp =
                                RequestParameter.Create(CredentialCache.DefaultCredentials, state.RequestParams);
                            QueueRequest(rqp, null, null, null, null, null, state.Priority + 1, state);
                            RequestThread.EndRequest(state);
                        }
                        else
                        {
                            if (state.RequestParams.SetCookies)
                            {
                                state.RetryCount++;
                                queuedRequests.Remove(state.InitialRequestUri.CanonicalizedUri());
                                RequestParameter rqp = RequestParameter.Create(false, state.RequestParams);
                                QueueRequest(rqp, null, null, null, null, null, state.Priority + 1, state);
                                RequestThread.EndRequest(state);
                            }
                            else
                            {
                                throw new ResourceAuthorizationException();
                            }
                        }
                    }
                    else if (httpResponse.StatusCode == HttpStatusCode.Gone)
                    {
                        throw new ResourceGoneException();
                    }
                    else
                    {
                        string statusCode = httpResponse.StatusCode.ToString();
                        throw new WebException("Unexpected HTTP httpResponse: " + statusCode);
                    }
                }
                else if (fileResponse != null)
                {
                    string reqFile = fileResponse.ResponseUri.LocalPath;
                    if (File.Exists(reqFile))
                    {
                        DateTime lwt = File.GetLastWriteTime(reqFile);
                        state.RequestParams.ETag = lwt.ToString();
                        state.RequestParams.LastModified = lwt;
                    }
                    state.ResponseStream = fileResponse.GetResponseStream();
                    state.ResponseStream.BeginRead(state.BufferRead, 0, RequestState.BUFFER_SIZE,
                                                   ReadCallback, state);
                    _log.Debug("ResponseCallback() file response OK: " + state.RequestUri);
                    return;
                }
                else if (nntpResponse != null)
                {
                    state.RequestParams.LastModified = DateTime.Now;
                    state.ResponseStream = nntpResponse.GetResponseStream();
                    state.ResponseStream.BeginRead(state.BufferRead, 0, RequestState.BUFFER_SIZE,
                                                   ReadCallback, state);
                    _log.Debug("ResponseCallback() nntp response OK: " + state.RequestUri);
                    return;
                }
                else
                {
                    Debug.Assert(false,
                                 "ResponseCallback(): unhandled WebResponse type: " +
                                 state.Response.GetType());
                    FinalizeWebRequest(state);
                }
            }
            catch (ThreadAbortException)
            {
                FinalizeWebRequest(state);
                return;
            }
            catch (Exception ex)
            {
                state.OnRequestException(state.InitialRequestUri, ex);
            }
        }
        private static ICredentials RebuildCredentials(ICredentials credentials, string redirectUrl)
        {
            CredentialCache cc = credentials as CredentialCache;
            if (cc != null)
            {
                IEnumerator iterate = cc.GetEnumerator();
                while (iterate.MoveNext())
                {
                    NetworkCredential c = iterate.Current as NetworkCredential;
                    if (c != null)
                    {
                        string domainUser = c.Domain;
                        if (!string.IsNullOrEmpty(domainUser))
                            domainUser = domainUser + @"\";
                        domainUser = String.Concat(domainUser, c.UserName);
                        return NewsHandler.CreateCredentialsFrom(redirectUrl, domainUser, c.Password);
                    }
                }
            }
            return credentials;
        }
        private void ReadCallback(IAsyncResult result)
        {
            RequestState state = null;
            try
            {
                state = result.AsyncState as RequestState;
            }
            catch
            {
            }
            if (state == null)
                return;
            try
            {
                Stream responseStream = state.ResponseStream;
                int read = responseStream.EndRead(result);
                if (Common.ClrVersion.Major < 2 && result.AsyncWaitHandle != null)
                    result.AsyncWaitHandle.Close();
                if (read > 0)
                {
                    state.bytesTransferred += read;
                    state.RequestData.Write(state.BufferRead, 0, read);
                    responseStream.BeginRead(state.BufferRead, 0, RequestState.BUFFER_SIZE,
                                             ReadCallback, state);
                    if (((state.bytesTransferred/RequestState.BUFFER_SIZE)%10) == 0)
                    {
                        state.OnRequestProgress(state.InitialRequestUri, state.bytesTransferred);
                    }
                    return;
                }
                else
                {
                    if (state.Response is HttpWebResponse)
                    {
                        state.ResponseStream =
                            GetDeflatedResponse(((HttpWebResponse) state.Response).ContentEncoding, state.RequestData);
                    }
                    else
                    {
                        state.ResponseStream = GetDeflatedResponse(String.Empty, state.RequestData);
                    }
                    state.OnRequestCompleted(state.InitialRequestUri, state.RequestParams.RequestUri,
                                             state.RequestParams.ETag, state.RequestParams.LastModified,
                                             RequestResult.OK);
                    responseStream.Close();
                    state.RequestData.Close();
                }
            }
            catch (WebException e)
            {
                _log.Error("ReadCallBack WebException raised. Status: " + e.Status, e);
                state.OnRequestException(state.RequestParams.RequestUri, e);
            }
            catch (Exception e)
            {
                _log.Error("ReadCallBack Exception raised", e);
                state.OnRequestException(state.RequestParams.RequestUri, e);
            }
            FinalizeWebRequest(state);
        }
        public static Stream GetDeflatedResponse(HttpWebResponse response)
        {
            return GetDeflatedResponse(response.ContentEncoding,
                                       ResponseToMemory(response.GetResponseStream()));
        }
        public static Stream GetDeflatedResponse(FileWebResponse response)
        {
            return ResponseToMemory(response.GetResponseStream());
        }
        public static Stream GetDeflatedResponse(string encoding, Stream inputStream)
        {
            const int BUFFER_SIZE = 4096;
            Stream compressed, input = inputStream;
            bool tryAgainDeflate = true;
            if (input.CanSeek)
                input.Seek(0, SeekOrigin.Begin);
            if (encoding == "deflate")
            {
                compressed = new InflaterInputStream(input);
            }
            else if (encoding == "gzip")
            {
                compressed = new GZipInputStream(input);
            }
            else
            {
                return input;
            }
            while (true)
            {
                MemoryStream decompressed = new MemoryStream();
                try
                {
                    int size = BUFFER_SIZE;
                    byte[] writeData = new byte[BUFFER_SIZE];
                    while (true)
                    {
                        size = compressed.Read(writeData, 0, size);
                        if (size > 0)
                        {
                            decompressed.Write(writeData, 0, size);
                        }
                        else
                        {
                            break;
                        }
                    }
                    decompressed.Seek(0, SeekOrigin.Begin);
                    return decompressed;
                }
                catch (SharpZipBaseException)
                {
                    if (tryAgainDeflate && (encoding == "deflate"))
                    {
                        input.Seek(0, SeekOrigin.Begin);
                        compressed = new InflaterInputStream(input, new Inflater(true));
                        tryAgainDeflate = false;
                    }
                    else
                        throw;
                }
            }
        }
        private static Stream ResponseToMemory(Stream input)
        {
            const int BUFFER_SIZE = 4096;
            MemoryStream output = new MemoryStream();
            int size = BUFFER_SIZE;
            byte[] writeData = new byte[BUFFER_SIZE];
            while (true)
            {
                size = input.Read(writeData, 0, size);
                if (size > 0)
                {
                    output.Write(writeData, 0, size);
                }
                else
                {
                    break;
                }
            }
            output.Seek(0, SeekOrigin.Begin);
            return output;
        }
        public static bool IsRedirect(HttpStatusCode statusCode)
        {
            if ((statusCode == HttpStatusCode.Ambiguous)
                || (statusCode == HttpStatusCode.Found)
                || (statusCode == HttpStatusCode.MultipleChoices)
                || (statusCode == HttpStatusCode.Redirect)
                || (statusCode == HttpStatusCode.RedirectKeepVerb)
                || (statusCode == HttpStatusCode.RedirectMethod)
                || (statusCode == HttpStatusCode.TemporaryRedirect)
                || (statusCode == HttpStatusCode.SeeOther))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        public static bool IsUnauthorized(HttpStatusCode statusCode)
        {
            if (statusCode == HttpStatusCode.Unauthorized)
                return true;
            return false;
        }
        public static WebResponse GetSyncResponse(string address, ICredentials credentials, string userAgent,
                                                  IWebProxy proxy, DateTime ifModifiedSince, string eTag, int timeout)
        {
            try
            {
                WebRequest webRequest = WebRequest.Create(address);
                HttpWebRequest httpRequest = webRequest as HttpWebRequest;
                FileWebRequest fileRequest = webRequest as FileWebRequest;
                if (httpRequest != null)
                {
                    httpRequest.Timeout = (timeout <= 0 ? DefaultTimeout : timeout);
                    httpRequest.UserAgent = FullUserAgent(userAgent);
                    httpRequest.Proxy = proxy;
                    httpRequest.AllowAutoRedirect = false;
                    httpRequest.IfModifiedSince = ifModifiedSince;
                    httpRequest.Headers.Add("Accept-Encoding", "gzip, deflate");
                    if (eTag != null)
                    {
                        httpRequest.Headers.Add("If-None-Match", eTag);
                        httpRequest.Headers.Add("A-IM", "feed");
                    }
                    if (credentials != null)
                    {
                        httpRequest.Credentials = credentials;
                    }
                }
                else if (fileRequest != null)
                {
                    fileRequest.Timeout = (timeout <= 0 ? DefaultTimeout : timeout);
                    if (credentials != null)
                    {
                        fileRequest.Credentials = credentials;
                    }
                }
                else
                {
                    Debug.Assert(false,
                                 "GetSyncResponse(): unhandled WebRequest type: " + webRequest.GetType());
                }
                return webRequest.GetResponse();
            }
            catch (Exception e)
            {
                WebException we = e as WebException;
                if ((we != null) && (we.Response != null))
                {
                    return we.Response;
                }
                else
                {
                    throw;
                }
            }
        }
        public static WebResponse GetSyncResponseHeadersOnly(string address, IWebProxy proxy, int timeout)
        {
            return GetSyncResponseHeadersOnly(address, proxy, timeout, null);
        }
        public static WebResponse GetSyncResponseHeadersOnly(string address, IWebProxy proxy, int timeout,
                                                             ICredentials credentials)
        {
            try
            {
                HttpWebRequest httpRequest = (HttpWebRequest) WebRequest.Create(address);
                httpRequest.Timeout = (timeout <= 0 ? DefaultTimeout : timeout);
                if (proxy != null)
                    httpRequest.Proxy = proxy;
                if (credentials != null)
                    httpRequest.Credentials = credentials;
                httpRequest.Method = "HEAD";
                return httpRequest.GetResponse();
            }
            catch (Exception e)
            {
                WebException we = e as WebException;
                if ((we != null) && (we.Response != null))
                {
                    return we.Response;
                }
                else
                {
                    throw;
                }
            }
        }
        public static Stream GetSyncResponseStream(string address, ICredentials credentials, IWebProxy proxy)
        {
            return GetSyncResponseStream(address, credentials, FullUserAgent(null), proxy, DefaultTimeout);
        }
        public static Stream GetSyncResponseStream(string address, ICredentials credentials, IWebProxy proxy,
                                                   int timeout)
        {
            return GetSyncResponseStream(address, credentials, FullUserAgent(null), proxy, timeout);
        }
        public static Stream GetSyncResponseStream(string address, ICredentials credentials, string userAgent,
                                                   IWebProxy proxy)
        {
            return GetSyncResponseStream(address, credentials, userAgent, proxy, DefaultTimeout);
        }
        public static Stream GetSyncResponseStream(string address, ICredentials credentials, string userAgent,
                                                   IWebProxy proxy, int timeout)
        {
            string newAddress, eTag = null;
            RequestResult result;
            DateTime ifModifiedSince = MinValue;
            return
                GetSyncResponseStream(address, out newAddress, credentials, userAgent, proxy, ref ifModifiedSince,
                                      ref eTag, timeout, out result);
        }
        public static Stream GetSyncResponseStream(string address, out string newAddress, ICredentials credentials,
                                                   string userAgent, IWebProxy proxy, int timeout)
        {
            string eTag = null;
            RequestResult result;
            DateTime ifModifiedSince = MinValue;
            return
                GetSyncResponseStream(address, out newAddress, credentials, userAgent, proxy, ref ifModifiedSince,
                                      ref eTag, timeout, out result);
        }
        public static Stream GetSyncResponseStream(string address, out string newAddress, ICredentials credentials,
                                                   string userAgent,
                                                   IWebProxy proxy, ref DateTime ifModifiedSince, ref string eTag,
                                                   int timeout, out RequestResult responseResult)
        {
            bool useDefaultCred = false;
            int requestRetryCount = 0;
            const int MaxRetries = 25;
            newAddress = null;
            send_request:
            string requestUri = address;
            if (useDefaultCred)
                credentials = CredentialCache.DefaultCredentials;
            WebResponse wr =
                GetSyncResponse(address, credentials, userAgent, proxy, ifModifiedSince, eTag, timeout);
            HttpWebResponse response = wr as HttpWebResponse;
            FileWebResponse fileresponse = wr as FileWebResponse;
            if (response != null)
            {
                if (HttpStatusCode.OK == response.StatusCode ||
                    HttpExtendedStatusCode.IMUsed == (HttpExtendedStatusCode) response.StatusCode)
                {
                    responseResult = RequestResult.OK;
                    Stream ret = GetDeflatedResponse(response);
                    response.Close();
                    return ret;
                }
                else if ((response.StatusCode == HttpStatusCode.MovedPermanently)
                         || (response.StatusCode == HttpStatusCode.Moved))
                {
                    newAddress = HtmlHelper.ConvertToAbsoluteUrl(response.Headers["Location"], address, false);
                    address = newAddress;
                    response.Close();
                    if (requestRetryCount < MaxRetries)
                    {
                        requestRetryCount++;
                        goto send_request;
                    }
                }
                else if (IsUnauthorized(response.StatusCode))
                {
                    useDefaultCred = true;
                    response.Close();
                    if (requestRetryCount < MaxRetries)
                    {
                        requestRetryCount++;
                        goto send_request;
                    }
                }
                else if (IsRedirect(response.StatusCode))
                {
                    address = HtmlHelper.ConvertToAbsoluteUrl(response.Headers["Location"], address, false);
                    response.Close();
                    if (requestRetryCount < MaxRetries)
                    {
                        requestRetryCount++;
                        goto send_request;
                    }
                }
                else if (response.StatusCode == HttpStatusCode.Gone)
                {
                    throw new ResourceGoneException();
                }
                else
                {
                    string statusCode = response.StatusCode.ToString();
                    response.Close();
                    throw new WebException("Unexpected HTTP response: " + statusCode);
                }
                if (IsUnauthorized(response.StatusCode))
                {
                    response.Close();
                    throw new ResourceAuthorizationException();
                }
                string returnCode = response.StatusCode.ToString();
                response.Close();
                throw new WebException("Repeated HTTP response: " + returnCode);
            }
            else if (fileresponse != null)
            {
                responseResult = RequestResult.OK;
                Stream ret = GetDeflatedResponse(fileresponse);
                fileresponse.Close();
                return ret;
            }
            else
            {
                throw new ApplicationException("no handler for WebResponse. Address: " + requestUri);
            }
        }
        private void RaiseOnAllRequestsComplete()
        {
            if (OnAllRequestsComplete != null)
            {
                try
                {
                    OnAllRequestsComplete();
                }
                catch
                {
                }
            }
        }
        internal static void RaiseOnCertificateIssue(object sender, CertificateIssueCancelEventArgs e)
        {
            string url = e.WebRequest.RequestUri.CanonicalizedUri();
            ICollection trusted = null;
            if (trustedCertificateIssues != null)
            {
                lock (trustedCertificateIssues)
                {
                    if (trustedCertificateIssues.ContainsKey(url))
                        trusted = (ICollection) trustedCertificateIssues[url];
                }
            }
            if (trusted != null && trusted.Count > 0)
            {
                foreach (CertificateIssue trustedIssue in trusted)
                {
                    if (trustedIssue == e.CertificateIssue)
                    {
                        e.Cancel = false;
                        return;
                    }
                }
            }
            if (OnCertificateIssue != null)
            {
                try
                {
                    OnCertificateIssue(sender, e);
                }
                catch
                {
                }
            }
        }
    }
    [Serializable]
    public enum CertificateIssue : long
    {
        CertEXPIRED = 0x800B0101,
        CertVALIDITYPERIODNESTING = 0x800B0102,
        CertROLE = 0x800B0103,
        CertPATHLENCONST = 0x800B0104,
        CertCRITICAL = 0x800B0105,
        CertPURPOSE = 0x800B0106,
        CertISSUERCHAINING = 0x800B0107,
        CertMALFORMED = 0x800B0108,
        CertUNTRUSTEDROOT = 0x800B0109,
        CertCHAINING = 0x800B010A,
        CertREVOKED = 0x800B010C,
        CertUNTRUSTEDTESTROOT = 0x800B010D,
        CertREVOCATION_FAILURE = 0x800B010E,
        CertCN_NO_MATCH = 0x800B010F,
        CertWRONG_USAGE = 0x800B0110,
        CertUNTRUSTEDCA = 0x800B0112
    }
    public delegate void CertificateIssueHandler(object sender, CertificateIssueCancelEventArgs e);
    [ComVisible(false)]
    public class CertificateIssueCancelEventArgs : CancelEventArgs
    {
        public CertificateIssue CertificateIssue;
        public X509Certificate Certificate;
        public WebRequest WebRequest;
        public CertificateIssueCancelEventArgs(CertificateIssue issue, X509Certificate cert, WebRequest request,
                                               bool cancel) : base(cancel)
        {
            CertificateIssue = issue;
            Certificate = cert;
            WebRequest = request;
        }
    }
    internal class TrustSelectedCertificatePolicy : ICertificatePolicy
    {
        public bool CheckValidationResult(ServicePoint sp, X509Certificate cert, WebRequest req, int problem)
        {
            try
            {
                if (problem != 0)
                {
                    CertificateIssue issue = (CertificateIssue) (((problem << 1) >> 1) + 0x80000000);
     CertificateIssueCancelEventArgs args = new CertificateIssueCancelEventArgs(issue, sp.Certificate, req, true);
                    AsyncWebRequest.RaiseOnCertificateIssue(sp, args);
                    return !args.Cancel;
                }
            }
            catch (Exception ex)
            {
                Trace.WriteLine("TrustSelectedCertificatePolicy.CheckValidationResult() error: " + ex.Message);
            }
            return (problem == 0);
        }
        public static bool CheckServerCertificate(object sender, X509Certificate certificate, X509Chain chain, SslPolicyErrors sslPolicyErrors)
        {
            return true;
        }
    }
    internal class HttpCookieManager
    {
        private static readonly ILog _log = Log.GetLogger(typeof (HttpCookieManager));
        public static void SetCookies(HttpWebRequest request)
        {
            CookieContainer c = GetCookieContainerUri(request.RequestUri);
            if (c.Count > 0)
                request.CookieContainer = c;
        }
        public static void GetCookies(HttpWebResponse response)
        {
            if (response.Headers["Set-Cookie"] != null)
            {
            }
        }
        [DllImport("wininet.dll", CharSet=CharSet.Auto, SetLastError=true)]
        private static extern bool InternetGetCookie(
            string lpszUrl, string lpszCookieName, StringBuilder lpCookieData, ref int lpdwSize);
        [DllImport("wininet.dll", CharSet=CharSet.Auto, SetLastError=true)]
        private static extern bool InternetSetCookie(
            string lpszUrl, string lpszCookieName, string lpszCookieData);
        private static CookieContainer GetCookieContainerUri(Uri url)
        {
            CookieContainer container = new CookieContainer();
            string cookieHeaders = RetrieveIECookiesForUrl(url.CanonicalizedUri());
            if (cookieHeaders.Length > 0)
            {
                try
                {
                    container.SetCookies(url, cookieHeaders);
                }
                catch (CookieException ce)
                {
                    _log.Error(
                        String.Format("GetCookieContainerUri() exception parsing '{0}' for url '{1}'", cookieHeaders,
                                      url.CanonicalizedUri()), ce);
                }
            }
            return container;
        }
        private static string RetrieveIECookiesForUrl(string url)
        {
            StringBuilder cookieHeader = new StringBuilder(new String(' ', 256), 256);
            int datasize = cookieHeader.Length;
            if (!InternetGetCookie(url, null, cookieHeader, ref datasize))
            {
                if (datasize < 0)
                    return String.Empty;
                cookieHeader = new StringBuilder(datasize);
                InternetGetCookie(url, null, cookieHeader, ref datasize);
            }
            return FixupIECookies(cookieHeader);
        }
        private static string FixupIECookies(StringBuilder b)
        {
            string s = b.ToString();
            if (s.IndexOf(",") >= 0 && s.IndexOf(";") >= 0)
            {
                s = s.Replace(",", escapedComma).Replace(";", ",");
            }
            return s;
        }
        private static readonly string escapedComma = HtmlHelper.UrlEncode(",");
    }
}
