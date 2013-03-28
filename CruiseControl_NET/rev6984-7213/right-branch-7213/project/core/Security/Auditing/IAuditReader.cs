using System;
using System.Collections.Generic;
using ThoughtWorks.CruiseControl.Remote.Security;
namespace ThoughtWorks.CruiseControl.Core.Security.Auditing
{
    public interface IAuditReader
    {
        List<AuditRecord> Read(int startPosition, int numberOfRecords);
        List<AuditRecord> Read(int startPosition, int numberOfRecords, AuditFilterBase filter);
    }
}
