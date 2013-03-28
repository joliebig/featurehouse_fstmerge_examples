

package koala.dynamicjava.tree;



public class CharacterLiteral extends Literal {
  
  public CharacterLiteral(String rep) {
    this(rep, SourceInfo.NONE);
  }
  
  
  public CharacterLiteral(String rep,
                          SourceInfo si) {
    super(rep,
          new Character(decodeCharacter(rep)),
          char.class,
          si);
  }
  
  
  private static char decodeCharacter(String rep) {
    if (rep.charAt(0) != '\'' || rep.charAt(rep.length()-1) != '\'') {
      throw new IllegalArgumentException("Malformed character literal");
    }
    if (rep.length() == 3) {
      return rep.charAt(1);
    }
    char c;
    
    switch (c = rep.charAt(2)) {
      case 'n' : return '\n';
      case 't' : return '\t';
      case 'b' : return '\b';
      case 'r' : return '\r';
      case 'f' : return '\f';
      default  :
        if (Character.isDigit(c)) {
        int v = 0;
        for (int i = 2; i < rep.length()-1; i++) {
          v = (v * 7) + Integer.parseInt(""+rep.charAt(i));
        }
        return (char)v;
      } else {
        return c;
      }
    }
  }
 
}
