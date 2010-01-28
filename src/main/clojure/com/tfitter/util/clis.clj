;; contiguous longest increasing subsequence

(use 'clojure.contrib.seq-utils)


(defn clis-partition-wrong
  "contiguous longest increasing subsequence"
  [s]
  (if (empty? s) nil  ; TODO or [0 0]?
    (if (= (count s) 1) (into [0] s)
     (->> s (partition 2 1) 
       (partition-by (fn [[x y]] (< x y))) 
       (map count) ((fn [v] [(reductions + 0 v) (map inc v)])) 
       (apply zipmap) (apply max-key second)))))

(defn clis-reduce [s] 
  (let [[x & xs] s [r zs _] 
    (reduce (fn [[r zs z] e] 
      (if (< z e) [r (conj zs e) e] [(conj r zs) [e] e])) 
      [[] [x] x] xs)] (conj r zs)))
   
;; Meikel Brandmeyer
;; http://groups.google.com/group/clojure/browse_thread/thread/e28f2687e00f9694   
(defn clis-lazy 
         [coll] 
         (letfn [(step 
                   [prev s] 
                   (let [fst (first s) 
                         lst (peek prev)] 
                     (if (or (nil? s) (and lst (<= fst lst))) 
                       (cons prev (clis s)) 
                       (recur (conj prev fst) (next s)))))] 
           (lazy-seq 
             (when-let [s (seq coll)] 
               (step [] s))))) 