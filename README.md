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
2. [Similarities graphs](#similarities-graphs)
3. [Conclusions](#conclusions)

<div style="page-break-after: always;"></div>

## Similarities graphs (#similarities-graphs)

![similarities-tspa](plots/tspa-similarity.png)
![similarities-tspb](plots/tspb-similarity.png)

## [Conclusions](#conclusions)

In all cases, the correlation coefficients are negative, suggesting that as the cost increases, solutions tend to become less similar on average. This aligns with the expectation that higher-cost solutions may deviate more significantly from optimal patterns.

A surprising observation is the shape of the average similarity curves. One might expect a parabolic relationship, where middle-cost solutions exhibit the highest similarity on average. However, the graphs reveal a steady, linear decline in similarity with increasing cost. This trend likely arises because some edges or nodes are "obvious" to include in most solutions, while others are less so. Higher-cost solutions likely fail to include these less obvious components consistently, leading to a gradual decrease in similarity.

The best solutions, by contrast, tend to incorporate both the obvious and the more challenging edges/nodes, making them structurally closer to the majority of solutions. This structural consistency may explain why these solutions exhibit higher similarity compared to suboptimal ones.
