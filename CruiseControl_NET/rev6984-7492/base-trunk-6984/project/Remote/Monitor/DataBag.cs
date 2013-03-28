using System;
using System.Collections.Generic;
using System.Text;
namespace ThoughtWorks.CruiseControl.Remote.Monitor
{
    public class DataBag
    {
        private Dictionary<Type, object> dataStore = new Dictionary<Type, object>();
        public TData Get<TData>()
        {
            var dataType = typeof(TData);
            if (dataStore.ContainsKey(dataType))
            {
                return (TData)dataStore[dataType];
            }
            else
            {
                return default(TData);
            }
        }
        public void Set<TData>(TData value)
        {
            var dataType = typeof(TData);
            dataStore[dataType] = value;
        }
        public void Delete<TData>()
        {
            var dataType = typeof(TData);
            if (dataStore.ContainsKey(dataType))
            {
                dataStore.Remove(dataType);
            }
        }
    }
}
