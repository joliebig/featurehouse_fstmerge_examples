

package genj.plugin.calculator;

import java.text.DecimalFormat;
import java.text.NumberFormat;


public class CalculatorEngine {

    private StringBuffer display    = new StringBuffer( 64 );
    private DecimalFormat df        = (DecimalFormat)NumberFormat.getInstance();
    private boolean newOp           = false;
    private boolean inDecimals      = false;
        
    private double  value;          
    private double  keep;           
    private int     toDo;           
    private int     decimalCount;   
                                    

    
    public CalculatorEngine(){
        super();
        df.setMaximumFractionDigits( 15 );
    }

    
    
    
    public void digit(final int n ){
        
        
        
        if( newOp ){
            display.delete( 0, display.length() );
            newOp = false;
        }
        
        char c = (char)n;
        
        if( c == '.' ){
            display.append( '.' );
            inDecimals = true;
        }else if( !inDecimals ){
            display.append( n );
        }else{
            if( decimalCount < 16 ){
                display.append( n );
                decimalCount++;
            }
        }
        
        try{
            value = Double.parseDouble( display.toString() );
        }catch( NumberFormatException e ){
            value = Double.parseDouble( "0.0" );
        }
    }
    
    
    
             
    public void add(){
        binaryOperation( "+" );
    }
    
    
    public void subtract(){
        binaryOperation( "-" );
    }
    
    
    public void multiply(){
        binaryOperation( "*" );
    }
    
    
    public void divide(){
        binaryOperation( "/" );
    }

     
    public void and(){
        binaryOperation( "&" );
    }
    
    
    public void or(){
        binaryOperation( "|" );     
    }
    
    
    public void xor(){
        binaryOperation( "^" );
    }
    
    
    public void leftShift(){
        binaryOperation( "<" );
    }
    
    
    public void rightShift(){
        binaryOperation( ">" );
    }
    
    
    public void mod(){
        binaryOperation( "m" );
    }
    
    
    public void pow(){
        binaryOperation( "p" );     
    }

    
    public void equals(){
        compute();
        toDo = 0;
        newOp = true;       
    }
    
    
    private void binaryOperation( final String op ){
        
        if( toDo == 0 ){
            keep = value;
        }else{
            compute();
        }
        
        value = 0;
        toDo = op.hashCode();
        resetDecimals();
        setDisplay();       
    }
    
         
    private void compute(){
        
        switch( toDo ){
            case '+':   value = keep + value;   break;
            case '-':   value = keep - value;   break;
            case '*':   value = keep * value;   break;
            case '/':
                if( value != 0 ){           
                    value = keep / value;
                }
            case '&':   value = (int)keep & (int)value;     break;
            case '|':   value = (int)keep | (int)value;     break;
            case '^':   value = (int)keep ^ (int)value;     break;
            case '<':   value = (int)keep << (int)value;    break;
            case '>':   value = (int)keep >> (int)value;    break;
            case 'm':   value = keep % value;               break;
            case 'p':   value = Math.pow( keep, value );    break;                                  
        }       
                
        keep = value;
        setDisplay();       
    }
    
    
    
         
    public void sqrt(){
        value = Math.sqrt( value );
        unaryOperation();
    }
    
    
    public void sign(){
        value = value * -1;
        unaryOperation();
    }
    
    
    public void percent(){
        value = value / 100;
        unaryOperation();
    }
    
    
    public void reciprocal(){
        if( value > 0 ){
            value = 1 / value;
        }else{
            value = 0;
        }
        unaryOperation();
    }       
    
    
    public void sin(){
        value = Math.sin( value );
        unaryOperation();
    }
    
    
    public void cos(){
        value = Math.cos( value );
        unaryOperation();
    }
    
    
    public void tan(){
        value = Math.tan( value );
        unaryOperation();
    }
    
    
    public void asin(){
        value = Math.asin( value );
        unaryOperation();
    }
    
    
    public void acos(){
        value = Math.acos( value );
        unaryOperation();
    }
    
    
    public void atan(){
        value = Math.atan( value );
        unaryOperation();
    }

    
    public void log(){
        value = Math.log( value );
        unaryOperation();
    }

    
    public void degrees(){
        value = Math.toDegrees( value );
        unaryOperation();
    }

    
    public void radians(){
        value = Math.toRadians( value );
        unaryOperation();
    }

    
    
    private void unaryOperation(){
        newOp = true;
        setDisplay();
    }
        
      
    
    
    public void backspace(){
        if (display.length()>0)
        	display.deleteCharAt( display.length() - 1 );
        if (display.length()==0)
        	clearEntry();
        else {
        	value = Double.parseDouble( display.toString() );
        	setDisplay();
        }
    }
    
         
    public void clear(){
        display.delete( 0, display.length() );
        value = 0;
        keep = 0;
        toDo = 0;
        resetDecimals();
    }
    
    
    public void clearEntry(){
        display.delete( 0, display.length() );
        value = 0;
        resetDecimals();
    }
    
     
    private void resetDecimals(){
        inDecimals = false;
        decimalCount = 0;
    }
    
                     
    private void setDisplay(){
        if( value == 0 ){
            display.delete( 0, display.length() );
        }else{
            display.replace( 0, display.length(), df.format( value ) );
        }
    }
    
             
    public String display(){        
        return display.toString();
    }   

}   
