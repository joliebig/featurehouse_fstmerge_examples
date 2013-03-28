namespace ThoughtWorks.CruiseControl.Core.Distribution
{
    using System.ServiceModel;
    using ThoughtWorks.CruiseControl.Core.Distribution.Messages;
    [ServiceContract(Namespace = "http://http://thoughtworks.org/ccnet/remote/1/6")]
    public interface IRemoteBuildService
    {
        [OperationContract]
        CheckIfBuildCanRunResponse CheckIfBuildCanRun(CheckIfBuildCanRunRequest request);
        [OperationContract]
        StartBuildResponse StartBuild(StartBuildRequest request);
        [OperationContract]
        CancelBuildResponse CancelBuild(CancelBuildRequest request);
        [OperationContract]
        RetrieveBuildStatusResponse RetrieveBuildStatus(RetrieveBuildStatusRequest request);
    }
}
