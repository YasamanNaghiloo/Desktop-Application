package api

import (
	"fmt"
	"io"
	"log"
	"net/http"
	"os"
	"path/filepath"
	"strings"

	"github.com/google/uuid"
)

func Upload(w http.ResponseWriter, r *http.Request) {
  // Limit the file size to 10MB
  r.ParseMultipartForm(10 << 20)

  file, handler, err := r.FormFile("file")
  if err != nil {
      fmt.Println("Error retrieving the file from the form:", err)
      return
  }
  defer file.Close()

  fmt.Printf("Received file: %s\n", handler.Filename)

  // Create the "public/images" directory if it doesn't exist
  err = os.MkdirAll("public/images", os.ModePerm)
  if err != nil {
      fmt.Println("Error creating directory:", err)
      return
  }

  // Generate a unique filename
	id := uuid.New()
	ext := filepath.Ext(handler.Filename)
	ext = strings.ToLower(ext[1:])
	filename := id.String() + "." + ext

	// Create the file on the server
	f, err := os.OpenFile("./public/images/"+filename, os.O_WRONLY|os.O_CREATE, 0666)
  if err != nil {
      fmt.Println("Error creating the file:", err)
      return
  }
  defer f.Close()

  // Copy the uploaded file to the newly created file
  _, err = io.Copy(f, file)
  if err != nil {
      fmt.Println("Error copying the file:", err)
      return
  }

  log.Println("File uploaded successfully.")

  // Construct the file path (The Server servers /public/ directory as /)
	filePath := "/images/" + filename

	// Send the file path as response
	w.WriteHeader(http.StatusOK)
	fmt.Fprintf(w, "File uploaded successfully. Path: %s", filePath)
}
