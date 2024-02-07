lazy val commonSettings = Seq(
  scalaVersion := "2.13.12"
)

lazy val native = project
  .in(file("native"))
  .settings(commonSettings)
  .settings(
    name := "lsbfdump-jni-rs-native",
    nativeCompile / sourceDirectory := baseDirectory.value,
  )
  .enablePlugins(JniNative)

lazy val root = project
  .in(file("."))
  .settings(commonSettings)
  .settings(
    name := "lsbfdump-jni-rs",
    Compile / mainClass := Some("org.apache.daffodil.lsbfDump.LSBFDump"),
    libraryDependencies ++= List(
      "junit" % "junit" % "4.13.2" % Test,
      // needed below package to successfully run junit tests
      "com.github.sbt" % "junit-interface" % "0.13.3" % Test exclude("junit", "junit-dep"),
    ),
    sbtJniCoreScope := Compile, // because we use `NativeLoader`, not the `@nativeLoader` macro
  )
  .dependsOn(native % Runtime)

// enablePlugins(NativeImagePlugin)
// // Settings for the native image
// nativeImageOptions ++= Seq(
//   "--no-fallback", // does not create nativeImage if it will require a JVM.
// //  "--enable-all-security-services"
// // Add other native-image options as needed
// )
