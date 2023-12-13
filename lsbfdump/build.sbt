version := "0.1.0"

scalaVersion := "2.13.12"

enablePlugins(NativeImagePlugin)

// Define the main class of your application
Compile / mainClass := Some("org.apache.daffodil.lsbfDump.LSBFDump")

// Settings for the native image
nativeImageOptions ++= Seq(
  "--no-fallback", // does not create nativeImage if it will require a JVM.
//  "--enable-all-security-services"
// Add other native-image options as needed
)

// Library dependencies
libraryDependencies ++= Seq(
  "junit" % "junit" % "4.13.2" % Test,
)
