# PasswordCracker v1

**Groupe 8** — Gueye Sokhna Fatou, Camara Mansour, Diarra Harouna Dah, Mbaye Ndèye Coumba Myriam, Niang El Hadji Mamadou

> Outil de cassage de hash MD5 utilisant le patron **Simple Factory**.

## 1. Introduction
*(À rédiger par Mamadou)*

## 2. Présentation du problème
*(À rédiger par Mamadou)*

## 3. Architecture
*(À rédiger par Mamadou)*

## 4. Diagramme UML
*(À rédiger par Mamadou)*

## 5. Usage du patron Simple Factory
*(À rédiger par Mamadou)*

## 6. Résultats obtenus

Les tests ont été réalisés sur un PC Windows 11 avec JDK 17. Le dictionnaire utilisé contient 28 mots courants.

### Tableau des tests

| Mot testé | Hash MD5 | Méthode | Résultat | Temps (ms) | Tentatives |
|---|---|---|---|---|---|
| `a` | `0cc175b9c0f1b6a831c399e269772661` | BRUTE | ✅ found | 29 | 1 |
| `test` | `098f6bcd4621d373cade4e832627b4f6` | DICO | ✅ found | 18 | 6 |
| `test` | `098f6bcd4621d373cade4e832627b4f6` | BRUTE | ✅ found | 1 831 | 355 414 |
| `admin` | `21232f297a57a5a743894a0e4a801fc3` | DICO | ✅ found | 23 | 3 |
| `admin` | `21232f297a57a5a743894a0e4a801fc3` | BRUTE | ❌ not found | 2 374 | 475 254 |
| `hello` | `5d41402abc4b2a76b9719d911017c592` | DICO | ✅ found | 15 | 7 |
| `hello` | `5d41402abc4b2a76b9719d911017c592` | BRUTE | ❌ not found | 2 219 | 475 254 |
| `java` | `93f725a07423fe1c889f448b33d21f46` | DICO | ✅ found | 32 | 9 |
| `abc` | `900150983cd24fb0d6963f7d28e17f72` | DICO | ❌ not found | 29 | 28 |
| `abc` | `900150983cd24fb0d6963f7d28e17f72` | BRUTE | ✅ found | 75 | 731 |
| `xyz` | `d16fb36f0911f878998c136191af705e` | DICO | ❌ not found | 24 | 28 |
| `xyz` | `d16fb36f0911f878998c136191af705e` | BRUTE | ✅ found | 547 | 16 900 |

### Analyse des résultats

- **Dictionnaire** : très rapide (15-32 ms), mais limité aux mots présents dans le fichier. Sur 28 mots testés, seuls ceux du dictionnaire sont trouvés.
- **Force brute** : garantit de trouver les mots ≤ 4 lettres, mais le temps croît exponentiellement :
  - 1 lettre → 1 tentative
  - 2 lettres → 702 tentatives
  - 3 lettres → 18 278 tentatives
  - 4 lettres → 475 254 tentatives
- **Limite** : les mots de 5 lettres ou plus (comme "admin", "hello") ne sont pas trouvés par la force brute avec `MAX_LENGTH = 4`.

### 🎥 Vidéo de démonstration

*(À réaliser par Harouna)*

## 7. Difficultés rencontrées

### Gestion des dépendances et compilation
Le projet utilise uniquement le JDK standard sans bibliothèque externe, ce qui simplifie la compilation et l'exécution. Cependant, il faut veiller à ce que le dossier `src/` soit le classpath correct (`-cp src`) lors de l'exécution.

### Découplage des stratégies
L'utilisation du patron Simple Factory a facilité l'ajout et le test des deux stratégies. Le principal défi a été de concevoir l'interface `HashCracker` de manière à ce qu'elle soit assez générique pour couvrir les deux approches (dictionnaire et force brute).

### Performance de la force brute
Le nombre de combinaisons pour une recherche exhaustive sur 26 lettres et 4 caractères maximum est de **475 254 tentatives**. Pour une recherche complète en 4 lettres (dernier cas possible : "zzzz"), le temps d'exécution est d'environ 2,2 à 2,4 secondes. Une recherche en 5 lettres nécessiterait plus de 12 millions de combinaisons, ce qui serait trop long pour une application console.

### Hash dupliqué
Initialement, le plan de projet contenait un mauvais hash MD5 pour le mot "test" (`e7247759c1633c0f9f1485f3690294a9` au lieu de `098f6bcd4621d373cade4e832627b4f6`), ce qui empêchait la validation. Il a fallu recalculer les hash avec `HashUtils.md5()` pour les corriger.

## 8. Conclusion

Ce projet a permis de mettre en œuvre concrètement le patron de conception **Simple Factory** dans un outil fonctionnel de cassage de hash MD5. Les deux stratégies (dictionnaire et force brute) montrent des compromis différents :

- **Le dictionnaire** est rapide et efficace pour les mots de passe courants, mais inefficace face à des mots inconnus.
- **La force brute** est exhaustive mais coûteuse en temps, et limitée à des mots courts dans cette implémentation.

L'architecture orientée objet permet d'ajouter facilement de nouvelles stratégies (par exemple, un cracker par tables arc-en-ciel) sans modifier le code client.

Le groupe 8 a ainsi démontré sa capacité à analyser un problème, concevoir une solution utilisant un patron de conception, l'implémenter en Java, et valider son fonctionnement sur des cas concrets.

---

## Questions de réflexion
*(À rédiger par Myriam)*
