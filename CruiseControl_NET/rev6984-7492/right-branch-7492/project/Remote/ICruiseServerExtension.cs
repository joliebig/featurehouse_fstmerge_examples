using System;
namespace ThoughtWorks.CruiseControl.Remote
{
    public interface ICruiseServerExtension
    {
        void Initialise(ICruiseServer server, ExtensionConfiguration extensionConfig);
        void Start();
        void Stop();
        void Abort();
    }
}
