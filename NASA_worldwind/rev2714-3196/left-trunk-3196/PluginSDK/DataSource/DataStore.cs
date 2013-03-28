using System;
using System.IO;
using System.Collections;
using System.Collections.Generic;
using System.Text;
using Utility;
namespace WorldWind.DataSource
{
    public class DataStore
    {
        static private ArrayList m_pendingRequests = new ArrayList(15);
        static private ArrayList m_activeRequests = new ArrayList(5);
        static private ArrayList m_finishedRequests = new ArrayList();
        static private int m_maxActiveRequests = 5;
        static private Object m_lock = new Object();
        static public ArrayList ActiveRequests
        {
            get { return m_activeRequests; }
        }
        static public ArrayList PendingRequests
        {
            get { return m_pendingRequests; }
        }
        static public int PendingRequestCount
        {
            get
            {
                return m_pendingRequests.Count;
            }
        }
        static public int ActiveRequestCount
        {
            get
            {
                return m_activeRequests.Count;
            }
        }
        static public DataRequest Request(DataRequestDescriptor request)
        {
            DataRequestHTTP drd = new DataRequestHTTP(request);
            lock (m_lock)
            {
                m_pendingRequests.Insert(0,drd);
            }
            return drd;
        }
        static public void Update()
        {
            for(int i =0; i < m_activeRequests.Count; i++)
            {
                DataRequestHTTP dr = m_activeRequests[i] as DataRequestHTTP;
                if (dr.State != DataRequestState.InProcess)
                {
                    if (dr.State == DataRequestState.Finished && dr.RequestDescriptor.CompletionCallback != null)
                    {
                        dr.RequestDescriptor.CompletionCallback(dr);
                    }
                    else if (dr.State == DataRequestState.Error)
                    {
                        dr.State = DataRequestState.Delayed;
                        dr.NextTry = DateTime.Now + TimeSpan.FromSeconds(120);
                        Log.Write(Log.Levels.Warning, "DataStore: request " + dr.Source + " has error, delaying until " + dr.NextTry.ToLongTimeString());
                        m_pendingRequests.Add(dr);
                    }
                    m_activeRequests.Remove(dr);
                }
            }
            lock(m_lock)
            {
                if (ActiveRequestCount < m_maxActiveRequests)
                {
                    foreach (DataRequest dr in m_pendingRequests)
                    {
                        dr.UpdatePriority();
                        if (dr.State == DataRequestState.Delayed && dr.NextTry < DateTime.Now)
                            dr.State = DataRequestState.NoCache;
                    }
                    for (int i = 0; i < PendingRequestCount; i++)
                    {
                        DataRequest dr = m_pendingRequests[i] as DataRequest;
                        if (dr.State == DataRequestState.Queued)
                        {
                            if (dr.TryCache())
                            {
                                dr.RequestDescriptor.CompletionCallback(dr);
                                m_pendingRequests.Remove(dr);
                            }
                            else
                                dr.State = DataRequestState.NoCache;
                        }
                        if (dr.State == DataRequestState.Cancelled)
                            m_pendingRequests.Remove(dr);
                    }
                    m_pendingRequests.Sort();
                }
            }
            while ((PendingRequestCount > 0) && (ActiveRequestCount < m_maxActiveRequests))
            {
                DataRequest drd = null;
                lock (m_lock)
                {
                    drd = m_pendingRequests[0] as DataRequest;
                    if (drd.State != DataRequestState.NoCache || drd.NextTry > DateTime.Now)
                        break;
                    m_pendingRequests.RemoveAt(0);
                }
                {
                    drd.Start();
                    m_activeRequests.Add(drd);
                }
            }
        }
    }
}
