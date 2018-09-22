# Simulated annealing in Clojure

Simulated annealing (SA) is an technique for finding a global optimum state within
a large discrete search space. SA is particularly useful for finding an *approximate* optimum in a short time.

SA shines as a general purpose optimization algorithm. The function does not need to be differentiable
nor does it need to be pure in a mathmatical sense.
Thus most optimization problems can be quickly expressed using SA. It is the sledgehammer of
discrete optimization, a blunt-force instrument that can crack open almost any problem.


## Status: experimental

This is largely an learning experiment for me.
Previously, I've worked on [SA in Python](https://github.com/perrygeo/simanneal) but this is
my first attempt at porting it to Clojure. I don't recommend using it in production yet!

## API overview

The `simanneal.anneal` namespace provides two public functions

`make-temperature-seq` will construct an exponentially-decreasing sequence of numbers, suitable for input
as a temperature schedule.

`run-sa` runs the simulated annealing heurstic to find the optimal state.


## Example: The world's worst sorting algorithm

To demonstrate the simulated
annealing technique, we're going to implement a sorting algorithm in SA.
The purpose is to demonstrate the concepts - don't actually use this to sort data in production!


#### 1. Model the system state as a Clojure data structure

The state of our system is a vector of numbers

```clojure
(def initial-state [9 1 2 3 0 8 7 4 5 6])
```

#### 2. Define a `move` function

The `move` function takes a `state` and returns a slightly altered version.
This function effectively allows the algorithm to move about the search space.

Our `move` function will *randomly* swap the position of two elements in the vector.

```clojure
(defn switch-elements
  "Helper function to switch 2 elements in a vector by index"
  [v i1 i2]
  (assoc v i2 (v i1) i1 (v i2)))

(defn move
  "Return a new state with a small modification"
  [state]
  (let [idx1 (rand-int (count state))
        idx2 (rand-int (count state))]
    (switch-elements state idx1 idx2)))

(move initial-state)
;; [9 1 7 3 0 8 2 4 5 6]
;; Note 7 and 2 swapped
```

#### 3. Define a `score` function

The `score` function takes a `state` and returns a scalar value, lower is better.
This is the "Objective Function" that we are trying to minimize.

In this case, `score` weights the numbers by index, penalizing larger numbers
appearing at the end of the vector.

```clojure
(defn score
  "Weight numbers by index.
  Higher numbers first will yield the lowest score.
  Effectively a reverse sort."
  [state]
  (let [indexes (range 1 (+ 1 (count state)))
        weighted-scores (map * indexes state)]
    (reduce + weighted-scores)))

(score initial-state)
263
```

#### 4. Create a temperature schedule

A decreasing sequence of "temperature" values.

TODO explain the art of developing a temperature schedule.

```clojure
(def temperatures (make-temperature-seq 1.5 0.1 5000))
(1.5
 1.4991876424785222
 ...)
```

#### 5. Run simulated annealing

The `run-sa` function in the `simanneal.anneal` namespace is the main entry point.

It takes the initial state, the move and score functions, and the temperature schedule and
returns the resulting optimum state. For this demo, it returns the reverse-sorted vector.

```clojure
(run-sa initial-state move score temperatures)
[9 8 7 6 5 4 3 2 1 0]
```



## TODO

Lots of Clojure details to learn

* How to package and distribute code?
* Traveling Salesman Problem example.
* Tests
* Visualization of intermediate results
* Implement as a [component](https://github.com/stuartsierra/component) behind a server.
