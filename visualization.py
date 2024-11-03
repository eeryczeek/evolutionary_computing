import matplotlib.pyplot as plt
import re
from collections import defaultdict


class City:
    def __init__(self, id, x, y, cost):
        self.id = id
        self.x = x
        self.y = y
        self.cost = cost


class Solution:
    def __init__(self, instance, method, path, cost):
        self.instance = instance
        self.method = method
        self.path = path
        self.cost = cost

    def __str__(self):
        return f"{self.path}, {self.cost})\n"

    def __repr__(self):
        return self.__str__()


def read_file(file_path):
    with open(file_path) as f:
        return f.readlines()


def read_solutions(file_path):
    with open(file_path) as f:
        return f.read()


def parse_cities(tspa, tspb):
    return {
        "tspa": [
            City(index, *map(float, line.strip().split(";")))
            for index, line in enumerate(tspa)
        ],
        "tspb": [
            City(index, *map(float, line.strip().split(";")))
            for index, line in enumerate(tspb)
        ],
    }


def parse_solutions(data):
    solution_dict = defaultdict(dict)
    pattern = re.compile(
        r"Instance: (\w+)\nMethod: (\w+)\nBest Solution Path: ([\d, ]+)\nBest Solution Cost: (\d+)"
    )

    matches = pattern.findall(data)
    for match in matches:
        instance, method, path_str, cost = match
        path = list(map(int, path_str.split(", ")))
        cost = int(cost)
        solution = Solution(instance, method, path, cost)
        solution_dict[instance][method] = solution

    return solution_dict


def plot_cities(suptitle, cities: dict[str, list[City]]):
    def plot_subplot(title, cities):
        plt.title(title)
        plt.xlabel("x")
        plt.ylabel("y")
        scatter = plt.scatter(
            [city.x for city in cities],
            [city.y for city in cities],
            c=[city.cost for city in cities],
            cmap="viridis",
        )
        plt.colorbar(scatter, label="city cost")

    plt.figure(figsize=(16, 6))
    plt.tight_layout()
    plt.suptitle(suptitle)
    plt.subplot(1, 2, 1)
    plot_subplot("TSPA", cities["tspa"])
    plt.subplot(1, 2, 2)
    plot_subplot("TSPB", cities["tspb"])
    plt.savefig(f"plots/{suptitle.lower().replace(' ', '_')}.png")


def plot_solutions(
    cities: dict[str, list[City]], solutions: dict[str, dict[str, Solution]]
):
    def plot_subplot(title, city_list, solution):
        plt.title(f"{title} [{solution.cost}]")
        plt.xlabel("x")
        plt.ylabel("y")
        scatter = plt.scatter(
            [city.x for city in city_list],
            [city.y for city in city_list],
            c=[city.cost for city in city_list],
            cmap="viridis",
        )
        plt.colorbar(scatter, label="city cost")

        path = solution.path
        for i in range(len(path) - 1):
            plt.plot(
                [city_list[path[i]].x, city_list[path[i + 1]].x],
                [city_list[path[i]].y, city_list[path[i + 1]].y],
                "black",
            )
        plt.plot(
            [city_list[path[-1]].x, city_list[path[0]].x],
            [city_list[path[-1]].y, city_list[path[0]].y],
            "black",
        )

    instances = list(solutions.keys())
    methods = set(solutions[instances[0]].keys()) & set(solutions[instances[1]].keys())
    for method in methods:
        plt.figure(figsize=(16, 6))
        plt.suptitle(f"{method}")
        plt.subplot(1, 2, 1)
        plot_subplot("TSPA", cities["tspa"], solutions["tspa"][method])
        plt.subplot(1, 2, 2)
        plot_subplot("TSPB", cities["tspb"], solutions["tspb"][method])
        plt.tight_layout(rect=[0, 0, 1, 0.96])
        plt.savefig(f"plots/{method}.png")
        plt.close()


cities = parse_cities(read_file("TSPA.csv"), read_file("TSPB.csv"))
solutions = parse_solutions(read_solutions("results/results_best.txt"))


plot_cities("cities", cities)
plot_solutions(cities, solutions)
