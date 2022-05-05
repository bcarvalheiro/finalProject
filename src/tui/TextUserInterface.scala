package tui

import camera.{CameraTransformer, CameraView}
import javafx.application.Application
import javafx.geometry.{Insets, Pos}
import javafx.scene.layout.StackPane
import javafx.scene.{Group, PerspectiveCamera, Scene, SceneAntialiasing, SubScene}
import javafx.scene.paint.{Color, PhongMaterial}
import javafx.scene.shape.{Cylinder, DrawMode}
import javafx.scene.transform.Rotate
import javafx.stage.Stage
import sceneViewer.sceneStarter
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
        Application.launch(classOf[sceneStarter])
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





