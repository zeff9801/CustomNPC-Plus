//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package noppes.npcs;

import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.world.*;
import net.minecraftforge.event.world.WorldEvent.PotentialSpawns;
import noppes.npcs.constants.EnumQuestType;
import noppes.npcs.controllers.*;
import noppes.npcs.controllers.data.PlayerDataScript;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.scripted.NpcAPI;
import noppes.npcs.scripted.entity.ScriptPlayer;
import noppes.npcs.scripted.event.ForgeEvent;
import org.lwjgl.input.Mouse;

public class ScriptPlayerEventHandler {
    public ScriptPlayerEventHandler() {
    }
    @SubscribeEvent
    public void onServerTick(TickEvent.PlayerTickEvent event) {
        if(event.player == null || event.player.worldObj == null)
            return;

        if(event.side == Side.SERVER && event.phase == TickEvent.Phase.START) {
            EntityPlayer player = event.player;

            if(player.ticksExisted%10 == 0) {
                PlayerDataScript handler = ScriptController.Instance.playerScripts;
                ScriptPlayer scriptPlayer = (ScriptPlayer) ScriptController.Instance.getScriptForEntity(player);
                EventHooks.onPlayerTick(handler, scriptPlayer);
            }

            PlayerData playerData = PlayerDataController.instance.getPlayerData(player);
            if(PlayerDataController.instance != null) {
                if(playerData.timers.size() > 0)
                    playerData.timers.update();
            }

            if(player.ticksExisted%(20*CustomNpcs.PlayerQuestCheck) == 0){
                PlayerQuestData questData = playerData.questData;
                for(EnumQuestType e : EnumQuestType.values())
                    questData.checkQuestCompletion(playerData, e);
            }
        }
    }

    @SubscribeEvent
    public void invoke(EntityInteractEvent event) {
        if(event.entityPlayer == null || event.entityPlayer.worldObj == null)
            return;

        if(!event.entityPlayer.worldObj.isRemote && event.entityPlayer.worldObj instanceof WorldServer) {
            PlayerDataScript handler = ScriptController.Instance.playerScripts;
            ScriptPlayer scriptPlayer = (ScriptPlayer) ScriptController.Instance.getScriptForEntity(event.entityPlayer);
            noppes.npcs.scripted.event.PlayerEvent.InteractEvent ev = new noppes.npcs.scripted.event.PlayerEvent.InteractEvent(scriptPlayer, 1, NpcAPI.Instance().getIEntity(event.target));
            event.setCanceled(EventHooks.onPlayerInteract(handler, ev));
        }
    }

    @SubscribeEvent
    public void invoke(ArrowNockEvent event) {
        if(event.entityPlayer == null || event.entityPlayer.worldObj == null)
            return;

        if(!event.entityPlayer.worldObj.isRemote && event.entityPlayer.worldObj instanceof WorldServer) {
            PlayerDataScript handler = ScriptController.Instance.playerScripts;
            ScriptPlayer scriptPlayer = (ScriptPlayer) ScriptController.Instance.getScriptForEntity(event.entityPlayer);
            noppes.npcs.scripted.event.PlayerEvent.RangedChargeEvent ev = new noppes.npcs.scripted.event.PlayerEvent.RangedChargeEvent(scriptPlayer);
            EventHooks.onPlayerBowCharge(handler, ev);
        }
    }

    @SubscribeEvent
    public void invoke(ArrowLooseEvent event) {
        if(event.entityPlayer == null || event.entityPlayer.worldObj == null)
            return;

        if(!event.entityPlayer.worldObj.isRemote && event.entityPlayer.worldObj instanceof WorldServer) {
            PlayerDataScript handler = ScriptController.Instance.playerScripts;
            ScriptPlayer scriptPlayer = (ScriptPlayer) ScriptController.Instance.getScriptForEntity(event.entityPlayer);
            noppes.npcs.scripted.event.PlayerEvent.RangedLaunchedEvent ev = new noppes.npcs.scripted.event.PlayerEvent.RangedLaunchedEvent(scriptPlayer, event.bow, event.charge);
            EventHooks.onPlayerRanged(handler, ev);
        }
    }

