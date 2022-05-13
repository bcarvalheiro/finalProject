package gui

import javafx.fxml.FXML
import javafx.scene.SubScene
import javafx.scene.paint.Color.color

import java.awt.Button
import sceneViewer.sceneStarter
import tree.Octree
import tree.Tree.Placement
import utils.configLoad

class Controller {

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

  @FXML
  private var subSceneFinal:SubScene = _

  //method automatically invoked after the @FXML fields have been injected
  @FXML
  def initialize(): Unit = {
    InitSubScene.subScene.widthProperty.bind(subSceneInitial.widthProperty)
    InitSubScene.subScene.heightProperty.bind(subSceneInitial.heightProperty)
    subSceneInitial.setRoot(InitSubScene.root)
  }

  def onScaleOctree05Clicked() = {
   subSceneFinal = configLoad.scaleOctreeNew(oct: Octree[Placement],0.5)
  }

  def onScaleOctree2Clicked() = {
    subSceneFinal = configLoad.scaleOctreeNew(oct: Octree[Placement],2.0)
  }

  def onSepiaClicked() = {
    subSceneFinal = configLoad.mapColourEffect(configLoad.toSepia(color : (Int,Int,Int)),oct: Octree[Placement])
  }

  def onGreenRemoveClicked() = {
   subSceneFinal = configLoad.mapColourEffect(configLoad.removeGreen(color : (Int,Int,Int)),oct: Octree[Placement])
  }

}
