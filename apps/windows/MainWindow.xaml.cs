using System;
using System.Diagnostics;
using System.Windows;
using System.Windows.Media;
using System.Windows.Navigation;

namespace DNSPro;

public partial class MainWindow : Window
{
    private readonly DnsConfig _config;
    private readonly DnsManager _dns;
    private bool _busy;

    public MainWindow()
    {
        InitializeComponent();
        _config = DnsConfig.Load();
        _dns = new DnsManager(_config);
        MessageText.Text = _config.Disclaimer;
        RefreshStatus();
    }

    private void RefreshStatus()
    {
        var active = _dns.IsDnsProActive();
        StatusText.Text = active ? "Status: Active" : "Status: Inactive";
        StatusText.Foreground = new SolidColorBrush(active
            ? Color.FromRgb(15, 157, 88)
            : Color.FromRgb(95, 99, 104));
        ToggleButton.Content = active ? "Disable DNSPro" : "Enable DNSPro";
        ToggleButton.Background = new SolidColorBrush(active
            ? Color.FromRgb(217, 48, 37)
            : Color.FromRgb(11, 95, 255));
    }

    private async void ToggleButton_Click(object sender, RoutedEventArgs e)
    {
        if (_busy) return;
        _busy = true;
        ToggleButton.IsEnabled = false;
        try
        {
            if (_dns.IsDnsProActive())
            {
                MessageText.Text = "Restoring automatic DNS…";
                await _dns.DisableAsync();
                MessageText.Text = "DNSPro disabled. Adapters restored to automatic DNS.";
            }
            else
            {
                MessageText.Text = "Administrator permission required…";
                await _dns.EnableAsync();
                MessageText.Text = "DNSPro enabled.";
            }
        }
        catch (Exception ex)
        {
            MessageText.Text = ex.Message;
        }
        finally
        {
            RefreshStatus();
            ToggleButton.IsEnabled = true;
            _busy = false;
        }
    }

    private async void CheckButton_Click(object sender, RoutedEventArgs e)
    {
        if (_busy) return;
        _busy = true;
        CheckButton.IsEnabled = false;
        MessageText.Text = "Checking…";
        try
        {
            var ok = await _dns.CheckConnectionAsync();
            MessageText.Text = ok ? "DNSPro is reachable" : "Could not verify DNSPro";
        }
        catch (Exception ex)
        {
            MessageText.Text = ex.Message;
        }
        finally
        {
            CheckButton.IsEnabled = true;
            _busy = false;
        }
    }

    private void Hyperlink_RequestNavigate(object sender, RequestNavigateEventArgs e)
    {
        Process.Start(new ProcessStartInfo(e.Uri.AbsoluteUri) { UseShellExecute = true });
        e.Handled = true;
    }
}
