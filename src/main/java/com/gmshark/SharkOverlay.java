package com.gmshark;

import java.awt.Color;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.WidgetItemOverlay;

public class SharkOverlay extends WidgetItemOverlay
{
	@Inject ItemManager itemManager;
	@Inject SharkPlugin plugin;

	public SharkOverlay() {
		showOnInventory();
	}

	@Override
	public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem widgetItem)
	{
		if (itemManager.getItemComposition(itemId).getMembersName().equalsIgnoreCase(plugin.config.sharkItemName().trim())) {
			boolean showCount = plugin.config.showTimesEatenOnItem();
			int yOffset = (int) widgetItem.getCanvasBounds().getY() + 10;
			int x = (int) widgetItem.getCanvasBounds().getX();
			if (showCount) {
				drawTextWithDropShadow("" + plugin.getConfig("timesEaten"), x, yOffset, graphics, plugin.config.showTimesEatenOnItemColor());
				yOffset += 22;
			}
			if (plugin.config.showHpHealedOnItem()) {
				drawTextWithDropShadow("" + plugin.getConfig("hpHealed"), x, yOffset, graphics, plugin.config.showHpHealedOnItemColor());
			}
		}
	}

	void drawTextWithDropShadow(String s, int x, int y, Graphics2D graphics, Color c) {
		graphics.setColor(Color.BLACK);
		graphics.drawString(s, x + 1, y + 1);
		graphics.setColor(c);
		graphics.drawString(s, x, y);
	}
}
