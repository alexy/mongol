mongol.repliers=> (first davrank)
["monhae" {9 [1632028 0.0], 10 [1735904 0.0], 11 [1857852 0.0], 12 [1966427 0.0], 13 [2060628 0.0], 14 [2145751 0.0], 15 [2207893 0.0], 16 
[2273567 0.0], 17 [2348324 0.0], 18 [2428559 0.0], 19 [2510339 0.0], 20 [2586924 0.0], 21 [2657311 0.0]}]

mongol.repliers=> (davrank "justinbieber")
{0 [0 263266.0755636807], 1 [0 491511.0377241806], 2 [0 655086.6241448692], 3 [0 808762.02181987], 4 [0 934326.477967859], 5 [0 1025713.125
99319], 6 [0 1062641.268433735], 7 [0 1138209.139042357], 8 [0 1191615.13760218], 9 [0 1258979.212583995], 10 [0 1359705.259163987], 11 [0 
1387844.34566075], 12 [0 1452082.514242879], 13 [0 1594333.653669725], 14 [0 1623814.878088962], 15 [0 1452615.812609457], 16 [0 1703495.28
1141267], 17 [0 1626026.219803371], 18 [0 1688772.0087116], 19 [0 1857144.178266178], 20 [0 1754914.971929825], 21 [0 1728743.555165144]}

mongol.repliers=> (davrank "amandademarest")
{0 [2 150976.4285714286], 1 [1 228358.7142857143], 2 [1 22538.0], 3 [1 449385.7058823529], 4 [1 407453.6363636364], 5 [1 645744.8333333333]
, 6 [1 961703.625], 7 [1 560723.4117647059], 8 [1 695214.3714285714], 9 [1 739571.5], 10 [1 760557.0833333333], 11 [1 1367276.0], 12 [1 990
359.7272727273], 13 [1 808740.375], 14 [2 0.0], 15 [2 2471406.0], 16 [2 151694.6666666667], 17 [2 1253061.333333333], 18 [2 505820.0], 19 [
1 501022.8], 20 [1 440670.6], 21 [1 1250533.5]}

mongol.repliers=> (davrank "aplusk")
{0 [46 277962.9725274725], 1 [66 563834.4344262294], 2 [63 740642.4594594595], 3 [80 979041.0147058824], 4 [73 1065507.614173228], 5 [72 12
25676.575342466], 6 [62 1273207.726256983], 7 [60 1411289.163461538], 8 [56 1349962.234848485], 9 [53 1365560.231343284], 10 [51 1731207.83
908046], 11 [44 1629891.331210191], 12 [44 1648931.413580247], 13 [42 1750803.653061224], 14 [45 1865727.965116279], 15 [47 1829613.6875], 
16 [50 1896691.368852459], 17 [51 1856827.862385321], 18 [53 2125279.245098039], 19 [53 2075361.325581395], 20 [55 2110556.099099099], 21 [
52 2244985.605555556]}

mongol.repliers=> (davrank "dpp")
{3 [648949 396647.0], 4 [888371 0.0], 5 [978474 0.0], 6 [456329 18972.0], 7 [589739 0.0], 8 [637299 0.0], 9 [734010 0.0], 10 [803623 0.0], 
11 [869094 0.0], 12 [875894 0.0], 13 [836738 0.0], 14 [930399 0.0], 15 [971442 0.0], 16 [988403 0.0], 17 [1048402 0.0], 18 [1120804 0.0], 1
9 [1114090 0.0], 20 [1151773 0.0], 21 [1166235 0.0]}

;; dr-star-rank
justinbieber =>
,(0.6197258124125861,0.5575966354511003,0.5424052148626077,0.5383237915056115,0.5221303226325452,0.537596734779112,0.4977620469594651,0.499020072285723,0.4478677241839154,0.48043019888755745,0.4674486889052287,0.47159988451518087,0.4796893840721758,0.5037635546256732,0.5083166931722443,0.3929166623356133,0.48363617115023266,0.4406010830691825,0.44937061155687985,0.5047682972129569,0.44991253022691446,0.4219551034008907)

