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
import tree.Tree.{checkInSight, createTreeFromRoot, getOcTreeLeafsSection, listWiredBox}
import tui.TextUserInterface
import utils.configLoad
import utils.configLoad.blueMaterial
class sceneStarter extends  Application {
  override def start(stage: Stage): Unit = {

  }
}

object FxApp{
    def main(args: Array[String]): Unit = {
    //Application.launch(classOf[Main], args: _*)
  }
}
