using WikiFunctions;
namespace AutoWikiBrowser
{
    internal sealed partial class Help : WikiFunctions.Controls.Help
    {
        public Help()
        {
            Text = "AWB Help";
        }
        protected override string URL
        {
            get { return Tools.GetENLinkWithSimpleSkinAndLocalLanguage("Wikipedia:AutoWikiBrowser/User_manual"); }
        }
        public void ShowHelpEN(string article)
        { ShowHelp( Tools.GetENLinkWithSimpleSkinAndLocalLanguage(article)); }
        public void ShowHelp(string url)
        {
            ShowDialog();
            if (string.IsNullOrEmpty(url))
                Navigate();
            else
                Navigate(url);
        }
        private void Navigate()
        { Navigate(URL); }
    }
}
