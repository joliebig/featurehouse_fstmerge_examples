using System;
using System.Collections.Generic;
using System.Text;
namespace ThoughtWorks.CruiseControl.Core.Security
{
    public interface ISessionCache
    {
        void Initialise();
        string AddToCache(string userName);
        string RetrieveFromCache(string sessionToken);
        void RemoveFromCache(string sessionToken);
        void StoreSessionValue(string sessionToken, string key, object value);
        object RetrieveSessionValue(string sessionToken, string key);
    }
}