;; stored as drclen, this is wmenstar, average real rank weighted by the number of tweets from each person per day, segmented by clis-decr,
;; with the maximum run taken

(def drank-clis-decr (clis-drank re-days :roll? :maxlen))

user=> (take 10 drank-clen-desc)
(["muse" 22] ["theellenshow" 22] ["nytimes" 22] ["ebertchicago" 21] ["kpereira" 21] ["mileycyrusnet" 21] ["mikeexpo" 21] ["mariliaruiz" 21] ["taberna,de_moe" 21] ["kekeinaction" 21])

(def dwrank-clis-incr (clis-drank re-days :maxlen clis-incr))
(def dwrank-clen-desc (sort-by second > drank-clis-incr))

(def dirank-clis-decr (clis-drank drank :maxlen))
(def dirank-clen-desc (sort-by second > drank-clis-decr))

user=> (take 10 dirank-clen-desc)
(["fredgol9" 17] ["rockstargames" 17] ["nihon_bot" 17] ["mileycyrusnet" 15] ["chetan_bhagat" 15] ["puku_puku" 15] ["jbieber_fever24" 14] ["flpr" 14] ["yumehina" 14] ["estoniabot" 14])

mongol.repliers=> (time (def wmenstar-clen-decr (clis-drank wmenstar :roll? :maxlen :pair? :second)))
mongol.repliers=> (take 10 wmenstar-clen-decr)
(["donniewahlberg" 11] ["camellia" 11] ["dubhghall" 10] ["bowwow614" 9] ["faydra_deon" 9] ["cooku" 9] ["extremely_juicy" 9] ["ares3d" 9] ["eddiegrayy" 9] ["itsue_1a" 9])
mongol.repliers=> (time (def wmenstar-clen-incr (clis-drank wmenstar :roll? :maxlen :pair? :second :clis clis-incr)))
mongol.repliers=> (take 10 wmenstar-clen-incr)
(["gm_web" 18] ["dealstobuy" 17] ["emanuelrizon" 17] ["tonyageh" 16] ["alexanderfog" 16] ["sherryonline4u" 16] ["nichopoulouzo" 15] ["heyysusmitaa" 15] ["jornal_record" 15] ["abolapt" 15])

;; NB histogram the above clens!

mongol.repliers=> (time (def wrank-ratio (clis-drank wmenstar :ratio :first)))
mongol.repliers=> (take 10 wrank-ratio)
(["onhae" 1979.9020917528683] ["kitosrd" 605.9666708835339] ["lins_a15" 560.6572408616712] ["andykanefield" 501.50261643382146] ["leehteixeira_" 439.7380246000087] ["be3n" 430.0771838855304] ["nadiasoma" 391.0394456649083] ["mrrichyoung" 376.59337117260253] ["abqtupperware" 376.2549033315411] ["fitl7" 357.9012994498925])

mongol.repliers=> (time (def wstar-ratio (clis-drank wmenstar :ratio :second)))
mongol.repliers=> (take 10 wstar-ratio)
(["jaq24" 156100.55983485506] ["gabiassmann" 46646.22994317674] ["queenmalikab" 32822.96963683567] ["mrkonnectionz" 31127.375024095196] ["benghele" 14421.798275840765] ["lala_noleto" 14153.193785027517] ["chollosocksgirl" 12262.767699055545] ["jadehot" 9449.162285699633] ["wesbuleriano" 8685.0583907638] ["mannygforever" 8420.673951938441])

;; acceleration by own rank:

(time (def wrank-axel-decr-3 (clis-drank wmenstar :pair? :first :roll? :maxxel :invert? true)))
mongol.repliers=> (take 10 wrank-axel-decr-3)
(["polvalente" [134300.74642867612 6]] ["leobarcellos" [112492.16831570824 4]] ["natyperdomo" [79258.4669359597 4]] ["theresamcardle" [63282.167932528355 6]] ["ricardo_tadeu" [41208.87105010808 3]] ["alfiehitchcock" [37199.56096834167 10]] ["jc_schuster" [30994.582739730944 4]] ["carlaciccarelli" [30102.283250492634 3]] ["drosa_shannon" [19813.550760068076 4]] ["carolbastos_" [17159.656485898726 8]])

