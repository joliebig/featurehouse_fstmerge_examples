namespace ThoughtWorks.CruiseControl.Core
{
    using System;
    internal static class SecurityOverride
    {
        static SecurityOverride()
        {
            SessionIdentifier = Guid.NewGuid().ToString();
        }
        internal static string SessionIdentifier { get; private set; }
    }
}
