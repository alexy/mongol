(import [org.joda.time DateTime])
(import [org.joda.time.format DateTimeFormat])
(def dtFmt (DateTimeFormat/forPattern "YYYY-MM-dd HH:mm:ss"))
(def t "2009-06-15 04:01:00")
(.parseDateTime dtFmt t)
;; (. dtFmt parseDateTime "2009-06-15 04:01:00")
;; (-> dtFmt (.parseDateTime "2009-06-15 04:01:00"))
;; turns out you don't have to do full format-parse:
(require '[clojure.contrib.str-utils2 :as s2])
(DateTime. (str (s2/replace t " " "T") "Z"))

; measuring how fast I get the collection back
(time (reduce (fn [n _] (let [n (inc n)] (when (= 0 (mod n 100000)) (print ".")) n)) 0 (take 1000000 (fetch :reps))))

(System/getProperty "java.class.path")
(add-claspath "file:///...")

(format "There's %s bottles of beer on the wall..." (clojure.contrib.str-utils2/join " " (range 100 90 -1)))
(format "There's %s bottles of beer on the wall..." (reduce #(str %1 " " %2) (range 100 90 -1)))

(ns-unmap *ns* '<<)
(refer 'commons.clojure.strint) 


(<< "hello ~(<< \"nested ~(str \\\"cemerick\\\")\")!")

(->> {:a {:b [1 2 3] :c [5 6]} :b {:c [7] :d [9 10]}} (vals) (map vals) (map count) (sort) (reverse))
(->> reps (vals) (map vals) (map count) (sort) (reverse) (take 10))

(reduce (fn [r0 [from repliers]] (concat r0 (reduce (fn [r1 [to dates]] (conj r1 [[from to] dates])) [] repliers))) [] tree)

(reduce (fn [r0 [from repliers]] (concat r0 (reduce (fn [r1 [to dates]] (conj r1 [[from to] dates])) [] repliers))) [] {:a {:b [1 2 3], :c [5 6]}, :b {:c [7], :d [9 10]}}) 
;;  => ([[:a :b] [1 2 3]] [[:a :c] [5 6]] [[:b :c] [7]] [[:b :d] [9 10]])

;; from tomoj 
(defn alex [m] (apply concat (for [[k1 v1] m] (for [[k2 v2] v1] [[k1 k2] v2]))))

(doseq [x '({:user "a" :reps {:a 1 :b 2}})] (let [{ a :user rs :reps} x] (doseq [to (keys rs)] (print to))))
(doseq [x (take 5 reps)] (let [{ from :user rs :reps} x] (doseq [to (keys rs)] (println [from (name to)]))))

(sort #(> (%1 1) (%2 1)) '(["a" 1] ["a" 2]))

;;  use sorted set or this to achieve (distinct) on an already sorted vector faster, @chouser:
(reduce #(if (= %2 (peek %1)) %1 (conj %1 %2)) [] [1 2 4 4 5 5 5 5 6 6 8])

;; durka42:
(let [v [1 5 3 3 2 4 5 5 4 5 1 2 1 2 3 5]] (map #(vector (first %) (count %)) (vals (group-by identity v))))

;; alexyk:
(let [g [1 5 3 3 2 4 5 5 4 5 1 2 1 2 3 5] gs (sort g) [v x c] (reduce (fn [[v x c] y] (if (= x y) [v x (inc c)] [(conj v [x c]) y 1])) [[] (first gs) 1] (rest gs))] (conj v [x c]))

;; devlinsf:
(let [v [1 5 3 3 2 4 5 5 4 5 1 2 1 2 3 5]] (map (juxt first count) (partition-by identity v)))


;;  sorting seqs of [string vector] by longer vectors first, alphabetic order of strings tie-breaking:
;; me
(sort (fn [[k1 v1][k2 v2]] (let [len1 (count v1) len2 (count v2)] (if (> len1 len2) true (if (< len1 len2) false (.compareTo k1 k2))))) sper-days)

;;  chouser
(sort (fn [[k1 v1][k2 v2]] (let [len1 (count v1) len2 (count v2) c1 (compare len1 len2)] (if (zero? c1) (compare k1 k2) c1))) coll) 

(sort-by (fn [[k v]] [(- (count v)) k]) coll)
(sort-by (fn [[k v]] [(- (apply + (map second v))) k]) coll)

(println "hello swank")
(use '(incanter core stats charts))

;; print local vars which are fns:
(->> (ns-interns *ns*) (filter (fn [[k v]] (.isBound v))) (map (fn [[k v]] [k (fn? (var-get v))])) (filter second) (map first))

;;  create a big map and convert it to sorted-map
(def m (->> (map (fn [x y] [x y]) (range 0 10000) (range 1 10001)) (reduce #(apply assoc! %1 %2) (transient {})) persistent!))
(def sm (apply sorted-map (apply concat m)))

;; take a sorted map, subtract previous value from each next but first, and keep it sorted map:
;; TODO are we guaranteed to get (seq sm) in the sorted-map order?
(->> (zipmap (keys m) (into [(val (first m))] (let [v (vals m)] (map - (rest v) v)))) (apply concat) (apply sorted-map))

;; finding the pattern of a star working the fans

user=> (->> (map (fn [[day reps]] [day (reps :donniesangel)]) (dreps "donniewahlberg")) (remove (comp nil? second)))
([11 1] [32 1])
user=> (->> (map (fn [[day reps]] [day (reps :donniewahlberg)]) (dreps "donniesangel")) (remove (comp nil? second)))
([18 2] [19 3] [20 1] [22 1] [23 1] [24 2] [26 2] [27 1] [28 1] [30 2] [31 2] [32 1] [33 1])
