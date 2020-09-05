package szewek.mctool.app

import javafx.application.Application
import javafx.beans.Observable
import javafx.beans.binding.Bindings
import javafx.beans.binding.ObjectBinding
import javafx.collections.ObservableList
import javafx.scene.Node
import tornadofx.UIComponent
import tornadofx.UI_COMPONENT_PROPERTY
import tornadofx.importStylesheet
import kotlin.reflect.KClass

inline fun <reified T : UIComponent> Node.comesFrom() = properties[UI_COMPONENT_PROPERTY] is T
fun <T : UIComponent> Node.comesFrom(kc: KClass<T>) = kc.isInstance(properties[UI_COMPONENT_PROPERTY])

inline fun <reified T : Application> T.import(cssFile: String) = importStylesheet(T::class.java.getResource(cssFile).toExternalForm())
inline fun <reified T : Node> T.css(): String? = T::class.java.let { it.getResource("/css/${it.simpleName}.css").toExternalForm() }
