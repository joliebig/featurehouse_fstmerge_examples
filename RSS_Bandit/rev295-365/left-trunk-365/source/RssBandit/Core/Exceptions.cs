using System;
namespace RssBandit.Exceptions
{
    [Serializable]
    public class ExceptionEventArgs : EventArgs
    {
        public ExceptionEventArgs()
        {
        }
        public ExceptionEventArgs(Exception exception, string theErrorMessage)
        {
            failureException = exception;
            errorMessage = theErrorMessage;
        }
        private Exception failureException;
        public Exception FailureException
        {
            get
            {
                return failureException;
            }
            set
            {
                failureException = value;
            }
        }
        private string errorMessage;
        public string ErrorMessage
        {
            get
            {
                return errorMessage;
            }
            set
            {
                errorMessage = value;
            }
        }
    }
    [Serializable]
    public class FeedExceptionEventArgs : ExceptionEventArgs
    {
        public FeedExceptionEventArgs()
        {
        }
        public FeedExceptionEventArgs(Exception exception, string link, string theErrorMessage)
            : base(exception, theErrorMessage)
        {
            feedLink = link;
        }
        private string feedLink;
        public string FeedLink
        {
            get
            {
                return feedLink;
            }
            set
            {
                feedLink = value;
            }
        }
    }
}
