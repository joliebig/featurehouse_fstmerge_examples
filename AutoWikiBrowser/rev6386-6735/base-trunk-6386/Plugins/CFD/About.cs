namespace AutoWikiBrowser.Plugins.CFD
{
    internal sealed class AboutBox : WikiFunctions.Controls.AboutBox
    {
        protected override void Initialise()
        {
            lblVersion.Text = "Version " +
                System.Reflection.Assembly.GetExecutingAssembly().GetName().Version.ToString();
            textBoxDescription.Text = GPLNotice;
            Text = "CFD Plugin";
        }
    }
}
