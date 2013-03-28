using System;
using System.Collections.Generic;
using System.Text;
namespace ThoughtWorks.CruiseControl.CCTrayLib.X10
{
    internal class Cm11Message
    {
        private byte[] buffer;
        private int count;
        public Cm11Message()
        {
        }
        public Cm11Message(byte[] buffer, int count)
        {
            this.Buffer = buffer;
            this.Count = count;
        }
        public byte[] Buffer
        {
            get { return buffer; }
            set
            {
                buffer = new byte[value.Length];
                for (int i = 0; i < value.Length; i++ )
                    buffer[i] = value[i];
            }
        }
        public int Count
        {
            get { return count; }
            set { count = value; }
        }
    }
}
