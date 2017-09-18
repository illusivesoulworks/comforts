/*
 * Copyright (c) 2017. C4, MIT License
 */

package c4.comforts.common.util;

import net.minecraft.item.ItemDye;

public class ComfortsHelper {

    public static int getColor(int metadata) {
        return ItemDye.DYE_COLORS[15 - metadata];
    }
}
