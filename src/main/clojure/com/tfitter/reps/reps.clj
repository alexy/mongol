(ns tfitter 
  (:use [somnium.congomongo]
        [clojure.contrib.seq-utils :only [partition-all]])
  (:require [clojure.contrib.str-utils2 :as s2])
  (:import [org.joda.time DateTime]
           [java.util Date]
           [java.text SimpleDateFormat]))
  
(mongo! :db "twitter")

(def twits '[
  {:id 1 :screen_name "vasya" :in_reply_to_screen_name "petya" :created_at "2009-06-15 04:01:11"},
  {:id 2 :screen_name "vasya" :in_reply_to_screen_name "masha" :created_at "2009-06-15 04:01:22"},
  {:id 3 :screen_name "petya" :in_reply_to_screen_name "vasya" :created_at "2009-06-15 04:01:33"},
  {:id 4 :screen_name "petya"                                  :created_at "2009-06-15 04:01:44"},
  {:id 5 :screen_name "vasya" :in_reply_to_screen_name "masha" :created_at "2009-06-15 04:01:55"}
  ])

(defn joda-at [s]
  (DateTime. (str (s2/replace s " " "T") "Z")))
  
(def dateFmt (SimpleDateFormat. "yyyy-MM-dd HH:mm:ss Z"))

(defn date-at [s]
  (.parse dateFmt (str s " +0000")))
  
(defn scan-reps [[reps counter] twit]
  (let [grane 1000000]
  (if (= (mod counter grane) 0) (.print System/err (format " %d" (quot counter grane)))))
  ;; (print twit)
  (let [to (:in_reply_to_screen_name twit)
        reps 
          (if to
            (let [from (:screen_name twit)       
                  ;; at (:created_at twit)
                  ;; at (joda-at (:created_at twit))
                  at (date-at (:created_at twit))
                  ]
            (update-in reps [from to] #(conj (or % []) at)))
            reps)]
    [reps (inc counter)]))
 
(def reps ((reduce scan-users [{} 0] twits) 0))
(print reps)
  
(time 
  (def reps
    ((reduce scan-users [{} 0] 
      (fetch :hose :only [:screen_name :in_reply_to_screen_name :created_at])) 0)))
(.println System/err)

;; (time (insert! :reps reps)) ; very slow
;; (use '[clojure.contrib.seq-utils :only (partition-all)]) ; when not in ns

(def hashify (partial apply hash-map))

(time 
  (doseq [x (partition-all 10000 
    ;; (map hashify (seq reps))
     (map (fn [[k v]] { :user k :reps v }) reps)
      )] 
    (.print System/err ".")
    (mass-insert! :reps x)))
(.println System/err)
