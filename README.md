# Tux.clj [![Build Status](https://travis-ci.org/rill-event-sourcing/rill.svg?branch=travis-ci)](https://travis-ci.org/rill-event-sourcing/rill)

This is a simple URL shortener written in Clojure.

## Prerequisites

[Leiningen][1] and [Redis][2].

[1]: https://github.com/technomancy/leiningen
[2]: http://redis.io/

## Running

Make sure the redis instance is running in the default port
(6379), then simply run

    lein ring server

