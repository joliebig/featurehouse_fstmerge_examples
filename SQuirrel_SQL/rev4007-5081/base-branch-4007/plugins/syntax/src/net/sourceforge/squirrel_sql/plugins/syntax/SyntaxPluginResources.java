package net.sourceforge.squirrel_sql.plugins.syntax;

import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;

public final class SyntaxPluginResources extends PluginResources
{
	SyntaxPluginResources(IPlugin plugin)
	{
		super(SyntaxPluginResources.class.getName(), plugin);
	}

	public interface IKeys
	{
		String BACKGROUND_IMAGE = "Background";
		String BOLD_IMAGE = "Bold";
		String COLOR_SELECTOR_IMAGE = "ColorSelector";
		String FOREGROUND_IMAGE = "Foreground";
		String ITALIC_IMAGE = "Italic";
	}
}
