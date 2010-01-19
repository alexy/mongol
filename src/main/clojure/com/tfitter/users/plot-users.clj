(ns mongol.repliers
  (:use [somnium.congomongo]
      [incanter core stats charts]
      [clojure.contrib.seq-utils :only [partition-all]]))
      
(mongo! :db "twitter")

(def utwits (map #(% 1) iusers))
(view (histogram utwits))

(view (histogram (filter #(> % 1000) utwits)))
(count (filter #(> % 1000) utwits))

;; (reduce (fn [res {user :user score :score}] (assoc res user score)) {} (take 3 pagerank))
;; (reduce (fn [res {user :user score :score}] (assoc res user score)) {} pagerank)
(time (def pgmap (reduce (fn [res {user :user score :score}] (assoc res user score)) {} (fetch :pagerank :only [:user :score]))))
;; NB remove is filter-not

(def users (map #(dissoc % :_ns :_id) (fetch :usernumtwits)))

(def upn (->> users (map (fn [{user :user numtwits :numtwits}] [user numtwits (pgmap user)])) (filter #(% 2))))
(defn third [x] (x 2))
(def xy { :x (map second upn) :y (map third upn) })

(time 
  (doseq [x (partition-all 10000 
    (map (fn [[user numtwits score]] { :user user :numtwits numtwits :score score }) upn))] 
    (.print System/err ".")
    (mass-insert! :prankpertwits x)))

;; reading back    
;; NB how can we sort first by first, then by second?
;; was )sort-by first ...); but (sort ...) seems to do lexcographic sort we're after right away
(def pn (sort (map (fn [{score :score num :numtwits}] [num score]) (fetch :prankpertwits))))

;; NB this works:
((fn [[xs ys]] [(mean xs) (mean ys)]) (->> '([1 2][2 3][5 4]) (reduce (fn [[xs ys] [x y]] [(conj xs x) (conj ys y)]) [[][]])))
;; and this too now:
(->> '([1 2][2 3][5 4]) (reduce (fn [[xs ys] [x y]] [(conj xs x) (conj ys y)]) [[][]]) (apply (fn [xs ys] [(mean xs) (mean ys)])))  ; as apply de-seq's, fn accepts [xs ys] instead of [[xs ys]]!

(->> '([1 2][1 3][2 1][2 2]) (partition-all 2) 
  (map #(reduce (fn [[xs ys] [x y]] [(conj xs x) (conj ys y)]) [[][]] %))
  (map (fn [[xs ys]] [(mean xs) (mean ys)])))

(def xy (->> pn (partition-all 1000) 
  (map #(reduce (fn [[xs ys] [x y]] [(conj xs x) (conj ys y)]) [[][]] %))
  (map (fn [[xs ys]] [(mean xs) (mean ys)]))))

  
(view (scatter-plot (map first xy) (map second xy) :x-label "number of twits" :y-label "pagerank"))

(def xy (->> pn (partition-all 1000) 
  (map #(reduce (fn [[xs ys] [x y]] [(conj xs x) (conj ys y)]) [[][]] %))
  (map (fn [[xs ys]] [(median xs) (median ys)]))))
(view (scatter-plot (map first xy) (map second xy) :x-label "number of twits" :y-label "pagerank, medians"))
