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

// View https://rust-lang.github.io/api-guidelines/naming.html for naming conventions for functions

use crate::stdin_seek::StdinWithSeek;

use std::fs::File;
use std::io::{self, Read, Seek, SeekFrom};


pub fn lsbf_dump(reader: &mut dyn Read, offset: u64, length: u64, show_address: bool) -> io::Result<Vec<String>> {
  let mut lines = Vec::new();
  let mut count = 0;
  let mut buffer = [0; 4];

  while count < length {
    match reader.read_exact(&mut buffer) {
      Ok(_) => {
        let byte_strings = buffer.iter().copied().rev().map(byte_to_binary_string).collect::<Vec<String>>().join(" ");
        let extra_spaces = if buffer.len() == 4 { "".to_string() } else { "         ".repeat(4 - buffer.len()) };
        let address_string = if show_address { format!(" | 0x{:08X}", count + offset) } else { "".to_string() };
        lines.push(format!("{}{}{}", extra_spaces, byte_strings, address_string));
        count += 4;
      }
      Err(_) => break,
    }
  }

  Ok(lines)
}

pub fn open_and_seek_input_stream(filename: &str, offset: u64) -> io::Result<Box<dyn Read>> {
  if filename == "-" {
    let mut stdin_with_seek = StdinWithSeek;
    stdin_with_seek.seek(SeekFrom::Start(offset))?;
    Ok(Box::new(stdin_with_seek) as Box<dyn Read>)
  } else {
    let file = File::open(filename)?;
    let mut file_seek = io::BufReader::new(file);
    file_seek.seek(SeekFrom::Start(offset))?;
    Ok(Box::new(file_seek) as Box<dyn Read>)
  }
}

fn byte_to_binary_string(b: u8) -> String {
  format!("{:08b}", b)
}
