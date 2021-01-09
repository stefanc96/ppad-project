# ppad-project

Task description:
Implement a thread pool executor, similar to the one present in Java platform: java.util.concurrent.ThreadPoolExecutor
WITHOUT using any class from java.util.concurrent package.
The component will accept for execution tasks of type java.lang.Runnable.
The input parameters are:
- int corePoolSize
- int maximumPoolSize
- int keepAliveTime (resolution = 1 second)
- int queueSize
