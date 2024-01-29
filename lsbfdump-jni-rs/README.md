# Example Scala calling Rust using JNI

## Rust naming conventions

This url, https://rust-lang.github.io/api-guidelines/naming.html, provides helpful information on Rust naming conventions.

## Reference

This repos implementation is based on the library [jni-rs](https://github.com/jni-rs/jni-rs). The example provided is very helpful to understand how things work.

## License

This is open-source software under the Apache Software License (V2.0). See the [LICENSE](./LICENSE) file for the full text.

## Testing

No prior commands should need ran.

The command:

```bash
sbt test
```

Should provide output similar to the below (note the `[...]` in the paths is so the full path wasn't shown):

```bash
[info] welcome to sbt 1.9.7 (Eclipse Adoptium Java 17.0.5)
[info] loading settings for project lsbfdump-jni-rs-build from plugins.sbt ...
[info] loading project definition from [...]/daffodil-extra/lsbfdump-jni-rs/project
[info] loading settings for project root from build.sbt ...
[info] set current project to lsbfdump-jni-rs (in build file:[...]/daffodil-extra/lsbfdump-jni-rs/)
[info] Building library with native build tool Cargo
[info] compiling 2 Scala sources to [...]/daffodil-extra/lsbfdump-jni-rs/target/scala-2.13/classes ...
[warn]     Finished release [optimized] target(s) in 0.02s
[warn] More than one file was created during compilation, only the first one ([...]/daffodil-extra/lsbfdump-jni-rs/native/target/native/arm64-darwin/bin/release/liblsbfdump.dylib) will be used.
[success] Library built in [...]/daffodil-extra/lsbfdump-jni-rs/native/target/native/arm64-darwin/bin/release/liblsbfdump.dylib
[info] compiling 1 Scala source to [...]/daffodil-extra/lsbfdump-jni-rs/target/scala-2.13/test-classes ...
01111000 01000101 00100000 00100011 | 0x00000000
01101100 01110000 01101101 01100001 | 0x00000004
01100011 01010011 00100000 01100101 | 0x00000008
00100000 01100001 01101100 01100001 | 0x0000000C
01101100 01101100 01100001 01100011 | 0x00000010
00100000 01100111 01101110 01101001 | 0x00000014
01110100 01110011 01110101 01010010 | 0x00000018
01101001 01110011 01110101 00100000 | 0x0000001C
01001010 00100000 01100111 01101110 | 0x00000020
00001010 00001010 01001001 01001110 | 0x00000024
01010010 00100000 00100011 00100011 | 0x00000028
00100000 01110100 01110011 01110101 | 0x0000002C
01101001 01101101 01100001 01101110 | 0x00000030
01100011 00100000 01100111 01101110 | 0x00000034
01100101 01110110 01101110 01101111 | 0x00000038
01101111 01101001 01110100 01101110 | 0x0000003C
00001010 00001010 01110011 01101110 | 0x00000040
01110011 01101001 01101000 01010100 | 0x00000044
01101100 01110010 01110101 00100000 | 0x00000048
01110100 01101000 00100000 00101100 | 0x0000004C
00111010 01110011 01110000 01110100 | 0x00000050
01110101 01110010 00101111 00101111 | 0x00000054
01101100 00101101 01110100 01110011 | 0x00000058
00101110 01100111 01101110 01100001 | 0x0000005C
01101000 01110100 01101001 01100111 | 0x00000060
01101001 00101110 01100010 01110101 | 0x00000064
01110000 01100001 00101111 01101111 | 0x00000068
01110101 01100111 00101101 01101001 | 0x0000006C
01101100 01100101 01100100 01101001 | 0x00000070
01110011 01100101 01101110 01101001 | 0x00000074
01101101 01100001 01101110 00101111 | 0x00000078
00101110 01100111 01101110 01101001 | 0x0000007C
lsbfdump: An I/O error occurred - No such file or directory (os error 2)
[info] Passed: Total 2, Failed 0, Errors 0, Passed 2
...
```

Don't worry about the output that comes out as it comes from the Rust code. The Rust code then also returns either true or false based on if everything ran okay.
