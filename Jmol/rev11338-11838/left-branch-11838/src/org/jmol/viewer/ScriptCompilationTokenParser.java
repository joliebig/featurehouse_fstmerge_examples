

package org.jmol.viewer;

import org.jmol.util.Logger;
import org.jmol.util.TextFormat;
import org.jmol.i18n.GT;

import java.util.Vector;

import javax.vecmath.Point3f;

abstract class ScriptCompilationTokenParser {
  
  

  protected String script;

  protected short lineCurrent;
  protected int iCommand;
 
  protected int ichCurrentCommand, ichComment, ichEnd;
  protected int ichToken;
  
  protected Token theToken;
  protected Token lastFlowCommand;
  protected Token tokenCommand;
  protected Token lastToken;
  protected Token tokenAndEquals;

  protected int theTok;
  protected int nTokens;
  protected int tokCommand;
  
  protected int ptNewSetModifier;
  protected boolean isNewSet;

  






  protected boolean logMessages = false;

  protected Token[] atokenInfix;
  protected int itokenInfix;

  protected boolean isSetBrace;
  protected boolean isImplicitExpression;
  protected boolean isSetOrDefine;

  private Vector ltokenPostfix;

  protected boolean isEmbeddedExpression;
  protected boolean isCommaAsOrAllowed;
  
  private Object theValue;

  protected boolean compileExpressions() {
    isEmbeddedExpression = (tokCommand != Token.nada
        && (tokCommand != Token.function || tokenCommand.intValue != Integer.MAX_VALUE) 
        && tokCommand != Token.end && !Token.tokAttrOr(tokCommand, Token.atomExpressionCommand,
            Token.implicitStringCommand));
    boolean checkExpression = isEmbeddedExpression
        || (Token.tokAttr(tokCommand, Token.atomExpressionCommand));

    
    
    if (tokAt(1) == Token.dollarsign
        && Token.tokAttr(tokCommand, Token.atomExpressionCommand))
      checkExpression = false;
    if (checkExpression && !compileExpression())
      return false;

    

    int size = atokenInfix.length;

    int nDefined = 0;
    for (int i = 1; i < size; i++) {
      if (tokAt(i) == Token.define)
        nDefined++;
    }

    size -= nDefined;
    if (isNewSet) {
      if (size == 1) {
        atokenInfix[0] = new Token(Token.function, 0, atokenInfix[0].value);
        isNewSet = false;
      }
    }

    if ((isNewSet || isSetBrace) && size < ptNewSetModifier + 2)
      return commandExpected();
    return (size == 1 || !Token.tokAttr(tokCommand, Token.noArgs) ? true
        : error(ERROR_badArgumentCount));
  }


  protected boolean compileExpression() {
    int firstToken = (isSetOrDefine && !isSetBrace ? 2 : 1);
    ltokenPostfix = new Vector();
    itokenInfix = 0;
    Token tokenBegin = null;
    if (tokCommand == Token.restrict && tokAt(1) == Token.bonds) {
      addNextToken();
      addNextToken();
    } else {
      for (int i = 0; i < firstToken && addNextToken(); i++) {
      }
    }
    while (moreTokens()) {
      if (isEmbeddedExpression) {
        while (!isExpressionNext() && addNextToken()) {
        }
        if (!moreTokens())
          break;
      }
      if (lastToken.tok == Token.define) {
        if (!clauseDefine())
          return false;
        continue;
      }
      if (!isImplicitExpression)
        addTokenToPostfix(tokenBegin = new Token(Token.expressionBegin, "implicitExpressionBegin"));
      if (!clauseOr(isCommaAsOrAllowed || !isImplicitExpression
          && tokPeek(Token.leftparen)))
        return false;
      if (!isImplicitExpression
          && !(isEmbeddedExpression && lastToken == Token.tokenCoordinateEnd)) {
        addTokenToPostfix(Token.tokenExpressionEnd);
      }
      if (moreTokens()) {
        if (tokCommand != Token.select && !isEmbeddedExpression)
          return error(ERROR_endOfExpressionExpected);
        if (tokCommand == Token.select) {
          
          
          tokenBegin.intValue = 0;
          tokCommand = Token.nada;
          isEmbeddedExpression = true;
          isImplicitExpression = true;
          isCommaAsOrAllowed = false;
        }
      }
    }
    atokenInfix = new Token[ltokenPostfix.size()];
    ltokenPostfix.copyInto(atokenInfix);
    return true;
  }

