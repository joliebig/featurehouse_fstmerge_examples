using System;
using System.Collections.Generic;
using System.Text;
using System.ComponentModel;
namespace NewsComponents.Utils
{
    class CollectionChangedEventArgs : PropertyChangedEventArgs {
        public CollectionChangedEventArgs(string collectionName, CollectionChangeAction action, object value)
            : base(collectionName)
        {
            this.Value = value;
            this.Action = action;
        }
        public object Value;
        public CollectionChangeAction Action;
    }
    class DataBindingHelper
    {
        private static Object syncRoot = new Object();
        private static IDictionary<string, PropertyChangedEventArgs> propertyChangedArgsCache = new Dictionary<string, PropertyChangedEventArgs>();
        public static PropertyChangedEventArgs
            GetPropertyChangedEventArgs(string propertyName)
        {
            if (String.IsNullOrEmpty(propertyName))
                throw new ArgumentException(
                    "propertyName cannot be null or empty.");
            PropertyChangedEventArgs args;
            lock (syncRoot)
            {
                if (!propertyChangedArgsCache.TryGetValue(propertyName, out args))
                {
                    args = new PropertyChangedEventArgs(propertyName);
                    propertyChangedArgsCache.Add(propertyName, args);
                }
            }
            return args;
        }
    }
}
