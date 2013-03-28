
package genj.option;


import genj.util.Registry;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;


public abstract class MultipleChoiceOption extends PropertyOption {

  
  private PropertyOption option;

  
  protected MultipleChoiceOption(PropertyOption option) {
    super(option.instance, option.getProperty());
    this.option = option;
  }

  
  public void restore(Registry r) {
    option.restore(r);
  }
  public void restore() {
    option.restore();
  }

  
  public void persist(Registry r) {
    option.persist(r);
  }
  public void persist() {
    option.persist();
  }

  
  public String getName() {
    return option.getName();
  }

  
  public void setName(String set) {
    option.setName(set);
  }

  
  public String getToolTip() {
    return option.getToolTip();
  }

  
  public void setToolTip(String set) {
    option.setToolTip(set);
  }

  
  public Object getValue() {
    return option.getValue();
  }

  
  public void setValue(Object set) {
    option.setValue(set);
  }

  
  public OptionUI getUI(OptionsWidget widget) {
    return new UI();
  }


  
  protected int getIndex() {
    return ((Integer)option.getValue()).intValue();
  }

  
  protected void setIndex(int i) {
    option.setValue(new Integer(i));
  }

  
  protected Object getChoice() {
    Object[] choices = getChoices();
    int i = getIndex();
    return i<0||i>choices.length-1 ? null : choices[i];
  }

  
  public final Object[] getChoices() {
    try  {
      return getChoicesImpl();
    } catch (Throwable t) {
      return new Object[0];
    }
  }

  
  protected abstract Object[] getChoicesImpl() throws Throwable;

  
  public class UI extends JComboBox implements OptionUI {

    
    private UI() {
      Object[] choices = getChoices();
      setModel(new DefaultComboBoxModel(choices));
      int index = getIndex();
      if (index<0||index>choices.length-1)
        index = -1;
      setSelectedIndex(index);
    }

    
    public JComponent getComponentRepresentation() {
      return this;
    }

    
    public String getTextRepresentation() {
      Object result = getChoice();
      return result!=null ? result.toString() : "";
    }

    
    public void endRepresentation() {
      setIndex(getSelectedIndex());
    }

  } 

} 