  private boolean isExpressionNext() {
    return tokPeek(Token.leftbrace) || !isImplicitExpression && tokPeek(Token.leftparen);
  }

  protected static boolean tokenAttr(Token token, int tok) {
    return token != null && Token.tokAttr(token.tok, tok);
  }
  
  private boolean moreTokens() {
    return (itokenInfix < atokenInfix.length);
  }
  
  private int tokAt(int i) {
    return (i < atokenInfix.length ? atokenInfix[i].tok : Token.nada);
  }
  
  private int tokPeek() {
    return (itokenInfix >= atokenInfix.length ? Token.nada
        : atokenInfix[itokenInfix].tok);
  }

  private boolean tokPeek(int tok) {
    return (itokenInfix < atokenInfix.length && atokenInfix[itokenInfix].tok == tok);
  }

  private int intPeek() {
    return (itokenInfix >= atokenInfix.length ? Integer.MAX_VALUE
        : atokenInfix[itokenInfix].intValue);
  }
  
  private Object valuePeek() {
    return (moreTokens() ? atokenInfix[itokenInfix].value : "");
  }
 
  
  private Token tokenNext() {
    return (itokenInfix >= atokenInfix.length ? null 
        : atokenInfix[itokenInfix++]);
  }
  
  private boolean tokenNext(int tok) {
    Token token = tokenNext();
    return (token != null && token.tok == tok);
  }

  private boolean returnToken() {
    itokenInfix--;
    return false;
  }

  
  private Token getToken() {
    theValue = ((theToken = tokenNext()) == null ? null : theToken.value);
    return theToken;
  }
  
  private boolean isToken(int tok) {
    return theToken != null && theToken.tok == tok;
  }
  
  private boolean getNumericalToken() {
    return (getToken() != null 
        && (isToken(Token.integer) || isToken(Token.decimal)));
  }
  
  private float floatValue() {
    switch (theToken.tok) {
    case Token.integer:
      return theToken.intValue;
    case Token.decimal:
      return ((Float) theValue).floatValue();
    }
    return 0;
  }

  private boolean addTokenToPostfix(int tok, Object value) {
    return addTokenToPostfix(new Token(tok, value));
  }

  private boolean addTokenToPostfix(int tok, int intValue, Object value) {
    return addTokenToPostfix(new Token(tok, intValue, value));
  }

  private boolean addTokenToPostfix(Token token) {
    if (token == null)
      return false;
    if (logMessages)
        Logger.debug("addTokenToPostfix" + token);
    ltokenPostfix.addElement(token);
    lastToken = token;
    return true;
  }

  private boolean addNextToken() {
    return addTokenToPostfix(tokenNext());
  }
  
  private boolean addNextTokenIf(int tok) {
    return (tokPeek(tok) && addNextToken());
  }
  
  private boolean addSubstituteTokenIf(int tok, Token token) {
    if (!tokPeek(tok))
      return false;
    itokenInfix++;
    return addTokenToPostfix(token);
  }
  
  boolean haveString;
  
  private boolean clauseOr(boolean allowComma) {
    haveString = false;
    if (!clauseAnd())
      return false;
    if (isEmbeddedExpression && lastToken.tok == Token.expressionEnd)
      return true;

    
    
    int tok;
    while ((tok = tokPeek())== Token.opOr || tok == Token.opXor
        || tok==Token.opToggle|| allowComma && tok == Token.comma) {
      if (tok == Token.comma && !haveString)
        addSubstituteTokenIf(Token.comma, Token.tokenOr);
      else
        addNextToken();
      if (!clauseAnd())
        return false;
    }
    return true;
  }

  private boolean clauseAnd() {
    if (!clauseNot())
      return false;
    if (isEmbeddedExpression && lastToken.tok == Token.expressionEnd)
      return true;
    while (tokPeek(Token.opAnd)) {
      addNextToken();
      if (!clauseNot())
        return false;
    }
    return true;
  }

  
  private boolean clauseNot() {
    if (tokPeek(Token.opNot)) {
      addNextToken();
      return clauseNot();
    }
    return (clausePrimitive());
  }
  
