/*
 * Copyright (c) 2017 <C4>
 *
 * This Java class is distributed as a part of Comforts.
 * Comforts is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.comforts.common.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemDye;
import net.minecraft.potion.PotionEffect;

import java.util.List;

public class ComfortsHelper {

    public static int getColor(int metadata) {
        return ItemDye.DYE_COLORS[15 - metadata];
    }

    public static void applyDebuffs(EntityPlayer player, List<PotionEffect> effects) {
        for (PotionEffect effect : effects) {
            player.addPotionEffect(new PotionEffect(effect.getPotion(), effect.getDuration(), effect.getAmplifier()));
        }
    }
}
