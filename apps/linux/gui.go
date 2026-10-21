package main

import (
	"net/url"

	"fyne.io/fyne/v2"
	"fyne.io/fyne/v2/app"
	"fyne.io/fyne/v2/container"
	"fyne.io/fyne/v2/widget"
)

func runGUI(cfg Config) {
	a := app.NewWithID("ir.dnspro.app")
	w := a.NewWindow("DNSPro")
	w.Resize(fyne.NewSize(420, 480))

	status := widget.NewLabel(StatusLine(cfg))
	status.Wrapping = fyne.TextWrapWord

	message := widget.NewLabel(cfg.Disclaimer)
	message.Wrapping = fyne.TextWrapWord

	toggle := widget.NewButton("Enable DNSPro", nil)
	check := widget.NewButton("Check connection", nil)

	refresh := func() {
		active := IsActive(cfg)
		status.SetText(StatusLine(cfg))
		if active {
			toggle.SetText("Disable DNSPro")
		} else {
			toggle.SetText("Enable DNSPro")
		}
	}

	toggle.OnTapped = func() {
		var err error
		if IsActive(cfg) {
			message.SetText("Restoring previous DNS…")
			err = DisableElevated(cfg)
			if err == nil {
				message.SetText("DNSPro disabled.")
			}
		} else {
			message.SetText("Administrator permission required…")
			err = EnableElevated(cfg)
			if err == nil {
				message.SetText("DNSPro enabled.")
			}
		}
		if err != nil {
			message.SetText(err.Error())
		}
		refresh()
	}

	check.OnTapped = func() {
		message.SetText("Checking…")
		if err := CheckConnection(cfg); err != nil {
			message.SetText("Could not verify DNSPro")
			return
		}
		message.SetText("DNSPro is reachable")
	}

	open := func(raw string) {
		u, err := url.Parse(raw)
		if err != nil {
			return
		}
		_ = a.OpenURL(u)
	}

	links := container.NewHBox(
		widget.NewButton("Website", func() { open(cfg.WebsiteURL) }),
		widget.NewButton("Telegram", func() { open(cfg.TelegramURL) }),
		widget.NewButton("Email", func() { open("mailto:" + cfg.Email) }),
	)

	title := widget.NewLabelWithStyle("DNSPro", fyne.TextAlignLeading, fyne.TextStyle{Bold: true})
	tagline := widget.NewLabel("Fast, secure DNS for sanction access and Safe Family protection")
	tagline.Wrapping = fyne.TextWrapWord

	refresh()

	w.SetContent(container.NewVBox(
		title,
		tagline,
		widget.NewSeparator(),
		status,
		toggle,
		check,
		message,
		widget.NewSeparator(),
		links,
	))
	w.ShowAndRun()
}
