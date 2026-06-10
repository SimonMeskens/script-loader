import net.minecraft.item.Item2
import net.minecraft.recipe.CraftingRecipeManager2

import org.codehaus.groovy.runtime.DefaultGroovyMethods

def l = [1,2,3]

println DefaultGroovyMethods.findAll(l) { it > 1 }
println DefaultGroovyMethods.collect(l) { it * 2 }

println l.findAll { it > 1 }
println l.collect { it * 2 }

def removeRecipe(item) {
    CraftingRecipeManager2.instance2.recipes.removeAll { it.output.itemId == item.id }
}

def pickaxe = Item2.IRON_PICKAXE2

removeRecipe(pickaxe)
println(pickaxe.getMaxCount2())
