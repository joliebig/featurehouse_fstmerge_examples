

package edu.rice.cs.drjava.config;


public class OptionEvent<T> {
  public final Option<T> option;
  public final T value;
  public OptionEvent(Option<T> option, T value) {
    this.option = option;
    this.value = value;
  }
}
