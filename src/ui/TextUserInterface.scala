package ui

import tree.Octree
import tree.Tree.{Placement, createTreeFromRoot}
import utils.ConfigLoad

import java.util.Scanner
import scala.annotation.tailrec
import scala.io.StdIn.readLine

object TextUserInterface extends App {
  val input = new Scanner(System.in)
  val confFile = chooseConfigFile()
  val objcList = ConfigLoad.create3DObjectsAux(confFile)
  val maxDepth = chooseDepth()
  val minSize = chooseMinSize()
  val initialSize = chooseInitialSize()
  val placement = (((initialSize / 2).toDouble, (initialSize / 2).toDouble, (initialSize / 2).toDouble), initialSize.toDouble)

  if (objcList.size != tree.Tree.listOfObjInSection(placement, objcList).size) {
    println("ERROR: At least one object is out of bounds!")
    System.exit(0)
  }

  val octree: Octree[Placement] = createTreeFromRoot(placement, objcList, minSize, maxDepth)
  println("this is the initial octree" + octree)
  val ourTree = mainLoop(octree)
  sceneViewer.FxApp.main(Array("1"))


  def chooseInitialSize(): Int = {
    println("Choose the size of the root spatial partition: (Choose a value between 1 and 32)")
    val initialSize = readLine(">")
    if (initialSize.toInt > 0 && initialSize.toInt < 33)
      initialSize.toInt
    else if (initialSize.toInt < 1) {
      println("The size of the root spatial partition will be set to 1")
      1
    }
    else {
      println("The size of the root spatial partition will be set to 32")
      32
    }
  }

  def chooseDepth():Int = {
    println("Choose the max depth of the Octree : ")
    val maxDepth = readLine(">")
    if (maxDepth.toInt >= 0)
      maxDepth.toInt
    else
      -1
  }

  def chooseMinSize(): Int = {
    print("Which is the minimium size of the spatial partitions?: (Choose a value between 1 and 32)\n")
    val minSize = readLine(">")
    if (minSize.toInt > 0 && minSize.toInt <= 32)
      minSize.toInt
    else
      -1
  }

  def printInitialMenu() : Unit = {
    println("============== This is the application Text-Based User Interface ============== \n" +
            "1. - Scale Octree (valid values : 0.5 or 2.0) \n" +
            "2. - Set effects on graphical models \n" +
            "0. - Show scene\n" +
    "===============================================================================")
  }

  def effectsMenu() : Unit = {
    println("Which effect do you want to apply? \n" +
              "1- Sepia\n" +
                "2- Remove green from color\n" +
                  "0- Exit\n"
    )
  }
@tailrec
  def scaleOctree(octree : Octree[Placement]) :  Octree[Placement] = {
    println("Choose the scale : (possible values : Double the Tree (input = 2) + " +
      "or halven the size (input = 0.5)")
    val in = input.next()
    in.toDouble match {
      case 0.5 | 2.0 =>
        val scaledOct = ConfigLoad.scaleOctreeNew(octree,in.toDouble)
        //sceneViewer.FxApp.setOctree(scaledOct)
        println("Octree scaled in " + in + "and the result is \n" + scaledOct)
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

  def mainLoop(sceneOctree : Octree[Placement]) : Octree[Placement] = {
    printInitialMenu()
    val in = input.nextInt()
    in match {
      case 0 =>

        sceneOctree
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
              val sepiaOct = ConfigLoad.mapColourEffect(ConfigLoad.toSepia (_ : Int,_ : Int ,_ : Int) ,sceneOctree)
              //sceneViewer.FxApp.setOctree(sepiaOct)
              mainLoop(sepiaOct)
              println("Sepiaed octree" + sepiaOct)
            case 2=>
              val greenLessOct = ConfigLoad.mapColourEffect(ConfigLoad.removeGreen(_ : Int, _ : Int, _ : Int), sceneOctree)
              //sceneViewer.FxApp.setOctree(greenLessOct)
              mainLoop(greenLessOct)
              println(greenLessOct)
            case _ =>
              println("invalid value")
              mainLoop(sceneOctree)

          }
      case _ =>
        println("Invalid option")
        mainLoop(sceneOctree)
    }
    sceneOctree
  }
}





