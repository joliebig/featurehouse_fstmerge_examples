package net.sourceforge.squirrel_sql.fw.codereformat;

public class PieceMarkerSpec
{
	public static final int TYPE_PIECE_MARKER_AT_BEGIN = 0;
	public static final int TYPE_PIECE_MARKER_AT_END = 1;
	public static final int TYPE_PIECE_MARKER_IN_OWN_PIECE = 2;

	public String _pieceMarker;
	public int _type;

	PieceMarkerSpec(String pieceMarker, int type)
	{
		this._pieceMarker = pieceMarker;

		if(TYPE_PIECE_MARKER_AT_BEGIN != type && TYPE_PIECE_MARKER_AT_END != type && TYPE_PIECE_MARKER_IN_OWN_PIECE != type)
		{
			throw new IllegalArgumentException("Unknow type: " + type);
		}

		this._type = type;
	}

	public String getPieceMarker()
	{
		return _pieceMarker;
	}

	public int getType()
	{
		return _type;
	}

	public int getLengthRightSpaced()
	{
		if(1 == _pieceMarker.length())
		{
			return _pieceMarker.length();
		}
		else
		{
			return _pieceMarker.length() + 1;
		}
	}

	public String getLeftSpace()
	{
		if(1 == _pieceMarker.length())
		{
			return "";
		}
		else
		{
			return " ";
		}
	}

	public boolean needsSuroundingWhiteSpaces()
	{
		if(1 == _pieceMarker.length())
		{
			return false;
		}
		else
		{
			return true;
		}
	}


}
