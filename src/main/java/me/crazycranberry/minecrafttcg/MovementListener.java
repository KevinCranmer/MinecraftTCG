package me.crazycranberry.minecrafttcg;

import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.ChatTypeDecoration;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftChatMessage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import static net.minecraft.network.chat.ChatType.CHAT;
import static net.minecraft.network.chat.ChatType.DEFAULT_CHAT_DECORATION;

public class MovementListener implements Listener {
    // Spigot mappings
//    @EventHandler
//    public void onPlayerMove(PlayerMoveEvent e) {
//        CraftPlayer craftPlayer = (CraftPlayer) e.getPlayer();
//        EntityPlayer entityPlayer = craftPlayer.getHandle();
//
//        PlayerConnection playerConnection = entityPlayer.c;
//
//        playerConnection.b(new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.c, 0.0F));
//
//
//        IChatBaseComponent[] var6;
//        int var5 = (var6 = CraftChatMessage.fromString("Sending Weather Change")).length;
//
//        for(int var4 = 0; var4 < var5; ++var4) {
//            IChatBaseComponent component = var6[var4];
//            entityPlayer.a(component);
//        }
//    }

    // Mojang Mappings
//    @EventHandler
//    public void onPlayerMove(PlayerMoveEvent e) {
//        CraftPlayer craftPlayer = (CraftPlayer) e.getPlayer();
//        ServerPlayer entityPlayer = craftPlayer.getHandle();
//
//        ServerGamePacketListenerImpl playerConnection = entityPlayer.connection;
//
//        playerConnection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.STOP_RAINING, 0.0F));
//
//        entityPlayer.sendSystemMessage(Component.nullToEmpty("Hello there"));
//    }
}
