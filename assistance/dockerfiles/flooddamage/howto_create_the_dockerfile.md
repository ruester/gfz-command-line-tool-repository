The Dockerfile for the riesgos flooddamage is a bit different it that
it will not get the data from a public github repository.

It is necessary to checkout the source code from the gfz gitlab:

```
git clone git@git.gfz-potsdam.de:fbrill/riesgos_flooddamage.git
```

At the moment this can only be done by some people with
the permissions to see the content of the repository.

Please send an mail at fabio.brill@gfz-potsdam.de to get access.

At the moment it is also necessary to checkout a customized version:

```
cd riesgos_flooddamage
git checkout nbck-analyse
```

After that you can go back to the folder with the Dockerfile and
type:

```
docker build . --tag flooddamage
``` 
