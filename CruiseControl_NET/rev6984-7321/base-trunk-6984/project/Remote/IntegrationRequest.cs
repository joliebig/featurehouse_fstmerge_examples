using System;
using ThoughtWorks.CruiseControl.Remote;
using System.Collections.Generic;
namespace ThoughtWorks.CruiseControl.Remote
{
 [Serializable]
 public class IntegrationRequest
 {
  public static readonly IntegrationRequest NullRequest = new IntegrationRequest(BuildCondition.NoBuild, "NullRequest", null);
  private readonly BuildCondition buildCondition;
  private readonly string source;
        private readonly DateTime requestTime;
        private Dictionary<string, string> parameterValues = new Dictionary<string,string>();
  public IntegrationRequest(BuildCondition buildCondition, string source, string userName)
  {
   this.buildCondition = buildCondition;
   this.source = source;
            this.requestTime = DateTime.Now;
            UserName = userName;
  }
  public BuildCondition BuildCondition
  {
   get { return buildCondition; }
  }
  public string Source
  {
   get { return source; }
  }
        public string UserName { get; private set; }
        public Dictionary<string, string> BuildValues
        {
            get { return parameterValues; }
            set { parameterValues = value; }
        }
        public DateTime RequestTime
        {
            get { return requestTime; }
        }
  public override bool Equals(object obj)
  {
   IntegrationRequest other = obj as IntegrationRequest;
   return other != null && other.BuildCondition == BuildCondition && other.Source == Source;
  }
  public override int GetHashCode()
  {
   return ToString().GetHashCode();
  }
  public override string ToString()
  {
            if (!string.IsNullOrEmpty(UserName))
            {
                return string.Format("{0} triggered a build ({1}) from_ {2}", UserName, BuildCondition, Source);
            }
            else
            {
                return string.Format("Build ({1}) triggered from_ {0}", Source, BuildCondition);
            }
  }
        public bool PublishOnSourceControlException { get; set; }
 }
}
