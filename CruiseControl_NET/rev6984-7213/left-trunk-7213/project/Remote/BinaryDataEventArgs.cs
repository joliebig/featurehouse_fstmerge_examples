namespace ThoughtWorks.CruiseControl.Remote
{
    using System;
    using System.ComponentModel;
    public class BinaryDataEventArgs
        : AsyncCompletedEventArgs
    {
        public BinaryDataEventArgs(byte[] data, Exception error, bool cancelled, object userState)
            : base(error, cancelled, userState)
        {
            this.Data = data;
        }
        public byte[] Data { get; private set; }
    }
}