  private boolean clausePrimitive() {
    int tok = tokPeek();
    switch (tok) {
    case Token.nada:
      return error(ERROR_endOfCommandUnexpected);

    case Token.all:
    case Token.bitset:
    case Token.divide:
    case Token.helix:
    case Token.isaromatic:
    case Token.none:
      
      return addNextToken();

    case Token.string:
      haveString = true;
      return addNextToken();

    case Token.decimal:
      
      return addTokenToPostfix(Token.spec_model2, getToken().intValue, theValue);

    case Token.colon:
    case Token.identifier:
    case Token.integer:
    case Token.leftsquare:
    case Token.percent:
    case Token.seqcode:
    case Token.times:
      
      if (clauseResidueSpec())
        return true;
    
    default:
      if (Token.tokAttr(tok, Token.atomproperty)) {
        int itemp = itokenInfix;
        boolean isOK = clauseComparator(Token.tokAttr(tok, Token.predefinedset));
        if (isOK || itokenInfix != itemp)
            return isOK;
      }
      if (tok != Token.integer && !Token.tokAttr(tok, Token.predefinedset))
        break;
      return addNextToken();

    case Token.cell:
      return clauseCell();
    case Token.connected:
      return clauseConnected();
    case Token.substructure:
      return clauseSubstructure();
    case Token.within:
      return clauseWithin();

    case Token.define:
      addNextToken();
      if (tokPeek() == Token.nada)
        return error(ERROR_endOfCommandUnexpected);
      return clauseDefine();
      
    case Token.bonds:
    case Token.monitor:
      addNextToken();
      if (tokPeek(Token.bitset))
        addNextToken();
      else if (tokPeek(Token.define)) {
        addNextToken();
        return clauseDefine();
      }
      return true;
    case Token.leftparen:
      addNextToken();
      if (!clauseOr(true))
        return false;
      if (!addNextTokenIf(Token.rightparen))
        return error(ERROR_tokenExpected, ")");
      return checkForItemSelector();
    case Token.leftbrace:
      return checkForCoordinate(isImplicitExpression);
    }
    return error(ERROR_unrecognizedExpressionToken, "" + valuePeek());
  }

  private boolean checkForCoordinate(boolean isImplicitExpression) {
    
    boolean isCoordinate = false;
    int pt = ltokenPostfix.size();
    if (isImplicitExpression) {
      addTokenToPostfix(Token.tokenExpressionBegin);
      tokenNext();
    }else if (isEmbeddedExpression) {
      tokenNext();
      pt--;
    } else {
      addNextToken();
    }
    if (!clauseOr(false))
      return false;
    int n = 1;
    while (!tokPeek(Token.rightbrace)) {
        boolean haveComma = addNextTokenIf(Token.comma);
        if (!clauseOr(false))
          return (haveComma || n < 3? false : error(ERROR_tokenExpected, "}"));
        n++;
    }
    isCoordinate = (n >= 2); 
    if (isCoordinate && (isImplicitExpression || isEmbeddedExpression)) {
      ltokenPostfix.set(pt, Token.tokenCoordinateBegin);
      addTokenToPostfix(Token.tokenCoordinateEnd);
      tokenNext();
    } else if (isImplicitExpression) {
      addTokenToPostfix(Token.tokenExpressionEnd);
      tokenNext();
    } else if (isEmbeddedExpression)
      tokenNext();
    else
      addNextToken();
    return checkForItemSelector();
  }
  
  private boolean checkForItemSelector() {
    
    for (int i = 0; i < 2; i++) {
      if (!addNextTokenIf(Token.leftsquare))
        break;
      if (!clauseItemSelector())
        return false;
      if (!addNextTokenIf(Token.rightsquare))
        return error(ERROR_tokenExpected, "]");
    }
    return true;
  }
  
  
  
  
  
  

