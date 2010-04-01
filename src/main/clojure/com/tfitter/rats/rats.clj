user=> (load-file "meat/bdb/parsuc.clj")                                                                                                                        
#'user/put-db
user=> (time (do (def je (je/db-env-open "je")) (def reps (agents-get-db 8 je "reps" 10000)) (def westar (agents-get-db 8 je "westar" 10000))))                 

user=> (load-file "meat/reps/graph-invert.clj")                                                                                                                 
(def sper (invert-graph-braver reps))

(defn mean [s] 
  (let [n (count s) sum (apply + s)] 
    (if (zero? n) 0. (/ sum n))))

(defn ratio [numer denom] 
  (when (and numer denom) (if (zero? denom) nil (/ numer denom))))

(defn ratrange [user] 
  (let [rats (->> user westar sort (map second) 
      (map (fn [[x y]] (when (> y 0) (/ x y)))) (remove nil?))] 
    (if (empty? rats) [nil nil]
      [(apply min rats) (apply max rats)])))

(defn rats [user] 
  (let [[user-min-rat user-max-rat] (ratrange user) 
    fans (keys (sper (keyword user))) 
    fans-rats (map ratrange fans) 
    fans-min-rats (remove nil? (map first fans-rats)) 
    fans-max-rats (remove nil? (map second fans-rats)) 
    fans-avg-min-rats (mean fans-min-rats) 
    fans-avg-max-rats (mean fans-max-rats) 
    min-rat (ratio user-min-rat fans-avg-min-rats) 
    max-rat (ratio user-max-rat fans-avg-max-rats)] 
  [min-rat max-rat]))

(def all-rats (map rats users))
;; user=> (time (count all-rats))
;; "Elapsed time: 2046178.233 msecs"

user=> (def all-urats (map vector users all-rats))
user=> (def urats (filter (fn [[_ [x y]]] (and x y)) all-urats))
user=> (def srats (sort-by second urats))

user=> (take 20 srats)
(["justinbieber" [0.0 0.0]] ["pefabiodemelo" [7.520695990735984E-8 2.0085945670071234E-5]] ["realwbonner" [8.841138098631045E-8 3.5828688245905438E-6]] ["amanda
demarest" [8.89194537498387E-8 8.263647846974859E-7]] ["stephenfry" [1.5168448661200297E-7 1.3622762465322292E-6]] ["ogochocinco" [1.7852943608169626E-7 2.79369
6188556656E-6]] ["jordanknight" [2.0130296455640595E-7 3.6050174136265366E-5]] ["ddlovato" [3.079321523720649E-7 2.4278326458823945E-6]] ["neto" [3.117056236488
842E-7 1.3272659531469162E-4]] ["donniewahlberg" [3.2066143679433356E-7 3.2272559177183037E-7]] ["106andpark" [3.256874364091013E-7 2.4489627903203585E-5]] ["mr
peterandre" [3.343199726526029E-7 9.480464595998765E-6]] ["eduardosurita" [3.7484459633515906E-7 1.750930861648556E-6]] ["huckluciano" [3.8472273835709136E-7 1.
294134662858683E-5]] ["danilogentili" [4.116966600935834E-7 7.648913294316696E-6]] ["rubarrichello" [4.6592264166375516E-7 1.1876283913521464E-5]] ["jonasbrothe
rs" [5.491312716731033E-7 5.95399371825372E-6]] ["calle13oficial" [6.869761125243835E-7 2.8316242222151757E-6]] ["kevinhart4real" [7.836693787960004E-7 5.091085
7018293545E-6]] ["lilduval" [7.87542570145998E-7 4.6516739349026106E-6]])

