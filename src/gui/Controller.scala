package gui

import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.SubScene
import javafx.scene.control.Button
import javafx.scene.paint.Color.color
import javafx.scene.shape.Box
import sceneViewer.sceneStarter
import tree.Octree
import tree.Tree.{Placement, checkInSight, createTreeFromRoot, getOcTreeLeafsSection, listWiredBox}
import ui.TextUserInterface
import ui.TextUserInterface.objcList
import utils.ConfigLoad

class Controller {

  val configFile = TextUserInterface.chooseConfigFile()
  val objList = ConfigLoad.create3DObjectsAux(configFile)
  val minSize = TextUserInterface.chooseMinSize()
  val maxDepth = TextUserInterface.chooseDepth()
  val maxSize = TextUserInterface.chooseInitialSize()
  var octree : Octree[Placement] = createTreeFromRoot(((maxSize/2,maxSize/2,maxSize/2),maxSize),objList,minSize,maxDepth)
  var wiredBoxes : List[Box] = listWiredBox(getOcTreeLeafsSection(List(octree)))


  @FXML
  private var scaleOctree05: Button = _

  @FXML
  private var scaleOctree2: Button = _

  @FXML
  private var sepia: Button = _

  @FXML
  private var greenRemove: Button = _

  @FXML
  private var subSceneInitial:SubScene = _

  //method automatically invoked after the @FXML fields have been injected
  @FXML
  def initialize(): Unit = {
    InitSubScene.subScene.widthProperty.bind(subSceneInitial.widthProperty)
    InitSubScene.subScene.heightProperty.bind(subSceneInitial.heightProperty)
    ConfigLoad.addObjectToWorld(wiredBoxes,InitSubScene.worldRoot)
    ConfigLoad.addObjectToWorld(objList,InitSubScene.worldRoot)
    subSceneInitial.setRoot(InitSubScene.root)

  }

  @FXML
  def exitApplication(event: Nothing): Unit = {
    Platform.exit()
  }

  def onScaleOctree05Clicked() = {
    octree = ConfigLoad.scaleOctree(0.5,octree)
    InitSubScene.worldRoot.getChildren.removeIf(x => wiredBoxes.contains(x))
    wiredBoxes = listWiredBox(getOcTreeLeafsSection(List(octree)))
    ConfigLoad.addObjectToWorld(wiredBoxes,InitSubScene.worldRoot)
  }

  def onScaleOctree2Clicked() = {
    octree = ConfigLoad.scaleOctree(2.0, octree)
    InitSubScene.worldRoot.getChildren.removeIf(x => wiredBoxes.contains(x))
    wiredBoxes = listWiredBox(getOcTreeLeafsSection(List(octree)))
    ConfigLoad.addObjectToWorld(wiredBoxes,InitSubScene.worldRoot)
  }

  def onSepiaClicked() = {
    octree = ConfigLoad.mapColourEffect(ConfigLoad.toSepia(_ : Int,_ : Int ,_ : Int),octree)
  }

  def onGreenRemoveClicked() = {
   octree = ConfigLoad.mapColourEffect(ConfigLoad.removeGreen(_ : Int,_ : Int ,_ : Int),octree)
  }

  def onSubSceneClicked() : Unit = {
    InitSubScene.camVolume.setTranslateX(InitSubScene.camVolume.getTranslateX + 2)
    checkInSight(wiredBoxes,InitSubScene.camVolume,InitSubScene.worldRoot)
  }
}
