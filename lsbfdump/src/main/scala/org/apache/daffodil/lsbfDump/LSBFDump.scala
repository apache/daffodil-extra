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

import java.io.IOException
import java.io.InputStream
import java.nio.channels.Channels
import java.nio.channels.FileChannel
import java.nio.file.NoSuchFileException
import java.nio.file.Paths

object LSBFDump {

  private val helpText: String =
    """Usage: lsbfdump [--file <filename>] [--offset <offset>] [--length <numBytes>] [--noAddress] [--help]
      |
      |<filename>   : The file to read bytes from or '-' for standard input or if not provided standard input is used.
      |[offset]     : The starting offset in the file (default is 0). If the offset is past the length of the data, no output is produced.
      |[length]     : The number of bytes to display (default is entire file). If 0 no output is produced.
      |--noAddress  : Do not display the address of each byte line.
      |--help       : Display this help information.
      |
      |Examples:
      | Default usage (128 bytes from standard input, starting at offset 0, with addresses):
      |   lsbfdump --file - --length 128
      |
      | With specific file, offset and byte count:
      |   lsbfdump --file filename --offset 10 --length 64
      |
      | With --noAddress to hide addresses:
      |   lsbfdump --file filename --offset 10 --length 64 --noAddress
      |""".stripMargin

  private def usageError(): Unit = {
    System.err.println("lsbfdump: Invalid arguments provided.")
    System.err.println(helpText)
    sys.exit(1)
  }

  def main(args: Array[String]): Unit = {
    val parsedArgs = parseArgs(args)

    if (parsedArgs.contains("help")) {
      println(helpText)
      sys.exit(0)
    }

    if (parsedArgs.contains("invalid")) usageError()

    val filename =
      parsedArgs.getOrElse("file", "-") // if not provided at all, also uses std-in.
    val offset = parsedArgs.get("offset").map(_.toLong).getOrElse(0L)
    val length = parsedArgs.get("length").map(_.toLong).getOrElse(Long.MaxValue)
    val showAddress = !parsedArgs.contains("noAddress")

    assert(offset >= 0)
    assert(length >= 0)

    val lines = lsbfDumpFile(filename, offset, length, showAddress)
    lines.foreach(println(_))
  }

  /**
   * Suitable for use on large data files, as it streams the data as it displays it.
   */
  private[lsbfDump] def lsbfDumpFile(
    filename: String,
    offset: Long,
    length: Long,
    showAddress: Boolean,
  ): Iterator[String] = {
    try {
      val input = openAndSeekInputStream(filename, offset)
      val bytes = inputStreamToIterator(input, length)
      val lines = lsbfDump(bytes, offset, showAddress)
      lines
    } catch {
      case e: NoSuchFileException =>
        System.err.println(s"lsbfdump: File not found - ${e.getMessage}")
        sys.exit(1)
      case e: IOException =>
        System.err.println(s"lsbfdump: An I/O error occurred - ${e.getMessage}")
        sys.exit(1)
    }
  }

  private[lsbfDump] def lsbfDump(
    bytes: Iterator[Byte],
    offset: Long,
    showAddress: Boolean,
  ): Iterator[String] = {
    val lines = bytes
      .grouped(4)
      .zipWithIndex
      .map { case (group, index) =>
        val byteStrings = group.reverse.map(byteToBinaryString).mkString(" ")
        val extraSpaces =
          if (group.length == 4) ""
          else {
            val nBytesMissing = 4 - group.length
            ((" " * 9) * nBytesMissing) // 9 to account for space between bytes
          }
        val addressString = if (showAddress) f" | 0x${index * 4 + offset}%08X" else ""
        extraSpaces + byteStrings + addressString
      }
    lines
  }

  private def openAndSeekInputStream(filename: String, offset: Long) = {
    if (filename == "-") {
      if (skip(System.in, offset) != offset) {
        // throw new IOException("lsbfdump: Unable to skip to offset.")
      }
      System.in
    } else
      Channels.newInputStream(FileChannel.open(Paths.get(filename)).position(offset))
  }

  /**
   * java.io.InputStream.skip on standard input doesn't work - I get illegal seek errors.
   * (at least on GraalVM nativeImage). So we use our own skip.
   * @param is
   * @param count
   * @return
   */
  private def skip(is: InputStream, count: Long): Long = {
    assert(count >= 0)
    var n: Long = 0
    while (is.read() != -1 && n < count) { n += 1 }
    n
  }

  private def inputStreamToIterator(inputStream: InputStream, len: Long): Iterator[Byte] =
    new Iterator[Byte] {

      private var count: Long = 0

      def hasNext: Boolean = inputStream.available() > 0 && count < len

      def next(): Byte = {
        val nextByte = inputStream.read()
        if (nextByte == -1) {
          throw new NoSuchElementException("End of stream reached")
        }
        count += 1
        nextByte.toByte
      }
    }

  private[lsbfDump] def byteToBinaryString(b: Byte): String = {
    val unsignedByte = b & 0xff
    String.format("%8s", Integer.toBinaryString(unsignedByte)).replace(' ', '0')
  }

  def parseArgs(args: Array[String]): Map[String, String] = {
    var argMap = Map[String, String]()
    var i = 0
    while (i < args.length) {
      args(i) match {
        case "--file" if i + 1 < args.length =>
          argMap += ("file" -> args(i + 1))
          i += 2
        case "--offset" if i + 1 < args.length =>
          argMap += ("offset" -> args(i + 1))
          if (!isNonNegativeInteger(args(i + 1))) usageError()
          i += 2
        case "--length" if i + 1 < args.length =>
          argMap += ("length" -> args(i + 1))
          if (!isNonNegativeInteger(args(i + 1))) usageError()
          i += 2
        case "--noAddress" =>
          argMap += ("noAddress" -> "")
          i += 1
        case "--help" =>
          argMap += ("help" -> "")
          i = args.length // Break the loop
        case _ =>
          argMap += ("invalid" -> "")
          i = args.length // Break the loop
      }
    }
    argMap
  }

  private def isNonNegativeInteger(s: String): Boolean =
    try { s.toLong >= 0 }
    catch { case _: NumberFormatException => false }

}
