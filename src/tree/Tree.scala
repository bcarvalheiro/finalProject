package tree

import javafx.geometry.Bounds
import javafx.scene.{Group, Node}
import javafx.scene.paint.{Color, PhongMaterial}
import javafx.scene.shape.{Box, Cylinder, DrawMode, Shape3D}
import tree.Tree.{Placement, getSectionList, listOfObjInSection}
import utils.ConfigLoad

import scala.annotation.tailrec
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


    def createTreeFromRoot(placement: Placement, listObj: List[Node], minSize: Int, maxTreeDepth: Int): Octree[Placement] = {
      val sectionList = getSectionList(placement, listObj)
      val octList = typeOfTreeNode(sectionList, placement, listObj, minSize, maxTreeDepth, 0)
      if(maxTreeDepth == 0){
        val ocLeaf = OcLeaf(placement, (placement,listObj))
        ocLeaf
      }
      else{
        val oct: Octree[Placement] = OcNode[Placement](placement, octList(0), octList(1), octList(2), octList(3), octList(4), octList(5), octList(6), octList(7))
        println(oct)
        getOcTreeLeafsSection(List(oct))
        oct
      }
    }


  def typeOfTreeNode(sectionList: List[Section], parentPlacement: Placement, listObj: List[Node], minSize: Int, maxTreeDepth: Int, actualTreeDepth: Int): List[Octree[Placement]] = {
    sectionList match {
      case List() => Nil
      case section :: secList => {
        // if section as no object in List is OcEmpty
        if (section._2.isEmpty) {
          val v = typeOfTreeNode(secList, parentPlacement, listObj, minSize, maxTreeDepth,0)
          (v.appended(OcEmpty))
          // otherwise
        } else {
          // if children can fit ALL objects in parent is OcNode
          println("Tamanho_Parent: " + section._2.size)
          println("Tamanho_filhos: " + objectsInChildren(getSectionList(section._1, listObj)).size)
          println(objectsInChildren(getSectionList(section._1, listObj)))
          val actualTreeDepthAux = actualTreeDepth + 1
          if (section._2.size == objectsInChildren(getSectionList(section._1, listObj)).size &&
            (maxTreeDepth > actualTreeDepthAux || maxTreeDepth == -1) &&
            (section._1._2 / 2 >= minSize || minSize == -1)) {
            val sectionList = getSectionList(section._1, listObj)
            val octList = typeOfTreeNode(sectionList, section._1, listObj, minSize,maxTreeDepth,actualTreeDepthAux)
            val ocNode = new OcNode[Placement](section._1, octList(0), octList(1), octList(2), octList(3), octList(4), octList(5), octList(6), octList(7))
            val v = typeOfTreeNode(secList, parentPlacement, listObj, minSize, maxTreeDepth, actualTreeDepthAux)
            println("OcNode: " + ocNode)
            v.appended(ocNode)
            // if children cannot contain objects is OcLeaf
          } else {
            val v = typeOfTreeNode(secList, parentPlacement, listObj,minSize,maxTreeDepth,0)
            val ocLeaf = OcLeaf(parentPlacement, section)
            println("OcLeaf: " + ocLeaf)
            v.appended(ocLeaf)
          }
        }
      }
    }
  }

  def getObjList (octree: Octree[Placement]) : List[Node] = {
      octree match {
        case OcEmpty => Nil
        case OcLeaf ((value : Placement, (placement : Placement, objList : List[Node]))) =>
          objList
        case OcNode (placement : Placement, q1,q2,q3,q4,q5,q6,q7,q8) =>
          getObjList(q1) ++ getObjList(q2) ++ getObjList(q3) ++ getObjList(q4) ++ getObjList(q5) ++ getObjList(q6) ++ getObjList(q7) ++ getObjList(q8)
      }
  }

  def objectsInChildren(sectionChildrenList: List[(Placement,List[Node])]):List[Node]={
    sectionChildrenList match {
      case List() => Nil
      case sec :: secList =>
        val v = objectsInChildren(secList)
        //println(sec)
         v ::: sec._2
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

    //creation of the spacial partition. Exemple: parente at (0,0,0) size 16 ((0,0,0),16)
    //8 box's with position (the combinatioon for each x,y,z thats calculated) size 8
    // ((newx,newy,newz), 8)
    val qPlacement: List[Placement] = List(((translXSup, translYSup, translZSup), resize), ((translXInf, translYSup, translZSup), resize), ((translXSup, translYInf, translZSup), resize),
      ((translXSup, translYSup, translZInf), resize), ((translXInf, translYInf, translZSup), resize), ((translXSup, translYInf, translZInf), resize),
      ((translXInf, translYSup, translZInf), resize), ((translXInf, translYInf, translZInf), resize))
//    println (qPlacement)

//    println("** root **")
//    println(createBox(placement).getBoundsInParent)
//    println("** root **")
//    println("")

    //println(createSection(qPlacement, listObj))
    //Now we got the placement for the boxes of the spacial partition, so now we can
    //create move on to create that Section
    createSection(qPlacement, listObj)
  }

  //We came from getSectionList() with the placements of the sections and graphical models we wanted to create
  def createSection(placementList: List[Placement], listObj: List[Node]): List[Section] = {
    placementList match {
      case List() => Nil
      case place :: placeList => {
        //listOfObjInSection() will create the box with the spacial partition
        //and will include in them the objects that fits them well (not interseting any line)
        val section: Section = (place, listOfObjInSection(place, listObj: List[Node]))
        val v = createSection(placeList, listObj)
        v.appended(section)
      }
    }
    //now we have a list of the Sections we want to to insert in our OcTree
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
          v
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
    box
  }
  //Porque é que isto recebe uma lista de OcTree se o createFromRoot devolve só uma OcTree?
  def getOcTreeLeafsSection(octreeList: List[Octree[Placement]]): List[(Placement,List[Node])] = {
    //Podemos só iterar através da OcTree e não de uma lista de OcTree
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
}
