user=> (use '[cupboard.bdb.je :as je])
nil
user=> (def je (je/db-env-open "je"))
#'user/je
user=> (load-file "meat/bdb/parsuc.clj")
#'user/put-db
user=> (time (def reps (agents-get-db 8 je "reps" 10000)))
user=> (count reps)
3204537
user=> (load-file "meat/reps/graph-invert.clj")
#'user/invert-graph-jonsmith
user=> (time (def sper (invert-graph-braver reps)))
"Elapsed time: 240025.102 msecs"
#'user/sper
user=> (count sper)
3425300
user=> (def dw-sper (sper "donniewahlberg"))
#'user/dw-sper
user=> (count dw-sper)
0
user=> (def dw-sper (sper :donniewahlberg))
#'user/dw-sper
user=> (count dw-sper)
6697   
user=> (first dw-sper)
["mary726" [#<Date Wed Oct 21 01:59:06 EDT 2009> #<Date Wed Oct 21 02:09:54 EDT 2009> #<Date Sun Oct 25 18:42:31 EDT 2009> #<Date Mon Oct 26 20:37:49 EDT 2009> #<Date Tue Oct 27 15:32:40 EDT 2009> #<Date Wed Oct 28 20:09:04 EDT 2009> #<Date Wed Oct 28 22:36:29 EDT 2009> #<Date Fri Oct 30 12:44:07 EDT 2009> #<Date Sat Oct 31 00:01:11 EDT 2009> #<Date Sun Nov 01 16:39:33 EST 2009> #<Date Mon Nov 02 11:19:32 EST 2009> #<Date Thu Nov 05 18:19:38 EST 2009> #<Date Tue Nov 10 02:04:23 EST 2009> #<Date Fri Nov 13 03:09:46 EST 2009> #<Date Fri Nov 13 03:11:22 EST 2009> #<Date Fri Nov 13 14:14:49 EST 2009>]]
user=> (def dw-sper-tops (->> dw-sper (map (fn [[user reps]] [user (count reps)])) (sort-by second >)))
#'user/dw-sper-tops
user=> (take 10 dw-sper-tops)
(["angeladf" 344] ["tineluvsdonnie" 277] ["sampan22" 245] ["caitlinwoos4dew" 244] ["mariabrazilian" 242] ["kissin_donnie" 237] ["nkotbluvr4life" 230] ["ddub_girl" 205] ["donniewsgirl32" 197] ["yougotmichelle" 194])
user=> (def dw-back (let [dw-reps (reps "donniewahlberg")] (->> dw-sper-tops (map (fn [[user num]] (let [back (dw-reps (keyword user))] [user (count back)]))))))
#'user/dw-back
user=> (count dw-back)                                                                                                                                          6697   
user=> (take 10 dw-back)
(["angeladf" 3] ["tineluvsdonnie" 0] ["sampan22" 0] ["caitlinwoos4dew" 0] ["mariabrazilian" 1] ["kissin_donnie" 1] ["nkotbluvr4life" 0] ["ddub_girl" 1] ["donniewsgirl32" 1] ["yougotmichelle" 1])
user=> (take 100 dw-back)
(["angeladf" 3] ["tineluvsdonnie" 0] ["sampan22" 0] ["caitlinwoos4dew" 0] ["mariabrazilian" 1] ["kissin_donnie" 1] ["nkotbluvr4life" 0] ["ddub_girl" 1] ["donniewsgirl32" 1] ["yougotmichelle" 1] ["dewsclaud9" 1] ["sparkythebeader" 0] ["mysummertime09" 3] ["valleygirl1976" 2] ["nkotbgal21" 3] ["_cassy919" 2] ["ddubroxmysox" 1] ["theddubchubcrew" 0] ["journey_woman" 0] ["momulicous" 1] ["jeffiner17" 4] ["youcandewme" 0] ["latinlove4ddub" 6] ["cindymac28" 0] ["donnieslady33" 1] ["ebonyjknight" 1] ["dewhasgotme" 1] ["hanaisabella" 0] ["dsbabygirl_170" 1] ["ddubsgermany" 0] ["ddubgirl4life" 1] ["rockergirl75" 2] ["nkotbworshiper" 0] ["kt384" 0] ["willysmommy285" 0] ["iluvddubsomuch" 2] ["dananj" 1] ["imeechan" 1] ["amy11674" 0] ["kerbear34" 2] ["ninajordan" 0] ["apriloj69" 0] ["lubddubsass" 0] ["dubalicious_jan" 1] ["andrea_wahlberg" 0] ["biggrlnow" 0] ["donniesbrunette" 0] ["dewmedub" 1] ["alisharay83" 1] ["donniedreamer" 0] ["hopin2bivfmommy" 0] ["angeladutchie" 0] ["joannarondo9" 1] ["emilyheartsyou2" 0] ["snoopygirl4ddub" 1] ["jennadyvyne" 0] ["macpack410" 1] ["vitamndannydew" 2] ["101024" 0] ["jayne_williams" 0] ["thebabygirl" 0] ["soccerlyds" 0] ["ddubandjongirl" 0] ["babyshoewoman" 0] ["sonja22" 0] ["jenangel76" 0] ["red_velvet11" 0] ["shirlsinluv2" 0] ["mel_1221" 0] ["vitamndanny" 0] ["gravulcano" 0] ["vania___" 0] ["sonya4u" 0] ["cheljim" 0] ["chadica" 3] ["ddubtwucker" 1] ["boston_ronni" 0] ["nkotbfan85" 0] ["ddubs_dimepiece" 1] ["dwslala" 1] ["smata" 0] ["yandellphillips" 0] ["kisses_4_donnie" 1] ["rissyluvsxiles7" 0] ["leeluvzyou" 0] ["mel_loves_ddub" 0] ["carrie518" 1] ["ddubsdirtydancr" 1] ["iloveeyeballs" 0] ["spants5" 0] ["donnies_cookie" 1] ["dewlicious1" 0] ["angvaughn" 0] ["sweetteach81" 1] ["irma18" 1] ["karareno" 0] ["nicole_4_eva" 0] ["devoted2ddub" 2] ["bostongirl05" 0] ["jkddubdrtydiva" 0])
user=> (def dw-back-real (remove (fn [[user num]] (zero? num)) dw-back))
#'user/dw-back-real
user=> (count dw-back-real)
262
user=> (take 100 dw-back-real)
(["angeladf" 3] ["mariabrazilian" 1] ["kissin_donnie" 1] ["ddub_girl" 1] ["donniewsgirl32" 1] ["yougotmichelle" 1] ["dewsclaud9" 1] ["mysummertime09" 3] ["valleygirl1976" 2] ["nkotbgal21" 3] ["_cassy919" 2] ["ddubroxmysox" 1] ["momulicous" 1] ["jeffiner17" 4] ["latinlove4ddub" 6] ["donnieslady33" 1] ["ebonyjknight" 1] ["dewhasgotme" 1] ["dsbabygirl_170" 1] ["ddubgirl4life" 1] ["rockergirl75" 2] ["iluvddubsomuch" 2] ["dananj" 1] ["imeechan" 1] ["kerbear34" 2] ["dubalicious_jan" 1] ["dewmedub" 1] ["alisharay83" 1] ["joannarondo9" 1] ["snoopygirl4ddub" 1] ["macpack410" 1] ["vitamndannydew" 2] ["chadica" 3] ["ddubtwucker" 1] ["ddubs_dimepiece" 1] ["dwslala" 1] ["kisses_4_donnie" 1] ["carrie518" 1] ["ddubsdirtydancr" 1] ["donnies_cookie" 1] ["sweetteach81" 1] ["irma18" 1] ["devoted2ddub" 2] ["mz_cali" 1] ["canadiangirl4nk" 1] ["jrkandddubluva" 1] ["stephbysteph10" 1] ["nautinkotbkitty" 2] ["r_a_m_89" 3] ["donnieshostess" 2] ["ddubs_ky_monkey" 3] ["ddubsprincess77" 1] ["barby312" 1] ["grinding4ddub" 2] ["margaretlo" 1] ["roxiomilagros" 1] ["miss_tattoo" 1] ["beautyfulbadgrl" 1] ["lucywillisnkotb" 1] ["wisegirlmartini" 1] ["mistral21" 1] ["christinatruax" 1] ["misswahlberg" 1] ["nklady" 1] ["tntcovergirl" 1] ["candy73" 2] ["evahz" 1] ["divadenice" 2] ["wahlbergfanatic" 2] ["raesabiggirlnow" 2] ["leahgmgdiva" 2] ["dwscovergurl" 2] ["kazzba" 1] ["ddubheartofgold" 2] ["beantownblond" 1] ["dewz_pingpong" 1] ["lovejknddub" 1] ["jonk_issosmooth" 1] ["redhot92" 1] ["peroni_grl_beth" 1] ["ddubsdeligirl" 1] ["shigirl2224" 2] ["nkangel74" 2] ["rrb_1311" 1] ["msdwlove" 2] ["emmagmgdiva" 1] ["mindi25" 1] ["ukkiltiechristy" 1] ["irish_cdn" 1] ["whispurr" 1] ["stephanie1086" 2] ["helenheartddub" 1] ["muffincakes34" 1] ["danishnkfever" 1] ["eenbean" 1] ["jordans1baby" 2] ["clairey77odt" 1] ["djsbadassgirl" 1] ["cmuskee" 1] ["cornpopcutie" 1])
user=> (def dw-back (let [dw-reps (reps "donniewahlberg")] (->> dw-sper-tops (map (fn [i [user num]] (let [back (dw-reps (keyword user))] [i user (count back)])) (iterate inc 0)))))
#'user/dw-back
user=> (take 5 dw-back)
([0 "angeladf" 3] [1 "tineluvsdonnie" 0] [2 "sampan22" 0] [3 "caitlinwoos4dew" 0] [4 "mariabrazilian" 1])
user=> (def dw-back-real (remove (fn [[i user num]] (zero? num)) dw-back))                                                                                      #'user/dw-back-real
user=> (take 50 dw-back-real)
user=> (def dw-back-real-x (map first dw-back-real))
#'user/dw-back-real-x
user=> dw-back-real-x
(0 4 5 7 8 9 10 12 13 14 15 16 19 20 22 24 25 26 28 30 31 35 36 37 39 43 47 48 52 54 56 57 74 75 78 79 82 86 87 90 93 94 97 104 106 107 110 114 122 126 129 130 133 135 143 147 149 154 158 160 163 168 172 174 177 179 180 182 185 195 198 199 201 204 205 219 221 223 224 229 233 237 240 243 247 250 251 260 266 268 279 286 304 310 316 326 332 341 346 351 355 364 368 370 371 379 380 381 383 386 404 405 407 409 415 428 430 455 457 462 463 464 467 472 477 478 480 482 488 498 503 505 518 523 527 530 544 552 561 568 583 602 603 612 622 647 651 655 658 675 688 699 708 728 732 734 768 773 775 791 793 804 821 836 847 858 873 889 900 904 909 920 927 929 933 964 977 1008 1039 1049 1069 1083 1095 1116 1126 1141 1149 1190 1204 1206 1216 1219 1254 1277 1285 1307 1335 1364 1391 1407 1431 1461 1480 1538 1539 1549 1564 1620 1741 1743 1761 1830 1838 1858 1918 1957 1990 2043 2080 2095 2132 2171 2311 2347 2357 2364 2368 2383 2430 2534 2563 2623 2634 2647 2734 2747 2989 3019 3035 3036 3065 3117 3153 3227 3275 3278 3467 3502 3506 3690 3884 3902 3909 4507 4519 4915 5056 5664 5895 6086 6149 6426)
user=> (->> (iterate #(* % 10) 1) (take 10))
(1 10 100 1000 10000 100000 1000000 10000000 100000000 1000000000)
user=> (->> (iterate #(+ % 500) 0) (take 10))
(0 500 1000 1500 2000 2500 3000 3500 4000 4500)
user=> (->> (iterate #(+ % 500) 0) (take 15))
(0 500 1000 1500 2000 2500 3000 3500 4000 4500 5000 5500 6000 6500 7000)
user=> (->> (iterate #(+ % 500) 0) (take 15) (map (fn [n] (let [less (filter #(< % n) dw-back-real-x)] (count less)))))
(0 130 177 203 217 229 237 247 253 253 256 257 259 262 262)
user=> (count dw-sper)                                                                                                                                          6697   
user=> (let [half (/ (count dw-sper) 2)] (->> dw-back-real-x (filter #(< % half)) count))
246
user=> (/ (count dw-sper) 2)
6697/2 
user=> (let [half (/ (count dw-sper) 2) front (->> dw-back-real-x (filter #(< % half)) count)] (/ front (count dw-back-real-x)))
123/131
