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
package org.apache.daffodil.lsbfDump

import java.io.File
import java.nio.file.Paths

import org.junit.Assert._
import org.junit.Test

class LSBFDumpTest {

  @Test
  def testDump1(): Unit = {
    val data = List(0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08).map { _.toByte }.iterator
    val lines = LSBFDump.lsbfDump(data, 0, showAddress = true).toSeq
    assertEquals(2, lines.length)
    assertEquals("00000100 00000011 00000010 00000001 | 0x00000000", lines.head)
    assertEquals("00001000 00000111 00000110 00000101 | 0x00000004", lines(1))
  }

  @Test
  def testDump2WithOffset4(): Unit = {
    val data = List(0x05, 0x06, 0x07, 0x08).map { _.toByte }.iterator
    val lines = LSBFDump.lsbfDump(data, 4, showAddress = true).toSeq
    assertEquals(1, lines.length)
    assertEquals("00001000 00000111 00000110 00000101 | 0x00000004", lines.head)
  }

  @Test
  def testDump2WithOffset4_shortBy1(): Unit = {
    val data = List(0x05, 0x06, 0x07).map { _.toByte }.iterator
    val lines = LSBFDump.lsbfDump(data, 4, showAddress = true).toSeq
    assertEquals(1, lines.length)
    assertEquals("         00000111 00000110 00000101 | 0x00000004", lines.head)
  }

  @Test
  def testDump2WithOffset4_noData(): Unit = {
    val data = List[Byte]().iterator
    val lines = LSBFDump.lsbfDump(data, 4, showAddress = true).toSeq
    assertEquals(0, lines.length)
  }

  @Test
  def testDump2WithOffset4NoAddress(): Unit = {
    val data = List(0x05, 0x06, 0x07, 0x08).map { _.toByte }.iterator
    val lines = LSBFDump.lsbfDump(data, 4, showAddress = false).toSeq
    assertEquals(1, lines.length)
    assertEquals("00001000 00000111 00000110 00000101", lines.head)
  }

  @Test
  def testDumpFileWithOffset4(): Unit = {
    val resource =
      new File(
        "src/test/resources/testData1.txt",
      ).toURI // getClass.getResource("testData1.txt").toURI
    val file = Paths.get(resource).toFile
    val fn = file.toString
    val lines = LSBFDump
      .lsbfDumpFile(
        filename = fn,
        offset = 4,
        length = file.length().toInt,
        showAddress = true,
      )
      .toSeq
    assertEquals(2, lines.length)
    assertEquals("00110111 00110110 00110101 00110100 | 0x00000004", lines.head)
  }

  @Test
  def testByteToBinaryString(): Unit = {
    assertEquals("00000000", LSBFDump.byteToBinaryString(0.toByte))
    assertEquals("00000001", LSBFDump.byteToBinaryString(1.toByte))
    assertEquals("11111111", LSBFDump.byteToBinaryString(255.toByte))
    assertEquals("10000000", LSBFDump.byteToBinaryString(128.toByte))
    assertEquals("01111111", LSBFDump.byteToBinaryString(127.toByte))
    assertEquals("11111110", LSBFDump.byteToBinaryString(-2.toByte))
    assertEquals("11111111", LSBFDump.byteToBinaryString(-1.toByte))
  }

  @Test
  def testParseArgsWithFile(): Unit = {
    val args = Array("--file", "testFile.txt")
    val parsedArgs = LSBFDump.parseArgs(args)
    assertEquals("testFile.txt", parsedArgs("file"))
    assertFalse(parsedArgs.contains("offset"))
    assertFalse(parsedArgs.contains("length"))
    assertFalse(parsedArgs.contains("noAddress"))
  }

  @Test
  def testParseArgsWithAllOptions(): Unit = {
    val args =
      Array("--file", "testFile.txt", "--offset", "10", "--length", "100", "--noAddress")
    val parsedArgs = LSBFDump.parseArgs(args)
    assertEquals("testFile.txt", parsedArgs("file"))
    assertEquals("10", parsedArgs("offset"))
    assertEquals("100", parsedArgs("length"))
    assertTrue(parsedArgs.contains("noAddress"))
  }

  @Test
  def testParseArgsWithInvalidOption(): Unit = {
    val args = Array("--unknownOption")
    val parsedArgs = LSBFDump.parseArgs(args)
    assertTrue(parsedArgs.contains("invalid"))
  }

}
