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
    return [
        City(index, *map(float, line.strip().split(";")))
        for index, line in enumerate(data)
    ]


def parse_solution(data):
    return {
        "cost": int(data[0].split(": ")[1].strip()),
        "solution": [[int(x) for x in line.split()] for line in data[1:]],
    }


tspa = parse_cities(read_file("TSPA.csv"))
tspb = parse_cities(read_file("TSPB.csv"))

tspa_random = parse_solution(read_file("tspa_random.txt"))
tspb_random = parse_solution(read_file("tspb_random.txt"))
tspa_greedy_append = parse_solution(read_file("tspa_greedy_append.txt"))
tspb_greedy_append = parse_solution(read_file("tspb_greedy_append.txt"))
tspa_greedy_at_any_position = parse_solution(
    read_file("tspa_greedy_at_any_position.txt")
)
tspb_greedy_at_any_position = parse_solution(
    read_file("tspb_greedy_at_any_position.txt")
)
tspa_greedy_cycle = parse_solution(read_file("tspa_greedy_cycle.txt"))
tspb_greedy_cycle = parse_solution(read_file("tspb_greedy_cycle.txt"))


def plot_cities(suptitle, tspa, tspb):
    plt.figure(figsize=(16, 6))
    plt.tight_layout()
    plt.suptitle(suptitle)
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
    plt.title(f"TSPB [{tspb_solution['cost']}]")
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