  private boolean clauseWithin() {
    addNextToken();
    if (!addNextTokenIf(Token.leftparen))
      return false;
    if (getToken() == null)
      return false;
    float distance = Float.MAX_VALUE;
    String key = null;
    boolean allowComma = true;
    switch (theToken.tok) {
    case Token.minus:
      if (getToken() == null)
        return false;
      if (theToken.tok != Token.integer)
        return error(ERROR_numberExpected);
      distance = -theToken.intValue;
      break;
    case Token.integer:
    case Token.decimal:
      distance = floatValue();
      break;
    case Token.branch:
      allowComma = false;
      
    case Token.atomType:
    case Token.atomName:
    case Token.boundbox:
    case Token.chain:
    case Token.coord:
    case Token.element:
    case Token.group:
    case Token.helix:
    case Token.hkl:
    case Token.model:
    case Token.molecule:
    case Token.plane:
    case Token.site:
    case Token.structure:
    case Token.string:
      key = (String) theValue;
      break;
    case Token.identifier:
      key = ((String) theValue).toLowerCase();
      break;
    default:
      return error(ERROR_unrecognizedParameter,"WITHIN", ": " + theToken.value);
    }
    if (key == null)
      addTokenToPostfix(Token.decimal, new Float(distance));
    else
      addTokenToPostfix(Token.string, key);

    while (true) {
      if (!addNextTokenIf(Token.comma))
        break;
      int tok = tokPeek();
      if (distance != Float.MAX_VALUE && (tok == Token.on || tok == Token.off)) {
        addTokenToPostfix(getToken());
        if (!addNextTokenIf(Token.comma))
          break;
        tok = tokPeek();
      }
      boolean isCoordOrPlane = false;
       if (key == null) {
        if (tok == Token.identifier) {
          
          getToken();
          key = ((String) theValue).toLowerCase();
          if (key.equals("hkl")) {
            isCoordOrPlane = true;
            addTokenToPostfix(Token.string, key);
          } else {
            returnToken();
          }
        } else if (tok == Token.coord || tok == Token.plane) {
          isCoordOrPlane = true;
          addNextToken();
        } else if (tok == Token.leftbrace) {
          returnToken();
          isCoordOrPlane = true;
          addTokenToPostfix(Token
              .getTokenFromName(distance == Float.MAX_VALUE ? "plane" : "coord"));
        }
        addNextTokenIf(Token.comma);
      }
      tok = tokPeek();
      if (isCoordOrPlane) {
        while (!tokPeek(Token.rightparen)) {
          switch (tokPeek()) {
          case Token.nada:
            return error(ERROR_endOfCommandUnexpected);
          case Token.leftparen:
            addTokenToPostfix(Token.tokenExpressionBegin);
            addNextToken();
            if (!clauseOr(false))
              return error(ERROR_unrecognizedParameter,"WITHIN", ": ?");
            if (!addNextTokenIf(Token.rightparen))
              return error(ERROR_tokenExpected, ", / )");
            addTokenToPostfix(Token.tokenExpressionEnd);
            break;
          case Token.define:
            addTokenToPostfix(getToken());
            if (!clauseDefine())
              return false;
            break;
          default:
            addTokenToPostfix(getToken());
          }
        }
      } else if (!clauseOr(allowComma)) {
        return error(ERROR_badArgumentCount);
      }
    }
    if (!addNextTokenIf(Token.rightparen))
      return error(ERROR_tokenExpected, ")");
    return true;
  }

  private boolean clauseConnected() {
    addNextToken();
    
    if (!addNextTokenIf(Token.leftparen)) {
      addTokenToPostfix(Token.tokenLeftParen);
      addTokenToPostfix(Token.tokenRightParen);
      return true;
    }
    while (true) {
      if (addNextTokenIf(Token.integer))
        if (!addNextTokenIf(Token.comma))
          break;
      if (addNextTokenIf(Token.integer))
        if (!addNextTokenIf(Token.comma))
          break;
      if (addNextTokenIf(Token.decimal))
        if (!addNextTokenIf(Token.comma))
          break;
      if (addNextTokenIf(Token.decimal))
        if (!addNextTokenIf(Token.comma))
          break;
      if (tokPeek() == Token.identifier || tokPeek() == Token.hbond) {
        String strOrder = (String) getToken().value;
        int intType = JmolConstants.getBondOrderFromString(strOrder);
        if (intType == JmolConstants.BOND_ORDER_NULL) {
          returnToken();
        } else {
          addTokenToPostfix(Token.string, strOrder);
          if (!addNextTokenIf(Token.comma))
            break;
        }
      }
      if (addNextTokenIf(Token.rightparen))
        return true;
      if (!clauseOr(tokPeek(Token.leftparen))) 
        return false;
      if (addNextTokenIf(Token.rightparen))
        return true;
      if (!addNextTokenIf(Token.comma))
        return false;
      if (!clauseOr(tokPeek(Token.leftparen))) 
        return false;

      break;
    }
    if (!addNextTokenIf(Token.rightparen))
      return error(ERROR_tokenExpected, ")");
    return true;
  }

