
package net.sourceforge.squirrel_sql.client.update.gui;

import static org.easymock.EasyMock.expect;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;

public class UpdateSummaryTableModelTest extends AbstractTableModelTest
{
	@Before
	public void setUp() throws Exception
	{
		List<ArtifactStatus> artifacts = new ArrayList<ArtifactStatus>();
		ArtifactStatus mockStatus = getArtifactStatus("testCoreArtifactName", "testArtifactType", true, true);
		ArtifactStatus mockStatus2 = getArtifactStatus("testPluginArtifactName", "testArtifactType", false, false);		
		artifacts.add(mockStatus);
		artifacts.add(mockStatus2);
		mockHelper.replayAll();
		classUnderTest = new UpdateSummaryTableModel(artifacts);
		editableColumns = new int[] { 3 };
	}

	
   private ArtifactStatus getArtifactStatus(String artifactName, String artifactType, boolean isInstalled, boolean isCoreArtifact)
   {
	   ArtifactStatus mockStatus = mockHelper.createMock(ArtifactStatus.class);
		expect(mockStatus.getName()).andStubReturn(artifactName);
		expect(mockStatus.getType()).andStubReturn(artifactType);
		expect(mockStatus.isInstalled()).andStubReturn(isInstalled);
		expect(mockStatus.isCoreArtifact()).andStubReturn(isCoreArtifact);
		expect(mockStatus.getArtifactAction()).andStubReturn(ArtifactAction.INSTALL);
	   return mockStatus;
   }

}
