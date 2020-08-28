package szewek.mctool.app

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.TableView
import szewek.mctool.mcdata.DataResourceType
import szewek.mctool.mcdata.ResourceType
import szewek.mctool.mcdata.Scanner
import szewek.mctool.mcdata.fixedDesc
import szewek.mctool.util.ZipLoader
import tornadofx.*

class LookupMod(private val name: String, private val zipLoader: ZipLoader): View("Lookup: $name") {
    private val dataList = FXCollections.observableArrayList<ResourceFieldData>()
    private val capList = FXCollections.observableArrayList<Triple<String, String, String>>()
    private val fieldList = FXCollections.observableArrayList<FieldData>()
    override val root = LoaderPane()

    init {
        root.apply {
            accordion(
                foldTable("Data resources (%d)", dataList) {
                    readonlyColumn("Path", ResourceFieldData::name).pctWidth(30)
                    readonlyColumn("Type", ResourceFieldData::drtype).pctWidth(15)
                    readonlyColumn("Namespace", ResourceFieldData::namespace).pctWidth(15)
                    readonlyColumn("Info", ResourceFieldData::info).remainingWidth()
                    smartResize()
                },
                foldTable("Capabilities (%d)", capList) {
                    readonlyColumn("Class", Triple<String, String, String>::first).pctWidth(20)
                    readonlyColumn("Capabilities", Triple<String, String, String>::second).pctWidth(30)
                    readonlyColumn("Inherited from", Triple<String, String, String>::third).remainingWidth()
                    smartResize()
                },
                foldTable("Fields (%d)", fieldList) {
                    readonlyColumn("Name", FieldData::name).pctWidth(15)
                    readonlyColumn("Resource type", FieldData::rtype).pctWidth(10)
                    readonlyColumn("From", FieldData::from).pctWidth(30)
                    readonlyColumn("Info", FieldData::info).remainingWidth()
                    smartResize()
                }
            )
        }
        lookupFields()
    }

    private fun lookupFields() {
        root.launchTask {
            updateMessage("Downloading file...")
            updateProgress(0, 1)
            val z = zipLoader.load(::updateProgress)
            updateMessage("Scanning classes...")
            updateProgress(0, 1)
            val si = Scanner.scanArchive(z)
            updateMessage("Gathering results...")
            updateProgress(2, 3)
            val dx = si.res.values.map {
                val info = if (it.details.isEmpty()) "(None)" else it.details.entries.joinToString("\n") { (k, v) -> "$k: $v" }
                ResourceFieldData(it.name, it.type, it.namespace, info)
            }
            val cx = si.caps.values.map { c ->
                val f = c.fields + c.supclasses.flatMap { si.getAllCapsFromType(it) }
                val x = if (f.isNotEmpty()) f.joinToString("\n") else "(None provided)"
                val y = c.supclasses.let { if (it.isNotEmpty()) it.joinToString("\n") else "(None provided)" }
                Triple(c.name, x, y)
            }
            val x = si.classes.values.flatMap { it.staticFields.values.map { v ->
                val desc = v.fixedDesc
                val rt = si.getResourceType(desc)
                val ift = si.getAllInterfaceTypes(desc)
                FieldData(v.name, rt, it.node.name, "Type: $desc\nInterfaces: ${ift.joinToString()}")
            } }
            updateProgress(3, 3)
            runLater {
                dataList.setAll(dx)
                capList.setAll(cx)
                fieldList.setAll(x)
            }

        }
    }

    private fun <T> foldTable(fmt: String, items: ObservableList<T>, op: TableView<T>.() -> Unit) = titledpane(items.sizeProperty.asString(fmt)) {
        isExpanded = false
        tableview(items, op)
    }

    internal class FieldData(val name: String, val rtype: ResourceType, val from: String, val info: String)
    internal class ResourceFieldData(val name: String, val drtype: DataResourceType, val namespace: String, val info: String)
}