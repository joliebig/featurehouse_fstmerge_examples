using System.Text.RegularExpressions;
using Exortech.NetReflector;
namespace ThoughtWorks.CruiseControl.Core.Sourcecontrol
{
    [ReflectorType("commentFilter")]
    public class CommentFilter : IModificationFilter
    {
        [ReflectorProperty("pattern", Required = true)]
        public string Pattern;
        public bool Accept(Modification modification)
        {
            if (modification.Comment == null)
                return false;
            return Regex.IsMatch(modification.Comment, Pattern);
        }
        public override string ToString()
        {
            return "commentFilter " + Pattern;
        }
    }
}
