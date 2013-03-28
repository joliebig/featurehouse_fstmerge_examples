package net.sourceforge.pmd;


public interface NumericPropertyDescriptor<T extends Object > extends PropertyDescriptor<T> {

    
    Number upperLimit();
    
    
    Number lowerLimit();
}
