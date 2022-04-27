package utils
import camera.CameraTransformer
import javafx.scene.paint.{Color, PhongMaterial}
import javafx.scene.shape._
import javafx.scene.transform.Rotate
import javafx.scene.{Group, Node, PerspectiveCamera}
import tree.{OcEmpty, OcLeaf, OcNode, Octree}
import tree.Tree.{Placement, Section}
import javafx.scene.transform.{Rotate, Translate}
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

import scala.annotation.tailrec
import scala.collection.immutable.Stream.Empty.append
import scala.util.{Success, Try}
//
object configLoad {

  val redMaterial = new PhongMaterial()
  redMaterial.setDiffuseColor(Color.rgb(150, 0, 0))

  val greenMaterial = new PhongMaterial()
  greenMaterial.setDiffuseColor(Color.rgb(0, 255, 0))

  val blueMaterial = new PhongMaterial()
  blueMaterial.setDiffuseColor(Color.rgb(0, 0, 150))

  def transScaleObject(object3D : Node, valueTrans : (Double,Double,Double), valueScale : (Double,Double,Double)) : Node = {
    object3D.setTranslateX(valueTrans._1)
    object3D.setTranslateY(valueTrans._2)
    object3D.setTranslateZ(valueTrans._3)
    object3D.setScaleX(valueScale._1)
    object3D.setScaleY(valueScale._2)
    object3D.setScaleZ(valueScale._3)
    object3D
  }


  def create3DObjects(objectList : List[String]) : List[Node] = {
    objectList match {
      case Nil => Nil
      case obj::tail => {
        val object3D = obj.split(" ").map(_.trim).toList
        if (object3D(0).equals("Box")){
          if (object3D.size == 2) {
            val node = new Box(1,1,1)
            transScaleObject(node,(0.0,0.0,0.0), (1.0,1.0,1.0))
            node.setMaterial(splitColorStringToMaterial(object3D(1)))
            val v = create3DObjects(tail)
            v.prepended(node)
          }else {
            val node = new Box(1, 1, 1)
            val translation = ( object3D(2).toDouble, object3D(3).toDouble, object3D(4).toDouble )
            val scale = ( object3D(5).toDouble, object3D(6).toDouble, object3D(7).toDouble )
            transScaleObject(node,translation,scale)
            node.setMaterial(splitColorStringToMaterial(object3D(1)))
            val v = create3DObjects(tail)
            v.prepended(node)
          }
        } else {
          if (object3D.size == 2) {
            val node = new Cylinder(0.5,1,10)
            transScaleObject(node,(0.0,0.0,0.0), (1.0,1.0,1.0))
            node.setMaterial(splitColorStringToMaterial(object3D(1)))
            val v = create3DObjects(tail)
            v.prepended(node)
          }else {
            val node = new Cylinder(0.5, 1, 10)
            val translation = ( object3D(2).toDouble, object3D(3).toDouble, object3D(4).toDouble )
            val scale = ( object3D(5).toDouble, object3D(6).toDouble, object3D(7).toDouble )
            transScaleObject(node,translation,scale)
            node.setMaterial(splitColorStringToMaterial(object3D(1)))
            val v = create3DObjects(tail)
            v.prepended(node)
          }
        }
      }
    }
  }

  def createEnvironment() : Group = {
    //3D objects
    val lineX = new Line(0, 0, 200, 0)
    lineX.setStroke(Color.GREEN)

    val lineY = new Line(0, 0, 0, 200)
    lineY.setStroke(Color.YELLOW)

    val lineZ = new Line(0, 0, 200, 0)
    lineZ.setStroke(Color.LIGHTSALMON)
    lineZ.getTransforms().add(new Rotate(-90, 0, 0, 0, Rotate.Y_AXIS))

    val camVolume = new Cylinder(10, 50, 10)
    camVolume.setTranslateX(1)
    camVolume.getTransforms().add(new Rotate(45, 0, 0, 0, Rotate.X_AXIS))
    camVolume.setMaterial(blueMaterial)
    camVolume.setDrawMode(DrawMode.LINE)

    val wiredBox = new Box(32, 32, 32)
    wiredBox.setTranslateX(16)
    wiredBox.setTranslateY(16)
    wiredBox.setTranslateZ(16)
    wiredBox.setMaterial(redMaterial)
    wiredBox.setDrawMode(DrawMode.LINE)

    val rootBox = new Box(16, 16, 16)
    rootBox.setTranslateX(8)
    rootBox.setTranslateY(8)
    rootBox.setTranslateZ(8)
    rootBox.setMaterial(redMaterial)
    rootBox.setDrawMode(DrawMode.LINE)

    // Camera
    val camera = new PerspectiveCamera(true)

    val cameraTransform = new CameraTransformer
    cameraTransform.setTranslate(0, 0, 0)
    cameraTransform.getChildren.add(camera)
    camera.setNearClip(0.1)
    camera.setFarClip(10000.0)
    camera.setTranslateZ(-500)
    camera.setFieldOfView(20)
    cameraTransform.ry.setAngle(-45.0)
    cameraTransform.rx.setAngle(-45.0)

    val envinronmentGroup : Group = addObjectToWorld(List(wiredBox, camVolume, lineX, lineY, lineZ,rootBox,cameraTransform),new Group())
    envinronmentGroup
  }

