# Otravo test assignment

We have 2 modules here - Cli and Core.

## Core

All business logic happens here.

You need to create InventoryService for working with it. You can pass
scala.io.BufferedSource to it or List\[Show\] directly.

## CLI

CLI client for test assignment.

### building

You can use pre-build version in root of the project, or you can build
it yourself. For it you can write `sbt cli/assembly` in console, when
you if root of project.

### runnig

you should have pre-installed java and write in console `java -jar
path/to/otravo-test.jar` and follow the instructions.