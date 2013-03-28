using System;
using System.Text.RegularExpressions;
using System.Threading;
namespace WikiFunctions.API
{
    public class ApiException : Exception
    {
        public ApiEdit Editor
        { get; private set; }
        public Thread ThrowingThread
        { get; private set; }
        public ApiException(ApiEdit editor, string message)
            : base(message)
        {
            Editor = editor;
            ThrowingThread = Thread.CurrentThread;
        }
        public ApiException(ApiEdit editor, string message, Exception innerException)
            : base(message, innerException)
        {
            Editor = editor;
            ThrowingThread = Thread.CurrentThread;
        }
    }
    public class AbortedException : ApiException
    {
        public AbortedException(ApiEdit editor)
            : base(editor, "API operation aborted")
        {
        }
    }
    public class ApiErrorException : ApiException
    {
        private static readonly Regex ExtractErrorVariable = new Regex("``(.*?)''", RegexOptions.Compiled);
        public string ErrorCode{ get; private set; }
        public string ApiErrorMessage
        { get; private set; }
        public string GetErrorVariable()
        {
            return ExtractErrorVariable.Match(ApiErrorMessage).Groups[1].Value;
        }
        public ApiErrorException(ApiEdit editor, string errorCode, string errorMessage)
            : base(editor, "Bot API returned the following error: '" + errorMessage + "'")
        {
            ErrorCode = errorCode;
            ApiErrorMessage = errorMessage;
        }
    }
    public class FeatureDisabledException : ApiErrorException
    {
        public FeatureDisabledException(ApiEdit editor, string errorCode, string errorMessage)
            : base(editor, errorCode, errorMessage)
        {
            DisabledFeature = errorCode.Replace("-disabled", "");
        }
        public string DisabledFeature
        { get; private set; }
    }
    public class OperationFailedException : ApiException
    {
        public OperationFailedException(ApiEdit editor, string action, string result)
            : base(editor, "Operation '" + action + "' ended with result '" + result + "'.")
        {
            Action = action;
            Result = result;
        }
        public readonly string Action, Result;
    }
    public class ApiBlankException : ApiException
    {
        public ApiBlankException(ApiEdit editor)
            : base(editor, "The result returned by server was blank")
        {
        }
    }
    public class BrokenXmlException : ApiException
    {
        public BrokenXmlException(ApiEdit editor, string message)
            : base(editor, message)
        {
        }
        public BrokenXmlException(ApiEdit editor, string message, Exception innerException)
            : base(editor, message, innerException)
        {
        }
        public BrokenXmlException(ApiEdit editor, Exception innerException)
            : base(editor, "Error parsing data returned by server: " + innerException.Message , innerException)
        {
        }
    }
    public class LoginException : ApiException
    {
        public string StatusCode { get; private set; }
        public LoginException(ApiEdit editor, string status)
            : base(editor, GetErrorMessage(status))
        {
            StatusCode = status;
        }
        protected static string GetErrorMessage(string code)
        {
            switch (code.ToLower())
            {
                case "noname":
                    return "You didn't specify your username";
                case "illegal":
                    return "You provided an illegal username";
                case "notexists":
                    return "The username you provided doesn't exist";
                case "emptypass":
                    return "You must specify your password to log in";
                case "wrongpass":
                    return "The password you provided is incorrect";
                case "wrongpluginpass":
                    return
                        "The password you provided is incorrect. (an authentication plugin rather than MediaWiki itself rejected the password)";
                case "createblocked":
                    return
                        "The wiki tried to automatically create a new account for you, but your IP address has been blocked from account creation";
                case "throttled":
                    return "You've logged in too many times in a short time.";
                case "blocked":
                    return "User is blocked";
                default:
                    return code;
            }
        }
    }
    public class MaxlagException : ApiErrorException
    {
        public int Maxlag
        { get; private set; }
        public int RetryAfter
        { get; private set; }
        public MaxlagException(ApiEdit editor, int maxlag, int retryAfter)
            : base(editor, "maxlag", "Maxlag exceeded by " + maxlag + " seconds, retry in " + retryAfter + " seconds")
        {
            Maxlag = maxlag;
            RetryAfter = retryAfter;
        }
    }
    public class AssertionFailedException : ApiException
    {
        public AssertionFailedException(ApiEdit editor, string assertion)
            : base(editor, "Assertion '" + assertion + "' failed")
        {
        }
    }
    public class InvocationException : Exception
    {
        public InvocationException(string message)
            : base(message)
        {
        }
        public InvocationException(Exception innerException)
            : this("There was a problem with an asynchronous API call", innerException)
        {
        }
        public InvocationException(string message, Exception innerException)
            : base(message, innerException)
        {
        }
    }
    public class SpamlistException : ApiException
    {
        public string URL
        { get; private set; }
        public SpamlistException(ApiEdit editor, string url)
            : base(editor, "The link '" + url + "' is blocked by spam blacklist")
        {
            URL = url;
        }
    }
    public class LoggedOffException : ApiException
    {
        public LoggedOffException(ApiEdit editor)
            : base(editor, "User is logged off")
        {
        }
    }
    public class CaptchaException : ApiException
    {
        public CaptchaException(ApiEdit editor)
            : base(editor, "Captcha required")
        {
        }
    }
    public class InterwikiException : ApiException
    {
        public InterwikiException(ApiEdit editor)
            : base(editor, "Page title contains interwiki")
        {
        }
    }
    public class NewMessagesException : ApiException
    {
        public NewMessagesException(ApiEdit editor)
            : base(editor, "You have new messages")
        {
        }
    }
}
