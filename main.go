package main

import (
	"os"

	"github.com/urfave/cli"
)

const version string = "0.0.0.1"

func main() {

	cli.VersionFlag = cli.BoolFlag{
		Name:  "version, V",
		Usage: "print only the version",
	}

	app := cli.NewApp()
	app.Name = "dvds"
	app.Usage = "Break a directory up into DVD-sized chunks"
	app.Version = version
	app.Metadata = map[string]interface{}{
		"dvdSize": int64(4509715660),
	}
	app.Flags = []cli.Flag{
		cli.BoolFlag{
			Name:  "verbose, v",
			Usage: "Enable verbose output",
		},
		cli.BoolFlag{
			Name:  "debug, d",
			Usage: "Enable debugging output",
		},
		cli.StringFlag{
			Name:  "source, src",
			Usage: "Source directory",
		},
		cli.StringFlag{
			Name:  "destination, dest",
			Value: "~/dvds",
			Usage: "Destination directory",
		},
		cli.StringFlag{
			Name:  "prefix",
			Value: "DVD",
			Usage: "Prefix used to name DVDs",
		},
		cli.IntFlag{
			Name:  "start",
			Value: 1,
			Usage: "Number of first DVD",
		},
	}
	app.ArgsUsage = " "
	app.HideHelp = true
	app.Action = func(cx *cli.Context) error {

		if cx.String("source") == "" {
			return cli.NewExitError("No source specified", 1)
		}

		if cx.String("destination") == "" {
			return cli.NewExitError("No destination specified", 1)
		}

		dvds, err := newDVDSet(cx)
		if err != nil {
			return cli.NewExitError(err, 1)
		}

		elements, err := getElements(cx)
		if err != nil {
			return cli.NewExitError(err, 1)
		}

		err = dvds.load(elements)
		if err != nil {
			return cli.NewExitError(err, 1)
		}

		return nil
	}

	app.Run(os.Args)
}
