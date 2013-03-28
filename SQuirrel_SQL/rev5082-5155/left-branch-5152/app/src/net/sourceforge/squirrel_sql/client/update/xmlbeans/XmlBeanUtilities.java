package net.sourceforge.squirrel_sql.client.update.xmlbeans;



import java.io.File;
import java.io.IOException;
import java.util.Date;

import net.sourceforge.squirrel_sql.client.ApplicationArguments;
import net.sourceforge.squirrel_sql.client.update.UpdateUtil;
import net.sourceforge.squirrel_sql.fw.util.IOUtilities;
import net.sourceforge.squirrel_sql.fw.util.IOUtilitiesImpl;


public class XmlBeanUtilities {

	private IOUtilities _iou = new IOUtilitiesImpl();

	
	public ChannelXmlBean buildChannelRelease(String channelName, String releaseName, String version,
	      String directory) throws IOException {
		ChannelXmlBean result = new ChannelXmlBean();
		result.setName(channelName);
		ReleaseXmlBean releaseBean = new ReleaseXmlBean(releaseName, version);
		releaseBean.setCreateTime(new Date());
		File dir = new File(directory);
		for (File f : dir.listFiles()) {
			System.out.println("Processing module directory: " + f);
			if (f.isDirectory()) {
				
				ModuleXmlBean module = new ModuleXmlBean();
				module.setName(f.getName());
				for (File a : f.listFiles()) {

					String filename = a.getName();
					System.out.println("Processing artifact file: " + filename);
					String type = filename.substring(filename.indexOf(".") + 1);
					ArtifactXmlBean artifact = new ArtifactXmlBean();
					artifact.setName(a.getName());
					artifact.setType(type);
					artifact.setVersion(version);
					artifact.setSize(a.length());
					artifact.setChecksum(_iou.getCheckSum(a));
					module.addArtifact(artifact);
				}
				releaseBean.addmodule(module);
			}
		}
		result.setCurrentRelease(releaseBean);
		return result;
	}

	
	public static void main(String[] args) throws Exception {
		ApplicationArguments.initialize(new String[0]);
		if (args.length != 3) {
			printUsage();
		} else {
			File f = new File(args[2], UpdateUtil.RELEASE_XML_FILENAME);
			String filename = f.getAbsolutePath();
			if (f.exists()) {
				System.err.println("File " + filename + " appears to already exist");
			} else {
				XmlBeanUtilities util = new XmlBeanUtilities();
				ChannelXmlBean channelBean = util.buildChannelRelease(args[0], args[0], args[1], args[2]);
				UpdateXmlSerializer serializer = new UpdateXmlSerializerImpl();

				System.out.println("Writing channel release bean to " + filename);
				serializer.write(channelBean, filename);
			}
		}

	}

	private static void printUsage() {
		System.err.println("Usage: java XmlBeanUtilities <channel> <version> <directory>");
	}
}
