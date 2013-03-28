namespace ThoughtWorks.CruiseControl.Core.Util
{
    using System;
    using System.Collections.Generic;
    using System.Text;
    public class PrivateArguments
        : IPrivateData
    {
        private List<PrivateArgument> arguments = new List<PrivateArgument>();
        public PrivateArguments(params object[] args)
        {
            foreach (var arg in args)
            {
                this.Add(arg);
            }
        }
        public int Count
        {
            get { return arguments.Count; }
        }
        public override string ToString()
        {
            return this.ToString(SecureDataMode.Public);
        }
        public string ToString(SecureDataMode dataMode)
        {
            var builder = new StringBuilder();
            foreach (var argument in this.arguments)
            {
                var arg = argument.ToString(dataMode);
                if (arg.Length > 0)
                {
                    builder.Append(arg + " ");
                }
            }
            if (builder.Length > 0)
            {
                builder.Remove(builder.Length - 1, 1);
            }
            return builder.ToString();
        }
        public void Add(object value)
        {
            this.Add(null, value, false);
        }
        public void Add(string prefix, object value)
        {
            this.Add(prefix, value, false);
        }
        public void Add(string prefix, object value, bool doubleQuote)
        {
            this.arguments.Add(
                new PrivateArgument(prefix, value, doubleQuote));
        }
        public void AddIf(bool condition, object value)
        {
            if (condition)
            {
                this.Add(null, value, false);
            }
        }
        public void AddIf(bool condition, string prefix, object value)
        {
            if (condition)
            {
                this.Add(prefix, value, false);
            }
        }
        public void AddIf(bool condition, string prefix, object value, bool doubleQuote)
        {
            if (condition)
            {
                this.Add(prefix, value, doubleQuote);
            }
        }
        public void AddQuote(object value)
        {
            this.Add(null, "\"" + (value == null ? string.Empty : value.ToString()) + "\"", false);
        }
        public void AddQuote(string prefix, object value)
        {
            this.Add(prefix, "\"" + (value == null ? string.Empty : value.ToString()) + "\"", false);
        }
        public static implicit operator PrivateArguments(string args)
        {
            return new PrivateArguments(args);
        }
        public static PrivateArguments operator +(PrivateArguments args, object value)
        {
            args.Add(value);
            return args;
        }
        private class PrivateArgument
        {
            private readonly object prefix;
            private readonly object value;
            private readonly bool doubleQuote;
            public PrivateArgument(string prefix, object value, bool doubleQuote)
            {
                this.prefix = prefix;
                this.value = value;
                this.doubleQuote = doubleQuote;
            }
            public string ToString(SecureDataMode dataMode)
            {
                var privateValue = this.value as IPrivateData;
                var actualValue = privateValue == null ? (this.value ?? string.Empty).ToString() : privateValue.ToString(dataMode);
                if (this.doubleQuote)
                {
                    actualValue = StringUtil.AutoDoubleQuoteString(actualValue);
                }
                return (this.prefix ?? null) + actualValue;
            }
        }
    }
}
