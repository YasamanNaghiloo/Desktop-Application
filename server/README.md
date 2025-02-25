# Server

This http server is used for uploading and storing images for Recipes, so we
don't clutter and slow the database by storing the images as blobs inside it.

## Installing Prerequisites

Install go 1.22.2:
    [Windows](https://go.dev/dl/go1.22.2.windows-amd64.msi)
    [MacOS x64](https://go.dev/dl/go1.22.2.darwin-amd64.pkg)
    [MacOS ARM](https://go.dev/dl/go1.22.2.darwin-arm64.pkg)

## Running the Dev Server

```bash
$ go run .
```

## Compiling the Server

```bash
$ go build
```
