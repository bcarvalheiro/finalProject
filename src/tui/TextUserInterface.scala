package tui

import javafx.scene.Group
import javafx.scene.paint.{Color, PhongMaterial}
import tree.Tree.{createTreeFromRoot, getOcTreeLeafsSection, listWiredBox}
import utils.configLoad

import scala.io.Source

object TextUserInterface {
  var configFile = "conf.txt"

  val redMaterial = new PhongMaterial()
  redMaterial.setDiffuseColor(Color.rgb(150, 0, 0))

  val greenMaterial = new PhongMaterial()
  greenMaterial.setDiffuseColor(Color.rgb(0, 255, 0))

  val blueMaterial = new PhongMaterial()
  blueMaterial.setDiffuseColor(Color.rgb(0, 0, 150))

  def printInitialMenu() = {
    println("============== This is the application Text-Based User Interface ==============")
    println("1. - Choose configuration file")
    println("2. - Launch 3D object visualization")
    println("3. - Scale Octree (valid values : 0.5 or 2.0)")
    println("4. - Color graphical models to Sepia")
    println("5. - Remove green color component from graphical models")
    println("0. - To quit")
    println("===============================================================================")
  }

  def mainLoop() : Unit = {
    printInitialMenu()
    val userInput = scala.io.StdIn.readLine("> ")
    userInput match {
      case "1" =>
        val confFile = scala.io.StdIn.readLine("Insert the name of the config file: ")
        configFile = confFile
        println("ConfigFile set to " + confFile)
      case "2" =>
        //Call the function to create the OcTree
        val wolrdObject = configLoad.createEnvironment()




    }
    mainLoop()
  }
}





