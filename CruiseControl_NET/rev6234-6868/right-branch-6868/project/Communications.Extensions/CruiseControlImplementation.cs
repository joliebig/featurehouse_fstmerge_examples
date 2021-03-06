namespace ThoughtWorks.CruiseControl.Remote
{
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.ServiceModel", "3.0.0.0")]
    [System.ServiceModel.ServiceContractAttribute(Namespace="http://ccnet.thoughtworks.com/1/5/extensions", ConfigurationName="ThoughtWorks.CruiseControl.Remote.ICruiseControlContract")]
    internal interface ICruiseControlContract
    {
        [System.ServiceModel.OperationContractAttribute(Action="http://ccnet.thoughtworks.com/1/5/extensions/ICruiseControlContract/ProcessMessag" +
            "e", ReplyAction="http://ccnet.thoughtworks.com/1/5/extensions/ICruiseControlContract/ProcessMessag" +
            "eResponse")]
        ThoughtWorks.CruiseControl.Remote.Messages.Response ProcessMessage(string action, ThoughtWorks.CruiseControl.Remote.Messages.ServerRequest message);
        [System.ServiceModel.OperationContractAttribute(AsyncPattern=true, Action="http://ccnet.thoughtworks.com/1/5/extensions/ICruiseControlContract/ProcessMessag" +
            "e", ReplyAction="http://ccnet.thoughtworks.com/1/5/extensions/ICruiseControlContract/ProcessMessag" +
            "eResponse")]
        System.IAsyncResult BeginProcessMessage(string action, ThoughtWorks.CruiseControl.Remote.Messages.ServerRequest message, System.AsyncCallback callback, object asyncState);
        ThoughtWorks.CruiseControl.Remote.Messages.Response EndProcessMessage(System.IAsyncResult result);
    }
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.ServiceModel", "3.0.0.0")]
    internal interface ICruiseControlContractChannel : ThoughtWorks.CruiseControl.Remote.ICruiseControlContract, System.ServiceModel.IClientChannel
    {
    }
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.ServiceModel", "3.0.0.0")]
    internal partial class CruiseControlContractClient : System.ServiceModel.ClientBase<ThoughtWorks.CruiseControl.Remote.ICruiseControlContract>, ThoughtWorks.CruiseControl.Remote.ICruiseControlContract
    {
        public CruiseControlContractClient()
        {
        }
        public CruiseControlContractClient(string endpointConfigurationName) :
                base(endpointConfigurationName)
        {
        }
        public CruiseControlContractClient(string endpointConfigurationName, string remoteAddress) :
                base(endpointConfigurationName, remoteAddress)
        {
        }
        public CruiseControlContractClient(string endpointConfigurationName, System.ServiceModel.EndpointAddress remoteAddress) :
                base(endpointConfigurationName, remoteAddress)
        {
        }
        public CruiseControlContractClient(System.ServiceModel.Channels.Binding binding, System.ServiceModel.EndpointAddress remoteAddress) :
                base(binding, remoteAddress)
        {
        }
        public ThoughtWorks.CruiseControl.Remote.Messages.Response ProcessMessage(string action, ThoughtWorks.CruiseControl.Remote.Messages.ServerRequest message)
        {
            return base.Channel.ProcessMessage(action, message);
        }
        public System.IAsyncResult BeginProcessMessage(string action, ThoughtWorks.CruiseControl.Remote.Messages.ServerRequest message, System.AsyncCallback callback, object asyncState)
        {
            return base.Channel.BeginProcessMessage(action, message, callback, asyncState);
        }
        public ThoughtWorks.CruiseControl.Remote.Messages.Response EndProcessMessage(System.IAsyncResult result)
        {
            return base.Channel.EndProcessMessage(result);
        }
    }
}
