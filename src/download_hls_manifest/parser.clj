(ns download-hls-manifest.parser
  (:require [clojure.string :as cljstr]))

(defn getStreamUrls [masterManifest]
  (filter #(re-matches #"[^#].+\.m3u8" %)
     (cljstr/split-lines masterManifest)))
