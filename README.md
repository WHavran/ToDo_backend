# TodoList – Backend

Backendová část projektu **TodoList** vytvořená v Javě pomocí Springu.

## Architektura

**Repository – Service – Controller**

### Repository
- Data jsou uložena v **MySQL** databázi.  
- Skript pro vytvoření a naplnění databáze: `resources/dbscripts/todo_setdb.sql`  
- K propojení a obsluze databáze je využito **JPA Repository (Hibernate)**.  
- Vedle základních metod jsou doplněny i vlastní **JPQL dotazy** pro specifickou logiku.  
- Pro testování je použita **H2 databáze**, která je nastavena přes `testing.properties`  
  (hlavní aplikace využívá `application.properties`).

### Model
- Obsahuje hlavní entitu **TodoEntry** a tři pomocná **DTO**, která slouží ke komunikaci mezi klientem a backendem.  
- DTO používají **@Valid** pro zajištění validace vstupů.  
- Součástí jsou také třídy pro **vlastní odpovědi při zpracování výjimek**.

### Service a Mapper
- **Service** se skládá z rozhraní a implementační třídy.  
- **Mapper** funguje obdobně – rozhraní + implementace.  
- Service obsahuje většinu aplikační logiky.  
- Mapper slouží k mapování dat mezi entitami a DTO.

### Controller
- Přijímá požadavky a vrací data, **neobsahuje logiku**.  
- Pro globální zpracování výjimek je využit **@RestControllerAdvice** s vlastními **ExceptionHandlery**.

### Testy
- **ControllerIntegrationTests** – integrační testy pro všechny endpointy.  
- **UnitsExempleTests** – ukázky jednotkových testů.

### Myšlenka architektury
- Striktní oddělení odpovědností jednotlivých vrstev.  
- Důraz na **čistý kód** a přehlednou strukturu.

### Možnosti rozšíření
- Vylepšené **filtrování** umožňující kombinovat více filtrů současně.  
- **Autentizace a autorizace** pomocí Spring Security pro víceuživatelský provoz.

---

## Spuštění backendu

1. Naklonujte projekt z GitHubu do IDE.  
2. Nastavte databázi pomocí skriptu `todo_setdb.sql`.  
3. Upravte propojení s databází v souboru `application.properties`.  
4. Spusťte aplikaci v IDE.
