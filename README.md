# SWEN1-Card-Trading-Game Protokoll

[git link 1]
[git link 2]

## Einleitung
Ich habe dieses Projekt in mehreren Phasen bestritten. 
Da ich den Server anfangs in einem zweiten IntelliJ-Projekt geschrieben habe, habe ich zwischendurch sogar das Repository gewechselt. 
Dies hatte den Vorteil, dass ich bereits bestehenden Code verwenden und weiter entwickeln konnte, anstatt alles von Grund auf neu zu schreiben.

Mein persönliches Ziel in diesem Projekt war es, ein flexibles und plug-and-play-System aufzubauen, dass später bei der Umsetzung des Projektes Zeit und Kopfschmerzen sparen würde. 
Um dies zu erreichen, habe ich den Code so strukturiert und modular wie möglich gestaltet, um ihn leicht erweiterbar und anpassbar zu machen.
Inspiriert von bekannten Frameworks wie Express.js habe ich versucht die Benennung und Bedienung meines Servers einfach und intuitiv zu gestalten. 
So hoffte ich, dass es für andere Entwickler leicht verständlich und einfach zu nutzen sein würde.

Im Zeitplan habe ich alle Arbeitsschritte und die dafür aufgewendete Zeiten aufgelistet. 
Zu Beginn habe ich mich mit den Grundlagen des Projekts beschäftigt und zB. UML-Diagramme erstellt, um die Struktur des Codes zu planen. 
Anschließend habe ich mich auf die Entwicklung des User-Modells konzentriert und das Decorator Pattern verwendet, um individuelle Funktionen hinzuzufügen. 
Während des Projekts habe ich mich dann auf die Entwicklung des Servers und des Parsers konzentriert und die HttpPackage, RouteWorkers und RoutingTable implementiert.

## Zeitplan
| Date       | Hours | Worked on                                                 |
| ---------- | ----- |-----------------------------------------------------------|
| 29.9.2022  | 2     | Kickoff                                                   |
| 3.10.2022  | 2     | Basics                                                    |
| 6.10.2022  | 2     | Basics                                                    |
| 10.10.2022 | 2     | UML                                                       |
| 13.10.2022 | 2     | Basics                                                    |
| 16.10.2022 | 4     | User                                                      |
| 17.10.2022 | 2     | Cards                                                     |
| 20.10.2022 | 6     | Decorator Pattern For unique features                     |
| 21.10.2022 | 4     | Trade and TradeManager Models                             |
| 7.11.2022  | 6     | Started on Server and Parser                              |
| 14.11.2022 | 2     | Server and Parser                                         |
| 16.11.2022 | 8     | HttpPackage                                               |
| 17.11.2022 | 2     | RouteWorkers                                              |
| 21.11.2022 | 6     | RoutingTable                                              |
| 22.11.2022 | 8     | RoutingTable & RouteWorkers                               |
| 23.11.2022 | 2     | HTTP-Parser & Database                                    |
| 18.12.2022 | 4     | Server Refactoring                                        |
| 19.12.2022 | 4     | Server Refactoring                                        |
| 5.1.2023   | 8     | Merging server and model                                  |
| 6.1.2023   | 8     | User Route Worker                                         |
| 7.1.2023   | 4     | Refactoring                                               |
| 8.1.2023   | 4     | Refactoring & Database (Middlewares)                      |
| 12.1.2023  | 5     | Card Route Worker & Refactoring                           |
| 13.1.2023  | 4     | Refactoring Actors/Cards/Battle classes (now CardWrapper) |
| 14.1.2023  | 8     | Battle Route Worker                                       |
| 17.1.2023  | 8     | Trade Route Worker & Bugfixing                            |
| 18.1.2023  | 8     | Bugfixing                                                 |

## Besondere Features
Eines der besonderen Features, das ich in meinem Projekt implementiert habe, ist die Möglichkeit von Critical Hits. 
Ein Critical Hit erhöht die Schadenswirkung eines Angriffs erheblich und kann den Ausgang eines Kampfes beeinflussen. 
Ich habe diese Funktion implementiert, um die Spannung im Kampf zu erhöhen und eine zusätzliche taktische Komponente hinzuzufügen.

Ein weiteres besonderes Feature, das ich implementiert habe, sind Status Effects. 
Status Effects können sowohl positive als auch negative Auswirkungen auf die Statistiken eines Charakters haben und können den Verlauf eines Kampfes beeinflussen. 
Ich habe diese Funktion hinzugefügt, um die taktischen Möglichkeiten im Kampf zu erweitern und die Spannung zu erhöhen.

Als Ergänzung zu den Status Effects habe ich auch Regeln für die Anwendung und Wirkung der Status Effects implementiert. 
Dies ermöglicht es den Entwicklern, die Auswirkungen der Status Effects genauer zu steuern und die Balance im Spiel zu halten.

Als weiteres Feature, das nicht direkt im Zusammenhang mit dem Battle steht, habe ich ein Logout-Feature implementiert. 
Dies ermöglicht es den Benutzern, sich sicher und einfach aus ihrem Konto auszuloggen, ohne dass Daten verloren gehen oder unerwünschte Änderungen vorgenommen werden. 
Dies trägt zur Sicherheit und Benutzerfreundlichkeit des Projekts bei.

## Design der Software

### Models
Zu beginn waren die Models. 

#### Battles

#### Rules

### HTTP und Server

### Routing, RouteWorkers and Middlewares
Sowohl die Datenbankanbindung, als auch die

### Database
Laut meiner interpretation der Spezifikation wäre es logisch folgende Tabellen zu erstellen:
* users: Daten für User
* cards: Daten einer Karte
* stacks: der Stack eines Users
* stack_cards: die Karten in einem Stack
* decks: das Deck eines Users
* deck_cards: die Karten in einem Stack
* battles: log ser Battles
* scoreboard: ELO-Werte der einzelnen User
* stats: Games/Wins/Losses von Usern
* trades: Im Moment offene Trades

