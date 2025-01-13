# Raport - Iterated Local Search [GITHUB](https://github.com/eeryczeek/evolutionary_computing)

Eryk Ptaszyński: 151950  
Eryk Walter: 151931

The **Traveling Salesman Problem (TSP)** is an optimization problem where the objective is to find the shortest possible route that visits a set of cities exactly once and returns to the starting city. In its traditional form, the TSP assumes that the cost of traveling between any two cities is known and fixed, and the salesman must visit all cities.

## Modified TSP Problem with Additional Constraints

1. **Additional City Cost**:  
   In this modified version, each city has an associated **fixed cost** (besides the cost of travel). This city cost represents an additional expense incurred for visiting the city. Therefore, the total cost of the route is the sum of the travel costs between cities and the individual costs for each visited city. The objective becomes minimizing the total of both travel costs and city costs.

2. **Selection of Only 50% of Cities**:  
   Another key modification is that the salesman is not required to visit **all** cities. Instead, the objective is to visit **exactly 50% of the available cities**. This creates a **sub-selection** problem where the salesman must decide which subset of cities to visit while minimizing the total cost (**travel + city cost**).

This visual representation provides an intuitive way to interpret the spatial relationships between cities, their associated fixed costs, and potential travel paths.

![cities.png](plots/cities.png)

<div style="page-break-after: always;"></div>

## [Table of Contents](#table-of-contents)

1. [Modified TSP Problem with Additional Constraints](#modified-tsp-problem-with-additional-constraints)
2. [Experiment results](#results)
3. [Conclusions](#conclusions)

<div style="page-break-after: always;"></div>

## Experiment results (#results)

### Instance A

| **Method**                        | **Min** | **Mean** | **Max** | **Avg time (s)** | **Iterations** |
| --------------------------------- | ------- | -------- | ------- | ---------------- | -------------- |
| `InsertAnyPositionSolution`       | 71263   | 73229    | 76156   | 0.0240           | -              |
| `MSLS`                            | 70630   | 71164    | 71554   | 21.6870          | -              |
| `LargeNeighborhoodSearchWithLS    | 69107   | 69496    | 70330   | 22.2230          | 1497           |
| `LargeNeighborhoodSearchWithoutLS | 69166   | 69645    | 70068   | 22.2190          | 2518           |
| `IteratedLocalSearch`             | 69100   | 69590    | 70046   | 22.1530          | 2808           |

### Instance B

| **Method**                     | **Min** | **Mean** | **Max** | **Avg time (s)** | **Iterations** |
| ------------------------------ | ------- | -------- | ------- | ---------------- | -------------- |
| `InsertAnyPositionSolution`    | 44446   | 46246    | 52152   | 0.0210           | -              |
| `MSLS`                         | 45158   | 45658    | 46156   | 22.3800          | -              |
| `LargeNeighborhoodSearchWithLS | 43446   | 44108    | 44926   | 22.2240          | 1468           |
| `LargeNeighborhoodSearchWithou | 43448   | 44293    | 45314   | 22.2190          | 2468           |
| `IteratedLocalSearch`          | 43477   | 44088    | 44700   | 22.1520          | 2902           |

![results-random](./plots/HybridEvolutionaryRandom.png)
![results-heuristic](./plots/HybridEvolutionaryHeuristic.png)

## [Conclusions](#conclusions)

The results show that the Hybrid Evolutionary Search is comparable to the previous advanced techniques. For sure there is space for further optimizations in our implementation but comparing how complicated was implementing this method compared to other ones, this one is probably the trickiest of them all.
