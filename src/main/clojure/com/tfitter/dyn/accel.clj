;; TODO

;;  it looks like we don't need to convert a sparse vector into a plain one
;;  before computing nondecreasing? and grew-by-factor -- they can be implemented
;;  directly for the sparse vectors.

(defn nondecreasing-sparse?
  "is this sparse vector monotonically non-decreasing?"
  [s]
  (let [[x & xs] s [day0 num0] x] (->> xs (reduce (fn [[r prev-day prev-num] [day num]] 
    [(and r (<= prev-day day) (<= prev-num num)) day num]) [true day0 num0]) (first))))

(defn increasing-sparse?
  "is this sparse vector monotonically non-decreasing?"
  [s]
  (let [[x & xs] s [day0 num0] x] (->> xs (reduce (fn [[r prev-day prev-num] [day num]] 
    [(and r (< prev-day day) (< prev-num num)) day num]) [true day0 num0]) (first))))

(defn nonincreasing-sparse?
  "is this sparse vector monotonically non-decreasing?"
  [s]
  (let [[x & xs] s [day0 num0] x] (->> xs (reduce (fn [[r prev-day prev-num] [day num]] 
    [(and r (<= prev-day day) (>= prev-num num)) day num]) [true day0 num0]) (first))))

(defn decreasing-sparse?
  "is this sparse vector monotonically non-decreasing?"
  [s]
  (let [[x & xs] s [day0 num0] x] (->> xs (reduce (fn [[r prev-day prev-num] [day num]] 
    [(and r (< prev-day day) (> prev-num num)) day num]) [true day0 num0]) (first))))


(defn grew-by-factor-sparse?
  "see if a sparse vector has grown by a factor between the ends"
  [coll factor]
  (let [
    [_ start]  (first coll)
    [_ finish] (last coll)
    ratio (/ finish start)]    
  (>= ratio factor)))

(defn nondecreasing? 
  "is this seq monotonically non-decreasing?"
  [s]  ; TODO separate cases for empty and head/tail?
  (let [[x & xs] s] (->> xs (reduce (fn [[r x] y] [(and r (<= x y)) y]) [true x]) (first))))
  ;; (let [[x & xs] s [r _ ] (reduce (fn [[r x] y] [(and r (<= x y)) y]) [true x] xs)] r))
  
(defn grew-by-factor?
  "see if a seq grew by a factor, simply between the ends"
  [coll factor]
  (let [
    start (first coll)
    finish (last coll)
    ratio (/ finish start)]    
  (>= ratio factor)))

(defn nondecreasing-map-pairs? [m] (nondecreasing-sparse? (vec m)))
(defn increasing-map-pairs? [m]    (increasing-sparse? (vec m)))
(defn nonincreasing-map-pairs? [m] (nonincreasing-sparse? (vec m)))
(defn decreasing-map-pairs? [m]    (decreasing-sparse? (vec m)))

(defn filter-nondecr-days [m] (filter (fn [[_ v]] (nondecreasing-map-pairs? v)) m))
(defn filter-incr-days [m]    (filter (fn [[_ v]] (increasing-map-pairs? v)) m))
(defn filter-nonincr-days [m] (filter (fn [[_ v]] (nonincreasing-map-pairs? v)) m))
(defn filter-decr-days [m]    (filter (fn [[_ v]] (decreasing-map-pairs? v)) m))

(defn filter-n-days [ipr n] (filter (fn [[_ v]] (>= (count v) n)) ipr))
(defn count-n-days  [ipr n] (count (filter-n-days ipr n)))
  
;; (defn paired-to-vector
;;   "convert a sper-days vector of day-mention pairs like (([0 2986] [1 3294] [3 4897]) to [2986 3294 0 4897]"
;; [pairs])

(defn max-clis-len [s] (->> s clis-reduce (map count) (apply max)))

(defn maxxel 
  "compute maximum acceleration, taking into account only subsequences of length greater than n"
  [s & [n tough]]
  (let [
    n (or n 3)]
    (->> s clis-reduce (filter #(>= (count %) n))
    (map #(let [
      ;; usually the first element is 1, so :tough skips that
      ;; and gets acceleration as a ratio not to 1 but larger
      fst (if tough (second %) (first %))
      lst (last %)] 
      [(/ lst fst) (count %)]))
    ;; max-key chokes on [], buggy?  short-circuit with -?> may help,
    ;; http://richhickey.github.com/clojure-contrib/core-api.html#clojure.contrib.core/-?%3E
    ((fn [s] (if (empty? s) [] (apply max-key first s))))
    )))