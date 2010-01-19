;; A series of merge exercises from chouser
(defn sorted-seq-merge
  "Lazily merge two sorted seqs into a single sorted seq"
  [a b]
  (lazy-seq
    (cond
      (empty? a) b
      (empty? b) a
      (< (first a) (first b)) (cons (first a) (sorted-seq-merge (rest a) b))
      :else                   (cons (first b) (sorted-seq-merge a (rest b))))))

;; TODO can generalize from above to a function with a sorting predicate
;; which defaults to <
(defn sorted-date-seq-merge
  "Lazily merge two sorted seqs into a single sorted seq"
  [a b]
  (lazy-seq
    (cond
      (empty? a) b
      (empty? b) a
      (< (.compareTo (first a) (first b)) 0) (cons (first a) (sorted-date-seq-merge (rest a) b))
      :else                   (cons (first b) (sorted-date-seq-merge a (rest b))))))
      
;; from chouser with predicate:
;; http://paste.lisp.org/display/93006#2
(defn sorted-seq-merge
  "Lazily merge two sorted seqs into a single sorted seq."
  ([a b] (sorted-seq-merge < a b))
  ([c a b]
   (lazy-seq
     (cond
       (empty? a) b
       (empty? b) a
       (neg? (.compare c (first a) (first b)))
           (cons (first a) (sorted-seq-merge c (rest a) b))
       :else (cons (first b) (sorted-seq-merge c a (rest b)))))))

;; user=> (sorted-seq-merge compare ["a" "c" "d" "g"] ["b" "e" "f"])
;; ("a" "b" "c" "d" "e" "f" "g")
;; user=> (sorted-seq-merge [1 3 4 5 8 9] [2 4 6 7 10])
;; (1 2 3 4 4 5 6 7 8 9 10)
;; user=> (sorted-seq-merge > [10 7 6 4 2] [9 8 5 4 3 1])
;; (10 9 8 7 6 5 4 4 3 2 1)
