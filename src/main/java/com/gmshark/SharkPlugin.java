package com.gmshark;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.HitsplatID;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.Skill;
import net.runelite.api.Varbits;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
	name = "Corrupted shark tracker",
	tags = {"gridmaster", "gm", "stat", "league"}
)
public class SharkPlugin extends Plugin
{
	@Inject Client client;
	@Inject ItemManager itemManager;
	@Inject ConfigManager configManager;
	@Inject OverlayManager overlayManager;
	@Inject SharkOverlay overlay;
	@Inject SharkConfig config;

	int hpLastTick = 0;
	boolean eatenSharkThisTick = false;
	int totalDamageTaken = 0;

	@Override protected void startUp() throws Exception
	{
		overlayManager.add(overlay);
	}

	@Override protected void shutDown() throws Exception
	{
		overlayManager.remove(overlay);
	}

	@Subscribe public void onChatMessage(ChatMessage e) {
		if (e.getType() != ChatMessageType.SPAM) return;

		if (e.getMessage().equalsIgnoreCase(config.sharkEatChatMessage().trim())) {
			increment("timesEaten", 1);
			eatenSharkThisTick = true;
		}
	}

	private Integer increment(String key, int a)
	{
		Integer i = getConfig(key);
		configManager.setRSProfileConfiguration("gm_shark", key, i + a);
//		System.out.println(key + " " + i + " + " + a + " = " + (i + a));
		return i;
	}

	Integer getConfig(String key)
	{
		Integer i = configManager.getRSProfileConfiguration("gm_shark", key, Integer.class);
		return i == null ? 0 : i;
	}

	@Subscribe public void onHitsplatApplied(HitsplatApplied e) {
		if (e.getActor() != client.getLocalPlayer()) return;
		int type = e.getHitsplat().getHitsplatType();
		if (type != HitsplatID.DAMAGE_ME && type != HitsplatID.POISON && type != HitsplatID.VENOM && type != HitsplatID.DAMAGE_MAX_ME) return;
		totalDamageTaken += e.getHitsplat().getAmount();
	}

	@Subscribe public void onGameTick(GameTick e) {
		int hpThisTick = client.getBoostedSkillLevel(Skill.HITPOINTS);

		if (eatenSharkThisTick) {
			eatenSharkThisTick = false;
			int gained = hpThisTick - (hpLastTick - totalDamageTaken);
			int baseHpLevel = client.getRealSkillLevel(Skill.HITPOINTS);
			int overheal = Math.max(0, hpThisTick - baseHpLevel);
			gained -= overheal;

			boolean bracers = glovesSlotItemName().equalsIgnoreCase("Sunlit bracers");
			int meleeMastery = client.getVarbitValue(Varbits.LEAGUES_MELEE_COMBAT_MASTERY_LEVEL);
			int rangedMastery = client.getVarbitValue(Varbits.LEAGUES_RANGED_COMBAT_MASTERY_LEVEL);
			int magicMastery = client.getVarbitValue(Varbits.LEAGUES_MAGIC_COMBAT_MASTERY_LEVEL);
			boolean hasMastery = meleeMastery >= 2 || rangedMastery >= 2 || magicMastery >= 2;
			int sharkHeals = (hasMastery ? 24 : 20) * (bracers ? 2 : 1);

			gained = Math.min(gained, sharkHeals);
//			System.out.println("mastery: " + hasMastery + " bracers: " + bracers);
//			System.out.println(hpLastTick + " -> " + hpThisTick + " gained " + gained);
			increment("hpHealed", gained);
		}

		hpLastTick = hpThisTick;
		totalDamageTaken = 0;

		if (sharkExamine && config.showStatsOnExamine()) {
			sharkExamine = false;

			int timesEaten = getConfig("timesEaten");
			int hpHealed = getConfig("hpHealed");
			int average = hpHealed / timesEaten;
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", addCommas(timesEaten) + " bites taken for " + addCommas(hpHealed) + " hitpoints healed (avg: " + average + ")", "");
		}
	}

	String glovesSlotItemName() {
		ItemContainer equipment = client.getItemContainer(InventoryID.EQUIPMENT);
		if (equipment == null) return "";
		Item item = equipment.getItem(EquipmentInventorySlot.GLOVES.getSlotIdx());
		if (item == null) return "";
		return itemManager.getItemComposition(item.getId()).getName();
	}

	public String addCommas(int i) {
		String s = "" + i;
		if (s.length() >= 7) {
			s = s.substring(0, s.length() - 6) + "," + s.substring(s.length() - 6);
		}
		if (s.length() >= 4) {
			s = s.substring(0, s.length() - 3) + "," + s.substring(s.length() - 3);
		}
		return s;
	}

	boolean sharkExamine = false;
	@Subscribe public void onMenuOptionClicked(MenuOptionClicked e) {
		if (e.getItemId() != -1 && e.getMenuOption().equals("Examine") && itemManager.getItemComposition(e.getItemId()).getMembersName().equalsIgnoreCase(config.sharkItemName().trim())) {
			sharkExamine = true;
		}
	}

	@Provides
	SharkConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(SharkConfig.class);
	}
}
