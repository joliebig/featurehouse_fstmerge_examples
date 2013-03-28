
package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders;

import static org.easymock.EasyMock.isA;

import java.sql.PreparedStatement;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.AbstractINodeExpanderTest;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.ISequenceParentExtractor;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.SequenceParentExpander;
import net.sourceforge.squirrel_sql.client.session.schemainfo.ObjFilterMatcher;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;

import org.easymock.EasyMock;
import org.junit.Before;

public class SequenceParentExpanderTest extends AbstractINodeExpanderTest
{

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new SequenceParentExpander();
		ISequenceParentExtractor mockExtractor = mockHelper.createMock(ISequenceParentExtractor.class);
		EasyMock.expect(mockExtractor.getSequenceParentQuery()).andReturn("test query");
		mockExtractor.bindParameters(isA(PreparedStatement.class), isA(IDatabaseObjectInfo.class),
			isA(ObjFilterMatcher.class));
		((SequenceParentExpander) classUnderTest).setExtractor(mockExtractor);
	}

}
