(ns cassowary-cljs-demo.core
  (:refer-clojure :exclude [+ - =])
  (:use-macros [c2.util :only [pp p]])
  (:use [c2.dom :only [append!]]
        [cassowary.core :only [cvar simplex-solver
                               constrain! stay! value
                               + - =]]))

(let [height 200, width 800, max-radius 40
      solver  (simplex-solver)
      spacing (cvar) ;;The spacing between circles (to be solved for)
      circles (repeatedly 10 #(hash-map :r  (cvar (* max-radius (rand)))
                                        :cx (cvar 0)
                                        :cy (cvar (/ height 2))))]

                 ;;The circle radii and vertical positions are constants
                 (doseq [c circles]
                   (stay! solver (:r c))
                   (stay! solver (:cy c)))
                 
                 ;;Spacing between first circle and the wall
                 (constrain! solver (= 0 (- (:cx (first circles))
                                            (:r (first circles))
                                            spacing)))
                 
                 ;;Spacing between each pair of neighboring circles
                 (doseq [[left right] (partition 2 1 circles)]
                   (constrain! solver (= spacing (- (:cx right)
                                                    (:r right)
                                                    (+ (:cx left) (:r left))))))

                 ;;Spacing between last circle and the wall
                 (constrain! solver (= spacing (- width
                                                  (:cx (last circles))
                                                  (:r (last circles)))))

                 ;;Draw the circles as SVG
                 (append! "body"
                          [:svg:svg {:width width :height height
                                     :style {:border "1px solid black"
                                             :margin "20px"}}])

                  (doseq [c circles]
                    (append! "body svg"
                             [:svg:circle {:cx (value (:cx c))
                                           :cy (value (:cy c))
                                           :r (value (:r c))}])))
