

package edu.rice.cs.drjava.config;


public interface FormatStrategy<T> {
    public abstract String format(T val);
}
