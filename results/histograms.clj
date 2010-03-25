;; decreasing integer rank
 rrr-hist
(2984 1023 392 158 85 52 30 15 8 5 2 0 0 0 0 0 0 0 0)

;; non-increasing integer rank -- with justin bieber
 nrr-hist
(2985 1024 393 159 86 53 31 16 9 6 3 1 1 1 1 1 1 1 1)

;; increasing number of mentions
 inm-hist
(2002 102 6 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0)

;; nondecreasing number of mentions
 nnm-hist
(333355 160407 77648 36657 16797 7293 2985 1145 438 133 40 7 2 1 0 0 0 0 0)

;; numner of total users with pageranks per day
user=> (map #(count (second %)) pr-days)
(396594 789937 1065252 1358559 1591226 1765756 1936809 2097928 2222221 2341354 2478528 2629111 2767989 2892705 3003975 3087760 3176088 3275556 3380727 3487572 3589058 3681171)

;; histogram of the number of people with that maximum increasing integer rank seqauence length
(def dirank-clen-decr-hist (frequencies (map second dirank-clen-desc)))
mongol.repliers=> dirank-clen-decr-hist
{1 1780244, 2 1156329, 3 498463, 4 166527, 5 52908, 6 17477, 7 5856, 8 2016, 9 823, 10 304, 11 126, 12 61, 13 22, 14 9, 15 3, 17 3}

;; drrank-growfall

3681171 total
2.5M non-neutral