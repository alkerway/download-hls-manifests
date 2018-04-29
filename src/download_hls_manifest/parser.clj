(ns download-hls-manifest.parser
  (:require [clojure.string :as cljstr]))

(defn getChildUrls [manifest]
  (filter #(re-matches #"[^#].+(?:\.m3u8|\.ts)" %)
          (cljstr/split-lines manifest)))

(defn getLocalPath [target base]
  (cond (re-matches #"^http.*" target)
        (cljstr/join "/" (rest (cljstr/split (subs target 8) #"/")))
        (= (first target) "/") (subs target 1)
        :else target))

(defn getRemotePath [target base]
  (let [basePath (cljstr/join "/" (pop (cljstr/split base #"/")))]
   (cond (re-matches #"^http.*" target) target
         (= (first target) "/")
         (str (cljstr/join "/" (take 3 (cljstr/split base #"/"))) target)
        :else (str basePath "/" target))))
