

package edu.rice.cs.drjava.config;


public interface ParseStrategy<T> {
    public abstract T parse(String val);
}
