using System;
using System.Collections;
using ThoughtWorks.CruiseControl.Remote;
namespace ThoughtWorks.CruiseControl.Core
{
 public interface IIntegrationResult
 {
  string ProjectName { get; }
  string ProjectUrl { get; set;}
  string WorkingDirectory { get; set; }
  string ArtifactDirectory { get; set;}
  string BaseFromArtifactsDirectory(string pathToBase);
  string BaseFromWorkingDirectory(string pathToBase);
        string BuildLogDirectory { get; set;}
  BuildCondition BuildCondition { get; }
  string Label { get; set; }
  IntegrationStatus Status { get; set; }
  DateTime StartTime { get; set; }
  DateTime EndTime { get; }
  TimeSpan TotalIntegrationTime { get; }
  bool Failed { get; }
  bool Fixed { get; }
  bool Succeeded { get; }
  void MarkStartTime();
  void MarkEndTime();
  bool IsInitial();
  IntegrationRequest IntegrationRequest { get; }
  IntegrationStatus LastIntegrationStatus { get; }
        ArrayList FailureUsers { get; }
  DateTime LastModificationDate { get; }
  int LastChangeNumber { get; }
  IntegrationSummary LastIntegration { get; }
  string LastSuccessfulIntegrationLabel { get; }
  IList TaskResults { get; }
  Modification[] Modifications { get; set; }
  Exception ExceptionResult { get; set; }
  string TaskOutput { get; }
  void AddTaskResult(string result);
  void AddTaskResult(ITaskResult result);
  bool HasModifications();
  bool ShouldRunBuild();
        bool SourceControlErrorOccured { get; set; }
  IDictionary IntegrationProperties { get; }
        Util.BuildProgressInformation BuildProgressInformation { get; }
 }
}
