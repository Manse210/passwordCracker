# PasswordCracker v1

**Groupe 8** — Gueye Sokhna Fatou, Camara Mansour, Diarra Harouna Dah, Mbaye Ndèye Coumba Myriam, Niang El Hadji Mamadou

> Outil de cassage de hash MD5 utilisant le patron **Simple Factory**.

## Compilation et exécution

```bash
# Depuis la racine du projet (passwordCracker/)
mkdir bin
javac -d bin src/*.java

# Utilisation
java -cp bin Main -m DICO -h <hash>
java -cp bin Main -m BRUTE -h <hash>

# Exemples
java -cp bin Main -m DICO -h 098f6bcd4621d373cade4e832627b4f6
java -cp bin Main -m BRUTE -h 098f6bcd4621d373cade4e832627b4f6
```

Sous Windows, vous pouvez aussi double-cliquer sur `compile.bat`.

> Les fichiers `.java` restent dans `src/`, les fichiers `.class` compilés vont uniquement dans `bin/`.

---

## 1. Introduction

Le présent projet a pour objectif la conception et le développement d'un outil de cassage de mots de passe hashés en **MD5**, en utilisant le patron de conception **Simple Factory** (fabrique simple). Il s'inscrit dans le cadre du cours de **Initiation aux Patrons de Conception et aux Tests Logiciels** et vise à mettre en pratique les principes de conception orientée objet, en particulier le découplage entre le code client et les stratégies concrètes.

L'outil supporte deux méthodes de cassage :
- **Attaque par dictionnaire** : parcourt une liste de mots courants.
- **Attaque par force brute** : génère toutes les combinaisons possibles (a-z, longueur 1 à 4).

## 2. Présentation du problème

Les mots de passe sont rarement stockés en clair dans les bases de données ; ils sont généralement hashés (MD5, SHA-1, SHA-256, etc.). MD5 (Message Digest 5) produit une empreinte de 128 bits (32 caractères hexadécimaux). Bien que MD5 soit aujourd'hui considéré comme cryptographiquement faible, il reste utilisé dans d'anciens systèmes.

Le problème est le suivant : **étant donné un hash MD5, retrouver le mot de passe en clair qui lui correspond**. La solution repose sur deux stratégies complémentaires :

1. **Dictionnaire** : efficace pour les mots de passe faibles ou communs (ex: "password", "123456").
2. **Force brute** : exhaustive, garantit de trouver le mot de passe si sa longueur est ≤ 4 caractères, quel que soit son contenu.

Le défi architectural est d'implémenter ces stratégies de manière interchangeable, sans que le code client ait à connaître les détails de leur implémentation.

## 3. Architecture

L'application suit une architecture en couches fondée sur le patron **Simple Factory** :

```
┌─────────────────────────────────────────────┐
│                   Main                       │  ← Point d'entrée (client)
└──────────────────┬──────────────────────────┘
                   │ utilise
                   ▼
┌─────────────────────────────────────────────┐
│           HashCrackerFactory                 │  ← Fabrique simple
│           create(method): HashCracker        │
└──┬────────────────────────────────┬─────────┘
   │ crée                          │ crée
   ▼                                ▼
┌─────────────────┐      ┌──────────────────────┐
│ DictionaryHash   │      │ BruteForceHash       │
│ Cracker          │      │ Cracker              │  ← Stratégies concrètes
└────────┬─────────┘      └──────────┬───────────┘
         │ utilise                   │ utilise
         └──────────┬────────────────┘
                    ▼
         ┌────────────────────┐
         │     HashUtils      │  ← Classe utilitaire MD5
         └────────────────────┘
```

**Principe :**
- **`HashCracker`** (interface) définit le contrat commun : `String crack(String hash)`.
- **`DictionaryHashCracker`** et **`BruteForceHashCracker`** implémentent ce contrat.
- **`HashCrackerFactory`** centralise la création des objets : le client demande une stratégie par son nom (`"DICO"` / `"BRUTE"`) sans connaître la classe concrète.
- **`HashUtils`** fournit la fonction de hashage MD5 partagée.
- **`Main`** est le point d'entrée : parse les arguments, interroge la fabrique, exécute le cassage et affiche les résultats.

## 4. Diagramme UML

```
┌────────────────────────────────────────────────────────────────────┐
│                          Diagramme de classes                       │
│         (disponible en image dans le dossier uml/diagramme.png)     │
└────────────────────────────────────────────────────────────────────┘

┌──────────────┐     ┌──────────────────────────┐
│  HashCracker │     │     HashUtils            │
│  (interface) │     │  + md5(String): String$  │
└──────┬───────┘     └──────────┬───────────────┘
       │                        │
  ┌────┴────┐              ┌────┴────┐
  │         │              │         │
  ▼         ▼              │         │
┌─────────┐ ┌───────────┐  │         │
│Dict...  │ │BruteForce │  │         │
│Cracker  │ │Cracker    │  │         │
└────┬────┘ └─────┬─────┘  │         │
     │            │        │         │
     └─────┬──────┘        │         │
           │ utilise       │         │
           └───────────────┘         │
                                     │
┌────────────────────┐               │
│ HashCrackerFactory │               │
│ + create(String)$  │               │
└─────────┬──────────┘               │
          │ crée                     │
          └──────────────────────────┘

┌──────────┐
│   Main   │ ────────── utilise ────► HashCrackerFactory
└──────────┘
```

