namespace ThoughtWorks.CruiseControl.UnitTests.IntegrationTests
{
    using System;
    public class HarnessException
        : Exception
    {
        public HarnessException()
            : base()
        {
        }
        public HarnessException(string message)
            : base(message)
        {
        }
        public HarnessException(string message, Exception innerException)
            : base(message, innerException)
        {
        }
    }
}
