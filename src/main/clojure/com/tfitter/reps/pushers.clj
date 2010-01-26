;; for each guy, how many fans he has  
(def sper-lens [sper] (map (fn [[k v]] [k (count v)]) sper))
(def sper-lens-desc [sper] (sort-by second > (sper-lens sper))

(def sper-sorted (sort (fn [[_ as] [_ bs]] (> (count a) (count bs))) (seq sper)))  ;  untested!

(;; day-number moved to days.clj

(use '[clojure.contrib.seq-utils :only (partition-by frequencies)])
 
(def sper-days 
  ;; "fold sper mentions into number of them by day"
  ;; [sper]
  (map (fn [[user reps]] 
  (let [days (->> reps (map second) (apply concat) (map #(day-number %)) 
    ;;  (into (sorted-set)) -- this only kept the days, without per day counts
    ;;  (sort) (partition-by identity) (map (juxt first count)) -- works, and
    (frequencies) (sort)   ; -- this is even clearer
    )] 
    [user days])) sper))

;; (def sper-days (sper-days-f sper))

(def sper-days-sorted
  (sort-by (fn [[k v]] [(- (apply + (map second v))) k]) sper-days))

(def sper-days-sorted (sper-days-sorted ))
;; had to decrease block size from 10000 to 1000 to avoid "too large" error from mongo,
;; also suppressed progress by block then  

(time 
  (doseq [x (partition-all 1000 
    (map (fn [[k v]] {:user k :days (seq v)}) 
      sper-days-sorted))] 
    ;; (.print System/err ".")
    (mass-insert! :sperdays x)))
(.println System/err)

(def mentioned (->> sper-days (map (fn [[user days]] [user (apply + (map second days))]))))
(def upm (->> mentioned (map (fn [[user ments]] [user ments (pgmap user)])) (filter #(% 2))))
(defn third [x] (x 2))
(def xy { :x (map second upm) :y (map third upm) })

(time 
  (doseq [x (partition-all 10000 
    (map (fn [[user ments score]] { :user user :ments ments :score score }) upm))] 
    (.print System/err ".")
    (mass-insert! :prankperments x)))

;; NB make this a macro or something, for saving to mongo -- ask on #clojure:
(time (doseq [x (partition-all 10000 (map (fn [[k v]] {:user k :mentioned v}) mentioned))] 
  (.print System/err ".") (mass-insert! :mentioned x)))

(use '[incanter core stats charts])

(def pm (sort (map (fn [{score :score num :ments}] [num score]) (fetch :prankperments))))
(def xy (->> pm (partition-all 1000) 
  (map #(reduce (fn [[xs ys] [x y]] [(conj xs x) (conj ys y)]) [[][]] %))
  (map (fn [[xs ys]] [(median xs) (median ys)]))))  ; mean or median

(view (scatter-plot (map first xy) (map second xy) :title "pagerank vs mentions, medians" :x-label "number of mentions" :y-label "pagerank"))

;; take-last
;; devlinsf:
(defn take-last [n coll] (drop (- (count coll) n) coll))
;;  
((comp first drop-while) (comp (partial not= 6) count) (iterate rest (range 1000)))

;; TODO index sper
(def jb (fetch-one :sper :where { :user "justinbieber" }))
(def jbments (jb :sper))
(def jbms (sort-by (fn [[k v]] [(- (count v)) k]) jbments))
(take 100 (map first jbms))
;; TODO compute Zipfian pagerank rank, cross-tabulate for top mentioners
(def jbmc (map (fn [[k v]] [k (count v)]) jbms))

;; NB pr is predefined, so we call it pg
(def pg (map #(dissoc % :_ns :_id) (fetch :pagerank)))
(def pgpos (map vector (map #(:user %) pg) (iterate inc 0)))
  
(mongo-store-pairs pgpos :pgpos :user :pgpos 10000)

;; show top fans with their pagerank pos
(def fans100 (take 100 (map first jbms)))
(time (def mpgpos (reduce (fn [m [k v]] (assoc m k v)) {} pgpos)))
(map #(mpgpos (name %)) fans100)
(def fanpos (sort-by second (map (fn [fan] [fan (mpgpos (name fan))]) (map first jbms))))

(->> (reps "AmandaDemarest") (keys) (map (fn [rep] (let [biebcount (count ((or (reps (name rep)) {}) :justinbieber))] [rep biebcount]))))

(defn man-reps
  "given a replier graph and a name, return a list of that person's repliers" 
  [reps man]
  (map first (reps man))

(->> jbmc (map first) )

;; lowercase reps and test
(time (def froms (map #(% :user) (fetch :reps :only [:user]))))
(def fromset (into #{} (map #(.toLowerCase %) froms)))
(def frommap (reduce (fn [dups from] (let [from-lc (.toLowerCase from)] (update-in dups [from-lc] #(conj (or % []) from)))) {} froms))
(def dups (filter (fn [[k v]] (> (count v) 1)) frommap))

(time (def creps (get-lc-reps :reps)))
(time (mongo-store-pairs creps :creps :user :reps 10000))

;;  count the number of leaves in creps, to compare with...
(time (reduce (fn [n1 [_ reps]] (reduce (fn [n2 [_ array]] (+ n2 (count array))) n1 reps)) 0 creps))
;; ..the number of leaves in reps
(let [reps (fetch :reps :only [:reps])]
(time (reduce (fn [n1 {reps :reps}] (reduce (fn [n2 [_ array]] (+ n2 (count array))) n1 reps)) 0 reps)))
