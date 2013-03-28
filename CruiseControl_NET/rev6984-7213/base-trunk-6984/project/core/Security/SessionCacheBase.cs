using Exortech.NetReflector;
using System;
using System.Collections.Generic;
namespace ThoughtWorks.CruiseControl.Core.Security
{
    public abstract class SessionCacheBase
        : ISessionCache
    {
        private readonly IClock clock;
        private readonly Dictionary<string, SessionDetails> cache = new Dictionary<string, SessionDetails>();
        private int durationInMinutes = 10;
        private SessionExpiryMode expiryMode = SessionExpiryMode.Sliding;
        protected SessionCacheBase(IClock clock)
        {
            this.clock = clock;
        }
        [ReflectorProperty("duration", Required = false)]
        public virtual int Duration
        {
            get { return durationInMinutes; }
            set { durationInMinutes = value; }
        }
        [ReflectorProperty("mode", Required = false)]
        public virtual SessionExpiryMode ExpiryMode
        {
            get { return expiryMode; }
            set { expiryMode = value; }
        }
        public virtual void Initialise()
        {
        }
        public virtual string AddToCache(string userName)
        {
            string sessionToken = Guid.NewGuid().ToString();
            SessionDetails session = new SessionDetails(userName, clock.Now.AddMinutes(durationInMinutes));
            AddToCacheInternal(sessionToken, session);
            return sessionToken;
        }
        protected virtual void AddToCacheInternal(string sessionToken, SessionDetails session)
        {
            lock (this)
            {
                cache.Add(sessionToken, session);
            }
        }
        public virtual string RetrieveFromCache(string sessionToken)
        {
            SessionDetails details = RetrieveSessionDetails(sessionToken);
            if (details == null)
            {
                return null;
            }
            else
            {
                return details.UserName;
            }
        }
        public virtual void RemoveFromCache(string sessionToken)
        {
            if (cache.ContainsKey(sessionToken))
            {
                lock (this)
                {
                    cache.Remove(sessionToken);
                }
            }
        }
        public virtual void StoreSessionValue(string sessionToken, string key, object value)
        {
            SessionDetails details = RetrieveSessionDetails(sessionToken);
            if (details != null)
            {
                lock (this)
                {
                    details.Values[key] = value;
                }
            }
        }
        public virtual object RetrieveSessionValue(string sessionToken, string key)
        {
            object value = null;
            SessionDetails details = RetrieveSessionDetails(sessionToken);
            if (details != null)
            {
                if (cache[sessionToken].Values.ContainsKey(key)) value = cache[sessionToken].Values[key];
            }
            return value;
        }
        protected virtual SessionDetails RetrieveSessionDetails(string sessionToken)
        {
            if (cache.ContainsKey(sessionToken))
            {
                SessionDetails session = cache[sessionToken];
                if (clock.Now < session.ExpiryTime)
                {
                    if (expiryMode == SessionExpiryMode.Sliding)
                    {
                        lock (this)
                        {
                            cache[sessionToken].ExpiryTime = clock.Now.AddMinutes(durationInMinutes);
                        }
                    }
                    return session;
                }
                else
                {
                    RemoveFromCache(sessionToken);
                    return null;
                }
            }
            else
            {
                return null;
            }
        }
        protected class SessionDetails
        {
            public string UserName;
            public DateTime ExpiryTime;
            public Dictionary<string, object> Values = new Dictionary<string, object>();
            public SessionDetails(string userName, DateTime expiry)
            {
                this.UserName = userName;
                this.ExpiryTime = expiry;
            }
        }
    }
}
