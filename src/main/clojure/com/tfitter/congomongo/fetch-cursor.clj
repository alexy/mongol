(ns somnium.congomongo
  (:use [somnium.congomongo])
  (:import 
    [somnium.congomongo ClojureDBObject]
    [com.mongodb DBCursor Bytes]))
  
; defn doesn't work for java methods, non-functions:  
;(defn methornil [x f a] (if a (f x a) x))
;chouser
;(defmacro methornil [x f a] `(if ~a (~f ~x ~a) ~x))
;arohner
(defmacro methornil [x f a] `(let [a# ~a x# ~x] (if a# (~f x# a#) x#)))

(defunk fetch 
  "Fetches objects from a collection.
   Note that MongoDB always adds the _id and _ns
   fields to objects returned from the database.
   Optional arguments include
   :where  -> takes a query map
   :only   -> takes an array of keys to retrieve
   :as     -> what to return, defaults to :clojure, can also be :json or :mongo or :cursor
   :from   -> argument type, same options as above
   :skip   -> number of records to skip
   :limit  -> number of records to return
   :one?   -> defaults to false, use fetch-one as a shortcut
   :count? -> defaults to false, use fetch-count as a shortcut
   :dismongo?   -> defaults to false, leave :_id and :_ns in
   :no-timeout? -> defaults to false, i.e. do timeout"
  {:arglists
   '([collection :where :only :limit :skip :as :from :one? :count? :no-timeout?])}
  [coll :where {} :only [] :as :clojure :from :clojure
   :one? false :count? false :limit 0 :skip 0 
   :dismongo? false :no-timeout? false]
  (let [n-where (coerce where [from :mongo])
        n-only  (coerce-fields only)
        n-col   (get-coll coll)
        n-limit (if limit (- 0 (Math/abs limit)) 0)
        n-limit (int n-limit) ; TODO is it really encessary after ^^?
        skip    (int skip)
        dismonge (fn [m] (dissoc m :_id :_ns))
        ]
    (cond
      count? (.getCount n-col n-where n-only)
      one?   (when-let [#^DBCursor m (.findOne
                         #^DBCollection n-col
                         #^DBObject n-where
                         #^DBObject n-only)]
               (let [res (coerce m [:mongo as])]
               		(if dismongo? (dismonge res) res)))
      :else  (when-let [#^DBCursor m (->  #^DBCollection n-col
      						   (.find 
                               #^DBObject n-where
                               #^DBObject n-only)
                               (methornil .skip  skip)
                               (methornil .limit n-limit))]
               (when no-timeout?
               	(.addOption m Bytes/QUERYOPTION_NOTIMEOUT)
               	(.println System/err (str "set no-timeout on " coll)))
               (if (= as :cursor) m	
               	(let [res (coerce m [:mongo as] :many :true)]
               		(if dismongo? (map dismonge res) res)
               	))))))
    
; if only to pass on:    
; (defn foo (partial bar new-arg))

(defn find-seq [s e] 
	((apply hash-map (interleave s (iterate inc 0))) e))
	
(defn without [s n & [k]]
	(let [len (or k 1)]
	(concat (take n s) (drop (+ n len) s))))
	
(defn pos-arg [s arg]
	(let [pos (find-seq s arg)
		  pos (when pos (inc pos))
		  ]
		  (when (and pos (< pos (count s)))
		  				nth s pos)))

(defn bool-arg [s arg]
	(let [pos (find-seq s arg)] 
	(if pos [pos (without s pos)] [nil s])))
	
(defn val-arg [s arg]
	(let [pos (find-seq s arg)] 
	(if pos [(nth s (inc pos)) (without s pos 2)]
		[nil s])))

(defn fetch-fast 
	"get cursor from fetch and conj with transients; TODO push quant, dismongo and keys to fetch?"
	[& args]
	(let [[quant args]	   (val-arg  args :progress)    
		  [dismongo args]  (bool-arg args :dismongo)
		  [keys args]      (val-arg  args :keys)
		  ; TODO may add :only keys to args for fetch:
		  #^DBCursor cursor (apply fetch (concat args [:as :cursor]))		  
		  res (transient [])
		]
	(loop [i 0]
	  (when (.hasNext cursor)
	  	(let [m (-> cursor #^ClojureDBObject (.next) .toClojure)
		  	m (if dismongo (dissoc m :_id :_ns) m)
		  	m (if keys (map #(m %) keys) m)
		  	]
		 (conj! res m))
		 (when (and quant (= (mod i quant) 0)) 
			(.print System/err (str " " (quot i quant))))
			(recur (inc i))))
	  (persistent! res)))

(defn fetch-graph-xyz 
	"get cursor from fetch and conj with transients; 
	 TODO push quant, dismongo and keys to fetch?"
	[& args]
	; (println args)
	(let [
		  [quant args]	   (val-arg  args :progress) 
		  quant (or quant 1000000)
		  [keys args]      (val-arg  args :keys)
		  [yint args]      (bool-arg args :yint)
		  ; TODO may add :only keys to args for fetch:
		  #^DBCursor cursor (apply fetch (concat args [:as :cursor]))		  
		]
	(loop [i 0 res (transient {})]
	  (if (.hasNext cursor)
      ;; NB need to make .next a list to apply type hint
	  	(let [m (-> cursor #^ClojureDBObject (.next) .toClojure)
		  	[x y z] (map #(m %) keys)
        ;; Integer/valueOf couldn't be resolved by w-o-reflection!
		  	y (if yint (Integer/parseInt y) y) 
		  	rx  (or (res x) (if yint (sorted-map) {}))
		  	yz (assoc rx y z)
		  	]
		 (when (and quant (= (mod i quant) 0)) 
			(.print System/err (str " " (quot i quant))))
		 (recur (inc i) (assoc! res x yz)))
   (persistent! res))
   )))
	  
(defn fetch-day-triples 
	"get cursor from fetch and conj with transients; 
	 TODO push quant, dismongo and keys to fetch?"
	[& args]
	(let [
		  [quant args]	   (val-arg  args :progress) 
		  quant (or quant 1000000)
		  [keys args]      (val-arg  args :keys)
		  [yint args]      (bool-arg args :yint)
		  ; TODO may add :only keys to args for fetch:
		  #^DBCursor cursor (apply fetch (concat args [:as :cursor]))		  
		]
	(loop [i 0 res (transient {})]
	  (if (.hasNext cursor)
      ;; NB need to make .next a list to apply type hint
	  	(let [m (-> cursor #^ClojureDBObject (.next) .toClojure)
		  	[x y z] (map #(m %) keys)
        ;; Integer/valueOf couldn't be resolved by w-o-reflection!
		  	y (if yint (Integer/parseInt y) y) 
		  	rx (or (res x) (transient []))
		  	yz (conj! rx [y z])
		  	]
		 (when (and quant (= (mod i quant) 0)) 
			(.print System/err (str " " (quot i quant))))
		 (recur (inc i) (assoc! res x yz)))
		 (do
		    (.print System/err " sorting days: ")
	  	  (->> res persistent! (reduce (fn [r [k v]]
	  	    (let [vlen (count v)] 
      	    (.print System/err (str " " k)) 
      	    (assoc! r k (->> v persistent! (sort-by second >) 
      	        (map (fn [idx [user rank]] 
      	          [user (double (/ idx vlen))]) (iterate inc 0) 
      	          ))))) 
      	      (transient {}))
      	    persistent!)
	    )))))
	  
(defn sort-map
  "convert a map to a sorted map and subtract each prev val from next"
  [m & [unkeyword]]
  (let [s
    (if unkeyword
        (let [k (->> m keys (map #(Integer/parseInt (name %))))
              v (vals m)]
        (interleave k v))
        (apply concat m))]
    (apply sorted-map s)))
    
(defn fetch-graph-xy 
	"get cursor from fetch and conj with transients; 
	 TODO push quant, dismongo and keys to fetch?"
	[& args]
	; (println args)
	(let [
		  [quant args]	   (val-arg  args :progress) 
		  quant (or quant 1000000)
		  [keys args]      (val-arg  args :keys)
		  [yint args]      (bool-arg args :yint)
		  #^DBCursor cursor (apply fetch (concat args [:as :cursor]))		  
		]
	(loop [i 0 res (transient {})]
	  (if (.hasNext cursor)
	  	(let [m (-> cursor #^ClojureDBObject (.next) .toClojure)
		  	[x y] (map #(m %) keys)
        y (if yint (sort-map y :unkeyword) y)
		  	]
		 (when (and quant (= (mod i quant) 0)) 
			(.print System/err (str " " (quot i quant))))
		 (recur (inc i) (assoc! res x y)))
	  (persistent! res)))))	   