  private boolean clauseSubstructure() {
    addNextToken();
    if (!addNextTokenIf(Token.leftparen))
      return false;
    if (!addNextTokenIf(Token.string))
      return error(ERROR_tokenExpected, "\"...\"");
    if (!addNextTokenIf(Token.rightparen))
      return error(ERROR_tokenExpected, ")");
    return true;
  }

  private boolean clauseItemSelector() {
    int tok;
    int nparen = 0;
    while ((tok = tokPeek()) != Token.nada && tok != Token.rightsquare) {
      addNextToken();
      if (tok == Token.leftsquare)
        nparen++;
      if (tokPeek() == Token.rightsquare && nparen-- > 0)
        addNextToken();
    }
    return true;
  }
  
  private boolean clauseComparator(boolean isOptional) {
    Token tokenAtomProperty = tokenNext();
    Token tokenComparator = tokenNext();
    if (!tokenAttr(tokenComparator, Token.comparator)) {
      if (!isOptional)
        return error(ERROR_tokenExpected, "== != < > <= >=");
      if (tokenComparator != null)
        returnToken();
      returnToken();
      return false;
    }
    if (tokenAttr(tokenAtomProperty, Token.strproperty) 
        && tokenComparator.tok != Token.opEQ && tokenComparator.tok != Token.opNE)
      return error(ERROR_tokenExpected, "== !=");
    if (getToken() == null)
      return error(ERROR_unrecognizedExpressionToken, "" + valuePeek());
    boolean isNegative = (isToken(Token.minus));
    if (isNegative && getToken() == null)
      return error(ERROR_numberExpected);
    switch (theToken.tok) {
    case Token.integer:
    case Token.decimal:
    case Token.identifier:
    case Token.string:
    case Token.leftbrace:
    case Token.define:
      break;
    default:
      return error(ERROR_numberOrVariableNameExpected);
    }
    addTokenToPostfix(tokenComparator.tok, tokenAtomProperty.tok,
        tokenComparator.value + (isNegative ? " -" : ""));
    if (tokenAtomProperty.tok == Token.property)
      addTokenToPostfix(tokenAtomProperty);
    if (isToken(Token.leftbrace)) {
      returnToken();
      return clausePrimitive();
    }
    addTokenToPostfix(theToken);
    if (theToken.tok == Token.define)
      return clauseDefine();
    return true;
  }

  private boolean clauseCell() {
    Point3f cell = new Point3f();
    tokenNext(); 
    if (!tokenNext(Token.opEQ)) 
      return error(ERROR_tokenExpected, "=");
    if (getToken() == null)
      return error(ERROR_coordinateExpected);
    
    
    if (isToken(Token.integer)) {
      int nnn = theToken.intValue;
      cell.x = nnn / 100 - 4;
      cell.y = (nnn % 100) / 10 - 4;
      cell.z = (nnn % 10) - 4;
      return addTokenToPostfix(Token.cell, cell);
    }
    if (!isToken(Token.leftbrace) || !getNumericalToken())
      return error(ERROR_coordinateExpected); 
    cell.x = floatValue();
    if (tokPeek(Token.comma)) 
      tokenNext();
    if (!getNumericalToken())
      return error(ERROR_coordinateExpected); 
    cell.y = floatValue();
    if (tokPeek(Token.comma)) 
      tokenNext();
    if (!getNumericalToken() || !tokenNext(Token.rightbrace))
      return error(ERROR_coordinateExpected); 
    cell.z = floatValue();
    return addTokenToPostfix(Token.cell, cell);
  }

  private boolean clauseDefine() {
    
    
    if (!addSubstituteTokenIf(Token.leftbrace, Token.tokenExpressionBegin))
      return addNextToken() && checkForItemSelector();
    while (moreTokens() && !tokPeek(Token.rightbrace)) {
      if (tokPeek(Token.leftbrace)) {
        if (!checkForCoordinate(true))
          return false;
      } else {
        addNextToken();
      }
    }
    return addSubstituteTokenIf(Token.rightbrace, Token.tokenExpressionEnd)
        && checkForItemSelector();
  }

