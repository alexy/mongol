  ;; technomancy
  ;; (let [l '([0 12] [1 23] [3 65]) m (into {} l)] (reduce #(conj %1 (m %2)) [] (range (inc (first (last l))))))
  ;; hiredman  
  ;; (let [l v (vec (repeat (count l) 0)) f (fn [v [k v’]] (assoc v k v’))] (reduce f v pairs)) 
  ;; chouser initial
  ;; (let [v [[0 12] [1 23] [3 65]], m (apply max (map first v)), o (vec (repeat (inc m) 0))] (reduce (fn [o [k v]] (assoc o k v)) o v))
  ;; chouser loop
  ;; (loop [[[k v] :as i] [[0 12] [1 23] [3 65]], o []] (cond (empty? i) o (== k (count o)) (recur (next i) (conj o v)) :else (recur i (conj o 0))))
  (loop [[[k v] :as i] pairs, o []] (cond (empty? i) o (== k (count o)) (recur (next i) (conj o v)) :else (recur i (conj o 0))))
  ;; chouser fully lazy
  ;; ((fn f [n [[k v] :as i]] (lazy-seq (when i (if (== k n) (cons v (f (inc n) (next i))) (cons 0 (f (inc n) i)))))) 0 [[0 12] [1 23] [3 65]])
  ;; qbg lazy
  ;; (let [a [[0 12] [1 23] [3 65]] b (map #(- (first %1) (first %2) 1) a (cons [0] a))] (mapcat #(concat (repeat %1 0) [(second %2)]) b a))
  ;; chouser more
  ;; (let [a [[1 23] [3 65]] b (map #(- (first %1) (first %2) 1) a (cons [0] a))] (mapcat #(concat (repeat %1 0) [(second %2)]) b a))  ;  (23 0 65)
  ;; (let [a [[1 12] [2 23] [4 65]], ks (map first a), b (map - ks (cons -1 ks))] (apply concat (interleave (map #(repeat (dec %) 0) b) (map #(list (second %)) a))))  ;  (0 12 23 0 65)
  ;; qbg even more horrible