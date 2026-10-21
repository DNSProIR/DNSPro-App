package main

import (
	"fmt"
	"os"
)

// Version is set via -ldflags at build time.
var Version = "0.1.0"

func main() {
	cfg := LoadConfig()

	if len(os.Args) > 1 {
		if err := runCLI(cfg, os.Args[1:]); err != nil {
			fmt.Fprintln(os.Stderr, err)
			os.Exit(1)
		}
		return
	}

	runGUI(cfg)
}