Das war dann auch der aufbau meiner Datenbank bis ich begonnen hab die RouteWorkers zu implementieren. 
Mir fiel ziemlich schnell auf, dass einige Tables ziemlich unnötig sind. 
Scoreboard, user_stats und stack hab ich, schon am Anfang mit User zusammenlegen können.
Jeder user kann sowieso nur einen stack haben, welche ja noch dazu alle Karten beinhalten sollte.
Aus stack_cards wurde somit user_cards und drei ganze Table konnten gestrichen werden.

Mit der Zeit wurde die Datenbank dann immer kürzer, bis ich bei den notwendigsten Tabellen angekommen war.
Die wahrscheinlich am technisch aufwendigste Änderung an der Datenbank war die auflösung der Tabellen decks und deck_cards.
Das ganze könnte ja auch durch eine Spalte in user_cards umgesetzt werden.
Da ich es aber für zu kompliziert hielt, immer im Programm darauf schauen zu müssen, dass immer maximal 4 karten im deck, sind habe ich dafür eine Datenbank-Funktion geschrieben.
Die Funktion setzt automatisch den als letzten Eintag, welcher sich gerade im Deck befindet auf false, falls ein neuer Eintag ins Deck kommen soll.
Wird ein Eintrag, der im Deck ist gelöscht oder transferiert, dann wird ein zufälliger Eintag für das Deck ausgewählt. 
Gibt es weniger als 4 Karten, so werden alle neuen Karten automatisch ins Deck gegeben.

``` sql
CREATE OR REPLACE FUNCTION check_deck_limit() RETURNS TRIGGER AS $$
BEGIN
  NEW.last_updated = NOW();
  IF ((TG_OP = 'INSERT' AND NEW.deck = TRUE) OR (NEW.deck = TRUE AND OLD.deck = FALSE)) THEN
    -- remove the first card from the deck if there are already 5 cards
    IF (SELECT COUNT(*) FROM users_cards WHERE user_id = NEW.user_id AND deck = TRUE) >= 4 THEN
      UPDATE users_cards SET deck = FALSE WHERE user_id = NEW.user_id AND id = (SELECT id FROM users_cards WHERE user_id = NEW.user_id AND deck = TRUE ORDER BY last_updated LIMIT 1);
    END IF;
  ELSEIF (TG_OP = 'INSERT' AND (SELECT COUNT(*) FROM users_cards WHERE user_id = NEW.user_id AND deck = TRUE) < 4) THEN
      NEW.deck = TRUE;
  END IF;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER check_deck_limit
BEFORE INSERT OR UPDATE ON users_cards
FOR EACH ROW
EXECUTE FUNCTION check_deck_limit();

--
CREATE OR REPLACE FUNCTION check_deck_limit_on_delete() RETURNS TRIGGER AS $$
BEGIN
  IF (OLD.deck = TRUE) THEN
    -- check if there are any other cards in the deck
    IF (SELECT COUNT(*) FROM users_cards WHERE user_id = OLD.user_id AND deck = TRUE) < 4 THEN
      -- if not, set a random card to deck=true
      UPDATE users_cards SET deck = TRUE WHERE user_id = OLD.user_id AND id = (SELECT id FROM users_cards WHERE user_id = OLD.user_id AND deck = FALSE ORDER BY RANDOM() LIMIT 1);
    END IF;
  END IF;
  RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER check_deck_limit_on_delete
AFTER DELETE ON users_cards
FOR EACH ROW
EXECUTE FUNCTION check_deck_limit_on_delete();
```

Zu guter letzt brauchte ich keinen Table für Battles mehr.
All das macht die Datenbank, zu dem, was sie jetzt ist.

## Testentscheidungen
Ich wollte sowohl die Infrastructure, als auch die Models des Projektes testen.

## Lessons Learned

### Beginne mit dem großen Ganzen, anstatt sofort ins detail zu gehen:
Im Projekt habe ich schnell festgestellt, dass es wichtig ist, sich zunächst einen Überblick über das gesamte Projekt zu verschaffen, bevor man sich in die Details vertieft. 
Durch den Fokus auf das große Ganze hätte ich besser abschätzen können, welche Schritte notwendig sind, um das Projekt erfolgreich abzuschließen. 
Auch hätte ich frühzeitiger mögliche Probleme identifizieren und gezielter an deren Lösung arbeiten können.

### Unschöner Aufbau der Datenbank:
Während der Arbeit am Projekt habe ich festgestellt, dass der Aufbau meiner Datenbank-Anbindung immer unübersichtlich und ineffizient wurde. 
Es gab zu viele Methoden und diese hätten auf verschiedene Klassen aufgeteilt werden können, um den Code übersichtlicher und leichter wartbar zu machen. 
Leider ist mir das erst viel zu Spät aufgefallen, um es noch ändern zu können.
Dies hat mich Zeit, Ressourcen und viel Kopfzerbrechen gekostet, um Probleme in der Datenbank zu lösen.

### Probleme die Übersicht zu behalten:
Ein weiteres Problem war, dass ich während der Arbeit am Projekt oft die Übersicht verloren und festgestellt hab, dass kleine Änderungen große Auswirkungen auf das Projekt hatten. 
Insbesondere die Verwendung von Middlewares und Änderungen am Parser haben große Probleme verursacht. 
Dies hat gezeigt, dass große Projekte oft unpraktisch und schwierig zu handhaben sind und es deshalb wichtig ist, eine Art Checklist zu führen und regelmäßig seinen Fortschritt am Projekt zu prüfen.
