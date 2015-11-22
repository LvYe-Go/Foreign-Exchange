package Scala
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._

import org.apache.spark.mllib.*
import org.apache.spark.rdd.RDD
import com.datastax.spark.connector._
import java.util.Calendar
import org.apache.spark.mllib.tree.RandomForest
import org.apache.spark.mllib.tree.model.RandomForestModel
import org.apache.spark.mllib.util.MLUtils


/*  Author: Jing Yu 
 *  Train a RandomForest model.
 ** Reference: https://spark.apache.org/docs/1.5.1/mllib-ensembles.html
 */
object RandomForest {
  // global configure
  val TableName = "FExchange"
  val trainCol = "train"
  val testCol = "test"
  val host = "127.0.0.1"
  val FEATURE_NUM = 4
  val LABEL = "label"
  val featureList = Array[String]("avg_bid", "bid_diff", "avg_spread", "aid_direction") // Set the featureList  will get 
  
  def main(args: Array[String]) {
     // 1. Init 
      val configure = new SparkConf().setAppName("Simple Applciation");
      // Fetch data from cassandra 
      configure.set("spark.cassandra.connection.host", host)
      val sc = new SparkContext(configure)

      val trainData = sc.cassandraTable(TableName, trainCol) 
      val testData = sc.cassandraTable(TableName, testCol) 
      
      // 2. Iterate traindata to RDD format 
      val trainRddData = RDD[LabeledPoint] = trainData.map{row => ( 
              val label = row.getInt(LABEL).toDouble
              val labelVal:Array[Double] = new Array[Double](FEATURE_NUM)
              for ( cur <- 0 to FEATURE_NUM){
                  labelVal(cur)= row.get(featureList(i)).toDouble
              }
              LabeledPoint(label, Vectors.sparse(FEATURE_NUM, new Array[Int](0,1,2,3,4), labelVal))
          )}
      
       // 3. Iterate testdata to RDD format 
      val testRddData = RDD[LabeledPoint] = trainData.map{row => ( 
              val label = row.getInt(LABEL).toDouble
              val labelVal:Array[Double] = new Array[Double](FEATURE_NUM)
              for ( cur <- 0 to FEATURE_NUM){
                  labelVal(cur)= row.get(featureList(i)).toDouble
              }
              LabeledPoint(label, Vectors.sparse(FEATURE_NUM, new Array[Int](0,1,2,3,4), labelVal))
          )}
      
      // 4. Train the model use RandomForest APIs
      // Reference from: https://spark.apache.org/docs/1.5.1/mllib-ensembles.html
      
      val numClasses = 2 // The 
      val categoricalFeaturesInfo = Map[Int, Int]()
      val numTrees =  10  //  Random Forest Trees number 
      val featureSubsetStrategy = "auto" // Let the algorithm choose.
      val impurity = "gini"
      val maxDepth = 4
      val maxBins = 32
      
      val model = RandomForest.trainClassifier(trainingData, numClasses, categoricalFeaturesInfo,
        numTrees, featureSubsetStrategy, impurity, maxDepth, maxBins)
      
      // Evaluate model on test instances and compute test error
      val labelAndPreds = testData.map { point =>
        val prediction = model.predict(point.features)
        (point.label, prediction)
      }
      val testErr = labelAndPreds.filter(r => r._1 != r._2).count.toDouble / testData.count()
      println("Test Error = " + testErr)
      var testAccracy = 1 - testErr
      println("Test Accuracy = " + testAccracy)
      
      // Output the acuracy corresponding to each times into Cassandra 
      println("Learned classification forest model:\n" + model.toDebugString)
      
      var res = sc.parallelize(Seq(numTrees, testAccracy))
      res.saveToCassandra(TableName, "Result", SomeColumns("TreeNum", "Accuracy"))
        
      // Save and load model
      model.save(sc, "myModelPath")
      val sameModel = RandomForestModel.load(sc, "myModelPath")
    }
}