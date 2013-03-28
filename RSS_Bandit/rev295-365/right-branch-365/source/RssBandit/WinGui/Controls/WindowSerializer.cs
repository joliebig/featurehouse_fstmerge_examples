using System;
using System.ComponentModel;
using IDesignerHost = System.ComponentModel.Design.IDesignerHost;
using System.Windows.Forms;
using System.Drawing;
using Genghis;
namespace RssBandit.WinGui.Controls
{
    [
        DesignTimeVisible(true),
        ToolboxItem(true),
        DefaultProperty("Form")
    ]
    public class WindowSerializer : Component {
        private Form form;
        private string formName;
  private bool saveOnlyLocation = false;
  private bool saveNoWindowState = false;
        private Rectangle dimensions;
        private FormWindowState windowState;
        public delegate void WindowSerializerDelegate(object sender, Preferences preferences);
        public event WindowSerializerDelegate LoadStateEvent;
        public event WindowSerializerDelegate SaveStateEvent;
        public WindowSerializer() {
            form = null;
            formName = null;
            dimensions = Rectangle.Empty;
        }
        public WindowSerializer(Form form) : this() {
            Form = form;
        }
        [
            Category("Misc"),
            Localizable(false),
            Description("The form which is to have it's state persisted.")
        ]
        public Form Form {
            get {
                if (form == null && this.DesignMode) {
                    IDesignerHost designerHost =
                        (IDesignerHost) this.GetService(typeof(IDesignerHost));
                    if (designerHost != null) {
                        IComponent rootComponent = designerHost.RootComponent;
                        if (rootComponent != null && rootComponent as Form != null)
                                this.form = (Form) rootComponent;
                    }
                }
                return form;
            }
            set {
                if (form != null && this.DesignMode == false) {
                    form.Closing -= new CancelEventHandler(OnClosing);
                    form.Resize -= new EventHandler(OnResize);
                    form.Move -= new EventHandler(OnMove);
                    form.Load -= new EventHandler(OnLoad);
                }
                form = value;
                if (form != null && this.DesignMode == false) {
                    form.Closing += new CancelEventHandler(OnClosing);
                    form.Resize += new EventHandler(OnResize);
                    form.Move += new EventHandler(OnMove);
                    form.Load += new EventHandler(OnLoad);
                }
            }
        }
        [
        Category("Misc"),
        Localizable(false),
  DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden),
        Description("The name of the form (automatically set and used).")
        ]
        public string FormName {
            get {
                if (Form == null)
                    return null;
                if (formName == null)
                    return form.GetType().FullName;
                return formName;
            }
        }
  [
  Category("Behavior"),
  DefaultValue(false),
  Description("Get or set the boolean to save only the location of a form, but not the size.")
  ]
  public bool SaveOnlyLocation {
   get { return saveOnlyLocation; }
   set { saveOnlyLocation = value; }
  }
  [
  Category("Behavior"),
  DefaultValue(false),
  Description("Get or set the boolean to prevent saving of the WindowState.")
  ]
  public bool SaveNoWindowState {
   get { return saveNoWindowState; }
   set { saveNoWindowState = value; }
  }
        private void OnLoad(object sender, System.EventArgs e) {
   dimensions.X = form.Left;
   dimensions.Y = form.Top;
   dimensions.Width = form.Width;
   dimensions.Height = form.Height;
   FormWindowState windowState = form.WindowState;
            Preferences prefReader = Preferences.GetUserNode(GetType());
            prefReader = prefReader.GetSubnode(FormName);
            Point location = new Point(
    prefReader.GetInt32("Left", form.Left),
    prefReader.GetInt32("Top", form.Top));
   dimensions.Location = AdjustLocationToAvailableScreens(location);
   if (!this.saveOnlyLocation) {
    dimensions.Width = prefReader.GetInt32("Width", form.Width);
    dimensions.Height = prefReader.GetInt32("Height", form.Height);
   }
   if (!this.saveNoWindowState) {
    windowState = (FormWindowState) prefReader.GetInt32("WindowState", (int) form.WindowState);
   }
            if (LoadStateEvent != null)
                LoadStateEvent(this, prefReader);
            prefReader.Close();
            form.Bounds = dimensions;
            form.WindowState = windowState;
        }
  private static Point AdjustLocationToAvailableScreens(Point location) {
   foreach (Screen s in Screen.AllScreens) {
    if (s.WorkingArea.Contains(location)) {
     return location;
    }
   }
   return Screen.PrimaryScreen.WorkingArea.Location;
  }
        private void OnMove(object sender, System.EventArgs e) {
            if (form.WindowState == FormWindowState.Normal)
                dimensions.Location = form.Location;
            windowState = form.WindowState;
        }
        private void OnResize(object sender, System.EventArgs e) {
            if (form.WindowState == FormWindowState.Normal)
                dimensions.Size = form.Size;
        }
        private void OnClosing(object sender, CancelEventArgs e) {
            if (windowState == FormWindowState.Minimized)
                windowState = FormWindowState.Normal;
            Preferences prefWriter = Preferences.GetUserNode(GetType());
            prefWriter = prefWriter.GetSubnode(FormName);
            prefWriter.SetProperty("Left", dimensions.Left);
            prefWriter.SetProperty("Top", dimensions.Top);
   if (this.saveOnlyLocation) {
    prefWriter.SetProperty("Width", null);
    prefWriter.SetProperty("Height", null);
   } else {
    prefWriter.SetProperty("Width", dimensions.Width);
    prefWriter.SetProperty("Height", dimensions.Height);
   }
   if (this.saveNoWindowState) {
    prefWriter.SetProperty("WindowState", null);
   } else {
    prefWriter.SetProperty("WindowState", (int) windowState);
   }
            if (SaveStateEvent != null)
                SaveStateEvent(this, prefWriter);
            prefWriter.Close();
        }
    }
}
