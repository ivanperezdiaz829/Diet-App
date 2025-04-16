from flask import Flask, send_file
from io import BytesIO
import seaborn as sns
import matplotlib.pyplot as plt
import pandas as pd
from Plates import *
from ObtainTotals import *


def barplot_generator(diet):

    res = nutritional_values_day(diet)

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

@app.route("/barplot", methods=["GET"])
def barplot():
    df = pd.DataFrame({
        'categoría': ['A', 'B', 'C', 'D'],
        'valor': [23, 17, 35, 29]
    })

    plt.figure(figsize=(6,4))
    sns.barplot(data=df, x='categoría', y='valor')
    plt.title('Barplot con Seaborn')

    img_bytes = BytesIO()
    plt.savefig(img_bytes, format='png')
    plt.close()
    img_bytes.seek(0)

    return send_file(img_bytes, mimetype='image/png')

