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

mod helper_functions;
mod read_seek;

use helper_functions::{lsbf_dump, open_and_seek_input_stream};

use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::{jboolean,jlong};

/**
 * For naming conventions for functions being exported to be callable from Scala
 * see https://github.com/jni-rs/jni-rs. But it basically is this pattern:
 * 
 *  1. Java_
 *  2. Organization folder structure, after src/main/scala, or package name by 
 * replacing "." or "/" with "_" folowed by a trailing "_"
 *    eg:
 *      package name: org.apache.daffodil.lsbfDump
 *      rust function part: org_apache_daffodil_lsbfDump
 *  3. Scala class name followed by "_" 
 *    eg:
 *      scala class: class FFI() extends NativeLoader("lsbfdump")
 *      rust function part: FFI_
 *  4. Scala @native function name.
 *    eg:
 *      scala class @native function: @native def lsbfDumpFile
 *      rust function part: lsbfDumpFile
 * 
 * If you follow these numbered items for this example you should get:
 * 
 *  1. Java_
 *  2. org_apache_daffodil_lsbfDump (package_name = org.apache.daffodil.lsbfDump)
 *  3. FFI_ (scala_class_name = FFI)
 *  4. lsbfDumpFile (scala_class_function_name = lsbfDumpFile)
 * 
 * Adding all four parts together gives us our function name:
 *  Java_org_apache_daffodil_lsbfDump_FFI_lsbfDumpFile
 *
 */

#[no_mangle]
pub extern "system" fn Java_org_apache_daffodil_lsbfDump_FFI_lsbfDumpFile(
  mut env: JNIEnv,
  _class: JClass,
  filename_in: JString,
  offset_in: jlong,
  length_in: jlong,
  show_address_in: jboolean,
) -> jboolean {
  let filename: String = env
    .get_string(&filename_in)
    .expect("Couldn't get scala string")
    .into();

  let offset: u64 = offset_in as u64;
  let length: u64 = length_in as u64;
  let show_address: bool = show_address_in != 0;

  match crate::open_and_seek_input_stream(&filename, offset) {
    Ok(mut reader) => {
      match crate::lsbf_dump(&mut reader, offset, length, show_address) {
        Ok(lines) => {
          for line in &lines {
              println!("{}", line);
          }
          return true as jboolean
        }
        Err(err) => {
          eprintln!("lsbfdump: An I/O error occurred - {}", err);
          // std::process::exit(1);
          return false as jboolean
        }
      }
    }
    Err(err) => {
      eprintln!("lsbfdump: An I/O error occurred - {}", err);
      // std::process::exit(1);
      return false as jboolean
    }
  }
}
