from flask import Flask, send_file
from io import BytesIO
import seaborn as sns
import matplotlib.pyplot as plt
import pandas as pd
from Plates import *
from ObtainTotals import *


def barplot_total_generator(diet):

    res = nutritional_values_total(diet)

    df = pd.DataFrame({
        'Valores Nutricionales': ["Carbohidratos", "Proteina", "Grasas", "Azúcares", "Sales", "Precio"],
        'Cantidades': [res[1], res[2], res[3], res[4], res[5], res[6]],
    })

    plt.figure(figsize=(6, 4))
    colores = sns.color_palette("blend:#b2e2b2,#b2dfee", n_colors=len(df))
    ax = sns.barplot(data=df, x='Valores Nutricionales', y='Cantidades', hue="Valores Nutricionales",
                     palette=colores, width=0.6, legend=False)
    ax.set_xlabel("")
    ax.set_ylabel(" Cantidades (gr.)")

    # Añadir borde negro a cada barra
    for patch in ax.patches:
        patch.set_edgecolor('black')
        patch.set_linewidth(1)

    plt.title("Datos dieta de " + str(res[0]) + " calorías")
    plt.xticks(fontsize=9)
    plt.tight_layout()
    plt.show()

    plt.show()


def barplot_day_generator(diet_day):

    res = nutritional_values_day(diet_day)

    df = pd.DataFrame({
        'Valores Nutricionales': ["Carbohidratos", "Proteina", "Grasas", "Azúcares", "Sales", "Precio"],
        'Cantidades': [res[1], res[2], res[3], res[4], res[5], res[6]],
    })

    plt.figure(figsize=(6, 4))
    colores = sns.color_palette("blend:#b2e2b2,#b2dfee", n_colors=len(df))
    ax = sns.barplot(data=df, x='Valores Nutricionales', y='Cantidades', hue="Valores Nutricionales",
                     palette=colores, width=0.6, legend=False)
    ax.set_xlabel("")
    ax.set_ylabel(" Cantidades (gr.)")

    # Añadir borde negro a cada barra
    for patch in ax.patches:
        patch.set_edgecolor('black')
        patch.set_linewidth(1)

    plt.title("Datos dieta de " + str(res[0]) + " calorías")
    plt.xticks(fontsize=9)
    plt.tight_layout()
    plt.show()

    plt.show()

app = Flask(__name__)



