import matplotlib.pyplot as plt


class City:
    def __init__(self, id, x, y, cost):
        self.id = id
        self.x = x
        self.y = y
        self.cost = cost


with open("TSPA.csv") as f:
    tspa = f.readlines()

with open("TSPB.csv") as f:
    tspb = f.readlines()

with open("tspa_random.txt") as f:
    tspa_random_solution = f.readlines()

with open("tspb_random.txt") as f:
    tspb_random_solution = f.readlines()

with open("tspa_greedy_append.txt") as f:
    tspa_greedy_append = f.readlines()

with open("tspb_greedy_append.txt") as f:
    tspb_greedy_append = f.readlines()

with open("tspa_greedy_at_any_position.txt") as f:
    tspa_greedy_at_any_position = f.readlines()

with open("tspb_greedy_at_any_position.txt") as f:
    tspb_greedy_at_any_position = f.readlines()

with open("tspa_greedy_cycle.txt") as f:
    tspa_greedy_cycle = f.readlines()

with open("tspb_greedy_cycle.txt") as f:
    tspb_greedy_cycle = f.readlines()

tspa_random_cost = int(tspa_random_solution[0].split(": ")[1].strip())
tspb_random_cost = int(tspb_random_solution[0].split(": ")[1].strip())
tspa_greedy_append_cost = int(tspa_greedy_append[0].split(": ")[1].strip())
tspb_greedy_append_cost = int(tspb_greedy_append[0].split(": ")[1].strip())
tspa_greedy_at_any_position_cost = int(
    tspa_greedy_at_any_position[0].split(": ")[1].strip()
)
tspb_greedy_at_any_position_cost = int(
    tspb_greedy_at_any_position[0].split(": ")[1].strip()
)
tspa_greedy_cycle_cost = int(tspa_greedy_cycle[0].split(": ")[1].strip())
tspb_greedy_cycle_cost = int(tspb_greedy_cycle[0].split(": ")[1].strip())

tspa = [
    City(index, *map(float, line.strip().split(";"))) for index, line in enumerate(tspa)
]
tspb = [
    City(index, *map(float, line.strip().split(";"))) for index, line in enumerate(tspb)
]
tspa_random_solution = [
    [int(x) for x in line.split()] for line in tspa_random_solution[1:]
]
tspb_random_solution = [
    [int(x) for x in line.split()] for line in tspb_random_solution[1:]
]
tspa_greedy_append = [[int(x) for x in line.split()] for line in tspa_greedy_append[1:]]
tspb_greedy_append = [[int(x) for x in line.split()] for line in tspb_greedy_append[1:]]
tspa_greedy_at_any_position = [
    [int(x) for x in line.split()] for line in tspa_greedy_at_any_position[1:]
]
tspb_greedy_at_any_position = [
    [int(x) for x in line.split()] for line in tspb_greedy_at_any_position[1:]
]
tspa_greedy_cycle = [[int(x) for x in line.split()] for line in tspa_greedy_cycle[1:]]
tspb_greedy_cycle = [[int(x) for x in line.split()] for line in tspb_greedy_cycle[1:]]


def plot_cities(tspa, tspb):
    plt.figure(figsize=(16, 6))
    plt.tight_layout()
    plt.subplot(1, 2, 1)
    plt.title("TSPA")
    plt.xlabel("x")
    plt.ylabel("y")
    scatter_tspa = plt.scatter(
        [city.x for city in tspa],
        [city.y for city in tspa],
        c=[city.cost for city in tspa],
        cmap="viridis",
    )
    plt.colorbar(scatter_tspa, label="city cost")

    plt.subplot(1, 2, 2)
    plt.title("TSPB")
    plt.xlabel("x")
    plt.ylabel("y")
    scatter_tspb = plt.scatter(
        [city.x for city in tspb],
        [city.y for city in tspb],
        c=[city.cost for city in tspb],
        cmap="viridis",
    )
    plt.colorbar(scatter_tspb, label="city cost")


def plot_solution(
    suptitle, tspa, tspa_cost, tspa_solution, tspb, tspb_cost, tspb_solution
):
    plt.figure(figsize=(16, 6))
    plt.tight_layout()

    plt.subplot(1, 2, 1)
    plt.suptitle(suptitle)
    plt.title(f"TSPA [{tspa_cost}]")
    plt.xlabel("x")
    plt.ylabel("y")
    scatter_tspa = plt.scatter(
        [city.x for city in tspa],
        [city.y for city in tspa],
        c=[city.cost for city in tspa],
        cmap="viridis",
    )
    plt.colorbar(scatter_tspa, label="city cost")

    for i in range(len(tspa_solution) - 1):
        plt.plot(
            [tspa_solution[i][1], tspa_solution[i + 1][1]],
            [tspa_solution[i][2], tspa_solution[i + 1][2]],
            "black",
        )
    plt.plot(
        [tspa_solution[-1][1], tspa_solution[0][1]],
        [tspa_solution[-1][2], tspa_solution[0][2]],
        "black",
    )

    plt.subplot(1, 2, 2)
    plt.title(f"TSPB [{tspb_cost}]")
    plt.xlabel("x")
    plt.ylabel("y")
    scatter_tspb = plt.scatter(
        [city.x for city in tspb],
        [city.y for city in tspb],
        c=[city.cost for city in tspb],
        cmap="viridis",
    )
    plt.colorbar(scatter_tspb, label="city cost")

    for i in range(len(tspb_solution) - 1):
        plt.plot(
            [tspb_solution[i][1], tspb_solution[i + 1][1]],
            [tspb_solution[i][2], tspb_solution[i + 1][2]],
            "black",
        )
    plt.plot(
        [tspb_solution[-1][1], tspb_solution[0][1]],
        [tspb_solution[-1][2], tspb_solution[0][2]],
        "black",
    )


plot_cities(tspa, tspb)
plt.savefig("cities.png")

plot_solution(
    "Random solution",
    tspa,
    tspa_random_cost,
    tspa_random_solution,
    tspb,
    tspb_random_cost,
    tspb_random_solution,
)
plt.savefig("random_solution.png")

plot_solution(
    "Greedy append solution",
    tspa,
    tspa_greedy_append_cost,
    tspa_greedy_append,
    tspb,
    tspb_greedy_append_cost,
    tspb_greedy_append,
)
plt.savefig("greedy_append_solution.png")

plot_solution(
    "Greedy at any position solution",
    tspa,
    tspa_greedy_at_any_position_cost,
    tspa_greedy_at_any_position,
    tspb,
    tspb_greedy_at_any_position_cost,
    tspb_greedy_at_any_position,
)
plt.savefig("greedy_at_any_position_solution.png")

plot_solution(
    "Greedy cycle solution",
    tspa,
    tspa_greedy_cycle_cost,
    tspa_greedy_cycle,
    tspb,
    tspb_greedy_cycle_cost,
    tspb_greedy_cycle,
)
plt.savefig("greedy_cycle_solution.png")
