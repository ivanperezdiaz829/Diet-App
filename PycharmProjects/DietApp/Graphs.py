from flask import Flask, send_file
from io import BytesIO
import seaborn as sns
import matplotlib.pyplot as plt
import pandas as pd
from Plates import *
from ObtainTotals import *
import numpy as np
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

def plot_nutritional_evolution(diet_data, parameter, title_suffix=""):
    # Mapeo de parámetros con alias alternativos
    param_mapping = {
        'calorias': ('Calorias', 0),
        'carbohidratos': ('Carbohidratos', 1),
        'proteina': ('Proteina', 2),
        'grasas': ('Grasas', 3),
        'azúcares': ('Azúcares', 4),
        'sales': ('Sales', 5),
        'precio': ('Precio', 6),
    }
    
    # Normalizar parámetro de entrada
    param_lower = parameter.lower()
    if param_lower not in param_mapping:
        valid_params = ", ".join(set([v[0] for v in param_mapping.values()]))
        raise ValueError(f"Parámetro '{parameter}' no válido. Opciones: {valid_params}")
    
    param_name, param_index = param_mapping[param_lower]
    unit = 'kcal' if param_name == 'Calorias' else ('€' if param_name == 'Precio' else 'gr.')    
    # Recopilar datos
    days = len(diet_data)
    values = []
    
    for day in diet_data:
        res = nutritional_values_day(day)
        values.append(res[param_index])
    
    # Crear DataFrame
    df = pd.DataFrame({
        'Día': np.arange(1, days+1),
        param_name: values
    })
    
    # Configurar gráfico
    plt.figure(figsize=(10, 6))
    sns.set_style("whitegrid")
    
    # Gráfico de puntos con línea
    sns.scatterplot(data=df, x='Día', y=param_name, 
                    color='#4c72b0', s=100, label=param_name)
    sns.lineplot(data=df, x='Día', y=param_name, 
                 color='#4c72b0', alpha=0.5, linestyle='--')
    
    # Añadir etiquetas de valor
    for i, row in df.iterrows():
        plt.text(x=row['Día'], y=row[param_name]+(max(values)*0.02), 
                 s=f"{row[param_name]:.1f}{unit}", ha='center', fontsize=9)
    
    # Personalización
    plt.title(f'Evolución de {param_name} {title_suffix}', fontsize=14, pad=20)
    plt.xlabel('Día de la dieta', fontsize=12)
    plt.ylabel(f'{param_name} ({unit})', fontsize=12)
    plt.xticks(df['Día'])
    plt.ylim(min(values)*0.9, max(values)*1.1)
    plt.tight_layout()
    plt.show()

app = Flask(__name__)



