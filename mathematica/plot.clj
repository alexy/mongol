(import '[com.wolfram.jlink MathLinkFactory])
(def kernel-link (MathLinkFactory/createKernelLink "-linkmode launch -linkname '/opt/mathematica/7.0/Executables/MathKernel -mathlink'"))
(.discardAnswer kernel-link)

(import 'com.wolfram.jlink.MathFrame)
(import 'java.awt.BorderLayout)
(import 'com.wolfram.jlink.MathCanvas)
(def frame (MathFrame.))
(.setLayout frame (BorderLayout.))
(def math-canvas (com.wolfram.jlink.MathCanvas. kernel-link))
(.add frame "Center" math-canvas)
(.setSize frame 400 400)
(.layout frame)
;; (.setMathCommand math-canvas "Plot[x, {x,0,1}]")
;; (.show frame) 

(use '[somnium.congomongo])
(mongo! :db "twitter")

(def pagerank (map #(:score %) (fetch :pagerank)))
(def pg100 (take 100 pagerank))
(def pg1000 (take 1000 pagerank))

(use 'clojuratica)
(def math-evaluate (math-evaluator kernel-link))
(def-math-macro math math-evaluate)

(def plot-expr (math :no-parse (Histogram ~pg1000)))
(.setMathCommand math-canvas (.toString plot-expr)) 
(.show frame) 

;; (Histogram ~pg1000 (-> HistogramRange (0 100)) -- range not respected
(def plot-expr (math :no-parse (Histogram ~pg1000 (-> HistogramRange [0 100]))))
(.setMathCommand math-canvas (.toString plot-expr)) 
(.show frame) 
