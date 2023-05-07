## Project téma:

Képzőművészeti termékek webshopja

## App funkciói:

* bejelentkezés / regisztráció
* Termékek kosárba tétele és kivétele a kosárból (bejelenetkezés után)
* Kosár tartalmának megtekintés (Legalább egy terméknek kell lennie a kosrába, a funkciót
  a kis kosár iconnal érhetkül el)
* Profil megtekintés (a funkciót a kosár icon melletti 'ember/avatar' iconnal érhetük el)
* Kijelentkezés / Profil törlése (a profil activity-ből érhető el)

## Pontozás:

Ezt igazából csak magamnak írtam, hogy nézzem mi hiányzik még, de szerintem nagyábból pontosan vannak benne az adatok

| Elem                                                                          | Pontszám szerintem | Egyéb                                                                                     |
|-------------------------------------------------------------------------------|--------------------|-------------------------------------------------------------------------------------------|
| Fordítási hiba nincs                                                          | 1p                 |                                                                                           |
| Futtatási hiba nincs                                                          | 1p                 |                                                                                           |
| Firebase autentikáció meg van valósítva: Be lehet jelentkezni és regisztrálni | 4p                 |                                                                                           |
| Adatmodell definiálása                                                        | 2p                 | java/com/example/asdf/Item.java                                                           |
| 3 különböző activity                                                          | 2p                 |                                                                                           |
| Beviteli mezők beviteli típusa megfelelő                                      | 3p                 |                                                                                           |
| ConstraintLayout és még egy másik layout típus charlatanism                   | 1p                 | pl.: res/layout/activity_profile.xml-ben Constraint és Linear is                          |
| Reszponzív                                                                    | 3p                 |                                                                                           |
| Legalább 2 különböző animáció használata                                      | 2p                 | Beúszás 2 különböző irányból a kosárban lévő itemeknél és az összes item megjelenítésénél |
| Intentek használata                                                           | 2p                 |                                                                                           |
| Lifecycle Hook                                                                | 2p                 | java/com/example/asdf/ShoppingActivity.java-ben onResume                                  |
| androidos erőforrás                                                           | idk                | Manifestben van uses-permission INTERNET meg egy másik is                                 |
| Notification manager                                                          | 2p                 | java/com/example/asdf/ShoppingActivity.java-ben amikor item kerül a kosárba               |
| CRUD                                                                          | 4p?                | mind a 4 van, de nem tudom hogy mennyire külön szálon                                     |
| complex                                                                       | 4p                 | ShoppingActivity-ben id szerint rendez és CartActivity-ben van egy whereIn feltétel       |
| szubjektív                                                                    | 5pls               | φ(゜▽゜*)♪                                                                                  |