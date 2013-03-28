using System;
using System.Collections.Generic;
using System.Text;
namespace ThoughtWorks.CruiseControl.Core.Util
{
    public interface IShadowCopier
    {
        string RetrieveFilePath(string fileName);
    }
}
