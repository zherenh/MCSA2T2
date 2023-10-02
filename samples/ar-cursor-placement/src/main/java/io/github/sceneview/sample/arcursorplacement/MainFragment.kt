package io.github.sceneview.sample.arcursorplacement

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.ar.core.Anchor
import com.google.ar.core.HitResult
import com.google.ar.core.Pose
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import dev.romainguy.kotlin.math.Float3
import io.github.sceneview.SceneView
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.ArNode
import io.github.sceneview.ar.node.CursorNode
import com.google.android.filament.MaterialInstance
import io.github.sceneview.material.setBaseColor
import io.github.sceneview.math.Scale
import io.github.sceneview.math.toFloat3
import io.github.sceneview.model.GLBLoader
import io.github.sceneview.model.ModelInstance
import io.github.sceneview.utils.Color
import io.github.sceneview.utils.doOnApplyWindowInsets
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainFragment : Fragment(R.layout.fragment_main) {

    var anchors: MutableList<Anchor> = mutableListOf()
    var recordCount: Int = 0
    var lastAnchor: Anchor? = null
    lateinit var sceneView: ArSceneView
    lateinit var loadingView: View
    lateinit var anchorButton: ExtendedFloatingActionButton
    lateinit var addNodeBtn: ExtendedFloatingActionButton
    var sphereModelInstance: ModelInstance? = null
    lateinit var cursorNode: CursorNode
    var modelNode: ArModelNode? = null
    var lineColor =  Color(0.0f, 0.0f, 1.0f, 1.0f)
    var placedLines = mutableListOf<ArNode>()
    var modelInstance: ModelInstance? = null
    var lineScale = 0.1f
    lateinit var cubeModelInstance: ModelInstance
    var isLoading = false
        set(value) {
            field = value
            loadingView.isGone = !value
            anchorButton.isGone = value
            addNodeBtn.isGone = value
        }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingView = view.findViewById(R.id.loadingView)
        anchorButton = view.findViewById<ExtendedFloatingActionButton>(R.id.anchorButton).apply {
            val bottomMargin = (layoutParams as ViewGroup.MarginLayoutParams).bottomMargin
            doOnApplyWindowInsets { systemBarsInsets ->
                (layoutParams as ViewGroup.MarginLayoutParams).bottomMargin =
                    systemBarsInsets.bottom + bottomMargin
            }
            setOnClickListener { cursorNode.createAnchor()?.let { anchorOrMove(it) } }
        }
        addNodeBtn = view.findViewById<ExtendedFloatingActionButton>(R.id.recordButton).apply {
            setOnClickListener {
                // Record current cursor position
                cursorNode.createAnchor()?.let { newAnchor ->
                    // If lastAnchor is not null, calculate the distance
                    lastAnchor?.let { lastAnchor ->



                        val newPose = newAnchor.pose
                        val lastPose = lastAnchor.pose

                        val dx = newPose.tx() - lastPose.tx()
                        val dy = newPose.ty() - lastPose.ty()
                        val dz = newPose.tz() - lastPose.tz()

                        // Compute the Euclidean distance and convert it to centimeters
                        val distance = Math.sqrt((dx * dx + dy * dy + dz * dz).toDouble())

//                        // Calculate the midpoint for positioning the cube
//                        val midX = (newPose.tx() + lastPose.tx()) / 2
//                        val midY = (newPose.ty() + lastPose.ty()) / 2
//                        val midZ = (newPose.tz() + lastPose.tz()) / 2
//                        val midPointPose = Pose.makeTranslation(midX, midY, midZ)
//                        val midPointAnchor =
//                        try {
//                            val cubeNode = ArModelNode(sceneView.engine).apply {
//                                modelInstance = cubeModelInstance
//                                scale = Float3(0.05f, 0.05f, distance.toFloat())
//                                position = Float3(midX, midY, midZ)
//                                anchor = midPointAnchor
//                                // Set anchor, parent, or other properties as necessary
//                            }
//                            sceneView.addChild(cubeNode)
//                        } catch (e: Exception) {
//                            Log.e("ERROR", "Error adding line to scene: ${e.message}")
//                        }


                        val distanceInCm = distance * 100

                        // Now you can use `distance` wherever you need
                        // For example, displaying it in a toast
                        Toast.makeText(context, "Distance between anchors: ${String.format("%.2f", distanceInCm)} cm", Toast.LENGTH_SHORT).show()


                    }

//                    // place a sphere at the current cursor position
//                    val posNode =ArModelNode(sceneView.engine,).apply {
//                        modelInstance = sphereModelInstance // Set the sphere model instance
//                        anchor = newAnchor // Set the anchor
//                        parent = sceneView // Set the parent of the node to the sceneView
//                        scaleToUnits
//                    }


                    // Set newAnchor as lastAnchor for the next click
                    lastAnchor = newAnchor
                    // Increment the record count and add the new anchor to the list
                    recordCount++
                    anchors.add(newAnchor)
                    Log.d("DEBUG", "Anchor created successfully") // Log
                    try {
                        sceneView.addChild(ArModelNode(sceneView.engine,"models/cube.glb",true,0.05f).apply {
                            Log.d("DEBUG", "Model instance set: $modelInstance") // Log for debugging
                            anchor = newAnchor // Set the anchor
                            parent = sceneView // Set the parent of the node to the sceneView
                            Log.d("DEBUG", "Node added to scene") // Log for debugging
                        })
                    } catch (e: Exception) {
                        Log.e("ERROR", "Error adding node to scene: ${e.message}") // Log error message
                    }


                    val anchorPose = newAnchor.pose
                    val position = anchorPose.translation.let {
                        "x: ${it[0]}, y: ${it[1]}, z: ${it[2]}"
                    }
                    // Display Toast with record count and position
                    Toast.makeText(context, "Record count: $recordCount at position $position", Toast.LENGTH_SHORT).show()
                    Log.e("pos:",position)
                    Log.e("anchors",anchors.toString())
                }
            }
        }

        sceneView = view.findViewById<ArSceneView?>(R.id.sceneView).apply {

            planeRenderer.isVisible = false
            depthEnabled = true
            instantPlacementEnabled = true
            // Handle a fallback in case of non AR usage. The exception contains the failure reason
            // e.g. SecurityException in case of camera permission denied
            onArSessionFailed = { e: Exception ->
                // If AR is not available or the camara permission has been denied, we add the model
                // directly to the scene for a fallback 3D only usage
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            }
            onTapAr = { hitResult, _ ->
                anchorOrMove(hitResult.createAnchor())
            }
        }

        cursorNode = CursorNode(sceneView.engine).apply {
            onHitResult = { node, _ ->
                if (!isLoading) {
                    anchorButton.isGone = !node.isTracking
                }
            }
        }
        sceneView.addChild(cursorNode)

        isLoading = true
        lifecycleScope.launchWhenCreated {
            modelInstance = GLBLoader.loadModelInstance(
                context = requireContext(),
                glbFileLocation = "models/bed.glb"
            )

            cubeModelInstance = GLBLoader.loadModelInstance(
                context = requireContext(),
                glbFileLocation = "models/cube.glb" // Replace with the actual path to your cube model
            )!!

            modelNode?.modelInstance = modelInstance
            anchorButton.text = getString(R.string.move_object)
            anchorButton.setIconResource(R.drawable.ic_target)
            isLoading = false

            while (true) {
                updateDistance()
                delay(1000)
            }
        }
    }



    fun anchorOrMove(anchor: Anchor) {
        if (modelNode == null) {
            modelNode = ArModelNode(
                sceneView.engine,
                followHitPosition = false
            ).apply {
                modelInstance = modelInstance
                parent = sceneView
            }
        }
        modelNode!!.anchor = anchor
    }




    // update the distance between lastAnchor and current position of the CursorNode in camera
    suspend fun updateDistance() {
        lastAnchor?.let { lastAnchor ->
            val lastPose = lastAnchor.pose
            val lastPosition = lastPose.translation

            // Get current position of the CursorNode
            val cursorPosition = cursorNode.worldPosition

            // Calculate the distance
            val distance = Math.sqrt(
                Math.pow((cursorPosition.x - lastPosition[0]).toDouble(), 2.0) +
                        Math.pow((cursorPosition.y - lastPosition[1]).toDouble(), 2.0) +
                        Math.pow((cursorPosition.z - lastPosition[2]).toDouble(), 2.0)
            )


            val distanceInCm = distance * 100

            // Update the UI with the calculated distance
            Toast.makeText(context, "anchor and current position have : ${String.format("%.2f", distanceInCm)} cm", Toast.LENGTH_SHORT).show()
            delay(2000)
        }


    }

    private fun addLineBetweenPoints(hitResult: HitResult, scene: SceneView, from: Vector3, to: Vector3) {
        // prepare an anchor position
        val camQ = scene.cameraNode.worldQuaternion
        val f1 = floatArrayOf(to.x, to.y, to.z)
        val f2 = floatArrayOf(camQ.x, camQ.y, camQ.z, camQ.w)
        val anchorPose = Pose(f1, f2)

        // make an ARCore Anchor
        val anchor = hitResult.createAnchor()
        // Node that is automatically positioned in world space based on the ARCore Anchor.
        val anchorNode = ArNode(scene.engine)
        anchorNode.anchor = anchor
        anchorNode.parent = scene

        // Compute a line's length
        val lineLength = Vector3.subtract(from, to).length()

        val job = GlobalScope.launch {
            val result = async {
                // Load a new model instance for each tap
                val modelInstance = GLBLoader.loadModelInstance(
                    context = requireContext(),
                    glbFileLocation = "models/cube.glb"
                ) as ModelInstance
                for (material in modelInstance.materialInstances) {
                    material.setBaseColor(lineColor)
                }
                // 3. make node
                val node = ArNode(scene.engine)
                node.parent = anchorNode
                node.modelInstance = modelInstance


                // 4. set rotation
                val difference = Vector3.subtract(to, from)
                val directionFromTopToBottom = difference.normalized()
                val rotationFromAToB =
                    Quaternion.lookRotation(
                        directionFromTopToBottom,
                        Vector3.up()
                    )

                val quaternionRot = Quaternion.multiply(
                    rotationFromAToB,
                    Quaternion.axisAngle(Vector3(1.0f, 0.0f, 0.0f), 90f)
                )
                node.worldPosition = Vector3.add(from, to).scaled(.5f).toFloat3()
                node.worldPosition.y = from.y
                node.worldQuaternion = dev.romainguy.kotlin.math.Quaternion(
                    quaternionRot.x,
                    quaternionRot.y,
                    quaternionRot.z,
                    quaternionRot.w
                )
                node.worldScale = Scale(lineScale, lineLength, lineScale)
                node.isPositionEditable = false
                node.isRotationEditable = false
                node.isScaleEditable = false

                placedLines.add(anchorNode)
                //calculateMeasure()
            }

            result.await()
        }
        runBlocking {
            job.join()
        }
    }


}


