;; contiguous longest increasing subsequence

;; (use 'clojure.contrib.seq-utils)

(defn- clis-wrong
  "contiguous longest increasing subsequence"
  [s]
  (if (empty? s) nil  ; TODO or [0 0]?
    (if (= (count s) 1) (into [0] s)
     (->> s (partition 2 1) 
       (partition-by (fn [[x y]] (< x y))) 
       (map count) ((fn [v] [(reductions + 0 v) (map inc v)])) 
       (apply zipmap) (apply max-key second)))))

(defn clis  ; clis-reduce
  [s] 
  (let [[x & xs] s [r zs _] 
    (reduce (fn [[r zs z] e] 
      (if (< z e) [r (conj zs e) e] [(conj r zs) [e] e])) 
      [[] [x] x] xs)] (conj r zs)))
   
;; Meikel Brandmeyer
;; http://groups.google.com/group/clojure/browse_thread/thread/e28f2687e00f9694   
;; TODO clis-lazy doesn't generalize to clis [pred coll] 
;; by simply replacung <= with pred as clis-reduce does             
(defn clis  ; clis-lazy 
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

(defn clis-pred  
  [pred s]
  (let [[x & xs] s [r zs _] 
    (reduce (fn [[r zs z] e] 
      (if (pred z e) [r (conj zs e) e] [(conj r zs) [e] e])) 
      [[] [x] x] xs)] (conj r zs)))
                
(def clis-incr    (partial clis-pred <))
(def clis-nondecr (partial clis-pred <=))
(def clis-nonincr (partial clis-pred >=))
(def clis-decr    (partial clis-pred >))