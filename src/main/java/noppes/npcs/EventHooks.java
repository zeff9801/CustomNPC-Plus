//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package noppes.npcs;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import net.minecraftforge.event.world.WorldEvent;
import noppes.npcs.controllers.IScriptHandler;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.controllers.data.ForgeDataScript;
import noppes.npcs.controllers.data.PlayerDataScript;
import noppes.npcs.scripted.*;
import noppes.npcs.scripted.event.ForgeEvent;
import noppes.npcs.scripted.event.PlayerEvent;
import noppes.npcs.scripted.event.PlayerEvent.AttackEvent;
import noppes.npcs.scripted.event.PlayerEvent.ChatEvent;
import noppes.npcs.scripted.event.PlayerEvent.ContainerClosed;
import noppes.npcs.scripted.event.PlayerEvent.ContainerOpen;
import noppes.npcs.scripted.event.PlayerEvent.DamagedEntityEvent;
import noppes.npcs.scripted.event.PlayerEvent.KeyPressedEvent;
import noppes.npcs.scripted.event.PlayerEvent.LevelUpEvent;
import noppes.npcs.scripted.event.PlayerEvent.LoginEvent;
import noppes.npcs.scripted.event.PlayerEvent.LogoutEvent;
import noppes.npcs.scripted.event.PlayerEvent.PickUpEvent;
import noppes.npcs.scripted.event.PlayerEvent.TossEvent;
import noppes.npcs.scripted.wrapper.WrapperNpcAPI;
import noppes.npcs.constants.EnumScriptType;
import noppes.npcs.controllers.PlayerData;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

public class EventHooks {
    public EventHooks() {
    }

    public static void onForgeInit(IScriptHandler handler) {
        noppes.npcs.scripted.event.ForgeEvent.InitEvent event = new noppes.npcs.scripted.event.ForgeEvent.InitEvent();
        handler.callScript(EnumScriptType.INIT, event);
        WrapperNpcAPI.EVENT_BUS.post(event);
    }

    public static void onPlayerInit(PlayerDataScript handler) {
        noppes.npcs.scripted.event.PlayerEvent.InitEvent event = new noppes.npcs.scripted.event.PlayerEvent.InitEvent(handler.getPlayer());
        handler.callScript(EnumScriptType.INIT, event);
        WrapperNpcAPI.EVENT_BUS.post(event);
    }

    public static void onPlayerTick(PlayerDataScript handler) {
        noppes.npcs.scripted.event.PlayerEvent.UpdateEvent event = new noppes.npcs.scripted.event.PlayerEvent.UpdateEvent(handler.getPlayer());
        handler.callScript(EnumScriptType.TICK, event);
        WrapperNpcAPI.EVENT_BUS.post(event);
    }

    public static boolean onPlayerInteract(PlayerDataScript handler, noppes.npcs.scripted.event.PlayerEvent.InteractEvent event) {
        handler.callScript(EnumScriptType.INTERACT, event, "target", event.target, "type", event.type);
        return WrapperNpcAPI.EVENT_BUS.post(event);
    }

    public static boolean onStartUsingItem(PlayerDataScript handler, int duration, ItemStack item){
        PlayerEvent.StartUsingItem event = new PlayerEvent.StartUsingItem(handler.getPlayer(), item, duration);
        handler.callScript(EnumScriptType.START_USING_ITEM, event, "duration", event.duration, "item", event.item);
        return WrapperNpcAPI.EVENT_BUS.post(event);
    }

    public static boolean onUsingItem(PlayerDataScript handler, int duration, ItemStack item){
        PlayerEvent.UsingItem event = new PlayerEvent.UsingItem(handler.getPlayer(), item, duration);
        handler.callScript(EnumScriptType.USING_ITEM, event, "duration", event.duration, "item", event.item);
        return WrapperNpcAPI.EVENT_BUS.post(event);
    }

    public static boolean onStopUsingItem(PlayerDataScript handler, int duration, ItemStack item){
        PlayerEvent.StopUsingItem event = new PlayerEvent.StopUsingItem(handler.getPlayer(), item, duration);
        handler.callScript(EnumScriptType.STOP_USING_ITEM, event, "duration", event.duration, "item", event.item);
        return WrapperNpcAPI.EVENT_BUS.post(event);
    }

