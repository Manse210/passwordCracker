# PasswordCracker v1

**Groupe 8** — Gueye Sokhna Fatou, Camara Mansour, Diarra Harouna Dah, Mbaye Ndèye Coumba Myriam, Niang El Hadji Mamadou

> Outil de cassage de hash MD5 utilisant le patron **Simple Factory**.

## Compilation et exécution

```bash
# Depuis la racine du projet
javac -d . src/*.java

# Utilisation
java -cp src Main -m DICO -h <hash>
java -cp src Main -m BRUTE -h <hash>

# Exemples
java -cp src Main -m DICO -h 098f6bcd4621d373cade4e832627b4f6
java -cp src Main -m BRUTE -h 098f6bcd4621d373cade4e832627b4f6
```

Ou double-cliquez sur `compile.bat` (Windows).

---

## 1. Introduction

Le présent projet a pour objectif la conception et le développement d'un outil de cassage de mots de passe hashés en **MD5**, en utilisant le patron de conception **Simple Factory** (fabrique simple). Il s'inscrit dans le cadre du module de **Génie Logiciel** et vise à mettre en pratique les principes de conception orientée objet, en particulier le découplage entre le code client et les stratégies concrètes.

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
*(À rédiger par Harouna)*

## 7. Difficultés rencontrées
*(À rédiger par Harouna)*

## 8. Conclusion
*(À rédiger par Harouna)*

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
