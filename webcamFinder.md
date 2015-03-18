# Come mappare una strada #
  1. Andare sul sito del provider e trovare la/le webcam che si vogliono mappare
  1. [Qui](http://code.google.com/p/trafficdroid/source/browse/res/values/) trovate tutti i file xml relativi alle strade, trovate il file relativo alla strada che vi interessa e premete edit.
  1. La logica di questi file è questa: ci sono 3 array il primo mappa gli id delle strade, il secondo i nomi ed il terzo le webcam. Sono array posizionali: il primo elemento di un array corrisponde al primo del seocondo array e cosi via. Te devi aggiornare il terzo. Prendi ad esempio sempre la A1 in quanto è l'unica strada che aggiorno io direttamente.
  1. Aprite la webcam dal provider e ricavate il parametro tlc, è un numero in genere di 5 cifre
  1. [Qui](http://code.google.com/p/trafficdroid/downloads/list) trovate la web-app per decodificare il codice chiamata WebcamFinder
  1. Una volta decodificato andare nell'xml e sostituite il codice attuale con il nuovo codice anteponendo la lettera 'A'. Per esempio "-1" diventerebbe "A34234"
  1. Una volta terminata la mappatura premete "Submit Patch", mi verranno inviate in automatico le vostre modifiche

PS -1 indica webcam non presente