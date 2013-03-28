using System; 
using System.Diagnostics; 
using System.Globalization; 
using System.IO; 
using System.Reflection; 
using System.Collections; 
using System.Text; 
using System.Security.Permissions; 
using System.Runtime.Serialization; 
using RssBandit.Resources; namespace  RssBandit.Utility {
	
 [Flags] 
 public enum  CommandLineArgumentTypes  {
  Required = 0x01,
  Unique = 0x02,
  Multiple = 0x04,
  Exclusive = 0x08,
  AtMostOnce = 0x00,
  MultipleUnique = Multiple | Unique,
 } 
 public class  CommandLineParser {
		
  public  CommandLineParser(Type argumentSpecification) {
   if (argumentSpecification == null) {
    throw new ArgumentNullException("argumentSpecification");
   }
   _argumentCollection = new CommandLineArgumentCollection();
   foreach (PropertyInfo propertyInfo in argumentSpecification.GetProperties(BindingFlags.Instance | BindingFlags.Public)) {
    if (propertyInfo.CanWrite || typeof(ICollection).IsAssignableFrom(propertyInfo.PropertyType)) {
     CommandLineArgumentAttribute attribute = GetCommandLineAttribute(propertyInfo);
     if (attribute is DefaultCommandLineArgumentAttribute) {
      Debug.Assert(_defaultArgument == null);
      _defaultArgument = new CommandLineArgument(attribute, propertyInfo);
     } else if (attribute != null) {
      _argumentCollection.Add(new CommandLineArgument(attribute, propertyInfo));
     }
    }
   }
   _argumentSpecification = argumentSpecification;
  }
 
  public virtual  string LogoBanner {
   get {
    StringBuilder logoBanner = new StringBuilder();
    Assembly assembly = Assembly.GetEntryAssembly();
    if (assembly == null) {
     assembly = Assembly.GetCallingAssembly();
    }
    object[] productAttributes = assembly.GetCustomAttributes(typeof(AssemblyProductAttribute), false);
    if (productAttributes.Length > 0) {
     AssemblyProductAttribute productAttribute = (AssemblyProductAttribute) productAttributes[0];
     if (productAttribute.Product != null && productAttribute.Product.Length != 0) {
      logoBanner.Append(productAttribute.Product);
     }
    } else {
     logoBanner.Append(assembly.GetName().Name);
    }
    object[] informationalVersionAttributes = assembly.GetCustomAttributes(typeof(AssemblyInformationalVersionAttribute), false);
    if (informationalVersionAttributes.Length > 0) {
     AssemblyInformationalVersionAttribute versionAttribute = (AssemblyInformationalVersionAttribute) informationalVersionAttributes[0];
     if (versionAttribute.InformationalVersion != null && versionAttribute.InformationalVersion.Length != 0) {
      logoBanner.Append(" version " + versionAttribute.InformationalVersion);
     }
    } else {
     FileVersionInfo info = FileVersionInfo.GetVersionInfo(assembly.Location);
     logoBanner.Append(" version " + info.FileVersion);
    }
    object[] copyrightAttributes = assembly.GetCustomAttributes(typeof(AssemblyCopyrightAttribute), false);
    if (copyrightAttributes.Length > 0) {
     AssemblyCopyrightAttribute copyrightAttribute = (AssemblyCopyrightAttribute) copyrightAttributes[0];
     if (copyrightAttribute.Copyright != null && copyrightAttribute.Copyright.Length != 0) {
      logoBanner.Append(" " + copyrightAttribute.Copyright);
     }
    }
    logoBanner.Append('\n');
    object[] companyAttributes = assembly.GetCustomAttributes(typeof(AssemblyCompanyAttribute), false);
    if (companyAttributes.Length > 0) {
     AssemblyCompanyAttribute companyAttribute = (AssemblyCompanyAttribute) companyAttributes[0];
     if (companyAttribute.Company != null && companyAttribute.Company.Length != 0) {
      logoBanner.Append(companyAttribute.Company);
      logoBanner.Append('\n');
     }
    }
    return logoBanner.ToString();
   }
  }
 
  public virtual  string Usage {
   get {
    StringBuilder helpText = new StringBuilder();
    Assembly assembly = Assembly.GetEntryAssembly();
    if (assembly == null) {
     assembly = Assembly.GetCallingAssembly();
    }
    if (helpText.Length > 0) {
     helpText.Append('\n');
    }
    helpText.Append("Usage : " + assembly.GetName().Name + " [options]");
    if (_defaultArgument != null) {
     helpText.Append(" <" + _defaultArgument.LongName + ">");
     if (_defaultArgument.AllowMultiple) {
      helpText.Append(" <" + _defaultArgument.LongName + ">");
      helpText.Append(" ...");
     }
    }
    helpText.Append('\n');
    helpText.Append("Options : ");
    helpText.Append('\n');
    helpText.Append('\n');
    foreach (CommandLineArgument argument in _argumentCollection) {
     string valType = "";
     if (argument.ValueType == typeof(string)) {
      valType = ":<text>";
     } else if (argument.ValueType == typeof(bool)) {
      valType = "[+|-]";
     } else if (argument.ValueType == typeof(FileInfo)) {
      valType = ":<filename>";
     } else if (argument.ValueType == typeof(int)) {
      valType = ":<number>";
     } else {
      valType = ":" + argument.ValueType.FullName;
     }
     string optionName = argument.LongName;
     if (argument.ShortName != null) {
      if (argument.LongName.StartsWith(argument.ShortName)) {
       optionName = optionName.Insert(argument.ShortName.Length, "[") + "]";
      }
      helpText.AppendFormat(CultureInfo.InvariantCulture, "  -{0,-30}{1}", optionName + valType, argument.Description);
      if (!optionName.StartsWith(argument.ShortName)) {
       helpText.AppendFormat(CultureInfo.InvariantCulture, " (Short format: /{0})", argument.ShortName);
      }
     } else {
      helpText.AppendFormat(CultureInfo.InvariantCulture, "  -{0,-30}{1}", optionName + valType, argument.Description);
     }
     helpText.Append('\n');
    }
    return helpText.ToString();
   }
  }
 
  public  bool NoArgs {
   get {
    foreach(CommandLineArgument argument in _argumentCollection) {
     if (argument.SeenValue) {
      return true;
     }
    }
    if (_defaultArgument != null) {
     return _defaultArgument.SeenValue;
    }
    return false;
   }
  }
 
  public  void Parse(string[] args, object destination) {
   if (destination == null) {
    throw new ArgumentNullException("destination");
   }
   if (!_argumentSpecification.IsAssignableFrom(destination.GetType())) {
    throw new ArgumentException("Type of destination does not match type of argument specification.");
   }
   ParseArgumentList(args);
   foreach (CommandLineArgument arg in _argumentCollection) {
    arg.Finish(destination);
   }
   if (_defaultArgument != null) {
    _defaultArgument.Finish(destination);
   }
  }
 
  private  void ParseArgumentList(string[] args) {
   if (args != null) {
    foreach (string argument in args) {
     if (argument.Length > 0) {
      switch (argument[0]) {
       case '-':
       case '/':
        int endIndex = argument.IndexOfAny(new char[] {':', '+', '-'}, 1);
        string option = argument.Substring(1, endIndex == -1 ? argument.Length - 1 : endIndex - 1);
        string optionArgument;
        if (option.Length + 1 == argument.Length) {
         optionArgument = null;
        } else if (argument.Length > 1 + option.Length && argument[1 + option.Length] == ':') {
         optionArgument = argument.Substring(option.Length + 2);
        } else {
         optionArgument = argument.Substring(option.Length + 1);
        }
        CommandLineArgument arg = _argumentCollection[option];
        if (arg == null) {
         throw new CommandLineArgumentException(string.Format(CultureInfo.InvariantCulture, "Unknown argument '{0}'", argument));
        } else {
         if (arg.IsExclusive && args.Length > 1) {
          throw new CommandLineArgumentException(string.Format(CultureInfo.InvariantCulture, "Commandline argument '-{0}' cannot be combined with other arguments.", arg.LongName));
         } else {
          arg.SetValue(optionArgument);
         }
        }
        break;
       default:
        if (_defaultArgument != null) {
         _defaultArgument.SetValue(argument);
        } else {
         throw new CommandLineArgumentException(string.Format(CultureInfo.InvariantCulture, "Unknown argument '{0}'", argument));
        }
        break;
      }
     }
    }
   }
  }
 
  private static  CommandLineArgumentAttribute GetCommandLineAttribute(PropertyInfo propertyInfo) {
   object[] attributes = propertyInfo.GetCustomAttributes(typeof(CommandLineArgumentAttribute), false);
   if (attributes.Length == 1)
    return (CommandLineArgumentAttribute) attributes[0];
   Debug.Assert(attributes.Length == 0);
   return null;
  }
 
  private  CommandLineArgumentCollection _argumentCollection;
 
  private  CommandLineArgument _defaultArgument;
 
  private  Type _argumentSpecification;

	}
	
 public class  CommandLineArgument {
		
  public  CommandLineArgument(CommandLineArgumentAttribute attribute, PropertyInfo propertyInfo) {
   _attribute = attribute;
   _propertyInfo = propertyInfo;
   _seenValue = false;
   _elementType = GetElementType(propertyInfo);
   _argumentType = GetArgumentType(attribute, propertyInfo);
   if (IsCollection || IsArray) {
    _collectionValues = new ArrayList();
   }
   Debug.Assert(LongName != null && LongName.Length > 0);
   Debug.Assert((!IsCollection && !IsArray) || AllowMultiple, "Collection and array arguments must have allow multiple");
   Debug.Assert(!Unique || (IsCollection || IsArray), "Unique only applicable to collection arguments");
  }
 
  public  Type ValueType {
   get { return IsCollection || IsArray ? _elementType : Type; }
  }
 
  public  string LongName {
   get {
    if (_attribute != null && _attribute.Name != null) {
     return _attribute.Name;
    } else {
     return _propertyInfo.Name;
    }
   }
  }
 
  public  string ShortName {
   get {
    if (_attribute != null) {
     return _attribute.ShortName;
    } else {
     return null;
    }
   }
  }
 
  public  string Description {
   get {
    if (_attribute != null) {
     return _attribute.Description;
    } else {
     return null;
    }
   }
  }
 
  public  bool IsRequired {
   get { return 0 != (_argumentType & CommandLineArgumentTypes.Required); }
  }
 
  public  bool SeenValue {
   get { return _seenValue; }
  }
 
  public  bool AllowMultiple {
   get { return (IsCollection || IsArray) && (0 != (_argumentType & CommandLineArgumentTypes.Multiple)); }
  }
 
  public  bool Unique {
   get { return 0 != (_argumentType & CommandLineArgumentTypes.Unique); }
  }
 
  public  Type Type {
   get { return _propertyInfo.PropertyType; }
  }
 
  public  bool IsCollection {
   get { return IsCollectionType(Type); }
  }
 
  public  bool IsArray {
   get { return IsArrayType(Type); }
  }
 
  public  bool IsDefault {
   get { return (_attribute != null && _attribute is DefaultCommandLineArgumentAttribute); }
  }
 
  public  bool IsExclusive {
   get { return 0 != (_argumentType & CommandLineArgumentTypes.Exclusive); }
  }
 
  [ReflectionPermission(SecurityAction.Demand, Flags=ReflectionPermissionFlag.NoFlags)] 
  public  void Finish(object destination) {
   if (IsRequired && !SeenValue) {
    throw new CommandLineArgumentException(string.Format(CultureInfo.InvariantCulture, "Missing required argument '-{0}'.", LongName));
   }
   if (IsArray) {
    _propertyInfo.SetValue(destination, _collectionValues.ToArray(_elementType), BindingFlags.Default, null, null, CultureInfo.InvariantCulture);
   } else if (IsCollection) {
    if (_propertyInfo.GetValue(destination, BindingFlags.Default, null, null, CultureInfo.InvariantCulture) == null) {
     if (!_propertyInfo.CanWrite) {
      throw new NotSupportedException(string.Format(CultureInfo.InvariantCulture, "Command-line argument '-{0}' is collection-based, but is not initialized and does not allow the collection to be initialized.", LongName));
     }
     object instance = Activator.CreateInstance(_propertyInfo.PropertyType, BindingFlags.Public | BindingFlags.Instance, null, null, CultureInfo.InvariantCulture);
     _propertyInfo.SetValue(destination, instance, BindingFlags.Default, null, null, CultureInfo.InvariantCulture);
    }
    object value = _propertyInfo.GetValue(destination, BindingFlags.Default, null, null, CultureInfo.InvariantCulture);
    MethodInfo addMethod = null;
    foreach (MethodInfo method in value.GetType().GetMethods(BindingFlags.Public | BindingFlags.Instance)) {
     if (method.Name == "Add" && method.GetParameters().Length == 1) {
      ParameterInfo parameter = method.GetParameters()[0];
      if (parameter.ParameterType != typeof(object)) {
       addMethod = method;
       break;
      }
     }
    }
    if (addMethod == null) {
     throw new NotSupportedException(string.Format(CultureInfo.InvariantCulture, "Collection-based command-line argument '-{0}' has no strongly-typed Add method.", LongName));
    } else {
     try {
      foreach (object item in _collectionValues) {
       addMethod.Invoke(value, BindingFlags.Default, null, new object[] {item}, CultureInfo.InvariantCulture);
      }
     } catch (Exception ex) {
      throw new NotSupportedException(string.Format(CultureInfo.InvariantCulture, "The signature of the Add method for the collection-based command-line argument '-{0}' is not supported.", LongName), ex);
     }
    }
   } else {
    if (_argumentValue != null) {
     _propertyInfo.SetValue(destination, _argumentValue, BindingFlags.Default, null, null, CultureInfo.InvariantCulture);
    }
   }
  }
 
  public  void SetValue(string value) {
   if (SeenValue && !AllowMultiple) {
    throw new CommandLineArgumentException(string.Format(CultureInfo.InvariantCulture, "Duplicate command-line argument '-{0}'.", LongName));
   }
   _seenValue = true;
   object newValue = ParseValue(ValueType, value);
   if (IsCollection || IsArray) {
    if (Unique && _collectionValues.Contains(newValue)) {
     throw new CommandLineArgumentException(string.Format(CultureInfo.InvariantCulture, "Duplicate value '-{0}' for command-line argument '{1}'.", value, LongName));
    } else {
     _collectionValues.Add(newValue);
    }
   } else {
    _argumentValue = newValue;
   }
  }
 
  private  object ParseValue(Type type, string stringData) {
   if ((stringData != null || type == typeof(bool)) && (stringData == null || stringData.Length > 0)) {
    try {
     if (type == typeof(string)) {
      return stringData;
     } else if (type == typeof(bool)) {
      if (stringData == null || stringData == "+") {
       return true;
      } else if (stringData == "-") {
       return false;
      }
     } else {
      if (type.IsEnum) {
       try {
        return Enum.Parse(type, stringData, true);
       } catch(ArgumentException ex) {
        string message = "Invalid value {0} for command-line argument '-{1}'. Valid values are: ";
        foreach (object value in Enum.GetValues(type)) {
         message += value.ToString() + ", ";
        }
        message = message.Substring(0, message.Length - 2) + ".";
        throw new CommandLineArgumentException(string.Format(CultureInfo.InvariantCulture, message, stringData, LongName), ex);
       }
      } else {
       System.Reflection.MethodInfo parseMethod = type.GetMethod("Parse", BindingFlags.Public | BindingFlags.Static, null, CallingConventions.Standard, new Type[] {typeof(string)}, null);
       if (parseMethod != null) {
        return parseMethod.Invoke(null, BindingFlags.Default, null, new object[] {stringData}, CultureInfo.InvariantCulture);
       } else if (type.IsClass) {
        ConstructorInfo stringArgumentConstructor = type.GetConstructor(new Type[] {typeof(string)});
        if (stringArgumentConstructor != null) {
         return stringArgumentConstructor.Invoke(BindingFlags.Default, null, new object[] {stringData}, CultureInfo.InvariantCulture);
        }
       }
      }
     }
    } catch (Exception ex) {
     throw new CommandLineArgumentException(string.Format(CultureInfo.InvariantCulture, "Invalid value '{0}' for command-line argument '-{1}'.", stringData, LongName), ex);
    }
   }
   throw new CommandLineArgumentException(string.Format(CultureInfo.InvariantCulture, "Invalid value '{0}' for command-line argument '-{1}'.", stringData, LongName));
  }
 
  private static  CommandLineArgumentTypes GetArgumentType(CommandLineArgumentAttribute attribute, PropertyInfo propertyInfo) {
   if (attribute != null) {
    return attribute.Type;
   } else if (IsCollectionType(propertyInfo.PropertyType)) {
    return CommandLineArgumentTypes.MultipleUnique;
   } else {
    return CommandLineArgumentTypes.AtMostOnce;
   }
  }
 
  private static  Type GetElementType(PropertyInfo propertyInfo) {
   Type elementType = null;
   if (propertyInfo.PropertyType.IsArray) {
    elementType = propertyInfo.PropertyType.GetElementType();
    if (elementType == typeof(object)) {
     throw new NotSupportedException(string.Format(CultureInfo.InvariantCulture, "Property {0} is not a strong-typed array.", propertyInfo.Name));
    }
   } else if (typeof(ICollection).IsAssignableFrom(propertyInfo.PropertyType)) {
    foreach (MethodInfo method in propertyInfo.PropertyType.GetMethods(BindingFlags.Public | BindingFlags.Instance)) {
     if (method.Name == "Add" && method.GetParameters().Length == 1) {
      ParameterInfo parameter = method.GetParameters()[0];
      if (parameter.ParameterType == typeof(object)) {
       throw new NotSupportedException(string.Format(CultureInfo.InvariantCulture, "Property {0} is not a strong-typed collection.", propertyInfo.Name));
      } else {
       elementType = parameter.ParameterType;
       break;
      }
     }
    }
    if (elementType == null) {
     throw new NotSupportedException(string.Format(CultureInfo.InvariantCulture, "Invalid commandline argument type for property {0}.", propertyInfo.Name));
    }
   }
   return elementType;
  }
 
  private static  bool IsCollectionType(Type type) {
   return typeof(ICollection).IsAssignableFrom(type);
  }
 
  private static  bool IsArrayType(Type type) {
   return type.IsArray;
  }
 
  private  Type _elementType;
 
  private  bool _seenValue;
 
  private  CommandLineArgumentTypes _argumentType;
 
  private  object _argumentValue;
 
  private  ArrayList _collectionValues;
 
  private  PropertyInfo _propertyInfo;
 
  private  CommandLineArgumentAttribute _attribute;

	}
	
 [Serializable] 
 public class  CommandLineArgumentCollection  : CollectionBase {
		
  public  CommandLineArgumentCollection() {
  }
 
  public  CommandLineArgumentCollection(CommandLineArgumentCollection value) {
   AddRange(value);
  }
 
  public  CommandLineArgumentCollection(CommandLineArgument[] value) {
   AddRange(value);
  }
 
  [System.Runtime.CompilerServices.IndexerName("Item")] 
  public  CommandLineArgument this[int index] {
   get {return ((CommandLineArgument)(base.List[index]));}
   set {base.List[index] = value;}
  }
 
  [System.Runtime.CompilerServices.IndexerName("Item")] 
  public  CommandLineArgument this[string name] {
   get {
    if (name != null) {
     foreach (CommandLineArgument CommandLineArgument in base.List) {
      if (name.Equals(CommandLineArgument.LongName)) {
       return CommandLineArgument;
      }
     }
     foreach (CommandLineArgument CommandLineArgument in base.List) {
      if (name.Equals(CommandLineArgument.ShortName)) {
       return CommandLineArgument;
      }
     }
    }
    return null;
   }
  }
 
  public  int Add(CommandLineArgument item) {
   return base.List.Add(item);
  }
 
  public  void AddRange(CommandLineArgument[] items) {
   for (int i = 0; (i < items.Length); i = (i + 1)) {
    Add(items[i]);
   }
  }
 
  public  void AddRange(CommandLineArgumentCollection items) {
   for (int i = 0; (i < items.Count); i = (i + 1)) {
    Add(items[i]);
   }
  }
 
  public  bool Contains(CommandLineArgument item) {
   return base.List.Contains(item);
  }
 
  public  void CopyTo(CommandLineArgument[] array, int index) {
   base.List.CopyTo(array, index);
  }
 
  public  int IndexOf(CommandLineArgument item) {
   return base.List.IndexOf(item);
  }
 
  public  void Insert(int index, CommandLineArgument item) {
   base.List.Insert(index, item);
  }
 
  public new  CommandLineArgumentEnumerator GetEnumerator() {
   return new CommandLineArgumentEnumerator(this);
  }
 
  public  void Remove(CommandLineArgument item) {
   base.List.Remove(item);
  }

	}
	
 public class  CommandLineArgumentEnumerator  : IEnumerator {
		
  internal  CommandLineArgumentEnumerator(CommandLineArgumentCollection arguments) {
   IEnumerable temp = arguments;
   _baseEnumerator = temp.GetEnumerator();
  }
 
  public  CommandLineArgument Current {
   get { return (CommandLineArgument) _baseEnumerator.Current; }
  }
 
  object IEnumerator.Current {
   get { return _baseEnumerator.Current; }
  }
 
  public  bool MoveNext() {
   return _baseEnumerator.MoveNext();
  }
 
  bool IEnumerator.MoveNext() {
   return _baseEnumerator.MoveNext();
  }
 
  public  void Reset() {
   _baseEnumerator.Reset();
  }
 
  void IEnumerator.Reset() {
   _baseEnumerator.Reset();
  }
 
  private  IEnumerator _baseEnumerator;

	}
	
 [Serializable()] 
 public sealed class  CommandLineArgumentException  : ArgumentException {
		
  public  CommandLineArgumentException() : base() {
  }
 
  public  CommandLineArgumentException(string message) : base(message) {
  }
 
  public  CommandLineArgumentException(string message, Exception innerException) : base(message, innerException) {
  }
 
  private  CommandLineArgumentException(SerializationInfo info, StreamingContext context) : base(info, context) {
  }

	}
	
 [AttributeUsage(AttributeTargets.Property, AllowMultiple = false, Inherited = true)] 
 public class  CommandLineArgumentAttribute  : Attribute {
		
  public  CommandLineArgumentAttribute(CommandLineArgumentTypes argumentType) {
   _argumentType = argumentType;
  }
 
  public  CommandLineArgumentTypes Type {
   get { return _argumentType; }
  }
 
  public  string Name {
   get { return _name; }
   set { _name = value; }
  }
 
  public  string ShortName {
   get { return _shortName; }
   set { _shortName = value; }
  }
 
  public  string Description {
   get {
    if (_descriptionIsResourceId) {
                    string s = SR.Keys.GetString(_description);
     if (s == null || s.Length == 0)
      return _description;
     else
      return s;
    } else
     return _description;
   }
   set { _description = value; }
  }
 
  public  bool DescriptionIsResourceId {
   get { return _descriptionIsResourceId; }
   set { _descriptionIsResourceId = value; }
  }
 
  private  CommandLineArgumentTypes _argumentType;
 
  private  string _name;
 
  private  string _shortName;
 
  private  string _description;
 
  private  bool _descriptionIsResourceId = false;

	}
	
 [AttributeUsage(AttributeTargets.Property, AllowMultiple = false, Inherited = true)] 
 public sealed class  DefaultCommandLineArgumentAttribute  : CommandLineArgumentAttribute {
		
  public  DefaultCommandLineArgumentAttribute(CommandLineArgumentTypes argumentType) : base(argumentType) {
  }

	}

}
