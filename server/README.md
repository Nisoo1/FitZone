# Serveur JSON - FitZone

Ce dossier contient le serveur REST local utilisé par l'application FitZone (`fitzone.json`).

## Démarrage

```bash
cd server
npm install
npm start
```

Le serveur démarre par défaut sur `http://localhost:3000` et expose les ressources suivantes :

- `GET/POST/PATCH /users`
- `GET /programs`
- `GET /seances`
- `PATCH /seances/:id`
- `GET /quizzes`
- `GET /nutritionTips`

## Accès depuis l'émulateur Android

L'émulateur Android ne peut pas joindre `localhost` de la machine hôte : il faut utiliser
l'adresse spéciale `10.0.2.2`, qui redirige vers la machine hôte. C'est la valeur configurée
par défaut dans `RetrofitClient` (`com.anis.fitzone.reseau.RetrofitClient`).

`10.0.2.2` est routé par l'émulateur vers l'interface **IPv4** (`127.0.0.1`) de la machine hôte,
pas vers `::1` (IPv6). C'est pourquoi le script `start` force `--host 0.0.0.0` : sans ce
paramètre, certaines versions de Node/json-server n'écoutent que sur `::1` et l'émulateur reçoit
un `ConnectException` même si `http://localhost:3000` fonctionne très bien depuis un navigateur
sur la machine hôte.

Si vous testez sur un appareil physique, changez `BASE_URL` pour l'adresse IP locale de votre
ordinateur sur le même réseau Wi-Fi (ex. `http://192.168.1.42:3000/`).
