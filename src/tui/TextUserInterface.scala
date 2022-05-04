package tui

import camera.{CameraTransformer, CameraView}
import javafx.geometry.{Insets, Pos}
import javafx.scene.layout.StackPane
import javafx.scene.{Group, PerspectiveCamera, Scene, SceneAntialiasing, SubScene}
import javafx.scene.paint.{Color, PhongMaterial}
import javafx.scene.shape.{Cylinder, DrawMode}
import javafx.scene.transform.Rotate
import javafx.stage.Stage
import tree.Tree.{checkInSight, createTreeFromRoot, getOcTreeLeafsSection, listWiredBox}
import utils.configLoad

import java.util.Scanner
import scala.io.Source
import scala.io.StdIn.readLine

object TextUserInterface {
  val input = new Scanner(System.in)
  var configFile = "conf.txt"

  val redMaterial = new PhongMaterial()
  redMaterial.setDiffuseColor(Color.rgb(150, 0, 0))
  val greenMaterial = new PhongMaterial()
  greenMaterial.setDiffuseColor(Color.rgb(0, 255, 0))
  val blueMaterial = new PhongMaterial()
  blueMaterial.setDiffuseColor(Color.rgb(0, 0, 150))

  def printInitialMenu() = {
    println("============== This is the application Text-Based User Interface ============== \n" +
            "1. - Choose configuration file \n" +
            "2. - Launch 3D object visualization \n" +
            "3. - Scale Octree (valid values : 0.5 or 2.0)\n " +
            "4. - Color graphical models to Sepia \n" +
            "5. - Remove green color component from graphical models \n" +
            "0. - To quit\n" +
    "===============================================================================")

    val in = input.nextInt()
    in match {
      case 0 =>
        this.exit()
      case 1=>
        this.chooseConfigFile()
      case 2=>
        val environmentObjects = configLoad.createEnvironment()
        val textLines = Source.fromFile(configFile).getLines().toList
        val lista3DObjects = configLoad.create3DObjects(textLines)
        configLoad.addObjectToWorld(lista3DObjects,environmentObjects)
        //EQUIVALE A SUBSTITUICAO 1
        val wiredBoxes = listWiredBox(getOcTreeLeafsSection(List(createTreeFromRoot(((8.0,8.0,8.0),16),lista3DObjects))))
        configLoad.addObjectToWorld(wiredBoxes,environmentObjects)
        //OcTree creation
        val ourOctree = createTreeFromRoot(((8.0,8.0,8.0),16),lista3DObjects)
        // Camera
        //environmentObjects.getChildren.add(cameraTransform)
        // SubScene - composed by the nodes present in the worldRoot
        val subScene = new SubScene(environmentObjects, 800, 600, true, SceneAntialiasing.BALANCED)
        subScene.setFill(Color.DARKSLATEGRAY)
        //subScene.setCamera(camera)
        // camera.CameraView - an additional perspective of the environment
        val cameraView = new CameraView(subScene)
        cameraView.setFirstPersonNavigationEabled(true)
        cameraView.setFitWidth(550)
        cameraView.setFitHeight(525)
        cameraView.getRx.setAngle(-45)
        cameraView.getT.setZ(-100)
        cameraView.getT.setY(-500)
        cameraView.getCamera.setTranslateZ(-50)
        cameraView.startViewing
        val camVolume = new Cylinder(10, 50, 10)
        camVolume.setTranslateX(1)
        camVolume.getTransforms().add(new Rotate(45, 0, 0, 0, Rotate.X_AXIS))
        camVolume.setMaterial(blueMaterial)
        camVolume.setDrawMode(DrawMode.LINE)
        // Position of the camera.CameraView: Right-bottom corner
        StackPane.setAlignment(cameraView, Pos.BOTTOM_RIGHT)
        StackPane.setMargin(cameraView, new Insets(5))
        // Scene - defines what is rendered (in this case the subScene and the cameraView)
        val root = new StackPane(subScene, cameraView)
        subScene.widthProperty.bind(root.widthProperty)
        subScene.heightProperty.bind(root.heightProperty)
        val scene = new Scene(root, 810, 610, true, SceneAntialiasing.BALANCED)
        checkInSight(wiredBoxes,camVolume,environmentObjects)
        //Mouse left click interaction
        scene.setOnMouseClicked((event) => {
          camVolume.setTranslateX(camVolume.getTranslateX + 2)
          checkInSight(wiredBoxes,camVolume,environmentObjects)
          environmentObjects.getChildren.removeAll()
        })
        val stage = new Stage()
        //setup and start the Stage
        stage.setTitle("PPM Project 21/22")
        stage.setScene(scene)
        stage.show
      case 3=>
      case 4=>
      case 5=>
    }
  }

  def chooseConfigFile() = {
    println("Insert the full name of the new config file, with its extension" +
    "(The file should be in the project base!)")
    val confFile = readLine(">")
    this.configFile = confFile
    print("Config file changed to " + confFile)
    mainLoop()
  }
  def exit() = {
    println("Exiting the program...")
    System.exit(1)
  }
  def mainLoop() : Unit = {
    printInitialMenu()
    }
}





