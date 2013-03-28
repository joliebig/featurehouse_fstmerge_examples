package net.sourceforge.squirrel_sql.fw.codereformat;

public class StateOfPosition
{
	boolean isTopLevel;

	int commentIndex = -1;
	int literalSepCount = 0;
	int braketDepth = 0;

	public Object clone()
	{
		StateOfPosition ret = new StateOfPosition();
		ret.commentIndex = commentIndex;
		ret.literalSepCount = commentIndex;
		ret.braketDepth = braketDepth;
		ret.isTopLevel = isTopLevel;

		return ret;
	}
}
