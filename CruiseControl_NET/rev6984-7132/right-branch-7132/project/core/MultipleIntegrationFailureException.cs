using System;
using System.Collections.Generic;
using System.Text;
namespace ThoughtWorks.CruiseControl.Core
{
    public class MultipleIntegrationFailureException
        : Exception
    {
        private List<Exception> failures = new List<Exception>();
        public MultipleIntegrationFailureException(Exception initialFailure)
            : base("There has been multiple integration failures")
        {
            if (initialFailure == null) throw new ArgumentNullException("initialFailure");
            failures.Add(initialFailure);
        }
        public Exception[] Failures
        {
            get { return failures.ToArray(); }
        }
        public void AddFailure(Exception failure)
        {
            if (failure == null) throw new ArgumentNullException("failure");
            failures.Add(failure);
        }
    }
}
