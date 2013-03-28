using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Glade;
using Gtk;
using System;
namespace FireIRC.Resources.GTK
{
    public class Program
    {
        public static void Main(string[] args)
        {
            new FireIRCMainWindow();
        }
    }
    public class FireIRCMainWindow
    {
        [Widget]
        ImageMenuItem quit1;
        [Widget]
        MenuItem about1;
        public FireIRCMainWindow()
        {
            Application.Init();
            Glade.XML gxml = new Glade.XML(null, "FireIRC.Resources.GTK.GTKResources.FireIRC.glade", "fireIRCWindow", null);
            gxml.Autoconnect(this);
            Application.Run();
        }
        void on_quit1_activate(object sender, EventArgs e)
        {
            Application.Quit();
        }
        void on_about1_activate(object sender, EventArgs e)
        {
            new FireIRCAboutWindow();
        }
    }
    public class FireIRCAboutWindow
    {
        [Widget]
        Button button1;
        [Widget]
        Window aboutFireIRC;
        public FireIRCAboutWindow()
        {
            Glade.XML gxml = new Glade.XML(null, "FireIRC.Resources.GTK.GTKResources.FireIRC.glade", "aboutFireIRC", null);
            gxml.Autoconnect(this);
        }
        void on_button1_clicked(object sender, EventArgs e)
        {
            aboutFireIRC.Destroy();
        }
    }
}
