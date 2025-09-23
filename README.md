Como clonar este proyecto en sus computadoras:

1.- Creen el directorio donde quieran desplegar el proyecto.

2.- Abran una terminal de tipo Git BASH (si no la tienen tienen que instalar git para windows o el SO que tengan)

3.- Ponen esto en la terminal de Git BASH:

    cd <aca ponen el path hasta el directorio que hayan creado>
    git clone https://github.com/PabloGalindoRoldan/ProyectoSemLen.git
    cd <aca ponen el path a la carpeta ProyectoSemLen>
    git checkout develop ---> esto es para posicionarse en el Branch "develop". El branch Main no se toca. 
    git checkout -b <aca ponen su nombre; esto va a crear un branch con sus nombres que deriva de la branch develop>

4.- Ahora pueden hacer todos los cambios que quieran. Cuando esten satisfechos con lo que hayan hecho, en la consola ponen:

    git add .
    git commit -m "<aca ponen el mensaje que quieran, normalmente es un mini resumen de lo que hicieron desde el último commit. Tiene que estar entre comillas>"
    git push origin <nombre de su branch>

5.- Cuando esté ahí, se meten en github, en el repo, y mandan un pull request para que se mergee todo a develop. Esto nos permite al resto revisar que no haya conflictos. "desarrollo" es nuestra rama principal de trabajo.
