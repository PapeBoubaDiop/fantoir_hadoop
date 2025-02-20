# Projet Hadoop MapReduce avec Docker

Ce document décrit les étapes pour configurer un cluster Hadoop avec Docker, charger les données d'entrée, exécuter un job MapReduce et récupérer les résultats. Le job MapReduce compte le nombre de voies par commune dans un fichier FANTOIR.

## Prérequis

- **Docker** installé sur votre machine.
- Les fichiers suivants sur votre hôte :
  - **FANTOIR1022** : fichier de données à charger dans HDFS.
  - **FantoirJob.jar** : JAR exécutable contenant le job MapReduce (Driver, Mapper, Reducer).
- Un répertoire local pour les entrées (ex : `C:\AS3\BIG_DATA\projet_bdcc_2024\pratique\inputs`) et pour les sorties (ex : `C:\AS3\BIG_DATA\projet_bdcc_2024\pratique\output`).

## Étapes

### 1. Récupérer l'image Docker et créer le réseau

Tirez l'image Docker du cluster Hadoop et créez un réseau Docker pour la communication entre conteneurs :

```bash
docker pull liliasfaxi/hadoop-cluster:latest
docker network create --driver=bridge hadoop
```

### 2. Lancer le conteneur master avec montage de volume

Lancez le conteneur hadoop-master en y montant le dossier local contenant vos fichiers d'entrée :

```bash
docker run -itd --net=hadoop -p 9870:9870 -p 8088:8088 -p 7077:7077 -p 16010:16010 --name hadoop-master --hostname hadoop-master -v C:\AS3\BIG_DATA\projet_bdcc_2024\pratique\inputs:/data liliasfaxi/hadoop-cluster:latest
```

### 3. Lancer les conteneurs worker
Lancez deux conteneurs workers pour compléter le cluster Hadoop :

```bash
docker run -itd -p 8040:8042 --net=hadoop --name hadoop-worker1 --hostname hadoop-worker1 liliasfaxi/hadoop-cluster:latest
docker run -itd -p 8041:8042 --net=hadoop --name hadoop-worker2 --hostname hadoop-worker2 liliasfaxi/hadoop-cluster:latest
```

### 4. Démarrer Hadoop sur le conteneur master

Connectez-vous au conteneur master et démarrez Hadoop :

```bash
docker exec -it hadoop-master bash
./start-hadoop.sh
```

### 5. Préparer HDFS et charger les données

Créez un répertoire d'entrée dans HDFS et copiez-y le fichier de données (FANTOIR1022) depuis le volume monté :

```bash
hdfs dfs -mkdir /input
hdfs dfs -put /data/FANTOIR1022 /input
```

### 6. Exécuter le job MapReduce

Lancez le job MapReduce en utilisant le JAR déployé dans le volume /data du conteneur. Le job lira les données depuis /input et écrira les résultats dans /output :

```bash
hadoop jar /data/FantoirJob.jar com.fantoir.mapper.FantoirDriver /input /output
```

### 7. Consulter les résultats sur HDFS
Pour afficher le contenu du fichier de sortie (par exemple part-r-00000) :

```bash
hdfs dfs -cat /output/part-r-00000
```

### 8. Copier les résultats sur le système hôte

Pour récupérer les résultats du job, copiez le répertoire de sortie de HDFS vers le système de fichiers local du conteneur, puis utilisez docker cp pour le transférer sur votre hôte :

#### 1. Copier depuis HDFS vers le système local du conteneur

```bash
hdfs dfs -get /output /tmp/output
```

#### 2. Copier le répertoire du conteneur vers l'hôte :

```bash
docker cp hadoop-master:/tmp/output "C:\AS3\BIG_DATA\projet_bdcc_2024\pratique\output"
```

## Remarques

- **Fichiers de sortie :**
Le job MapReduce génère généralement des fichiers de sortie au format texte (par exemple, part-r-00000). Vous pouvez transformer ces fichiers en CSV, JSON ou tout autre format à l'aide d'outils de transformation (script Python, commandes Unix, etc.).

- **Nettoyage :**
Hadoop refuse d'écrire dans un répertoire de sortie existant. Dans le code de votre driver, le répertoire de sortie est automatiquement supprimé s'il existe. Vous pouvez également le faire manuellement avec :

```bash
hdfs dfs -rm -r /output
```

**Conclusion**

Ce workflow vous permet de déployer un cluster Hadoop dans Docker, de charger des données dans HDFS, d'exécuter un job MapReduce pour compter le nombre de voies par commune et de récupérer les résultats sur votre machine hôte. Adaptez ces étapes en fonction de vos besoins spécifiques et consultez la documentation Hadoop pour plus de détails.



