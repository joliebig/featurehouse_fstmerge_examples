using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using Exortech.NetReflector;
using System.Reflection;
namespace Validator
{
    public partial class VersionInformationForm
        : Form
    {
        public VersionInformationForm()
        {
            InitializeComponent();
        }
        public void LoadInformation(NetReflectorTypeTable typeTable)
        {
            var assemblies = new Dictionary<Assembly, SortedDictionary<string, Type> >();
            foreach (IXmlTypeSerialiser type in typeTable)
            {
                SortedDictionary<string, Type> types;
                var assembly = type.Type.Assembly;
                if (assemblies.ContainsKey(assembly))
                {
                    types = assemblies[assembly];
                }
                else
                {
                    types = new SortedDictionary<string, Type>();
                    assemblies.Add(assembly, types);
                }
                types.Add(type.Attribute.Name, type.Type);
            }
            foreach (var assembly in assemblies)
            {
                var name = assembly.Key.GetName();
                var parentNode = new TreeNode(name.Name + " [" + name.Version.ToString() + "]");
                this.versionInformation.Nodes.Add(parentNode);
                foreach (var type in assembly.Value)
                {
                    parentNode.Nodes.Add(new TreeNode(type.Key, 1, 1));
                }
            }
        }
        private void closeButton_Click(object sender, EventArgs e)
        {
            this.Close();
        }
    }
}