  private boolean residueSpecCodeGenerated;

  private boolean generateResidueSpecCode(Token token) {
    if (residueSpecCodeGenerated)
      addTokenToPostfix(Token.tokenAnd);
    addTokenToPostfix(token);
    residueSpecCodeGenerated = true;
    return true;
  }

  private boolean clauseResidueSpec() {
    boolean specSeen = false;
    residueSpecCodeGenerated = false;
    int tok = tokPeek();
    if (tok == Token.times || tok == Token.leftsquare
        || tok == Token.identifier) {

      
      
      
      
      

      if (!clauseResNameSpec())
        return false;
      specSeen = true;
      tok = tokPeek();
    }
    boolean wasInteger = false;
    if (tok == Token.times || tok == Token.integer || tok == Token.seqcode) {
      wasInteger = (tok == Token.integer);
      
      if (tokPeek(Token.times))
        getToken();
      else if (!clauseSequenceSpec())
        return false;
      specSeen = true;
      tok = tokPeek();
    }
    if (tok == Token.colon || tok == Token.times || tok == Token.identifier
        || tok == Token.integer && !wasInteger) {
      if (!clauseChainSpec(tok))
        return false;
      specSeen = true;
      tok = tokPeek();
    }
    if (tok == Token.period) {
      if (!clauseAtomSpec())
        return false;
      specSeen = true;
      tok = tokPeek();
    }
    if (tok == Token.percent) {
      if (!clauseAlternateSpec())
        return false;
      specSeen = true;
      tok = tokPeek();
    }
    if (tok == Token.colon || tok == Token.divide) {
      if (!clauseModelSpec())
        return false;
      specSeen = true;
      tok = tokPeek();
    }
    if (!specSeen)
      return error(ERROR_residueSpecificationExpected);
    if (!residueSpecCodeGenerated) {
      
      addTokenToPostfix(Token.tokenAll);
    }
    return true;
  }

  private boolean clauseResNameSpec() {
    getToken();
    if (isToken(Token.times) || isToken(Token.nada))
      return (!isToken(Token.nada));
    if (isToken(Token.leftsquare)) {
      String strSpec = "";
      while (getToken() != null && !isToken(Token.rightsquare))
        strSpec += theValue;
      if (!isToken(Token.rightsquare))
        return false;
      if (strSpec == "")
        return true;
      int pt;
      if (strSpec.length() > 0 && (pt = strSpec.indexOf("*")) >= 0
          && pt != strSpec.length() - 1)
        return error(ERROR_residueSpecificationExpected);
      strSpec = strSpec.toUpperCase();
      return generateResidueSpecCode(new Token(Token.spec_name_pattern, strSpec));
    }

    

    if (!isToken(Token.identifier))
      return error(ERROR_identifierOrResidueSpecificationExpected);
    
    

    if (tokPeek(Token.times)) {
      String res = theValue + "*";
      getToken();
      return generateResidueSpecCode(new Token(Token.identifier, res));
    }
    return generateResidueSpecCode(theToken);
  }

  private boolean clauseSequenceSpec() {
    Token seqToken = getSequenceCode(false);
    if (seqToken == null)
      return false;
    int tok = tokPeek();
    if (tok == Token.minus || tok == Token.integer && intPeek() < 0) {
      if (tok == Token.minus) {
        tokenNext();
      } else {
         
          int i = -intPeek();
          tokenNext().intValue = i;
          returnToken();
      }
      seqToken.tok = Token.spec_seqcode_range;
      generateResidueSpecCode(seqToken);
      return addTokenToPostfix(getSequenceCode(true));
    }
    return generateResidueSpecCode(seqToken);
  }

  private Token getSequenceCode(boolean isSecond) {
    int seqcode = Integer.MAX_VALUE;
    int seqvalue = Integer.MAX_VALUE;
    int tokPeek = tokPeek();
    if (tokPeek == Token.seqcode)
      seqcode = tokenNext().intValue;
    else if (tokPeek == Token.integer)
      seqvalue = tokenNext().intValue;
    else if (!isSecond){
      return null;
      
      
    }
    return new Token(Token.spec_seqcode, seqvalue, new Integer(seqcode));
  }

