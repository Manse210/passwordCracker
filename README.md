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
