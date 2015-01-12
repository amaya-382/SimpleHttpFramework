Simple Http Framework
=====================

## Requirements
* ./private/routingTable.json
* ./public/{your assets}
* path/to/controller/{your controller}.scala

## Usage
add following to your `build.sbt`
```scala
lazy val root = project.in(file(".")).dependsOn(simpleHttpFramework)

lazy val simpleHttpFramework = uri("git://github.com/amaya-382/SimpleHttpFramework.git")
```

### About routing table
```json
[
  {
    "method": method,
    "pattern": url pattern,
    "controller": name of controller with namespace
  }
]
```

### About controller
```scala
object Controller {
  def someAction: HttpRequest => HttpResponse = ???
}
```

## Project example
[Board](https://github.com/amaya-382/Board)

## Acknowledgement
* [json4s](https://github.com/json4s/json4s)

## License
***MIT License***
