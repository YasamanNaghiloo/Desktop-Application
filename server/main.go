package main

import (
	"flag"
	"log"
	"net/http"
	. "server/routes"
)

func main() {
  addr := flag.String("addr", ":4000", "HTTPS network address")
  flag.Parse()

  mux := http.NewServeMux()

  var routes = map[string]http.Handler{
    "/": http.FileServer(http.Dir("public")),
    "/upload": http.HandlerFunc(Upload),
  };

  for path, handler := range routes {
    mux.Handle(path, handler)
  }

  srv := &http.Server{
    Addr:    *addr,
    Handler: mux,
  }

  log.Printf("Starting server on %s", *addr)
  err := srv.ListenAndServe()
  log.Fatal(err)
}
