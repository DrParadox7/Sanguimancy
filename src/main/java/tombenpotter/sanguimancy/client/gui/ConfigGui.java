package tombenpotter.sanguimancy.client.gui;

import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import tombenpotter.sanguimancy.Sanguimancy;

import java.util.ArrayList;
import java.util.List;

import static tombenpotter.sanguimancy.util.ConfigHandler.*;

public class ConfigGui extends GuiConfig {

	public ConfigGui(GuiScreen parentScreen) {
		super(parentScreen, getConfigElements(parentScreen), Sanguimancy.modid, false, false, StatCollector.translateToLocal("gui." + Sanguimancy.modid  + ".config.title"));
	}

	@SuppressWarnings("rawtypes")
	private static List<IConfigElement> getConfigElements(GuiScreen parent) {
		List<IConfigElement> list = new ArrayList<IConfigElement>();

		list.add(new ConfigElement<ConfigCategory>(config.getCategory(rituals.toLowerCase())));
		list.add(new ConfigElement<ConfigCategory>(config.getCategory(balancing.toLowerCase())));
		list.add(new ConfigElement<ConfigCategory>(config.getCategory(features.toLowerCase())));

		return list;
	}
}
