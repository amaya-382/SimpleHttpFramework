SimpleHttpServer
================

## Requirements
* ./private/routingTable.json
* ./public/{your assets}
* path/to/controller/{your controller}.scala

## About routing table
```json
[
  {
    "method": method,
    "regex": url pattern,
    "controller": full path to controller
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
