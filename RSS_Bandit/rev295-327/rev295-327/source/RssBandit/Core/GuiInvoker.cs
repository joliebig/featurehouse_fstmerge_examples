using System; 
using System.Reflection; 
using System.Threading; 
using System.Windows.Forms; 
using System.Diagnostics; namespace  System {
	
    public delegate  void  Action ();

}
namespace  RssBandit {
	
    [DebuggerNonUserCode] 
    public static class  GuiInvoker {
		
        public static  void Initialize()
        {
            if (_context == null)
                _context = GetMarshalingControl();
        }
 
        public static  void InvokeAsync(Control control, Action action)
        {
            EnsureInitialized();
            DoInvoke(control, action, _context, false);
        }
 
        public static  void Invoke(Control control, Action action)
        {
            EnsureInitialized();
            DoInvoke(control, action, _context, true);
        }
 
        private static  void SafeInvoke(object state)
        {
            InvokeControl d = (InvokeControl)state;
            if (d.Disposing || d.IsDisposed)
            {
                return;
            }
            d.Action();
        }
 
        private static  void ActionInvoke(object state)
        {
            Action a = (Action)state;
            a();
        }
 
        private static  void EnsureInitialized()
        {
            if (_context == null)
                throw new InvalidOperationException("Initialize must be called first.");
        }
 
        private static  void DoInvoke(Control control, Action action, Control context, bool synchronous)
        {
            if (action == null)
                throw new ArgumentNullException("action");
            if (context == null)
                throw new ArgumentNullException("context");
            if (control != null)
            {
                InvokeControl invokeControl = new InvokeControl(control, action);
                if (synchronous)
                {
                    if (control.IsHandleCreated && !control.InvokeRequired)
                    {
                        SafeInvoke(invokeControl);
                        return;
                    }
                }
                if (synchronous)
                    context.Invoke((WaitCallback)SafeInvoke, invokeControl);
                else
                    context.BeginInvoke((WaitCallback)SafeInvoke, invokeControl);
            }
            else
            {
                if (synchronous)
                    context.Invoke((WaitCallback)ActionInvoke, action);
                else
                    context.BeginInvoke((WaitCallback)ActionInvoke, action);
            }
        }
 
        private static  Control GetMarshalingControl()
        {
            Type context = Type.GetType("System.Windows.Forms.Application+ThreadContext, System.Windows.Forms, Version=2.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089");
            MethodInfo current = context.GetMethod("FromCurrent", BindingFlags.NonPublic | BindingFlags.Static);
            PropertyInfo prop = context.GetProperty("MarshalingControl", BindingFlags.Instance | BindingFlags.NonPublic);
            object thread = current.Invoke(null, null);
            Control control = (Control)prop.GetValue(thread, null);
            return control;
        }
 
        private class  InvokeControl {
			
            private readonly  Control control;
 
            private readonly  Action action;
 
            public  InvokeControl(Control targetObject, Action action)
            {
                if (targetObject == null)
                    throw new ArgumentNullException("targetObject");
                if (action == null)
                    throw new ArgumentNullException("action");
                control = targetObject;
                this.action = action;
            }
 
            public  bool Disposing
            {
                get
                {
                    return control.Disposing;
                }
            }
 
            public  bool IsDisposed
            {
                get
                {
                    return control.IsDisposed;
                }
            }
 
            public  Action Action
            {
                get { return action; }
            }

		}
		
        private static  Control _context;

	}

}
