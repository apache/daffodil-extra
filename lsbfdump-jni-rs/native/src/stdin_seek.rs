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

// View https://rust-lang.github.io/api-guidelines/naming.html for naming conventions for traits and structs
use std::io::{self, Cursor, Read, Seek, SeekFrom};

pub struct StdinWithSeek;

impl Read for StdinWithSeek {
  fn read(&mut self, buf: &mut [u8]) -> io::Result<usize> {
    io::stdin().read(buf)
  }
}

impl Seek for StdinWithSeek {
  fn seek(&mut self, pos: SeekFrom) -> io::Result<u64> {
    let mut cursor = Cursor::new(Vec::new());
    cursor.seek(pos)
  }
}
