(ns main
  (:require
   [reagent.core :as r]
   [reagent.dom :as rdom]))

(defonce state (r/atom {}))

(or
 (:code-text @state)
 (->
  (js/fetch "main.cljs")
  (.then (fn [x] (.text x)))
  (.then (fn [x] (swap! state assoc :code-text x)))))

(defn drop-area []
  [:div#drop-area
   {:style {:margin-top "1rem"
            :height "7rem"
            :width "10rem"
            :background "Aquamarine"}
    :on-drag-enter
    (fn [_]
      (set! (.. (js/document.getElementById "drop-area") -style -background) "cyan"))
    :on-drag-exit
    (fn [_]
      (set! (.. (js/document.getElementById "drop-area") -style -background) "Aquamarine"))
    :on-drag-over
    (fn [event]
      (doto
          event
          .stopPropagation
          .preventDefault)
      (set! (.. event -dataTransfer -dropEffect) "copy"))
    :on-drop
    (fn [event]
      (doto
          event
          .stopPropagation
          .preventDefault)
      (let [file (->  (.. event -dataTransfer -files) first)]
        (->
         (.text file)
         (.then
          (fn [t]
            (try
              (load-string t)
              (swap! state assoc :code-text t)
              (catch js/Error _
                (js/alert "That code does not work. In case you garbled stuff, just reload this website.")))))))
      (set! (.. (js/document.getElementById "drop-area") -style -background) "Aquamarine"))}
   [:div
    {:style {:margin "1rem" :padding-top "1rem"}}
    "drop a file here"]])

(defn code-snippet []
  [:div
   [:h3 "Last evaluated code: "]
   [:div
    {:style {:background "gainsboro"}}
    [:pre
     [:code
      (:code-text @state)]]]])

(defn my-component []
  [:div
   [drop-area]
   [:div
    {:style {:margin-top "0.5rem"}}
    "Similar thing on "
    [:a {:href "https://codepen.io/Prestance/pen/PoOdZQw"} "codepen"]]
   [:div [:a {:href "https://github.com/benjamin-asdf/scittle-prints-itself"} "github"]]
   [:div [:a {:href "https://benjamin-asdf.github.io/faster-than-light-memes/scittle-prints-itself.html"} "blog post"]]
   [:h4 "Start hacking"]
   [:div
    [:div "1. Copy the code and put it into a text file."]
    [:div "2. Modify the source code. For instance, change the" [:span  {:style {:color "chocolate"}} " color"] " of something."]
    [:div "3. Drop the code into the happy peaceful rectangle up there."]]
   [code-snippet]])

(rdom/render [my-component] (.getElementById js/document "app"))



(comment
  (swap! state assoc :code-text "foi110")
  (load-string (@state :code-text)))
