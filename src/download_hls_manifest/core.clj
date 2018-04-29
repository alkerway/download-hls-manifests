(ns download-hls-manifest.core
  (:require [clj-http.client :as client]
            [me.raynes.fs :as fs]
            [download-hls-manifest.parser :as parser]
            [download-hls-manifest.config :as config]
            [clojure.string :as cljstr]
            [clojure.java.io :as io]))

(defn saveRemoteToFile [remoteUrl filePath]
  (let [body (:body (client/get remoteUrl))
        replacedBody (if (re-matches #".+\.m3u8" remoteUrl) (parser/replaceAbsolutePaths body) body)]
    (with-open [wrtr (io/writer (str config/basePath "/" filePath))]
      (.write wrtr replacedBody)) replacedBody))

(defn getHls ([url] (getHls url "" ""))
  ([currentUrl parentUrl parentPath]
  (let [filePath (if (empty? parentUrl) "master.m3u8" (str (if (not-empty parentPath) (str parentPath "/"))
                                                           (parser/getLocalPath currentUrl parentUrl)))
        remotePath (if (empty? parentUrl) currentUrl (parser/getRemotePath currentUrl parentUrl))
        newFolder (fs/mkdirs (str config/basePath "/" (cljstr/join "/" (pop (cljstr/split filePath #"/")))))
        file (saveRemoteToFile remotePath filePath)
        childUrls (if (re-matches #".+\.m3u8" currentUrl) (parser/getChildUrls file))]
    (if (not-empty childUrls)
      (loop [children childUrls]
        (let [eachUrl (first children)]
          (if eachUrl (do (getHls eachUrl remotePath (cljstr/join "/" (pop (cljstr/split filePath #"/"))))
            (recur (rest children)))))) true))))
