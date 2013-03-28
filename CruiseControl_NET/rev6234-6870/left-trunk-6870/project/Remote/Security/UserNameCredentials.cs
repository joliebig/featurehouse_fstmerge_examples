using System;
using System.Collections.Generic;
using System.Runtime.Serialization;
using System.Text;
using System.Xml;
namespace ThoughtWorks.CruiseControl.Remote.Security
{
    [Serializable]
    public class UserNameCredentials
        : ISecurityCredentials
    {
        private const string userNameCredential = "username";
        [NonSerialized]
        private Dictionary<string, string> credentialsStore = new Dictionary<string,string>();
        private string asXml;
        public UserNameCredentials()
        {
            credentialsStore.Add(userNameCredential, null);
        }
        public UserNameCredentials(string userName)
        {
            credentialsStore.Add(userNameCredential, userName);
        }
        public string UserName
        {
            get { return this[userNameCredential]; }
            set { this[userNameCredential] = value; }
        }
        public string Identifier
        {
            get { return UserName; }
        }
        public string this[string credential]
        {
            get
            {
                string key = credential.ToLowerInvariant();
                if (credentialsStore.ContainsKey(key))
                {
                    return credentialsStore[key];
                }
                else
                {
                    return null;
                }
            }
            set
            {
                string key = credential.ToLowerInvariant();
                if ((value == null) && (key != userNameCredential))
                {
                    if (credentialsStore.ContainsKey(key)) credentialsStore.Remove(key);
                }
                else
                {
                    if (credentialsStore.ContainsKey(key))
                    {
                        credentialsStore[key] = value;
                    }
                    else
                    {
                        credentialsStore.Add(key, value);
                    }
                }
            }
        }
        public string Serialise()
        {
            XmlDocument document = new XmlDocument();
            XmlElement rootNode = document.CreateElement("credentials");
            document.AppendChild(rootNode);
            foreach (string key in credentialsStore.Keys)
            {
                XmlElement keyNode = document.CreateElement("value");
                string value = credentialsStore[key];
                if (value == null) value = string.Empty;
                keyNode.SetAttribute("key", key);
                keyNode.SetAttribute("value", value);
                rootNode.AppendChild(keyNode);
            }
            return document.OuterXml;
        }
        public void Deserialise(string credentials)
        {
            XmlDocument document = new XmlDocument();
            document.LoadXml(credentials);
            if (credentialsStore == null)
            {
                credentialsStore = new Dictionary<string, string>();
            }
            else
            {
                credentialsStore.Clear();
            }
            credentialsStore.Add(userNameCredential, null);
            foreach (XmlElement keyNode in document.SelectNodes("/credentials/value"))
            {
                string key = keyNode.GetAttribute("key");
                string value = keyNode.GetAttribute("value");
                this[key] = value;
            }
        }
        public string[] Credentials
        {
            get
            {
                string[] credentials = new string[credentialsStore.Count];
                credentialsStore.Keys.CopyTo(credentials, 0);
                return credentials;
            }
        }
        [OnSerializing()]
        internal void OnSerializingMethod(StreamingContext context)
        {
            asXml = Serialise();
        }
        [OnDeserialized()]
        internal void OnDeserializedMethod(StreamingContext context)
        {
            Deserialise(asXml);
        }
    }
}
