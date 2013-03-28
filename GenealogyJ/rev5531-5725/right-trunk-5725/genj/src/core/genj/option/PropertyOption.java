
package genj.option;

import genj.util.Registry;
import genj.util.Resources;
import genj.util.swing.FileChooserWidget;
import genj.util.swing.FontChooser;
import genj.util.swing.TextFieldWidget;

import java.awt.Font;
import java.awt.font.TextAttribute;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JCheckBox;
import javax.swing.JComponent;


public abstract class PropertyOption extends Option {

  
  protected String property;

  
  protected Object instance;

  
  public static List introspect(Object instance) {

    
    List result = new ArrayList();
    Set beanattrs = new HashSet();

    
    try {
      BeanInfo info = Introspector.getBeanInfo(instance.getClass());
      PropertyDescriptor[] properties = info.getPropertyDescriptors();
      for (int p=0; p<properties.length; p++) {

        PropertyDescriptor property = properties[p];

        try {
          
          if (property.getReadMethod()==null||property.getWriteMethod()==null)
            continue;

          
          if (!Impl.isSupportedArgument(property.getPropertyType()))
            continue;

          
          property.getReadMethod().invoke(instance, (Object[])null);

          
          Option option = BeanPropertyImpl.create(instance, property);

          
          result.add(option);

          
          beanattrs.add(property.getName());
        } catch (Throwable t) {
        }
      }
    } catch (IntrospectionException e) {
    }

    
    Field[] fields = instance.getClass().getFields();
    for (int f=0;f<fields.length;f++) {

      Field field = fields[f];
      Class type = field.getType();

      
      if (beanattrs.contains(field.getName()))
        continue;

      
      int mod = field.getModifiers();
      if (Modifier.isFinal(mod) || Modifier.isStatic(mod))
        continue;
      try {
        field.get(instance);
      } catch (Throwable t) {
        continue;
      }

      
      if (!Impl.isSupportedArgument(type))
        continue;

      
      Option option = FieldImpl.create(instance, field);

      
      result.add(option);

      
    }

    
    return result;
  }

  
  protected PropertyOption(Object instance, String property) {
    this.instance = instance;
    this.property = property;
  }

  
  public abstract Object getValue();

  
  public abstract void setValue(Object set);

  
  public abstract void setName(String set);

  
  public abstract void setToolTip(String set);

  
  public String getProperty() {
    return property;
  }

  
  public String getCategory() {
    String result = super.getCategory();
    if (result==null) {
      
      Resources resources = Resources.get(instance);
      result = resources.getString("options", false);
      if (result!=null)
        super.setCategory(result);
    }
    return result;
  }

  
  protected static class FontUI implements OptionUI {

    
    private FontChooser chooser = new FontChooser();

    
    private PropertyOption option;

    
    public FontUI(PropertyOption option) {
      this.option = option;
    }

    
    public String getTextRepresentation() {
      Font font = (Font)option.getValue();
      return font==null ? "..." : font.getFamily() + "," + font.getSize();
    }

    
    public JComponent getComponentRepresentation() {
      chooser.setSelectedFont((Font)option.getValue());
      return chooser;
    }

    
    public void endRepresentation() {
      option.setValue(chooser.getSelectedFont());
    }

  } 

  
  protected static class FileUI implements OptionUI {

    
    private FileChooserWidget chooser = new FileChooserWidget();

    
    private PropertyOption option;

    
    public FileUI(PropertyOption option) {
      this.option = option;
      chooser.setFile((File)option.getValue());
    }

    
    public String getTextRepresentation() {
      return chooser.getFile().toString();
    }

    
    public JComponent getComponentRepresentation() {
      return chooser;
    }

    
    public void endRepresentation() {
      option.setValue(chooser.getFile());
    }

  } 

  
  protected static class BooleanUI extends JCheckBox implements OptionUI {
    
    private PropertyOption option;

    
    public BooleanUI(PropertyOption option) {
      this.option = option;
      setOpaque(false);
      setHorizontalAlignment(JCheckBox.LEFT);
      Boolean value = (Boolean)option.getValue();
      if (value.booleanValue())
        setSelected(true);
    }
    
