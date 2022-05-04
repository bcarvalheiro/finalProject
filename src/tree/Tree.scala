package tree

import javafx.geometry.Bounds
import javafx.scene.{Group, Node}
import javafx.scene.paint.{Color, PhongMaterial}
import javafx.scene.shape.{Box, Cylinder, DrawMode, Shape3D}
import tree.Tree.{getSectionList, listOfObjInSection}
import utils.configLoad

import scala.io.Source

object Tree {

  //Auxiliary types
  type Point = (Double, Double, Double)
  type Size = Double
  type Placement = (Point, Size) //1st point: origin, 2nd point: size

  //Materials to be applied to the 3D objects
  val redMaterial = new PhongMaterial()
  redMaterial.setDiffuseColor(Color.rgb(150, 0, 0))

  val greenMaterial = new PhongMaterial()
  greenMaterial.setDiffuseColor(Color.rgb(0, 255, 0))

  val blueMaterial = new PhongMaterial()
  blueMaterial.setDiffuseColor(Color.rgb(0, 0, 150))

  //Shape3D is an abstract class that extends javafx.scene.Node
  //Box and Cylinder are subclasses of Shape3D
  type Section = (Placement, List[Node]) //example: ( ((0.0,0.0,0.0), 2.0), List(new Cylinder(0.5, 1, 10)))


  def createTreeFromRoot(placement: Placement, listObj: List[Node]): Octree[Placement] = {
    val sectionList = getSectionList(placement, listObj)
    val octList = typeOfTreeNode(sectionList, placement, listObj)
    val oct: Octree[Placement] = OcNode[Placement](placement, octList(0), octList(1), octList(2), octList(3), octList(4), octList(5), octList(6), octList(7))
    //println(oct)
    getOcTreeLeafsSection(List(oct))
    oct
  }

    def typeOfTreeNode(sectionList: List[Section], parentPlacement: Placement, listObj: List[Node]): List[Octree[Placement]] = {
    sectionList match {
      case List() => Nil
      case section :: secList => {
        // if section as no object in List is OcEmpty
        if (section._2.isEmpty) {
          val v = typeOfTreeNode(secList, parentPlacement, listObj)
          (v.appended(OcEmpty))
          // otherwise
        } else {
          // if children can fit ALL objects in parent is OcNode
        //  if (areSectionChildrenBigEnough(getSectionList(section._1, listObj),listOfObjInSection(section._1,listObj))) {
          //println("Tamanho_Parent: " + section._2.size)
         // println("Tamanho_filhos: " + objectsInChildren(getSectionList(section._1, listObj)).size)
          //println(objectsInChildren(getSectionList(section._1, listObj)))
          if (section._2.size == objectsInChildren(getSectionList(section._1, listObj)).size) {
            val sectionList = getSectionList(section._1, listObj)
            val octList = typeOfTreeNode(sectionList, section._1, listObj)
            val ocNode = new OcNode[Placement](section._1, octList(0), octList(1), octList(2), octList(3), octList(4), octList(5), octList(6), octList(7))
            val v = typeOfTreeNode(secList, parentPlacement, listObj)
            //println("OcNode: " + ocNode)
            v.appended(ocNode)
            // if children cannot contain objects is OcLeaf
          } else {
            val v = (typeOfTreeNode(secList, parentPlacement, listObj))
            val ocLeaf = new OcLeaf(parentPlacement, section)
            //println("OcLeaf: " + ocLeaf)
            v.appended(ocLeaf)
          }
        }
      }
    }
  }

//  def areSectionChildrenBigEnough(sectionChildrenList: List[Section],parentSectionObjList: List[Section]): Boolean = {
//
//    sectionChildrenList match {
//      case List() => false
//      case sec :: secList => {
//        if (sec._2.isEmpty) areSectionChildrenBigEnough(secList,parentSectionObjList)
//        else true
//      }
//    }
//  }

  private def objectsInChildren(sectionChildrenList: List[Section]):List[Node]={
    sectionChildrenList match {
      case List() => Nil
      case sec :: secList => {
        val v = objectsInChildren(secList)
        //println(sec)
         v ::: sec._2
      }
    }
  }



