package main

import (
	"io"
	"os"
	"os/user"
	"path/filepath"
)

// ExpandPath : Expand a path starting with a tilde
func ExpandPath(path string) string {
	if len(path) > 1 {
		if path[:2] == "~/" {
			user, _ := user.Current()
			home := user.HomeDir
			path = filepath.Join(home, path[2:])
		}
	}

	return path
}

// DirSize : Get size of adirectory, including subdirectories
func DirSize(path string) (int64, error) {
	var size int64
	err := filepath.Walk(ExpandPath(path), func(_ string, info os.FileInfo, err error) error {
		if !info.IsDir() {
			size += info.Size()
		}
		return err
	})
	return size, err
}

// IsDirEmpty : Returns true if directory contains no files
func IsDirEmpty(dir string) (bool, error) {
	f, err := os.Open(dir)
	if err != nil {
		return false, err
	}
	defer f.Close()

	_, err = f.Readdirnames(1)
	if err == io.EOF {
		return true, nil
	}
	return false, err
}

// CopyFile : Copy a file from source to destination
func CopyFile(source string, dest string) (err error) {
	sourcefile, err := os.Open(source)
	if err != nil {
		return err
	}

	defer sourcefile.Close()

	destfile, err := os.Create(dest)
	if err != nil {
		return err
	}

	defer destfile.Close()

	_, err = io.Copy(destfile, sourcefile)
	if err == nil {
		sourceinfo, err := os.Stat(source)
		if err != nil {
			err = os.Chmod(dest, sourceinfo.Mode())
		}

	}

	return
}

// CopyDirectory : Copy a directory and its contents from source to destination
func CopyDirectory(source string, dest string) (err error) {

	// Create destination
	sourceinfo, err := os.Stat(source)
	if err != nil {
		return err
	}
	err = os.MkdirAll(dest, sourceinfo.Mode())
	if err != nil {
		return err
	}

	// Copy over contents
	directory, _ := os.Open(source)
	objects, err := directory.Readdir(-1)
	for _, obj := range objects {

		sourceElement := source + "/" + obj.Name()
		destinationElement := dest + "/" + obj.Name()
		if obj.IsDir() {
			// create sub-directories - recursively
			err = CopyDirectory(sourceElement, destinationElement)
			if err != nil {
				return err
			}
		} else {
			// perform copy
			err = CopyFile(sourceElement, destinationElement)
			if err != nil {
				return err
			}
		}

	}
	return nil
}
