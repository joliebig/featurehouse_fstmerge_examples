using System;
using System.IO;
using ThoughtWorks.CruiseControl.Core.Sourcecontrol;
using ThoughtWorks.CruiseControl.Core.Util;
using ThoughtWorks.CruiseControl.Remote;
namespace ThoughtWorks.CruiseControl.Core
{
    public class IntegrationRunner : IIntegratable
    {
        public IIntegrationRunnerTarget target;
        private readonly IIntegrationResultManager resultManager;
        private readonly IQuietPeriod quietPeriod;
        public IntegrationRunner(IIntegrationResultManager resultManager, IIntegrationRunnerTarget target, IQuietPeriod quietPeriod)
        {
            this.target = target;
            this.quietPeriod = quietPeriod;
            this.resultManager = resultManager;
        }
        public IIntegrationResult Integrate(IntegrationRequest request)
        {
            Log.Trace();
            this.target.InitialiseForBuild(request);
            IIntegrationResult result = resultManager.StartNewIntegration(request);
            IIntegrationResult lastResult = resultManager.LastIntegrationResult;
            CreateDirectoryIfItDoesntExist(result.WorkingDirectory);
            CreateDirectoryIfItDoesntExist(result.ArtifactDirectory);
            if ((request.BuildValues != null) && (request.BuildValues.Count > 0))
            {
                result.Parameters.AddRange(
                    NameValuePair.FromDictionary(request.BuildValues));
            }
            result.MarkStartTime();
            this.GenerateSystemParameterValues(result);
            Log.Trace("Getting Modifications for project {0}", result.ProjectName);
            try
            {
                result.Modifications = GetModifications(lastResult, result);
            }
            catch (Exception error)
            {
                result.SourceControlError = error;
                result.LastBuildStatus = lastResult.HasSourceControlError ? lastResult.LastBuildStatus : lastResult.Status;
                Log.Warning(string.Format("Source control failure (GetModifications): {0}", error.Message));
                if (request.PublishOnSourceControlException)
                {
                    result.ExceptionResult = error;
                    CompleteIntegration(result);
                }
            }
            var runBuild = false;
            try
            {
                runBuild = (result.SourceControlError == null) && result.ShouldRunBuild();
                if (runBuild)
                {
                    Log.Info("Building: " + request);
                    target.ClearNotNeededMessages();
                    Log.Trace("Creating Label for project {0}", result.ProjectName);
                    if (result.LastIntegrationStatus == IntegrationStatus.Exception)
                    {
                        IntegrationSummary isExceptionFix = new IntegrationSummary(IntegrationStatus.Success, result.LastIntegration.Label, result.LastIntegration.LastSuccessfulIntegrationLabel, result.LastIntegration.StartTime);
                        IIntegrationResult irExceptionFix = new IntegrationResult(result.ProjectName, result.WorkingDirectory, result.ArtifactDirectory, result.IntegrationRequest, isExceptionFix);
                        irExceptionFix.Modifications = result.Modifications;
                        target.CreateLabel(irExceptionFix);
                        result.Label = irExceptionFix.Label;
                    }
                    else
                    {
                        target.CreateLabel(result);
                    }
                    Log.Trace("Running tasks of project {0}", result.ProjectName);
                    this.GenerateSystemParameterValues(result);
                    Build(result);
                }
                else if (lastResult.HasSourceControlError)
                {
                    result.Status = lastResult.LastBuildStatus;
                    resultManager.FinishIntegration();
                }
            }
            catch (Exception ex)
            {
                Log.Debug("Exception caught: " + ex.Message);
                result.ExceptionResult = ex;
            }
            finally
            {
                if (runBuild)
                {
                    CompleteIntegration(result);
                }
            }
            this.target.Activity = ProjectActivity.Sleeping;
            return result;
        }
        public void GenerateSystemParameterValues(IIntegrationResult result)
        {
            var props = result.IntegrationProperties;
            foreach (var property in props.Keys)
            {
                var key = string.Format("${0}", property);
                var value = (props[property] ?? string.Empty).ToString();
                result.IntegrationRequest.BuildValues[key] = value;
                var namedValue = new NameValuePair(key, value);
                if (result.Parameters.Contains(namedValue))
                {
                    var index = result.Parameters.IndexOf(namedValue);
                    result.Parameters[index] = namedValue;
                }
                else
                {
                    result.Parameters.Add(namedValue);
                }
            }
        }
        private void CompleteIntegration(IIntegrationResult result)
        {
            result.MarkEndTime();
            PostBuild(result);
            Log.Info(string.Format("Integration complete: {0} - {1}", result.Status, result.EndTime));
        }
        private Modification[] GetModifications(IIntegrationResult from_, IIntegrationResult to)
        {
            target.Activity = ProjectActivity.CheckingModifications;
            to.BuildProgressInformation.SignalStartRunTask("Getting source ... ");
            target.RecordSourceControlOperation(SourceControlOperation.CheckForModifications, ItemBuildStatus.Running);
            bool success = false;
            try
            {
                Modification[] modifications = quietPeriod.GetModifications(target.SourceControl, from_, to);
                success = true;
                return modifications;
            }
            finally
            {
                target.RecordSourceControlOperation(SourceControlOperation.CheckForModifications,
                    success ? ItemBuildStatus.CompletedSuccess : ItemBuildStatus.CompletedFailed);
            }
        }
        private void Build(IIntegrationResult result)
        {
            target.Activity = ProjectActivity.Building;
            target.Prebuild(result);
            if (!result.Failed)
            {
                bool success = false;
                target.RecordSourceControlOperation(SourceControlOperation.GetSource, ItemBuildStatus.Running);
                try
                {
                    target.SourceControl.GetSource(result);
                    success = true;
                }
                finally
                {
                    target.RecordSourceControlOperation(SourceControlOperation.GetSource,
                        success ? ItemBuildStatus.CompletedSuccess : ItemBuildStatus.CompletedFailed);
                }
                target.Run(result);
                target.SourceControl.LabelSourceControl(result);
            }
        }
        public virtual void PostBuild(IIntegrationResult result)
        {
            resultManager.FinishIntegration();
            Log.Trace("Running publishers");
            target.PublishResults(result);
        }
        private static void CreateDirectoryIfItDoesntExist(string directory)
        {
            if (!Directory.Exists(directory))
                Directory.CreateDirectory(directory);
        }
    }
}
