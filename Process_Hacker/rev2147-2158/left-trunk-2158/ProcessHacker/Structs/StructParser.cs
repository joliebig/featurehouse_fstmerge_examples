using System;
using System.Collections.Generic;
using System.Text;
using ProcessHacker.Common;
namespace ProcessHacker.Structs
{
    public class ParserException : Exception
    {
        public ParserException(string fileName, int line, string message) :
            base((new System.IO.FileInfo(fileName)).Name + ": " + line.ToString() + ": " + message) { }
    }
    public class StructParser
    {
        private Dictionary<string, StructDef> _structs;
        private string _fileName = "";
        private int _lineNumber = 1;
        private Dictionary<string, FieldType> _typeDefs = new Dictionary<string, FieldType>();
        private bool _eatResult = false;
        public Dictionary<string, StructDef> Structs
        {
            get { return _structs; }
        }
        public StructParser(Dictionary<string, StructDef> structs)
        {
            _structs = structs;
            foreach (string s in Enum.GetNames(typeof(FieldType)))
                if (s != "Pointer")
                    _typeDefs.Add(s.ToLower(), (FieldType)Enum.Parse(typeof(FieldType), s));
        }
        private FieldType GetType(string typeName)
        {
            if (_typeDefs.ContainsKey(typeName))
                return _typeDefs[typeName];
            else
                throw new ParserException(_fileName, _lineNumber, "Unknown identifier '" + typeName + "' (type name)");
        }
        private bool IsTypePointer(FieldType type)
        {
            return (type & FieldType.Pointer) != 0;
        }
        public void Parse(string fileName)
        {
            List<StructDef> defs = new List<StructDef>();
            int i = 0;
            string text = System.IO.File.ReadAllText(fileName);
            _lineNumber = 1;
            _fileName = fileName;
            while (true)
            {
                if (EatWhitespace(text, ref i)) break;
                string modeName = EatId(text, ref i);
                if (modeName == "")
                    throw new ParserException(_fileName, _lineNumber, "Expected keyword");
                if (modeName == "typedef")
                {
                    this.ParseTypeDef(text, ref i);
                }
                else if (modeName == "struct")
                {
                    this.ParseStructDef(text, ref i);
                }
                else if (modeName == "include")
                {
                    _eatResult = EatWhitespace(text, ref i);
                    string includeFile = EatQuotedString(text, ref i);
                    if (_eatResult || includeFile == "")
                        throw new ParserException(_fileName, _lineNumber, "String expected (file name)");
                    _eatResult = EatWhitespace(text, ref i);
                    string endSemicolon = EatSymbol(text, ref i);
                    if (_eatResult || endSemicolon != ";")
                        throw new ParserException(_fileName, _lineNumber, "Expected ';'");
                    System.IO.FileInfo info = new System.IO.FileInfo(_fileName);
                    string oldFileName = _fileName;
                    int oldLine = _lineNumber;
                    try
                    {
                        if (includeFile.Contains(":"))
                            this.Parse(includeFile);
                        else
                            this.Parse(info.DirectoryName + "\\" + includeFile);
                    }
                    catch (System.IO.FileNotFoundException)
                    {
                        throw new ParserException(_fileName, _lineNumber, "Could not find the file '" + includeFile + "'");
                    }
                    _fileName = oldFileName;
                    _lineNumber = oldLine;
                }
                else
                {
                    throw new ParserException(_fileName, _lineNumber, "Expected keyword");
                }
            }
        }
        private void ParseTypeDef(string text, ref int i)
        {
            _eatResult = EatWhitespace(text, ref i);
            string existingType = EatId(text, ref i);
            if (_eatResult || existingType == "")
                throw new ParserException(_fileName, _lineNumber, "Expected identifier (type name)");
            if (!_typeDefs.ContainsKey(existingType))
                throw new ParserException(_fileName, _lineNumber, "Unknown identifier '" + existingType + "' (type name)");
            _eatResult = EatWhitespace(text, ref i);
            string asterisk = EatSymbol(text, ref i);
            if (asterisk != "*" && asterisk.Length > 0)
                throw new ParserException(_fileName, _lineNumber, "Unexpected '" + asterisk + "'");
            _eatResult = EatWhitespace(text, ref i);
            string newType = EatId(text, ref i);
            if (_eatResult || existingType == "")
                throw new ParserException(_fileName, _lineNumber, "Expected identifier (new type name)");
            if (_typeDefs.ContainsKey(newType))
                throw new ParserException(_fileName, _lineNumber, "Type name '" + newType + "' already used");
            if (this.IsTypePointer(this.GetType(existingType)) && asterisk == "*")
                throw new ParserException(_fileName, _lineNumber, "Invalid '*'; type '" + existingType + "' is already a pointer");
            _typeDefs.Add(newType, this.GetType(existingType) | (asterisk == "*" ? FieldType.Pointer : 0));
            _eatResult = EatWhitespace(text, ref i);
            string endSemicolon = EatSymbol(text, ref i);
            if (_eatResult || endSemicolon != ";")
                throw new ParserException(_fileName, _lineNumber, "Expected ';'");
        }
        private void ParseStructDef(string text, ref int i)
        {
            StructDef def = new StructDef();
            _eatResult = EatWhitespace(text, ref i);
            string structName = EatId(text, ref i);
            if (_eatResult || structName == "")
                throw new ParserException(_fileName, _lineNumber, "Expected identifier (struct name)");
            if (_structs.ContainsKey(structName))
                throw new ParserException(_fileName, _lineNumber, "Struct name '" + structName + "' already used");
            _structs.Add(structName, null);
            _eatResult = EatWhitespace(text, ref i);
            string openingBrace = EatSymbol(text, ref i);
            if (_eatResult || openingBrace != "{")
                throw new ParserException(_fileName, _lineNumber, "Expected '{'");
            while (true)
            {
                _eatResult = EatWhitespace(text, ref i);
                string endBrace = EatSymbol(text, ref i);
                if (_eatResult)
                    throw new ParserException(_fileName, _lineNumber, "Expected type name or '}'");
                if (endBrace == "}")
                    break;
                if (endBrace.Length > 0)
                    throw new ParserException(_fileName, _lineNumber, "Unexpected '" + endBrace + "'");
                _eatResult = EatWhitespace(text, ref i);
                string typeName = EatId(text, ref i);
                if (_eatResult || typeName == "")
                    throw new ParserException(_fileName, _lineNumber, "Expected type name");
                FieldType type;
                if (_typeDefs.ContainsKey(typeName))
                {
                    type = this.GetType(typeName);
                }
                else
                {
                    type = FieldType.Struct;
                    if (!_structs.ContainsKey(typeName))
                        throw new ParserException(_fileName, _lineNumber, "Unknown identifier '" + typeName + "' (type or struct name)");
                }
                FieldType justType = type;
                _eatResult = EatWhitespace(text, ref i);
                if (EatSymbol(text, ref i) == "*")
                {
                    if (this.IsTypePointer(type))
                        throw new ParserException(_fileName, _lineNumber, "Invalid '*'; type '" + typeName + "' is already a pointer");
                    type |= FieldType.Pointer;
                }
                _eatResult = EatWhitespace(text, ref i);
                string fieldName = EatId(text, ref i);
                if (_eatResult || fieldName == "")
                    throw new ParserException(_fileName, _lineNumber, "Expected identifier (struct field name)");
                if (def.ContainsField(fieldName))
                    throw new ParserException(_fileName, _lineNumber, "Field name '" + fieldName + "' already used");
                _eatResult = EatWhitespace(text, ref i);
                string leftSqBracket = EatSymbol(text, ref i);
                int varLength = 0;
                if (leftSqBracket == "[")
                {
                    _eatResult = EatWhitespace(text, ref i);
                    string fieldRefName = EatId(text, ref i);
                    string fieldSizeSpec = EatNumber(text, ref i);
                    if (fieldRefName != "")
                    {
                        if (!def.ContainsField(fieldRefName))
                            throw new ParserException(_fileName, _lineNumber, "Unknown identifier '" + fieldRefName + "' (field name)");
                        def.GetField(fieldRefName).SetsVarOn = fieldName;
                        int iSave = i;
                        _eatResult = EatWhitespace(text, ref i);
                        string plusOrMulOrDivSign = EatSymbol(text, ref i);
                        if (plusOrMulOrDivSign == "+")
                        {
                            def.GetField(fieldRefName).SetsVarOnAdd = EatParseInt(text, ref i);
                        }
                        else if (plusOrMulOrDivSign == "*")
                        {
                            def.GetField(fieldRefName).SetsVarOnMultiply = EatParseFloat(text, ref i);
                            int iSave2 = i;
                            _eatResult = EatWhitespace(text, ref i);
                            string plusSign = EatSymbol(text, ref i);
                            if (plusSign == "+")
                                def.GetField(fieldRefName).SetsVarOnAdd = EatParseInt(text, ref i);
                            else if (plusSign == "-")
                                def.GetField(fieldRefName).SetsVarOnAdd = -EatParseInt(text, ref i);
                            else
                                i = iSave2;
                        }
                        else if (plusOrMulOrDivSign == "/")
                        {
                            def.GetField(fieldRefName).SetsVarOnMultiply = 1 / EatParseFloat(text, ref i);
                            int iSave2 = i;
                            _eatResult = EatWhitespace(text, ref i);
                            string plusSign = EatSymbol(text, ref i);
                            if (plusSign == "+")
                                def.GetField(fieldRefName).SetsVarOnAdd = EatParseInt(text, ref i);
                            else if (plusSign == "-")
                                def.GetField(fieldRefName).SetsVarOnAdd = -EatParseInt(text, ref i);
                            else
                                i = iSave2;
                        }
                        else
                        {
                            i = iSave;
                        }
                    }
                    else if (fieldSizeSpec != "")
                    {
                        try
                        {
                            varLength = (int)BaseConverter.ToNumberParse(fieldSizeSpec);
                            varLength = (int)BaseConverter.ToNumberParse(fieldSizeSpec);
                        }
                        catch
                        {
                            throw new ParserException(_fileName, _lineNumber, "Could not parse number '" + fieldSizeSpec + "'");
                        }
                    }
                    else
                    {
                        throw new ParserException(_fileName, _lineNumber, "Number or identifier expected (size specifier)");
                    }
                    if (justType != FieldType.StringASCII && justType != FieldType.StringUTF16)
                        type |= FieldType.Array;
                    _eatResult = EatWhitespace(text, ref i);
                    string rightSqBracket = EatSymbol(text, ref i);
                    if (_eatResult || rightSqBracket != "]")
                        throw new ParserException(_fileName, _lineNumber, "Expected ']'");
                    _eatResult = EatWhitespace(text, ref i);
                    leftSqBracket = EatSymbol(text, ref i);
                }
                string endSemicolon = leftSqBracket;
                if (_eatResult || endSemicolon != ";")
                    throw new ParserException(_fileName, _lineNumber, "Expected ';'");
                StructField field = new StructField(fieldName, type);
                if (field.Type == FieldType.Struct)
                    field.StructName = typeName;
                field.VarArrayLength = varLength;
                field.VarLength = varLength;
                def.AddField(field);
            }
            _structs[structName] = def;
        }
        private float EatParseFloat(string text, ref int i)
        {
            _eatResult = EatWhitespace(text, ref i);
            string number = EatNumber(text, ref i);
            if (_eatResult || number == "")
                throw new ParserException(_fileName, _lineNumber, "Expected floating-point number");
            try
            {
                return float.Parse(number);
            }
            catch
            {
                throw new ParserException(_fileName, _lineNumber, "Could not parse number '" + number + "'");
            }
        }
        private int EatParseInt(string text, ref int i)
        {
            _eatResult = EatWhitespace(text, ref i);
            string number = EatNumber(text, ref i);
            if (_eatResult || number == "")
                throw new ParserException(_fileName, _lineNumber, "Expected integer");
            try
            {
                return (int)BaseConverter.ToNumberParse(number);
            }
            catch
            {
                throw new ParserException(_fileName, _lineNumber, "Could not parse number '" + number + "'");
            }
        }
        private bool EatWhitespace(string text, ref int i)
        {
            bool ranOut = true;
            bool preComment = false;
            bool inComment = false;
            bool prePostComment = false;
            while (i < text.Length)
            {
                if (inComment && text[i] == '*')
                {
                    prePostComment = true;
                    i++;
                    continue;
                }
                else if (prePostComment && text[i] == '/')
                {
                    prePostComment = false;
                    inComment = false;
                    i++;
                    continue;
                }
                else if (!inComment && text[i] == '/')
                {
                    preComment = true;
                    i++;
                    continue;
                }
                else if (preComment)
                {
                    if (text[i] == '*')
                    {
                        preComment = false;
                        inComment = true;
                        i++;
                        continue;
                    }
                    else
                    {
                        i -= 1;
                        break;
                    }
                }
                else
                {
                    preComment = false;
                    prePostComment = false;
                }
                if (text[i] == '\n')
                    _lineNumber++;
                if (!(text[i] == '\r' || text[i] == '\n' || text[i] == ' ' || text[i] == '\t') && !inComment)
                {
                    ranOut = false;
                    break;
                }
                i++;
            }
            return ranOut;
        }
        private string EatQuotedString(string text, ref int i)
        {
            StringBuilder sb = new StringBuilder();
            bool inEscape = false;
            if (text[i] == '"')
            {
                i++;
            }
            else
                return "";
            while (i < text.Length)
            {
                if (text[i] == '\\')
                {
                    inEscape = true;
                    i++;
                    continue;
                }
                else if (inEscape)
                {
                    if (text[i] == '\\')
                        sb.Append('\\');
                    else if (text[i] == '"')
                        sb.Append('"');
                    else if (text[i] == '\'')
                        sb.Append('\'');
                    else if (text[i] == 'r')
                        sb.Append('\r');
                    else if (text[i] == 'n')
                        sb.Append('\n');
                    else if (text[i] == 't')
                        sb.Append('\t');
                    else
                        throw new ParserException(_fileName, _lineNumber, "Unrecognized escape sequence '\\" + text[i] + "'");
                    i++;
                    inEscape = false;
                    continue;
                }
                else if (text[i] == '"')
                {
                    i++;
                    break;
                }
                sb.Append(text[i]);
                i++;
            }
            return sb.ToString();
        }
        private string EatId(string text, ref int i)
        {
            StringBuilder sb = new StringBuilder();
            while (i < text.Length)
            {
                if (sb.Length == 0)
                {
                    if (!(char.IsLetter(text[i]) || text[i] == '_'))
                        break;
                }
                else
                {
                    if (!(char.IsLetterOrDigit(text[i]) || text[i] == '_'))
                        break;
                }
                sb.Append(text[i]);
                i++;
            }
            return sb.ToString();
        }
        private string EatNumber(string text, ref int i)
        {
            StringBuilder sb = new StringBuilder();
            while (i < text.Length)
            {
                if (sb.Length == 1 && sb[0] == '0')
                {
                    if (!char.IsDigit(text[i]) && char.ToLower(text[i]) != 'x' && text[i] != '.')
                        break;
                }
                else if (sb.Length >= 2 && sb[0] == '0' && char.ToLower(sb[1]) == 'x')
                {
                    if (!(char.IsDigit(text[i]) ||
                        char.ToLower(text[i]) == 'a' ||
                        char.ToLower(text[i]) == 'b' ||
                        char.ToLower(text[i]) == 'c' ||
                        char.ToLower(text[i]) == 'd' ||
                        char.ToLower(text[i]) == 'e' ||
                        char.ToLower(text[i]) == 'f'))
                        break;
                }
                else
                {
                    if (!char.IsDigit(text[i]))
                        break;
                }
                sb.Append(text[i]);
                i++;
            }
            return sb.ToString();
        }
        private string EatSymbol(string text, ref int i)
        {
            StringBuilder sb = new StringBuilder();
            while (i < text.Length && sb.Length < 1)
            {
                char c = text[i];
                if (c < ' ' || c > '~')
                    break;
                if (char.IsLetterOrDigit(c) || c == '_')
                    break;
                sb.Append(c);
                i++;
            }
            return sb.ToString();
        }
    }
}
