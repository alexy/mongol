(ns tfitter 
  (:use [somnium.congomongo]
        [clojure.contrib.seq-utils :only [partition-all]])
  (:require [clojure.contrib.str-utils2 :as s2])
  (:import [org.joda.time DateTime]
           [java.util Date]
           [java.text SimpleDateFormat]))
  
(mongo! :db "twitter")

(def sample-twit-reps '[
  {:id 1 :screen_name "Vasya" :in_reply_to_screen_name "petya" :created_at "2009-06-15 04:01:11"},
  {:id 2 :screen_name "vasya" :in_reply_to_screen_name "Masha" :created_at "2009-06-15 04:01:22"},
  {:id 3 :screen_name "petya" :in_reply_to_screen_name "vasya" :created_at "2009-06-15 04:01:33"},
  {:id 4 :screen_name "petya"                                  :created_at "2009-06-15 04:01:44"},
  {:id 5 :screen_name "vasya" :in_reply_to_screen_name "masha" :created_at "2009-06-15 04:01:55"}
  ])

;; TODO date-at moved to days.clj

(defn scan-reps 
  "reduce function to build the replier graph"
  [[reps counter quant] twit]
  (if (= (mod counter quant) 0) 
    (.print System/err (format " %d" (quot counter quant))))
  ;; (print twit)
  (let [to (:in_reply_to_screen_name twit)
        reps 
          (if to
            (let [to (.toLowerCase to) 
                  ;; NB will fail on nil screen_name, but shouldn't happen (c)!
                  from (.toLowerCase (:screen_name twit))
                  ;; at (:created_at twit)
                  ;; at (joda-at (:created_at twit))
                  at (date-at (:created_at twit))
                  ]
            (update-in reps [from to] #(conj (or % []) at)))
            reps)]
    [reps (inc counter) quant]))
 
  
(defn fetch-twit-reps
  "lazy seq of twits from mongo collection coll with reps fields"
  [coll]
  (fetch coll :only [:screen_name :in_reply_to_screen_name :created_at]))
  
(defn replier-graph
  "build replier graph of twits, directed by mentions"
  [twits quant]
  ((reduce scan-reps [{} 0 quant] twits) 0))

;; (def reps (replier-graph sample-twit-reps 2))
;; (time (def reps (replier-graph (fetch-twit-reps :hose) 1000000)))
  
;; (time (insert! :reps reps)) ; very slow
;; (use '[clojure.contrib.seq-utils :only (partition-all)]) ; when not in ns

(def hashify (partial apply hash-map))


  (doseq [x (partition-all 10000 
    ;; (map hashify (seq reps))
    ;; (map (fn [[from rs]] { :user from :reps (map (fn [to times] {:to to ??}) rs) }) reps)
    (map (fn [[k v]] { :user k :reps v }) reps)
      )] 
    (.print System/err ".")
    (mass-insert! :reps x))
(.println System/err)