user=> (def srats1 (map (fn [[user [x _]]] [user x]) srats))
#'user/srats1
user=> (take 20 srats1)
(["justinbieber" 0.0] ["pefabiodemelo" 7.520695990735984E-8] ["realwbonner" 8.841138098631045E-8] ["amandademarest" 8.89194537498387E-8] ["stephenfry" 1.5168448
661200297E-7] ["ogochocinco" 1.7852943608169626E-7] ["jordanknight" 2.0130296455640595E-7] ["ddlovato" 3.079321523720649E-7] ["neto" 3.117056236488842E-7] ["don
niewahlberg" 3.2066143679433356E-7] ["106andpark" 3.256874364091013E-7] ["mrpeterandre" 3.343199726526029E-7] ["eduardosurita" 3.7484459633515906E-7] ["huckluci
ano" 3.8472273835709136E-7] ["danilogentili" 4.116966600935834E-7] ["rubarrichello" 4.6592264166375516E-7] ["jonasbrothers" 5.491312716731033E-7] ["calle13ofici
al" 6.869761125243835E-7] ["kevinhart4real" 7.836693787960004E-7] ["lilduval" 7.87542570145998E-7])
user=> (def srats2 (sort-by second (map (fn [[user [_ y]]] [user y]) srats)))
(["justinbieber" 0.0] ["donniewahlberg" 3.2272559177183037E-7] ["amandademarest" 8.263647846974859E-7] ["tommcfly" 1.1443065366566492E-6] ["caiquenogueira" 1.20
77429740337216E-6] ["stephenfry" 1.3622762465322292E-6] ["copilotowbonner" 1.5345753278335057E-6] ["johncmayer" 1.5433468252395666E-6] ["thiagobanik" 1.62316983
51676762E-6] ["eduardosurita" 1.750930861648556E-6] ["ddlovato" 2.4278326458823945E-6] ["marcoluque" 2.4495273363268633E-6] ["hugogloss" 2.730598316962178E-6] [
"ogochocinco" 2.793696188556656E-6] ["calle13oficial" 2.8316242222151757E-6] ["juanestwiter" 2.863472860231952E-6] ["realwbonner" 3.5828688245905438E-6] ["jonat
hanrknight" 4.425052147440693E-6] ["katyperry" 4.521231771357091E-6] ["tomfelton" 4.549517108034358E-6])

user=> (def midrats1 (filter (fn [[_ x]] (and (< 0.5 x) (< x 2))) srats1))
user=> (take 20 midrats1)
(["angelicarabang" 0.5000000241766961] ["deaaida" 0.5000002927341994] ["roq_q" 0.5000004069405791] ["makemewine" 0.5000030336923323] ["afadingvoice" 0.5000030750884051] ["cleberparadela" 0.5000047050929396] ["samanthatolbert" 0.5000051499324891] ["quellcarter" 0.5000055997793885] ["anaeliaspaes" 0.500006334784607] ["finsterdexter" 0.5000103690068266] ["strivingwife" 0.5000149306819744] ["ritarocksout" 0.5000155677209547] ["flamingneck" 0.5000159469261254] ["millky" 0.5000164576444097] ["glendacide" 0.5000211451213006] ["r3llyr3llz" 0.5000216899686585] ["oceanob" 0.5000250426894263] ["kateincognito" 0.5000251525185965] ["ricardohdz" 0.5000266372017758] ["ohshessbadd" 0.5000267949015103])
user=> (count midrats1)
649270
user=> (def rats1-2-10 (filter (fn [[_ x]] (and (<= 2 x) (< x 10))) srats1)) 
#'user/rats1-2-10
user=> (count rats1-2-10)
486531
user=> (def rats1-10-100 (filter (fn [[_ x]] (and (<= 10 x) (< x 100))) srats1)) 
#'user/rats1-10-100
user=> (count rats1-10-100)
295595
user=> (def rats1-0-01-0-5 (filter (fn [[_ x]] (and (<= 0.01 x) (< x 0.5))) srats1)) 
#'user/rats1-0-01-0-5
user=> (count rats1-0-01-0-5)
626711
user=> (count (filter (fn [[_ x]] (< x 0.1)) srats1))
236990
user=> (count (filter (fn [[_ x]] (< x 0.01)) srats1))
51130
user=> (count (filter (fn [[_ x]] (< x 0.001)) srats1))
9682
user=> (count (filter (fn [[_ x]] (< x 0.0001)) srats1))
1640
user=> (count (filter (fn [[_ x]] (< x 0.00001)) srats1))
219
user=> (count (filter (fn [[_ x]] (< x 0.000001)) srats1))
25
user=> (count (filter (fn [[_ x]] (>= x 100)) srats1))
164380
user=> (count (filter (fn [[_ x]] (< 0.1 x)) srats1))
2036627
user=> (->> srats1 (filter (fn [[_ x]] (< x 0.00001))) (map first) (map #(->> % keyword sper count)) mean float)
2398.8677
user=> (->> srats1 (filter (fn [[_ x]] (< x 0.0001))) (map first) (map #(->> % keyword sper count)) mean float)
687.4561
user=> (->> srats1 (filter (fn [[_ x]] (< x 0.001))) (map first) (map #(->> % keyword sper count)) mean float)
200.71515
user=> (->> srats1 (filter (fn [[_ x]] (< x 0.01))) (map first) (map #(->> % keyword sper count)) mean float)
62.71946
user=> (->> srats1 (filter (fn [[_ x]] (< x 0.1))) (map first) (map #(->> % keyword sper count)) mean float)
24.628601
user=> (->> srats1 (filter (fn [[_ x]] (and (<= 0.1 x) (< x 0.5)))) (map first) (map #(->> % keyword sper count)) mean float)
7.2569985
user=> (->> srats1 (filter (fn [[_ x]] (and (<= 0.5 x) (< x 2.0)))) (map first) (map #(->> % keyword sper count)) mean float)
3.971086
user=> (time (->> srats1 (filter (fn [[_ x]] (and (<= 2 x) (< x 10)))) (map first) (map #(->> % keyword sper count)) mean float))
"Elapsed time: 168625.406 msecs"
2.7797058
user=> (time (->> srats1 (filter (fn [[_ x]] (and (<= 10 x) (< x 100)))) (map first) (map #(->> % keyword sper count)) mean float))
"Elapsed time: 94828.29 msecs"
2.0138297
user=> (time (->> srats1 (filter (fn [[_ x]] (> x 100))) (map first) (map #(->> % keyword sper count)) mean float))
"Elapsed time: 50389.957 msecs"
1.4713712

(def rats (->> westar 
  (map (fn [[user _]] [user (first (ratrange user))])) 
  (remove (comp nil? second) 
  (sort-by second))))

user=> (count (filter (fn [[_ x]] (< x 0.1)) rats))
177792
user=> (count (filter (fn [[_ x]] (< x 0.01)) rats))
19710
user=> (count (filter (fn [[_ x]] (< x 0.001)) rats))
2287
user=> (count (filter (fn [[_ x]] (< x 0.0001)) rats))
246
user=> (count (filter (fn [[_ x]] (< x 0.00001)) rats))
27
user=> (count (filter (fn [[_ x]] (< x 0.000001)) rats))
3
user=> (count (filter (fn [[_ x]] (and (<= 0.1 x) (< x 0.5))) rats))
945749
user=> (count (filter (fn [[_ x]] (and (<= 0.5 x) (< x 2.0))) rats))
1034084
user=> (count (filter (fn [[_ x]] (and (<= 2 x) (< x 10))) rats))
366015
user=> (count (filter (fn [[_ x]] (and (<= 10 x) (< x 100))) rats))
115514
user=> (count (filter (fn [[_ x]] (<= 100 x)) rats))
23625

(defn sr-range [user] (let [starranks (->> user westar (map second) (map first) (remove nil?))] (if (empty? starranks) [nil nil] [(apply min starranks) (apply max starranks) (mean starranks)])))

