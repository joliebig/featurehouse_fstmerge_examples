using Exortech.NetReflector;
using System;
using System.Reflection;
using System.Collections;
using System.Collections.Generic;
namespace ThoughtWorks.CruiseControl.Core.Util
{
    public static class DynamicValueUtility
    {
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
                object actualValue = value;
                if (myProperty is PropertyInfo)
                {
                    ChangePropertyValue(value, actualValue);
                }
                else
                {
                    actualValue = ChangeFieldValue(value, actualValue);
                }
            }
            private void ChangePropertyValue(object value, object actualValue)
            {
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
                    if (property.PropertyType != value.GetType())
                    {
                        actualValue = Convert.ChangeType(value, property.PropertyType);
                    }
                }
                property.SetValue(mySource, actualValue, index);
            }
            private object ChangeFieldValue(object value, object actualValue)
            {
                FieldInfo property = (myProperty as FieldInfo);
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
                return actualValue;
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
