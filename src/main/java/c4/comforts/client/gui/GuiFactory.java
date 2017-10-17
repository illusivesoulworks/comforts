/*
 * Copyright (c) 2017 <C4>
 *
 * This Java class is distributed as a part of Comforts.
 * Comforts is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.comforts.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

import java.util.Set;

public class GuiFactory implements IModGuiFactory {

    public void initialize(Minecraft minecraftInstance) {}

    public boolean hasConfigGui() { return true;}

    public GuiScreen createConfigGui(GuiScreen parentScreen) {
        return new ConfigGui(parentScreen);
    }

    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }
}