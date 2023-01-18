package io.github.sceneview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.viewinterop.AndroidView
import io.github.sceneview.node.Node

@Composable
fun Scene(
    modifier: Modifier = Modifier,
    nodes: List<Node> = listOf(),
    onCreate: ((SceneView) -> Unit)? = null
) {
    if (LocalInspectionMode.current) {
        ScenePreview(modifier)
    } else {
        var sceneViewNodes = remember { listOf<Node>() }

        AndroidView(
            modifier = modifier,
            factory = { context ->
                SceneView(context).apply {
                    onCreate?.invoke(this)
                }
            },
            update = { sceneView ->
                sceneViewNodes.filter { it !in nodes }.forEach {
                    sceneView.removeChild(it)
                }
                nodes.filter { it !in sceneViewNodes }.forEach {
                    sceneView.addChild(it)
                }
                sceneViewNodes = nodes
            }
        )
    }
}

@Composable
private fun ScenePreview(modifier: Modifier) {
    Box(
        modifier = modifier
            .background(Color.DarkGray)
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.Center),
            text = "SceneView",
            color = Color.White
        )
    }
}