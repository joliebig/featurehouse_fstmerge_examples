namespace Validator
{
    using System;
    using System.Collections.Generic;
    using System.Text;
    using System.ComponentModel;
    using System.Reflection;
    using Exortech.NetReflector;
    using ThoughtWorks.CruiseControl.Core.Util;
    public class ConfigurationTypeDescriptor
        : ICustomTypeDescriptor
    {
        private readonly PropertyDescriptorCollection properties;
        private readonly object value;
        public ConfigurationTypeDescriptor(object value)
        {
            this.value = value;
            var descriptors = new List<PropertyDescriptor>();
            if (value != null)
            {
                var type = this.value.GetType();
                var fields = type.GetFields();
                foreach (var field in fields)
                {
                    var name = this.GetReflectionName(field);
                    if (name != null)
                    {
                        descriptors.Add(new FieldPropertyDescriptor(name, field, this.value));
                    }
                }
                var properties = type.GetProperties();
                foreach (var property in properties)
                {
                    var name = this.GetReflectionName(property);
                    if (name != null)
                    {
                        descriptors.Add(new PropertyPropertyDescriptor(name, property, this.value));
                    }
                }
            }
            this.properties = new PropertyDescriptorCollection(descriptors.ToArray(), true);
        }
        public AttributeCollection GetAttributes()
        {
            return TypeDescriptor.GetAttributes(this.value, true);
        }
        public string GetClassName()
        {
            return TypeDescriptor.GetClassName(this.value, true);
        }
        public string GetComponentName()
        {
            return TypeDescriptor.GetComponentName(this.value, true);
        }
        public TypeConverter GetConverter()
        {
            return TypeDescriptor.GetConverter(this.value, true);
        }
        public EventDescriptor GetDefaultEvent()
        {
            return TypeDescriptor.GetDefaultEvent(this.value, true);
        }
        public PropertyDescriptor GetDefaultProperty()
        {
            return null;
        }
        public object GetEditor(Type editorBaseType)
        {
            return TypeDescriptor.GetEditor(this.value, editorBaseType, true);
        }
        public EventDescriptorCollection GetEvents(Attribute[] attributes)
        {
            return TypeDescriptor.GetEvents(this.value, attributes, true);
        }
        public EventDescriptorCollection GetEvents()
        {
            return TypeDescriptor.GetEvents(this.value, true);
        }
        public PropertyDescriptorCollection GetProperties(Attribute[] attributes)
        {
            return this.GetProperties();
        }
        public PropertyDescriptorCollection GetProperties()
        {
            return this.properties;
        }
        public object GetPropertyOwner(PropertyDescriptor pd)
        {
            return this.value;
        }
        public override string ToString()
        {
            return this.GetReflectionName(this.value);
        }
        private string GetReflectionName(MemberInfo value)
        {
            string valueName = null;
            var attributes = value.GetCustomAttributes(true);
            foreach (var attribute in attributes)
            {
                var reflection = attribute as ReflectorPropertyAttribute;
                if (reflection != null)
                {
                    valueName = reflection.Name;
                    break;
                }
            }
            return valueName;
        }
        private string GetReflectionName(object value)
        {
            string valueName = string.Empty;
            var reflection = value.GetType().GetCustomAttributes(typeof(ReflectorTypeAttribute), true);
            if (reflection.Length > 0)
            {
                valueName = (reflection[0] as ReflectorTypeAttribute).Name;
            }
            return valueName;
        }
        public abstract class PropertyDescriptorBase
            : PropertyDescriptor
        {
            public PropertyDescriptorBase(string name, Attribute[] attributes, object value)
                : base(name, attributes)
            {
                this.Value = value;
            }
            public object Value { get; private set; }
            public override bool IsReadOnly
            {
                get { return true; }
            }
            public override void ResetValue(object component)
            {
            }
            public override bool CanResetValue(object component)
            {
                return false;
            }
            public override bool ShouldSerializeValue(object component)
            {
                return false;
            }
            public override void SetValue(object component, object value)
            {
            }
            public object WrapValue(object value)
            {
                if (value == null)
                {
                    return value;
                }
                else
                {
                    var type = value.GetType();
                    if (type.IsPrimitive || (type == typeof(string)) || (type == typeof(PrivateString)) || type.IsArray)
                    {
                        return value;
                    }
                    else
                    {
                        return new ConfigurationTypeDescriptor(value);
                    }
                }
            }
        }
        public class FieldPropertyDescriptor
            : PropertyDescriptorBase
        {
            public FieldPropertyDescriptor(string name, FieldInfo field, object value)
                : base(name, (Attribute[])field.GetCustomAttributes(typeof(Attribute), true), value)
            {
                this.Field = field;
            }
            public FieldInfo Field { get; private set; }
            public override Type ComponentType
            {
                get { return this.Field.DeclaringType; }
            }
            public override Type PropertyType
            {
                get { return this.Field.FieldType; }
            }
            public override object GetValue(object component)
            {
                return this.WrapValue(this.Field.GetValue(this.Value));
            }
        }
        public class PropertyPropertyDescriptor
            : PropertyDescriptorBase
        {
            public PropertyPropertyDescriptor(string name, PropertyInfo property, object value)
                : base(name, (Attribute[])property.GetCustomAttributes(typeof(Attribute), true), value)
            {
                this.Property = property;
            }
            public PropertyInfo Property { get; private set; }
            public override Type ComponentType
            {
                get { return this.Property.DeclaringType; }
            }
            public override Type PropertyType
            {
                get { return this.Property.PropertyType; }
            }
            public override object GetValue(object component)
            {
                return this.WrapValue(this.Property.GetValue(this.Value, new object[0]));
            }
        }
    }
}