  def splitColorStringToMaterial(rgbColor : String) : PhongMaterial = {
    val colorAux  = rgbColor.substring(1,rgbColor.length-1).split(",")
    val color1 : List[Double] = List(colorAux(0).toDouble,colorAux(1).toDouble,colorAux(2).toDouble)
    def subTract(color1 : List[Double]) : List[Double]={
      color1 match {
        case List() => Nil
        case color::listColor =>
          if(color == 255.0)
            (color-0.1)+:subTract(listColor)
          else
            color+:subTract(listColor)
      }
    }
    val color = subTract(color1)

    //color map (x => if (x.eq(255.0)) x-1 else None)
    val material = new PhongMaterial()
    material.setDiffuseColor(Color.rgb(color(0).toInt,color(1).toInt,color(2).toInt))
    material
  }

  def addObjectToWorld(listaObjects3D : List[Node], worldRoot : Group) : Group = listaObjects3D match {
    case List() => worldRoot
    case head::tail => {
      worldRoot.getChildren.add(head)
      addObjectToWorld(tail, worldRoot)
    }
  }

  def scaleObject(listObject : List[Node], fact : Double ) : List[Node] = {
   def scaleAux(listObject : List[Node]) : List[Node] = {
     listObject match {
       case List() => Nil
       case head::tail =>
         head.setScaleX(fact * head.getScaleX)
         head.setScaleY(fact * head.getScaleY)
         head.setScaleZ(fact * head.getScaleZ)
         val scaledObjects = scaleAux(tail)
         scaledObjects.appended(head)
     }
   }
    val scaledList = scaleAux(listObject)
    scaledList
  }

  def scaleOctree(d: Double, oct : Octree[Placement]) : Octree[Placement] = {
    oct match{
      case OcEmpty => OcEmpty
      case OcLeaf((value : Placement, (placement : Placement, lista : List[Node]))) =>
        val objList : List[Node] = lista
        objList map (x => x.setScaleX(x.getScaleX * d))
        objList map (x => x.setScaleY(x.getScaleY * d))
        objList map (x => x.setScaleZ(x.getScaleZ * d))
        val newPlacement = ((value._1._1 * d, value._1._2 * d, value._1._3 * d), value._2 * d)
        OcLeaf(newPlacement,objList)
      case OcNode(oldPlacement : Placement, q1, q2, q3,q4,q5,q6,q7,q8) =>
        val newPlacement = ((oldPlacement._1._1 * d, oldPlacement._1._2 * d, oldPlacement._1._3 * d), oldPlacement._2 * d)
        OcNode[Placement]((newPlacement), scaleOctree(d,q1), scaleOctree(d,q2), scaleOctree(d,q3), scaleOctree(d,q4),
          scaleOctree(d,q5), scaleOctree(d,q6), scaleOctree(d,q7),scaleOctree(d,q8))
    }
  }


  def mapColourEffect(func: List[Node] => List[Node], oct: Octree[Placement]): Octree[Placement] = {
    oct match {
      case OcEmpty => OcEmpty
      case OcLeaf(section: Section) =>
        OcLeaf(section._1, func(section._2))
      case OcNode(placement: Placement, q1, q2, q3, q4, q5, q6, q7, q8) =>
        OcNode[Placement](placement, mapColourEffect(func, q1), mapColourEffect(func, q2), mapColourEffect(func, q3),
          mapColourEffect(func, q4), mapColourEffect(func, q5), mapColourEffect(func, q6), mapColourEffect(func, q7),
          mapColourEffect(func, q8))
    }
  }

  def toSepia(objList : List[Node]) : List[Node] =
    objList match {
      case List() => Nil
      case h::tail =>
      //40%r + 77%g + 20%b; G = 35%r + 69%g + 17%b; B = 27%r + 53%g + 13%b)
          val r = h.asInstanceOf[Shape3D].getMaterial.asInstanceOf[PhongMaterial].getDiffuseColor
        val colorString = ("(" + (0.4 * r.getRed + 0.77*r.getGreen + 0.20 * r.getBlue).toString + "," +
          (0.35*r.getRed + 0.69*r.getGreen + 0.17*r.getBlue).toString + "," +
          (0.27 * r.getRed + 0.53 * r.getGreen + 0.13*r.getBlue).toString + ")")
          val newMaterial =splitColorStringToMaterial(colorString)
        h.asInstanceOf[Shape3D].setMaterial(newMaterial)
        h+:(toSepia(tail))
    }

  def removeGreen(objList : List[Node]) : List[Node] =
    objList match {
      case List()=>Nil
      case h::tail=>
          val r = h.asInstanceOf[Shape3D].getMaterial.asInstanceOf[PhongMaterial].getDiffuseColor
          val color = ("(" + r.getRed + ",0," + r.getBlue + ")")
          val newMaterial = splitColorStringToMaterial(color)
        h.asInstanceOf[Shape3D].setMaterial(newMaterial)
        h+:removeGreen(tail)
    }
}