using System;
namespace ThoughtWorks.CruiseControl.Remote
{
 [Serializable]
 public class Message
    {
        private string message;
        public Message()
        {
        }
  public Message(string message)
  {
   this.message = message;
        }
        public string Text
        {
            get { return message; }
            set { message = value; }
        }
        public override string ToString()
  {
   return message;
  }
    }
}
