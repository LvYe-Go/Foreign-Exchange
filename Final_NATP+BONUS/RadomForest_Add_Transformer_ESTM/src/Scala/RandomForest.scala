package Scala
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._

import org.apache.spark.mllib.*
import org.apache.spark.rdd.RDD
import com.datastax.spark.connector._
import java.util.Calendar
import org.apache.spark.mllib.tree.RandomForest
import org.apache.spark.mllib.tree.model.RandomForestModel
import org.apache.spark.ml.feature.PolynomialExpansion
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.util.MLUtils

import org.apache.spark.ml.*;


/*  Author: Jing Yu 
 *  Train a RandomForest model. Add transformer and estimator 
 ** Reference: https://spark.apache.org/docs/1.5.1/mllib-ensembles.html
 *             http://spark.apache.org/docs/latest/ml-guide.html#transformers
 *             https://spark.apache.org/docs/1.5.1/ml-features.html#vectorassembler 
 */
object RandomForest {
  // global configure
  val TableName = "FExchange"
  val trainCol = "train"
  val testCol = "test"
  val host = "127.0.0.1"
  val FEATURE_NUM = 4
  val COM_NUM = 3
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
      
      
      // 4. Estimator starts here, reference: http://spark.apache.org/docs/latest/ml-guide.html#pipeline-components
      
      // Create a LogisticRegression instance.  
      val lr = new LogisticRegression()
      
      // We may set parameters using setter methods.
      lr.setMaxIter(10).setRegParam(0.01)
      
      // Learn a LogisticRegression model.  This uses the parameters stored in lr.
      val model1 = lr.fit(trainRddData)

      // We may alternatively specify parameters using a ParamMap
      val paramMap = ParamMap(lr.maxIter -> 20)
                    .put(lr.maxIter, 30) // Specify 1 Param.  This overwrites the original maxIter.
                    .put(lr.regParam -> 0.1, lr.threshold -> 0.55) // Specify multiple Params.
      
      // One can also combine ParamMaps.
      val paramMap2 = ParamMap(
            lr.probabilityCol -> "myProbability"
      ) // Change output column name
      val paramMapCombined = paramMap ++ paramMap2
      
      // A PCA class trains a model to project vectors to a low-dimensional space 
      // Reference: https://spark.apache.org/docs/1.5.1/ml-features.html#pca
      val trainDf = sqlContext.createDataFrame(trainRddData.map(Tuple1.apply))
                              .toDF("features")
      val testDf = sqlContext.createDataFrame(testRddData.map(Tuple1.apply))
                             .toDF("features")
      
      // Feature transformer
      val pcaTrain = new PCA().setInputCol("features")
                              .setOutputCol("pcaFeatures")
                              .setK(COM_NUM) // set components number 
                              .fit(trainDf) // Estimator
                              
      val pcaTest = new PCA().setInputCol("features")
                              .setOutputCol("pcaFeatures")
                              .setK(COM_NUM) // set components number 
                              .fit(testDf)
      // Get transformed data
      val train_pcaDF = pcaTrain.transform(trainDf)
      val train_result = train_pcaDF.select("pcaFeatures")
      val test_pcaDF = pcaTest.transform(testDf)
      val test_result = test_pcaDF.select("pcaFeatures")
        
       //val polyDF = polynomialExpansion.transform(df) // Not good 

      // Now learn a new model using the paramMapCombined parameters.
      val model2 = lr.fit(train_pcaDF, paramMapCombined)

      // Calculation
      val match_num = 0
      val total_num = 0
      // Make predictions on test data using the Transformer.transform() method.
      // LogisticRegression.transform will only use the 'features' column.
      // Note that model2.transform() outputs a 'myProbability' column instead of the usual
      // 'probability' column since we renamed the lr.probabilityCol parameter previously.
      model2.transform(test)
          .select("features", LABEL, "myProbability", "prediction").collect()
          .foreach { case Row(features: Vector, label: Double, prob: Vector, prediction: Double) =>
            println(s"($features, $label) -> prob=$prob, prediction=$prediction")
               total_num = total_num + 1
               if(label == prediction) {
                   match_num = match_num + 1
               }
          }
      
      println("PCA Accuracy is:" + match_num.toDouble/total_num.toDouble)
      // Estimator and transformer finished 

      // 5. Train the model use RandomForest APIs
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