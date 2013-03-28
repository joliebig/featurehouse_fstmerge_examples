using System;
using System.Runtime.InteropServices;
using System.Security.Permissions;
using System.Security.Principal;
namespace ThoughtWorks.CruiseControl.Core.Util
{
    public class Impersonation
        : IDisposable
    {
        private IntPtr tokenHandle = new IntPtr(0);
        private WindowsImpersonationContext impersonatedUser;
        public Impersonation(string domainName, string userName, string password)
        {
            const int LOGON32_PROVIDER_DEFAULT = 0;
            const int LOGON32_LOGON_INTERACTIVE = 2;
            this.tokenHandle = IntPtr.Zero;
            bool returnValue = LogonUser(
                                userName,
                                domainName,
                                password,
                                LOGON32_LOGON_INTERACTIVE,
                                LOGON32_PROVIDER_DEFAULT,
                                ref this.tokenHandle);
            if (false == returnValue)
            {
                int ret = Marshal.GetLastWin32Error();
                throw new System.ComponentModel.Win32Exception(ret);
            }
        }
        public void Impersonate()
        {
            WindowsIdentity newId = new WindowsIdentity(this.tokenHandle);
            this.impersonatedUser = newId.Impersonate();
        }
        public void Revert()
        {
            if (this.impersonatedUser != null)
            {
                this.impersonatedUser.Undo();
            }
            if (this.tokenHandle != IntPtr.Zero)
            {
                CloseHandle(this.tokenHandle);
            }
        }
        [DllImport("advapi32.dll", SetLastError = true)]
        private static extern bool LogonUser(
                string lpszUsername,
                string lpszDomain,
                string lpszPassword,
                int dwLogonType,
                int dwLogonProvider,
                ref IntPtr phToken);
        [DllImport("kernel32.dll", CharSet = CharSet.Auto)]
        private static extern bool CloseHandle(IntPtr handle);
        public void Dispose()
        {
            Revert();
        }
    }
}