    @SubscribeEvent
    public void invoke(BlockEvent.BreakEvent event) {
        if(event.getPlayer() == null || event.getPlayer().worldObj == null)
            return;

        if(!event.getPlayer().worldObj.isRemote && event.world instanceof WorldServer) {
            PlayerDataScript handler = ScriptController.Instance.playerScripts;
            ScriptPlayer scriptPlayer = (ScriptPlayer) ScriptController.Instance.getScriptForEntity(event.getPlayer());
            noppes.npcs.scripted.event.PlayerEvent.BreakEvent ev = new noppes.npcs.scripted.event.PlayerEvent.BreakEvent(scriptPlayer, NpcAPI.Instance().getIBlock(event.world, new BlockPos(event.x,event.y,event.z)), event.getExpToDrop());
            event.setCanceled(EventHooks.onPlayerBreak(handler, ev));
            event.setExpToDrop(ev.exp);
        }
    }

    @SubscribeEvent
    public void invoke(PlayerUseItemEvent.Start event) {
        if(event.entityPlayer == null || event.entityPlayer.worldObj == null)
            return;

        if(event.entityPlayer.worldObj instanceof WorldServer) {
            PlayerDataScript handler = ScriptController.Instance.playerScripts;
            ScriptPlayer scriptPlayer = (ScriptPlayer) ScriptController.Instance.getScriptForEntity(event.entityPlayer);
            event.setCanceled(EventHooks.onStartUsingItem(handler, scriptPlayer, event.duration, event.item));
        }
    }
    @SubscribeEvent
    public void invoke(PlayerUseItemEvent.Tick event) {
        if(event.entityPlayer == null || event.entityPlayer.worldObj == null)
            return;

        if(event.entityPlayer.worldObj instanceof WorldServer) {
            PlayerDataScript handler = ScriptController.Instance.playerScripts;
            ScriptPlayer scriptPlayer = (ScriptPlayer) ScriptController.Instance.getScriptForEntity(event.entityPlayer);
            event.setCanceled(EventHooks.onUsingItem(handler, scriptPlayer, event.duration, event.item));
        }
    }
    @SubscribeEvent
    public void invoke(PlayerUseItemEvent.Stop event) {
        if(event.entityPlayer == null || event.entityPlayer.worldObj == null)
            return;

        if(event.entityPlayer.worldObj instanceof WorldServer) {
            PlayerDataScript handler = ScriptController.Instance.playerScripts;
            ScriptPlayer scriptPlayer = (ScriptPlayer) ScriptController.Instance.getScriptForEntity(event.entityPlayer);
            event.setCanceled(EventHooks.onStopUsingItem(handler, scriptPlayer, event.duration, event.item));
        }
    }
    @SubscribeEvent
    public void invoke(PlayerUseItemEvent.Finish event) {
        if(event.entityPlayer == null || event.entityPlayer.worldObj == null)
            return;

        if(event.entityPlayer.worldObj instanceof WorldServer) {
            PlayerDataScript handler = ScriptController.Instance.playerScripts;
            ScriptPlayer scriptPlayer = (ScriptPlayer) ScriptController.Instance.getScriptForEntity(event.entityPlayer);
            EventHooks.onFinishUsingItem(handler, scriptPlayer, event.duration, event.item);
        }
    }

    @SubscribeEvent
    public void invoke(PlayerDropsEvent event) {
        if(event.entityPlayer == null || event.entityPlayer.worldObj == null)
            return;

        if(event.entityPlayer.worldObj instanceof WorldServer) {
            PlayerDataScript handler = ScriptController.Instance.playerScripts;
            ScriptPlayer scriptPlayer = (ScriptPlayer) ScriptController.Instance.getScriptForEntity(event.entityPlayer);
            event.setCanceled(EventHooks.onPlayerDropItems(handler, scriptPlayer, event.drops));
        }
    }

    @SubscribeEvent
    public void invoke(PlayerPickupXpEvent event) {
        if(event.entityPlayer == null || event.entityPlayer.worldObj == null)
            return;

        if(event.entityPlayer.worldObj instanceof WorldServer) {
            PlayerDataScript handler = ScriptController.Instance.playerScripts;
            ScriptPlayer scriptPlayer = (ScriptPlayer) ScriptController.Instance.getScriptForEntity(event.entityPlayer);
            EventHooks.onPlayerPickupXP(handler, scriptPlayer, event.orb);
        }
    }