    public String getTextRepresentation() {
      return null;
    }
    
    public JComponent getComponentRepresentation() {
      return this;
    }
    
    public void endRepresentation() {
      option.setValue(isSelected()?Boolean.TRUE : Boolean.FALSE);
    }
  } 

  
  protected static class SimpleUI extends TextFieldWidget implements OptionUI {
    
    private PropertyOption option;

    
    public SimpleUI(PropertyOption option) {
      this.option = option;
      Object value = option.getValue();
      setText(value!=null?value.toString():"");
      setSelectAllOnFocus(true);
      setColumns(12);
    }
    
    public String getTextRepresentation() {
      return getText();
    }
    
    public JComponent getComponentRepresentation() {
      return this;
    }
    
    public void endRepresentation() {
      option.setValue(getText());
    }
  } 

  
  private static abstract class Impl extends PropertyOption {

    
    protected Class type;

    
    private String name;

    
    private String toolTip;

    
    private Mapper mapper;

    
    protected Impl(Object instance, String property, Class type) {
      super(instance, property);
      this.type     = type;

      
      this.mapper   = type==Font.class ? new FontMapper() : new Mapper();
    }

    
    public String getName() {
      if (name==null) {
        
        Resources resources = Resources.get(instance);
        name = resources.getString("option."+property, false);
        if (name==null) {
          name = resources.getString(property, false);
          if (name==null)
            name = property;
        }
      }
      
      return name;
    }

    
    public void setName(String set) {
      name = set;
    }

    
    public String getToolTip() {
      if (toolTip==null) {
        
        Resources resources = Resources.get(instance);
        toolTip = resources.getString("option." + property + ".tip", false);
        if (toolTip==null) {
          toolTip = resources.getString(property + ".tip", false);
        }
      }
      
      return toolTip;
    }

    
    public void setToolTip(String set) {
      toolTip = set;
    }

    
    public void restore(Registry registry) {
      String value = registry.get(instance.getClass().getName() + '.' + getProperty(), (String)null);
      if (value!=null)
        setValue(value);
    }

    
    public void persist(Registry registry) {
      Object value = getValue();
      if (value!=null)
        registry.put(instance.getClass().getName() + '.' + getProperty(), value.toString());
    }

    
    public OptionUI getUI(OptionsWidget widget) {
      
      
      if (Font.class.isAssignableFrom(type))
        return new FontUI(this);
      
      if (type==Boolean.TYPE)
        return new BooleanUI(this);
      
      if (type==File.class)
        return new FileUI(this);
      
      return new SimpleUI(this);
    }

    
    public final Object getValue() {
      try {
        
        return getValueImpl();
      } catch (Throwable t) {
        return null;
      }
    }

    
    protected abstract Object getValueImpl() throws Throwable;

    
    public final void setValue(Object value) {

      
      try {
        Object old = getValueImpl();
        if (old==value)
          return;
        if (old!=null&&value!=null&&old.equals(value))
          return;

        setValueImpl(mapper.toObject(value, type));

      } catch (Throwable t) {
        
      }
      
      fireChangeNotification();
    }

    
    protected abstract void setValueImpl(Object value) throws Throwable;

    
    private static boolean isSupportedArgument(Class type) {
      return
        Font.class.isAssignableFrom(type)   ||
        File.class.isAssignableFrom(type)   ||
        String.class.isAssignableFrom(type) ||
        Float.TYPE.isAssignableFrom(type) ||
        Double.TYPE.isAssignableFrom(type) ||
        Long.TYPE.isAssignableFrom(type) ||
        Integer.TYPE.isAssignableFrom(type) ||
        Boolean.TYPE.isAssignableFrom(type);
    }

  } 

  
  private static class FieldImpl extends Impl {

    
    protected Field field;

    
    protected static Option create(final Object instance, Field field) {
      
      PropertyOption result = new FieldImpl(instance, field);
      
      if (field.getType()==Integer.TYPE) try {
        final Field choices = instance.getClass().getField(field.getName()+"s");
        if (choices.getType().isArray())
          
          return new MultipleChoiceOption(result) {
            public Object[] getChoicesImpl() throws Throwable {
              return (Object[])choices.get(instance);
            }
          };
      } catch (Throwable t) {
      }
      
      return result;
    }

    
    private FieldImpl(Object instance, Field field) {
      super(instance, field.getName(), field.getType());
      this.field = field;
    }

    
    protected Object getValueImpl() throws Throwable {
      return field.get(instance);
    }

    
    protected void setValueImpl(Object value) throws Throwable {
      field.set(instance, value);
    }

  } 

  
  private static class BeanPropertyImpl extends Impl {

    
    PropertyDescriptor descriptor;

    
    protected static Option create(final Object instance, PropertyDescriptor descriptor) {
      
      PropertyOption result = new BeanPropertyImpl(instance, descriptor);
      
      if (descriptor.getPropertyType()==Integer.TYPE) try {
        final Method choices = instance.getClass().getMethod(descriptor.getReadMethod().getName()+"s", (Class[])null);
        if (choices.getReturnType().isArray())
          
          return new MultipleChoiceOption(result) {
            public Object[] getChoicesImpl() throws Throwable {
              return (Object[])choices.invoke(instance, (Object[])null);
            }
          };
      } catch (Throwable t) {
      }
      
      return result;
    }

    
    private BeanPropertyImpl(Object instance, PropertyDescriptor property) {
      super(instance, property.getName(), property.getPropertyType());
      this.descriptor = property;
    }

    
    protected Object getValueImpl() throws Throwable {
      return descriptor.getReadMethod().invoke(instance, (Object[])null);
    }

    
    protected void setValueImpl(Object value) throws Throwable {
      descriptor.getWriteMethod().invoke(instance, new Object[]{value} );
    }

  } 

  
  private static class Mapper {

    
    private static Class box(Class type) {
      if (type == boolean.class) return Boolean.class;
      if (type == byte.class) return Byte.class;
      if (type == char.class) return Character.class;
      if (type == short.class) return Short.class;
      if (type == int.class) return Integer.class;
      if (type == long.class) return Long.class;
      if (type == float.class) return Float.class;
      if (type == double.class) return Double.class;
      return type;
    }


