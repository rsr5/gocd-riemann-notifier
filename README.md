# gocd-riemann-notifier
GoCD Plugin that Sends Pipeline events to Riemann

## Installation

Create configuration file:

```
echo "riemann_port=5555
riemann_host=loclahost
" > /var/go/gocd-riemann-notifier.conf
```

Install the plugin and restart GoCD

```
cd /var/lib/go-server/plugins/external/
sudo wget https://github.com/rsr5/gocd-riemann-notifier/releases/download/0.6/gocd-riemann-notifier-0.6.jar
sudo service go-server restart
```
