package main

import (
	"io/ioutil"
	"path/filepath"
	"sort"
	"strings"

	"github.com/urfave/cli"
)

// Element : A resource to be stored on DVD
type Element struct {
	Name  string
	Size  int64
	IsDir bool
}

// Elements : A splice of Element structures
type Elements []Element

// Adjust Size to include ubdirectories
func (c *Element) fillInDirectorySize(cx *cli.Context) error {

	if !c.IsDir {
		return nil
	}

	size, err := DirSize(filepath.Join(cx.String("source"), c.Name))
	if err != nil {
		return err
	}

	c.Size = size

	return nil
}

// Implement sort.Interface
// Sort by size, descending

// Len : sort.Interface - Get length of Elements splice
func (c Elements) Len() int {
	return len(c)
}

// Swap : sort.Interface - Swap two elements in the Elements splice
func (c Elements) Swap(i, j int) {
	c[i], c[j] = c[j], c[i]
}

// Less : sort.Interface - Compare to elements of the Elements slice
func (c Elements) Less(i, j int) bool {
	return c[i].Size > c[j].Size
}

// Get a list of elements for the source directory
func getElements(cx *cli.Context) (Elements, error) {

	var elements Elements

	source := ExpandPath(cx.String("source"))

	files, err := ioutil.ReadDir(source)
	if err != nil {
		return elements, err
	}
	for _, info := range files {
		if strings.HasPrefix(info.Name(), ".") {
			continue
		}
		element := Element{
			Name:  info.Name(),
			Size:  info.Size(),
			IsDir: info.IsDir(),
		}
		err := element.fillInDirectorySize(cx)
		if err != nil {
			return elements, err
		}
		elements = append(elements, element)
	}

	sort.Sort(Elements(elements))

	return elements, nil
}
