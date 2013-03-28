using System;
using System.Collections.Specialized;
using System.Collections.Generic;
using System.Text;
using System.IO;
using System.Net;
using Utility;
namespace WorldWind.DataSource
{
    public class DataRequestHTTP : DataRequest
    {
        private WebRequest m_webRequest;
        private IAsyncResult m_requestResult;
        private IAsyncResult m_readResult;
        private Stream m_responseStream;
        private byte[] m_buffer;
        private int m_bytesRead;
        public override float Progress
        {
            get
            {
                if (m_buffer == null)
                    return 0;
                else
                    return (float)(100.0f * m_bytesRead / (float)m_buffer.Length);
            }
        }
        public DataRequestHTTP(DataRequestDescriptor request)
            : base(request)
        {
        }
        public override void Start()
        {
            m_state = DataRequestState.InProcess;
            m_webRequest = WebRequest.Create(m_request.Source);
            m_webRequest.CachePolicy = new System.Net.Cache.RequestCachePolicy(System.Net.Cache.RequestCacheLevel.BypassCache);
            m_webRequest.Proxy = WebRequest.GetSystemWebProxy();
            m_requestResult = m_webRequest.BeginGetResponse(new AsyncCallback(ResponseCallback), this);
        }
        private static void ResponseCallback(IAsyncResult asyncResult)
        {
            DataRequestHTTP dataRequest = asyncResult.AsyncState as DataRequestHTTP;
            try
            {
                WebResponse response = dataRequest.m_webRequest.EndGetResponse(asyncResult);
                if (response.ContentLength > 0)
                {
                    dataRequest.m_headers = response.Headers;
                    dataRequest.m_responseStream = response.GetResponseStream();
                    dataRequest.m_buffer = new byte[response.ContentLength];
                    dataRequest.m_bytesRead = 0;
                    dataRequest.m_readResult = dataRequest.m_responseStream.BeginRead(dataRequest.m_buffer, 0, (int)(response.ContentLength), new AsyncCallback(ReadCallback), dataRequest);
                }
                else
                {
                    response.Close();
                    using (System.Net.WebClient client = new WebClient())
                    {
                        dataRequest.m_buffer = client.DownloadData(response.ResponseUri.OriginalString);
                        dataRequest.m_bytesRead = dataRequest.m_buffer.Length;
                        dataRequest.m_contentStream = new MemoryStream(dataRequest.m_buffer);
                        dataRequest.m_state = DataRequestState.Finished;
                    }
                }
            }
            catch (System.Net.WebException ex)
            {
                Log.Write(Log.Levels.Warning, "DataRequest: exception caught trying to access " + dataRequest.Source);
                Log.Write(ex);
                dataRequest.m_state = DataRequestState.Error;
            }
        }
        private static void ReadCallback(IAsyncResult asyncResult)
        {
            DataRequestHTTP dataRequest = asyncResult.AsyncState as DataRequestHTTP;
            int newBytes = dataRequest.m_responseStream.EndRead(asyncResult);
            m_totalBytes += newBytes;
            dataRequest.m_bytesRead += newBytes;
            if (dataRequest.m_bytesRead < dataRequest.m_buffer.Length)
            {
                dataRequest.m_responseStream.BeginRead(dataRequest.m_buffer, dataRequest.m_bytesRead, dataRequest.m_buffer.Length - dataRequest.m_bytesRead, new AsyncCallback(ReadCallback), dataRequest);
                return;
            }
            dataRequest.m_responseStream.Close();
            dataRequest.m_contentStream = new MemoryStream(dataRequest.m_buffer);
            dataRequest.m_state = DataRequestState.Finished;
        }
    }
}
