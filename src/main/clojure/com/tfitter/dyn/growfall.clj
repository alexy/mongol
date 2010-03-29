(defn twice-more? [x y & [invert?]]
  (if invert?
    (>= y (* 2 x))
    (>= x (* 2 y))
  ))

(defn twice-more-seq? [s & [invert?]]
  (when (seq s)
    (let [fst (first s)
          lst (last s)]
    (twice-more? lst fst invert?))))

(defn change-ratio [s & [invert?]]
  (when (seq s)
    (let [fst (first s)
          lst (last s)
          [numer denom] (if invert?  [fst lst] [lst fst])
          denom (if (zero? denom) (inc denom) denom)
          ratio (/ numer denom)
        ]
        (if invert? (- ratio) ratio))))

(defn growfall
  "compute whether a sequence is growing or declining"
  [s & [twice-wider? twice-higher?]]
  (let [[x & xs] s 
    [up down _] (reduce (fn [[up down prev] curr]
    (if (< prev curr) 
      [(inc up) down curr]
      [up (inc down) curr])
    ) [0 0 x] xs)]
    (cond 
      (>= up (if twice-wider? (* down 2) down))
        (if (or (not twice-higher?) (twice-more-seq? s)) 
          (change-ratio s) 0.0)
      (>= down (if twice-wider? (* up 2) up))
        (if (or (not twice-higher?) (twice-more-seq? s :invert)) 
          (change-ratio s :invert) 0.0)
      :else
       nil
      )  
  ))