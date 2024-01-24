package me.crazycranberry.minecrafttcg;

import org.bukkit.event.Listener;

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
