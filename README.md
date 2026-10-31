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

## Chosen data structure: bounded circular deque

We implemented our own **double-ended queue (deque) on a fixed circular array of 8 slots** (`CircularDequeStorage`).

* A deque supports both ways the chef works with one structure: add at the front, then remove from the front (stack mode) or the back (queue mode). Both ends are O(1).
* A fixed array of 8 matches the physical unit exactly, so there's no resizing and the "full" check is just `size == 8`.
* The circular indexes (`(front + i) % capacity`) mean nothing gets shifted when we add or remove, so the core operations stay O(1).

We didn't use `java.util.Stack` or `ArrayDeque` because those grow without limit (they would need extra checks to respect the 8-tray limit) and they hide the algorithm we're being assessed on.

## Time complexity

| Operation | Method | Time | Why |
|---|---|---|---|
| Add | `add()` → `addFront()` | **O(1)** | Move `front` back one slot (wrapping), write one cell |
| Remove (stack) | `remove()` → `removeFront()` | **O(1)** | Read the front cell, move `front` forward |
| Remove (queue) | `remove()` → `removeBack()` | **O(1)** | Back index is computed: `(front + size − 1) % capacity` |
| Peek / top item | `peek()`, `peekFront()` | **O(1)** | Direct index access, nothing removed |
| Display all | `toList()` | **O(n)** | Visits each of the n stored items once |
| Search (name / weight / date) | `search(Predicate)` | **O(n)** | Linear search; items aren't sorted by these fields |
| isFull / isEmpty / size | | **O(1)** | Stored counter |
| Space | | **O(capacity)** | One array of 8 references |

n is at most 8, so the linear search is effectively instant.

## Project structure

```
src/main/java/ie/cct/fastfood/
├── Main.java          starts the app
├── model/             FoodItem (abstract) + 5 foods, FoodType, FoodFactory
├── datastructure/     FoodStorage<T>, CircularDequeStorage<T>, StorageMode, exceptions
├── service/           FoodValidator, StorageManager, ValidationException
└── ui/                MainFrame, TrayPanel
src/test/java/...      JUnit 5 tests (25 tests)
```

**OOP used:** encapsulation (private fields + getters), abstraction (`FoodStorage` interface, abstract `FoodItem`), inheritance (5 food subclasses, exception hierarchy), polymorphism (`getHandlingNote()` per food), generics (`CircularDequeStorage<T>`).

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

## How to run (NetBeans)

1. Clone the repository and in NetBeans choose **File → Open Project** → select the `CA1-FastFoodStorage` folder (NetBeans recognises it as a Maven project).
2. Press **Run (F6)**. The first build downloads JUnit, so you need internet the first time.
3. **Run → Test Project (Alt+F6)** runs all 25 JUnit tests.

Requires JDK 17 or newer.

## Test plan (also shown in the video)

| # | Test | Input | Expected | Result |
|---|---|---|---|---|
| 1 | Remove on empty storage | click Remove | "Storage is empty" message | Pass |
| 2 | Search on empty storage | Search "Pizza" | "Storage is empty" message | Pass |
| 3 | Add valid item | Pizza, 800, date +5 days | Appears at front, tray view updates | Pass |
| 4 | Invalid name | Pizzaa | Error listing valid foods | Pass |
| 5 | Invalid weight | Burger, "abc" / 600 | Error with the allowed range | Pass |
| 6 | Invalid date | 31/02/2026, past date, +15 days | Error with the reason | Pass |
| 7 | Fill to 8, add 9th | | "Storage is full" | Pass |
| 8 | Stack remove | add A, B, C → remove | C removed | Pass |
| 9 | Queue remove | add A, B, C → remove | A removed | Pass |
| 10 | Peek | | Shows next item, size unchanged | Pass |
| 11 | Search by name | "pizza" | All pizzas with full details | Pass |
| 12 | Search by weight / date range | 100–400 g | Only matching items | Pass |
| 13 | Exit | Exit → Yes|