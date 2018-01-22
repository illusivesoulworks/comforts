package c4.comforts.common.util;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class OreDictHelper {

    /**
     * Check to see if a block is registered with a certain oreDict name
     * @param dictEntry name eg. logWood / oreCopper
     * @param state block state to search against
     * @return true if a dictionary matching the dictEntry parameter was found.
     */
    public static boolean oreDictMatches(String dictEntry, IBlockState state) {
        ItemStack stack = new ItemStack(state.getBlock(),1, state.getBlock().getMetaFromState(state));
        boolean result = false;
        if(!stack.isEmpty()) {//avoid invalid stack exception
            int[] ids = OreDictionary.getOreIDs(stack);//get all dict entries for this block
            for (int i : ids) {//get oreName for each entry
                //or the result to return true if any matches dictEntry
                result |= OreDictionary.getOreName(i).contentEquals(dictEntry);
            }
            return result;
        }
        return false;
    }
}
