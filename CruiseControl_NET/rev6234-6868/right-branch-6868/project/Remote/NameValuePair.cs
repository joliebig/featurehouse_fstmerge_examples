using System;
using System.Collections.Generic;
using System.Runtime.Serialization;
using System.Text;
using System.Xml;
using System.Xml.Serialization;
using Exortech.NetReflector;
namespace ThoughtWorks.CruiseControl.Remote
{
    [Serializable]
    [ReflectorType("namedValue")]
    public class NameValuePair
    {
        private string name;
        private string namedValue;
        public NameValuePair() { }
        public NameValuePair(string name, string value)
        {
            this.name = name;
            this.namedValue = value;
        }
        [XmlAttribute("name")]
        [ReflectorProperty("name")]
        public string Name
        {
            get { return name; }
            set { name = value; }
        }
        [XmlAttribute("value")]
        [ReflectorProperty("value")]
        public string Value
        {
            get { return namedValue; }
            set { namedValue = value; }
        }
        public static Dictionary<string, string> ToDictionary(List<NameValuePair> values)
        {
            Dictionary<string, string> dictionary = new Dictionary<string, string>();
            if (values != null)
            {
                foreach (NameValuePair value in values)
                {
                    dictionary.Add(value.name, value.Value);
                }
            }
            return dictionary;
        }
        public static List<NameValuePair> FromDictionary(Dictionary<string, string> values)
        {
            List<NameValuePair> pairs = new List<NameValuePair>();
            if (values != null)
            {
                foreach (string key in values.Keys)
                {
                    pairs.Add(new NameValuePair(key, values[key]));
                }
            }
            return pairs;
        }
        public static string FindNamedValue(List<NameValuePair> values, string name)
        {
            string actualValue = null;
            if (values != null)
            {
                foreach (NameValuePair value in values)
                {
                    if (string.Equals(value.name, name, StringComparison.InvariantCultureIgnoreCase))
                    {
                        actualValue = value.Value;
                        break;
                    }
                }
            }
            return actualValue;
        }
        public static void Copy(Dictionary<string, string> dictionary, List<NameValuePair> list)
        {
            if (dictionary != null)
            {
                foreach (var pair in dictionary)
                {
                    list.Add(new NameValuePair(pair.Key, pair.Value));
                }
            }
        }
        public static void Copy(List<NameValuePair> source, List<NameValuePair> destination)
        {
            if (source != null)
            {
                foreach (var pair in source)
                {
                    destination.Add(new NameValuePair(pair.Name, pair.Value));
                }
            }
        }
    }
}
