(ns download-hls-manifest.parser
  (:require [clojure.string :as cljstr]))

(defn getChildUrls [manifest]
  ; TODO - add EXT-MEDIA urls (audio tracks, etc)
  (filter #(re-matches #"[^#].+(?:\.m3u8|\.ts)" %)
          (cljstr/split-lines manifest)))

(defn removeOrigin [url]
   (cljstr/join "/" (rest (cljstr/split (subs url 8) #"/"))))

(defn getLocalPath [target base]
  (cond (re-matches #"^http.*" target)
        (removeOrigin target)
        (= (first target) "/") (subs target 1)
        :else target))

(defn getRemotePath [target base]
  (let [basePath (cljstr/join "/" (pop (cljstr/split base #"/")))]
   (cond (re-matches #"^http.*" target) target
         (re-matches  #"^/.+" target)
            (str (cljstr/join "/" (take 3 (cljstr/split base #"/"))) target)
        :else (str basePath "/" target))))

(defn replaceAbsolutePaths [manifest]
  (cljstr/join "\n" (map #(if (re-matches #"^http.*" %) (str "/" (removeOrigin %)) %)
                        (cljstr/split-lines manifest))))
