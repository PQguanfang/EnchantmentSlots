package cn.superiormc.enchantmentslots.protolcol.ProtocolLib;

import cn.superiormc.enchantmentslots.EnchantmentSlots;
import cn.superiormc.enchantmentslots.configs.ConfigReader;
import cn.superiormc.enchantmentslots.methods.ItemLimits;
import cn.superiormc.enchantmentslots.methods.ItemModify;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SetSlots extends GeneralPackets {
    public SetSlots() {
        super();
    }
    @Override
    protected void initPacketAdapter(){
        packetAdapter = new PacketAdapter(EnchantmentSlots.instance, ConfigReader.getPriority(), PacketType.Play.Server.SET_SLOT) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if (ConfigReader.getDebug()) {
                    Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[EnchantmentSlots] §f" +
                            "Found SetSlots packet.");
                }
                if (event.getPlayer() == null) {
                    return;
                }
                PacketContainer packet = event.getPacket();
                StructureModifier<ItemStack> itemStackStructureModifier = packet.getItemModifier();
                ItemStack serverItemStack = itemStackStructureModifier.read(0);
                if (serverItemStack == null || serverItemStack.getType().isAir()) {
                    return;
                }
                int slot = packet.getIntegers().read(packet.getIntegers().size() - 1);
                ItemModify.addLore(event.getPlayer(), serverItemStack, true);
                ItemStack clientItemStack = ItemModify.serverToClient(event.getPlayer(), serverItemStack);
                // client 是加过 Lore 的，server 是没加过的！
                itemStackStructureModifier.write(0, clientItemStack);
                if (ConfigReader.getRemoveExtraEnchants()) {
                    if (slot < 5 || slot > 44) {
                        return;
                    }
                    int spigotSlot = slot;
                    if (slot >= 36) {
                        spigotSlot = slot - 36;
                    } else if (slot <= 8) {
                        spigotSlot = slot + 31;
                    }
                    if (ConfigReader.getDebug()) {
                        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[EnchantmentSlots] §f" +
                                "Packet Slot ID: " + slot + ", Spigot Slot ID: " + spigotSlot + ".");
                    }
                    ItemStack tempItemStack = event.getPlayer().getInventory().getItem(spigotSlot);
                    if (tempItemStack == null || tempItemStack.getType().isAir()) {
                        return;
                    }
                    int maxEnchantments = ItemLimits.getMaxEnchantments(event.getPlayer(), tempItemStack);
                    if (tempItemStack.getEnchantments().size() >= maxEnchantments) {
                        int removeAmount = tempItemStack.getEnchantments().size() - maxEnchantments;
                        for (Enchantment enchant : tempItemStack.getEnchantments().keySet()) {
                            if (removeAmount <= 0) {
                                break;
                            }
                            ItemMeta meta = tempItemStack.getItemMeta();
                            meta.removeEnchant(enchant);
                            tempItemStack.setItemMeta(meta);
                            removeAmount--;
                        }
                    }
                }
            }
        };
    }
}