    protected String toString(Object object) {
      return object!=null ? object.toString() : "";
    }

    protected Object toObject(Object object, Class expected) {
      
      expected = box(expected);
      
      if (object==null||object.getClass()==expected)
        return object;
      
      try {
        return expected.getConstructor(new Class[]{object.getClass()})
          .newInstance(new Object[]{ object });
      } catch (Throwable t) {
        throw new IllegalArgumentException("can't map "+object+" to expected");
      }
    }
  } 

  
  private static class FontMapper extends Mapper{

    private final static String
    FAMILY = "family=",
    STYLE  = "style=",
    SIZE   = "size=";

    
    protected Object toObject(Object object, Class expected) {

      if (expected!=Font.class||object==null||object.getClass()!=String.class)
        return super.toObject(object, expected);
      String string = (String)object;

      
      Map map = new HashMap();

      String family = getAttribute(string, FAMILY);
      if (family==null)
        family = "SansSerif";
      map.put(TextAttribute.FAMILY, family);

      try {
        map.put(TextAttribute.SIZE, new Float(getAttribute(string, SIZE)));
      } catch (Throwable t) {
        map.put(TextAttribute.SIZE, new Float(11F));
      }

      
      return new Font(map);
    }

    protected String getAttribute(String string, String key) {

      int i = string.indexOf(key);
      if (i<0)
        return null;
      i += key.length();

      int j = i;
      for (;j<string.length();j++) {
        char c = string.charAt(j);
        if (!(Character.isLetterOrDigit(c)||Character.isWhitespace(c)))
          break;
      }

      return j<i ? null : string.substring(i, j);
    }

  }

} 
