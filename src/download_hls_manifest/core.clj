(ns download-hls-manifest.core
  (:require [clj-http.client :as client]
            [me.raynes.fs :as fs]
            [download-hls-manifest.parser :as parser]
            [download-hls-manifest.config :as config]
            [clojure.string :as cljstr]
            [clojure.java.io :as io]))

(defn saveRemoteToFile [remoteUrl filePath]
  (let [body (:body (client/get remoteUrl))]
    (with-open [wrtr (io/writer (str config/basePath "/" filePath))]
      (.write wrtr body))
    body))

(defn getFrag [fragUrl levelUrl]
  (let [filePath (parser/getLocalPath fragUrl levelUrl)
        remotePath (parser/getRemotePath fragUrl levelUrl)
        newFolder (fs/mkdirs (str config/basePath "/" (cljstr/join "/" (pop (cljstr/split filePath #"/")))))]
    (saveRemoteToFile remotePath filePath)))
  
  
(defn getFrags [levelUrl masterUrl] 
  (let [filePath (parser/getLocalPath levelUrl masterUrl)
        remotePath (parser/getRemotePath levelUrl masterUrl)
        newFolder (fs/mkdirs (str config/basePath "/" (cljstr/join "/" (pop (cljstr/split filePath #"/")))))
        fragUrls (parser/getChildUrls (saveRemoteToFile remotePath filePath))]
    (loop [frags fragUrls]
      (let [eachFrag (first frags)]
        (if eachFrag (do (getFrag eachFrag remotePath)
                         (recur (rest frags))))))))


(defn downloadMaster [masterUrl]
  (let [master (saveRemoteToFile masterUrl "master.m3u8")
        levelUrls (parser/getStreamUrls master)]
    (loop [levels levelUrls]
      (let [eachUrl (first levels)]
        (if eachUrl (do (getFrags eachUrl masterUrl)
          (recur (rest levels))))))
    {:status 200
     :body "success"}))

(defn getHls [currentUrl parentUrl parentPath]
  (println "new")
  (println currentUrl)
  (println "p" parentPath)
  (let [filePath (if (empty? parentUrl) "master.m3u8" (str (if (not-empty parentPath) (str parentPath "/")) (parser/getLocalPath currentUrl parentUrl)))
        remotePath (if (empty? parentUrl) currentUrl (parser/getRemotePath currentUrl parentUrl))
        storeFolder (str config/basePath "/" (cljstr/join "/" (pop (cljstr/split filePath #"/"))))
        newFolder (fs/mkdirs storeFolder)
        file (saveRemoteToFile remotePath filePath)
        childUrls (if (re-matches #".+\.m3u8" currentUrl) (parser/getChildUrls file))]
    (println storeFolder)
    (if (not-empty childUrls)
      (loop [children childUrls]
        (let [eachUrl (first children)]
          (if eachUrl (do (getHls eachUrl remotePath (cljstr/join "/" (pop (cljstr/split filePath #"/"))))
            (recur (rest children))))))
      {:status 200 :body "success"})
    ))
