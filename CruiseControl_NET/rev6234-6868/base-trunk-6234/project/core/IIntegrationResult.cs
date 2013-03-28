using System;
using System.Collections;
using System.Collections.Generic;
using ThoughtWorks.CruiseControl.Remote;
using System.Xml;
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
        List<NameValuePair> Parameters { get; set; }
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
  string LastChangeNumber { get; }
  IntegrationSummary LastIntegration { get; }
  string LastSuccessfulIntegrationLabel { get; }
  IList TaskResults { get; }
  Modification[] Modifications { get; set; }
  Exception ExceptionResult { get; set; }
  string TaskOutput { get; }
  void AddTaskResult(string result);
        void AddTaskResult(ITaskResult result);
        void AddTaskResultFromFile(string filename);
        void AddTaskResultFromFile(string filename, bool wrapInCData);
  bool HasModifications();
  bool ShouldRunBuild();
        Exception SourceControlError { get; set; }
        bool HasSourceControlError { get; }
        IntegrationStatus LastBuildStatus { get; set; }
        IDictionary IntegrationProperties { get; }
        Util.BuildProgressInformation BuildProgressInformation { get; }
        IIntegrationResult Clone();
        void Merge(IIntegrationResult value);
        List<NameValuePair> SourceControlData { get; }
    }
}
