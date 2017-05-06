# Objectif

Le but de cette demo est de voir comment construire une application microservices sur base du framework VertX
sur une stack Docker Swarm. Dans cette première version, nous n'utilisons pas l'event-bus de VertX qui est une des 
killer-features de l'outil. Ceci sera fait dans la version 2 de cette série d'articles.

# Principe
 
Cette démo utilise de nombreux concepts à la fois :
* Plugin Maven pour Docker de Spotify 
* Plugin Maven pour Git
* Plugin Shade pour générer le fatjar de Vertx
* Docker Compose avec fichier .env
* Docker Stack deployé dans un cluster Swarm fourni en local avec Docker Machine (5 noeuds)
* Outils de visualisation du cluster Swarm de Manomark de Docker
* Création d'une infrastructure immutable avec LinuxKit 
* Déploiement de l'image Linux sur GCE

Il existe d'autres plugin concurrents comme celui de Fabric8 pour Docker. 
A chacun de choisir celui qui lui convient le mieux.
Idema toujours chez Fabric8, il existe depuis peu un plugin Maven pour piloter Vertx.
