# Fast Food Party Storage – CA1 Algorithms and Constructs

**CCT College Dublin – BSc (Hons) Computing, Semester 5 – Lecturer: Taufique Ahmed**

Group members: Ikramullah Mazhar, Shahzaib Hassan, Majid Nawaz

## The problem

A fast-food restaurant stores party food (Burger, Pizza, Fries, Sandwich, Hotdog) in a rectangular storage unit that holds **8 trays**. The unit has a **front** side and a **back** side. The chef always adds at the front, and either:

* removes from the **front** too → Last In, First Out (**Stack**), or
* removes from the **back** → First In, First Out (**Queue**).

For each item we record its name, weight (g), best-before date (max 14 days) and the time it was placed in storage.

## Team roles

| Member | Responsible for |
|---|---|
| Ikramullah Mazhar | Data structure (`FoodStorage`, `CircularDequeStorage`), exceptions, data structure tests, complexity analysis |
| Shahzaib Hassan | Food model and subclasses, input validation (`FoodValidator`), validator and manager tests |
| Majid Nawaz | `StorageManager`, Swing GUI (`MainFrame`, `TrayPanel`), README run instructions and test plan |


## Validation rules

Every value the user types is checked by `FoodValidator` before it reaches the storage. Bad input shows a clear message and the storage is not changed.

| Input | Rule | Example rejected |
|---|---|---|
| Name | One of the 5 foods, any case ("hot dog" accepted) | `Pasta`, `Pizzaa`, empty |
| Weight | Whole number of grams within the food's range (Burger 100–500, Pizza 200–1500, Fries 50–400, Sandwich 100–600, Hotdog 80–400) | `abc`, `-5`, `12.5`, `2000` for a burger |
| Best-before | `dd/MM/yyyy`, a real date, today up to 14 days ahead | `31/02/2026`, yesterday, 15 days ahead |
| Storage full | 9th item rejected with a message (checked before the inputs) | |
| Storage empty | Remove / peek / display / search show "Storage is empty" | |

Dates are parsed with `ResolverStyle.STRICT`, so impossible dates like 31/02 are rejected instead of being rolled over to March. The validator takes a `Clock`, so the JUnit tests can fix "today" and always give the same result.