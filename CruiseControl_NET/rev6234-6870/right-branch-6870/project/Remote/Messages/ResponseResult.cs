using System;
namespace ThoughtWorks.CruiseControl.Remote.Messages
{
    [Serializable]
    public enum ResponseResult
    {
        Success,
        Failure,
        Warning,
    }
}
