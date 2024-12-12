import pandas as pd
import matplotlib.pyplot as plt
import numpy as np

similarities_tspa = pd.read_csv("results/tspa_similarity.csv")
similarities_tspb = pd.read_csv('results/tspb_similarity.csv')

def add_correlation(ax, x, y):
    corr = np.corrcoef(x, y)[0, 1]
    ax.text(0.05, 0.9, f'Corr: {corr:.2f}', transform=ax.transAxes, fontsize=12, bbox=dict(facecolor='white', alpha=0.7))

def plot_similarity(df, title, filename):
    fig, axs = plt.subplots(2,2, figsize=(20, 12), sharex=True)

    # Plot each similarity metric on its own subplot
    axs[0][0].scatter(df['cost'], df['similarityEdgesAvg'], label='similarityEdgesAvg', marker='o')
    axs[0][0].set_xlabel('Cost')
    axs[0][0].set_ylabel('similarity')
    axs[0][0].set_ylim(30, 70)
    axs[0][0].grid(True)
    axs[0][0].title.set_text("Average similarity based on edges")
    add_correlation(axs[0][0], df['cost'], df['similarityEdgesAvg'])

    axs[1][0].scatter(df['cost'], df['similarityNodesAvg'], label='similarityNodesAvg', marker='s', color='orange')
    axs[1][0].set_xlabel('Cost')
    axs[1][0].set_ylabel('similarity')
    axs[1][0].set_ylim(75, 100)
    axs[1][0].grid(True)
    axs[1][0].title.set_text("Average similarity based on nodes")
    add_correlation(axs[1][0], df['cost'], df['similarityNodesAvg'])

    axs[0][1].scatter(df['cost'], df['similarityEdgesBest'], label='similarityEdgesBest', marker='^', color='green')
    axs[0][1].set_xlabel('Cost')
    axs[0][1].set_ylabel('similarity')
    axs[0][1].set_ylim(30, 70)
    axs[0][1].grid(True)
    axs[0][1].title.set_text("Similartity to the best solution based on edges")
    add_correlation(axs[0][1], df['cost'], df['similarityEdgesBest'])

    axs[1][1].scatter(df['cost'], df['similarityNodesBest'], label='similarityNodesBest', marker='d', color='red')
    axs[1][1].set_ylabel('similarity')
    axs[1][1].set_xlabel('Cost')
    axs[1][1].set_ylim(75, 100)
    axs[1][1].grid(True)
    axs[1][1].title.set_text("Similartity to the best solution based on nodes")
    add_correlation(axs[1][1], df['cost'], df['similarityNodesBest'])

    # Add a title to the figure
    fig.suptitle(title, fontsize=16)

    plt.tight_layout(rect=[0, 0, 1, 0.96])
    plt.savefig('plots/' + filename + '.png')
    plt.close()

plot_similarity(similarities_tspa, 'TSP A', 'tspa_similarity')
plot_similarity(similarities_tspb, 'TSP B', 'tspb_similarity')

