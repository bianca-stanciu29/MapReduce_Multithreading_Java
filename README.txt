STANCIU BIANCA-ANDREEA, 335CC
                                         TEMA 2- APD
                           Procesarea de documente folosind paradigme Map-Reduce

        In cadrul acestei teme, am pornit de la notiuni din cadrul laboratorului 7. Am folosit interfata Executor
Service pentru a permite executarea taskurilor, iar pentru metoda submit() care creeaza taskurile se va primi un obiect
care implementeaza interfata Callable pentru a returna rezultatul final. Atat dupa etapa de Map, cat si dupa etapa de
Reduce, se va opri ExecutorService-ul din a mai primi taskuri prin shutdown().

        Din linia de comanda se vor extrage numarul de workeri, numele fisierului de intrare si numele fisierului de
iesire. La citirea datelor de intrare din fisierul de input, se va initializa fragment size-ul, numarul fisierelor si
intr-un vector de Stringuri se vor retine path-urile fisierelor. Pentru fiecare fisier, se va initializa offset-start
cu 0 si offset_end cu fragment size. Cat timp exista portiuni de dimensiunea fragment size-ului, se pornesc taskurile
si se vor updata offset-urile. La portiunea ramasa din text mai mica decat fragment size-ului se va trimite un task cu
offset-urile ramase. Pentru a retine rezultatul oferit de Callable, am folosit interfata Future avand ca tip clasa
pentru Map Result.

MyMap:
        - implementeaza Callable
        - return the result with file name, the map with dimension and number of words, the maximum word
        - pentru delimitatori am folosit stringul: " ;:/?~\\.,><`[]{}()!@#$%^&-_+'=*\"|\t\r\n"
        - prelucrare offset_end:
                    *daca offset_end nu a ajuns la finalul textului
                    *daca caracterul curent este litera                -> va creste offset_end-ul
                    *caracterul urmator este litera
        - prelucrare offset_start:
                    * daca offset-ul nu este la inceput de sir
                    * caracterul curent este litera                     -> va creste offset-ul
                    * caracterul de dinainte este litera

        - se va extrage corect portiunea cu noul offset_start si offset_end pentru a se asigura ca nu exista jumatati de
        cuvant

        - se vor extrage cuvintele din portiun in functie de delimitatori si se vor salva intr-un vector de stringuri

        - am aflat dimensiunea cuvantului maxim din portiune

        - am parcurs vectorul de cuvinte si am salvat cuvantul cu dimensiunea maxima

        - am parcurs vectorul de cuvinte si am creat un map cu cheia egala cu dimensiunea fiecarui cuvant si valoarea
        egala cu numarul de aparitii
                OBS!! Daca portiunes este sirul vid, nu se va adauga in map

        - in resultat se va pune: numele fisierului, map-ul si cuvantul cu dimensiunea maxima


MyReduce:
        - implementeaza Callable
        -  primeste ca parametrii numele fisierului si rezultatul din MyMap
        -  se va parcurge array-ul rezultat din MyMap si daca fisirul trimis de task este egal cu numele fisierului din
        array atunci:
                    * se cauta dimensiunea maxima
                    * se va calcula numarul de cuvinte
                    * se va face suma in functie de sirul lui fibonacii(cheie + 1) * valoarea_cheii
                    * se calculteaza astfel rangul fisierului
        - am parcurs din nou array-ul rezultat din MyMap si voi gasi cate cuvinte cu dimensiunea maximala exista
        -se adauga in rezultat numele fisierului, rangul, dimensiunea maxima a cuvantului din fisier si numarul de
        cuvinte maximale


        Dupa oprirea Executor Service din a mai primii task-uri, se va sorta array-ul de rezultatul final in functie de
rang, descrescator. In fisierul de output se va pune numele fisierului, rangul de doua zecimale, dimensiunea maxima
a cuvantului si numarul de cuvinte maximale.                                                                                      