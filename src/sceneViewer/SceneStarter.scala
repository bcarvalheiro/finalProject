package sceneViewer
import camera.{CameraTransformer, CameraView}
import javafx.application.Application
import javafx.geometry.Insets
import javafx.scene.shape._
import javafx.scene.transform.Rotate
import javafx.scene.Group
import javafx.stage.Stage
import javafx.geometry.Pos
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.{PerspectiveCamera, Scene, SceneAntialiasing, SubScene}
import tree.Tree.{checkInSight, getObjList, getOcTreeLeafsSection, listWiredBox, redMaterial}
import ui.TextUserInterface
import utils.ConfigLoad
import utils.ConfigLoad.blueMaterial

import scala.io.Source
class sceneStarter extends  Application {
  override def start(stage: Stage): Unit = {



    //3D objects
    val lineX = new Line(0, 0, 200, 0)
    lineX.setStroke(Color.GREEN)

    val lineY = new Line(0, 0, 0, 200)
    lineY.setStroke(Color.YELLOW)

    val lineZ = new Line(0, 0, 200, 0)
    lineZ.setStroke(Color.LIGHTSALMON)
    lineZ.getTransforms().add(new Rotate(-90, 0, 0, 0, Rotate.Y_AXIS))

    val camVolume = new Cylinder(10, 50, 10)
    camVolume.setTranslateX(1)
    camVolume.getTransforms().add(new Rotate(45, 0, 0, 0, Rotate.X_AXIS))
    camVolume.setMaterial(blueMaterial)
    camVolume.setDrawMode(DrawMode.LINE)

    val wiredBox = new Box(TextUserInterface.initialSize, TextUserInterface.initialSize, TextUserInterface.initialSize)
    wiredBox.setTranslateX(TextUserInterface.initialSize / 2)
    wiredBox.setTranslateY(TextUserInterface.initialSize / 2)
    wiredBox.setTranslateZ(TextUserInterface.initialSize / 2)
    wiredBox.setMaterial(redMaterial)
    wiredBox.setDrawMode(DrawMode.LINE)

    // 3D objects (group of nodes - javafx.scene.Node) that will be provide to the subScene
    val worldRoot:Group = new Group(wiredBox, camVolume, lineX, lineY, lineZ)
    val octree = TextUserInterface.ourTree

    //OcTree creation
    val wiredBoxes = listWiredBox(getOcTreeLeafsSection(List(octree)))
    ConfigLoad.addObjectToWorld(wiredBoxes,worldRoot)
    ConfigLoad.addObjectToWorld(TextUserInterface.objcList,worldRoot)
    ConfigLoad.saveToFile(worldRoot)
    println("this is my current OcTree!" + TextUserInterface.ourTree)
    // Camera
    val camera = new PerspectiveCamera(true)

    val cameraTransform = new CameraTransformer
    cameraTransform.setTranslate(0, 0, 0)
    cameraTransform.getChildren.add(camera)
    camera.setNearClip(0.1)
    camera.setFarClip(10000.0)

    camera.setTranslateZ(-500)
    camera.setFieldOfView(20)
    cameraTransform.ry.setAngle(-45.0)
    cameraTransform.rx.setAngle(-45.0)
    worldRoot.getChildren.add(cameraTransform)

    // SubScene - composed by the nodes present in the worldRoot
    val subScene = new SubScene(worldRoot, 800, 600, true, SceneAntialiasing.BALANCED)
    subScene.setFill(Color.DARKSLATEGRAY)
    subScene.setCamera(camera)

    // CameraView - an additional perspective of the environment
    val cameraView = new CameraView(subScene)
    cameraView.setFirstPersonNavigationEabled(true)
    cameraView.setFitWidth(350)
    cameraView.setFitHeight(225)
    cameraView.getRx.setAngle(-45)
    cameraView.getT.setZ(-100)
    cameraView.getT.setY(-500)
    cameraView.getCamera.setTranslateZ(-50)
    cameraView.startViewing

    // Position of the CameraView: Right-bottom corner
    StackPane.setAlignment(cameraView, Pos.BOTTOM_RIGHT)
    StackPane.setMargin(cameraView, new Insets(5))

    // Scene - defines what is rendered (in this case the subScene and the cameraView)
    val root = new StackPane(subScene, cameraView)
    subScene.widthProperty.bind(root.widthProperty)
    subScene.heightProperty.bind(root.heightProperty)

    val scene = new Scene(root, 810, 610, true, SceneAntialiasing.BALANCED)
    //Mouse left click interaction
    scene.setOnMouseClicked((event) => {
      camVolume.setTranslateX(camVolume.getTranslateX + 2)
      checkInSight(wiredBoxes,camVolume,worldRoot)
      worldRoot.getChildren.removeAll()
    })


    stage.setTitle("PPM Project 21/22")
    checkInSight(wiredBoxes,camVolume,worldRoot)
    stage.setScene(scene)
    stage.show
  }

}

object FxApp{
    def main(args: Array[String]): Unit = {
      if(args(0) == "1")
        Application.launch(classOf[sceneStarter], args: _*)
      else
        println("Run GUI")
      //TextUserInterface.mainLoop()
  }
}
