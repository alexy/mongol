(ns mongol.repliers
  (:use [somnium.congomongo]
        [clojure.contrib.seq-utils :only [partition-all]])
  (:import [edu.uci.ics.jung.graph DirectedSparseGraph]
            edu.uci.ics.jung.algorithms.scoring.PageRank))

(mongo! :db "twitter")

;; build graph from mongo reps, form in-memory would require [k v] instead of {from :user rs :reps}
;; we map indexes explicitly, or could have used seq-utils indexed doing the same:

(defn graph-from-mongo
  "compute pagerank on a graph fetched from a mongo collection; TODO parameterize user-key and reps-key"
  [coll-name & args]
  (let [{user-key :user-key, reps-key :reps-key, quant :quant 
         :or {user-key :user reps-key :reps quant 10000}} 
         (apply hash-map args)]
    (doseq [[i {from user-key rs reps-key}] (map vector (iterate inc 0) (fetch coll-name))] 
      (when (= (mod i quant) 0) (.print System/err (str " "(quot i quant))))
      (doseq [to (keys rs)]
        ;;  from mongo, the to end comes up as keyword, convert to string with (name _)
      (let [to (name to)]
      (.addEdge g (str from " " to) from to))))
      g))
      
(defn graph-from-reps
  "compute pagerank from a reps graph in memory already"
  [reps & args]
  (let [{quant :quant :or {quant 10000}} (apply hash-map args)
    g (DirectedSparseGraph.)]
    (doseq [[i [from rs]] (map vector (iterate inc 0) reps)] 
      (when (= (mod i quant) 0) (.print System/err (str " "(quot i quant))))
      (doseq [to (keys rs)]
        ;; in lcreps, both ends are strings already
      (.addEdge g (str from " " to) from to)))
      g))



(def ranker (PageRank. g 0.15))
(.evaluate ranker)

;; (def nodes (.getVertices g))
;; (def nodes-iter (.iterator nodes))
(def nodes-iter (.. g (getVertices) (iterator)))
(def pg (map (fn [user] [user (.getVertexScore ranker user)]) (iterator-seq nodes-iter)))
(def pg-desc (sort-by second > pg))

(mongo-store-pairs pg-desc :pagerank :user :score 10000)

(take 10 (map #(dissoc % :_ns :_id) (fetch :pagerank)))
