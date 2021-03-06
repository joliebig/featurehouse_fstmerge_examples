using System;
using ThoughtWorks.CruiseControl.Remote;
namespace ThoughtWorks.CruiseControl.UnitTests.Remote
{
    public class ServerExtensionStub
        : ICruiseServerExtension
    {
        public static bool HasInitialised;
        public static bool HasStarted;
        public static bool HasStopped;
        public static bool HasAborted;
        public void Initialise(ICruiseServer server, ExtensionConfiguration extensionConfig)
        {
            HasInitialised = true;
        }
        public void Start()
        {
            HasStarted = true;
        }
        public void Stop()
        {
            HasStopped = true;
        }
        public void Abort()
        {
            HasAborted = true;
        }
        public void WaitForExit()
        {
        }
    }
}