    @SubscribeEvent
    public void invoke(PlayerEvent.PlayerChangedDimensionEvent event) {
        if(event.player == null || event.player.worldObj == null)
            return;

        if(event.player.worldObj instanceof WorldServer) {
            PlayerDataScript handler = ScriptController.Instance.playerScripts;
            ScriptPlayer scriptPlayer = (ScriptPlayer) ScriptController.Instance.getScriptForEntity(event.player);
            EventHooks.onPlayerChangeDim(handler, scriptPlayer, event.fromDim, event.toDim);
        }
    }

    @SubscribeEvent
    public void invoke(PlayerEvent.ItemPickupEvent event) {
        if(event.player == null || event.player.worldObj == null)
            return;

        if(event.player.worldObj instanceof WorldServer) {
            PlayerDataScript handler = ScriptController.Instance.playerScripts;
            ScriptPlayer scriptPlayer = (ScriptPlayer) ScriptController.Instance.getScriptForEntity(event.player);
            EventHooks.onPlayerPickUp(handler, scriptPlayer, event.pickedUp);
        }
    }

    @SubscribeEvent
    public void invoke(PlayerOpenContainerEvent event) {
        if(event.entityPlayer == null || event.entityPlayer.worldObj == null)
            return;

        if(event.entityPlayer.worldObj instanceof WorldServer && !(event.entityPlayer.openContainer instanceof ContainerPlayer)) {
            PlayerDataScript handler = ScriptController.Instance.playerScripts;
            ScriptPlayer scriptPlayer = (ScriptPlayer) ScriptController.Instance.getScriptForEntity(event.entityPlayer);
            EventHooks.onPlayerContainerOpen(handler, scriptPlayer, event.entityPlayer.openContainer);
        }
    }

    @SubscribeEvent
    public void invoke(UseHoeEvent event) {
        if(event.entityPlayer == null || event.entityPlayer.worldObj == null)
            return;

        if(event.entityPlayer.worldObj instanceof WorldServer) {
            PlayerDataScript handler = ScriptController.Instance.playerScripts;
            ScriptPlayer scriptPlayer = (ScriptPlayer) ScriptController.Instance.getScriptForEntity(event.entityPlayer);
            EventHooks.onPlayerUseHoe(handler, scriptPlayer, event.current, event.x, event.y, event.z);
        }
    }

    @SubscribeEvent
    public void invoke(PlayerSleepInBedEvent event) {
        if(event.entityPlayer == null || event.entityPlayer.worldObj == null)
            return;

        if(event.entityPlayer.worldObj instanceof WorldServer) {
            PlayerDataScript handler = ScriptController.Instance.playerScripts;
            ScriptPlayer scriptPlayer = (ScriptPlayer) ScriptController.Instance.getScriptForEntity(event.entityPlayer);
            EventHooks.onPlayerSleep(handler, scriptPlayer, event.x, event.y, event.z);
        }
    }

    @SubscribeEvent
    public void invoke(PlayerWakeUpEvent event) {
        if(event.entityPlayer == null || event.entityPlayer.worldObj == null)
            return;

        if(event.entityPlayer.worldObj instanceof WorldServer) {
            PlayerDataScript handler = ScriptController.Instance.playerScripts;
            ScriptPlayer scriptPlayer = (ScriptPlayer) ScriptController.Instance.getScriptForEntity(event.entityPlayer);
            EventHooks.onPlayerWakeUp(handler, scriptPlayer, event.setSpawn);
        }
    }

    @SubscribeEvent
    public void invoke(FillBucketEvent event) {
        if(event.entityPlayer == null || event.entityPlayer.worldObj == null)
            return;

        if(event.entityPlayer.worldObj instanceof WorldServer) {
            PlayerDataScript handler = ScriptController.Instance.playerScripts;
            ScriptPlayer scriptPlayer = (ScriptPlayer) ScriptController.Instance.getScriptForEntity(event.entityPlayer);
            EventHooks.onPlayerFillBucket(handler, scriptPlayer, event.current, event.result);
        }
    }

    @SubscribeEvent
    public void invoke(BonemealEvent event) {
        if(event.entityPlayer == null || event.entityPlayer.worldObj == null)
            return;

        if(event.entityPlayer.worldObj instanceof WorldServer) {
            PlayerDataScript handler = ScriptController.Instance.playerScripts;
            ScriptPlayer scriptPlayer = (ScriptPlayer) ScriptController.Instance.getScriptForEntity(event.entityPlayer);
            EventHooks.onPlayerBonemeal(handler, scriptPlayer, event.x, event.y, event.z, event.world);
        }
    }

