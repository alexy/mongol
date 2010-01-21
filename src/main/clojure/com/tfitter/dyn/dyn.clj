(ns mongol.repliers
  (:use [somnium.congomongo]
        [clojure.contrib.seq-utils :only [partition-all]])
  ;; (:require [clojure.contrib.str-utils2 :as s2])
  (:import [org.joda.time DateTime]
           [edu.uci.ics.jung.graph DirectedSparseGraph]
            edu.uci.ics.jung.algorithms.scoring.PageRank))

(mongo! :db "twitter")      
      
(defn mongo-store-day-pairs 
  "store a 2-tuple collection in mongo as map-pairs with given key and value name, in a given collection"
  [coll coll-name key-name val-name day & quant]
  (.print System/err (str "saving " coll-name ", day " day))
  (let [coll-desc (sort-by second > coll)
    _ (.print System/err ">>>")
    quant (or quant 10000) 
    ]
  (doseq [x (partition-all quant (map (fn [[k v]] {key-name k val-name v :day day}) coll-desc))] 
  (.print System/err ".") (mass-insert! coll-name x))
  (.println System/err)))
  
;;  (->> {:a {:b [1 2] :c [3]} :b {:a [5 6] :d [7 8 9]}} 
;;  (reduce (fn [m [from reps]] (let [length 
;;  (reduce (fn [num [_ array]] (+ num (count array))) 0 reps)] 
;;      (assoc m from length))) {}))  

(defn reps-lens
  "summarize a replier graph by replacing, for each from node,
  the map of to-event-lists with the total number of the to-events"
  [reps]
  (->> reps (reduce (fn [m [from reps]] (let [length 
       (reduce (fn [num [_ array]] (+ num (count array))) 0 reps)] 
           (assoc m from length))) {})))
           
(defn pagerank 
	"compute pagerank of a graph, return pairs (user, rank) in decreasing order of rank"
	[graph & alpha]
	(let [alpha (or alpha 0.15)
		  ranker (PageRank. graph alpha)
		  _ (.evaluate ranker)
		  nodes-iter (.. graph (getVertices) (iterator))
		  nodes (iterator-seq nodes-iter)
          pg (map (fn [user] [user (.getVertexScore ranker user)]) nodes)]
          (sort-by second > pg)))
          
		
		
(defn day-change 
  "record daily stats in mongo; TODO key names are hardcoded for now"
  [mongo-names numtwits replies mentions graph progress day]
  (let [{:keys [
      twits-mongo
      daytwits-mongo 
      numtwits-mongo
      numreplies-mongo
      nummentions-mongo
      pagerank-mongo
      ]
      :or 
      {
      twits-mongo         :hose
      daytwits-mongo      :daytwits ; number of twits per day scanned, total
      numtwits-mongo      :dnumtwits  ; d is for dynamic
      numreplies-mongo    :dnumreplies
      nummentions-mongo   :dnummentions
      pagerank-mongo      :dpagerank
      }} mongo-names
      
      numreplies    (reps-lens replies)
      nummentions   (reps-lens mentions)
      pageranks     (pagerank graph) 
      ]
      (.println System/err (str "\nsaving " daytwits-mongo ", for day " day "=> " progress))
      ;; NB :to nil is needed to avoid NPE:
      (insert! daytwits-mongo {:day day, :twits progress} :to nil)
      (mongo-store-day-pairs numtwits    numtwits-mongo    :user :numtwits    day)
      (mongo-store-day-pairs numreplies  numreplies-mongo  :user :numreplies  day)
      (mongo-store-day-pairs nummentions nummentions-mongo :user :nummentions day)   
      (mongo-store-day-pairs pageranks   pagerank-mongo    :user :pagerank    day)   
    ))
      
(defn update-numtwits
  [numtwits user]
  (let [old-num (numtwits user)]
    ;; (if (old-num) (assoc numtwits user (inc old-num)) (assoc numtwits user 1))
    (update-in numtwits [user] #(inc (or % 0)))
  ))

;; TODO this doesn't work with nestedness:
;; works: (let [half-there (persistent! (update-in! (transient {:a (transient {:b 1})}) [:a :b] inc)) keys (keys half-there) vals (vals half-there)] (zipmap keys (map persistent! vals)))
;; fails: (update-in! (transient {}) [:a :b] #(inc (or % 0)))
;; (defn update-in! [m ks f & args] (binding [assoc assoc!] (apply update-in m ks f args)))

(defn update-replies
  [replies  from to at]
  (update-in replies  [from to] #(conj (or % []) at)))
  
(defn update-mentions
  [mentions from to at]
  (update-in mentions [to from] #(conj (or % []) at)))
  
(defn update-graph
  [graph from to]
  (when (and from to) 
  	(.addEdge graph (str from " " to) from to))
  graph
  )
  
(defn get-twits
  "lazy seq of all twits with possible :only restriction"
  [coll-name]
  (let [
    raw  (fetch coll-name :only [:screen_name :in_reply_to_screen_name :created_at]
    					  :where {:$orderby {:created_at 1}} :no-timeout? true)
    ]
    ;; TODO result destructuring ahead anyways, dissoc not needed?
  (map #(dissoc % :_id :_ns) raw)))
    
  
(defn joda-at [s]
  (DateTime. (str (.replace s " " "T") "Z")))

(defn daynum-of-raw-twit
  "day of study, 0-based on the first day in the dataset ; TODO fails across years"
  [{:keys [created_at] :as twit}]
  (let [dt (joda-at created_at)]
  (.getDayOfYear (DateTime. dt))))
  
(defn daynum-of-dt
  "day of twit from created_at's datetime"
  [dt]
  (.getDayOfYear dt))
  


;; NB here's what a twit looks like in :hose in MongoDB:
;; mongol.repliers=> (fetch-one :hose)
;; {:text "@KwanaWrites I'm avoiding writing by making avatars :)", :in_reply_to_screen_name "KwanaWrites", :user_id 1.7581647E7, :truncated false, :_ns "hose", :created_at "2009-10-16 17:28:45", :geo nil, :_id #<ObjectId 4b084e72beaf8d7ff84a0509>, :in_reply_to_status_id 4.919717719E9, :favorited false, :source "web", :id 4.921075609E9, :in_reply_to_user_id 1.9607982E7, :screen_name "Amandamccabe1"}      
