using System;
using System.Collections.Generic;
using System.Text;
namespace ThoughtWorks.CruiseControl.CCTrayLib.Presentation
{
    public delegate TResult Func<T, TResult>(T value);
    public delegate TResult Func<TResult>();
}
