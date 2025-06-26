package crafting

import entity.Anvil
import entity.Chest
import entity.Furnace
import entity.Lantern
import entity.Oven
import entity.Workbench
import item.ToolType
import item.resource.Resource

object Crafting {
    val anvilRecipes: MutableList<Recipe> = ArrayList<Recipe>()
    val ovenRecipes: MutableList<Recipe> = ArrayList<Recipe>()
    val furnaceRecipes: MutableList<Recipe> = ArrayList<Recipe>()
    val workbenchRecipes: MutableList<Recipe> = ArrayList<Recipe>()

    init {
        workbenchRecipes.add(
            FurnitureRecipe(furnitureFactory = { Lantern() }).addCost(
                Resource.wood,
                5
            ).addCost(Resource.slime, 10).addCost(Resource.glass, 4)
        )

        workbenchRecipes.add(
            FurnitureRecipe(furnitureFactory = { Oven() }).addCost(
                Resource.stone,
                15
            )
        )
        workbenchRecipes.add(
            FurnitureRecipe(furnitureFactory = { Furnace() }).addCost(
                Resource.stone,
                20
            )
        )
        workbenchRecipes.add(
            FurnitureRecipe(furnitureFactory = { Workbench() }).addCost(
                Resource.wood,
                20
            )
        )
        workbenchRecipes.add(
            FurnitureRecipe(furnitureFactory = { Chest() }).addCost(
                Resource.wood,
                20
            )
        )
        workbenchRecipes.add(
            FurnitureRecipe(furnitureFactory = { Anvil() }).addCost(
                Resource.ironIngot,
                5
            )
        )

        workbenchRecipes.add(
            ToolRecipe(
                ToolType.sword,
                0
            ).addCost(Resource.wood, 5)
        )
        workbenchRecipes.add(
            ToolRecipe(
                ToolType.axe,
                0
            ).addCost(Resource.wood, 5)
        )
        workbenchRecipes.add(
            ToolRecipe(
                ToolType.hoe,
                0
            ).addCost(Resource.wood, 5)
        )
        workbenchRecipes.add(
            ToolRecipe(ToolType.pickaxe, 0).addCost(
                Resource.wood,
                5
            )
        )
        workbenchRecipes.add(
            ToolRecipe(
                ToolType.shovel,
                0
            ).addCost(Resource.wood, 5)
        )
        workbenchRecipes.add(
            ToolRecipe(
                ToolType.sword,
                1
            ).addCost(Resource.wood, 5).addCost(Resource.stone, 5)
        )
        workbenchRecipes.add(
            ToolRecipe(
                ToolType.axe,
                1
            ).addCost(Resource.wood, 5).addCost(Resource.stone, 5)
        )
        workbenchRecipes.add(
            ToolRecipe(
                ToolType.hoe,
                1
            ).addCost(Resource.wood, 5).addCost(Resource.stone, 5)
        )
        workbenchRecipes.add(
            ToolRecipe(ToolType.pickaxe, 1).addCost(
                Resource.wood,
                5
            ).addCost(Resource.stone, 5)
        )
        workbenchRecipes.add(
            ToolRecipe(
                ToolType.shovel,
                1
            ).addCost(Resource.wood, 5).addCost(Resource.stone, 5)
        )

        anvilRecipes.add(
            ToolRecipe(
                ToolType.sword,
                2
            ).addCost(Resource.wood, 5).addCost(Resource.ironIngot, 5)
        )
        anvilRecipes.add(
            ToolRecipe(ToolType.axe, 2).addCost(
                Resource.wood,
                5
            ).addCost(Resource.ironIngot, 5)
        )
        anvilRecipes.add(
            ToolRecipe(ToolType.hoe, 2).addCost(
                Resource.wood,
                5
            ).addCost(Resource.ironIngot, 5)
        )
        anvilRecipes.add(
            ToolRecipe(
                ToolType.pickaxe,
                2
            ).addCost(Resource.wood, 5).addCost(Resource.ironIngot, 5)
        )
        anvilRecipes.add(
            ToolRecipe(
                ToolType.shovel,
                2
            ).addCost(Resource.wood, 5).addCost(Resource.ironIngot, 5)
        )

        anvilRecipes.add(
            ToolRecipe(
                ToolType.sword,
                3
            ).addCost(Resource.wood, 5).addCost(Resource.goldIngot, 5)
        )
        anvilRecipes.add(
            ToolRecipe(ToolType.axe, 3).addCost(
                Resource.wood,
                5
            ).addCost(Resource.goldIngot, 5)
        )
        anvilRecipes.add(
            ToolRecipe(ToolType.hoe, 3).addCost(
                Resource.wood,
                5
            ).addCost(Resource.goldIngot, 5)
        )
        anvilRecipes.add(
            ToolRecipe(
                ToolType.pickaxe,
                3
            ).addCost(Resource.wood, 5).addCost(Resource.goldIngot, 5)
        )
        anvilRecipes.add(
            ToolRecipe(
                ToolType.shovel,
                3
            ).addCost(Resource.wood, 5).addCost(Resource.goldIngot, 5)
        )

        anvilRecipes.add(
            ToolRecipe(
                ToolType.sword,
                4
            ).addCost(Resource.wood, 5).addCost(Resource.gem, 50)
        )
        anvilRecipes.add(
            ToolRecipe(ToolType.axe, 4).addCost(
                Resource.wood,
                5
            ).addCost(Resource.gem, 50)
        )
        anvilRecipes.add(
            ToolRecipe(ToolType.hoe, 4).addCost(
                Resource.wood,
                5
            ).addCost(Resource.gem, 50)
        )
        anvilRecipes.add(
            ToolRecipe(
                ToolType.pickaxe,
                4
            ).addCost(Resource.wood, 5).addCost(Resource.gem, 50)
        )
        anvilRecipes.add(
            ToolRecipe(
                ToolType.shovel,
                4
            ).addCost(Resource.wood, 5).addCost(Resource.gem, 50)
        )

        furnaceRecipes.add(
            ResourceRecipe(Resource.ironIngot).addCost(
                Resource.ironOre,
                4
            ).addCost(Resource.coal, 1)
        )
        furnaceRecipes.add(
            ResourceRecipe(Resource.goldIngot).addCost(
                Resource.goldOre,
                4
            ).addCost(Resource.coal, 1)
        )
        furnaceRecipes.add(
            ResourceRecipe(Resource.glass).addCost(
                Resource.sand,
                4
            ).addCost(Resource.coal, 1)
        )

        ovenRecipes.add(
            ResourceRecipe(Resource.bread).addCost(
                Resource.wheat,
                4
            )
        )
    }
}
