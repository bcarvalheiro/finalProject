package tui

import camera.{CameraTransformer, CameraView}
import javafx.application.Application
import javafx.geometry.{Insets, Pos}
import javafx.scene.layout.StackPane
import javafx.scene.{Group, Node, PerspectiveCamera, Scene, SceneAntialiasing, SubScene}
import javafx.scene.paint.{Color, PhongMaterial}
import javafx.scene.shape.{Box, Cylinder, DrawMode}
import javafx.scene.transform.Rotate
import javafx.stage.Stage
import sceneViewer.FxApp.objList
import sceneViewer.sceneStarter
import tree.Octree
import tree.Tree.{Placement, checkInSight, createTreeFromRoot, getOcTreeLeafsSection, listWiredBox}
import utils.configLoad
import sceneViewer.sceneStarter

import java.util.Scanner
import scala.io.Source
import scala.io.StdIn.readLine
import scala.sys.SystemProperties.headless.clear

object TextUserInterface {
  val input = new Scanner(System.in)

  def printInitialMenu() = {
    println("============== This is the application Text-Based User Interface ============== \n" +
            "1. - Scale Octree (valid values : 0.5 or 2.0) \n" +
            "2. - Set effects on graphical models \n" +
            "0. - Show scene\n" +
    "===============================================================================")
  }

  def effectsMenu() : Unit = {
    println("Wich effect do you want to apply? \n" +
              "1- Sepia\n" +
                "2- Remove green from color\n" +
                  "0- Exit\n"
    )
  }

  def scaleOctree(octree : Octree[Placement]) :  Octree[Placement] = {
    println("Choose the scale : (possible values : Double the Tree (input = 2) + " +
      "or halven the size (input = 0.5)")
    val in = input.next()
    in.toDouble match {
      case 0.5 | 2.0 =>
        val scaledOct = configLoad.scaleOctree(in.toDouble,octree)
        sceneViewer.FxApp.setOctree(scaledOct)
        println("Octree scaled in " + in + "and the result is \n" + scaledOct)
        mainLoop(scaledOct)
        scaledOct
      case _ =>
        print("Invalid Value")
        scaleOctree(octree)
    }
  }

  def chooseConfigFile() : String = {
    println("Insert the full name of the new config file, with its extension" +
    "(The file should be in the project base!)")
    val confFile = readLine(">")
    confFile
  }

  def exit() = {
    println("Exiting the program...")
    System.exit(1)
  }

  def main(args: Array[String]): Unit = {
    val confFile = chooseConfigFile()
    //Programa inicial e cria uma octree com o ficheiro escolhido
    val objcList = configLoad.create3DObjectsAux(confFile)
    val octree : Octree[Placement] = createTreeFromRoot(((8.0,8.0,8.0),16),objcList)
    println("this is the initial octree" + octree)
    sceneViewer.FxApp.setOctree(octree)
    mainLoop(octree)
  }


  def mainLoop(sceneOctree : Octree[Placement]) : Unit = {
    printInitialMenu()
    val in = input.nextInt()
    in match {
      case 0 =>
        Application.launch(classOf[sceneStarter])
      case 1=>
        //Tenho aqui a OcTree escalada, supostamente (NOT TESTED), e agora preciso de criar uma scene nova com esta OcTree
        //E tambem tenho aqui as wiredBoxes que representam a tree
        val scaledOctree = scaleOctree(sceneOctree) //devolvidos nesta função
        mainLoop(scaledOctree)
      case 2=>
        effectsMenu()
          val in = input.nextInt()
          in match {
            case 1=>
              val sepiaOct = configLoad.mapColourEffect(configLoad.toSepia (_ : Int,_ : Int ,_ : Int) ,sceneOctree)
              sceneViewer.FxApp.setOctree(sepiaOct)
              mainLoop(sepiaOct)
              println("Sepiaed octree" + sepiaOct)
            case 2=>
              val greenLessOct = configLoad.mapColourEffect(configLoad.removeGreen(_ : Int, _ : Int, _ : Int), sceneOctree)
              sceneViewer.FxApp.setOctree(greenLessOct)
              mainLoop(greenLessOct)
              println(greenLessOct)
            case default =>
              println("invalid value")
              mainLoop(sceneOctree)

          }
      case _ =>
        println("Invalid option")
        mainLoop(sceneOctree)
    }
  }
}





