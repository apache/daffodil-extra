<!--
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->
# lsbfDump - Least Significant Bit First Bit Dump Utility

Creates a data dump at bits level for data that has dfdl:bitOrder="leastSignificantBitFirst".

For example:

```
01000110 01001100 01000101 01111111 | 0x00000000
00000000 00000001 00000001 00000010 | 0x00000004
00000000 00000000 00000000 00000000 | 0x00000008
00000000 00000000 00000000 00000000 | 0x0000000C
00000000 00111110 00000000 00000011 | 0x00000010
00000000 00000000 00000000 00000001 | 0x00000014
```
The address (in hex) is on the right. The bytes start on the right and increase moving left and downward. 
The least significant bit of each byte is on the right (as people usually write numbers). 

The purpose of this is for use with data where the bit positions are numbered from right to left. I.e., 
the first bit (position 0, or position 1 if using 1-based indexing) in each byte is the rightmost bit. 

# License

This is open-source software under the Apache Software License (V2.0). See the LICENSE file for the full text.

# Running

After building, the native executable will be in the target/native-image directory, named lsbfdump.
It can be copied/moved to wherever you want it, likely to some directory on the PATH so that it can be used 
conveniently. 

Instructions on how to use can be obtained by running `target/native-image/lsbfdump --help`.

# Building

This is written in Scala (2.13) and compiled and deployed using GraalVM to create a fast-starting executable intended for command line use.

The command

```
sbt nativeImage
```

Creates a native image under target/native, named lsbfdump.

Note that a huge amount of stuff including an entire GraalVM SDK downloads on first build using 'sbt nativeImage'.