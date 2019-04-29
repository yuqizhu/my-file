package com.yungoal.cep


import java.net.{InetAddress, URL}
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit
import java.util.Locale
import com.microsoft.azure.storage.blob._
import com.microsoft.azure.storage.blob.models.BlockBlobUploadResponse
import com.typesafe.scalalogging.Logger
import io.reactivex.Flowable
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.slf4j.LoggerFactory


object SparkToBlob {
  val logger = Logger(LoggerFactory.getLogger("DirectKafkaWordCount"))
  val storageKey = sys.env.get("STORAGE_KEY")
  val storageName = sys.env.get("STORAGE_NAME")

  def main(args: Array[String]): Unit = {
    val accountName = System.getenv("STORAGE_NAME");
    val accountKey = System.getenv("STORAGE_KEY");

    // Use your Storage account's name and key to create a credential object; this is used to access your account.
    val credential = new SharedKeyCredentials(accountName, accountKey);

    /*
    Create a request pipeline that is used to process HTTP(S) requests and responses. It requires your account
    credentials. In more advanced scenarios, you can configure telemetry, retry policies, logging, and other
    options. Also you can configure multiple pipelines for different scenarios.
     */
    val pipeline = StorageURL.createPipeline(credential, new PipelineOptions());

    /*
    From the Azure portal, get your Storage account blob service URL endpoint.
    The URL typically looks like this:
     */
    val u = new URL(String.format(Locale.ROOT, "https://%s.blob.core.windows.net", accountName));

    // Create a ServiceURL objet that wraps the service URL and a request pipeline.
    val serviceURL = new ServiceURL(u, pipeline);

    // Now you can use the ServiceURL to perform various container and blob operations.

    // This example shows several common operations just to get you started.

    /*
    Create a URL that references a to-be-created container in your Azure Storage account. This returns a
    ContainerURL object that wraps the container's URL and a request pipeline (inherited from serviceURL).
    Note that container names require lowercase.
     */
//        ContainerURL containerURL = serviceURL.createContainerURL("myjavacontainerbasic" + System.currentTimeMillis());
    val containerURL = serviceURL.createContainerURL("trusted2");

    /*
    Create a URL that references a to-be-created blob in your Azure Storage account's container.
    This returns a BlockBlobURL object that wraps the blob's URl and a request pipeline
    (inherited from containerURL). Note that blob names can be mixed case.
     */
    val blobURL = containerURL.createBlockBlobURL("HelloWorld.txt");

    val data = "Hello world!";
        logger.info("Started saving")
    blobURL.upload(Flowable.just(ByteBuffer.wrap(data.getBytes())), data.length(),
    null, null, null, null).blockingGet();
    // val record = "abc"
    // val credential = new SharedKeyCredentials(storageName.get, storageKey.get)
    // /*
    // Create a request pipeline that is used to process HTTP(S) requests and responses. It requires your accont
    // credentials. In more advanced scenarios, you can configure telemetry, retry policies, logging, and other
    // options. Also you can configure multiple pipelines for different scenarios.
    //     */
    // val pipeline = StorageURL.createPipeline(credential, new PipelineOptions())
    // import java.util.Locale
    // /*
    //         From the Azure portal, get your Storage account blob service URL endpoint.
    //         The URL typically looks like this:
    //             *//*
    //         From the Azure portal, get your Storage account blob service URL endpoint.
    //         The URL typically looks like this:
    //             */
    // val u = new URL(String.format(Locale.ROOT, "https://%s.blob.core.windows.net", storageName.get))

    // // Create a ServiceURL object that wraps the service URL and a request pipeline.
    // val serviceURL = new ServiceURL(u, pipeline)
    // val containerURL = serviceURL.createContainerURL("trusted2")
    
    // try {
    //     val blobURL = containerURL.createBlockBlobURL("Test2")
    //     logger.info("Started saving")
    //     blobURL.upload(Flowable.just(ByteBuffer.wrap(record.getBytes())), record.length).blockingGet()
    //     logger.info("Saved message ")
    // } catch {
    //     case unknown: Exception => println("Got this unknown exception: " + unknown)
    // }         
  } 
}

