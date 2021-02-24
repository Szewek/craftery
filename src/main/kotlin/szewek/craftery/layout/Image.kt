package szewek.craftery.layout

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import szewek.craftery.util.Downloader
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_INT_ARGB
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

@Composable
fun ImageURL(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null
) {
    val imgBytes = remember { mutableStateOf(emptyImageBytes) }
    GlobalScope.launch {
        val stream = Downloader.downloadFile(url) { _, _ -> }
        imgBytes.value = stream.readBytes()
    }
    Image(org.jetbrains.skija.Image.makeFromEncoded(imgBytes.value).asImageBitmap(), contentDescription, modifier, alignment, contentScale, alpha, colorFilter)
}

val emptyImageBytes: ByteArray = ByteArrayOutputStream().also {
    ImageIO.write(BufferedImage(1, 1, TYPE_INT_ARGB), "png", it)
}.toByteArray()
