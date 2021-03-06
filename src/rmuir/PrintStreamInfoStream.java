/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rmuir;


import java.io.IOException;
import java.io.PrintStream;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * InfoStream implementation over a {@link PrintStream}
 * such as <code>System.out</code>.
 * 
 * @lucene.internal
 */
public class PrintStreamInfoStream extends InfoStream {
  // Used for printing messages
  private static final AtomicInteger MESSAGE_ID = new AtomicInteger();
  protected final int messageID;

  protected final PrintStream stream;
  
  public PrintStreamInfoStream(PrintStream stream) {
    this(stream, MESSAGE_ID.getAndIncrement());
  }
  
  public PrintStreamInfoStream(PrintStream stream, int messageID) {
    this.stream = stream;
    this.messageID = messageID;
  }
  
  @Override
  public void message(String component, String message) {
    // build message and call print once, so stacks are not interleaved from multiple threads.
    StringBuilder sb = new StringBuilder();
    sb.append(component + " " + messageID + " [" + getTimestamp() + "; " + Thread.currentThread().getName() + "]: " + message);
    sb.append('\n');
    StackTraceElement stack[] = Thread.currentThread().getStackTrace();
    for (StackTraceElement frame : stack) {
      if (filterFrame(frame)) {
        continue;
      }
      sb.append("\tat ");
      sb.append(frame);
      sb.append('\n');
    }
    stream.print(sb.toString());
  }
  
  private boolean filterFrame(StackTraceElement frame) {
    if ("java.lang.Thread".equals(frame.getClassName()) && "getStackTrace".equals(frame.getMethodName())) {
      return true;
    }
    if ("rmuir.PrintStreamInfoStream".equals(frame.getClassName())) {
      return true;
    }
    if ("rmuir.VerboseFS".equals(frame.getClassName()) && "sop".equals(frame.getMethodName())) {
      return true;
    }
    return false;
  }

  @Override
  public boolean isEnabled(String component) {
    return true;
  }

  @Override
  public void close() throws IOException {
    if (!isSystemStream()) {
      stream.close();
    }
  }
  
  public boolean isSystemStream() {
    return stream == System.out || stream == System.err;
  }
  
  /** Returns the current time as string for insertion into log messages. */
  protected String getTimestamp() {
    return Instant.now().toString();
  }  
}
