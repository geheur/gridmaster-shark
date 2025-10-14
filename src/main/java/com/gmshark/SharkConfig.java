package com.gmshark;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("gm_shark")
public interface SharkConfig extends Config
{
	@ConfigItem(
		keyName = "void",
		name = "<html>IMPORTANT: make sure you check the<br>bottom two fields to make sure they<br>match the in-game text exactly or the<br>plugin won't work!</html>",
		description = "",
		position = 0
	)
	default void v() {}

	@ConfigItem(
		keyName = "sharkItemName",
		name = "Corrupted shark item name",
		description = "The exact name of the item",
		position = 1
	)
	default String sharkItemName()
	{
		return "Corrupted shark";
	}

	@ConfigItem(
		keyName = "sharkEatChatMessage",
		name = "Eating chat message",
		description = "",
		position = 2
	)
	default String sharkEatChatMessage()
	{
		return "You eat the corrupted shark.";
	}

	@ConfigItem(
		name = "Show times eaten on item",
		keyName = "showTimesEatenOnItem",
		description = "",
		position = 3
	)
	default boolean showTimesEatenOnItem()
	{
		return true;
	}

	@ConfigItem(
		name = "Color",
		keyName = "showTimesEatenOnItemColor",
		description = "",
		position = 4
	)
	default Color showTimesEatenOnItemColor()
	{
		return new Color(0xFFDCC4);
	}

	@ConfigItem(
		name = "Show hp healed on item",
		keyName = "showHpHealedOnItem",
		description = "",
		position = 5
	)
	default boolean showHpHealedOnItem()
	{
		return true;
	}

	@ConfigItem(
		name = "Color",
		keyName = "showHpHealedOnItemColor",
		description = "",
		position = 6
	)
	default Color showHpHealedOnItemColor()
	{
		return new Color(0xFFDCC4);
	}

	@ConfigItem(
		name = "Show stats on examine",
		keyName = "showStatsOnExamine",
		description = "",
		position = 7
	)
	default boolean showStatsOnExamine()
	{
		return true;
	}
}
