using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;
using OVT.Melissa.PluginSupport;
using System.Resources;
namespace OVT.FireIRC.Resources.Resources
{
    public class ResourceManagement
    {
        public static global::FireIRC.Resources.Localization.ResourceLocalizer r = new global::FireIRC.Resources.Localization.ResourceLocalizer();
        public static void ConvertToManagedResource(Form form)
        {
            try
            {
                string p = form.Text.Replace("{", "").Replace("}", "");
                form.Text = r.GetString(p, new System.Globalization.CultureInfo(PropertyService.Get<string>("language", "en")));
            }
            catch (MissingManifestResourceException) { }
            P(form.Controls);
        }
        public static void ConvertToManagedResource(ListView listview)
        {
            foreach (ColumnHeader i in listview.Columns)
            {
                try
                {
                    string p = i.Text.Replace("{", "").Replace("}", "");
                    i.Text = r.GetString(p, new System.Globalization.CultureInfo(PropertyService.Get<string>("language", "en")));
                }
                catch (MissingManifestResourceException) { }
            }
        }
        public static void ConvertToManagedResource(MenuStrip menuStrip)
        {
            foreach (object i in menuStrip.Items)
            {
                try
                {
                    if (i.GetType().Name == "ToolStripMenuItem")
                    {
                        ToolStripMenuItem ib = (ToolStripMenuItem)i;
                        string p = ib.Text.Replace("{", "").Replace("}", "");
                        ib.Text = r.GetString(p, new System.Globalization.CultureInfo(PropertyService.Get<string>("language", "en")));
                        P(ib.DropDownItems);
                    }
                }
                catch (MissingManifestResourceException) { }
                catch (TypeInitializationException) { }
            }
        }
        static void P(Control.ControlCollection c)
        {
            foreach (Control i in c)
            {
                if (i.Text != null)
                {
                    if (i.Text != "")
                    {
                        try
                        {
                            string p = i.Text.Replace("{", "").Replace("}", "");
                            i.Text = r.GetString(p, new System.Globalization.CultureInfo(PropertyService.Get<string>("language", "en")));
                        }
                        catch (MissingManifestResourceException) { }
                    }
                }
                P(i.Controls);
            }
        }
        static void P(ToolStripItemCollection c)
        {
            foreach (object i in c)
            {
                try
                {
                    if (i.GetType().Name == "ToolStripMenuItem")
                    {
                        ToolStripMenuItem ib = (ToolStripMenuItem)i;
                        string p = ib.Text.Replace("{", "").Replace("}", "");
                        ib.Text = r.GetString(p, new System.Globalization.CultureInfo(PropertyService.Get<string>("language", "en")));
                        P(ib.DropDownItems);
                    }
                }
                catch (MissingManifestResourceException) { }
                catch (TypeInitializationException) { }
            }
        }
    }
}
