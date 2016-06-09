# verbosefs
Java 7 FileSystem wrapper for debugging **PROBLEMS**. It logs all major destructive filesystem operations (not individual writes, seriously major things such as overwriting a file, deleting a file, renaming a file) to `stdout` or to a file along with timestamp, thread name, and stacktrace. 

Log messages look like this:
```
FS 0 [2016-06-08T05:28:10.829Z; elasticsearch[Rock Python][clusterService#updateTask][T#1]]: newFileChannel[WRITE]: /home/rmuir/Downloads/elasticsearch-2.3.1/data/elasticsearch/nodes/0/_state/global-12.st.tmp
	at rmuir.VerboseFS.newFileChannel(VerboseFS.java:209)
	at java.nio.channels.FileChannel.open(FileChannel.java:287)
	at java.nio.channels.FileChannel.open(FileChannel.java:335)
	at org.apache.lucene.util.IOUtils.fsync(IOUtils.java:391)
	at org.elasticsearch.gateway.MetaDataStateFormat.write(MetaDataStateFormat.java:131)
	at org.elasticsearch.gateway.MetaStateService.writeGlobalState(MetaStateService.java:149)
	at org.elasticsearch.gateway.GatewayMetaState.clusterChanged(GatewayMetaState.java:148)
	at org.elasticsearch.gateway.Gateway.clusterChanged(Gateway.java:185)
	at org.elasticsearch.cluster.service.InternalClusterService.runTasksForExecutor(InternalClusterService.java:610)
	at org.elasticsearch.cluster.service.InternalClusterService$UpdateTask.run(InternalClusterService.java:772)
	at org.elasticsearch.common.util.concurrent.PrioritizedEsThreadPoolExecutor$TieBreakingPrioritizedRunnable.runAndClean(PrioritizedEsThreadPoolExecutor.java:231)
	at org.elasticsearch.common.util.concurrent.PrioritizedEsThreadPoolExecutor$TieBreakingPrioritizedRunnable.run(PrioritizedEsThreadPoolExecutor.java:194)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617)
	at java.lang.Thread.run(Thread.java:745)
```

# Enable VerboseFS
1. Download the jar: https://github.com/rmuir/verbosefs/releases/download/1.1/verbosefs.jar
2. Put this jar in the application classpath
3. You must pass this argument to the **JVM**: `-Djava.nio.file.spi.DefaultFileSystemProvider=rmuir.VerboseFS`

# Change log file
To log to a file instead, also pass this argument to the **JVM** `-Dverbosefs.logfile=/somewhere/file.txt`

# Elasticsearch users
1. Download the jar: https://github.com/rmuir/verbosefs/releases/download/1.1/verbosefs.jar
2. Put jar in elasticsearch `lib` folder (alongside elasticsearch-2.3.1.jar or whatever)
3. Start elasticsearch, specifying options in `ES_JAVA_OPTS` environment variable.

Example:
```
ES_JAVA_OPTS="-Djava.nio.file.spi.DefaultFileSystemProvider=rmuir.VerboseFS" bin/elasticsearch
```

Example 2:
```
ES_JAVA_OPTS="-Djava.nio.file.spi.DefaultFileSystemProvider=rmuir.VerboseFS -Dverbosefs.logfile=/somewhere/file.txt" bin/elasticsearch
```