mongol.repliers=> (time (def wrank-axel-decr-4 (clis-drank wmenstar :pair? :first :roll? :maxxel :invert? true :minsublen 4)))
mongol.repliers=> (take 10 wrank-axel-decr-4)
(["polvalente" [134300.74642867612 6]] ["leobarcellos" [112492.16831570824 4]] ["natyperdomo" [79258.4669359597 4]] ["theresamcardle" [63282.167932528355 6]] ["alfiehitchcock" [37199.56096834167 10]] ["jc_schuster" [30994.582739730944 4]] ["drosa_shannon" [19813.550760068076 4]] ["carolbastos_" [17159.656485898726 8]] ["bunito07" [12882.86935051968 4]] ["dollbabyv" [10800.683853302524 4]])

;; acceleration by star-rank, with maxxel before drop-while zero?, simply dropping 0-started subseqs:
mongol.repliers=> (time (def wstar-axel-decr-3 (clis-drank wmenstar :pair? :second :roll? :maxxel :invert? true)))
mongol.repliers=> (take 10 wrank-axel-decr-3)
(["joycepascowitch" [207773.43624757472 4]] ["biia_assis" [185685.482651769 3]] ["minni_w" [152842.83805066883 3]] ["biofa" [150704.61084932814 3]] ["marcelopanizza" [132775.71140550353 4]] ["janacavalcanti" [121893.20966984319 4]] ["baybgyrl88" [61659.870613647676 3]] ["iluvddubsomuch" [57383.41730255576 3]] ["pinkywainer" [36403.60614784597 3]] ["ontddubtrouble" [31056.519233674186 3]])

mongol.repliers=> (time (def wstar-axel-decr-4 (clis-drank wmenstar :pair? :second :roll? :maxxel :invert? true :minsublen 4)))
mongol.repliers=> (take 10 wstar-axel-decr-4)
(["joycepascowitch" [207773.43624757472 4]] ["marcelopanizza" [132775.71140550353 4]] ["janacavalcanti" [121893.20966984319 4]] ["rafa_consentino" [24387.422741755676 5]] ["mynadias" [18857.779187010525 4]] ["dejdia" [14122.537871391269 4]] ["lisa_nova" [13425.948168109135 4]] ["stephjonesmusic" [12126.725604089632 5]] ["brunaodog" [9499.95515321749 4]] ["kiruba" [9097.672669009748 7]])

NB: do majority of wrank/wstars grow or fall?  metric: total number of growth days vs fall days.
soft measure: simple majority
harder: 2/3 either way, now with neutral bucket
hardest: out of those growing, how many grew more than twice?

;; plain growfall, without twice-wider
(time (def drrank-grow (clis-drank drrank :roll? :growfall)))
user=> (take 10 drrank-grow)
(["onhae" 1979.9020917528683] ["kitosrd" 605.9666708835339] ["lins_a15" 560.6572408616712] ["andykanefield" 501.50261643382146] ["leehteixeira_" 439.7380246000087] ["be3n" 430.0771838855304] ["nadiasoma" 391.0394456649083] ["mrrichyoung" 376.59337117260253] ["abqtupperware" 376.2549033315411] ["fitl7" 357.9012994498925])

(def drrank-grow (clis-drank drrank :roll? :growfall :twice-wider? true))
user=> (take 10 drrank-grow)
(["onhae" 1979.9020917528683] ["kitosrd" 605.9666708835339] ["lins_a15" 560.6572408616712] ["andykanefield" 501.50261643382146] ["leehteixeira_" 439.7380246000087] ["be3n" 430.0771838855304] ["nadiasoma" 391.0394456649083] ["mrrichyoung" 376.59337117260253] ["abqtupperware" 376.2549033315411] ["fitl7" 357.9012994498925])