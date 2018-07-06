# A REST API with http4s and Cats IO Monad

### How to

Initialize application: `sbt run`

Create a hut
```shell
curl -v -H "Content-Type: application/json" \
     -X POST \
     http://localhost:8080/huts \
     -d '{"name":"River Hut"}'
```

Update a hut
```shell
curl -v -H "Content-Type: application/json" \
     -X PUT \
     http://localhost:8080/huts \
     -d '{"id":"123","name":"Mountain Hut"}'
```

You can get hut `curl -i http://localhost:8080/huts/123`

And finally delete a hut `curl -v -X DELETE http://localhost:8080/huts/123`

Hello World? See at the [hello-world](hello-world) branch

### Reference

https://medium.com/@alandevlin7/pure-functional-rest-api-with-http4s-7a38782a2a99
