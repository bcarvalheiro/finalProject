package gui

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.{Parent, Scene}
import javafx.stage.Stage

class GUI extends Application {

  override def start(primaryStage: Stage): Unit = {
    primaryStage.setTitle("PPM Project")
    val fxmlLoader =
      new FXMLLoader(getClass.getResource("Controller.fxml"))
    val mainViewRoot: Parent = fxmlLoader.load()
    val scene = new Scene(mainViewRoot)
    primaryStage.setResizable (false);
    primaryStage.setScene(scene)
    primaryStage.show()
  }

}
