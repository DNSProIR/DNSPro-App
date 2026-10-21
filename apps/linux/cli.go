package main

import (
	"fmt"
	"os"
)

func runCLI(cfg Config, args []string) error {
	if len(args) == 0 {
		return fmt.Errorf("usage: dnspro [enable|disable|status|check|gui]")
	}

	cmd := args[0]
	applyDirect := len(args) > 1 && args[1] == "--apply"

	switch cmd {
	case "enable":
		if applyDirect || os.Geteuid() == 0 {
			return Enable(cfg)
		}
		return EnableElevated(cfg)
	case "disable":
		if applyDirect || os.Geteuid() == 0 {
			return Disable(cfg)
		}
		return DisableElevated(cfg)
	case "status":
		fmt.Println(StatusLine(cfg))
		return nil
	case "check":
		if err := CheckConnection(cfg); err != nil {
			return fmt.Errorf("Could not verify DNSPro: %w", err)
		}
		fmt.Println("DNSPro is reachable")
		return nil
	case "gui":
		runGUI(cfg)
		return nil
	case "help", "-h", "--help":
		fmt.Println(`DNSPro Linux client

Usage:
  dnspro              Launch GUI
  dnspro enable       Enable DNSPro DNS (prompts for privilege)
  dnspro disable      Disable DNSPro DNS
  dnspro status       Show status
  dnspro check        Check connectivity
  dnspro gui          Launch GUI`)
		return nil
	default:
		return fmt.Errorf("unknown command %q (try: enable|disable|status|check|gui)", cmd)
	}
}
