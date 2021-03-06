(defproject mongol "1.0"
  :repositories {"incanter" "http://repo.incanter.org"}
  :description "The Incanting Mathematical Mongol"
  :dependencies [
    [org.clojure/clojure "1.1.0"]
    [org.clojure/clojure-contrib "1.1.0"]
    [org.clojars.somnium/congomongo "0.1.1-SNAPSHOT"
      :exclusions [org.clojars.somnium/mongo-java-driver]]
    [org.incanter/incanter-full "1.2.0-SNAPSHOT"]
    [org.mongodb/mongo-java-driver "1.3.0-SNAPSHOT"]
    [net.sf.jung/jung-api "2.0"]
    [net.sf.jung/jung-algorithms "2.0"]
    [net.sf.jung/jung-graph-impl "2.0"]
    [joda-time "1.6"]
    [cupboard "1.0-SNAPSHOT"]
    [net.1978th/tokyocabinet "1.23"]
    [com.geni/clojure-protobuf "1.0"]
    [com.geni/jiraph "1.0"]
  ]
  :native-path "/opt/lib"
  )
