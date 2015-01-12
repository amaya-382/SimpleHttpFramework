Simple Http Framework
=====================

## Requirements
* ./private/routingTable.json
* ./public/{your assets}
* path/to/controller/{your controller}.scala

## Example project
[Board](https://github.com/amaya-382/Board)

## About routing table
```json
[
  {
    "method": method,
    "pattern": url pattern,
    "controller": name of controller with namespace
  }
]
```

## About controller
```scala
object Controller {
  def someAction: HttpRequest => HttpResponse = ???
}
```

## Acknowledgement
* [json4s](https://github.com/json4s/json4s)

## License
***MIT License***