  private boolean clauseChainSpec(int tok) {
    if (tok == Token.colon) {
      tokenNext();
      tok = tokPeek();
      if (isSpecTerminator(tok))
        return generateResidueSpecCode(new Token(Token.spec_chain, '\0',
            "spec_chain"));
    }
    if (tok == Token.times)
      return (getToken() != null);
    char chain;
    switch (tok) {
    case Token.integer:
      getToken();
      int val = theToken.intValue;
      if (val < 0 || val > 9)
        return error(ERROR_invalidChainSpecification);
      chain = (char) ('0' + val);
      break;
    case Token.identifier:
      String strChain = (String) getToken().value;
      if (strChain.length() != 1)
        return error(ERROR_invalidChainSpecification);
      chain = strChain.charAt(0);
      if (chain == '?')
        return true;
      break;
    default:
      return error(ERROR_invalidChainSpecification);
    }
    return generateResidueSpecCode(new Token(Token.spec_chain, chain,
        "spec_chain"));
  }

  private boolean isSpecTerminator(int tok) {
    switch (tok) {
    case Token.nada:
    case Token.divide:
    case Token.opAnd:
    case Token.opOr:
    case Token.opNot:
    case Token.comma:
    case Token.percent:
    case Token.rightparen:
      return true;
    }
    return false;
  }

  private boolean clauseAlternateSpec() {
    tokenNext();
    int tok = tokPeek();
    if (isSpecTerminator(tok))
      return generateResidueSpecCode(new Token(Token.spec_alternate, null));
    String alternate = (String) getToken().value;
    switch (theToken.tok) {
    case Token.times:
    case Token.string:
    case Token.integer:
    case Token.identifier:
      break;
    default:
      return error(ERROR_invalidModelSpecification);
    }
    
    return generateResidueSpecCode(new Token(Token.spec_alternate, alternate));
  }

  private boolean clauseModelSpec() {
    getToken();
    if (tokPeek(Token.times)) {
      getToken();
      return true;
    }
    switch (tokPeek()) {
    case Token.integer:
      return generateResidueSpecCode(new Token(Token.spec_model, new Integer(
          getToken().intValue)));
    case Token.decimal:
            return generateResidueSpecCode(new Token(Token.spec_model,
          getToken().intValue, theValue));
    case Token.comma:
    case Token.rightbrace:
    case Token.nada:
      return generateResidueSpecCode(new Token(Token.spec_model, new Integer(1)));
    }
    return error(ERROR_invalidModelSpecification);
  }

  private boolean clauseAtomSpec() {
    if (!tokenNext(Token.period))
      return error(ERROR_invalidAtomSpecification);
    if (getToken() == null)
      return true;
    String atomSpec = "";
    if (isToken(Token.integer)) {
      atomSpec += "" + theToken.intValue;
      if (getToken() == null)
        return error(ERROR_invalidAtomSpecification);
    }
    switch (theToken.tok) {
    case Token.times:
      return true;
    case Token.opIf:
    case Token.cd:
    case Token.identifier:
      break;
    default:
      return error(ERROR_invalidAtomSpecification);
    }
    atomSpec += theValue;
    if (tokPeek(Token.times)) {
      tokenNext();
      
      atomSpec += "*";
    }
    return generateResidueSpecCode(new Token(Token.spec_atom, atomSpec));
  }
  





  
  protected String errorMessage;
  protected String errorMessageUntranslated;
  protected String errorLine;
  protected String errorType;

  protected final static int ERROR_badArgumentCount  = 0;
  protected final static int ERROR_badContext  = 1;
  protected final static int ERROR_commandExpected = 2;
  protected final static int ERROR_endOfCommandUnexpected  = 4;
  protected final static int ERROR_invalidExpressionToken  = 9;
  protected final static int ERROR_missingEnd  = 11;
  protected final static int ERROR_tokenExpected  = 15;
  protected final static int ERROR_tokenUnexpected  = 16;
  protected final static int ERROR_unrecognizedParameter  = 18;
  protected final static int ERROR_unrecognizedToken  = 19;

