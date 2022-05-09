package sceneViewer
import camera.{CameraTransformer, CameraView}
import javafx.application.Application
import javafx.geometry.Insets
import javafx.scene.paint.PhongMaterial
import javafx.scene.shape._
import javafx.scene.transform.{Rotate, Translate}
import javafx.scene.{Group, Node}
import javafx.stage.Stage
import javafx.geometry.Pos
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.{PerspectiveCamera, Scene, SceneAntialiasing, SubScene}
import sceneViewer.FxApp.sceneOctree
import tree.{OcEmpty, Octree}
import tree.Tree.{Placement, checkInSight, createTreeFromRoot, getObjList, getOcTreeLeafsSection, greenMaterial, listWiredBox, redMaterial}
import tui.TextUserInterface
import utils.configLoad
import utils.configLoad.blueMaterial

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

    val wiredBox = new Box(32, 32, 32)
    wiredBox.setTranslateX(16)
    wiredBox.setTranslateY(16)
    wiredBox.setTranslateZ(16)
    wiredBox.setMaterial(redMaterial)
    wiredBox.setDrawMode(DrawMode.LINE)

    // 3D objects (group of nodes - javafx.scene.Node) that will be provide to the subScene
    val worldRoot:Group = new Group(wiredBox, camVolume, lineX, lineY, lineZ)

//    //OcTree creation
    val wiredBoxes = listWiredBox(getOcTreeLeafsSection(List(sceneOctree)))

    val objList = getObjList(sceneOctree)
    configLoad.addObjectToWorld(wiredBoxes,worldRoot)
    configLoad.addObjectToWorld(objList,worldRoot)
    println("this is my current OcTree!" + sceneOctree)
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
      //To-Do: Create a function to paint in a different color the Spatial Partitions that are inside the camView
      checkInSight(wiredBoxes,camVolume,worldRoot)
      worldRoot.getChildren.removeAll()
    })


    stage.setTitle("PPM Project 21/22")
    stage.setScene(scene)
    stage.show
  }

}

object FxApp{
  var sceneOctree : Octree[Placement] = OcEmpty
  var objList : List[Node] = List()

    def main(args: Array[String]): Unit = {
//   Application.launch(classOf[sceneStarter], args: _*)
      //TextUserInterface.mainLoop()
  }
  def setOctree (oct : Octree[Placement]) : Unit = {
    this.sceneOctree = oct
    this.objList = tree.Tree.getObjList(sceneOctree)

    TextUserInterface.mainLoop(sceneOctree)
  }
}
