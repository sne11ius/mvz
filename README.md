mvz
===

mvz uses quick hashing (tm) to compare all files from source folder with files in the target
folder to finally present a list of copy recommendations. you may then choose from the list
what files/dirs you want to get copied and let mvz handle it for you.

usage
=====
usage: mvz
    -c <arg>   Cache file to use (defaults to cache file in target
               directory).
    -h         [Optional] Print help and exit.
    -i         Ignore cache file in target directory.
    -s <arg>   Source directory.
    -t <arg>   Target directory.

outcome
=======
    INFO [main] (Mvz.java:34) - Source: /xmpl/series
    INFO [main] (Mvz.java:35) - Target: /xmpl/movies
    INFO [main] (Mvz.java:37) - plz wait...
    INFO [main] (Mvz.java:41) - Consider copying:
    INFO [main] (Mvz.java:43) - 	/xmpl/series/Da Ali G Show
    INFO [main] (Mvz.java:43) - 	/xmpl/series/IT Crowd
    INFO [main] (Mvz.java:43) - 	/xmpl/series/Mongrels
    INFO [main] (Mvz.java:43) - 	/xmpl/series/Sherlock Holmes

gui
===
copy recommendation:

![screenshot 1](https://raw.github.com/sne11ius/mvz/master/screenshot01.png)

copying:

![screenshot 2](https://raw.github.com/sne11ius/mvz/master/screenshot02.png)

build
=====
prerequisites
 - clone https://github.com/sne11ius/commons-io
 - mvn install because mvz depends on it

building mvz
 - mvn compile assembly:single
 - java -jar target/mvz-0.0.1-SNAPSHOT-jar-with-dependencies.jar
