# download-hls-manifest

Download HLS manifests to your local machine, fragments and all, while preserving the original file structure. Send a master manifest url to the endpoint "/dl", and the server will download it to resources/public

## Prerequisites

You will need [Leiningen][] 2.0.0 or above installed.

[leiningen]: https://github.com/technomancy/leiningen

## Running

To start a web server for the application locally, run:

    lein ring server-headless
    
Start a download by making a POST request to /dl, or http://localhost:300/dl, the body should be JSON with a property "url" set to whichever manifest to download

```
{
    "url": "http://site.com/manifest.m3u8"
}
```

## Notes
  1. If absolute paths are in the manifest, they will be overwritten by the same paths without the url origin, so that it is ready to serve as soon as it is downloaded
  2. Media types, such as audio tracks, are supported as long as a URI is specified in an #EXT-X-MEDIA tag
  3. Downloading large amounts of data takes time, make sure you have a stable internet connection
  4. Remove any credentials/cors restrictions on remote manifests - nothing more than a simple get request is made to the level and fragment urls
