# CampRelay

Plugin Paper 1.21.11 pour NovusMC SMP : relais logistiques personnels (Camp Relay), basés sur un bloc de Lodestone.

## Compilation

Ce dépôt inclut un workflow GitHub Actions (`.github/workflows/build.yml`) qui compile
automatiquement le plugin à chaque push et fournit `CampRelay.jar` en artifact téléchargeable.

En local (si tu as Java 21 + Maven) :
```bash
mvn package
```
Le jar sera généré dans `target/CampRelay.jar`.

## Commande

- `/cr` — ouvre le menu principal (5 onglets : Respawn, Téléportation, Stockage, Journal, Paramètres)
- `/cr give camp [joueur]` — donne un Camp Relay *(camprelay.admin)*
- `/cr tp` — ouvre le réseau de téléportation entre tes Camp Relay *(camprelay.tp)*
- `/cr storage` — ouvre ton stockage d'urgence *(camprelay.storage)*
- `/cr log` — ouvre le journal de tes relais
- `/cr reload` — recharge les données *(camprelay.admin)*
- Alias : `/camprelay`

Astuce : clic droit + sneak sur un Camp Relay ouvre directement le menu principal.

## Fonctionnalités

1. **Smart Respawn** — choix entre respawn au Camp Relay sélectionné, au lit, ou au dernier
   Camp Relay utilisé (menu "Respawn" = choix du mode, menu "Paramètres" = choix du relais précis).
2. **Death Recovery** — mort à moins de 150 blocs d'un Camp Relay → les items sont aspirés dans un
   stockage personnel de 54 slots au lieu de tomber au sol (récupérable via `/cr storage`).
3. **Réseau de téléportation** — téléportation instantanée entre tes propres Camp Relay
   (cooldown 30s, coût 5 niveaux d'XP, distance max 5000 blocs).
4. **Mini-Beacon** — dans un rayon de 20 blocs autour d'un Camp Relay : Haste I, Speed I,
   Night Vision, Saturation I, réappliqués toutes les 5 secondes.
5. **Journal de base** — chaque Camp Relay enregistre la dernière mort à proximité, le dernier
   joueur approché (rayon 10 blocs), le dernier usage réseau, et le dernier accès au stockage.

## Stockage des données

```
plugins/CampRelay/data/
├── players.yml       # mode de respawn, relais sélectionné/dernier utilisé par joueur
├── relays.yml        # position + journal de chaque Camp Relay
└── storage/
    └── <UUID>.yml     # inventaire d'urgence (54 slots) par joueur
```

## Permissions

| Permission          | Défaut | Description                                |
|----------------------|--------|----------------------------------------------|
| `camprelay.use`      | true   | Accès de base (GUI, respawn)                  |
| `camprelay.admin`    | op     | `/cr give`, `/cr reload`                      |
| `camprelay.tp`       | true   | Réseau de téléportation                       |
| `camprelay.storage`  | true   | Stockage d'urgence                            |
