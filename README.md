# Anti-Monitor

Un serveur permettant d'agréger des données pour créer un annuaire de micro-service pour une enreprise.

![Liste des micro services](img/main-monitor.png)

## Build du projet

Deux moyens possibles pour lancer le projet : 
- La création d'un jar exécutable.
- Un container Docker. 

### Pré requis
Ce projet nécessite les dépendances suivantes pour être construis :
- __Jdk 1.8__
- __Maven >= 3.5.2__
- __Nodejs >=8.5__
- __yarn__
- __Docker__ : Dépendance Optionnelle. Uniquement si vous voulez utilisez docker pour lancer le projet.    
 

### Jar exécutable

- Clone the git repository. 
- Build la partie serveur en exécutant dans le répertoire `server` la commande : 
```batchfile
make 
```

### Image Docker (Optionelle)

- Clone the git repository.
- Si vous voulez utilliser Docker, alors toujours dans le répertoire `server` lancer la commande :
```batchfile
make docker
``` 

## lancer le serveur
Deux manières de lancer le serveur :
- Soit via un jar exécutable 
- Soit via l'image Docker 

### Via le jar exécutable 

Le lancement du serveur s'effectue via la commande suivante : 

```batchfile
java -jar <fichier JAR> -conf <Fichier de configuration>
``` 
avec`
- __fichier JAR__ : l'artifact généré lors la fabrication du serveur présent dans le répertoire `server/target/*-fat.jar
- __Fichier de configuration__ : un fichier de configuration pour paramétrer le serveur (fichier exemple dans `config.json`).

### Via l'image Docker 

Une fois l'image docker sur la machine ou dans le registry, il suffit de lancer la commande suivante :

```batchfile 
docker run -p <Port Cible>:8080 -v <BD NITRITE>:/nitrite -v <LOG DIR>:/logs -t christophegeninnet/antimonitor
``` 
avec :
- __Port Cible__ : Port cible pour l'application.
- __BD NITRITE__ : Répertoire pour la base de données Nitrite.
- __LOG DIR__ : Répertoire des logs de l'application.

