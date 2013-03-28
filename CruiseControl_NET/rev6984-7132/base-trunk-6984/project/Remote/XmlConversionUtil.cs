using System;
using System.Collections.Generic;
using System.Text;
using System.Xml.Serialization;
using System.Xml;
using ThoughtWorks.CruiseControl.Remote.Messages;
using System.Reflection;
using System.IO;
namespace ThoughtWorks.CruiseControl.Remote
{
    public static class XmlConversionUtil
    {
        private static Dictionary<string, Type> messageTypes = null;
        private static Dictionary<Type, XmlSerializer> messageSerialisers = new Dictionary<Type, XmlSerializer>();
        public static Response ProcessResponse(string response)
        {
            XmlDocument messageXml = new XmlDocument();
            messageXml.LoadXml(response);
            Type messageType = FindMessageType(messageXml.DocumentElement.Name);
            if (messageType == null)
            {
                throw new CommunicationsException(
                    string.Format(
                        "Unable to translate message: '{0}' is unknown",
                        messageXml.DocumentElement.Name));
            }
            object result = ConvertXmlToObject(messageType, response);
            return result as Response;
        }
        public static Type FindMessageType(string messageName)
        {
            Type messageType = null;
            if (messageTypes == null)
            {
                messageTypes = new Dictionary<string, Type>();
                Assembly remotingLibrary = typeof(IServerConnection).Assembly;
                foreach (Type remotingType in remotingLibrary.GetExportedTypes())
                {
                    XmlRootAttribute[] attributes = remotingType.GetCustomAttributes(
                        typeof(XmlRootAttribute), false) as XmlRootAttribute[];
                    foreach (XmlRootAttribute attribute in attributes)
                    {
                        if (messageTypes.ContainsKey(attribute.ElementName))
                        {
                            throw new ApplicationException(
                                string.Format("Duplicate message type found: '{0}'.\r\nFirst type: {1}\r\nSecond type: {2}",
                                    attribute.ElementName,
                                    messageTypes[attribute.ElementName].FullName,
                                    remotingType.FullName));
                        }
                        else
                        {
                            messageTypes.Add(attribute.ElementName, remotingType);
                        }
                    }
                }
            }
            if (messageTypes.ContainsKey(messageName))
            {
                messageType = messageTypes[messageName];
            }
            return messageType;
        }
        public static object ConvertXmlToObject(Type messageType, string message)
        {
            object messageObj = null;
            if (!messageSerialisers.ContainsKey(messageType))
            {
                messageSerialisers[messageType] = new XmlSerializer(messageType);
            }
            using (StringReader reader = new StringReader(message))
            {
                messageObj = messageSerialisers[messageType].Deserialize(reader);
            }
            return messageObj;
        }
        public static ServerRequest ConvertXmlToRequest(string message)
        {
            var messageXml = new XmlDocument();
            messageXml.LoadXml(message);
            var messageType = XmlConversionUtil.FindMessageType(messageXml.DocumentElement.Name);
            if (messageType == null) throw new CommunicationsException("Unknown message type");
            var request = ConvertXmlToObject(messageType, message) as ServerRequest;
            return request;
        }
    }
}
