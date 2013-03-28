using Exortech.NetReflector;
using NUnit.Framework;
using System;
using System.Collections.Generic;
using System.Text;
using ThoughtWorks.CruiseControl.Core.Security;
using ThoughtWorks.CruiseControl.Remote;
using ThoughtWorks.CruiseControl.Remote.Security;
using ThoughtWorks.CruiseControl.Core.Security.Auditing;
namespace ThoughtWorks.CruiseControl.UnitTests.Core
{
    [ReflectorType("securityStub")]
    public class StubSecurityManager
        : ISecurityManager
    {
        private string[] methods = new string[] {
                "Initialise",
                "Login",
                "Logout",
                "ValidateSession",
                "GetUserName",
                "RetrieveSetting",
                "GetDisplayName"
            };
        private int[] expectedCounts = new int[7];
        private int[] actualCounts = new int[7];
        private Queue<string> loginResults = new Queue<string>();
        private Queue<bool> validateSessionResults = new Queue<bool>();
        private Queue<string> getUserNameResults = new Queue<string>();
        private Queue<string> getDisplayNameResults = new Queue<string>();
        private Queue<object> retrieveSettingsResults = new Queue<object>();
        public void Initialise()
        {
            actualCounts[0]++;
        }
        [ReflectorProperty("dummy")]
        public string Dummy
        {
            get { return "Dummy Value"; }
            set { }
        }
        [ReflectorProperty("password")]
        public string Password
        {
            get { return "Dummy Value"; }
            set { }
        }
        public string Login(ISecurityCredentials credentials)
        {
            actualCounts[1]++;
            return loginResults.Dequeue();
        }
        public void Logout(string sessionToken)
        {
            actualCounts[2]++;
        }
        public bool ValidateSession(string sessionToken)
        {
            actualCounts[3]++;
            return validateSessionResults.Dequeue();
        }
        public string GetUserName(string sessionToken)
        {
            actualCounts[4]++;
            if (getUserNameResults.Count == 0)
            {
                throw new Exception("Unexpected call to GetUserName");
            }
            else
            {
                return getUserNameResults.Dequeue();
            }
        }
        public string GetDisplayName(string sessionToken)
        {
            actualCounts[6]++;
            if (getDisplayNameResults.Count == 0)
            {
                throw new Exception("Unexpected call to GetDisplayName");
            }
            else
            {
                return getDisplayNameResults.Dequeue();
            }
        }
        public IAuthentication RetrieveUser(string identifier)
        {
            actualCounts[5]++;
            return retrieveSettingsResults.Dequeue() as IAuthentication;
        }
        public IPermission RetrievePermission(string identifier)
        {
            actualCounts[5]++;
            return retrieveSettingsResults.Dequeue() as IPermission;
        }
        public void Verify()
        {
            for (int iLoop = 0; iLoop < methods.Length; iLoop++)
            {
                Assert.AreEqual(expectedCounts[iLoop], actualCounts[iLoop], methods[iLoop] + " call count does not match expected");
            }
        }
        public void Expect(string method, params object[] parameters)
        {
            switch (method)
            {
                case "Initialise":
                    expectedCounts[0]++;
                    break;
                case "Login":
                    expectedCounts[1]++;
                    break;
                case "Logout":
                    expectedCounts[2]++;
                    break;
                case "ValidateSession":
                    expectedCounts[3]++;
                    break;
                case "GetUserName":
                    expectedCounts[4]++;
                    break;
                case "RetrieveSetting":
                    expectedCounts[5]++;
                    break;
                case "GetDisplayName":
                    expectedCounts[6]++;
                    break;
                default:
                    Assert.Fail("Unknown method: " + method);
                    break;
            }
        }
        public void ExpectAndReturn(string method, object result, params object[] parameters)
        {
            Expect(method, parameters);
            switch (method)
            {
                case "Login":
                    loginResults.Enqueue((string)result);
                    break;
                case "ValidateSession":
                    validateSessionResults.Enqueue((bool)result);
                    break;
                case "GetUserName":
                    getUserNameResults.Enqueue((string)result);
                    break;
                case "RetrieveSetting":
                    retrieveSettingsResults.Enqueue(result);
                    break;
                case "GetDisplayName":
                    getDisplayNameResults.Enqueue((string)result);
                    break;
                default:
                    Assert.Fail("Unknown method: " + method);
                    break;
            }
        }
        public void LogEvent(string projectName, string userName, SecurityEvent eventType, SecurityRight eventRight, string message)
        {
        }
        public virtual List<UserDetails> ListAllUsers()
        {
            return new List<UserDetails>();
        }
        public virtual List<SecurityCheckDiagnostics> DiagnoseSecurityPermissions(string userName, params string[] projectNames)
        {
            return new List<SecurityCheckDiagnostics>();
        }
        public virtual bool CheckServerPermission(string userName, SecurityPermission permission)
        {
            return true;
        }
        public virtual List<AuditRecord> ReadAuditRecords(int startPosition, int numberOfRecords)
        {
            List<AuditRecord> records = new List<AuditRecord>();
            return records;
        }
        public virtual List<AuditRecord> ReadAuditRecords(int startPosition, int numberOfRecords, IAuditFilter filter)
        {
            List<AuditRecord> records = new List<AuditRecord>();
            return records;
        }
        public virtual void ChangePassword(string sessionToken, string oldPassword, string newPassword)
        {
            throw new NotImplementedException("Password management is not allowed for this security manager");
        }
        public virtual void ResetPassword(string sessionToken, string userName, string newPassword)
        {
            throw new NotImplementedException("Password management is not allowed for this security manager");
        }
        public TComponent RetrieveComponent<TComponent>()
            where TComponent : class
        {
            return null;
        }
    }
}
