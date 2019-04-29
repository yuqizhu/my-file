package com.yungoal.cep


import java.net.{InetAddress, URL}
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

import com.microsoft.azure.storage.blob._
import com.microsoft.azure.storage.blob.models.BlockBlobUploadResponse
import com.typesafe.scalalogging.Logger
import io.reactivex.Flowable
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.slf4j.LoggerFactory


object MessageToBlob {
  val logger = Logger(LoggerFactory.getLogger("DirectKafkaWordCount"))

  def formatBlobName(record: ConsumerRecord[String, String]): String = {
    val topic = record.topic()
    val dt = new java.util.Date(record.timestamp())
    val dtf = new SimpleDateFormat("yyyy/M/d")
    val dt_str = dtf.format(dt)
    val hostname = InetAddress.getLocalHost.getHostName
    val name = record.timestamp()
    val blobName = s"$topic/$dt_str/$name"
    blobName
  }

  def main(args: Array[String]): Unit = {

    // Configurations for kafka consumer
    val kafkaBrokers = sys.env.get("KAFKA_BROKERS")
    val kafkaGroupId = sys.env.get("KAFKA_GROUP_ID")

    val kafkaTopic = sys.env.get("KAFKA_TOPIC")

    val storageKey = sys.env.get("STORAGE_KEY")
    val storageName = sys.env.get("STORAGE_NAME")

    // Verify that all settings are set
    require(kafkaBrokers.isDefined, "KAFKA_BROKERS has not been set")
    // require(kafkaGroupId.isDefined, "KAFKA_GROUP_ID has not been set")
    require(kafkaTopic.isDefined, "KAFKA_TOPIC has not been set")

    require(storageKey.isDefined, "STORAGE_KEY has not been set")
    require(storageName.isDefined, "STORAGE_NAME has not been set")


    // Create Spark Session
    val spark = SparkSession
      .builder()
      .appName("MessageToBlob")
      // .master("local[*]")
      .getOrCreate()
      
      // Create Streaming Context and Kafka Direct Stream with provided settings and 10 seconds batches
      val ssc = new StreamingContext(spark.sparkContext, Seconds(10))

    val kafkaParams = scala.collection.immutable.Map[String, Object](
      "bootstrap.servers" -> kafkaBrokers.get,
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> kafkaGroupId.getOrElse("msg_blob"),
      "auto.offset.reset" -> "earliest",
      "enable.auto.commit" -> (true: java.lang.Boolean)
    )

    val topics = Array(kafkaTopic.get)

    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )

        
            /*
                    Create a URL that references a to-be-created container in your Azure Storage account. This returns a
                    ContainerURL object that wraps the container's URL and a request pipeline (inherited from serviceURL).
                    Note that container names require lowercase.
                     *//*
                    Create a URL that references a to-be-created container in your Azure Storage account. This returns a
                    ContainerURL object that wraps the container's URL and a request pipeline (inherited from serviceURL).
                    Note that container names require lowercase.
                     */
            // Create the container on the service (with no metadata and no public access)
            stream.foreachRDD(rdd => {
              logger.info("Receive RDD")
              rdd.foreachPartition(partitionofRecoreds => {
                logger.info("Receive Partition")
                val credential = new SharedKeyCredentials(storageName.get, storageKey.get)
                /*
                Create a request pipeline that is used to process HTTP(S) requests and responses. It requires your accont
                credentials. In more advanced scenarios, you can configure telemetry, retry policies, logging, and other
                options. Also you can configure multiple pipelines for different scenarios.
                 */
                val pipeline = StorageURL.createPipeline(credential, new PipelineOptions())
                import java.util.Locale
                /*
                        From the Azure portal, get your Storage account blob service URL endpoint.
                        The URL typically looks like this:
                         *//*
                        From the Azure portal, get your Storage account blob service URL endpoint.
                        The URL typically looks like this:
                         */
                val u = new URL(String.format(Locale.ROOT, "https://%s.blob.core.windows.net", storageName.get))
            
                // Create a ServiceURL object that wraps the service URL and a request pipeline.
                val serviceURL = new ServiceURL(u, pipeline)
                val containerURL = serviceURL.createContainerURL("trusted2").create().blockingGet()
                
                //containerURL.create().blockingGet()
                        
                // partitionofRecoreds.foreach { record => {
                //     logger.info("Receive Record")
                //     try {
                //         val blobName = formatBlobName(record)
                //         val blobURL = containerURL.createBlockBlobURL(blobName)
                //         logger.info("Receive message " + blobName)
                //         val data_bytes = record.value().getBytes()
                //         blobURL.upload(Flowable.just(ByteBuffer.wrap(data_bytes)), record.value().length).subscribe()
                //     } catch {
                //         case unknown: Exception => println("Got this unknown exception: " + unknown)
                //     }         
                //   } 
                // }
            })
    })


    // Start Stream
    ssc.start()
    logger.info("App start")
    ssc.awaitTermination()

    logger.info("App Termination")
  }


  def uuid = java.util.UUID.randomUUID.toString


  def saveToAzure(data_str: String, blobName: String = uuid, containerURL: ContainerURL): Unit = {

    /*
    Create a URL that references a to-be-created blob in your Azure Storage account's container.
    This returns a BlockBlobURL object that wraps the blob's URl and a request pipeline
    (inherited from containerURL). Note that blob names can be mixed case.
     */


  }

}
