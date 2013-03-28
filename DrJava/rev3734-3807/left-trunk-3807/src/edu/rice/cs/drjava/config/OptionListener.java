

package edu.rice.cs.drjava.config;


public interface OptionListener<T> {
    public void optionChanged(OptionEvent<T> oce);
}
