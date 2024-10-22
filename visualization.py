import matplotlib.pyplot as plt


class City:
    def __init__(self, id, x, y, cost):
        self.id = id
        self.x = x
        self.y = y
        self.cost = cost


def read_file(file_path):
    with open(file_path) as f:
        return f.readlines()


def parse_cities(data):
    cities = [
        City(index, *map(float, line.strip().split(";")))
        for index, line in enumerate(data)
    ]
    return {city.id: city for city in cities}


def parse_solution(data):
    cost = int(data[0].split(": ")[1].strip())
    path = [int(x) for x in data[1].split(": ")[1].split(",")]
    return {
        "cost": cost,
        "solution": path,
    }


def plot_cities(suptitle, tspa, tspb):
    plt.figure(figsize=(16, 6))
    plt.tight_layout()
    plt.suptitle(suptitle)
    plt.subplot(1, 2, 1)
    plt.title("TSPA")
    plt.xlabel("x")
    plt.ylabel("y")
    scatter_tspa = plt.scatter(
        [city.x for _, city in tspa.items()],
        [city.y for _, city in tspa.items()],
        c=[city.cost for _, city in tspa.items()],
        cmap="viridis",
    )
    plt.colorbar(scatter_tspa, label="city cost")

    plt.subplot(1, 2, 2)
    plt.title("TSPB")
    plt.xlabel("x")
    plt.ylabel("y")
    scatter_tspb = plt.scatter(
        [city.x for _, city in tspb.items()],
        [city.y for _, city in tspb.items()],
        c=[city.cost for _, city in tspb.items()],
        cmap="viridis",
    )
    plt.colorbar(scatter_tspb, label="city cost")


def plot_solution(suptitle, tspa, tspa_solution, tspb, tspb_solution):
    plt.figure(figsize=(16, 6))
    plt.tight_layout()
    plt.suptitle(suptitle)
    plt.subplot(1, 2, 1)
    plt.suptitle(suptitle)
    plt.title(f"TSPA [{tspa_solution['cost']}]")
    plt.xlabel("x")
    plt.ylabel("y")
    scatter_tspa = plt.scatter(
        [city.x for _, city in tspa.items()],
        [city.y for _, city in tspa.items()],
        c=[city.cost for _, city in tspa.items()],
        cmap="viridis",
    )
    plt.colorbar(scatter_tspa, label="city cost")

    tspa_path = tspa_solution["solution"]
    for i in range(len(tspa_path) - 1):
        plt.plot(
            [tspa[tspa_path[i]].x, tspa[tspa_path[i + 1]].x],
            [tspa[tspa_path[i]].y, tspa[tspa_path[i + 1]].y],
            "black",
        )
    plt.plot(
        [tspa[tspa_path[-1]].x, tspa[tspa_path[0]].x],
        [tspa[tspa_path[-1]].y, tspa[tspa_path[0]].y],
        "black",
    )

    plt.subplot(1, 2, 2)
    plt.title(f"TSPB [{tspb_solution['cost']}]")
    plt.xlabel("x")
    plt.ylabel("y")
    scatter_tspb = plt.scatter(
        [city.x for _, city in tspb.items()],
        [city.y for _, city in tspb.items()],
        c=[city.cost for _, city in tspb.items()],
        cmap="viridis",
    )
    plt.colorbar(scatter_tspb, label="city cost")

    tspb_path = tspb_solution["solution"]
    for i in range(len(tspb_path) - 1):
        plt.plot(
            [tspb[tspb_path[i]].x, tspb[tspb_path[i + 1]].x],
            [tspb[tspb_path[i]].y, tspb[tspb_path[i + 1]].y],
            "black",
        )
    plt.plot(
        [tspb[tspb_path[-1]].x, tspb[tspb_path[0]].x],
        [tspb[tspb_path[-1]].y, tspb[tspb_path[0]].y],
        "black",
    )


