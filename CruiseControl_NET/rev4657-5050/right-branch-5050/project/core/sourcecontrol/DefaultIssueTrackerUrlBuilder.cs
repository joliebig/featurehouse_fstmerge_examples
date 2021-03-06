using Exortech.NetReflector;
using System;
namespace ThoughtWorks.CruiseControl.Core.Sourcecontrol
{
    [ReflectorType("defaultIssueTracker")]
    public class DefaultIssueTrackerUrlBuilder : IModificationUrlBuilder
    {
        private string _url;
        [ReflectorProperty("url")]
        public string Url
        {
            get { return _url; }
            set { _url = value; }
        }
        public void SetupModification(Modification[] modifications)
        {
            if (modifications == null) throw new ArgumentNullException("modifications");
            foreach (Modification mod in modifications)
            {
                if ((mod != null) && !string.IsNullOrEmpty(mod.Comment))
                {
                    string SearchingComment = mod.Comment.Split(' ')[0];
                    int EndPosition = SearchingComment.Length - 1;
                    char CurrentChar = SearchingComment[EndPosition];
                    string Result = string.Empty;
                    bool NumericPartFound = false;
                    while (EndPosition > 0 && !char.IsNumber(CurrentChar))
                    {
                        EndPosition--;
                        CurrentChar = SearchingComment[EndPosition];
                    }
                    while (EndPosition >= 0 && char.IsNumber(CurrentChar))
                    {
                        Result = Result.Insert(0, CurrentChar.ToString());
                        EndPosition--;
                        if (EndPosition >= 0 ) CurrentChar = SearchingComment[EndPosition];
                        NumericPartFound = true;
                    }
                    if (NumericPartFound)
                    {
                        mod.IssueUrl = string.Format(_url, Result);
                    }
                }
            }
        }
    }
}
