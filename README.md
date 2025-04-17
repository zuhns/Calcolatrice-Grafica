# CALCOLATRICE GRAFICA

## CREATORE Elia Caviglia
## VERSIONE 1.0.1


La calcolatrice utilizza la libreria exp4j, che supporta la seguente sintassi:

    Operazioni aritmetiche: +, -, *, /, ^ (potenza)

		Costanti matematiche: pi, e

		Funzioni matematiche integrate:

		Funzioni trigonometriche: sin(x), cos(x), tan(x)

		Funzioni trigonometriche inverse: asin(x), acos(x), atan(x)

		Funzioni iperboliche: sinh(x), cosh(x), tanh(x)

		Logaritmi ed esponenziali: log(x), log10(x), ln(x), exp(x)

		Altre funzioni comuni: abs(x), sqrt(x), cbrt(x), ceil(x), floor(x)


	Esempi validi:

		- sin(x) + log(x)

		- 3*x^2 - sqrt(x)

		- (tan(x) + 5)/(e^x - 1)

		- sin((x + 2) / (x - 1))


Obiettivo:
		Risolvere equazioni miste, ovvero equazioni la cui soluzione può essere trovata graficamente, come ad esempio:

        ln(x) = 3 - x

La calcolatrice restituirà un risultato numerico approssimato, calcolato in un intervallo suddiviso in 100 valori.
In alternativa, l'utente può anche semplicemente inserire una singola funzione per visualizzarne il grafico, senza necessariamente cercarne l'intersezione con un'altra.
La funzione può essere inserita in forma diretta, ad esempio:

    			3x + 2

oppure specificando la variabile dipendente:

    			y = 3x + 2
	

Metodi matematici utilizzati:

		- Metodo della Bisezione:

			Per risolvere numericamente le equazioni miste, viene utilizzato il metodo della bisezione:
			L'algoritmo individua intervalli in cui la funzione cambia segno, indicando la presenza di una radice.
			Per ogni intervallo viene eseguito il metodo della bisezione:
			Si calcola il punto medio dell’intervallo.
			Si valuta la funzione nel punto medio.
			Se il risultato è vicino a zero (tolleranza fissata a 1e-6), viene trovato un punto di intersezione.
			Altrimenti, si determina il nuovo intervallo dimezzato, scegliendo la metà in cui si verifica il cambio di segno.
			Il procedimento viene ripetuto fino alla precisione desiderata o al raggiungimento di un limite massimo di iterazioni.

		- Tracciamento grafico delle funzioni:

			Per tracciare le funzioni, la calcolatrice effettua un campionamento adattivo del dominio, calcolando numerosi punti intermedi per garantire una rappresentazione accurata, specialmente vicino ad asintoti o punti di forte variazione.
			Se incontra valori non validi (come divisioni per zero o risultati indefiniti), cerca di gestire correttamente tali casi evitando linee continue tra punti non definiti.
			In alcune situazioni, a causa delle limitazioni della risoluzione grafica e della gestione numerica, si possono verificare dei troncamenti o discontinuità visive, soprattutto quando si utilizzano forti livelli di zoom-out.

Bug e limitazioni note:
		Troncamento delle linee: in alcuni casi, specialmente con funzioni come tan(x) o 1/x, le linee verticali vengono troncate a causa di limiti nella risoluzione grafica o nell'approssimazione numerica effettuata dal metodo di campionamento.
		Performance: con livelli molto elevati di zoom-out, la visualizzazione può rallentare leggermente a causa del grande numero di punti calcolati.
