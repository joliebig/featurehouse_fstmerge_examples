using System;
using System.Collections;
using System.IO;
using System.Text;
namespace GpsTrackerPlugin
{
    public class CSVReader : IDisposable
    {
        private Stream stream;
        private StreamReader reader;
        public CSVReader()
        {
            stream = null;
            reader = null;
        }
        public CSVReader(Stream s) : this(s, null)
        {
        }
        public CSVReader(Stream s, Encoding enc)
        {
            this.stream = s;
            if (!s.CanRead)
            {
                throw new CSVReaderException("Could not read the given CSV stream!");
            }
            reader = (enc != null) ? new StreamReader(s, enc) : new StreamReader(s);
        }
        public CSVReader(string filename) : this(filename, null)
        {
        }
        public CSVReader(string filename, Encoding enc)
            : this(new FileStream(filename, FileMode.Open), enc)
        {
        }
        public string[] GetCSVLine()
        {
            string data = reader.ReadLine();
            if (data == null) return null;
            if (data.Length == 0) return new string[0];
            ArrayList result = new ArrayList();
            ParseCSVFields(result, data);
            return (string[])result.ToArray(typeof(string));
        }
        public string[] GetCSVLine(string sLine)
        {
            if (sLine == null) return null;
            if (sLine.Length == 0) return new string[0];
            ArrayList result = new ArrayList();
            ParseCSVFields(result, sLine);
            return (string[])result.ToArray(typeof(string));
        }
        private void ParseCSVFields(ArrayList result, string data)
        {
            int pos = -1;
            while (pos < data.Length)
                result.Add(ParseCSVField(data, ref pos));
        }
        private string ParseCSVField(string data, ref int startSeparatorPosition)
        {
            if (startSeparatorPosition == data.Length - 1)
            {
                startSeparatorPosition++;
                return "";
            }
            int fromPos = startSeparatorPosition + 1;
            if (data[fromPos] == '"')
            {
                if (fromPos == data.Length - 1)
                {
                    fromPos++;
                    return "\"";
                }
                int nextSingleQuote = FindSingleQuote(data, fromPos + 1);
                startSeparatorPosition = nextSingleQuote + 1;
                return data.Substring(fromPos + 1, nextSingleQuote - fromPos - 1).Replace("\"\"", "\"");
            }
            int nextComma = data.IndexOf(',', fromPos);
            if (nextComma == -1)
            {
                startSeparatorPosition = data.Length;
                return data.Substring(fromPos);
            }
            else
            {
                startSeparatorPosition = nextComma;
                return data.Substring(fromPos, nextComma - fromPos);
            }
        }
        private int FindSingleQuote(string data, int startFrom)
        {
            int i = startFrom - 1;
            while (++i < data.Length)
                if (data[i] == '"')
                {
                    if (i < data.Length - 1 && data[i + 1] == '"')
                    {
                        i++;
                        continue;
                    }
                    else
                        return i;
                }
            return i;
        }
        public void Dispose()
        {
            if (reader != null) reader.Close();
            else if (stream != null)
                stream.Close();
            GC.SuppressFinalize(this);
        }
    }
    public class CSVReaderException : ApplicationException
    {
        public CSVReaderException(string message) : base(message) { }
    }
}
