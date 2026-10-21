package main

import (
	"encoding/json"
	"os"
	"path/filepath"
)

type Config struct {
	ServiceName   string `json:"serviceName"`
	PrimaryIPv4   string `json:"primaryIPv4"`
	SecondaryIPv4 string `json:"secondaryIPv4"`
	CheckHost     string `json:"checkHost"`
	WebsiteURL    string `json:"websiteUrl"`
	TelegramURL   string `json:"telegramUrl"`
	Email         string `json:"email"`
	Disclaimer    string `json:"disclaimer"`
}

func defaultConfig() Config {
	return Config{
		ServiceName:   "DNSPro",
		PrimaryIPv4:   "80.244.11.244",
		SecondaryIPv4: "80.244.11.244",
		CheckHost:     "dnspro.ir",
		WebsiteURL:    "https://dnspro.ir",
		TelegramURL:   "https://t.me/dnspro_ir",
		Email:         "we@dnspro.ir",
		Disclaimer:    "DNSPro helps you reach sanction-restricted websites and provides Safe Family filtering. It is not intended for bypassing government filtering.",
	}
}

func LoadConfig() Config {
	cfg := defaultConfig()
	candidates := []string{
		"dns-config.json",
		filepath.Join("shared", "dns-config.json"),
		filepath.Join("..", "..", "shared", "dns-config.json"),
		"/usr/share/dnspro/dns-config.json",
	}
	if exe, err := os.Executable(); err == nil {
		dir := filepath.Dir(exe)
		candidates = append([]string{
			filepath.Join(dir, "dns-config.json"),
			filepath.Join(dir, "..", "share", "dnspro", "dns-config.json"),
		}, candidates...)
	}
	for _, path := range candidates {
		data, err := os.ReadFile(path)
		if err != nil {
			continue
		}
		if err := json.Unmarshal(data, &cfg); err == nil {
			return cfg
		}
	}
	return cfg
}

func (c Config) Servers() []string {
	servers := []string{c.PrimaryIPv4}
	if c.SecondaryIPv4 != "" && c.SecondaryIPv4 != c.PrimaryIPv4 {
		servers = append(servers, c.SecondaryIPv4)
	}
	return servers
}
