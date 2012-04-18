(ns cassowary-cljs-demo.core
  (:refer-clojure :exclude [+ - =])
  (:use-macros [c2.util :only [pp p]])
  (:use [c2.dom :only [append!]]
        [cassowary.core :only [cvar simplex-solver
                               constrain! stay! value
                               + - =]]))


;; mult cannot be used as cassowary mult in constraints is bnot imported 

(def cnstrnt "100-3_x-100-2_x-200-1_x-300") 



( defn mymult  [x y] (if (= y 0)
							0
							(+ x (mymult x (- y 1)))))

(defn evenelelst ([] ()) ([x] ()) ([x y & more] (concat (list y) (apply evenelelst more))))
				

(defn oddelelst ([] ())
				([x] (list x))
				([x y & more] (concat (list x) (apply oddelelst more))))
				


(def strlist (.split cnstrnt "-"))

(defn remlst ([] ())
			([x] ())
			([x & more] (concat (list x) (apply remlst more))))
 

(def fullspclst (map #(js/parseInt %) (apply oddelelst (.split cnstrnt "-"))))

(def spclst (apply remlst (rest fullspclst)))

(def circlst  (map  #(js/parseInt % )(map first ( map #(.split % "_") (apply evenelelst strlist)))))


(let [ height 200, width 800,
	
	solver  (simplex-solver),
	
		spaces (map #(hash-map :s (cvar %)) spclst),
			
		noofcirc  (count spaces) ,
		
		radius (cvar 40),
		
      ;;circles (repeatedly (+ noofcirc 1) #(hash-map :r  radius
        ;;                       :cx  (cvar (/ width 2))
          ;;                              :cy (cvar (/ height 2))))
           
           circles (map #(hash-map
           						:mulfac % 
           						:r  radius 
           						:cx (cvar (/ width 2))
           						:cy (cvar (/ height 2)))  circlst),
           
                                       
     	seq4ans (partition 2 2 (interleave (partition 2 1 circles)  spaces))]
                                 
                 (doseq [c circles]                       
                (stay! solver (:cy c)))
					
					(doseq [s spaces]
					(stay! solver (:s s)))
					
                 
                 (constrain! solver (= (first fullspclst) (- (:cx (first circles)) (mymult (:r (first circles))  (:mulfac (first circles)) ))))
                 
                 (doseq [[[left right] spc] seq4ans]
                 	(constrain! solver (= (- (:cx right) (:cx left)) (+ (:s spc) (mymult (:r right) (:mulfac right))  (mymult (:r left) (:mulfac left))))))
                 	
                 
                 	(constrain! solver (= width (+ (:cx (last circles)) (mymult (:r (last circles)) (:mulfac (last circles))) (last fullspclst) )))
                 
                 
                 
                 
                 (append! "body"
                          [:svg:svg {:width width :height height
                                     :style {:border "1px solid black"
                                             :margin
                                              "20px"}}])

                 	(doseq [c circles]
                    (append! "body svg"
                             [:svg:circle {:cx (value (:cx c))
                                           :cy (value (:cy c))
                                           :r (mymult (value (:r c))  (:mulfac c) )}])))
