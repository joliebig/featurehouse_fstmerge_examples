using System;
using System.Collections.Generic;
using System.CodeDom.Compiler;
using System.Reflection;
using System.IO;
namespace WikiFunctions.CustomModules
{
    public abstract class CustomModuleCompiler
    {
        static CustomModuleCompiler()
        {
            AppDomain.CurrentDomain.AssemblyResolve += ResolveAssembly;
        }
        public abstract string Name
        { get; }
        public abstract string CodeStart
        { get; }
        public abstract string CodeEnd
        { get; }
        public abstract string CodeExample
        { get; }
        public virtual bool CanHandleLanguage(string language)
        {
            return Name == language;
        }
        public virtual CompilerResults Compile(string sourceCode, CompilerParameters parameters)
        {
            var src = CodeStart + sourceCode + "\r\n" + CodeEnd;
            return Compiler.CompileAssemblyFromSource(parameters, src);
        }
        public override string ToString()
        {
            return Name;
        }
        protected CodeDomProvider Compiler;
        public static CustomModuleCompiler[] GetList()
        {
            var modules = new List<CustomModuleCompiler>();
            modules.Add(new CSharpCustomModule());
            AddToList(modules, typeof(VbModuleCompiler));
            return modules.ToArray();
        }
        private static void AddToList(List<CustomModuleCompiler> modules, Type type)
        {
            try
            {
                modules.Add((CustomModuleCompiler)Instantiate(type));
            }
            catch { }
        }
        protected static object Instantiate(Type type)
        {
            return type.GetConstructor(new Type[] { }).Invoke(new Type[] { });
        }
        protected static object Instantiate(Assembly asm, string typeName)
        {
            return asm.CreateInstance(typeName);
        }
        static readonly Dictionary<string, string> ResolvablePaths = new Dictionary<string, string>();
        protected static Assembly LoadAssembly(string path, string dependantAssembliesPrefix)
        {
            var dir = Path.GetDirectoryName(path);
            ResolvablePaths[dependantAssembliesPrefix] = dir;
            var asm = Assembly.LoadFile(path);
            if (asm == null) throw new FileNotFoundException("Can't find assembly", path);
            return asm;
        }
        static Assembly ResolveAssembly(Object sender, ResolveEventArgs args)
        {
            var name = new AssemblyName(args.Name);
            foreach (var p in ResolvablePaths)
            {
                if (name.Name.StartsWith(p.Key))
                    return Assembly.LoadFile(Path.Combine(p.Value, name.Name + ".dll"));
            }
            return null;
        }
    }
}
