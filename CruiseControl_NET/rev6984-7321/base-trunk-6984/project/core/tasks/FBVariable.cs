namespace ThoughtWorks.CruiseControl.Core.Tasks
{
    using Exortech.NetReflector;
    [ReflectorType("FBVariable")]
    public class FBVariable
    {
        private string _name;
        private string _value;
        [ReflectorProperty("name")]
        public string Name
        {
            get { return _name; }
            set { _name = value; }
        }
        [ReflectorProperty("value")]
        public string Value
        {
            get { return _value; }
            set { _value = value; }
        }
        public override string ToString()
        {
            return string.Format("FB Variable: {0} = {1}", Name, Value);
        }
        public FBVariable(string name, string avalue)
        {
            _name = name;
            _value = avalue;
        }
        public FBVariable() { }
    }
}
