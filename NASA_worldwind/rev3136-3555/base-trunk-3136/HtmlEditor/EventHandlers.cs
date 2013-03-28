using System;
using System.ComponentModel;
using System.Windows.Forms;
namespace onlyconnect
{
 public delegate void HtmlNavigateEventHandler(Object s,
 HtmlNavigateEventArgs e);
 public class HtmlNavigateEventArgs
 {
  String target;
  public HtmlNavigateEventArgs(String target)
  {
   this.target = target;
  }
  public String Target
  {
   get { return target; }
  }
 }
 public class HtmlUpdateUIEventArgs : EventArgs
 {
  private IHTMLElement mcurrentElement;
  public HtmlUpdateUIEventArgs(): base()
  {
  }
  public IHTMLElement currentElement
  {
   get
   {
    return mcurrentElement;
   }
   set
   {
    mcurrentElement = value;
   }
  }
 }
 public delegate void ReadyStateChangedHandler(object sender, ReadyStateChangedEventArgs e);
 public class ReadyStateChangedEventArgs : EventArgs
 {
  private string mReadyState;
  public ReadyStateChangedEventArgs(string readystateVal) : base()
  {
   mReadyState = readystateVal;
  }
  public string ReadyState
  {
   get
   {
    return mReadyState;
   }
  }
 }
 public delegate void UpdateUIHandler(object sender, HtmlUpdateUIEventArgs e);
 public delegate void HtmlKeyPressHandler(object sender, HtmlKeyPressEventArgs e);
 public class HtmlKeyPressEventArgs : EventArgs
 {
  private IHTMLEventObj m_ev;
  public HtmlKeyPressEventArgs(ref IHTMLEventObj ev) : base()
  {
   m_ev = ev;
  }
  public IHTMLEventObj HtmlEventObject
  {
   get
   {
    return m_ev;
   }
  }
 }
 public delegate void HtmlEventHandler(Object s, HtmlEventArgs e);
 public class HtmlEventArgs
 {
  public IHTMLEventObj Event;
  public HtmlEventArgs(IHTMLEventObj Event)
  {
   this.Event = Event;
  }
 }
    public delegate void BeforeNavigateEventHandler(object s, BeforeNavigateEventArgs e);
    public class BeforeNavigateEventArgs : System.ComponentModel.CancelEventArgs
    {
        string pTarget = string.Empty;
        string pNewTarget = string.Empty;
        public delegate void BeforeNavigateEventHandler(object s, BeforeNavigateEventArgs e);
        public BeforeNavigateEventArgs(string Target)
        {
            this.pTarget = Target;
            this.pNewTarget = Target;
        }
        [Description("Gets the URL that will be navigated to.")]
        public string Target
        {
            get { return pTarget; }
        }
        [Description("Gets/Sets the revised URL that will be used to navigate.")]
        public string NewTarget
        {
            get { return pTarget; }
            set
            {
                pTarget = value;
            }
        }
    }
    public delegate void BeforeShortcutEventHandler(HtmlEditor h, BeforeShortcutEventArgs e);
    public class BeforeShortcutEventArgs
    {
        bool mCancel = false;
        Keys mKey;
        public BeforeShortcutEventArgs(Keys key)
        {
            mKey = key;
        }
        public Keys Key
        {
            get
            { return mKey; }
        }
        public bool Cancel
        {
            get
            { return mCancel; }
            set
            { mCancel = value; }
        }
    }
    public delegate void BeforePasteHandler(object s, BeforePasteArgs e);
    public class BeforePasteArgs
    {
        bool mCancel = false;
        public bool Cancel
        {
            get
            { return mCancel; }
            set
            { mCancel = value; }
        }
    }
}
