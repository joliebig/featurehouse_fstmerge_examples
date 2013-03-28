using System.Windows.Forms;
namespace WikiFunctions.ReplaceSpecial
{
    public interface IRuleControlOwner
    {
        void NameChanged(Control rc, string name);
    }
}
