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