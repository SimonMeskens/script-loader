import net.minecraft.item.Item2
import net.minecraft.recipe.CraftingRecipeManager

def removeRecipe(item) {
    CraftingRecipeManager.instance.recipes.removeAll { it.output.itemId == item.id }
}

def pickaxe = Item2.IRON_PICKAXE2

removeRecipe(pickaxe)
println(pickaxe.getMaxCount2())