    public static void onFinishUsingItem(PlayerDataScript handler, int duration, ItemStack item){
        PlayerEvent.FinishUsingItem event = new PlayerEvent.FinishUsingItem(handler.getPlayer(), item, duration);
        handler.callScript(EnumScriptType.FINISH_USING_ITEM, event, "duration", event.duration, "item", event.item);
        WrapperNpcAPI.EVENT_BUS.post(event);
    }

    public static boolean onPlayerDropItems(PlayerDataScript handler, ArrayList<EntityItem> entityItems) {
        IItemStack[] items = new IItemStack[entityItems.size()];
        for(int i = 0; i < entityItems.size(); i++){ items[i] = NpcAPI.Instance().getIItemStack(entityItems.get(i).getEntityItem()); }

        TossEvent event = new TossEvent(handler.getPlayer(), items);
        handler.callScript(EnumScriptType.DROP, event, "items", event.items);
        return WrapperNpcAPI.EVENT_BUS.post(event);
    }

    public static void onPlayerPickupXP(PlayerDataScript handler, EntityXPOrb orb) {
        PlayerEvent.PickupXPEvent event = new PlayerEvent.PickupXPEvent(handler.getPlayer(), orb);
        handler.callScript(EnumScriptType.PICKUP_XP, event, "amount", event.amount);
        WrapperNpcAPI.EVENT_BUS.post(event);
    }

    public static boolean onPlayerPickUp(PlayerDataScript handler, EntityItem entityItem) {
        PickUpEvent event = new PickUpEvent(handler.getPlayer(), NpcAPI.Instance().getIItemStack(entityItem.getEntityItem()));
        handler.callScript(EnumScriptType.PICKUP, event, "item", event.item);
        return WrapperNpcAPI.EVENT_BUS.post(event);
    }

    public static void onPlayerContainerOpen(PlayerDataScript handler, Container container) {
        ContainerOpen event = new ContainerOpen(handler.getPlayer(), NpcAPI.Instance().getIContainer(container));
        handler.callScript(EnumScriptType.CONTAINER_OPEN, event, "container", event.container);
        WrapperNpcAPI.EVENT_BUS.post(event);
    }

    public static void onPlayerDeath(PlayerDataScript handler, DamageSource source, Entity entity) {
        noppes.npcs.scripted.event.PlayerEvent.DiedEvent event = new noppes.npcs.scripted.event.PlayerEvent.DiedEvent(handler.getPlayer(), source, entity);
        handler.callScript(EnumScriptType.KILLED, event, "source", event.source, "damageSource", event.damageSource, "type", event.type);
        WrapperNpcAPI.EVENT_BUS.post(event);
    }

    public static void onPlayerKills(PlayerDataScript handler, EntityLivingBase entityLiving) {
        noppes.npcs.scripted.event.PlayerEvent.KilledEntityEvent event = new noppes.npcs.scripted.event.PlayerEvent.KilledEntityEvent(handler.getPlayer(), entityLiving);
        handler.callScript(EnumScriptType.KILLS, event, "entity", event.entity);
        WrapperNpcAPI.EVENT_BUS.post(event);
    }

    public static boolean onPlayerDamaged(PlayerDataScript handler, noppes.npcs.scripted.event.PlayerEvent.DamagedEvent event) {
        handler.callScript(EnumScriptType.DAMAGED, event, "damage", event.damage, "source", event.source, "damageSource", event.damageSource);
        return WrapperNpcAPI.EVENT_BUS.post(event);
    }

    public static void onPlayerFall(PlayerDataScript handler, float distance) {
        PlayerEvent.FallEvent event = new PlayerEvent.FallEvent(handler.getPlayer(),distance);
        handler.callScript(EnumScriptType.FALL, event, "distance", event.distance);
        WrapperNpcAPI.EVENT_BUS.post(event);
    }

    public static void onPlayerLogin(PlayerDataScript handler) {
        LoginEvent event = new LoginEvent(handler.getPlayer());
        handler.callScript(EnumScriptType.LOGIN, event);
        WrapperNpcAPI.EVENT_BUS.post(event);
    }

    public static void onPlayerLogout(PlayerDataScript handler) {
        LogoutEvent event = new LogoutEvent(handler.getPlayer());
        handler.callScript(EnumScriptType.LOGOUT, event);
        WrapperNpcAPI.EVENT_BUS.post(event);
    }

