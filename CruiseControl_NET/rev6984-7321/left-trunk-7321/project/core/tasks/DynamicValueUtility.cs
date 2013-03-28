namespace ThoughtWorks.CruiseControl.Core.Tasks
{
    using System;
    using System.Collections;
    using System.Collections.Generic;
    using System.Reflection;
    using System.Text;
    using System.Text.RegularExpressions;
    using System.Xml;
    using Exortech.NetReflector;
    using ThoughtWorks.CruiseControl.Remote.Parameters;
    using System.ComponentModel;
    public static class DynamicValueUtility
    {
        private static Regex parameterRegex = new Regex(@"\$\[[^\]]*\]", RegexOptions.Compiled);
        private static Regex paramPartRegex = new Regex(@"(?<!\\)\|", RegexOptions.Compiled);
        public static PropertyValue FindProperty(object value, string property)
        {
            PropertyValue actualProperty = null;
            object currentValue = value;
            MemberInfo currentProperty = null;
            int lastIndex = 0;
            if (value != null)
            {
                PropertyPart[] parts = SplitPropertyName(property);
                int position = 0;
                while ((position < parts.Length) && (currentValue != null))
                {
                    actualProperty = null;
                    if (currentProperty != null)
                    {
                        currentValue = GetValue(currentProperty, currentValue);
                        currentProperty = null;
                    }
                    if (currentValue is IEnumerable)
                    {
                        currentValue = FindTypedValue(currentValue as IEnumerable, parts[position].Name);
                    }
                    else
                    {
                        currentProperty = FindActualProperty(currentValue, parts[position].Name);
                        if (!string.IsNullOrEmpty(parts[position].KeyName))
                        {
                            IEnumerable values = GetValue(currentProperty, currentValue) as IEnumerable;
                            currentValue = FindKeyedValue(values, parts[position].KeyName, parts[position].KeyValue);
                            currentProperty = null;
                        }
                        else if (parts[position].Index >= 0)
                        {
                            Array values = GetValue(currentProperty, currentValue) as Array;
                            if (values != null)
                            {
                                lastIndex = parts[position].Index;
                                if (lastIndex < values.GetLength(0))
                                {
                                    actualProperty = new PropertyValue(currentValue, currentProperty, lastIndex);
                                    currentValue = values.GetValue(lastIndex);
                                    currentProperty = null;
                                }
                            }
                            currentProperty = null;
                        }
                    }
                    position++;
                }
                if (currentProperty != null)
                {
                    actualProperty = new PropertyValue(currentValue, currentProperty, lastIndex);
                }
            }
            return actualProperty;
        }
        public static MemberInfo FindActualProperty(object value, string reflectorProperty)
        {
            MemberInfo actualProperty = null;
            if (value != null)
            {
                List<MemberInfo> allProperties = new List<MemberInfo>(value.GetType().GetProperties());
                allProperties.AddRange(value.GetType().GetFields());
                foreach (MemberInfo property in allProperties)
                {
                    object[] attributes = property.GetCustomAttributes(true);
                    foreach (object attribute in attributes)
                    {
                        string name = null;
                        if (attribute is ReflectorPropertyAttribute)
                        {
                            name = (attribute as ReflectorPropertyAttribute).Name;
                        }
                        if (name == reflectorProperty)
                        {
                            actualProperty = property;
                            break;
                        }
                    }
                    if (actualProperty != null) break;
                }
            }
            return actualProperty;
        }
        public static object FindTypedValue(IEnumerable values, string typeName)
        {
            object actualValue = null;
            foreach (object value in values)
            {
                object[] attributes = value.GetType().GetCustomAttributes(true);
                foreach (object attribute in attributes)
                {
                    if (attribute is ReflectorTypeAttribute)
                    {
                        string name = (attribute as ReflectorTypeAttribute).Name;
                        if (name == typeName)
                        {
                            actualValue = value;
                            break;
                        }
                    }
                }
            }
            return actualValue;
        }
        public static object FindKeyedValue(IEnumerable values, string keyName, string keyValue)
        {
            object actualValue = null;
            if (values != null)
            {
                foreach (object value in values)
                {
                    MemberInfo property = FindActualProperty(value, keyName);
                    if (property != null)
                    {
                        object propertyValue = GetValue(property, value);
                        if (propertyValue != null)
                        {
                            if (string.Equals(keyValue, propertyValue.ToString()))
                            {
                                actualValue = value;
                                break;
                            }
                        }
                    }
                }
            }
            return actualValue;
        }
        public static PropertyPart[] SplitPropertyName(string propertyName)
        {
            string[] parts = propertyName.Split('.');
            List<PropertyPart> results = new List<PropertyPart>();
            foreach (string part in parts)
            {
                PropertyPart newPart = new PropertyPart();
                int keyIndex = part.IndexOf('[');
                if (keyIndex >= 0)
                {
                    newPart.Name = part.Substring(0, keyIndex);
                    string key = part.Substring(keyIndex + 1);
                    int equalsIndex = key.IndexOf('=');
                    if (equalsIndex >= 0)
                    {
                        newPart.KeyName = key.Substring(0, equalsIndex);
                        newPart.KeyValue = key.Substring(equalsIndex + 1);
                        newPart.KeyValue = newPart.KeyValue.Remove(newPart.KeyValue.Length - 1);
                    }
                    else
                    {
                        int index;
                        if (int.TryParse(key.Substring(0, key.Length - 1), out index))
                        {
                            newPart.Index = index;
                        }
                    }
                }
                else
                {
                    newPart.Name = part;
                }
                results.Add(newPart);
            }
            return results.ToArray();
        }
        public static object ConvertValue(string parameterName, string inputValue, IEnumerable<ParameterBase> parameterDefinitions)
        {
            object actualValue = inputValue;
            if (parameterDefinitions != null)
            {
                foreach (var parameter in parameterDefinitions)
                {
                    if (parameter.Name == parameterName)
                    {
                        try
                        {
                            if (inputValue == null)
                            {
                                actualValue = parameter.Convert(parameter.DefaultValue);
                            }
                            else
                            {
                                actualValue = parameter.Convert(inputValue);
                            }
                        }
                        catch (InvalidCastException)
                        {
                            if (actualValue == null)
                            {
                                throw new CruiseControlException(
                                    "Unable to set dynamic value for " + parameterName + ", unable to find a valid default value");
                            }
                            else
                            {
                                throw new CruiseControlException(
                                    "Unable to set dynamic value for " + parameterName + ", unable to convert value");
                            }
                        }
                        break;
                    }
                }
            }
            return actualValue;
        }
        public static XmlNode ConvertXmlToDynamicValues(NetReflectorTypeTable typeTable, XmlNode inputNode, params string[] exclusions)
        {
            var resultNode = inputNode;
            var doc = inputNode.OwnerDocument;
            var parameters = new List<XmlElement>();
            var inputNodeType = typeTable.ContainsType(inputNode.Name) ? typeTable[inputNode.Name] : null;
            var inputMembers = new Dictionary<string, XmlMemberSerialiser>();
            if (inputNodeType != null)
            {
                foreach (XmlMemberSerialiser value in inputNodeType.MemberSerialisers)
                {
                    if (value != null)
                    {
                        inputMembers.Add(value.Attribute.Name, value);
                    }
                }
            }
            var nodes = inputNode.SelectNodes("descendant::text()|descendant-or-self::*[@*]/@*");
            foreach (XmlNode nodeWithParam in nodes)
            {
                var text = nodeWithParam.Value;
                var isExcluded = CheckForExclusion(nodeWithParam, exclusions);
                if (!isExcluded && parameterRegex.Match(text).Success)
                {
                    var parametersEl = doc.CreateElement("parameters");
                    var index = 0;
                    var lastReplacement = string.Empty;
                    var format = parameterRegex.Replace(text, (match) =>
                    {
                        var parts = paramPartRegex.Split(match.Value.Substring(2, match.Value.Length - 3));
                        var dynamicValueEl = doc.CreateElement("namedValue");
                        dynamicValueEl.SetAttribute("name", parts[0].Replace("\\|", "|"));
                        if (parts.Length > 1)
                        {
                            dynamicValueEl.SetAttribute("value", parts[1].Replace("\\|", "|"));
                        }
                        parametersEl.AppendChild(dynamicValueEl);
                        lastReplacement = string.Format("{{{0}{1}}}",
                            index++,
                            parts.Length > 2 ? ":" + parts[2].Replace("\\|", "|") : string.Empty);
                        return lastReplacement;
                    });
                    var replacementValue = string.Empty;
                    XmlElement replacementEl;
                    if (lastReplacement != format)
                    {
                        replacementEl = doc.CreateElement("replacementValue");
                        AddElement(replacementEl, "format", format);
                        replacementEl.AppendChild(parametersEl);
                    }
                    else
                    {
                        replacementEl = doc.CreateElement("directValue");
                        AddElement(replacementEl, "parameter", parametersEl.SelectSingleNode("namedValue/@name").InnerText);
                        var innerValue = parametersEl.SelectSingleNode("namedValue/@value");
                        if (innerValue != null)
                        {
                            replacementValue = innerValue.InnerText;
                            AddElement(replacementEl, "default", replacementValue);
                        }
                    }
                    parameters.Add(replacementEl);
                    var propertyName = new StringBuilder();
                    var currentNode = (nodeWithParam is XmlAttribute) ? nodeWithParam : nodeWithParam.ParentNode;
                    var previousNode = currentNode;
                    var lastName = string.Empty;
                    while ((currentNode != inputNode) && (currentNode != null))
                    {
                        var nodeName = currentNode.Name;
                        if (inputMembers.ContainsKey(nodeName) && inputMembers[nodeName].ReflectorMember.MemberType.IsArray)
                        {
                            propertyName.Remove(0, lastName.Length + 1);
                            var position = 0;
                            var indexNode = previousNode;
                            while (indexNode.PreviousSibling != null)
                            {
                                position++;
                                indexNode = indexNode.PreviousSibling;
                            }
                            propertyName.Insert(0, "." + nodeName + "[" + position.ToString() + "]");
                        }
                        else
                        {
                            propertyName.Insert(0, "." + nodeName);
                        }
                        lastName = nodeName;
                        previousNode = currentNode;
                        if (currentNode is XmlAttribute)
                        {
                            currentNode = (currentNode as XmlAttribute).OwnerElement;
                        }
                        else
                        {
                            currentNode = currentNode.ParentNode;
                        }
                    }
                    propertyName.Remove(0, 1);
                    AddElement(replacementEl, "property", propertyName.ToString());
                    nodeWithParam.Value = replacementValue;
                }
            }
            if (parameters.Count > 0)
            {
                var parametersEl = inputNode.SelectSingleNode("dynamicValues");
                if (parametersEl == null)
                {
                    parametersEl = doc.CreateElement("dynamicValues");
                    inputNode.AppendChild(parametersEl);
                }
                foreach (var element in parameters)
                {
                    parametersEl.AppendChild(element);
                }
            }
            return resultNode;
        }
        private static object GetValue(MemberInfo member, object source)
        {
            object value = null;
            if (member is PropertyInfo)
            {
                value = (member as PropertyInfo).GetValue(source, new object[0]);
            }
            else
            {
                value = (member as FieldInfo).GetValue(source);
            }
            return value;
        }
        private static void AddElement(XmlElement parent, string name, string value)
        {
            var element = parent.OwnerDocument.CreateElement(name);
            element.InnerText = value;
            parent.AppendChild(element);
        }
        private static bool CheckForExclusion(XmlNode node, string[] exclusions)
        {
            var isExcluded = false;
            if ((exclusions != null) && (exclusions.Length > 0))
            {
                foreach (var exclusion in exclusions)
                {
                    var ancestor = node.SelectSingleNode("ancestor::" + exclusion + "[1]");
                    if (ancestor != null)
                    {
                        isExcluded = true;
                        break;
                    }
                }
            }
            return isExcluded;
        }
        public class PropertyValue
        {
            private object mySource;
            private MemberInfo myProperty;
            private int myArrayIndex;
            internal PropertyValue(object source, MemberInfo property, int arrayIndex)
            {
                this.mySource = source;
                this.myProperty = property;
                this.myArrayIndex = arrayIndex;
            }
            public object Source
            {
                get { return mySource; }
            }
            public MemberInfo Property
            {
                get { return myProperty; }
            }
            public object Value
            {
                get
                {
                    if (myProperty is PropertyInfo)
                    {
                        return (myProperty as PropertyInfo).GetValue(mySource, new object[0]);
                    }
                    else
                    {
                        return (myProperty as FieldInfo).GetValue(mySource);
                    }
                }
            }
            public void ChangeProperty(object value)
            {
                if (myProperty is PropertyInfo)
                {
                    ChangePropertyValue(value);
                }
                else
                {
                    ChangeFieldValue(value);
                }
            }
            private void ChangePropertyValue(object value)
            {
                object actualValue = value;
                object[] index = new object[0];
                PropertyInfo property = (myProperty as PropertyInfo);
                if (property.PropertyType.IsArray)
                {
                    index = new object[] {
                                myArrayIndex
                            };
                    if (property.PropertyType.GetElementType() != value.GetType())
                    {
                        actualValue = Convert.ChangeType(value, property.PropertyType.GetElementType());
                    }
                }
                else
                {
                    if ((value != null) && (property.PropertyType != value.GetType()))
                    {
                        var converters = property.GetCustomAttributes(typeof(TypeConverterAttribute), true);
                        if (converters.Length == 0)
                        {
                            converters = property.PropertyType.GetCustomAttributes(typeof(TypeConverterAttribute), true);
                        }
                        var converted = false;
                        if (converters.Length > 0)
                        {
                            var valueType = value.GetType();
                            foreach (TypeConverterAttribute converterAttrib in converters)
                            {
                                var converter = Activator.CreateInstance(Type.GetType(converterAttrib.ConverterTypeName)) as TypeConverter;
                                if (converter.CanConvertFrom(valueType))
                                {
                                    actualValue = converter.ConvertFrom(value);
                                    converted = true;
                                    break;
                                }
                            }
                        }
                        if (!converted)
                    {
                        actualValue = Convert.ChangeType(value, property.PropertyType);
                        }
                    }
                }
                property.SetValue(mySource, actualValue, index);
            }
            private void ChangeFieldValue(object value)
            {
                FieldInfo property = (myProperty as FieldInfo);
                object actualValue = value;
                if (property.FieldType.IsArray)
                {
                    if (property.FieldType.GetElementType() != value.GetType())
                    {
                        actualValue = Convert.ChangeType(value, property.FieldType);
                    }
                    Array array = property.GetValue(mySource) as Array;
                    array.SetValue(actualValue, myArrayIndex);
                }
                else
                {
                    if (property.FieldType != value.GetType())
                    {
                        actualValue = Convert.ChangeType(value, property.FieldType);
                    }
                    property.SetValue(mySource, actualValue);
                }
            }
        }
        public class PropertyPart
        {
            public string Name;
            public string KeyName;
            public string KeyValue;
            public int Index = -1;
        }
    }
}
