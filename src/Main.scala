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
import sceneViewer.sceneStarter
import tree.Tree.{checkInSight, createTreeFromRoot, getOcTreeLeafsSection, listWiredBox}
import tui.TextUserInterface
import utils.configLoad
import utils.configLoad.{blueMaterial, mapColourEffect, removeGreen, toSepia}

import scala.io.Source

class Main extends Application {

  //Auxiliary types
  type Point = (Double, Double, Double)
  type Size = Double
  type Placement = (Point, Size) //1st point: origin, 2nd point: size

  //Shape3D is an abstract class that extends javafx.scene.Node
  //Box and Cylinder are subclasses of Shape3D
  type Section = (Placement, List[Node])  //example: ( ((0.0,0.0,0.0), 2.0), List(new Cylinder(0.5, 1, 10)))

  override def start(stage: Stage): Unit = {
    //Get and print program arguments (args: Array[String])
    val params = getParameters
    println("Program arguments:" + params.getRaw)

    //Materials to be applied to the 3D objects
    val redMaterial = new PhongMaterial()
    redMaterial.setDiffuseColor(Color.rgb(150,0,0))

    val greenMaterial = new PhongMaterial()
    greenMaterial.setDiffuseColor(Color.rgb(0,255,0))

    val blueMaterial = new PhongMaterial()
    blueMaterial.setDiffuseColor(Color.rgb(0,0,150))

    val environmentObjects = configLoad.createEnvironment()

    //Make objects from file, instead of those two prebuild:
    //conf.txt has to be  avariable, so you can choose wich file you will run as config
    val configFile = "conf.txt"
    val textLines = Source.fromFile(configFile).getLines().toList
    val lista3DObjects = configLoad.create3DObjects(textLines)
    configLoad.addObjectToWorld(lista3DObjects,environmentObjects)
    
    //EQUIVALE A SUBSTITUICAO 1
    val wiredBoxes = listWiredBox(getOcTreeLeafsSection(List(createTreeFromRoot(((8.0,8.0,8.0),16),lista3DObjects))))
    configLoad.addObjectToWorld(wiredBoxes,environmentObjects)

    //OcTree creation
    val ourOctree = createTreeFromRoot(((8.0,8.0,8.0),16),lista3DObjects)
    //println(ourOctree)


//    val camera = new PerspectiveCamera(true)
//
//    val cameraTransform = new CameraTransformer
//    cameraTransform.setTranslate(0, 0, 0)
//    cameraTransform.getChildren.add(camera)
//    camera.setNearClip(0.1)
//    camera.setFarClip(10000.0)
//    camera.setTranslateZ(-500)
//    camera.setFieldOfView(20)
//    cameraTransform.ry.setAngle(-45.0)
//    cameraTransform.rx.setAngle(-45.0)
//
//    // SubScene - composed by the nodes present in the worldRoot
//    val subScene = new SubScene(environmentObjects, 800, 600, true, SceneAntialiasing.BALANCED)
//    subScene.setFill(Color.DARKSLATEGRAY)
//    subScene.setCamera(camera)
//
//    // camera.CameraView - an additional perspective of the environment
//    val cameraView = new CameraView(subScene)
//    cameraView.setFirstPersonNavigationEabled(true)
//    cameraView.setFitWidth(550)
//    cameraView.setFitHeight(525)
//    cameraView.getRx.setAngle(-45)
//    cameraView.getT.setZ(-100)
//    cameraView.getT.setY(-500)
//    cameraView.getCamera.setTranslateZ(-50)
//    cameraView.startViewing
//
//    val camVolume = new Cylinder(10, 50, 10)
//    camVolume.setTranslateX(1)
//    camVolume.getTransforms().add(new Rotate(45, 0, 0, 0, Rotate.X_AXIS))
//    camVolume.setMaterial(blueMaterial)
//    camVolume.setDrawMode(DrawMode.LINE)
//
//      // Position of the camera.CameraView: Right-bottom corner
//      StackPane.setAlignment(cameraView, Pos.BOTTOM_RIGHT)
//      StackPane.setMargin(cameraView, new Insets(5))
//
//    // Scene - defines what is rendered (in this case the subScene and the cameraView)
//    val root = new StackPane(subScene, cameraView)
//    subScene.widthProperty.bind(root.widthProperty)
//    subScene.heightProperty.bind(root.heightProperty)
//
//    val scene = new Scene(root, 810, 610, true, SceneAntialiasing.BALANCED)
//    checkInSight(wiredBoxes,camVolume,environmentObjects)


//    //Mouse left click interaction
//    scene.setOnMouseClicked((event) => {
//      camVolume.setTranslateX(camVolume.getTranslateX + 2)
//      checkInSight(wiredBoxes,camVolume,environmentObjects)
//      environmentObjects.getChildren.removeAll()
//    })
//
//    //setup and start the Stage
//    stage.setTitle("PPM Project 21/22")
//    stage.setScene(scene)
//    stage.show
  //mapColourEffect(removeGreen(_ : Int,_ : Int ,_ : Int),ourOctree)

    //adding boxes b2 and b3 to the world
    //configLoad.addObjectToWorld(ourOctree,worldRoot)
    //println(configLoad.scaleOctree(0.5,ourOctree))
  }
//  override def init(): Unit = {
//    println("init")
//  }
//
//  override def stop(): Unit = {
//    println("stopped")
//  }
}

object FxApp {
  def main(args: Array[String]): Unit = {
    println("This is before launching app")
    TextUserInterface.mainLoop()
    //Application.launch(classOf[Main], args: _*)
    //Application.launch(classOf[sceneStarter], args : _ *)
  }
}

