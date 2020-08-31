package szewek.mctool.app

import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.geometry.Pos
import javafx.scene.control.ComboBox
import javafx.scene.control.ScrollPane
import javafx.scene.layout.*
import szewek.mctool.app.recipe.CraftingView
import szewek.mctool.app.recipe.SlotView
import szewek.mctool.mcdata.MinecraftData
import tornadofx.*

class RecipeCreator: View("Create recipes") {
    override val root = GridPane()
    private val recipeType = SimpleStringProperty("")
    private val allRecipes = FXCollections.observableArrayList<String>()
    //private val mapViews = FXCollections.observableHashMap<String, Node>()

    init {
        task { MinecraftData.getAsset("/") }
        val pct50 = root.widthProperty().divide(2)

        root.addRow(0, ComboBox(allRecipes).apply { bind(recipeType) })
        root.addRow(
                1,
                VBox(CraftingView()).apply { prefWidthProperty().bind(pct50) },
                ScrollPane(TilePane().apply {
                    alignment = Pos.TOP_LEFT
                } children {
                    for (i in 0..26) {
                        + SlotView()
                    }
                }).apply {
                    prefWidthProperty().bind(pct50)
                    isFitToWidth = true
                    isFitToHeight = true
                }
        )
        root.rowConstraints.addAll(
                RowConstraints(),
                RowConstraints().apply {
                    //isFillHeight = true
                    //maxHeight = Double.MAX_VALUE
                    vgrow = Priority.ALWAYS
                }
        )
    }
}