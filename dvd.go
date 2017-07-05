package main

import (
	"fmt"
	"os"
	"path/filepath"

	"github.com/urfave/cli"
)

// DVD : A description of DVD contents
type DVD struct {
	ID       int
	Size     int64
	Elements Elements
}

// DVDs : A set of DVD structures
type DVDs struct {
	Context   *cli.Context
	Dir       string
	Entries   []*DVD
	NextIndex int
}

func newDVDSet(cx *cli.Context) (*DVDs, error) {

	dvds := &DVDs{
		Context:   cx,
		NextIndex: cx.Int("start"),
	}

	// Create destination directory if it doesn't exist
	destination := ExpandPath(cx.String("destination"))
	err := os.MkdirAll(destination, 0700)
	if err != nil {
		return dvds, err
	}

	// Make sure destination is empty
	empty, err := IsDirEmpty(destination)
	if err != nil {
		return dvds, err
	}
	if !empty {
		return dvds, fmt.Errorf("Destination directory %s is not empty", destination)
	}

	dvds.Dir = destination

	return dvds, nil
}

func (s *DVDs) load(elements Elements) error {

	maxSize := s.Context.App.Metadata["dvdSize"].(int64)
	sourceDir := ExpandPath(s.Context.String("source"))

	for _, candidate := range elements {
		var entry *DVD
		var entryDir string

		if s.Context.Bool("debug") {
			fmt.Printf("Considering candidate element %s (%dB)\n", candidate.Name, candidate.Size)
		}

		// Look for an existing dvd to hold entry
		added := false
		for _, entry = range s.Entries {
			if s.Context.Bool("debug") {
				fmt.Printf("  Considering DVD %d (%dB)\n", entry.ID, entry.Size)
			}
			if entry.Size+candidate.Size < maxSize {
				added = true
				break
			}
		}
		if !added {

			// Entry fits on no dvd, create a new one
			if candidate.Size > maxSize {
				return fmt.Errorf("%s exceeds the max size of %d", candidate.Name, maxSize)
			}

			entry = &DVD{
				ID: s.NextIndex,
			}
			s.NextIndex++
			s.Entries = append(s.Entries, entry)
		}

		// Add entry to selected dvd
		entry.Elements = append(entry.Elements, candidate)
		entry.Size += candidate.Size
		entryDir = filepath.Join(s.Dir, fmt.Sprintf("%s%04d", s.Context.String("prefix"), entry.ID))
		err := os.MkdirAll(entryDir, 0700)
		if err != nil {
			return err
		}

		// Copy element to new location
		source := filepath.Join(sourceDir, candidate.Name)
		destination := filepath.Join(entryDir, candidate.Name)
		if s.Context.Bool("verbose") {
			fmt.Printf("Copying %s to %s...\n", source, destination)
		}
		if candidate.IsDir {
			err = CopyDirectory(source, destination)
			if err != nil {
				return err
			}
		} else {
			err = CopyFile(source, destination)
			if err != nil {
				return err
			}
		}

	}

	return nil
}
