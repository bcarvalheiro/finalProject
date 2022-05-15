package utils
import javafx.scene.paint.{Color, PhongMaterial}
import javafx.scene.shape._
import javafx.scene.{Group, Node}
import tree.{OcEmpty, OcLeaf, OcNode, Octree}
import tree.Tree.{Placement, createTreeFromRoot, getObjList}
import ui.TextUserInterface

import java.io.{File, PrintWriter}
import scala.annotation.tailrec
import scala.collection.convert.ImplicitConversions.`collection AsScalaIterable`
import scala.io.Source
object ConfigLoad {

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

  def create3DObjectsAux(configFile : String) : List[Node] = {
    //TO-DO : Try to change it to a try catch function
    val textLines = Source.fromFile(configFile).getLines().toList
    create3DObjects(textLines)
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

  def splitColorStringToMaterial(rgbColor : String) : PhongMaterial = {
    val colorAux  = rgbColor.substring(1,rgbColor.length-1).split(",")
    val color = Color.rgb(math min(colorAux(0).toInt, 255),math min(colorAux(1).toInt, 255),math min(colorAux(2).toInt, 255))
    val material = new PhongMaterial()
    material.setDiffuseColor(color)
    material
  }

   @tailrec
   def addObjectToWorld(listaObjects3D : List[Node], worldRoot : Group) : Group =
    listaObjects3D match {
      case List() => worldRoot
      case head::tail =>
        worldRoot.getChildren.add(head)
        addObjectToWorld(tail, worldRoot)
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

  def scaleOctreeNew(oct : Octree[Placement], fact : Double) : Octree[Placement] = {
    val objList = getObjList(oct)
    val scaledObjects = scaleObject(objList,fact)
    oct match {
      case OcNode(value : Placement, _,_,_,_,_,_,_,_) =>
        val newPlacement : Placement = ((value._1._1, value._1._2,value._1._3), value._2 * fact)
        createTreeFromRoot(newPlacement,scaledObjects,TextUserInterface.minSize, TextUserInterface.maxDepth)
    }
  }

  def scaleOctree(d: Double, oct : Octree[Placement]) : Octree[Placement] = {
    oct match{
      case OcEmpty => OcEmpty
      case OcLeaf((value : Placement, (placement : Placement, objList : List[Node]))) =>
        objList map (x => transScaleObject(x,(x.getTranslateX*d, x.getTranslateY*d,x.getTranslateZ*d),
         (x.getScaleX * d, x.getScaleY*d,x.getScaleZ*d)))
        val newPlacement = ((placement._1._1 * d, placement._1._2 * d, placement._1._3 * d), placement._2 * d)
        val newParentsPlacement = ((value._1._1 * d, value._1._2 * d, value._1._3 * d), value._2 * d)
        OcLeaf(newParentsPlacement,(newPlacement,objList))
      case OcNode(oldPlacement : Placement, q1, q2, q3,q4,q5,q6,q7,q8) =>
        val newPlacement = ((oldPlacement._1._1 * d, oldPlacement._1._2 * d, oldPlacement._1._3 * d), oldPlacement._2 * d)
        OcNode[Placement]((newPlacement), scaleOctree(d,q1), scaleOctree(d,q2), scaleOctree(d,q3), scaleOctree(d,q4),
          scaleOctree(d,q5), scaleOctree(d,q6), scaleOctree(d,q7),scaleOctree(d,q8))
    }
  }

  def mapColourEffect(func : (Int,Int,Int) => Color, oct: Octree[Placement]): Octree[Placement] = {
    oct match {
      case OcEmpty => OcEmpty
      case OcLeaf((value : Placement, (placement : Placement, lista : List[Node]))) => lista match {
          case List() => OcLeaf((value,(placement,lista)))
          case head::tail =>
            val color = head.asInstanceOf[Shape3D].getMaterial.asInstanceOf[PhongMaterial].getDiffuseColor
            val colorInt = ((color.getRed * 255).toInt, (color.getGreen * 255).toInt, (color.getBlue * 255).toInt)
            println("color2" + colorInt)
            val newColor : Color = func(colorInt._1,colorInt._2,colorInt._3)
            println("this is the new color " + newColor)
            val newMaterial = new PhongMaterial()
            newMaterial.setDiffuseColor(newColor)
            head.asInstanceOf[Shape3D].setMaterial(newMaterial)
            mapColourEffect(func,OcLeaf(value,(placement,tail)))
            OcLeaf((value,(placement,lista)))
        }
      case OcNode(placement: Placement, q1, q2, q3, q4, q5, q6, q7, q8) =>
        OcNode[Placement](placement, mapColourEffect(func, q1), mapColourEffect(func, q2), mapColourEffect(func, q3),
          mapColourEffect(func, q4), mapColourEffect(func, q5), mapColourEffect(func, q6), mapColourEffect(func, q7),
          mapColourEffect(func, q8))
    }
  }
  def toSepia(color : (Int,Int,Int)) : Color = {
  //  println("i recied this color" + color.getRed + color.getGreen + color.getBlue)
    val newRed = math min(color._1 * .40 + color._2 * .77 + color._3 * .20, 255)
    val newGreen = math min(color._1 * .35 + color._2 * .69 + color._3 * .17, 255)
    val newBlue = math min(color._1 * .27 + color._2 * .53 + color._3 * .13, 255)
    //println(newRed + newGreen + newBlue)
    Color.rgb(newRed.asInstanceOf[Int],newGreen.asInstanceOf[Int],newBlue.asInstanceOf[Int])
  }

  def removeGreen(color : (Int,Int,Int)) : Color = {
    println(color + "in removeGreen")
    Color.rgb(color._1, math min(color._2,0),color._3)
  }

  def get3DObjects(worldroot : Group) : List[Shape3D] = {
    def get3DObjectsAux(nodeList : List[Node]) : List[Shape3D] = {
      nodeList match {
        case List() => Nil
        case head::tail =>
          if (head.isInstanceOf[Shape3D] && !(head.isInstanceOf[Line])) {
            head.asInstanceOf[Shape3D] :: get3DObjectsAux(tail)
          }
          else
            get3DObjectsAux(tail)
      }
    }
    val nodeChilderList = worldroot.getChildren.toList
    get3DObjectsAux(nodeChilderList)
  }

  def getObjectColor(obj : Shape3D) : String = {
    "(" + (obj.getMaterial.asInstanceOf[PhongMaterial].getDiffuseColor.getRed * 255).toInt + "," +
      (obj.getMaterial.asInstanceOf[PhongMaterial].getDiffuseColor.getGreen * 255).toInt + "," +
      (obj.getMaterial.asInstanceOf[PhongMaterial].getDiffuseColor.getBlue * 255).toInt + ")"
  }

  def saveToFile(worldroot : Group) : Unit = {
    System.out.println("Stage is closing, saving file in resultingConfs.txt")
    val printWriter = new PrintWriter(new File("resultingConfs.txt"))
    val objectList = get3DObjects(worldroot)
    objectList.filter(x => x.getDrawMode == DrawMode.FILL).map(x => {
      val rgb = getObjectColor(x)
      printWriter.write(s"${if (x.isInstanceOf[Box]) "Box" else "Cylinder"} ${rgb} ${(x.getTranslateX).toInt} ${x.getTranslateY.toInt} ${x.getTranslateZ.toInt} ${x.getScaleX} ${x.getScaleY} ${x.getScaleZ} \n")
  })
    printWriter.close()
}
}