Les relations :
- `HashCracker` ← `DictionaryHashCracker` : **réalisation** (implantation d'interface)
- `HashCracker` ← `BruteForceHashCracker` : **réalisation**
- `HashCrackerFactory` → `HashCracker` : **création** (dépendance)
- `DictionaryHashCracker` → `HashUtils` : **utilisation**
- `BruteForceHashCracker` → `HashUtils` : **utilisation**
- `Main` → `HashCrackerFactory` : **utilisation**

👉 Le diagramme UML complet (PNG) se trouve dans le dossier `uml/`.

## 5. Usage du patron Simple Factory

Le patron **Simple Factory** est utilisé pour encapsuler la logique de création des objets `HashCracker`. Voici son fonctionnement :

### Principe

Au lieu que `Main` instancie directement les stratégies (avec `new DictionaryHashCracker(...)` ou `new BruteForceHashCracker(...)`), il délègue cette responsabilité à la classe `HashCrackerFactory` :

```java
// Dans Main.java
HashCracker cracker = HashCrackerFactory.create(method);
```

### Implémentation

```java
public class HashCrackerFactory {
    public static HashCracker create(String method) {
        switch (method.toUpperCase()) {
            case "DICO":
                return new DictionaryHashCracker("dictionary.txt");
            case "BRUTE":
                return new BruteForceHashCracker();
            default:
                throw new IllegalArgumentException("Méthode inconnue: " + method);
        }
    }
}
```

### Avantages dans ce projet

1. **Découplage** : `Main` ne dépend que de l'interface `HashCracker`, pas des classes concrètes.
2. **Point de configuration unique** : pour changer de stratégie, on modifie seulement l'argument `-m`.
3. **Maintenance facilitée** : si un constructeur évolue, une seule classe est modifiée.

### Responsabilités des classes

| Classe | Rôle | Responsabilité |
|---|---|---|
| `HashCracker` (interface) | Contrat | Définit la méthode `crack(String hash)` que toutes les stratégies doivent implémenter. |
| `HashUtils` | Utilitaire | Centralise le calcul MD5 via `MessageDigest`, évite la duplication de code. |
| `DictionaryHashCracker` | Stratégie dictionnaire | Lit un fichier dictionnaire, hashe chaque mot et compare au hash cible. |
| `BruteForceHashCracker` | Stratégie force brute | Génère récursivement toutes les combinaisons a-z (1-4 caractères). |
| `HashCrackerFactory` | Fabrique simple | Instancie la bonne stratégie selon le paramètre reçu (`DICO` ou `BRUTE`). |
| `Main` | Point d'entrée | Parse les arguments CLI, utilise la fabrique, exécute le cassage, affiche les résultats. |

## 6. Résultats obtenus

Les tests ont été réalisés sur un PC Windows 11 avec JDK 17. Le dictionnaire utilisé contient 109 mots courants.

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
| `abc` | `900150983cd24fb0d6963f7d28e17f72` | DICO | ❌ not found | 53 | 109 |
| `abc` | `900150983cd24fb0d6963f7d28e17f72` | BRUTE | ✅ found | 79 | 731 |
| `xyz` | `d16fb36f0911f878998c136191af705e` | DICO | ❌ not found | 42 | 109 |
| `xyz` | `d16fb36f0911f878998c136191af705e` | BRUTE | ✅ found | 547 | 16 900 |

### Analyse des résultats

- **Dictionnaire** : très rapide (15-32 ms), mais limité aux mots présents dans le fichier. Sur les mots testés, seuls ceux du dictionnaire sont trouvés.
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

### 1. Quels sont les avantages de la fabrique simple (Simple Factory) dans ce projet ?
La fabrique simple centralise la création des objets `HashCracker`. Cela permet de :
- **Découpler** le code client (`Main`) des classes concrètes (`DictionaryHashCracker`, `BruteForceHashCracker`).
- **Faciliter la maintenance** : si le constructeur d'une stratégie change, une seule modification est nécessaire dans la fabrique.
- **Améliorer la lisibilité** : le choix de la stratégie est explicite via un paramètre (`"DICO"` ou `"BRUTE"`).

### 2. Quels sont ses inconvénients ?
- **Violation du principe Open/Closed** : pour ajouter une nouvelle stratégie, il faut modifier la fabrique existante (ajouter un `case` dans le `switch`).
- **Logique de branchement** : le `switch` grossit avec le nombre de stratégies, rendant la fabrique moins lisible à terme.
- **Couplage** : la fabrique connaît toutes les classes concrètes, ce qui peut poser problème dans une architecture plus large.

### 3. Comment feriez-vous pour ajouter une nouvelle stratégie (par exemple, un cracker par tables arc-en-ciel) ?
1. Créer une nouvelle classe `RainbowTableHashCracker` implémentant `HashCracker`.
2. Modifier `HashCrackerFactory.create()` pour ajouter un `case "RAINBOW"` retournant la nouvelle instance.
3. Utiliser l'application avec `-m RAINBOW`.
Le code client (`Main`) n'a pas besoin d'être modifié.

### 4. La Simple Factory respecte-t-elle le principe Open/Closed (ouvert à l'extension, fermé à la modification) ?
**Non.** La Simple Factory ne respecte pas ce principe. Ajouter une stratégie oblige à modifier le code existant de la fabrique (ajouter un `case`), ce qui est une modification et non une extension. Des alternatives comme le pattern **Factory Method** ou l'**Abstract Factory** seraient plus conformes à Open/Closed.