    public static void onPlayerChat(PlayerDataScript handler, ChatEvent event) {
        handler.callScript(EnumScriptType.CHAT, event, "message", event.message);
        WrapperNpcAPI.EVENT_BUS.post(event);
    }

    public static boolean onPlayerRanged(PlayerDataScript handler, noppes.npcs.scripted.event.PlayerEvent.RangedLaunchedEvent event) {
        handler.callScript(EnumScriptType.RANGED_LAUNCHED, event, "bow", event.bow, "charge", event.charge);
        return WrapperNpcAPI.EVENT_BUS.post(event);
    }

    public static boolean onPlayerDamagedEntity(PlayerDataScript handler, DamagedEntityEvent event) {
        handler.callScript(EnumScriptType.DAMAGED_ENTITY, event, "target", event.target, "damage", event.damage, "damageSource", event.damageSource, "");
        return WrapperNpcAPI.EVENT_BUS.post(event);
    }

    public static void onPlayerMouseClicked(EntityPlayerMP player, int button, int mouseWheel) {
        PlayerDataScript handler = PlayerData.get(player).scriptData;
        PlayerEvent.MouseClickedEvent event = new PlayerEvent.MouseClickedEvent(handler.getPlayer(), button, mouseWheel);
        handler.callScript(EnumScriptType.MOUSE_CLICKED, event, "button", event.button, "mouseWheel", event.mouseWheel);
        WrapperNpcAPI.EVENT_BUS.post(event);
    }

    public static void onPlayerKeyPressed(EntityPlayerMP player, int button, boolean isCtrlPressed, boolean isShiftPressed, boolean isAltPressed, boolean isMetaPressed) {
        PlayerDataScript handler = PlayerData.get(player).scriptData;
        KeyPressedEvent event = new KeyPressedEvent(handler.getPlayer(), button, isCtrlPressed, isAltPressed, isShiftPressed, isMetaPressed);
        handler.callScript(EnumScriptType.KEY_PRESSED, event, "key", button, "isCtrlPressed", isCtrlPressed, "isAltPressed", isAltPressed, "isShiftPressed", isShiftPressed, "isMetaPressed", isMetaPressed);
        WrapperNpcAPI.EVENT_BUS.post(event);
    }

    public static void onPlayerTimer(PlayerData data, int id) {
        PlayerDataScript handler = data.scriptData;
        noppes.npcs.scripted.event.PlayerEvent.TimerEvent event = new noppes.npcs.scripted.event.PlayerEvent.TimerEvent(handler.getPlayer(), id);
        handler.callScript(EnumScriptType.TIMER, event);
        WrapperNpcAPI.EVENT_BUS.post(event);
    }

    public static boolean onPlayerBreak(PlayerDataScript handler, PlayerEvent.BreakEvent event) {
        handler.callScript(EnumScriptType.BREAK, event, "block", event.block, "xp", event.exp);
        return WrapperNpcAPI.EVENT_BUS.post(event);
    }

    public static void onForgeEntityEvent(EntityEvent event) {
        IEntity e = NpcAPI.Instance().getIEntity(event.entity);
        onForgeEvent(new noppes.npcs.scripted.event.ForgeEvent.EntityEvent(event, e), event);
    }

    public static void onForgeEvent(ForgeEvent ev, Event event) {
        ForgeDataScript data = ScriptController.Instance.forgeScripts;
        if(data.isEnabled()) {
            String eventName = event.getClass().getName();
            int i = eventName.lastIndexOf(".");
            eventName = StringUtils.uncapitalize(eventName.substring(i + 1).replace("$", ""));
            if(event.isCancelable()) {
                ev.setCanceled(event.isCanceled());
            }

            data.callScript(eventName, event);
            WrapperNpcAPI.EVENT_BUS.post(ev);
            if(event.isCancelable()) {
                event.setCanceled(ev.isCanceled());
            }

        }
    }

    public static void onForgeWorldEvent(WorldEvent event) {
        if(ScriptController.Instance.forgeScripts.isEnabled()) {
            IWorld e = NpcAPI.Instance().getIWorld((WorldServer)event.world);
            onForgeEvent(new noppes.npcs.scripted.event.ForgeEvent.WorldEvent(event, e), event);
        }
    }
}
