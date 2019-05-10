/*
 * Copyright (C) 2017-2019  C4
 *
 * This file is part of Comforts, a mod made for Minecraft.
 *
 * Comforts is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Comforts is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Comforts.  If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.comforts.common.init;

import net.minecraft.block.Block;
import net.minecraft.item.EnumDyeColor;
import net.minecraftforge.registries.ObjectHolder;

import java.util.EnumMap;

public class ComfortsBlocks {

    public static final EnumMap<EnumDyeColor, Block> SLEEPING_BAGS = new EnumMap<>(EnumDyeColor.class);

    public static final EnumMap<EnumDyeColor, Block> HAMMOCKS = new EnumMap<>(EnumDyeColor.class);

    @ObjectHolder("comforts:rope_and_nail")
    public static final Block ROPE_AND_NAIL = null;
}
