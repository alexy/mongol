(defproject mongol "0.1"
  :description "The Incanting Mathematical Mongol"
  :dependencies [
    [org.clojure/clojure "1.1.0-master-SNAPSHOT"]
    [org.clojure/clojure-contrib "1.1.0-master-SNAPSHOT"]
    [org.clojars.somnium/congomongo "0.1.1-SNAPSHOT"
      :exclusions [org.clojars.somnium/mongo-java-driver]]
    [incanter/incanter "1.0-master-SNAPSHOT"]
    [org.mongodb/mongo-java-driver "1.2.0-SNAPSHOT"]
    [net.sf.jung/jung-api "2.0"]
    [net.sf.jung/jung-algorithms "2.0"]
    [net.sf.jung/jung-graph-impl "2.0"]
    [joda-time "1.6"]]
  :main hello)
