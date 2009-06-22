/**
 * Copyright 2005 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.dfs;

import java.util.*;

/**************************************************
 * DatanodeDescriptor tracks stats on a given DataNode,
 * such as available storage capacity, last update time, etc.,
 * and maintains a set of blocks stored on the datanode. 
 *
 * @author Mike Cafarella
 * @author Konstantin Shvachko
 **************************************************/
class DatanodeDescriptor extends DatanodeInfo {

  private volatile TreeSet blocks = null;

  DatanodeDescriptor( DatanodeID nodeID ) {
    this( nodeID, 0L, 0L, 0 );
  }
  
  /**
   * Create DatanodeDescriptor.
   */
  DatanodeDescriptor( DatanodeID nodeID, 
                      long capacity, 
                      long remaining,
                      int xceiverCount ) {
    super( nodeID );
    this.blocks = new TreeSet();
    updateHeartbeat(capacity, remaining, xceiverCount);
  }

  /**
   */
  void updateBlocks(Block newBlocks[]) {
    blocks.clear();
    for (int i = 0; i < newBlocks.length; i++) {
      blocks.add(newBlocks[i]);
    }
  }

  /**
   */
  void addBlock(Block b) {
    blocks.add(b);
  }

  /**
   */
  void updateHeartbeat(long capacity, long remaining, int xceiverCount) {
    this.capacity = capacity;
    this.remaining = remaining;
    this.lastUpdate = System.currentTimeMillis();
    this.xceiverCount = xceiverCount;
  }
  
  /**
   * Verify whether the node is dead.
   * 
   * A data node is considered dead if its last heartbeat was received
   * EXPIRE_INTERVAL msecs ago.
   */
  boolean isDead() {
    return getLastUpdate() < 
              System.currentTimeMillis() - FSConstants.EXPIRE_INTERVAL;
  }

  Block[] getBlocks() {
    return (Block[]) blocks.toArray(new Block[blocks.size()]);
  }

  Iterator getBlockIterator() {
    return blocks.iterator();
  }
}