  private final static int ERROR_coordinateExpected  = 3;
  private final static int ERROR_endOfExpressionExpected  = 5;
  private final static int ERROR_identifierOrResidueSpecificationExpected  = 6;
  private final static int ERROR_invalidAtomSpecification  = 7;
  private final static int ERROR_invalidChainSpecification  = 8;
  private final static int ERROR_invalidModelSpecification  = 10;
  private final static int ERROR_numberExpected  = 12;
  private final static int ERROR_numberOrVariableNameExpected  = 13;
  private final static int ERROR_residueSpecificationExpected  = 14;
  private final static int ERROR_unrecognizedExpressionToken  = 17;
  
  static String errorString(int iError, String value, String more,
                            boolean translated) {
    boolean doTranslate = false;
    if (!translated && (doTranslate = GT.getDoTranslate()) == true)
      GT.setDoTranslate(false);
    String msg;
    switch (iError) {
    default:
      msg = "Unknown compiler error message number: " + iError;
      break;
    case ERROR_badArgumentCount: 
      msg = GT._("bad argument count"); 
      break;
    case ERROR_badContext: 
      msg = GT._("invalid context for {0}"); 
      break;
    case ERROR_commandExpected: 
      msg = GT._("command expected"); 
      break;
    case ERROR_coordinateExpected: 
      msg = GT._("{ number number number } expected"); 
      break;
    case ERROR_endOfCommandUnexpected: 
      msg = GT._("unexpected end of script command"); 
      break;
    case ERROR_endOfExpressionExpected: 
      msg = GT._("end of expression expected"); 
      break;
    case ERROR_identifierOrResidueSpecificationExpected: 
      msg = GT._("identifier or residue specification expected"); 
      break;
    case ERROR_invalidAtomSpecification: 
      msg = GT._("invalid atom specification"); 
      break;
    case ERROR_invalidChainSpecification: 
      msg = GT._("invalid chain specification"); 
      break;
    case ERROR_invalidExpressionToken: 
      msg = GT._("invalid expression token: {0}"); 
      break;
    case ERROR_invalidModelSpecification: 
      msg = GT._("invalid model specification"); 
      break;
    case ERROR_missingEnd: 
      msg = GT._("missing END for {0}"); 
      break;
    case ERROR_numberExpected: 
      msg = GT._("number expected"); 
      break;
    case ERROR_numberOrVariableNameExpected: 
      msg = GT._("number or variable name expected"); 
      break;
    case ERROR_residueSpecificationExpected: 
      msg = GT._("residue specification (ALA, AL?, A*) expected"); 
      break;
    case ERROR_tokenExpected: 
      msg = GT._("{0} expected"); 
      break;
    case ERROR_tokenUnexpected: 
      msg = GT._("{0} unexpected"); 
      break;
    case ERROR_unrecognizedExpressionToken: 
      msg = GT._("unrecognized expression token: {0}"); 
      break;
    case ERROR_unrecognizedParameter: 
      msg = GT._("unrecognized {0} parameter"); 
      break;
    case ERROR_unrecognizedToken: 
      msg = GT._("unrecognized token: {0}"); 
      break;
    }
    if (msg.indexOf("{0}") < 0) {
      if (value != null)
        msg += ": " + value;
    } else {
      msg = TextFormat.simpleReplace(msg, "{0}", value);
      if (msg.indexOf("{1}") >= 0)
        msg = TextFormat.simpleReplace(msg, "{1}", more);
      else if (more != null)
        msg += ": " + more;
    }
    if (!translated)
      GT.setDoTranslate(doTranslate);
    return msg;
  }
  
  protected boolean commandExpected() {
    ichToken = ichCurrentCommand;
    return error(ERROR_commandExpected);
  }

  protected boolean error(int error) {
    return error(error, null, null);
  }

  protected boolean error(int error, String value) {
    return error(error, value, null);
  }
  
  protected boolean error(int iError, String value, String more) {
    String strError = errorString(iError, value, more, true);
    String strUntranslated = (GT.getDoTranslate() ? errorString(iError, value, more, false) : null);
    return error(strError, strUntranslated);
  }

  protected boolean error(String errorMessage, String strUntranslated) {
    this.errorMessage = errorMessage;
    errorMessageUntranslated = strUntranslated;
    return false;
  }

}
