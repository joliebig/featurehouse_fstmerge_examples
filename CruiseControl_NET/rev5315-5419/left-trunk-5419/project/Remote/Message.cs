using System;
using System.Xml.Serialization;
namespace ThoughtWorks.CruiseControl.Remote
{
 [Serializable]
    [XmlRoot("message")]
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
        [XmlText]
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
