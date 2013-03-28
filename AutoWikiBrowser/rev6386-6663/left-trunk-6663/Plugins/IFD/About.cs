namespace AutoWikiBrowser.Plugins.IFD
{
    internal sealed class AboutBox : WikiFunctions.Controls.AboutBox
    {
        protected override void Initialise()
        {
            lblVersion.Text = "Version " +
                System.Reflection.Assembly.GetExecutingAssembly().GetName().Version;
            textBoxDescription.Text = GPLNotice;
            Text = "IFD Plugin";
        }
    }
}
