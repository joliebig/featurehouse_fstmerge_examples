namespace ThoughtWorks.CruiseControl.Core.Util
{
    using System.ComponentModel;
    [TypeConverter(typeof(PrivateStringTypeConverter))]
    public sealed class PrivateString
        : IPrivateData
    {
        public string PrivateValue { get; set; }
        public string PublicValue
        {
            get { return "********"; }
        }
        public override string ToString()
        {
            return this.ToString(SecureDataMode.Public);
        }
        public string ToString(SecureDataMode dataMode)
        {
            switch (dataMode)
            {
                case SecureDataMode.Private:
                    return this.PrivateValue;
                default:
                    return this.PublicValue;
            }
        }
        public static implicit operator PrivateString(string args)
        {
            return new PrivateString() { PrivateValue = args };
        }
    }
}