  private def getSectionList(placement: Placement, listObj: List[Node]): List[Section] = {
    val x = placement._1._1
    val y = placement._1._2
    val z = placement._1._3
    val resize = placement._2 / 2
    val translXInf = (placement._1._1 - resize / 2)
    val translXSup = (placement._1._1 + resize / 2)
    val translYInf = (placement._1._2 - resize / 2)
    val translYSup = (placement._1._2 + resize / 2)
    val translZInf = (placement._1._3 - resize / 2)
    val translZSup = (placement._1._3 + resize / 2)

    val qPlacement: List[Placement] = List(((translXSup, translYSup, translZSup), resize), ((translXInf, translYSup, translZSup), resize), ((translXSup, translYInf, translZSup), resize),
      ((translXSup, translYSup, translZInf), resize), ((translXInf, translYInf, translZSup), resize), ((translXSup, translYInf, translZInf), resize),
      ((translXInf, translYSup, translZInf), resize), ((translXInf, translYInf, translZInf), resize))
//    println (qPlacement)

//    println("** root **")
//    println(createBox(placement).getBoundsInParent)
//    println("** root **")
//    println("")

    //println(createSection(qPlacement, listObj))
    createSection(qPlacement, listObj)
  }

  def createSection(placementList: List[Placement], listObj: List[Node]): List[Section] = {
    placementList match {
      case List() => Nil
      case place :: placeList => {
        val section: Section = (place, listOfObjInSection(place, listObj: List[Node]))
        val v = createSection(placeList, listObj)
        v.appended(section)
      }
    }
  }


  def listOfObjInSection(placement: Placement, listObj: List[Node]): List[Node] = {
    val node = createBox(placement)
    listObj match {
      case List() => Nil
      case obj :: objList => {

//        println(node.getBoundsInParent)
//        println(obj.getBoundsInParent)
//        println(obj)

        if (node.getBoundsInParent.contains(obj.getBoundsInParent)) {

//          println(node.getBoundsInParent)
//          println(obj.getBoundsInParent)
//          println(obj)
//          println("true")
          val v = listOfObjInSection(placement, objList)
          (v.prepended(obj))
        } else {

        //  println("false")
          val v = listOfObjInSection(placement, objList)
          (v)
        }
      }
    }
  }

  private def createBox(placement: Placement): Box = {
    val dim = placement._2
    val box = new Box(dim, dim, dim)
    box.setTranslateX(placement._1._1)
    box.setTranslateY(placement._1._2)
    box.setTranslateZ(placement._1._3)
    (box)
  }

  def getOcTreeLeafsSection(octreeList: List[Octree[Placement]]): List[Section] = {
    octreeList match {
      case List() => Nil
      case node :: nodeList =>
        node match {
          case OcEmpty =>
            getOcTreeLeafsSection(nodeList)
          case OcLeaf(ocLeaf:(Placement,Section)) =>
            val v = getOcTreeLeafsSection(nodeList)
            v.appended(ocLeaf._2)
          case OcNode(placement: Placement, q1, q2, q3, q4, q5, q6, q7, q8) =>
            getOcTreeLeafsSection(nodeList ++ List(q1, q2, q3, q4, q5, q6, q7, q8))
        }
    }
  }

    def listWiredBox(sectionList: List[Section]): List[Box] = {
      sectionList match {
        case List() => Nil
        case head :: tail =>
          val dim = head._1._2
          val box = new Box(dim, dim, dim)
          box.setTranslateX(head._1._1._1)
          box.setTranslateY(head._1._1._2)
          box.setTranslateZ(head._1._1._3)
          box.setMaterial(redMaterial)
          box.setDrawMode(DrawMode.LINE)
          val v = listWiredBox(tail)
          (v.prepended(box))
      }
    }

  def getNodePlacement(node: Node): Placement =
    ((node.getTranslateX, node.getTranslateY, node.getTranslateZ), node.asInstanceOf[Box].getHeight)

  def checkInSight(listaWiredBoxs: List[Box], camVolume: Cylinder, worldRoot: Group): Unit = listaWiredBoxs match {
    case List() => Nil
    case head::tail =>
      if (camVolume.asInstanceOf[Shape3D].getBoundsInParent.intersects(head.getBoundsInParent)) head.setMaterial(blueMaterial)
      else head.setMaterial(redMaterial)
      checkInSight(tail,camVolume,worldRoot)
  }

  def createOcTree(confFile : String) : Octree[Placement] = {
    if (confFile.isEmpty) {
      println("There's no configuration file")
      System.exit(1)
    }
    val textLines = Source.fromFile(confFile).getLines().toList
    val lista3DObjects = configLoad.create3DObjects(textLines)
    val wiredBoxes = listWiredBox(getOcTreeLeafsSection(List(createTreeFromRoot(((8.0,8.0,8.0),16),lista3DObjects))))
    createTreeFromRoot(((8.0,8.0,8.0),16),lista3DObjects)
  }
}
