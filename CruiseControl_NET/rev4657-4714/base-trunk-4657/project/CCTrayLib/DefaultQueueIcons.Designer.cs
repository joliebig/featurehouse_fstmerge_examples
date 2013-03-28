namespace ThoughtWorks.CruiseControl.CCTrayLib {
    using System;
    [global::System.CodeDom.Compiler.GeneratedCodeAttribute("System.Resources.Tools.StronglyTypedResourceBuilder", "2.0.0.0")]
    [global::System.Diagnostics.DebuggerNonUserCodeAttribute()]
    [global::System.Runtime.CompilerServices.CompilerGeneratedAttribute()]
    internal class DefaultQueueIcons {
        private static global::System.Resources.ResourceManager resourceMan;
        private static global::System.Globalization.CultureInfo resourceCulture;
        [global::System.Diagnostics.CodeAnalysis.SuppressMessageAttribute("Microsoft.Performance", "CA1811:AvoidUncalledPrivateCode")]
        internal DefaultQueueIcons() {
        }
        [global::System.ComponentModel.EditorBrowsableAttribute(global::System.ComponentModel.EditorBrowsableState.Advanced)]
        internal static global::System.Resources.ResourceManager ResourceManager {
            get {
                if (object.ReferenceEquals(resourceMan, null)) {
                    global::System.Resources.ResourceManager temp = new global::System.Resources.ResourceManager("ThoughtWorks.CruiseControl.CCTrayLib.DefaultQueueIcons", typeof(DefaultQueueIcons).Assembly);
                    resourceMan = temp;
                }
                return resourceMan;
            }
        }
        [global::System.ComponentModel.EditorBrowsableAttribute(global::System.ComponentModel.EditorBrowsableState.Advanced)]
        internal static global::System.Globalization.CultureInfo Culture {
            get {
                return resourceCulture;
            }
            set {
                resourceCulture = value;
            }
        }
        internal static System.Drawing.Icon BuildCheckingModifications {
            get {
                object obj = ResourceManager.GetObject("BuildCheckingModifications", resourceCulture);
                return ((System.Drawing.Icon)(obj));
            }
        }
        internal static System.Drawing.Icon BuildPending {
            get {
                object obj = ResourceManager.GetObject("BuildPending", resourceCulture);
                return ((System.Drawing.Icon)(obj));
            }
        }
        internal static System.Drawing.Icon QueueEmpty {
            get {
                object obj = ResourceManager.GetObject("QueueEmpty", resourceCulture);
                return ((System.Drawing.Icon)(obj));
            }
        }
        internal static System.Drawing.Icon QueuePopulated {
            get {
                object obj = ResourceManager.GetObject("QueuePopulated", resourceCulture);
                return ((System.Drawing.Icon)(obj));
            }
        }
        internal static System.Drawing.Icon ServerHttp {
            get {
                object obj = ResourceManager.GetObject("ServerHttp", resourceCulture);
                return ((System.Drawing.Icon)(obj));
            }
        }
        internal static System.Drawing.Icon ServerRemoting {
            get {
                object obj = ResourceManager.GetObject("ServerRemoting", resourceCulture);
                return ((System.Drawing.Icon)(obj));
            }
        }
        internal static System.Drawing.Icon Yellow {
            get {
                object obj = ResourceManager.GetObject("Yellow", resourceCulture);
                return ((System.Drawing.Icon)(obj));
            }
        }
    }
}
