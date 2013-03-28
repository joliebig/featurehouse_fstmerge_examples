namespace ThoughtWorks.CruiseControl.Core.Util
{
    using System;
    using System.ComponentModel;
    using System.Globalization;
    public class PrivateStringTypeConverter
        : TypeConverter
    {
        public override bool CanConvertFrom(ITypeDescriptorContext context, Type sourceType)
        {
            return sourceType == typeof(string);
        }
        public override object ConvertFrom(ITypeDescriptorContext context, CultureInfo culture, object value)
        {
            object convertedValue = null;
            if (value is string)
            {
                convertedValue = new PrivateString()
                {
                    PrivateValue = (string)value
                };
            }
            return convertedValue;
        }
    }
}
