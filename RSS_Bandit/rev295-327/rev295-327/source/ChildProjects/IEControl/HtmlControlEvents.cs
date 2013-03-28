using System; 
using System.Drawing; 
using System.ComponentModel; namespace  IEControl {
	
 public delegate  void  BrowserPrivacyImpactedStateChangeEventHandler (object sender, BrowserPrivacyImpactedStateChangeEvent e);
	
 public class  BrowserPrivacyImpactedStateChangeEvent : EventArgs {
		
  public  bool bImpacted;
 
  public  BrowserPrivacyImpactedStateChangeEvent(bool bImpacted) {
   this.bImpacted = bImpacted;
  }

	}
	
 public delegate  void  BrowserUpdatePageStatusEventHandler (object sender, BrowserUpdatePageStatusEvent e);
	
 public class  BrowserUpdatePageStatusEvent : EventArgs {
		
  public  object pDisp;
 
  public  object nPage;
 
  public  object fDone;
 
  public  BrowserUpdatePageStatusEvent(object pDisp, object nPage, object fDone) {
   this.pDisp = pDisp;
   this.nPage = nPage;
   this.fDone = fDone;
  }

	}
	
 public delegate  void  BrowserPrintTemplateTeardownEventHandler (object sender, BrowserPrintTemplateTeardownEvent e);
	
 public class  BrowserPrintTemplateTeardownEvent : EventArgs {
		
  public  object pDisp;
 
  public  BrowserPrintTemplateTeardownEvent(object pDisp) {
   this.pDisp = pDisp;
  }

	}
	
 public delegate  void  BrowserPrintTemplateInstantiationEventHandler (object sender, BrowserPrintTemplateInstantiationEvent e);
	
 public class  BrowserPrintTemplateInstantiationEvent : EventArgs {
		
  public  object pDisp;
 
  public  BrowserPrintTemplateInstantiationEvent(object pDisp) {
   this.pDisp = pDisp;
  }

	}
	
 public delegate  void  BrowserNavigateErrorEventHandler (object sender, BrowserNavigateErrorEvent e);
	
 public class  BrowserNavigateErrorEvent :CancelEventArgs {
		
  public  object pDisp;
 
  public  object uRL;
 
  public  object frame;
 
  public  object statusCode;
 
  public  BrowserNavigateErrorEvent(object pDisp, object uRL, object frame, object statusCode, bool cancel):base(cancel) {
   this.pDisp = pDisp;
   this.uRL = uRL;
   this.frame = frame;
   this.statusCode = statusCode;
   base.Cancel = cancel;
  }

	}
	
 public delegate  void  BrowserFileDownloadEventHandler (object sender, BrowserFileDownloadEvent e);
	
 public class  BrowserFileDownloadEvent :CancelEventArgs {
		
  public  BrowserFileDownloadEvent(bool cancel):base(cancel) {}

	}
	
 public delegate  void  BrowserSetSecureLockIconEventHandler (object sender, BrowserSetSecureLockIconEvent e);
	
 public class  BrowserSetSecureLockIconEvent : EventArgs {
		
  public  SHDocVw.SecureLockIconConstants secureLockIcon;
 
  public  BrowserSetSecureLockIconEvent(int secureLockIcon) {
   this.secureLockIcon = (SHDocVw.SecureLockIconConstants)secureLockIcon;
  }

	}
	
 public delegate  void  BrowserClientToHostWindowEventHandler (object sender, BrowserClientToHostWindowEvent e);
	
 public class  BrowserClientToHostWindowEvent : EventArgs {
		
  public  int cX;
 
  public  int cY;
 
  public  BrowserClientToHostWindowEvent(int cX, int cY) {
   this.cX = cX;
   this.cY = cY;
  }

	}
	
 public delegate  void  BrowserWindowClosingEventHandler (object sender, BrowserWindowClosingEvent e);
	
 public class  BrowserWindowClosingEvent :CancelEventArgs {
		
  public  bool isChildWindow;
 
  public  BrowserWindowClosingEvent(bool isChildWindow, bool cancel):base(cancel) {
   this.isChildWindow = isChildWindow;
  }

	}
	
 public delegate  void  BrowserWindowSetHeightEventHandler (object sender, BrowserWindowSetHeightEvent e);
	
 public class  BrowserWindowSetHeightEvent : EventArgs {
		
  public  int height;
 
  public  BrowserWindowSetHeightEvent(int height) {
   this.height = height;
  }

	}
	
 public delegate  void  BrowserWindowSetWidthEventHandler (object sender, BrowserWindowSetWidthEvent e);
	
 public class  BrowserWindowSetWidthEvent : EventArgs {
		
  public  int width;
 
  public  BrowserWindowSetWidthEvent(int width) {
   this.width = width;
  }

	}
	
 public delegate  void  BrowserWindowSetTopEventHandler (object sender, BrowserWindowSetTopEvent e);
	
 public class  BrowserWindowSetTopEvent : EventArgs {
		
  public  int top;
 
  public  BrowserWindowSetTopEvent(int top) {
   this.top = top;
  }

	}
	
 public delegate  void  BrowserWindowSetLeftEventHandler (object sender, BrowserWindowSetLeftEvent e);
	
 public class  BrowserWindowSetLeftEvent : EventArgs {
		
  public  int left;
 
  public  BrowserWindowSetLeftEvent(int left) {
   this.left = left;
  }

	}
	
 public delegate  void  BrowserWindowSetResizableEventHandler (object sender, BrowserWindowSetResizableEvent e);
	
 public class  BrowserWindowSetResizableEvent : EventArgs {
		
  public  bool resizable;
 
  public  BrowserWindowSetResizableEvent(bool resizable) {
   this.resizable = resizable;
  }

	}
	
 public delegate  void  BrowserOnTheaterModeEventHandler (object sender, BrowserOnTheaterModeEvent e);
	
 public class  BrowserOnTheaterModeEvent : EventArgs {
		
  public  bool theaterMode;
 
  public  BrowserOnTheaterModeEvent(bool theaterMode) {
   this.theaterMode = theaterMode;
  }

	}
	
 public delegate  void  BrowserOnFullScreenEventHandler (object sender, BrowserOnFullScreenEvent e);
	
 public class  BrowserOnFullScreenEvent : EventArgs {
		
  public  bool fullScreen;
 
  public  BrowserOnFullScreenEvent(bool fullScreen) {
   this.fullScreen = fullScreen;
  }

	}
	
 public delegate  void  BrowserOnStatusBarEventHandler (object sender, BrowserOnStatusBarEvent e);
	
 public class  BrowserOnStatusBarEvent : EventArgs {
		
  public  bool statusBar;
 
  public  BrowserOnStatusBarEvent(bool statusBar) {
   this.statusBar = statusBar;
  }

	}
	
 public delegate  void  BrowserOnMenuBarEventHandler (object sender, BrowserOnMenuBarEvent e);
	
 public class  BrowserOnMenuBarEvent : EventArgs {
		
  public  bool menuBar;
 
  public  BrowserOnMenuBarEvent(bool menuBar) {
   this.menuBar = menuBar;
  }

	}
	
 public delegate  void  BrowserOnToolBarEventHandler (object sender, BrowserOnToolBarEvent e);
	
 public class  BrowserOnToolBarEvent : EventArgs {
		
  public  bool toolBar;
 
  public  BrowserOnToolBarEvent(bool toolBar) {
   this.toolBar = toolBar;
  }

	}
	
 public delegate  void  BrowserOnVisibleEventHandler (object sender, BrowserOnVisibleEvent e);
	
 public class  BrowserOnVisibleEvent : EventArgs {
		
  public  bool visible;
 
  public  BrowserOnVisibleEvent(bool visible) {
   this.visible = visible;
  }

	}
	
 public delegate  void  BrowserDocumentCompleteEventHandler (object sender, BrowserDocumentCompleteEvent e);
	
 public class  BrowserDocumentCompleteEvent : EventArgs {
		
  public  bool IsRootPage;
 
  public  object pDisp;
 
  public  string url;
 
  public  BrowserDocumentCompleteEvent(object pDisp, object uRL, bool isRootPage) {
   this.pDisp = pDisp;
   this.url = (String)uRL;
   this.IsRootPage = isRootPage;
  }

	}
	
 public delegate  void  BrowserNavigateComplete2EventHandler (object sender, BrowserNavigateComplete2Event e);
	
 public class  BrowserNavigateComplete2Event : EventArgs {
		
  public  bool IsRootPage;
 
  public  object pDisp;
 
  public  string url;
 
  public  BrowserNavigateComplete2Event(object pDisp, object uRL, bool isRootPage) {
   this.pDisp = pDisp;
   this.url = (String)uRL;
   this.IsRootPage = isRootPage;
  }

	}
	
 public delegate  void  BrowserNewWindowEventHandler (object sender, BrowserNewWindowEvent e);
	
 public class  BrowserNewWindowEvent  : CancelEventArgs {
		
  string _url;
 
  public  BrowserNewWindowEvent(string url, bool cancel) : base(cancel) { this._url = url; }
 
  public  string url { get { return this._url; } }

	}
	
 public delegate  void  BrowserNewWindow2EventHandler (object sender, BrowserNewWindow2Event e);
	
 public class  BrowserNewWindow2Event :CancelEventArgs {
		
  public  object ppDisp;
 
  public  BrowserNewWindow2Event(object ppDisp, bool cancel):base(cancel) {
   this.ppDisp = ppDisp;
  }

	}
	
 public delegate  void  BrowserBeforeNavigate2EventHandler (object sender, BrowserBeforeNavigate2Event e);
	
 public class  BrowserBeforeNavigate2Event : CancelEventArgs {
		
  public  bool IsRootPage;
 
  public  object pDisp;
 
  public  string url;
 
  public  object flags;
 
  public  object targetFrameName;
 
  public  object postData;
 
  public  object headers;
 
  public  BrowserBeforeNavigate2Event(object pDisp, object uRL, object flags, object targetFrameName, object postData, object headers, bool cancel, bool isRootPage):base(cancel) {
   this.pDisp = pDisp;
   this.url = (String)uRL;
   this.flags = flags;
   this.targetFrameName = targetFrameName;
   this.postData = postData;
   this.headers = headers;
   this.IsRootPage = isRootPage;
  }

	}
	
 public delegate  void  BrowserPropertyChangeEventHandler (object sender, BrowserPropertyChangeEvent e);
	
 public class  BrowserPropertyChangeEvent : EventArgs {
		
  public  string szProperty;
 
  public  BrowserPropertyChangeEvent(string szProperty) {
   this.szProperty = szProperty;
  }

	}
	
 public delegate  void  BrowserTitleChangeEventHandler (object sender, BrowserTitleChangeEvent e);
	
 public class  BrowserTitleChangeEvent : EventArgs {
		
  public  string text;
 
  public  BrowserTitleChangeEvent(string text) {
   this.text = text;
  }

	}
	
 public delegate  void  BrowserCommandStateChangeEventHandler (object sender, BrowserCommandStateChangeEvent e);
	
 public class  BrowserCommandStateChangeEvent : EventArgs {
		
  public  SHDocVw.CommandStateChangeConstants command;
 
  public  bool enable;
 
  public  BrowserCommandStateChangeEvent(int command, bool enable) {
   this.command = (SHDocVw.CommandStateChangeConstants)command;
   this.enable = enable;
  }

	}
	
 public delegate  void  BrowserProgressChangeEventHandler (object sender, BrowserProgressChangeEvent e);
	
 public class  BrowserProgressChangeEvent : EventArgs {
		
  public  int progress;
 
  public  int progressMax;
 
  public  BrowserProgressChangeEvent(int progress, int progressMax) {
   this.progress = progress;
   this.progressMax = progressMax;
  }

	}
	
 public delegate  void  BrowserStatusTextChangeEventHandler (object sender, BrowserStatusTextChangeEvent e);
	
 public class  BrowserStatusTextChangeEvent : EventArgs {
		
  public  string text;
 
  public  BrowserStatusTextChangeEvent(string text) {
   this.text = text;
  }

	}
	
 public delegate  void  BrowserContextMenuCancelEventHandler (object sender, BrowserContextMenuCancelEventArgs e);
	
 public class  BrowserContextMenuCancelEventArgs  : CancelEventArgs {
		
  Point location;
 
  public  BrowserContextMenuCancelEventArgs(Point loaction, bool cancel) : base(cancel) { this.location = location; }
 
  public  Point Location { get { return this.location; } }

	}
	
 public delegate  void  BrowserTranslateUrlEventHandler (object sender, BrowserTranslateUrlEventArgs e);
	
 public class  BrowserTranslateUrlEventArgs : EventArgs {
		
  string url;
 
  string translatedUrl;
 
  public  BrowserTranslateUrlEventArgs(string url) { this.url = this.translatedUrl = url; }
 
  public  string Url { get { return this.url; } }
 
  public  string TranslatedUrl { get { return this.translatedUrl; } set { this.translatedUrl = value; } }

	}

}
