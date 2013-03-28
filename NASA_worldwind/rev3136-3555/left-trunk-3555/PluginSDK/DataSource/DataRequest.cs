using System;
using System.Collections.Specialized;
using System.Collections.Generic;
using System.Text;
using System.IO;
using Utility;
namespace WorldWind.DataSource
{
    public delegate Stream CacheCallback(DataRequestDescriptor drd);
    public delegate void CompletionCallback(DataRequest dr);
    public delegate float PriorityCallback();
    public enum DataRequestState
    {
        Queued,
        NoCache,
        InProcess,
        Finished,
        Error,
        Delayed,
        Cancelled
    }
    public struct DataRequestDescriptor
    {
        public string Source;
        public string CacheLocation;
        public TimeSpan? MaxCacheAge;
        public string Description;
        public int BasePriority;
        public CacheCallback CacheCallback;
        public CompletionCallback CompletionCallback;
        public PriorityCallback PriorityCallback;
        public DataRequestDescriptor(string source, string cacheLocation, CacheCallback callback) : this(source, cacheLocation)
        {
            CacheCallback = callback;
        }
        public DataRequestDescriptor(string source, string cacheLocation)
        {
            this.Source = source;
            this.CacheLocation = cacheLocation;
            MaxCacheAge = null;
            CacheCallback = null;
            CompletionCallback = null;
            PriorityCallback = null;
            BasePriority = 0;
            Description = "";
        }
    }
    public abstract class DataRequest : IComparable
    {
        protected Object m_lock;
        protected DataRequestDescriptor m_request;
        protected Stream m_contentStream;
        protected DataRequestState m_state;
        protected bool m_cacheHit;
        protected NameValueCollection m_headers;
        protected float m_priority;
        protected DateTime m_nextTry;
        public DataRequestDescriptor RequestDescriptor { get { return m_request; } }
        public NameValueCollection Headers
        {
            get
            {
                return m_headers;
            }
        }
        public String Source
        {
            get
            {
                return m_request.Source;
            }
        }
        public DataRequestState State
        {
            get
            {
                return m_state;
            }
            set
            {
                m_state = value;
            }
        }
        public Stream Stream
        {
            get
            {
                return m_contentStream;
            }
        }
        public string CacheLocation
        {
            get
            {
                return m_request.CacheLocation;
            }
        }
        public bool CacheHit
        {
            get
            {
                return m_cacheHit;
            }
        }
        public float Priority
        {
            get
            {
                return m_priority;
            }
        }
        public DateTime NextTry
        {
            get { return m_nextTry; }
            set { m_nextTry = value; }
        }
        abstract public float Progress
        {
            get;
        }
        static protected int m_cacheHits = 0;
        static protected int m_totalRequests = 0;
        static protected int m_totalBytes = 0;
        static public int CacheHits { get { return m_cacheHits; } }
        static public int TotalRequests { get { return m_totalRequests; } }
        static public int TotalBytes { get { return m_totalBytes; } }
        public DataRequest(DataRequestDescriptor request)
        {
            m_lock = new Object();
            m_request = request;
            m_contentStream = null;
            m_state = DataRequestState.Queued;
            m_cacheHit = false;
            m_headers = new NameValueCollection();
            m_priority = 50;
            m_totalRequests++;
            m_nextTry = DateTime.Now;
        }
        public void UpdatePriority()
        {
            if (m_nextTry > DateTime.Now)
            {
                m_priority = -1;
                return;
            }
            if (m_request.PriorityCallback != null)
                m_priority = m_request.PriorityCallback();
            else
                m_priority = 50;
        }
        [Obsolete]
        public void Cancel()
        {
            this.State = DataRequestState.Cancelled;
        }
        public abstract void Start();
        public bool TryCache()
        {
            try
            {
                if (m_request.CacheCallback != null)
                {
                    m_contentStream = m_request.CacheCallback(m_request);
                    if (m_contentStream != null)
                    {
                        m_state = DataRequestState.Finished;
                        m_cacheHit = true;
                        m_cacheHits++;
                        return true;
                    }
                }
                return false;
            }
            catch (IOException)
            {
                return false;
            }
        }
        public int CompareTo(object obj)
        {
            DataRequest dr = obj as DataRequest;
            if(dr == null)
                return 0;
            if (this.NextTry > DateTime.Now) return 1;
            if (dr.NextTry > DateTime.Now) return -1;
            if (this.m_request.BasePriority != dr.m_request.BasePriority)
                return -Math.Sign(this.m_request.BasePriority - dr.m_request.BasePriority);
            return -Math.Sign(this.m_priority - dr.m_priority);
        }
    }
}