    @SubscribeEvent
    public void invoke(AchievementEvent event) {
        if(event.entityPlayer == null || event.entityPlayer.worldObj == null)
            return;

        if(event.entityPlayer.worldObj instanceof WorldServer) {
            PlayerDataScript handler = ScriptController.Instance.playerScripts;
            ScriptPlayer scriptPlayer = (ScriptPlayer) ScriptController.Instance.getScriptForEntity(event.entityPlayer);
            EventHooks.onPlayerAchievement(handler, scriptPlayer, CustomNpcs.proxy.getAchievementDesc(event.achievement));
        }
    }

    @SubscribeEvent
    public void invoke(ItemTossEvent event) {
        if(event.player == null || event.player.worldObj == null)
            return;

        if(event.player.worldObj instanceof WorldServer) {
            PlayerDataScript handler = ScriptController.Instance.playerScripts;
            ScriptPlayer scriptPlayer = (ScriptPlayer) ScriptController.Instance.getScriptForEntity(event.player);
            EventHooks.onPlayerToss(handler, scriptPlayer, event.entityItem);
        }
    }

    @SubscribeEvent
    public void invoke(LivingFallEvent event) {
        if(event.entityLiving == null || event.entityLiving.worldObj == null)
            return;

        if(event.entityLiving.worldObj instanceof WorldServer) {
            if (event.entityLiving instanceof EntityPlayer) {
                PlayerDataScript handler = ScriptController.Instance.playerScripts;
                ScriptPlayer scriptPlayer = (ScriptPlayer) ScriptController.Instance.getScriptForEntity(event.entityLiving);
                EventHooks.onPlayerFall(handler, scriptPlayer, event.distance);
            }
        }
    }

    @SubscribeEvent
    public void invoke(LivingEvent.LivingJumpEvent event) {
        if(event.entityLiving == null || event.entityLiving.worldObj == null)
            return;

        if(event.entityLiving.worldObj instanceof WorldServer) {
            if (event.entityLiving instanceof EntityPlayer) {
                PlayerDataScript handler = ScriptController.Instance.playerScripts;
                ScriptPlayer scriptPlayer = (ScriptPlayer) ScriptController.Instance.getScriptForEntity(event.entityLiving);
                EventHooks.onPlayerJump(handler, scriptPlayer);
            }
        }
    }

    @SubscribeEvent
    public void invoke(EntityStruckByLightningEvent event) {
        if(event.entity == null || event.entity.worldObj == null)
            return;

        if(event.entity.worldObj instanceof WorldServer) {
            if (event.entity instanceof EntityPlayer) {
                PlayerDataScript handler = ScriptController.Instance.playerScripts;
                ScriptPlayer scriptPlayer = (ScriptPlayer) ScriptController.Instance.getScriptForEntity(event.entity);
                EventHooks.onPlayerLightning(handler, scriptPlayer);
            }
        }
    }

    @SubscribeEvent
    public void invoke(PlaySoundAtEntityEvent event) {
        if(event.entity == null || event.entity.worldObj == null)
            return;

        if(event.entity.worldObj instanceof WorldServer) {
            if (event.entity instanceof EntityPlayer) {
                PlayerDataScript handler = ScriptController.Instance.playerScripts;
                ScriptPlayer scriptPlayer = (ScriptPlayer) ScriptController.Instance.getScriptForEntity(event.entity);
                EventHooks.onPlayerSound(handler,scriptPlayer,event.name,event.pitch,event.volume);
            }
        }
    }

    @SubscribeEvent
    public void invoke(LivingDeathEvent event) {
        if(event.entityLiving == null || event.entityLiving.worldObj == null)
            return;

        if(event.entityLiving.worldObj instanceof WorldServer) {
            Entity source = NoppesUtilServer.GetDamageSourcee(event.source);
            PlayerDataScript handler = ScriptController.Instance.playerScripts;
            if(event.entityLiving instanceof EntityPlayer) {
                ScriptPlayer scriptPlayer = (ScriptPlayer) ScriptController.Instance.getScriptForEntity(event.entityLiving);
                EventHooks.onPlayerDeath(handler,scriptPlayer, event.source, source);
            }

            if(source instanceof EntityPlayer) {
                ScriptPlayer scriptPlayer = (ScriptPlayer) ScriptController.Instance.getScriptForEntity(event.source.getEntity());
                EventHooks.onPlayerKills(handler,scriptPlayer, event.entityLiving);
            }

        }
    }

