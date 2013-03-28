
package net.sourceforge.squirrel_sql.client.update.downloader;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.client.update.gui.ArtifactStatus;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import utils.EasyMockHelper;

public class ArtifactDownloaderFactoryImplTest extends BaseSQuirreLJUnit4TestCase {

	
	ArtifactDownloaderFactoryImpl classUnderTest = null; 
	
	EasyMockHelper mockHelper = new EasyMockHelper();
	
	@Before
	public void setUp() throws Exception {
		classUnderTest = new ArtifactDownloaderFactoryImpl();
	}

	@After
	public void tearDown() throws Exception {
		classUnderTest = null;
	}

	@Test
	public final void testCreate() {
		
		ArtifactStatus mockArtifactStatus = mockHelper.createMock(ArtifactStatus.class);
		List<ArtifactStatus> artifactStatusList = new ArrayList<ArtifactStatus>();
		artifactStatusList.add(mockArtifactStatus);
		ArtifactDownloader result = classUnderTest.create(artifactStatusList);
		List<ArtifactStatus> actualArtifactStatusList = result.getArtifactStatus();
		assertEquals(artifactStatusList.size(), actualArtifactStatusList.size());
		assertEquals(artifactStatusList.get(0), actualArtifactStatusList.get(0));
	}

	@Test (expected = IllegalArgumentException.class)
	public final void testCreateWithEmptyList() {
		List<ArtifactStatus> artifactStatusList = new ArrayList<ArtifactStatus>();
		classUnderTest.create(artifactStatusList);
	}
}