tspa = parse_cities(read_file("TSPA.csv"))
tspb = parse_cities(read_file("TSPB.csv"))

tspa_random = parse_solution(read_file("results/tspa_random.txt"))
tspb_random = parse_solution(read_file("results/tspb_random.txt"))
tspa_greedy_tail = parse_solution(read_file("results/tspa_greedy_tail.txt"))
tspb_greedy_tail = parse_solution(read_file("results/tspb_greedy_tail.txt"))
tspa_greedy_any_position = parse_solution(
    read_file("results/tspa_greedy_any_position.txt")
)
tspb_greedy_any_position = parse_solution(
    read_file("results/tspb_greedy_any_position.txt")
)
tspa_greedy_cycle = parse_solution(read_file("results/tspa_greedy_cycle.txt"))
tspb_greedy_cycle = parse_solution(read_file("results/tspb_greedy_cycle.txt"))
tspa_greedy_cycle_regret = parse_solution(
    read_file("results/tspa_greedy_cycle_regret.txt")
)
tspb_greedy_cycle_regret = parse_solution(
    read_file("results/tspb_greedy_cycle_regret.txt")
)
tspa_greedy_cycle_weighted_regret = parse_solution(
    read_file("results/tspa_greedy_cycle_weighted_regret.txt")
)
tspb_greedy_cycle_weighted_regret = parse_solution(
    read_file("results/tspb_greedy_cycle_weighted_regret.txt")
)
tspa_node_exchange_greedy = parse_solution(
    read_file("results/tspa_node_exchange_greedy.txt")
)
tspb_node_exchange_greedy = parse_solution(
    read_file("results/tspb_node_exchange_greedy.txt")
)
tspa_node_exchange_steepest = parse_solution(
    read_file("results/tspa_node_exchange_steepest.txt")
)
tspb_node_exchange_steepest = parse_solution(
    read_file("results/tspb_node_exchange_steepest.txt")
)


plot_cities("cities", tspa, tspb)
plt.savefig("plots/cities.png")

plot_solution(
    "random",
    tspa,
    tspa_random,
    tspb,
    tspb_random,
)
plt.savefig("plots/random.png")

plot_solution(
    "greedy tail",
    tspa,
    tspa_greedy_tail,
    tspb,
    tspb_greedy_tail,
)
plt.savefig("plots/greedy_tail.png")

plot_solution(
    "greedy any position",
    tspa,
    tspa_greedy_any_position,
    tspb,
    tspb_greedy_any_position,
)
plt.savefig("plots/greedy_any_position.png")

plot_solution(
    "greedy cycle",
    tspa,
    tspa_greedy_cycle,
    tspb,
    tspb_greedy_cycle,
)
plt.savefig("plots/greedy_cycle.png")

plot_solution(
    "greedy cycle regret",
    tspa,
    tspa_greedy_cycle_regret,
    tspb,
    tspb_greedy_cycle_regret,
)
plt.savefig("plots/greedy_cycle_regret.png")

plot_solution(
    "greedy cycle weighted regret",
    tspa,
    tspa_greedy_cycle_weighted_regret,
    tspb,
    tspb_greedy_cycle_weighted_regret,
)
plt.savefig("plots/greedy_cycle_weighted_regret.png")

plot_solution(
    "greedy cycle weighted regret",
    tspa,
    tspa_greedy_cycle_weighted_regret,
    tspb,
    tspb_greedy_cycle_weighted_regret,
)
plt.savefig("plots/greedy_cycle_weighted_regret.png")

plot_solution(
    "node exchange_greedy",
    tspa,
    tspa_node_exchange_greedy,
    tspb,
    tspb_node_exchange_greedy,
)
plt.savefig("plots/node_exchange_greedy.png")

plot_solution(
    "node exchange_steepest",
    tspa,
    tspa_node_exchange_steepest,
    tspb,
    tspb_node_exchange_steepest,
)
plt.savefig("plots/node_exchange_steepest.png")