    @SubscribeEvent
    public void invoke(LivingHurtEvent event) {
        if(event.entityLiving == null || event.entityLiving.worldObj == null)
            return;

        if(event.entityLiving.worldObj instanceof WorldServer) {
            Entity source = NoppesUtilServer.GetDamageSourcee(event.source);
            PlayerDataScript handler = ScriptController.Instance.playerScripts;
            if(event.entityLiving instanceof EntityPlayer) {
                noppes.npcs.scripted.event.PlayerEvent.DamagedEvent pevent = new noppes.npcs.scripted.event.PlayerEvent.DamagedEvent((ScriptPlayer)ScriptController.Instance.getScriptForEntity((EntityPlayer)event.entityLiving), source, event.ammount, event.source);
                event.setCanceled(EventHooks.onPlayerDamaged(handler, pevent));
                event.ammount = pevent.damage;
            }

            if(source instanceof EntityPlayer) {
                noppes.npcs.scripted.event.PlayerEvent.DamagedEntityEvent pevent1 = new noppes.npcs.scripted.event.PlayerEvent.DamagedEntityEvent((ScriptPlayer)ScriptController.Instance.getScriptForEntity((EntityPlayer)event.source.getEntity()), event.entityLiving, event.ammount, event.source);
                event.setCanceled(EventHooks.onPlayerDamagedEntity(handler, pevent1));
                event.ammount = pevent1.damage;
            }

        }
    }

    @SubscribeEvent
    public void invoke(PlayerEvent.PlayerRespawnEvent event) {
        if(event.player == null || event.player.worldObj == null)
            return;

        if(event.player.worldObj instanceof WorldServer) {
            PlayerDataScript handler = ScriptController.Instance.playerScripts;
            ScriptPlayer scriptPlayer = (ScriptPlayer) ScriptController.Instance.getScriptForEntity(event.player);
            EventHooks.onPlayerRespawn(handler, scriptPlayer);
        }
    }

    @SubscribeEvent
    public void invoke(PlayerEvent.PlayerLoggedInEvent event) {
        if(event.player == null || event.player.worldObj == null)
            return;

        if(event.player.worldObj instanceof WorldServer) {
            PlayerDataScript handler = ScriptController.Instance.playerScripts;
            ScriptPlayer scriptPlayer = (ScriptPlayer) ScriptController.Instance.getScriptForEntity(event.player);
            EventHooks.onPlayerLogin(handler, scriptPlayer);
        }
    }

    @SubscribeEvent
    public void invoke(PlayerEvent.PlayerLoggedOutEvent event) {
        if(event.player == null || event.player.worldObj == null)
            return;

        if(event.player.worldObj instanceof WorldServer) {
            PlayerDataScript handler = ScriptController.Instance.playerScripts;
            ScriptPlayer scriptPlayer = (ScriptPlayer) ScriptController.Instance.getScriptForEntity(event.player);
            EventHooks.onPlayerLogout(handler, scriptPlayer);
        }
    }

    @SubscribeEvent(
            priority = EventPriority.HIGHEST
    )
    public void invoke(net.minecraftforge.event.ServerChatEvent event) {
        if(event.player == null || event.player.worldObj == null)
            return;

        if(event.player.worldObj instanceof WorldServer && !event.player.equals(EntityNPCInterface.chateventPlayer)) {
            PlayerDataScript handler = ScriptController.Instance.playerScripts;
            ScriptPlayer scriptPlayer = (ScriptPlayer) ScriptController.Instance.getScriptForEntity(event.player);
            String message = event.message;
            noppes.npcs.scripted.event.PlayerEvent.ChatEvent ev = new noppes.npcs.scripted.event.PlayerEvent.ChatEvent(scriptPlayer, event.message);
            EventHooks.onPlayerChat(handler, ev);
            event.setCanceled(ev.isCanceled());
            if(!message.equals(ev.message)) {
                ChatComponentTranslation chat = new ChatComponentTranslation("", new Object[0]);
                chat.appendSibling(ForgeHooks.newChatWithLinks(ev.message));
                event.component = chat;
            }
        }
    }
}