

package com.mimer.ws.validateSQL;


public class ValidatorResult {
    
    
    private String data;
    
    
    private int standard;
    
    
    public ValidatorResult() {
    }
    
    
    public String getData() {
        return this.data;
    }
    
    
    public void setData(String data) {
        this.data = data;
    }
    
    
    public int getStandard() {
        return this.standard;
    }
    
    
    public void setStandard(int standard) {
        this.standard = standard;
    }
    
    public String toString() {
        return "standard = " + this.standard +
               " (0 = not standard, 1 = Core, 2 = Core plus extensions)\n" +
               "\ndata = " + this.data;
    }
    
}