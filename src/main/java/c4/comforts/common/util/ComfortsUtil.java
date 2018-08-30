/*
 * Copyright (c) 2017-2018 <C4>
 *
 * This Java class is distributed as a part of the Comforts mod for Minecraft.
 * Comforts is open source and distributed under the GNU Lesser General Public License v3.
 * View the source code and license file on github: https://github.com/TheIllusiveC4/Comforts
 */

package c4.comforts.common.util;

import c4.comforts.Comforts;
import c4.comforts.client.gui.ComfortsTab;
import c4.comforts.common.ConfigHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemDye;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.List;

public class ComfortsUtil {

    public static List<PotionEffect> debuffs = new ArrayList<>();

    public static final ComfortsTab comfortsTab = new ComfortsTab();

    public static int getColor(int metadata) {
        return ItemDye.DYE_COLORS[15 - metadata];
    }

    public static void applyDebuffs(EntityPlayer player) {
        for (PotionEffect effect : getDebuffs()) {
            player.addPotionEffect(new PotionEffect(effect.getPotion(), effect.getDuration(), effect.getAmplifier()));
        }
    }

    public static List<PotionEffect> getDebuffs() {
        return debuffs;
    }

    public static void parseDebuffs() {

        for (String s : ConfigHandler.sleepingBagDebuffs) {

            String[] elements = s.split("\\s+");
            Potion potion = Potion.getPotionFromResourceLocation(elements[0]);
            if (potion == null) continue;
            int duration = 0;
            int amp = 0;
            try {
                duration = Math.max(1, Math.min(Integer.parseInt(elements[1]), 1600));
                amp = Math.max(1, Math.min(Integer.parseInt(elements[2]), 4));
            } catch (Exception e1) {
                Comforts.logger.log(Level.ERROR, "Problem parsing sleeping bag debuffs in config!", e1);
            }
            debuffs.add(new PotionEffect(potion, duration * 20, amp - 1));
        }
    }
}
