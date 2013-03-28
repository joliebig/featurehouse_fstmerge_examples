using System.Collections.Generic;
using System.Xml.Linq;
namespace ThoughtWorks.CruiseControl.Core.Config.Preprocessor
{
    internal class SymbolicDef
    {
        public bool IsExplicitlyDefined;
        public string Name;
        public IEnumerable< XNode > Value;
    }
}
